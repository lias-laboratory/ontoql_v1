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
package fr.ensma.lisi.ontoql.sparql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;

/**
 * Test the extension of OntoQL to OWL
 * 
 * @author Stéphane JEAN
 */
public class SPARQLToOntoQLTest extends OntoQLTestCase {

	/**
	 * Test the translation of SPARQL content-query into OntoQL content-query
	 */
	@Test
	public void testQueryOnContent() throws SQLException {
		OntoQLSession s = getSession();

		String querySPARQL = "PREFIX ns: <EGALIS_F> " + "SELECT ?f ?size WHERE {?f rdf:type ns:FOC_BABY . "
				+ " ?f ns:its_hudsons ?h . ?h ns:Size ?size " + "} ";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://300075", rs.getString(1));
		Assert.assertEquals("XL", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://300075", rs.getString(1));
		Assert.assertEquals("L", rs.getString(2));

		querySPARQL = "PREFIX ns: <EGALIS_F> " + "SELECT ?f ?h  WHERE {?f rdf:type ns:FOC_BABY . "
				+ " ?f ns:its_hudsons ?h " + "} ";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://300075", rs.getString(1));
		Assert.assertEquals("http://300062", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://300075", rs.getString(1));
		Assert.assertEquals("http://300061", rs.getString(2));

		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?c ?s WHERE {?c rdf:type ns:HUDSON "
				+ "OPTIONAL {?c ns:Size ?s }" + "} ORDER BY DESC(?s)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://300062", rs.getString(1));
		Assert.assertEquals("XL", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://300061", rs.getString(1));
		Assert.assertEquals("L", rs.getString(2));
		Assert.assertFalse(rs.next());

		querySPARQL = "select ns:Size from ns:HUDSON USING NAMESPACE ns='http://lisi.ensma.fr/' union select ns:Size from ns:MUFFS USING NAMESPACE ns='http://lisi.ensma.fr/'";
		rs = statement.executeQuery(querySPARQL);
		List<String> values = new ArrayList<String>();
		Assert.assertTrue(rs.next());
		values.add(rs.getString(1));
		Assert.assertTrue(rs.next());
		values.add(rs.getString(1));
		Assert.assertTrue(rs.next());
		values.add(rs.getString(1));
		Assert.assertTrue(rs.next());
		values.add(rs.getString(1));
		Assert.assertFalse(rs.next());

		values.contains("L");
		values.contains("Taille unique");
		values.contains("XL");
		values.contains(null);

		// two classes with the union operator
		// and the same properties
		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?s WHERE { { ?c rdf:type ns:HUDSON . "
				+ " OPTIONAL {?c ns:Size ?s} } UNION  " + " {?c rdf:type ns:MUFFS . OPTIONAL {?c ns:Size ?s} } }";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		values = new ArrayList<String>();
		Assert.assertTrue(rs.next());
		values.add(rs.getString(1));
		Assert.assertTrue(rs.next());
		values.add(rs.getString(1));
		Assert.assertTrue(rs.next());
		values.add(rs.getString(1));
		Assert.assertTrue(rs.next());
		values.add(rs.getString(1));
		Assert.assertFalse(rs.next());

		values.contains("L");
		values.contains("Taille unique");
		values.contains("XL");
		values.contains(null);

		querySPARQL = "select ns:Size,ns:virage,NULL from ns:HUDSON USING NAMESPACE ns='http://lisi.ensma.fr/' union select ns:Size,NULL,ns:Reference from ns:ONTARIO USING NAMESPACE ns='http://lisi.ensma.fr/'";
		rs = statement.executeQuery(querySPARQL);

		List<List<String>> matrixValues = new ArrayList<List<String>>();

		Assert.assertTrue(rs.next());
		values = new ArrayList<String>();
		values.add(rs.getString(1));
		values.add(rs.getString(2));
		values.add(rs.getString(3));
		matrixValues.add(values);

		Assert.assertTrue(rs.next());
		values = new ArrayList<String>();
		values.add(rs.getString(1));
		values.add(rs.getString(2));
		values.add(rs.getString(3));
		matrixValues.add(values);

		Assert.assertTrue(rs.next());
		values = new ArrayList<String>();
		values.add(rs.getString(1));
		values.add(rs.getString(2));
		values.add(rs.getString(3));
		matrixValues.add(values);

		Assert.assertTrue(rs.next());
		values = new ArrayList<String>();
		values.add(rs.getString(1));
		values.add(rs.getString(2));
		values.add(rs.getString(3));
		matrixValues.add(values);

		Assert.assertTrue(rs.next());
		values = new ArrayList<String>();
		values.add(rs.getString(1));
		values.add(rs.getString(2));
		values.add(rs.getString(3));
		matrixValues.add(values);

		Assert.assertTrue(rs.next());
		values = new ArrayList<String>();
		values.add(rs.getString(1));
		values.add(rs.getString(2));
		values.add(rs.getString(3));
		matrixValues.add(values);

		Assert.assertFalse(rs.next());

		values = new ArrayList<String>();
		values.add("L");
		values.add("3");
		values.add(null);
		Assert.assertTrue(matrixValues.contains(values));
		values = new ArrayList<String>();
		values.add("L");
		values.add(null);
		values.add("300030");
		Assert.assertTrue(matrixValues.contains(values));
		values = new ArrayList<String>();
		values.add("M");
		values.add(null);
		values.add("300029");
		Assert.assertTrue(matrixValues.contains(values));
		values = new ArrayList<String>();
		values.add("S");
		values.add(null);
		values.add("300028");
		Assert.assertTrue(matrixValues.contains(values));
		values = new ArrayList<String>();
		values.add("XL");
		values.add("1");
		values.add(null);
		Assert.assertTrue(matrixValues.contains(values));
		values = new ArrayList<String>();
		values.add("XL");
		values.add(null);
		values.add("300031");
		Assert.assertTrue(matrixValues.contains(values));

		// two classes with the union operator
		// and different properties
		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?s ?v ?r WHERE { { ?c rdf:type ns:HUDSON . "
				+ " OPTIONAL {?c ns:Size ?s} OPTIONAL {?c ns:virage ?v} } UNION  "
				+ " {?c rdf:type ns:ONTARIO . OPTIONAL {?c ns:Size ?s} OPTIONAL {?c ns:Reference ?r} } }";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);

		matrixValues = new ArrayList<List<String>>();

		Assert.assertTrue(rs.next());
		values = new ArrayList<String>();
		values.add(rs.getString(1));
		values.add(rs.getString(2));
		values.add(rs.getString(3));
		matrixValues.add(values);

		Assert.assertTrue(rs.next());
		values = new ArrayList<String>();
		values.add(rs.getString(1));
		values.add(rs.getString(2));
		values.add(rs.getString(3));
		matrixValues.add(values);

		Assert.assertTrue(rs.next());
		values = new ArrayList<String>();
		values.add(rs.getString(1));
		values.add(rs.getString(2));
		values.add(rs.getString(3));
		matrixValues.add(values);

		Assert.assertTrue(rs.next());
		values = new ArrayList<String>();
		values.add(rs.getString(1));
		values.add(rs.getString(2));
		values.add(rs.getString(3));
		matrixValues.add(values);

		Assert.assertTrue(rs.next());
		values = new ArrayList<String>();
		values.add(rs.getString(1));
		values.add(rs.getString(2));
		values.add(rs.getString(3));
		matrixValues.add(values);

		Assert.assertTrue(rs.next());
		values = new ArrayList<String>();
		values.add(rs.getString(1));
		values.add(rs.getString(2));
		values.add(rs.getString(3));
		matrixValues.add(values);

		values = new ArrayList<String>();
		values.add("L");
		values.add("3");
		values.add(null);
		Assert.assertTrue(matrixValues.contains(values));

		values = new ArrayList<String>();
		values.add("L");
		values.add(null);
		values.add("300030");
		Assert.assertTrue(matrixValues.contains(values));

		values = new ArrayList<String>();
		values.add("M");
		values.add(null);
		values.add("300029");
		Assert.assertTrue(matrixValues.contains(values));

		values = new ArrayList<String>();
		values.add("S");
		values.add(null);
		values.add("300028");
		Assert.assertTrue(matrixValues.contains(values));

		values = new ArrayList<String>();
		values.add("XL");
		values.add("1");
		values.add(null);
		Assert.assertTrue(matrixValues.contains(values));

		values = new ArrayList<String>();
		values.add("XL");
		values.add(null);
		values.add("300031");
		Assert.assertTrue(matrixValues.contains(values));

		// One class without Optional behind properties
		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?c ?s WHERE {?c rdf:type ns:MUFFS . " + "?c ns:Size ?s "
				+ "} ORDER BY ASC(?s)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://389000", rs.getString(1));
		Assert.assertEquals("Taille unique", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://387000", rs.getString(1));
		Assert.assertEquals("Taille unique", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://388800", rs.getString(1));
		Assert.assertEquals("Taille unique", rs.getString(2));
		Assert.assertFalse(rs.next());

		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?c ?s WHERE {?c rdf:type ns:MUFFS . "
				+ "?c ns:Size ?s FILTER(?c=<http://389000>) " + "} ORDER BY DESC(?s)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://389000", rs.getString(1));
		Assert.assertEquals("Taille unique", rs.getString(2));
		Assert.assertFalse(rs.next());

		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?c ?s WHERE {?c rdf:type ns:HUDSON "
				+ "OPTIONAL {?c ns:Size ?s } FILTER (?s = 'L')" + "} ORDER BY DESC(?s)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://300061", rs.getString(1));
		Assert.assertEquals("L", rs.getString(2));
		Assert.assertFalse(rs.next());

		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?c ?s WHERE {?c rdf:type ns:HUDSON "
				+ "OPTIONAL {?c ns:Size ?s } FILTER (?s = 'L' && ?c=<http://300061>)" + "} ORDER BY DESC(?s)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://300061", rs.getString(1));
		Assert.assertEquals("L", rs.getString(2));
		Assert.assertFalse(rs.next());

		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?c ?s WHERE {?c rdf:type ns:HUDSON "
				+ "OPTIONAL {?c ns:Size ?s } FILTER (?s = 'L' || ?s='XL')" + "} ORDER BY DESC(?s)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://300062", rs.getString(1));
		Assert.assertEquals("XL", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://300061", rs.getString(1));
		Assert.assertEquals("L", rs.getString(2));
		Assert.assertFalse(rs.next());

		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?c ?s WHERE {?c rdf:type ns:MUFFS "
				+ "OPTIONAL {?c ns:Size ?s } FILTER (bound(?s))" + "} ORDER BY DESC(?s)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://389000", rs.getString(1));
		Assert.assertEquals("Taille unique", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertTrue(rs.next());
		Assert.assertFalse(rs.next());

		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?c ?s WHERE {?c rdf:type ns:MUFFS "
				+ "OPTIONAL {?c ns:Size ?s } FILTER (!bound(?s))" + "} ORDER BY DESC(?s)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://", rs.getString(1));
		Assert.assertNull(rs.getString(2));
		Assert.assertFalse(rs.next());

		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?c ?v WHERE {?c rdf:type ns:HUDSON "
				+ "OPTIONAL {?c ns:virage ?v} FILTER (?v > 2)" + "} ORDER BY DESC(?v)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://300061", rs.getString(1));
		Assert.assertEquals("3", rs.getString(2));
		Assert.assertFalse(rs.next());

		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?c ?v WHERE {?c rdf:type ns:HUDSON "
				+ "OPTIONAL {?c ns:virage ?v} FILTER (?v < 2)" + "} ORDER BY DESC(?v)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://300062", rs.getString(1));
		Assert.assertEquals("1", rs.getString(2));
		Assert.assertFalse(rs.next());

		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?c ?v WHERE {?c rdf:type ns:HUDSON "
				+ "OPTIONAL {?c ns:virage ?v } FILTER (?v != 3)" + "} ORDER BY DESC(?v)";
		statement = s.createOntoQLStatement();
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://300062", rs.getString(1));
		Assert.assertEquals("1", rs.getString(2));
		Assert.assertFalse(rs.next());

		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?c ?v WHERE {?c rdf:type ns:HUDSON "
				+ "OPTIONAL {?c ns:virage ?v } FILTER (?v * 2 / 2  - 2 = 1)" + "} ORDER BY DESC(?v)";
		statement = s.createOntoQLStatement();
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://300061", rs.getString(1));
		Assert.assertEquals("3", rs.getString(2));
		Assert.assertFalse(rs.next());

		Transaction t = s.beginTransaction();
		// we put a NULL value
		String ontoQLInsert = "insert into HUDSON (Reference) values ('33')";
		statement.executeUpdate(ontoQLInsert);
		String ontoQL = "SELECT DISTINCT COALESCE(o.Size, h.Size), o.Size  from ns:HUDSON h JOIN ns:ONTARIO o ON (o.Size=h.Size OR o.Size IS NULL OR h.Size IS NULL) order by o.Size desc USING namespace ns='http://lisi.ensma.fr/' ";
		rs = statement.executeQuery(ontoQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("XL", rs.getString(1));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("S", rs.getString(1));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("M", rs.getString(1));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("L", rs.getString(1));
		Assert.assertFalse(rs.next());

		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?s WHERE {?c rdf:type ns:HUDSON "
				+ "OPTIONAL {?c ns:Size ?s } ?o rdf:type ns:ONTARIO " + "OPTIONAL {?o ns:Size ?s } "
				+ "} ORDER BY DESC(?s)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("XL", rs.getString(1));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("S", rs.getString(1));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("M", rs.getString(1));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("L", rs.getString(1));
		Assert.assertFalse(rs.next());

		ontoQLInsert = "insert into HUDSON (Size) values ('XXL')";
		statement.executeUpdate(ontoQLInsert);
		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?s WHERE {?c rdf:type ns:HUDSON "
				+ "OPTIONAL {?c ns:Size ?s } OPTIONAL { ?o rdf:type ns:ONTARIO " + "OPTIONAL {?o ns:Size ?s } } "
				+ "} ORDER BY DESC(?s)";

		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("XXL", rs.getString(1));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("XL", rs.getString(1));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("S", rs.getString(1));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("M", rs.getString(1));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("L", rs.getString(1));
		Assert.assertFalse(rs.next());

		ontoQLInsert = "insert into HUDSON (Size) values ('XL')";
		statement.executeUpdate(ontoQLInsert);
		querySPARQL = "PREFIX ns: <EGALIS_FR> " + "SELECT ?s ?r WHERE {?c rdf:type ns:HUDSON "
				+ "OPTIONAL {?c ns:Size ?s } OPTIONAL {?c ns:Reference ?r }  ?o rdf:type ns:ONTARIO "
				+ "OPTIONAL {?o ns:Size ?s } OPTIONAL {?o ns:Reference ?r }  " + "} ORDER BY DESC(?s)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("XL", rs.getString(1));
		Assert.assertEquals("300031", rs.getString(2));
		Assert.assertFalse(rs.next());

		t.rollback();

		s.close();
	}

	/**
	 * Test the translation of SPARQL content-query into OntoQL content-query
	 */
	@Test
	public void testQueryOnOntology() throws SQLException {
		OntoQLSession s = getSession();

		String querySPARQL = "SELECT ?p1name ?p1def ?p2name WHERE {?p1 rdf:type rdfs:Class . "
				+ "  ?p1 rdfs:label ?p1name . OPTIONAL {?p1 rdfs:comment ?p1def}  FILTER (?p1name = 'HUDSON') OPTIONAL { "
				+ "  ?p2 rdf:type rdfs:Property . ?p2 rdfs:label ?p2name . ?p2 rdf:domain ?p1 . OPTIONAL {?p2 rdfs:comment ?p1def}  } }";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("HUDSON", rs.getString(1));
		Assert.assertEquals(
				"High performance dry cag. Nylon heavy resistance fabric. Taped seams. Inner twin waist. Neoprene belt.",
				rs.getString(2));
		Assert.assertNull(rs.getString(3));
		Assert.assertFalse(rs.next());

		querySPARQL = "SELECT ?namecsup WHERE {?c rdf:type rdfs:Class "
				+ "  OPTIONAL {?c rdfs:subClassOf ?csup} FILTER (?c = <EGALIS_FR71DC2FE26F151>) "
				+ " ?csup rdf:type rdfs:Class OPTIONAL {?csup rdfs:label ?namecsup} } ORDER BY asc(?namecsup)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("CAGS", rs.getString(1));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("PERSONAL EQUIPEMENT/SAFETY", rs.getString(1));

		querySPARQL = "SELECT ?csup WHERE {?c rdf:type rdfs:Class "
				+ " OPTIONAL {?c rdfs:label ?l} OPTIONAL {?c rdfs:subClassOf ?csup} FILTER (?c = <EGALIS_FR71DC2FE26F151>) }";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("EGALIS_FR71DC247C9EE8C", rs.getString(1));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("EGALIS_FR71DC2FDBFD904", rs.getString(1));

		// two entities with the union operator
		// and the same attributes
		querySPARQL = "SELECT ?n WHERE { { ?c rdf:type rdfs:Class . " + " ?c rdfs:label ?n } UNION  "
				+ " {?c rdf:type rdfs:Property . ?c rdfs:label ?n } }";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		List<String> values = new ArrayList<String>();

		Assert.assertTrue(rs.next());
		values.add(rs.getString(1));
		Assert.assertTrue(rs.next());
		values.add(rs.getString(1));
		Assert.assertTrue(rs.next());
		values.add(rs.getString(1));

		values.contains("          ");
		values.contains("3 LITERS PLASTIC PUMP");
		values.contains("3mm NEOPRENE SPRAY DECK");

		querySPARQL = "SELECT ?l WHERE {?c rdf:type rdfs:Class " + " OPTIONAL {?c rdfs:label ?l} }"
				+ " ORDER BY DESC(?l)";
		statement = s.createOntoQLStatement();
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("ZICRAL TUBE DIAMETER 30", rs.getString(1));

		querySPARQL = "SELECT ?comment WHERE {?c rdf:type rdfs:Class "
				+ " OPTIONAL {?c rdfs:comment ?comment}  FILTER (bound(?comment)) }" + " ORDER BY DESC(?comment)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("zicral shaft, slalom polypro, montées parallèles PP blades", rs.getString(1));

		querySPARQL = "SELECT ?l WHERE {?c rdf:type rdfs:Class "
				+ " OPTIONAL {?c rdfs:label ?l} FILTER (?l > 'ZICRAL TUBE DIAMETER 28') }" + " ORDER BY DESC(?l)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("ZICRAL TUBE DIAMETER 30", rs.getString(1));
		Assert.assertFalse(rs.next());

		querySPARQL = "SELECT ?l WHERE {?c rdf:type rdfs:Class "
				+ " OPTIONAL {?c rdfs:label ?l} FILTER (?l = 'ZICRAL TUBE DIAMETER 28' || ?l = 'ZICRAL TUBE DIAMETER 30') }"
				+ " ORDER BY DESC(?l)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("ZICRAL TUBE DIAMETER 30", rs.getString(1));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("ZICRAL TUBE DIAMETER 28", rs.getString(1));
		Assert.assertFalse(rs.next());

		querySPARQL = "SELECT ?l WHERE {?c rdf:type rdfs:Class " + " OPTIONAL {?c rdfs:label ?l} FILTER (?l = 'CAGS') }"
				+ " ORDER BY DESC(?l)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("CAGS", rs.getString(1));

		querySPARQL = "SELECT ?l WHERE {?c rdf:type rdfs:Class "
				+ " OPTIONAL {?c rdfs:comment ?l} OPTIONAL {?c rdfs:label ?lbel} FILTER (?lbel='CHARENTE' && bound(?l)) }"
				+ " ORDER BY DESC(?l)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals(
				"Rowing buoyancy aid. Chasuble buoyancy aid. 120g fabric. Alvéo foam. Adjustable side straps (for size M only). Double stitching. Adjustable leg strap.",
				rs.getString(1));

		querySPARQL = "SELECT ?csup WHERE {?c rdf:type rdfs:Class "
				+ " OPTIONAL {?c rdfs:label ?l} OPTIONAL {?c rdfs:subClassOf ?csup} FILTER (?l = 'ONTARIO') }";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("EGALIS_FR71DC247C9EE8C", rs.getString(1));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("EGALIS_FR71DC2FDBFD904", rs.getString(1));

		querySPARQL = "SELECT ?p1name ?p1def ?p2name WHERE {?p1 rdf:type rdfs:Property . "
				+ "  ?p1 rdfs:label ?p1name . OPTIONAL {?p1 rdfs:comment ?p1def}  FILTER (?p1name = 'Size') . "
				+ "  ?p2 rdf:type rdfs:Property . ?p2 rdfs:label ?p2name . OPTIONAL {?p2 rdfs:comment ?p1def}  FILTER (?p2name = 'Reference') }";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("Size", rs.getString(1));
		Assert.assertEquals("          ", rs.getString(2));
		Assert.assertEquals("Reference", rs.getString(3));
		Assert.assertFalse(rs.next());

		querySPARQL = "SELECT ?l WHERE {?c rdf:type rdfs:Class " + " OPTIONAL {?c rdfs:label ?l} FILTER (?l = 'CAGS') }"
				+ " ORDER BY DESC(?l)";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("CAGS", rs.getString(1));

		querySPARQL = "SELECT ?domain ?range WHERE {?p rdf:type rdfs:Property "
				+ " OPTIONAL {?p rdfs:label ?name} OPTIONAL {?p rdfs:domain ?domain} OPTIONAL {?p rdfs:range ?range} FILTER (?name = 'Size') }";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		// Must be corrected (the URI must be retrieved)
		Assert.assertEquals("1040", rs.getString(1));
		Assert.assertEquals("1560", rs.getString(2));

		querySPARQL = "SELECT ?domain ?range WHERE {?p rdf:type rdfs:Property "
				+ " OPTIONAL {?p rdfs:label ?name} OPTIONAL {?p rdfs:domain ?domain} OPTIONAL {?p rdfs:range ?range} FILTER (?name = 'Size') }";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("1040", rs.getString(1));
		Assert.assertEquals("1560", rs.getString(2));

		querySPARQL = "SELECT ?comment WHERE {?p rdf:type rdfs:Property "
				+ " OPTIONAL {?p rdfs:label ?name} OPTIONAL {?p rdfs:comment ?comment} FILTER (?name = 'Size') }";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("          ", rs.getString(1));
		Assert.assertFalse(rs.next());

		querySPARQL = "SELECT ?name ?comment WHERE {?p rdf:type rdfs:Property . "
				+ " ?p rdfs:label ?name . ?p rdfs:comment ?comment  FILTER (?name = 'Size') }";
		statement = s.createOntoQLStatement();
		rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("Size", rs.getString(1));
		Assert.assertEquals("          ", rs.getString(2));
		Assert.assertFalse(rs.next());

		s.close();
	}
}
