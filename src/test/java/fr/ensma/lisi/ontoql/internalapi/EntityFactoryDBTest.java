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
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCategory;
import fr.ensma.lisi.ontoql.core.ontodb.EntityDatatypeEnumerateOntoDB;
import fr.ensma.lisi.ontoql.core.ontodb.EntityDatatypeIntOntoDB;
import fr.ensma.lisi.ontoql.core.ontodb.EntityDatatypeStringOntoDB;
import fr.ensma.lisi.ontoql.core.ontodb.FactoryEntityOntoDB;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.util.OntoDBHelper;

/**
 * @author Stéphane JEAN
 */
public class EntityFactoryDBTest extends OntoQLTestCase {

	public OntoQLSession s;

	public FactoryEntityOntoDB factoryDB;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		s = getSession();
		factoryDB = new FactoryEntityOntoDB(s);
	}

	@Test
	public void testCreateDescription() {
		s.close();
	}

	@Test
	public void testCreateEntityDatatype() {
		EntityDatatype datatypeCreated;

		datatypeCreated = factoryDB.createEntityDatatype(OntoDBHelper.ASSOCIATION_TYPE_TABLE, "6235");
		try {
			((EntityDatatypeCategory) datatypeCreated).getCagetory();
		} catch (ClassCastException e) {
			Assert.fail();
		}

		datatypeCreated = factoryDB.createEntityDatatype(EntityDatatype.ENNUMERATE_NAME);
		try {
			EntityDatatypeEnumerateOntoDB d = (EntityDatatypeEnumerateOntoDB) datatypeCreated;
			Assert.assertNotNull(d.toString());
		} catch (ClassCastException e) {
			Assert.fail();
		}

		datatypeCreated = factoryDB.createEntityDatatype(EntityDatatype.INT_NAME);
		try {
			EntityDatatypeIntOntoDB d = (EntityDatatypeIntOntoDB) datatypeCreated;
			Assert.assertNotNull(d.toString());
		} catch (ClassCastException e) {
			Assert.fail();
		}

		datatypeCreated = factoryDB.createEntityDatatype(EntityDatatype.STRING_NAME);
		try {
			EntityDatatypeStringOntoDB d = (EntityDatatypeStringOntoDB) datatypeCreated;
			Assert.assertNotNull(d.toString());
		} catch (ClassCastException e) {
			Assert.fail();
		}

		s.close();

	}
}
