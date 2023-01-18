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
package fr.ensma.lisi.ontoql.core.ontodb;

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ensma.lisi.ontoql.core.AbstractEntityClass;
import fr.ensma.lisi.ontoql.core.AbstractEntityProperty;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.exception.JDBCExceptionHelper;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.NotSupportedDatatypeException;
import fr.ensma.lisi.ontoql.exception.SQLExceptionConverterFactory;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.util.ArrayHelper;
import fr.ensma.lisi.ontoql.util.JDBCHelper;
import fr.ensma.lisi.ontoql.util.OntoDBHelper;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Methods required on an ontology class for the conversion of OntoQL into SQL
 * Implementation on Ontolib (both Flat and Onto DB)
 * 
 * @author Stéphane JEAN
 */
public class EntityClassOntoDB extends AbstractEntityClass {

	private static final Log log = LogFactory.getLog(EntityClassOntoDB.class);

	/**
	 * A session needed to access to the underlying OBDB
	 */
	protected OntoQLSession session;

	/**
	 * In OntoDB a class has an oid = ridBSU and an oid for the class = ridClass
	 */
	protected String ridClass;

	public EntityClassOntoDB(String id, String namespace, OntoQLSession session, FactoryEntity factory) {
		super(id, namespace, session.getReferenceLanguage(), factory);
		this.session = session;
	}

	public EntityClassOntoDB(String id, OntoQLSession session, FactoryEntity factory) {
		this(id, session.getDefaultNameSpace(), session, factory);
	}

	@Override
	public void dropProperty(EntityProperty prop, OntoQLSession session) throws JOBDBCException {
		// needs to be implemented
	}

	@Override
	public void setCurrentLanguage(String lg) {
		session.setReferenceLanguage(lg);
	}

	@Override
	protected String getCurrentLanguage() {
		return session.getReferenceLanguage();
	}

	/**
	 * Helper method to add a projection to an union query
	 */
	private String addProject(String currentQuery, String project) {
		String res = currentQuery;

		boolean isCurrentQueryEmpty = currentQuery.equals("");
		boolean isProjectEmpty = project.equals("");

		if (!isCurrentQueryEmpty && !isProjectEmpty)
			res += " union all " + project;
		else if (isCurrentQueryEmpty && !isProjectEmpty) {
			res = project;
		}
		return res;
	}

	@Override
	public String toSQL(boolean polymorph) throws JOBDBCException {

		String res = "";

		String alias = tableAlias == null ? "" : " " + tableAlias;
		if (!polymorph) {
			if (isAbstract()) {
				res = ("(select 0 where false)" + alias);
			} else {
				res = getNameExtent() + alias;
			}
		} else {
			EntityProperty[] properties = getUsedPropertiesPolymorph();
			if (properties.length == 0) { // No Extent in all the hierarchy
				return toSQL(false);
			}
			// project on id (for implicit join) and used properties
			EntityProperty ridProperty = (EntityProperty) factory.createDescription("oid");
			EntityProperty[] propertiesToProject = new EntityProperty[properties.length + 1];
			propertiesToProject[0] = ridProperty;

			for (int i = 1; i < propertiesToProject.length; i++) {
				propertiesToProject[i] = properties[i - 1];
			}
			res = "(" + project(propertiesToProject, true) + ")" + alias;
		}

		return res;
	}

	@Override
	public String project(EntityProperty[] properties, boolean polymorph) {
		String res = "";

		if (!polymorph) {
			if (!isAbstract()) {
				res += "select ";
				for (int i = 0; i < properties.length; i++) {
					res += properties[i].toSQL(this, false) + " as " + properties[i].toSQL(null, false) + ", ";
					// Add the typeOf this entity
					if (i == 0) {
						res += "'e" + getInternalId() + "' as tablename, ";
					}
					try {
						if (properties[i].getRange().isCollectionAssociationType()) {
							res += properties[i].toSQL(this, false).replaceFirst("rids", "tablenames")
									.replaceFirst("::int8", "::varchar") + " as "
									+ properties[i].toSQL(null, false).replaceFirst("rids", "tablenames") + ", ";
						} else if (properties[i].getRange().isAssociationType()) {
							res += properties[i].toSQL(this, false).replaceFirst("rid", "tablename")
									.replaceFirst("::int8", "::varchar") + " as "
									+ properties[i].toSQL(null, false).replaceFirst("rid", "tablename") + ", ";
						} else if (properties[i].isMultilingualDescription()) {
							String referenceLanguage = session.getReferenceLanguage();
							res += properties[i].toSQL(this, false).replaceFirst(referenceLanguage,
									OntoQLHelper.getOtherLanguage(referenceLanguage)) + ", ";
							;
						}
					} catch (NotSupportedDatatypeException oExc) {
					}
				}
				res = res.substring(0, res.length() - ", ".length());
				res += " from " + this.toSQL(false);
			}
		} else {
			res = addProject(res, project(properties, false));
			for (int i = 0; i < this.getDirectSubclasses().length; i++) {
				res = addProject(res, this.getDirectSubclasses()[i].project(properties, true));
			}
		}

		return res;
	}

	@Override
	protected void load() throws JOBDBCException {
		// Bug - load by name - if two different class have the same name
		// in two differents languages (not treated now)

		// TODO must be an ontoQL query whith OntoqlResultSet exploitation
		// SELECT #oid, #code, #version, #name[], #name[] FROM #class where "

		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("SELECT c.rid_bsu, c.rid, ");
		queryBuffer.append("crid.code, crid.version,");
		queryBuffer.append("labelen.value, labelfr.value ");
		queryBuffer.append("FROM class_bsu_e crid, class_e c, supplier_bsu_e supplier, ");
		queryBuffer.append("class_and_property_elements_2_names cton, ");
		queryBuffer.append("item_names_2_preferred_name itop,translated_label_e t, ");
		queryBuffer.append("array_value_translated_label_labels labelen,");
		queryBuffer.append("array_value_translated_label_labels labelfr ");

		queryBuffer.append("where crid.rid = c.rid_bsu ");
		queryBuffer.append("and c.rid=cton.rid_s ");
		queryBuffer.append("and crid.rid_supplier = supplier.rid and supplier.code = '" + namespace + "' ");
		queryBuffer.append("and cton.rid_d = itop.rid_s ");
		queryBuffer.append("and itop.rid_d = t.rid ");
		queryBuffer
				.append("and labelen.rid = t.labels[" + OntoQLHelper.getLanguage(OntoQLHelper.ENGLISH, session) + "] ");
		queryBuffer
				.append("and labelfr.rid = t.labels[" + OntoQLHelper.getLanguage(OntoQLHelper.FRENCH, session) + "] ");

		String identifier = null;
		// the class may have been inialized in another language
		// yet we consider only english and french
		String otherLg = OntoQLHelper.getOtherLanguage(getCurrentLanguage());

		// This query consider non multilingue catalog and
		// multilingu catalog by using an union query
		// if the loading is done throught identifier, the conditions for
		// the two query are the same and we must avoid to recalculate it
		String commonCondition = "";
		boolean isCommonCondition = false; // if the loading is done throught
		// identifier

		if (isInternalIdInitialized()) {
			identifier = getValueInternalId();
			isCommonCondition = true;
			commonCondition = "and c.rid_bsu = " + identifier;
			queryBuffer.append(commonCondition);
		} else if (isNameInitialized()) {
			identifier = getValueName();
			queryBuffer.append("and label" + getCurrentLanguage() + ".value = '" + identifier + "'");
		} else if (isExternalIdInitialized()) {
			identifier = getValueExternalId();
			isCommonCondition = true;
			commonCondition = " and crid.code = '" + code + "'";
			if (version != null) {
				commonCondition += " and crid.version = '" + version + "'";
			}
			queryBuffer.append(commonCondition);
		} else if (isNameInitialized(otherLg)) {
			queryBuffer.append("and label" + otherLg + ".value = '" + getValueName(otherLg) + "'");

		} else {
			String msg = "An identifier is needed to load the class.";
			msg += getCurrentLanguage() == null ? " Set the reference language to identify the class by a name" : "";
			throw new JOBDBCException(msg);
		}

		queryBuffer.append(" union \n");

		queryBuffer.append("SELECT c.rid_bsu, c.rid, ");
		queryBuffer.append("crid.code, crid.version,");
		queryBuffer.append("t.value, t.value ");
		queryBuffer.append("FROM class_bsu_e crid, class_e c, supplier_bsu_e supplier, ");
		queryBuffer.append("class_and_property_elements_2_names cton, ");
		queryBuffer.append("item_names_2_preferred_name itop,label_t t ");

		queryBuffer.append("where crid.rid = c.rid_bsu ");
		queryBuffer.append("and c.rid=cton.rid_s ");
		queryBuffer.append("and crid.rid_supplier = supplier.rid and supplier.code = '" + namespace + "' ");
		queryBuffer.append("and cton.rid_d = itop.rid_s ");
		queryBuffer.append("and itop.rid_d = t.rid ");

		if (isCommonCondition) {
			queryBuffer.append(commonCondition);
		} else if (isNameInitialized()) {
			queryBuffer.append("and t.value = '" + getValueName() + "'");
		}

		log.debug("Loading an ontology class : " + queryBuffer.toString());
		try {
			Statement st = session.connection().createStatement();
			ResultSet rs = st.executeQuery(queryBuffer.toString());

			int i = 1;
			if (!rs.next()) {
				throw new JOBDBCException(identifier + " is not a valid class name on the namespace " + namespace);
			}

			String id = rs.getString(i++);
			ridClass = rs.getString(i++);
			String code = rs.getString(i++);
			String version = rs.getString(i++);
			String name_en = rs.getString(i++);
			String name_fr = rs.getString(i++);

			setValueInternalId(id);
			setValueExternalId(code + "-" + version);
			setName(name_en, OntoQLHelper.ENGLISH);
			setName(name_fr, OntoQLHelper.FRENCH);

		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}

	}

	@Override
	public String getNameExtent() throws JOBDBCException {
		return "e" + getInternalId();
	}

	@Override
	public void createView(EntityProperty[] propertiesExtent, String query) {

		Boolean[] containOid = new Boolean[1];
		String[] ridProperties = getRidPropertiesWithoutOid(propertiesExtent, new StringBuffer(), containOid);

		String oid = "";
		if (!containOid[0].booleanValue()) {
			oid = "rid,";
		}
		query.trim();
		query = "select " + oid + " version_min, version_max, " // version_min
		// are required
		// for the
		// update of
		// view
				+ query.substring(6);

		try {
			Statement st = session.connection().createStatement();
			st.executeUpdate("create view " + getNameExtent() + " as " + query);
			log.debug("Create a view: " + query);
		} catch (SQLException sqle) {
			throw JDBCExceptionHelper.convert(SQLExceptionConverterFactory.buildMinimalSQLExceptionConverter(), sqle,
					"could not execute query", "");
		}

		// We must also insert a class_extension
		createClassExtension(ridProperties);
		// the next time the used properties are asked
		// they must be reloaded from the database
		usedProperties = null;
	}

	@Override
	public void createTable(EntityProperty[] propertiesExtent) throws JOBDBCException {

		String nameExtent = getNameExtent();

		StringBuffer updateCommand = new StringBuffer("CREATE TABLE " + nameExtent
				+ "(rid int8 NOT NULL DEFAULT nextval('public.root_table_extension_rid_seq'::text), "
				+ "version_min int8 NOT NULL, " + "version_max int8,");
		String[] ridProperties = getRidPropertiesWithoutOid(propertiesExtent, updateCommand, new Boolean[1]);

		updateCommand.append(
				"CONSTRAINT " + getNameExtent() + "_pkey PRIMARY KEY (rid)) INHERITS (root_table_extension) WITH OIDS");
		System.out.println(updateCommand.toString());
		try {
			Statement st = session.connection().createStatement();
			st.executeUpdate(updateCommand.toString());
			log.debug("Create an extent: " + updateCommand);

		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
		// We must also insert a class_extension
		createClassExtension(ridProperties);

		// the next time the used properties are asked
		// they must be reloaded from the database
		usedProperties = null;

	}

	@Override
	public void addColumnToTable(EntityProperty propertyToAdd) throws JOBDBCException {
		String nameExtent = getNameExtent();

		StringBuffer updateCommand = new StringBuffer("ALTER TABLE " + nameExtent + " ADD ");

		if (!(propertyToAdd instanceof EntityPropertyOidOntoDB)) {
			propertyToAdd.setCurrentContext(this);
			if (!propertyToAdd.isDefined()) {
				throw new JOBDBCException(
						"The property " + propertyToAdd.getName() + " is not defined on " + getName());
			}
			updateCommand.append(propertyToAdd.getExtent());

			try {
				Statement st = session.connection().createStatement();
				log.debug("Alter an extent: " + updateCommand);
				st.executeUpdate(updateCommand.toString());

			} catch (SQLException e) {
				throw new JOBDBCException(e);
			}
			// We must also insert a class_extension
			addPropertyToClassExtension(propertyToAdd.getInternalId());

			// the next time the used properties are asked
			// they must be reloaded from the database
			usedProperties = null;
		}
	}

	/**
	 * Retrieve the identifier of all the properties which are not an Oid property.
	 * Moreover, check that all the properties are defined on this class.
	 * 
	 * @param propertiesExtent list of properties
	 * @param updateCommand    a string that represent the list of the column of the
	 *                         extent created
	 * @param isOidPresent     true if there was an oid
	 * @return the list of identifier of all the properties which are not an Oid
	 *         property.
	 */
	private String[] getRidPropertiesWithoutOid(EntityProperty[] propertiesExtent, StringBuffer updateCommand,
			Boolean[] isOidPresent) {
		String[] res = null;
		isOidPresent[0] = new Boolean(false);

		EntityProperty currentProperty = null;
		List<String> listRidProperties = new ArrayList<String>(propertiesExtent.length);
		for (int i = 0; i < propertiesExtent.length; i++) {
			currentProperty = propertiesExtent[i];
			if (!(currentProperty instanceof EntityPropertyOidOntoDB)) {
				currentProperty.setCurrentContext(this);
				if (!currentProperty.isDefined()) {
					throw new JOBDBCException(
							"The property " + currentProperty.getName() + " is not defined on " + getName());
				}
				listRidProperties.add(currentProperty.getInternalId());
				updateCommand.append(currentProperty.getExtent() + ", ");
			} else {
				isOidPresent[0] = new Boolean(true);
			}
		}
		res = (String[]) ArrayHelper.toStringArray(listRidProperties);

		return res;
	}

	/**
	 * Create a class extension for this class
	 * 
	 * @param ridProperties the set of properties used on this class
	 */
	private void createClassExtension(String[] ridProperties) {

		try {

			// get the rid BSU of the class
			String ridClassExtension = OntoDBHelper.getSequenceNextVal(session.connection(),
					"root_table_entity_rid_seq");

			String ridClassExtensionToClassBsu = OntoDBHelper.insertInIntermediateTable(session.connection(),
					"class_extension_2_dictionary_definition", ridClassExtension, "EXPLICIT_ITEM_CLASS_EXTENSION_E",
					getInternalId(), "CLASS_BSU_E");

			String query = "insert into EXPLICIT_ITEM_CLASS_EXTENSION_E (rid, dictionary_definition, rid_bsu, tablename_bsu, properties, table_like) values (?,?,?,?,?,?)";

			PreparedStatement pst = session.connection().prepareStatement(query);
			int i = 1;
			pst.setInt(i++, Integer.parseInt(ridClassExtension));
			pst.setInt(i++, Integer.parseInt(ridClassExtensionToClassBsu));
			pst.setInt(i++, Integer.parseInt(getInternalId()));
			pst.setString(i++, "CLASS_BSU_E");
			Array arrayRid = JDBCHelper.convertToIntegerArray(ridProperties);
			pst.setArray(i++, arrayRid);
			pst.setBoolean(i++, true);
			pst.executeUpdate();
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}

	}

	/**
	 * Add a property to the class extension of this class.
	 * 
	 * @param ridProperty
	 */
	private void addPropertyToClassExtension(String ridProperty) {

		try {
			String query = "update EXPLICIT_ITEM_CLASS_EXTENSION_E set properties = properties || " + ridProperty
					+ "::varchar where rid_bsu = " + getInternalId();
			Statement st = session.connection().createStatement();
			st.executeUpdate(query);
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}

	}

	@Override
	public void insert() throws JOBDBCException {

		try {
			OntoQLStatement st = session.createOntoQLStatement();

			// Get the rid of the supplier
			// Later we must handle supplier supplied in the query
			String queryOntoQL = "select #oid from #ontology where #namespace = '";
			OntoQLResultSet rs = st.executeQuery(queryOntoQL + session.getDefaultNameSpace() + "'");
			if (!rs.next()) {
				throw new JOBDBCException("The namepace " + session.getDefaultNameSpace() + " doesn't exist");
			}
			String ridSupplierBsu = rs.getString(1);

			if (code == null) {
				genereExternalId();
			}

			if (version == null) {
				version = "001";
			}

			// insert the BSU of this class
			// get the rid BSU of the class
			String ridClassBsu = OntoDBHelper.getSequenceNextVal(session.connection(), "root_table_entity_rid_seq");
			setValueInternalId(ridClassBsu);

			// Add this class to the library
			String query = "select rid_s from dictionary_2_responsible_supplier where rid_d = " + ridSupplierBsu;
			Statement stmt = session.connection().createStatement();
			ResultSet rset = stmt.executeQuery(query);
			if (rset.next()) {
				String ridLibrary = rset.getString(1);
				String ridLibraryToClass = OntoDBHelper.insertInIntermediateTable(session.connection(),
						"dictionary_2_contained_classes", ridLibrary, "LIBRARY_E", ridClassBsu, "CLASS_BSU_E");
				query = "update library_e set contained_classes = contained_classes || " + ridLibraryToClass
						+ "::bigint where rid=" + ridLibrary;
				stmt.executeUpdate(query);
			}

			// set the link between the bsu and the supplier of this class
			String ridBsuToSupplierBsu = OntoDBHelper.insertInIntermediateTable(session.connection(),
					"class_bsu_2_defined_by", ridClassBsu, "CLASS_BSU_E", ridSupplierBsu, "SUPPLIER_BSU_E");

			query = "insert into class_bsu_e (rid, code, version, is_version_current, rid_supplier, tablename_supplier, defined_by) values (?,?,?,?,?,?,?) ";
			PreparedStatement pst = session.connection().prepareStatement(query);
			pst.setInt(1, Integer.parseInt(ridClassBsu));
			pst.setString(2, code);
			pst.setString(3, version);
			pst.setBoolean(4, true);
			pst.setInt(5, Integer.parseInt(ridSupplierBsu));
			pst.setString(6, "SUPPLIER_BSU_E");
			pst.setInt(7, Integer.parseInt(ridBsuToSupplierBsu));
			pst.executeUpdate();

			// now handle the names and definitions
			String ridItemNames = OntoDBHelper.createItemNames(session.connection(), getValueName(OntoQLHelper.FRENCH),
					getValueName(OntoQLHelper.ENGLISH), "");

			String ridDefinitions = OntoDBHelper.createDefinitions(session.connection(),
					getValueDefinition(OntoQLHelper.FRENCH), getValueDefinition(OntoQLHelper.ENGLISH), "");

			// Insert the class and link with its bsu and its names
			String ridClass = OntoDBHelper.getSequenceNextVal(session.connection(), "root_table_entity_rid_seq");

			String ridClassToClassBsu = OntoDBHelper.insertInIntermediateTable(session.connection(),
					"class_2_identified_by", ridClass, "ITEM_CLASS_E", ridClassBsu, "CLASS_BSU_E");

			String ridClassToItemNames = OntoDBHelper.insertInIntermediateTable(session.connection(),
					"class_and_property_elements_2_names", ridClass, "ITEM_CLASS_E", ridItemNames, "ITEM_NAMES_E");

			String ridClassToDefinition = OntoDBHelper.insertInIntermediateTable(session.connection(),
					"class_and_property_elements_2_definition", ridClass, "ITEM_CLASS_E", ridDefinitions,
					"TRANSLATED_TEXT_E");

			// Insert the link with its superclass
			String ridClassToSuperClass = null;
			String queryInsert = "insert into item_class_e (rid, identified_by, revision, rid_bsu, tablename_bsu, names, definition";
			String queryValues = ") values (?,?,?,?,?,?,?";
			if (hasSuperClass()) {
				ridClassToSuperClass = OntoDBHelper.insertInIntermediateTable(session.connection(),
						"class_2_its_superclass", ridClass, "ITEM_CLASS_E", superClass.getInternalId(), "CLASS_BSU_E");
				queryInsert += ", its_superclass";
				queryValues += ", ?";
			}

			// Insert the properties defined by this class
			String[] ridClassToScopeProperties = null;
			if (scopeProperties != null) {
				ridClassToScopeProperties = new String[scopeProperties.length];
				for (int i = 0; i < scopeProperties.length; i++) {
					scopeProperties[i].insert();
					ridClassToScopeProperties[i] = OntoDBHelper.insertInIntermediateTable(session.connection(),
							"class_2_described_by", ridClass, "ITEM_CLASS_E", scopeProperties[i].getInternalId(),
							"PROPERTY_BSU_E");
				}
				queryInsert += ", described_by";
				queryValues += ", ?";
			}

			query = queryInsert + queryValues + ")";
			pst = session.connection().prepareStatement(query);
			int i = 1;
			pst.setInt(i++, Integer.parseInt(ridClass));
			pst.setInt(i++, Integer.parseInt(ridClassToClassBsu));
			pst.setString(i++, "001");
			pst.setInt(i++, Integer.parseInt(ridClassBsu));
			pst.setString(i++, "CLASS_BSU_E");
			pst.setInt(i++, Integer.parseInt(ridClassToItemNames));
			pst.setInt(i++, Integer.parseInt(ridClassToDefinition));
			if (hasSuperClass()) {
				pst.setInt(i++, Integer.parseInt(ridClassToSuperClass));
			}
			if (scopeProperties != null) {
				Array arrayRid = JDBCHelper.convertToIntegerArray(ridClassToScopeProperties);
				pst.setArray(i++, arrayRid);
			}

			pst.executeUpdate();

			// The defined properties must be reloaded
			definedProperties = null;

			int sizeAttributesNonHandled = nonCoreAttributes.size();
			if (sizeAttributesNonHandled > 0) {
				String OntoQLUpdate = "update #CLASS SET ";
				for (int j = 0; j < sizeAttributesNonHandled; j++) {
					if (j > 0) {
						OntoQLUpdate += ",";
					}
					OntoQLUpdate += nonCoreAttributes.get(j) + "= '" + nonCoreAttributesValues.get(j) + "'";
				}
				OntoQLUpdate += " WHERE #oid=" + ridClassBsu;
				OntoQLStatement ontoqlStmt = session.createOntoQLStatement();
				ontoqlStmt.executeUpdate(OntoQLUpdate);
			}

		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}

	}

	@Override
	public int createProperty(EntityProperty prop, OntoQLSession session) throws JOBDBCException {
		try {
			this.getInternalId(); // to be sure that the class is loaded
			prop.insert();
			String ridClassToScopeProperty = OntoDBHelper.insertInIntermediateTable(session.connection(),
					"class_2_described_by", ridClass, "ITEM_CLASS_E", prop.getInternalId(), "PROPERTY_BSU_E");
			String update = "update item_class_e set described_by = described_by || " + ridClassToScopeProperty
					+ "::bigint" + " where rid = " + ridClass;
			Statement pst = session.connection().createStatement();
			int res = pst.executeUpdate(update);
			return res == 1 ? 0 : -1;

		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	public boolean hasSuperClass() {
		boolean res;
		res = superClass != null;
		return res;
	}

	public void genereExternalId() {
		String code = OntoDBHelper.genereCode();

		this.code = code;
		this.version = "001";
	}

	@Override
	protected void loadDefinedProperties() {
		// TODO must work
		// SELECT p, p.#id, p.#code, p.#version, p.#name[en], p.#name[fr],
		// p.#its_superclass from #property p where p.name_scope = "+
		// getinternalid
		// if its_superclass == null res += new
		// entityclass(its_superclass).getDefinedProperties;

		StringBuffer query = new StringBuffer();
		query.append("SELECT ");
		query.append("pglsql_class_bsu_known_visible_properties(" + getInternalId() + ")");
		log.debug("Load the defined poperties of a class : " + query);

		try {

			Statement st = session.connection().createStatement();
			ResultSet rs = st.executeQuery(query.toString());

			if (!rs.next()) {
				throw new JOBDBCException(
						"Unable to load the defined properties of the class " + getCurrentLanguage() == null
								? getExternalId()
								: getName());
			}

			int i = 1;
			Array tab;
			String[] definedPropertiesInternalId;

			tab = rs.getArray(i++);
			if (tab != null) {
				definedPropertiesInternalId = (String[]) tab.getArray();
				definedProperties = new AbstractEntityProperty[definedPropertiesInternalId.length];
				for (int j = 0; j < definedPropertiesInternalId.length; j++) {
					definedProperties[j] = (EntityProperty) factory.createDescription("!"
							+ definedPropertiesInternalId[j].substring(0, definedPropertiesInternalId[j].indexOf('#')),
							this.namespace);
					definedProperties[j].setCurrentContext(this);
				}
			} else {
				definedProperties = new AbstractEntityProperty[0];
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	@Override
	protected void loadDirectSubclasses() {
		// TODO SELECT c, c.# WHERE c.#its_superclass.#id = + rid
		// if initialized where #.code-version = ... name[fr]=...
		StringBuffer query = new StringBuffer();
		query.append("SELECT ");
		query.append("pglsql_class_subsomptionclasses(class_e.rid) ");
		query.append("FROM class_e ");
		query.append("WHERE class_e.rid_bsu=" + getInternalId());

		log.debug("Load all the subclasses of a class: " + query);

		try {

			Statement st = session.connection().createStatement();
			ResultSet rs = st.executeQuery(query.toString());
			if (!rs.next()) {
				throw new JOBDBCException(
						"Unable to load the subclasses of the class " + getCurrentLanguage() == null ? getExternalId()
								: getName());
			}
			int i = 1;
			Array tab;
			String[] subclassesInternalIdentifier;

			tab = rs.getArray(i++);
			if (tab != null) {

				subclassesInternalIdentifier = (String[]) tab.getArray();
				directSubclasses = new AbstractEntityClass[subclassesInternalIdentifier.length];

				for (int j = 0; j < subclassesInternalIdentifier.length; j++) {
					directSubclasses[j] = ((AbstractEntityClass) factory
							.createCategory("!" + subclassesInternalIdentifier[j], this.namespace));
				}
			} else {
				directSubclasses = new AbstractEntityClass[0];
			}

			rs.close();
			st.close();
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}

	}

	@Override
	protected void loadUsedProperties() {
		// TODO SELECT ?
		StringBuffer query = new StringBuffer();
		query.append("SELECT properties ");
		query.append("FROM class_e ");
		query.append("left outer join class_extension_e ");
		query.append("on class_e.rid_bsu = class_extension_e.rid_bsu ");
		query.append("WHERE class_e.rid_bsu=" + getInternalId());

		log.debug("Load the used properties of a class: " + query);

		try {

			Statement st = session.connection().createStatement();
			ResultSet rs = st.executeQuery(query.toString());
			if (!rs.next()) {
				throw new JOBDBCException(
						"Unable to load the subclasses of the class " + getCurrentLanguage() == null ? getExternalId()
								: getName());
			}
			int i = 1;
			Array tab;
			String[] usedPropertiesInternalId;

			tab = rs.getArray(i++);
			if (tab != null) {
				usedPropertiesInternalId = (String[]) tab.getArray();
				usedProperties = new AbstractEntityProperty[usedPropertiesInternalId.length];
				for (int j = 0; j < usedPropertiesInternalId.length; j++) {
					usedProperties[j] = (EntityProperty) factory.createDescription("!" + usedPropertiesInternalId[j],
							this.namespace);
					usedProperties[j].setCurrentContext(this);
				}
			} else {
				usedProperties = new AbstractEntityProperty[0];
			}

			rs.close();
			st.close();
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}

	}

	@Override
	protected void loadUsedPropertiesPolymorph() {
		super.loadUsedPropertiesPolymorph();
	}
}
