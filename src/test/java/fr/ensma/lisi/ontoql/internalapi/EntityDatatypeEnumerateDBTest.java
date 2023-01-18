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

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.core.AbstractFactoryEntityDB;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeEnumerate;
import fr.ensma.lisi.ontoql.core.ontodb.EntityDatatypeEnumerateOntoDB;
import fr.ensma.lisi.ontoql.core.ontodb.FactoryEntityOntoDB;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.util.ArrayHelper;

/**
 * @author Stéphane JEAN
 */
public class EntityDatatypeEnumerateDBTest extends OntoQLTestCase {

	public OntoQLSession s;

	public AbstractFactoryEntityDB factoryDB;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		s = getSession();
		factoryDB = new FactoryEntityOntoDB(s);
	}

	@Test
	public void testGetBooleanOperators() {
		EntityDatatypeEnumerateOntoDB dt = (EntityDatatypeEnumerateOntoDB) factoryDB
				.createEntityDatatype(EntityDatatype.ENNUMERATE_NAME);
		String[] operators = dt.getBooleanOperators();
		Assert.assertEquals(operators.length, 7);
		Assert.assertEquals(operators[0], EntityDatatype.OP_IN);
		s.close();
	}

	@Test
	public void testGetValues() {
		EntityDatatypeEnumerate dt = (EntityDatatypeEnumerate) factoryDB
				.createEntityDatatype(EntityDatatype.ENNUMERATE_NAME);
		dt.setInternalId("1560");
		List<String> values = dt.getValues();
		Assert.assertEquals(values.size(), 28);
		Assert.assertTrue(ArrayHelper.contain(values.toArray(), "43 - 46"));
		Assert.assertTrue(ArrayHelper.contain(values.toArray(), "39 - 42"));
		s.close();
	}
}
