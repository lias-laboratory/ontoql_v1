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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ensma.lisi.ontoql.core.AbstractEntityProperty;
import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCount;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.NotSupportedDatatypeException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.util.OntoDBHelper;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * A property.
 * 
 * @author Stéphane JEAN
 */
public class EntityPropertyOntoDB extends AbstractEntityProperty {

	private static final Log log = LogFactory.getLog(EntityPropertyOntoDB.class);

	private Long id;

	private String version;

	private String code;

	/**
	 * A default name for a new class.
	 */
	private static final String DEFAULT_NAME = "";

	private static final String DEFAULT_DEF = "";

	protected OntoQLSession session;

	public EntityPropertyOntoDB(String id, OntoQLSession session, FactoryEntity factory) {
		this(id, session.getDefaultNameSpace(), session, factory);
	}

	public EntityPropertyOntoDB(String id, String namespace, OntoQLSession session, FactoryEntity factory) {
		super(id, namespace, session.getReferenceLanguage(), factory);
		this.session = session;
	}

	@Override
	public void setCurrentLanguage(String lg) {
		session.setReferenceLanguage(lg);
	}

	@Override
	protected String getValueInternalId() {
		String res = null;
		if (id != null) {
			res = id.toString();
		}
		return res;
	}

	@Override
	protected String getValueExternalId() {
		String res = null;
		if (code != null) {
			res = code;
			if (version != null) {
				res += OntoQLHelper.SEPARATOR_EXTERNAL_ID + version;
			}
		}
		return res;
	}

	@Override
	protected String getValueName() {
		return getValueName(getCurrentLanguage());
	}

	@Override
	protected String getValueName(String lg) {
		String res = null;
		if (lg == null) {
			res = name_en != null ? name_en : name_fr;
		} else {
			if (lg.equals(OntoQLHelper.ENGLISH)) {
				res = name_en;
			} else if (lg.equals(OntoQLHelper.FRENCH)) {
				res = name_fr;
			}
		}
		return res;
	}

	protected String getValueDefinition() {
		return getValueDefinition(getCurrentLanguage());
	}

	protected String getValueDefinition(String lg) {
		String res = null;
		if (lg == null) {
			res = definition_en != null ? definition_en : definition_fr;
		} else {
			if (lg.equals(OntoQLHelper.ENGLISH)) {
				res = definition_en;
			} else if (lg.equals(OntoQLHelper.FRENCH)) {
				res = definition_fr;
			}
		}
		return res;
	}

	@Override
	protected void setValueExternalId(String externalId) {
		String[] codeAndVersion = getCodeAndVersion(externalId);
		this.code = codeAndVersion[0];
		this.version = codeAndVersion[1];
	}

	/**
	 * Return the code and the version composing an external id. Requires that the
	 * syntax of the external id (@) is removed
	 * 
	 * @param externalId an external id
	 * @return an array of 2 strings: the code and the version composing this
	 *         external id
	 */
	protected String[] getCodeAndVersion(String externalId) {
		String[] res = null;
		String code, version;
		int separatorIndex = externalId.indexOf(OntoQLHelper.SEPARATOR_EXTERNAL_ID);
		if (separatorIndex == -1) {
			code = externalId;
			version = null;
		} else {
			code = externalId.substring(0, separatorIndex);
			version = externalId.substring(separatorIndex + 1, externalId.length());
		}
		res = new String[] { code, version };
		return res;
	}

	@Override
	protected void setValueInternalId(String internalId) {
		this.id = Long.valueOf(internalId);

	}

	@Override
	protected String getCurrentLanguage() {
		return session.getReferenceLanguage();
	}

	@Override
	protected boolean isExternalIdInitialized() {
		return code != null;
	}

	@Override
	protected boolean isInternalIdInitialized() {
		return id != null;
	}

	@Override
	protected boolean isNameInitialized() {
		return isNameInitialized(getCurrentLanguage());
	}

	@Override
	protected boolean isNameInitialized(String lg) {
		boolean res = false;
		if (lg == null) {
			res = name_en != null || name_fr != null;
		} else {
			if (lg.equals(OntoQLHelper.ENGLISH)) {
				res = name_en != null;
			} else if (lg.equals(OntoQLHelper.FRENCH)) {
				res = name_fr != null;
			}
		}
		return res;
	}

	@Override
	protected void load() throws JOBDBCException {
		StringBuffer queryBuffer = new StringBuffer();

		queryBuffer.append("SELECT p.#oid, p.#code, p.#version, p.#name[en], p.#name[fr] ");
		queryBuffer.append("FROM #property p WHERE p.#definedBy.#namespace = '" + namespace + "' and ");

		String identifier = null;
		String otherLg = OntoQLHelper.getOtherLanguage(getCurrentLanguage());

		String commonCondition = "";
		if (isInternalIdInitialized()) {
			identifier = getValueInternalId();
			commonCondition = " p.#oid = " + identifier;
			queryBuffer.append(commonCondition);
		} else if (isNameInitialized()) {
			identifier = getValueName();
			String lg = getCurrentLanguage() != null ? getCurrentLanguage() : OntoQLHelper.ENGLISH;
			queryBuffer.append(" p.#name[" + lg + "] = '" + identifier + "'");
		} else if (isExternalIdInitialized()) {
			identifier = getValueExternalId();
			commonCondition = " p.#code = '" + code + "'";
			if (version != null) {
				commonCondition += " and p.#version = '" + version + "'";
			}
			queryBuffer.append(commonCondition);
		} else if (isNameInitialized(otherLg)) {
			queryBuffer.append(" #name[" + otherLg + "] = '" + getValueName(otherLg) + "'");

		} else {
			String msg = "An identifier is needed to load the property.";
			msg += getCurrentLanguage() == null ? " Set the reference language to identify a property by a name" : "";
			throw new JOBDBCException(msg);
		}

		log.debug("Load a property: " + queryBuffer.toString());
		try {

			OntoQLStatement st = session.createOntoQLStatement();
			// TODO change this line by forcing the language in the query
			String oldLg = session.getReferenceLanguage();
			if (oldLg != null) {
				session.setReferenceLanguage(OntoQLHelper.ENGLISH);
			}
			ResultSet rs = st.executeQuery(queryBuffer.toString());
			session.setReferenceLanguage(oldLg);

			int i = 1;
			if (!rs.next()) {
				throw new JOBDBCException(identifier + " is not a property defined on the namespace " + namespace);
			}

			setValueInternalId(rs.getString(i++));
			String resCode = rs.getString(i++);
			String resVersion = rs.getString(i++);
			String resEternalId = resVersion == null ? resCode
					: resCode + OntoQLHelper.SEPARATOR_EXTERNAL_ID + resVersion;
			setValueExternalId(resEternalId);
			setName(rs.getString(i++), OntoQLHelper.ENGLISH);
			setName(rs.getString(i++), OntoQLHelper.FRENCH);

		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}

	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public void loadRange() {
		// The internal id must be loaded
		getInternalId();
		StringBuffer query = new StringBuffer();
		query.append("SELECT tablename_d,rid_d");
		query.append(" FROM property_det_e,property_det_2_domain ");
		query.append(" WHERE property_det_e.domain = property_det_2_domain.rid and ");
		query.append(" property_det_e.rid_bsu = " + id);

		log.debug("Load the range of a property: " + query);

		try {

			@SuppressWarnings("deprecation")
			Statement st = session.connection().createStatement();
			ResultSet rs = st.executeQuery(query.toString());

			int i = 1;
			rs.next();
			String tableName = rs.getString(i++);
			String rid = rs.getString(i++);
			range = factory.createEntityDatatype(tableName, rid);
			rs.close();
			st.close();

		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	@Override
	public String getNameExtent() throws JOBDBCException {

		String res = "p" + getInternalId();

		EntityDatatype dt = getRange();
		boolean isAssociationProperty = dt.isAssociationType();
		boolean isCollectionAssociationProperty = dt.isCollectionAssociationType();

		if (isAssociationProperty) {
			res += "_rid";
		} else if (isCollectionAssociationProperty) {
			res += "_rids";
		} else if (dt.isMultilingualType()) {
			if (lgCode == null) {
				lgCode = session.getReferenceLanguage();
			}
			res += "_" + lgCode;
		}

		return res;
	}

	@Override
	public String getExtent() {

		EntityDatatype dt = getRange();

		if (dt.isMultilingualType()) {
			return "p" + getInternalId() + "_en " + getRange().getExtent() + ", p" + getInternalId() + "_fr "
					+ getRange().getExtent();
		}

		String res = getNameExtent() + " ";

		if (dt instanceof EntityDatatypeCount) {
			res += ((EntityDatatypeCount) dt).getExtent(getInternalId());
		} else {
			res += getRange().getExtent();
		}

		boolean isAssociationProperty = dt.isAssociationType();
		boolean isCollectionAssociationProperty = dt.isCollectionAssociationType();

		if (isAssociationProperty) {
			res += ", " + "p" + getInternalId() + "_tablename varchar";
		} else if (isCollectionAssociationProperty) {
			res += ", " + "p" + getInternalId() + "_tablenames varchar[]";
		}

		return res;
	}

	@Override
	public String toSQL(Category context, boolean polymorph) {
		String res = "NULL";

		try {
			// a cast of the null value is required
			// because sometimes postgres can not
			// do an union between null and a value of type int8
			res += "::" + getRange().getExtent() + " ";
			if (context != null) {

				if (isUsed((EntityClass) context, polymorph)) {
					String tableAlias = context.getTableAlias();
					String alias = tableAlias == null ? ((EntityClass) context).toSQL(false) : tableAlias;
					res = alias + "." + getNameExtent();
				}
			} else {
				res = getNameExtent();
			}
		} catch (NotSupportedDatatypeException oexc) {
			res += " ";
		}

		return res;
	}

	@Override
	public void insert() throws JOBDBCException {
		try {
			@SuppressWarnings("deprecation")
			Connection connection = session.connection();

			if (code == null) {
				this.code = OntoDBHelper.genereCode();
				this.version = "001";
			}

			// insert the BSU of this property
			// get the rid BSU of the property
			String ridPropertyBsu = OntoDBHelper.getSequenceNextVal(connection, "root_table_entity_rid_seq");

			// set the link between the bsu and its scope
			String ridScope = getScope().getInternalId();
			String ridPropertyBsuToScope = OntoDBHelper.insertInIntermediateTable(connection,
					"property_bsu_2_name_scope", ridPropertyBsu, "PROPERTY_BSU_E", ridScope, "CLASS_BSU_E");
			this.setValueInternalId(ridPropertyBsu);
			String query = "insert into property_bsu_e (rid, code, version, is_version_current, name_scope, rid_class, tablename_class) values (?,?,?,?,?,?,?) ";
			@SuppressWarnings("deprecation")
			PreparedStatement pst = session.connection().prepareStatement(query);
			pst.setInt(1, Integer.parseInt(ridPropertyBsu));
			pst.setString(2, code);
			pst.setString(3, version);
			pst.setBoolean(4, true);
			pst.setInt(5, Integer.parseInt(ridPropertyBsuToScope));
			pst.setInt(6, Integer.parseInt(ridScope));
			pst.setString(7, "CLASS_BSU_E");
			pst.executeUpdate();

			// now handle the names
			// The names may not have been set in all languages
			// We put a default name values instead
			String nameFr = getValueName(OntoQLHelper.FRENCH);
			String nameEn = getValueName(OntoQLHelper.ENGLISH);
			String nameFrToInsert = nameFr == null ? DEFAULT_NAME : nameFr;
			String nameEnToInsert = nameEn == null ? DEFAULT_NAME : nameEn;
			String ridItemNames = OntoDBHelper.createItemNames(connection, nameFrToInsert, nameEnToInsert,
					DEFAULT_NAME);

			// now handle the names
			// The names may not have been set in all languages
			// We put a default name values instead
			String defFr = getValueDefinition(OntoQLHelper.FRENCH);
			String defEn = getValueDefinition(OntoQLHelper.ENGLISH);
			String defFrToInsert = nameFr == null ? DEFAULT_DEF : defFr;
			String defEnToInsert = nameEn == null ? DEFAULT_DEF : defEn;
			String ridDef = OntoDBHelper.createDefinitions(connection, defFrToInsert, defEnToInsert, DEFAULT_DEF);

			// Insert the property and link with its bsu and its names
			String ridProperty = OntoDBHelper.getSequenceNextVal(connection, "root_table_entity_rid_seq");
			String ridPropertyToPropertyBsu = OntoDBHelper.insertInIntermediateTable(connection,
					"property_det_2_identified_by", ridProperty, "NON_DEPENDENT_P_DET_E", ridPropertyBsu,
					"PROPERTY_BSU_E");
			String ridPropertyToItemNames = OntoDBHelper.insertInIntermediateTable(connection,
					"class_and_property_elements_2_names", ridProperty, "NON_DEPENDENT_P_DET_E", ridItemNames,
					"ITEM_NAMES_E");
			String ridPropertyToDefinition = OntoDBHelper.insertInIntermediateTable(connection,
					"class_and_property_elements_2_definition", ridProperty, "NON_DEPENDENT_P_DET_E", ridDef,
					"TRANSLATED_TEXT_E");

			// Insert its range
			String ridRange = getRange().insert();
			String ridPropertyToRange = OntoDBHelper.insertInIntermediateTable(connection, "property_det_2_domain",
					ridProperty, "NON_DEPENDENT_P_DET_E", ridRange, getRange().getTableName());

			if (getRange() instanceof EntityDatatypeCount) {
				((EntityDatatypeCount) getRange()).postInsert(ridPropertyBsu);
			}

			// Insert the property
			query = "insert into non_dependent_p_det_e (rid, identified_by, revision, rid_bsu, tablename_bsu, names, definition, domain) values (?,?,?,?,?,?,?,?)";
			pst = connection.prepareStatement(query);
			pst.setInt(1, Integer.parseInt(ridProperty));
			pst.setInt(2, Integer.parseInt(ridPropertyToPropertyBsu));
			pst.setString(3, "001");
			pst.setInt(4, Integer.parseInt(ridPropertyBsu));
			pst.setString(5, "PROPERTY_BSU_E");
			pst.setInt(6, Integer.parseInt(ridPropertyToItemNames));
			pst.setInt(7, Integer.parseInt(ridPropertyToDefinition));
			pst.setInt(8, Integer.parseInt(ridPropertyToRange));
			pst.executeUpdate();

			int sizeAttributesNonHandled = nonCoreAttributes.size();
			if (sizeAttributesNonHandled > 0) {
				String OntoQLUpdate = "update #property SET ";
				for (int i = 0; i < sizeAttributesNonHandled; i++) {
					if (i > 0) {
						OntoQLUpdate += ",";
					}
					OntoQLUpdate += nonCoreAttributes.get(i) + "= '" + nonCoreAttributesValues.get(i) + "'";
				}
				OntoQLUpdate += " WHERE #oid=" + ridPropertyBsu;
				OntoQLStatement stmt = session.createOntoQLStatement();
				stmt.executeUpdate(OntoQLUpdate);
			}

		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}
}
