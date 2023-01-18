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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;

/**
 * Helper class to search in the system catalog of a database.
 * 
 * @author Stéphane Jean
 */
public class DatabaseHelper {

	/**
	 * A logger for this class.
	 */
	private static Log log = LogFactory.getLog(DatabaseHelper.class);

	/**
	 * Test wether a table exists in the database.
	 * 
	 * @param cnx connection to the database
	 * @return True if a table exists in the database.
	 * @exception SQLException if a database access error occurs
	 */
	public static boolean existTable(String nameTable, Connection cnx) throws SQLException {
		boolean res = false;

		Statement stmt = cnx.createStatement();
		ResultSet rset = stmt
				.executeQuery("select tablename from pg_tables where upper(tablename) = upper('" + nameTable + "')");
		res = rset.next();

		return res;
	}

	/**
	 * Test wether a table exists in the database.
	 * 
	 * @param cnx connection to the database
	 * @return True if a table exists in the database.
	 * @exception SQLException if a database access error occurs
	 */
	public static int executeUpdate(String cmd, OntoQLSession session) throws SQLException {
		int res = 0;

		Statement stmt = session.connection().createStatement();
		log.info(cmd);
		res = stmt.executeUpdate(cmd);

		return res;
	}
}
