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
package fr.ensma.lisi.ontoql.engine.tree;

import java.util.List;

import antlr.SemanticException;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.AbstractEntityClass;
import fr.ensma.lisi.ontoql.core.AbstractEntityProperty;
import fr.ensma.lisi.ontoql.core.Attribute;
import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.Description;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCategory;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCollection;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.dql.FromClause;
import fr.ensma.lisi.ontoql.engine.tree.dql.FromElement;
import fr.ensma.lisi.ontoql.engine.tree.dql.FromReferenceNode;
import fr.ensma.lisi.ontoql.engine.util.ASTUtil;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.NotSupportedDatatypeException;
import fr.ensma.lisi.ontoql.exception.QueryException;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Represent an identifier node. It may be a reference to a from element, a
 * property or an (multilingual)attribute.
 * 
 * @author Stéphane JEAN
 */
public class IdentNode extends FromReferenceNode implements ResolvableNode {

	private static final long serialVersionUID = -2142890318569318930L;

	/**
	 * Constant for alias ref.
	 */
	private static final int ALIAS_REF = 0;

	/**
	 * Constant for property.
	 */
	private static final int PROPERTY = 1;

	/**
	 * Constant for attribute.
	 */
	private static final int ATTRIBUTE = 2;

	/**
	 * Constant for a dynamic property.
	 */
	private static final int DYNAMIC_PROPERTY = 4;

	/**
	 * Constant for a column.
	 */
	private static final int COLUMN = 5;

	/**
	 * the kind of ident node : a reference to a from element, a property or an
	 * (multilingual)attribute.
	 */
	private int typeOfIdentNode;

	/**
	 * The property/attribute corresponding to this node. null if this node is a
	 * reference to a from element
	 */
	private Description description;

	/**
	 * @return true is this node is an alias of a from element
	 */
	public final boolean isAliasRef() {
		return typeOfIdentNode == ALIAS_REF;
	}

	/**
	 * Set the type of this node as an alias to a from element.
	 */
	public void setAliasRef() {
		typeOfIdentNode = ALIAS_REF;
	}

	/**
	 * @return true is this node is a property
	 */
	public final boolean isProperty() {
		return typeOfIdentNode == PROPERTY;
	}

	/**
	 * Set the type of this node as a property.
	 */
	public void setProperty() {
		typeOfIdentNode = PROPERTY;
	}

	/**
	 * @return true is this node is an (multilingual)attribute
	 */
	public final boolean isAttribute() {
		return typeOfIdentNode == ATTRIBUTE;
	}

	/**
	 * Set the type of this node as an attribute.
	 */
	public void setAttribute() {
		typeOfIdentNode = ATTRIBUTE;
	}

	/**
	 * @return true is this node is a dynamic property
	 */
	public boolean isDynamicProperty() {
		return typeOfIdentNode == DYNAMIC_PROPERTY;
	}

	/**
	 * Set the type of this node as dynamic property.
	 */
	public void setDynamicProperty() {
		typeOfIdentNode = DYNAMIC_PROPERTY;
	}

	/**
	 * @return true is this node is a column
	 */
	public boolean isColumn() {
		return typeOfIdentNode == COLUMN;
	}

	/**
	 * Set the type of this node as dynamic property.
	 */
	public void setColumn() {
		typeOfIdentNode = COLUMN;
	}

	/** natural language of this node (if meaningfull). */
	private String lgCode;

	/**
	 * @return Returns the lgCode.
	 */
	public final String getLgCode() {
		return lgCode;
	}

	/**
	 * @param lgCode The lgCode to set.
	 */
	public final void setLgCode(final String lgCode) {
		this.lgCode = lgCode;
	}

	/**
	 * Namespace of this description
	 */
	protected String namespaceAlias = null;

	/**
	 * index of an element of a collection. Index starts at 1.
	 */
	private int index = 0;

	/**
	 * @param indexString The index of this element in the collection.
	 */
	public final void setIndex(String indexString) {
		this.index = (Integer.valueOf(indexString)).intValue();
	}

	/** True if this node must be added in the projection list. */
	private boolean toAddInProjectionList = true;

	/**
	 * @return Returns the toAddInProjectionList.
	 */
	public final boolean isToAddInProjectionList() {
		return toAddInProjectionList;
	}

	/**
	 * @param toAddInProjectionList The toAddInProjectionList to set.
	 */
	public final void setToAddInProjectionList(final boolean toAddInProjectionList) {
		this.toAddInProjectionList = toAddInProjectionList;
	}

	@Override
	public final String getLabel() {
		String res;
		res = (description == null) ? getText() : description.toString();
		return res;
	}

	/**
	 * @return True if this ident node is an element of a collection
	 */
	public boolean isElementCollection() {
		return index > 0;
	}

	@Override
	public final EntityDatatype getDataType() {
		EntityDatatype res = null;
		if (isAliasRef() || isDynamicProperty()) {
			res = getWalker().getFactoryEntity().createEntityDatatype(EntityDatatype.ASSOCIATION_NAME);
			((EntityDatatypeCategory) res).setCategory(getFromElement().getCategory());
		} else {
			if (description != null) {
				res = description.getRange();
				if (isElementCollection()) {
					res = ((EntityDatatypeCollection) res).getDatatype();
				}
			}
		}
		return res;
	}

	/**
	 * @return the property/attribut corresponding to this node
	 */
	public final Description getDescription() {
		return description;
	}

	/**
	 * @param description the property/attribut to set
	 */
	public final void setDescription(final Description description) {

		this.description = description;
		if (description.isMultilingualDescription()) {
			if (lgCode == null) {
				lgCode = getSession().getReferenceLanguage();
			}
			description.setLgCode(lgCode);
		}

		if (description.isAttribute()) {
			setAttribute();
		} else {
			setProperty();
		}
	}

	/**
	 * String property of attribute used for message of error.
	 */
	private String descriptionType;

	/**
	 * String class of entity used for message of error.
	 */
	private String descriptionContextType;

	/**
	 * Get the value of descriptionType and descriptionContextType.
	 */
	public final void loadDataForException() {
		if (isProperty()) {
			descriptionType = "property";
			descriptionContextType = "class";
		} else {
			descriptionType = "attribute";
			descriptionContextType = "entity";
		}
	}

	/**
	 * @return True if this identifier is a SQL Column
	 */
	private boolean isSQLColumn(AST prefix) {
		boolean res = false;
		if (prefix != null) {
			String prefixText = prefix.getText();
			FromClause currentFromClause = getWalker().getCurrentFromClause();
			FromElement currentFromElement = currentFromClause.getFromElement(prefixText);
			if (currentFromElement != null)
				return currentFromElement.isSQLTable();
		}
		return (getDefaultNamespace() == OntoQLHelper.NO_NAMESPACE && namespaceAlias == null
				&& !getText().startsWith(OntoQLHelper.PREFIX_ONTOLOGYMODEL_ELEMENT));
	}

	/**
	 * Translate this node into SQL.
	 * 
	 * @param prefix prefix of this node
	 * @return the resolved node
	 * @throws SemanticException if a semantic error is detected
	 * @see ResolvableNode#resolve(AST)
	 */
	public final AST resolve(final AST prefix) throws SemanticException {
		return resolve(prefix, true, false);
	}

	/**
	 * Translate this node into SQL.
	 * 
	 * @param prefix                 reference to its from clause element
	 * @param locateFromClauseNeeded True if we need to search its from clause
	 *                               element
	 * @param inPath                 True if this node is resolved as part of a
	 *                               DotNode (path expression)
	 * @return the resolved noded
	 * @throws SemanticException if this node is not defined in the From clause
	 */
	public final AST resolve(final AST prefix, final boolean locateFromClauseNeeded, final boolean inPath)
			throws SemanticException {

		AST firstChild = getFirstChild();
		if (firstChild != null && firstChild.getType() == OntoQLTokenTypes.NAMESPACE_ALIAS) {
			setNamespaceAlias(firstChild.getText());
		}

		// Check if this node is an alias to a from element
		if (getWalker().getCurrentFromClause().isFromElementAlias(getText())) {
			resolveAliasFromElement(prefix);
			return this;
		}

		// Check if this is an SQL query (no namespace defined)
		if (isSQLColumn(prefix)) {
			setType(SQLTokenTypes.COLUMN);
			setColumn();
			return this;
		}

		// If it is not the case, it's a property or an attribute;
		if (locateFromClauseNeeded) {
			locateFromElement(prefix);
		} else {
			isDefined();
		}

		try {
			// Check if this node is an association property
			if (description.getRange().isAssociationType() && !inPath && !description.isAttribute()
					&& getWalker().isSelectStatement()) {
				resolveAssociationProperty();
				return this;

			}

			// Check if this node is a collection attribute/property
			if (description.getRange().isCollectionAssociationType() && !description.isAttribute()
					&& getWalker().isSelectStatement() && getWalker().isInSelect()) {
				resolveCollectionProperty();
				return this;
			}
		} catch (NotSupportedDatatypeException oExc) {
		}

		// now if the attribut is not defined on this entity in the ontology
		// model encoded in the odbd, processing must be done
		if (description instanceof Attribute && !description.getRange().isCollectionAssociationType()
				&& !getWalker().isInFrom()) {
			Attribute attribut = (Attribute) description;
			getOntoAlgebraEvaluator().ontoImageAttribute(attribut, lgCode, this, getFromElement());
		}

		setType(SQLTokenTypes.COLUMN);
		setText(getSQL());

		return this;

	}

	public void resolveCollectionProperty() throws SemanticException {
		String text = getSQL();
		setText(text);
		setType(SQLTokenTypes.COLUMN);

		if (getWalker().isInSelect()) {
			IdentNode typeOfNode = (IdentNode) ASTUtil.create(getASTFactory(), SQLTokenTypes.IDENT,
					text.replaceAll("_rids", "_tablenames"));

			typeOfNode.setType(SQLTokenTypes.COLUMN);
			ASTUtil.appendSibling(this, typeOfNode);
		}
	}

	public void resolveAssociationProperty() throws SemanticException {
		// search if the range of this property has already been added
		// in the from clause
		FromElement fromElementAlreadyAdded = getWalker().getGeneratedFromElement((AbstractEntityProperty) description);
		if (fromElementAlreadyAdded == null) {
			// the range hasn't already been added
			fromElementAlreadyAdded = getWalker().addImplicitJoin(getFromElement(), this, true);
		}
		AbstractEntityClass aClass = (AbstractEntityClass) fromElementAlreadyAdded.getCategory();
		EntityProperty propOid = getWalker().getFactoryEntity().createEntityProperty("oid");
		propOid.setCurrentContext(aClass);
		setText(propOid.toSQL());
		setType(SQLTokenTypes.COLUMN);

		if (getWalker().isInSelect()) {
			IdentNode typeOfNode = getWalker().createPropertyTypeOfNode(fromElementAlreadyAdded);
			typeOfNode.translateToSQL(false, false);

			ASTUtil.appendSibling(this, typeOfNode);

			// Create all the properties nodes
			IdentNode[] propertiesNodes = getWalker().createDescriptionsNodes(fromElementAlreadyAdded, false, false);
			// the first property is the next sibling of the node typeof
			typeOfNode.setNextSibling(propertiesNodes[0]);
		}
	}

	public void resolveAliasFromElement(AST prefix) {
		FromElement element = getWalker().getCurrentFromClause().getFromElement(getText());
		setFromElement(element);

		Category aCategory = element.getCategory();
		Description descriptionOid = getWalker().getFactoryEntity().createDescriptionOid(aCategory);
		setText(descriptionOid.toSQL());
		setType(SQLTokenTypes.COLUMN);

		if (aCategory.isClass()) {
			int statementType = getWalker().getStatementType();
			if (statementType == getWalker().INSERT || statementType == getWalker().UPDATE
					|| (statementType == getWalker().SELECT && !getWalker().isSubQuery() && getWalker().isInSelect()
							&& !getWalker().isInCase())) {
				EntityProperty propTypeOf = getWalker().getFactoryEntity().createEntityProperty("tablename");
				AbstractEntityClass aClass = (AbstractEntityClass) aCategory;
				propTypeOf.setCurrentContext(aClass);
				AST typeOfNode = ASTUtil.create(getASTFactory(), SQLTokenTypes.COLUMN, propTypeOf.toSQL());
				ASTUtil.appendSibling(this, typeOfNode);
				ASTUtil.appendSibling(this, getWalker().createPropertyNode(aClass, null));
			}
		}

		// if this node has a prefix this is a projection of a dynamic
		// property (e.g., i.p)
		if (prefix != null) {
			setDynamicProperty();
			String prefixText = prefix.getText();
			FromElement instanceFromElement = getWalker().getCurrentFromClause().getFromElement(prefixText);
			Category instance = instanceFromElement.getCategory();

			AST oidOfInstanceAST = ASTUtil.create(getASTFactory(), SQLTokenTypes.COLUMN,
					instance.getTableAlias() + ".rid");
			ASTUtil.appendSibling(this, oidOfInstanceAST);

			AST classOfInstanceAST = ASTUtil.create(getASTFactory(), SQLTokenTypes.COLUMN,
					instance.getTableAlias() + ".classid");
			ASTUtil.appendSibling(this, classOfInstanceAST);
		} else {
			// else this node represents an alias of a from element
			setAliasRef();
		}
	}

	/**
	 * get the SQL conversion of this node
	 * 
	 * @return the SQL conversion of this node as a string
	 */
	public String getSQL() {
		FromElement currentFromElement = getFromElement();
		Category currentCategory = currentFromElement.getCategory();
		String res = description.toSQL(currentCategory);
		if (res.startsWith("NULL")) {
			EntityClass downClass = getFromElement().getTreatAsClass();
			if (downClass != null) {
				res = description.toSQL(downClass);
				int indexAlias = res.indexOf('.');
				res = res.substring(indexAlias + 1, res.length());
				String tableAlias = currentCategory.getTableAlias();
				String alias = tableAlias == null ? ((EntityClass) currentCategory).toSQL(false) : tableAlias;
				res = alias + "." + res;
			}
		}

		boolean isAttributeCollection = description.isAttribute()
				&& ((Attribute) description).getRange().isCollectionAssociationType();
		if (isAttributeCollection && !((Attribute) description).isMapToFunction()
				&& !(getWalker().getStatementType() == getWalker().UPDATE)) {
			String nameCurrentCategory = ((Attribute) description).getMapAttribut().getOfEntity().getMapTo().getName(); //
			String nameCorrespondingAttribute = ((Attribute) description).getMapAttribut().getMapTo().getName();
			String nameAssociationTable = nameCurrentCategory + "_2_" + nameCorrespondingAttribute;
			res = "ARRAY(SELECT rid_d  FROM " + nameAssociationTable + " WHERE rid = ANY ("
					+ currentCategory.getTableAlias() + "." + nameCorrespondingAttribute + "))";
		}

		if (index > 0) {
			// this node represents a function call '()' or a position index
			// '[]'
			if (isAttributeCollection) {
				// This syntax is required for a function
				res = "(" + res + ")";
			}
			res += "[" + index + "]";
		}

		return res;
	}

	public Description loadDescription(Category category) throws SemanticException {
		Description res = null;
		String identifier = getText();
		res = category.getDefinedDescription(identifier, getSession().getReferenceLanguage());
		if (res == null) {
			// test if the description exist
			testExistenceOfDescription(identifier);
			// if this description exist we must raise an exception
			// stating that this description is not defined on the category
			thowDescriptionNotDefinedException(identifier, category);
		}
		setDescription(res);
		return res;
	}

	private void testExistenceOfDescription(String identifier) throws SemanticException {
		try {
			String currentNamespace = namespaceAlias == null ? getDefaultNamespace() : getNamespace(namespaceAlias);
			setDescription(getEntityFactory().createDescription(identifier, currentNamespace));
			description.checkExistence();
		} catch (JOBDBCException oExc) {
			throw new SemanticException(oExc.getMessage());
		}
	}

	/**
	 * Check and locate the element of definition of this node in the from clause.
	 * 
	 * @param prefix prefix of this node
	 * @throws SemanticException if the from element can not be located
	 */
	public final void locateFromElement(final AST prefix) throws SemanticException {
		FromClause currentFromClause = getWalker().getCurrentFromClause();
		if (prefix != null) {
			locateFromElementWithPrefix(prefix, currentFromClause);
		} else {
			locateFromElementWithoutPrefix(currentFromClause);
		}
	}

	/**
	 * Locate the from element corresponding to this ident node
	 * 
	 * @param currentFromClause the from clause
	 */
	private void locateFromElementWithoutPrefix(FromClause currentFromClause) throws SemanticException {
		// the result may be from the current from clause
		// or from an upper from clause
		List fromElements = currentFromClause.getFromElements();
		currentFromClause = currentFromClause.getParentFromClause();
		while (currentFromClause != null) {
			fromElements.addAll(currentFromClause.getFromElements());
			currentFromClause = currentFromClause.getParentFromClause();
		}
		FromElement currentFromElement = null;
		for (int i = 0; i < fromElements.size(); i++) {
			currentFromElement = (FromElement) fromElements.get(i);
			Category category = currentFromElement.getCategory();
			if (category != null && category.getNamespace() == getCurrentNamespace()) {
				description = category.getDefinedDescription(getText(), getSession().getReferenceLanguage());
				if (description != null) {
					// check that the from element has not already been found
					// in this case the query is ambiguous
					if (getFromElement() != null) {
						loadDataForException();
						throw new QueryException("The " + descriptionType + " '" + description.toString()
								+ "' is ambiguous in this query");
					} else {
						setFromElement(currentFromElement);
						setDescription(description);
					}
				}
			}
		}
		if (getFromElement() == null) {
			// test if the description exist
			testExistenceOfDescription(getText());
			loadDataForException();
			throw new SemanticException(
					"The " + descriptionType + " '" + getText() + "' is not defined in the context of the from clause");
		}
	}

	/**
	 * Locate the from element corresponding to this ident node given a prefix
	 * 
	 * @param prefix            a given prefix
	 * @param currentFromClause the from clause
	 */
	private void locateFromElementWithPrefix(final AST prefix, FromClause currentFromClause) throws SemanticException {
		FromElement currentFromElement;
		String prefixText = prefix.getText();
		currentFromElement = currentFromClause.getFromElement(prefixText);
		if (currentFromElement == null) {
			throw new QueryException("The prefix '" + prefixText + "' is not defined in the from clause");
		} else {
			setFromElement(currentFromElement);
			isDefined();
		}
	}

	private void isDefined() throws SemanticException {
		isDefined(getFromElement());
	}

	private void isDefined(FromElement contextFromElement) throws SemanticException {

		Category contextCategory = contextFromElement.getCategory();
		try {
			loadDescription(contextCategory);
		} catch (SemanticException oExc) {
			EntityClass auxClass = contextFromElement.getTreatAsClass();
			if (auxClass == null || auxClass.getDefinedDescription(getText(),
					getWalker().getSession().getReferenceLanguage()) == null) {
				thowDescriptionNotDefinedException(description.getName(), contextCategory);
			}
		}
	}

	/**
	 * @param contextCategory
	 */
	private void thowDescriptionNotDefinedException(String nameDescription, Category contextCategory)
			throws SemanticException {
		loadDataForException();
		String prefixText = contextCategory.getCategoryAlias();
		if (prefixText == null) {
			prefixText = contextCategory.getName();
		}
		throw new SemanticException("The " + descriptionType + " '" + nameDescription + "' is not defined on the "
				+ descriptionContextType + " '" + prefixText + "'");
	}

	/**
	 * Translate this node in SQL.
	 * 
	 * @param inPath             True if this node is part of a path expression
	 * @param resolveAssociation True if we must resolved association property (add
	 *                           implicit join)
	 * @return The first and last node result of the translation
	 * @throws SemanticException if this node can not be translated
	 */
	public final IdentNode[] translateToSQL(final boolean inPath, final boolean resolveAssociation)
			throws SemanticException {
		// The result is the first and last identNode result of the translation
		IdentNode[] res = new IdentNode[2];

		// default, result = this node
		res[0] = this;
		res[1] = this;

		try {

			if (description.getRange().isAssociationType() && !inPath && getWalker().isSelectStatement()
					&& description.isProperty()) {

				if (resolveAssociation) {

					// search if this path has already been resolved
					FromElement fromElementAlreadyAdded = getWalker()
							.getGeneratedFromElement((AbstractEntityProperty) description);
					// If this is not the case we resolved it by adding
					// an implicit join operation
					if (fromElementAlreadyAdded == null) {
						// the range hasn't already been added
						fromElementAlreadyAdded = getWalker().addImplicitJoin(getFromElement(), this, true);
					}

					// The ident node corresponding to this property
					IdentNode propOidNode = getWalker().createPropertyOidNode(fromElementAlreadyAdded);
					// Translate it into SQL (this is not a path)
					propOidNode.translateToSQL(false, resolveAssociation);
					// In a SELECT clause, we must also retrieve the typeOf
					if (getWalker().isInSelect()) {
						// The typeOf node
						IdentNode typeOfNode = getWalker().createPropertyTypeOfNode(fromElementAlreadyAdded);
						typeOfNode.translateToSQL(false, false);

						ASTUtil.appendSibling(propOidNode, typeOfNode);

						// Create all the properties nodes
						IdentNode[] propertiesNodes = getWalker().createDescriptionsNodes(fromElementAlreadyAdded,
								false, false);
						// the first property is the next sibling of the node
						// typeof
						typeOfNode.setNextSibling(propertiesNodes[0]);
						// the first result is the oid node
						// the last result is the last property
						res[0] = propOidNode;
						res[1] = propertiesNodes[1];
					}
					return res;
				} else { // just put the oid and typeof

					setText(description.toSQL());
					setType(SQLTokenTypes.COLUMN);

					IdentNode typeOfNode = (IdentNode) ASTUtil.create(getASTFactory(), SQLTokenTypes.IDENT,
							description.toSQL().replaceFirst("rid", "tablename"));
					typeOfNode.setDescription(description);
					typeOfNode.setType(SQLTokenTypes.COLUMN);
					ASTUtil.appendSibling(this, typeOfNode);
					res[1] = typeOfNode;
					return res;
				}

			}

		} catch (NotSupportedDatatypeException oExc) {
			setType(SQLTokenTypes.COLUMN);
			setText(getSQL());
		}

		// now if the attribut is not defined on this entity in the ontology
		// model encoded in the odbd, processing must be done
		if (description instanceof Attribute) {
			Attribute attribut = (Attribute) description;
			getOntoAlgebraEvaluator().ontoImageAttribute(attribut, lgCode, this, getFromElement());
		}

		setType(SQLTokenTypes.COLUMN);
		setText(getSQL());

		return res;

	}

	/**
	 * @param namespaceAlias the namespaceAlias to set
	 */
	public void setNamespaceAlias(String namespaceAlias) {
		this.namespaceAlias = namespaceAlias;
	}

	public String getCurrentNamespace() {
		String res = null;
		if (namespaceAlias == null) {
			if (this.getText().startsWith("#")) {
				res = OntoQLHelper.NAMESPACE_ONTOLOGY_MODEL;
			} else {
				res = getDefaultNamespace();
			}
		} else {
			res = getNamespace(namespaceAlias);
		}
		return res;
	}

}
