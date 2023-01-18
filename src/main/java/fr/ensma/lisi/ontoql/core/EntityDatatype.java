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
 * Methods required on an ontology datatype for the execution of an OntoQL query
 * 
 * @author Stephane JEAN
 */
public interface EntityDatatype {

	String OP_LIKE = "LIKE";

	String OP_NOT_LIKE = "NOT LIKE";

	String OP_INF = "<";

	String OP_SUP = ">";

	String OP_INFEG = "<=";

	String OP_SUPEG = ">=";

	String OP_EG = "=";

	String OP_IN = "IN";

	String OP_PLUS = "+";

	String OP_MINUS = "-";

	String OP_DIV = "/";

	String OP_STAR = "*";

	String OP_CONCAT = "||";

	String STRING_NAME = "STRING";

	String INT_NAME = "INT";

	String REAL_NAME = "REAL";

	String BOOLEAN_NAME = "BOOLEAN";

	String ENNUMERATE_NAME = "ENUM";

	String COLLECTION_NAME = "ARRAY";

	String ASSOCIATION_NAME = "REF";

	String URI_TYPE_NAME = "URITYPE";

	String COUNT_TYPE_NAME = "COUNTTYPE";

	/**
	 * @return True if this datatype is an association type.
	 */
	boolean isAssociationType();

	/**
	 * @return True if this datatype is a collection of association type.
	 */
	boolean isCollectionAssociationType();

	/**
	 * @return True if this datatype is a multilingual type.
	 */
	boolean isMultilingualType();

	/**
	 * Get the internal identifier of this datatype.
	 * 
	 * @return the internal identifier of this datatype.
	 */
	String getInternalId();

	/**
	 * Set the internal identifier of this datatype.
	 * 
	 * @param id the internal identifier of this datatype.
	 */
	void setInternalId(String id);

	/**
	 * The name for this datatype.
	 * 
	 * @return a printable label for this datatype
	 */
	String getName();

	/**
	 * The name of the table where this datatype persist.
	 * 
	 * @return a printable label for this datatype
	 */
	String getTableName();

	/**
	 * Insert a new datatype.
	 * 
	 * @return the identifier of this datatype
	 * @throws JOBDBCException if a database access error occurs
	 */
	String insert() throws JOBDBCException;

	/**
	 * The boolean operators usable on this datatype.
	 * 
	 * @return the usable boolean operators
	 */
	String[] getBooleanOperators();

	/**
	 * The arithmetic operators usable on this datatype.
	 * 
	 * @return the usable arithmetics operators
	 */
	String[] getArithmeticOperators();

	/**
	 * Return how a value of this type is represented in the OntoQL syntax.
	 * 
	 * @param value a constant to format
	 * @return the constant formatted according to the OntoQL syntax
	 */
	String valueToOntoql(String value);

	/**
	 * @return A String representation of this datatype as range of a property in an
	 *         extent of a class.
	 */
	String getExtent();

	/**
	 * Return the value of this type from its representation in the OntoQL syntax.
	 * 
	 * @param value a constant in the OntoQL syntax.
	 * @return the constant represented by this value
	 */
	String ontoQLToValue(String value);

	/**
	 * Get the corresponding java class of this type.
	 *
	 * @return Class The corresponding java class of this type.
	 */
	Class getReturnedClass();
}
