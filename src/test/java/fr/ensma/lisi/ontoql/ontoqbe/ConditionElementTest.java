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
package fr.ensma.lisi.ontoql.ontoqbe;

import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.core.AbstractEntityProperty;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Stéphane JEAN
 */
public class ConditionElementTest extends OntoQLTestCase {

	protected ConditionElement ceExpected, ceExpected2, ceExpected3;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		QueryOntoQLTable ontoqlTable = new QueryOntoQLTable();
		Condition cond = new Condition(ontoqlTable);
		ceExpected = new ConditionElement(cond);
		ceExpected.setOnProperty(pReference);
		ceExpected.setBooleanOperator("LIKE");
		ceExpected.setCste("'%A'");

		ceExpected2 = new ConditionElement(cond);
		ceExpected2.setOnProperty(pSize);
		ceExpected2.setBooleanOperator(EntityDatatype.OP_IN);
		ceExpected2.setCste("('XL')");

		ceExpected3 = new ConditionElement(cond);
		ceExpected3.setOnProperty(pSize);
		ceExpected3.setBooleanOperator(EntityDatatype.OP_IN);
		ceExpected3.setCste("('43 - 46')");
	}

	@Test
	public void testIsNaturalJoinCdtElt() throws Exception {
		getSession().setReferenceLanguage(OntoQLHelper.ENGLISH);

		pReference.setCurrentContext(cOntario);
		AbstractEntityProperty referenceHudson = (AbstractEntityProperty) entityFactory
				.createDescription((String) properties.get("Reference"));
		referenceHudson.setCurrentContext(cHudson);
		QueryOntoQLTable ontoqlTable = new QueryOntoQLTable();
		Condition cond = new Condition(ontoqlTable);
		ConditionElement ce = new ConditionElement(cond);
		ce.setOnProperty(pReference);
		ce.setBooleanOperator("=");
		ce.setCste("HUDSON.Reference");

		Assert.assertTrue(ce.isNaturalJoinConditionElement());
		Assert.assertEquals(referenceHudson.getName(), ce.getRightPropertyName());
		Assert.assertEquals(referenceHudson.getCurrentContext().getName(), ce.getRightPropertyContextName());

		ce = new ConditionElement(cond);
		ce.setOnProperty(pReference);
		ce.setBooleanOperator("=");
		ce.setCste("ONTARIO.Size");
		Assert.assertFalse(ce.isNaturalJoinConditionElement());
		Assert.assertEquals("Size", ce.getRightPropertyName());

		ce = new ConditionElement(cond);
		ce.setOnProperty(pReference);
		ce.setBooleanOperator("LIKE");
		ce.setCste("HUDSON.Reference");
		Assert.assertFalse(ce.isNaturalJoinConditionElement());
		Assert.assertEquals(null, ce.getRightPropertyName());

		ce = new ConditionElement(cond, "=", "MUFFS.Reference");
		pReference.setCurrentContext(cHudson);
		ce.setOnProperty(pReference);
		Assert.assertTrue(ce.isNaturalJoinConditionElement());
		Assert.assertEquals("Reference", ce.getRightPropertyName());
		Assert.assertEquals("MUFFS", ce.getRightPropertyContextName());

		getSession().close();
	}

	@Test
	public void testToString() {
		getSession().setReferenceLanguage(OntoQLHelper.ENGLISH);
		Assert.assertEquals("Reference LIKE '%A' ", ceExpected.toString());
		Assert.assertEquals(ceExpected2.toString(), "Size IN ('XL') ");
		getSession().close();
	}

	@Test
	public void testConstructor() {
		Session s = getSession();

		QueryOntoQLTable ontoqlTable = new QueryOntoQLTable();
		Condition cond = new Condition(ontoqlTable);
		ConditionElement ceGet = new ConditionElement(cond);
		ceGet.setOnProperty(pReference);
		ceGet.initialize("LIKE '%A'");
		Assert.assertEquals(ceExpected, ceGet);

		ConditionElement ceGet2 = new ConditionElement(cond);
		ceGet2.setOnProperty(pSize);
		ceGet2.initialize("IN ('XL')");
		Assert.assertEquals(ceExpected2, ceGet2);

		ConditionElement ceGet3 = new ConditionElement(cond);
		ceGet3.setOnProperty(pSize);
		ceGet3.initialize("IN ('43 - 46')");

		Assert.assertEquals(ceExpected3, ceGet3);

		s.close();
	}
}
