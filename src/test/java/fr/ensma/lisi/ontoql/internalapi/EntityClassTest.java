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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.core.AbstractFactoryEntityDB;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.core.ontodb.EntityDatatypeCategoryOntoDB;
import fr.ensma.lisi.ontoql.core.ontodb.FactoryEntityOntoDB;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.jobdbc.impl.OntoQLSessionImpl;
import fr.ensma.lisi.ontoql.util.ArrayHelper;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Stéphane JEAN
 */
public class EntityClassTest extends OntoQLTestCase {

	private static final Log log = LogFactory.getLog(EntityClassTest.class);

	public AbstractFactoryEntityDB factoryDB, factoryDBFixation;

	public EntityClass personalequipment, cags, anchorage, hudson, muffs;

	// To test subclass
	public EntityClass standardNeoprene, neopreneMuffs, neopreneMuffsForChildren, fjord, ontario, randonnee, standard,
			ecrou, vis, goujon;

	// To test getDefined, getUsed
	public EntityProperty reference, itsSlalom, buoyancy, size, chest, buoyancyRaft, weightOfTheUser, virage, its_muff;

	public OntoQLSession s, sFixation;

	private Connection database; // The connection to the database

	@Before
	public void setUp() throws Exception {
		super.setUp();

		try {
			Class.forName("org.postgresql.Driver");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			database = DriverManager
					.getConnection("jdbc:postgresql://" + HOST + ":" + PORT + "/OntoQLJUnitTestMonoLingual", USR, PWD); // on
																														// ouvre
																														// la
																														// connexion
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		sFixation = new OntoQLSessionImpl(database);
		sFixation.setDefaultNameSpace("1111");
		sFixation.setReferenceLanguage(OntoQLHelper.ENGLISH);

		factoryDBFixation = new FactoryEntityOntoDB(sFixation);
		ecrou = (EntityClass) factoryDBFixation.createCategory("Ecrou");
		vis = (EntityClass) factoryDBFixation.createCategory("@FAM_000002-001");
		goujon = (EntityClass) factoryDBFixation.createCategory("!1020");

		s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);
		factoryDB = new FactoryEntityOntoDB(s);
		cags = (EntityClass) factoryDB.createCategory("CAGS");
		anchorage = (EntityClass) factoryDB.createCategory("\"ANCHORAGE (KAYAK PANTS)\"");
		hudson = (EntityClass) factoryDB.createCategory("@71DC2FE2C64B8-001");
		muffs = (EntityClass) factoryDB.createCategory("!" + cMuffs.getInternalId());

		personalequipment = (EntityClass) factoryDB.createCategory("!" + cPersonnalEquipementSafety.getInternalId());

		neopreneMuffs = (EntityClass) factoryDB.createCategory("!" + cNeopreneMuffs.getInternalId());
		neopreneMuffsForChildren = (EntityClass) factoryDB
				.createCategory("!" + cNeopreneMuffsForChildren.getInternalId());
		standardNeoprene = (EntityClass) factoryDB.createCategory("!" + cStandardNeopreneMuffs.getInternalId());
		fjord = (EntityClass) factoryDB.createCategory("!" + cFjord.getInternalId());
		ontario = (EntityClass) factoryDB.createCategory("!" + cOntario.getInternalId());
		randonnee = (EntityClass) factoryDB.createCategory("!" + cRandonnee.getInternalId());
		standard = (EntityClass) factoryDB.createCategory("!" + cStandard.getInternalId());

		reference = (EntityProperty) factoryDB.createDescription("!" + pReference.getInternalId());
		itsSlalom = (EntityProperty) factoryDB.createDescription("!" + pItsSlalom.getInternalId());
		buoyancy = (EntityProperty) factoryDB.createDescription("!" + pBuoyancy.getInternalId());
		size = (EntityProperty) factoryDB.createDescription("!" + pSize.getInternalId());
		chest = (EntityProperty) factoryDB.createDescription("!" + pChestMeasurement.getInternalId());
		buoyancyRaft = (EntityProperty) factoryDB.createDescription("!" + pBuoyancyRaft.getInternalId());
		weightOfTheUser = (EntityProperty) factoryDB.createDescription("!" + pWeightOfTheUser.getInternalId());
		virage = (EntityProperty) factoryDB.createDescription("!" + pVirage.getInternalId());
		its_muff = (EntityProperty) factoryDB.createDescription("!" + pItsMuff.getInternalId());
	}

	@Test
	public void testConstructor() {
		Assert.assertEquals(muffs.getInternalId(), cMuffs.getInternalId());
		Assert.assertEquals(hudson.getExternalId(), "71DC2FE2C64B8-001");
		Assert.assertEquals(anchorage.getName(), "ANCHORAGE (KAYAK PANTS)");
		Assert.assertEquals(cags.getName(), "CAGS");

		factoryDB.createCategory("DAGGER");
		Assert.assertEquals(muffs.getInternalId(), "1105");
		EntityClass stranger = (EntityClass) factoryDB.createCategory("!1090");
		Assert.assertEquals(stranger.getName(), "DAGGER");

		Assert.assertEquals(ecrou.getInternalId(), "1017");
		Assert.assertEquals("FAM_000004-001", ecrou.getExternalId());
		Assert.assertEquals("Vis", vis.getName());
		Assert.assertEquals("1019", vis.getInternalId());
		Assert.assertEquals("Goujon", goujon.getName());
		Assert.assertEquals("FAM_000003-001", goujon.getExternalId());

		s.close();
	}

	@Test
	public void testCreateExtent() throws SQLException {
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		Transaction t = s.beginTransaction();
		EntityClass c = factoryDB.createEntityClass("CAGS");
		Assert.assertTrue(c.isAbstract());
		EntityProperty[] properties = new EntityProperty[] { pSize, pReference };
		c.createTable(properties);
		Assert.assertFalse(c.isAbstract());
		t.rollback();

		s.close();
	}

	@Test
	public void testInsert() throws SQLException {
		s.setReferenceLanguage(OntoQLHelper.FRENCH);

		// Set only the names in french and english
		Transaction t = s.beginTransaction();
		EntityClass c = factoryDB.createEntityClass();
		c.setName("Véhicule");
		c.setName("Vehicle", OntoQLHelper.ENGLISH);
		c.insert();
		Assert.assertEquals("Vehicle", c.getName(OntoQLHelper.ENGLISH));
		OntoQLStatement stmt = s.createOntoQLStatement();
		OntoQLResultSet rs = stmt.executeQuery("select c from #classe c where c.#nom[en] = 'Vehicle'");
		rs.next();
		EntityClass c1 = rs.getEntityClass(1);
		Assert.assertEquals("Vehicle", c1.getName(OntoQLHelper.ENGLISH));
		t.rollback();

		// Set the code and version
		t = s.beginTransaction();
		c.setVersion("001");
		c.setCode("BBB00X");
		c.insert();
		Assert.assertEquals("Vehicle", c.getName(OntoQLHelper.ENGLISH));
		stmt = s.createOntoQLStatement();
		rs = stmt.executeQuery("select c from #classe c where c.#nom[en] = 'Vehicle'");
		rs.next();
		c1 = rs.getEntityClass(1);
		Assert.assertEquals("Vehicle", c1.getName(OntoQLHelper.ENGLISH));
		t.rollback();

		// set its superclass
		t = s.beginTransaction();
		c.setSuperClass(cags);
		c.insert();
		stmt = s.createOntoQLStatement();
		rs = stmt.executeQuery(
				"select c from #classe c, unnest(c.#superClassesDirectes) as directSuperclass where c.#nom[en] = 'Vehicle' and directSuperclass.#nom[en] = 'CAGS'");
		rs.next();
		c1 = rs.getEntityClass(1);
		Assert.assertEquals("Vehicle", c1.getName(OntoQLHelper.ENGLISH));
		t.rollback();

		// set its properties
		t = s.beginTransaction();
		EntityProperty p = factoryDB.createEntityProperty();
		p.setName("number", OntoQLHelper.ENGLISH);
		p.setRange(factoryDB.createEntityDatatype(EntityDatatype.STRING_NAME));
		p.setScope(c);
		c.setScopeProperties(new EntityProperty[] { p });
		c.insert();
		stmt = s.createOntoQLStatement();
		rs = stmt.executeQuery("select c from #classe c, #propriété p where p.#nom[en] = 'number' and p.#domaine = c");
		rs.next();
		p.setCurrentContext(c);
		Assert.assertTrue(p.isDefined());
		c1 = rs.getEntityClass(1);
		Assert.assertEquals("Vehicle", c1.getName(OntoQLHelper.ENGLISH));
		t.rollback();

		// set its properties
		t = s.beginTransaction();
		EntityProperty p1 = factoryDB.createEntityProperty();
		p1.setName("number", OntoQLHelper.FRENCH);
		p1.setRange(factoryDB.createEntityDatatype(EntityDatatype.STRING_NAME));
		p1.setScope(c);

		EntityProperty p3 = factoryDB.createEntityProperty();
		p3.setName("nbRoues", OntoQLHelper.FRENCH);
		p3.setRange(factoryDB.createEntityDatatype(EntityDatatype.INT_NAME));
		p3.setScope(c);

		p = factoryDB.createEntityProperty();
		p.setName("its_cags", OntoQLHelper.ENGLISH);
		p.setName("its_cags", OntoQLHelper.FRENCH);
		EntityDatatypeCategoryOntoDB dt = (EntityDatatypeCategoryOntoDB) factoryDB
				.createEntityDatatype(EntityDatatype.ASSOCIATION_NAME);
		dt.setCategory(cags);
		p.setRange(dt);
		p.setScope(c);
		c.setScopeProperties(new EntityProperty[] { p, p1, p3 });
		c.insert();
		stmt = s.createOntoQLStatement();
		c.createTable(new EntityProperty[] { p, p1, p3 });
		stmt.executeUpdate(
				"insert into Véhicule (number, its_cags, nbRoues) values ('5255TH16', (select c.oid from \"COUPES - VENT\" c where c.Taille like '6-8%'),4)");

		rs = stmt.executeQuery("select its_cags.Taille from Véhicule");
		rs.next();
		p.setCurrentContext(c);
		Assert.assertTrue(p.isDefined());
		Assert.assertTrue(p.isUsed());
		Assert.assertEquals("6-8 ans", rs.getString(1));
		t.rollback();

		s.close();

	}

	public void testGet() {
		Assert.assertEquals(muffs.getExternalId(), "71DC338877222-001");
		Assert.assertEquals(muffs.getName(), "MUFFS");

		Assert.assertEquals(hudson.getInternalId(), hudson.getInternalId());
		Assert.assertEquals(hudson.getName(), "HUDSON");

		Assert.assertEquals(anchorage.getInternalId(), anchorage.getInternalId());
		Assert.assertEquals(anchorage.getExternalId(), "71DC2FE39B1EA-001");

		Assert.assertEquals(cags.getInternalId(), cCags.getInternalId());
		Assert.assertEquals(cags.getExternalId(), "71DC2FDBFD904-001");
		s.close();
	}

	public void testGetDefinedProperties() {
		EntityProperty[] muffsDefinedProperties = muffs.getDefinedProperties();
		reference.setCurrentContext(muffs);
		itsSlalom.setCurrentContext(muffs);
		buoyancy.setCurrentContext(muffs);
		size.setCurrentContext(muffs);
		chest.setCurrentContext(muffs);
		buoyancyRaft.setCurrentContext(muffs);
		weightOfTheUser.setCurrentContext(muffs);
		Assert.assertEquals(muffsDefinedProperties.length, 8);
		Assert.assertTrue(ArrayHelper.contain(muffsDefinedProperties, reference));
		Assert.assertTrue(ArrayHelper.contain(muffsDefinedProperties, itsSlalom));
		Assert.assertTrue(ArrayHelper.contain(muffsDefinedProperties, buoyancy));
		Assert.assertTrue(ArrayHelper.contain(muffsDefinedProperties, size));
		Assert.assertTrue(ArrayHelper.contain(muffsDefinedProperties, chest));
		Assert.assertTrue(ArrayHelper.contain(muffsDefinedProperties, buoyancyRaft));
		Assert.assertTrue(ArrayHelper.contain(muffsDefinedProperties, weightOfTheUser));

		EntityProperty[] cagsDefinedProperties = cags.getDefinedProperties();
		Assert.assertEquals(cagsDefinedProperties.length, 7);
		reference.setCurrentContext(cags);
		buoyancy.setCurrentContext(cags);
		size.setCurrentContext(cags);
		chest.setCurrentContext(cags);
		buoyancyRaft.setCurrentContext(cags);
		weightOfTheUser.setCurrentContext(cags);
		Assert.assertTrue(ArrayHelper.contain(cagsDefinedProperties, reference));
		Assert.assertTrue(ArrayHelper.contain(cagsDefinedProperties, buoyancy));
		Assert.assertTrue(ArrayHelper.contain(cagsDefinedProperties, size));
		Assert.assertTrue(ArrayHelper.contain(cagsDefinedProperties, chest));
		Assert.assertTrue(ArrayHelper.contain(cagsDefinedProperties, buoyancyRaft));
		Assert.assertTrue(ArrayHelper.contain(cagsDefinedProperties, weightOfTheUser));

		EntityProperty[] anchorageDefinedProperties = anchorage.getDefinedProperties();
		Assert.assertEquals(anchorageDefinedProperties.length, 7);
		reference.setCurrentContext(anchorage);
		buoyancy.setCurrentContext(anchorage);
		size.setCurrentContext(anchorage);
		chest.setCurrentContext(anchorage);
		buoyancyRaft.setCurrentContext(anchorage);
		weightOfTheUser.setCurrentContext(anchorage);
		Assert.assertTrue(ArrayHelper.contain(anchorageDefinedProperties, reference));
		Assert.assertTrue(ArrayHelper.contain(anchorageDefinedProperties, buoyancy));
		Assert.assertTrue(ArrayHelper.contain(anchorageDefinedProperties, size));
		Assert.assertTrue(ArrayHelper.contain(anchorageDefinedProperties, chest));
		Assert.assertTrue(ArrayHelper.contain(anchorageDefinedProperties, buoyancyRaft));
		Assert.assertTrue(ArrayHelper.contain(anchorageDefinedProperties, weightOfTheUser));

		EntityProperty[] hudsonDefinedProperties = hudson.getDefinedProperties();
		Assert.assertEquals(hudsonDefinedProperties.length, 9);
		reference.setCurrentContext(hudson);
		itsSlalom.setCurrentContext(hudson);
		buoyancy.setCurrentContext(hudson);
		size.setCurrentContext(hudson);
		chest.setCurrentContext(hudson);
		buoyancyRaft.setCurrentContext(hudson);
		weightOfTheUser.setCurrentContext(hudson);
		virage.setCurrentContext(hudson);
		its_muff.setCurrentContext(hudson);
		Assert.assertTrue(ArrayHelper.contain(hudsonDefinedProperties, reference));
		Assert.assertTrue(ArrayHelper.contain(hudsonDefinedProperties, virage));
		Assert.assertTrue(ArrayHelper.contain(hudsonDefinedProperties, its_muff));
		Assert.assertTrue(ArrayHelper.contain(hudsonDefinedProperties, buoyancy));
		Assert.assertTrue(ArrayHelper.contain(hudsonDefinedProperties, size));
		Assert.assertTrue(ArrayHelper.contain(hudsonDefinedProperties, chest));
		Assert.assertTrue(ArrayHelper.contain(hudsonDefinedProperties, buoyancyRaft));
		Assert.assertTrue(ArrayHelper.contain(hudsonDefinedProperties, weightOfTheUser));

		s.close();
	}

	public void testGetUsedProperties() {

		EntityClass e1099 = (EntityClass) factoryDB.createCategory("!1099");
		EntityProperty[] e1099UsedProperties = e1099.getUsedProperties();
		Assert.assertEquals(e1099UsedProperties.length, 0);

		EntityProperty[] muffsUsedProperties = muffs.getUsedProperties();
		Assert.assertEquals(muffsUsedProperties.length, 3);
		reference.setCurrentContext(muffs);
		itsSlalom.setCurrentContext(muffs);
		Assert.assertTrue(ArrayHelper.contain(muffsUsedProperties, reference));
		Assert.assertTrue(ArrayHelper.contain(muffsUsedProperties, itsSlalom));

		EntityProperty[] cagsUsedProperties = cags.getUsedProperties();
		Assert.assertEquals(cagsUsedProperties.length, 0);

		EntityProperty[] anchorageUsedProperties = anchorage.getUsedProperties();
		Assert.assertEquals(anchorageUsedProperties.length, 2);
		reference.setCurrentContext(anchorage);
		size.setCurrentContext(anchorage);
		Assert.assertTrue(ArrayHelper.contain(anchorageUsedProperties, reference));
		Assert.assertTrue(ArrayHelper.contain(anchorageUsedProperties, size));

		EntityProperty[] hudsonUsedProperties = hudson.getUsedProperties();
		Assert.assertEquals(hudsonUsedProperties.length, 5);
		reference.setCurrentContext(hudson);
		size.setCurrentContext(hudson);
		its_muff.setCurrentContext(hudson);
		virage.setCurrentContext(hudson);
		Assert.assertTrue(ArrayHelper.contain(hudsonUsedProperties, reference));
		Assert.assertTrue(ArrayHelper.contain(hudsonUsedProperties, size));
		Assert.assertTrue(ArrayHelper.contain(hudsonUsedProperties, its_muff));
		Assert.assertTrue(ArrayHelper.contain(hudsonUsedProperties, virage));

		EntityProperty[] focBabyUsedProperties = cFocBaby.getUsedProperties();
		Assert.assertEquals(hudsonUsedProperties.length, 5);
		pNames.setCurrentContext(cFocBaby);
		Assert.assertTrue(ArrayHelper.contain(focBabyUsedProperties, pNames));

		s.close();
	}

	public void testGetUsedPropertiesPolymorph() {
		EntityProperty[] muffsUsedPropertiesPolymorph = muffs.getUsedPropertiesPolymorph();
		// Assert.assertEquals(muffsUsedPropertiesPolymorph.length, 3);
		reference.setCurrentContext(muffs);
		itsSlalom.setCurrentContext(muffs);
		size.setCurrentContext(muffs);
		Assert.assertTrue(ArrayHelper.contain(muffsUsedPropertiesPolymorph, reference));
		Assert.assertTrue(ArrayHelper.contain(muffsUsedPropertiesPolymorph, itsSlalom));
		Assert.assertTrue(ArrayHelper.contain(muffsUsedPropertiesPolymorph, size));

		EntityProperty[] cagsUsedPropertiesPolymorph = cags.getUsedPropertiesPolymorph();
		reference.setCurrentContext(cags);
		size.setCurrentContext(cags);
		Assert.assertEquals(cagsUsedPropertiesPolymorph.length, 3);
		Assert.assertTrue(ArrayHelper.contain(cagsUsedPropertiesPolymorph, reference));
		Assert.assertTrue(ArrayHelper.contain(cagsUsedPropertiesPolymorph, size));

		EntityProperty[] anchorageUsedPropertiesPolymorph = anchorage.getUsedPropertiesPolymorph();
		reference.setCurrentContext(anchorage);
		size.setCurrentContext(anchorage);
		Assert.assertEquals(anchorageUsedPropertiesPolymorph.length, 2);
		Assert.assertTrue(ArrayHelper.contain(anchorageUsedPropertiesPolymorph, reference));
		Assert.assertTrue(ArrayHelper.contain(anchorageUsedPropertiesPolymorph, size));

		EntityProperty[] hudsonUsedPropertiesPolymorph = hudson.getUsedPropertiesPolymorph();
		Assert.assertEquals(hudsonUsedPropertiesPolymorph.length, 5);
		reference.setCurrentContext(hudson);
		size.setCurrentContext(hudson);
		its_muff.setCurrentContext(hudson);
		virage.setCurrentContext(hudson);
		Assert.assertTrue(ArrayHelper.contain(hudsonUsedPropertiesPolymorph, reference));
		Assert.assertTrue(ArrayHelper.contain(hudsonUsedPropertiesPolymorph, size));
		Assert.assertTrue(ArrayHelper.contain(hudsonUsedPropertiesPolymorph, its_muff));
		Assert.assertTrue(ArrayHelper.contain(hudsonUsedPropertiesPolymorph, virage));

		s.close();
	}

	public void testGetSubclasses() {

		// only the direct subclasses
		EntityClass personnalEquipement = (EntityClass) factoryDB.createCategory("\"PERSONAL EQUIPEMENT/SAFETY\"");
		EntityClass[] personnalEquipementSubclasses = personnalEquipement.getDirectSubclasses();
		Assert.assertEquals(personnalEquipementSubclasses.length, 6);

		EntityClass[] muffsSubclasses = muffs.getDirectSubclasses();
		Assert.assertEquals(muffsSubclasses.length, 3);
		Assert.assertTrue(ArrayHelper.contain(muffsSubclasses, standardNeoprene));
		Assert.assertTrue(ArrayHelper.contain(muffsSubclasses, neopreneMuffs));
		Assert.assertTrue(ArrayHelper.contain(muffsSubclasses, neopreneMuffsForChildren));

		EntityClass[] cagsSubclasses = cags.getDirectSubclasses();
		Assert.assertEquals(cagsSubclasses.length, 6);
		Assert.assertTrue(ArrayHelper.contain(cagsSubclasses, anchorage));
		Assert.assertTrue(ArrayHelper.contain(cagsSubclasses, fjord));
		Assert.assertTrue(ArrayHelper.contain(cagsSubclasses, hudson));
		Assert.assertTrue(ArrayHelper.contain(cagsSubclasses, ontario));
		Assert.assertTrue(ArrayHelper.contain(cagsSubclasses, randonnee));
		Assert.assertTrue(ArrayHelper.contain(cagsSubclasses, standard));

		EntityClass[] anchorageSubclasses = anchorage.getDirectSubclasses();
		Assert.assertEquals(anchorageSubclasses.length, 0);

		EntityClass[] hudsonSubclasses = hudson.getDirectSubclasses();
		Assert.assertEquals(hudsonSubclasses.length, 1);

		s.close();
	}

	public void testToSQL() {
		Assert.assertEquals("(select 0 where false)", cags.toSQL());
		Assert.assertEquals(hudson.toSQL(), "e" + cHudson.getInternalId());

		cags.setPolymorph(true);
		Assert.assertEquals(
				"(select e1067.rid as rid, 'e1067' as tablename, e1067.p1204 as p1204, e1067.p1202 as p1202, NULL::varchar  as p20476 from e1067 union all select e1023.rid as rid, 'e1023' as tablename, e1023.p1204 as p1204, e1023.p1202 as p1202, NULL::varchar  as p20476 from e1023 union all select e1062.rid as rid, 'e1062' as tablename, e1062.p1204 as p1204, e1062.p1202 as p1202, e1062.p20476 as p20476 from e1062 union all select e1063.rid as rid, 'e1063' as tablename, e1063.p1204 as p1204, e1063.p1202 as p1202, NULL::varchar  as p20476 from e1063 union all select e1061.rid as rid, 'e1061' as tablename, e1061.p1204 as p1204, e1061.p1202 as p1202, NULL::varchar  as p20476 from e1061 union all select e1060.rid as rid, 'e1060' as tablename, e1060.p1204 as p1204, e1060.p1202 as p1202, NULL::varchar  as p20476 from e1060)",
				cags.toSQL());

		hudson.setPolymorph(true);
		Assert.assertEquals(
				"(select e1062.rid as rid, 'e1062' as tablename, e1062.p1204 as p1204, e1062.p1202 as p1202, e1062.p6216_rid as p6216_rid, e1062.p6216_tablename as p6216_tablename, e1062.p6237 as p6237, e1062.p20476 as p20476 from e1062)",
				hudson.toSQL());

		muffs.setPolymorph(true);
		Assert.assertEquals(muffs.getDirectSubclasses().length, 3);

		Assert.assertEquals(
				"(select e1105.rid as rid, 'e1105' as tablename, e1105.p1204 as p1204, e1105.p6226_rid as p6226_rid, e1105.p6226_tablename as p6226_tablename, e1105.p20476 as p20476, NULL::varchar  as p1202 from e1105 union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102)",
				muffs.toSQL());

		Assert.assertEquals(
				"(select e1037.rid as rid, 'e1037' as tablename, e1037.p1204 as p1204, e1037.p1205 as p1205, NULL  as NULL , NULL  as NULL , e1037.p1209 as p1209, e1037.p1202 as p1202, NULL::varchar  as p20476 from e1037 union all select e1036.rid as rid, 'e1036' as tablename, e1036.p1204 as p1204, e1036.p1205 as p1205, NULL  as NULL , NULL  as NULL , e1036.p1209 as p1209, e1036.p1202 as p1202, NULL::varchar  as p20476 from e1036 union all select e1034.rid as rid, 'e1034' as tablename, e1034.p1204 as p1204, e1034.p1205 as p1205, NULL  as NULL , NULL  as NULL , e1034.p1209 as p1209, e1034.p1202 as p1202, NULL::varchar  as p20476 from e1034 union all select e1033.rid as rid, 'e1033' as tablename, e1033.p1204 as p1204, e1033.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1033.p1202 as p1202, NULL::varchar  as p20476 from e1033 union all select e1031.rid as rid, 'e1031' as tablename, e1031.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1031.p1202 as p1202, e1031.p20476 as p20476 from e1031 union all select e1035.rid as rid, 'e1035' as tablename, e1035.p1204 as p1204, e1035.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1035.p1202 as p1202, NULL::varchar  as p20476 from e1035 union all select e1028.rid as rid, 'e1028' as tablename, e1028.p1204 as p1204, e1028.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1028.p1202 as p1202, NULL::varchar  as p20476 from e1028 union all select e1027.rid as rid, 'e1027' as tablename, e1027.p1204 as p1204, e1027.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1027.p1202 as p1202, NULL::varchar  as p20476 from e1027 union all select e1018.rid as rid, 'e1018' as tablename, e1018.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1018.p1202 as p1202, NULL::varchar  as p20476 from e1018 union all select e1026.rid as rid, 'e1026' as tablename, e1026.p1204 as p1204, e1026.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1026.p1202 as p1202, NULL::varchar  as p20476 from e1026 union all select e1024.rid as rid, 'e1024' as tablename, e1024.p1204 as p1204, e1024.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1024.p1202 as p1202, NULL::varchar  as p20476 from e1024 union all select e1025.rid as rid, 'e1025' as tablename, e1025.p1204 as p1204, e1025.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1025.p1202 as p1202, NULL::varchar  as p20476 from e1025 union all select e1022.rid as rid, 'e1022' as tablename, e1022.p1204 as p1204, e1022.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1022.p1202 as p1202, NULL::varchar  as p20476 from e1022 union all select e1077.rid as rid, 'e1077' as tablename, e1077.p1204 as p1204, e1077.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1077.p1202 as p1202, NULL::varchar  as p20476 from e1077 union all select e1020.rid as rid, 'e1020' as tablename, e1020.p1204 as p1204, e1020.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1020.p1202 as p1202, NULL::varchar  as p20476 from e1020 union all select e1075.rid as rid, 'e1075' as tablename, e1075.p1204 as p1204, e1075.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1075.p1202 as p1202, NULL::varchar  as p20476 from e1075 union all select e1021.rid as rid, 'e1021' as tablename, e1021.p1204 as p1204, e1021.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1021.p1202 as p1202, NULL::varchar  as p20476 from e1021 union all select e1076.rid as rid, 'e1076' as tablename, e1076.p1204 as p1204, e1076.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1076.p1202 as p1202, NULL::varchar  as p20476 from e1076 union all select e1073.rid as rid, 'e1073' as tablename, e1073.p1204 as p1204, e1073.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1073.p1202 as p1202, NULL::varchar  as p20476 from e1073 union all select e1070.rid as rid, 'e1070' as tablename, e1070.p1204 as p1204, e1070.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1070.p1202 as p1202, NULL::varchar  as p20476 from e1070 union all select e1074.rid as rid, 'e1074' as tablename, e1074.p1204 as p1204, e1074.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1074.p1202 as p1202, NULL::varchar  as p20476 from e1074 union all select e1071.rid as rid, 'e1071' as tablename, e1071.p1204 as p1204, e1071.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1071.p1202 as p1202, NULL::varchar  as p20476 from e1071 union all select e1072.rid as rid, 'e1072' as tablename, e1072.p1204 as p1204, e1072.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1072.p1202 as p1202, NULL::varchar  as p20476 from e1072 union all select e1069.rid as rid, 'e1069' as tablename, e1069.p1204 as p1204, e1069.p1205 as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1069.p1202 as p1202, NULL::varchar  as p20476 from e1069 union all select e1067.rid as rid, 'e1067' as tablename, e1067.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1067.p1202 as p1202, NULL::varchar  as p20476 from e1067 union all select e1023.rid as rid, 'e1023' as tablename, e1023.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1023.p1202 as p1202, NULL::varchar  as p20476 from e1023 union all select e1062.rid as rid, 'e1062' as tablename, e1062.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1062.p1202 as p1202, e1062.p20476 as p20476 from e1062 union all select e1063.rid as rid, 'e1063' as tablename, e1063.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1063.p1202 as p1202, NULL::varchar  as p20476 from e1063 union all select e1061.rid as rid, 'e1061' as tablename, e1061.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1061.p1202 as p1202, NULL::varchar  as p20476 from e1061 union all select e1060.rid as rid, 'e1060' as tablename, e1060.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1060.p1202 as p1202, NULL::varchar  as p20476 from e1060 union all select e1058.rid as rid, 'e1058' as tablename, e1058.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1058 union all select e1057.rid as rid, 'e1057' as tablename, e1057.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1057 union all select e1056.rid as rid, 'e1056' as tablename, e1056.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1056 union all select e1055.rid as rid, 'e1055' as tablename, e1055.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1055 union all select e1054.rid as rid, 'e1054' as tablename, e1054.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1054.p1202 as p1202, NULL::varchar  as p20476 from e1054 union all select e1053.rid as rid, 'e1053' as tablename, e1053.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1053.p1202 as p1202, NULL::varchar  as p20476 from e1053 union all select e1052.rid as rid, 'e1052' as tablename, e1052.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1052.p1202 as p1202, NULL::varchar  as p20476 from e1052 union all select e1051.rid as rid, 'e1051' as tablename, e1051.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1051.p1202 as p1202, NULL::varchar  as p20476 from e1051 union all select e1050.rid as rid, 'e1050' as tablename, e1050.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1050 union all select e1049.rid as rid, 'e1049' as tablename, e1049.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1049.p1202 as p1202, NULL::varchar  as p20476 from e1049 union all select e1048.rid as rid, 'e1048' as tablename, e1048.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1048.p1202 as p1202, NULL::varchar  as p20476 from e1048 union all select e1046.rid as rid, 'e1046' as tablename, e1046.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1046.p1202 as p1202, NULL::varchar  as p20476 from e1046 union all select e1045.rid as rid, 'e1045' as tablename, e1045.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1045.p1202 as p1202, NULL::varchar  as p20476 from e1045 union all select e1044.rid as rid, 'e1044' as tablename, e1044.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1044.p1202 as p1202, NULL::varchar  as p20476 from e1044 union all select e1043.rid as rid, 'e1043' as tablename, e1043.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1043.p1202 as p1202, NULL::varchar  as p20476 from e1043 union all select e1042.rid as rid, 'e1042' as tablename, e1042.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1042.p1202 as p1202, NULL::varchar  as p20476 from e1042 union all select e1041.rid as rid, 'e1041' as tablename, e1041.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1041.p1202 as p1202, NULL::varchar  as p20476 from e1041 union all select e1105.rid as rid, 'e1105' as tablename, e1105.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, e1105.p20476 as p20476 from e1105 union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1104.p1202 as p1202, e1104.p20476 as p20476 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1103.p1202 as p1202, e1103.p20476 as p20476 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1102.p1202 as p1202, e1102.p20476 as p20476 from e1102 union all select e1101.rid as rid, 'e1101' as tablename, e1101.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, e1101.p1202 as p1202, NULL::varchar  as p20476 from e1101 union all select e1092.rid as rid, 'e1092' as tablename, e1092.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1092 union all select e1096.rid as rid, 'e1096' as tablename, e1096.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1096 union all select e1095.rid as rid, 'e1095' as tablename, e1095.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1095 union all select e1065.rid as rid, 'e1065' as tablename, e1065.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1065 union all select e1064.rid as rid, 'e1064' as tablename, e1064.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1064 union all select e1097.rid as rid, 'e1097' as tablename, e1097.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1097 union all select e1094.rid as rid, 'e1094' as tablename, e1094.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1094 union all select e1093.rid as rid, 'e1093' as tablename, e1093.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1093 union all select e1091.rid as rid, 'e1091' as tablename, e1091.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1091 union all select e1090.rid as rid, 'e1090' as tablename, e1090.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1090 union all select e1089.rid as rid, 'e1089' as tablename, e1089.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1089 union all select e1087.rid as rid, 'e1087' as tablename, e1087.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1087 union all select e1086.rid as rid, 'e1086' as tablename, e1086.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1086 union all select e1084.rid as rid, 'e1084' as tablename, e1084.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1084 union all select e1085.rid as rid, 'e1085' as tablename, e1085.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1085 union all select e1083.rid as rid, 'e1083' as tablename, e1083.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1083 union all select e1082.rid as rid, 'e1082' as tablename, e1082.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1082 union all select e1081.rid as rid, 'e1081' as tablename, e1081.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1081 union all select e1080.rid as rid, 'e1080' as tablename, e1080.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1080 union all select e1079.rid as rid, 'e1079' as tablename, e1079.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1079 union all select e1078.rid as rid, 'e1078' as tablename, e1078.p1204 as p1204, NULL::float8  as p1205, NULL  as NULL , NULL  as NULL , NULL::float8  as p1209, NULL::varchar  as p1202, NULL::varchar  as p20476 from e1078)",
				personalequipment.toSQL(true));

		s.close();
	}

	public void testProject() {
		log.warn("testProject");

		Assert.assertEquals(
				"select e1067.p1204 as p1204, 'e1067' as tablename, e1067.p1202 as p1202, NULL::varchar  as p20476 from e1067 union all select e1023.p1204 as p1204, 'e1023' as tablename, e1023.p1202 as p1202, NULL::varchar  as p20476 from e1023 union all select e1062.p1204 as p1204, 'e1062' as tablename, e1062.p1202 as p1202, e1062.p20476 as p20476 from e1062 union all select e1063.p1204 as p1204, 'e1063' as tablename, e1063.p1202 as p1202, NULL::varchar  as p20476 from e1063 union all select e1061.p1204 as p1204, 'e1061' as tablename, e1061.p1202 as p1202, NULL::varchar  as p20476 from e1061 union all select e1060.p1204 as p1204, 'e1060' as tablename, e1060.p1202 as p1202, NULL::varchar  as p20476 from e1060",
				cags.project(cags.getUsedPropertiesPolymorph(), true));
		Assert.assertEquals("", cags.project(cags.getUsedPropertiesPolymorph(), false));

		EntityProperty[] properties = cags.getUsedPropertiesPolymorph();
		for (int i = 0; i < properties.length; i++) {
			properties[i].setCurrentContext(hudson);
		}
		Assert.assertEquals(
				"select e1062.p1204 as p1204, 'e1062' as tablename, e1062.p1202 as p1202, e1062.p20476 as p20476 from e1062",
				hudson.project(properties, false));

		s.close();
	}

	public void testEquals() {

		Assert.assertTrue(!muffs.equals(null));

		EntityClass compareMuffs = (EntityClass) factoryDB.createCategory("!" + cMuffs.getInternalId());
		Assert.assertEquals(muffs, compareMuffs);

		EntityClass compareCags = (EntityClass) factoryDB.createCategory("!" + cCags.getInternalId());
		Assert.assertEquals(cags, compareCags);

		EntityClass compareAnchorage = (EntityClass) factoryDB.createCategory("!" + cAnchorage.getInternalId());
		Assert.assertEquals(anchorage, compareAnchorage);

		EntityClass compareHudson = (EntityClass) factoryDB.createCategory("!" + cHudson.getInternalId());
		Assert.assertEquals(hudson, compareHudson);
		s.close();
	}

	@After
	public void tearDown() throws Exception {
		sFixation.close();
	}
}
