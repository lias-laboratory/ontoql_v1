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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.QueryException;
import fr.ensma.lisi.ontoql.ontoapi.Instance;
import fr.ensma.lisi.ontoql.ontoapi.OntoProperty;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Mickael BARON
 */
public class OntoQLDDLTest extends OntoQLTestCase {

	@Test
	@SuppressWarnings("unchecked")
	public void testDMLWithSamePropertyName() throws JOBDBCException, SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		s.setDefaultNameSpace("http://lisi.ensma.fr/");
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate("CREATE #CLASS ClassA (DESCRIPTOR (#code ='0001'))");
		statement.executeUpdate("ALTER #CLASS ClassA ADD Type String DESCRIPTOR (#code ='0002')");
		statement.executeUpdate("CREATE EXTENT OF ClassA (Type)");

		statement.executeUpdate("CREATE #CLASS ClassB (DESCRIPTOR (#code ='0003'))");
		statement.executeUpdate("ALTER #CLASS ClassB ADD Type String DESCRIPTOR (#code ='0004')");
		statement.executeUpdate("CREATE EXTENT OF ClassB (Type)");

		OntoQLResultSet rset = statement.executeQuery("SELECT #properties FROM #Class WHERE #name[fr]='ClassA'");
		Set<OntoProperty> setClassA = null;
		Set<OntoProperty> setClassB = null;
		while (rset.next()) {
			setClassA = (Set<OntoProperty>) rset.getSet(1);
			System.out.println(rset.getString(1));
		}

		rset = statement.executeQuery("SELECT #properties FROM #Class WHERE #name[fr]='ClassB'");
		while (rset.next()) {
			setClassB = (Set<OntoProperty>) rset.getSet(1);
			System.out.println(rset.getString(1));
		}

		Assert.assertFalse(setClassA.equals(setClassB));

		t.rollback();
		getSession().close();
	}

	@Test
	public void testDDLWithoutDescriptor() {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.NO_LANGUAGE);

		Transaction t = s.beginTransaction();
		String queryOntoQL = "CREATE #CLASS C_Vehicle";
		OntoQLStatement statement = s.createOntoQLStatement();
		int res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		t.rollback();
	}

	@Test
	public void testDDLWithLanguage() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.NO_LANGUAGE);

		Transaction t = s.beginTransaction();
		String queryOntoQL = "CREATE #CLASS C_Vehicle ( "
				+ "DESCRIPTOR (#name[en] = 'Vehicle', #name[fr] = 'Véhicule', #definition[en]='Def Vehicle', #definition[fr]='Def Vehicule') "
				+ ")";
		OntoQLStatement statement = s.createOntoQLStatement();
		int res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		statement = s.createOntoQLStatement();
		OntoQLResultSet rs = statement.executeQuery(
				"select c.#name[en], c.#name[fr], c.#code, c.#definition[en], c.#definition[fr] from #class c where c.#oid = (select max(c1.#oid) from #class c1)");
		rs.next();
		Assert.assertEquals("Vehicle", rs.getString(1));
		Assert.assertEquals("Véhicule", rs.getString(2));
		Assert.assertEquals("C_Vehicle", rs.getString(3));
		Assert.assertEquals("Def Vehicle", rs.getString(4));
		Assert.assertEquals("Def Vehicule", rs.getString(5));
		queryOntoQL = "select oid from C_Vehicle";
		rs = statement.executeQuery(queryOntoQL);
		Assert.assertFalse(rs.next());

		t.rollback();

		t = s.beginTransaction();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);
		queryOntoQL = "CREATE #CLASS Vehicle ( " + "DESCRIPTOR (#code = 'C_Vehicle', #name[fr] = 'Véhicule') " + ")";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		statement = s.createOntoQLStatement();
		rs = statement.executeQuery(
				"select c.#name[en], c.#name[fr], c.#code from #class c where c.#oid = (select max(c1.#oid) from #class c1)");
		rs.next();
		Assert.assertEquals("Vehicle", rs.getString(1));
		Assert.assertEquals("Véhicule", rs.getString(2));
		Assert.assertEquals("C_Vehicle", rs.getString(3));

		queryOntoQL = "select oid from Vehicle";
		rs = statement.executeQuery(queryOntoQL);
		Assert.assertFalse(rs.next());
		queryOntoQL = "select oid from @C_Vehicle";
		rs = statement.executeQuery(queryOntoQL);
		Assert.assertFalse(rs.next());

		t.rollback();

		s.close();
	}

	@Test
	public void testAlterClass() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		Transaction t = s.beginTransaction();
		String queryOntoQL = "CREATE #CLASS Vehicle ( "
				+ "DESCRIPTOR (#name[fr] = 'Véhicule', #definition[en]='Def Vehicle', #definition[fr]='Def Vehicule') "
				+ "PROPERTIES (immatriculation String DESCRIPTOR (#nom[en]='im', #definition[en]='def im en', #definition[fr]='def im fr')))";
		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate(queryOntoQL);

		queryOntoQL = "ALTER #CLASS Vehicle ADD source String DESCRIPTOR (#nom[fr]='source', #definition[en]='a source', #definition[fr]='la source')";
		statement = s.createOntoQLStatement();
		int res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		OntoQLResultSet rs = statement.executeQuery(
				"select p.#name[en], p.#name[fr], p.#definition[en], p.#definition[fr] from #class c, #property p where p.#scope = c.#oid and c.#name[en]='Vehicle' order by p.#name[en] ASC");
		rs.next();
		Assert.assertEquals("im", rs.getString(1));
		rs.next();
		Assert.assertEquals("source", rs.getString(1));
		Assert.assertEquals("source", rs.getString(2));
		Assert.assertEquals("a source", rs.getString(3));
		Assert.assertEquals("la source", rs.getString(4));
		Assert.assertFalse(rs.next());

		// The following test is not yet implemented
		// queryOntoQL = "ALTER #CLASS Vehicle DROP source";
		// statement = s.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		// rs = statement
		// .executeQuery("select p.#name[en], p.#name[fr], p.#definition[en],
		// p.#definition[fr] from #class c, #property p where p.#scope = c.#oid and
		// c.#name[en]='Vehicle' order by p.#name[en] ASC");
		// rs.next();
		// Assert.assertEquals("im", rs.getString(1));
		// Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE EXTENT OF Vehicle (im)";
		statement = s.createOntoQLStatement();
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "INSERT INTO Vehicle (im) VALUES ('test1')";
		statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select p.#name[en] from #class c, unnest(c.#usedProperties) as p where c.#name[en]='Vehicle' order by p.#name[en] ASC");
		rs.next();
		Assert.assertEquals("im", rs.getString(1));
		Assert.assertFalse(rs.next());
		queryOntoQL = "ALTER EXTENT OF Vehicle ADD source";
		statement = s.createOntoQLStatement();
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "INSERT INTO Vehicle (im, source) VALUES ('test2', 'source1')";
		statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery("select im, source from Vehicle");
		rs.next();
		Assert.assertEquals("test1", rs.getString(1));
		Assert.assertNull(rs.getString(2));
		rs.next();
		Assert.assertEquals("test2", rs.getString(1));
		Assert.assertEquals("source1", rs.getString(2));
		Assert.assertFalse(rs.next());
		rs = statement.executeQuery(
				"select p.#name[en] from #class c, unnest(c.#usedProperties) as p where c.#name[en]='Vehicle' order by p.#name[en] ASC");
		rs.next();
		Assert.assertEquals("im", rs.getString(1));
		rs.next();
		Assert.assertEquals("source", rs.getString(1));
		Assert.assertFalse(rs.next());
		t.rollback();
		s.close();
	}

	@Test
	public void testExecuteDDLCommands() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);

		// Create a class without a superclass
		Transaction t = s.beginTransaction();
		String queryOntoQL = "CREATE #CLASSe Véhicule ( "
				+ "DESCRIPTOR (#nom[en] = 'Vehicle', #code = 'BB3DFD4', #version = '001') " + ")";
		OntoQLStatement statement = s.createOntoQLStatement();
		int res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		statement = s.createOntoQLStatement();
		OntoQLResultSet rs = statement.executeQuery(
				"select c.#nom[en], c.#code, c.#définition[en], #révision, #note[en], #note[fr], #remarque[en], #remarque[fr], #nomCourt[en], #nomCourt[fr], #dateVersionCourante from #classe c where c.#code = 'BB3DFD4'");
		rs.next();
		Assert.assertEquals("Vehicle", rs.getString(1));
		Assert.assertEquals("BB3DFD4", rs.getString(2));

		t.rollback();

		// Create a class with a superclass
		t = s.beginTransaction();
		queryOntoQL = "CREATE #CLASSe Véhicule UNDER \"COUPES - VENT\" ( "
				+ "DESCRIPTOR (#nom[en] = 'Vehicle', #code = 'BB3DFD4', #version = '001') " + ")";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		statement = s.createOntoQLStatement();
		rs = statement.executeQuery(
				"select c from #classe c, unnest(c.#superClassesDirectes) as directSuperclass where c.#code = 'BB3DFD4' and "
						+ "directSuperclass.#nom[en] = 'CAGS'");
		rs.next();
		EntityClass c = rs.getEntityClass(1);
		Assert.assertEquals("Vehicle", c.getName(OntoQLHelper.ENGLISH));
		Assert.assertEquals("BB3DFD4-001", c.getExternalId());
		t.rollback();

		queryOntoQL = "CREATE #PROPRIété Véhicule UNDER \"COUPES-VENT\" ( "
				+ "DESCRIPTOR (#nom[en] = 'Vehicle', #code = 'BB3DFD4', #version = '001') " + ")";

		statement = s.createOntoQLStatement();
		try {
			res = statement.executeUpdate(queryOntoQL);
			Assert.fail();
		} catch (JOBDBCException e) {
			Assert.assertEquals("Clause UNDER incompatible avec l'entité property", e.getMessage());
		}

		// Create a class with some properties
		t = s.beginTransaction();
		queryOntoQL = "CREATE #CLASSe Véhicule ( "
				+ "DESCRIPTOR (#nom[en] = 'Vehicle', #code = 'BB3DFD4', #version = '001') "
				+ "PROPERTIES (immatriculation String DESCRIPTOR (#nom[en]='im', #definition[en]='def im en', #definition[fr]='def im fr')))";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		statement = s.createOntoQLStatement();
		rs = statement.executeQuery(
				"select c, p.#nom[en], p.#definition[en], p.#definition[fr] from #classe c, #propriété p where p.#nom[fr] = 'immatriculation' and p.#domaine = c and c.#nom[en]='Vehicle'");
		rs.next();
		c = rs.getEntityClass(1);
		Assert.assertEquals("Vehicle", c.getName(OntoQLHelper.ENGLISH));
		Assert.assertEquals("BB3DFD4-001", c.getExternalId());
		Assert.assertEquals("im", rs.getString(2));
		Assert.assertEquals("def im en", rs.getString(3));
		Assert.assertEquals("def im fr", rs.getString(4));
		t.rollback();

		t = s.beginTransaction();
		queryOntoQL = "CREATE #CLASSe Véhicule ( "
				+ "DESCRIPTOR (#nom[en] = 'Vehicle', #code = 'BB3DFD4', #version = '001') "
				+ "PROPERTIES (immatriculation String, \"nb roues\" Int))";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		statement = s.createOntoQLStatement();
		rs = statement.executeQuery(
				"select c from #classe c, #propriété p where p.#nom[fr] = 'nb roues' and p.#domaine = c and c.#nom[en]='Vehicle'");
		rs.next();
		c = rs.getEntityClass(1);
		Assert.assertEquals("Vehicle", c.getName(OntoQLHelper.ENGLISH));
		Assert.assertEquals("BB3DFD4-001", c.getExternalId());
		t.rollback();

		// Test the creation of a complete class
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);
		t = s.beginTransaction();
		queryOntoQL = "create #class Vehicle UNDER CAGS ("
				+ "DESCRIPTOR (#name[fr]='Véhicule', #code='AFBDF54D', #version='002')"
				+ "properties (\"number of wheels\" int, number String))";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		statement = s.createOntoQLStatement();
		rs = statement.executeQuery("select c from #class c where c.#name[fr] = 'Véhicule'");
		rs.next();
		c = rs.getEntityClass(1);
		Assert.assertEquals("Vehicle", c.getName(OntoQLHelper.ENGLISH));
		t.rollback();

		s.setReferenceLanguage(OntoQLHelper.ENGLISH);
		t = s.beginTransaction();
		queryOntoQL = "create #class Vehicle UNDER CAGS ("
				+ "DESCRIPTOR (#name[fr]='Véhicule', #code='AFBDF54D', #version='002')"
				+ "properties (\"number of wheels\" int, number String))";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		queryOntoQL = "create extent of Vehicle (\"number of wheels\", number)";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		queryOntoQL = "insert into Vehicle (\"number of wheels\", number) values (4, '5255TH16')";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);

		rs = statement.executeQuery("select \"number of wheels\" from Vehicle where number = '5255TH16'");
		rs.next();
		Assert.assertEquals(4, rs.getInt(1));
		t.rollback();

		// Define a class and an extent with an association
		t = s.beginTransaction();
		queryOntoQL = "create #class Vehicle UNDER CAGS ("
				+ "DESCRIPTOR (#name[fr]='Véhicule', #code='AFBDF54D', #version='002')"
				+ "properties (\"number of wheels\" int, number String))";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		queryOntoQL = "create #class Person (" + "DESCRIPTOR (#name[fr]='Personne', #code='AXDFBDF54D', #version='001')"
				+ "properties (age int, its_car REF(Vehicle)))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		queryOntoQL = "create extent of Vehicle (\"number of wheels\", number)";
		res = statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into Vehicle (\"number of wheels\", number) values (4, '5255TH16')";
		res = statement.executeUpdate(queryOntoQL);

		queryOntoQL = "create extent of Person (its_car)";
		res = statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into Person (its_car) values ((SELECT v.oid From Vehicle v))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);

		rs = statement.executeQuery("select p.its_car.number from Person p");
		rs.next();
		Assert.assertEquals("5255TH16", rs.getString(1));
		t.rollback();

		// Define a class and an extent with a collection
		t = s.beginTransaction();
		queryOntoQL = "create #class Vehicle under CAGS ("
				+ "DESCRIPTOR (#name[fr]='Véhicule', #code='AFBDF54D', #version='002')"
				+ "properties (\"number of wheels\" int, number String))";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		queryOntoQL = "create #class Person (" + "DESCRIPTOR (#name[fr]='Personne', #code='AXDFBDF54D', #version='001')"
				+ "properties (age int, its_cars REF(Vehicle) ARRAY))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		queryOntoQL = "create extent of Vehicle (\"number of wheels\", number)";
		res = statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into Vehicle (\"number of wheels\", number) values (4, '5255TH16')";
		res = statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into Vehicle (\"number of wheels\", number) values (4, '5255TH86')";
		res = statement.executeUpdate(queryOntoQL);

		queryOntoQL = "create extent of Person (its_cars)";
		res = statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into Person (its_cars) values (ARRAY(SELECT v.oid From Vehicle v))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);

		rs = statement.executeQuery("select p.its_cars from only(Person) p");
		rs.next();
		Instance[] its_cars = (Instance[]) rs.getCollection(1);
		Assert.assertEquals("5255TH16", its_cars[0].getStringPropertyValue("number"));
		t.rollback();

		// create an extent of a class as a view
		t = s.beginTransaction();
		queryOntoQL = "create #class Vehicle under CAGS ("
				+ "DESCRIPTOR (#name[fr]='Véhicule', #code='AFBDF54D', #version='001')"
				+ "properties (\"number of wheels\" int, number String))";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		queryOntoQL = "create view of Vehicle as (select oid, Reference, Size from ONLY(HUDSON))";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery("select oid, Reference, Size from only(Vehicle)");
		Assert.assertEquals(resultSetToString(statement.executeQuery("select oid, Reference, Size from HUDSON")),
				resultSetToString(rs));

		queryOntoQL = "insert into Vehicle (Size, Reference) values ('XL', '5255TH16')";
		try {
			res = statement.executeUpdate(queryOntoQL);
			Assert.fail();
		} catch (JOBDBCException oExc) {
			// This error message should be more meaningful.
			Assert.assertNotNull(oExc.getMessage());
			// Assert.assertEquals("ERROR: cannot insert into a view", oExc.getMessage());
		}
		t.rollback();

		t = s.beginTransaction();
		queryOntoQL = "CREATE #class Vehicle (" + "DESCRIPTOR (#name[fr]='Véhicule', #code='AFBDF54D', #version='002')"
				+ "PROPERTIES (\"number of wheels\" int, number String))";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "CREATE #class Person (" + "DESCRIPTOR (#name[fr]='Personne', #code='AXDFBDF54D', #version='001')"
				+ "PROPERTIES (name String, age int, its_cars REF(Vehicle) ARRAY, favorite_car REF(Vehicle)))";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "CREATE extent of Vehicle (\"number of wheels\", number)";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into Vehicle (\"number of wheels\", number) values (4, '5255TH16')";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into Vehicle (\"number of wheels\", number) values (2, '1245RX86')";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "create extent of Person (name, its_cars, favorite_car)";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into Person (name, its_cars, favorite_car) values ('jean', ARRAY(SELECT v.oid From only(Vehicle) v), (SELECT v.oid FROM Vehicle v WHERE v.\"number of wheels\"=2) )";
		statement.executeUpdate(queryOntoQL);
		try {
			queryOntoQL = "insert into Person (name, its_cars, favorite_car) values ('jean', ARRAY[12,15,15], (SELECT v.oid FROM Vehicle v WHERE v.\"number of wheels\"=2) )";
			statement.executeUpdate(queryOntoQL);
			Assert.fail();
		} catch (JOBDBCException oExc) {
			Assert.assertEquals("the values of its_cars are not all instances of Vehicle", oExc.getMessage());
		}
		t.rollback();

		t = s.beginTransaction();
		queryOntoQL = "CREATE #class Vehicle (" + "DESCRIPTOR (#name[fr]='Véhicule', #code='AFBDF54D', #version='002')"
				+ "PROPERTIES (\"number of wheels\" int, number STRING))";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "CREATE #class Person (" + "DESCRIPTOR (#name[fr]='Personne', #code='AXDFBDF54D', #version='001')"
				+ "PROPERTIES (name String, age int, its_cars REF(Vehicle) ARRAY, favorite_car REF(Vehicle)))";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "CREATE extent of Vehicle (\"number of wheels\", number)";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into Vehicle (\"number of wheels\", number) values (4, '5255TH16')";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into Vehicle (\"number of wheels\", number) values (2, '1245RX86')";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "create extent of Person (name, its_cars, favorite_car)";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into Person (name, its_cars, favorite_car) values ('jean', ARRAY(SELECT v.oid From only(Vehicle) v), (SELECT v.oid FROM Vehicle v WHERE v.\"number of wheels\"=2) )";
		statement.executeUpdate(queryOntoQL);
		try {
			queryOntoQL = "insert into Person (name, its_cars, favorite_car) values ('jean', ARRAY[12,15,15], (SELECT v.oid FROM Vehicle v WHERE v.\"number of wheels\"=2) )";
			statement.executeUpdate(queryOntoQL);
			Assert.fail();
		} catch (JOBDBCException oExc) {
			Assert.assertEquals("the values of its_cars are not all instances of Vehicle", oExc.getMessage());
		}
		t.rollback();

		s.close();
	}

	@Test
	public void testMultilingualProperties() throws SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		Transaction t = s.beginTransaction();
		String queryOntoQL = "CREATE #class Vehicle ("
				+ "DESCRIPTOR (#name[fr]='Véhicule', #code='AFBDF54D', #version='002')"
				+ "PROPERTIES (\"number of wheels\" int, colored MULTILINGUAL STRING))";
		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "CREATE extent of Vehicle (\"number of wheels\", colored)";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into Vehicle (\"number of wheels\", colored[en], colored[fr]) values (4, 'Red', 'Rouge')";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "select colored[en] from Vehicle where colored[fr] = 'Rouge'";
		OntoQLResultSet resultset = statement.executeQuery(queryOntoQL);
		resultset.next();
		Assert.assertEquals("Red", resultset.getString(1));
		t.rollback();

		s.close();
	}

	/**
	 * Test if the extension are taken into account by the database
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testODLCreateAndDrop() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		// a new entity with a primitive attribute
		Transaction t = s.beginTransaction();
		String queryOntoQL = "CREATE ENTITY #expression (#oid int)";
		OntoQLStatement statement = s.createOntoQLStatement();
		int res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		queryOntoQL = "SELECT #oid from #expression";
		statement = s.createOntoQLStatement();
		OntoQLResultSet resultSet = statement.executeQuery(queryOntoQL);
		Assert.assertFalse(resultSet.next());
		statement = s.createOntoQLStatement();

		queryOntoQL = "DROP ENTITY #expression";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		queryOntoQL = "SELECT #oid from #expression";
		try {
			resultSet = statement.executeQuery(queryOntoQL);
			Assert.fail();
		} catch (JOBDBCException exc) {
			Assert.assertEquals("expression is not defined in the mapping.", exc.getMessage());
		}
		try {
			// Create the structure in the database.
			Connection cnx = getSession().connection();
			Statement stmt = cnx.createStatement();
			stmt.executeQuery("select * from expression_e");
			Assert.fail();
		} catch (SQLException exc) {
			Assert.assertNotNull(exc.getMessage());
		}
		t.rollback();

		// With a multilingual attribute.
		t = s.beginTransaction();
		queryOntoQL = "CREATE ENTITY #expression (#oid INT, #description MULTILINGUAL STRING)";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		queryOntoQL = "SELECT #description[en] from #expression";
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery(queryOntoQL);
		Assert.assertFalse(resultSet.next());
		statement = s.createOntoQLStatement();

		queryOntoQL = "DROP ENTITY #expression";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		queryOntoQL = "SELECT #description[en] from #expression";
		try {
			resultSet = statement.executeQuery(queryOntoQL);
			Assert.fail();
		} catch (JOBDBCException exc) {
			Assert.assertEquals("expression is not defined in the mapping.", exc.getMessage());
		}
		try {
			// Create the structure in the database.
			Connection cnx = getSession().connection();
			Statement stmt = cnx.createStatement();
			stmt.executeQuery("select * from expression_2_description");
			Assert.fail();
		} catch (SQLException exc) {
			Assert.assertNotNull(exc.getMessage());
		}
		t.rollback();

		// with a multilingual attribute of type collection
		// t = s.beginTransaction();
		// queryOntoQL = "CREATE ENTITY #expression (#oid INT, #comments
		// MULTILINGUAL STRING ARRAY)";
		// statement = s.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		//
		// queryOntoQL = "SELECT #comments[en] from #expression";
		// statement = s.createOntoQLStatement();
		// resultSet = statement.executeQuery(queryOntoQL);
		// Assert.assertFalse(resultSet.next());
		// statement = s.createOntoQLStatement();
		// t.rollback();

		// With a ref attribute.
		t = s.beginTransaction();
		queryOntoQL = "CREATE ENTITY #expression (#oid INT, #appliedOn REF(#class))";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		queryOntoQL = "SELECT #appliedOn.#name[en] from #expression";
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery(queryOntoQL);
		Assert.assertFalse(resultSet.next());
		statement = s.createOntoQLStatement();

		queryOntoQL = "DROP ENTITY #expression";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		queryOntoQL = "SELECT #appliedOn.#name[en] from #expression";
		try {
			resultSet = statement.executeQuery(queryOntoQL);
			Assert.fail();
		} catch (JOBDBCException exc) {
			Assert.assertEquals("expression is not defined in the mapping.", exc.getMessage());
			// Test ok
		}
		try {
			// create the structure in the database
			Connection cnx = getSession().connection();
			Statement stmt = cnx.createStatement();
			stmt.executeQuery("select * from expression_2_appliedon");
			Assert.fail();
		} catch (SQLException exc) {
			Assert.assertNotNull(exc.getMessage());
//	    Assert.assertEquals(
//		    "ERROR: relation \"expression_2_appliedon\" does not exist",
//		    exc.getMessage());
		}
		t.rollback();

		// with a collection attribute of type int
		t = s.beginTransaction();
		queryOntoQL = "CREATE ENTITY #expression (#oid INT, #codes int ARRAY)";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		queryOntoQL = "SELECT #codes from #expression";
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery(queryOntoQL);
		Assert.assertFalse(resultSet.next());
		statement = s.createOntoQLStatement();
		queryOntoQL = "DROP ENTITY #expression";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		queryOntoQL = "SELECT #codes from #expression";
		try {
			resultSet = statement.executeQuery(queryOntoQL);
			Assert.fail();
		} catch (JOBDBCException exc) {
			Assert.assertEquals("expression is not defined in the mapping.", exc.getMessage());
			// Test ok
		}
		try {
			// create the structure in the database
			Connection cnx = getSession().connection();
			Statement stmt = cnx.createStatement();
			stmt.executeQuery("select * from expression_e");
			Assert.fail();
		} catch (SQLException exc) {
			Assert.assertNotNull(exc.getMessage());
//	    Assert.assertEquals("ERROR: relation \"expression_e\" does not exist",
//		    exc.getMessage());
		}
		t.rollback();

		// with a collection attribute of type ref
		t = s.beginTransaction();
		queryOntoQL = "CREATE ENTITY #expression (#oid INT, #usedProperties REF(#property) ARRAY)";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		queryOntoQL = "SELECT p.#name[en] from #expression e, unnest(e.#usedProperties) as p";
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery(queryOntoQL);
		Assert.assertFalse(resultSet.next());
		statement = s.createOntoQLStatement();

		queryOntoQL = "DROP ENTITY #expression";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		queryOntoQL = "SELECT p.#name[en] from #expression e, unnest(e.#usedProperties) as p";
		try {
			resultSet = statement.executeQuery(queryOntoQL);
			Assert.fail();
		} catch (JOBDBCException exc) {
			Assert.assertEquals("expression is not defined in the mapping.", exc.getMessage());
			// Test ok
		}
		try {
			// create the structure in the database
			Connection cnx = getSession().connection();
			Statement stmt = cnx.createStatement();
			stmt.executeQuery("select * from expression_2_usedproperties");
			Assert.fail();
		} catch (SQLException exc) {
			Assert.assertNotNull(exc.getMessage());
//	    Assert.assertEquals(
//		    "ERROR: relation \"expression_2_usedproperties\" does not exist",
//		    exc.getMessage());
		}

		t.rollback();

		s.close();
	}

	/**
	 * Test if the extension are taken into account by the database Doesn't work in
	 * the AllTest suite but work alone (maybe a problem of transaction)...
	 */
	@Test
	public void testODLAlter() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		// an existing entity with a new primitive attribute

		// String queryOntoQL =
		// "ALTER ENTITY #property ADD ATTRIBUTE #isTransitive boolean";
		// OntoQLStatement statement = s.createOntoQLStatement();
		// int res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		//
		// queryOntoQL = "SELECT #isTransitive from #property";
		// statement = s.createOntoQLStatement();
		// OntoQLResultSet resultSet = statement.executeQuery(queryOntoQL);
		// resultSet.next();
		// Assert.Assert.assertNull(resultSet.getString(1));
		//
		// queryOntoQL = "ALTER ENTITY #property DROP ATTRIBUTE #isTransitive";
		// statement = s.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		//
		// queryOntoQL = "ALTER ENTITY #property DROP ATTRIBUTE #isTransitiv";
		// statement = s.createOntoQLStatement();
		// try {
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.fail();
		// } catch (JOBDBCException exc) {
		// Assert.assertEquals("The attribute #isTransitiv is not defined on the entity
		// #property",
		// exc
		// .getMessage());
		// // Test ok
		// }
		//
		// queryOntoQL = "SELECT #isTransitive from #property";
		// try {
		// resultSet = statement.executeQuery(queryOntoQL);
		// Assert.fail();
		// } catch (JOBDBCException exc) {
		// Assert.assertEquals("The attribute '#isTransitive' is not defined in the
		// context of the from clause",
		// exc
		// .getMessage());
		// // Test ok
		// }
		//
		// // with a multilingual attribute
		// queryOntoQL =
		// "ALTER ENTITY #property ADD ATTRIBUTE #description multilingual string";
		// statement = s.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		//
		// queryOntoQL = "SELECT #description[en] from #property";
		// statement = s.createOntoQLStatement();
		// resultSet = statement.executeQuery(queryOntoQL);
		// resultSet.next();
		// Assert.Assert.assertNull(resultSet.getString(1));
		//
		// queryOntoQL = "ALTER ENTITY #property DROP ATTRIBUTE #description";
		// statement = s.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		//
		// queryOntoQL = "SELECT #description[en] from #property";
		// try {
		// resultSet = statement.executeQuery(queryOntoQL);
		// Assert.fail();
		// } catch (JOBDBCException exc) {
		// Assert.assertEquals("The attribute '#description' is not defined in the
		// context of the from clause",
		// exc
		// .getMessage());
		// // Test ok
		// }
		//
		// try {
		// // create the structure in the database
		// Connection cnx = session.connection();
		// Statement stmt = cnx.createStatement();
		// stmt.executeQuery("select * from property_det_2_description");
		// Assert.fail();
		// } catch (SQLException exc) {
		// Assert.assertEquals("ERROR: relation \"property_det_2_description\" does not
		// exist",
		// exc
		// .getMessage());
		// }
		//
		//
		// // with a ref attribute
		// queryOntoQL =
		// "ALTER ENTITY #property ADD ATTRIBUTE #inverseOf REF(#property)";
		// statement = s.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		//
		// queryOntoQL = "SELECT #inverseOf.#name[en] from #property";
		// statement = s.createOntoQLStatement();
		// resultSet = statement.executeQuery(queryOntoQL);
		// resultSet.next();
		// Assert.Assert.assertNull(resultSet.getString(1));
		//
		// queryOntoQL = "ALTER ENTITY #property DROP ATTRIBUTE #inverseOf";
		// statement = s.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		//
		// queryOntoQL = "SELECT #inverseOf.#name[en] from #property";
		// try {
		// resultSet = statement.executeQuery(queryOntoQL);
		// Assert.fail();
		// } catch (JOBDBCException exc) {
		// Assert.assertEquals("The attribute '#inverseOf' is not defined in the context
		// of the from clause",
		// exc
		// .getMessage());
		// // Test ok
		// }
		//
		// try {
		// // create the structure in the database
		// Connection cnx = session.connection();
		// Statement stmt = cnx.createStatement();
		// stmt.executeQuery("select * from property_det_2_inverseof");
		// Assert.fail();
		// } catch (SQLException exc) {
		// Assert.assertEquals("ERROR: relation \"property_det_2_inverseof\" does not
		// exist",
		// exc
		// .getMessage());
		// }
		//
		// // with a collection attribute of type int
		// queryOntoQL =
		// "ALTER ENTITY #property ADD ATTRIBUTE #codes int array";
		// statement = s.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		//
		// queryOntoQL = "SELECT #codes from #property";
		// statement = s.createOntoQLStatement();
		// resultSet = statement.executeQuery(queryOntoQL);
		// resultSet.next();
		// Assert.Assert.assertNull(resultSet.getString(1));
		//
		// queryOntoQL = "ALTER ENTITY #property DROP ATTRIBUTE #codes";
		// statement = s.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		//
		// // with a collection attribute of type ref
		// queryOntoQL =
		// "ALTER ENTITY #property ADD ATTRIBUTE #directSuperproperties REF(#property)
		// ARRAY";
		// statement = s.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		//
		// queryOntoQL = "SELECT p.#directSuperproperties from #property p";
		// statement = s.createOntoQLStatement();
		// resultSet = statement.executeQuery(queryOntoQL);
		// resultSet.next();
		// Assert.Assert.assertNull(resultSet.getString(1));
		//
		// queryOntoQL =
		// "ALTER ENTITY #property DROP ATTRIBUTE #directSuperproperties";
		// statement = s.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		//
		// queryOntoQL = "SELECT p.#directSuperproperties from #property p";
		// try {
		// resultSet = statement.executeQuery(queryOntoQL);
		// Assert.fail();
		// } catch (JOBDBCException exc) {
		// Assert.assertEquals("The attribute 'directSuperproperties' is not defined on
		// the entity 'p'",
		// exc
		// .getMessage());
		// // Test ok
		// }
		//
		// try {
		// // create the structure in the database
		// Connection cnx = session.connection();
		// Statement stmt = cnx.createStatement();
		// stmt.executeQuery("select * from property_det_2_directSuperproperties");
		// Assert.fail();
		// } catch (SQLException exc) {
		// Assert.assertEquals("ERROR: relation \"property_det_2_directsuperproperties\"
		// does not exist",
		// exc
		// .getMessage());
		// }
		//
		// Transaction t = s.beginTransaction();
		//
		// String queryOntoQL =
		// "ALTER ENTITY #concept ADD ATTRIBUTE #OWLNamespace STRING";
		// OntoQLStatement statement = s.createOntoQLStatement();
		// int res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		//
		// queryOntoQL = "SELECT #OWLNamespace from #concept";
		// statement = s.createOntoQLStatement();
		// OntoQLResultSet resultSet = statement.executeQuery(queryOntoQL);
		// resultSet.next();
		// Assert.Assert.assertNull(resultSet.getString(1));
		//
		// queryOntoQL = "CREATE #CLASS Test ( "
		// +
		// "DESCRIPTOR (#name[en] = 'Test_EN', #name[fr] = 'TEST_FR',
		// #OWLNamespace='http://www.lisi.ensma.fr'"
		// +
		// ")PROPERTIES (prop String DESCRIPTOR (#name[en]='prop_en',
		// #name[fr]='prop_fr', #OWLNamespace='http://www.lisi.ensma.fr2')))";
		// statement = s.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		//
		// queryOntoQL =
		// "SELECT #OWLNamespace from #concept where #name[en]= 'Test_EN' or #name[en]=
		// 'prop_en' order by #OWLNamespace ASC";
		// statement = s.createOntoQLStatement();
		// resultSet = statement.executeQuery(queryOntoQL);
		// resultSet.next();
		// Assert.assertEquals("http://www.lisi.ensma.fr", resultSet.getString(1));
		// resultSet.next();
		// Assert.assertEquals("http://www.lisi.ensma.fr2", resultSet.getString(1));
		//
		// queryOntoQL = "ALTER ENTITY #concept DROP ATTRIBUTE #OWLNamespace";
		// statement = s.createOntoQLStatement();
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		//
		// t.rollback();

		s.close();
	}

	public String resultSetToString(ResultSet rs) {
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

	/*
	 * belaidn - test if the following type of query work (implementation of the
	 * array) UPDATE #class SET #testArray = ARRAY[1068,1062] WHERE #oid='161086'
	 */
	/*
	 * belaidn - test if the following type of query work (implementation of the
	 * array) UPDATE #class SET #testArray = ARRAY[1068,1062] WHERE #oid= SELECT
	 * #oid from #class where #name[en]= 'Test_EN')
	 */
	/*
	 * belaidn - test if the following type of query work (implementation of the
	 * array) UPDATE #class SET #testArray = ARRAY(SELECT c.#oid from #class c where
	 * c.#name[en] = 'HUDSON' or c.#name[en] = 'CAGS') WHERE #oid='161086'
	 */
	/*
	 * belaidn - test if the following type of query work (implementation of the
	 * array) UPDATE #class SET #testArray = ARRAY(SELECT c.#oid from #class c where
	 * c.#name[en] = 'HUDSON' or c.#name[en] = 'CAGS') WHERE #oid=SELECT #oid from
	 * #class where #name[en]= 'Test_EN')
	 */
	// public void testODLAlterForArray() throws Exception {
	//
	// OntoQLSession s = openSession();
	// s.setReferenceLanguage(OntoQLHelper.ENGLISH);
	//
	// Transaction t = s.beginTransaction();
	//
	// String queryOntoQL =
	// "ALTER ENTITY #class ADD ATTRIBUTE #testArray REF(#class) ARRAY";
	// OntoQLStatement statement = s.createOntoQLStatement();
	// int res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(0, res);
	// queryOntoQL = "CREATE #CLASS Test ( "
	// + "DESCRIPTOR (#name[en] = 'Test_EN', #name[fr] = 'TEST_FR' "
	// +
	// ")PROPERTIES (prop String DESCRIPTOR (#name[en]='prop_en',
	// #name[fr]='prop_fr')))";
	// statement = s.createOntoQLStatement();
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(0, res);
	//
	// queryOntoQL = "SELECT #oid from #class where #name[en]= 'Test_EN'";
	// statement = s.createOntoQLStatement();
	// OntoQLResultSet resultSet = statement.executeQuery(queryOntoQL);
	// resultSet.next();
	// String oidClassTest = resultSet.getString(1);
	//
	// // 091009: the 4 following queries have been implemented
	// queryOntoQL =
	// "UPDATE #class SET #testArray = ARRAY[1068,1062] WHERE #oid = '"
	// + oidClassTest + "'";
	// queryOntoQL =
	// "UPDATE #class SET #testArray = ARRAY[1068,1062] WHERE #oid =(SELECT c.#oid
	// from #class c where c.#name[en]= 'Test_EN')";
	// queryOntoQL =
	// "UPDATE #class SET #testArray = ARRAY(SELECT c.#oid from #class c where
	// c.#name[en] = 'HUDSON' or c.#name[en] = 'CAGS') WHERE #oid = '"
	// + oidClassTest + "'";
	// queryOntoQL =
	// "UPDATE #class SET #testArray = ARRAY(SELECT c.#oid from #class c where
	// c.#name[en] = 'HUDSON' or c.#name[en] = 'CAGS') WHERE #oid =(SELECT c.#oid
	// from #class c where c.#name[en]= 'Test_EN')";
	//
	// statement = s.createOntoQLStatement();
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(1, res);
	//
	// String sql = "select rid from class_2_testArray";
	// Statement st = s.connection().createStatement();
	// ResultSet resultsetQuerySQL = st.executeQuery(sql);
	// assertTrue(resultsetQuerySQL.next());
	// System.out
	// .println("#################################################################");
	// System.out.println(resultsetQuerySQL.getInt(1));
	// System.out
	// .println("#################################################################");
	//
	// queryOntoQL =
	// "SELECT t.#name[en] from #class c, unnest(c.#testArray) as t where c.#oid =
	// '"
	// + oidClassTest + "' order by t.#name[en]";
	// statement = s.createOntoQLStatement();
	// resultSet = statement.executeQuery(queryOntoQL);
	// assertTrue(resultSet.next());
	// Assert.assertEquals("CAGS", resultSet.getString(1));
	// resultSet.next();
	// Assert.assertEquals("HUDSON", resultSet.getString(1));
	// Assert.assertFalse(resultSet.next());
	//
	// t.rollback();
	//
	// s.close();
	// }

	//
	// /*
	// * belaidn - test if the following type of query work (caused by the
	// implementation of the array)
	// * INSERT INTO #class (#oid) VALUES (11111)
	// */
	// public void testODLAlterForInsertIntoClass() throws Exception {
	//
	// OntoQLSession s = openSession();
	// s.setReferenceLanguage(OntoQLHelper.ENGLISH);
	//
	// Transaction t = s.beginTransaction();
	//
	// String queryOntoQL = "INSERT INTO #class (#oid) VALUES (11111)";
	// OntoQLStatement statement = s.createOntoQLStatement();
	// int res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(1, res);
	//
	// t.rollback();
	//
	// s.close();
	// }

	/*
	 * belaidn - test the simple query
	 */
	// public void testUpdateClass() throws Exception {
	//
	// OntoQLSession s = openSession();
	// s.setReferenceLanguage(OntoQLHelper.ENGLISH);
	//
	// Transaction t = s.beginTransaction();
	//
	// String queryOntoQL;
	// OntoQLStatement statement;
	// int res;
	// OntoQLResultSet rs;
	// OntoQLResultSet rs2;
	//
	// queryOntoQL =
	// "CREATE #CLASS SuperReflector (DESCRIPTOR (#name[en]='SuperReflector')
	// PROPERTIES (name STRING));";
	// statement = s.createOntoQLStatement();
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(0, res);
	// queryOntoQL = "CREATE EXTENT OF SuperReflector (name)";
	// statement.executeUpdate(queryOntoQL);
	//
	// queryOntoQL =
	// "CREATE #Class SubReflector (DESCRIPTOR (#name[en]='SubReflector') PROPERTIES
	// (name STRING));";
	// statement = s.createOntoQLStatement();
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(0, res);
	// queryOntoQL = "CREATE EXTENT OF SubReflector (name)";
	// statement.executeUpdate(queryOntoQL);
	//
	// queryOntoQL =
	// "CREATE #Class Reflector (DESCRIPTOR (#name[en]='Reflector') PROPERTIES (name
	// STRING, other STRING, isUpperThan REF (SubReflector) ARRAY, isLowerThan REF
	// (SuperReflector)));";
	// statement = s.createOntoQLStatement();
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(0, res);
	// queryOntoQL =
	// "CREATE EXTENT OF Reflector (name,other,isUpperThan,isLowerThan)";
	// statement.executeUpdate(queryOntoQL);
	//
	// queryOntoQL = "INSERT INTO SuperReflector (name) VALUES ('super1')";
	// statement.executeUpdate(queryOntoQL);
	//
	// rs = statement.executeQuery("SELECT name FROM SuperReflector");
	// rs.next();
	// Assert.assertEquals("super1", rs.getString(1));
	//
	// queryOntoQL = "INSERT INTO SubReflector (name) VALUES ('sub1')";
	// statement.executeUpdate(queryOntoQL);
	// queryOntoQL = "INSERT INTO SubReflector (name) VALUES ('sub2')";
	// statement.executeUpdate(queryOntoQL);
	// queryOntoQL = "INSERT INTO SubReflector (name) VALUES ('sub3')";
	// statement.executeUpdate(queryOntoQL);
	//
	// queryOntoQL = "INSERT INTO Reflector (name) VALUES ('ref1')";
	// statement.executeUpdate(queryOntoQL);
	//
	// queryOntoQL = "UPDATE Reflector SET other = 'here' WHERE name='ref1'";
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(1, res);
	//
	// rs = statement
	// .executeQuery("SELECT SubReflector.oid FROM SubReflector WHERE
	// SubReflector.name = 'sub1' OR SubReflector.name = 'sub2'");
	// rs.next();
	//
	// // insert
	// queryOntoQL =
	// "UPDATE Reflector SET isLowerThan = (SELECT SuperReflector.oid FROM
	// SuperReflector WHERE SuperReflector.name ='super1') WHERE name='ref1'";
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(1, res);
	//
	// // check insertion
	// queryOntoQL = "SELECT isLowerThan FROM Reflector WHERE name = 'ref1'";
	// rs = statement.executeQuery(queryOntoQL);
	// rs.next();
	// queryOntoQL =
	// "SELECT SuperReflector.oid FROM SuperReflector WHERE SuperReflector.name
	// ='super1'";
	// rs2 = statement.executeQuery(queryOntoQL);
	// rs2.next();
	// Assert.assertEquals(rs.getInt(1), rs2.getInt(1));
	//
	// // insert
	// queryOntoQL =
	// "UPDATE Reflector SET isUpperThan = ARRAY(SELECT SubReflector.oid FROM
	// SubReflector WHERE SubReflector.name = 'sub1' OR SubReflector.name = 'sub2')
	// WHERE name='ref1'";
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(1, res);
	//
	// // check insertion
	// queryOntoQL =
	// "SELECT t.oid FROM Reflector r, unnest(r.isUpperThan) as t WHERE name =
	// 'ref1'";
	// rs = statement.executeQuery(queryOntoQL);
	// rs.next();
	// queryOntoQL =
	// "SELECT SubReflector.oid FROM SubReflector WHERE SubReflector.name = 'sub1'";
	// rs2 = statement.executeQuery(queryOntoQL);
	// rs2.next();
	// Assert.assertEquals(rs.getInt(1), rs2.getInt(1));
	//
	// // check insertion 2
	// queryOntoQL =
	// "SELECT t.name FROM Reflector r, unnest(r.isUpperThan) as t WHERE name =
	// 'ref1'";
	// rs = statement.executeQuery(queryOntoQL);
	// rs.next();
	// Assert.assertEquals(rs.getString(1), "sub1");
	//
	// // // do not work: select isUpperThan
	// // queryOntoQL = "SELECT isUpperThan FROM Reflector";
	// // rs = statement.executeQuery(queryOntoQL);
	// // System.out
	// //
	// .println("#################################################################");
	// // while (rs.next()) {
	// // System.out.println(rs.getInt(1));
	// // System.out
	// //
	// .println("isUpperThan#################################################################");
	// // }
	//
	// // print reflector
	// queryOntoQL = "SELECT oid, name, isLowerThan FROM Reflector";
	// rs = statement.executeQuery(queryOntoQL);
	// System.out
	// .println("#################################################################");
	// while (rs.next()) {
	// System.out.println(rs.getInt(1) + " _ " + rs.getString(2) + " _ "
	// + rs.getInt(3));
	// System.out
	// .println("Ref#################################################################");
	// }
	//
	// // print superreflector
	// queryOntoQL = "SELECT oid,name FROM SuperReflector";
	// rs = statement.executeQuery(queryOntoQL);
	// System.out
	// .println("#################################################################");
	// while (rs.next()) {
	// System.out.println(rs.getInt(1) + " _ " + rs.getString(2));
	// System.out
	// .println("Super#################################################################");
	// }
	//
	// // print subreflector
	// queryOntoQL = "SELECT oid,name FROM SubReflector";
	// rs = statement.executeQuery(queryOntoQL);
	// System.out
	// .println("#################################################################");
	// while (rs.next()) {
	// System.out.println(rs.getInt(1) + " _ " + rs.getString(2));
	// System.out
	// .println("Super#################################################################");
	// }
	//
	// // print reflector isupperthat
	// queryOntoQL =
	// "SELECT t.oid FROM Reflector r, unnest(r.isUpperThan) as t WHERE name =
	// 'ref1'";
	// rs = statement.executeQuery(queryOntoQL);
	// System.out
	// .println("#################################################################");
	// while (rs.next()) {
	// System.out.println(rs.getInt(1));
	// System.out
	// .println("AAA#################################################################");
	// }
	//
	// t.rollback();
	//
	// s.close();
	// }
}