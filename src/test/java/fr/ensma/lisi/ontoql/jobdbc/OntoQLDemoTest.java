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
import java.util.Set;

import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.exception.QueryException;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Stéphane JEAN
 */
public class OntoQLDemoTest extends OntoQLTestCase {

	@Test
	public void testDemoODBASE() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);
		Transaction t = s.beginTransaction();

		// Create the class address
		StringBuffer queryOntoQL = new StringBuffer();
		queryOntoQL.append("CREATE #CLASS Address (");
		queryOntoQL.append("DESCRIPTOR (");
		queryOntoQL.append("#code= 'Class_Address', ");
		queryOntoQL.append("#name[fr] ='Adresse')");
		queryOntoQL.append("PROPERTIES (");
		queryOntoQL.append("street STRING,");
		queryOntoQL.append("city STRING))");
		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate(queryOntoQL.toString());
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("select c.#code, c.#name[fr] from #class c where c.#name[en] = 'Address'");
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Assert.assertEquals("Class_Address", resultSetObtenu.getString(1));
		Assert.assertEquals("Adresse", resultSetObtenu.getString(2));
		Assert.assertFalse(resultSetObtenu.next());

		// Create the class Car
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("CREATE #CLASS Car (");
		queryOntoQL.append("DESCRIPTOR (");
		queryOntoQL.append("#code='Class_Car',  ");
		queryOntoQL.append("#name[fr] ='Voiture')");
		queryOntoQL.append("PROPERTIES (");
		queryOntoQL.append("registration STRING,");
		queryOntoQL.append("color STRING,");
		queryOntoQL.append("miles INT))");
		statement.executeUpdate(queryOntoQL.toString());
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("select c.#code, c.#name[fr], c.#version from #class c where c.#name[en] = 'Car'");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Assert.assertEquals("Class_Car", resultSetObtenu.getString(1));
		Assert.assertEquals("Voiture", resultSetObtenu.getString(2));
		Assert.assertEquals("001", resultSetObtenu.getString(3));
		Assert.assertFalse(resultSetObtenu.next());

		// Create the class Person
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("CREATE #CLASS Person (");
		queryOntoQL.append("DESCRIPTOR (");
		queryOntoQL.append("#code='Class_Person',  ");
		queryOntoQL.append("#name[fr] ='Personne')");
		queryOntoQL.append("PROPERTIES (");
		queryOntoQL.append("\"first name\" STRING,");
		queryOntoQL.append("\"last names\" STRING ARRAY,");
		queryOntoQL.append("age INT,");
		queryOntoQL.append("address REF(Address),");
		queryOntoQL.append("cars REF(Car) ARRAY)))");
		statement.executeUpdate(queryOntoQL.toString());
		queryOntoQL = new StringBuffer();
		queryOntoQL
				.append("select c.#code, c.#name[fr], c.#version, c.#oid from #class c where c.#name[en] = 'Person'");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Assert.assertEquals("Class_Person", resultSetObtenu.getString(1));
		Assert.assertEquals("Personne", resultSetObtenu.getString(2));
		Assert.assertEquals("001", resultSetObtenu.getString(3));
		int oidPersonne = resultSetObtenu.getInt(4);
		Assert.assertFalse(resultSetObtenu.next());

		// Create the class Student
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("CREATE #CLASS Student UNDER Person (");
		queryOntoQL.append("DESCRIPTOR (");
		queryOntoQL.append("#code='Class_Student',  ");
		queryOntoQL.append("#name[fr] ='Etudiant')");
		queryOntoQL.append("PROPERTIES (");
		queryOntoQL.append("grade STRING))");
		statement.executeUpdate(queryOntoQL.toString());
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("select c.#code, c.#name[fr] from #class c where c.#name[en] = 'Student'");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Assert.assertEquals("Class_Student", resultSetObtenu.getString(1));
		Assert.assertEquals("Etudiant", resultSetObtenu.getString(2));
		Assert.assertFalse(resultSetObtenu.next());

		// Create the class Employee
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("CREATE #CLASS Employee UNDER Person (");
		queryOntoQL.append("DESCRIPTOR (");
		queryOntoQL.append("#code='Class_Employee',  ");
		queryOntoQL.append("#name[fr] ='Employé')");
		queryOntoQL.append("PROPERTIES (");
		queryOntoQL.append("salary INT))");
		statement.executeUpdate(queryOntoQL.toString());
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("select c.#code, c.#name[fr] from #class c where c.#name[en] = 'Employee'");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Assert.assertEquals("Class_Employee", resultSetObtenu.getString(1));
		Assert.assertEquals("Employé", resultSetObtenu.getString(2));
		Assert.assertFalse(resultSetObtenu.next());

		// Create an extent for the class Address
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("CREATE EXTENT OF Address (street, city)");
		statement.executeUpdate(queryOntoQL.toString());
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("select c.#usedProperties from #class c where c.#name[en] = 'Address'");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Set setResult = resultSetObtenu.getSet(1);
		Assert.assertEquals(2, setResult.size());
		Assert.assertFalse(resultSetObtenu.next());

		// Create an extent for the class Car
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("CREATE EXTENT OF Car (registration, color, miles)");
		statement.executeUpdate(queryOntoQL.toString());
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("select c.#usedProperties from #class c where c.#name[en] = 'Car'");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		setResult = resultSetObtenu.getSet(1);
		Assert.assertEquals(3, setResult.size());
		Assert.assertFalse(resultSetObtenu.next());

		// Create an extent for the class Person
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("CREATE EXTENT OF Person (\"first name\", \"last names\", address, cars)");
		statement.executeUpdate(queryOntoQL.toString());
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("select c.#usedProperties from #class c where c.#name[en] = 'Person'");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		setResult = resultSetObtenu.getSet(1);
		Assert.assertEquals(4, setResult.size());
		Assert.assertFalse(resultSetObtenu.next());

		// Create an extent for the class Student
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("CREATE EXTENT OF Student (\"first name\", \"last names\", age, address)");
		statement.executeUpdate(queryOntoQL.toString());
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("select c.#usedProperties from #class c where c.#name[en] = 'Student'");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		setResult = resultSetObtenu.getSet(1);
		Assert.assertEquals(4, setResult.size());
		Assert.assertFalse(resultSetObtenu.next());

		// Create an extent for the class Employee
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("CREATE EXTENT OF Employee (\"first name\", \"last names\", address, salary, cars)");
		statement.executeUpdate(queryOntoQL.toString());
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("select c.#usedProperties from #class c where c.#name[en] = 'Employee'");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		setResult = resultSetObtenu.getSet(1);
		Assert.assertEquals(5, setResult.size());
		Assert.assertFalse(resultSetObtenu.next());

		// Insert some new address
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("INSERT INTO Address (street, city) values ('Sunset','Los Angeles')");
		int nbrInserted = statement.executeUpdate(queryOntoQL.toString());
		Assert.assertEquals(1, nbrInserted);
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("INSERT INTO Address (street, city) values ('Road Street','Miami')");
		nbrInserted = statement.executeUpdate(queryOntoQL.toString());
		Assert.assertEquals(1, nbrInserted);
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("INSERT INTO Address (street, city) values ('Scholar Street','New York')");
		nbrInserted = statement.executeUpdate(queryOntoQL.toString());
		Assert.assertEquals(1, nbrInserted);
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("INSERT INTO Address (street, city) values ('Balco Street','Miami')");
		nbrInserted = statement.executeUpdate(queryOntoQL.toString());
		Assert.assertEquals(1, nbrInserted);

		// Insert some new cars
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("INSERT INTO Car (registration, color) values ('5552TH16','black')");
		nbrInserted = statement.executeUpdate(queryOntoQL.toString());
		Assert.assertEquals(1, nbrInserted);
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("INSERT INTO Car (registration, color, miles) values ('1245RS34','red', 10000)");
		nbrInserted = statement.executeUpdate(queryOntoQL.toString());
		Assert.assertEquals(1, nbrInserted);
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("INSERT INTO Car (registration, color) values ('4578GF78','blue')");
		nbrInserted = statement.executeUpdate(queryOntoQL.toString());
		Assert.assertEquals(1, nbrInserted);

		// Insert a new person
		queryOntoQL = new StringBuffer();
		queryOntoQL.append(
				"INSERT INTO Person (\"first name\", \"last names\", address, cars) values ('Jopart', ARRAY['Steve', 'John', 'Henry'], (SELECT oid from Address where street='Sunset' and city='Los Angeles'), ARRAY(SELECT oid from Car where registration ='5552TH16'))");
		nbrInserted = statement.executeUpdate(queryOntoQL.toString());
		Assert.assertEquals(1, nbrInserted);

		// Insert a new employee
		queryOntoQL = new StringBuffer();
		queryOntoQL.append(
				"INSERT INTO Employee (\"first name\", \"last names\", address, cars, salary) values ('Blanc', ARRAY['Georges'],(SELECT oid from Address where street='Scholar Street' and city='New York'), ARRAY(SELECT oid from Car where registration ='4578GF78' or registration = '1245RS34'), 15000)");
		nbrInserted = statement.executeUpdate(queryOntoQL.toString());
		Assert.assertEquals(1, nbrInserted);

		// Insert a new student
		queryOntoQL = new StringBuffer();
		queryOntoQL.append(
				"INSERT INTO Student (\"first name\", \"last names\", age, address) values ('Croro', ARRAY['Joe', 'Jim'], 18, (SELECT oid from Address where street='Road Street' and city='Miami'))");
		nbrInserted = statement.executeUpdate(queryOntoQL.toString());
		Assert.assertEquals(1, nbrInserted);

		// Insert another student
		queryOntoQL = new StringBuffer();
		queryOntoQL.append(
				"INSERT INTO Student (\"first name\", \"last names\", age, address) values ('Sorine', ARRAY['Jeremy'], 20, (SELECT oid from Address where street='Balco Street' and city='Miami'))");
		nbrInserted = statement.executeUpdate(queryOntoQL.toString());
		Assert.assertEquals(1, nbrInserted);

		// Query the data
		queryOntoQL = new StringBuffer();
		queryOntoQL.append("SELECT \"first name\", \"last names\" FROM ONLY (Person)");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Assert.assertEquals("Jopart", resultSetObtenu.getString(1));
		String[] names = (String[]) resultSetObtenu.getCollection(2);
		Assert.assertEquals(3, names.length);
		Assert.assertEquals("Steve", names[0]);
		Assert.assertEquals("John", names[1]);
		Assert.assertEquals("Henry", names[2]);
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = new StringBuffer();
		queryOntoQL.append("SELECT p.address.city FROM Person p order by p.address.city asc");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Assert.assertEquals("Los Angeles", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("Miami", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("Miami", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("New York", resultSetObtenu.getString(1));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = new StringBuffer();
		queryOntoQL.append(
				"SELECT \"first name\", age, address.city, car.registration, car.color FROM Person p, UNNEST (p.cars) as car");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Assert.assertEquals("Jopart", resultSetObtenu.getString(1));
		Assert.assertNull(resultSetObtenu.getString(2));
		Assert.assertEquals("Los Angeles", resultSetObtenu.getString(3));
		Assert.assertEquals("5552TH16", resultSetObtenu.getString(4));
		Assert.assertEquals("black", resultSetObtenu.getString(5));
		resultSetObtenu.next();
		Assert.assertEquals("Blanc", resultSetObtenu.getString(1));
		Assert.assertNull(resultSetObtenu.getString(2));
		Assert.assertEquals("New York", resultSetObtenu.getString(3));
		Assert.assertEquals("1245RS34", resultSetObtenu.getString(4));
		Assert.assertEquals("red", resultSetObtenu.getString(5));
		resultSetObtenu.next();
		Assert.assertEquals("Blanc", resultSetObtenu.getString(1));
		Assert.assertNull(resultSetObtenu.getString(2));
		Assert.assertEquals("New York", resultSetObtenu.getString(3));
		Assert.assertEquals("4578GF78", resultSetObtenu.getString(4));
		Assert.assertEquals("blue", resultSetObtenu.getString(5));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = new StringBuffer();
		queryOntoQL.append(
				"SELECT \"first name\", ARRAY (SELECT a.street FROM Address a WHERE a.city = p.address.city) FROM Person p order by \"first name\"");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Assert.assertEquals("Blanc", resultSetObtenu.getString(1));
		String[] streets = (String[]) resultSetObtenu.getCollection(2);
		Assert.assertEquals(streets[0], "Scholar Street");
		resultSetObtenu.next();
		Assert.assertEquals("Croro", resultSetObtenu.getString(1));
		streets = (String[]) resultSetObtenu.getCollection(2);
		Assert.assertEquals(streets[0], "Road Street");
		Assert.assertEquals(streets[1], "Balco Street");
		resultSetObtenu.next();
		Assert.assertEquals("Jopart", resultSetObtenu.getString(1));
		streets = (String[]) resultSetObtenu.getCollection(2);
		Assert.assertEquals(streets[0], "Sunset");
		resultSetObtenu.next();
		Assert.assertEquals("Sorine", resultSetObtenu.getString(1));
		streets = (String[]) resultSetObtenu.getCollection(2);
		Assert.assertEquals(streets[0], "Road Street");
		Assert.assertEquals(streets[1], "Balco Street");
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = new StringBuffer();
		queryOntoQL.append("(SELECT \"first name\" FROM Person) except (SELECT \"first name\" FROM Student) ");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Assert.assertEquals("Blanc", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("Jopart", resultSetObtenu.getString(1));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = new StringBuffer();
		queryOntoQL.append("SELECT avg(age) FROM Student s WHERE s.address.city = 'Miami'");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Assert.assertEquals(19.0, resultSetObtenu.getDouble(1), 0.1);
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = new StringBuffer();
		queryOntoQL.append(
				"SELECT distinct a.city FROM Address a WHERE EXISTS (SELECT s.\"first name\" FROM Student s WHERE s.address.city = a.city AND s.age >= 20)");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Assert.assertEquals("Miami", resultSetObtenu.getString(1));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = new StringBuffer();
		queryOntoQL.append("SELECT c.#name[fr], p.#name[en] FROM #CLASS c, UNNEST (c.#properties) p WHERE #oid = "
				+ oidPersonne + " order by p.#name[en] desc");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Assert.assertEquals("Personne", resultSetObtenu.getString(1));
		Assert.assertEquals("last names", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("Personne", resultSetObtenu.getString(1));
		Assert.assertEquals("first name", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("Personne", resultSetObtenu.getString(1));
		Assert.assertEquals("cars", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("Personne", resultSetObtenu.getString(1));
		Assert.assertEquals("age", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("Personne", resultSetObtenu.getString(1));
		Assert.assertEquals("address", resultSetObtenu.getString(2));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = new StringBuffer();
		queryOntoQL.append(
				"SELECT p.#name[en], CASE WHEN d IS OF (ONLY #intType, ONLY #realType) THEN 'int or real type' WHEN d IS OF (#stringType) THEN 'string type' WHEN d IS OF (#refType) THEN 'reference type' WHEN d IS OF (#collectionType) THEN 'collection type'  ELSE 'unknown type' END FROM #property p, #datatype d where p.#range = d.#oid and p.#scope.#name[en]='Person' order by p.#name[en] asc");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Assert.assertEquals("address", resultSetObtenu.getString(1));
		Assert.assertEquals("reference type", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("age", resultSetObtenu.getString(1));
		Assert.assertEquals("int or real type", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("cars", resultSetObtenu.getString(1));
		Assert.assertEquals("collection type", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("first name", resultSetObtenu.getString(1));
		Assert.assertEquals("string type", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("last names", resultSetObtenu.getString(1));
		Assert.assertEquals("collection type", resultSetObtenu.getString(2));

		queryOntoQL = new StringBuffer();
		queryOntoQL
				.append("SELECT p.#name[en], i.p FROM #CLASS c, UNNEST (c.#properties) p, only(c) as i WHERE c.#oid = "
						+ oidPersonne + "  order by p.#name[en] asc");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Assert.assertEquals("address", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("age", resultSetObtenu.getString(1));
		Assert.assertNull("18", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("cars", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("first name", resultSetObtenu.getString(1));
		Assert.assertEquals("Jopart", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("last names", resultSetObtenu.getString(1));
		Assert.assertEquals("{Steve,John,Henry}", resultSetObtenu.getString(2));

		queryOntoQL = new StringBuffer();
		queryOntoQL.append(
				"SELECT typeOf(p).#name[en], CASE WHEN p IS OF (Employee) THEN treat(p as Employee).salary ELSE 0 END FROM Person p order by typeOf(p).#name[en] asc");
		resultSetObtenu = statement.executeQuery(queryOntoQL.toString());
		resultSetObtenu.next();
		Assert.assertEquals("Employee", resultSetObtenu.getString(1));
		Assert.assertEquals("15000", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("Person", resultSetObtenu.getString(1));
		Assert.assertEquals("0", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("Student", resultSetObtenu.getString(1));
		Assert.assertEquals("0", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("Student", resultSetObtenu.getString(1));
		Assert.assertEquals("0", resultSetObtenu.getString(2));
		Assert.assertFalse(resultSetObtenu.next());

		t.rollback();
		s.close();
	}
}