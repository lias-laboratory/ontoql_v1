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

import java.sql.SQLException;

import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.QueryException;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Stéphane JEAN
 */
public class OntoQLParameterTest extends OntoQLTestCase {

	@Test
	public void testNamespaceCommands() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);

		// Create a class without a superclass
		Transaction t = s.beginTransaction();
		String queryOntoQL = "insert into #Ontology (#namespace) values ('http://test')";
		OntoQLStatement statement = s.createOntoQLStatement();
		int res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);
		queryOntoQL = "SET NAMESPACE 'http://test'";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		Assert.assertEquals("http://test", s.getDefaultNameSpace());
		t.rollback();

		t = s.beginTransaction();
		queryOntoQL = "insert into #Ontology (#namespace) values ('http://test2')";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);
		queryOntoQL = "SET NAMESPACE 'http://test'";
		try {
			res = statement.executeUpdate(queryOntoQL);
			Assert.fail();
		} catch (JOBDBCException e) {
			Assert.assertEquals("The namespace http://test doesn't exist", e.getMessage());
		}
		t.rollback();

		queryOntoQL = "SET NAMESPACE NONE";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(OntoQLHelper.NO_NAMESPACE, s.getDefaultNameSpace());

		queryOntoQL = "SET LANGUAGE EN";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(OntoQLHelper.ENGLISH, s.getReferenceLanguage());

		queryOntoQL = "SET LANGUAGE FR";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(OntoQLHelper.FRENCH, s.getReferenceLanguage());

		queryOntoQL = "SET LANGUAGE NONE";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(OntoQLHelper.NO_LANGUAGE, s.getReferenceLanguage());

		s.close();
	}
}