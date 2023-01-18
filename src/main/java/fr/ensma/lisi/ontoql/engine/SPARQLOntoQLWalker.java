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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import antlr.RecognitionException;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.antlr.SPARQLOntoQLBaseWalker;
import fr.ensma.lisi.ontoql.engine.antlr.SPARQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.util.ASTPrinter;
import fr.ensma.lisi.ontoql.engine.util.ASTUtil;
import fr.ensma.lisi.ontoql.engine.util.AliasGenerator;
import fr.ensma.lisi.ontoql.engine.util.ErrorCounter;
import fr.ensma.lisi.ontoql.engine.util.ErrorReporter;
import fr.ensma.lisi.ontoql.engine.util.ParseErrorHandler;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.QueryException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.sparql.CategorySPARQL;
import fr.ensma.lisi.ontoql.sparql.DescriptionSPARQL;
import fr.ensma.lisi.ontoql.sparql.FactorySPARQL;
import fr.ensma.lisi.ontoql.sparql.PropertySPARQL;

/**
 * Implements methods used by the SPARQL->OntoQL tree transform grammar
 * <ul>
 * <li>Isolates the OntoQL API-specific code from the ANTLR generated code.</li>
 * </ul>
 * 
 * @author Stéphane JEAN
 */
public class SPARQLOntoQLWalker extends SPARQLOntoQLBaseWalker implements ErrorReporter {

	/**
	 * A reference to the current session.
	 */
	private OntoQLSession session;

	/**
	 * The from clause of the translated OntoQL query.
	 */
	private AST fromClause;

	/**
	 * Class added in the query by an UNNEST operator
	 */
	private Vector unnestCategories = new Vector();

	/**
	 * Mapping of each variable to a class or a property.
	 */
	private Map<String, Object> varMapping = new HashMap<String, Object>();

	/**
	 * Generator of alias names for classes.
	 */
	private AliasGenerator aliasGenerator = new AliasGenerator();

	/**
	 * 
	 */
	private Set unnestOperators = new HashSet();

	/**
	 * Factory to create SPARQL element
	 */
	private FactorySPARQL factorySPARQL = new FactorySPARQL();

	private static Map<String, String> mappingEntityAttributeRDFSOntoQL = new HashMap<String, String>();

	/**
	 * Handles parser errors.
	 */
	private ParseErrorHandler parseErrorHandler;

	/**
	 * A printer of tree.
	 */
	private ASTPrinter printer;

	static {
		// oid is mapped on uri
		mappingEntityAttributeRDFSOntoQL.put("oid", "OWLNamespace || #code"); // for
		// the
		// moment,
		// we use only oid
		// the first label is mapped on name
		mappingEntityAttributeRDFSOntoQL.put("label", "name");
		// the first comment is mapped on definition
		mappingEntityAttributeRDFSOntoQL.put("comment", "definition");
		mappingEntityAttributeRDFSOntoQL.put("domain", "scope");
		mappingEntityAttributeRDFSOntoQL.put("range", "range");
		mappingEntityAttributeRDFSOntoQL.put("subClassOf", "superclasses");
	}

	public String getMappingOfEntityOrAttribute(String nameRdfs) {
		return (String) mappingEntityAttributeRDFSOntoQL.get(nameRdfs);
	}

	protected void reinitTranslator() {
		currentCategory = null;
		fromClause = null;
		whereClause = null;
		previousCategory = null;
		varMapping = new HashMap<String, Object>();
	}

	@Override
	protected void addClassOrEntity(AST nodeClass, AST nodeVar) {
		CategorySPARQL newCategory = factorySPARQL.createCategory(nodeClass.getText(), nodeVar.getText(), this);
		if (currentCategory != null) {
			if (previousCategory == null) {
				initFromClause(currentCategory.getFromElement(astFactory));
			}
			addUnnestOperators();
			previousCategory = currentCategory;
		}
		currentCategory = newCategory;
	}

	@Override
	protected void postProcessSPARQLQuery() {
		// we need to add the unnest node if there is one
		if (unnestOperators.size() > 0) {
			addUnnestOperators();
		}
	}

	/**
	 * Add all the unnest operators to the OntoAlgebra tree
	 */
	protected void addUnnestOperators() {
		// add all the unnest operators
		Iterator it = unnestOperators.iterator();
		while (it.hasNext()) {
			fromClause.addChild((AST) it.next());
			it.remove();
		}
	}

	private void initFromClause(AST fromElement) {
		fromClause = ASTUtil.create(astFactory, OntoQLTokenTypes.FROM, "from");
		fromClause.addChild(fromElement);
	}

	protected void addJoin(CategorySPARQL categoryToJoin, CategorySPARQL categoryJoined) {
		AST joinNode = ASTUtil.create(astFactory, OntoQLTokenTypes.JOIN, "JOIN");
		;
		if (operator == OPTIONAL) {
			joinNode.addChild(ASTUtil.create(astFactory, OntoQLTokenTypes.LEFT, "LEFT"));
		}
		ASTUtil.create(astFactory, OntoQLTokenTypes.JOIN, "JOIN");
		joinNode.addChild(categoryToJoin.getFromElement(astFactory));
		AST onNode = ASTUtil.create(astFactory, OntoQLTokenTypes.ON, "ON");

		List<DescriptionSPARQL> descriptionCategoryToJoin = categoryToJoin.getDescriptions();
		DescriptionSPARQL currentDescriptionJoined = null;
		DescriptionSPARQL currentDescriptionToJoin = null;
		List<DescriptionSPARQL> descriptionsCategoryJoined = categoryJoined.getDescriptions();
		if (categoryJoined.isClass() && categoryToJoin.isClass()) {
			// the join can be made on the uri of the category
			// so, we must add the variable of the uri in the test
			DescriptionSPARQL uriCategoryToJoin = new PropertySPARQL("URI", categoryToJoin.getVariable(), this);
			uriCategoryToJoin.setScope(categoryToJoin);
			descriptionCategoryToJoin.add(uriCategoryToJoin);
			DescriptionSPARQL uriCategoryJoined = new PropertySPARQL("URI", categoryJoined.getVariable(), this);
			descriptionsCategoryJoined.add(uriCategoryJoined);
			uriCategoryJoined.setScope(categoryJoined);
		}

		AST currentCondition = null;
		for (int i = 0; i < descriptionCategoryToJoin.size(); i++) {
			for (int j = 0; j < descriptionsCategoryJoined.size(); j++) {
				currentDescriptionToJoin = (DescriptionSPARQL) descriptionCategoryToJoin.get(i);
				currentDescriptionJoined = (DescriptionSPARQL) descriptionsCategoryJoined.get(j);
				if (currentDescriptionJoined.getVariable().equals(currentDescriptionToJoin.getVariable())) {
					if (currentCondition == null) {
						currentCondition = getJoinCondition(currentDescriptionToJoin, currentDescriptionJoined);
					} else {
						AST andNode = ASTUtil.create(astFactory, OntoQLTokenTypes.AND, "AND");
						andNode.addChild(currentCondition);
						andNode.addChild(getJoinCondition(currentDescriptionToJoin, currentDescriptionJoined));
						currentCondition = andNode;
					}
				}
			}
		}
		onNode.addChild(currentCondition);
		joinNode.addChild(onNode);
		fromClause.addChild(joinNode);
	}

	private AST getJoinCondition(DescriptionSPARQL descriToJoin, DescriptionSPARQL descriJoined) {
		if (descriToJoin.getName().equals("subdivision")) {
			return processTransitiveProperty(descriToJoin, descriJoined);
		}
		AST res = ASTUtil.create(astFactory, OntoQLTokenTypes.EQ, "EQ");
		AST descriToJoinNode = descriToJoin.getDotElement(false);
		res.addChild(descriToJoinNode);
		AST descriJoinedNode = descriJoined.getDotElement(false);
		res.addChild(descriJoinedNode);

		boolean isDescriJoinedNotOid = false;
		if (descriJoined.isOptional()) {
			needDistinct = true;
			res = addOrIsNullCondition(res, descriJoinedNode);
			// We must project the non null property
			varMapping.put(descriJoined.getVariable(), descriJoined);
			isDescriJoinedNotOid = true;
		}
		if (descriToJoin.isOptional()) {
			needDistinct = true;
			res = addOrIsNullCondition(res, descriToJoinNode);
			if (isDescriJoinedNotOid) {
				// The 2 properties may be NULL: we use COALESCE
				descriToJoin.setCoalesce(true);
				descriToJoin.setCoalesceDescription(descriJoined);
				descriJoined.setCoalesce(true);
				descriJoined.setCoalesceDescription(descriToJoin);
			} else {
				varMapping.put(descriToJoin.getVariable(), descriToJoin);
			}
		}
		return res;
	}

	private AST addOrIsNullCondition(AST nodeCondition, AST nodeProperty) {
		AST res = ASTUtil.create(astFactory, OntoQLTokenTypes.OR, "OR");
		res.addChild(nodeCondition);
		AST isNullNodePropJoined = ASTUtil.create(astFactory, OntoQLTokenTypes.IS_NULL, "IS NULL");
		isNullNodePropJoined.addChild(astFactory.dupTree(nodeProperty));
		res.addChild(isNullNodePropJoined);
		return res;
	}

	protected boolean mustBeAdded(AST nodeVar) {
		boolean res = true;
		if (varMapping.get(nodeVar.getText()) != null) {
			DescriptionSPARQL descri = (DescriptionSPARQL) varMapping.get(nodeVar.getText());
			if (descri.getScope().equals(currentCategory)) {
				res = false;
			}
		}
		return res;
	}

	protected AST getIdentNodeGeolocalized(String propertyName) {
		AST res = ASTUtil.create(astFactory, OntoQLTokenTypes.DOT, "DOT");
		AST prefixNode = ASTUtil.create(astFactory, OntoQLTokenTypes.IDENT, "hasRef2Geolocalization");
		res.addChild(prefixNode);
		AST propNode = ASTUtil.create(astFactory, OntoQLTokenTypes.IDENT, propertyName);
		res.addChild(propNode);
		return res;
	}

	protected AST processTransitiveProperty(DescriptionSPARQL descriToJoin, DescriptionSPARQL descriJoined) {

		AST res = ASTUtil.create(astFactory, OntoQLTokenTypes.AND, "and");

		AST nodeSup = ASTUtil.create(astFactory, OntoQLTokenTypes.GT, ">");

		DescriptionSPARQL descriBinfRegion = factorySPARQL.createDescription("Binf", "?Binf", this);
		currentCategory.addDescription(descriBinfRegion);

		AST identNodeBinfCommune = getIdentNodeGeolocalized("Binf");
		AST identNodeBinfRegion = descriBinfRegion.getDotElement(false);
		nodeSup.addChild(identNodeBinfCommune);
		nodeSup.addChild(identNodeBinfRegion);

		AST nodeInf = ASTUtil.create(astFactory, OntoQLTokenTypes.LT, "<");
		DescriptionSPARQL descriBsubRegion = factorySPARQL.createDescription("Bsup", "?Bsup", this);
		currentCategory.addDescription(descriBsubRegion);

		AST identNodeBsupCommune = getIdentNodeGeolocalized("Bsup");
		AST identNodeBsupRegion = descriBsubRegion.getDotElement(false);
		nodeInf.addChild(identNodeBsupCommune);
		nodeInf.addChild(identNodeBsupRegion);

		res.addChild(nodeInf);
		res.addChild(nodeSup);

		return res;

	}

	protected void addDescriptionOrConstraint(AST nodeScope, AST nodeDescription, AST nodeVar, AST nodeURI) {
		if (nodeVar != null) {
			addPropertyorAttribute(nodeScope, nodeDescription, nodeVar);
		} else { // set a constraint on a property value
			addConstraint(nodeScope, nodeDescription, nodeURI);
		}
	}

	protected void addConstraint(AST nodeScope, AST nodeDescription, AST nodeURI) {

		AST nodeConstraint = ASTUtil.create(astFactory, OntoQLTokenTypes.EQ, "EQ");
		DescriptionSPARQL descriConstraint = factorySPARQL.createDescription(nodeDescription.getText(), "", this);
		addDescriptionToCategory(nodeScope, descriConstraint);
		treatMultiValuedProperty(nodeDescription, nodeDescription, descriConstraint);
		AST descriConstraintNode = descriConstraint.getDotElement(false);
		nodeConstraint.addChild(descriConstraintNode);
		String nodeText = nodeURI.getText();

		if (nodeURI.getType() == SPARQLTokenTypes.Q_IRI_REF) {
			nodeText = "'" + nodeText + "'";
		}

		nodeConstraint.addChild(ASTUtil.create(astFactory, OntoQLTokenTypes.QUOTED_STRING, nodeText));
		addOntoSelect(nodeConstraint);
	}

	@Override
	protected void addPropertyorAttribute(AST nodeScope, AST nodeDescription, AST nodeVar) {
		if (mustBeAdded(nodeVar)) {
			// we assume that currentClass is not null
			DescriptionSPARQL newDescription = factorySPARQL.createDescription(nodeDescription.getText(),
					nodeVar.getText(), this);

			addDescriptionToCategory(nodeScope, newDescription);
			treatMultiValuedProperty(nodeDescription, nodeVar, newDescription);

			if (tmpOperator == INNER && newDescription.isOptional()) {
				// we must add a condition IS NOT NULL
				addIsNotNull(newDescription);
			}
			varMapping.put(nodeVar.getText(), newDescription);
		}
	}

	private void treatMultiValuedProperty(AST nodeDescription, AST nodeVar, DescriptionSPARQL newDescription) {
		if (newDescription.isMultivalued()) {
			CategorySPARQL unnestCategory = factorySPARQL.createCategory(nodeDescription.getText(), nodeVar.getText(),
					this);
			unnestCategory.setAlias(newDescription.getName());
			unnestCategory.setVariable(nodeVar.getText());
			unnestCategories.add(unnestCategory);
			unnest(newDescription);
		}
	}

	private void addDescriptionToCategory(AST nodeScope, DescriptionSPARQL newDescription) {
		if (nodeScope.getText().equals(currentCategory.getVariable())) {
			currentCategory.addDescription(newDescription);
		} else {
			for (int i = 0; i < unnestCategories.size(); i++) {
				CategorySPARQL aCategory = (CategorySPARQL) unnestCategories.get(i);
				if (aCategory.getVariable().equals(nodeScope.getText())) {
					aCategory.addDescription(newDescription);
				}
			}
		}
	}

	private void addIsNotNull(DescriptionSPARQL descri) {
		if (whereClause == null) {
			whereClause = ASTUtil.create(astFactory, OntoQLTokenTypes.WHERE, "where");
			AST nodeIsNotNull = ASTUtil.create(astFactory, OntoQLTokenTypes.IS_NOT_NULL, "is_not_null");
			AST identNode = descri.getDotElement(false);
			nodeIsNotNull.addChild(identNode);
			whereClause.addChild(nodeIsNotNull);
		}
	}

	/**
	 * Create an unnest operator in the OntoAlgebra tree
	 * 
	 * @param descri a property or attribute to unnest
	 */
	protected void unnest(DescriptionSPARQL descri) {
		AST res = ASTUtil.create(astFactory, OntoQLTokenTypes.UNNEST, "UNNEST");
		AST dot = ASTUtil.create(astFactory, OntoQLTokenTypes.DOT, "DOT");
		AST identAlias = ASTUtil.create(astFactory, OntoQLTokenTypes.IDENT, currentCategory.getAlias());
		AST identDescri = ASTUtil.create(astFactory, OntoQLTokenTypes.IDENT, descri.getName());
		AST alias = ASTUtil.create(astFactory, OntoQLTokenTypes.ALIAS, descri.getName());
		dot.addChild(identAlias);
		dot.addChild(identDescri);
		res.addChild(dot);
		res.addChild(alias);
		unnestOperators.add(res);
	}

	@Override
	protected AST getFromClause() {
		if (fromClause == null) {
			initFromClause(currentCategory.getFromElement(astFactory));
			addUnnestOperators();
		}
		return fromClause;
	}

	/**
	 * Translate a variable of the SELECT clause into a property
	 */
	protected AST resolveSelectElement(AST node, boolean aliasNeeded) {
		AST res = null;

		DescriptionSPARQL descri = (DescriptionSPARQL) varMapping.get(node.getText());
		if (descri == null) {
			if (inUnionQuery) {
				res = ASTUtil.create(astFactory, NULL, "null");
			} else {
				throw new JOBDBCException("The variable " + node.getText() + " is not defined in the WHERE clause");
			}
		} else {
			if (descri.getName().equals("oid")) { // replace oid by uri
				descri = (DescriptionSPARQL) varMapping.get(node.getText() + "URI");
			}
			res = descri.getDotElement(aliasNeeded);
		}

		return res;
	}

	public void registerVariable(String variable, Object obj) {
		varMapping.put(variable, obj);
	}

	/**
	 * @param e the RecognitionException to report
	 * @see antlr.TreeParser#reportError(antlr.RecognitionException)
	 */
	public final void reportError(final RecognitionException e) {
		parseErrorHandler.reportError(e); // Use the delegate.
	}

	/**
	 * @param s the message to report
	 * @see antlr.TreeParser#reportError(java.lang.String)
	 */
	public final void reportError(final String s) {
		parseErrorHandler.reportError(s); // Use the delegate.
	}

	/**
	 * @param s the message to report
	 * @see antlr.TreeParser#reportWarning(java.lang.String)
	 */
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
	public SPARQLOntoQLWalker(final OntoQLSession aSession) {
		this.session = aSession;

		this.parseErrorHandler = new ErrorCounter();
		this.printer = new ASTPrinter(SQLTokenTypes.class);

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
	 * @return the aliasGenerator
	 */
	public AliasGenerator getAliasGenerator() {
		return aliasGenerator;
	}

	@Override
	protected void addOntoSelect(AST node) {
		if (whereClause == null) {
			whereClause = ASTUtil.create(astFactory, OntoQLTokenTypes.WHERE, "where");
			whereClause.addChild(node);
		} else {
			AST andNode = ASTUtil.create(astFactory, OntoQLTokenTypes.AND, "and");
			andNode.addChild(whereClause.getFirstChild());
			andNode.addChild(node);
			whereClause.setFirstChild(andNode);
		}
	}

}
