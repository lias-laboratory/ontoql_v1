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
package fr.ensma.lisi.ontoql.ontoapi;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.core.AbstractFactoryEntityDB;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.exception.QueryException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Test that the datamodel of OntoQL can be extended and that these extensions
 * are taken into account for the DQL, DDL, the API and the table of the
 * database.
 * 
 * @author Stéphane Jean
 */
public class OntoQLOntoAPITest extends OntoQLTestCase {

	public AbstractFactoryEntityDB factoryDB;

	/**
	 * Test if the extension are taken into account by the data definition language
	 */
	@Test
	public void testLoadOnDemandAPI() throws SQLException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		OntoClass c = s.newOntoClass(1040);

		// test the value of its primitive attributes (loaded by default)
		Assert.assertEquals("71DC247C9EE8C", c.getCode());
		Assert.assertEquals("PERSONAL EQUIPEMENT/SAFETY", c.getName());
		Assert.assertEquals("EQUIPEMENT DE LA PERSONNE/SECURITE", c.getName(OntoQLHelper.FRENCH));
		Assert.assertEquals("          ", c.getDefinition());
		Assert.assertEquals("", c.getDefinition(OntoQLHelper.FRENCH));

		// test the values of its reference
		OntoOntology ontology = c.getDefinedBy();
		Assert.assertEquals(1016, ontology.getOid());
		Assert.assertEquals("http://lisi.ensma.fr/", ontology.getNamespace());

		// properties defined by this class as applicable
		Set propsDefinedObtained = c.getScopeProperties();
		Set propsDefinedExpected = new HashSet();
		propsDefinedExpected.add(new OntoProperty(1204, s));
		propsDefinedExpected.add(new OntoProperty(20476, s));
		Assert.assertEquals(propsDefinedExpected.size(), propsDefinedObtained.size());
		Assert.assertTrue(propsDefinedObtained.containsAll(propsDefinedExpected));

		// all properties applicables
		Set propsApplicablesObtained = c.getProperties();
		Set propsApplicablesExpected = new HashSet();
		propsApplicablesExpected.add(new OntoProperty(1204, s));
		propsApplicablesExpected.add(new OntoProperty(20476, s));
		Assert.assertEquals(propsApplicablesExpected.size(), propsApplicablesObtained.size());
		Assert.assertTrue(propsApplicablesObtained.containsAll(propsApplicablesExpected));

		// all properties used
		Set propsUsedObtained = c.getUsedProperties();
		Assert.assertEquals(0, propsUsedObtained.size());

		// direct superclasses of this class
		Set superClassObtained = c.getDirectSuperclasses();
		Set superClassExpected = new HashSet();
		Assert.assertEquals(superClassExpected.size(), superClassObtained.size());
		Assert.assertTrue(superClassObtained.containsAll(superClassExpected));

		// all superclasses of this class
		superClassObtained = c.getSuperclasses();
		superClassExpected = new HashSet();
		Assert.assertEquals(superClassExpected.size(), superClassObtained.size());
		Assert.assertTrue(superClassObtained.containsAll(superClassExpected));

		// direct subclasses of this class
		Set subClassObtained = c.getDirectSubclasses();
		Set subClassExpected = new HashSet();
		subClassExpected.add(new OntoClass(1039, s));
		subClassExpected.add(new OntoClass(1047, s));
		subClassExpected.add(new OntoClass(1059, s));
		subClassExpected.add(new OntoClass(1068, s));
		subClassExpected.add(new OntoClass(1088, s));
		subClassExpected.add(new OntoClass(1100, s));

		Assert.assertEquals(subClassExpected.size(), subClassObtained.size());
		Assert.assertTrue(subClassObtained.containsAll(subClassExpected));

		// all superclasses of this class
		subClassObtained = c.getSubclasses();
		subClassExpected = new HashSet();
		subClassExpected.add(new OntoClass(1024, s));
		subClassExpected.add(new OntoClass(1026, s));
		subClassExpected.add(new OntoClass(1020, s));
		subClassExpected.add(new OntoClass(1021, s));
		subClassExpected.add(new OntoClass(1028, s));
		subClassExpected.add(new OntoClass(1027, s));

		Assert.assertEquals(89, subClassObtained.size());
		Assert.assertTrue(subClassObtained.containsAll(subClassExpected));

		Assert.assertEquals("001", c.getVersion());
		Assert.assertEquals("001", c.getRevision());
		Assert.assertEquals(null, c.getNote());
		Assert.assertEquals(null, c.getRemark());
		Assert.assertEquals("          ", c.getShortName());
		Assert.assertEquals(null, c.getDateOfCurrentVersion());
		Assert.assertEquals(null, c.getDateOfCurrentRevision());
		Assert.assertEquals(null, c.getDateOfOriginalDefinition());

		Assert.assertEquals(null, c.getIcon());
		Assert.assertEquals(null, c.getDocOfDefinition());

		// same test but the class is initialized with its name
		c = s.newOntoClass(0);
		c.setName("PERSONAL EQUIPEMENT/SAFETY");

		// test the value of its primitive attributes (loaded by default)
		Assert.assertEquals("71DC247C9EE8C", c.getCode());
		Assert.assertEquals("PERSONAL EQUIPEMENT/SAFETY", c.getName());
		Assert.assertEquals("EQUIPEMENT DE LA PERSONNE/SECURITE", c.getName(OntoQLHelper.FRENCH));
		Assert.assertEquals("          ", c.getDefinition());
		Assert.assertEquals("", c.getDefinition(OntoQLHelper.FRENCH));

		// test the values of its reference
		ontology = c.getDefinedBy();
		Assert.assertEquals(1016, ontology.getOid());
		Assert.assertEquals("http://lisi.ensma.fr/", ontology.getNamespace());

		// same test but the class is initialized with its code
		c = s.newOntoClass(0);
		c.setCode("71DC247C9EE8C");
		c.setVersion("001");

		// test the value of its primitive attributes (loaded by default)
		Assert.assertEquals("71DC247C9EE8C", c.getCode());
		Assert.assertEquals("PERSONAL EQUIPEMENT/SAFETY", c.getName());
		Assert.assertEquals("EQUIPEMENT DE LA PERSONNE/SECURITE", c.getName(OntoQLHelper.FRENCH));
		Assert.assertEquals("          ", c.getDefinition());
		Assert.assertEquals("", c.getDefinition(OntoQLHelper.FRENCH));

		// test the values of its reference
		ontology = c.getDefinedBy();
		Assert.assertEquals(1016, ontology.getOid());
		Assert.assertEquals("http://lisi.ensma.fr/", ontology.getNamespace());

		s.close();
	}

	/**
	 * Test queries that return an element of OntoAPI.
	 * 
	 * @throws SQLException
	 * @throws QueryException
	 */
	@Test
	public void testOntologyAPIQuery() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String queryOntoQL = "SELECT c FROM #class c WHERE c.#name[en]='CAGS'";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSet = statement.executeQuery(queryOntoQL);
		resultSet.next();
		EntityClass classCags = resultSet.getEntityClass(1);
		Assert.assertEquals("CAGS", classCags.getName());
		Assert.assertEquals("1068", classCags.getInternalId());
		Assert.assertEquals("71DC2FDBFD904-001", classCags.getExternalId());

		queryOntoQL = "SELECT p FROM #property p WHERE p.#name[en]='Size'";
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery(queryOntoQL);
		resultSet.next();
		EntityProperty propSize = resultSet.getEntityProperty(1);
		Assert.assertEquals("Size", propSize.getName());
		Assert.assertEquals("1202", propSize.getInternalId());
		Assert.assertEquals("71DC2486B78EF-001", propSize.getExternalId());
		EntityDatatype rangeSize = propSize.getRange();
		Assert.assertEquals("NON_QUANTITATIVE_CODE_TYPE", rangeSize.getName());

		queryOntoQL = "SELECT p.#scope FROM #property p WHERE p.#name[en]='Size'";
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery(queryOntoQL);
		resultSet.next();
		classCags = resultSet.getEntityClass(1);
		Assert.assertEquals("PERSONAL EQUIPEMENT/SAFETY", classCags.getName());

		s.close();
	}
}