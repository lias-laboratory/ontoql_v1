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
package fr.ensma.lisi.ontoql.preference;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.jobdbc.impl.OntoQLSessionImpl;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Stéphane JEAN
 */
public class PreferenceTest {

	public OntoQLSession sPreference;

	private Connection database;

	@Before
	public void setUp() throws Exception {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (Exception ex) {
		}

		try {
			database = DriverManager.getConnection("jdbc:postgresql://" + OntoQLTestCase.HOST + ":"
					+ OntoQLTestCase.PORT + "/OntoQLJUnitTestPreference", OntoQLTestCase.USR, OntoQLTestCase.PWD);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		sPreference = new OntoQLSessionImpl(database);
		sPreference.setReferenceLanguage(OntoQLHelper.FRENCH);
		sPreference.setDefaultNameSpace("INTERNATIONAL_ID");
	}

	@Test
	public void testOntoQLQuery() throws SQLException {
		String queryOntoQL = "SELECT name, price, description from Boutique_Hotel order by price asc";
		OntoQLStatement statement = sPreference.createOntoQLStatement();
		OntoQLResultSet resultset = statement.executeQuery(queryOntoQL);
		resultset.next();
		Assert.assertEquals("Hotel Formule1", resultset.getString(1));
		Assert.assertEquals("30", resultset.getString(2));
		Assert.assertEquals("very cheap hotel", resultset.getString(3));
		resultset.next();
		Assert.assertEquals("Hotel Première Classe", resultset.getString(1));
		Assert.assertEquals("40", resultset.getString(2));
		Assert.assertEquals("very cheap hotel", resultset.getString(3));
		resultset.next();
		Assert.assertEquals("Hotel Kyriad", resultset.getString(1));
		Assert.assertEquals("55", resultset.getString(2));
		Assert.assertEquals("cheap hotel", resultset.getString(3));
		resultset.next();
		Assert.assertEquals("Hotel IBIS", resultset.getString(1));
		Assert.assertEquals("75", resultset.getString(2));
		Assert.assertEquals("normal hotel", resultset.getString(3));
		resultset.next();
		Assert.assertEquals("Hotel Mercure", resultset.getString(1));
		Assert.assertEquals("95", resultset.getString(2));
		Assert.assertEquals("expensive hotel", resultset.getString(3));
		resultset.next();
		Assert.assertEquals("Hotel Sophitel", resultset.getString(1));
		Assert.assertEquals("115", resultset.getString(2));
		Assert.assertEquals("very expensive hotel", resultset.getString(3));
		Assert.assertFalse(resultset.next());

	}

	@Test
	public void testIntervalPreference() throws SQLException {

		// String queryOntoQL =
		// "CREATE ENTITY #Preference (#label String, #min int, #max int)";
		// OntoQLStatement statement = sPreference.createOntoQLStatement();
		// int res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);

		// String queryOntoQL =
		// "insert into #Preference (#label, #min, #max) VALUES ('very cheap', 20, 45)";
		// OntoQLStatement statement = sPreference.createOntoQLStatement();
		// int res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(1, res);
		//
		// queryOntoQL =
		// "insert into #Preference (#label, #min, #max) VALUES ('cheap', 45, 60)";
		// statement = sPreference.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(1, res);
		//
		// queryOntoQL =
		// "insert into #Preference (#label, #min, #max) VALUES ('normal', 60, 90)";
		// statement = sPreference.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(1, res);
		//
		// queryOntoQL =
		// "insert into #Preference (#label, #min, #max) VALUES ('expensive', 90, 100)";
		// statement = sPreference.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(1, res);
		//
		// queryOntoQL =
		// "insert into #Preference (#label, #min, #max) VALUES ('very expensive', 100,
		// 120)";
		// statement = sPreference.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(1, res);

		String queryOntoQL = "select #oid, #label, #min, #max from #Preference";
		OntoQLStatement statement = sPreference.createOntoQLStatement();
		OntoQLResultSet resultset = statement.executeQuery(queryOntoQL);
		Assert.assertTrue(resultset.next());
		Assert.assertEquals("1490", resultset.getString(1));
		Assert.assertEquals("very cheap", resultset.getString(2));
		Assert.assertEquals("20", resultset.getString(3));
		Assert.assertEquals("45", resultset.getString(4));
		Assert.assertTrue(resultset.next());
		Assert.assertEquals("1491", resultset.getString(1));
		Assert.assertEquals("cheap", resultset.getString(2));
		Assert.assertEquals("45", resultset.getString(3));
		Assert.assertEquals("60", resultset.getString(4));
		Assert.assertTrue(resultset.next());
		Assert.assertEquals("1492", resultset.getString(1));
		Assert.assertEquals("normal", resultset.getString(2));
		Assert.assertEquals("60", resultset.getString(3));
		Assert.assertEquals("90", resultset.getString(4));
		Assert.assertTrue(resultset.next());
		Assert.assertEquals("1493", resultset.getString(1));
		Assert.assertEquals("expensive", resultset.getString(2));
		Assert.assertEquals("90", resultset.getString(3));
		Assert.assertEquals("100", resultset.getString(4));
		Assert.assertTrue(resultset.next());
		Assert.assertEquals("1494", resultset.getString(1));
		Assert.assertEquals("very expensive", resultset.getString(2));
		Assert.assertEquals("100", resultset.getString(3));
		Assert.assertEquals("120", resultset.getString(4));
		Assert.assertFalse(resultset.next());

		// Je crois que cela n'a pas ajoute l'attribut dans le fichier
		// ontology_model.xml
		// queryOntoQL =
		// "ALTER ENTITY #concept ADD ATTRIBUTE #preferences REF(#Preference) ARRAY";
		// statement = sPreference.createOntoQLStatement();
		// int res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);

		// ne marche pas
		// queryOntoQL =
		// "update #property set #preferences = ARRAY[1490,1491,1492,1493,1494] where
		// #name = 'price'";
		// statement = sPreference.createOntoQLStatement();
		// int res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(1, res);

		// equivalent :
		// insert into class_and_property_elements_2_preferences (rid_s,
		// tablename_s, rid_d, tablename_d) values (1141,
		// 'NON_DEPENDENT_P_DET_E', 1490, 'PREFERENCE_E')
		// insert into class_and_property_elements_2_preferences (rid_s,
		// tablename_s, rid_d, tablename_d) values (1141,
		// 'NON_DEPENDENT_P_DET_E', 1491, 'PREFERENCE_E')
		// insert into class_and_property_elements_2_preferences (rid_s,
		// tablename_s, rid_d, tablename_d) values (1141,
		// 'NON_DEPENDENT_P_DET_E', 1492, 'PREFERENCE_E')
		// insert into class_and_property_elements_2_preferences (rid_s,
		// tablename_s, rid_d, tablename_d) values (1141,
		// 'NON_DEPENDENT_P_DET_E', 1493, 'PREFERENCE_E')
		// insert into class_and_property_elements_2_preferences (rid_s,
		// tablename_s, rid_d, tablename_d) values (1141,
		// 'NON_DEPENDENT_P_DET_E', 1494, 'PREFERENCE_E')
		// update property_det_e set preferences =
		// ARRAY[4188,4189,4190,4191,4192] where rid_bsu = 1141.

		queryOntoQL = "select pref.#oid, pref.#label, pref.#min, pref.#max from #property p, unnest(p.#preferences) as pref where p.#name='price'";
		statement = sPreference.createOntoQLStatement();
		resultset = statement.executeQuery(queryOntoQL);
		Assert.assertTrue(resultset.next());
		System.out.println(resultset.getString(1));
		Assert.assertEquals("1490", resultset.getString(1));
		Assert.assertEquals("very cheap", resultset.getString(2));
		Assert.assertEquals("20", resultset.getString(3));
		Assert.assertEquals("45", resultset.getString(4));
		Assert.assertTrue(resultset.next());
		System.out.println(resultset.getString(1));
		Assert.assertEquals("1491", resultset.getString(1));
		Assert.assertEquals("cheap", resultset.getString(2));
		Assert.assertEquals("45", resultset.getString(3));
		Assert.assertEquals("60", resultset.getString(4));
		Assert.assertTrue(resultset.next());
		Assert.assertEquals("1492", resultset.getString(1));
		Assert.assertEquals("normal", resultset.getString(2));
		Assert.assertEquals("60", resultset.getString(3));
		Assert.assertEquals("90", resultset.getString(4));
		Assert.assertTrue(resultset.next());
		Assert.assertEquals("1493", resultset.getString(1));
		Assert.assertEquals("expensive", resultset.getString(2));
		Assert.assertEquals("90", resultset.getString(3));
		Assert.assertEquals("100", resultset.getString(4));
		Assert.assertTrue(resultset.next());
		Assert.assertEquals("1494", resultset.getString(1));
		Assert.assertEquals("very expensive", resultset.getString(2));
		Assert.assertEquals("100", resultset.getString(3));
		Assert.assertEquals("120", resultset.getString(4));
		Assert.assertFalse(resultset.next());

		queryOntoQL = "select p.#name, pref.#min, pref.#max from #property p, unnest(p.#preferences) as pref where pref.#label='cheap'";
		statement = sPreference.createOntoQLStatement();
		resultset = statement.executeQuery(queryOntoQL);
		resultset.next();
		Assert.assertEquals("price", resultset.getString(1));
		Assert.assertEquals("45", resultset.getString(2));
		Assert.assertEquals("60", resultset.getString(3));
		Assert.assertFalse(resultset.next());

		queryOntoQL = "SELECT name, price, description from Boutique_Hotel PREFERRING cheap";
		statement = sPreference.createOntoQLStatement();
		resultset = statement.executeQuery(queryOntoQL);
		resultset.next();
		Assert.assertEquals("Hotel Kyriad", resultset.getString(1));
		Assert.assertEquals("55", resultset.getString(2));
		Assert.assertEquals("cheap hotel", resultset.getString(3));
		Assert.assertFalse(resultset.next());
	}

	@Test
	public void testNumericPreference() throws SQLException {
		String queryOntoQL = "select #name, #number_value, #order_relation from #Numeric_Interpreted_Preference";
		OntoQLStatement statement = sPreference.createOntoQLStatement();
		OntoQLResultSet resultset = statement.executeQuery(queryOntoQL);
		resultset.next();
		Assert.assertEquals("low level", resultset.getString(1));
		Assert.assertEquals("3", resultset.getString(2));
		Assert.assertEquals("<", resultset.getString(3));
		resultset.next();
		Assert.assertEquals("standard", resultset.getString(1));
		Assert.assertEquals("3", resultset.getString(2));
		Assert.assertEquals("=", resultset.getString(3));
		resultset.next();
		Assert.assertEquals("middle", resultset.getString(1));
		Assert.assertEquals("4", resultset.getString(2));
		Assert.assertEquals("=", resultset.getString(3));
		resultset.next();
		Assert.assertEquals("lux level", resultset.getString(1));
		Assert.assertEquals("5", resultset.getString(2));
		Assert.assertEquals("=", resultset.getString(3));
		resultset.next();
		Assert.assertEquals("very lux level", resultset.getString(1));
		Assert.assertEquals("5", resultset.getString(2));
		Assert.assertEquals(">", resultset.getString(3));
		Assert.assertFalse(resultset.next());

	}
}
