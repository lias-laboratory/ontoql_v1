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

import org.junit.Assert;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Stéphane JEAN
 */
public class ConditionTest extends OntoQLTestCase {

	public Condition cond;

	@Test
	public void setUp() throws Exception {
		super.setUp();
		QueryOntoQLTable ontoqlTable = new QueryOntoQLTable();
		cond = new Condition(ontoqlTable);
		ConditionElement ce = new ConditionElement(cond);
		ce.setCste("'%A'");
		ce.setBooleanOperator("LIKE");
		ConditionElement ce2 = new ConditionElement(cond);
		ce2.setCste("'TOTO'");
		ce2.setBooleanOperator("=");
		ConditionElement ce3 = new ConditionElement(cond);
		ce3.setCste("'TITI'");
		ce3.setBooleanOperator("<");
		ConditionElement ce4 = new ConditionElement(cond);
		ce4.setCste("'SJEA%'");
		ce4.setBooleanOperator("LIKE");
		ConditionElement ce5 = new ConditionElement(cond);
		ce5.setCste("'Test'");
		ce5.setBooleanOperator(">");
		ConditionElement ce6 = new ConditionElement(cond);
		ce6.setCste("'View'");
		ce6.setBooleanOperator(">");
		ce.setOnProperty(pSize);
		ce2.setOnProperty(pSize);
		ce3.setOnProperty(pSize);
		ce4.setOnProperty(pSize);
		ce5.setOnProperty(pSize);
		ce6.setOnProperty(pSize);

		cond.setConditions(new ConditionElement[] { ce, ce2, ce3, ce4, ce5, ce6 });
	}

	@Test
	public void testToString() {
		getSession().setReferenceLanguage(OntoQLHelper.ENGLISH);
		Assert.assertEquals(
				"Size LIKE '%A'  AND Size = 'TOTO'  AND Size < 'TITI'  AND Size LIKE 'SJEA%'  AND Size > 'Test'  AND Size > 'View' ",
				cond.toString());
		getSession().close();
	}
}
