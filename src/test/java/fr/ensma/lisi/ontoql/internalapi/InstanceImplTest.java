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
import fr.ensma.lisi.ontoql.core.AbstractFactoryEntityDB;
import fr.ensma.lisi.ontoql.core.ontodb.FactoryEntityOntoDB;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.ontoapi.Instance;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

public class InstanceImplTest extends OntoQLTestCase {

	public AbstractFactoryEntityDB factoryDB;

	public OntoQLSession s;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);
		factoryDB = new FactoryEntityOntoDB(s);
	}

	@Test
	public void testLoad() {
		Instance instance = factoryDB.createInstance("100", cHudson);
		Assert.assertEquals(instance.getOid(), "100");
		Assert.assertNull(instance.getFloatPropertyValue("Buoyancy"));
		Assert.assertNull(instance.getStringPropertyValue("Weight of the user"));
		Assert.assertNull(instance.getStringPropertyValue("Chest measurement"));
		Assert.assertNull(instance.getStringPropertyValue("Buoyancy RAFT"));
		Assert.assertEquals(instance.getStringPropertyValue("Size"), "XL");
		Assert.assertEquals(instance.getStringPropertyValue("Reference"), "300062");
		Assert.assertEquals((int)instance.getIntPropertyValue("virage"), 1);
		Instance instanceRefMuff = instance.getInstancePropertyValue("its_muff");
		Assert.assertEquals(instanceRefMuff.getOid(), "423");
		// For the moment, this instance is not loaded
		Instance instanceRefSlalom = instanceRefMuff.getInstancePropertyValue("its_slalom");
		Assert.assertNull(instanceRefMuff.getStringPropertyValue("Reference"));
		// now this is the case
		Assert.assertEquals(instanceRefSlalom.getOid(), "164");
		Assert.assertEquals(instanceRefSlalom.getStringPropertyValue("Reference"), "305000");
		Assert.assertEquals(instanceRefSlalom.getStringPropertyValue("@71DC58B950F36-001"), "Rouge");

		s.close();
	}
}
