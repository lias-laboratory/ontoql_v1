/*********************************************************************************
* This file is part of OntoQL Project.
* Copyright (C) 2006  LISI - ENSMA
*   Teleport 2 - 1 avenue Clement Ader
*   BP 40109 - 86961 Futuroscope Chasseneuil Cedex - FRANCE
* 
* OntoQL is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* OntoQL is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with OntoQL.  If not, see <http://www.gnu.org/licenses/>.
**********************************************************************************/
package fr.ensma.lisi.ontoql.engine.tree.dml;

import java.util.ArrayList;
import java.util.List;

import antlr.ASTFactory;
import antlr.SemanticException;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.Description;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCount;
import fr.ensma.lisi.ontoql.core.EntityDatatypeEnumerate;
import fr.ensma.lisi.ontoql.core.EntityDatatypeInt;
import fr.ensma.lisi.ontoql.core.EntityDatatypeString;
import fr.ensma.lisi.ontoql.core.EntityDatatypeUri;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.DisplayableNode;
import fr.ensma.lisi.ontoql.engine.tree.IdentNode;
import fr.ensma.lisi.ontoql.engine.tree.OntoQLSQLWalkerNode;
import fr.ensma.lisi.ontoql.engine.util.ASTUtil;
import fr.ensma.lisi.ontoql.exception.QueryException;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Represents a category referenced in the INTO clause of an OntoQL INSERT
 * statement.
 * 
 * @author Stéphane JEAN
 */
public class IntoClause extends OntoQLSQLWalkerNode implements DisplayableNode {

	private static final long serialVersionUID = -928678408974491685L;

	/**
	 * The category instantiated.
	 */
	private Category categoryInstantiated;

	/**
	 * The descriptions valued by this instruction.
	 */
	private List<Description> descriptions = new ArrayList<Description>(5);

	/**
	 * Initialize this intoClause with the instanciated category
	 * 
	 * @param aClass the modified class
	 */
	public void initialize(String categoryId) {
		this.categoryInstantiated = getWalker().getFactoryEntity().createCategory(categoryId);
		getDMLEvaluator().evaluateExt(categoryInstantiated, (IdentNode) this.getFirstChild());
	}

	/**
	 * Get the parent node of the valuated attributes or properties
	 * 
	 * @return the parent node of the valuated attributes or properties
	 */
	public AST getRangeClause() {
		return getFirstChild().getNextSibling();
	}

	/**
	 * Get the name of the instantiated category.
	 * 
	 * @return the name of the instantiated category.
	 */
	public String getCategoryInstanciatedName() {
		return categoryInstantiated.getName();
	}

	/**
	 * Get a label for the type of the descriptions valued in this statement
	 * 
	 * @return a label for the descriptions valued in this statement
	 */
	public String getDescriptionsTypeLabel() {
		String res = null;
		res = categoryInstantiated.isClass() ? "properties" : "attributes";
		return res;
	}

	/**
	 * Check if the values clause is consistent with the into clause.
	 * 
	 * @param valuesClause the values-clause corresponding to this into clause
	 * @throws QueryException If an inconsistency is found
	 */
	public void validateTypes(ValuesClause valuesClause) throws SemanticException {
		// Check if the number of values are equals to the number of
		// descriptions
		List valuesTypes = valuesClause.getValuesTypes();
		List values = valuesClause.getValues();

		int nbrValues = valuesTypes.size();
		int nbrDescriptions = descriptions.size();
		if (nbrValues != nbrDescriptions) {
			String descriptionsLabel = getDescriptionsTypeLabel();
			throw new SemanticException("Number of " + descriptionsLabel + " valued (" + nbrDescriptions
					+ ") doesn't match the number of values (" + nbrValues + ") in this insert statement");
		}
		// check if the descriptions are defined on the category
		String currentValue;
		EntityDatatype currentDatatypeValue;
		EntityDatatype currentDatatypeDescription;
		Description currentDescription;
		for (int i = 0; i < nbrDescriptions; i++) {
			currentDescription = (Description) descriptions.get(i);
			currentDescription.setCurrentContext(categoryInstantiated);
			currentDatatypeValue = (EntityDatatype) valuesTypes.get(i);
			currentValue = (String) values.get(i);
			if (!currentDescription.isDefined()) {
				String descriptionLabel = currentDescription.getTypeLabel();
				String categoryLabel = categoryInstantiated.getTypeLabel();
				throw new SemanticException("The " + descriptionLabel + " " + currentDescription.getName()
						+ " is not defined on the " + categoryLabel + " " + categoryInstantiated.getName());
			}
			// and if the corresponding description-value are consistent
			// a null value is consistent with all types
			currentDatatypeDescription = currentDescription.getRange();
			if (!OntoQLHelper.isNull(currentValue)
					&& !areCompatible(currentDatatypeValue, currentDatatypeDescription)) {
				String descriptionLabel = currentDescription.getTypeLabel();
				throw new SemanticException("Type of the " + descriptionLabel + " " + currentDescription.getName()
						+ " (" + currentDatatypeDescription.getName() + ")" + " and value " + currentValue + " ("
						+ currentDatatypeValue.getName() + ")" + " at position " + (i + 1) + " are not compatible");
			}
		}
	}

	@Override
	public String getDisplayText() {
		StringBuffer buf = new StringBuffer();
		buf.append("IntoClause{");
		buf.append("categoryName=").append(getCategoryInstanciatedName());
		buf.append(",columns={").append(descriptions).append("}");
		buf.append("}");
		return buf.toString();
	}

	/**
	 * Append an attribute or a property in the list after a given node. If this
	 * node is null, this attribute is inserted in first place
	 */
	public void appendColumn(AST node, String text) {
		AST range = getRangeClause();

		ASTFactory inputAstFactory = this.getASTFactory();
		AST newNode = ASTUtil.create(inputAstFactory, SQLTokenTypes.COLUMN, text);
		if (node == null) {
			AST firstColumn = range.getFirstChild();
			ASTUtil.appendSibling(newNode, firstColumn);
			range.setFirstChild(newNode);
		} else {
			ASTUtil.insertSibling(newNode, node);
		}
	}

	/**
	 * Determine whether the two types are "assignment compatible".
	 * 
	 * @param target The type defined in the into-clause.
	 * @param source The type defined in the select clause.
	 * @return True if they are assignment compatible.
	 */
	private boolean areCompatible(EntityDatatype target, EntityDatatype source) {

		boolean res = false;
		if (target.getName().equals(source.getName())) {
			return true;
		} else {
			boolean isTargetStringType = (target instanceof EntityDatatypeString);
			boolean isSourceEnumerateType = (source instanceof EntityDatatypeEnumerate);
			boolean isSourceUriType = (source instanceof EntityDatatypeUri);
			boolean isSourceCountType = (source instanceof EntityDatatypeCount);
			boolean isTargetIntType = (target instanceof EntityDatatypeInt);

			if (isTargetStringType && isSourceEnumerateType) {
				res = true;
			}

			if (isTargetStringType && isSourceUriType) {
				res = true;
			}

			if (isTargetIntType && (source.isAssociationType())) {
				res = true;
			}

			if (isTargetIntType && (isSourceCountType)) {
				res = true;
			}

		}

		return res;
	}

	/**
	 * Get the category instanciated
	 * 
	 * @return the category instanciated
	 */
	public Category getCategoryInstantiated() {
		return categoryInstantiated;
	}

	/**
	 * get the descriptions valuated
	 * 
	 * @return the descriptions valuated
	 */
	public List getDescriptions() {
		return descriptions;
	}

	/**
	 * add a description valuated
	 * 
	 * @param description a description valuated
	 */
	public void addDescription(Description description) {
		descriptions.add(description);
	}
}
