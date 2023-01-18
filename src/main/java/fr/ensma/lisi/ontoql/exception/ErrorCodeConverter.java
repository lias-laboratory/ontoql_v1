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
 * A SQLExceptionConverter implementation which performs converion based on the
 * vendor specific ErrorCode. This is just intended really as just a base class
 * for converters which know the interpretation of vendor-specific codes.
 *
 * @author Stéphane JEAN
 */
public class ErrorCodeConverter implements SQLExceptionConverter {

	private ViolatedConstraintNameExtracter extracter;

	public ErrorCodeConverter(ViolatedConstraintNameExtracter extracter) {
		this.extracter = extracter;
	}

	/**
	 * The error codes representing SQL grammar issues.
	 * 
	 * @return The SQL grammar error codes.
	 */
	protected int[] getSQLGrammarErrorCodes() {
		return null;
	}

	/**
	 * The error codes representing issues with a connection.
	 * 
	 * @return The connection error codes.
	 */
	protected int[] getConnectionErrorCodes() {
		return null;
	}

	/**
	 * The error codes representing various types of database integrity issues.
	 * 
	 * @return The integrity violation error codes.
	 */
	protected int[] getIntegrityViolationErrorCodes() {
		return null;
	}

	protected int[] getLockAcquisitionErrorCodes() {
		return null;
	}

	@Override
	public JDBCException convert(SQLException sqlException, String message, String sql) {
		int errorCode = JDBCExceptionHelper.extractErrorCode(sqlException);

		if (isMatch(getConnectionErrorCodes(), errorCode)) {
			return new JDBCConnectionException(message, sqlException, sql);
		} else if (isMatch(getSQLGrammarErrorCodes(), errorCode)) {
			return new SQLGrammarException(message, sqlException, sql);
		} else if (isMatch(getIntegrityViolationErrorCodes(), errorCode)) {
			String constraintName = extracter.extractConstraintName(sqlException);
			return new ConstraintViolationException(message, sqlException, sql, constraintName);
		} else if (isMatch(getLockAcquisitionErrorCodes(), errorCode)) {
			return new LockAcquisitionException(message, sqlException, sql);
		}

		return handledNonSpecificException(sqlException, message, sql);
	}

	/**
	 * Handle an exception not converted to a specific type based on the built-in
	 * checks.
	 * 
	 * @param sqlException The exception to be handled.
	 * @param message      An optional message
	 * @param sql          Optionally, the sql being performed when the exception
	 *                     occurred.
	 * @return The converted exception; should <b>never</b> be null.
	 */
	protected JDBCException handledNonSpecificException(SQLException sqlException, String message, String sql) {
		return new GenericJDBCException(message, sqlException, sql);
	}

	private boolean isMatch(int[] errorCodes, int errorCode) {
		if (errorCodes != null) {
			for (int i = 0, max = errorCodes.length; i < max; i++) {
				if (errorCodes[i] == errorCode) {
					return true;
				}
			}
		}
		return false;
	}
}
