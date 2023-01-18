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

/**
 * 
 * Methods required on an ontology property for the conversion of OntoQL into
 * SQL
 * 
 * @author Stephane JEAN
 */
public interface EntityProperty extends Description {

	/**
	 * Set the range of this property.
	 */
	void setRange(EntityDatatype range);

	/**
	 * @return Returns the internal identifier of this property.
	 */
	String getInternalId() throws JOBDBCException;

	/**
	 * Set the name of this property in the current language.
	 */
	void setName(String name);

	/**
	 * Set the name of this property in the given language.
	 */
	void setName(String name, String lg);

	/**
	 * Set the definition of this property in the current language.
	 */
	void setDefinition(String name);

	/**
	 * Set the definition of this property in the given language.
	 */
	void setDefinition(String name, String lg);

	/**
	 * Set the code of this property.
	 */
	void setCode(String code);

	/**
	 * Set the version of this property.
	 */
	void setVersion(String version);

	/**
	 * @return Returns the external identifier of this property
	 */
	String getExternalId();

	/**
	 * @return Returns the name of this property in the current language
	 */
	String getName();

	/**
	 * Get the name of this property in the given natural language.
	 * 
	 * @return the name of this class in this natural language
	 */
	String getName(String lg);

	/**
	 * @return Returns the namespace of this property
	 */
	String getNamespace();

	/**
	 * @return Returns the datatype of this property
	 */
	EntityDatatype getRange();

	/**
	 * @return True If the property is of an enumerate type
	 */
	boolean isEnumerateType();

	/**
	 * @return Returns the alias used in an OntoQL Query to reference this property
	 */
	String getAlias();

	/**
	 * Return the name of this property in an extent of a class.
	 * 
	 * @throws JOBDBCException if a database error occurs
	 */
	String getNameExtent() throws JOBDBCException;

	/**
	 * @return A String representation of this property in the extent of a class.
	 */
	String getExtent();

	/**
	 * Set the alias used in an OntoQL Query to reference this property
	 */
	void setAlias(String alias);

	/**
	 * @return Returns the context (an ontology class) in which the property is
	 *         considered
	 */
	Category getCurrentContext();

	/**
	 * Set the context (an ontology class) in which the property is considered
	 */
	void setCurrentContext(Category currentContext);

	/**
	 * @return Returns the scope (an ontology class) of this property.
	 */
	EntityClass getScope();

	/**
	 * Set the scope of this property.
	 */
	void setScope(EntityClass scope);

	/**
	 * @return True if the property is used in the extend of the current context
	 *         Require The current context is set (offensive programmation)
	 */
	boolean isUsed() throws JOBDBCException;

	/**
	 * @return True if the property is used in the extend of the current context
	 *         Require The current context is set (offensive programmation)
	 */
	boolean isUsed(boolean polymorph) throws JOBDBCException;

	/**
	 * @return True if the property is used in the extend of the current context or,
	 *         if polymorph is true one one of its subclasses Require The current
	 *         context is set (offensive programmation)
	 */
	boolean isUsed(EntityClass currentContext, boolean polymorph) throws JOBDBCException;

	/**
	 * @return True if a property is defined in the current context Require The
	 *         current context is set (offensive programmation)
	 */
	boolean isDefined();

	/**
	 * @return The name of the attribut in the relation of the extent of the given
	 *         context. Return NULL if this property is not used in this context
	 */
	String toSQL(Category context, boolean polymorph);

	/**
	 * @return The name of the attribut in the relation of the extent of the given
	 *         context. Return NULL if this property is not used in this context
	 */
	String toSQL(Category context);

	/**
	 * @return The name of the attribut in the relation of the extent of its current
	 *         context. Return NULL if this property is not used
	 */
	String toSQL();

	/**
	 * Generate the OntoQL representation according to some namespaces
	 */
	String toString(Map<String, String> namespaces, AliasGenerator aliasGenerator);

	/**
	 * @return Returns the pathProperty.
	 */
	EntityProperty getPathProperty();

	/**
	 * @param pathProperty The pathProperty to set.
	 */
	void setPathProperty(EntityProperty pathProperty);

	/**
	 * Create a new property in the obdb.
	 * 
	 * @throws JOBDBCException if a database error occurs
	 */
	void insert() throws JOBDBCException;

	/**
	 * Set the current language
	 */
	void setCurrentLanguage(String lg);

	/**
	 * @param list of the attributes that are not part of the core ontology model
	 *             (e.g, #owlnamespace)
	 */
	void setNonCoreAttributes(Vector attributes);

	void setNonCoreAttributesValues(Vector attributes);
}
