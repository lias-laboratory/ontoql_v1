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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.QueryException;
import fr.ensma.lisi.ontoql.ontoapi.Instance;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Stéphane JEAN
 */
public class OntoQLDQLTest extends OntoQLTestCase {

	@Test
	public void testExecuteJoinQuery() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryOntoQL = "SELECT ONTARIO.Reference, HUDSON.its_muff.Reference FROM ONTARIO, HUDSON where HUDSON.its_muff.Reference is not null";
		String resultSetAttendu = "Reference,Reference\n";
		resultSetAttendu += "-------------------------------------\n";
		OntoQLStatement statement = s.createOntoQLStatement();

		String resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT h.its_muff.Reference, p.Reference from HUDSON h, \"PERSONAL EQUIPEMENT/SAFETY\" p where h.its_muff.Reference is not null";
		resultSetAttendu = "Reference,Reference\n";
		resultSetAttendu += "-------------------------------------\n";
		statement = s.createOntoQLStatement();

		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT h1.its_muff.its_slalom.Reference,h2.its_muff.Reference from HUDSON h1, HUDSON h2";
		resultSetAttendu = "Reference,Reference\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "305000,null\n";
		resultSetAttendu += "305000,null\n";
		resultSetAttendu += "305000,null\n";
		resultSetAttendu += "305000,null\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select c2.#name "
				+ "from #class c, unnest(c.#superclasses) as #superclasses JOIN #class c2 ON c2.#oid = #superclasses.#oid "
				+ "where c.#oid = 1063";
		resultSetAttendu = "name\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "PERSONAL EQUIPEMENT/SAFETY\n";
		resultSetAttendu += "CAGS\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		s.close();
	}

	@Test
	public void testMultilinguismMetaschema() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);

		String queryOntoQL = "SELECT #nom[fr], #définition[fr], #remarque, #révision, #définiPar.#espaceDeNoms, #propriétés, #dateVersionCourante from #classe where #nom[en] = 'CAGS'";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultset = statement.executeQuery(queryOntoQL);
		resultset.next();
		Assert.assertEquals("COUPES - VENT", resultset.getString(1));
		Assert.assertEquals("", resultset.getString(2));
		Assert.assertNull(resultset.getString(3));
		Assert.assertEquals("001", resultset.getString(4));
		Assert.assertEquals("http://lisi.ensma.fr/", resultset.getString(5));
		Assert.assertEquals(3, resultset.getSet(6).size());
		Assert.assertNull(resultset.getString(7));
		Assert.assertFalse(resultset.next());

		s.close();
	}

	@Test
	public void testNamespace() throws SQLException, QueryException {
		OntoQLSession s = getSession();

		String queryOntoQL = "SELECT Size from CAGS where Size like '%ans' USING NAMESPACE 'http://lisi.ensma.fr#'";
		OntoQLStatement statement = s.createOntoQLStatement();
		try {
			statement.executeQuery(queryOntoQL);
			Assert.fail();
		} catch (JOBDBCException oExc) {
			Assert.assertEquals("CAGS is not a valid class name on the namespace http://lisi.ensma.fr#",
					oExc.getMessage());
		}

		queryOntoQL = "SELECT Size from CAGS where Size like '%ans' USING NAMESPACE 'http://lisi.ensma.fr/'";
		statement = s.createOntoQLStatement();
		OntoQLResultSet resultset = statement.executeQuery(queryOntoQL);
		resultset.next();
		Assert.assertEquals("6-8 ans", resultset.getString(1));

		s.setDefaultNameSpace("http://lisi.ensma.fr#");
		queryOntoQL = "SELECT ns:Size from ns:CAGS where ns:Size like '%ans' USING NAMESPACE 'http://lisi.ensma.fr#', ns='http://lisi.ensma.fr/'";
		statement = s.createOntoQLStatement();
		resultset = statement.executeQuery(queryOntoQL);
		resultset.next();
		Assert.assertEquals("6-8 ans", resultset.getString(1));

		queryOntoQL = "SELECT Size from ns:CAGS where ns:Size like '%ans' USING NAMESPACE 'http://lisi.ensma.fr#', ns='http://lisi.ensma.fr/'";
		try {
			statement.executeQuery(queryOntoQL);
			Assert.fail();
		} catch (JOBDBCException oExc) {
			Assert.assertEquals("Size is not a property defined on the namespace http://lisi.ensma.fr#",
					oExc.getMessage());
		}

		s.setDefaultNameSpace("");
		queryOntoQL = "SELECT ns:its_muff.ns:Size from ns:HUDSON where ns:Size like '%L' USING NAMESPACE ns='http://lisi.ensma.fr/'";
		statement = s.createOntoQLStatement();
		resultset = statement.executeQuery(queryOntoQL);
		resultset.next();
		Assert.assertNull(resultset.getString(1));
		resultset.next();
		Assert.assertNull(resultset.getString(1));
		Assert.assertFalse(resultset.next());

		s.close();
	}

	@Test
	public void testSelectArrayToString() throws SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		s.setDefaultNameSpace("http://lisi.ensma.fr/");
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate("CREATE #CLASS Voiture (DESCRIPTOR (#code ='0002')))");
		statement.executeUpdate("ALTER #CLASS Voiture ADD refenrencesPneumatique String ARRAY");
		statement.executeUpdate("CREATE EXTENT OF Voiture (refenrencesPneumatique)");

		Assert.assertEquals(1, statement
				.executeUpdate("insert into Voiture (refenrencesPneumatique) values (ARRAY['Michelin', 'Pirreli'])"));
		OntoQLResultSet executeQuery = statement.executeQuery(
				"select refenrencesPneumatique from Voiture where upper(array_to_string(refenrencesPneumatique, ',')) like upper('%Michelin%')");

		Assert.assertTrue(executeQuery.next());

		t.rollback();
	}

	@Test
	public void testIQLClassWithoutExtent() throws SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		s.setDefaultNameSpace("http://lisi.ensma.fr/");
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();

		Assert.assertEquals(0, statement
				.executeUpdate("CREATE #CLASS Person (DESCRIPTOR (#code = 'Person') PROPERTIES (name string))"));
		ResultSet resultSet = statement.executeQuery("SELECT oid FROM Person");
		Assert.assertEquals(1, resultSet.getMetaData().getColumnCount());
		Assert.assertEquals("oid", resultSet.getMetaData().getColumnName(1));
		Assert.assertFalse(resultSet.next());

		t.rollback();
	}

	@Test
	public void testLimitClause() throws SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		s.setDefaultNameSpace("http://lisi.ensma.fr/");
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate("CREATE #CLASS Voiture (DESCRIPTOR (#code ='0002')))");
		statement.executeUpdate("ALTER #CLASS Voiture ADD couleur String");
		statement.executeUpdate("CREATE EXTENT OF Voiture (couleur)");
		statement.executeUpdate("INSERT INTO Voiture (couleur) values ('rouge')");
		statement.executeUpdate("INSERT INTO Voiture (couleur) values ('bleu')");
		statement.executeUpdate("INSERT INTO Voiture (couleur) values ('jaune')");
		statement.executeUpdate("INSERT INTO Voiture (couleur) values ('violet')");
		statement.executeUpdate("INSERT INTO Voiture (couleur) values ('mauve')");

		OntoQLResultSet executeQuery = statement
				.executeQuery("select couleur from Voiture order by couleur limit 4 offset 1");
		executeQuery.next();
		Assert.assertEquals("jaune", executeQuery.getString(1));
		executeQuery.next();
		Assert.assertEquals("mauve", executeQuery.getString(1));
		executeQuery.next();
		Assert.assertEquals("rouge", executeQuery.getString(1));
		executeQuery.next();
		Assert.assertEquals("violet", executeQuery.getString(1));
		Assert.assertFalse(executeQuery.next());

		executeQuery = statement.executeQuery("select couleur from Voiture order by couleur limit 2");
		executeQuery.next();
		Assert.assertEquals("bleu", executeQuery.getString(1));
		executeQuery.next();
		Assert.assertEquals("jaune", executeQuery.getString(1));
		Assert.assertFalse(executeQuery.next());

		t.rollback();
	}

	@Test
	public void testSelectUpper() throws SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		s.setDefaultNameSpace("http://lisi.ensma.fr/");
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate("CREATE #CLASS Voiture (DESCRIPTOR (#code ='0002')))");
		statement.executeUpdate("ALTER #CLASS Voiture ADD Marque String");
		statement.executeUpdate("CREATE EXTENT OF Voiture (Marque)");

		Assert.assertEquals(1, statement.executeUpdate("insert into Voiture (Marque) values ('Renault')"));
		OntoQLResultSet executeQuery = statement
				.executeQuery("select Marque from Voiture where Upper(Marque) like Upper('%ReNaulT%')");

		Assert.assertTrue(executeQuery.next());
		Assert.assertEquals("Renault", executeQuery.getString(1));

		t.rollback();
	}

	@Test
	public void testStringOperators() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryOntoQL = "SELECT upper(Size) from CAGS where Size like '%ans'";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("6-8 ANS", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("10-12 ANS", resultSetObtenu.getString(1));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT lower(Size) from CAGS where Size like 'XL'";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("xl", resultSetObtenu.getString(1));

		queryOntoQL = "SELECT length(Size), bit_length(Size) from CAGS where Size like 'XL'";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("2", resultSetObtenu.getString(1));
		Assert.assertEquals("16", resultSetObtenu.getString(2));

		s.close();
	}

	@Test
	public void testNumericOperators() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryOntoQL = "SELECT abs(-1.2), sqrt(4) from CAGS where Size like '%ans'";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("1.2", resultSetObtenu.getString(1));
		Assert.assertEquals("2", resultSetObtenu.getString(2));

		s.close();
	}

	/**
	 * Test SELECT * on OntoQL
	 */
	@Test
	public void testSelectAlias() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryOntoQL = "SELECT (Size) AS LASIZE FROM only (ONTARIO) Where oid=96";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		OntoQLResultSetMetaData resultSetMetaDataObtenu = resultSetObtenu.getOntoQLMetaData();
		Assert.assertEquals("LASIZE", resultSetMetaDataObtenu.getColumnName(1));
		Assert.assertEquals("LASIZE", resultSetMetaDataObtenu.getColumnLabel(1));

		s.close();
	}

	/**
	 * Test SELECT cste on OntoQL
	 */
	@Test
	public void testSelectCste() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryOntoQL = "SELECT ARRAY[1*2,2,3], 'Toto', 1, its_muff FROM ONLY(HUDSON) Where oid=100";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("Toto", resultSetObtenu.getString(2));
		Assert.assertEquals(1, resultSetObtenu.getInt(3));
		Assert.assertEquals("423", resultSetObtenu.getString(4));
		Integer[] collectionInt = (Integer[]) resultSetObtenu.getCollection(1);
		Assert.assertEquals(2, collectionInt[0].intValue());
		Assert.assertEquals(2, collectionInt[1].intValue());
		Assert.assertEquals(3, collectionInt[2].intValue());

		queryOntoQL = "SELECT ARRAY[1.0,2.1,3.85], 'Toto', 1.4, its_muff FROM ONLY(HUDSON) Where oid=100";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("Toto", resultSetObtenu.getString(2));
		Assert.assertEquals(1.4, resultSetObtenu.getBigDecimal(3).floatValue(), 0.01);
		Assert.assertEquals("423", resultSetObtenu.getString(4));
		BigDecimal[] collectionFloat = (BigDecimal[]) resultSetObtenu.getCollection(1);
		Assert.assertEquals(1.0, collectionFloat[0].floatValue(), 0.01);
		Assert.assertEquals(2.1, collectionFloat[1].floatValue(), 0.01);
		Assert.assertEquals(3.85, collectionFloat[2].floatValue(), 0.01);

		queryOntoQL = "SELECT ARRAY['1.0' || 'a','2.1','3.85'], 'Toto' || 'T', 1.4*2, its_muff FROM ONLY(HUDSON) Where oid=100";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("TotoT", resultSetObtenu.getString(2));
		Assert.assertEquals(2.8, resultSetObtenu.getBigDecimal(3).floatValue(), 0.01);
		Assert.assertEquals("423", resultSetObtenu.getString(4));
		String[] collectionString = (String[]) resultSetObtenu.getCollection(1);
		Assert.assertEquals("1.0a", collectionString[0]);
		Assert.assertEquals("2.1", collectionString[1]);
		Assert.assertEquals("3.85", collectionString[2]);

		s.close();
	}

	/**
	 * Test SELECT {distinct|all}
	 */
	@Test
	public void testSelectDistinct() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryOntoQL = "SELECT DISTINCT Size FROM CAGS order by Size";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("10-12 ans", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("6-8 ans", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("L", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("M", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("S", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("XL", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("XXL", resultSetObtenu.getString(1));
		Assert.assertFalse(resultSetObtenu.next());

		s.close();
	}

	/**
	 * Test if the CASE expression is implemented by OntoQL
	 */
	@Test
	public void testCaseExpression() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryOntoQL = "select case Size when 'XXL' then 'trés trés large' "
				+ "when 'XL' then 'trés large' when 'M' then 'moyen' when 'L' then 'large' "
				+ "when 'S' then 'petit' ELSE null END  from CAGS  where oid <= 90 order by oid asc";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("large", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("trés large", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("trés trés large", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals(null, resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals(null, resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("petit", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("moyen", resultSetObtenu.getString(1));

		queryOntoQL = "select case when Size in ('XXL', 'XL') then 'au moins trés large' "
				+ "when Size='M' then 'moyen' when Size='L' then 'large' "
				+ "when Size='S' then 'petit' ELSE null END  from CAGS  where oid <= 90 order by oid asc";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("large", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("au moins trés large", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("au moins trés large", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals(null, resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals(null, resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("petit", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("moyen", resultSetObtenu.getString(1));

		queryOntoQL = "SELECT nullif(Size, 'L'),nullif(Size, 'XL') FROM ONLY(HUDSON) where Size ='L'";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals(null, resultSetObtenu.getString(1));
		Assert.assertEquals("L", resultSetObtenu.getString(2));

		queryOntoQL = "SELECT COALESCE(Size, 'AB'), COALESCE(its_muff.Reference, 'AB'), COALESCE(its_muff.Reference, its_muff.its_slalom.Reference, 'TOTO') FROM ONLY(HUDSON) where Size ='L'";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("L", resultSetObtenu.getString(1));
		Assert.assertEquals("AB", resultSetObtenu.getString(2));
		Assert.assertEquals("305000", resultSetObtenu.getString(3));

		s.close();
	}

	/**
	 * Test if the CASE expression is implemented by OntoQL
	 */
	@Test
	public void testCastExpression() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryOntoQL = "select cast(1 as string) || 'toto' from ONLY(HUDSON) ";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("1toto", resultSetObtenu.getString(1));

		queryOntoQL = "select cast('1' as int) * 2 from ONLY(HUDSON) where Size in ('XL', 'L')";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals(2, resultSetObtenu.getInt(1));

		queryOntoQL = "select i.oid  from CAGS i where i is of (HUDSON,ONTARIO)";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		int nbrHudsonOntario = 0;
		while (resultSetObtenu.next()) {
			nbrHudsonOntario++;
		}
		Assert.assertEquals(6, nbrHudsonOntario);

		queryOntoQL = "select i.oid  from \"PERSONAL EQUIPEMENT/SAFETY\" i where i is of (ONLY CAGS)";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		Assert.assertFalse(resultSetObtenu.next());

		// Latter when new entity may be added
		queryOntoQL = "select i.oid  from #property i where i is of (#nonDependentPdet)";

		queryOntoQL = "select case when i IS OF (HUDSON) THEN treat(i as HUDSON).virage ELSE 0 END from CAGS i";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		int nbrHudson = 0;
		while (resultSetObtenu.next()) {
			if (resultSetObtenu.getInt(1) > 0) {
				nbrHudson++;
			}
		}
		Assert.assertEquals(2, nbrHudson);

		queryOntoQL = "select case when i IS OF (HUDSON) THEN treat(i as HUDSON).virage ELSE 0 END from ONLY(CAGS) i";
		statement = s.createOntoQLStatement();
		try {
			resultSetObtenu = statement.executeQuery(queryOntoQL);
		} catch (JOBDBCException oExc) {
			Assert.assertEquals("Can not downcast the instance i of the non polymorphic element CAGS",
					oExc.getMessage());
		}

		queryOntoQL = "select case when i IS OF (HUDSON) THEN treat(i as HUDSON).its_muff.its_slalom.Reference WHEN i IS OF (MUFFS) THEN treat(i as MUFFS).its_slalom.Reference ELSE 'NULL' END from \"PERSONAL EQUIPEMENT/SAFETY\" i";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		int nbrHudsonMuffs = 0;
		while (resultSetObtenu.next()) {
			if (resultSetObtenu.getString(1) != null) {
				if (resultSetObtenu.getString(1).equals("305000")) {
					nbrHudsonMuffs++;
				}
			}
		}
		Assert.assertEquals(3, nbrHudsonMuffs);

		s.close();
	}

	/**
	 * Test if the {union|intersect|except} {all|distinct} queries are provided by
	 * OntoQL
	 */
	@Test
	public void testUnionAllQueries() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryOntoQL = "SELECT Size FROM HUDSON UNION SELECT Size FROM ONLY(HUDSON) ";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("L", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("XL", resultSetObtenu.getString(1));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT Size FROM HUDSON EXCEPT SELECT Size FROM ONLY(HUDSON) ";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT Size FROM HUDSON INTERSECT SELECT Size FROM ONLY(HUDSON) ";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("L", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("XL", resultSetObtenu.getString(1));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT #name[fr] FROM #class where #name[en]='CAGS' UNION SELECT #name[fr] FROM #class where #name[en]='CAGS' ";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("COUPES - VENT", resultSetObtenu.getString(1));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT #name[fr] FROM #class where #name[en]='CAGS' INTERSECT SELECT #name[fr] FROM #class where #name[en]='CAGS' ";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("COUPES - VENT", resultSetObtenu.getString(1));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT #name[fr] FROM #class where #name[en]='CAGS' EXCEPT SELECT #name[fr] FROM #class where #name[en]='CAGS' ";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		Assert.assertFalse(resultSetObtenu.next());

		s.close();
	}

	/**
	 * Test aggregate function implementation of OntoQL
	 */
	@Test
	public void testAggregateFunction() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryOntoQL = "SELECT count(*) FROM ONLY(HUDSON)";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals(2, resultSetObtenu.getInt(1));

		s.close();
	}

	/**
	 * Test SQL compatibility of OntoQL
	 */
	@Test
	public void testSQLCompatibility() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setDefaultNameSpace(OntoQLHelper.NO_NAMESPACE);

		String queryOntoQL = "SELECT p1202 from e1062";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("XL", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("L", resultSetObtenu.getString(1));

		// queryOntoQL =
		// "select g1062x0_.rid as rid, 'e1062' as tablename, g1062x0_.p1204 as p1204,
		// g1062x0_.p1202 as p1202, g1062x0_.p6216_rid as p6216_rid,
		// g1062x0_.p6216_tablename as p6216_tablename, g1062x0_.p6237 as p6237 from
		// e1062 g1062x0_";
		// resultSetObtenu = statement.executeQuery(queryOntoQL);
		// resultSetObtenu.next();
		// Assert.assertEquals("100", resultSetObtenu.getString(1));
		// resultSetObtenu.next();
		// Assert.assertEquals("101", resultSetObtenu.getString(1));

		s.close();
	}

	@Test
	public void testLongTimeProcessingQuery() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryAsc = "SELECT c.Reference, c.Size, \"PERSONAL EQUIPEMENT/SAFETY\".Reference, \"PERSONAL EQUIPEMENT/SAFETY\".Buoyancy, \"PERSONAL EQUIPEMENT/SAFETY\".\"Buoyancy RAFT\", \"PERSONAL EQUIPEMENT/SAFETY\".Size FROM CAGS c, \"PERSONAL EQUIPEMENT/SAFETY\"";
		String queryDsc = "SELECT c.Reference, c.Size, \"PERSONAL EQUIPEMENT/SAFETY\".Reference, \"PERSONAL EQUIPEMENT/SAFETY\".Buoyancy, \"PERSONAL EQUIPEMENT/SAFETY\".\"Buoyancy RAFT\", \"PERSONAL EQUIPEMENT/SAFETY\".Size FROM \"PERSONAL EQUIPEMENT/SAFETY\", CAGS c";
		String otherQueryAsc = "SELECT CAGS.Reference, CAGS.Size, \"PERSONAL EQUIPEMENT/SAFETY\".Reference, \"PERSONAL EQUIPEMENT/SAFETY\".Buoyancy, \"PERSONAL EQUIPEMENT/SAFETY\".\"Buoyancy RAFT\", \"PERSONAL EQUIPEMENT/SAFETY\".Size FROM CAGS, \"PERSONAL EQUIPEMENT/SAFETY\"";

		OntoQLStatement statement = s.createOntoQLStatement();

		OntoQLResultSet resultSetAsc = statement.executeQuery(queryAsc);
		OntoQLResultSet resultSetDsc = statement.executeQuery(queryDsc);
		OntoQLResultSet otherResultSetAsc = statement.executeQuery(otherQueryAsc);
		int nbrRowAsc = 0;
		while (resultSetAsc.next()) {
			nbrRowAsc++;
		}
		int nbrRowDsc = 0;
		while (resultSetDsc.next()) {
			nbrRowDsc++;
		}
		int otherNbrRowAsc = 0;
		while (otherResultSetAsc.next()) {
			otherNbrRowAsc++;
		}
		Assert.assertEquals(nbrRowAsc, 192 * 24);
		Assert.assertEquals(nbrRowAsc, nbrRowDsc);
		Assert.assertEquals(nbrRowAsc, otherNbrRowAsc);

		s.close();
	}

	@Test
	public void testArrayOperators() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryOntoQL = "SELECT names[1] FROM ONLY(!1031) where names = ARRAY['BABY 1','BABY 2','BABY 3','BABY 4']";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("BABY 1", resultSetObtenu.getString(1));

		queryOntoQL = "SELECT oid FROM ONLY(!1031) where 101 = ANY(its_hudsons)";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("12", resultSetObtenu.getString(1));

		queryOntoQL = "SELECT oid FROM ONLY(!1031) where 102 = ANY(its_hudsons)";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT names[1] FROM ONLY(!1031) where names=ARRAY['BABY 1','BABY 2','BABY 3','BABY 4']";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("BABY 1", resultSetObtenu.getString(1));

		// This JUnit Test worked fines with PostgreSQL 7.4 but don't work with
		// 8.2 (ERROR: cache lookup failed for function 0)
		// queryOntoQL =
		// "SELECT names[1] FROM ONLY(!1031) where names <> ARRAY['BABY 1','BABY
		// 2','BABY 3','BABY 5']";
		// statement = s.createOntoQLStatement();
		// resultSetObtenu = statement.executeQuery(queryOntoQL);
		// resultSetObtenu.next();
		// Assert.assertEquals("BABY 1", resultSetObtenu.getString(1));
		//
		// queryOntoQL =
		// "SELECT names[1] FROM ONLY(!1031) where names <> ARRAY['BABY 2','BABY
		// 2','BABY 3','BABY 4']";
		// statement = s.createOntoQLStatement();
		// resultSetObtenu = statement.executeQuery(queryOntoQL);
		// resultSetObtenu.next();
		// Assert.assertEquals("BABY 1", resultSetObtenu.getString(1));

		queryOntoQL = "SELECT its_hudsons FROM ONLY(!1031) where its_hudsons = ARRAY[100,101,100]";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Instance[] instancesHudson = (Instance[]) resultSetObtenu.getCollection(1);
		Assert.assertEquals(instancesHudson[0].getOid(), "100");

		// idem for these test
		// queryOntoQL =
		// "SELECT its_hudsons FROM ONLY(!1031) where its_hudsons <>
		// ARRAY[100,101,101]";
		// statement = s.createOntoQLStatement();
		// resultSetObtenu = statement.executeQuery(queryOntoQL);
		// resultSetObtenu.next();
		// instancesHudson = (Instance[]) resultSetObtenu.getCollection(1);
		// Assert.assertEquals(instancesHudson[0].getOid(), "100");

		// queryOntoQL =
		// "SELECT its_hudsons FROM ONLY(!1031) where ARRAY[100,101,101] <>
		// its_hudsons";
		// statement = s.createOntoQLStatement();
		// resultSetObtenu = statement.executeQuery(queryOntoQL);
		// resultSetObtenu.next();
		// instancesHudson = (Instance[]) resultSetObtenu.getCollection(1);
		// Assert.assertEquals(instancesHudson[0].getOid(), "100");

		// queryOntoQL =
		// "SELECT its_hudsons FROM ONLY(!1031) where ARRAY[100,101,100] = its_hudsons";
		// statement = s.createOntoQLStatement();
		// resultSetObtenu = statement.executeQuery(queryOntoQL);
		// resultSetObtenu.next();
		// instancesHudson = (Instance[]) resultSetObtenu.getCollection(1);
		// Assert.assertEquals(instancesHudson[0].getOid(), "100");

		s.close();
	}

	@Test
	public void testExecuteNestedQuery() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryOntoQL = "SELECT c.#name[en], array(select p.#name[en] from unnest(c.#properties) as p) "
				+ "FROM #class c where c.#oid = '1068'";

		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();

		Assert.assertEquals("CAGS", resultSetObtenu.getString(1));
		String[] names = (String[]) resultSetObtenu.getCollection(2);
		Assert.assertEquals("Reference", names[0]);
		Assert.assertEquals("Size", names[1]);

		queryOntoQL = "SELECT c.#name[en], array(select p.#name[en] from unnest(c.#properties) as p where p.#oid='1202') "
				+ "FROM #class c where c.#oid = '1068'";

		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();

		Assert.assertEquals("CAGS", resultSetObtenu.getString(1));
		names = (String[]) resultSetObtenu.getCollection(2);
		Assert.assertEquals("Size", names[0]);

		s.close();
	}

	/**
	 * @throws SQLException
	 * @throws QueryException
	 */
	@Test
	public void testExecuteMixedQuery() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		// Test behaviour of array
		String t = "t";
		String[] ts = { "t", "t" };
		Object[] test = { t, ts };
		Assert.assertEquals("t", test[0]);
		String[] verif = (String[]) test[1];
		Assert.assertEquals("t", verif[0]);
		Assert.assertEquals("t", verif[1]);

		String queryOntoQL = "SELECT i.oid "
				+ "FROM #class as c, ONLY(c) as i where (c.#name[en] = 'HUDSON' or c.#name[en] = 'ONTARIO') and (i.oid > 97 and i.oid < 101) order by i.oid asc";

		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();

		Assert.assertEquals("98", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("99", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("100", resultSetObtenu.getString(1));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT i.oid, c.#name[en] "
				+ "FROM #class as c, ONLY(c) as i where (c.#name[en] = 'HUDSON' or c.#name[en] = 'ONTARIO') and (i.oid > 97 and i.oid < 101) order by i.oid asc";

		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();

		Assert.assertEquals("98", resultSetObtenu.getString(1));
		Assert.assertEquals("ONTARIO", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("99", resultSetObtenu.getString(1));
		Assert.assertEquals("ONTARIO", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("100", resultSetObtenu.getString(1));
		Assert.assertEquals("HUDSON", resultSetObtenu.getString(2));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT i.oid, c.#name[en] "
				+ "FROM #class as c, c as i where (c.#name[en] = 'CAGS' or c.#name[en] = 'HUDSON') and (i.oid > 97 and i.oid < 101) order by i.oid asc";

		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();

		Assert.assertEquals("98", resultSetObtenu.getString(1));
		Assert.assertEquals("CAGS", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("99", resultSetObtenu.getString(1));
		Assert.assertEquals("CAGS", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("100", resultSetObtenu.getString(1));
		Assert.assertEquals("CAGS", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("100", resultSetObtenu.getString(1));
		Assert.assertEquals("HUDSON", resultSetObtenu.getString(2));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT i.oid, p.#name[en], i.p "
				+ "FROM #class as c, only(c) as i, unnest(c.#properties) as p where (c.#name[en] = 'HUDSON' or c.#name[en] = 'ONTARIO') and (i.oid = 101 or i.oid = 96) order by upper(p.#name[en]), i.oid asc";

		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("101", resultSetObtenu.getString(1));
		Assert.assertEquals("its_muff", resultSetObtenu.getString(2));
		Assert.assertEquals("423", resultSetObtenu.getString(3));
		resultSetObtenu.next();
		Assert.assertEquals("96", resultSetObtenu.getString(1));
		Assert.assertEquals("Reference", resultSetObtenu.getString(2));
		Assert.assertEquals("300030", resultSetObtenu.getString(3));
		resultSetObtenu.next();
		Assert.assertEquals("101", resultSetObtenu.getString(1));
		Assert.assertEquals("Reference", resultSetObtenu.getString(2));
		Assert.assertEquals("300061", resultSetObtenu.getString(3));
		resultSetObtenu.next();
		Assert.assertEquals("96", resultSetObtenu.getString(1));
		Assert.assertEquals("Size", resultSetObtenu.getString(2));
		Assert.assertEquals("L", resultSetObtenu.getString(3));
		resultSetObtenu.next();
		Assert.assertEquals("101", resultSetObtenu.getString(1));
		Assert.assertEquals("Size", resultSetObtenu.getString(2));
		Assert.assertEquals("L", resultSetObtenu.getString(3));
		resultSetObtenu.next();
		Assert.assertEquals("96", resultSetObtenu.getString(1));
		Assert.assertEquals("URI", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("101", resultSetObtenu.getString(1));
		Assert.assertEquals("URI", resultSetObtenu.getString(2));
		resultSetObtenu.next();
		Assert.assertEquals("101", resultSetObtenu.getString(1));
		Assert.assertEquals("virage", resultSetObtenu.getString(2));
		Assert.assertEquals("3", resultSetObtenu.getString(3));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT i.Reference, i.Size, typeof(i).#name[en] FROM CAGS i where i.oid > 97 and i.oid < 101 order by i.oid asc";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();

		Assert.assertEquals("300028", resultSetObtenu.getString(1));
		Assert.assertEquals("S", resultSetObtenu.getString(2));
		Assert.assertEquals("ONTARIO", resultSetObtenu.getString(3));
		resultSetObtenu.next();
		Assert.assertEquals("300029", resultSetObtenu.getString(1));
		Assert.assertEquals("M", resultSetObtenu.getString(2));
		Assert.assertEquals("ONTARIO", resultSetObtenu.getString(3));
		resultSetObtenu.next();
		Assert.assertEquals("300062", resultSetObtenu.getString(1));
		Assert.assertEquals("XL", resultSetObtenu.getString(2));
		Assert.assertEquals("HUDSON", resultSetObtenu.getString(3));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT i.Reference, i.Size, typeof(i).#name[en], typeof(i).#superclasses FROM CAGS i where i.oid > 97 and i.oid < 101 order by i.oid asc";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();

		Assert.assertEquals("300028", resultSetObtenu.getString(1));
		Assert.assertEquals("S", resultSetObtenu.getString(2));
		Assert.assertEquals("ONTARIO", resultSetObtenu.getString(3));
		Assert.assertEquals("{1068,1040}", resultSetObtenu.getString(4));
		resultSetObtenu.next();
		Assert.assertEquals("300029", resultSetObtenu.getString(1));
		Assert.assertEquals("M", resultSetObtenu.getString(2));
		Assert.assertEquals("ONTARIO", resultSetObtenu.getString(3));
		Assert.assertEquals("{1068,1040}", resultSetObtenu.getString(4));
		resultSetObtenu.next();
		Assert.assertEquals("300062", resultSetObtenu.getString(1));
		Assert.assertEquals("XL", resultSetObtenu.getString(2));
		Assert.assertEquals("HUDSON", resultSetObtenu.getString(3));
		Assert.assertEquals("{1068,1040}", resultSetObtenu.getString(4));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT i.Reference, i.Size, typeof(i).#name[en], typeof(i).#superclasses FROM CAGS i where (i.oid > 97 and i.oid < 101) and typeof(i).#name[en] like 'H%' order by i.oid asc";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("300062", resultSetObtenu.getString(1));
		Assert.assertEquals("XL", resultSetObtenu.getString(2));
		Assert.assertEquals("HUDSON", resultSetObtenu.getString(3));
		Assert.assertEquals("{1068,1040}", resultSetObtenu.getString(4));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT i.oid, array(select i.p from unnest(c.#properties) as p) "
				+ "FROM #class as c, only(c) as i where (c.#name[en] = 'HUDSON' or c.#name[en] = 'CAGS') and (i.oid >= 101 and i.oid < 103) order by i.oid asc";

		// statement = s.createOntoQLStatement();
		// resultSetObtenu = statement.executeQuery(queryOntoQL);
		// resultSetObtenu.next();
		// Assert.assertEquals("101", resultSetObtenu.getString(1));
		// Object[] propertiesValues = (Object[]) resultSetObtenu
		// .getCollection(2);
		// Assert.assertEquals("423", propertiesValues[0]);
		// Assert.assertEquals("3", propertiesValues[1]);
		// Assert.assertEquals("L", propertiesValues[2]);
		// Assert.assertEquals("300061", propertiesValues[3]);
		//
		// Assert.assertEquals("102", resultSetObtenu.getString(1));
		// propertiesValues = (Object[]) resultSetObtenu.getCollection(2);
		// Assert.assertEquals("XL", propertiesValues[0]);
		// Assert.assertEquals("300052", propertiesValues[1]);

		s.close();
	}

	@Test
	public void testBooleanOperatorQuery() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryOntoQL = "SELECT c.#name[en] FROM #class c where c.#oid='1068' union select p.#name[en] from #property p where p.#oid = '1202'";

		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("CAGS", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("Size", resultSetObtenu.getString(1));

		queryOntoQL = "SELECT c.#name[en] FROM #class c where c.#oid='1068' union select c.#name[en] FROM #class c where c.#oid='1068'";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("CAGS", resultSetObtenu.getString(1));
		// because an union is proceed and not an union all there is only one
		// result
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT p.#name[en] FROM #class c, unnest(c.#properties) as p where c.#oid='1062' except SELECT p.#name[en] FROM #class c, unnest(c.#properties) as p where c.#oid='1068'";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		resultSetObtenu.next();
		Assert.assertEquals("its_muff", resultSetObtenu.getString(1));
		resultSetObtenu.next();
		Assert.assertEquals("virage", resultSetObtenu.getString(1));
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT p.#name[en] FROM #class c, unnest(c.#properties) as p where c.#oid='1062' except SELECT p.#name[en] FROM #class c, unnest(c.#properties) as p where c.#oid='1062'";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		Assert.assertFalse(resultSetObtenu.next());

		queryOntoQL = "SELECT p.#name[en] FROM #class c, unnest(c.#properties) as p where c.#oid='1062' intersect SELECT p.#name[en] FROM #class c, unnest(c.#properties) as p where c.#oid='1068'";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		List<String> resultValues = new ArrayList<String>();
		Assert.assertTrue(resultSetObtenu.next());
		resultValues.add(resultSetObtenu.getString(1));
		Assert.assertTrue(resultSetObtenu.next());
		resultValues.add(resultSetObtenu.getString(1));
		Assert.assertTrue(resultSetObtenu.next());
		resultValues.add(resultSetObtenu.getString(1));
		Assert.assertFalse(resultSetObtenu.next());

		resultValues.contains("Reference");
		resultValues.contains("Size");
		resultValues.contains("URI");

		queryOntoQL = "SELECT p.#name[en] FROM #class c, unnest(c.#properties) as p where c.#oid='1062' order by p.#name[en] asc union SELECT p.#name[en] FROM #class c, unnest(c.#properties) as p where c.#oid='1068' order by p.#name[en] asc";
		statement = s.createOntoQLStatement();
		resultSetObtenu = statement.executeQuery(queryOntoQL);
		Vector<String> result = new Vector<String>(4);
		for (int i = 0; i < 5; i++) {
			resultSetObtenu.next();
			result.addElement(resultSetObtenu.getString(1));
		}
		Assert.assertTrue(result.contains("its_muff"));
		Assert.assertTrue(result.contains("Reference"));
		Assert.assertTrue(result.contains("Size"));
		Assert.assertTrue(result.contains("URI"));
		Assert.assertTrue(result.contains("virage"));
		Assert.assertFalse(resultSetObtenu.next());

		s.close();
	}

	@Test
	public void testExecuteQueryOnPropulsion() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryOntoQL = "SELECT @71DC338C04573-001 FROM @71DC338B55137-001 where @71DC338C04573-001 is not null";
		String resultSetAttendu = "Configuration\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "2 x 138 cm ou 1 x 222 cm\n";
		resultSetAttendu += "Una pala de kayak de 210 cm o 2 palas de canoa de 140 cm\n";
		resultSetAttendu += "Una pala de kayak de 210 cm o 2 palas de canoa de 140 cm\n";
		resultSetAttendu += "manche de 28mm de diamètre inséré jusqu'en bout de pale\n";

		OntoQLStatement statement = s.createOntoQLStatement();

		String resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT Configuration FROM PROPULSION where Configuration is not null";
		resultSetAttendu = "Configuration\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "2 x 138 cm ou 1 x 222 cm\n";
		resultSetAttendu += "Una pala de kayak de 210 cm o 2 palas de canoa de 140 cm\n";
		resultSetAttendu += "Una pala de kayak de 210 cm o 2 palas de canoa de 140 cm\n";
		resultSetAttendu += "manche de 28mm de diamètre inséré jusqu'en bout de pale\n";

		statement = s.createOntoQLStatement();

		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		s.close();
	}

	@Test
	public void testExecuteQuery() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String sHelmets = cHelmets.getInternalId();
		String sReference = pReference.getInternalId();
		String sColor = pColor.getInternalId();
		String sDurance = cDurance.getInternalId();
		Assert.assertNotNull(sDurance);
		String sJunior = cJunior.getInternalId();
		Assert.assertNotNull(sJunior);
		String sSlalom = cSlalom.getInternalId();
		Assert.assertNotNull(sSlalom);

		String queryOntoQL3 = "SELECT !" + sReference + ",!" + sColor + " FROM !" + sHelmets + "";
		String resultSetAttendu = "Reference,Color\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "302000,Jaune\n";
		resultSetAttendu += "304000,Jaune\n";
		resultSetAttendu += "304000,Rouge\n";
		resultSetAttendu += "304000,Bleu\n";
		resultSetAttendu += "304000,Blanc\n";
		resultSetAttendu += "305000,Rouge\n";

		OntoQLStatement statement = s.createOntoQLStatement();

		String resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL3));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL3 = "SELECT Reference FROM only(\"PERSONAL EQUIPEMENT/SAFETY\")";
		resultSetAttendu = "Reference\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL3));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL3 = "SELECT Reference, Size FROM only(\"PERSONAL EQUIPEMENT/SAFETY\")";
		resultSetAttendu = "Reference,Size\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL3));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL3 = "SELECT m.oid from only(MUFFS) m where m.Reference like '%' || m.Reference || '%' or m.Reference is null";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "423\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL3));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL3 = "SELECT m.oid from only(MUFFS) m where m.Reference not like '%' || m.Reference || '%' or m.Reference is null";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "423\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL3));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL3 = "SELECT Reference, Size, Buoyancy FROM only(\"PERSONAL EQUIPEMENT/SAFETY\")";
		resultSetAttendu = "Reference,Size,Buoyancy\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL3));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL3 = "SELECT Reference, Size, Buoyancy,\"Buoyancy RAFT\" FROM only(\"PERSONAL EQUIPEMENT/SAFETY\")";
		resultSetAttendu = "Reference,Size,Buoyancy,Buoyancy RAFT\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL3));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		String queryAsc = "SELECT CAGS.Reference, CAGS.Size, MUFFS.Reference, MUFFS.its_slalom.oid, MUFFS.Size  FROM CAGS, MUFFS ";
		String queryDsc = "SELECT CAGS.Reference, CAGS.Size, MUFFS.Reference, MUFFS.its_slalom.oid, MUFFS.Size  FROM MUFFS,CAGS";
		String otherQueryAsc = "SELECT CAGS.Reference FROM CAGS, MUFFS ";
		OntoQLResultSet otherResultSetAsc = statement.executeQuery(otherQueryAsc);

		OntoQLResultSet resultSetAsc = statement.executeQuery(queryAsc);
		OntoQLResultSet resultSetDsc = statement.executeQuery(queryDsc);
		int nbrRowAsc = 0;
		while (resultSetAsc.next()) {
			nbrRowAsc++;
		}
		int nbrRowDsc = 0;
		while (resultSetDsc.next()) {
			nbrRowDsc++;
		}
		int otherNbrRowAsc = 0;
		while (otherResultSetAsc.next()) {
			otherNbrRowAsc++;
		}
		Assert.assertEquals(nbrRowAsc, 96);
		Assert.assertEquals(nbrRowAsc, nbrRowDsc);
		Assert.assertEquals(nbrRowAsc, otherNbrRowAsc);

		String queryCount = "SELECT count(Size) FROM CAGS";
		String queryCountEquiv = "SELECT count(*) FROM CAGS";
		OntoQLResultSet resultSetObtain = statement.executeQuery(queryCount);
		OntoQLResultSet resultSetObtainEquiv = statement.executeQuery(queryCountEquiv);
		resultSetObtain.next();
		int countObtain = resultSetObtain.getInt(1);
		Assert.assertEquals(24, countObtain);
		resultSetObtainEquiv.next();
		int countObtainEquiv = resultSetObtainEquiv.getInt(1);
		Assert.assertEquals(countObtain, countObtainEquiv);

		queryCount = "SELECT count(Reference), Size FROM CAGS group by Size order by Size";
		resultSetAttendu = "count,Size\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1,10-12 ans\n";
		resultSetAttendu += "1,6-8 ans\n";
		resultSetAttendu += "6,L\n";
		resultSetAttendu += "5,M\n";
		resultSetAttendu += "3,S\n";
		resultSetAttendu += "6,XL\n";
		resultSetAttendu += "2,XXL\n";

		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryCount));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryCount = "SELECT count(Reference), Size FROM CAGS group by Size having Size = 'XL' or Size='L' order by Size asc";
		resultSetAttendu = "count,Size\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "6,L\n";
		resultSetAttendu += "6,XL\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryCount));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryCount = "SELECT count(Reference), 'Taille-' || Size || '-' FROM CAGS group by Size having Size = 'XL' or Size='L' order by Size asc";
		resultSetAttendu = "count,concatenation\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "6,Taille-L-\n";
		resultSetAttendu += "6,Taille-XL-\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryCount));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryCount = "SELECT count(Reference), 'Taille-' || Size FROM CAGS group by Size having Size = 'XL' or Size='L' order by Size asc";
		resultSetAttendu = "count,concatenation\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "6,Taille-L\n";
		resultSetAttendu += "6,Taille-XL\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryCount));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryCount = "SELECT count(Reference), Size FROM CAGS group by Size having Size = 'XL' or Size='L' order by Size desc";
		resultSetAttendu = "count,Size\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "6,XL\n";
		resultSetAttendu += "6,L\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryCount));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		String queryMin = "SELECT min(Size) FROM CAGS";
		resultSetObtain = statement.executeQuery(queryMin);
		resultSetObtain.next();
		String minObtain = resultSetObtain.getString(1);
		Assert.assertEquals("10-12 ans", minObtain);

		queryMin = "SELECT min(Reference), Size FROM CAGS group by Size order by Size";
		resultSetAttendu = "min,Size\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "300011,10-12 ans\n";
		resultSetAttendu += "300010,6-8 ans\n";
		resultSetAttendu += "300022,L\n";
		resultSetAttendu += "300021,M\n";
		resultSetAttendu += "300020,S\n";
		resultSetAttendu += "300023,XL\n";
		resultSetAttendu += "300024,XXL\n";

		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryMin));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		String queryMax = "SELECT max(Size) FROM CAGS";
		resultSetObtain = statement.executeQuery(queryMax);
		resultSetObtain.next();
		String maxObtain = resultSetObtain.getString(1);
		Assert.assertEquals("XXL", maxObtain);

		queryMax = "SELECT max(Reference), Size FROM CAGS group by Size order by Size";
		resultSetAttendu = "max,Size\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "300011,10-12 ans\n";
		resultSetAttendu += "300010,6-8 ans\n";
		resultSetAttendu += "300061,L\n";
		resultSetAttendu += "300055,M\n";
		resultSetAttendu += "300040,S\n";
		resultSetAttendu += "300062,XL\n";
		resultSetAttendu += "300044,XXL\n";

		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryMax));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryMax = "SELECT min(Size),max(Size) FROM CAGS";
		resultSetObtain = statement.executeQuery(queryMax);
		resultSetObtain.next();
		minObtain = resultSetObtain.getString(1);
		Assert.assertEquals("10-12 ans", minObtain);
		maxObtain = resultSetObtain.getString(2);
		Assert.assertEquals("XXL", maxObtain);

		String querySum = "SELECT sum(virage) FROM HUDSON";
		resultSetObtain = statement.executeQuery(querySum);
		resultSetObtain.next();
		String sumObtain = resultSetObtain.getString(1);
		Assert.assertEquals("4", sumObtain);

		String queryAvg = "SELECT avg(virage) FROM HUDSON";
		resultSetObtain = statement.executeQuery(queryAvg);
		resultSetObtain.next();
		String avgObtain = resultSetObtain.getString(1);
		Assert.assertTrue(avgObtain.startsWith("2."));

		queryCount = "select count(its_muff.its_slalom) from only(HUDSON)";
		resultSetObtain = statement.executeQuery(queryCount);
		resultSetObtain.next();
		countObtain = resultSetObtain.getInt(1);
		Assert.assertEquals(2, countObtain);

		// TODO when union of Ontoql work
		// queryCount = "(SELECT count(Size) FROM CAGS*) union (SELECT
		// count(Size) FROM CAGS*)";
		// resultSetObtain = statement.executeQuery(queryCount);
		// resultSetObtain.next();
		// countObtain = resultSetObtain.getInt(1);
		// Assert.assertEquals(24, countObtain);
		// resultSetObtain.next();
		// countObtain = resultSetObtain.getInt(1);
		// Assert.assertEquals(24, countObtain);

		String queryStar = "SELECT * FROM CAGS";
		String nonQueryStar = "SELECT c.oid, c.Size, c.Reference, c.\"Buoyancy\", c.\"Weight of the user\",c.\"Chest measurement\",c.\"Buoyancy RAFT\", URI FROM CAGS as c";

		OntoQLResultSet resultSetQueryStar = statement.executeQuery(queryStar);
		OntoQLResultSet resultSetNonQueryStar = statement.executeQuery(nonQueryStar);
		Assert.assertEquals(ResultSetToString(resultSetNonQueryStar), ResultSetToString(resultSetQueryStar));

		String queryArthimetic = "SELECT virage+2 FROM only(HUDSON)";
		resultSetAttendu = "virage + 2\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "3\n";
		resultSetAttendu += "5\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryArthimetic));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		nonQueryStar = "SELECT * FROM only(CAGS)";
		resultSetAttendu = "oid,Size,Reference,Buoyancy,Weight of the user,Chest measurement,Buoyancy RAFT,URI\n";
		resultSetAttendu += "-------------------------------------\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(nonQueryStar));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// String ontoQuery = "SELECT #code, #version FROM #class where #code is
		// null";
		// resultSetAttendu = "code,version\n";
		// resultSetAttendu += "-------------------------------------\n";
		// statement = s.createOntoQLStatement();
		// resultSetObtenu =
		// ResultSetToString(statement.executeQuery(ontoQuery));
		// Assert.assertEquals(resultSetAttendu, resultSetObtenu);
		//
		// ontoQuery = "SELECT #name[fr], #version FROM #class where #name[en] =
		// 'CAGS'";
		// resultSetAttendu = "name_fr,version\n";
		// resultSetAttendu += "-------------------------------------\n";
		// resultSetAttendu += "COUPES - VENT,001\n";
		// statement = s.createOntoQLStatement();
		// resultSetObtenu =
		// ResultSetToString(statement.executeQuery(ontoQuery));
		// Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		s.close();
	}

	@Test
	public void testExecuteQueryWithCaseOf() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		// Test star with the case of operator
		String queryStar = "SELECT * from Human";
		OntoQLStatement stmt = s.createOntoQLStatement();
		OntoQLResultSet resultset = stmt.executeQuery(queryStar);
		OntoQLResultSetMetaData rsetMetaData = resultset.getOntoQLMetaData();
		Assert.assertEquals(rsetMetaData.getColumnCount(), 4);
		Assert.assertEquals(rsetMetaData.getColumnName(1), "oid");
		Assert.assertEquals(rsetMetaData.getColumnName(2), "name");
		Assert.assertEquals(rsetMetaData.getColumnName(3), "birthdate");
		Assert.assertEquals(rsetMetaData.getColumnName(4), "nationality");

		// Student is case of Person. It has the property course
		// and import name and age from Person
		queryStar = "SELECT *  from HumanCaseOf";
		stmt = s.createOntoQLStatement();
		resultset = stmt.executeQuery(queryStar);
		rsetMetaData = resultset.getOntoQLMetaData();
		Assert.assertEquals(4, rsetMetaData.getColumnCount());
		Assert.assertEquals("oid", rsetMetaData.getColumnName(1));
		Assert.assertEquals("course", rsetMetaData.getColumnName(2));
		Assert.assertEquals("name", rsetMetaData.getColumnName(3));
		Assert.assertEquals("birthdate", rsetMetaData.getColumnName(4));

		s.close();
	}

	/**
	 * New feature number QL17.
	 * 
	 * @throws SQLException
	 * @throws QueryException
	 */
	@Test
	public void testExecuteQueryInvolvingCollection() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String collectionQuery = "SELECT names FROM only(!1031)";
		String resultSetAttendu = "names\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "{\"BABY 1\",\"BABY 2\",\"BABY 3\",\"BABY 4\"}\n";
		OntoQLStatement statement = s.createOntoQLStatement();
		String resultSetObtenu = ResultSetToString(statement.executeQuery(collectionQuery));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		OntoQLResultSet rs = statement.executeQuery(collectionQuery);
		rs.next();
		String[] names = (String[]) rs.getCollection(1);
		Assert.assertEquals(names[0], "BABY 1");
		Assert.assertEquals(names[1], "BABY 2");
		Assert.assertEquals(names[2], "BABY 3");

		collectionQuery = "SELECT names[1] FROM only(!1031)";
		resultSetAttendu = "names\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "BABY 1\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(collectionQuery));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		collectionQuery = "SELECT names[1] FROM only(!1031) where names[2] = 'BABY 2' or names[3] = 'TOTO'";
		resultSetAttendu = "names\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "BABY 1\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(collectionQuery));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		collectionQuery = "SELECT names[1] FROM only(!1031) where names[2] like 'BABY%' or names[3] like 'TOTO'";
		resultSetAttendu = "names\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "BABY 1\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(collectionQuery));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		collectionQuery = "SELECT its_hudsons FROM only(!1031)";
		rs = statement.executeQuery(collectionQuery);
		rs.next();
		Instance[] instancesHudson = (Instance[]) rs.getCollection(1);
		Assert.assertEquals(instancesHudson[0].getOid(), "100");
		Assert.assertEquals(instancesHudson[1].getOid(), "101");
		Assert.assertEquals(instancesHudson[2].getOid(), "100");

		collectionQuery = "SELECT h.Size FROM only(!1031), unnest(!1031.its_hudsons) as h ";
		rs = statement.executeQuery(collectionQuery);
		rs.next();
		Assert.assertEquals("XL", rs.getString(1));
		rs.next();
		Assert.assertEquals("L", rs.getString(1));
		// For the moment the implementation do not process
		// duplicate in array
		// rs.next();
		// Assert.assertEquals("XL", rs.getString(1));

		collectionQuery = "SELECT #properties FROM #class where #name[en] = 'HUDSON'";
		rs = statement.executeQuery(collectionQuery);
		rs.next();
		EntityProperty[] properties = (EntityProperty[]) rs.getCollection(1);
		Assert.assertEquals("number of properties applicable on Hudson must be 4", properties.length, 5);
		Assert.assertEquals("its_muff", properties[0].getName());
		Assert.assertEquals("virage", properties[1].getName());
		Assert.assertEquals("Size", properties[2].getName());
		Assert.assertEquals("Reference", properties[3].getName());

		collectionQuery = "SELECT #properties[1] FROM #class where #name[en] = 'HUDSON'";
		rs = statement.executeQuery(collectionQuery);
		rs.next();
		EntityProperty property = rs.getEntityProperty(1);
		Assert.assertEquals("its_muff", property.getName());

		collectionQuery = "SELECT #properties[1].#name[en] FROM #class where #name[en] = 'HUDSON'";
		// rs = statement.executeQuery(collectionQuery);
		// rs.next();
		// Assert.assertEquals("its_muff", rs.getString(1));

		collectionQuery = "SELECT p.#name[en] FROM #class c, unnest (c.#properties) p  where c.#name[en] = 'HUDSON'";
		;
		// Trop long ... 30s
		// rs = statement.executeQuery(collectionQuery);
		// rs.next();
		// Assert.assertEquals("Reference", rs.getString(1));
		// rs.next();
		// Assert.assertEquals("Size", rs.getString(1));
		// rs.next();
		// Assert.assertEquals("its_muff", rs.getString(1));
		// rs.next();
		// Assert.assertEquals("virage", rs.getString(1));

		collectionQuery = "SELECT p.#oid FROM #class c, unnest (c.#properties) p  where c.#oid = '1062'";
		;
		// Trop long ... 30s
		rs = statement.executeQuery(collectionQuery);
		rs.next();
		Assert.assertEquals("1204", rs.getString(1));
		rs.next();
		Assert.assertEquals("1202", rs.getString(1));
		rs.next();
		Assert.assertEquals("6216", rs.getString(1));
		rs.next();
		Assert.assertEquals("6237", rs.getString(1));

		collectionQuery = "SELECT #properties FROM #class where #name[en] like '%CASEOF'";
		rs = statement.executeQuery(collectionQuery);
		rs.next();
		properties = (EntityProperty[]) rs.getCollection(1);
		Assert.assertEquals("number of properties applicable on HudsonOntarioCaseOf must be 5", properties.length, 5);
		Assert.assertEquals("Size", properties[0].getName());
		Assert.assertEquals("Reference", properties[1].getName());
		Assert.assertEquals("URI", properties[2].getName());
		Assert.assertEquals("its_muff", properties[3].getName());
		Assert.assertEquals("virage", properties[4].getName());

		collectionQuery = "SELECT #scopeProperties FROM #class where #name[en] = 'HUDSON'";
		rs = statement.executeQuery(collectionQuery);
		rs.next();
		properties = (EntityProperty[]) rs.getCollection(1);
		Assert.assertEquals("number of defined properties by Hudson must be 2", properties.length, 2);
		Assert.assertEquals("its_muff", properties[0].getName());
		Assert.assertEquals("virage", properties[1].getName());

		collectionQuery = "SELECT p.#name[en] FROM #class c, unnest (c.#scopeProperties) p  where c.#oid = '1062'";
		rs = statement.executeQuery(collectionQuery);
		rs.next();
		Assert.assertEquals("its_muff", rs.getString(1));
		rs.next();
		Assert.assertEquals("virage", rs.getString(1));

		collectionQuery = "SELECT #scopeProperties[1] FROM #class c where c.#oid = '1062'";
		rs = statement.executeQuery(collectionQuery);
		rs.next();
		EntityProperty prop = rs.getEntityProperty(1);
		Assert.assertEquals("its_muff", prop.getName());

		collectionQuery = "SELECT c.#oid FROM #class c, unnest (c.#scopeProperties) p  where p.#name[en] = 'its_muff'";
		rs = statement.executeQuery(collectionQuery);
		rs.next();
		Assert.assertEquals("1062", rs.getString(1));

		collectionQuery = "SELECT #usedProperties FROM #class where #name[en] = 'HUDSON'";
		rs = statement.executeQuery(collectionQuery);
		rs.next();
		properties = (EntityProperty[]) rs.getCollection(1);
		Assert.assertEquals("number of defined properties by Hudson must be 5", properties.length, 5);
		Assert.assertEquals("its_muff", properties[2].getName());
		Assert.assertEquals("virage", properties[3].getName());
		Assert.assertEquals("Reference", properties[0].getName());
		Assert.assertEquals("Size", properties[1].getName());

		collectionQuery = "SELECT p.#name[en] FROM #class c, unnest (c.#usedProperties) p  where c.#oid = '1062' order by p.#name[en]";
		rs = statement.executeQuery(collectionQuery);
		List<String> values = new ArrayList<String>();
		Assert.assertTrue(rs.next());
		values.add(rs.getString(1));
		Assert.assertTrue(rs.next());
		values.add(rs.getString(1));
		Assert.assertTrue(rs.next());
		values.add(rs.getString(1));
		Assert.assertTrue(rs.next());
		values.add(rs.getString(1));
		Assert.assertTrue(rs.next());
		values.add(rs.getString(1));
		Assert.assertFalse(rs.next());

		values.contains("its_muff");
		values.contains("Reference");
		values.contains("Size");
		values.contains("URI");
		values.contains("virage");

		collectionQuery = "SELECT #directSuperclasses FROM #class where #name[en] = 'HUDSON'";

		rs = statement.executeQuery(collectionQuery);
		rs.next();
		EntityClass[] classes = (EntityClass[]) rs.getCollection(1);
		Assert.assertEquals("number of superclasses of Hudson must be 1", classes.length, 1);
		Assert.assertEquals("CAGS", classes[0].getName());

		collectionQuery = "SELECT #directSuperclasses FROM #class where #name[en] like '%CASEOF'";

		rs = statement.executeQuery(collectionQuery);
		rs.next();
		classes = (EntityClass[]) rs.getCollection(1);
		Assert.assertEquals("number of superclasses of HudsonOntarioCaseOf must be 3", classes.length, 3);
		Assert.assertEquals("STANDARD", classes[0].getName());
		Assert.assertEquals("HUDSON", classes[1].getName());
		Assert.assertEquals("ONTARIO", classes[2].getName());

		collectionQuery = "SELECT superClass.#name[en]  FROM #class c, unnest(c.#directSuperclasses) as superClass where #oid = '9037'";

		rs = statement.executeQuery(collectionQuery);
		rs.next();
		Assert.assertEquals("STANDARD", rs.getString(1));
		rs.next();
		Assert.assertEquals("ONTARIO", rs.getString(1));
		rs.next();
		Assert.assertEquals("HUDSON", rs.getString(1));

		collectionQuery = "SELECT #superclasses FROM #class where #name[en] = 'HUDSON'";
		rs = statement.executeQuery(collectionQuery);
		rs.next();
		classes = (EntityClass[]) rs.getCollection(1);
		Assert.assertEquals("number of superclasses of Hudson must be 2", classes.length, 2);
		Assert.assertEquals("CAGS", classes[0].getName());
		Assert.assertEquals("PERSONAL EQUIPEMENT/SAFETY", classes[1].getName());

		collectionQuery = "SELECT #superclasses FROM #class where #name[en] like '%CASEOF'";
		rs = statement.executeQuery(collectionQuery);
		rs.next();
		classes = (EntityClass[]) rs.getCollection(1);
		Assert.assertEquals("number of superclasses of HudsonOntarioCaseOf must be 5", classes.length, 5);
		Assert.assertEquals("STANDARD", classes[0].getName());
		Assert.assertEquals("HUDSON", classes[1].getName());
		Assert.assertEquals("ONTARIO", classes[2].getName());
		Assert.assertEquals("CAGS", classes[3].getName());
		Assert.assertEquals("PERSONAL EQUIPEMENT/SAFETY", classes[4].getName());

		collectionQuery = "SELECT #directSubclasses FROM #class where #name[en] = 'CAGS'";
		rs = statement.executeQuery(collectionQuery);
		Assert.assertTrue(rs.next());
		classes = (EntityClass[]) rs.getCollection(1);
		List<String> containsValues = new ArrayList<String>();
		Assert.assertEquals("number of direct sub classes of Cags must be 6", 6, classes.length);
		containsValues.add(classes[0].getName());
		containsValues.add(classes[1].getName());
		containsValues.add(classes[2].getName());
		containsValues.add(classes[3].getName());
		containsValues.add(classes[4].getName());
		containsValues.add(classes[5].getName());
		containsValues.contains("RANDONNEE");
		containsValues.contains("ANCHORAGE (KAYAK PANTS)");
		containsValues.contains("FJORD (SEA PARKA)");
		containsValues.contains("HUDSON");
		containsValues.contains("ONTARIO");
		containsValues.contains("STANDARD");

		collectionQuery = "SELECT #subclasses FROM #class where #name[en] = 'CAGS'";
		rs = statement.executeQuery(collectionQuery);
		rs.next();
		classes = (EntityClass[]) rs.getCollection(1);
		Assert.assertEquals("number of sub classes of Cags must be 8", 8, classes.length);
		containsValues = new ArrayList<String>();
		containsValues.add(classes[0].getName());
		containsValues.add(classes[1].getName());
		containsValues.add(classes[2].getName());
		containsValues.add(classes[3].getName());
		containsValues.add(classes[4].getName());
		containsValues.add(classes[5].getName());
		containsValues.add(classes[6].getName());
		containsValues.add(classes[7].getName());

		containsValues.contains("RANDONNEE");
		containsValues.contains("ANCHORAGE (KAYAK PANTS)");
		containsValues.contains("FJORD (SEA PARKA)");
		containsValues.contains("HUDSON");
		containsValues.contains("ONTARIO");
		containsValues.contains("STANDARD");
		containsValues.contains("HUDSONONTARIOCASEOF");
		containsValues.contains("HUDSONONTARIOISA");

		s.close();

	}

	@Test
	public void testExecuteQueryDemo() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryOntoQL = "select Reference,Size from CAGS where Size = 'XXL' order by Reference desc";
		String resultSetAttendu = "Reference,Size\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "300044,XXL\n";
		resultSetAttendu += "300024,XXL\n";
		OntoQLStatement statement = s.createOntoQLStatement();
		String resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT Reference FROM only(\"ANCHORAGE (KAYAK PANTS)\") order by Reference";
		resultSetAttendu = "Reference\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "300055\n";
		resultSetAttendu += "300056\n";
		resultSetAttendu += "300057\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		queryOntoQL = "Select Référence, Taille from \"COUPES - VENT\" where Taille = 'XXL' order by Référence desc";
		resultSetAttendu = "Référence,Taille\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "300044,XXL\n";
		resultSetAttendu += "300024,XXL\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT Référence FROM only(\"ANCHORAGE (PANTALON KAYAK)\") order by Référence";
		resultSetAttendu = "Référence\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "300055\n";
		resultSetAttendu += "300056\n";
		resultSetAttendu += "300057\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT @71DC24862420C-001, @71DC2486B78EF-001 FROM @71DC2FDBFD904-001 where @71DC2486B78EF-001 = 'XXL' order by  @71DC24862420C-001 desc";
		resultSetAttendu = "Référence,Taille\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "300044,XXL\n";
		resultSetAttendu += "300024,XXL\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT @71DC24862420C-001 from only(@71DC2FE39B1EA-001) order by @71DC24862420C-001";
		resultSetAttendu = "Référence\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "300055\n";
		resultSetAttendu += "300056\n";
		resultSetAttendu += "300057\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT !1204, !1202 FROM !1068 where !1202 = 'XXL' order by !1204 desc";
		resultSetAttendu = "Référence,Taille\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "300044,XXL\n";
		resultSetAttendu += "300024,XXL\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT !1204  FROM only(!1060) order by !1204";
		resultSetAttendu = "Référence\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "300055\n";
		resultSetAttendu += "300056\n";
		resultSetAttendu += "300057\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT !1204, !1202 FROM !1068 where !1202 = 'XXL' order by !1204";
		resultSetAttendu = "Référence,Taille\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "300024,XXL\n";
		resultSetAttendu += "300044,XXL\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select Référence,Taille from \"COUPES - VENT\" where Taille like 'X%' order by Référence";
		resultSetAttendu = "Référence,Taille\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "300023,XL\n";
		resultSetAttendu += "300024,XXL\n";
		resultSetAttendu += "300031,XL\n";
		resultSetAttendu += "300043,XL\n";
		resultSetAttendu += "300044,XXL\n";
		resultSetAttendu += "300052,XL\n";
		resultSetAttendu += "300057,XL\n";
		resultSetAttendu += "300062,XL\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select Référence,Taille,virage from only(HUDSON) where Taille in ('XL','M','S')";
		resultSetAttendu = "Référence,Taille,virage\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "300062,XL,1\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select Référence, Taille from CASQUES where Taille is not null";
		resultSetAttendu = "Référence,Taille\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select Référence,Taille,virage from only(HUDSON) where virage/3+2 = 3 or Taille like 'X%' order by virage";
		resultSetAttendu = "Référence,Taille,virage\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "300062,XL,1\n";
		resultSetAttendu += "300061,L,3\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select Référence,Taille,virage from only(HUDSON) where virage/3+2 = 3 and Taille like 'X%'";
		resultSetAttendu = "Référence,Taille,virage\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT g.Référence, c.Référence FROM GANTS g, CHAUSSONS c WHERE g.Taille = c.Taille";
		resultSetAttendu = "Référence,Référence\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT g.Référence, c.Référence FROM GANTS g, CHAUSSONS c WHERE g.Taille > c.Taille and c.Référence= '354603' order by g.Référence desc";
		resultSetAttendu = "Référence,Référence\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "389502,354603\n";
		resultSetAttendu += "389501,354603\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select c1.Référence, c1.Taille, c2.Référence, c2.Taille, c1.virage, c2.virage FROM HUDSON c1, HUDSON as c2 WHERE c1.virage = c2.virage+2";
		resultSetAttendu = "Référence,Taille,Référence,Taille,virage,virage\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "300061,L,300062,XL,3,1\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT g.Référence FROM GANTS as g WHERE g.Taille > all (select c.Taille from  only(CHAUSSONS) as c) order by Référence";
		resultSetAttendu = "Référence\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "389501\n";
		resultSetAttendu += "389502\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select c1.Référence,c1.virage FROM only(HUDSON) c1 WHERE c1.virage in (select c2.virage+2 from only(HUDSON) as c2)";
		resultSetAttendu = "Référence,virage\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "300061,3\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		queryOntoQL = "SELECT Size from HUDSON WHERE its_muff in (select m from only(MUFFS) m WHERE m.its_slalom.Reference = '305000') order by Size desc";
		resultSetAttendu = "Size\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "XL\n";
		resultSetAttendu += "L\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT h.its_muff.Reference from only(HUDSON) h";
		resultSetAttendu = "Reference\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "null\n";
		resultSetAttendu += "null\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT its_muff.its_slalom.Reference from only(HUDSON) ";
		resultSetAttendu = "Reference\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "305000\n";
		resultSetAttendu += "305000\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT Reference from only(HUDSON) where its_muff.its_slalom.Reference like '3006%'";
		resultSetAttendu = "Reference\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT h1.its_muff.its_slalom.Reference,h2.its_muff.Reference from only(HUDSON) h1, only(HUDSON) h2 where h2.its_muff.Reference is not null ";
		resultSetAttendu = "Reference,Reference\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT h1.its_muff.its_slalom.Reference,h2.its_muff.Reference from only(HUDSON) h1, only(HUDSON) h2 where  h1.its_muff.its_slalom.Reference > h2.its_muff.Reference or (h2.its_muff.Reference is null)";
		resultSetAttendu = "Reference,Reference\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "305000,null\n";
		resultSetAttendu += "305000,null\n";
		resultSetAttendu += "305000,null\n";
		resultSetAttendu += "305000,null\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select max(virage) FROM only(HUDSON)";
		resultSetAttendu = "max\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "3\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select min(virage) FROM only(HUDSON)";
		resultSetAttendu = "min\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select avg(virage) FROM only(HUDSON)";
		resultSetAttendu = "avg\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "2.0000000000000000\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select sum(virage) FROM only(HUDSON)";
		resultSetAttendu = "sum\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "4\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select sum(virage+100/3) FROM only(HUDSON)";
		resultSetAttendu = "sum\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "70\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT count(Reference), 'Taille-' || Size || '-' FROM CAGS group by Size having Size = 'XL' or Size='L' order by Size asc";
		resultSetAttendu = "count,concatenation\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "6,Taille-L-\n";
		resultSetAttendu += "6,Taille-XL-\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select count(Reference) FROM CAGS";
		resultSetAttendu = "count\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "24\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select count(Reference), Size FROM CAGS WHERE Reference like '%3%' GROUP BY Size HAVING Size = 'XL' OR Size = 'L' ORDER BY Size DESC";
		resultSetAttendu = "count,Size\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "6,XL\n";
		resultSetAttendu += "6,L\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select max(its_muff.its_slalom.Reference) from only(HUDSON)";
		resultSetAttendu = "max\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "305000\n";
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "select size from only(HUDSON)";
		try {
			statement.executeQuery(queryOntoQL);
			Assert.fail();
		} catch (JOBDBCException oExc) {
			Assert.assertEquals(oExc.getMessage(),
					"size is not a property defined on the namespace http://lisi.ensma.fr/");
		}

		queryOntoQL = "select virage from only(HUSON)";
		try {
			statement = s.createOntoQLStatement();
			statement.executeQuery(queryOntoQL);
			Assert.fail();
		} catch (JOBDBCException oExc) {
			Assert.assertEquals(oExc.getMessage(),
					"HUSON is not a valid class name on the namespace http://lisi.ensma.fr/");
		}

		queryOntoQL = "select virage+2-Size from only(HUDSON)";
		try {
			statement.executeQuery(queryOntoQL);
			Assert.fail();
		} catch (JOBDBCException oExc) {
			Assert.assertEquals(oExc.getMessage(), "The operator '-' can not be used on Size");
		}

		s.close();

	}

	public String ResultSetToString(ResultSet rs) {
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

	/**
	 * Test the extension of the core model to OntoDB.
	 * 
	 * @throws SQLException
	 * @throws QueryException
	 */
	@Test
	public void testFullOntologyModelQuery() throws SQLException, QueryException {
		OntoQLSession s = getSession();

		// revision
		String queryOntoQL = "SELECT #revision FROM #class where #name[en] like 'CAGS'";
		String resultSetAttendu = "revision\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "001\n";
		OntoQLStatement statement = s.createOntoQLStatement();
		String resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// note
		queryOntoQL = "SELECT #name[fr] FROM #class where #note[en] like '%Suitable%'";
		resultSetAttendu = "name\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "DAMES DE NAGE PLASTIQUE\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// remark
		queryOntoQL = "SELECT #name[fr] FROM #class where #remark[en] is not null";
		resultSetAttendu = "name\n";
		resultSetAttendu += "-------------------------------------\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// short name
		queryOntoQL = "SELECT #shortName[en] FROM #class where #name[en] = 'CAGS'";
		resultSetAttendu = "shortName\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "co-vent\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		// Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// namespace
		queryOntoQL = "SELECT #definedBy.#oid FROM #class where #name[en] = 'CAGS'";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1016\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #definedBy.#namespace FROM #class where #name[en] = 'CAGS'";
		resultSetAttendu = "namespace\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "http://lisi.ensma.fr/\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #revision FROM #property where #name[en] like 'Size'";
		resultSetAttendu = "revision\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "001\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// note
		queryOntoQL = "SELECT #name[fr] FROM #property where #note[en] is not null";
		resultSetAttendu = "name\n";
		resultSetAttendu += "-------------------------------------\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// remark
		queryOntoQL = "SELECT #name[fr] FROM #property where #remark[en] is not null";
		resultSetAttendu = "name\n";
		resultSetAttendu += "-------------------------------------\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// short name
		queryOntoQL = "SELECT #shortName[en] FROM #property where #name[en] = 'Size'";
		resultSetAttendu = "shortName\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "          \n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// document
		queryOntoQL = "SELECT #oid FROM #document";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// document-attribut
		queryOntoQL = "SELECT #docOfDefinition.#oid FROM #class where #oid='1060'";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "null\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// dateOfCurrentVersion
		queryOntoQL = "SELECT #dateOfCurrentVersion, #dateOfOriginalDefinition, #dateOfCurrentRevision FROM #class where #oid='1060'";
		resultSetAttendu = "dateOfCurrentVersion,dateOfOriginalDefinition,dateOfCurrentRevision\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "null,null,null\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// icon
		queryOntoQL = "SELECT #icon.#oid FROM #class where #name[en]='CAGS'";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "10125\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// dateOfCurrentVersion for property
		queryOntoQL = "SELECT #dateOfCurrentVersion, #dateOfOriginalDefinition, #dateOfCurrentRevision FROM #property where #name[fr]='Taille'";
		resultSetAttendu = "dateOfCurrentVersion,dateOfOriginalDefinition,dateOfCurrentRevision\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "null,null,null\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #icon.#oid FROM #property where #name[fr]='Taille'";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "null\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// document-attribut
		queryOntoQL = "SELECT #docOfDefinition.#oid FROM #property where #name[fr]='Taille'";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "null\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// Entity concept
		queryOntoQL = "SELECT #oid, #code, #definition[fr] FROM #concept where #name[fr] = 'Taille'";
		resultSetAttendu = "oid,code,definition\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1202,71DC2486B78EF,\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// short name
		queryOntoQL = "SELECT #shortName[en] FROM #concept where #name[en] = 'Size'";
		resultSetAttendu = "shortName\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "          \n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// namespace
		queryOntoQL = "SELECT #definedBy.#oid FROM #property where #name[fr] = 'Taille'";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1016\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #definedBy.#namespace FROM #property where #name[fr] = 'Taille'";
		resultSetAttendu = "namespace\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "http://lisi.ensma.fr/\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #revision FROM #nonDependentProperty where #name[en] like 'Size'";
		resultSetAttendu = "revision\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "001\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #name[en] FROM #contextProperty";
		resultSetAttendu = "name\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "owner\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #name[en] FROM #dependentProperty";
		resultSetAttendu = "name\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "lifeTime\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #name[en], c.#name[en] FROM #dependentProperty p, unnest(p.#dependsOn) as c";
		resultSetAttendu = "name,name\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "lifeTime,owner\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		s.close();
	}

	@Test
	public void testOntologyQuery() throws SQLException, QueryException {
		OntoQLSession s = getSession();

		// Query on ontology with star
		String queryOntoQL = "SELECT * FROM #ontology";
		String resultSetAttendu = "oid,namespace\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1016,http://lisi.ensma.fr/\n";
		OntoQLStatement statement = s.createOntoQLStatement();
		String resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT * FROM #datatype where #oid = 6235";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "6235\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT * FROM #refType";
		resultSetAttendu = "oid,onClass\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "6235,1097\n";
		resultSetAttendu += "6224,1105\n";
		resultSetAttendu += "6269,1062\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT * FROM #collectionType order by #oid";
		resultSetAttendu = "oid,ofDatatype\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "6256,6258\n";
		resultSetAttendu += "6266,6269\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT * FROM #booleanType";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// Query on ontology
		queryOntoQL = "SELECT #namespace FROM #ontology";
		resultSetAttendu = "namespace\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "http://lisi.ensma.fr/\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// Selection-Restriction
		queryOntoQL = "SELECT #oid FROM #class where #code like '71DC24A4C2257'";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1036\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// Selection-Restriction
		queryOntoQL = "SELECT #code, #version FROM #class where #code like '71DC24A4C2257'";
		resultSetAttendu = "code,version\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "71DC24A4C2257,001\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// Selection-Restriction
		queryOntoQL = "SELECT c1.#code, c2.#version FROM #class c1, #class c2 where c1.#code like '71DC24A4C2257' and c2.#code like '71DC24A4C2257'";
		resultSetAttendu = "code,version\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "71DC24A4C2257,001\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// Selection-Restriction on multilingual attribut
		queryOntoQL = "SELECT #name[en] FROM #class where #name[en] = 'CAGS'";
		resultSetAttendu = "name\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "CAGS\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// Selection-Restriction on multilingual attribut with default language
		queryOntoQL = "SELECT #name FROM #class where #name[en] = 'CAGS'";
		resultSetAttendu = "name\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "CAGS\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// Selection-Restriction on multilingual attribut
		queryOntoQL = "SELECT #name[fr] FROM #class where #name[en] = 'CAGS'";
		resultSetAttendu = "name\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "COUPES - VENT\n";
		statement = s.createOntoQLStatement();

		// Selection-Restriction on multilingual attribut
		queryOntoQL = "SELECT count(*) FROM #class where #name[en] like '%C'";
		resultSetAttendu = "count\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1\n";
		statement = s.createOntoQLStatement();

		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// Add Test on name on non multilingue catalog
		// AutoJoin
		queryOntoQL = "SELECT c1.#name[en], c2.#name[fr] FROM #class c1, #class c2 where c1.#version <> c2.#version  ";
		resultSetAttendu = "name,name\n";
		resultSetAttendu += "-------------------------------------\n";
		statement = s.createOntoQLStatement();

		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// Selection-Restriction on multilingual attribut
		queryOntoQL = "SELECT #definition[fr],#definition[en] FROM #class where #name[en] = 'JUNIOR'";
		resultSetAttendu = "definition,definition\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "Casque enfant en polypropylène injecté, dégrafage rapide par clip, conforme à la norme CE EN 1385.,\"Junior\" kid helmet made of polypropylene by injection. Quick unclap with a clip. Standard CE EN 1385.\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// Other entity
		queryOntoQL = "SELECT p1.#oid FROM #property p1 where p1.#oid=1204 ";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1204\n";
		statement = s.createOntoQLStatement();

		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT p1.#code, p1.#version FROM #property p1 where p1.#oid=1204 ";
		resultSetAttendu = "code,version\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "71DC24862420C,001\n";
		statement = s.createOntoQLStatement();

		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT p1.#name[fr] FROM #property p1 where p1.#oid =1204 ";
		resultSetAttendu = "name\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "Référence\n";
		statement = s.createOntoQLStatement();

		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #name[fr] FROM #property WHERE #version > all (SELECT c.#version FROM #class c)  ";
		resultSetAttendu = "name\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "Dimensions\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// for the moment retrieve the oid - latter retrieve the object
		queryOntoQL = "SELECT #scope.#oid FROM #property WHERE #name[en] = 'Size'";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1040\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #scope.#oid, #version, #code FROM #property WHERE #name[en] = 'Size'";
		resultSetAttendu = "oid,version,code\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1040,001,71DC2486B78EF\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #scope.#name[fr] FROM #property WHERE #oid = 1204";
		resultSetAttendu = "name\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "EQUIPEMENT DE LA PERSONNE/SECURITE\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #scope.#name[fr] FROM #property WHERE #name[en] = 'Size'";
		resultSetAttendu = "name\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "EQUIPEMENT DE LA PERSONNE/SECURITE\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #scope.#name[fr],#scope.#name[en] FROM #property WHERE #name[en] = 'Size'";
		resultSetAttendu = "name,name\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "EQUIPEMENT DE LA PERSONNE/SECURITE,PERSONAL EQUIPEMENT/SAFETY\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #range.#oid FROM #property WHERE #name[en] = 'Size'";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1560\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #oid FROM #DATATYPE where #oid < 1559";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1480\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #oid FROM #refType";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "6224\n";
		resultSetAttendu += "6235\n";
		resultSetAttendu += "6269\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #oid FROM #collectionType";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "6256\n";
		resultSetAttendu += "6266\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #oid FROM #primitiveType where #oid = 1480";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1480\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #oid FROM #stringType where #oid=2182";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "2182\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #oid FROM #booleanType";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #oid FROM #intType";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "6243\n";
		resultSetAttendu += "2071\n";
		resultSetAttendu += "2078\n";
		resultSetAttendu += "2127\n";
		resultSetAttendu += "2135\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #oid FROM #realType where #oid=1618";
		resultSetAttendu = "oid\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1618\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #onClass FROM #refType where #oid = 6224";
		resultSetAttendu = "onClass\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1105\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #onClass.#name[en] FROM #refType where #oid = 6224";
		resultSetAttendu = "name\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "MUFFS\n";

		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT #ofDatatype FROM #collectionType where #oid = 6256";
		resultSetAttendu = "ofDatatype\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "6258\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// Dur
		queryOntoQL = "SELECT d.#oid, CASE WHEN d IS OF (#refType) THEN 'refType' WHEN d IS OF (#collectionType) THEN 'collectionType' ELSE 'primitiveType' END  FROM #datatype d where d.#oid in (6256,6224,1618) order by d.#oid";
		resultSetAttendu = "oid,'refType'\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "1618,primitiveType\n";
		resultSetAttendu += "6224,refType\n";
		resultSetAttendu += "6256,collectionType\n";
		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		queryOntoQL = "SELECT CASE WHEN d IS OF (ONLY #intType, ONLY #realType) THEN 'int or real type exactly' WHEN d IS OF (#numberType) THEN 'int or real type' ELSE 'other type' END FROM #datatype d where d.#oid in (6243,1618)";
		resultSetAttendu = "'int or real type exactly'\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "int or real type exactly\n";
		resultSetAttendu += "int or real type\n";

		statement = s.createOntoQLStatement();
		resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		// Reflechir si il faut utiliser DEREF ou pas.
		// queryOntoQL = "SELECT CASE WHEN #ofDatatype IS OF (refType) THEN
		// treat(#ofDatatype as refType).onClass.#name[en] ELSE 'Ce n''est pas
		// un type référence' END FROM #collectionType";
		// resultSetAttendu = "case\n";
		// resultSetAttendu += "-------------------------------------\n";
		// statement = s.createOntoQLStatement();
		// resultSetObtenu = ResultSetToString(statement
		// .executeQuery(queryOntoQL));
		// Assert.assertEquals(resultSetAttendu, resultSetObtenu);

		s.close();
	}

	/**
	 * Bug QL27 correction
	 */
	@Test
	public void testQL27() {
		OntoQLSession s = getSession();

		// Selection-Restriction
		String queryOntoQL = "select p.#name[fr] FROM #class c, #property p WHERE p.#scope = c and c.#name[fr] like 'HUDSON' order by p.#name[fr] desc";
		String resultSetAttendu = "name\n";
		resultSetAttendu += "-------------------------------------\n";
		resultSetAttendu += "virage\n";
		resultSetAttendu += "its_muff\n";
		OntoQLStatement statement = s.createOntoQLStatement();
		String resultSetObtenu = ResultSetToString(statement.executeQuery(queryOntoQL));
		Assert.assertEquals(resultSetAttendu, resultSetObtenu);
		s.close();
	}

	@Test
	public void test() throws SQLException {
		OntoQLSession s = getSession();
		Transaction beginTransaction = s.beginTransaction();
		beginTransaction.begin();
		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate(
				"CREATE #CLASS SuperClass (DESCRIPTOR (#name[fr] ='SuperClasse') PROPERTIES (prop1 STRING, prop2 STRING))");
		statement.executeUpdate(
				"CREATE #CLASS SubClass UNDER SuperClass (DESCRIPTOR (#name[fr] ='SubClasse') PROPERTIES (prop3 STRING))");
		statement.executeUpdate("CREATE EXTENT OF SubClass (prop1, prop3)"); // prop2 is not defined.
		statement.executeUpdate("INSERT INTO SubClass (prop1, prop3) values ('value11', 'value31')");

		OntoQLResultSet executeQuery = statement
				.executeQuery("SELECT c.#oid from #class c where c.#name = 'SuperClass'");
		executeQuery.next();
		System.out.println(executeQuery.getString(1));

		executeQuery = statement.executeQuery("SELECT c.#oid from #class c where c.#name = 'SubClass'");
		executeQuery.next();
		System.out.println(executeQuery.getString(1));

		executeQuery = statement.executeQuery("SELECT p.prop1, p.prop2 FROM SuperClass p");
		while (executeQuery.next()) {
			System.out.println(executeQuery.getString(1));
		}
		beginTransaction.rollback();
		s.close();
	}
}