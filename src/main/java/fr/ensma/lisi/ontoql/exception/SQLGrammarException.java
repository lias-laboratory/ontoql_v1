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
package fr.ensma.lisi.ontoql.exception;

import java.sql.SQLException;

/**
 * Implementation of JDBCException indicating that the SQL sent to the database
 * server was invalid (syntax error, invalid object references, etc).
 *
 * @author Stéphane JEAN
 */
public class SQLGrammarException extends JDBCException {

	private static final long serialVersionUID = -1763451156635343619L;

	/**
	 * Constructor for JDBCException.
	 * 
	 * @param root The underlying exception.
	 */
	public SQLGrammarException(String message, SQLException root) {
		super(message, root);
	}

	/**
	 * Constructor for JDBCException.
	 * 
	 * @param message Optional message.
	 * @param root    The underlying exception.
	 */
	public SQLGrammarException(String message, SQLException root, String sql) {
		super(message, root, sql);
	}
}
