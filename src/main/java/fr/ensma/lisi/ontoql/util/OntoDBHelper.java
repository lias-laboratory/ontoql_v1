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
package fr.ensma.lisi.ontoql.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.ontomodel.OntoAttribute;
import fr.ensma.lisi.ontoql.ontomodel.OntoEntity;

/**
 * Method usefull to handle data in the OntoDB repository.
 * 
 * @author Stéphane JEAN
 */
public class OntoDBHelper {

	/**
	 * A logger for this class.
	 */
	private static Log log = LogFactory.getLog(OntoDBHelper.class);

	/**
	 * Separator between the oid and the type of an instance.
	 */
	public static final char TYPEOF_SEPARATOR = '#';

	public static final String STRING_TYPE_TABLE = "STRING_TYPE_E";

	public static final String URI_TYPE_TABLE = "URI_TYPE_E";

	public static final String COUNT_TYPE_TABLE = "COUNT_TYPE_E";

	public static final String MULTILINGUAL_STRING_TYPE_TABLE = "MULTILINGUAL_STRING_TYPE_E";

	public static final String INT_TYPE_TABLE = "INT_TYPE_E";

	public static final String BOOLEAN_TYPE_TABLE = "BOOLEAN_TYPE_E";

	public static final String INT_MEASURE_TYPE_TABLE = "INT_MEASURE_TYPE_E";

	public static final String REAL_MEASURE_TYPE_TABLE = "REAL_MEASURE_TYPE_E";

	public static final String REAL_TYPE_TABLE = "REAL_TYPE_E";

	public static final String ASSOCIATION_TYPE_TABLE = "CLASS_INSTANCE_TYPE_E";

	public static final String COLLECTION_TYPE_TABLE = "ENTITY_INSTANCE_TYPE_FOR_AGGREGATE_E";

	public static final String ENUMERATE_TYPE_TABLE = "NON_QUANTITATIVE_CODE_TYPE_E";

	/**
	 * integer use to generate an unique code.
	 */
	private static int randomOffset = 0;

	public static String getPLIBAttributeForOid(String nameEntity) {

		String res = "rid";
		if (nameEntity.equals("class") || nameEntity.equals("property") || nameEntity.equals("concept")
				|| nameEntity.equals("ontology")) {
			res = "rid_bsu";
		}
		return res;
	}

	/**
	 * Get the name of an association table for a given attribute defined on an
	 * entity.
	 * 
	 * @param entity    entity entity of definition of the attribute
	 * @param attribute attribute a given attribute
	 * @return the name of the association table
	 */
	public static String getNameAssociationTable(OntoEntity entity, OntoAttribute attribute) {
		String res = null;
		String nameEntity = entity.getMapTo().getName();
		String nameAttribute = attribute.getName();
		res = getNameAssociationTable(nameEntity, nameAttribute);
		return res;
	}

	/**
	 * Get the name of an association table for a given name of attribute and a
	 * given name of entity.
	 * 
	 * @param entity    entity entity of definition of the attribute
	 * @param attribute attribute a given attribute
	 * @return the name of the association table
	 */
	public static String getNameAssociationTable(String nameEntity, String nameAttribute) {
		String res = null;
		res = nameEntity + "_2_" + nameAttribute;
		return res;
	}

	/**
	 * Create an association table for a given attribute defined on an entity.
	 * 
	 * @param entity    entity of definition of the attribute
	 * @param attribute a given attribute
	 * @param session   access to the database
	 */
	public static void createAssociationTable(OntoEntity entity, OntoAttribute attribute, OntoQLSession session) {
		String nameTableAssociation = getNameAssociationTable(entity, attribute);
		StringBuffer ddlCommand = new StringBuffer();
		ddlCommand.append("CREATE TABLE " + nameTableAssociation + " (");
		ddlCommand.append("rid int8 NOT NULL DEFAULT nextval('public.root_table_association_rid_seq'::text),");
		ddlCommand.append("rid_s int8,");
		ddlCommand.append("tablename_s varchar,");
		ddlCommand.append("rid_d int8,");
		ddlCommand.append("tablename_d varchar,");
		ddlCommand.append("CONSTRAINT " + nameTableAssociation + "_pkey PRIMARY KEY (rid)");
		ddlCommand.append(") INHERITS (root_table_association) ");
		ddlCommand.append("WITH OIDS");

		try {
			// create the structure in the database
			Connection cnx = session.connection();
			Statement stmt = cnx.createStatement();
			String cmdDDL = ddlCommand.toString();
			log.info(cmdDDL);
			stmt.executeUpdate(cmdDDL);
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}

	}

	/**
	 * Drop an association table for a given attribute defined on an entity.
	 * 
	 * @param entity    entity of definition of the attribute
	 * @param attribute a given attribute
	 * @param session   access to the database
	 */
	public static void dropAssociationTable(OntoEntity entity, OntoAttribute attribute, OntoQLSession session) {
		String nameTableAssociation = getNameAssociationTable(entity, attribute);
		StringBuffer ddlCommand = new StringBuffer();
		ddlCommand.append("DROP TABLE " + nameTableAssociation);

		try {
			// create the structure in the database
			Connection cnx = session.connection();
			Statement stmt = cnx.createStatement();
			String cmdDDL = ddlCommand.toString();
			log.info(cmdDDL);
			stmt.executeUpdate(cmdDDL);
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}

	}

	/**
	 * add a column for a given association attribute.
	 * 
	 * @param entity    entity of definition of the attribute
	 * @param attribute a given attribute
	 * @param session   access to the database
	 */
	public static void addColumnArrayForAssociation(OntoEntity entity, OntoAttribute attribute, OntoQLSession session) {
		String nameTableEntity = entity.toSQL();
		String nameColumnnAttribute = attribute.getName();
		StringBuffer ddlCommand = new StringBuffer();
		ddlCommand.append("ALTER TABLE " + nameTableEntity + " ADD COLUMN ");
		ddlCommand.append(nameColumnnAttribute + " int8[] ");
		try {
			// create the structure in the database
			Connection cnx = session.connection();
			Statement stmt = cnx.createStatement();
			String cmdDDL = ddlCommand.toString();
			log.info(cmdDDL);
			stmt.executeUpdate(cmdDDL);
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * add a column for an attribute of type collection of a primitive type.
	 * 
	 * @param entity    entity of definition of the attribute
	 * @param attribute a given attribute
	 * @param session   access to the database
	 */
	public static void addColumnArray(OntoEntity entity, OntoAttribute attribute, OntoQLSession session) {
		String nameTableEntity = entity.toSQL();
		String nameColumnnAttribute = attribute.getName();
		StringBuffer ddlCommand = new StringBuffer();
		ddlCommand.append("ALTER TABLE " + nameTableEntity + " ADD COLUMN ");
		ddlCommand.append(nameColumnnAttribute + " " + attribute.getRange().getExtent());
		try {
			// create the structure in the database
			Connection cnx = session.connection();
			Statement stmt = cnx.createStatement();
			String cmdDDL = ddlCommand.toString();
			log.info(cmdDDL);
			stmt.executeUpdate(cmdDDL);
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * add a column for a given attribute defined on an entity that references an
	 * association table.
	 * 
	 * @param entity    entity of definition of the attribute
	 * @param attribute a given attribute
	 * @param session   access to the database
	 */
	public static void addColumnReferencingAssociationTable(OntoEntity entity, OntoAttribute attribute,
			OntoQLSession session) {
		String nameTableEntity = entity.toSQL();
		String nameColumnnAttribute = attribute.getName();
		String nameTableAssociation = getNameAssociationTable(entity, attribute);
		StringBuffer ddlCommand = new StringBuffer();
		ddlCommand.append("ALTER TABLE " + nameTableEntity + " ADD COLUMN ");
		ddlCommand.append(nameColumnnAttribute + " int8 ");
		StringBuffer ddlCommandForeignKey = new StringBuffer();
		ddlCommandForeignKey.append("ALTER TABLE " + nameTableEntity + " ADD CONSTRAINT ck_" + nameColumnnAttribute);
		ddlCommandForeignKey.append(" FOREIGN KEY (" + nameColumnnAttribute + ")");
		ddlCommandForeignKey.append(" REFERENCES " + nameTableAssociation + " (rid) MATCH SIMPLE");
		ddlCommandForeignKey.append(" ON UPDATE NO ACTION ON DELETE NO ACTION");
		try {
			// create the structure in the database
			Connection cnx = session.connection();
			Statement stmt = cnx.createStatement();
			String cmdDDL = ddlCommand.toString();
			log.info(cmdDDL);
			stmt.executeUpdate(cmdDDL);
			cmdDDL = ddlCommandForeignKey.toString();
			log.info(cmdDDL);
			stmt.executeUpdate(cmdDDL);
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Create the link beetween two entity in the intermediate table.
	 * 
	 * @param connection  access to the database
	 * @param tablename   name of the intermediate table
	 * @param rid_s       id of the source entity
	 * @param tablename_s name of the table of the source entity
	 * @param rid_d       id of the target entity
	 * @param tablename_d name of the table of the target entity
	 * @return the rid inserted
	 * @throws JOBDBCException
	 */
	public static String insertInIntermediateTable(Connection connection, String tablename, String rid_s,
			String tablename_s, String rid_d, String tablename_d) throws SQLException {

		String res = null;

		res = getSequenceNextVal(connection, "root_table_association_rid_seq");

		String query = "insert into " + tablename
				+ " (rid, rid_s, tablename_s, rid_d, tablename_d) values (?,?,?,?,?) ";
		log.info(
				query + " param (" + res + ", " + rid_s + ", " + tablename_s + ", " + rid_d + ", " + tablename_d + ")");
		PreparedStatement pst = connection.prepareStatement(query);
		pst.setInt(1, Integer.parseInt(res));
		pst.setInt(2, Integer.parseInt(rid_s));
		pst.setString(3, tablename_s);
		pst.setInt(4, Integer.parseInt(rid_d));
		pst.setString(5, tablename_d);

		pst.executeUpdate();

		return res;
	}

	/**
	 * Generate the code of a BSU.
	 * 
	 * @return a generrated code of a BSU
	 */
	public static String genereCode() {
		String res;

		// Code length = 14 ( = min *_code_type).
		// pattern length = 16 but 10^16-1 < 16^14-1 so its ok.
		// Format the current time.
		String dateString;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyDDDHHmmssSSS");
		Date currentTime = new Date();

		dateString = formatter.format(currentTime);
		dateString = Long.toHexString(new Long(dateString).longValue() + (randomOffset++));
		res = dateString.toUpperCase();

		return res;
	}

	/**
	 * Get the nextval of a given sequence. This val can not be used by another
	 * process.
	 * 
	 * @param connection   access to the database
	 * @param nameSequence name of the given sequence
	 * @return the nextval of this sequence
	 * @throws SQLException if a database error occurs
	 */
	public static String getSequenceNextVal(Connection connection, String nameSequence) throws SQLException {

		String res = null;

		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery("select nextval('" + nameSequence + "')");
		rs.next();
		res = rs.getString(1);

		return res;

	}

	/**
	 * Create the translated values
	 * 
	 * @return the rid of the language of these values
	 * @throws SQLException
	 */
	private static String createTranslatedValue(Connection connection, String rid, String nameFr, String nameEn,
			String nameEs, boolean isLabel) throws SQLException {
		// the result
		String ridLanguage = null;

		String textOrLabel = isLabel ? "label" : "text";
		// create the multilingual values (array_value ...)
		String[] ridNames = OntoDBHelper.createMultilingualNames(connection, nameFr, nameEn, nameEs, textOrLabel);

		// Set the link between the translated label and the language
		// Get the rid of the reference language
		Statement st = connection.createStatement();
		ResultSet rs = st.executeQuery("select rid from present_translations_e");
		rs.next(); // assumes that there is one and only one result
		ridLanguage = rs.getString(1);

		String ridTranslatedLabelToLanguages = OntoDBHelper.insertInIntermediateTable(connection,
				"translated_" + textOrLabel + "_2_languages", rid, "TRANSLATED_" + textOrLabel.toUpperCase() + "_E",
				ridLanguage, "PRESENT_TRANSLATIONS_E");

		String updateStatement = "insert into translated_" + textOrLabel + "_e (rid, " + textOrLabel
				+ "s, languages) values (" + rid + ", ARRAY[" + ridNames[0] + "," + ridNames[1] + "," + ridNames[2]
				+ "]" + ", " + ridTranslatedLabelToLanguages + ")";
		st.executeUpdate(updateStatement);
		log.info(updateStatement);
		return ridLanguage;
	}

	/**
	 * Create text values
	 */
	public static String createTranslatedText(Connection connection, String rid, String nameFr, String nameEn,
			String nameEs) throws SQLException {
		return createTranslatedValue(connection, rid, nameFr, nameEn, nameEs, false);
	}

	/**
	 * Create label values
	 */
	public static String createTranslatedLabel(Connection connection, String rid, String nameFr, String nameEn,
			String nameEs) throws SQLException {
		return createTranslatedValue(connection, rid, nameFr, nameEn, nameEs, true);
	}

	public static String createDefinitions(Connection connection, String defFr, String defEn, String defEs)
			throws SQLException {

		// the rid of the item names
		String ridTranslatedText = OntoDBHelper.getSequenceNextVal(connection, "root_table_entity_rid_seq");
		createTranslatedText(connection, ridTranslatedText, defFr, defEn, defEs);

		return ridTranslatedText;
	}

	public static String createItemNames(Connection connection, String nameFr, String nameEn, String nameEs)
			throws SQLException {
		// the rid of the item names
		String ridItemNames = OntoDBHelper.getSequenceNextVal(connection, "root_table_entity_rid_seq");
		return createItemNames(connection, ridItemNames, nameFr, nameEn, nameEs);
	}

	public static String createItemNames(Connection connection, String ridItemNames, String nameFr, String nameEn,
			String nameEs) throws SQLException {

		// the rid of the item names
		String ridTranslatedLabel = OntoDBHelper.getSequenceNextVal(connection, "root_table_entity_rid_seq");
		String ridLanguage = createTranslatedLabel(connection, ridTranslatedLabel, nameFr, nameEn, nameEs);

		// Link with the language
		String ridItemNamesToLanguages = OntoDBHelper.insertInIntermediateTable(connection, "item_names_2_languages",
				ridItemNames, "ITEM_NAMES_E", ridLanguage, "PRESENT_TRANSLATIONS_E");

		// Link with the translated label
		String ridItemNamesToTranslatedLabel = OntoDBHelper.insertInIntermediateTable(connection,
				"item_names_2_preferred_name", ridItemNames, "ITEM_NAMES_E", ridTranslatedLabel, "TRANSLATED_LABEL_E");

		Statement st = connection.createStatement();
		String updateStatement = "insert into item_names_e (rid, preferred_name, synonymous_names, languages) values ("
				+ ridItemNames + ", " + ridItemNamesToTranslatedLabel + ", '{}', " + ridItemNamesToLanguages + ")";
		st.executeUpdate(updateStatement);
		log.info(updateStatement);
		return ridItemNames;
	}

	/**
	 * Create the multilingual names of this class.
	 * 
	 * @return the rid of the names inserted
	 * @throws SQLException if a database access occurs
	 */
	private static String[] createMultilingualNames(Connection connection, String nameFr, String nameEn, String nameEs,
			String textOrLabel) throws SQLException {

		// The assumption is that every multilingual
		// catalog supports french, english and spanish
		// in this given order

		String[] res = new String[3];
		Statement st = connection.createStatement();

		String exprSequence = "nextval('array_value_translated_" + textOrLabel + "_" + textOrLabel + "s_rid_seq')";
		ResultSet rs = st.executeQuery("select " + exprSequence + ", " + exprSequence + ", " + exprSequence);

		rs.next();
		res[0] = rs.getString(1);
		res[1] = rs.getString(2);
		res[2] = rs.getString(3);

		insertLabel(connection, res[0], nameFr, textOrLabel);
		insertLabel(connection, res[1], nameEn, textOrLabel);
		insertLabel(connection, res[2], "", textOrLabel); // Spanish not yet
		// supported

		return res;

	}

	/**
	 * Insert a multilingual label.
	 * 
	 * @param label label to insert
	 * @throws SQLException if a database access occurs
	 */
	private static void insertLabel(Connection connection, String rid, String label, String textOrLabel)
			throws SQLException {

		String query = "insert into array_value_translated_" + textOrLabel + "_" + textOrLabel
				+ "s (rid, value) values (?,?)";
		PreparedStatement pst = connection.prepareStatement(query);
		log.info(query + "param " + rid + ", " + label);
		pst.setInt(1, Integer.parseInt(rid));
		pst.setString(2, label);
		pst.executeUpdate();
	}

	/**
	 * Delete the typeof in a string containing an oid and the type of an instance.
	 * 
	 * @param param a string containing an oid and the type of an instance.
	 * @return the string without the typeof
	 */
	public static String deleteTypeOf(String param) {
		String res = param;
		int indexOfSeparator = param.indexOf(TYPEOF_SEPARATOR);
		if (indexOfSeparator != -1) {
			res = param.substring(0, indexOfSeparator);
		}
		return res;
	}

	/**
	 * belaidn - returns the substring without the suffixe
	 * 
	 * example : (str == "class_e") => (subString == "class")
	 */
	public static String removeSuffixeOfEntity(String str) {

		String subString = str;

		if (subString.indexOf("_") >= 0) {
			subString = subString.substring(0, subString.indexOf("_"));
		}

		return subString;
	}

	/**
	 * belaidn - returns the substring without the table name
	 * 
	 * example : (str == "class_e.testArray") => (subString == "testArray")
	 */
	public static String getAttributeFromExpression(String str) {

		String subString = str;

		if (subString.indexOf(".") >= 0) {
			subString = subString.substring(subString.indexOf(".") + 1);
		}

		return subString;
	}
}
