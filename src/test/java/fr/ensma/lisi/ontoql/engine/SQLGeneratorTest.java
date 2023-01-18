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
package fr.ensma.lisi.ontoql.engine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import antlr.collections.AST;
import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.util.ASTPrinter;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;

/**
 * @author Stéphane JEAN
 */
public class SQLGeneratorTest extends OntoQLTestCase {

	/**
	 * A logger for this class.
	 */
	private static final Log log = LogFactory.getLog(OntoQLWalkerTest.class);

	@Test
	public void testGeneratedSQLNameIdEnglish() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");

		ASTPrinter printer = new ASTPrinter(SQLTokenTypes.class);

		String toParse = "SELECT Size FROM only(HUDSON)";
		System.out.println("input: ." + toParse + "<-");
		OntoQLParser parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		AST ast = parser.getAST();

		OntoQLSQLWalker walker = new OntoQLSQLWalker(s);
		walker.statement(ast);

		SQLGenerator gen = new SQLGenerator(s);
		gen.statement(walker.getAST());

		Assert.assertEquals(gen.getSQL(),
				"select g1062x0_.p" + pSize.getInternalId() + " from e" + cHudson.getInternalId() + " g1062x0_");

		// 2éme tests : plusieurs propriétés
		toParse = "SELECT Size, Reference FROM only(HUDSON)";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		gen.statement(walker.getAST());
		Assert.assertEquals("select g1062x0_.p" + pSize.getInternalId() + ", g1062x0_.p" + pReference.getInternalId()
				+ " from e" + cHudson.getInternalId() + " g1062x0_", gen.getSQL());

		// La classe n'a pas d'extension
		toParse = "SELECT Reference FROM only(CAGS)";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		System.out.println("OntoQL  :  " + walker.getAST().toStringTree() + "");
		gen.statement(walker.getAST());

		Assert.assertEquals("select NULL::varchar  from (select 0 where false) g1068x0_", gen.getSQL());

		toParse = "SELECT * FROM only(CAGS)";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		gen.statement(walker.getAST());

		Assert.assertEquals(
				"select NULL , NULL::varchar , NULL::varchar , NULL::float8 , NULL , NULL , NULL::float8 , NULL::varchar  from (select 0 where false) g1068x0_",
				gen.getSQL());

		// requéte polymorphe
		toParse = "SELECT Reference FROM HELMETS";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		gen.statement(walker.getAST());
		Assert.assertEquals(
				"select g1066x0_.p1204 from (select e1065.rid as rid, 'e1065' as tablename, e1065.p1204 as p1204, NULL  as NULL , e1065.p1212 as p1212 from e1065 union all select e1064.rid as rid, 'e1064' as tablename, e1064.p1204 as p1204, NULL  as NULL , e1064.p1212 as p1212 from e1064 union all select e1097.rid as rid, 'e1097' as tablename, e1097.p1204 as p1204, NULL  as NULL , e1097.p1212 as p1212 from e1097) g1066x0_",
				gen.getSQL());

		// Filter condition
		toParse = "SELECT Reference FROM only(HUDSON) Where Reference like '3%'";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		Assert.assertEquals("select g1062x0_.p" + pReference.getInternalId() + " from e" + cHudson.getInternalId()
				+ " g1062x0_ where g1062x0_.p" + pReference.getInternalId() + " like '3%'", gen.getSQL());

		toParse = "SELECT count(Reference) FROM only(HUDSON) Where Reference like '3%' group by Reference having Size = 'Jaune'  order by Reference  asc";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		Assert.assertEquals(
				"select count(g1062x0_.p1204) from e1062 g1062x0_ where g1062x0_.p1204 like '3%' group by g1062x0_.p1204 having g1062x0_.p1202='Jaune' order by g1062x0_.p1204 asc",
				gen.getSQL());
		// Distinct
		toParse = "SELECT distinCT Reference FROM only(HUDSON) Where Reference like '3%'";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		Assert.assertEquals(gen.getSQL(), "select distinct g1062x0_.p" + pReference.getInternalId() + " from e"
				+ cHudson.getInternalId() + " g1062x0_ where g1062x0_.p" + pReference.getInternalId() + " like '3%'");

		// OR - AND condition
		toParse = "SELECT Reference FROM only(HUDSON) WHERE (Reference = 'TOTO') OR ( (Size = 'JAUNE') AND (Reference > '3') )";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		// AND has a higher priority on OR => no parenthesis
		Assert.assertEquals(gen.getSQL(),
				"select g1062x0_.p" + pReference.getInternalId() + " from e" + cHudson.getInternalId()
						+ " g1062x0_ where g1062x0_.p" + pReference.getInternalId() + "='TOTO' or g1062x0_.p"
						+ pSize.getInternalId() + "='JAUNE' and g1062x0_.p" + pReference.getInternalId() + ">'3'");

		toParse = "SELECT Reference FROM only(HUDSON) WHERE (Reference = 'TOTO' OR Size = 'JAUNE') AND (Reference > '3') ";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		// AND has a higher priority on OR => ()
		Assert.assertEquals(gen.getSQL(),
				"select g1062x0_.p" + pReference.getInternalId() + " from e" + cHudson.getInternalId()
						+ " g1062x0_ where (g1062x0_.p" + pReference.getInternalId() + "='TOTO' or g1062x0_.p"
						+ pSize.getInternalId() + "='JAUNE') and g1062x0_.p" + pReference.getInternalId() + ">'3'");

		// Jointure
		toParse = "select h.Reference,h.Size from only(HUDSON) h, only(DURANCE) d where h.Size = 'XL' and h.Reference = d.Reference";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		// AND has a higher priority on OR => ()
		Assert.assertEquals(gen.getSQL(),
				"select g1062x0_.p1204, g1062x0_.p1202 from e1062 g1062x0_, e1064 g1064x1_ where g1062x0_.p1202='XL' and g1062x0_.p1204=g1064x1_.p1204");

		// Path expression
		toParse = "SELECT h.its_muff.Reference from only(HUDSON) h";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println(printer.showAsString(ast, "OntoQL  :  "));
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		Assert.assertEquals(
				"select g1105x1_.p1204 from e1062 g1062x0_ left outer join (select g1105x1_.rid as rid, 'e1105' as tablename, g1105x1_.p1204 as p1204, g1105x1_.p6226_rid as p6226_rid, g1105x1_.p6226_tablename as p6226_tablename, g1105x1_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x1_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x1_ ON g1062x0_.p6216_rid = g1105x1_.rid",
				gen.getSQL());

		toParse = "SELECT its_muff.its_slalom.Reference from only(HUDSON)";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println(printer.showAsString(ast, "OntoQL  :  "));
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		Assert.assertEquals(
				"select g1097x2_.p1204 from e1062 g1062x0_ left outer join (select g1105x1_.rid as rid, 'e1105' as tablename, g1105x1_.p1204 as p1204, g1105x1_.p6226_rid as p6226_rid, g1105x1_.p6226_tablename as p6226_tablename, g1105x1_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x1_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x1_ left outer join (select g1097x2_.rid as rid, 'e1097' as tablename, g1097x2_.p1204 as p1204, NULL  as NULL , g1097x2_.p1212 as p1212 from e1097 g1097x2_) g1097x2_ ON g1105x1_.p6226_rid = g1097x2_.rid ON g1062x0_.p6216_rid = g1105x1_.rid",
				gen.getSQL());

		toParse = "SELECT Reference from only(HUDSON) where its_muff.its_slalom.Reference like '%3%'";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println(printer.showAsString(ast, "OntoQL  :  "));
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		Assert.assertEquals(
				"select g1062x0_.p1204 from e1062 g1062x0_ left outer join (select g1105x1_.rid as rid, 'e1105' as tablename, g1105x1_.p1204 as p1204, g1105x1_.p6226_rid as p6226_rid, g1105x1_.p6226_tablename as p6226_tablename, g1105x1_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x1_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x1_ left outer join (select g1097x2_.rid as rid, 'e1097' as tablename, g1097x2_.p1204 as p1204, NULL  as NULL , g1097x2_.p1212 as p1212 from e1097 g1097x2_) g1097x2_ ON g1105x1_.p6226_rid = g1097x2_.rid ON g1062x0_.p6216_rid = g1105x1_.rid where g1097x2_.p1204 like '%3%'",
				gen.getSQL());

		toParse = "SELECT its_muff.its_slalom.Reference from only(HUDSON) where its_muff.its_slalom.Reference like '%3%'";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println(printer.showAsString(ast, "OntoQL  :  "));
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		Assert.assertEquals(
				"select g1097x2_.p1204 from e1062 g1062x0_ left outer join (select g1105x1_.rid as rid, 'e1105' as tablename, g1105x1_.p1204 as p1204, g1105x1_.p6226_rid as p6226_rid, g1105x1_.p6226_tablename as p6226_tablename, g1105x1_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x1_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x1_ left outer join (select g1097x2_.rid as rid, 'e1097' as tablename, g1097x2_.p1204 as p1204, NULL  as NULL , g1097x2_.p1212 as p1212 from e1097 g1097x2_) g1097x2_ ON g1105x1_.p6226_rid = g1097x2_.rid ON g1062x0_.p6216_rid = g1105x1_.rid where g1097x2_.p1204 like '%3%'",
				gen.getSQL());

		toParse = "SELECT Reference FROM SAFETY  ";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println(printer.showAsString(ast, "OntoQL  :  "));
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		Assert.assertTrue(gen.getSQL().indexOf(")(") == -1);
		Assert.assertTrue(gen.getSQL().indexOf("from )") == -1);

		toParse = "SELECT Reference FROM \"PERSONAL EQUIPEMENT/SAFETY\"  ";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		// System.out.println(printer.showAsString(ast, "OntoQL : "));
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		Assert.assertTrue(gen.getSQL().indexOf(")(") == -1);
		Assert.assertTrue(gen.getSQL().indexOf("from )") == -1);

		toParse = "SELECT Reference FROM only(\"PERSONAL EQUIPEMENT/SAFETY\")  ";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();

		ast = parser.getAST();
		// System.out.println(printer.showAsString(ast, "OntoQL : "));
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		Assert.assertEquals("select NULL::varchar  from (select 0 where false) g1040x0_", gen.getSQL());
		s.close();
	}

	@Test
	public void testGeneratedSQLNameIdFrench() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("fr");

		ASTPrinter printer = new ASTPrinter(SQLTokenTypes.class);

		String toParse = "SELECT Taille FROM only(HUDSON)";
		System.out.println("input: ." + toParse + "<-");
		OntoQLParser parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		AST ast = parser.getAST();

		OntoQLSQLWalker walker = new OntoQLSQLWalker(s);
		walker.statement(ast);

		SQLGenerator gen = new SQLGenerator(s);
		gen.statement(walker.getAST());

		Assert.assertEquals(gen.getSQL(),
				"select g1062x0_.p" + pSize.getInternalId() + " from e" + cHudson.getInternalId() + " g1062x0_");

		// 2éme tests : plusieurs propriétés
		toParse = "SELECT Taille, Référence FROM only(HUDSON)";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		gen.statement(walker.getAST());
		Assert.assertEquals(gen.getSQL(), "select g1062x0_.p" + pSize.getInternalId() + ", g1062x0_.p"
				+ pReference.getInternalId() + " from e" + cHudson.getInternalId() + " g1062x0_");

		// La classe n'a pas d'extension
		toParse = "SELECT Référence FROM only(\"COUPES - VENT\")";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		gen.statement(walker.getAST());
		Assert.assertEquals("select NULL::varchar  from (select 0 where false) g1068x0_", gen.getSQL());

		// Requéte polymorphe
		toParse = "SELECT Référence FROM CASQUES";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		gen.statement(walker.getAST());
		Assert.assertEquals(
				"select g1066x0_.p1204 from (select e1065.rid as rid, 'e1065' as tablename, e1065.p1204 as p1204, NULL  as NULL , e1065.p1212 as p1212 from e1065 union all select e1064.rid as rid, 'e1064' as tablename, e1064.p1204 as p1204, NULL  as NULL , e1064.p1212 as p1212 from e1064 union all select e1097.rid as rid, 'e1097' as tablename, e1097.p1204 as p1204, NULL  as NULL , e1097.p1212 as p1212 from e1097) g1066x0_",
				gen.getSQL());

		// Filter condition
		toParse = "SELECT Référence FROM only(HUDSON) Where Référence like '3%'";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		Assert.assertEquals(gen.getSQL(), "select g1062x0_.p" + pReference.getInternalId() + " from e"
				+ cHudson.getInternalId() + " g1062x0_ where g1062x0_.p" + pReference.getInternalId() + " like '3%'");

		toParse = "SELECT count(Référence) FROM only(HUDSON) Where Référence like '3%' group by Référence having Taille = 'Jaune'  order by Référence  asc";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		Assert.assertEquals(
				"select count(g1062x0_.p1204) from e1062 g1062x0_ where g1062x0_.p1204 like '3%' group by g1062x0_.p1204 having g1062x0_.p1202='Jaune' order by g1062x0_.p1204 asc",
				gen.getSQL());
		// Distinct
		toParse = "SELECT distinCT Référence FROM only(HUDSON) Where Référence like '3%'";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		Assert.assertEquals(gen.getSQL(), "select distinct g1062x0_.p" + pReference.getInternalId() + " from e"
				+ cHudson.getInternalId() + " g1062x0_ where g1062x0_.p" + pReference.getInternalId() + " like '3%'");

		// OR - AND condition
		toParse = "SELECT Référence FROM only(HUDSON) WHERE (Référence = 'TOTO') OR ( (Taille = 'JAUNE') AND (Référence > '3') )";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		// AND has a higher priority on OR => no parenthesis
		Assert.assertEquals(gen.getSQL(),
				"select g1062x0_.p" + pReference.getInternalId() + " from e" + cHudson.getInternalId()
						+ " g1062x0_ where g1062x0_.p" + pReference.getInternalId() + "='TOTO' or g1062x0_.p"
						+ pSize.getInternalId() + "='JAUNE' and g1062x0_.p" + pReference.getInternalId() + ">'3'");

		toParse = "SELECT Référence FROM only(HUDSON) WHERE (Référence = 'TOTO' OR Taille = 'JAUNE') AND (Référence > '3') ";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		// AND has a higher priority on OR => ()
		Assert.assertEquals(gen.getSQL(),
				"select g1062x0_.p" + pReference.getInternalId() + " from e" + cHudson.getInternalId()
						+ " g1062x0_ where (g1062x0_.p" + pReference.getInternalId() + "='TOTO' or g1062x0_.p"
						+ pSize.getInternalId() + "='JAUNE') and g1062x0_.p" + pReference.getInternalId() + ">'3'");

		s.close();
	}

	@Test
	public void testGeneratedSQLInternalId() throws Exception {
		OntoQLSession s = getSession();

		ASTPrinter printer = new ASTPrinter(SQLTokenTypes.class);

		String toParse = "SELECT !" + pSize.getInternalId() + " FROM only(!" + cHudson.getInternalId() + ")";
		System.out.println("input: ." + toParse + "<-");
		OntoQLParser parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		AST ast = parser.getAST();

		OntoQLSQLWalker walker = new OntoQLSQLWalker(s);
		walker.statement(ast);

		SQLGenerator gen = new SQLGenerator(s);
		gen.statement(walker.getAST());

		Assert.assertEquals(gen.getSQL(),
				"select g1062x0_.p" + pSize.getInternalId() + " from e" + cHudson.getInternalId() + " g1062x0_");

		// 2éme tests : plusieurs propriétés
		toParse = "SELECT !" + pSize.getInternalId() + ", !" + pReference.getInternalId() + " FROM only(!"
				+ cHudson.getInternalId() + ") g1062x0_";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		gen.statement(walker.getAST());
		Assert.assertEquals(gen.getSQL(), "select g1062x0_.p" + pSize.getInternalId() + ", g1062x0_.p"
				+ pReference.getInternalId() + " from e" + cHudson.getInternalId() + " g1062x0_");

		// La classe n'a pas d'extension
		toParse = "SELECT !" + pReference.getInternalId() + " FROM only(!" + cCags.getInternalId() + ")";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		gen.statement(walker.getAST());
		Assert.assertEquals("select NULL::varchar  from (select 0 where false) g1068x0_", gen.getSQL());

		// requéte polymorphe
		toParse = "SELECT !" + pReference.getInternalId() + " FROM !" + cHelmets.getInternalId() + "";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		gen.statement(walker.getAST());
		Assert.assertEquals(
				"select g1066x0_.p1204 from (select e1065.rid as rid, 'e1065' as tablename, e1065.p1204 as p1204, NULL  as NULL , e1065.p1212 as p1212 from e1065 union all select e1064.rid as rid, 'e1064' as tablename, e1064.p1204 as p1204, NULL  as NULL , e1064.p1212 as p1212 from e1064 union all select e1097.rid as rid, 'e1097' as tablename, e1097.p1204 as p1204, NULL  as NULL , e1097.p1212 as p1212 from e1097) g1066x0_",
				gen.getSQL());

		// Filter condition
		toParse = "SELECT !" + pReference.getInternalId() + " FROM only(!" + cHudson.getInternalId()
				+ ") g1062x0_ Where !" + pReference.getInternalId() + " like '3%'";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		Assert.assertEquals(gen.getSQL(), "select g1062x0_.p" + pReference.getInternalId() + " from e"
				+ cHudson.getInternalId() + " g1062x0_ where g1062x0_.p" + pReference.getInternalId() + " like '3%'");

		toParse = "SELECT count(!" + pReference.getInternalId() + ") FROM only(!" + cHudson.getInternalId()
				+ ") g1062x0_ Where !" + pReference.getInternalId() + " like '3%' group by !"
				+ pReference.getInternalId() + " having !" + pSize.getInternalId() + " = 'Jaune'  order by !"
				+ pReference.getInternalId() + "  asc";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		Assert.assertEquals(
				"select count(g1062x0_.p1204) from e1062 g1062x0_ where g1062x0_.p1204 like '3%' group by g1062x0_.p1204 having g1062x0_.p1202='Jaune' order by g1062x0_.p1204 asc",
				gen.getSQL());

		// Distinct
		toParse = "SELECT distinCT !" + pReference.getInternalId() + " FROM only(!" + cHudson.getInternalId()
				+ ") g1062x0_ Where !" + pReference.getInternalId() + " like '3%'";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		Assert.assertEquals(gen.getSQL(), "select distinct g1062x0_.p" + pReference.getInternalId() + " from e"
				+ cHudson.getInternalId() + " g1062x0_ where g1062x0_.p" + pReference.getInternalId() + " like '3%'");

		// OR - AND condition
		toParse = "SELECT !" + pReference.getInternalId() + " FROM only(!" + cHudson.getInternalId()
				+ ") g1062x0_ WHERE (!" + pReference.getInternalId() + " = 'TOTO') OR ( (!" + pSize.getInternalId()
				+ " = 'JAUNE') AND (!" + pReference.getInternalId() + " > '3') )";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		// AND has a higher priority on OR => no parenthesis
		Assert.assertEquals(gen.getSQL(),
				"select g1062x0_.p" + pReference.getInternalId() + " from e" + cHudson.getInternalId()
						+ " g1062x0_ where g1062x0_.p" + pReference.getInternalId() + "='TOTO' or g1062x0_.p"
						+ pSize.getInternalId() + "='JAUNE' and g1062x0_.p" + pReference.getInternalId() + ">'3'");

		toParse = "SELECT !" + pReference.getInternalId() + " FROM only(!" + cHudson.getInternalId()
				+ ") g1062x0_ WHERE (!" + pReference.getInternalId() + " = 'TOTO' OR !" + pSize.getInternalId()
				+ " = 'JAUNE') AND (!" + pReference.getInternalId() + " > '3') ";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL  :  " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());
		// AND has a higher priority on OR => ()
		Assert.assertEquals(gen.getSQL(),
				"select g1062x0_.p" + pReference.getInternalId() + " from e" + cHudson.getInternalId()
						+ " g1062x0_ where (g1062x0_.p" + pReference.getInternalId() + "='TOTO' or g1062x0_.p"
						+ pSize.getInternalId() + "='JAUNE') and g1062x0_.p" + pReference.getInternalId() + ">'3'");

		s.close();
	}

	@Test
	public void testInsert() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");

		ASTPrinter printer = new ASTPrinter(SQLTokenTypes.class);
		String toParse = "INSERT INTO ONTARIO (Reference,Size) values ('300', 'XL')";
		System.out.println("input: ." + toParse + "<-");
		OntoQLParser parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		AST ast = parser.getAST();
		log.warn(printer.showAsString(ast, "--- OntoQL AST ---"));
		OntoQLSQLWalker walker = new OntoQLSQLWalker(s);
		walker.statement(ast);

		SQLGenerator gen = new SQLGenerator(s);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());

		Assert.assertEquals("insert into e1063(version_min, p1204, p1202)values (1, '300', 'XL')", gen.getSQL());

		s.close();
	}

	@Test
	public void testUpdate() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");

		ASTPrinter printer = new ASTPrinter(SQLTokenTypes.class);
		String toParse = "UPDATE only(ONTARIO) SET Reference='300' where Size = 'XL'";
		System.out.println("input: ->" + toParse + "<-");
		OntoQLParser parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		AST ast = parser.getAST();
		log.warn(printer.showAsString(ast, "--- OntoQL AST ---"));
		OntoQLSQLWalker walker = new OntoQLSQLWalker(s);
		walker.statement(ast);

		SQLGenerator gen = new SQLGenerator(s);
		log.warn(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		gen.statement(walker.getAST());

		Assert.assertEquals("update e1063 set p1204='300' where e1063.p1202='XL'", gen.getSQL());

		s.close();
	}
}