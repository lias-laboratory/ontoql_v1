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
 * Implementation of JDBCException indicating that the requested DML operation
 * resulted in a violation of a defined integrity constraint.
 * 
 * @author Stéphane JEAN
 */
public class ConstraintViolationException extends JDBCException {

	private static final long serialVersionUID = -3089656956505468095L;

	private String constraintName;

	public ConstraintViolationException(String message, SQLException root, String constraintName) {
		super(message, root);
		this.constraintName = constraintName;
	}

	public ConstraintViolationException(String message, SQLException root, String sql, String constraintName) {
		super(message, root, sql);
		this.constraintName = constraintName;
	}

	/**
	 * Returns the name of the violated constraint, if known.
	 * 
	 * @return The name of the violated constraint, or null if not known.
	 */
	public String getConstraintName() {
		return constraintName;
	}
}
