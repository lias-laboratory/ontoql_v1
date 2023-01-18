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

import fr.ensma.lisi.ontoql.exception.JOBDBCException;

/**
 * Interface reprensenting a description, i.e a property of an ontology or an
 * attribute of the ontology model.
 *
 * @author Stephane JEAN
 */
public interface Description {

	/**
	 * Get the internal identifier (oid) of this desscription.
	 * 
	 * @return the internal identifier (oid) of this desscription.
	 * @throws JOBDBCException if a database access error occurs
	 */
	String getInternalId() throws JOBDBCException;

	/**
	 * Get the name in the current natural language of this category.
	 * 
	 * @return the name in the current natural language of this category.
	 */
	String getName();

	/**
	 * Check if this description is a property of an ontology.
	 * 
	 * @return true if this description is a property of an ontology.
	 */
	boolean isProperty();

	/**
	 * Check if this description is an attribute of the ontology model.
	 * 
	 * @return true if this description is an attribute of the ontology model.
	 */
	boolean isAttribute();

	/**
	 * Check if this description is a multilingual attribute or property.
	 * 
	 * @return true if this description is a multilingual attribute or property.
	 */
	boolean isMultilingualDescription();

	/**
	 * Set the language of a multilingual attribute.
	 */
	void setLgCode(String lgCode);

	/**
	 * Test if a description exist with a given identifier. Throw an exception if it
	 * is not the case
	 * 
	 * @param identifier an identifier of description
	 */
	void checkExistence();

	/**
	 * Get a label for the type of this description ("property" or "attribute").
	 * 
	 * @return a label for the type of this description.
	 */
	String getTypeLabel();

	/**
	 * @return
	 */
	String toSQL();

	/**
	 * @param currentContext
	 * @return
	 */
	String toSQL(Category currentContext);

	/**
	 * @return
	 */
	Category getCurrentContext();

	/**
	 * @param currentContext
	 */
	void setCurrentContext(Category currentContext);

	/**
	 * @return
	 */
	EntityDatatype getRange();

	/**
	 * Set the range of this description.
	 * 
	 * @param range
	 */
	void setRange(EntityDatatype range);

	/**
	 * Determine if this description is defined on its current context.
	 * 
	 * @return True is this description is defined on its current context
	 */
	boolean isDefined();

	/**
	 * Determine if this description is defined on the parameter context.
	 * 
	 * @param context category on which this description is tested
	 * @return True is this description is defined on the parameter context
	 */
	boolean isDefined(Category context);
}
