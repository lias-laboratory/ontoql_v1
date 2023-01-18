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
 * Implementation of JDBCException indicating problems with communicating with
 * the database (can also include incorrect JDBC setup).
 *
 * @author Stéphane JEAN
 */
public class JDBCConnectionException extends JDBCException {

	private static final long serialVersionUID = 871694565080354888L;

	public JDBCConnectionException(String string, SQLException root) {
		super(string, root);
	}

	public JDBCConnectionException(String string, SQLException root, String sql) {
		super(string, root, sql);
	}
}
