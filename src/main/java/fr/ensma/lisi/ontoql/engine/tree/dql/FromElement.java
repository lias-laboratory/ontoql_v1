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
package fr.ensma.lisi.ontoql.engine.tree.dql;

import antlr.ASTFactory;
import antlr.SemanticException;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.engine.OntoQLSQLWalker;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLSQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.DisplayableNode;
import fr.ensma.lisi.ontoql.engine.tree.OntoQLSQLWalkerNode;
import fr.ensma.lisi.ontoql.engine.util.ASTUtil;
import fr.ensma.lisi.ontoql.evaluator.Evaluator;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.util.ArrayHelper;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Represents a single class mentioned in an Ontoql FROM clause.
 * 
 * @author Stéphane JEAN
 */
public class FromElement extends OntoQLSQLWalkerNode implements DisplayableNode {

	private static final long serialVersionUID = -4388704267715883402L;

	/**
	 * Class/entity corresponding to this node.
	 */
	private Category category;

	/**
	 * Reference to its from clause.
	 */
	private FromClause fromClause;

	/**
	 * The entity class as which this class is treated (TREAT function)
	 */
	private EntityClass treatAsClass = null;

	private boolean isSQLTable = false;

	public boolean isSQLTable() {
		return isSQLTable;
	}

	/**
	 * The properties to project on this FromElement. Only used if the projected
	 * properties are not all the properties defined on the class of the from clause
	 * of this node
	 */
	private EntityProperty[] propertiesToProject = null;

	/**
	 * true if this from element has been added to resolve a path expression.
	 */
	private boolean implicitJoin = false;

	/**
	 * @return true if this from element has been added to resolve a path expression
	 */
	public final boolean isImplicitJoin() {
		return implicitJoin;
	}

	/**
	 * @return Returns the fromClause.
	 */
	public final FromClause getFromClause() {
		return fromClause;
	}

	protected String namespaceAlias = null;

	public void setSQLTable(String path) {
		isSQLTable = getDefaultNamespace() == OntoQLHelper.NO_NAMESPACE && namespaceAlias == null
				&& !path.startsWith(OntoQLHelper.PREFIX_ONTOLOGYMODEL_ELEMENT);
	}

	/**
	 * Used to create an implicit join due to a path expression or a join due to a
	 * differences of ontology model.
	 * 
	 * @param aCategory category corresponding to this from element
	 * @param polymorph true if this is a polymorphic query
	 * @param walker    the walker of the tree containing this node
	 */
	public FromElement(final Category aCategory, final boolean polymorph, final OntoQLSQLWalker walker) {
		initialize(walker);
		this.category = aCategory;
		setType(SQLTokenTypes.TABLE);

		aCategory.setPolymorph(polymorph);
		aCategory.setTableAlias(getAliasGenerator().createName(aCategory.getInternalId()));

		setText(aCategory.toSQL());
		implicitJoin = true;

	}

	/**
	 * Constructor of a from element.
	 * 
	 * @param path     text of this node
	 * @param alias    alias of this from element
	 * @param only     true if this is a polymorphic query
	 * @param genAlias true if must generate an alias to this node
	 * @param walker   the walker of the tree containing this node
	 * @throws SemanticException if a semantic error occurs
	 */
	public FromElement(final String path, String nsAlias, final String alias, final AST only, final boolean genAlias,
			final OntoQLSQLWalker walker) throws SemanticException {
		initialize(walker);

		setNamespaceAlias(nsAlias);
		String currentNamespace = namespaceAlias == null ? getDefaultNamespace() : getNamespace(namespaceAlias);

		// if the this node is not linked to a
		// namespace this is an SQL identifier
		setSQLTable(path);
		if (isSQLTable()) {
			setText(path);
			setType(SQLTokenTypes.TABLE);
			return;
		}

		try {
			category = getEntityFactory().createCategory(path, currentNamespace);
			if (genAlias) { // DML statement don't accept alias
				category.setTableAlias(getAliasGenerator().createName(category.getInternalId()));
			}
		} catch (JOBDBCException exc) {
			FromClause currentFromClause = getWalker().getCurrentFromClause();
			FromElement fromElementDependency = currentFromClause.getFromElement(path);
			if (fromElementDependency != null) {
				if (!fromElementDependency.category.isEntity()) {
					throw new SemanticException("a dynamic iterator can only be defined on an entity not on a class");
				} else {
					category = getEntityFactory().createEntityClassRoot();
					category.setTableAlias(getAliasGenerator().createName("root"));
					ASTFactory astFactory = getWalker().getASTFactory();
					AST innerJoin = ASTUtil.create(astFactory, OntoQLSQLTokenTypes.INNER_JOIN, "INNER JOIN");
					String condition = fromElementDependency.category.getTableAlias() + ".rid_bsu = ";
					if (only == null) {
						condition += " ANY (" + category.getTableAlias() + ".classid::BIGINT[])";
					} else {
						condition += category.getTableAlias() + ".classid";
					}
					AST joinCondition = ASTUtil.create(astFactory, OntoQLSQLTokenTypes.JOIN_CONDITION,
							"ON " + condition);
					fromElementDependency.addChild(innerJoin);
					fromElementDependency.addChild(this);
					fromElementDependency.addChild(joinCondition);
					implicitJoin = true; // not sure
				}
			} else {
				throw new JOBDBCException(exc.getMessage());
			}
		}

		// evaluate this OntoAlgebra expression
		evaluate(only);

		category.setCategoryAlias(alias);

	}

	/**
	 * @param only
	 */
	private void evaluate(final AST only) {
		boolean isPolymorph = only == null;
		Evaluator evaluator = null;
		if (getWalker().isDMLStatement() && !getWalker().isSubQuery()) {
			evaluator = getDMLEvaluator();
		} else {
			evaluator = getOntoAlgebraEvaluator();
		}
		if (isPolymorph) {
			evaluator.evaluateExtPolymorph(category, this);
		} else {
			evaluator.evaluateExt(category, this);
		}
	}

	/**
	 * Add the properties used on a class not defined on the class corresponding to
	 * this ident node.
	 * 
	 * @param currentClass the class corresponding to this ident node
	 * @param downClass    class which properties must be added
	 */
	public void addPropertiesToProject(EntityClass currentClass, EntityClass downClass) {

		if (propertiesToProject == null) {
			EntityProperty[] properties = currentClass.getUsedPropertiesPolymorph();

			// project on id (for implicit join) and used properties
			EntityProperty ridProperty = (EntityProperty) getEntityFactory().createDescription("oid");
			propertiesToProject = new EntityProperty[properties.length + 1];
			propertiesToProject[0] = ridProperty;

			for (int i = 1; i < propertiesToProject.length; i++) {
				propertiesToProject[i] = properties[i - 1];
			}
		}
		EntityProperty[] propertiesDownClass = downClass.getUsedPropertiesPolymorph();
		// the result is the merge between the two set of properties
		Object[] objectToProject = ArrayHelper.merge(propertiesToProject, propertiesDownClass);
		// we are obliged to cast manually the array
		propertiesToProject = new EntityProperty[objectToProject.length];
		for (int i = 0; i < objectToProject.length; i++) {
			propertiesToProject[i] = (EntityProperty) objectToProject[i];
		}
	}

	public void processTreatAs(EntityClass downClass) {
		treatAsClass = downClass;
		EntityClass currentClass = (EntityClass) category;
		// ArrayHelper.join(x, y)
		addPropertiesToProject(currentClass, downClass);

		String alias = currentClass.getTableAlias();
		if (alias == null) {
			alias = "";
		}
		setText("(" + ((EntityClass) category).project(propertiesToProject, true) + ")" + alias);
	}

	@Override
	public final String getDisplayText() {
		StringBuffer buf = new StringBuffer();
		if (category != null) {
			String text = category.getName();
			buf.append("FromElement{" + text + "}");
		}

		return buf.toString();
	}

	/**
	 * Get the class/entity corresponding to this node.
	 * 
	 * @return the class/entity corresponding to this node
	 */
	public final Category getCategory() {
		return category;
	}

	public void setFromClause(FromClause fromClause) {
		this.fromClause = fromClause;
	}

	public EntityClass getTreatAsClass() {
		return treatAsClass;
	}

	/**
	 * @param namespaceAlias the namespaceAlias to set
	 */
	public void setNamespaceAlias(String namespaceAlias) {
		this.namespaceAlias = namespaceAlias;
	}

	public boolean isEntityFromElement() {
		return category.isEntity();
	}
}
