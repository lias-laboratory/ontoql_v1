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
package fr.ensma.lisi.ontoql.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLWarning;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Stéphane JEAN
 */
public final class JDBCExceptionReporter {

	public static final Log log = LogFactory.getLog(JDBCExceptionReporter.class);

	public static final String DEFAULT_EXCEPTION_MSG = "SQL Exception";

	public static final String DEFAULT_WARNING_MSG = "SQL Warning";

	private JDBCExceptionReporter() {
	}

	public static void logAndClearWarnings(Connection connection) {
		if (log.isWarnEnabled()) {
			try {
				logWarnings(connection.getWarnings());
				connection.clearWarnings();
			} catch (SQLException sqle) {
				// workaround for WebLogic
				log.debug("could not log warnings", sqle);
			}
		}
	}

	public static void logWarnings(SQLWarning warning) {
		logWarnings(warning, null);
	}

	public static void logWarnings(SQLWarning warning, String message) {
		if (log.isWarnEnabled()) {
			if (log.isDebugEnabled() && warning != null) {
				message = StringHelper.isNotEmpty(message) ? message : DEFAULT_WARNING_MSG;
				log.debug(message, warning);
			}
			while (warning != null) {
				StringBuffer buf = new StringBuffer(30).append("SQL Warning: ").append(warning.getErrorCode())
						.append(", SQLState: ").append(warning.getSQLState());
				log.warn(buf.toString());
				log.warn(warning.getMessage());
				warning = warning.getNextWarning();
			}
		}
	}

	public static void logExceptions(SQLException ex) {
		logExceptions(ex, null);
	}

	public static void logExceptions(SQLException ex, String message) {
		if (log.isErrorEnabled()) {
			if (log.isDebugEnabled()) {
				message = StringHelper.isNotEmpty(message) ? message : DEFAULT_EXCEPTION_MSG;
				log.debug(message, ex);
			}
			while (ex != null) {
				StringBuffer buf = new StringBuffer(30).append("SQL Error: ").append(ex.getErrorCode())
						.append(", SQLState: ").append(ex.getSQLState());
				log.warn(buf.toString());
				log.error(ex.getMessage());
				ex = ex.getNextException();
			}
		}
	}
}
