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
package fr.ensma.lisi.ontoql.sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.jobdbc.impl.OntoQLSessionImpl;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Stéphane JEAN
 */
public class OntoQLBugCFCATest {

	public OntoQLSession sPreference;

	private Connection database;

	@Before
	public void setUp() throws Exception {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (Exception ex) {
		}

		try {
			database = DriverManager.getConnection(
					"jdbc:postgresql://" + OntoQLTestCase.HOST + ":" + OntoQLTestCase.PORT + "/OntoWebStudioCFCA",
					OntoQLTestCase.USR, OntoQLTestCase.PWD);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		sPreference = new OntoQLSessionImpl(database);
		sPreference.setReferenceLanguage(OntoQLHelper.FRENCH);
		sPreference.setDefaultNameSpace("http://www.cfca.fr/");

		database.setAutoCommit(false);
	}

	@Test
	public void testAlterExtentQuery() throws SQLException {
		String alterClassStatement = "ALTER #CLASS RubansColliers ADD \"Lg Conditionnement\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-246#1', #definition[fr] = 'Longueur maximum exploitable en mm')";
		OntoQLStatement statement = sPreference.createOntoQLStatement();
		statement.executeUpdate(alterClassStatement);

		String alterExtentStatement = "ALTER EXTENT OF \"Ruban Adhésif\" ADD \"Lg Conditionnement\"";
		statement.executeUpdate(alterExtentStatement);
	}
}
