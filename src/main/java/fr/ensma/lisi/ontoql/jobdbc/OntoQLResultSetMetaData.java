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
package fr.ensma.lisi.ontoql.jobdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import fr.ensma.lisi.ontoql.core.Description;

/**
 * An object that can be used to get information about the types and properties
 * of the columns in a <code>ResultSet</code> object. The following code
 * fragment creates the <code>ResultSet</code> object rs, creates the
 * <code>ResultSetMetaData</code> object rsmd, and uses rsmd to find out how
 * many columns rs has
 * 
 * <PRE>
 * ResultSet rs = stmt.executeQuery("SELECT p1, p2, p3 FROM C1");
 * ResultSetMetaData rsmd = rs.getMetaData();
 * int numberOfColumns = rsmd.getColumnCount();
 * </PRE>
 * 
 * @author Stéphane JEAN
 */
public interface OntoQLResultSetMetaData extends ResultSetMetaData {

	/**
	 * Gets the designated column's description (attribute or property).
	 * 
	 * @param column the first column is 1, the second is 2, ...
	 * @return the description (attribute or property) or null if not applicable
	 * @exception SQLException if a database access error occurs
	 */
	Description getDescription(int column) throws SQLException;
}
