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
package fr.ensma.lisi.ontoql.evaluator.ontodb;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import antlr.ASTFactory;
import antlr.RecognitionException;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.Attribute;
import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.Description;
import fr.ensma.lisi.ontoql.core.Entity;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCategory;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCollection;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.core.MultilingualAttribute;
import fr.ensma.lisi.ontoql.engine.OntoQLSQLWalker;
import fr.ensma.lisi.ontoql.engine.SQLGenerator;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.IdentNode;
import fr.ensma.lisi.ontoql.engine.tree.OntoQLSQLWalkerNode;
import fr.ensma.lisi.ontoql.engine.tree.dml.InsertStatement;
import fr.ensma.lisi.ontoql.engine.tree.dml.IntoClause;
import fr.ensma.lisi.ontoql.engine.tree.dml.ValuesClause;
import fr.ensma.lisi.ontoql.engine.tree.dql.FromElement;
import fr.ensma.lisi.ontoql.engine.util.ASTUtil;
import fr.ensma.lisi.ontoql.evaluator.AbstractEvaluator;
import fr.ensma.lisi.ontoql.evaluator.DMLEvaluator;
import fr.ensma.lisi.ontoql.exception.JDBCExceptionHelper;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.SQLExceptionConverterFactory;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.jobdbc.impl.OntoQLResultSetImpl;
import fr.ensma.lisi.ontoql.ontoapi.Instance;
import fr.ensma.lisi.ontoql.ontomodel.OntoAttribute;
import fr.ensma.lisi.ontoql.ontomodel.OntoMultilingualAttribute;
import fr.ensma.lisi.ontoql.ontomodel.mapping.PlibAttribute;
import fr.ensma.lisi.ontoql.util.DatabaseHelper;
import fr.ensma.lisi.ontoql.util.JDBCHelper;
import fr.ensma.lisi.ontoql.util.OntoDBHelper;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;
import fr.ensma.lisi.ontoql.util.StringHelper;

/**
 * @author Stéphane JEAN
 */
public class DMLEvaluatorOntoDB extends AbstractEvaluator implements DMLEvaluator {

	/**
	 * A logger for this class.
	 */
	private static Log log = LogFactory.getLog(DMLEvaluatorOntoDB.class);

	/**
	 * A reference to its walker.
	 */
	private OntoQLSQLWalker walker;

	/**
	 * access to the database
	 */
	private OntoQLSession session;

	/**
	 * an builder of node
	 */
	private ASTFactory astFactory;

	/**
	 * Constructor
	 * 
	 * @param ontoQLWalker the walker that use this evaluator
	 */
	public DMLEvaluatorOntoDB(OntoQLSQLWalker ontoQLWalker) {
		this.walker = ontoQLWalker;
		session = walker.getSession();
		astFactory = walker.getASTFactory();
	}

	protected String getSQLWithoutPrefix(Description description) {
		String sql = description.toSQL();
		int indexPrefix = sql.indexOf('.');
		return sql.substring(indexPrefix + 1, sql.length());
	}

	/**
	 * Do a last transformation of an insert statement on the content part of an
	 * OBDB
	 * 
	 * @param insert     an insert statement on the content part of an OBDB
	 * @param intoClause the into clause of this insert statement
	 */
	public void postProcessInsertInContent(InsertStatement insert, IntoClause intoClause) {
		ValuesClause valuesClause = insert.getValuesClause();
		// First, we replace queries in the VALUES clause by
		// their returning values (this is required for the
		// second step
		replaceSubQueries(intoClause, valuesClause);
		// Then, we add the tablename or tablenames for properties of type REF
		// or
		// collection
		addTablenamesColumn(intoClause, valuesClause);
		// for a new instance of an ontology class
		// the version min is required in ontodb
		addVersionMin(insert, intoClause);
	}

	/**
	 * Replace all subqueries by their results
	 * 
	 * @param intoClause   into clause of the insert statement
	 * @param valuesClause values clause of the insert statement
	 */
	private void replaceSubQueries(IntoClause intoClause, ValuesClause valuesClause) {
		// previous node of the current node
		AST previousNode = null;
		AST exprRefNode = valuesClause.getFirstChild();
		// multiple subqueries may be processed
		// so expressionInSelect contain SELECT clause
		// of different subqueries
		// This index stores the index in the expressionInSelect list
		// of the current subquery
		int indexInSelectList = 0;
		// index of the current value in the list of values
		int indexOfValue = 0;
		// hasChanged is true if a query has been executed
		Boolean hasChanged = new Boolean(false);
		while (exprRefNode != null) {
			AST newNode = null;
			if (exprRefNode.getType() == SQLTokenTypes.ARRAY) {
				// a subquery can be inside an array
				newNode = replaceSubQuery(intoClause, valuesClause, exprRefNode.getFirstChild(), indexInSelectList,
						hasChanged, indexOfValue);
			} else {
				newNode = replaceSubQuery(intoClause, valuesClause, exprRefNode, indexInSelectList, hasChanged,
						indexOfValue);
			}
			if (newNode != null) {
				if (previousNode != null) {
					previousNode.setNextSibling(newNode);
				} else {
					valuesClause.setFirstChild(newNode);
				}
				newNode.setNextSibling(exprRefNode.getNextSibling());
				exprRefNode = newNode;
			}
			previousNode = exprRefNode;
			exprRefNode = exprRefNode.getNextSibling();
			if (hasChanged.booleanValue()) {
				indexInSelectList++;
			}
			indexOfValue++;
		}

	}

	/**
	 * Replace a subQuery at the given index in expressionInSelect by a value in DML
	 * statement
	 * 
	 * @param the        subquery node
	 * @param the        index of this node in expressionInSelect
	 * @param isSubQuery Out parameter stating is this node was a subquery
	 * @return The value of the result of the subquery
	 */
	protected AST replaceSubQuery(IntoClause intoClause, ValuesClause valuesClause, AST exprRefNode,
			int indexInSelectList, Boolean isSubQuery, int indexOfValue) {
		AST res = null;
		if (exprRefNode.getType() == SQLTokenTypes.SELECT) {
			isSubQuery = new Boolean(true);

			EntityDatatype typeProperty = ((Description) intoClause.getDescriptions().get(indexOfValue)).getRange();

			try {
				SQLGenerator gen = new SQLGenerator(session);
				gen.statement(exprRefNode);
				String sql = gen.getSQL();
				gen.getParseErrorHandler().throwQueryException();
				log.warn("generated SQL : " + sql);
				Statement st = session.connection().createStatement();
				OntoQLResultSet rs = new OntoQLResultSetImpl(st.executeQuery(sql),
						walker.getExpressionInSelect().subList(indexInSelectList, indexInSelectList + 1),
						walker.getFactoryEntity(), walker.getSession());
				if (!rs.next()) {
					throw new JOBDBCException("A subquery must retrieve at least one result");
				} else {
					if (typeProperty.isAssociationType()) {
						String newValue = rs.getString(1);
						res = ASTUtil.create(astFactory, SQLTokenTypes.NUM_INT, newValue);
						valuesClause.setValue(indexOfValue, newValue);
						if (rs.next()) {
							throw new JOBDBCException("A subquery return more than one result for an association");
						}
					} else if (typeProperty.isCollectionAssociationType()) {
						String rids = "ARRAY[";
						boolean otherResult;
						do {
							String currentOid = rs.getString(1);
							otherResult = rs.next();
							String comma = otherResult ? ", " : "";
							rids += currentOid + comma;
						} while (otherResult);
						res = ASTUtil.create(astFactory, SQLTokenTypes.NUM_INT, rids + "]");
						valuesClause.setValue(indexOfValue, rids + "]");
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

	private void addTablenamesColumn(IntoClause intoClause, ValuesClause valuesClause) {
		List values = valuesClause.getValues();
		List descriptions = intoClause.getDescriptions();
		int nbrDescriptions = descriptions.size();
		AST currentValueNode = valuesClause.getFirstChild();
		AST currentPropertyNode = intoClause.getRangeClause().getFirstChild();
		EntityDatatype currentDatatypeProperty;
		EntityProperty currentProperty;
		for (int i = 0; i < nbrDescriptions; i++) {
			currentProperty = (EntityProperty) descriptions.get(i);
			currentDatatypeProperty = currentProperty.getRange();
			if (currentDatatypeProperty.isAssociationType()) {
				// append the column tablename
				intoClause.appendColumn(currentPropertyNode,
						currentPropertyNode.getText().replaceFirst("rid", "tablename"));
				currentPropertyNode = currentPropertyNode.getNextSibling();
				// add the value of this tablename
				processRefProperty(currentProperty, (EntityDatatypeCategory) currentDatatypeProperty,
						(String) values.get(i), currentValueNode, valuesClause);
				currentValueNode = currentValueNode.getNextSibling();
			} else if (currentDatatypeProperty.isCollectionAssociationType()) {
				// append the column tablenames
				intoClause.appendColumn(currentPropertyNode,
						currentPropertyNode.getText().replaceFirst("rids", "tablenames"));
				currentPropertyNode = currentPropertyNode.getNextSibling();
				// add the values of this tablenames
				processRefCollectionProperty(currentProperty, (EntityDatatypeCollection) currentDatatypeProperty,
						(String) values.get(i), currentValueNode, valuesClause);
				currentValueNode = currentValueNode.getNextSibling();
			}
			currentValueNode = currentValueNode.getNextSibling();
			currentPropertyNode = currentPropertyNode.getNextSibling();
		}

	}

	private void processRefProperty(EntityProperty property, EntityDatatypeCategory range, String value, AST valueNode,
			ValuesClause valuesClause) {
		if (OntoQLHelper.isNull(value)) {
			valuesClause.appendStringValue(valueNode, "null");
		} else {
			try {
				Category rangeCategory = range.getCagetory();
				String fromElement = "!" + rangeCategory.getInternalId();
				OntoQLStatement stmt = ((OntoQLSQLWalkerNode) valueNode).getSession().createOntoQLStatement();
				OntoQLResultSet resultset = stmt.executeQuery("select i from " + fromElement + " i where oid=" + value);
				if (!resultset.next()) {
					throw new JOBDBCException(
							"the value of " + property + " is not an instance of " + rangeCategory.getName());
				} else {
					Instance i = resultset.getInstance(1);
					valuesClause.appendStringValue(valueNode, "'e" + i.getBaseType().getInternalId() + "'");

				}
			} catch (SQLException e) {
				throw new JOBDBCException(e.getMessage());
			}
		}
	}

	private void processRefCollectionProperty(EntityProperty property, EntityDatatypeCollection range, String value,
			AST valueNode, ValuesClause valuesClause) {

		try {

			Category rangeCategory = ((EntityDatatypeCategory) range.getDatatype()).getCagetory();
			String fromElement = "!" + rangeCategory.getInternalId();

			OntoQLStatement stmt = ((OntoQLSQLWalkerNode) valueNode).getSession().createOntoQLStatement();
			List values = extractValues(value);
			if (values.size() > 0) {
				String whereClause = "oid=" + values.get(0);
				for (int i = 1; i < values.size(); i++) {
					whereClause += " OR oid=" + values.get(i);
				}
				OntoQLResultSet resultset = stmt
						.executeQuery("select i from " + fromElement + " i where " + whereClause);
				int nbrResult = 0;
				String tablenames = "";
				while (resultset.next()) {
					nbrResult++;
					Instance i = resultset.getInstance(1);
					tablenames += ", 'e" + i.getBaseType().getInternalId() + "'";
				}
				if (nbrResult != values.size()) {
					throw new JOBDBCException(
							"the values of " + property + " are not all instances of " + rangeCategory.getName());
				}
				valuesClause.appendStringValue(valueNode, "ARRAY[" + tablenames.substring(2) + "]");
			} else {
				valuesClause.appendStringValue(valueNode, "null");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e.getMessage());
		}

	}

	private List extractValues(String values) {
		List<String> res = new ArrayList<String>();
		if (!OntoQLHelper.isNull(values)) {
			values = values.substring(values.indexOf('[') + 1);
			boolean stop = false;
			while (!stop) {
				int indexOfComma = values.indexOf(',');
				int indexOfBracket = values.indexOf(']');
				if (indexOfComma != -1) {
					res.add(values.substring(0, indexOfComma).trim());
					values = values.substring(indexOfComma + 1, values.length());
				} else if (indexOfBracket != -1) {
					if (indexOfBracket >= 1) {
						res.add(values.substring(0, indexOfBracket).trim());
					}
					stop = true;
				}
				// else error but it must not happen so we don't check it
			}
		}
		return res;
	}

	/**
	 * Add the version min of a new instance
	 * 
	 * @param insert     the insert statement
	 * @param intoClause the into clause
	 */
	private void addVersionMin(InsertStatement insert, IntoClause intoClause) {
		Category categoryInstantiated = intoClause.getCategoryInstantiated();
		intoClause.appendColumn(null, "version_min");
		String externalId = ((EntityClass) categoryInstantiated).getExternalId();
		String versionValueString = externalId.substring(externalId.indexOf('-') + 1, externalId.length());
		int versionValue = 1;
		if (!StringHelper.isEmpty(versionValueString)) {
			try {
				versionValue = Integer.parseInt(versionValueString);
			} catch (NumberFormatException e) {
				versionValue = 1;
			}
		}
		insert.getValuesClause().appendIntValue(null, versionValue + "");
	}

	/**
	 * Do a last transformation of an insert statement on the ontology part of an
	 * OBDB
	 * 
	 * @param insert     an insert statement on the ontology part of an OBDB
	 * @param intoClause the into clause of this insert statement
	 */
	public void postProcessInsertInOntology(InsertStatement insert, IntoClause intoClause) {
		try {
			// the values clause
			ValuesClause valuesClause = insert.getValuesClause();
			// we replace queries in the VALUES clause by
			// their returning values
			replaceSubQueries(intoClause, valuesClause);
			// list of the attributes to valued
			List attributes = intoClause.getDescriptions();
			// Append the oid value to the insert statement
			intoClause.appendColumn(null, "rid");
			String oid = OntoDBHelper.getSequenceNextVal(insert.getSession().connection(), "root_table_entity_rid_seq");
			valuesClause.appendIntValue(null, oid);
			// variable to follow the AST
			AST previousAttributeNode = intoClause.getRangeClause().getFirstChild();
			AST currentAttributeNode = previousAttributeNode.getNextSibling();
			AST previousValueNode = valuesClause.getFirstChild();
			AST currentValueNode = previousValueNode.getNextSibling();
			// List of insert command required by this insert
			Map<String, InsertCommand> insertCmdToExecute = new HashMap<String, InsertCommand>();
			for (Iterator iter = attributes.iterator(); iter.hasNext();) {
				Attribute currentAttribute = (Attribute) iter.next();
				OntoAttribute currentMapAttribute = currentAttribute.getMapAttribut();
				List currentLinks = currentMapAttribute.getLink();
				if (currentLinks.size() != 0) {
					handleAttributeWithLink(currentAttribute, currentAttributeNode, previousAttributeNode,
							currentMapAttribute, (PlibAttribute) currentLinks.get(0), currentValueNode,
							previousValueNode, currentLinks, insertCmdToExecute, intoClause, oid);
				}
				previousAttributeNode = currentAttributeNode;
				currentAttributeNode = currentAttributeNode.getNextSibling();
				previousValueNode = currentValueNode;
				currentValueNode = currentValueNode.getNextSibling();
			}
			executeInsertRequired(insertCmdToExecute);
		} catch (SQLException e) {
			throw new JOBDBCException(e.getMessage());
		}
	}

	/**
	 * Handle the case of an attribute with a link already processed
	 */
	private void handleAttributeWithLink(Attribute attribute, AST attributeNode, AST previousAttributeNode,
			OntoAttribute mapAttribute, PlibAttribute plibAttribute, AST valNode, AST previousValNode, List links,
			Map<String, InsertCommand> insertCmdToExecute, IntoClause intoClause, String oid) throws SQLException {
		// we must insert a new instance in the table
		if (attribute instanceof MultilingualAttribute) {
			handleMultilingualAttributeWithLink((MultilingualAttribute) attribute, attributeNode, previousAttributeNode,
					mapAttribute, plibAttribute, valNode, previousValNode, links, insertCmdToExecute, intoClause, oid);
		} else {
			EntityDatatype range = attribute.getRange();
			if (range.isAssociationType()) {
				String nameTable = ((EntityDatatypeCategory) range).getCagetory().toSQL();
				handleRefAttribute(nameTable, attribute, attributeNode, plibAttribute, valNode, insertCmdToExecute,
						intoClause, oid);
			} else if (range.isCollectionAssociationType()) {
				EntityDatatypeCategory rangeCollection = (EntityDatatypeCategory) ((EntityDatatypeCollection) range)
						.getDatatype();
				String nameTable = rangeCollection.getCagetory().toSQL();
				handleCollectionAttribute(nameTable, attribute, attributeNode, plibAttribute, valNode, intoClause, oid);
			} else {
				handleSimpleAttributeWithLink(attribute, attributeNode, previousAttributeNode, mapAttribute,
						plibAttribute, valNode, previousValNode, links, insertCmdToExecute, intoClause, oid);
			}
		}
	}

	/**
	 * Handle the case of a non multilingual attribute
	 */
	private void handleSimpleAttributeWithLink(Attribute attribute, AST attributeNode, AST previousAttributeNode,
			OntoAttribute mapAttribute, PlibAttribute plibAttribute, AST valNode, AST previousValNode, List links,
			Map<String, InsertCommand> insertCmdToExecute, IntoClause intoClause, String oid) throws SQLException {

		String nameTable = mapAttribute.getMapTo().getOfEntity().toSQL();
		if (insertCmdToExecute.containsKey(nameTable)) {
			handleSimpleAttributeWithAlreadyProcessedLink(nameTable, attribute, attributeNode, previousAttributeNode,
					valNode, previousValNode, insertCmdToExecute, intoClause);
		} else {
			handleSimpleAttributeWithNewLink(nameTable, attribute, attributeNode, plibAttribute, valNode, links,
					insertCmdToExecute, intoClause, oid);
		}
	}

	/**
	 * Handle the case of an attribute of type ref
	 */
	private void handleRefAttribute(String nameTable, Attribute attribute, AST attributeNode, PlibAttribute link,
			AST valNode, Map<String, InsertCommand> insertCmdToExecute, IntoClause intoClause, String oid)
			throws SQLException {

		// we must also insert an entry in the association table
		Entity entityInstanciated = (Entity) intoClause.getCategoryInstantiated();

		ArrayList listOflink = attribute.getMapAttribut().getLink();
		String ridTableAssociation = null;
		String nameLink = null;
		PlibAttribute currentLink = null;
		String nameEntity = entityInstanciated.toSQL().toUpperCase();
		String OldValNode = valNode.getText();
		for (int i = 0; i < listOflink.size() - 1; i++) {
			currentLink = (PlibAttribute) listOflink.get(i);
			nameEntity = ((PlibAttribute) listOflink.get(i + 1)).getOfEntity().toSQL().toUpperCase();
			oid = handleIntermediateLink(nameEntity, attributeNode, currentLink, valNode, insertCmdToExecute,
					entityInstanciated, oid);
		}
		link = (PlibAttribute) listOflink.get(listOflink.size() - 1);
		nameLink = link.getName();
		String nameAssociationTable = OntoDBHelper.getNameAssociationTable(link.getOfEntity().getName(), nameLink);
		ridTableAssociation = OntoDBHelper.insertInIntermediateTable(session.connection(), nameAssociationTable, oid,
				nameEntity, OldValNode, nameTable.toUpperCase());
		if (listOflink.size() <= 1) {
			attributeNode.setText(nameLink);
			valNode.setType(OntoQLTokenTypes.NUM_INT);
			valNode.setText(ridTableAssociation);
		}
	}

	/**
	 * Handle the case of an attribute with a link not yet processed
	 */
	private String handleIntermediateLink(String nameTable, AST attributeNode, PlibAttribute link, AST valNode,
			Map<String, InsertCommand> insertCmdToExecute, Entity entityInstanciated, String oid) throws SQLException {
		InsertCommand cmd = null;
		if (insertCmdToExecute.containsKey(nameTable)) {
			cmd = (InsertCommand) insertCmdToExecute.get(nameTable);
		} else {
			cmd = new InsertCommand(nameTable);
			insertCmdToExecute.put(nameTable, cmd);
			// we must also insert an entry in the association table
			String nameAssociationTable = OntoDBHelper.getNameAssociationTable(link.getOfEntity().getName(),
					link.getName());
			String ridTableAssociation = OntoDBHelper.insertInIntermediateTable(walker.getSession().connection(),
					nameAssociationTable, oid, entityInstanciated.toSQL().toUpperCase(), cmd.getRid(),
					nameTable.toUpperCase());

			String attributeNodeText = link.getName();
			String valNodeText = ridTableAssociation;
			if (nameTable.indexOf("BSU") != -1) {
				// The rid_bsu needs to be valued manually
				// because the trigger in Postgresql
				// are not inherited
				attributeNodeText += ", rid_bsu";
				valNodeText += ", " + cmd.getRid();
			}

			attributeNode.setText(attributeNodeText);
			valNode.setType(OntoQLTokenTypes.NUM_INT);
			valNode.setText(valNodeText);
		}

		return cmd.getRid();
	}

	/**
	 * Handle the case of an attribute of type collection
	 */
	private void handleCollectionAttribute(String nameTable, Attribute attribute, AST attributeNode, PlibAttribute link,
			AST valNode, IntoClause intoClause, String oid) throws SQLException {

		// we must also insert an entry in the association table
		Entity entityInstanciated = (Entity) intoClause.getCategoryInstantiated();
		String nameLink = link.getName();
		String nameAssociationTable = OntoDBHelper.getNameAssociationTable(link.getOfEntity().getName(), nameLink);

		List listOfValues = extractValues(valNode.getText());
		List<String> listOfRidTableAssociation = new ArrayList<String>();
		for (int i = 0; i < listOfValues.size(); i++) {
			listOfRidTableAssociation.add(OntoDBHelper.insertInIntermediateTable(session.connection(),
					nameAssociationTable, oid, entityInstanciated.toSQL().toUpperCase(), listOfValues.get(i).toString(),
					nameTable.toUpperCase()));
		}
		String ridsTableAssociation = "ARRAY[";
		for (int i = 0; i < listOfRidTableAssociation.size(); i++) {
			if (i != 0) {
				ridsTableAssociation += ", ";
			}
			ridsTableAssociation += listOfRidTableAssociation.get(i);
		}
		ridsTableAssociation += "]";
		attributeNode.setText(nameLink);
		valNode.setType(OntoQLTokenTypes.NUM_INT);
		valNode.setText(ridsTableAssociation);
	}

	private void handleSimpleAttributeWithAlreadyProcessedLink(String nameTable, Attribute attribute, AST attributeNode,
			AST previousAttributeNode, AST valNode, AST previousValNode, Map insertCmdToExecute, IntoClause intoClause)
			throws SQLException {
		InsertCommand cmd = (InsertCommand) insertCmdToExecute.get(nameTable);
		attribute.setCurrentContext(null);
		cmd.addColumn(attribute.toSQL());
		cmd.addValue(valNode.getText());
		previousAttributeNode.setNextSibling(attributeNode.getNextSibling());
		previousValNode.setNextSibling(valNode.getNextSibling());
	}

	/**
	 * Handle the case of an attribute with a link not yet processed
	 */
	private void handleSimpleAttributeWithNewLink(String nameTable, Attribute attribute, AST attributeNode,
			PlibAttribute link, AST valNode, List links, Map<String, InsertCommand> insertCmdToExecute,
			IntoClause intoClause, String oid) throws SQLException {
		InsertCommand cmd = new InsertCommand(nameTable);
		attribute.setCurrentContext(null);
		cmd.addColumn(attribute.toSQL());
		cmd.addValue(valNode.getText());
		insertCmdToExecute.put(nameTable, cmd);
		// we must also insert an entry in the association table
		Entity entityInstanciated = (Entity) intoClause.getCategoryInstantiated();
		String nameLink = link.getName();
		String nameAssociationTable = OntoDBHelper.getNameAssociationTable(link.getOfEntity().getName(), nameLink);
		String ridTableAssociation = OntoDBHelper.insertInIntermediateTable(walker.getSession().connection(),
				nameAssociationTable, oid, entityInstanciated.toSQL().toUpperCase(), cmd.getRid(),
				nameTable.toUpperCase());
		String attributeNodeText = nameLink;
		String valNodeText = ridTableAssociation;
		if (nameTable.indexOf("bsu") != -1) {
			// The rid_bsu needs to be valued manually
			// because the trigger in Postgresql
			// are not inherited
			attributeNodeText += ", rid_bsu";
			valNodeText += ", " + cmd.getRid();
		}

		// Special treatment for namespace
		// We need to insert a library to see it in Plibeditor
		if (attribute.getName().equalsIgnoreCase("namespace")) {
			addLibrary(cmd.getRid(), StringHelper.removeFirstAndLastletter(valNode.getText()));

		}
		attributeNode.setText(attributeNodeText);
		valNode.setType(OntoQLTokenTypes.NUM_INT);
		valNode.setText(valNodeText);

	}

	public void addLibrary(String ridSupplierBSU, String namespace) {

		try {
			String ridLibrary = OntoDBHelper.getSequenceNextVal(session.connection(), "root_table_entity_rid_seq");

			// set the link between the library and the responsible supplier
			String ridLibraryToResponsibleSupplier = OntoDBHelper.insertInIntermediateTable(session.connection(),
					"dictionary_2_responsible_supplier", ridLibrary, "LIBRARY_E", ridSupplierBSU, "SUPPLIER_BSU_E");

			// The library structure
			String ridLibraryIdentification = OntoDBHelper.getSequenceNextVal(session.connection(),
					"root_table_entity_rid_seq");

			String query = "insert into library_iim_identification_e (rid, status, name, date, application) values (?,?,?,?,?) ";
			PreparedStatement pst = session.connection().prepareStatement(query);
			pst.setInt(1, Integer.parseInt(ridLibraryIdentification));
			pst.setString(2, "IS");
			pst.setString(3, "ISO_13584_25");
			pst.setInt(4, 2009);
			pst.setInt(5, 3);
			pst.executeUpdate();

			String ridLibraryToLibraryStructure = OntoDBHelper.insertInIntermediateTable(session.connection(),
					"dictionary_2_library_structure", ridLibrary, "LIBRARY_E", ridLibraryIdentification,
					"LIBRARY_IIM_IDENTIFICATION_E");

			// set the link between the library and the referred supplier
			String ridLibraryToReferredSupplier = OntoDBHelper.insertInIntermediateTable(session.connection(),
					"dictionary_2_referred_suppliers", ridLibrary, "LIBRARY_E", ridSupplierBSU, "SUPPLIER_BSU_E");
			String[] ridSuppliers = new String[1];
			ridSuppliers[0] = ridLibraryToReferredSupplier;

			// the names
			String ridItemNames = OntoDBHelper.createItemNames(session.connection(), namespace, namespace, "");

			String ridLibraryToItemNames = OntoDBHelper.insertInIntermediateTable(session.connection(),
					"dictionary_2_names", ridLibrary, "ITEM_CLASS_E", ridItemNames, "ITEM_NAMES_E");

			query = "insert into library_e (rid, responsible_supplier, library_structure, referred_suppliers, names) values (?,?,?,?,?) ";
			pst = session.connection().prepareStatement(query);
			pst.setInt(1, Integer.parseInt(ridLibrary));
			pst.setInt(2, Integer.parseInt(ridLibraryToResponsibleSupplier));
			pst.setInt(3, Integer.parseInt(ridLibraryToLibraryStructure));
			Array arrayRidSupplier = JDBCHelper.convertToIntegerArray(ridSuppliers);
			pst.setArray(4, arrayRidSupplier);
			pst.setInt(5, Integer.parseInt(ridLibraryToItemNames));
			pst.executeUpdate();
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	/**
	 * Handle the case of a multilingual attribute with a link already processed
	 */
	@SuppressWarnings("unchecked")
	private void handleMultilingualAttributeWithLink(MultilingualAttribute attribute, AST attributeNode,
			AST previousAttributeNode, OntoAttribute mapAttribute, PlibAttribute plibAttribute, AST valNode,
			AST previousValNode, List links, Map insertCmdToExecute, IntoClause intoClause, String oid)
			throws SQLException {

		String nameAttribute = mapAttribute.getName();
		if (insertCmdToExecute.containsKey(nameAttribute)) {
			handleMultilingualAttributeWithAlreadyProcessedLink(nameAttribute, attribute, attributeNode,
					previousAttributeNode, valNode, previousValNode, insertCmdToExecute, intoClause);
		} else {
			handleMultilingualAttributeWithNewLink(nameAttribute, attribute, attributeNode, plibAttribute, valNode,
					links, insertCmdToExecute, intoClause, oid);
		}
	}

	/**
	 * Handle the case of an already proceed link for a multilingual attribute
	 */
	private void handleMultilingualAttributeWithAlreadyProcessedLink(String nameAttribute,
			MultilingualAttribute attribute, AST attributeNode, AST previousAttributeNode, AST valNode,
			AST previousValNode, Map<String, String[]> insertCmdToExecute, IntoClause intoClause) throws SQLException {
		String[] values = (String[]) insertCmdToExecute.get(nameAttribute);
		int indexValue = attribute.getLgCode().equals(OntoQLHelper.FRENCH) ? 1 : 2;
		values[indexValue] = attribute.getRange().ontoQLToValue(valNode.getText());
		previousAttributeNode.setNextSibling(attributeNode.getNextSibling());
		previousValNode.setNextSibling(valNode.getNextSibling());
	}

	/**
	 * Handle the case of a multilingual attribute with a link not yet processed
	 */
	private void handleMultilingualAttributeWithNewLink(String nameAttribute, MultilingualAttribute attribute,
			AST attributeNode, PlibAttribute link, AST valNode, List links, Map<String, String[]> insertCmdToExecute,
			IntoClause intoClause, String oid) throws SQLException {

		boolean isLabel = ((OntoMultilingualAttribute) attribute.getMapAttribut()).isLabel();
		String[] values = new String[5];
		String rid = OntoDBHelper.getSequenceNextVal(walker.getSession().connection(), "root_table_entity_rid_seq");
		values[0] = rid;
		values[1] = "";
		values[2] = "";
		values[3] = "";
		values[4] = isLabel + "";
		int indexValue = attribute.getLgCode().equals(OntoQLHelper.FRENCH) ? 1 : 2;
		values[indexValue] = attribute.getRange().ontoQLToValue(valNode.getText());
		insertCmdToExecute.put(nameAttribute, values);

		// we must also insert an entry in the association table
		Entity entityInstanciated = (Entity) intoClause.getCategoryInstantiated();
		String nameLink = link.getName();
		String nameAssociationTable = OntoDBHelper.getNameAssociationTable(link.getOfEntity().getName(), nameLink);
		String textOrLabel = isLabel ? "LABEL" : "TEXT";
		String ridTableAssociation = null;
		// it this is a name a link with an item_name must be done
		if (nameAttribute.equals("name")) {
			ridTableAssociation = OntoDBHelper.insertInIntermediateTable(walker.getSession().connection(),
					nameAssociationTable, oid, entityInstanciated.toSQL().toUpperCase(), rid, "ITEM_NAMES_E");
		} else {
			ridTableAssociation = OntoDBHelper.insertInIntermediateTable(walker.getSession().connection(),
					nameAssociationTable, oid, entityInstanciated.toSQL().toUpperCase(), rid,
					"TRANSLATED_" + textOrLabel + "_E");
		}
		attributeNode.setText(link.getName());
		valNode.setType(OntoQLTokenTypes.NUM_INT);
		valNode.setText(ridTableAssociation);

	}

	private void executeInsertRequired(Map insertCmds) throws SQLException {
		Set entriesOfCmd = insertCmds.entrySet();
		for (Iterator iter = entriesOfCmd.iterator(); iter.hasNext();) {
			Entry currentEntry = (Entry) iter.next();
			if (currentEntry.getValue() instanceof InsertCommand) {
				String cmd = ((InsertCommand) currentEntry.getValue()).toSQL();
				DatabaseHelper.executeUpdate(cmd, walker.getSession());
			} else {
				String[] ridAndValues = (String[]) currentEntry.getValue();
				// special treatment for item names
				if (currentEntry.getKey().equals("name")) {
					OntoDBHelper.createItemNames(session.connection(), ridAndValues[0], ridAndValues[1],
							ridAndValues[2], "");
				} else {
					String isLabelText = ridAndValues[4];
					if (Boolean.valueOf(isLabelText).booleanValue()) {
						OntoDBHelper.createTranslatedLabel(session.connection(), ridAndValues[0], ridAndValues[1],
								ridAndValues[2], "");
					} else {
						OntoDBHelper.createTranslatedText(session.connection(), ridAndValues[0], ridAndValues[1],
								ridAndValues[2], "");
					}
				}
			}
		}
	}

	/**
	 * An insert command
	 */
	class InsertCommand {

		private String rid = null;

		/**
		 * List of columns.
		 */
		private List<String> columns = new ArrayList<String>();

		/**
		 * Name of the table instantiated.
		 */
		private String table;

		/**
		 * List of values.
		 */
		private List<String> values = new ArrayList<String>();

		public void addColumn(String column) {
			columns.add(column);
		}

		public void addValue(String value) {
			values.add(value);
		}

		/**
		 * return the internal identifier of the new instance
		 */
		public String getRid() throws SQLException {
			if (rid == null) {
				// the id of this new instance is the next element in
				// a sequence
				rid = OntoDBHelper.getSequenceNextVal(walker.getSession().connection(), "root_table_entity_rid_seq");
			}
			return rid;
		}

		public InsertCommand(String table) {
			this.table = table;
		}

		public String toSQL() throws SQLException {
			String res = "insert into " + table;

			String intoClause = "(rid";
			String valuesClause = "(" + getRid();
			int i = 0;
			for (Iterator iter = columns.iterator(); iter.hasNext();) {
				String currentColumn = (String) iter.next();
				intoClause += ", " + currentColumn;
				String currentValue = (String) values.get(i);
				valuesClause += ", " + currentValue;
				i++;
			}
			intoClause += ")";
			valuesClause += ")";
			res = res + " " + intoClause + " values " + valuesClause;
			return res;
		}
	}

	@Override
	public void evaluateDescription(Description description, IdentNode node) {
		node.setType(SQLTokenTypes.COLUMN);
		node.setText(getSQLWithoutPrefix(description));
	}

	@Override
	public void evaluateExt(Category category, IdentNode node) {
		auxEvaluateExt(category, node);
	}

	@Override
	public void postProcessInsert(InsertStatement insert) {
		IntoClause intoClause = insert.getIntoClause();
		Category categoryInstantiated = intoClause.getCategoryInstantiated();
		if (categoryInstantiated.isClass()) {
			postProcessInsertInContent(insert, intoClause);
		} else {
			postProcessInsertInOntology(insert, intoClause);
		}

	}

	@Override
	public void evaluateExt(Category category, FromElement node) {
		node.setType(SQLTokenTypes.TABLE);
		boolean oldPolymorph = category.isPolymorph();
		if (category.isAbstract()) {
			throw new JOBDBCException(
					"can not update the abstract " + category.getTypeLabel() + " '" + category.getName() + "'");
		}
		node.setText(category.toSQL());
		category.setPolymorph(oldPolymorph);
	}

	@Override
	public void evaluateExtPolymorph(Category category, FromElement node) {
		// For the moment same treatment as non polymorph
		// Latter, will send a DML statement for each subclass
		evaluateExt(category, node);
	}
}
