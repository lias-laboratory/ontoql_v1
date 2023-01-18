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
import fr.ensma.lisi.ontoql.core.AbstractEntityClass;
import fr.ensma.lisi.ontoql.core.AbstractFactoryEntityDB;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.ontodb.EntityDatatypeCategoryOntoDB;
import fr.ensma.lisi.ontoql.core.ontodb.FactoryEntityOntoDB;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Mickael BARON
 */
public class EntityDatatypeClassOntoDBTest extends OntoQLTestCase {

	public OntoQLSession s;

	public AbstractFactoryEntityDB factoryDB;

	public EntityClass hudson, muffs;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);
		factoryDB = new FactoryEntityOntoDB(s);
		hudson = (EntityClass) factoryDB.createCategory("@71DC2FE2C64B8-001");
		muffs = (EntityClass) factoryDB.createCategory("!" + cMuffs.getInternalId());
	}

	@Test
	public void testGetClass() {
		EntityDatatypeCategoryOntoDB dt = (EntityDatatypeCategoryOntoDB) factoryDB
				.createEntityDatatype(EntityDatatype.ASSOCIATION_NAME);
		dt.setInternalId("6224");

		AbstractEntityClass range = (AbstractEntityClass) dt.getCagetory();
		Assert.assertTrue(range.equals(muffs));
		range.setCategoryAlias("m");
		range = (AbstractEntityClass) dt.getCagetory();
		Assert.assertTrue(range.equals(muffs));
		Assert.assertTrue(range.getCategoryAlias().equals("m"));
		s.close();
	}
}
