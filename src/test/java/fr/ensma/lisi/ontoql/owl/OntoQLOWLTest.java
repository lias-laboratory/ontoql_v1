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
package fr.ensma.lisi.ontoql.owl;

import java.sql.SQLException;

import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Test the extension of OntoQL to OWL.
 * 
 * @author Stéphane JEAN
 */
public class OntoQLOWLTest extends OntoQLTestCase {

	public OntoQLSession sessionOWL;

	/**
	 * Test the extension of the core model to the RDF-Schema model
	 */
	@Test
	public void testExtensionToRDFS() throws SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		// Add some attributes to the entity concept
		Transaction t = s.beginTransaction();
		String queryOntoQL = "ALTER ENTITY #concept " + "ADD ATTRIBUTE #seeAlso REF(#concept) ARRAY";
		OntoQLStatement statement = s.createOntoQLStatement();
		int res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		statement = s.createOntoQLStatement();
		OntoQLResultSet rs = statement.executeQuery("select #seeAlso from #concept where #name[en] = 'CAGS'");
		rs.next();
		Assert.assertNull(rs.getString(1));

		queryOntoQL = "CREATE ENTITY #RDFSClass UNDER #Class";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		rs = statement.executeQuery("select #name[en] from #RDFSClass");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #RDFProperty UNDER #Property (#directSuperProperties REF(#RDFProperty) ARRAY)";
		res = statement.executeUpdate(queryOntoQL);
		Assert.assertEquals(0, res);
		rs = statement
				.executeQuery("select pSup.#name[en] from #RDFProperty p, unnest(p.#directSuperProperties) as pSup");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #RDFSContainer UNDER #collectionType";
		res = statement.executeUpdate(queryOntoQL);
		queryOntoQL = "CREATE ENTITY #RDFAlt UNDER #RDFSContainer";
		res = statement.executeUpdate(queryOntoQL);
		queryOntoQL = "CREATE ENTITY #RDFBag UNDER #RDFSContainer";
		res = statement.executeUpdate(queryOntoQL);
		queryOntoQL = "CREATE ENTITY #RDFList UNDER #RDFSContainer";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#oid, alt.#oid, b.#oid, l.#oid from #RDFSContainer c, #RDFAlt alt, #RDFBag b, #RDFList l");
		Assert.assertFalse(rs.next());

		queryOntoQL = "ALTER ENTITY #Concept ADD ATTRIBUTE #OWLversionInfo STRING";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery("select #OWLversionInfo from #Concept where #name[en] = 'CAGS'");
		rs.next();
		Assert.assertNull(rs.getString(1));

		queryOntoQL = "CREATE ENTITY #OWLOntology UNDER #Ontology (";
		queryOntoQL += "#OWLpriorVersion REF(#OWLOntology) ARRAY,";
		queryOntoQL += "#OWLincompatibleWith REF(#OWLOntology) ARRAY,";
		queryOntoQL += "#OWLbackwardCompatibleWith REF(#OWLOntology) ARRAY,";
		queryOntoQL += "#OWLimports REF(#OWLOntology) ARRAY,";
		queryOntoQL += "#OWLversionInfo String)";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select o.#OWLversionInfo, prior.#OWLversionInfo, inc.#OWLversionInfo, back.#OWLversionInfo, imp.#OWLversionInfo from #OWLOntology o, unnest(o.#OWLpriorVersion) prior, unnest(o.#OWLincompatibleWith) inc, unnest(o.#OWLbackwardCompatibleWith) back, unnest(o.#OWLimports) imp");
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #OWLOntology (#namespace, #OWLversionInfo) VALUES ('http://rdfs.org/sioc/ns', 'Revision: 1.10')";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery("select o.#namespace, o.#OWLversionInfo from #OWLOntology o");
		rs.next();
		Assert.assertEquals("http://rdfs.org/sioc/ns", rs.getString(1));
		Assert.assertEquals("Revision: 1.10", rs.getString(2));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #RDFSClass (#name, #definition, #definedBy)" + " VALUES ('Community', "
				+ "'Community is a top level concept that defines an online community and what it consists of.',"
				+ "(SELECT #oid from #OWLOntology where #namespace = 'http://rdfs.org/sioc/ns'))";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery("select c.#name, c.#definition, c.#definedBy.#namespace from #RDFSClass c");
		rs.next();
		Assert.assertEquals("Community", rs.getString(1));
		Assert.assertEquals(
				"Community is a top level concept that defines an online community and what it consists of.",
				rs.getString(2));
		Assert.assertEquals("http://rdfs.org/sioc/ns", rs.getString(3));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #RDFSClass (#name, #definition, #definedBy) " + " VALUES ('Forum', "
				+ "'A discussion area on which Posts or entries are made.  Synonyms include channel and feed.',"
				+ "(SELECT #oid from #ontology where #namespace = 'http://rdfs.org/sioc/ns'))";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#name, c.#definition, c.#definedBy.#namespace from #RDFSClass c where #name='Forum'");
		rs.next();
		Assert.assertEquals("Forum", rs.getString(1));
		Assert.assertEquals("A discussion area on which Posts or entries are made.  Synonyms include channel and feed.",
				rs.getString(2));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #RDFSClass (#name, #definition, #definedBy) " + " VALUES ('Post', "
				+ "'An article or message posted to a Forum.  Synonyms include item and entry.',"
				+ "(SELECT #oid from #ontology where #namespace = 'http://rdfs.org/sioc/ns'))";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#oid, c.#name, c.#definition, c.#definedBy.#namespace from #RDFSClass c where #name='Post'");
		rs.next();
		Assert.assertNotNull(rs.getString(1));
		Assert.assertEquals("Post", rs.getString(2));
		Assert.assertEquals("An article or message posted to a Forum.  Synonyms include item and entry.",
				rs.getString(3));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #RDFSClass (#name, #definition, #definedBy) " + " VALUES ('Role', "
				+ "'A Role is a function of a User within a scope of a particular Forum.',"
				+ "(SELECT #oid from #ontology where #namespace = 'http://rdfs.org/sioc/ns'))";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#name, c.#definition, c.#definedBy.#namespace from #RDFSClass c where #name='Role'");
		rs.next();
		Assert.assertEquals("Role", rs.getString(1));
		Assert.assertEquals("A Role is a function of a User within a scope of a particular Forum.", rs.getString(2));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #RDFSClass (#name, #definition, #definedBy) " + " VALUES ('Site', "
				+ "'The location of an online community or set of communities, with Users and Groups creating Posts on a set of Forums.',"
				+ "(SELECT #oid from #ontology where #namespace = 'http://rdfs.org/sioc/ns'))";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#name, c.#definition, c.#definedBy.#namespace from #RDFSClass c where #name='Site'");
		rs.next();
		Assert.assertEquals("Site", rs.getString(1));
		Assert.assertEquals(
				"The location of an online community or set of communities, with Users and Groups creating Posts on a set of Forums.",
				rs.getString(2));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #RDFSClass (#name, #definition, #definedBy) " + " VALUES ('User', "
				+ "'An online account (User) in an online community site.',"
				+ "(SELECT #oid from #ontology where #namespace = 'http://rdfs.org/sioc/ns'))";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#name, c.#definition, c.#definedBy.#namespace from #RDFSClass c where #name='User'");
		rs.next();
		Assert.assertEquals("User", rs.getString(1));
		Assert.assertEquals("An online account (User) in an online community site.", rs.getString(2));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #RDFSClass (#name, #definition, #definedBy) " + " VALUES ('Usergroup', "
				+ "'A set of User accounts whose owners have a common purpose or interest.  Can be used for access control purposes.',"
				+ "(SELECT #oid from #ontology where #namespace = 'http://rdfs.org/sioc/ns'))";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#name, c.#definition, c.#definedBy.#namespace from #RDFSClass c where #name='Usergroup'");
		rs.next();
		Assert.assertEquals("Usergroup", rs.getString(1));
		Assert.assertEquals(
				"A set of User accounts whose owners have a common purpose or interest.  Can be used for access control purposes.",
				rs.getString(2));
		Assert.assertFalse(rs.next());

		queryOntoQL = "insert into #CLASS (#code, #name[en], #name[fr]) values ('RootClass', 'RootClass', 'RootClass')";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery("SELECT #code FROM #CLASS WHERE #name[en]='RootClass'");
		rs.next();
		Assert.assertEquals("RootClass", rs.getString(1));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #RefType (#onClass) VALUES ((SELECT #oid FROM #class WHERE #name='RootClass'))";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery("SELECT #oid FROM #RefType WHERE #onClass.#name[en]='RootClass'");
		rs.next();
		Assert.assertNotNull(rs.getString(1));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #RDFProperty (#name, #scope, #range, #definition) " + " VALUES ('attachment', "
				+ "(SELECT #oid FROM #RDFSClass WHERE #name='Post' and #definedBy.#namespace='http://rdfs.org/sioc/ns'),"
				+ "(SELECT #oid FROM #RefType WHERE #onClass.#name[en]='RootClass'),"
				+ "'A URI of the attachment related to a Post.')";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select p.#name, p.#scope.#name, p.#range.#oid, p.#definition, p.#definedBy.#namespace from #RDFProperty p where #name='attachment'");
		rs.next();
		Assert.assertEquals("attachment", rs.getString(1));
		Assert.assertEquals("Post", rs.getString(2));
		Assert.assertNotNull(rs.getString(3));
		Assert.assertEquals("A URI of the attachment related to a Post.", rs.getString(4));
		Assert.assertEquals("http://rdfs.org/sioc/ns", rs.getString(5));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #OWLOntology (#namespace) VALUES ('http://xmlns.com/foaf/0.1/')";
		statement = s.createOntoQLStatement();
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery("select o.#namespace from #OWLOntology o where o.#namespace like '%foaf%'");
		rs.next();
		Assert.assertEquals("http://xmlns.com/foaf/0.1/", rs.getString(1));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #RDFSClass (#name, #definition, #definedBy) " + " VALUES ('Agent', "
				+ "'An agent (eg. person, group, software or physical artifact).',"
				+ "(SELECT #oid from #ontology where #namespace = 'http://xmlns.com/foaf/0.1/'))";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#name, c.#definition, c.#definedBy.#namespace from #RDFSClass c where #name='Agent'");
		rs.next();
		Assert.assertEquals("Agent", rs.getString(1));
		Assert.assertEquals("An agent (eg. person, group, software or physical artifact).", rs.getString(2));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #RefType (#onClass) VALUES ((SELECT #oid FROM #class WHERE #name='Agent' and #definedBy.#namespace='http://xmlns.com/foaf/0.1/'));";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery("SELECT #oid FROM #RefType WHERE #onClass.#name[en]='Agent'");
		rs.next();
		Assert.assertNotNull(rs.getString(1));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #RDFProperty (#name, #scope, #range, #definition) " + " VALUES ('account_of', "
				+ "(SELECT #oid FROM #class WHERE #name='User' and #definedBy.#namespace='http://rdfs.org/sioc/ns'),"
				+ "(SELECT #oid FROM #RefType WHERE #onClass.#name[en]='Agent' and #onClass.#definedBy.#namespace='http://xmlns.com/foaf/0.1/'),"
				+ "'Person (foaf:Person) who has registered this account (sioc:User).')";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select p.#name, p.#scope.#name, p.#range.#oid, p.#definition, p.#definedBy.#namespace from #RDFProperty p where #name='account_of'");
		rs.next();
		Assert.assertEquals("account_of", rs.getString(1));
		Assert.assertEquals("User", rs.getString(2));
		Assert.assertNotNull(rs.getString(3));
		Assert.assertEquals("Person (foaf:Person) who has registered this account (sioc:User).", rs.getString(4));
		Assert.assertEquals("http://rdfs.org/sioc/ns", rs.getString(5));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #RDFProperty (#name, #scope, #range, #definition) " + " VALUES ('avatar', "
				+ "(SELECT #oid FROM #class WHERE #name='User' and #definedBy.#namespace='http://rdfs.org/sioc/ns'),"
				+ "(SELECT #oid FROM #RefType WHERE #onClass.#name[en]='RootClass'),"
				+ "'An image or depiction used to represent this User.')";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select p.#name, p.#scope.#name, p.#range.#oid, p.#definition, p.#definedBy.#namespace from #RDFProperty p where #name='avatar'");
		rs.next();
		Assert.assertEquals("avatar", rs.getString(1));
		Assert.assertEquals("User", rs.getString(2));
		Assert.assertNotNull(rs.getString(3));
		Assert.assertEquals("An image or depiction used to represent this User.", rs.getString(4));
		Assert.assertEquals("http://rdfs.org/sioc/ns", rs.getString(5));
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLClass UNDER #RDFSClass (";
		queryOntoQL += "#OWLequivalentClass REF(#OWLClass) ARRAY,";
		queryOntoQL += "#OWLdisjointWith REF(#OWLClass) ARRAY,";
		queryOntoQL += "#OWLisDeprecated BOOLEAN)";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#name[en], equiv.#OWLisDeprecated, dis.#name[en] from #OWLClass c, unnest(c.#OWLequivalentClass) equiv, unnest(c.#OWLdisjointWith) dis");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLComplementClass UNDER #OWLClass (";
		queryOntoQL += "#OWLcomplementOf REF(#OWLClass))";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery("select c.#name[en], c.#OWLcomplementOf.#name[en] from #OWLComplementClass c");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLIntersectionClass UNDER #OWLClass (";
		queryOntoQL += "#OWLintersectionOf REF(#OWLClass) ARRAY)";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#name[en], inter.#name[en] from #OWLIntersectionClass c, unnest(c.#OWLintersectionOf) as inter");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLUnionClass UNDER #OWLClass (";
		queryOntoQL += "#OWLunionOf REF(#OWLClass) ARRAY)";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#name[en], inter.#name[en] from #OWLUnionClass c, unnest(c.#OWLunionOf) as inter");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLEnnumeratedClass UNDER #OWLClass (";
		queryOntoQL += "#OWLoneOf REF(RootClass) ARRAY)";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery("select c.#name[en], i.oid from #OWLEnnumeratedClass c, unnest(c.#OWLoneOf) as i");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLProperty UNDER #RDFProperty (";
		queryOntoQL += "#OWLequivalentProperty REF(#OWLProperty) ARRAY,";
		queryOntoQL += "#OWLisDeprecated BOOLEAN)";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select p.#name[en], equiv.#OWLisDeprecated from #OWLProperty p, unnest(p.#OWLequivalentProperty) as equiv");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLDatatypeProperty UNDER #OWLProperty";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select p.#name[en], equiv.#OWLisDeprecated from #OWLDatatypeProperty p, unnest(p.#OWLequivalentProperty) as equiv");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLFunctionalProperty UNDER #OWLProperty";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select p.#name[en], equiv.#OWLisDeprecated from #OWLFunctionalProperty p, unnest(p.#OWLequivalentProperty) as equiv");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLObjectProperty UNDER #OWLProperty (";
		queryOntoQL += "#OWLinverseOf REF(#OWLObjectProperty))";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select p.#name[en], equiv.#OWLisDeprecated, p.#OWLinverseOf.#name[en] from #OWLObjectProperty p, unnest(p.#OWLequivalentProperty) as equiv");
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #RefType (#onClass) VALUES ((SELECT #oid FROM #class WHERE #name='Post' and #definedBy.#namespace='http://rdfs.org/sioc/ns'));";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery("SELECT #oid FROM #RefType WHERE #onClass.#name[en]='Post'");
		rs.next();
		Assert.assertNotNull(rs.getString(1));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #OWLObjectProperty (#name, #scope, #range, #definition) "
				+ " VALUES ('container_of',  "
				+ "(SELECT #oid FROM #class WHERE #name='Forum' and #definedBy.#namespace='http://rdfs.org/sioc/ns'),"
				+ "(SELECT #oid FROM #RefType WHERE #onClass.#name[en]='Post' and #onClass.#definedBy.#namespace='http://rdfs.org/sioc/ns'),"
				+ "'A Post that this Forum contains.')";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select p.#name, p.#scope.#name, p.#range.#oid, p.#definition, p.#definedBy.#namespace from #OWLObjectProperty p where #name='container_of'");
		rs.next();
		Assert.assertEquals("container_of", rs.getString(1));
		Assert.assertEquals("Forum", rs.getString(2));
		Assert.assertNotNull(rs.getString(3));
		Assert.assertEquals("A Post that this Forum contains.", rs.getString(4));
		Assert.assertEquals("http://rdfs.org/sioc/ns", rs.getString(5));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #RDFProperty (#name, #scope, #range, #definition) " + " VALUES ('content',  "
				+ "(SELECT #oid FROM #class WHERE #name='Post' and #definedBy.#namespace='http://rdfs.org/sioc/ns'),"
				+ "(SELECT max(#oid) FROM #StringType)," + "'The content of the Post.')";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select p.#name, p.#scope.#name, p.#range.#oid, p.#definition, p.#definedBy.#namespace from #RDFProperty p where #name='content'");
		rs.next();
		Assert.assertEquals("content", rs.getString(1));
		Assert.assertEquals("Post", rs.getString(2));
		Assert.assertNotNull(rs.getString(3));
		Assert.assertEquals("The content of the Post.", rs.getString(4));
		Assert.assertEquals("http://rdfs.org/sioc/ns", rs.getString(5));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #OWLDatatypeProperty (#name, #scope, #range, #definition, #OWLisDeprecated, #OWLversionInfo) "
				+ " VALUES ('content_encoded',  "
				+ "(SELECT #oid FROM #class WHERE #name='Post' and #definedBy.#namespace='http://rdfs.org/sioc/ns'),"
				+ "(SELECT max(#oid) FROM #StringType),"
				+ "'The encoded content of the Post, contained in CDATA areas.'," + "true,"
				+ "'This property is deprecated. Use content:encoded from RSS 1.0 content module.')";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select p.#name, p.#scope.#name, p.#range.#oid, p.#definition, p.#definedBy.#namespace from #OWLDatatypeProperty p where #name='content_encoded'");
		rs.next();
		Assert.assertEquals("content_encoded", rs.getString(1));
		Assert.assertEquals("Post", rs.getString(2));
		Assert.assertNotNull(rs.getString(3));
		Assert.assertEquals("The encoded content of the Post, contained in CDATA areas.", rs.getString(4));
		Assert.assertEquals("http://rdfs.org/sioc/ns", rs.getString(5));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #OWLDatatypeProperty (#name, #scope, #range, #definition, #OWLisDeprecated, #OWLversionInfo) "
				+ " VALUES ('created_at',  "
				+ "(SELECT #oid FROM #class WHERE #name='Post' and #definedBy.#namespace='http://rdfs.org/sioc/ns'),"
				+ "(SELECT max(#oid) FROM #StringType)," + "'When this was created, in ISO 8601 format.'," + "true,"
				+ "'This property is deprecated. Use dcterms:created from Dublin Core instead.')";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select p.#name, p.#scope.#name, p.#range.#oid, p.#definition, p.#definedBy.#namespace from #OWLDatatypeProperty p where #name='created_at'");
		rs.next();
		Assert.assertEquals("created_at", rs.getString(1));
		Assert.assertEquals("Post", rs.getString(2));
		Assert.assertNotNull(rs.getString(3));
		Assert.assertEquals("When this was created, in ISO 8601 format.", rs.getString(4));
		Assert.assertEquals("http://rdfs.org/sioc/ns", rs.getString(5));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #RefType (#onClass) VALUES ((SELECT #oid FROM #class WHERE #name='User' and #definedBy.#namespace='http://rdfs.org/sioc/ns'));";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery("SELECT #oid FROM #RefType WHERE #onClass.#name[en]='User'");
		rs.next();
		Assert.assertNotNull(rs.getString(1));
		Assert.assertFalse(rs.next());

		queryOntoQL = "INSERT INTO #OWLObjectProperty (#name, #scope, #range, #definition) "
				+ " VALUES ('creator_of',  "
				+ "(SELECT #oid FROM #class WHERE #name='Post' and #definedBy.#namespace='http://rdfs.org/sioc/ns'),"
				+ "(SELECT #oid FROM #RefType WHERE #onClass.#name[en]='User' and #onClass.#definedBy.#namespace='http://rdfs.org/sioc/ns'),"
				+ "'A Post that the User is a creator of.')";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select p.#name, p.#scope.#name, p.#range.#oid, p.#definition, p.#definedBy.#namespace from #OWLObjectProperty p where #name='creator_of'");
		rs.next();
		Assert.assertEquals("creator_of", rs.getString(1));
		Assert.assertEquals("Post", rs.getString(2));
		Assert.assertNotNull(rs.getString(3));
		Assert.assertEquals("A Post that the User is a creator of.", rs.getString(4));
		Assert.assertEquals("http://rdfs.org/sioc/ns", rs.getString(5));
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLInverseFunctionalProperty UNDER #OWLObjectProperty";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select p.#name[en], equiv.#OWLisDeprecated, p.#OWLinverseOf.#name[en] from #OWLInverseFunctionalProperty p, unnest(p.#OWLequivalentProperty) as equiv");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLSymetricProperty UNDER #OWLObjectProperty";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select p.#name[en], equiv.#OWLisDeprecated, p.#OWLinverseOf.#name[en] from #OWLSymetricProperty p, unnest(p.#OWLequivalentProperty) as equiv");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLTransitiveProperty UNDER #OWLObjectProperty";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select p.#name[en], equiv.#OWLisDeprecated, p.#OWLinverseOf.#name[en] from #OWLTransitiveProperty p, unnest(p.#OWLequivalentProperty) as equiv");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLRestriction UNDER #OWLClass (";
		queryOntoQL += "#OWLonProperty REF(#OWLProperty))";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery("select c.#name[en], c.#OWLonProperty.#name[en] from #OWLRestriction c");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLHasValueRestriction UNDER #OWLRestriction (";
		queryOntoQL += "#OWLhasLiteralValue String,";
		queryOntoQL += "#OWLhasIndividualValue REF(RootClass))";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#name[en], c.#OWLonProperty.#name[en], c.#OWLhasLiteralValue, c.#OWLhasIndividualValue.oid from #OWLHasValueRestriction c");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLAllValuesFromRestriction UNDER #OWLRestriction (";
		queryOntoQL += "#OWLallValuesFromDataRange REF(#datatype))";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#name[en], c.#OWLonProperty.#name[en], c.#OWLallValuesFromDataRange.#oid from #OWLAllValuesFromRestriction c");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLSomeValuesFromRestriction UNDER #OWLRestriction (";
		queryOntoQL += "#OWLsomeValuesFromDataRange REF(#datatype))";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#name[en], c.#OWLonProperty.#name[en], c.#OWLsomeValuesFromDataRange.#oid from #OWLSomeValuesFromRestriction c");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLCardinalityRestriction UNDER #OWLRestriction (";
		queryOntoQL += "#OWLcardinity INT)";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#name[en], c.#OWLonProperty.#name[en], c.#OWLcardinity from #OWLCardinalityRestriction c");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLMaxCardinalityRestriction UNDER #OWLRestriction (";
		queryOntoQL += "#OWLmaxCardinity INT)";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#name[en], c.#OWLonProperty.#name[en], c.#OWLmaxCardinity from #OWLMaxCardinalityRestriction c");
		Assert.assertFalse(rs.next());

		queryOntoQL = "CREATE ENTITY #OWLMinCardinalityRestriction UNDER #OWLRestriction (";
		queryOntoQL += "#OWLminCardinity INT)";
		res = statement.executeUpdate(queryOntoQL);
		rs = statement.executeQuery(
				"select c.#name[en], c.#OWLonProperty.#name[en], c.#OWLminCardinity from #OWLMinCardinalityRestriction c");
		Assert.assertFalse(rs.next());

		// queryOntoQL = "CREATE #CLASS RootClass (PROPERTIES(OWLdifferentFrom
		// REF(RootClass) ARRAY, OWLsameAs REF(RootClass) ARRAY))";
		// res = statement.executeUpdate(queryOntoQL);
		// rs = statement
		// .executeQuery("select i.oid, dif.oid, same.oid from RootClass i,
		// unnest(i.OWLdifferentFrom) as dif, unnest(i.OWLsameAs) as same");
		// Assert.assertFalse(rs.next());

		t.rollback();

		s.close();
	}

	public void testInsertSioc() throws SQLException {

		// try {
		// Class.forName("org.postgresql.Driver");
		// }
		// catch (Exception ex) {
		// }
		//
		// try {
		// database = DriverManager.getConnection(url, usr, pwd); // on ouvre
		// // la
		// // connexion
		//
		// sessionOWL = new OntoQLSessionImpl(database);
		// sessionOWL.setReferenceLanguage(ModelUtil.ENGLISH);
		//
		// // Insertion de SIOC
		// String queryOntoQL =
		// "INSERT INTO #OWLOntology (#namespace, #OWLversionInfo) VALUES
		// ('http://rdfs.org/sioc/ns', 'Revision: 1.10')";
		// OntoQLStatement statement = sessionOWL.createOntoQLStatement();
		// int res = statement.executeUpdate(queryOntoQL);
		// OntoQLResultSet rs = statement
		// .executeQuery("select o.#namespace, o.#OWLversionInfo from #OWLOntology o");
		// rs.next();
		// Assert.assertEquals("http://rdfs.org/sioc/ns", rs.getString(1));
		// Assert.assertEquals("Revision: 1.10", rs.getString(2));
		// Assert.assertFalse(rs.next());
		// a
		// queryOntoQL =
		// "INSERT INTO #RDFSClass (#name, #definition, #definedBy)"
		// + " VALUES ('Community', "
		// +
		// "'Community is a top level concept that defines an online community and what
		// it consists of.',"
		// +
		// "(SELECT #oid from #ontology where #namespace = 'http://rdfs.org/sioc/ns'))";
		// res = statement.executeUpdate(queryOntoQL);
		// rs = statement
		// .executeQuery("select c.#name, c.#definition, c.#definedBy.#namespace from
		// #RDFSClass c");
		// rs.next();
		// Assert.assertEquals("Community", rs.getString(1));
		// Assert.assertEquals(
		// "Community is a top level concept that defines an online community and what
		// it consists of.",
		// rs.getString(2));
		// Assert.assertFalse(rs.next());
		//
		// queryOntoQL =
		// "INSERT INTO #RDFSClass (#name, #definition, #definedBy) "
		// + " VALUES ('Forum', "
		// +
		// "'A discussion area on which Posts or entries are made. Synonyms include
		// channel and feed.',"
		// +
		// "(SELECT #oid from #ontology where #namespace = 'http://rdfs.org/sioc/ns'))";
		// res = statement.executeUpdate(queryOntoQL);
		// rs = statement
		// .executeQuery("select c.#name, c.#definition, c.#definedBy.#namespace from
		// #RDFSClass c where #name='Forum'");
		// rs.next();
		// Assert.assertEquals("Forum", rs.getString(1));
		// Assert.assertEquals("Community", rs.getString(2));
		// Assert.assertFalse(rs.next());
		//
		// sessionOWL.connection().rollback();
		// sessionOWL.close();
		getSession().close();

		// }
		// catch (Exception ex) {
		// sessionOWL.connection().rollback();
		// session.close();
		// ex.printStackTrace();
		// fail();
		// }
	}
}
