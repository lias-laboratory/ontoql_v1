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

/**
 * Any exception that occurs inside the persistence layer or JDBC driver.
 * <tt>SQLException</tt>s are always wrapped by instances of
 * <tt>JDBCException</tt>.
 *
 * @author Stéphane JEAN
 */
public class JOBDBCException extends NestableRuntimeException {

	private static final long serialVersionUID = 8341715108492381968L;

	public JOBDBCException(Throwable root) {
		super(root);
	}

	public JOBDBCException(String string, Throwable root) {
		super(string, root);
	}

	public JOBDBCException(String s) {
		super(s);
	}
}
