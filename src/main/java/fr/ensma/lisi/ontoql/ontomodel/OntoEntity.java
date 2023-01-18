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
package fr.ensma.lisi.ontoql.ontomodel;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ensma.lisi.ontoql.cfg.OntoQLConfiguration;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSessionFactory;
import fr.ensma.lisi.ontoql.ontomodel.mapping.PlibEntity;
import fr.ensma.lisi.ontoql.util.DatabaseHelper;
import fr.ensma.lisi.ontoql.util.FileHelper;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Mapping for an entity of the core (or an extended) model
 * 
 * @author Stéphane Jean
 */
public class OntoEntity {

	/**
	 * A logger for this class.
	 */
	private static Log log = LogFactory.getLog(OntoEntity.class);

	/**
	 * Name of this entity.
	 */
	private String name;

	/**
	 * Name in French of this entity.
	 */
	private String name_fr;

	/**
	 * Class of OntoLib API corresponding to this entity.
	 */
	private Class ontoAPIClass;

	/**
	 * Class of the internal API corresponding to this entity.
	 */
	private Class internalAPIClass;

	/**
	 * Super entity of this entity. e.g, concept for the entity class.
	 */
	private OntoEntity superMapEntity;

	/**
	 * Sub entities of this entities. derived from superEntity using the function
	 * setSuperEntity
	 */
	private List<OntoEntity> directSubMapEntities = new ArrayList<OntoEntity>();

	/**
	 * Attributes defined or redefined by this entity.
	 */
	private ArrayList definedAttributes = new ArrayList();

	/**
	 * Mapping of this entity with PLIB.
	 */
	private PlibEntity mapTo;

	public boolean isRedefinedAttribute(OntoAttribute attribute) {
		boolean res = false;

		if (superMapEntity != null) {
			res = superMapEntity.getApplicableAttributes().contains(attribute) && definedAttributes.contains(attribute);
		}

		return res;
	}

	public boolean existInSchema(OntoQLSession session) {
		boolean res = false;

		try {
			res = DatabaseHelper.existTable(getMapTo().toSQL(), session.connection());
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}

		return res;
	}

	/**
	 * @param name
	 */
	public OntoEntity(String name) {
		this.mapTo = new PlibEntity(name);
		this.name = name;
		this.name_fr = name;
	}

	/**
	 * Set the super entity of this entity.
	 * 
	 * @param superEntity the super entity of this entity
	 */
	public void setSuperMapEntity(OntoEntity superEntity) {
		this.superMapEntity = superEntity;
		superEntity.addDirectSubMapEntity(this);
	}

	/**
	 * add a sub entity to this entity. This methods is private to avoid
	 * modification by a client application
	 * 
	 * @param e a sub entity of this entity.
	 */
	private void addDirectSubMapEntity(OntoEntity e) {
		directSubMapEntities.add(e);
	}

	/**
	 * @return Returns the attributes defined by this entity.
	 */
	public ArrayList getApplicableAttributes() {
		ArrayList res = (ArrayList) definedAttributes.clone();
		if (superMapEntity != null) {
			ArrayList applicableAttributesOfSuperEntity = superMapEntity.getApplicableAttributes();
			Object currentAttribut = null;
			int index = 0; // index of insertion
			for (int i = 0; i < applicableAttributesOfSuperEntity.size(); i++) {
				currentAttribut = applicableAttributesOfSuperEntity.get(i);
				if (!definedAttributes.contains(currentAttribut)) {
					res.add(index, currentAttribut);
					index++;
				}
			}
		}
		return res;
	}

	/**
	 * Create the table and columns required in the OBDB to store instances of this
	 * entity
	 * 
	 * @param session access to the database
	 */
	public int create(OntoQLSession session) {
		// the correct result is 0
		int res = 1;

		// get the name of the super table
		String nameSuperTable;
		if (getSuperMapEntity() != null) {
			nameSuperTable = getSuperMapEntity().toSQL();
		} else {
			nameSuperTable = "root_table_entity"; // default
		}

		if (this.superMapEntity != null) {
			nameSuperTable = superMapEntity.toSQL();
		}
		// get the sql name of this entity
		String nameSQL = toSQL();
		// create the table of this entity
		String cmdDDL = "CREATE TABLE " + nameSQL + "("
				+ "rid int8 NOT NULL DEFAULT nextval('public.root_table_entity_rid_seq'::text)";
		List attributes = getApplicableAttributes();
		// we must record the attributes to create
		// They must be created after this table because they reference this
		// table
		List attributesToCreate = new ArrayList();
		OntoAttribute currentMapAttribute = null;
		String currentSqlDefinition = null;
		for (int i = 0; i < attributes.size(); i++) {
			currentMapAttribute = (OntoAttribute) attributes.get(i);
			if (!currentMapAttribute.getName().equalsIgnoreCase("oid")) {
				currentSqlDefinition = currentMapAttribute.getSQLDefinition();
				if (currentSqlDefinition != null) {
					if (getDefinedAttributes().contains(currentMapAttribute)) {
						// a primitive attribute must be represented
						// only if it is not inherited
						cmdDDL += ", " + currentSqlDefinition;
					}
				} else {
					attributesToCreate.add(currentMapAttribute);
				}
			}
		}
		cmdDDL += ", CONSTRAINT " + nameSQL + "_pkey PRIMARY KEY (rid)" + ") INHERITS (" + nameSuperTable
				+ ") WITH OIDS";
		try {
			// create the structure in the database
			Connection cnx = session.connection();
			Statement stmt = cnx.createStatement();
			log.info(cmdDDL);
			res = stmt.executeUpdate(cmdDDL);
			// create the structure for the other attributes
			for (int i = 0; i < attributesToCreate.size(); i++) {
				currentMapAttribute = (OntoAttribute) attributesToCreate.get(i);
				currentMapAttribute.create(this, session);
			}
			// add this entity in the meta-schema (xml file)
			URL urlFile = Thread.currentThread().getContextClassLoader()
					.getResource(OntoQLConfiguration.FILE_ONTOLOGY_MODEL);
			File file = new File(urlFile.getPath());
			FileHelper.appendElementToFile(file, toXML());
			// this entity must be added to the loaded entities
			((OntoQLSessionFactory) session.getSessionFactory()).addEntityOntologyModel(this);

		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		} catch (Exception oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}

		return res;

	}

	/**
	 * Drop the table and columns required in the OBDB to store instances of this
	 * entity
	 * 
	 * @param session access to the database
	 */
	public int drop(OntoQLSession session) {
		// the correct result is 0
		int res = 1;

		// Check that this entity is not referenced by inheritance relationships
		// TODO do the same with association relationships
		if (getDirectSubMapEntities().size() != 0) {
			throw new JOBDBCException("This entity can not be dropped because it has subentities");
		}

		// First delete the table of this entity
		String nameSQL = toSQL();
		String cmdDDL = "DROP TABLE " + nameSQL;
		try {
			// create the structure in the database
			Connection cnx = session.connection();
			Statement stmt = cnx.createStatement();
			log.info(cmdDDL);
			res = stmt.executeUpdate(cmdDDL);

			// then delete association table
			List attributes = getApplicableAttributes();
			OntoAttribute currentMapAttribute = null;
			for (int i = 0; i < attributes.size(); i++) {
				currentMapAttribute = (OntoAttribute) attributes.get(i);
				currentMapAttribute.drop(this, session);
			}

			// finally, drop this entity from the meta-schema (xml file)
			URL urlFile = Thread.currentThread().getContextClassLoader()
					.getResource(OntoQLConfiguration.FILE_ONTOLOGY_MODEL);
			File file = new File(urlFile.getPath());
			FileHelper.removeXMLElementFromFile(file, getFirstLineXML());
			// this entity must be removed from the loaded entities
			((OntoQLSessionFactory) session.getSessionFactory()).removeEntityOntologyModel(this);
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		} catch (Exception oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}

		return res;
	}

	public int createAttribute(OntoAttribute attribute, OntoQLSession session) {
		// the correct result is 0
		int res = 1;

		try {
			String sqlDefinition = attribute.getSQLDefinition();

			if (sqlDefinition != null) {
				// get the sql name of this entity
				String nameSQL = toSQL();
				String cmdDDL = "ALTER TABLE " + nameSQL + " ADD COLUMN " + sqlDefinition;
				Connection cnx = session.connection();
				Statement stmt = cnx.createStatement();
				log.info(cmdDDL);
				res = stmt.executeUpdate(cmdDDL);
			} else {
				attribute.create(session);
				res = 0;
			}

			// add this attribute in the meta-schema (xml file)
			URL urlFile = Thread.currentThread().getContextClassLoader()
					.getResource(OntoQLConfiguration.FILE_ONTOLOGY_MODEL);
			File file = new File(urlFile.getPath());
			String beginingOfLineToFind = "<entity name=\"" + name + "\"";
			FileHelper.appendXMLAttributeToFile(file, attribute.toXML(), beginingOfLineToFind);

			((OntoQLSessionFactory) session.getSessionFactory()).addEntityOntologyModel(this);

		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		} catch (Exception oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}

		return res;
	}

	public int dropAttribute(OntoAttribute attribute, OntoQLSession session) {
		// the correct result is 0
		int res = 1;

		try {

			// get the sql name of this entity
			String nameSQL = toSQL();
			String cmdDDL = "ALTER TABLE " + nameSQL + " DROP COLUMN " + attribute.getName();
			Connection cnx = session.connection();
			Statement stmt = cnx.createStatement();
			log.info(cmdDDL);
			res = stmt.executeUpdate(cmdDDL);

			// drop other table for this attribute (if necessary)
			attribute.drop(this, session);

			// remove this attribute from the meta-schema (xml file)
			URL urlFile = Thread.currentThread().getContextClassLoader()
					.getResource(OntoQLConfiguration.FILE_ONTOLOGY_MODEL);
			File file = new File(urlFile.getPath());
			String beginingOfLineToFind = "<entity name=\"" + name + "\"";
			FileHelper.removeXMLAttributeFromFile(file, attribute.toXML(), beginingOfLineToFind);

			((OntoQLSessionFactory) session.getSessionFactory()).removeAttributeFromEntityOntologyModel(this,
					attribute);

		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		} catch (Exception oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}

		return res;
	}

	/**
	 * @return Returns the attributes applicable on this entity.
	 */
	public ArrayList getDefinedAttributes() {
		return definedAttributes;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	public String getName(String lg) {
		String res = null;
		if (lg != null) {
			if (lg.equals(OntoQLHelper.ENGLISH)) {
				res = getName();
			} else if (lg.equals(OntoQLHelper.FRENCH)) {
				res = name_fr;
			}
		}
		return res;

	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void setName(String name, String lg) {
		if (lg.equals(OntoQLHelper.ENGLISH)) {
			this.name = name;
		} else if (lg.equals(OntoQLHelper.FRENCH)) {
			name_fr = name;
		}
	}

	/**
	 * Add an attribute defined by this entity.
	 * 
	 * @param an attribute defined by this entity
	 */
	public void addDefinedAttribute(OntoAttribute a) {
		definedAttributes.add(a);
	}

	/**
	 * Remove an attribute defined by this entity.
	 * 
	 * @param an attribute defined by this entity
	 */
	public void removeDefinedAttribute(OntoAttribute a) {
		definedAttributes.remove(a);
	}

	/**
	 * Return the name of table corresponding to this entity.
	 */
	public String toSQL() throws JOBDBCException {
		return mapTo.toSQL();

	}

	public String getFirstLineXML() throws JOBDBCException {
		String res = "<entity name=\"" + name + "\"";
		if (superMapEntity != null) {
			res += " superEntity=\"" + superMapEntity.getName() + "\"";
		}
		res += ">";
		return res;
	}

	/**
	 * Return the name of table corresponding to this entity.
	 */
	public String toXML() throws JOBDBCException {
		// the super entity

		String res = "\t" + getFirstLineXML();
		res += "\n\n\t\t<attributePrimitive name=\"oid\" type=\"Int\" attributePLIB=\"rid\"/>\n\n";
		List attributes = getDefinedAttributes();
		OntoAttribute currentMapAttribute = null;
		for (int i = 0; i < attributes.size(); i++) {
			currentMapAttribute = (OntoAttribute) attributes.get(i);
			if (!currentMapAttribute.getName().equalsIgnoreCase("oid")) {
				currentMapAttribute = (OntoAttribute) attributes.get(i);
				res += currentMapAttribute.toXML();
			}
		}
		res += "\t</entity>\n";

		return res;
	}

	public PlibEntity getMapTo() {
		return mapTo;
	}

	public void setMapTo(PlibEntity mapTo) {
		this.mapTo = mapTo;
	}

	public Class getOntoAPIClass() {
		return ontoAPIClass;
	}

	public void setOntoAPIClass(Class mappedClass) {
		this.ontoAPIClass = mappedClass;
	}

	public List<OntoEntity> getDirectSubMapEntities() {
		return directSubMapEntities;
	}

	public OntoEntity getSuperMapEntity() {
		return superMapEntity;
	}

	public Class getInternalAPIClass() {
		return internalAPIClass;
	}

	public void setInternalAPIClass(Class internalAPIClass) {
		this.internalAPIClass = internalAPIClass;
	}
}