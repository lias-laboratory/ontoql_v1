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
package fr.ensma.lisi.ontoql.engine;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import antlr.RecognitionException;
import antlr.SemanticException;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.AbstractEntityClass;
import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.Description;
import fr.ensma.lisi.ontoql.core.Entity;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCategory;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCollection;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.core.ontodb.EntityDatatypeCollectionOntoDB;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLSQLBaseWalker;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLSQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.IdentNode;
import fr.ensma.lisi.ontoql.engine.tree.MethodNode;
import fr.ensma.lisi.ontoql.engine.tree.ResolvableNode;
import fr.ensma.lisi.ontoql.engine.tree.ddl.AlterStatement;
import fr.ensma.lisi.ontoql.engine.tree.ddl.CreateStatement;
import fr.ensma.lisi.ontoql.engine.tree.ddl.DropStatement;
import fr.ensma.lisi.ontoql.engine.tree.dml.InsertStatement;
import fr.ensma.lisi.ontoql.engine.tree.dml.IntoClause;
import fr.ensma.lisi.ontoql.engine.tree.dml.ValuesClause;
import fr.ensma.lisi.ontoql.engine.tree.dql.AbstractSelectExpression;
import fr.ensma.lisi.ontoql.engine.tree.dql.FromClause;
import fr.ensma.lisi.ontoql.engine.tree.dql.FromElement;
import fr.ensma.lisi.ontoql.engine.tree.dql.SelectExpression;
import fr.ensma.lisi.ontoql.engine.tree.dql.SelectStatement;
import fr.ensma.lisi.ontoql.engine.util.ASTPrinter;
import fr.ensma.lisi.ontoql.engine.util.ASTUtil;
import fr.ensma.lisi.ontoql.engine.util.AliasGenerator;
import fr.ensma.lisi.ontoql.engine.util.ErrorCounter;
import fr.ensma.lisi.ontoql.engine.util.ErrorReporter;
import fr.ensma.lisi.ontoql.engine.util.ParseErrorHandler;
import fr.ensma.lisi.ontoql.evaluator.DMLEvaluator;
import fr.ensma.lisi.ontoql.evaluator.OntoAlgebraEvaluator;
import fr.ensma.lisi.ontoql.evaluator.ontodb.DMLEvaluatorOntoDB;
import fr.ensma.lisi.ontoql.evaluator.ontodb.OntoAlgebraEvaluatorOntoDB;
import fr.ensma.lisi.ontoql.exception.JDBCExceptionHelper;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.NotSupportedDatatypeException;
import fr.ensma.lisi.ontoql.exception.QueryException;
import fr.ensma.lisi.ontoql.exception.SQLExceptionConverterFactory;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSetMetaData;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSessionFactory;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.jobdbc.impl.OntoQLResultSetImpl;
import fr.ensma.lisi.ontoql.ontoapi.Instance;
import fr.ensma.lisi.ontoql.util.ArrayHelper;
import fr.ensma.lisi.ontoql.util.OntoDBHelper;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;
import fr.ensma.lisi.ontoql.util.StringHelper;

/**
 * Implements methods used by the OntoQL->SQL tree transform grammar (a.k.a. the
 * second phase).
 * <ul>
 * <li>Isolates the OntoQL API-specific code from the ANTLR generated code.</li>
 * <li>Uses SqlASTFactory to create customized AST nodes.</li>
 * </ul>
 * 
 * @author Stéphane JEAN
 */
public class OntoQLSQLWalker extends OntoQLSQLBaseWalker implements ErrorReporter {

	/**
	 * A logger for this class.
	 */
	private static Log log = LogFactory.getLog(OntoQLSQLWalker.class);

	/**
	 * A reference to the current session.
	 */
	private OntoQLSession session;

	private String defaultNamespace;

	/**
	 * Alias of the namespaces used in this query
	 */
	private Map<String, String> namespacesAlias = new Hashtable<String, String>();

	/**
	 * A reference to the session factory.
	 */
	private OntoQLSessionFactory sessionFactory;

	/**
	 * A factory to create entity element.
	 */
	private FactoryEntity factoryEntity;

	/**
	 * Handles parser errors.
	 */
	private ParseErrorHandler parseErrorHandler;

	/**
	 * The current context for a SELECT statement.
	 */
	private FromClause currentFromClause = null;

	/**
	 * The current context for an INSERT statement.
	 */
	private IntoClause currentIntoClause = null;

	private ValuesClause currentValueClause = null;

	/**
	 * The top-level SelectClause.
	 */
//    private SelectClause selectClause;

	/**
	 * Generator of alias names for tables.
	 */
	private AliasGenerator aliasGenerator = new AliasGenerator();

	/**
	 * The parser that performed the OntoQL tree.
	 */
//    private OntoQLParser ontoqlParser;

	/**
	 * List of pathProperties to explicit.
	 */
	private Vector<EntityProperty> pathPropertiesProceed = new Vector<EntityProperty>();

	/**
	 * List of typeOf resolved.
	 */
	private Map<String, FromElement> typeOfResolvedHashtable = new Hashtable<String, FromElement>();

	/**
	 * List of rootClass resolved.
	 */
	private Map<String, FromElement> rootClassResolvedHashtable = new Hashtable<String, FromElement>();

	/**
	 * List of FromElement added to explicit join.
	 */
	private Vector<FromElement> fromElementAdded = new Vector<FromElement>();

	/**
	 * List of Expression in first select This list is used to fill
	 * ResultSetMetaData.
	 */
	private List<SelectExpression> expressionInSelect = new ArrayList<SelectExpression>();

	/**
	 * A printer of tree.
	 */
	private ASTPrinter printer;

	private DMLEvaluator dmlEvaluator;

	private OntoAlgebraEvaluator ontoAlgebraEvaluator;

	/**
	 * @return Returns the expressionInSelect.
	 */
	public final List<SelectExpression> getExpressionInSelect() {
		return expressionInSelect;
	}

	/**
	 * @return Returns the factoryEntity.
	 */
	public final FactoryEntity getFactoryEntity() {
		return factoryEntity;
	}

	/**
	 * @return Returns the parseErrorHandler.
	 */
	public final ParseErrorHandler getParseErrorHandler() {
		return parseErrorHandler;
	}

	@Override
	public final void reportError(final RecognitionException e) {
		parseErrorHandler.reportError(e); // Use the delegate.
	}

	@Override
	public final void reportError(final String s) {
		parseErrorHandler.reportError(s); // Use the delegate.
	}

	@Override
	public final void reportWarning(final String s) {
		parseErrorHandler.reportWarning(s);
	}

	/**
	 * Constructor with a sesion and the parser which have generated the walked
	 * tree.
	 * 
	 * @param aSession Database access
	 * @param parser   The parser which have generated the walked tree
	 */
	public OntoQLSQLWalker(final OntoQLSession aSession) {
		this.session = aSession;
		defaultNamespace = session.getDefaultNameSpace();

		this.factoryEntity = OntoQLHelper.constructFactoryEntity(session);

		this.sessionFactory = (OntoQLSessionFactory) session.getSessionFactory();
		setASTFactory(new SQLASTFactory(this));
		this.parseErrorHandler = new ErrorCounter();
		this.printer = new ASTPrinter(SQLTokenTypes.class);
		dmlEvaluator = new DMLEvaluatorOntoDB(this);
		ontoAlgebraEvaluator = new OntoAlgebraEvaluatorOntoDB(this);
	}

	protected void reinitTranslator() {
	}

	public DMLEvaluator getDMLEvaluator() {
		return dmlEvaluator;
	}

	public OntoAlgebraEvaluator getOntoAlgebraEvaluator() {
		return ontoAlgebraEvaluator;
	}

	@Override
	protected final void setAlias(final AST selectExpr, final AST ident) {
		String alias = ident.getText();
		((SelectExpression) selectExpr).setAlias(alias);
	}

	@Override
	protected final void pushFromClause(final AST fromNode, final AST inputFromNode) {
		FromClause newFromClause = (FromClause) fromNode;
		newFromClause.setParentFromClause(currentFromClause);
		currentFromClause = newFromClause;
	}

	@Override
	protected final void processQuery(final AST select, final AST query) throws SemanticException {
		popFromClause();
	}

	/**
	 * Returns to the previous 'FROM' context.
	 */
	private void popFromClause() {
		currentFromClause = currentFromClause.getParentFromClause();
	}

	@Override
	protected final AST createFromElement(final AST node, final AST star, final AST alias, final boolean genAlias)
			throws SemanticException {
		FromElement fromElement = currentFromClause.addFromElement(node, star, alias, genAlias);
		if (fromElement.isImplicitJoin()) {
			fromElement = null;
		}
		return fromElement;
	}

	@Override
	protected AST resolveIsOf(AST instanceNode, boolean neg) {

		FromElement generatedFromElementRootClass = getFromElementRootClass(instanceNode);
		Category generatedRootCategory = generatedFromElementRootClass.getCategory();
		// the attribute projected depends on the type of category
		boolean isClass = generatedRootCategory.isClass();
		String projectedAttribute = isClass ? "classid" : "relname";

		instanceNode.setType(SQLTokenTypes.COLUMN);

		instanceNode.setText(generatedRootCategory.getTableAlias() + "." + projectedAttribute);

		AST inNode = ASTUtil.create(astFactory, OntoQLSQLTokenTypes.IN, "IN");
		inNode.setFirstChild(instanceNode);

		AST inListNode = ASTUtil.create(astFactory, OntoQLSQLTokenTypes.IN_LIST, "IN_LIST");

		AST categoryIteratorNode = instanceNode.getNextSibling();
		inListNode.setFirstChild(categoryIteratorNode);
		AST currentCategoryNode = null;
		// The previous sibling of the current category node
		// must be kept
		AST previousCategoryNode = null;
		Category currentCategory = null;
		Category[] currentSubcategories = null;
		// A boolean is needed to know if each category must
		// be considered with all its subclasses
		boolean isPolymorph = true;
		while (categoryIteratorNode != null) {
			currentCategoryNode = categoryIteratorNode;
			categoryIteratorNode = categoryIteratorNode.getNextSibling();
			if (currentCategoryNode.getType() == OntoQLTokenTypes.ONLY) {
				isPolymorph = false;
				// the ONLY node must be erased
				if (previousCategoryNode == null) {
					inListNode.setFirstChild(categoryIteratorNode);
				} else { // this is not the first node
					previousCategoryNode.setNextSibling(categoryIteratorNode);
				}
			} else {
				AST currentNodeEntity = currentCategoryNode;
				if (currentCategoryNode.getType() == OntoQLTokenTypes.REF) {
					currentNodeEntity = currentCategoryNode.getFirstChild();
				}
				currentCategory = factoryEntity.createCategory(currentNodeEntity.getText());
				String internalId = getTypeOfAsString(currentCategory);
				int typeOfNode = SQLTokenTypes.NUM_INT;
				currentCategoryNode.setText(internalId);
				currentCategoryNode.setType(typeOfNode);
				if (isPolymorph) {
					currentSubcategories = currentCategory.getSubcategories();
					for (int i = 0; i < currentSubcategories.length; i++) {
						internalId = getTypeOfAsString(currentSubcategories[i]);
						ASTUtil.insertSibling(astFactory.create(typeOfNode, internalId), currentCategoryNode);
					}
				}
				isPolymorph = true;
			}
			previousCategoryNode = currentCategoryNode;
		}

		instanceNode.setNextSibling(inListNode);

		return inNode;
	}

	private String getTypeOfAsString(Category cat) {
		String res = null;
		if (cat.isClass()) {
			res = cat.getInternalId();
		} else {
			res = "'" + ((Entity) cat).getDelegateEntity().getMapTo().getName() + "_e'";
		}
		return res;
	}

	private FromElement getFromElementRootClass(AST instanceNode) {

		IdentNode descriptionNode = ((IdentNode) instanceNode);
		String instanceAlias = instanceNode.getText();
		FromElement res = (FromElement) rootClassResolvedHashtable.get(instanceAlias);
		if (res == null) {
			// the root class must be genereated and joined
			FromElement fromElementInstance = descriptionNode.getFromElement();
			if (fromElementInstance == null) {
				fromElementInstance = getCurrentFromClause().getFromElement(instanceAlias);
			}

			Category instanceCategory = fromElementInstance.getCategory();

			// the root class is pg_class for an entity
			// and instances for a class
			String condition = null;
			if (instanceCategory.isEntity()) {
				Entity typeOfEntity = new Entity("pg_class");
				// the suffix _e must not be added
				// so this entity is declared as an association table
				typeOfEntity.getDelegateEntity().getMapTo().setAssociationTable(true);
				res = new FromElement(typeOfEntity, false, this);
				condition = instanceCategory.getTableAlias() + ".tableoid = " + typeOfEntity.getTableAlias() + ".oid";
			} else {
				EntityClass rootClass = factoryEntity.createEntityClassRoot();
				res = new FromElement(rootClass, false, this);
				condition = instanceCategory.getTableAlias() + ".rid = " + rootClass.getTableAlias() + ".rid";
			}

			AST innerJoinRootClass = ASTUtil.create(astFactory, OntoQLSQLTokenTypes.INNER_JOIN, "INNER JOIN");

			AST joinConditionRootClass = ASTUtil.create(astFactory, OntoQLSQLTokenTypes.JOIN_CONDITION,
					"ON " + condition);
			fromElementInstance.addChild(innerJoinRootClass);
			fromElementInstance.addChild(res);
			fromElementInstance.addChild(joinConditionRootClass);

			// the resolved rootClass element must be registered
			rootClassResolvedHashtable.put(instanceAlias, res);

		}
		return res;
	}

	@Override
	protected AST resolveTypeOf(AST instanceAliasNode) {

		String instanceAlias = instanceAliasNode.getText();

		FromElement generatedFromElementMetaClass = (FromElement) typeOfResolvedHashtable.get(instanceAlias);
		if (generatedFromElementMetaClass == null) {
			// the typeOf must be resolved
			FromElement generatedFromElementRootClass = getFromElementRootClass(instanceAliasNode);

			Entity metaClass = factoryEntity.createEntity("#class");
			metaClass.setCategoryAlias(aliasGenerator.createName("class"));
			generatedFromElementMetaClass = new FromElement(metaClass, false, this);
			currentFromClause.registerFromElement(generatedFromElementMetaClass);
			AST innerJoinMetaClass = ASTUtil.create(astFactory, OntoQLSQLTokenTypes.INNER_JOIN, "INNER JOIN");
			String condition = metaClass.getTableAlias() + ".rid_bsu = "
					+ generatedFromElementRootClass.getCategory().getTableAlias() + ".classid";
			AST joinConditionMetaClass = ASTUtil.create(astFactory, OntoQLSQLTokenTypes.JOIN_CONDITION,
					"ON " + condition);
			generatedFromElementRootClass.addChild(innerJoinMetaClass);
			generatedFromElementRootClass.addChild(generatedFromElementMetaClass);
			generatedFromElementRootClass.addChild(joinConditionMetaClass);
			// the resolved typeOf element must be registered
			typeOfResolvedHashtable.put(instanceAlias, generatedFromElementMetaClass);
		}

		instanceAliasNode.setText(generatedFromElementMetaClass.getCategory().getCategoryAlias());

		return instanceAliasNode;

	}

	@Override
	protected AST unnest(AST propertyNode, AST aliasNode) {
		IdentNode identPropertyNode = (IdentNode) propertyNode;
		// check that the property to unnest is a collection
		Description description = identPropertyNode.getDescription();
		EntityDatatype typeOfDescription = description.getRange();
		if (!typeOfDescription.isCollectionAssociationType()) {
			// for the moment only collection of ref can be unnested.
			throw new JOBDBCException("Only a collection of reference can be unnested");
		}
		// type of the collection
		EntityDatatypeCategory rangeDatatypeOfProperty = (EntityDatatypeCategory) ((EntityDatatypeCollection) typeOfDescription)
				.getDatatype();
		Category rangeCategoryOfDescription = rangeDatatypeOfProperty.getCagetory();

		if (rangeCategoryOfDescription.isEntity()) {
			rangeCategoryOfDescription = (Category) ((Entity) rangeCategoryOfDescription).clone();
		}

		rangeCategoryOfDescription.setCategoryAlias(aliasNode.getText());

		// get the from element associated with this property
		FromElement joinFromElement = identPropertyNode.getFromElement();

		FromElement generatedFromElement = new FromElement(rangeCategoryOfDescription, true, this);
		currentFromClause.registerFromElement(generatedFromElement);

		// 2 cases : 1) unnest of a collection property/attribut of an iterator
		// in the same from clause
		// 2) unnest of a collection property/attribut of an iterator in an
		// other from clause (subquery)
		boolean isUnnestSubquery = joinFromElement.getFromClause().getLevel() != currentFromClause.getLevel();

		String condition;
		if (rangeCategoryOfDescription.isClass()) {
			EntityProperty ridProperty = (EntityProperty) factoryEntity.createDescription("oid");
			condition = ridProperty.toSQL(rangeCategoryOfDescription) + " = any (" + identPropertyNode.getSQL() + ")";
		} else {
			// does the range is identified by a BSU (yes => rid_bsu)
			if (((Entity) rangeCategoryOfDescription).isIdentifiedByBSU()) {
				condition = rangeCategoryOfDescription.getTableAlias() + ".rid_bsu" + " = any ("
						+ identPropertyNode.getSQL() + "::BIGINT[])";
			} // if not, use the rid
			else
				condition = rangeCategoryOfDescription.getTableAlias() + ".rid" + " = any ("
						+ identPropertyNode.getSQL() + ")";
		}

		if (isUnnestSubquery) { // case 2) the join is implicit
			currentFromClause.addChild(generatedFromElement);
			// we must only add the join condition
			currentFromClause.addWhereCondition(condition);
		} else { // case 1) the join must be explicited

			AST innerJoin = ASTUtil.create(astFactory, OntoQLSQLTokenTypes.INNER_JOIN, "INNER JOIN");

			AST joinCondition = ASTUtil.create(astFactory, OntoQLSQLTokenTypes.JOIN_CONDITION, "ON " + condition);
			joinFromElement.addChild(innerJoin);
			joinFromElement.addChild(generatedFromElement);
			joinFromElement.addChild(joinCondition);
		}
		return joinFromElement;

	}

	@Override
	protected AST addWhereClause(AST whereClauseBuilt, AST logicalExpression) {
		AST res = whereClauseBuilt;
		res.setFirstChild(logicalExpression);

		AST currentWhereClause = currentFromClause.getNextSibling();
		if (currentWhereClause != null && currentWhereClause.getType() == OntoQLTokenTypes.WHERE) {
			// a where clause has already been added
			// the logical expression must be attached using an AND
			AST andClause = ASTUtil.create(getASTFactory(), OntoQLSQLTokenTypes.AND, "and");
			andClause.addChild(logicalExpression);
			andClause.addChild(currentWhereClause.getFirstChild());
			currentWhereClause.setFirstChild(andClause);
			res = null;
		}

		return res;
	}

	@Override
	protected final AST resolve(final AST node, final AST prefix) throws SemanticException {
		ResolvableNode r = (ResolvableNode) node;
		return r.resolve(prefix);
	}

	/**
	 * If the path has already been resolved return the corresponding from element
	 * generated.
	 * 
	 * @param property association property for which we search the range
	 * @return the from element if the path has already been resolved, else null
	 */
	public final FromElement getGeneratedFromElement(final EntityProperty property) {
		FromElement res = null;
		EntityProperty currentPathProperty;
		for (int i = 0; i < pathPropertiesProceed.size(); i++) {
			currentPathProperty = (EntityProperty) pathPropertiesProceed.elementAt(i);
			if (isEquivalent(property, currentPathProperty)) {
				res = (FromElement) fromElementAdded.elementAt(i);
			}
		}
		return res;
	}

	/**
	 * Add a join element to resolve a path expression.
	 * 
	 * @param toThisFromElement from element on which a join is linked
	 * @param thisFromElement   new from element
	 * @param condition         condition of join
	 * @param isOptional        true if an outer join must be added
	 */
	public final void addJoin(final FromElement toThisFromElement, final FromElement thisFromElement,
			final String condition, final boolean isOptional) {
		AST join = null;
		if (isOptional) {
			join = ASTUtil.create(astFactory, OntoQLSQLTokenTypes.LEFT_OUTER, "LEFT OUTER JOIN");
		} else {
			join = ASTUtil.create(astFactory, OntoQLSQLTokenTypes.INNER_JOIN, "INNER JOIN");
		}

		AST joinCondition = ASTUtil.create(astFactory, OntoQLSQLTokenTypes.JOIN_CONDITION, condition);
		// We add this join before all other join
		// because an other join may be dependant of this one
		ASTUtil.insertChild(toThisFromElement, joinCondition);
		ASTUtil.insertChild(toThisFromElement, thisFromElement);
		ASTUtil.insertChild(toThisFromElement, join);
		fromElementAdded.add(thisFromElement);
	}

	public FromElement addImplicitJoin(FromElement fromElement, IdentNode pathPropNode, boolean polymorph)
			throws SemanticException {

		EntityProperty pathProp = (EntityProperty) pathPropNode.getDescription();
		AST leftJoin = ASTUtil.create(astFactory, OntoQLSQLTokenTypes.LEFT_OUTER, "LEFT OUTER JOIN");

		// AST star = polymorph ? leftJoin : null;

		AbstractEntityClass rangeClass = (AbstractEntityClass) ((EntityDatatypeCategory) pathProp.getRange())
				.getCagetory();
		FromElement generatedFromElement = new FromElement(rangeClass, true, this);
		// change true by polymorph if you want that a non polymorphic query
		// is also non polymorphic on path expression

		EntityProperty ridProperty = (EntityProperty) factoryEntity.createDescription("oid");
		String condition = "ON " + pathPropNode.getSQL() + " = " + ridProperty.toSQL(rangeClass);

		AST joinCondition = ASTUtil.create(astFactory, OntoQLSQLTokenTypes.JOIN_CONDITION, condition);

		fromElement.addChild(leftJoin);
		fromElement.addChild(generatedFromElement);
		fromElement.addChild(joinCondition);

		pathPropertiesProceed.add(pathProp);
		fromElementAdded.add(generatedFromElement);

		return generatedFromElement;
	}

	/**
	 * Generate an alias for the range of the path property
	 */
	public void genereAlias(AbstractEntityClass range) {
		String aliasResultat = aliasGenerator.createName(range.getInternalId());
		range.setCategoryAlias(aliasResultat);
	}

	/**
	 * Compare two path property used in the path expression resolution
	 */
	protected boolean isEquivalent(EntityProperty propertyToMatch, EntityProperty property) {
		boolean isEquivalent = false;
		if (isEquivalentContext((AbstractEntityClass) propertyToMatch.getCurrentContext(),
				(AbstractEntityClass) property.getCurrentContext())) {
			if (propertyToMatch.getInternalId().equals(property.getInternalId())) {
				isEquivalent = true;
			}
		}
		return isEquivalent;
	}

	/**
	 * Compare two context of path property in the path expression resolution
	 */
	protected boolean isEquivalentContext(AbstractEntityClass contextToMatch, AbstractEntityClass context) {
		boolean isEquivalent = false;
		if (contextToMatch == null && context == null) {
			isEquivalent = true;
		} else if (contextToMatch != null && context != null) {
			String contextToMatchAlias = contextToMatch.getCategoryAlias();
			String contextAlias = context.getCategoryAlias();
			if (contextToMatchAlias != null && contextAlias != null) {
				isEquivalent = contextToMatchAlias.equals(contextAlias);
			} else if (contextToMatchAlias == null && contextAlias == null) {
				String contextToMatchId = contextToMatch.getInternalId();
				String contextId = context.getInternalId();
				if (contextToMatchId == null && contextId == null) {
					isEquivalent = true;
				} else if (contextToMatchId != null && contextId != null)
					isEquivalent = contextToMatchId.equals(contextId);
			}
		}
		return isEquivalent;

	}

	protected void checkType(AST node, AST operatorNode) {
		EntityDatatype datatype = ((SelectExpression) node).getDataType();
		if (datatype != null) {
			String[] operators = ArrayHelper.join(datatype.getBooleanOperators(), datatype.getArithmeticOperators());
			String operator = operatorNode.getText();
			// TODO Improvate by testing datatype compatibility
			// when calling some functions
			if (!operator.equals("(")) { // this is a function call
				if (!ArrayHelper.contain(operators, operator.toUpperCase())) {
					String msg = ((SelectExpression) node).getLabel();
					throw new QueryException("The operator '" + operator + "' can not be used on " + msg);
				}
			}
		}
	}

	protected void checkType(AST nodeLeft, AST operatorNode, AST nodeRight) {
		checkType(nodeLeft, operatorNode);
		if (nodeRight != null) {
			checkType(nodeRight, operatorNode);
		}

		// We need to cast an integer into a bigint if
		// we do this concatenation prop_collection_ref || int
		// need to be revised (done for ewokhub)
		String operator = operatorNode.getText();
		if (operator.equals(EntityDatatype.OP_CONCAT)) {
			AST nodeExprList = operatorNode.getFirstChild().getNextSibling();
			AST nodeValue = nodeExprList.getFirstChild();
			if (nodeValue.getType() == OntoQLTokenTypes.NUM_INT) {
				// add the cast and the tablename (latter, must be done
				// elsewhere)
				String nodeRightText = nodeRight.getText();
				int indexOfPoint = nodeRightText.indexOf('.');
				nodeRightText = nodeRightText.substring(indexOfPoint + 1, nodeRightText.length());
				nodeRight.setText(nodeRightText);
				String columnTablename = nodeRight.getText().replaceFirst("rids", "tablenames");
				try {
					Category rangeCategory = ((EntityDatatypeCategory) ((EntityDatatypeCollection) ((IdentNode) nodeRight)
							.getDataType()).getDatatype()).getCagetory();
					String fromElement = "!" + rangeCategory.getInternalId();
					OntoQLStatement stmt = getSession().createOntoQLStatement();
					OntoQLResultSet resultset = stmt
							.executeQuery("select i from " + fromElement + " i where oid=" + nodeValue.getText());
					if (!resultset.next()) {
						throw new JOBDBCException(
								nodeValue.getText() + " is not an instance of " + rangeCategory.getName());
					} else {
						Instance i = resultset.getInstance(1);
						String tablename = "'e" + i.getBaseType().getInternalId() + "'";
						nodeValue.setText(nodeValue.getText() + "::bigint");
						nodeRight.setText(nodeRight.getText() + ", " + columnTablename + " = " + tablename
								+ "::varchar || " + columnTablename);

					}
				} catch (SQLException e) {
					throw new JOBDBCException(e.getMessage());
				}
			}

		}

	}

	protected void addSelectExpr(AST node) {
		// A Select Expression is added only if this is the top level query
		// or if it is a sub query in a DML statement
		if ((currentFromClause.getParentFromClause() == null) || (!isSelectStatement())) {

			// Moreover the projection must be manually managed (for example see
			// Star node)
			// So, we must check if we must add this automatically
			boolean isIdentNode = node instanceof IdentNode;
			if (!isIdentNode || ((IdentNode) node).isToAddInProjectionList()) {
				expressionInSelect.add((SelectExpression) node);
			}
		}
	}

	/**
	 * Create a node corresponding to a given description (property or attribute
	 * 
	 * @param fromElement the element in the from clause related to this description
	 * @param description an attribute or property
	 * @return the node corresponding to this description
	 */
	public IdentNode createDescriptionNode(FromElement fromElement, Description description) {
		// The result is a node corresponding to a description (property or
		// attribute)
		IdentNode res = null;

		String identifier = description.isAttribute() ? "#" + description.getName() : "!" + description.getInternalId();
		// The result node
		res = (IdentNode) ASTUtil.create(getASTFactory(), SQLTokenTypes.IDENT, identifier);
		res.setDescription(description);
		res.setFromElement(fromElement);

		return res;
	}

	/**
	 * Create an oid node corresponding to a given class
	 * 
	 * @param aClass a class
	 * @return the oid node corresponding to this class
	 */
	public IdentNode createPropertyOidNode(FromElement fromElement) {
		// The result is a node corresponding to an oid property
		IdentNode res = null;

		// The class corresponding to the from element
		AbstractEntityClass aClass = (AbstractEntityClass) fromElement.getCategory();
		// The property corresponding to the result node
		EntityProperty propOid = getFactoryEntity().createEntityPropertyOid(aClass);

		// The result node
		res = createDescriptionNode(fromElement, propOid);

		return res;
	}

	/**
	 * Create a type node corresponding to a given class
	 * 
	 * @param aClass a class
	 * @return the typeof node corresponding to this class
	 */
	public IdentNode createPropertyTypeOfNode(FromElement fromElement) {
		// The result is a node corresponding to an oid property
		IdentNode res = null;

		// The class corresponding to the from element
		AbstractEntityClass aClass = (AbstractEntityClass) fromElement.getCategory();
		// The property corresponding to the result node
		EntityProperty propTypeOf = getFactoryEntity().createEntityPropertyTypeOf(aClass);

		// The result node
		res = createDescriptionNode(fromElement, propTypeOf);

		return res;
	}

	/**
	 * Create a list of node corresponding to the list of the defined properties on
	 * a from element
	 * 
	 * @param fromElement a from element
	 * @param lastRes     the last element of the result list
	 * @param addInSelect True if each element of the list must be added to the
	 *                    projection list
	 * @return the list of the defined properties on a from element
	 */
	public IdentNode[] createDescriptionsNodes(FromElement fromElement, boolean addInSelect, boolean resolveAssociation)
			throws SemanticException {

		// The result is the first and last node of a linked list of node
		// corresponding to the different properties
		// The list may be empty
		IdentNode[] res = new IdentNode[2];

		Description[] descriptions = null;
		if (fromElement.isEntityFromElement()) {
			descriptions = ((Entity) fromElement.getCategory()).getDefinedAttributes();
		} else {
			// List of properties defined on this class
			descriptions = ((EntityClass) fromElement.getCategory()).getDefinedProperties();
		}

		// iterator on properties
		Description currentDescription;
		// iterator on nodes
		IdentNode currentDescriNode;

		for (int i = 0; i < descriptions.length; i++) {
			currentDescription = descriptions[i];
			currentDescription.setCurrentContext(fromElement.getCategory());
			currentDescriNode = createDescriptionNode(fromElement, currentDescription);
			// translate this node in SQL (not a path)
			IdentNode[] currentPropNodeTranslated = currentDescriNode.translateToSQL(false, resolveAssociation);
			// the first node is the result
			if (i == 0) {
				res[0] = currentPropNodeTranslated[0];
			} else {// lastRes is not null
				res[1].setNextSibling(currentPropNodeTranslated[0]);
			}
			res[1] = currentPropNodeTranslated[1];

			// add it eventually to the projection list
			if (addInSelect) {
				getExpressionInSelect().add(currentDescriNode);
			}

		}

		return res;

	}

	@Override
	protected void postProcessCreate(AST create) throws SemanticException {
		((CreateStatement) create).process();
	}

	@Override
	protected void postProcessAlter(AST alter) throws SemanticException {
		((AlterStatement) alter).process();
	}

	@Override
	protected void postProcessDrop(AST dropNode) throws SemanticException {
		((DropStatement) dropNode).process();
	}

	@Override
	protected AST resolveTreatFunction(AST function) throws SemanticException {
		MethodNode treatFunction = (MethodNode) function;
		treatFunction.resolve(true);
		// check if this function is really the treat function
		if (!treatFunction.getMethodName().equalsIgnoreCase("treat")) {
			throw new JOBDBCException("Only the treat function can be used in a path expression");
		}
		// get the data of this function
		// a good refactor will be to export this code
		// into a dedicated TreatFunction class, subclass of MethodNode
		AST exprList = treatFunction.getFirstChild().getNextSibling();
		AST instanceNode = exprList.getFirstChild();
		AST downClassNode = instanceNode.getNextSibling();
		String instanceText = instanceNode.getText();
		String downClassText = downClassNode.getText();

		// latter it must be a category
		FromElement fromElementOfInstance = currentFromClause.getFromElement(instanceText);
		EntityClass currentClass = (EntityClass) fromElementOfInstance.getCategory();
		if (!currentClass.isPolymorph()) {
			throw new JOBDBCException("Can not downcast the instance " + instanceText
					+ " of the non polymorphic element " + currentClass.getName());
		}
		EntityClass downClass = factoryEntity.createEntityClass(downClassText);
		fromElementOfInstance.processTreatAs(downClass);

		// the result node is the reference to the instance without sibling
		instanceNode.setNextSibling(null);
		return instanceNode;
	}

	@Override
	protected void processInsertTarget(AST intoClause, AST element) throws SemanticException {
		IntoClause currentIntoClause = (IntoClause) intoClause;
		currentIntoClause.initialize(element.getText());
	}

	@Override
	protected void processInsertColumnElement(AST intoColumnElement) throws SemanticException {
		IdentNode currentIntoColumnElement = (IdentNode) intoColumnElement;
		IntoClause currentIntoClause = getCurrentIntoClause();
		Description descriptionValuated = currentIntoColumnElement
				.loadDescription(currentIntoClause.getCategoryInstantiated());
		currentIntoClause.addDescription(descriptionValuated);
		dmlEvaluator.evaluateDescription(descriptionValuated, currentIntoColumnElement);
	}

	@Override
	protected AST addTypeOfUpdate(AST assignmentNode) {
		AST res = assignmentNode;
		AST attributeNode = null;
		AST valueNode = null;
		int currentIndex = 0;
		AST typeOfValueNode;
		Boolean hasChanged = new Boolean(false);
		List<AST> newNodes = new ArrayList<AST>();
		while (assignmentNode != null) {
			AST typeOfAttributNode = null;
			attributeNode = assignmentNode.getFirstChild();
			valueNode = attributeNode.getNextSibling();

			Description description = ((IdentNode) attributeNode).getDescription();
			boolean isAssociationType = false;
			boolean isCollectionAssociationType = false;

			if (description.getRange().isAssociationType()) {
				String typeOfText = description.toSQL().replaceFirst("rid", "tablename");
				typeOfAttributNode = ASTUtil.create(getASTFactory(), SQLTokenTypes.COLUMN, typeOfText);
				isAssociationType = true;
			}

			if (description.getRange().isCollectionAssociationType()) {
				String typeOfText = description.toSQL().replaceFirst("rids", "tablenames");
				typeOfAttributNode = ASTUtil.create(getASTFactory(), SQLTokenTypes.COLUMN, typeOfText);
				isCollectionAssociationType = true;
			}

			if (typeOfAttributNode != null) {
				if (isQueryNode(valueNode)) {
					AST newAssignmentNode = ASTUtil.create(getASTFactory(), SQLTokenTypes.EQ, "=");
					newAssignmentNode.addChild(typeOfAttributNode);
					typeOfValueNode = replaceSubQuery(valueNode, currentIndex, hasChanged);
					if (typeOfValueNode != null) {
						newAssignmentNode.addChild(typeOfValueNode);
						ASTUtil.insertSibling(newAssignmentNode, assignmentNode);
						assignmentNode = assignmentNode.getNextSibling();
					}
				} else {
					if (isAssociationType) {
						String referenceValue = valueNode.getText();
						Category category = ((EntityDatatypeCategory) description.getRange()).getCagetory();
						String fromElement = "!" + (category.getInternalId());

						if (referenceValue != null && !referenceValue.equals("null")) {
							String tableName = null;
							OntoQLResultSet resultset = session.createOntoQLStatement()
									.executeQuery("select i from " + fromElement + " i where oid =" + referenceValue);

							try {
								if (resultset.next()) {
									Instance instance = resultset.getInstance(1);
									tableName = "'e" + instance.getBaseType().getInternalId() + "'";
								} else {
									throw new JOBDBCException("the values of " + category.getName()
											+ " are not all instances of " + category.getName());
								}
							} catch (SQLException e) {
								throw new JOBDBCException(e.getMessage());
							}

							if (tableName != null) {
								AST newAssignmentNode = ASTUtil.create(getASTFactory(), SQLTokenTypes.EQ, "=");
								newAssignmentNode.addChild(typeOfAttributNode);
								ASTUtil.insertSibling(newAssignmentNode, assignmentNode);
								newNodes.add(newAssignmentNode);
								AST value1 = ASTUtil.create(getASTFactory(), SQLTokenTypes.NUM_INT, tableName);
								ASTUtil.insertSibling(assignmentNode, value1);
							}
						}

					} else if (isCollectionAssociationType) {
						List<String> referenceValues = new ArrayList<String>();

						if (!(valueNode instanceof MethodNode)) {
							AST referenceValue = valueNode.getFirstChild();
							while (referenceValue != null) {
								referenceValues.add(referenceValue.getText());
								referenceValue = referenceValue.getNextSibling();
							}

							EntityDatatypeCollectionOntoDB ref = (EntityDatatypeCollectionOntoDB) description
									.getRange();
							Category category = ((EntityDatatypeCategory) ref.getDatatype()).getCagetory();
							String fromElement = "!" + (category.getInternalId());

							if (referenceValues.size() > 0) {
								List<String> tableNames = new ArrayList<String>();
								for (int i = 0; i < referenceValues.size(); i++) {
									OntoQLResultSet resultset = session.createOntoQLStatement().executeQuery(
											"select i from " + fromElement + " i where oid =" + referenceValues.get(i));
									try {
										if (resultset.next()) {
											Instance instance = resultset.getInstance(1);
											tableNames.add("'e" + instance.getBaseType().getInternalId() + "'");
										} else {
											throw new JOBDBCException("the values of " + category.getName()
													+ " are not all instances of " + category.getName());
										}
									} catch (SQLException e) {
										throw new JOBDBCException(e.getMessage());
									}
								}

								if (tableNames.size() > 0) {
									AST newAssignmentNode = ASTUtil.create(getASTFactory(), SQLTokenTypes.EQ, "=");
									newAssignmentNode.addChild(typeOfAttributNode);
									ASTUtil.insertSibling(newAssignmentNode, assignmentNode);
									newNodes.add(newAssignmentNode);

									AST arrayAssignmentNode = ASTUtil.create(getASTFactory(), SQLTokenTypes.ARRAY,
											"ARRAY");
									ASTUtil.appendSibling(typeOfAttributNode, arrayAssignmentNode);

									for (String currentTableName : tableNames) {
										AST value1 = ASTUtil.create(getASTFactory(), SQLTokenTypes.QUOTED_STRING,
												currentTableName);
										ASTUtil.insertChild(arrayAssignmentNode, value1);
									}
								}
							}
						}
					} else {
						throw new JOBDBCException("Not Yet Implemented");
					}
				}
			}

			replaceSubQuery(valueNode, currentIndex, hasChanged);

			assignmentNode = assignmentNode.getNextSibling();
			while (newNodes.contains(assignmentNode)) {
				assignmentNode = assignmentNode.getNextSibling();
			}

			if (hasChanged.booleanValue()) {
				currentIndex++;
			}

		}
		return res;

	}

	/**
	 * Reinitialise ce composant dans une requête union
	 */
	protected void reinitWalker() {
		expressionInSelect = new ArrayList<SelectExpression>();
	}

	protected boolean isQueryNode(AST node) {
		return (node.getType() == SQLTokenTypes.SELECT);
	}

	/**
	 * Replace a subQuery at the given index in expressionInSelect by a value in DML
	 * statement
	 * 
	 * @param the        subquery node
	 * @param the        index of this node in expressionInSelect
	 * @param isSubQuery Out parameter stating is this node was a subquery
	 * @return The typeof value node of this subquery if is a query that return an
	 *         instance
	 */
	protected AST replaceSubQuery(AST exprRefNode, int currentIndex, Boolean isSubQuery) {
		AST res = null;

		if (exprRefNode.getType() == SQLTokenTypes.SELECT) {
			isSubQuery = new Boolean(true);

			try {
				SQLGenerator gen = new SQLGenerator(this.session);
				gen.statement(exprRefNode);
				String sql = gen.getSQL();
				gen.getParseErrorHandler().throwQueryException();
				log.warn("generated SQL : " + sql);
				Statement st = session.connection().createStatement();
				OntoQLResultSet rs = new OntoQLResultSetImpl(st.executeQuery(sql),
						expressionInSelect.subList(currentIndex, currentIndex + 1), factoryEntity, session);
				OntoQLResultSetMetaData rsmd = rs.getOntoQLMetaData();
				String typeName = rsmd.getColumnTypeName(1);
				if (!rs.next()) {
					throw new JOBDBCException("A subquery must retrieve at least one result");
				} else {
					if (typeName.equals(EntityDatatype.ASSOCIATION_NAME)) {
						if (rs.next()) {
							throw new JOBDBCException("A subquery return more than one result for an association");
						}
						Instance i = rs.getInstance(1);
						exprRefNode.setType(SQLTokenTypes.NUM_INT);
						exprRefNode.setText(i.getOid());
						exprRefNode.setFirstChild(null);
						String typeOfText = "'e" + i.getBaseType().getInternalId() + "'";
						res = ASTUtil.create(getASTFactory(), SQLTokenTypes.QUOTED_STRING, typeOfText);
					} else if (typeName.equals(EntityDatatype.INT_NAME)) {
						exprRefNode.setType(SQLTokenTypes.NUM_INT);
						exprRefNode.setText(rs.getString(1));
					} else {
						exprRefNode.setType(SQLTokenTypes.QUOTED_STRING);
						exprRefNode.setText("'" + rs.getString(1) + "'");
					}
					exprRefNode.setFirstChild(null);
				}

			} catch (RecognitionException exc) {
				throw new JOBDBCException(exc);
			}

			catch (SQLException sqle) {
				throw JDBCExceptionHelper.convert(SQLExceptionConverterFactory.buildMinimalSQLExceptionConverter(),
						sqle, "could not execute query", "");
			}
		}

		return res;
	}

	/**
	 * Method used by Star Node and Ident Node to genrate a list of Ident Nodes
	 * corresponding to the properties defined on a class.
	 * 
	 * @param aClass     class which are unfold
	 * @param nodeToLink node on which the list of ident node are linked (can be
	 *                   null)
	 * @return the first node of the list of properties
	 */
	public AST createPropertyNode(EntityClass aClass, AST nodeToLink) {
		EntityProperty[] props = aClass.getDefinedProperties();

		if (props.length == 0) {
			return null;
		}
		// The result is the first node
		AST firstPropNode = null;
		AST propNode = null;
		AST currentNode = null;

		for (int i = 0; i < props.length; i++) {
			propNode = ASTUtil.create(getASTFactory(), SQLTokenTypes.IDENT, props[i].toSQL());
			((IdentNode) propNode).setDescription(props[i]);
			propNode.setType(SQLTokenTypes.COLUMN);
			if (currentNode != null) {
				ASTUtil.appendSibling(currentNode, propNode);
			} else { // firstNode
				firstPropNode = propNode;
			}
			currentNode = propNode;

			// If it's a relationship property/attribute we
			// must get the typeOf the instance
			try {
				if (props[i].getRange().getName().equals("REF")) {
					AST typeOfNode = ASTUtil.create(getASTFactory(), SQLTokenTypes.IDENT,
							props[i].toSQL().replaceFirst("rid", "tablename"));
					((IdentNode) typeOfNode).setDescription(props[i]);
					typeOfNode.setType(SQLTokenTypes.COLUMN);
					ASTUtil.appendSibling(currentNode, typeOfNode);
					currentNode = typeOfNode;
				}
			} catch (NotSupportedDatatypeException oExc) {
			}

			if (i == props.length - 1 && nodeToLink != null) {
				ASTUtil.appendSibling(currentNode, nodeToLink);
			}
		}

		return firstPropNode;

	}

	/**
	 * Resolve a function call (yet only concat is implemented)
	 */
	protected void processFunction(AST functionCall, boolean inSelect) throws SemanticException {
		MethodNode methodNode = (MethodNode) functionCall;
		methodNode.resolve(inSelect);
	}

	public void showAst(AST ast, PrintStream out) {
		showAst(ast, new PrintWriter(out));
	}

	private void showAst(AST ast, PrintWriter pw) {
		printer.showAst(ast, pw);
	}

	public static void panic() {
		throw new QueryException("TreeWalker: panic");
	}

	/**
	 * @return Returns the session.
	 */
	public OntoQLSession getSession() {
		return session;
	}

	/**
	 * @return Returns the session.
	 */
	public AliasGenerator getAliasGenerator() {
		return aliasGenerator;
	}

	/**
	 * @return Returns the currentFromClause.
	 */
	public FromClause getCurrentFromClause() {
		return currentFromClause;
	}

	/**
	 * @return Returns the pathPropertiesToProceed.
	 */
	public Vector getPathPropertiesProceed() {
		return pathPropertiesProceed;
	}

	/**
	 * @return Returns the sessionFactory.
	 */
	public OntoQLSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public Vector getFromElementAdded() {
		return fromElementAdded;
	}

	/**
	 * @return the currentIntoClause
	 */
	public IntoClause getCurrentIntoClause() {
		return currentIntoClause;
	}

	@Override
	protected void setIntoClause(AST intoClause) {
		currentIntoClause = (IntoClause) intoClause;
	}

	@Override
	protected void setValuesClause(AST valuesClause) {
		currentValueClause = (ValuesClause) valuesClause;
	}

	@Override
	protected void addValueInInsert(AST valueElement) throws SemanticException {
		AbstractSelectExpression valueNode = (AbstractSelectExpression) valueElement;
		String valueToAdd = valueNode.getText();
		if (valueNode.getType() == OntoQLTokenTypes.ARRAY) {
			AST firstChild = valueNode.getFirstChild();
			if (firstChild.getType() != OntoQLTokenTypes.QUERY) {
				valueToAdd = "[" + getArrayValues(firstChild) + "]";
			}
		}
		currentValueClause.addValue(valueToAdd);
		currentValueClause.addValueType(valueNode.getDataType());
	}

	private String getArrayValues(AST node) {
		String res = node.getText();
		AST currentNode = node.getNextSibling();
		while (currentNode != null) {
			res += ", " + currentNode.getText();
			currentNode = currentNode.getNextSibling();
		}
		return res;
	}

	@Override
	protected void addQueryInInsert(AST queryElement) throws SemanticException {
		SelectStatement select = (SelectStatement) queryElement;
		SelectExpression projectElement = (SelectExpression) select.getSelectClause().getFirstSelectExpression();
		currentValueClause.addValue(""); // the value is unknown until the
		// query is executed
		currentValueClause.addValueType(projectElement.getDataType());
	}

	/**
	 * Define a given namespace as the default local namespace for the statement.
	 */
	protected void setLocalNamespace(AST nodeNamespace, AST nodeAliasNamespace) {
		String namespaceValue = StringHelper.removeFirstAndLastletter(nodeNamespace.getText());
		if (nodeAliasNamespace == null) {
			this.defaultNamespace = namespaceValue;
		} else {
			this.namespacesAlias.put(nodeAliasNamespace.getText(), namespaceValue);
		}
	}

	/**
	 * Define a given namespace as the default globalnamespace.
	 */
	protected void setGlobalNamespace(AST nodeNamespace) {

		if (nodeNamespace == null) {
			session.setDefaultNameSpace(OntoQLHelper.NO_NAMESPACE);
		} else {
			// 1. Check that this namespace exists
			String namespaceValueWithSyntax = nodeNamespace.getText();
			// in the syntax the namespace is around by the character '
			String namespaceValue = StringHelper.removeFirstAndLastletter(namespaceValueWithSyntax);
			String queryNamespace = "select #namespace from #ontology where #namespace = " + namespaceValueWithSyntax;
			OntoQLStatement stmt = session.createOntoQLStatement();
			try {
				OntoQLResultSet resultset = stmt.executeQuery(queryNamespace);
				if (!resultset.next()) {
					throw new JOBDBCException("The namespace " + namespaceValue + " doesn't exist");
				}
			} catch (SQLException e) {
				throw new JOBDBCException(e);
			}
			// 2. If everything is ok, set the new global default namespace
			session.setDefaultNameSpace(namespaceValue);
		}
	}

	/**
	 * Define a given language as the default language.
	 */
	protected void setGlobalLanguage(AST nodeLanguage) {

		if (nodeLanguage == null) { // no language
			session.setReferenceLanguage(OntoQLHelper.NO_LANGUAGE);
		} else {
			// 1. Check that the language is available
			String language = nodeLanguage.getText().toLowerCase();
			if (!OntoQLHelper.isLanguageAvailable(language)) {
				throw new JOBDBCException("The language " + language + " is not available");
			}
			// 2. If everything is ok, set the new global default language
			session.setReferenceLanguage(language);
		}
	}

	/**
	 * @return the defaultNamespace
	 */
	public String getDefaultNamespace() {
		return defaultNamespace;
	}

	/**
	 * @return the namespace corresponding to a given alias
	 */
	public final String getNamespace(String alias) {
		return (String) namespacesAlias.get(alias);
	}

	protected void rewriteQueryWithPreference(AST nodePreference) {
		String prefLabel = nodePreference.getText();
		// 1) Search the semantics of the preference in the ontology
		String query = "select p.#oid, pref.#min, pref.#max from #property as p, unnest(p.#preferences) as pref where pref.#label ='"
				+ prefLabel + "'";
		try {
			OntoQLStatement statement = session.createOntoQLStatement();
			OntoQLResultSet resultset = statement.executeQuery(query);
			if (!resultset.next()) {
				throw new QueryException("The preference " + prefLabel + " is not defined");
			} else {
				String propertyOid = resultset.getString(1);
				String minValue = resultset.getString(2);
				String maxValue = resultset.getString(3);
				getCurrentFromClause()
						.addWhereCondition("p" + propertyOid + " BETWEEN " + minValue + " AND " + maxValue);
			}
		} catch (SQLException oExc) {
			throw new QueryException(oExc.getMessage());
		}
	}

	@Override
	protected void postProcessInsert(AST insert) throws SemanticException {
		InsertStatement insertStatement = (InsertStatement) insert;
		// The statement is validated. It requires to check that
		// all properties of type REF
		// are valued by existing values and valued also their tablenames
		insertStatement.validate();
		// Let a chance to the evaluator to do some other transformations
		getDMLEvaluator().postProcessInsert(insertStatement);
	}

	@Override
	protected void postProcessUpdate(AST updateStatement) {
		FromClause fromClause = (FromClause) updateStatement.getFirstChild();
		FromElement firstFromElement = fromClause.getFirstFromElement();

		if (firstFromElement.getCategory().isClass()) {
			// TODO.
		} else {
			postProcessOntologicUpdate(updateStatement);
		}
	}

	private void postProcessOntologicUpdate(AST updateStatement) {
		// check whether we are in the adequate case or not
		if (isArrayInUpdate(updateStatement)) {

			String entityName = getEntityName(updateStatement);
			String attributeName = getAttributeName(updateStatement);

			// 091009: we do not know if it is:
			// a select where clause,
			// a simple value where clause
			transformWhereClauseIntoSimpleValue(updateStatement);

			int oidOfEntity = getOidOfEntity(updateStatement);

			// the name of the association table
			String arrayAttributeTableName = OntoDBHelper.getNameAssociationTable(entityName, attributeName);
			log.debug("nom de la table d'attributs" + arrayAttributeTableName);

			// 091008: we do not know if it is:
			// a select array node or,
			// a table array node
			AST arrayAmbiguousNode = getArrayNode(updateStatement);

			// 091008: if the arrayNode is note a table and is a select instead,
			// change it into an array of table type
			AST arrayNode = transformIntoArrayTableOfElements(arrayAmbiguousNode);

			// 091008: set the arrayNode as the array node of updateStatement
			setArrayNode(updateStatement, arrayNode);

			// how many elements in the array
			int numChildren = arrayNode.getNumberOfChildren();
			// what are the oid of the classes
			int[] arrayChildren = new int[numChildren];
			// what are the oid of the classes in the association table
			int[] arrayChildrenSql = new int[numChildren];

			// initialization of the arrays
			for (int i = 1; i < numChildren; i++) {
				arrayChildren[i] = 0;
				arrayChildrenSql[i] = 0;
			}

			// fill arrayChildren with the oids of the array
			arrayChildren[0] = Integer.valueOf(arrayNode.getFirstChild().getText()).intValue();
			AST remainArrayQuery = arrayNode.getFirstChild();
			for (int i = 1; i < numChildren; i++) {
				remainArrayQuery = remainArrayQuery.getNextSibling();
				arrayChildren[i] = Integer.valueOf(remainArrayQuery.getText()).intValue();
			}

			try {

				String sql = "";
				Statement st = session.connection().createStatement();
				// what is the oid of the class being updated
				sql = "select rid_bsu from " + entityName + "_e where rid_bsu=" + oidOfEntity + ";";
				st = session.connection().createStatement();
				// ResultSet rSet = st.executeQuery(sql);
				ResultSet rSet = st.executeQuery(sql);

				// check whether the class being updated exists
				if (rSet.next()) {
					// delete the previous elements from the association table
					sql = "delete from " + arrayAttributeTableName + " where rid_s=" + oidOfEntity + ";";
					st = session.connection().createStatement();
					st.executeUpdate(sql);

					// insert the elements in the association table
					for (int i = 0; i < numChildren; i++) {
						// sql = "insert into " + arrayAttributeTableName +
						// " (rid, rid_s, rid_d) values (478, 23 , 38);";
						sql = "insert into " + arrayAttributeTableName + " (rid_s, rid_d) values (" + oidOfEntity + ", "
								+ arrayChildren[i] + ");";
						st = session.connection().createStatement();
						st.executeUpdate(sql);
					}

					// search for the values of the oids in the association
					// table
					sql = "select rid from " + arrayAttributeTableName + " where rid_s=" + oidOfEntity;
					st = session.connection().createStatement();
					rSet = st.executeQuery(sql);

					for (int i = 0; i < numChildren; i++) {
						rSet.next();
						arrayChildrenSql[i] = rSet.getInt(1);
					}
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// update the array of the sql query with the new values
			arrayNode.getFirstChild().setText(String.valueOf(arrayChildrenSql[0]));
			remainArrayQuery = arrayNode.getFirstChild();
			for (int i = 1; i < numChildren; i++) {
				remainArrayQuery = remainArrayQuery.getNextSibling();
				remainArrayQuery.setText(String.valueOf(arrayChildrenSql[i]));
			}
		}
	}

	/**
	 * belaidn (091008)- sub query converting a array select (if the case) into
	 * array of table type
	 */
	protected void transformWhereClauseIntoSimpleValue(AST updateStatement) {

		// get the second part of the where node
		AST secondPartOfWhereNode = updateStatement.getFirstChild().getNextSibling().getNextSibling().getFirstChild()
				.getFirstChild();
		AST temporarySecondPartOfWhereNode = null;

		// test if we are in a select node in the where
		// else, do nothing!
		if (secondPartOfWhereNode.getNextSibling().getText() == "SELECT") {

			String querySQL = null;
			Statement st = null;
			ResultSet rSet = null;

			// calculate the values returned by the select query (by serializing
			// the query)
			try {
				SQLGenerator gen = new SQLGenerator(this.session);
				gen.statement(secondPartOfWhereNode.getNextSibling());
				querySQL = gen.getSQL();

				try {
					st = session.connection().createStatement();
					rSet = st.executeQuery(querySQL);

					rSet.next();
					// suppose that it returns only one result (a name
					// corresponds to a single class)
					temporarySecondPartOfWhereNode = ASTUtil.create(getASTFactory(), OntoQLSQLTokenTypes.QUOTED_STRING,
							Integer.toString(rSet.getInt(1)));

					secondPartOfWhereNode.setNextSibling(temporarySecondPartOfWhereNode);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (RecognitionException exc) {
				throw new JOBDBCException(exc);
			}

		}
	}

	/**
	 * belaidn (091008)- sub query converting a array select (if the case) into
	 * array of table type
	 */
	protected AST transformIntoArrayTableOfElements(AST arrayUmbiguousNode) {

		// building of an array node
		AST arrayNode = ASTUtil.create(getASTFactory(), OntoQLSQLTokenTypes.ARRAY, "ARRAY");
		AST temporaryNewChildNode = null;
		AST temporaryLastChildNode = null;

		// test if we are in a select node
		if (arrayUmbiguousNode.getFirstChild().getText() == "SELECT") {

			String querySQL = null;
			Statement st = null;
			ResultSet rSet = null;

			// calculate the values returned by the select query (by serializing
			// the query)
			try {
				SQLGenerator gen = new SQLGenerator(this.session);
				gen.statement(arrayUmbiguousNode.getFirstChild());
				querySQL = gen.getSQL();

				try {
					st = session.connection().createStatement();
					rSet = st.executeQuery(querySQL);

					rSet.next();
					temporaryNewChildNode = ASTUtil.create(getASTFactory(), OntoQLSQLTokenTypes.NUM_INT,
							Integer.toString(rSet.getInt(1)));

					temporaryLastChildNode = temporaryNewChildNode;
					arrayNode.setFirstChild(temporaryLastChildNode);

					// do this while new childs exist
					while (rSet.next()) {

						// create a new child
						temporaryNewChildNode = ASTUtil.create(getASTFactory(), OntoQLSQLTokenTypes.NUM_INT,
								Integer.toString(rSet.getInt(1)));

						// link the new child to the last child
						temporaryLastChildNode.setNextSibling(temporaryNewChildNode);
						temporaryLastChildNode = temporaryNewChildNode;
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (RecognitionException exc) {
				throw new JOBDBCException(exc);
			}

		} else {
			// all other cases
			arrayNode = arrayUmbiguousNode;
		}

		return arrayNode;

	}

	/**
	 * belaidn - sub query for inserting in the association table for the update
	 * statement
	 */
	protected boolean isArrayInUpdate(AST updateStatement) {

		// suppose it is the right case unless proven wrong
		boolean bEntityName = false;
		boolean bAttributeName = false;
		boolean bArray = false;
		boolean bOidOfEntity = false;
		boolean b = false;
		boolean bClass = false;

		// the case of the update of an array attribute
		AST entityNode = null;
		AST attributeNode = null;
		AST arrayNode = null;
		AST whereClauseNode = null;
		AST classNode = null;

		if (updateStatement.getFirstChild() != null) {

			entityNode = updateStatement.getFirstChild().getFirstChild();
			if (entityNode != null) {
				if (entityNode.getText() != null) {
					bEntityName = true;
				}
			}

			// 091207 belaidn : if it is a semantic concept (for now, only
			// class_e)
			classNode = entityNode;
			if (classNode != null) {

				if (classNode.getText().equals("class_e")) {
					bClass = true;

				}
			}

			attributeNode = updateStatement.getFirstChild().getNextSibling();
			if (attributeNode != null) {
				attributeNode = attributeNode.getFirstChild();

				if (attributeNode != null) {
					attributeNode = attributeNode.getFirstChild();

					if (attributeNode != null) {
						bAttributeName = true;

						arrayNode = attributeNode.getNextSibling();
						if (arrayNode != null) {
							// if (arrayNode.getType() == SQLASTFactory.ARRAY) {
							if (arrayNode.getType() == ARRAY) {
								bArray = true;
							}
						}
					}
				}

				whereClauseNode = updateStatement.getFirstChild().getNextSibling().getNextSibling();
				if (whereClauseNode != null) {
					whereClauseNode = whereClauseNode.getFirstChild();

					if (whereClauseNode != null) {
						whereClauseNode = whereClauseNode.getFirstChild();

						if (whereClauseNode != null) {
							whereClauseNode = whereClauseNode.getNextSibling();

							if (whereClauseNode != null) {
								if (whereClauseNode.getText() != null) {
									bOidOfEntity = true;
								}

							}
						}
					}
				}
			}
		}

		if (bEntityName & bAttributeName & bArray & bOidOfEntity & bClass) {
			b = true;
		}
		return b;
	}

	/**
	 * belaidn - sub query
	 */
	protected String getEntityName(AST updateStatement) {

		String getEntityName = updateStatement.getFirstChild().getFirstChild().getText();
		getEntityName = OntoDBHelper.removeSuffixeOfEntity(getEntityName);
		return getEntityName;
	}

	/**
	 * belaidn - sub query
	 */
	protected String getAttributeName(AST updateStatement) {
		String getAttributeName = updateStatement.getFirstChild().getNextSibling().getFirstChild().getFirstChild()
				.getText();
		getAttributeName = OntoDBHelper.getAttributeFromExpression(getAttributeName);
		return getAttributeName;
	}

	/**
	 * belaidn - sub query
	 */
	protected int getOidOfEntity(AST updateStatement) {
		int oidOfEntity = Integer.valueOf(updateStatement.getFirstChild().getNextSibling().getNextSibling()
				.getFirstChild().getFirstChild().getNextSibling().getText().replace("'", "")).intValue();
		return oidOfEntity;
	}

	/**
	 * belaidn - sub query
	 */
	protected AST getArrayNode(AST updateStatement) {

		return updateStatement.getFirstChild().getNextSibling().getFirstChild().getFirstChild().getNextSibling();
	}

	/**
	 * belaidn - sub query
	 */
	protected void setArrayNode(AST updateStatement, AST arrayNode) {
		updateStatement.getFirstChild().getNextSibling().getFirstChild().getFirstChild().setNextSibling(arrayNode);
	}
}
