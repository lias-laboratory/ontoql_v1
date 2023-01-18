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
import java.sql.Statement;
import java.util.List;
import java.util.Set;

import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.core.AbstractFactoryEntityDB;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeEnumerate;
import fr.ensma.lisi.ontoql.core.ontodb.FactoryEntityOntoDB;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.QuerySyntaxException;
import fr.ensma.lisi.ontoql.ontoapi.Instance;
import fr.ensma.lisi.ontoql.ontoapi.OntoProperty;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Stéphane JEAN
 */
public class OntoQLDMLTest extends OntoQLTestCase {

	@Test
	public void testWrongUpdate() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		OntoQLStatement statement = s.createOntoQLStatement();

		try {
			statement.executeUpdate(
					"Insert into HUDSON (Reference,Size,its_muff, virage) values ('300', 'XL', (SELECT m from CAGS m), 10)");
			Assert.fail();
		} catch (JOBDBCException oExc) {
			Assert.assertEquals("A subquery return more than one result for an association", oExc.getMessage());
		}

		try {
			statement.executeUpdate(
					"Insert into HUDSON (Reference,Size,its_muff, virage) values ('300', 'XL', (SELECT m from only(MUFFS) m where m.oid > 424), 10)");
			Assert.fail();
		} catch (JOBDBCException oExc) {
			Assert.assertEquals("A subquery must retrieve at least one result", oExc.getMessage());
		}

		try {
			statement.executeUpdate("Insert into !1031 (Size, its_hudsons) values ('XL',1)");
			Assert.fail();
		} catch (JOBDBCException oExc) {
			Assert.assertEquals(
					"Type of the property its_hudsons (ARRAY) and value 1 (INT) at position 2 are not compatible",
					oExc.getMessage());
		}

		// not enough values
		try {
			statement.executeUpdate("Insert into !1031 (its_hudsons, Reference) values ((SELECT h from HUDSON h))");
			Assert.fail();
		} catch (JOBDBCException oExc) {
			Assert.assertEquals(
					"Number of properties valued (2) doesn't match the number of values (1) in this insert statement",
					oExc.getMessage());
		}

		// too much values
		try {
			statement.executeUpdate("Insert into !1031 (its_hudsons) values ((SELECT h from HUDSON h),1)");
			Assert.fail();
		} catch (JOBDBCException oExc) {
			Assert.assertEquals(
					"Number of properties valued (1) doesn't match the number of values (2) in this insert statement",
					oExc.getMessage());
		}

		// incompatible type String-Int
		try {
			statement.executeUpdate("Insert into !1031 (Reference) values (1)");
			Assert.fail();
		} catch (JOBDBCException oExc) {
			Assert.assertEquals(
					"Type of the property Reference (STRING) and value 1 (INT) at position 1 are not compatible",
					oExc.getMessage());
		}

		// incompatible type Int-String
		try {
			statement.executeUpdate("Insert into !1031 (Reference) values (1)");
			Assert.fail();
		} catch (JOBDBCException oExc) {
			Assert.assertEquals(
					"Type of the property Reference (STRING) and value 1 (INT) at position 1 are not compatible",
					oExc.getMessage());
		}

		// Check that all properties are defined on the from clause
		try {
			statement.executeUpdate("Insert into !1031 (virage) values (1)");
			Assert.fail();
		} catch (JOBDBCException oExc) {
			Assert.assertEquals("The property 'virage' is not defined on the class 'FOC_BABY'", oExc.getMessage());
		}

		s.close();
	}

	/**
	 * Test if the extension are taken in the DML
	 */
	@Test
	public void testInsertInExtensionOfSchema() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		// a new entity without attribute
		Transaction t = s.beginTransaction();
		String queryOntoQL = "CREATE ENTITY #expression ()";
		OntoQLStatement statement = s.createOntoQLStatement();
		int res;
		// int res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		// queryOntoQL = "insert into #expression () values()";
		// res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(1, res);
		t.rollback();

		// 2 new entities with a relationship
		t = s.beginTransaction();
		queryOntoQL = "create entity #test (#oid int, #name String)";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "insert into #test (#name) values ('test')";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);
		queryOntoQL = "insert into #test (#name) values ('test2')";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);

		queryOntoQL = "CREATE ENTITY #expression (#oid INT, #label String, #usedTest REF(#test) ARRAY)";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "insert into #expression (#label, #usedTest) values('test',array(select #oid from #test) )";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);
		OntoQLResultSet resultSet = statement
				.executeQuery("SELECT test.#name from #expression e, unnest(e.#usedTest) as test");
		Assert.assertTrue(resultSet.next());
		Assert.assertEquals("test", resultSet.getString(1));
		Assert.assertTrue(resultSet.next());
		Assert.assertEquals("test2", resultSet.getString(1));
		Assert.assertFalse(resultSet.next());

		queryOntoQL = "drop entity #expression";
		res = statement.executeUpdate(queryOntoQL);
		queryOntoQL = "drop entity #test";
		res = statement.executeUpdate(queryOntoQL);

		t.rollback();

		// a new entity with a primitive attribute
		t = s.beginTransaction();
		queryOntoQL = "CREATE ENTITY #expression (#label String)";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "insert into #expression (#label) values('aLabel')";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);
		resultSet = statement.executeQuery("SELECT #label from #expression where #label = 'aLabel'");
		Assert.assertTrue(resultSet.next());
		queryOntoQL = "drop entity #expression";
		res = statement.executeUpdate(queryOntoQL);
		t.rollback();

		// a new entity with a primitive attribute
		t = s.beginTransaction();
		queryOntoQL = "CREATE ENTITY #expression (#oid INT, #description MULTILINGUAL STRING, #appliedOn REF(#class), #usedProperties REF(#property) ARRAY)";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "insert into #expression (#description[en], #description[fr]) values('an expression of class like union, intersection ...', 'une expression de classe telle que l''union, l''intersection ...' )";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);
		resultSet = statement
				.executeQuery("SELECT #description[en] from #expression where #description[fr] like 'une expression%'");
		Assert.assertTrue(resultSet.next());
		Assert.assertEquals("an expression of class like union, intersection ...", resultSet.getString(1));
		queryOntoQL = "drop entity #expression";
		res = statement.executeUpdate(queryOntoQL);
		t.rollback();

		// a new entity with a primitive attribute
		t = s.beginTransaction();
		queryOntoQL = "CREATE ENTITY #expression (#oid INT, #code String, #description MULTILINGUAL STRING, #appliedOn REF(#class), #usedProperties REF(#property) ARRAY)";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "insert into #expression (#code, #description[en], #description[fr], #appliedOn) values('BBBDR02', 'an expression of class like union, intersection ...', 'une expression de classe telle que l''union, l''intersection ...',(select #oid from #class where #name[en]='CAGS'))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);
		resultSet = statement.executeQuery(
				"SELECT #appliedOn.#name[fr] from #expression where #description[fr] like 'une expression%'");
		Assert.assertTrue(resultSet.next());
		Assert.assertEquals("COUPES - VENT", resultSet.getString(1));
		queryOntoQL = "drop entity #expression";
		res = statement.executeUpdate(queryOntoQL);
		t.rollback();

		// a new entity with a primitive attribute
		t = s.beginTransaction();
		queryOntoQL = "CREATE ENTITY #expression (#oid INT, #description MULTILINGUAL STRING, #appliedOn REF(#class), #usedProperties REF(#property) ARRAY)";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "insert into #expression (#description[en], #description[fr], #appliedOn, #usedProperties) values('an expression of class like union, intersection ...', 'une expression de classe telle que l''union, l''intersection ...',(select #oid from #class where #name[en]='CAGS'), array(select p.#oid from #class c, unnest(c.#properties) as p where c.#oid=1068) )";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);
		resultSet = statement.executeQuery(
				"SELECT prop.#name[fr] from #expression e, unnest(e.#usedProperties) as prop where #description[fr] like 'une expression%'");
		Assert.assertTrue(resultSet.next());
		Assert.assertEquals("Référence", resultSet.getString(1));
		Assert.assertTrue(resultSet.next());
		Assert.assertEquals("Taille", resultSet.getString(1));
		Assert.assertTrue(resultSet.next());
		Assert.assertEquals("URI", resultSet.getString(1));
		Assert.assertFalse(resultSet.next());
		queryOntoQL = "drop entity #expression";
		res = statement.executeUpdate(queryOntoQL);
		t.rollback();

		s.close();

	}

	@Test
	public void testUpdateArray() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);
		Transaction t = s.beginTransaction();

		String queryOntoQL = "insert into HUDSON (oid) values (102) ";
		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate(queryOntoQL);

		queryOntoQL = "update !1031 set its_hudsons = 102 || its_hudsons";
		statement = s.createOntoQLStatement();
		statement.executeUpdate(queryOntoQL);

		queryOntoQL = "select h.oid from !1031 as i, unnest(i.its_hudsons) as h";
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("100", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("101", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("102", resultSetObtenu.getString(1));

		queryOntoQL = "select its_hudsons from !1031";
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();

		t.rollback();
		s.close();
	}

	@Test
	public void testExecuteUpdate() throws Exception {
		// Test insert
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		// Insert Null in an array
		Transaction t = s.beginTransaction();
		OntoQLStatement statement = s.createOntoQLStatement();
		String queryOntoQL = "create #class Vehicle under CAGS ("
				+ "DESCRIPTOR (#name[fr]='Véhicule', #code='AFBDF54D', #version='002')"
				+ "properties (\"number of wheels\" int, number String))";
		int res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "create #class Person (" + "DESCRIPTOR (#name[fr]='Personne', #code='AXDFBDF54D', #version='001')"
				+ "properties (age int, its_cars REF(Vehicle) ARRAY))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "create extent of Person (age, its_cars)";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "insert into Person (age) values (1)";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);
		queryOntoQL = "select age, its_cars from Person";
		OntoQLResultSet resultSet = statement.executeQuery(queryOntoQL);
		resultSet.next();
		Assert.assertEquals("1", resultSet.getString(1));
		Assert.assertNull(resultSet.getString(2));
		queryOntoQL = "insert into Person (age,its_cars) values (1, null)";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);
		t.rollback();

		t = s.beginTransaction();
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery("SELECT count(*) from ONTARIO");
		resultSet.next();
		int nbrBeforeInsert = resultSet.getInt(1);
		int nbrInsert = statement.executeUpdate("INSERT INTO ONTARIO (Reference,Size) values ('300', 'XL')");
		Assert.assertEquals(nbrInsert, 1);
		resultSet = statement.executeQuery("SELECT count(*) from ONTARIO");
		resultSet.next();
		int nbrAfterInsert = resultSet.getInt(1);
		Assert.assertEquals(nbrBeforeInsert + 1, nbrAfterInsert);

		t.rollback();
		resultSet = statement.executeQuery("SELECT count(*) from ONTARIO");
		resultSet.next();
		int nbrAfterRollback = resultSet.getInt(1);
		Assert.assertEquals(nbrBeforeInsert, nbrAfterRollback);

		t = s.beginTransaction();
		statement = s.createOntoQLStatement();

		resultSet = statement.executeQuery("SELECT count(*) from \"FJORD (SEA PARKA)\"");
		resultSet.next();
		nbrBeforeInsert = resultSet.getInt(1);
		nbrInsert = statement.executeUpdate("Insert into \"FJORD (SEA PARKA)\" (Reference,Size) values ('300', 'XL')");
		Assert.assertEquals(nbrInsert, 1);
		resultSet = statement.executeQuery("SELECT count(*) from \"FJORD (SEA PARKA)\"");
		resultSet.next();
		nbrAfterInsert = resultSet.getInt(1);
		Assert.assertEquals(nbrBeforeInsert + 1, nbrAfterInsert);

		t.rollback();
		resultSet = statement.executeQuery("SELECT count(*) from \"FJORD (SEA PARKA)\"");
		resultSet.next();
		nbrAfterRollback = resultSet.getInt(1);
		Assert.assertEquals(nbrBeforeInsert, nbrAfterRollback);

		t = s.beginTransaction();
		statement = s.createOntoQLStatement();

		resultSet = statement.executeQuery("SELECT count(*) from HUDSON");
		resultSet.next();
		nbrBeforeInsert = resultSet.getInt(1);
		nbrInsert = statement.executeUpdate("Insert into HUDSON (Reference,Size, virage) values ('300', 'XL', 10)");
		Assert.assertEquals(nbrInsert, 1);
		resultSet = statement.executeQuery("SELECT count(*) from HUDSON");
		resultSet.next();
		nbrAfterInsert = resultSet.getInt(1);
		Assert.assertEquals(nbrBeforeInsert + 1, nbrAfterInsert);

		t.rollback();
		resultSet = statement.executeQuery("SELECT count(*) from HUDSON");
		resultSet.next();
		nbrAfterRollback = resultSet.getInt(1);
		Assert.assertEquals(nbrBeforeInsert, nbrAfterRollback);

		t = s.beginTransaction();
		statement = s.createOntoQLStatement();

		resultSet = statement.executeQuery("SELECT count(*) from HUDSON");
		resultSet.next();
		nbrBeforeInsert = resultSet.getInt(1);
		nbrInsert = statement.executeUpdate(
				"Insert into HUDSON (Reference,Size,its_muff, virage) values ('300', 'XL', (SELECT m.oid from ONLY(MUFFS) m where m.Reference like '%%' or m.Reference is null), 10)");
		Assert.assertEquals(nbrInsert, 1);
		resultSet = statement.executeQuery("SELECT count(*) from HUDSON");
		resultSet.next();
		nbrAfterInsert = resultSet.getInt(1);
		Assert.assertEquals(nbrBeforeInsert + 1, nbrAfterInsert);

		resultSet = statement.executeQuery("SELECT its_muff from HUDSON");
		resultSet.next();
		Instance instanceMuff = resultSet.getInstance(1);
		Assert.assertEquals(cMuffs, instanceMuff.getBaseType());

		t.rollback();
		resultSet = statement.executeQuery("SELECT count(*) from HUDSON");
		resultSet.next();
		nbrAfterRollback = resultSet.getInt(1);
		Assert.assertEquals(nbrBeforeInsert, nbrAfterRollback);

		t = s.beginTransaction();
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery("SELECT count(*) from HUDSON");
		resultSet.next();
		int nbrRow = resultSet.getInt(1);
		int nbrUpdated = statement.executeUpdate("Update only(HUDSON) set Reference = 'XL'");
		Assert.assertEquals(nbrRow, nbrUpdated);
		resultSet = statement.executeQuery("SELECT Reference from HUDSON");
		while (resultSet.next()) {
			Assert.assertTrue(resultSet.getString(1).equals("XL"));
		}
		t.rollback();

		// Using where clause
		t = s.beginTransaction();
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery("SELECT count(*) from only(\"FJORD (SEA PARKA)\") where Size = 'XL'");
		resultSet.next();
		nbrRow = resultSet.getInt(1);
		Assert.assertTrue(nbrRow > 0);
		nbrUpdated = statement
				.executeUpdate("UPDATE only(\"FJORD (SEA PARKA)\") SET Reference='300' where Size = 'XL'");
		Assert.assertEquals(nbrRow, nbrUpdated);
		resultSet = statement.executeQuery("SELECT Reference from \"FJORD (SEA PARKA)\" where Size='XL'");
		while (resultSet.next()) {
			Assert.assertTrue(resultSet.getString(1).equals("300"));
		}
		t.rollback();

		// two SET
		t = s.beginTransaction();
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery("SELECT count(*) from HUDSON where Size = 'XL'");
		resultSet.next();
		nbrRow = resultSet.getInt(1);
		nbrUpdated = statement.executeUpdate("UPDATE only(HUDSON) set Reference='300', virage=10 where Size = 'XL'");
		Assert.assertEquals(nbrRow, nbrUpdated);
		resultSet = statement.executeQuery("SELECT Reference,virage from HUDSON where Size = 'XL'");
		while (resultSet.next()) {
			Assert.assertTrue(resultSet.getString(1).equals("300"));
			Assert.assertEquals(resultSet.getInt(2), 10);
		}
		t.rollback();

		t = s.beginTransaction();
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery("SELECT count(*) from HUDSON WHERE Reference = '300061'");
		resultSet.next();
		nbrRow = resultSet.getInt(1);
		nbrUpdated = statement.executeUpdate(
				"UPDATE only(HUDSON) SET its_muff = (SELECT m from \"NEOPRENE MUFFS, HOOK AND LOOP CLOSURE\" m where m.Reference = '389000') WHERE Reference = '300061'");
		Assert.assertEquals(nbrRow, nbrUpdated);
		resultSet = statement.executeQuery("SELECT its_muff from only(HUDSON) WHERE Reference = '300061'");
		resultSet.next();
		instanceMuff = resultSet.getInstance(1);
		Assert.assertEquals("154", instanceMuff.getOid());
		Assert.assertEquals("NEOPRENE MUFFS, HOOK AND LOOP CLOSURE", instanceMuff.getBaseType().getName());

		t.rollback();

		// DELETE
		t = s.beginTransaction();
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery("SELECT count(*) from only(ONTARIO)");
		resultSet.next();
		nbrRow = resultSet.getInt(1);
		int nbrDeleted = statement.executeUpdate("DELETE FROM only(ONTARIO)");
		Assert.assertEquals(nbrRow, nbrDeleted);
		resultSet = statement.executeQuery("SELECT count(*) from only(ONTARIO)");
		resultSet.next();
		int nbrRowAfterDelete = resultSet.getInt(1);
		Assert.assertEquals(0, nbrRowAfterDelete);
		Assert.assertEquals(nbrRow, nbrDeleted);
		t.rollback();

		// With a Where clause
		t = s.beginTransaction();
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery("SELECT count(*) from only(\"FJORD (SEA PARKA)\") where Size = 'XL'");
		resultSet.next();
		nbrRow = resultSet.getInt(1);
		nbrDeleted = statement.executeUpdate("DELETE FROM only(\"FJORD (SEA PARKA)\") where Size = 'XL'");
		Assert.assertEquals(nbrRow, nbrUpdated);
		resultSet = statement.executeQuery("SELECT count(*) from only(\"FJORD (SEA PARKA)\") where Size = 'XL'");
		resultSet.next();
		nbrRowAfterDelete = resultSet.getInt(1);
		Assert.assertEquals(nbrRow - nbrDeleted, nbrRowAfterDelete);
		Assert.assertEquals(nbrRow, nbrDeleted);
		t.rollback();

		// With a SubQuery
		t = s.beginTransaction();
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery("SELECT count(*) from only(HUDSON)");
		resultSet.next();
		nbrRow = resultSet.getInt(1);
		nbrDeleted = statement.executeUpdate(
				"DELETE FROM only(HUDSON) where its_muff in (select m from MUFFS m where m.Reference = '%%' or m.Reference is null)");
		Assert.assertEquals(nbrRow, nbrDeleted);
		resultSet = statement.executeQuery("SELECT count(*) from only(HUDSON)");
		resultSet.next();
		nbrRowAfterDelete = resultSet.getInt(1);
		Assert.assertEquals(nbrRow - nbrDeleted, nbrRowAfterDelete);
		Assert.assertEquals(nbrRow, nbrDeleted);
		t.rollback();

		// With a SubQuery returning a collection of association
		t = s.beginTransaction();
		statement = s.createOntoQLStatement();

		resultSet = statement.executeQuery("SELECT count(*) from only(!1031)");
		resultSet.next();
		nbrBeforeInsert = resultSet.getInt(1);
		nbrInsert = statement.executeUpdate(
				"Insert into !1031 (Reference,Size,its_hudsons) values ('300', 'XL', ARRAY(SELECT h.oid from HUDSON h))");
		Assert.assertEquals(nbrInsert, 1);
		resultSet = statement.executeQuery("SELECT count(*) from only(!1031)");
		resultSet.next();
		nbrAfterInsert = resultSet.getInt(1);
		Assert.assertEquals(nbrBeforeInsert + 1, nbrAfterInsert);

		resultSet = statement.executeQuery("SELECT its_hudsons from only(!1031) where Reference = '300'");
		resultSet.next();
		Instance[] instancesHudson = (Instance[]) resultSet.getCollection(1);
		Assert.assertEquals(instancesHudson.length, 2);
		Instance hudson = instancesHudson[0];
		Assert.assertEquals(hudson.getOid(), "100");
		hudson = instancesHudson[1];
		Assert.assertEquals(hudson.getOid(), "101");

		t.rollback();

		// specifying a collection of association
		// for the moment update doesn't update the table column
		// so this test is not successful
		// t = s.beginTransaction();
		// statement = s.createOntoQLStatement();
		//
		// resultSet = statement.executeQuery("SELECT count(*) from
		// only(!1031)");
		// resultSet.next();
		// nbrBeforeInsert = resultSet.getInt(1);
		// nbrInsert = statement
		// .executeUpdate("Insert into !1031 (Reference,Size,its_hudsons) values
		// ('300', 'XL', ARRAY[100])");
		// Assert.assertEquals(nbrInsert, 1);
		// nbrInsert = statement
		// .executeUpdate("update !1031 set its_hudsons = ARRAY[100, 101] where
		// Reference = 300 and Size = 'XL'");
		// Assert.assertEquals(nbrInsert, 1);
		// resultSet = statement.executeQuery("SELECT count(*) from
		// only(!1031)");
		// resultSet.next();
		// nbrAfterInsert = resultSet.getInt(1);
		// Assert.assertEquals(nbrBeforeInsert + 1, nbrAfterInsert);
		//
		// resultSet = statement
		// .executeQuery("SELECT its_hudsons from only(!1031) where Reference =
		// '300'");
		// resultSet.next();
		// instancesHudson = (Instance[]) resultSet.getCollection(1);
		// Assert.assertEquals(instancesHudson.length, 2);
		// hudson = instancesHudson[0];
		// Assert.assertEquals(hudson.getOid(), "100");
		// Assert.assertEquals(hudson.getBaseType().getName(), "HUDSON");
		// hudson = instancesHudson[1];
		// Assert.assertEquals(hudson.getOid(), "101");
		// t.rollback();

		// with a SubQuery returning a collection of string
		t = s.beginTransaction();
		statement = s.createOntoQLStatement();

		resultSet = statement.executeQuery("SELECT count(*) from !1031");
		resultSet.next();
		nbrBeforeInsert = resultSet.getInt(1);
		nbrInsert = statement.executeUpdate(
				"Insert into !1031 (Reference,Size, names) values ('300', 'XL', ARRAY['baby1','baby2'])");
		Assert.assertEquals(nbrInsert, 1);
		resultSet = statement.executeQuery("SELECT count(*) from !1031");
		resultSet.next();
		nbrAfterInsert = resultSet.getInt(1);
		Assert.assertEquals(nbrBeforeInsert + 1, nbrAfterInsert);

		resultSet = statement.executeQuery("SELECT names from !1031 where Reference = '300'");
		resultSet.next();
		String[] names = (String[]) resultSet.getCollection(1);
		Assert.assertEquals(names.length, 2);
		String name = names[0];
		Assert.assertEquals(name, "baby1");
		name = names[1];
		Assert.assertEquals(name, "baby2");

		t.rollback();

		getSession().close();
	}

	/**
	 * Test the DML on the ontology part.
	 */
	@Test
	public void testExecuteUpdateOnOntologies() throws Exception {
		// Test insert of a new class in the ontology
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		Transaction t = s.beginTransaction();
		OntoQLStatement statement = s.createOntoQLStatement();
		int nbrInsert = statement.executeUpdate("INSERT INTO #ontology (#namespace) values ('http://test')");
		Assert.assertEquals(nbrInsert, 1);
		OntoQLResultSet resultSet = statement
				.executeQuery("SELECT #namespace from #ontology where #namespace = 'http://test'");
		Assert.assertTrue(resultSet.next());
		t.rollback();

		t = s.beginTransaction();
		statement = s.createOntoQLStatement();
		nbrInsert = statement.executeUpdate("INSERT INTO #class (#code) values ('BB3DFD4')");
		Assert.assertEquals(nbrInsert, 1);
		resultSet = statement.executeQuery("SELECT #oid from #class where #code = 'BB3DFD4'");
		Assert.assertTrue(resultSet.next());
		t.rollback();

		t = s.beginTransaction();
		statement = s.createOntoQLStatement();
		nbrInsert = statement.executeUpdate("INSERT INTO #class (#code, #version) values ('BB3DFD4', '001')");
		Assert.assertEquals(nbrInsert, 1);
		resultSet = statement.executeQuery("SELECT #oid from #class where #code = 'BB3DFD4' and #version='001'");
		Assert.assertTrue(resultSet.next());
		t.rollback();

		t = s.beginTransaction();
		statement = s.createOntoQLStatement();
		nbrInsert = statement.executeUpdate(
				"INSERT INTO #class (#code, #version, #definition[en]) values ('BB3DFD4', '001', 'Toto')");
		Assert.assertEquals(nbrInsert, 1);
		resultSet = statement.executeQuery("SELECT #oid from #class where #definition[en] like '%Toto%'");
		Assert.assertTrue(resultSet.next());
		Assert.assertFalse(resultSet.next());
		t.rollback();

		t = s.beginTransaction();
		statement = s.createOntoQLStatement();
		nbrInsert = statement.executeUpdate(
				"INSERT INTO #class (#code, #version, #definition[en], #definition[fr]) values ('BB3DFD4', '001', 'a human being', 'condition humaine')");
		Assert.assertEquals(nbrInsert, 1);
		resultSet = statement.executeQuery(
				"SELECT #code, #version from #class where #definition[en] like '%a human be%' and #definition[fr] like '%ondition hum%' ");
		Assert.assertTrue(resultSet.next());
		Assert.assertEquals("BB3DFD4", resultSet.getString(1));
		Assert.assertEquals("001", resultSet.getString(2));
		Assert.assertFalse(resultSet.next());
		t.rollback();

		t = s.beginTransaction();
		statement = s.createOntoQLStatement();
		nbrInsert = statement.executeUpdate(
				"INSERT INTO #class (#code, #version, #definition[en], #definition[fr], #remark[en], #remark[fr], #note[en], #note[fr]) values ('BB3DFD4', '001', 'a human being', 'condition humaine', 'a remark', 'une remarque', 'a note', 'une note')");
		Assert.assertEquals(nbrInsert, 1);
		resultSet = statement.executeQuery(
				"SELECT #code, #version from #class where #note[en] like '%a note%' and #remark[fr] like '%remarque%' ");
		Assert.assertTrue(resultSet.next());
		Assert.assertEquals("BB3DFD4", resultSet.getString(1));
		Assert.assertEquals("001", resultSet.getString(2));
		Assert.assertFalse(resultSet.next());
		t.rollback();

		t = s.beginTransaction();
		statement = s.createOntoQLStatement();
		nbrInsert = statement.executeUpdate(
				"INSERT INTO #class (#code, #version, #name[en], #name[fr]) values ('BB3DFD4', '001', 'Toto', 'lobo')");
		Assert.assertEquals(nbrInsert, 1);
		resultSet = statement.executeQuery("SELECT #oid from #class where #name[fr] = 'lobo' and #name[en] = 'Toto'");
		Assert.assertTrue(resultSet.next());
		t.rollback();

		getSession().close();
	}

	@Test
	public void testDMLWithStringCode() throws SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		s.setDefaultNameSpace("http://lisi.ensma.fr/");
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate(
				"CREATE #CLASS CFCA (DESCRIPTOR (#code ='0002-41982799300025#01-1#1', #definition[fr]='Famille racine de la classification des composants utilisés par CFCA')))");
		statement.executeUpdate(
				"ALTER #CLASS CFCA ADD filmProtecteur String DESCRIPTOR (#code = '0002-41982799300025#02-14#1', #definition[fr] = 'Booléen, qui, si vrai, indique si un film protecteur est présent sur le conducteur')");
		statement.executeUpdate("CREATE EXTENT OF CFCA (filmProtecteur)");

		try {
			statement.executeUpdate("insert into CFCA (filmProtecteur) values ('true')");
		} catch (NumberFormatException e) {
			e.printStackTrace();
			Assert.fail("Don't throw this exception");
		}

		t.rollback();
		getSession().close();
	}

	@Test
	public void testDMLUpdateWithSyntaxError() throws SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		s.setDefaultNameSpace("http://lisi.ensma.fr/");
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate("CREATE #CLASS SampleClass (DESCRIPTOR (#code ='0001'))");
		statement.executeUpdate("ALTER #CLASS SampleClass ADD Property1 String DESCRIPTOR (#code = '0002')");
		statement.executeUpdate("ALTER #CLASS SampleClass ADD Property2 String DESCRIPTOR (#code = '0003')");
		statement.executeUpdate("ALTER #CLASS SampleClass ADD Property3 String DESCRIPTOR (#code = '0004')");
		statement.executeUpdate("ALTER #CLASS SampleClass ADD Property4 String DESCRIPTOR (#code = '0005')");
		statement.executeUpdate("ALTER #CLASS SampleClass ADD Property5 String DESCRIPTOR (#code = '0006')");
		statement.executeUpdate("ALTER #CLASS SampleClass ADD Property6 String DESCRIPTOR (#code = '0007')");
		statement.executeUpdate("ALTER #CLASS SampleClass ADD Property7 String DESCRIPTOR (#code = '0008')");
		statement.executeUpdate("ALTER #CLASS SampleClass ADD Property8 String DESCRIPTOR (#code = '0009')");
		statement.executeUpdate("ALTER #CLASS SampleClass ADD Property9 String DESCRIPTOR (#code = '0010')");
		statement.executeUpdate("ALTER #CLASS SampleClass ADD Property10 String DESCRIPTOR (#code = '0011')");
		statement.executeUpdate("ALTER #CLASS SampleClass ADD Property11 String DESCRIPTOR (#code = '0012')");
		statement.executeUpdate("ALTER #CLASS SampleClass ADD Property12 String DESCRIPTOR (#code = '0013')");
		statement.executeUpdate(
				"CREATE EXTENT OF SampleClass (Property1, Property2, Property3, Property4, Property5,Property6,Property7,Property8,Property9,Property10,Property11,Property12)");

		statement.executeUpdate(
				"insert into SampleClass (Property1, Property2, Property3) values ('test1','test2','test3')");

		OntoQLResultSet rset = statement.executeQuery("select oid from SampleClass");
		Assert.assertTrue(rset.next());
		String oid = rset.getString(1);

		rset = statement.executeQuery("select #oid from #Class where #name='SampleClass'");
		Assert.assertTrue(rset.next());
		String oidClass = rset.getString(1);
		System.out.println(oidClass);

		String query = "UPDATE !" + oidClass + " SET \"Property1\"=null, \"Property2\"='test2', "
				+ "\"Property3\"='test3', \"Property4\"='test4', \"Property5\"='test5', "
				+ "\"Property6\"=null, \"Property7\"=null, \"Property8\"=null, \"Property9\"=null, \"Property10\"=,\"Property11\"=,\"Property12\"= WHERE oid = "
				+ oid;
		try {
			statement.executeUpdate(query);
			Assert.fail();
		} catch (QuerySyntaxException e) {
		}

		t.rollback();
		getSession().close();
	}

	@Test
	public void testDMLWithBooleanTypeProperties() throws SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		s.setDefaultNameSpace("http://lisi.ensma.fr/");
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate(
				"CREATE #CLASS CFCA (DESCRIPTOR (#code ='0002', #definition[fr]='Famille racine de la classification des composants utilisés par CFCA')))");
		statement.executeUpdate(
				"ALTER #CLASS CFCA ADD filmProtecteur Boolean DESCRIPTOR (#code = '0002', #definition[fr] = 'Booléen, qui, si vrai, indique si un film protecteur est présent sur le conducteur')");
		statement.executeUpdate("CREATE EXTENT OF CFCA (filmProtecteur)");

		statement.executeUpdate("insert into CFCA (filmProtecteur) values (true)");
		statement.executeUpdate("insert into CFCA (filmProtecteur) values (false)");
		OntoQLResultSet rset = statement.executeQuery("select * from CFCA");

		int columnSize = rset.getMetaData().getColumnCount();
		Assert.assertEquals(2, columnSize);
		Assert.assertTrue(rset.next());
		Assert.assertEquals(true, rset.getBoolean(2));
		Assert.assertTrue(rset.next());
		Assert.assertEquals(false, rset.getBoolean(2));
		Assert.assertFalse(rset.next());

		rset = statement.executeQuery("select * from CFCA c where c.filmProtecteur = true");
		columnSize = rset.getMetaData().getColumnCount();
		Assert.assertEquals(2, columnSize);
		Assert.assertTrue(rset.next());
		Assert.assertEquals(true, rset.getBoolean(2));
		int oid = rset.getInt(1);
		Assert.assertFalse(rset.next());

		rset = statement.executeQuery("select * from CFCA c where c.filmProtecteur = true and c.oid = " + oid);
		columnSize = rset.getMetaData().getColumnCount();
		Assert.assertEquals(2, columnSize);
		Assert.assertTrue(rset.next());
		Assert.assertEquals(true, rset.getBoolean(2));

		t.rollback();
		getSession().close();
	}

	@Test
	public void testDMLWithCountTypeProperties() throws SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		s.setDefaultNameSpace("http://lisi.ensma.fr/");
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate("CREATE ENTITY #count_Type under #intType");
		statement.executeUpdate(
				"CREATE #CLASS CFCA (DESCRIPTOR (#code ='0002', #definition[fr]='Famille racine de la classification des composants utilisés par CFCA')))");

		statement.executeUpdate(
				"ALTER #CLASS CFCA ADD Désignation String DESCRIPTOR (#code = '0002-41982799300025#02-3#1', #definition[fr] = 'Désignation')");
		statement.executeUpdate(
				"ALTER #CLASS CFCA ADD \"Référence BE\" CountType DESCRIPTOR (#code = '0002-41982799300025#02-9#1', #definition[fr] = 'Clé')");
		statement.executeUpdate("CREATE EXTENT OF CFCA (Désignation, \"Référence BE\")");

		Assert.assertEquals(1,
				statement.executeUpdate("insert into CFCA (Désignation) values ('http://www.google.fr')"));
		OntoQLResultSet executeQuery = statement.executeQuery("select Désignation, \"Référence BE\" from CFCA");

		Assert.assertTrue(executeQuery.next());
		Assert.assertEquals(2, executeQuery.getMetaData().getColumnCount());
		Assert.assertEquals("http://www.google.fr", executeQuery.getString(1));
		Assert.assertEquals(1, executeQuery.getInt(2));

		Assert.assertEquals(1, statement
				.executeUpdate("insert into CFCA (Désignation, \"Référence BE\") values ('http://www.google.fr',8)"));
		executeQuery = statement.executeQuery("select Désignation, \"Référence BE\" from CFCA");
		Assert.assertTrue(executeQuery.next());
		Assert.assertEquals(2, executeQuery.getMetaData().getColumnCount());
		Assert.assertEquals("http://www.google.fr", executeQuery.getString(1));
		Assert.assertEquals(1, executeQuery.getInt(2));

		Assert.assertTrue(executeQuery.next());
		Assert.assertEquals(2, executeQuery.getMetaData().getColumnCount());
		Assert.assertEquals("http://www.google.fr", executeQuery.getString(1));
		Assert.assertEquals(8, executeQuery.getInt(2));

		statement.executeUpdate("DROP ENTITY #count_Type");
		t.rollback();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDMLWithUriTypeProperties() throws SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		s.setDefaultNameSpace("http://lisi.ensma.fr/");
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate("CREATE ENTITY #uri_Type under #stringType");

		statement.executeUpdate(
				"CREATE #CLASS CFCA (DESCRIPTOR (#code ='0002', #definition[fr]='Famille racine de la classification des composants utilisés par CFCA')))");
		statement.executeUpdate(
				"ALTER #CLASS CFCA ADD \"Fiche technique\" UriType DESCRIPTOR (#code = '0002', #definition[fr] = 'Référence externe à une description technique du composant')");
		statement.executeUpdate("CREATE EXTENT OF CFCA (\"Fiche technique\")");

		Assert.assertEquals(1,
				statement.executeUpdate("insert into CFCA (\"Fiche technique\") values ('http://www.google.fr')"));
		OntoQLResultSet executeQuery = statement.executeQuery("select \"Fiche technique\" from CFCA");

		Assert.assertTrue(executeQuery.next());
		Assert.assertEquals(1, executeQuery.getMetaData().getColumnCount());
		Assert.assertEquals("http://www.google.fr", executeQuery.getString(1));

		executeQuery = statement.executeQuery(
				"select \"Fiche technique\" from CFCA c where c.\"Fiche technique\" = 'http://www.google.fr'");
		Assert.assertTrue(executeQuery.next());
		Assert.assertEquals(1, executeQuery.getMetaData().getColumnCount());
		Assert.assertEquals("http://www.google.fr", executeQuery.getString(1));

		executeQuery = statement.executeQuery("select #oid from #class c where c.#name = 'CFCA'");
		Assert.assertTrue(executeQuery.next());

		executeQuery = statement
				.executeQuery("select #properties from #class where #oid = " + executeQuery.getString(1));
		Assert.assertTrue(executeQuery.next());
		Assert.assertNotNull(executeQuery.getSet(1));
		Set<OntoProperty> propertiesSet = (Set<OntoProperty>) executeQuery.getSet(1);

		String query = "select " + "case " + "when d IS OF (#intMeasureType) then 1 "
				+ "when d IS OF (#realMeasureType) then 2 " + "when d IS OF (#intType) then 1 "
				+ "when d IS OF (#realType) then 2 " + "when d IS OF (#booleanType) then 3 "
				+ "when d IS OF (#uriType) then 4 " + "when d IS OF (#stringType) then 5 "
				+ "when d IS OF (#collectionType) then 6 " + "when d IS OF (#reftype) then 7 "
				+ "when d IS OF (#primitiveType) then 8 " + "when d IS OF (#numberType) then 9 "
				+ "when d IS OF (#dataType) then 10 " + "else 0 end," + "p.#range "
				+ "from #Property AS p, #dataType AS d " + "where p.#oid='" + propertiesSet.iterator().next().getOid()
				+ "' " + "and p.#range=d.#oid";
		executeQuery = statement.executeQuery(query);
		Assert.assertTrue(executeQuery.next());
		Assert.assertEquals(4, executeQuery.getInt(1));

		statement.executeUpdate("DROP ENTITY #uri_Type");

		t.rollback();
	}

	@Test
	public void testDMLWithRealTypeProperties() throws SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		s.setDefaultNameSpace("http://lisi.ensma.fr/");
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate(
				"CREATE #CLASS CFCA (DESCRIPTOR (#code ='0002', #definition[fr]='Famille racine de la classification des composants utilisés par CFCA')))");
		statement.executeUpdate(
				"ALTER #CLASS CFCA ADD \"Température Min\" Real DESCRIPTOR (#code = '0002', #definition[fr] = 'Température minimum d''utilisation (degré Celcius)')");
		statement.executeUpdate("CREATE EXTENT OF CFCA (\"Température Min\")");

		statement.executeUpdate("insert into CFCA (\"Température Min\") values (1.2)");
		statement.executeUpdate("insert into CFCA (\"Température Min\") values (1.3)");
		OntoQLResultSet rset = statement.executeQuery("select * from CFCA");

		int columnSize = rset.getMetaData().getColumnCount();
		Assert.assertEquals(2, columnSize);
		Assert.assertTrue(rset.next());
		Assert.assertEquals(1.2, rset.getFloat(2), 1);
		Assert.assertTrue(rset.next());
		Assert.assertEquals(1.3, rset.getFloat(2), 1);
		Assert.assertFalse(rset.next());

		rset = statement.executeQuery("select * from CFCA c where c.\"Température Min\" = 1.2");
		columnSize = rset.getMetaData().getColumnCount();
		Assert.assertEquals(2, columnSize);
		Assert.assertTrue(rset.next());
		Assert.assertEquals(1.2, rset.getFloat(2), 1);
		int oid = rset.getInt(1);
		Assert.assertFalse(rset.next());

		rset = statement.executeQuery("select * from CFCA c where c.\"Température Min\" = 1.2 and c.oid = " + oid);
		columnSize = rset.getMetaData().getColumnCount();
		Assert.assertEquals(2, columnSize);
		Assert.assertTrue(rset.next());
		Assert.assertEquals(1.2, rset.getFloat(2), 1);

		t.rollback();
		getSession().close();
	}

	@Test
	public void testDMLWithEnumTypeProperties() throws SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		s.setDefaultNameSpace("http://lisi.ensma.fr/");
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate("CREATE #CLASS CFCA (PROPERTIES (color ENUM ('NOIR','ROUGE','BLANC')))");

		@SuppressWarnings("deprecation")
		Statement stmtSQL = s.connection().createStatement();
		ResultSet rst = stmtSQL.executeQuery(
				"select value from value_code_type_t where value IN ('NOIR','ROUGE','BLANC') order by value ASC");
		Assert.assertTrue(rst.next());
		Assert.assertEquals(rst.getString(1), "BLANC");
		Assert.assertTrue(rst.next());
		Assert.assertEquals(rst.getString(1), "NOIR");
		Assert.assertTrue(rst.next());
		Assert.assertEquals(rst.getString(1), "ROUGE");
		Assert.assertFalse(rst.next());

		StringBuffer query = new StringBuffer();
		query.append("SELECT nqcttod.rid_s");
		query.append(" FROM value_code_type_t as v,");
		query.append("dic_value_2_value_code as dtov,");
		query.append("value_domain_2_its_values as vdtov,");
		query.append("non_quantitative_code_type_2_domain as nqcttod ");
		query.append(" WHERE v.rid = dtov.rid_d and ");
		query.append(" dtov.rid_s = vdtov.rid_d and ");
		query.append(" vdtov.rid_s = nqcttod.rid_d and ");
		query.append(" value = 'BLANC'");
		rst = stmtSQL.executeQuery(query.toString());
		Assert.assertTrue(rst.next());

		OntoQLResultSet ontoqlRst = statement
				.executeQuery("select p.#oid from #Property p where p.#name[fr] = 'color'");
		Assert.assertTrue(ontoqlRst.next());

		ontoqlRst = statement.executeQuery("select " + "case " + "when d IS OF (#enumType) then 1 " + "else 0 end,"
				+ "p.#range " + "from #Property AS p, #dataType AS d " + "where p.#oid='" + ontoqlRst.getString(1)
				+ "' " + "and p.#range=d.#oid");
		Assert.assertTrue(ontoqlRst.next());
		Assert.assertEquals(1, ontoqlRst.getInt(1));

		AbstractFactoryEntityDB factoryDB = new FactoryEntityOntoDB(s);
		EntityDatatypeEnumerate dt = (EntityDatatypeEnumerate) factoryDB
				.createEntityDatatype(EntityDatatype.ENNUMERATE_NAME);
		dt.setInternalId(ontoqlRst.getString(2));

		List<String> currentEnums = dt.getValues();
		Assert.assertNotNull(currentEnums);
		Assert.assertEquals(3, currentEnums.size());

		dt.addValue("GRIS");
		currentEnums = dt.getValues();
		Assert.assertNotNull(currentEnums);
		Assert.assertEquals(4, currentEnums.size());

		Assert.assertTrue(currentEnums.contains("GRIS"));
		Assert.assertTrue(currentEnums.contains("BLANC"));
		Assert.assertTrue(currentEnums.contains("NOIR"));
		Assert.assertTrue(currentEnums.contains("ROUGE"));

		statement.executeUpdate(
				"ALTER #CLASS CFCA ADD Genre ENUM ('mâle', 'femelle', 'hermaphrodite') DESCRIPTOR (#code = '0002-41982799300025#02-101#1',#definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS CFCA ADD Forme ENUM ('Clip Drapeau', 'Clip','Douille', 'Languette', 'Broche', 'Batterie', 'Tubulaire', 'Douille coudée', 'Ronde', 'Préisolé', 'Fourche', 'Roulé Brasé din 46234','NFC 20-130', 'DIN 46225', 'Etroite', 'Ferroviaire NFF00363', 'DIN 46235','Embout', 'boutAbout', 'Manchon', 'Splice', 'Harpon', 'Languette Préisolé','Lire', 'Clip Drapeau Préisolé', 'CLIP RETOUR LANGUETTE', 'Embout sans isolant','Clip Drapeaux', 'Douille Préisolé', 'Broche présiolé') DESCRIPTOR (#code = '0002-41982799300025#02-102#1',#definition[fr] = '')");

		t.rollback();
	}

	@Test
	public void testDMLWithReferenceProperties() throws SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);
		s.setDefaultNameSpace("http://lisi.ensma.fr/");
		Transaction t = s.beginTransaction();
		OntoQLStatement statement = s.createOntoQLStatement();

		statement.executeUpdate("create #class Vehicule (properties (name String))");
		statement.executeUpdate("create #class Motorcycle under Vehicule (properties (number String))");
		statement.executeUpdate("create #class Person (properties (mainVehicule REF(Vehicule)))");

		Assert.assertEquals(0, statement.executeUpdate("create extent of Vehicule (name)"));
		Assert.assertEquals(1, statement.executeUpdate("insert into Vehicule (name) values ('Lada')"));
		Assert.assertEquals(0, statement.executeUpdate("create extent of Motorcycle (number)"));
		Assert.assertEquals(0, statement.executeUpdate("create extent of Person (mainVehicule)"));
		Assert.assertEquals(1,
				statement.executeUpdate("insert into Person (mainVehicule) values ((SELECT v.oid From Vehicule v))"));
		Assert.assertEquals(1, statement.executeUpdate("insert into Motorcycle (number) values ('5255TH16C')"));

		OntoQLResultSet rs = statement.executeQuery("select p.oid from Motorcycle p where p.number like '%5255TH16C%'");
		Assert.assertTrue(rs.next());
		String value = rs.getString(1);

		rs = statement.executeQuery("select oid from Person");
		Assert.assertTrue(rs.next());
		String oidPerson = rs.getString(1);
		Assert.assertEquals(1,
				statement.executeUpdate("UPDATE Person SET mainVehicule = " + value + " WHERE oid=" + oidPerson));

		rs = statement.executeQuery("select p.mainVehicule from Person p");
		Assert.assertTrue(rs.next());

		Assert.assertEquals(1, statement.executeUpdate("UPDATE Person SET mainVehicule = null WHERE oid=" + oidPerson));

		rs = statement.executeQuery("select p.mainVehicule from Person p");
		Assert.assertTrue(rs.next());
		Assert.assertNull(rs.getObject(1));

		t.rollback();
	}

	@Test
	public void testDMLWithAggregateReferenceProperties() throws SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);
		s.setDefaultNameSpace("http://lisi.ensma.fr/");
		Transaction t = s.beginTransaction();
		OntoQLStatement statement = s.createOntoQLStatement();

		statement.executeUpdate("create #class Vehicule (properties (name String))");
		statement.executeUpdate("create #class Motorcycle under Vehicule (properties (number String))");
		statement.executeUpdate("create #class Person (properties (its_cars REF(Vehicule) ARRAY))");

		Assert.assertEquals(0, statement.executeUpdate("create extent of Vehicule (name)"));
		Assert.assertEquals(1, statement.executeUpdate("insert into Vehicule (name) values ('Lada')"));
		Assert.assertEquals(0, statement.executeUpdate("create extent of Motorcycle (number)"));
		Assert.assertEquals(1, statement.executeUpdate("insert into Motorcycle (number) values ('5255TH16A')"));
		Assert.assertEquals(1, statement.executeUpdate("insert into Motorcycle (number) values ('5255TH16B')"));
		Assert.assertEquals(0, statement.executeUpdate("create extent of Person (its_cars)"));
		Assert.assertEquals(1,
				statement.executeUpdate("insert into Person (its_cars) values (ARRAY(SELECT v.oid From Vehicule v))"));
		Assert.assertEquals(1, statement.executeUpdate("insert into Motorcycle (number) values ('5255TH16C')"));

		OntoQLResultSet rs = statement.executeQuery("select p.oid from Vehicule p");
		String values = "";
		while (rs.next()) {
			values += rs.getString(1) + ",";
		}
		values = values.substring(0, values.length() - 1);

		rs = statement.executeQuery("select oid from Person");
		Assert.assertTrue(rs.next());
		String oidPerson = rs.getString(1);
		Assert.assertEquals(1,
				statement.executeUpdate("UPDATE Person SET its_cars = ARRAY[" + values + "] WHERE oid=" + oidPerson));

		rs = statement.executeQuery("select p.its_cars from Person p");
		Assert.assertTrue(rs.next());
		Instance[] instances = (Instance[]) rs.getCollection(1);
		Assert.assertEquals(4, instances.length);

		t.rollback();
	}
}