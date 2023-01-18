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
package fr.ensma.lisi.ontoql.internalapi;

import org.junit.Assert;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.ontodb.EntityDatatypeIntOntoDB;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;

/**
 * @author Stéphane JEAN
 */
public class EntityDatatypeIntDBTest extends OntoQLTestCase {

	public OntoQLSession s;

	@Test
	public void testGetBooleanOperators() {
		s = getSession();
		EntityDatatypeIntOntoDB dt = new EntityDatatypeIntOntoDB(s);
		String[] operators = dt.getBooleanOperators();
		Assert.assertEquals(operators.length, 5);
		Assert.assertEquals(operators[0], EntityDatatype.OP_EG);
		Assert.assertEquals(operators[1], EntityDatatype.OP_SUP);
		Assert.assertEquals(operators[2], EntityDatatype.OP_INF);
		Assert.assertEquals(operators[3], EntityDatatype.OP_SUPEG);
		Assert.assertEquals(operators[4], EntityDatatype.OP_INFEG);
		s.close();
	}
}
