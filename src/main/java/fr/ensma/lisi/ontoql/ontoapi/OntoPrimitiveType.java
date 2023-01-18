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
package fr.ensma.lisi.ontoql.ontoapi;

import java.sql.SQLException;

import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;

/**
 * OntoPrimitiveType
 *
 * @author Mickael BARON
 */
public class OntoPrimitiveType extends OntoDatatype {

	public OntoPrimitiveType() {
	}

	public OntoPrimitiveType(int oid, OntoQLSession s) {
		super(oid, s);
	}

	public void load() {
		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			String whereClause = "#oid = " + oid;
			OntoQLResultSet resultSet = stmt.executeQuery("SELECT #oid FROM #primitiveType WHERE " + whereClause);
			if (resultSet.next()) {
				setOid(resultSet.getInt(1));

				isLoaded = true;
			} else {
				throw new JOBDBCException("the instance of primitiveType with oid " + oid + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}
}
