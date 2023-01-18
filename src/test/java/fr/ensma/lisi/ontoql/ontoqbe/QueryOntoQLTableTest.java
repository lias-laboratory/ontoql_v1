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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.core.AbstractEntityClass;
import fr.ensma.lisi.ontoql.core.AbstractEntityProperty;
import fr.ensma.lisi.ontoql.core.AbstractFactoryEntityDB;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.core.ontodb.FactoryEntityOntoDB;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Mickael BARON
 */
public class QueryOntoQLTableTest extends OntoQLTestCase {

	private static final Log log = LogFactory.getLog(QueryOntoQLTableTest.class);

	public AbstractFactoryEntityDB factoryDB;

	public AbstractEntityClass cags, hudson, personnalEquipement;

	public AbstractEntityProperty size, reference, pathReference, its_muff;

	protected QueryOntoQLTable ontoqlQuery;

	protected Condition cdt11, cdt12, cdt21, cdt31, cdt41, cdt51;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		getSession().setReferenceLanguage(OntoQLHelper.ENGLISH);
		factoryDB = new FactoryEntityOntoDB(getSession());
		cags = (AbstractEntityClass) factoryDB.createCategory("CAGS");
		hudson = (AbstractEntityClass) factoryDB.createCategory("HUDSON");
		personnalEquipement = (AbstractEntityClass) factoryDB.createCategory("PERSONAL EQUIPEMENT/SAFETY");

		size = (AbstractEntityProperty) factoryDB.createDescription("Size");
		reference = (AbstractEntityProperty) factoryDB.createDescription("Reference");
		pathReference = (AbstractEntityProperty) factoryDB.createDescription("Reference");
		its_muff = (AbstractEntityProperty) factoryDB.createDescription("its_muff");
	}

	@Test
	public void testEquals() {

		QueryOntoQLTable firstOntoqlQuery = new QueryOntoQLTable();
		firstOntoqlQuery.setPolymorph(true);
		firstOntoqlQuery.setRange(new AbstractEntityClass[] { cags });

		QueryOntoQLTable secondOntoqlQuery = new QueryOntoQLTable();
		secondOntoqlQuery.setPolymorph(true);
		secondOntoqlQuery.setRange(new AbstractEntityClass[] { cags });
		Assert.assertTrue(firstOntoqlQuery.equals(secondOntoqlQuery));

		getSession().close();
	}

	@Test
	public void testMerge() {

		QueryOntoQLTable firstOntoqlQuery = new QueryOntoQLTable();
		pItsSlalom.setCurrentContext(cMuffs);
		firstOntoqlQuery = new QueryOntoQLTable();
		firstOntoqlQuery.setTarget(new EntityProperty[] { pItsSlalom });
		firstOntoqlQuery.setRange(new AbstractEntityClass[] { cMuffs });
		firstOntoqlQuery.setPolymorph(false);

		QueryOntoQLTable secondOntoqlQuery = new QueryOntoQLTable();
		pReference.setCurrentContext(cAnchorage);
		pSize.setCurrentContext(cAnchorage);
		secondOntoqlQuery.setTarget(new EntityProperty[] { pReference, pSize });
		secondOntoqlQuery.setRange(new AbstractEntityClass[] { cAnchorage });
		secondOntoqlQuery.setPolymorph(true);

		QueryOntoQLTable expectedOntoqlQuery = new QueryOntoQLTable();
		expectedOntoqlQuery = new QueryOntoQLTable();
		expectedOntoqlQuery.setTarget(new EntityProperty[] { pItsSlalom, pReference, pSize });
		expectedOntoqlQuery.setRange(new AbstractEntityClass[] { cMuffs, cAnchorage });
		expectedOntoqlQuery.setPolymorph(new boolean[] { false, true });

		Assert.assertEquals(firstOntoqlQuery.merge(secondOntoqlQuery), expectedOntoqlQuery);

		// Where Test
		firstOntoqlQuery = new QueryOntoQLTable();
		cdt11 = new Condition(firstOntoqlQuery);
		ConditionElement cdtElt1 = new ConditionElement(cdt11, "LIKE", "re%");
		cdtElt1.setOnProperty(pReference);
		ConditionElement cdtElt2 = new ConditionElement(cdt11, "LIKE", "%ref%");
		cdtElt2.setOnProperty(pReference);
		cdt11.setConditions(new ConditionElement[] { cdtElt1, cdtElt2 });
		cdt12 = new Condition(firstOntoqlQuery);
		ConditionElement cdtElt3 = new ConditionElement(cdt12, ">", "10");
		cdtElt3.setOnProperty(pSize);
		cdt12.setConditions(new ConditionElement[] { cdtElt3 });

		pReference.setCurrentContext(cHudson);
		pSize.setCurrentContext(cHudson);
		firstOntoqlQuery.setTarget(new EntityProperty[] { pReference, pSize });
		firstOntoqlQuery.setRange(new AbstractEntityClass[] { cHudson });
		firstOntoqlQuery.setDisjonctiveConditions(new Condition[] { cdt11, cdt12 });
		firstOntoqlQuery.setPolymorph(false);

		secondOntoqlQuery = new QueryOntoQLTable();
		Condition cdt21 = new Condition(secondOntoqlQuery);
		ConditionElement cdtElt21 = new ConditionElement(cdt21, "=", "423");
		cdtElt21.setOnProperty(pItsSlalom);
		cdt21.setConditions(new ConditionElement[] { cdtElt21 });

		pItsSlalom.setCurrentContext(cMuffs);
		secondOntoqlQuery = new QueryOntoQLTable();
		secondOntoqlQuery.setTarget(new EntityProperty[] { pItsSlalom });
		secondOntoqlQuery.setRange(new AbstractEntityClass[] { cMuffs });
		secondOntoqlQuery.setPolymorph(false);
		secondOntoqlQuery.setDisjonctiveConditions(new Condition[] { cdt21 });

		expectedOntoqlQuery = new QueryOntoQLTable();
		expectedOntoqlQuery.setTarget(new EntityProperty[] { pReference, pSize, pItsSlalom });
		expectedOntoqlQuery.setRange(new AbstractEntityClass[] { cHudson, cMuffs });
		expectedOntoqlQuery.setPolymorph(new boolean[] { false, false });
		Condition cdt1Expected = new Condition(expectedOntoqlQuery);
		cdtElt1.setCondition(cdt1Expected);
		cdtElt2.setCondition(cdt1Expected);
		cdtElt21.setCondition(cdt1Expected);
		cdt1Expected.setConditions(new ConditionElement[] { cdtElt1, cdtElt2, cdtElt21 });
		Condition cdt2Expected = new Condition(expectedOntoqlQuery);
		cdtElt3.setCondition(cdt2Expected);
		cdt2Expected.setConditions(new ConditionElement[] { cdtElt3 });
		expectedOntoqlQuery.setDisjonctiveConditions(new Condition[] { cdt1Expected, cdt2Expected });
		Assert.assertEquals(firstOntoqlQuery.merge(secondOntoqlQuery), expectedOntoqlQuery);

		// Order by test
		firstOntoqlQuery = new QueryOntoQLTable();
		pItsSlalom.setCurrentContext(cMuffs);
		firstOntoqlQuery = new QueryOntoQLTable();
		firstOntoqlQuery.setTarget(new EntityProperty[] { pItsSlalom });
		firstOntoqlQuery.setRange(new AbstractEntityClass[] { cMuffs });
		firstOntoqlQuery.setPolymorph(false);
		firstOntoqlQuery.setOrderBy(new EntityProperty[] { pItsSlalom });
		firstOntoqlQuery.setOrderByType(QueryOntoQLTable.ORDER_BY_ASC);

		secondOntoqlQuery = new QueryOntoQLTable();
		pReference.setCurrentContext(cAnchorage);
		pSize.setCurrentContext(cAnchorage);
		secondOntoqlQuery.setTarget(new EntityProperty[] { pReference, pSize });
		secondOntoqlQuery.setRange(new AbstractEntityClass[] { cAnchorage });
		secondOntoqlQuery.setPolymorph(true);
		secondOntoqlQuery.setOrderBy(new EntityProperty[] { pReference });
		secondOntoqlQuery.setOrderByType(QueryOntoQLTable.ORDER_BY_ASC);

		expectedOntoqlQuery = new QueryOntoQLTable();
		expectedOntoqlQuery.setTarget(new EntityProperty[] { pItsSlalom, pReference, pSize });
		expectedOntoqlQuery.setRange(new AbstractEntityClass[] { cMuffs, cAnchorage });
		expectedOntoqlQuery.setPolymorph(new boolean[] { false, true });
		expectedOntoqlQuery.setOrderBy(new EntityProperty[] { pItsSlalom, pReference });
		expectedOntoqlQuery.setOrderByType(QueryOntoQLTable.ORDER_BY_ASC);

		Assert.assertEquals(firstOntoqlQuery.merge(secondOntoqlQuery), expectedOntoqlQuery);

		// Test generated prefix in the where clause
		firstOntoqlQuery = new QueryOntoQLTable();
		cdt11 = new Condition(firstOntoqlQuery);
		cdtElt1 = new ConditionElement(cdt11, "=", "XL");
		cdtElt1.setOnProperty(pSize);
		cdt11.setConditions(new ConditionElement[] { cdtElt1 });
		firstOntoqlQuery.setDisjonctiveConditions(new Condition[] { cdt11 });
		pSize.setCurrentContext(cHudson);
		pReference.setCurrentContext(cHudson);
		firstOntoqlQuery.setTarget(new EntityProperty[] { pReference, pSize });
		firstOntoqlQuery.setRange(new AbstractEntityClass[] { cHudson });
		firstOntoqlQuery.setPolymorph(true);

		secondOntoqlQuery = new QueryOntoQLTable();
		reference.setCurrentContext(hudson);
		size.setCurrentContext(hudson);
		secondOntoqlQuery.setTarget(new EntityProperty[] { reference, size });
		secondOntoqlQuery.setRange(new AbstractEntityClass[] { hudson });
		secondOntoqlQuery.setPolymorph(true);

		expectedOntoqlQuery = new QueryOntoQLTable();
		cdt11 = new Condition(expectedOntoqlQuery);
		cdtElt1 = new ConditionElement(cdt11, "=", "XL");
		cdtElt1.setOnProperty(pSize);
		cdt11.setConditions(new ConditionElement[] { cdtElt1 });
		expectedOntoqlQuery.setDisjonctiveConditions(new Condition[] { cdt11 });
		pReference.setCurrentContext(cHudson);
		pSize.setCurrentContext(cHudson);
		reference.setCurrentContext(hudson);
		size.setCurrentContext(hudson);
		expectedOntoqlQuery.setTarget(new EntityProperty[] { pReference, pSize, reference, size });
		expectedOntoqlQuery.setRange(new AbstractEntityClass[] { cHudson, hudson });
		expectedOntoqlQuery.setPolymorph(new boolean[] { true, true });
		log.warn("--- test -----");
		Assert.assertEquals(firstOntoqlQuery.merge(secondOntoqlQuery), expectedOntoqlQuery);
		Assert.assertEquals(firstOntoqlQuery.merge(secondOntoqlQuery).toString(), expectedOntoqlQuery.toString());

		// Test impicit and explicit join

		its_muff.setCurrentContext(hudson);
		pathReference.setPathProperty(its_muff);

		firstOntoqlQuery = new QueryOntoQLTable();
		firstOntoqlQuery.setPolymorph(true);
		firstOntoqlQuery.setTarget(new EntityProperty[] { pathReference });
		firstOntoqlQuery.setRange(hudson);
		firstOntoqlQuery.setOrderBy(new EntityProperty[0]);
		log.warn(firstOntoqlQuery);

		secondOntoqlQuery = new QueryOntoQLTable();
		pReference.setCurrentContext(cAnchorage);
		secondOntoqlQuery.setTarget(new EntityProperty[] { pReference });
		secondOntoqlQuery.setRange(new AbstractEntityClass[] { cAnchorage });
		secondOntoqlQuery.setPolymorph(true);
		log.warn(secondOntoqlQuery);

		expectedOntoqlQuery = new QueryOntoQLTable();
		expectedOntoqlQuery.setPolymorph(new boolean[] { true, true });
		expectedOntoqlQuery.setTarget(new EntityProperty[] { pathReference, pReference });
		expectedOntoqlQuery.setRange(new AbstractEntityClass[] { hudson, cAnchorage });
		expectedOntoqlQuery.setOrderBy(new AbstractEntityProperty[0]);

		Assert.assertEquals(expectedOntoqlQuery, firstOntoqlQuery.merge(secondOntoqlQuery));

		// Test natural join
		firstOntoqlQuery = new QueryOntoQLTable();
		cdt11 = new Condition(firstOntoqlQuery);
		cdtElt1 = new ConditionElement(cdt11, "=", "MUFFS.Reference");
		cdtElt1.setOnProperty(pReference);
		cdt11.setConditions(new ConditionElement[] { cdtElt1 });

		pReference.setCurrentContext(cHudson);
		firstOntoqlQuery.setTarget(new EntityProperty[] { pReference });
		firstOntoqlQuery.setRange(new AbstractEntityClass[] { cHudson });
		firstOntoqlQuery.setDisjonctiveConditions(new Condition[] { cdt11 });
		firstOntoqlQuery.setPolymorph(false);

		secondOntoqlQuery = new QueryOntoQLTable();
		reference.setCurrentContext(cMuffs);
		secondOntoqlQuery = new QueryOntoQLTable();
		secondOntoqlQuery.setTarget(new AbstractEntityProperty[] { reference });
		secondOntoqlQuery.setRange(new AbstractEntityClass[] { cMuffs });
		secondOntoqlQuery.setPolymorph(false);
		secondOntoqlQuery.setDisjonctiveConditions(new Condition[] {});

		expectedOntoqlQuery = new QueryOntoQLTable();
		expectedOntoqlQuery.setTarget(new EntityProperty[] { pReference });
		expectedOntoqlQuery.setRange(new AbstractEntityClass[] { cHudson, cMuffs });
		expectedOntoqlQuery.setPolymorph(new boolean[] { false, false });
		cdt1Expected = new Condition(expectedOntoqlQuery);
		cdtElt1.setCondition(cdt1Expected);
		cdt1Expected.setConditions(new ConditionElement[] { cdtElt1 });
		expectedOntoqlQuery.setDisjonctiveConditions(new Condition[] { cdt1Expected });
		Assert.assertEquals(expectedOntoqlQuery, firstOntoqlQuery.merge(secondOntoqlQuery));

		getSession().close();
	}

	@Test
	public void testToString() {
		getSession().setReferenceLanguage(OntoQLHelper.ENGLISH);

		ontoqlQuery = new QueryOntoQLTable();
		cdt11 = new Condition(ontoqlQuery);
		ConditionElement cdtElt1 = new ConditionElement(cdt11, "LIKE", "'re%'");
		cdtElt1.setOnProperty(pReference);
		ConditionElement cdtElt2 = new ConditionElement(cdt11, "LIKE", "'%ref%'");
		cdtElt2.setOnProperty(pReference);
		cdt11.setConditions(new ConditionElement[] { cdtElt1, cdtElt2 });
		cdt12 = new Condition(ontoqlQuery);
		ConditionElement cdtElt3 = new ConditionElement(cdt12, ">", "'10'");
		cdtElt3.setOnProperty(pSize);
		cdt12.setConditions(new ConditionElement[] { cdtElt3 });

		ontoqlQuery.setTarget(new EntityProperty[] { pReference, pSize });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cHudson });
		ontoqlQuery.setDisjonctiveConditions(new Condition[] { cdt11, cdt12 });
		ontoqlQuery.setPolymorph(false);

		String queryOntoqlExpected = "SELECT h_t:Reference, h_t:Size \n FROM only(h_t:HUDSON) \n WHERE (h_t:Reference LIKE 're%'  AND h_t:Reference LIKE '%ref%' ) \n OR (h_t:Size > '10' )\nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		ontoqlQuery = new QueryOntoQLTable();
		ontoqlQuery.setTarget(new EntityProperty[] { pItsSlalom });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cMuffs });
		ontoqlQuery.setPolymorph(false);

		queryOntoqlExpected = "SELECT h_t:its_slalom \n FROM only(h_t:MUFFS) \n \nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		ontoqlQuery = new QueryOntoQLTable();
		ontoqlQuery.setTarget(new EntityProperty[] { pItsSlalom, pSize, pReference, pVirage });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cHudson });
		ontoqlQuery.setPolymorph(false);
		ontoqlQuery.setOrderBy(new EntityProperty[] { pSize, pReference });
		ontoqlQuery.setOrderByType(QueryOntoQLTable.ORDER_BY_ASC);

		queryOntoqlExpected = "SELECT h_t:its_slalom, h_t:Size, h_t:Reference, h_t:virage \n FROM only(h_t:HUDSON) \n  ORDER BY h_t:Size, h_t:Reference ASC\nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		ontoqlQuery = new QueryOntoQLTable();
		ontoqlQuery.setTarget(new EntityProperty[] { pReference, pSize });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cAnchorage });
		ontoqlQuery.setPolymorph(false);
		queryOntoqlExpected = "SELECT h_t:Reference, h_t:Size \n FROM only(h_t:\"ANCHORAGE (KAYAK PANTS)\") \n \nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		ontoqlQuery = new QueryOntoQLTable();
		pReference.setCurrentContext(cHudson);
		pSize.setCurrentContext(cOntario);
		ontoqlQuery.setTarget(new EntityProperty[] { pReference, pSize });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cHudson, cOntario });
		ontoqlQuery.setPolymorph(new boolean[] { false, true });
		queryOntoqlExpected = "SELECT h_t:HUDSON.h_t:Reference, h_t:ONTARIO.h_t:Size \n FROM only(h_t:HUDSON), h_t:ONTARIO \n \nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		ontoqlQuery = new QueryOntoQLTable();
		pReference.setCurrentContext(cHudson);
		reference.setCurrentContext(cOntario);
		ontoqlQuery.setTarget(new EntityProperty[] { pReference, reference });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cHudson, cOntario });
		ontoqlQuery.setPolymorph(new boolean[] { true, true });
		queryOntoqlExpected = "SELECT h_t:HUDSON.h_t:Reference, h_t:ONTARIO.h_t:Reference \n FROM h_t:HUDSON, h_t:ONTARIO \n \nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(queryOntoqlExpected, ontoqlQuery.toString());

		ontoqlQuery = new QueryOntoQLTable();
		pReference.setCurrentContext(cHudson);
		pSize.setCurrentContext(cOntario);
		ontoqlQuery.setTarget(new EntityProperty[] { pReference, pSize });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cHudson, cOntario });
		ontoqlQuery.setPolymorph(new boolean[] { false, true });
		ontoqlQuery.setOrderBy(new EntityProperty[] { pReference });
		queryOntoqlExpected = "SELECT h_t:HUDSON.h_t:Reference, h_t:ONTARIO.h_t:Size \n FROM only(h_t:HUDSON), h_t:ONTARIO \n  ORDER BY h_t:HUDSON.h_t:Reference ASC\nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		ontoqlQuery = new QueryOntoQLTable();
		pReference.setCurrentContext(cHudson);
		pSize.setCurrentContext(cHudson);
		reference.setCurrentContext(hudson);
		size.setCurrentContext(hudson);
		ontoqlQuery.setTarget(new EntityProperty[] { pReference, pSize, reference, size });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cHudson, hudson });
		ontoqlQuery.setPolymorph(new boolean[] { true, true });
		queryOntoqlExpected = "SELECT ghudson0_.h_t:Reference, ghudson0_.h_t:Size, ghudson1_.h_t:Reference, ghudson1_.h_t:Size \n FROM h_t:HUDSON as ghudson0_, h_t:HUDSON as ghudson1_ \n \nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		ontoqlQuery = new QueryOntoQLTable();
		pReference.setCurrentContext(cHudson);
		pSize.setCurrentContext(cHudson);
		reference.setCurrentContext(hudson);
		size.setCurrentContext(hudson);
		ontoqlQuery.setTarget(new EntityProperty[] { pReference, pSize, reference, size });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cHudson, hudson });
		ontoqlQuery.setPolymorph(new boolean[] { true, true });
		ontoqlQuery.setOrderBy(new EntityProperty[] { pSize, pReference });
		queryOntoqlExpected = "SELECT ghudson0_.h_t:Reference, ghudson0_.h_t:Size, ghudson1_.h_t:Reference, ghudson1_.h_t:Size \n FROM h_t:HUDSON as ghudson0_, h_t:HUDSON as ghudson1_ \n  ORDER BY ghudson0_.h_t:Size, ghudson0_.h_t:Reference ASC\nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		ontoqlQuery = new QueryOntoQLTable();
		cdt11 = new Condition(ontoqlQuery);
		cdtElt1 = new ConditionElement(cdt11, "LIKE", "'re%'");
		cdtElt1.setOnProperty(pReference);
		cdtElt2 = new ConditionElement(cdt11, "LIKE", "'%ref%'");
		cdtElt2.setOnProperty(pReference);
		cdt11.setConditions(new ConditionElement[] { cdtElt1, cdtElt2 });
		cdt12 = new Condition(ontoqlQuery);
		cdtElt3 = new ConditionElement(cdt12, ">", "'10'");
		cdtElt3.setOnProperty(size);
		cdt12.setConditions(new ConditionElement[] { cdtElt3 });

		ontoqlQuery.setTarget(new EntityProperty[] { pReference, pSize });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cHudson });
		ontoqlQuery.setDisjonctiveConditions(new Condition[] { cdt11, cdt12 });
		pReference.setCurrentContext(cHudson);
		pSize.setCurrentContext(cHudson);
		reference.setCurrentContext(hudson);
		size.setCurrentContext(hudson);
		ontoqlQuery.setTarget(new EntityProperty[] { pReference, pSize, reference, size });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cHudson, hudson });
		ontoqlQuery.setPolymorph(new boolean[] { true, true });
		ontoqlQuery.setOrderBy(new EntityProperty[] { pSize, pReference });
		queryOntoqlExpected = "SELECT ghudson0_.h_t:Reference, ghudson0_.h_t:Size, ghudson1_.h_t:Reference, ghudson1_.h_t:Size \n FROM h_t:HUDSON as ghudson0_, h_t:HUDSON as ghudson1_ \n WHERE (ghudson0_.h_t:Reference LIKE 're%'  AND ghudson0_.h_t:Reference LIKE '%ref%' ) \n OR (ghudson1_.h_t:Size > '10' ) ORDER BY ghudson0_.h_t:Size, ghudson0_.h_t:Reference ASC\nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		ontoqlQuery = new QueryOntoQLTable();
		cdt11 = new Condition(ontoqlQuery);
		cdtElt1 = new ConditionElement(cdt11, "LIKE", "'re%'");
		cdtElt1.setOnProperty(pReference);
		cdt11.setConditions(new ConditionElement[] { cdtElt1 });
		ontoqlQuery.setDisjonctiveConditions(new Condition[] { cdt11 });
		pReference.setCurrentContext(cHudson);
		pSize.setCurrentContext(cOntario);
		ontoqlQuery.setTarget(new EntityProperty[] { pReference, pSize });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cHudson, cOntario });
		ontoqlQuery.setPolymorph(new boolean[] { false, true });
		queryOntoqlExpected = "SELECT h_t:HUDSON.h_t:Reference, h_t:ONTARIO.h_t:Size \n FROM only(h_t:HUDSON), h_t:ONTARIO \n WHERE (h_t:HUDSON.h_t:Reference LIKE 're%' )\nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		ontoqlQuery = new QueryOntoQLTable();
		ontoqlQuery.setTarget(new EntityProperty[] { pBuoyancyRaft });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cPersonnalEquipementSafety });
		ontoqlQuery.setPolymorph(false);

		queryOntoqlExpected = "SELECT h_t:\"Buoyancy RAFT\" \n FROM only(h_t:\"PERSONAL EQUIPEMENT/SAFETY\") \n \nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		ontoqlQuery = new QueryOntoQLTable();
		cdt11 = new Condition(ontoqlQuery);
		cdtElt1 = new ConditionElement(cdt11, "=", "10");
		cdtElt1.setOnProperty(pBuoyancyRaft);
		cdt11.setConditions(new ConditionElement[] { cdtElt1 });
		ontoqlQuery.setDisjonctiveConditions(new Condition[] { cdt11 });
		ontoqlQuery.setTarget(new EntityProperty[] { pBuoyancyRaft });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cPersonnalEquipementSafety });
		ontoqlQuery.setPolymorph(false);
		queryOntoqlExpected = "SELECT h_t:\"Buoyancy RAFT\" \n FROM only(h_t:\"PERSONAL EQUIPEMENT/SAFETY\") \n WHERE (h_t:\"Buoyancy RAFT\" = 10 )\nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		pBuoyancyRaft.setCurrentContext(cPersonnalEquipementSafety);
		ontoqlQuery = new QueryOntoQLTable();
		ontoqlQuery.setTarget(new EntityProperty[] { pBuoyancyRaft });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cPersonnalEquipementSafety, cCags });
		ontoqlQuery.setPolymorph(new boolean[] { false, false });
		queryOntoqlExpected = "SELECT h_t:\"PERSONAL EQUIPEMENT/SAFETY\".h_t:\"Buoyancy RAFT\" \n FROM only(h_t:\"PERSONAL EQUIPEMENT/SAFETY\"), only(h_t:CAGS) \n \nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		pReference.setCurrentContext(cPersonnalEquipementSafety);
		pSize.setCurrentContext(cPersonnalEquipementSafety);
		reference.setCurrentContext(personnalEquipement);
		size.setCurrentContext(personnalEquipement);
		ontoqlQuery = new QueryOntoQLTable();
		ontoqlQuery.setTarget(new EntityProperty[] { pReference, pSize, reference, size });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cPersonnalEquipementSafety, personnalEquipement });
		ontoqlQuery.setPolymorph(new boolean[] { true, true });
		queryOntoqlExpected = "SELECT gpersonal_e0_.h_t:Reference, gpersonal_e0_.h_t:Size, gpersonal_e1_.h_t:Reference, gpersonal_e1_.h_t:Size \n FROM h_t:\"PERSONAL EQUIPEMENT/SAFETY\" as gpersonal_e0_, h_t:\"PERSONAL EQUIPEMENT/SAFETY\" as gpersonal_e1_ \n \nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		// Test the prefix in the Where clause
		// when an auto join is process
		ontoqlQuery = new QueryOntoQLTable();
		cdt11 = new Condition(ontoqlQuery);
		cdtElt1 = new ConditionElement(cdt11, "=", "'XL'");
		cdtElt1.setOnProperty(pSize);
		cdt11.setConditions(new ConditionElement[] { cdtElt1 });
		ontoqlQuery.setDisjonctiveConditions(new Condition[] { cdt11 });
		pReference.setCurrentContext(cHudson);
		pSize.setCurrentContext(cHudson);
		reference.setCurrentContext(hudson);
		size.setCurrentContext(hudson);
		ontoqlQuery.setTarget(new EntityProperty[] { pReference, pSize, reference, size });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cHudson, hudson });
		ontoqlQuery.setPolymorph(new boolean[] { true, true });
		queryOntoqlExpected = "SELECT ghudson0_.h_t:Reference, ghudson0_.h_t:Size, ghudson1_.h_t:Reference, ghudson1_.h_t:Size \n FROM h_t:HUDSON as ghudson0_, h_t:HUDSON as ghudson1_ \n WHERE (ghudson0_.h_t:Size = 'XL' )\nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		// Bug path property and join

		// 1. without join : it works
		// because no prefix is used
		its_muff.setCurrentContext(hudson);
		pathReference.setPathProperty(its_muff);
		ontoqlQuery = new QueryOntoQLTable();
		ontoqlQuery.setPolymorph(new boolean[] { true });
		ontoqlQuery.setTarget(new EntityProperty[] { pathReference });
		ontoqlQuery.setRange(new AbstractEntityClass[] { hudson });
		ontoqlQuery.setOrderBy(new EntityProperty[0]);
		queryOntoqlExpected = "SELECT h_t:its_muff.h_t:Reference \n FROM h_t:HUDSON \n \nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		its_muff.setCurrentContext(hudson);
		pathReference.setPathProperty(its_muff);
		pReference.setCurrentContext(cAnchorage);
		ontoqlQuery = new QueryOntoQLTable();
		ontoqlQuery.setPolymorph(new boolean[] { true, true });
		ontoqlQuery.setTarget(new EntityProperty[] { pathReference, pReference });
		ontoqlQuery.setRange(new AbstractEntityClass[] { hudson, cAnchorage });
		ontoqlQuery.setOrderBy(new EntityProperty[0]);
		queryOntoqlExpected = "SELECT h_t:HUDSON.h_t:its_muff.h_t:Reference, h_t:\"ANCHORAGE (KAYAK PANTS)\".h_t:Reference \n FROM h_t:HUDSON, h_t:\"ANCHORAGE (KAYAK PANTS)\" \n \nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		// Bug order by a property with a space in the name
		pBuoyancyRaft.setCurrentContext(personnalEquipement);
		ontoqlQuery = new QueryOntoQLTable();
		ontoqlQuery.setPolymorph(new boolean[] { true });
		ontoqlQuery.setTarget(new EntityProperty[] { pBuoyancyRaft });
		ontoqlQuery.setRange(new AbstractEntityClass[] { personnalEquipement });
		ontoqlQuery.setOrderBy(new EntityProperty[] { pBuoyancyRaft });
		queryOntoqlExpected = "SELECT h_t:\"Buoyancy RAFT\" \n FROM h_t:\"PERSONAL EQUIPEMENT/SAFETY\" \n  ORDER BY h_t:\"Buoyancy RAFT\" ASC\nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(ontoqlQuery.toString(), queryOntoqlExpected);

		// Bug Where containing prefix with space
		ontoqlQuery = new QueryOntoQLTable();
		pReference.setCurrentContext(cPersonnalEquipementSafety);
		cdt11 = new Condition(ontoqlQuery);
		cdtElt1 = new ConditionElement(cdt11, "=", "CAGS.Reference");
		cdtElt1.setOnProperty(pReference);
		cdt11.setConditions(new ConditionElement[] { cdtElt1 });
		ontoqlQuery.setDisjonctiveConditions(new Condition[] { cdt11 });
		ontoqlQuery.setTarget(new EntityProperty[] { pReference });
		ontoqlQuery.setRange(new AbstractEntityClass[] { cPersonnalEquipementSafety, cCags });
		ontoqlQuery.setPolymorph(new boolean[] { true, true });
		queryOntoqlExpected = "SELECT h_t:\"PERSONAL EQUIPEMENT/SAFETY\".h_t:Reference \n FROM h_t:\"PERSONAL EQUIPEMENT/SAFETY\", h_t:CAGS \n WHERE (h_t:\"PERSONAL EQUIPEMENT/SAFETY\".h_t:Reference = h_t:CAGS.h_t:Reference )\nUSING NAMESPACE h_t = 'http://lisi.ensma.fr/'";
		Assert.assertEquals(queryOntoqlExpected, ontoqlQuery.toString());

		getSession().close();
	}

	@Test
	public void testDoubleInRange() {
		QueryOntoQLTable ontoqlQuery = new QueryOntoQLTable();
		Assert.assertTrue(ontoqlQuery.doubleInRange(new EntityClass[] { cHudson, hudson }));

		ontoqlQuery = new QueryOntoQLTable();
		Assert.assertFalse(ontoqlQuery.doubleInRange(new EntityClass[] { cHudson, cOntario }));

		ontoqlQuery = new QueryOntoQLTable();
		Assert.assertTrue(ontoqlQuery.doubleInRange(new EntityClass[] { cHudson, cOntario, hudson }));
		getSession().close();
	}

	@Test
	public void testApplySyntax() {
		Assert.assertEquals(QueryOntoQLTable.applySyntax("CAGS"), "CAGS");
		Assert.assertEquals(QueryOntoQLTable.applySyntax("ANCH KAY"), "\"ANCH KAY\"");
		getSession().close();
	}
}