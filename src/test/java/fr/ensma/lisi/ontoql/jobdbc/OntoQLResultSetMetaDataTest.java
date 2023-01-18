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

import org.junit.Assert;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;

/**
 * @author Stéphane JEAN
 */
public class OntoQLResultSetMetaDataTest extends OntoQLTestCase {

	@Test
	public void testGetColumnName() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");
		String toParse = "SELECT Size FROM HUDSON";

		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSet = statement.executeQuery(toParse);
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
		Assert.assertEquals(resultSetMetaData.getColumnName(1), "Size");

		toParse = "SELECT * FROM CAGS";
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery(toParse);
		resultSetMetaData = resultSet.getMetaData();
		Assert.assertEquals(resultSetMetaData.getColumnName(1), "oid");
		Assert.assertEquals(resultSetMetaData.getColumnName(2), "Size");
		Assert.assertEquals(resultSetMetaData.getColumnCount(), 8);

		s.close();
	}

	@Test
	public void testGetColumnCount() throws Exception {
		OntoQLSession s = getSession();

		s.setReferenceLanguage("en");
		String toParse = "SELECT its_muff FROM HUDSON";

		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSet = statement.executeQuery(toParse);
		ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
		Assert.assertEquals(resultSetMetaData.getColumnCount(), 1);

		s.close();
	}
}
