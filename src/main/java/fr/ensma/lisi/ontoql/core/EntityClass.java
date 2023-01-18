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
package fr.ensma.lisi.ontoql.core;

import java.util.Map;
import java.util.Vector;

import fr.ensma.lisi.ontoql.engine.util.AliasGenerator;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;

/**
 * Describe an EntityClass category.
 *
 * @author Stephane JEAN
 */
public interface EntityClass extends Category {

	/**
	 * Set the value of the name of this class in the current language
	 */
	void setName(String name);

	/**
	 * Set the value of the name of this class in the given language
	 */
	void setName(String name, String lg);

	/**
	 * Set the code of this class.
	 */
	void setCode(String code);

	/**
	 * Set the version of this class.
	 */
	void setVersion(String version);

	/**
	 * Set the value of the definition of this class in the current language
	 */
	void setDefinition(String name);

	/**
	 * Set the value of the definition of this class in the given language
	 */
	void setDefinition(String name, String lg);

	/**
	 * Set the superclass of this class.
	 */
	void setSuperClass(EntityClass superClass);

	/**
	 * @return Returns the external identifier of this class
	 */
	String getExternalId();

	/**
	 * Get the name of this class in the current natural language.
	 * 
	 * @return the name of this class in this natural language
	 */
	String getName(String lg);

	/**
	 * Create a new class in the obdb.
	 * 
	 * @throws JOBDBCException if a database error occurs
	 */
	void insert() throws JOBDBCException;

	/**
	 * Add a new property to this class
	 * 
	 * @param prop    the property created
	 * @param session connexion to the database
	 * @return 0 if the property has been created -1 otherwise
	 */
	int createProperty(EntityProperty prop, OntoQLSession session);

	/**
	 * Drop a property of this class
	 */
	void dropProperty(EntityProperty prop, OntoQLSession session);

	/**
	 * Return the name of the extent of this class even if this class has no extent.
	 * 
	 * @throws JOBDBCException if a database error occurs
	 */
	String getNameExtent() throws JOBDBCException;

	/**
	 * Create an extent to this class as a view in the obdb.
	 * 
	 * @param propertiesExtent the properties created in the extent
	 * @param query            the query which create the extent
	 * @throws JOBDBCException if a database error occurs
	 */
	void createView(EntityProperty[] propertiesExtent, String query) throws JOBDBCException;

	/**
	 * Create an extent to this class in the obdb.
	 * 
	 * @param propertiesExtent the properties created in the extent
	 * @throws JOBDBCException if a database error occurs
	 */
	void createTable(EntityProperty[] propertiesExtent) throws JOBDBCException;

	/**
	 * Get all (inherited included) the properties defined on this class.
	 * 
	 * @return Returns all (inherited included) the properties defined on this
	 *         class.
	 */
	EntityProperty[] getDefinedProperties();

	/**
	 * Get all the properties defined by this class.
	 * 
	 * @return all the properties defined by this class.
	 */
	EntityProperty[] getScopeProperties();

	void setScopeProperties(EntityProperty[] scopeProperties);

	/**
	 * @return Returns the direct subclasses of this class
	 */
	EntityClass[] getDirectSubclasses();

	/**
	 * @return The properties used in the extent of this class
	 */
	EntityProperty[] getUsedProperties();

	/**
	 * @return True if the class has an extent
	 */
	boolean isAbstract();

	/**
	 * @return The properties used in the extent of this class or used in the extent
	 *         of one of its subclasses
	 */
	EntityProperty[] getUsedPropertiesPolymorph();

	/**
	 * @return The sql query to retrieve the instances of this class and only the
	 *         name of the extent if the query is not polymorphic
	 */
	String toSQL();

	/**
	 * @return The ontoql representation of this class
	 */
	String toString(Map<String, String> namespace, AliasGenerator aliasGenerator);

	/**
	 * @return The sql query to retrieve the instances of this (polymorphic?) class
	 */
	String toSQL(boolean polymorph) throws JOBDBCException;

	/**
	 * @return give the sql query corresponding to the projection of the given
	 *         properties on the class Require : the properties have as current
	 *         context this class
	 */
	String project(EntityProperty[] properties, boolean polymorph) throws JOBDBCException;

	/**
	 * @param list of the attributes that are not part of the core ontology model
	 *             (e.g, #owlnamespace)
	 */
	void setNonCoreAttributes(Vector attributes);

	void setNonCoreAttributesValues(Vector attributes);
}