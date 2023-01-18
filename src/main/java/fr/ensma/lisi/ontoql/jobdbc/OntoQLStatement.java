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

import fr.ensma.lisi.ontoql.exception.JOBDBCException;

/**
 * Defines the interface for executing a static OntoQL statement and returning
 * the results it produces.
 * 
 * @author Stéphane JEAN
 */
public interface OntoQLStatement {

	/**
	 * Executes the given OntoQL statement, which returns a single
	 * <code>ResultSet</code> object.
	 * 
	 * @param ontoql an OntoQL statement to be sent to the session
	 * @return a ResultSet object that contains the data produced by the given
	 *         query; never null
	 * @throws JOBDBCException if a database access error occurs or the given OntoQL
	 *                         statement produces anything other than a single
	 *                         ResultSet object
	 */
	OntoQLResultSet executeQuery(String ontoql) throws JOBDBCException;

	/**
	 * Executes the given SPARQL statement, which returns a single
	 * <code>ResultSet</code> object.
	 * 
	 * @param sparqlQuery a SPARQL query
	 * @return a ResultSet object that contains the data produced by the given
	 *         query; never null
	 * @throws JOBDBCException if a database access error occurs or the given SPARQL
	 *                         statement produces anything other than a single
	 *                         ResultSet object
	 */
	OntoQLResultSet executeSPARQLQuery(String sparqlQuery) throws JOBDBCException;

	/**
	 * @return the SQL string generated.
	 */
	String getSQLString();

	/**
	 * Executes the given OntoQL statement, which may be an INSERT, UPDATE, or
	 * DELETE statement or an OntoQL statement that returns nothing, such as an
	 * OntoQL DDL statement.
	 * 
	 * @param ontoql an OntoQL DML or DDL statement
	 * @return either the row count for DML statements, or 0 for DDL statements
	 * @throws JOBDBCException if a database access error occurs
	 */
	int executeUpdate(String ontoql) throws JOBDBCException;

}
