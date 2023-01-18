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
package fr.ensma.lisi.ontoql.jobdbc;

import java.sql.ResultSet;
import java.util.Set;

import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.ontoapi.Instance;
import fr.ensma.lisi.ontoql.ontoapi.OntoBooleanType;
import fr.ensma.lisi.ontoql.ontoapi.OntoClass;
import fr.ensma.lisi.ontoql.ontoapi.OntoCollectionType;
import fr.ensma.lisi.ontoql.ontoapi.OntoConcept;
import fr.ensma.lisi.ontoql.ontoapi.OntoContextProperty;
import fr.ensma.lisi.ontoql.ontoapi.OntoDatatype;
import fr.ensma.lisi.ontoql.ontoapi.OntoDependentProperty;
import fr.ensma.lisi.ontoql.ontoapi.OntoDocument;
import fr.ensma.lisi.ontoql.ontoapi.OntoGraphics;
import fr.ensma.lisi.ontoql.ontoapi.OntoIntType;
import fr.ensma.lisi.ontoql.ontoapi.OntoNonDependentProperty;
import fr.ensma.lisi.ontoql.ontoapi.OntoNumberType;
import fr.ensma.lisi.ontoql.ontoapi.OntoOntology;
import fr.ensma.lisi.ontoql.ontoapi.OntoPrimitiveType;
import fr.ensma.lisi.ontoql.ontoapi.OntoProperty;
import fr.ensma.lisi.ontoql.ontoapi.OntoRealMeasureType;
import fr.ensma.lisi.ontoql.ontoapi.OntoRealType;
import fr.ensma.lisi.ontoql.ontoapi.OntoRefType;
import fr.ensma.lisi.ontoql.ontoapi.OntoStringType;

/**
 * A table of data representing a database result set, which is usually
 * generated by executing a statement that queries the database.
 * <P>
 * The interface <code>OntoQLResultSet</code> is upward compatible with the
 * <code>ResultSet</code> interface. Thus, it provides <i>getter</i> methods
 * (<code>getBoolean</code>,<code>getLong</code>, and so on) for retrieving
 * column values from the current row. Values can be retrieved using either the
 * index number of the column or the name of the column. In general, using the
 * column index will be more efficient. Columns are numbered from 1.
 * <P>
 * <P>
 * The interface <code>OntoQLResultSet</code> extends <code>ResultSet</code>
 * providing getter methods for class instances <code>getInstance</code>
 * <P>
 * <P>
 * A <code>OntoQLResultSet</code> object is automatically closed when the
 * <code>OntoQLStatement</code> object that generated it is closed, re-executed,
 * or used to retrieve the next result from a sequence of multiple results.
 * <P>
 * The number, types and properties of an <code>OntoQLResultSet</code> object's
 * columns are provided by the <code>OntoQLResulSetMetaData</code> object
 * returned by the <code>OntoQLResultSet.getOntoQLMetaData</code> method.
 * 
 * @author Stéphane JEAN
 */
public interface OntoQLResultSet extends ResultSet {

	/**
	 * Return the SPARQL QueryResults XML Format corresponding to this resultset
	 * 
	 * @exception JOBDBCException if a database access error occurs
	 */
	String toSPARQLQueryResultsXMLFormat() throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoProperty</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoProperty getOntoProperty(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoNonDependentProperty</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoNonDependentProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoNonDependentProperty getOntoNonDependentProperty(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoPrimitiveType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoPrimitiveType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoPrimitiveType getOntoPrimitiveType(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoCollectionType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoCollectionType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoCollectionType getOntoCollectionType(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoOntology</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoOntology</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoOntology getOntoOntology(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoGraphics</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoGraphics</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoGraphics getOntoGraphics(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoRefType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoRefType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoRefType getOntoRefType(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoContextProperty</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoContextProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoContextProperty getOntoContextProperty(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoRealMeasureType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoRealMeasureType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoRealMeasureType getOntoRealMeasureType(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoIntType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoIntType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoIntType getOntoIntType(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoDatatype</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoDatatype</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoDatatype getOntoDatatype(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoBooleanType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoBooleanType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoBooleanType getOntoBooleanType(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoDependentProperty</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoDependentProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoDependentProperty getOntoDependentProperty(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoRealType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoRealType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoRealType getOntoRealType(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoDocument</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoDocument</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoDocument getOntoDocument(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoStringType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoStringType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoStringType getOntoStringType(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoNumberType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoNumberType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoNumberType getOntoNumberType(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoClass</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoClass</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoClass getOntoClass(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoConcept</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoConcept</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoConcept getOntoConcept(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as a Set of Object
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return a set of Object
	 * @exception JOBDBCException if a database access error occurs
	 */
	Set getSet(int columnIndex) throws JOBDBCException;

	/**
	 * Get the OntoQL metada of this result set.
	 * 
	 * @return the OntoQL metada
	 * @throws JOBDBCException if a database access problem occurs
	 */
	OntoQLResultSetMetaData getOntoQLMetaData() throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an <code>Instance</code> object in
	 * OntoAPI.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an <code>Instance</code> holding the instance properties values
	 * @exception JOBDBCException if a database access error occurs
	 */
	Instance getInstance(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>EntityClass</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>EntityClass</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	EntityClass getEntityClass(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>EntityProperty</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>EntityProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	EntityProperty getEntityProperty(int columnIndex) throws JOBDBCException;

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as a array of Object
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an array of Object
	 * @exception JOBDBCException if a database access error occurs
	 */
	Object[] getCollection(int columnIndex) throws JOBDBCException;

}
