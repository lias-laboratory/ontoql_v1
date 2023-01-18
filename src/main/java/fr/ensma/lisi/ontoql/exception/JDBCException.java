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
 * Wraps an <tt>SQLException</tt>. Indicates that an exception occurred during a
 * JDBC call.
 *
 * @author Stéphane JEAN
 */
public class JDBCException extends JOBDBCException {

	private static final long serialVersionUID = -8254887805496942757L;

	private SQLException sqle;

	private String sql;

	public JDBCException(String string, SQLException root) {
		super(string, root);
		sqle = root;
	}

	public JDBCException(String string, SQLException root, String sql) {
		this(string, root);
		this.sql = sql;
	}

	/**
	 * Get the SQLState of the underlying <tt>SQLException</tt>.
	 * 
	 * @see java.sql.SQLException
	 * @return String
	 */
	public String getSQLState() {
		return sqle.getSQLState();
	}

	/**
	 * Get the <tt>errorCode</tt> of the underlying <tt>SQLException</tt>.
	 * 
	 * @see java.sql.SQLException
	 * @return int the error code
	 */
	public int getErrorCode() {
		return sqle.getErrorCode();
	}

	/**
	 * Get the underlying <tt>SQLException</tt>.
	 * 
	 * @return SQLException
	 */
	public SQLException getSQLException() {
		return sqle;
	}

	/**
	 * Get the actual SQL statement that caused the exception (may be null)
	 */
	public String getSQL() {
		return sql;
	}
}
