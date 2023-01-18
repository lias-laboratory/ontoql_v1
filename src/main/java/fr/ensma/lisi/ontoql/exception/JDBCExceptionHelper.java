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

import fr.ensma.lisi.ontoql.util.JDBCExceptionReporter;

/**
 * Implementation of JDBCExceptionHelper.
 * 
 * @author Stéphane JEAN
 */
public final class JDBCExceptionHelper {

	private JDBCExceptionHelper() {
	}

	/**
	 * Converts the given SQLException into Hibernate's JDBCException hierarchy, as
	 * well as performing appropriate logging.
	 * 
	 * @param converter    The converter to use.
	 * @param sqlException The exception to convert.
	 * @param message      An optional error message.
	 * @return The converted JDBCException.
	 */
	public static JDBCException convert(SQLExceptionConverter converter, SQLException sqlException, String message) {
		return convert(converter, sqlException, message, "???");
	}

	/**
	 * Converts the given SQLException into Hibernate's JDBCException hierarchy, as
	 * well as performing appropriate logging.
	 * 
	 * @param converter    The converter to use.
	 * @param sqlException The exception to convert.
	 * @param message      An optional error message.
	 * @return The converted JDBCException.
	 */
	public static JDBCException convert(SQLExceptionConverter converter, SQLException sqlException, String message,
			String sql) {
		JDBCExceptionReporter.logExceptions(sqlException, message + " [" + sql + "]");
		return converter.convert(sqlException, message, sql);
	}

	/**
	 * For the given SQLException, locates the vendor-specific error code.
	 * 
	 * @param sqlException The exception from which to extract the SQLState
	 * @return The error code.
	 */
	public static int extractErrorCode(SQLException sqlException) {
		int errorCode = sqlException.getErrorCode();
		SQLException nested = sqlException.getNextException();
		while (errorCode == 0 && nested != null) {
			errorCode = nested.getErrorCode();
			nested = nested.getNextException();
		}
		return errorCode;
	}

	/**
	 * For the given SQLException, locates the X/Open-compliant SQLState.
	 * 
	 * @param sqlException The exception from which to extract the SQLState
	 * @return The SQLState code, or null.
	 */
	public static String extractSqlState(SQLException sqlException) {
		String sqlState = sqlException.getSQLState();
		SQLException nested = sqlException.getNextException();
		while (sqlState == null && nested != null) {
			sqlState = nested.getSQLState();
			nested = nested.getNextException();
		}
		return sqlState;
	}

	/**
	 * For the given SQLException, locates the X/Open-compliant SQLState's class
	 * code.
	 * 
	 * @param sqlException The exception from which to extract the SQLState class
	 *                     code
	 * @return The SQLState class code, or null.
	 */
	public static String extractSqlStateClassCode(SQLException sqlException) {
		String sqlState = extractSqlState(sqlException);

		if (sqlState == null || sqlState.length() < 2) {
			return sqlState;
		}

		return sqlState.substring(0, 2);
	}
}
