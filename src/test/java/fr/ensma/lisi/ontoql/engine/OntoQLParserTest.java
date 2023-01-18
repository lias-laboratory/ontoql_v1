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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Assert;
import org.junit.Test;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.engine.util.ASTPrinter;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Stéphane JEAN
 */
public class OntoQLParserTest extends OntoQLTestCase {

	@Test
	public void testInternalId() throws Exception {
		OntoQLSession s = getSession();

		parse("select * from !1145 where !1145 like 'test' and !1145 <> 'test' and  !1145 <> 'test' ");
		parse("select !" + pReference.getInternalId() + ", !" + pSize.getInternalId() + "" + " from !"
				+ cHudson.getInternalId());

		s.setReferenceLanguage("fr");
		// identification interne toujours possible
		parse("select !" + pReference.getInternalId() + ", !" + pSize.getInternalId() + "" + " from !"
				+ cHudson.getInternalId());

		s.close();

	}

	@Test
	public void testExternalId() throws Exception {
		OntoQLSession s = getSession();

		parse("select @71DC338877222-001.@71FAD155DE7D9-001 " + "from @71DC338877222-001");
		parse("select @71DC338877222-001.@71DC249C6E4AC-001 \n" + "from @71DC338877222-001\n");

		s.close();
	}

	@Test
	public void testNameId() throws Exception {
		OntoQLSession s = getSession();

		s.setReferenceLanguage("en");

		parse("SELECT count(Size)\n" + "FROM CAGS\n");
		parse("SELECT Reference\n" + "FROM HUDSON\n" + "WHERE  NOT (NOT (virage > 10))\n");

		s.setReferenceLanguage("fr");

		parse("SELECT Taille\n" + "FROM ONTARIO\n" + "WHERE NOT (Taille = 'XL')\n");
		parse("SELECT Taille\n" + "FROM HUDSON\n" + "WHERE  NOT (Taille = 'XL' OR virage > 10)\n");
		parse("SELECT Taille\n" + "FROM HUDSON\n" + "WHERE  NOT (Taille = 'XL' OR virage > 10)\n" + "AND EXISTS (\n"
				+ "SELECT * FROM ONTARIO WHERE HUDSON.Taille = ONTARIO.Taille)");

		s.setReferenceLanguage("en");

		parse("SELECT Size\n" + "FROM CAGS\n" + "UNION\n" + "SELECT Size\n" + "FROM ONTARIO\n");
		parse("SELECT Size, Reference\n" + "FROM CAGS\n" + "WHERE Size = 'XL'\n");

		// cas où le nom ne peut pas servir de token

		parse("select \"Reference\", \"Size\"\n" + "from \"FJORD (SEA PARKA)\"\n");

		parse("SELECT Reference FROM CAGS");

		parse("SELECT Reference, Buoyancy, \"Buoyancy RAFT\", Size FROM \"PERSONAL EQUIPEMENT/SAFETY\" ");

		s.close();
	}

	@Test
	public void testOntologyQuery() throws Exception {
		OntoQLSession s = getSession();

		parse("select #name,#defintion\n" + "from #class");
		parse("select #name[fr], #name, #defintion[fr], #definition \n" + "from #class");
		parse("select #name[fr], #name, #defintion[fr], #definition \n" + "from #class c, #property p \n");
		parse("select c.#code," + "c.#version," + "c.#name[en]," + "c.#name[fr]," + "c.#contextProp.#code,"
				+ "c.#contextProp.#version," + "c.#contextProp.#name[en]," + "c.#contextProp.#name[fr] "
				+ "from #class c " + "where c.#oid = 1062");

		s.close();
	}

	@Test
	public void testPrefixClass() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");
		parse("select HUDSON.Reference, HUDSON.Size from HUDSON");

		s.close();
	}

	@Test
	public void testSelect() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");

		parse("SELECT f.Reference FROM HUDSON qat, ONTARIO f");
		parse("SELECT DISTINCT Reference FROM HUDSON qat,ONTARIO as bar, CAGS f, STANDARD");
		parse("SELECT count(*) FROM STANDARD qat");
		parse("SELECT avg(qat.Reference) FROM STANDARD qat");
		parse("SELECT qat.rid FROM STANDARD qat");

		s.close();
	}

	@Test
	public void testWhere() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");

		parse("select * FROM HUDSON qat where qat.Reference like '%fluffy%' or qat.virage > 5");
		parse("select * FROM HUDSON qat where not qat.Reference like '%fluffy%' or qat.virage > 5");
		parse("select * FROM HUDSON qat where not qat.Reference not like '%fluffy%'");
		parse("select * FROM HUDSON qat where qat.Reference in ('crater','bean','fluffy')");
		parse("select * FROM HUDSON qat where qat.Reference not in ('crater','bean','fluffy')");
		parse("select * from HUDSON an where (an.virage > 10 and an.virage < 100) or an.virage is null");
		s.close();
	}

	@Test
	public void testGroupBy() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");
		parse("select * FROM HUDSON qat group by qat.Reference");
		parse("select * FROM HUDSON qat group by qat.Reference, qat.virage");
		s.close();
	}

	@Test
	public void testOrderBy() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");
		parse("select * FROM HUDSON qat order by avg(qat.virage)");
		s.close();
	}

	@Test
	public void testDoubleLiteral() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");
		parse("select * from HUDSON as tinycat where fatcat.virage < 3.1415");
		parse("select * from HUDSON as enormouscat where fatcat.virage > 3.1415e3");
		s.close();
	}

	@Test
	public void testInNotIn() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");
		parse("select * from HUDSON where foo.Reference in ('a' , 'b', 'c')");
		parse("select * from HUDSON where foo.Reference not in ('a' , 'b', 'c')");
		s.close();
	}

	@Test
	public void testOperatorPrecedence() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");
		parse("select * from HUDSON where foo.virage = 123 + foo.virage * foo.virage");
		parse("select * from HUDSON where foo.Reference like foo.Reference or foo.Reference in ('duh', 'gob')");
		s.close();
	}

	@Test
	public void testNot() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");
		parse("select * from HUDSON cat where not ( cat.virage < 1 )");
		parse("select * from HUDSON cat where not ( cat.virage > 1 )");
		parse("select * from HUDSON cat where not ( cat.virage >= 1 )");
		parse("select * from HUDSON cat where not ( cat.virage <= 1 )");
		parse("select * from HUDSON cat where not ( cat.Reference between 'A' and 'B' ) ");
		parse("select * from HUDSON cat where not ( cat.Reference not between 'A' and 'B' ) ");
		parse("select * from HUDSON cat where not ( not cat.virage <= 1 )");
		parse("select * from HUDSON cat where not  not ( not cat.virage <= 1 )");
		s.close();
	}

	@Test
	public void testCount() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");
		parse("select count(Reference)  FROM CAGS");

		s.setReferenceLanguage("en");
		parse("select count(*)  FROM CAGS");

		s.close();
	}

	@Test
	public void testInsert() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");
		parse("INSERT INTO ONTARIO (Reference,Size) values ('300', 'XL')");
		parse("Insert into \"FJORD (SEA PARKA)\" (Reference,Size) values ('300', 'XL')");
		parse("Insert into HUDSON (Reference,Size, virage) values ('300', 'XL', 10)");
		parse("INSERT INTO ONTARIO (Reference,Size) select Reference,Size from HUDSON ");
		parse("Insert into !1031 (Reference,Size, names) values ('300', 'XL', ARRAY['baby1','baby2'])");
		s.close();
	}

	@Test
	public void testUpdate() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");
		parse("UPDATE ONTARIO SET Reference='300' where Size = 'XL'");
		parse("UPDATE \"FJORD (SEA PARKA)\" SET Reference='300' where Size = 'XL'");
		parse("UPDATE HUDSON set Reference='300', virage=10 where Size = 'XL'");
		parse("UPDATE HUDSON SET its_muff = (SELECT m.rid from \"NEOPRENE MUFFS, HOOK AND LOOP CLOSURE\" m where m.Reference = '15' or m.Reference is null)");

		s.close();
	}

	@Test
	public void testDelete() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");
		parse("DELETE FROM ONTARIO");
		parse("DELETE FROM \"FJORD (SEA PARKA)\" where Size = 'XL'");
		parse("DELETE FROM \"FJORD (SEA PARKA)\" where its_muff in (select m.rid from MUFFS m where m.Reference = '%%' or m.Reference is null)");

		s.close();
	}

	@Test
	public void testDDL() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		parse("CREATE #CLASS Vehicule ( " + "DESCRIPTOR (#name[en] = 'Vehicle', #code = 'BB3DFD4', #version = '001') "
				+ "PROPERTIES (immatriculation String))");
		parse("CREATE #CLASS Voiture UNDER Vehicule");
		parse("CREATE #PROPERTY owner CONTEXT Vehicule (DESCRIPTOR(#range=String))");
		parse("create extent of Vehicule (\"number of wheels\", number)");
		s.close();

	}

	@Test
	public void testDemonstrationV1_0() throws Exception {
		OntoQLSession s = getSession();

		parse("select Reference,Size from CAGS");
		parse("SELECT Reference FROM \"ANCHORAGE (KAYAK PANTS)\"");
		parse("Select Référence, Taille from \"COUPES - VENT\"");
		parse("SELECT Référence FROM \"ANCHORAGE (PANTALON KAYAK)\"");
		parse("SELECT @71DC24862420C-001, @71DC2486B78EF-001 FROM @71DC2FDBFD904-001");
		parse("SELECT @71DC24862420C-001 from @71DC2FE39B1EA-001");
		parse("SELECT !1204, !1202 FROM !1068");
		parse("SELECT !1204  FROM !1060");
		parse("select Référence,Taille from \"COUPES - VENT\" where Taille = 'XL'");
		parse("select Référence,Taille from \"COUPES - VENT\" where Taille like 'X%'");
		parse("select Référence,Taille,virage from HUDSON where Taille in ('XL','M','S')");
		parse("select Référence, Taille from CASQUES where Taille is null");
		parse("select Référence, Taille from CASQUES where Taille is not null");
		parse("select Référence,Taille,virage from HUDSON where virage/3+2 = 3");
		parse("select Référence,Taille,virage from HUDSON where virage/3+2 = 3 and Taille like 'X%'");
		parse("select Référence,Taille,virage from HUDSON where virage/3+2 = 3 or Taille like 'X%'");
		parse("select HUDSON.Référence, HUDSON.Taille from HUDSON");
		parse("select h.Référence, h.Taille from HUDSON h");
		parse("select h.Référence, h.Taille from HUDSON as h");
		parse("select c.Référence, c.Taille from CASQUES c");
		parse("select c.Référence,c.Taille from \"COUPES - VENT\" c where c.Taille like 'X%'");
		parse("SELECT g.Référence, c.Référence FROM GANTS g, CHAUSSONS c WHERE g.Taille = c.Taille");
		parse("SELECT g.Référence, c.Référence FROM GANTS g, CHAUSSONS c WHERE g.Taille > c.Taille");
		parse("select c1.Référence, c1.Taille, c2.Référence, c2.Taille, c1.virage, c2.virage FROM HUDSON c1, HUDSON c2 WHERE c1.virage = c2.virage+2");
		parse("SELECT g.Référence FROM GANTS g WHERE g.Taille > all (select c.Taille from  CHAUSSONS c)");
		parse("select c1.Référence,c1.virage FROM  HUDSON c1 WHERE c1.virage in (select c2.virage+2 from HUDSON c2)");
		parse("SELECT h.its_muff.Reference from HUDSON h");
		parse("SELECT its_muff.its_slalom.Reference from HUDSON");
		parse("SELECT Reference from HUDSON where its_muff.its_slalom.Reference like '3006%'");
		parse("SELECT h1.its_muff.its_slalom.Reference,h2.its_muff.Reference from HUDSON h1, HUDSON h2");
		parse("SELECT h1.its_muff.its_slalom.Reference,h2.its_muff.Reference from HUDSON h1, HUDSON h2 where  h1.its_muff.its_slalom.Reference > h2.its_muff.Reference or (h2.its_muff.Reference is null)");
		parse("select max(virage) FROM HUDSON");
		parse("select min(virage) FROM HUDSON");
		parse("select avg(virage) FROM HUDSON");
		parse("select sum(virage) FROM HUDSON");
		parse("select sum(virage+2/3.5) FROM HUDSON");
		parse("select min(Reference) FROM CAGS");
		parse("select max(Reference) FROM CAGS");
		parse("select count(Reference) FROM CAGS");
		parse("select count(Reference), Size FROM CAGS GROUP BY Size");
		parse("select count(Reference), Size FROM CAGS GROUP BY Size HAVING Size = 'XL' OR Size = 'L'");
		parse("select count(Reference), Size FROM CAGS GROUP BY Size HAVING Size = 'XL' OR Size = 'L' ORDER BY Size DESC");
		parse("select count(Reference), Size FROM CAGS WHERE Reference like '%3%' GROUP BY Size HAVING Size = 'XL' OR Size = 'L' ORDER BY Size DESC");
		parse("select max(its_muff.its_slalom.Reference) from ONLY (HUDSON)");
		parse("INSERT INTO ONTARIO (Reference,Size) values ('300', 'XL')");
		parse("Insert into \"FJORD (SEA PARKA)\" (Reference,Size) values ('300', 'XL')");
		parse("Insert into HUDSON (Reference,Size, virage) values ('300', 'XL', 10)");
		parse("Insert into HUDSON (Reference,Size,its_muff, virage) values ('300', 'XL', (SELECT m.rid from MUFFS m where m.Reference like '%%' or m.Reference is null), 10)");
		parse("UPDATE \"FJORD (SEA PARKA)\" SET Reference='300' where Size = 'XL'");
		parse("UPDATE HUDSON set Reference='300', virage=10 where Size = 'XL'");
		parse("UPDATE HUDSON SET its_muff = (SELECT m.rid from \"NEOPRENE MUFFS\" m where m.Reference = '389000') WHERE Reference = '300061'");
		parse("DELETE FROM ONTARIO");
		parse("DELETE FROM \"FJORD (SEA PARKA)\" where Size = 'XL'");
		parse("DELETE FROM HUDSON where its_muff in (select m.rid from MUFFS m where m.Reference = '%%' or m.Reference is null)");
		parse("SELECT #code, #version FROM #class");
		parse("select #name[fr] from #class");
		parse("select #name[fr] from #class where #name[en]='CAGS'");
		parse("select #name[fr] from #class where #name[en] like 'C%'");

		// Ajout pour décrire le processing d'une requéte ontoQL
		parse("SELECT h1.its_muff.its_slalom.Reference,h2.its_muff.Reference from HUDSON h1, HUDSON h2 where h1.oid = h2.oid and h2.its_muff.its_slalom.reference = 'XL'");

		s.close();

	}

	private void parse(String input) throws RecognitionException, TokenStreamException {
		doParse(input);
	}

	private void doParse(String input) throws RecognitionException, TokenStreamException {

		System.out.println("input -> " + ASTPrinter.escapeMultibyteChars(input) + "<-");
		OntoQLParser parser = OntoQLParser.getInstance(input);
		parser.statement();
		AST ast = parser.getAST();
		System.out.println("AST  ->  " + ast.toStringTree() + "");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		parser.showAst(ast, new PrintStream(baos));
		System.out.println(baos.toString());
		Assert.assertEquals("At least one error occurred during parsing!", 0,
				parser.getParseErrorHandler().getErrorCount());
	}
}