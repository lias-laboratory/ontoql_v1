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
package fr.ensma.lisi.ontoql.ewokhub;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.jobdbc.impl.OntoQLSessionImpl;
import fr.ensma.lisi.ontoql.ontoapi.Instance;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Mickael BARON
 */
public class EWokHubTest {

	public OntoQLSession sEWokHub;

	private Connection database;

	@Before
	public void setUp() throws Exception {
		try {
			Class.forName("org.postgresql.Driver");
		} catch (Exception ex) {
		}
		try {
			database = DriverManager.getConnection(
					"jdbc:postgresql://" + OntoQLTestCase.HOST + ":" + OntoQLTestCase.PORT + "/OntoQLJUnitTestEWokHub",
					OntoQLTestCase.USR, OntoQLTestCase.PWD);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		sEWokHub = new OntoQLSessionImpl(database);
	}

	// This code is included in the Web Services to load annotations
	private void loadAnnotationOnSegment(String uriDocument, String sourceDocument, String uriSegment,
			String uriPredicate, String uriInstance, OntoQLSession session) throws SQLException {
		// we encode the predicate to optimize the processing
		Map<String, String> rangeOfPredicate = new Hashtable<String, String>();
		rangeOfPredicate.put("hasRef2Datation", "GeochronologicUnit");
		rangeOfPredicate.put("hasRef2Geolocalization", "Territoire_FR");
		rangeOfPredicate.put("hasRef2GeologicalObject", "GeologicalObject");
		rangeOfPredicate.put("hasRef2OtherGeoEntities", "OWLRootClass");
		// identifiy the namespace and the name of the predicate
		int indexOfLastSeparator = uriPredicate.lastIndexOf('/');
		String namePredicate = uriPredicate.substring(indexOfLastSeparator + 1, uriPredicate.length());

		// test if an annotation exists for the resource
		session.setDefaultNameSpace("http://lisi.ensma.fr/");
		String queryOntoQL = "select oid from Document where URI = '" + uriDocument + "'";
		OntoQLStatement statement = session.createOntoQLStatement();
		OntoQLResultSet rs = statement.executeQuery(queryOntoQL);

		// if the document doesn't exist it is created with the segment
		if (!rs.next()) {
			queryOntoQL = "insert into Segment (URI, " + namePredicate + ") values('" + uriSegment
					+ "', ARRAY(select oid from " + rangeOfPredicate.get(namePredicate) + " where URI = '" + uriInstance
					+ "'))";
			statement.executeUpdate(queryOntoQL);
			queryOntoQL = "select oid from Segment where URI = '" + uriSegment + "'";
			rs = statement.executeQuery(queryOntoQL);
			rs.next();
			String oidSegment = rs.getString(1);

			if (sourceDocument != null) {
				queryOntoQL = "insert into Document (URI, source, contains) values('" + uriDocument + "', '"
						+ sourceDocument + "', ARRAY[" + oidSegment + "]))";
			} else {
				queryOntoQL = "insert into Document (URI, contains) values('" + uriDocument + "', ARRAY[" + oidSegment
						+ "]))";
			}
			statement.executeUpdate(queryOntoQL);
		}
		// else we need to know if the segment exists
		else {
			queryOntoQL = "select oid, " + namePredicate + " from Segment where URI = '" + uriSegment + "'";
			rs = statement.executeQuery(queryOntoQL);
			if (!rs.next()) {
				// we create the segment and add it to the document
				queryOntoQL = "insert into Segment (URI, " + namePredicate + ") values('" + uriSegment
						+ "', ARRAY(select oid from " + rangeOfPredicate.get(namePredicate) + " where URI = '"
						+ uriInstance + "'))";
				statement.executeUpdate(queryOntoQL);
				queryOntoQL = "select oid from Segment where URI = '" + uriSegment + "'";
				rs = statement.executeQuery(queryOntoQL);
				rs.next();
				String oidSegment = rs.getString(1);
				queryOntoQL = "update Document set contains = " + oidSegment + " || contains where URI = '"
						+ uriDocument + "'";
				statement.executeUpdate(queryOntoQL);
			} else {
				// add the annotation to the segment (if not already done)
				Instance[] previousInstancesofAnnotations = (Instance[]) rs.getCollection(2);
				queryOntoQL = "select oid from " + rangeOfPredicate.get(namePredicate) + " where URI = '" + uriInstance
						+ "'";
				rs = statement.executeQuery(queryOntoQL);
				rs.next();
				String newInstanceofAnnotation = rs.getString(1);
				boolean hasBeenAlreadyAnnotated = false;
				if (previousInstancesofAnnotations != null) {
					// there is some annotation of the same predicate

					for (int i = 0; i < previousInstancesofAnnotations.length && !hasBeenAlreadyAnnotated; i++) {
						String oidOfPreviousInstancesofAnnotation = previousInstancesofAnnotations[i].getOid();
						if (oidOfPreviousInstancesofAnnotation.equals(newInstanceofAnnotation)) {
							// this document has been already annotated by this
							// instance
							hasBeenAlreadyAnnotated = true;
						}
					}
				}

				if (!hasBeenAlreadyAnnotated) {
					queryOntoQL = "update Segment set " + namePredicate + " = " + newInstanceofAnnotation + " || "
							+ namePredicate + " where URI = '" + uriSegment + "'";
					statement.executeUpdate(queryOntoQL);
				}

			}
		}
	}

	private void loadAnnotationOnSegmentOptimized(Document d, OntoQLSession session) throws SQLException {
		// we encode the predicate to optimize the processing
		Hashtable<String, String> rangeOfPredicate = new Hashtable<String, String>();
		rangeOfPredicate.put("hasRef2Datation", "GeochronologicUnit");
		rangeOfPredicate.put("hasRef2Geolocalization", "Territoire_FR");
		rangeOfPredicate.put("hasRef2GeologicalObject", "GeologicalObject");
		rangeOfPredicate.put("hasRef2OtherGeoEntities", "OWLRootClass");

		// we cache oid corresponding to object URI
		Hashtable<String, String> oidOfObjects = new Hashtable<String, String>();

		List<Segment> listOfSegments = d.getSegments();
		String sourceDocument = d.getSource();
		String uriDocument = d.getUri();

		// test if an annotation exists for the resource
		session.setDefaultNameSpace("http://lisi.ensma.fr/");
		String queryOntoQL = "select oid from Document where URI = '" + d.getUri() + "'";
		OntoQLStatement statement = session.createOntoQLStatement();
		OntoQLResultSet rs = statement.executeQuery(queryOntoQL);

		// if the document doesn't exist it is created with the segment
		if (!rs.next()) {
			String oidOfSegments = "ARRAY[";
			for (Iterator<Segment> iterator = listOfSegments.iterator(); iterator.hasNext();) {
				Segment segment = iterator.next();
				List<Annotation> listOfAnnotations = segment.getAnnotations();
				String listOfPredicates = "";
				String listOfValues = "";
				String uriSegment = segment.getUri();
				for (Iterator<Annotation> iterator2 = listOfAnnotations.iterator(); iterator2.hasNext();) {
					Annotation annotation = iterator2.next();
					String uriPredicate = annotation.getUriPredicate();
					int indexOfLastSeparator = uriPredicate.lastIndexOf('/');
					String namePredicate = uriPredicate.substring(indexOfLastSeparator + 1, uriPredicate.length());
					listOfPredicates += ", " + namePredicate;
					String currentListOfVaues = "ARRAY[";
					List<String> listOfObjects = annotation.getUriObjects();
					for (Iterator<String> iterator3 = listOfObjects.iterator(); iterator3.hasNext();) {
						String object = iterator3.next();
						String oidCorrespondingToObject = (String) oidOfObjects.get(object);
						if (oidCorrespondingToObject == null) {
							String query = "select oid from " + rangeOfPredicate.get(namePredicate) + " where URI = '"
									+ object + "'";
							rs = statement.executeQuery(query);
							rs.next();
							oidCorrespondingToObject = rs.getString(1);
							oidOfObjects.put(object, oidCorrespondingToObject);
						}
						currentListOfVaues += oidCorrespondingToObject + ",";
					}
					currentListOfVaues = currentListOfVaues.substring(0, currentListOfVaues.length() - 1) + "]";
					listOfValues += ", " + currentListOfVaues;
				}
				queryOntoQL = "insert into Segment (URI " + listOfPredicates + ") values('" + uriSegment + "' "
						+ listOfValues + ")";
				statement.executeUpdate(queryOntoQL);
				queryOntoQL = "select oid from Segment where URI = '" + uriSegment + "'";
				rs = statement.executeQuery(queryOntoQL);
				rs.next();
				oidOfSegments += rs.getString(1) + ",";
			}
			oidOfSegments = oidOfSegments.substring(0, oidOfSegments.length() - 1) + "]";
			if (sourceDocument != null) {
				queryOntoQL = "insert into Document (URI, source, contains) values('" + uriDocument + "', '"
						+ sourceDocument + "', " + oidOfSegments + "))";
			} else {
				queryOntoQL = "insert into Document (URI, contains) values('" + uriDocument + "', " + oidOfSegments
						+ "))";
			}
			statement.executeUpdate(queryOntoQL);
		}

		else {
			for (Iterator<Segment> iterator = listOfSegments.iterator(); iterator.hasNext();) {
				Segment segment = iterator.next();
				String uriSegment = segment.getUri();
				List<Annotation> listOfAnnotations = segment.getAnnotations();
				for (Iterator<Annotation> iterator2 = listOfAnnotations.iterator(); iterator2.hasNext();) {
					Annotation annotation = iterator2.next();
					String uriPredicate = annotation.getUriPredicate();
					List<String> listOfObjects = annotation.getUriObjects();
					for (Iterator<String> iterator3 = listOfObjects.iterator(); iterator3.hasNext();) {
						String object = iterator3.next();
						loadAnnotationOnSegment(uriDocument, sourceDocument, uriSegment, uriPredicate, object, session);
					}
				}

			}

		}

	}

	@Test
	public void testLoadAnnotationOnSegment() throws SQLException {
		sEWokHub.setReferenceLanguage(OntoQLHelper.ENGLISH);
		sEWokHub.setDefaultNameSpace("http://lisi.ensma.fr/");

		// prepare the database
		String queryOntoQL = "insert into GeochronologicUnit (URI) values ('PERIOD_SELECTED_BY_USER')";
		OntoQLStatement statement = sEWokHub.createOntoQLStatement();
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into GeochronologicUnit (URI) values ('PERIOD_NOT_SELECTED_BY_USER')";
		statement.executeUpdate(queryOntoQL);

		// a new document with a new segment
		loadAnnotationOnSegment("weblab://crawlerFolder/file6", "nameFile6",
				"weblab://crawlerFolder/file6#9_inriaForGeo_37", "http://ns.inria.fr/ewok/model/hasRef2Geolocalization",
				"http://rdf.insee.fr/geo/COM_06080", sEWokHub);

		loadAnnotationOnSegment("weblab://crawlerFolder/file19", "nameFile19",
				"weblab://crawlerFolder/file19#0_inriaForGeo_1", "http://ns.inria.fr/ewok/model/hasRef2Geolocalization",
				"http://rdf.insee.fr/geo/COM_06080", sEWokHub);

		queryOntoQL = "select seg.URI, z.URI, d.source  from Document d, unnest(d.contains) as seg, Segment as s, unnest(s.hasRef2Geolocalization) as z where s.oid = seg.oid and d.URI = 'weblab://crawlerFolder/file19'";
		statement = sEWokHub.createOntoQLStatement();
		OntoQLResultSet rs = statement.executeQuery(queryOntoQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("weblab://crawlerFolder/file19#0_inriaForGeo_1", rs.getString(1));
		Assert.assertEquals("http://rdf.insee.fr/geo/COM_06080", rs.getString(2));
		Assert.assertEquals("nameFile19", rs.getString(3));
		Assert.assertFalse(rs.next());

		// a new document with a new segment
		loadAnnotationOnSegment("weblab://crawlerFolder/file0", "nameFile0",
				"weblab://crawlerFolder/file0#0_inriaForTime_1", "http://ns.inria.fr/ewok/model/hasRef2Datation",
				"PERIOD_SELECTED_BY_USER", sEWokHub);

		queryOntoQL = "select seg.URI, z.URI, d.source  from Document d, unnest(d.contains) as seg, Segment as s, unnest(s.hasRef2Datation) as z where s.oid = seg.oid and d.URI = 'weblab://crawlerFolder/file0'";
		statement = sEWokHub.createOntoQLStatement();
		rs = statement.executeQuery(queryOntoQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("weblab://crawlerFolder/file0#0_inriaForTime_1", rs.getString(1));
		Assert.assertEquals("PERIOD_SELECTED_BY_USER", rs.getString(2));
		Assert.assertEquals("nameFile0", rs.getString(3));
		Assert.assertFalse(rs.next());

		// an existing document with a new segment
		loadAnnotationOnSegment("weblab://crawlerFolder/file0", "nameFile0",
				"weblab://crawlerFolder/file0#0_inriaForTime_2", "http://ns.inria.fr/ewok/model/hasRef2Datation",
				"PERIOD_NOT_SELECTED_BY_USER", sEWokHub);

		queryOntoQL = "select seg.URI, z.URI, d.source  from Document d, unnest(d.contains) as seg, Segment as s, unnest(s.hasRef2Datation) as z where s.oid = seg.oid and d.URI = 'weblab://crawlerFolder/file0'";
		statement = sEWokHub.createOntoQLStatement();
		rs = statement.executeQuery(queryOntoQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("weblab://crawlerFolder/file0#0_inriaForTime_1", rs.getString(1));
		Assert.assertEquals("PERIOD_SELECTED_BY_USER", rs.getString(2));
		Assert.assertEquals("nameFile0", rs.getString(3));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("weblab://crawlerFolder/file0#0_inriaForTime_2", rs.getString(1));
		Assert.assertEquals("PERIOD_NOT_SELECTED_BY_USER", rs.getString(2));
		Assert.assertEquals("nameFile0", rs.getString(3));
		Assert.assertFalse(rs.next());

		// an existing document with an existing segment and a new annotation
		loadAnnotationOnSegment("weblab://crawlerFolder/file0", "nameFile0",
				"weblab://crawlerFolder/file0#0_inriaForTime_1", "http://ns.inria.fr/ewok/model/hasRef2Datation",
				"PERIOD_NOT_SELECTED_BY_USER", sEWokHub);

		queryOntoQL = "select seg.URI, z.URI, d.source  from Document d, unnest(d.contains) as seg, Segment as s, unnest(s.hasRef2Datation) as z where s.oid = seg.oid and d.URI = 'weblab://crawlerFolder/file0' order by seg.URI asc";
		statement = sEWokHub.createOntoQLStatement();
		rs = statement.executeQuery(queryOntoQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("weblab://crawlerFolder/file0#0_inriaForTime_1", rs.getString(1));
		Assert.assertEquals("PERIOD_SELECTED_BY_USER", rs.getString(2));
		Assert.assertEquals("nameFile0", rs.getString(3));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("weblab://crawlerFolder/file0#0_inriaForTime_1", rs.getString(1));
		Assert.assertEquals("PERIOD_NOT_SELECTED_BY_USER", rs.getString(2));
		Assert.assertEquals("nameFile0", rs.getString(3));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("weblab://crawlerFolder/file0#0_inriaForTime_2", rs.getString(1));
		Assert.assertEquals("PERIOD_NOT_SELECTED_BY_USER", rs.getString(2));
		Assert.assertEquals("nameFile0", rs.getString(3));
		Assert.assertFalse(rs.next());

		// an existing document with an existing segment and an existing
		// annotation
		loadAnnotationOnSegment("weblab://crawlerFolder/file0", "nameFile0",
				"weblab://crawlerFolder/file0#0_inriaForTime_1", "http://ns.inria.fr/ewok/model/hasRef2Datation",
				"PERIOD_NOT_SELECTED_BY_USER", sEWokHub);

		queryOntoQL = "select seg.URI, z.URI, d.source  from Document d, unnest(d.contains) as seg, Segment as s, unnest(s.hasRef2Datation) as z where s.oid = seg.oid and d.URI = 'weblab://crawlerFolder/file0' order by seg.URI asc";
		statement = sEWokHub.createOntoQLStatement();
		rs = statement.executeQuery(queryOntoQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("weblab://crawlerFolder/file0#0_inriaForTime_1", rs.getString(1));
		Assert.assertEquals("PERIOD_SELECTED_BY_USER", rs.getString(2));
		Assert.assertEquals("nameFile0", rs.getString(3));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("weblab://crawlerFolder/file0#0_inriaForTime_1", rs.getString(1));
		Assert.assertEquals("PERIOD_NOT_SELECTED_BY_USER", rs.getString(2));
		Assert.assertEquals("nameFile0", rs.getString(3));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("weblab://crawlerFolder/file0#0_inriaForTime_2", rs.getString(1));
		Assert.assertEquals("PERIOD_NOT_SELECTED_BY_USER", rs.getString(2));
		Assert.assertEquals("nameFile0", rs.getString(3));
		Assert.assertFalse(rs.next());

		queryOntoQL = "delete from Document";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "delete from Segment";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "delete from GeochronologicUnit where URI = 'PERIOD_SELECTED_BY_USER' OR URI = 'PERIOD_NOT_SELECTED_BY_USER'";
		statement.executeUpdate(queryOntoQL);

	}

	@Test
	public void testSPARQLQueryForGeography() throws SQLException {

		sEWokHub.setReferenceLanguage(OntoQLHelper.ENGLISH);
		sEWokHub.setDefaultNameSpace("http://lisi.ensma.fr/");

		// Requète sur la géographie
		String queryOntoQL = "delete from Document";
		OntoQLStatement statement = sEWokHub.createOntoQLStatement();
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "delete from Segment";
		statement.executeUpdate(queryOntoQL);

		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/REG_54", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/DEP_64", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc2", "sourceDoc2", "http://lisi.ensma.fr/seg2",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/DEP_24", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc2", "sourceDoc2", "http://lisi.ensma.fr/seg2-2",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/DEP_64", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc3", "sourceDoc3", "http://lisi.ensma.fr/seg3",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/DEP_64", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc4", "sourceDoc4", "http://lisi.ensma.fr/seg4",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/COM_86115", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc4", "sourceDoc4", "http://lisi.ensma.fr/seg4-2",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/COM_86062", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc5",
				"1992_Lenoir_Geothermal-exploitation-of-the-dogger-in-the-paris-basin-maintenance,-renewal-and-reliability-of-the-geothermal.pdf",
				"http://lisi.ensma.fr/seg5", "http://ns.inria.fr/ewok/model/hasRef2Geolocalization",
				"http://rdf.insee.fr/geo/COM_86062", sEWokHub);

		String querySPARQL = "PREFIX geo: <http://rdf.insee.fr/geo/> "
				+ "PREFIX meta: <http://ns.inria.fr/ewok/model/> " + " PREFIX wl: <http://model.core.weblab.eads.com#> "
				+ " PREFIX dc: <http://purl.org/dc/elements/1.1/> "
				+ "SELECT distinct ?doc ?source  WHERE { { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg . ?seg rdf:type wl:Segment . ?seg meta:hasRef2Geolocalization ?localisation . FILTER(?localisation=<http://rdf.insee.fr/geo/REG_54> || ?localisation=<http://rdf.insee.fr/geo/DEP_24>) }"
				+ " UNION {?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg2 . ?seg2 rdf:type wl:Segment . ?seg2 meta:hasRef2Geolocalization ?localisation2 . ?z rdf:type geo:Territoire_FR . ?z geo:subdivision ?localisation2 . FILTER(?z=<http://rdf.insee.fr/geo/REG_54> || ?z=<http://rdf.insee.fr/geo/DEP_24>) } } ";
		statement = sEWokHub.createOntoQLStatement();
		OntoQLResultSet rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc1", rs.getString(1));
		Assert.assertEquals("sourceDoc1", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc2", rs.getString(1));
		Assert.assertEquals("sourceDoc2", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc4", rs.getString(1));
		Assert.assertEquals("sourceDoc4", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc5", rs.getString(1));
		Assert.assertEquals(
				"1992_Lenoir_Geothermal-exploitation-of-the-dogger-in-the-paris-basin-maintenance,-renewal-and-reliability-of-the-geothermal.pdf",
				rs.getString(2));
		Assert.assertFalse(rs.next());

		StringBuffer XMLFormat = new StringBuffer("<?xml version=\"1.0\"?>\n");
		XMLFormat.append("<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\">\n");
		XMLFormat.append("\t<head>\n");
		XMLFormat.append("\t\t<variable name=\"doc\"/>\n");
		XMLFormat.append("\t\t<variable name=\"source\"/>\n");
		XMLFormat.append("\t</head>\n");
		XMLFormat.append("\t<results>\n");
		XMLFormat.append("\t\t<result>\n");
		XMLFormat.append("\t\t\t<binding name=\"doc\">\n");
		XMLFormat.append("\t\t\t\t<uri>http://lisi.ensma.fr/doc1</uri>\n");
		XMLFormat.append("\t\t\t</binding>\n");
		XMLFormat.append("\t\t\t<binding name=\"source\">\n");
		XMLFormat.append("\t\t\t\t<uri>sourceDoc1</uri>\n");
		XMLFormat.append("\t\t\t</binding>\n");
		XMLFormat.append("\t\t</result>\n");
		XMLFormat.append("\t\t<result>\n");
		XMLFormat.append("\t\t\t<binding name=\"doc\">\n");
		XMLFormat.append("\t\t\t\t<uri>http://lisi.ensma.fr/doc2</uri>\n");
		XMLFormat.append("\t\t\t</binding>\n");
		XMLFormat.append("\t\t\t<binding name=\"source\">\n");
		XMLFormat.append("\t\t\t\t<uri>sourceDoc2</uri>\n");
		XMLFormat.append("\t\t\t</binding>\n");
		XMLFormat.append("\t\t</result>\n");
		XMLFormat.append("\t\t<result>\n");
		XMLFormat.append("\t\t\t<binding name=\"doc\">\n");
		XMLFormat.append("\t\t\t\t<uri>http://lisi.ensma.fr/doc4</uri>\n");
		XMLFormat.append("\t\t\t</binding>\n");
		XMLFormat.append("\t\t\t<binding name=\"source\">\n");
		XMLFormat.append("\t\t\t\t<uri>sourceDoc4</uri>\n");
		XMLFormat.append("\t\t\t</binding>\n");
		XMLFormat.append("\t\t</result>\n");
		XMLFormat.append("\t\t<result>\n");
		XMLFormat.append("\t\t\t<binding name=\"doc\">\n");
		XMLFormat.append("\t\t\t\t<uri>http://lisi.ensma.fr/doc5</uri>\n");
		XMLFormat.append("\t\t\t</binding>\n");
		XMLFormat.append("\t\t\t<binding name=\"source\">\n");
		XMLFormat.append(
				"\t\t\t\t<uri>1992_Lenoir_Geothermal-exploitation-of-the-dogger-in-the-paris-basin-maintenance,-renewal-and-reliability-of-the-geothermal.pdf</uri>\n");
		XMLFormat.append("\t\t\t</binding>\n");
		XMLFormat.append("\t\t</result>\n");
		XMLFormat.append("\t</results>\n");
		XMLFormat.append("</sparql>");
		Assert.assertEquals(XMLFormat.toString(), rs.toSPARQLQueryResultsXMLFormat());

		queryOntoQL = "delete from Document";
		statement = sEWokHub.createOntoQLStatement();
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "delete from Segment";
		statement.executeUpdate(queryOntoQL);

		sEWokHub.close();
	}

	@Test
	public void testSPARQLQueryForDatation() throws SQLException {

		sEWokHub.setReferenceLanguage(OntoQLHelper.ENGLISH);
		sEWokHub.setDefaultNameSpace("http://lisi.ensma.fr/");

		String queryOntoQL = "insert into GeochronologicUnit (URI) values ('PERIOD_END_SELECTED_BY_USER')";
		OntoQLStatement statement = sEWokHub.createOntoQLStatement();
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into GeochronologicUnit (URI) values ('PERIOD_BEGIN_SELECTED_BY_USER')";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into GeochronologicUnit (URI) values ('NOT_PERIOD_SELECTED_BY_USER')";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into GeochronologicUnit (URI, isYoungerThan, isOlderThan) values ('http://lisi.ensma.fr/GeochronologicUnit_1', ARRAY(select oid from GeochronologicUnit where URI = 'PERIOD_END_SELECTED_BY_USER'), ARRAY(select oid from GeochronologicUnit where URI = 'PERIOD_BEGIN_SELECTED_BY_USER') )";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into GeochronologicUnit (URI, isYoungerThan, isOlderThan) values ('http://lisi.ensma.fr/GeochronologicUnit_wrong', ARRAY(select oid from GeochronologicUnit where URI = 'PERIOD_END_SELECTED_BY_USER'), ARRAY(select oid from GeochronologicUnit where URI = 'NOT_PERIOD_SELECTED_BY_USER') )";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into GeochronologicUnit (URI, isPartOf) values ('http://lisi.ensma.fr/GeochronologicUnit_2', ARRAY(select oid from GeochronologicUnit where URI = 'PERIOD_BEGIN_SELECTED_BY_USER')) )";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into GeochronologicUnit (URI, isPartOf) values ('http://lisi.ensma.fr/GeochronologicUnit_3', ARRAY(select oid from GeochronologicUnit where URI = 'PERIOD_END_SELECTED_BY_USER')) )";
		statement.executeUpdate(queryOntoQL);

		// Doc1 fulfill the first union query, Doc2 the second and so on.
		// Doc6 don't fulfill the query
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "http://lisi.ensma.fr/GeochronologicUnit_1", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc2", "sourceDoc2", "http://lisi.ensma.fr/seg_wrong",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "NOT_PERIOD_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc2", "sourceDoc2", "http://lisi.ensma.fr/seg2",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_BEGIN_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc3", "sourceDoc3", "http://lisi.ensma.fr/seg_wrong",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "NOT_PERIOD_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc3", "sourceDoc3", "http://lisi.ensma.fr/seg3",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_END_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc4", "sourceDoc4", "http://lisi.ensma.fr/seg_wrong",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "NOT_PERIOD_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc4", "sourceDoc4", "http://lisi.ensma.fr/seg4",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "http://lisi.ensma.fr/GeochronologicUnit_2", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc5", "sourceDoc5", "http://lisi.ensma.fr/seg_wrong",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "NOT_PERIOD_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc5", "sourceDoc5", "http://lisi.ensma.fr/seg5",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "http://lisi.ensma.fr/GeochronologicUnit_3", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc6", "sourceDoc6", "http://lisi.ensma.fr/seg_wrong",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "NOT_PERIOD_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc6", "sourceDoc6", "http://lisi.ensma.fr/seg_wrong2",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "http://lisi.ensma.fr/GeochronologicUnit_wrong",
				sEWokHub);

		String querySPARQL = "PREFIX meta: <http://ns.inria.fr/ewok/model/> ";
		querySPARQL += "PREFIX wl: <http://model.core.weblab.eads.com#> ";
		querySPARQL += "PREFIX gt: <http://www.ifp.fr/GeologicalTimeOntology#> ";
		querySPARQL += "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
				+ "SELECT distinct ?doc ?source  WHERE { { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg . ?seg rdf:type wl:Segment . ?seg meta:hasRef2Datation ?x . ?x rdf:type gt:GeochronologicUnit . ?x gt:isYoungerThan <PERIOD_END_SELECTED_BY_USER> . ?x gt:isOlderThan <PERIOD_BEGIN_SELECTED_BY_USER> } "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg . ?seg rdf:type wl:Segment . ?seg meta:hasRef2Datation <PERIOD_BEGIN_SELECTED_BY_USER> }  "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg . ?seg rdf:type wl:Segment . ?seg meta:hasRef2Datation <PERIOD_END_SELECTED_BY_USER> } "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg . ?seg rdf:type wl:Segment . ?seg meta:hasRef2Datation ?z . ?z rdf:type gt:GeochronologicUnit . ?z gt:isPartOf <PERIOD_BEGIN_SELECTED_BY_USER> } "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg . ?seg rdf:type wl:Segment . ?seg meta:hasRef2Datation ?z . ?z rdf:type gt:GeochronologicUnit . ?z gt:isPartOf <PERIOD_END_SELECTED_BY_USER> } } order by ASC(?doc)  ";
		statement = sEWokHub.createOntoQLStatement();
		OntoQLResultSet rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc1", rs.getString(1));
		Assert.assertEquals("sourceDoc1", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc2", rs.getString(1));
		Assert.assertEquals("sourceDoc2", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc3", rs.getString(1));
		Assert.assertEquals("sourceDoc3", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc4", rs.getString(1));
		Assert.assertEquals("sourceDoc4", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc5", rs.getString(1));
		Assert.assertEquals("sourceDoc5", rs.getString(2));
		Assert.assertFalse(rs.next());

		queryOntoQL = "delete from Document";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "delete from Segment";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "delete from GeochronologicUnit where URI = 'PERIOD_END_SELECTED_BY_USER' OR URI='PERIOD_BEGIN_SELECTED_BY_USER' OR URI='NOT_PERIOD_SELECTED_BY_USER' OR URI='http://lisi.ensma.fr/GeochronologicUnit_1' OR URI='http://lisi.ensma.fr/GeochronologicUnit_2' OR URI='http://lisi.ensma.fr/GeochronologicUnit_3' OR URI='http://lisi.ensma.fr/GeochronologicUnit_wrong'";
		statement.executeUpdate(queryOntoQL);

		sEWokHub.close();
	}

	@Test
	public void testSPARQLQueryForGeographyDatation() throws SQLException {
		sEWokHub.setReferenceLanguage(OntoQLHelper.ENGLISH);
		sEWokHub.setDefaultNameSpace("http://lisi.ensma.fr/");

		// Prepare the data
		String queryOntoQL = "insert into GeochronologicUnit (URI) values ('PERIOD_SELECTED_BY_USER')";
		OntoQLStatement statement = sEWokHub.createOntoQLStatement();
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into GeochronologicUnit (URI) values ('PERIOD_NOT_SELECTED_BY_USER')";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into GeochronologicUnit (URI, isPartOf) values ('http://lisi.ensma.fr/GeochronologicUnit_1', ARRAY(select oid from GeochronologicUnit where URI = 'PERIOD_SELECTED_BY_USER')) )";
		statement.executeUpdate(queryOntoQL);

		// Load annotations
		// Doc 1: fulfill the first union
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_NOT_SELECTED_BY_USER", sEWokHub);
		// Doc 2: the second
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc2", "sourceDoc2", "http://lisi.ensma.fr/seg2-2",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/DEP_24", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc2", "sourceDoc1", "http://lisi.ensma.fr/seg2",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "http://lisi.ensma.fr/GeochronologicUnit_1", sEWokHub);
		// Doc 3: the third
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc3", "sourceDoc3", "http://lisi.ensma.fr/seg3",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/REG_54", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc3", "sourceDoc3", "http://lisi.ensma.fr/seg3",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_NOT_SELECTED_BY_USER", sEWokHub);
		// Doc 4: the forth
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc4", "sourceDoc4", "http://lisi.ensma.fr/seg4",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/COM_86062", sEWokHub);
		// Doc 5: 2 of them
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc5", "sourceDoc5", "http://lisi.ensma.fr/seg5",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/COM_86115", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc5", "sourceDoc5", "http://lisi.ensma.fr/seg5",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "http://lisi.ensma.fr/GeochronologicUnit_1", sEWokHub);
		// Doc 6: none of them
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc6", "sourceDoc6", "http://lisi.ensma.fr/seg6",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/DEP_64", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc6", "sourceDoc6", "http://lisi.ensma.fr/seg6",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_NOT_SELECTED_BY_USER", sEWokHub);

		// Execute the SPARQL query
		String querySPARQL = "PREFIX meta: <http://ns.inria.fr/ewok/model/> ";
		querySPARQL += "PREFIX wl: <http://model.core.weblab.eads.com#> ";
		querySPARQL += "PREFIX gt: <http://www.ifp.fr/GeologicalTimeOntology#> ";
		querySPARQL += "PREFIX dc: <http://purl.org/dc/elements/1.1/> " + " PREFIX geo: <http://rdf.insee.fr/geo/> "
				+ "SELECT distinct ?doc ?source  WHERE { { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg . ?seg rdf:type wl:Segment . ?seg meta:hasRef2Datation <PERIOD_SELECTED_BY_USER> } "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg . ?seg rdf:type wl:Segment . ?seg meta:hasRef2Datation ?z . ?z rdf:type gt:GeochronologicUnit . ?z gt:isPartOf <PERIOD_SELECTED_BY_USER> } "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg2 . ?seg2 rdf:type wl:Segment . ?seg2 meta:hasRef2Geolocalization ?localisation . FILTER(?localisation=<http://rdf.insee.fr/geo/REG_54> || ?localisation=<http://rdf.insee.fr/geo/DEP_24>) }"
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg2 . ?seg2 rdf:type wl:Segment . ?seg2 meta:hasRef2Geolocalization ?localisation2 . ?z rdf:type geo:Territoire_FR . ?z geo:subdivision ?localisation2 . FILTER(?z=<http://rdf.insee.fr/geo/REG_54> || ?z=<http://rdf.insee.fr/geo/DEP_24>) } } order by ASC(?doc) ";

		OntoQLResultSet rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc1", rs.getString(1));
		Assert.assertEquals("sourceDoc1", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc2", rs.getString(1));
		Assert.assertEquals("sourceDoc2", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc3", rs.getString(1));
		Assert.assertEquals("sourceDoc3", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc4", rs.getString(1));
		Assert.assertEquals("sourceDoc4", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc5", rs.getString(1));
		Assert.assertEquals("sourceDoc5", rs.getString(2));
		Assert.assertFalse(rs.next());

		queryOntoQL = "delete from Document";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "delete from Segment";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "delete from GeochronologicUnit where URI='PERIOD_SELECTED_BY_USER' OR URI='PERIOD_NOT_SELECTED_BY_USER' OR URI='http://lisi.ensma.fr/GeochronologicUnit_1'";
		statement.executeUpdate(queryOntoQL);

		sEWokHub.close();
	}

	@Test
	public void testSPARQLQueryForGeology() throws SQLException {

		sEWokHub.setReferenceLanguage(OntoQLHelper.ENGLISH);
		sEWokHub.setDefaultNameSpace("http://lisi.ensma.fr/");

		// prepare the data
		String queryOntoQL = "insert into GeologicalObject (URI) values ('CONCEPT_SELECTED_BY_USER')";
		OntoQLStatement statement = sEWokHub.createOntoQLStatement();
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into GeologicalObject (URI) values ('NOT_CONCEPT_SELECTED_BY_USER')";
		statement.executeUpdate(queryOntoQL);

		// Doc1 and Doc2 fulfill the query on the first union
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1-2",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc2", "sourceDoc2", "http://lisi.ensma.fr/seg2",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc2", "sourceDoc2", "http://lisi.ensma.fr/seg2",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc2", "sourceDoc2", "http://lisi.ensma.fr/seg2-2",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "CONCEPT_SELECTED_BY_USER", sEWokHub);
		// Doc3 don't fulfill the query
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc3", "sourceDoc3", "http://lisi.ensma.fr/seg3",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc3", "sourceDoc3", "http://lisi.ensma.fr/seg3-2",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		// Doc4 fulfill the query with the second union
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc4", "sourceDoc4", "http://lisi.ensma.fr/seg4",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "http://ns.inria.fr/ewok/geology#Reservoir",
				sEWokHub);
		// Doc5 fulfill the third union
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc5", "sourceDoc5", "http://lisi.ensma.fr/seg5",
				"http://ns.inria.fr/ewok/model/hasRef2OtherGeoEntities",
				"http://ns.inria.fr/ewok/geology#GeologicalObject", sEWokHub);
		// Doc6 the fourth
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc6", "sourceDoc6", "http://lisi.ensma.fr/seg6",
				"http://ns.inria.fr/ewok/model/hasRef2OtherGeoEntities", "http://ns.inria.fr/ewok/geology#Reservoir",
				sEWokHub);
		// Doc7 the fourth (with three levels of hierarchy)
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc7", "sourceDoc7", "http://lisi.ensma.fr/seg7",
				"http://ns.inria.fr/ewok/model/hasRef2OtherGeoEntities",
				"http://ns.inria.fr/ewok/geology#MacroFracture", sEWokHub);
		// Doc8 none of them
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc8", "sourceDoc8", "http://lisi.ensma.fr/seg8",
				"http://ns.inria.fr/ewok/model/hasRef2OtherGeoEntities", "http://model.core.weblab.eads.com#Query",
				sEWokHub);

		String querySPARQL = "PREFIX meta: <http://ns.inria.fr/ewok/model/> ";
		querySPARQL += "PREFIX wl: <http://model.core.weblab.eads.com#> ";
		querySPARQL += "PREFIX gt: <http://www.ifp.fr/GeologicalTimeOntology#> ";
		querySPARQL += "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
				+ "SELECT distinct ?doc ?source  WHERE { { ?doc rdf:type wl:Document . . ?doc dc:source ?source  . ?doc wl:contains ?seg . ?seg rdf:type wl:Segment . ?seg meta:hasRef2GeologicalObject <CONCEPT_SELECTED_BY_USER> } "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg . ?seg rdf:type wl:Segment . ?seg meta:hasRef2GeologicalObject ?obj1 . ?obj1 rdf:type rdfs:class . ?obj1 rdfs:subClassOf <http://ns.inria.fr/ewok/geology#GeologicalObject> } "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg . ?seg rdf:type wl:Segment . ?seg meta:hasRef2OtherGeoEntities <http://ns.inria.fr/ewok/geology#GeologicalObject> } "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg . ?seg rdf:type wl:Segment . ?seg meta:hasRef2OtherGeoEntities ?obj2 . ?obj2 rdf:type rdfs:class . ?obj2 rdfs:subClassOf <http://ns.inria.fr/ewok/geology#GeologicalObject> } } order by ASC(?doc) ";
		OntoQLResultSet rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc1", rs.getString(1));
		Assert.assertEquals("sourceDoc1", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc2", rs.getString(1));
		Assert.assertEquals("sourceDoc2", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc4", rs.getString(1));
		Assert.assertEquals("sourceDoc4", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc5", rs.getString(1));
		Assert.assertEquals("sourceDoc5", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc6", rs.getString(1));
		Assert.assertEquals("sourceDoc6", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc7", rs.getString(1));
		Assert.assertEquals("sourceDoc7", rs.getString(2));
		Assert.assertFalse(rs.next());

		queryOntoQL = "delete from Document";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "delete from Segment";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "delete from GeologicalObject where URI = 'CONCEPT_SELECTED_BY_USER' OR URI='NOT_CONCEPT_SELECTED_BY_USER'";
		statement.executeUpdate(queryOntoQL);

		sEWokHub.close();
	}

	@Test
	public void testSPARQLQueryForGeologyDatation() throws SQLException {

		sEWokHub.setReferenceLanguage(OntoQLHelper.ENGLISH);
		sEWokHub.setDefaultNameSpace("http://lisi.ensma.fr/");

		// prepare the data
		String queryOntoQL = "insert into GeologicalObject (URI) values ('CONCEPT_SELECTED_BY_USER')";
		OntoQLStatement statement = sEWokHub.createOntoQLStatement();
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into GeologicalObject (URI) values ('NOT_CONCEPT_SELECTED_BY_USER')";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into GeochronologicUnit (URI) values ('PERIOD_SELECTED_BY_USER')";
		statement = sEWokHub.createOntoQLStatement();
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into GeochronologicUnit (URI) values ('PERIOD_NOT_SELECTED_BY_USER')";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into GeochronologicUnit (URI, isPartOf) values ('http://lisi.ensma.fr/GeochronologicUnit_1', ARRAY(select oid from GeochronologicUnit where URI = 'PERIOD_SELECTED_BY_USER')) )";
		statement.executeUpdate(queryOntoQL);

		// Load annotations
		// Doc 1: fulfill the first union
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1-1",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1-2",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1-3",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_NOT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1-2",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1-2",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/REG_53", sEWokHub);
		// Doc 2: the second
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc2", "sourceDoc2", "http://lisi.ensma.fr/seg2-1",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc2", "sourceDoc2", "http://lisi.ensma.fr/seg2-1",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/REG_54", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc2", "sourceDoc2", "http://lisi.ensma.fr/seg2-1",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "http://lisi.ensma.fr/GeochronologicUnit_1", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc2", "sourceDoc2", "http://lisi.ensma.fr/seg2-1",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_NOT_SELECTED_BY_USER", sEWokHub);
		// Doc3 the third
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc3", "sourceDoc3", "http://lisi.ensma.fr/seg3",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "http://ns.inria.fr/ewok/geology#Reservoir",
				sEWokHub);
		// Doc4: the fourth
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc4", "sourceDoc4", "http://lisi.ensma.fr/seg4-1",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc4", "sourceDoc4", "http://lisi.ensma.fr/seg4-1",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_NOT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc4", "sourceDoc4", "http://lisi.ensma.fr/seg4-2",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc4", "sourceDoc4", "http://lisi.ensma.fr/seg4-2",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_NOT_SELECTED_BY_USER", sEWokHub);
		// Doc5: 3 of them
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc5", "sourceDoc5", "http://lisi.ensma.fr/seg5-1",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_NOT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc5", "sourceDoc5", "http://lisi.ensma.fr/seg5-1",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc5", "sourceDoc5", "http://lisi.ensma.fr/seg5-2",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_NOT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc5", "sourceDoc5", "http://lisi.ensma.fr/seg5-2",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "http://lisi.ensma.fr/GeochronologicUnit_1", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc5", "sourceDoc5", "http://lisi.ensma.fr/seg5-3",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_NOT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc5", "sourceDoc5", "http://lisi.ensma.fr/seg5-3",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "CONCEPT_SELECTED_BY_USER", sEWokHub);
		// Doc6: none of them
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc6", "sourceDoc6", "http://lisi.ensma.fr/seg6-1",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc6", "sourceDoc6", "http://lisi.ensma.fr/seg6-1",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_NOT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc6", "sourceDoc6", "http://lisi.ensma.fr/seg6-2",
				"http://ns.inria.fr/ewok/model/hasRef2Datation", "PERIOD_NOT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc6", "sourceDoc6", "http://lisi.ensma.fr/seg6-2",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		// Doc7 the fifth
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc7", "sourceDoc7", "http://lisi.ensma.fr/seg7",
				"http://ns.inria.fr/ewok/model/hasRef2OtherGeoEntities",
				"http://ns.inria.fr/ewok/geology#GeologicalObject", sEWokHub);
		// Doc8 the sixth
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc8", "sourceDoc8", "http://lisi.ensma.fr/seg8",
				"http://ns.inria.fr/ewok/model/hasRef2OtherGeoEntities", "http://ns.inria.fr/ewok/geology#Reservoir",
				sEWokHub);

		String querySPARQL = "PREFIX meta: <http://ns.inria.fr/ewok/model/> ";
		querySPARQL += "PREFIX wl: <http://model.core.weblab.eads.com#> ";
		querySPARQL += "PREFIX gt: <http://www.ifp.fr/GeologicalTimeOntology#> ";
		querySPARQL += "PREFIX dc: <http://purl.org/dc/elements/1.1/> "
				+ "SELECT distinct ?doc ?source  WHERE { { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg2 . ?seg2 rdf:type wl:Segment . ?seg2 meta:hasRef2Datation <PERIOD_SELECTED_BY_USER> } "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg2 . ?seg2 rdf:type wl:Segment . ?seg2 meta:hasRef2Datation ?z . ?z rdf:type gt:GeochronologicUnit . ?z gt:isPartOf <PERIOD_SELECTED_BY_USER> } "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg1 . ?seg1 rdf:type wl:Segment . ?seg1 meta:hasRef2GeologicalObject ?obj1 . ?obj1 rdf:type rdfs:class . ?obj1 rdfs:subClassOf <http://ns.inria.fr/ewok/geology#GeologicalObject> } "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg1 . ?seg1 rdf:type wl:Segment . ?seg1 meta:hasRef2GeologicalObject <CONCEPT_SELECTED_BY_USER>} "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg1 . ?seg1 rdf:type wl:Segment . ?seg1 meta:hasRef2OtherGeoEntities <http://ns.inria.fr/ewok/geology#GeologicalObject> } "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg1 . ?seg1 rdf:type wl:Segment . ?seg1 meta:hasRef2OtherGeoEntities ?obj2 . ?obj2 rdf:type rdfs:class . ?obj2 rdfs:subClassOf <http://ns.inria.fr/ewok/geology#GeologicalObject> } } order by ASC(?doc) ";
		OntoQLResultSet rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc1", rs.getString(1));
		Assert.assertEquals("sourceDoc1", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc2", rs.getString(1));
		Assert.assertEquals("sourceDoc2", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc3", rs.getString(1));
		Assert.assertEquals("sourceDoc3", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc4", rs.getString(1));
		Assert.assertEquals("sourceDoc4", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc5", rs.getString(1));
		Assert.assertEquals("sourceDoc5", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc7", rs.getString(1));
		Assert.assertEquals("sourceDoc7", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc8", rs.getString(1));
		Assert.assertEquals("sourceDoc8", rs.getString(2));
		Assert.assertFalse(rs.next());

		queryOntoQL = "delete from Document";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "delete from Segment";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "delete from GeologicalObject where URI = 'CONCEPT_SELECTED_BY_USER' OR URI='NOT_CONCEPT_SELECTED_BY_USER'";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "delete from GeochronologicUnit where URI='PERIOD_SELECTED_BY_USER' OR URI='PERIOD_NOT_SELECTED_BY_USER' OR URI='http://lisi.ensma.fr/GeochronologicUnit_1'";
		statement.executeUpdate(queryOntoQL);

		sEWokHub.close();
	}

	@Test
	public void testSPARQLQueryForGeographyGeology() throws SQLException {

		sEWokHub.setReferenceLanguage(OntoQLHelper.ENGLISH);
		sEWokHub.setDefaultNameSpace("http://lisi.ensma.fr/");

		// prepare the data
		String queryOntoQL = "insert into GeologicalObject (URI) values ('CONCEPT_SELECTED_BY_USER')";
		OntoQLStatement statement = sEWokHub.createOntoQLStatement();
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "insert into GeologicalObject (URI) values ('NOT_CONCEPT_SELECTED_BY_USER')";
		statement.executeUpdate(queryOntoQL);

		// Load annotations
		// Doc 1: fulfill the first union
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1-1",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1-1",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/REG_53", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1-2",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1-2",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/REG_54", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc1", "sourceDoc1", "http://lisi.ensma.fr/seg1-2",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/REG_53", sEWokHub);
		// Doc 2: fulfill the second union
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc2", "sourceDoc2", "http://lisi.ensma.fr/seg2-1",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc2", "sourceDoc2", "http://lisi.ensma.fr/seg2-2",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc2", "sourceDoc2", "http://lisi.ensma.fr/seg2-3",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/COM_86115", sEWokHub);
		// Doc 3: fulfill the fourth union
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc3", "sourceDoc3", "http://lisi.ensma.fr/seg3-1",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc3", "sourceDoc3", "http://lisi.ensma.fr/seg3-1",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/DEP_64", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc3", "sourceDoc3", "http://lisi.ensma.fr/seg3-1",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "CONCEPT_SELECTED_BY_USER", sEWokHub);
		// Doc 4: don't fulfill the query
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc4", "sourceDoc4", "http://lisi.ensma.fr/seg4-1",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "NOT_CONCEPT_SELECTED_BY_USER", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc4", "sourceDoc4", "http://lisi.ensma.fr/seg4-1",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/DEP_72", sEWokHub);
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc4", "sourceDoc4", "http://lisi.ensma.fr/seg4-2",
				"http://ns.inria.fr/ewok/model/hasRef2Geolocalization", "http://rdf.insee.fr/geo/DEP_64", sEWokHub);
		// Doc5 the third
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc5", "sourceDoc5", "http://lisi.ensma.fr/seg5",
				"http://ns.inria.fr/ewok/model/hasRef2GeologicalObject", "http://ns.inria.fr/ewok/geology#Reservoir",
				sEWokHub);
		// Doc7 the fifth
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc7", "sourceDoc7", "http://lisi.ensma.fr/seg7",
				"http://ns.inria.fr/ewok/model/hasRef2OtherGeoEntities",
				"http://ns.inria.fr/ewok/geology#GeologicalObject", sEWokHub);
		// Doc8 the sixth
		loadAnnotationOnSegment("http://lisi.ensma.fr/doc8", "sourceDoc8", "http://lisi.ensma.fr/seg8",
				"http://ns.inria.fr/ewok/model/hasRef2OtherGeoEntities", "http://ns.inria.fr/ewok/geology#Reservoir",
				sEWokHub);

		String querySPARQL = "PREFIX meta: <http://ns.inria.fr/ewok/model/> ";
		querySPARQL += "PREFIX wl: <http://model.core.weblab.eads.com#> ";
		querySPARQL += "PREFIX gt: <http://www.ifp.fr/GeologicalTimeOntology#> ";
		querySPARQL += "PREFIX dc: <http://purl.org/dc/elements/1.1/> " + "PREFIX geo: <http://rdf.insee.fr/geo/> "
				+ "SELECT distinct ?doc ?source  WHERE { { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg2 . ?seg2 rdf:type wl:Segment . ?seg2 meta:hasRef2Geolocalization ?localisation . FILTER(?localisation=<http://rdf.insee.fr/geo/REG_54> || ?localisation=<http://rdf.insee.fr/geo/DEP_24>) }"
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg2 . ?seg2 rdf:type wl:Segment . ?seg2 meta:hasRef2Geolocalization ?localisation2 . ?z rdf:type geo:Territoire_FR . ?z geo:subdivision ?localisation2 . FILTER(?z=<http://rdf.insee.fr/geo/REG_54> || ?z=<http://rdf.insee.fr/geo/DEP_24>) }"
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg1 . ?seg1 rdf:type wl:Segment . ?seg1 meta:hasRef2GeologicalObject ?obj1 . ?obj1 rdf:type rdfs:class . ?obj1 rdfs:subClassOf <http://ns.inria.fr/ewok/geology#GeologicalObject> } "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg1 . ?seg1 rdf:type wl:Segment . ?seg1 meta:hasRef2GeologicalObject <CONCEPT_SELECTED_BY_USER>} "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg1 . ?seg1 rdf:type wl:Segment . ?seg1 meta:hasRef2OtherGeoEntities <http://ns.inria.fr/ewok/geology#GeologicalObject> } "
				+ " UNION { ?doc rdf:type wl:Document . ?doc dc:source ?source . ?doc wl:contains ?seg1 . ?seg1 rdf:type wl:Segment . ?seg1 meta:hasRef2OtherGeoEntities ?obj2 . ?obj2 rdf:type rdfs:class . ?obj2 rdfs:subClassOf <http://ns.inria.fr/ewok/geology#GeologicalObject> } } order by ASC(?doc) ";
		OntoQLResultSet rs = statement.executeSPARQLQuery(querySPARQL);
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc1", rs.getString(1));
		Assert.assertEquals("sourceDoc1", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc2", rs.getString(1));
		Assert.assertEquals("sourceDoc2", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc3", rs.getString(1));
		Assert.assertEquals("sourceDoc3", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc5", rs.getString(1));
		Assert.assertEquals("sourceDoc5", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc7", rs.getString(1));
		Assert.assertEquals("sourceDoc7", rs.getString(2));
		Assert.assertTrue(rs.next());
		Assert.assertEquals("http://lisi.ensma.fr/doc8", rs.getString(1));
		Assert.assertEquals("sourceDoc8", rs.getString(2));
		Assert.assertFalse(rs.next());

		queryOntoQL = "delete from Document";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "delete from Segment";
		statement.executeUpdate(queryOntoQL);
		queryOntoQL = "delete from GeologicalObject where URI = 'CONCEPT_SELECTED_BY_USER' OR URI='NOT_CONCEPT_SELECTED_BY_USER'";
		statement.executeUpdate(queryOntoQL);

		sEWokHub.close();
	}
}
