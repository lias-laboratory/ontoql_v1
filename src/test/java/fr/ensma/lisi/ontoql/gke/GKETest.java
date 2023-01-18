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
package fr.ensma.lisi.ontoql.gke;

import java.sql.SQLException;

import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.exception.QueryException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.ontoapi.Instance;

/**
 * @author Mickael BARON
 */
public class GKETest extends OntoQLTestCase {

	@Test
	public void testCoordOntology() throws SQLException, QueryException {
		OntoQLSession s = getSession();

		Transaction t = s.beginTransaction();
		String queryOntoQL = "SET LANGUAGE NONE";
		OntoQLStatement statement = s.createOntoQLStatement();
		int res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "INSERT INTO #Ontology (#namespace) VALUES ('http://sabre.inria.fr/sws-2.4/data/geologicalData/ontologies/coord.rdfs#')";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);
		queryOntoQL = "SET NAMESPACE 'http://sabre.inria.fr/sws-2.4/data/geologicalData/ontologies/coord.rdfs#'";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "CREATE #Class CoordinateSystem(DESCRIPTOR (#name[en] = 'Coordinate system', #name[fr] = 'Système	de coordonnées') PROPERTIES (rdfId STRING DESCRIPTOR (#name[en] = 'rdfId', #name[fr] = 'rdfId')))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "CREATE #Class Coordinates(DESCRIPTOR (#name[en] = 'Coordinates', #name[fr] = 'Coordonnées') PROPERTIES (rdfId STRING DESCRIPTOR (#name[en] = 'rdfId', #name[fr] = 'rdfId'), hasCoordinateSystem REF(CoordinateSystem) ARRAY DESCRIPTOR (#name[en] = 'has coordinate system', #name[fr] = 'a pour système de coordonnées')))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "CREATE #Class Geometry( DESCRIPTOR (#name[en] = 'Geometry', #name[fr] = 'Géometrie') PROPERTIES (rdfId STRING DESCRIPTOR (#name[en] = 'rdfId', #name[fr] = 'rdfId'), hasCoordinates REF(Coordinates) ARRAY DESCRIPTOR (#name[en] = 'has coordinates', #name[fr] = 'a pour coordonnées')))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "CREATE #Class GeometricLocalization( DESCRIPTOR (#name[en] = 'Geometric localization',#name[fr] = 'Localisation géométrique') PROPERTIES (rdfId STRING DESCRIPTOR (#name[en] = 'rdfId', #name[fr] = 'rdfId'), hasGeometricShape REF(Geometry) ARRAY DESCRIPTOR (#name[en] = 'has shape', #name[fr] = 'a pour forme géométrique')))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "CREATE #Class Toponymy( DESCRIPTOR (#name[en] = 'Toponymy', #name[fr] = 'Toponyme') PROPERTIES (rdfId STRING DESCRIPTOR (#name[en] = 'rdfId', #name[fr] = 'rdfId')))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "CREATE #Class GeologicalObject( DESCRIPTOR (#name[en] = 'GeologicalObject', #name[fr] = 'GéologicalObject') PROPERTIES (rdfId STRING DESCRIPTOR (#name[en] = 'rdfId', #name[fr] = 'rdfId'), hasToponymy REF(Toponymy) ARRAY DESCRIPTOR (#name[en] = 'has toponymy', #name[fr] = 'a pour toponyme'), hasGeometricLocalization REF(GeometricLocalization) ARRAY DESCRIPTOR (#name[en] = 'has geometric localization', #name[fr] = 'a pour localisation géométrique')))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "CREATE #Class SegmentChain UNDER Geometry( DESCRIPTOR (#name[en] = 'Segment chain',#name[fr] = 'Chaîne de segment'))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "CREATE #Class Polygon UNDER Geometry( DESCRIPTOR (#name[en] = 'Polygon',#name[fr] = 'Polygône'))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "CREATE #Class Point UNDER Geometry( DESCRIPTOR (#name[en] = 'Point',  #name[fr] = 'Point'))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);

		queryOntoQL = "SELECT #code, #name[en], #name[fr] from #Class WHERE #definedBy.#namespace = 'http://sabre.inria.fr/sws-2.4/data/geologicalData/ontologies/coord.rdfs#' order by #name[fr] ASC";
		OntoQLResultSet rset = statement.executeQuery(queryOntoQL);
		rset.next();
		Assert.assertEquals("SegmentChain", rset.getString(1));
		Assert.assertEquals("Segment chain", rset.getString(2));
		Assert.assertEquals("Chaîne de segment", rset.getString(3));
		rset.next();
		Assert.assertEquals("Coordinates", rset.getString(1));
		Assert.assertEquals("Coordinates", rset.getString(2));
		Assert.assertEquals("Coordonnées", rset.getString(3));
		rset.next();
		Assert.assertEquals("GeologicalObject", rset.getString(1));
		Assert.assertEquals("GeologicalObject", rset.getString(2));
		Assert.assertEquals("GéologicalObject", rset.getString(3));
		rset.next();
		Assert.assertEquals("Geometry", rset.getString(1));
		Assert.assertEquals("Geometry", rset.getString(2));
		Assert.assertEquals("Géometrie", rset.getString(3));
		rset.next();
		Assert.assertEquals("GeometricLocalization", rset.getString(1));
		Assert.assertEquals("Geometric localization", rset.getString(2));
		Assert.assertEquals("Localisation géométrique", rset.getString(3));
		rset.next();
		Assert.assertEquals("Point", rset.getString(1));
		Assert.assertEquals("Point", rset.getString(2));
		Assert.assertEquals("Point", rset.getString(3));
		rset.next();
		Assert.assertEquals("Polygon", rset.getString(1));
		Assert.assertEquals("Polygon", rset.getString(2));
		Assert.assertEquals("Polygône", rset.getString(3));
		rset.next();
		Assert.assertEquals("CoordinateSystem", rset.getString(1));
		Assert.assertEquals("Coordinate system", rset.getString(2));
		Assert.assertEquals("Système	de coordonnées", rset.getString(3));
		rset.next();
		Assert.assertEquals("Toponymy", rset.getString(1));
		Assert.assertEquals("Toponymy", rset.getString(2));
		Assert.assertEquals("Toponyme", rset.getString(3));
		Assert.assertFalse(rset.next());

		queryOntoQL = "SELECT #code, #name[en], #name[fr] from #Property WHERE #definedBy.#namespace = 'http://sabre.inria.fr/sws-2.4/data/geologicalData/ontologies/coord.rdfs#' order by #name[fr] ASC";
		rset = statement.executeQuery(queryOntoQL);
		rset.next();
		Assert.assertEquals("hasCoordinates", rset.getString(1));
		Assert.assertEquals("has coordinates", rset.getString(2));
		Assert.assertEquals("a pour coordonnées", rset.getString(3));
		rset.next();
		Assert.assertEquals("hasGeometricShape", rset.getString(1));
		Assert.assertEquals("has shape", rset.getString(2));
		Assert.assertEquals("a pour forme géométrique", rset.getString(3));
		rset.next();
		Assert.assertEquals("hasGeometricLocalization", rset.getString(1));
		Assert.assertEquals("has geometric localization", rset.getString(2));
		Assert.assertEquals("a pour localisation géométrique", rset.getString(3));
		rset.next();
		Assert.assertEquals("hasCoordinateSystem", rset.getString(1));
		Assert.assertEquals("has coordinate system", rset.getString(2));
		Assert.assertEquals("a pour système de coordonnées", rset.getString(3));
		rset.next();
		Assert.assertEquals("hasToponymy", rset.getString(1));
		Assert.assertEquals("has toponymy", rset.getString(2));
		Assert.assertEquals("a pour toponyme", rset.getString(3));
		rset.next();
		Assert.assertEquals("rdfId", rset.getString(1));
		Assert.assertEquals("rdfId", rset.getString(2));
		Assert.assertEquals("rdfId", rset.getString(3));

		queryOntoQL = "SELECT c.rdfId, cs.rdfId from Coordinates c, unnest(c.hasCoordinateSystem) as cs";
		rset = statement.executeQuery(queryOntoQL);
		Assert.assertFalse(rset.next());

		t.rollback();

		s.close();
	}

	public void testBug() throws SQLException {
		OntoQLSession s = getSession();
		Transaction t = s.beginTransaction();

		String queryOntoQL = "INSERT INTO #Ontology (#namespace) VALUES ('test2#')";
		OntoQLStatement statement = s.createOntoQLStatement();
		int res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);
		queryOntoQL = "SET NAMESPACE 'test2#'";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "CREATE #Class Study(DESCRIPTOR (#name[en] = 'Study', #name[fr] = 'Etude')PROPERTIES (rdfId STRING, hasName STRING))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "CREATE EXTENT OF Study(rdfId, hasName)";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "CREATE #Class UnitSystem(DESCRIPTOR (#name[en] = 'UnitSystem', #name[fr] = 'UnitSystem')PROPERTIES (rdfId STRING, hasName STRING))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "CREATE EXTENT OF UnitSystem(rdfId, hasName)";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "CREATE #Class Prospect(DESCRIPTOR (#name[en] = 'Prospect', #name[fr] = 'Prospect')PROPERTIES (rdfId STRING, hasUnitSystem REF(UnitSystem), hasStudy REF(Study) ARRAY, hasName STRING))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "CREATE EXTENT OF Prospect(rdfId, hasUnitSystem, hasStudy, hasName)";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		queryOntoQL = "insert into UnitSystem(rdfId) values('us5')";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);
		queryOntoQL = "insert into UnitSystem(rdfId) values('us6')";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);
		queryOntoQL = "insert into Study(hasName) values('st5')";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);
		queryOntoQL = "insert into Study(hasName) values('st6')";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);
		queryOntoQL = "insert into Prospect(hasName,hasStudy,hasUnitSystem) values('p1',ARRAY(select oid from Study),(select oid from UnitSystem where rdfId = 'us5'))";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);

		queryOntoQL = "select oid from UnitSystem where rdfId = 'us6'";
		OntoQLResultSet resultSet = statement.executeQuery(queryOntoQL);
		resultSet.next();
		String newOid = resultSet.getString(1);

		queryOntoQL = "update Prospect set hasUnitSystem=" + newOid + " where hasName='p1'";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);

		queryOntoQL = "select hasUnitSystem.oid from Prospect where hasName = 'p1'";
		resultSet = statement.executeQuery(queryOntoQL);
		resultSet.next();
		Assert.assertEquals(newOid, resultSet.getString(1));

		queryOntoQL = "select hasStudy from Prospect " + "where hasName='" + "p1" + "'";
		resultSet = statement.executeQuery(queryOntoQL);
		while (resultSet.next()) {
			Object[] test = resultSet.getCollection(1);
			Instance i = (Instance) test[0];
			Assert.assertEquals("st5", i.getStringPropertyValue("hasName"));
			i = (Instance) test[1];
			Assert.assertEquals("st6", i.getStringPropertyValue("hasName"));
			Assert.assertEquals(2, test.length);
		}

		queryOntoQL = "select s.hasName from Prospect p, unnest(p.hasStudy) as s " + "where p.hasName='" + "p1" + "'";
		resultSet = statement.executeQuery(queryOntoQL);
		resultSet.next();
		Assert.assertEquals("st5", resultSet.getString(1));
		resultSet.next();
		Assert.assertEquals("st6", resultSet.getString(1));
		Assert.assertFalse(resultSet.next());

		queryOntoQL = "INSERT INTO Prospect (hasStudy) values(NULL)";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);

		queryOntoQL = "INSERT INTO Prospect (hasUnitSystem) values(NULL)";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(1, res);

		t.rollback();
		s.close();
	}
}
