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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.core.AbstractEntityClass;
import fr.ensma.lisi.ontoql.core.AbstractFactoryEntityDB;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCategory;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCollection;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.core.ontodb.EntityDatatypeEnumerateOntoDB;
import fr.ensma.lisi.ontoql.core.ontodb.FactoryEntityOntoDB;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.jobdbc.impl.OntoQLSessionImpl;
import fr.ensma.lisi.ontoql.util.ArrayHelper;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Mickael BARON
 */
public class EntityPropertyTest extends OntoQLTestCase {

	private static final Log log = LogFactory.getLog(EntityPropertyTest.class);

	public AbstractFactoryEntityDB factoryDB, factoryDBFixation;

	public OntoQLSession s, sFixation;

	// The connection to the database.
	private Connection database;

	public EntityProperty reference, size, buoyancy, chest, its_slalom, norme, numero, pas;

	// Some context class.
	public AbstractEntityClass cags, hudson, muff;

	@Before
	public void setUp() throws Exception {
		super.setUp();

		try {
			database = DriverManager
					.getConnection("jdbc:postgresql://" + HOST + ":" + PORT + "/OntoQLJUnitTestMonoLingual", USR, PWD);
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		sFixation = new OntoQLSessionImpl(database);
		sFixation.setReferenceLanguage(OntoQLHelper.ENGLISH);
		sFixation.setDefaultNameSpace("1111");

		factoryDBFixation = new FactoryEntityOntoDB(sFixation);
		norme = (EntityProperty) factoryDBFixation.createDescription("Norme du composant");
		numero = (EntityProperty) factoryDBFixation.createDescription("@PROP_000002-001");
		pas = (EntityProperty) factoryDBFixation.createDescription("!1058");

		s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);
		factoryDB = new FactoryEntityOntoDB(s);
		reference = (EntityProperty) factoryDB.createDescription("Reference");
		chest = (EntityProperty) factoryDB.createDescription("\"Chest measurement\"");
		buoyancy = (EntityProperty) factoryDB.createDescription("@71DC249C74986-001");
		size = (EntityProperty) factoryDB.createDescription("!" + pSize.getInternalId());
		its_slalom = (EntityProperty) factoryDB.createDescription("its_slalom");
		cags = (AbstractEntityClass) factoryDB.createCategory("CAGS");
		muff = (AbstractEntityClass) factoryDB.createCategory("MUFFS");
		hudson = (AbstractEntityClass) factoryDB.createCategory("HUDSON");
	}

	@After
	public void tearDown() throws Exception {
		sFixation.close();
	}

	@Test
	public void testConstructor() {
		Assert.assertEquals(size.getInternalId(), pSize.getInternalId());
		Assert.assertEquals(buoyancy.getExternalId(), "71DC249C74986-001");
		Assert.assertEquals(chest.getName(), "Chest measurement");
		Assert.assertEquals(reference.getName(), "Reference");
		Assert.assertEquals(its_slalom.getName(), "its_slalom");
		Assert.assertEquals(its_slalom.getInternalId(), pItsSlalom.getInternalId());

		sFixation.setReferenceLanguage(null);
		Assert.assertEquals("1056", norme.getInternalId());
		Assert.assertEquals("PROP_000003-001", norme.getExternalId());
		Assert.assertEquals("Numero du composant", numero.getName());
		Assert.assertEquals("1057", numero.getInternalId());
		Assert.assertEquals("Pas du filetage", pas.getName());
		Assert.assertEquals("FAM_000005-001", pas.getExternalId());

		s.close();

	}

	@Test
	public void testGet() {
		Assert.assertEquals(size.getExternalId(), "71DC2486B78EF-001");
		Assert.assertEquals(size.getName(), "Size");

		Assert.assertEquals(buoyancy.getInternalId(), pBuoyancy.getInternalId());
		Assert.assertEquals(buoyancy.getName(), "Buoyancy");

		Assert.assertEquals(chest.getInternalId(), pChestMeasurement.getInternalId());
		Assert.assertEquals(chest.getExternalId(), "71DC249C6E4AC-001");

		Assert.assertEquals(reference.getInternalId(), pReference.getInternalId());
		Assert.assertEquals(reference.getExternalId(), "71DC24862420C-001");
		s.close();
	}

	@Test
	public void testSetCurrentLanguage() {
		size.setCurrentLanguage(OntoQLHelper.FRENCH);
		Assert.assertEquals(size.getName(), "Taille");
		Assert.assertEquals(reference.getName(), "Référence");
		s.close();
	}

	@Test
	public void testIsUsed() {
		// not used
		size.setCurrentContext(cags);
		Assert.assertFalse(size.isUsed());

		// used
		size.setCurrentContext(hudson);
		Assert.assertTrue(size.isUsed());
		s.close();
	}

	@Test
	public void testIsDefined() {
		// Defined
		size.setCurrentContext(cags);
		Assert.assertTrue(size.isDefined());

		// Not defined
		EntityProperty virage = (EntityProperty) factoryDB.createDescription("virage");
		virage.setCurrentContext(cags);
		boolean isDef = virage.isDefined();
		Assert.assertFalse(isDef); // return false only in debug mode executed step
		// by step ...
		s.close();
	}

	@Test
	public void testToSQL() {
		// current context not set
		Assert.assertEquals(size.toSQL(), "p" + pSize.getInternalId());

		// not used
		size.setCurrentContext(cags);
		Assert.assertEquals("NULL::varchar ", size.toSQL());

		// used
		size.setCurrentContext(hudson);
		Assert.assertEquals(size.toSQL(), "e" + cHudson.getInternalId() + ".p" + pSize.getInternalId());

		pNames.setCurrentContext(cFocBaby);
		Assert.assertEquals(pNames.toSQL(), "e" + cFocBaby.getInternalId() + ".p" + pNames.getInternalId());

		s.close();
	}

	@Test
	public void testEquals() {

		Assert.assertTrue(!size.equals(null));

		EntityProperty compareReference = (EntityProperty) factoryDB
				.createDescription("!" + pReference.getInternalId());
		Assert.assertEquals(reference, compareReference);

		EntityProperty compareSize = (EntityProperty) factoryDB.createDescription("!" + pSize.getInternalId());
		Assert.assertEquals(size, compareSize);

		EntityProperty compareChest = (EntityProperty) factoryDB
				.createDescription("!" + pChestMeasurement.getInternalId());
		Assert.assertEquals(chest, compareChest);

		EntityProperty compareBuoyancy = (EntityProperty) factoryDB.createDescription("!" + pBuoyancy.getInternalId());
		log.warn(compareBuoyancy.getInternalId());
		log.warn(buoyancy.getInternalId());
		Assert.assertTrue(buoyancy.equals(compareBuoyancy));

		EntityProperty referenceMuff = (EntityProperty) factoryDB.createDescription("!" + pReference.getInternalId());
		referenceMuff.setCurrentContext(muff);
		compareReference.setCurrentContext(hudson);
		// 2 properties with same identifier are equals even used in different
		// context
		Assert.assertTrue(reference.equals(compareReference));

		s.close();
	}

	@Test
	public void testGetRange() {

		EntityDatatype dtSize = size.getRange();
		Assert.assertEquals(dtSize.getInternalId(), "1560");
		try {
			EntityDatatypeEnumerateOntoDB dtEnumSize = (EntityDatatypeEnumerateOntoDB) dtSize;
			List<String> values = dtEnumSize.getValues();
			Assert.assertEquals(values.size(), 28);
			Assert.assertTrue(ArrayHelper.contain(values.toArray(), "43 - 46"));
			Assert.assertTrue(ArrayHelper.contain(values.toArray(), "39 - 42"));

		} catch (ClassCastException e) {
			Assert.fail();
		}

		EntityProperty itsMuffs = (EntityProperty) factoryDB.createDescription("!" + pItsMuff.getInternalId());
		Assert.assertEquals(((EntityDatatypeCategory) itsMuffs.getRange()).getCagetory().getInternalId(),
				cMuffs.getInternalId());
		EntityDatatype dt = itsMuffs.getRange();
		try {
			EntityDatatypeCategory dtClass = (EntityDatatypeCategory) dt;
			AbstractEntityClass cl = (AbstractEntityClass) dtClass.getCagetory();
			Assert.assertEquals(cl.getInternalId(), cMuffs.getInternalId());
			cl = (AbstractEntityClass) dtClass.getCagetory();
			Assert.assertEquals(cl.getInternalId(), cMuffs.getInternalId());

			Assert.assertEquals(((EntityDatatypeCategory) itsMuffs.getRange()).getCagetory().getInternalId(),
					cMuffs.getInternalId());
		} catch (ClassCastException e) {
			Assert.fail();
		}

		Assert.assertEquals(((EntityDatatypeCategory) its_slalom.getRange()).getCagetory().getInternalId(),
				cSlalom.getInternalId());

		s.close();
	}

	@Test
	public void testIsEnumerateType() {
		Assert.assertTrue(size.isEnumerateType());
		Assert.assertFalse(reference.isEnumerateType());
		s.close();
	}

	/**
	 * Test the creation of a new property.
	 */
	@Test
	public void testInsert() throws SQLException {

		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		// Set only the names in french and english
		Transaction t = s.beginTransaction();
		EntityProperty p = factoryDB.createEntityProperty();
		p.setName("immatriculation");
		p.setName("number", OntoQLHelper.ENGLISH);
		p.setScope(factoryDB.createEntityClass("HUDSON"));
		p.setRange(factoryDB.createEntityDatatype(EntityDatatype.STRING_NAME));
		p.insert();
		Assert.assertEquals("number", p.getName(OntoQLHelper.ENGLISH));
		Assert.assertNotNull(p.getInternalId());
		OntoQLStatement stmt = s.createOntoQLStatement();
		OntoQLResultSet rs = stmt.executeQuery("select p from #propriété p where p.#nom[en] = 'number'");
		rs.next();
		EntityProperty p1 = rs.getEntityProperty(1);
		Assert.assertEquals("number", p.getName(OntoQLHelper.ENGLISH));
		t.rollback();

		// Set the code and version
		t = s.beginTransaction();
		p.setVersion("001");
		p.setCode("BBB00X");
		p.insert();
		Assert.assertEquals("number", p.getName(OntoQLHelper.ENGLISH));
		stmt = s.createOntoQLStatement();
		rs = stmt.executeQuery("select p from #propriété p where p.#code = 'BBB00X'");
		rs.next();
		p1 = rs.getEntityProperty(1);
		Assert.assertEquals("number", p1.getName(OntoQLHelper.ENGLISH));
		t.rollback();

		// A property of datatype association
		t = s.beginTransaction();
		p.setVersion("001");
		p.setCode("BBB00X");
		EntityDatatype dt = factoryDB.createEntityDatatype(EntityDatatype.ASSOCIATION_NAME);
		((EntityDatatypeCategory) dt).setCategory(factoryDB.createEntityClass("HUDSON"));
		p.setRange(dt);
		p.insert();
		stmt = s.createOntoQLStatement();
		rs = stmt.executeQuery("select p from #propriété p where p.#code = 'BBB00X'");
		rs.next();
		p1 = rs.getEntityProperty(1);
		Assert.assertEquals("HUDSON", (((EntityDatatypeCategory) p1.getRange()).getCagetory().getName()));
		Assert.assertEquals("number", p1.getName(OntoQLHelper.ENGLISH));
		t.rollback();

		// A property of datatype collection
		t = s.beginTransaction();
		p.setVersion("001");
		p.setCode("BBB00X");
		EntityDatatype dtCollection = factoryDB.createEntityDatatype(EntityDatatype.COLLECTION_NAME);
		((EntityDatatypeCollection) dtCollection).setDatatype(dt);
		p.setRange(dtCollection);
		p.insert();
		stmt = s.createOntoQLStatement();
		rs = stmt.executeQuery("select p from #propriété p where p.#code = 'BBB00X'");
		rs.next();
		p1 = rs.getEntityProperty(1);
		Assert.assertEquals("REF", (((EntityDatatypeCollection) p1.getRange()).getDatatype().getName()));
		t.rollback();

		s.close();
	}
}
