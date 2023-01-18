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
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * OntoContextProperty.
 *
 * @author Stéphane JEAN
 */
public class OntoContextProperty extends OntoProperty {

	public OntoContextProperty() {
	}

	public OntoContextProperty(int oid, OntoQLSession s) {
		super(oid, s);
	}

	public void load() {
		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			String whereClause = "#oid = " + oid;
			OntoQLResultSet resultSet = stmt.executeQuery(
					"SELECT #code, #version, #oid, #name[en], #name[fr], #definition[en], #definition[fr], #revision, #note[en], #note[fr], #remark[en], #remark[fr], #dateOfCurrentVersion, #dateOfCurrentRevision, #dateOfOriginalDefinition, #shortName[en], #shortName[fr] FROM #contextProperty WHERE "
							+ whereClause);
			if (resultSet.next()) {
				setCode(resultSet.getString(1));
				setVersion(resultSet.getString(2));
				setOid(resultSet.getInt(3));
				setName(resultSet.getString(4), OntoQLHelper.ENGLISH);
				setName(resultSet.getString(5), OntoQLHelper.FRENCH);
				setDefinition(resultSet.getString(6), OntoQLHelper.ENGLISH);
				setDefinition(resultSet.getString(7), OntoQLHelper.FRENCH);
				setRevision(resultSet.getString(8));
				setNote(resultSet.getString(9), OntoQLHelper.ENGLISH);
				setNote(resultSet.getString(10), OntoQLHelper.FRENCH);
				setRemark(resultSet.getString(11), OntoQLHelper.ENGLISH);
				setRemark(resultSet.getString(12), OntoQLHelper.FRENCH);
				setDateOfCurrentVersion(resultSet.getString(13));
				setDateOfCurrentRevision(resultSet.getString(14));
				setDateOfOriginalDefinition(resultSet.getString(15));
				setShortName(resultSet.getString(16), OntoQLHelper.ENGLISH);
				setShortName(resultSet.getString(17), OntoQLHelper.FRENCH);

				isLoaded = true;
			} else {
				throw new JOBDBCException("the instance of contextProperty with oid " + oid + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}
}
