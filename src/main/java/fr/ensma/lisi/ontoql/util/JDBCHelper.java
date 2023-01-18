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

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

/**
 * Method usefull to handle JDBC data such as array.
 * 
 * @author Stéphane JEAN
 */
public class JDBCHelper {

	/**
	 * Convert an array of String into an SQL Array of int.
	 * 
	 * @param p an array of String
	 * @return an SQL Array of int
	 */
	public static Array convertToIntegerArray(final String[] p) {

		if (p == null || p.length < 1)
			return null;
		Array a = new Array() {
			public String getBaseTypeName() {
				return "int8";
			}

			public int getBaseType() {
				return 0;
			}

			public Object getArray() {
				return null;
			}

			public Object getArray(long index, int count) {
				return null;
			}

			public ResultSet getResultSet() {
				return null;
			}

			public String toString() {
				String fp = "{";
				if (p.length == 0) {
				} else {
					for (int i = 0; i < p.length - 1; i++)
						fp += p[i] + ",";
					fp += p[p.length - 1];
				}
				fp += "}";
				return fp;
			}

			public Object getArray(long index, int count, Map map) throws SQLException {
				return null;
			}

			public Object getArray(Map map) throws SQLException {
				return null;
			}

			public ResultSet getResultSet(long index, int count, Map map) throws SQLException {
				return null;
			}

			public ResultSet getResultSet(long index, int count) throws SQLException {
				return null;
			}

			public ResultSet getResultSet(Map map) throws SQLException {
				return null;
			}

			public void free() {
			}
		};

		return a;
	}

	public static String resultSetToString(ResultSet rs) {
		try {
			int i;
			String res = "";
			// Get the ResultSetMetaData. This will be used for the column
			// headings
			ResultSetMetaData rsmd = rs.getMetaData();

			// Get the number of columns in the result set
			int numCols = rsmd.getColumnCount();

			// Display column headings
			for (i = 1; i <= numCols; i++) {
				if (i > 1)
					res += (",");
				res += (rsmd.getColumnName(i));
			}
			res += ("\n-------------------------------------\n");

			// Display data, fetching until end of the result set
			while (rs.next()) {
				// Loop through each column, getting the
				// column data and displaying
				for (i = 1; i <= numCols; i++) {
					if (i > 1)
						res += (",");
					res += (rs.getString(i));
				}
				res += ("\n");
				// Fetch the next result set row
			}
			return res;
		} catch (Exception e) {

		}
		return null;
	}
}
