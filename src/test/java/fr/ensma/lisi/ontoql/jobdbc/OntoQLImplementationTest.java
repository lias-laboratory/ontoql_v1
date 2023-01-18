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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.exception.QueryException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Test the quality of the implementation of OntoQL on OntoDB
 * 
 * @author Stéphane JEAN
 */
public class OntoQLImplementationTest extends OntoQLTestCase {

	/**
	 * An inner class to get the execution time of a query
	 */
	class TestTimer {

		private long startTime;
		private String message;

		/**
		 * Initiate a timer
		 */
		public TestTimer(String message) {
			startTime = System.currentTimeMillis();
			this.message = message;
		}

		/**
		 * Reset the timer for another timing session.
		 * 
		 */
		public void reset() {
			startTime = System.currentTimeMillis();
		}

		/**
		 * End the timing session and output the results.
		 */
		public void done() {

			System.out.println(message + " : " + (System.currentTimeMillis() - startTime) + " ms.");
		}
	}

	/**
	 * Test if the queries are optimized in a cache
	 */
	@Test
	public void testOptimizeQuery() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		for (int i = 1; i <= 5; i++) {
			TestTimer timer = new TestTimer("testOptimizeQuery");
			String queryOntoQL = "SELECT Size FROM ONTARIO";
			OntoQLStatement statement = s.createOntoQLStatement();
			ResultSet rset = statement.executeQuery(queryOntoQL);
			timer.done();
			rset.next();
			Assert.assertEquals("L", rset.getString(1));
		}

		s.close();

	}
}
