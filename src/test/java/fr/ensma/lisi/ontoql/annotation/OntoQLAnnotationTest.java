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
package fr.ensma.lisi.ontoql.annotation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.QueryException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.jobdbc.impl.OntoQLSessionImpl;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Test case on model annotation (Laura)
 * 
 * @author Laura MASTELLA
 */
public class OntoQLAnnotationTest extends OntoQLTestCase {

	public OntoQLSession sLaura;

	private Connection database;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		try {
			database = DriverManager
					.getConnection("jdbc:postgresql://" + HOST + ":" + PORT + "/OntoQLJUnitTestAnnotation", USR, PWD);
			database.setAutoCommit(false);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		sLaura = new OntoQLSessionImpl(database);
		sLaura.setReferenceLanguage(OntoQLHelper.FRENCH);
		sLaura.setDefaultNameSpace("INTERNATIONAL_ID");

		Assert.assertNotNull(sLaura);
	}

	@Test
	public void testBugLaura() throws SQLException, QueryException {
		OntoQLStatement statement = sLaura.createOntoQLStatement();
		String queryOntoQL = "insert into #Annotation (#isAnnotatedBy) values ( (select #oid from #Class where #name = 'XYZFile') )  ";
		int resDDL = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, resDDL);
		queryOntoQL = "SELECT #isAnnotatedBy  from #Annotation";
		OntoQLResultSet resultset = statement.executeQuery(queryOntoQL);
		Assert.assertTrue(resultset.next());

		getSession().close();
		sLaura.close();
	}

	@Test
	public void testBugsSandro() throws SQLException, QueryException {
		getSession().setReferenceLanguage(OntoQLHelper.ENGLISH);

		Transaction t = getSession().beginTransaction();

		String queryOntoQL = "SELECT #directSubclasses FROM #class WHERE #name[fr] = 'LRC'";
		OntoQLStatement statement = getSession().createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "insert into #ontology (#namespace) values ( 'http://www.lisi.ensma.fr' )  ";
		int resDDL = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, resDDL);

		getSession().setDefaultNameSpace("http://www.lisi.ensma.fr");
		queryOntoQL = "CREATE #class LRC( DESCRIPTOR ( #name[fr]='LRC' )  )";
		resDDL = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, resDDL);
		queryOntoQL = "select #name[fr] from #class where #name[en]='LRC'";
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		Assert.assertTrue(resultSetObtenu.next());
		Assert.assertEquals("LRC", resultSetObtenu.getString(1));

		queryOntoQL = "CREATE #class Sample UNDER LRC( "
				+ " DESCRIPTOR ( #name[fr]='Sample') PROPERTIES ( sampleID String ) )";
		resDDL = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, resDDL);
		queryOntoQL = "select csub.#name[fr] from #class c, unnest(c.#subclasses) as csub where #name[en]='LRC'";
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		Assert.assertTrue(resultSetObtenu.next());
		Assert.assertEquals("Sample", resultSetObtenu.getString(1));

		queryOntoQL = "CREATE #class Identification UNDER LRC(" + "DESCRIPTOR ( #name[fr]='Identification' ) "
				+ "PROPERTIES (" + "partOf        REF(Sample)," + "field        STRING," + "well        STRING,"
				+ "depth        INT," + "petrographer    STRING ) )";
		resDDL = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, resDDL);
		queryOntoQL = "select csub.#name[fr] from #class c, unnest(c.#subclasses) as csub where #name[en]='LRC' order by csub.#name[fr] desc";
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		Assert.assertTrue(resultSetObtenu.next());
		Assert.assertEquals("Sample", resultSetObtenu.getString(1));
		Assert.assertTrue(resultSetObtenu.next());
		Assert.assertEquals("Identification", resultSetObtenu.getString(1));

		queryOntoQL = "CREATE EXTENT OF Identification (" + "partOf, field, well, depth, petrographer)";
		resDDL = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, resDDL);

		queryOntoQL = "CREATE EXTENT OF Sample (" + "sampleID)";
		resDDL = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, resDDL);

		queryOntoQL = "INSERT INTO Sample (sampleID) VALUES ('1')";
		resDDL = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, resDDL);

		queryOntoQL = "INSERT INTO Identification (partOf, field, well, depth, petrographer) VALUES ("
				+ "(select oid from Sample where sampleID='1'), 'field1', 'well1', 1, 'petrographer1') ";
		resDDL = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, resDDL);

		queryOntoQL = "select partOf.sampleID, field, well, depth, petrographer from Identification";
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		Assert.assertTrue(resultSetObtenu.next());
		Assert.assertEquals("1", resultSetObtenu.getString(1));
		Assert.assertEquals("field1", resultSetObtenu.getString(2));
		Assert.assertEquals("well1", resultSetObtenu.getString(3));
		Assert.assertEquals("1", resultSetObtenu.getString(4));
		Assert.assertEquals("petrographer1", resultSetObtenu.getString(5));

		t.rollback();
		getSession().close();
	}

	@Test
	public void testBugs() throws SQLException, QueryException {
		getSession().setReferenceLanguage(OntoQLHelper.ENGLISH);

		// B1
		String queryOntoQL = "SELECT #note[en] from #class where #name[en]='CAGS'";
		OntoQLStatement statement = getSession().createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals(null, resultSetObtenu.getString(1));
		Assert.assertFalse(resultSetObtenu.next());

		// B2
		queryOntoQL = "SELECT p.#oid FROM #class c, unnest(c.#properties) as p Where c.#oid='1040'";
		statement = getSession().createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("1204", resultSetObtenu.getString(1));

		// B3
		queryOntoQL = "SELECT #code, #name[en], #definition[en] FROM #concept where #definedBy.#namespace = 'EGALIS_FR' order by #name[en] asc";
		// statement = s.createOntoQLStatement();
		// statement.executeQuery(queryOntoQL);

		// B4
		queryOntoQL = "SELECT 'Toto', 1, its_muff, ARRAY[1,2,3] FROM HUDSON Where oid=100";
		// statement = s.createOntoQLStatement();
		// resultSetObtenu = statement.executeQuery(queryOntoQL);
		// resultSetObtenu.next();
		// Assert.assertEquals("Toto", resultSetObtenu.getString(1));
		// Assert.assertEquals(1, resultSetObtenu.getInt(2));
		// Assert.assertEquals("423", resultSetObtenu.getString(3));
		// Object[] collectionInt = (Object[]) resultSetObtenu.getCollection(4);
		// Assert.assertEquals(1, ((Integer) collectionInt[0]).intValue());

		// B5
		queryOntoQL = "select distinct i.p from #class c, c as i, unnest(c.#properties) as p where p.#oid = '1202'";
		// statement = s.createOntoQLStatement();
		// resultSetObtenu = statement.executeQuery(queryOntoQL);
		// int i = 0;
		// while (resultSetObtenu.next()) {
		// i++;
		// }
		// Assert.assertFalse(i == 153);

		// B7
		queryOntoQL = "select p.#name[en] from #property p, #datatype d where p.#name[fr] like 'R%' and p.#range=d.#oid and d is of (only #stringType)";
		statement = getSession().createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("Reference", resultSetObtenu.getString(1));
		Assert.assertFalse(resultSetObtenu.next());
		queryOntoQL = "select p.#name[en] from #property p where p.#name[fr] like 'R%' and p.#range is of (only ref(#stringType))";
		statement = getSession().createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("Reference", resultSetObtenu.getString(1));
		Assert.assertFalse(resultSetObtenu.next());

		// B6
		queryOntoQL = "select p.#oid from #class c, unnest(c.#subclasses) csub, unnest (csub.#properties) as p where c.#oid = '1068'";
		statement = getSession().createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);

		// B8
		queryOntoQL = "update only(CAGS) set Size = 'XL'";
		try {
			statement.executeUpdate(queryOntoQL);
			Assert.fail();
		} catch (JOBDBCException oExc) {
			Assert.assertEquals("can not update the abstract class 'CAGS'", oExc.getMessage());
		}

		// B9
		queryOntoQL = "select prop.#oid from #class c, unnest(c.#scopeProperties) as prop where #name[en] = 'CAGS'";
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		queryOntoQL = "select #scope.#name[en] from #property where #oid = " + resultSetObtenu.getString(1);
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		// Assert.assertEquals("CAGS", resultSetObtenu.getString(1));

		// B10
		getSession().setDefaultNameSpace(null);
		queryOntoQL = "select ns:Size, p1224 from ns:HUDSON, e1124 order by p1224 asc, ns:Size desc using namespace ns='http://lisi.ensma.fr/'";
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("XL", resultSetObtenu.getString(1));
		Assert.assertEquals("blanc", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("L", resultSetObtenu.getString(1));
		Assert.assertEquals("blanc", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("XL", resultSetObtenu.getString(1));
		Assert.assertEquals("bleu", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("L", resultSetObtenu.getString(1));
		Assert.assertEquals("bleu", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("XL", resultSetObtenu.getString(1));
		Assert.assertEquals("jaune", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("L", resultSetObtenu.getString(1));
		Assert.assertEquals("jaune", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("XL", resultSetObtenu.getString(1));
		Assert.assertEquals("rouge", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("L", resultSetObtenu.getString(1));
		Assert.assertEquals("rouge", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("XL", resultSetObtenu.getString(1));
		Assert.assertEquals("vert", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("L", resultSetObtenu.getString(1));
		Assert.assertEquals("vert", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("XL", resultSetObtenu.getString(1));
		Assert.assertEquals("violet", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("L", resultSetObtenu.getString(1));
		Assert.assertEquals("violet", resultSetObtenu.getString(2));
		Assert.assertFalse(resultSetObtenu.next());

		// B13
		getSession().setDefaultNameSpace("http://lisi.ensma.fr/");
		queryOntoQL = "SELECT Size from HUDSON UNION SELECT Size FROM ONTARIO ORDER BY Size DESC";
		// Bug le order by n'est pas e l'exterieur
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		// Assert.assertEquals("XL", resultSetObtenu.getString(1));
		// resultSetObtenu.next();
		// Assert.assertEquals("S", resultSetObtenu.getString(1));
		// resultSetObtenu.next();
		// Assert.assertEquals("M", resultSetObtenu.getString(1));
		// resultSetObtenu.next();
		// Assert.assertEquals("L", resultSetObtenu.getString(1));
		// Assert.assertFalse(resultSetObtenu.next());

		getSession().close();
	}

	@Test
	public void testImprovements() throws SQLException, QueryException {
		getSession().setReferenceLanguage(OntoQLHelper.ENGLISH);

		// I1
		String queryOntoQL = "SELECT Size, name from \"FOC BABY\" f, unnest(f.names) as name";
		OntoQLStatement statement = getSession().createOntoQLStatement();
		// OntoQLResultSet resultSetObtenu =
		// statement.executeQuery(queryOntoQL);
		// resultSetObtenu.next();
		// Assert.assertEquals(null, resultSetObtenu.getString(1));
		// Assert.assertFalse(resultSetObtenu.next());

		// I2
		Transaction t = getSession().beginTransaction();
		// queryOntoQL = "update CAGS set Size = 'XL'";
		// statement.executeUpdate(queryOntoQL);
		// queryOntoQL = "select count(*) from CAGS where Size = 'XL'";
		// OntoQLResultSet resultSetObtenu =
		// statement.executeQuery(queryOntoQL);
		// resultSetObtenu.next();
		// int nbrCagsAttendu = resultSetObtenu.getInt(1);
		// queryOntoQL = "select count(*) from CAGS";
		// resultSetObtenu = statement.executeQuery(queryOntoQL);
		// resultSetObtenu.next();
		// int nbrCagsObtenu = resultSetObtenu.getInt(1);
		// Assert.assertEquals(nbrCagsAttendu, nbrCagsObtenu);
		// queryOntoQL = "delete from CAGS";
		// statement.executeUpdate(queryOntoQL);
		// queryOntoQL = "select count(*) from CAGS";
		// resultSetObtenu = statement.executeQuery(queryOntoQL);
		// resultSetObtenu.next();
		// Assert.assertEquals(0, resultSetObtenu.getInt(1));
		t.rollback();

		// I3
		queryOntoQL = "delete From MUFFS";
		// try {
		// statement.executeUpdate(queryOntoQL);
		// Assert.fail();
		// }
		// catch (JOBDBCException oExc) {
		// Assert.assertEquals("can not delete the instances because they are
		// referenced by another property", oExc.getMessage());
		// }

		// I4
		queryOntoQL = "select its_hudsons[1].Size from \"FOC BABY\"";
		// statement = s.createOntoQLStatement();
		// OntoQLResultSet resultSetObtenu =
		// statement.executeQuery(queryOntoQL);
		// resultSetObtenu.next();
		// Assert.assertEquals("XL", resultSetObtenu.getString(1));

		// I5
		// Create a class with superclasses
		t = getSession().beginTransaction();
		queryOntoQL = "CREATE #CLASS Vehicule EXTENDS \"COUPES - VENT\", HUDSON, ONTARIO ( "
				+ "DESCRIPTOR (#name[en] = 'Vehicle', #code = 'BB3DFD4', #version = '001') " + ")";
		statement = getSession().createOntoQLStatement();
		// int res = statement.executeUpdate(queryOntoQL);
		// Assert.assertEquals(0, res);
		// statement = s.createOntoQLStatement();
		// OntoQLResultSet resultSetObtenu = statement
		// .executeQuery("select directSuperclass.#name[en] from #class c,
		// unnest(c.#directSuperclasses) as directSuperclass where c.#code =
		// 'BB3DFD4'");
		// resultSetObtenu.next();
		// Assert.assertEquals("CAGS", resultSetObtenu.getString(1));
		// resultSetObtenu.next();
		// Assert.assertEquals("HUDSON", resultSetObtenu.getString(1));
		// resultSetObtenu.next();
		// Assert.assertEquals("ONTARIO", resultSetObtenu.getString(1));
		// Assert.assertFalse(resultSetObtenu.next());
		t.rollback();

		// I6
		queryOntoQL = "select its_muff.* from HUDSON";
		statement = getSession().createOntoQLStatement();
		// statement.executeUpdate(queryOntoQL);

		// I7
		queryOntoQL = "select count(*) from MUFFS m left join HUDSON h on m.Size = h.Size ";
		statement = getSession().createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("4", resultSetObtenu.getString(1));

		queryOntoQL = "select count(*) from MUFFS m right join HUDSON h left join ONTARIO o on h.Size=o.Size on m.Size = h.Size ";
		statement = getSession().createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("2", resultSetObtenu.getString(1));

		getSession().close();
	}

	// public void testAposterioriCaseOf() throws Exception {
	//
	// OntoQLSession s = openSession();
	// s.setReferenceLanguage(OntoQLHelper.ENGLISH);
	//
	// Transaction t = s.beginTransaction();
	//
	// // a new entity with a primitive attribute
	// t = s.beginTransaction();
	// String queryOntoQL =
	// "create entity #MapProperty (#oid INT, #source REF(#Property), #mapTo
	// REF(#Property))";
	// OntoQLStatement statement = s.createOntoQLStatement();
	// int res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(0, res);
	//
	// queryOntoQL = "CREATE #Class B (PROPERTIES (propB1 INT, propB2 INT))";
	// statement = s.createOntoQLStatement();
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(0, res);
	//
	// queryOntoQL =
	// "CREATE #Class A(PROPERTIES (propA1 REF(B) ARRAY, propA2 String ARRAY))";
	// statement = s.createOntoQLStatement();
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(0, res);
	//
	// queryOntoQL =
	// "insert into #MapProperty (#source, #mapTo) values ((select #oid from
	// #property where #name='propA1'),(select #oid from #property where
	// #name='propB1'))";
	// statement = s.createOntoQLStatement();
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(1, res);
	//
	// queryOntoQL =
	// "insert into #MapProperty (#source, #mapTo) values ((select #oid from
	// #property where #name='propA2'),(select #oid from #property where
	// #name='propB2'))";
	// statement = s.createOntoQLStatement();
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(1, res);
	//
	// queryOntoQL = "select #oid from #MapProperty";
	// statement = s.createOntoQLStatement();
	// OntoQLResultSet resultSet = statement.executeQuery(queryOntoQL);
	// Assert.assertTrue(resultSet.next());
	// Assert.assertTrue(resultSet.next());
	// Assert.assertFalse(resultSet.next());
	//
	// queryOntoQL =
	// "insert into #AposterioriCaseof (#source, #isCaseOf,
	// #correspondingProperties) values ((select #oid from #class where
	// #name='A'),(select #oid from #class where #name='B'),Array(select #oid from
	// #MapProperty))";
	// statement = s.createOntoQLStatement();
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(1, res);
	//
	// queryOntoQL =
	// "select #source.#name, #isCaseOf.#name, p.#source.#name, p.#mapTo.#name from
	// #AposterioriCaseof a, unnest(a.#correspondingProperties) as p";
	// statement = s.createOntoQLStatement();
	// resultSet = statement.executeQuery(queryOntoQL);
	// Assert.assertTrue(resultSet.next());
	// Assert.assertEquals("A", resultSet.getString(1));
	// Assert.assertEquals("B", resultSet.getString(2));
	// Assert.assertEquals("propA1",resultSet.getString(3));
	// Assert.assertEquals("propB1",resultSet.getString(4));
	// Assert.assertTrue(resultSet.next());
	// Assert.assertEquals("A", resultSet.getString(1));
	// Assert.assertEquals("B", resultSet.getString(2));
	// Assert.assertEquals("propA2",resultSet.getString(3));
	// Assert.assertEquals("propB2",resultSet.getString(4));
	// Assert.assertFalse(resultSet.next());
	//
	// queryOntoQL = "DROP ENTITY #MapProperty";
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(0, res);
	// t.rollback();
	//
	// queryOntoQL = "CREATE #Class B (PROPERTIES (propB1 INT, propB2 INT))";
	// statement = s.createOntoQLStatement();
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(0, res);
	//
	// queryOntoQL =
	// "CREATE #Class A(PROPERTIES (propA1 REF(B) ARRAY, propA2 String ARRAY))";
	// statement = s.createOntoQLStatement();
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(0, res);
	//
	// queryOntoQL =
	// "create #AposterioriCaseof A CASEOF B (propA1 MAP propB1, propA2 MAP
	// propB2)";
	// statement = s.createOntoQLStatement();
	// res = statement.executeUpdate(queryOntoQL);
	// Assert.assertEquals(0, res);
	//
	// queryOntoQL =
	// "select #source.#name, #isCaseOf.#name, #correspondingProperties from
	// #AposterioriCaseof";
	// statement = s.createOntoQLStatement();
	// resultSet = statement.executeQuery(queryOntoQL);
	// Assert.assertTrue(resultSet.next());
	// Assert.assertEquals("A", resultSet.getString(1));
	// Assert.assertEquals("B", resultSet.getString(2));
	// assertNotNull(resultSet.getString(3));
	//
	// t.rollback();
	//
	// s.close();
	// }
}