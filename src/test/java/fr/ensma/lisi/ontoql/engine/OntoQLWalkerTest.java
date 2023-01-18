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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import antlr.collections.AST;
import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.dql.SelectExpression;
import fr.ensma.lisi.ontoql.engine.util.ASTPrinter;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.QueryException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Stéphane JEAN
 */
public class OntoQLWalkerTest extends OntoQLTestCase {

	/**
	 * A logger for this class.
	 */
	private static final Log log = LogFactory.getLog(OntoQLWalkerTest.class);

	@Test
	public void testSemanticChecking() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String toParse = "select Size from ONLY(CAGS)";

		OntoQLParser parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		AST ast = parser.getAST();
		log.warn(ast.toStringTree());

		OntoQLSQLWalker walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} NULL::varchar  ) ( FromClause{level=1} (select 0 where false) g1068x0_ ) )",
				walker.getAST().toStringTree());

		toParse = "select size from CAGS";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		try {
			walker.statement(ast);
			walker.getParseErrorHandler().throwQueryException();
			Assert.fail();
		} catch (JOBDBCException exc) {
			Assert.assertEquals(exc.getMessage(),
					"size is not a property defined on the namespace http://lisi.ensma.fr/");
		}

		toParse = "select Size from CaGS";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		try {
			walker.statement(ast);
			Assert.assertEquals(walker.getParseErrorHandler().getErrorCount(), 2);
			walker.getParseErrorHandler().throwQueryException();
			Assert.fail();
		} catch (JOBDBCException exc) {
			Assert.assertEquals("CaGS is not a valid class name on the namespace http://lisi.ensma.fr/",
					exc.getMessage());
		}

		toParse = "select virage from CAGS";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		try {
			walker.statement(ast);
			walker.getParseErrorHandler().throwQueryException();
			Assert.fail();
		} catch (QueryException exc) {
			Assert.assertEquals(exc.getMessage(),
					"The property 'virage' is not defined in the context of the from clause");
		}

		// Type checking
		toParse = "select Size+2 from CAGS";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		try {
			walker.statement(ast);
			walker.getParseErrorHandler().throwQueryException();
			Assert.fail();
		} catch (QueryException exc) {
			Assert.assertEquals(exc.getMessage(), "The operator '+' can not be used on Size");
		}

		toParse = "select virage  from HUDSON where virage like 'toto'";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		try {
			walker.statement(ast);
			walker.getParseErrorHandler().throwQueryException();
			Assert.fail();
		} catch (QueryException exc) {
			Assert.assertEquals(exc.getMessage(), "The operator 'like' can not be used on virage");
		}

		toParse = "select virage + Size  from HUDSON where virage like 'toto'";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		try {
			walker.statement(ast);
			walker.getParseErrorHandler().throwQueryException();
			Assert.fail();
		} catch (QueryException exc) {
			Assert.assertEquals(exc.getMessage(), "The operator '+' can not be used on Size");
		}

		toParse = "select Reference || Size  from HUDSON where Size like 'toto'";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		try {
			walker.statement(ast);
			walker.getParseErrorHandler().throwQueryException();

		} catch (QueryException exc) {
			Assert.fail();
		}

		toParse = "select virage  from HUDSON where (virage + virage) like 'toto'";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		try {
			walker.statement(ast);
			walker.getParseErrorHandler().throwQueryException();
			Assert.fail();
		} catch (QueryException exc) {
			Assert.assertEquals(exc.getMessage(), "The operator 'like' can not be used on virage + virage");
		}

		// Duplicate alias
		toParse = "select Size from HUDSON,HUDSON";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		try {
			walker.statement(ast);
			walker.getParseErrorHandler().throwQueryException();
			Assert.fail();
		} catch (QueryException exc) {
			Assert.assertEquals(exc.getMessage(), "Duplicate definition of alias 'HUDSON'");
		}

		// Duplicate alias
		toParse = "select Size from HUDSON h1,HUDSON h1";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		try {
			walker.statement(ast);
			walker.getParseErrorHandler().throwQueryException();
			Assert.fail();
		} catch (QueryException exc) {
			Assert.assertEquals(exc.getMessage(), "Duplicate definition of alias 'h1'");
		}

		// ambiguous property
		toParse = "select Size from HUDSON h1,HUDSON h2";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		try {
			walker.statement(ast);
			walker.getParseErrorHandler().throwQueryException();
			Assert.fail();
		} catch (QueryException exc) {
			Assert.assertEquals(exc.getMessage(), "The property 'Size' is ambiguous in this query");
		}

		// ambiguous property
		toParse = "select virage from HUDSON where Size IN (SELECT Size FROM CAGS)";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		try {
			walker.statement(ast);
			walker.getParseErrorHandler().throwQueryException();
			Assert.fail();
		} catch (QueryException exc) {
			Assert.assertEquals(exc.getMessage(), "The property 'Size' is ambiguous in this query");
		}

		s.close();
	}

	@Test
	public void testStarInSelect() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String toParse = "select * from ONLY(CAGS)";

		OntoQLParser parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		AST ast = parser.getAST();
		log.warn(ast.toStringTree());

		OntoQLSQLWalker walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} NULL  NULL::varchar  NULL::varchar  NULL::float8  NULL  NULL  NULL::float8  NULL::varchar  ) ( FromClause{level=1} (select 0 where false) g1068x0_ ) )",
				walker.getAST().toStringTree());

		toParse = "select * from CAGS";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1068x0_.rid g1068x0_.p1202 g1068x0_.p1204 NULL::float8  NULL  NULL  NULL::float8  g1068x0_.p20476 ) ( FromClause{level=1} (select e1067.rid as rid, 'e1067' as tablename, e1067.p1204 as p1204, e1067.p1202 as p1202, NULL::varchar  as p20476 from e1067 union all select e1023.rid as rid, 'e1023' as tablename, e1023.p1204 as p1204, e1023.p1202 as p1202, NULL::varchar  as p20476 from e1023 union all select e1062.rid as rid, 'e1062' as tablename, e1062.p1204 as p1204, e1062.p1202 as p1202, e1062.p20476 as p20476 from e1062 union all select e1063.rid as rid, 'e1063' as tablename, e1063.p1204 as p1204, e1063.p1202 as p1202, NULL::varchar  as p20476 from e1063 union all select e1061.rid as rid, 'e1061' as tablename, e1061.p1204 as p1204, e1061.p1202 as p1202, NULL::varchar  as p20476 from e1061 union all select e1060.rid as rid, 'e1060' as tablename, e1060.p1204 as p1204, e1060.p1202 as p1202, NULL::varchar  as p20476 from e1060) g1068x0_ ) )",
				walker.getAST().toStringTree());

		toParse = "select * from ONLY(HUDSON)";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1062x0_.rid g1105x1_.rid g1105x1_.tablename g1105x1_.p6226_rid g1105x1_.p6226_tablename g1105x1_.p1202 g1105x1_.p1204 NULL::float8  NULL  NULL  NULL::float8  g1105x1_.p20476 g1062x0_.p6237 g1062x0_.p1202 g1062x0_.p1204 NULL::float8  NULL  NULL  NULL::float8  g1062x0_.p20476 ) ( FromClause{level=1} ( e1062 g1062x0_ LEFT OUTER JOIN (select g1105x1_.rid as rid, 'e1105' as tablename, g1105x1_.p1204 as p1204, g1105x1_.p6226_rid as p6226_rid, g1105x1_.p6226_tablename as p6226_tablename, g1105x1_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x1_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x1_ ON g1062x0_.p6216_rid = g1105x1_.rid ) ) )",
				walker.getAST().toStringTree());

		toParse = "select * from only(HUDSON) h1, only(HUDSON) h2";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1062x0_.rid g1105x2_.rid g1105x2_.tablename g1105x2_.p6226_rid g1105x2_.p6226_tablename g1105x2_.p1202 g1105x2_.p1204 NULL::float8  NULL  NULL  NULL::float8  g1105x2_.p20476 g1062x0_.p6237 g1062x0_.p1202 g1062x0_.p1204 NULL::float8  NULL  NULL  NULL::float8  g1062x0_.p20476 g1062x1_.rid g1105x3_.rid g1105x3_.tablename g1105x3_.p6226_rid g1105x3_.p6226_tablename g1105x3_.p1202 g1105x3_.p1204 NULL::float8  NULL  NULL  NULL::float8  g1105x3_.p20476 g1062x1_.p6237 g1062x1_.p1202 g1062x1_.p1204 NULL::float8  NULL  NULL  NULL::float8  g1062x1_.p20476 ) ( FromClause{level=1} ( e1062 g1062x0_ LEFT OUTER JOIN (select g1105x2_.rid as rid, 'e1105' as tablename, g1105x2_.p1204 as p1204, g1105x2_.p6226_rid as p6226_rid, g1105x2_.p6226_tablename as p6226_tablename, g1105x2_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x2_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x2_ ON g1062x0_.p6216_rid = g1105x2_.rid ) ( e1062 g1062x1_ LEFT OUTER JOIN (select g1105x3_.rid as rid, 'e1105' as tablename, g1105x3_.p1204 as p1204, g1105x3_.p6226_rid as p6226_rid, g1105x3_.p6226_tablename as p6226_tablename, g1105x3_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x3_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x3_ ON g1062x1_.p6216_rid = g1105x3_.rid ) ) )",
				walker.getAST().toStringTree());

		s.close();
	}

	@Test
	public void testPolymorphism() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String toParse = "select Size from CAGS";

		OntoQLParser parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		AST ast = parser.getAST();
		log.warn(ast.toStringTree());

		OntoQLSQLWalker walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1068x0_.p1202 ) ( FromClause{level=1} (select e1067.rid as rid, 'e1067' as tablename, e1067.p1204 as p1204, e1067.p1202 as p1202, NULL::varchar  as p20476 from e1067 union all select e1023.rid as rid, 'e1023' as tablename, e1023.p1204 as p1204, e1023.p1202 as p1202, NULL::varchar  as p20476 from e1023 union all select e1062.rid as rid, 'e1062' as tablename, e1062.p1204 as p1204, e1062.p1202 as p1202, e1062.p20476 as p20476 from e1062 union all select e1063.rid as rid, 'e1063' as tablename, e1063.p1204 as p1204, e1063.p1202 as p1202, NULL::varchar  as p20476 from e1063 union all select e1061.rid as rid, 'e1061' as tablename, e1061.p1204 as p1204, e1061.p1202 as p1202, NULL::varchar  as p20476 from e1061 union all select e1060.rid as rid, 'e1060' as tablename, e1060.p1204 as p1204, e1060.p1202 as p1202, NULL::varchar  as p20476 from e1060) g1068x0_ ) )",
				walker.getAST().toStringTree());

		toParse = "select virage from CAGS,HUDSON";

		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1062x1_.p6237 ) ( FromClause{level=1} (select e1067.rid as rid, 'e1067' as tablename, e1067.p1204 as p1204, e1067.p1202 as p1202, NULL::varchar  as p20476 from e1067 union all select e1023.rid as rid, 'e1023' as tablename, e1023.p1204 as p1204, e1023.p1202 as p1202, NULL::varchar  as p20476 from e1023 union all select e1062.rid as rid, 'e1062' as tablename, e1062.p1204 as p1204, e1062.p1202 as p1202, e1062.p20476 as p20476 from e1062 union all select e1063.rid as rid, 'e1063' as tablename, e1063.p1204 as p1204, e1063.p1202 as p1202, NULL::varchar  as p20476 from e1063 union all select e1061.rid as rid, 'e1061' as tablename, e1061.p1204 as p1204, e1061.p1202 as p1202, NULL::varchar  as p20476 from e1061 union all select e1060.rid as rid, 'e1060' as tablename, e1060.p1204 as p1204, e1060.p1202 as p1202, NULL::varchar  as p20476 from e1060) g1068x0_ (select g1062x1_.rid as rid, 'e1062' as tablename, g1062x1_.p1204 as p1204, g1062x1_.p1202 as p1202, g1062x1_.p6216_rid as p6216_rid, g1062x1_.p6216_tablename as p6216_tablename, g1062x1_.p6237 as p6237, g1062x1_.p20476 as p20476 from e1062 g1062x1_) g1062x1_ ) )",
				walker.getAST().toStringTree());

		toParse = "select virage from CAGS,HUDSON where CAGS.Size = HUDSON.Size";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1062x1_.p6237 ) ( FromClause{level=1} (select e1067.rid as rid, 'e1067' as tablename, e1067.p1204 as p1204, e1067.p1202 as p1202, NULL::varchar  as p20476 from e1067 union all select e1023.rid as rid, 'e1023' as tablename, e1023.p1204 as p1204, e1023.p1202 as p1202, NULL::varchar  as p20476 from e1023 union all select e1062.rid as rid, 'e1062' as tablename, e1062.p1204 as p1204, e1062.p1202 as p1202, e1062.p20476 as p20476 from e1062 union all select e1063.rid as rid, 'e1063' as tablename, e1063.p1204 as p1204, e1063.p1202 as p1202, NULL::varchar  as p20476 from e1063 union all select e1061.rid as rid, 'e1061' as tablename, e1061.p1204 as p1204, e1061.p1202 as p1202, NULL::varchar  as p20476 from e1061 union all select e1060.rid as rid, 'e1060' as tablename, e1060.p1204 as p1204, e1060.p1202 as p1202, NULL::varchar  as p20476 from e1060) g1068x0_ (select g1062x1_.rid as rid, 'e1062' as tablename, g1062x1_.p1204 as p1204, g1062x1_.p1202 as p1202, g1062x1_.p6216_rid as p6216_rid, g1062x1_.p6216_tablename as p6216_tablename, g1062x1_.p6237 as p6237, g1062x1_.p20476 as p20476 from e1062 g1062x1_) g1062x1_ ) ( where ( = g1068x0_.p1202 g1062x1_.p1202 ) ) )",
				walker.getAST().toStringTree());

		toParse = "select virage from HUDSON where Size IN (SELECT CAGS.Size FROM CAGS)";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1062x0_.p6237 ) ( FromClause{level=1} (select g1062x0_.rid as rid, 'e1062' as tablename, g1062x0_.p1204 as p1204, g1062x0_.p1202 as p1202, g1062x0_.p6216_rid as p6216_rid, g1062x0_.p6216_tablename as p6216_tablename, g1062x0_.p6237 as p6237, g1062x0_.p20476 as p20476 from e1062 g1062x0_) g1062x0_ ) ( where ( in g1062x0_.p1202 ( inList ( SELECT ( {select clause} g1068x1_.p1202 ) ( FromClause{level=2} (select e1067.rid as rid, 'e1067' as tablename, e1067.p1204 as p1204, e1067.p1202 as p1202, NULL::varchar  as p20476 from e1067 union all select e1023.rid as rid, 'e1023' as tablename, e1023.p1204 as p1204, e1023.p1202 as p1202, NULL::varchar  as p20476 from e1023 union all select e1062.rid as rid, 'e1062' as tablename, e1062.p1204 as p1204, e1062.p1202 as p1202, e1062.p20476 as p20476 from e1062 union all select e1063.rid as rid, 'e1063' as tablename, e1063.p1204 as p1204, e1063.p1202 as p1202, NULL::varchar  as p20476 from e1063 union all select e1061.rid as rid, 'e1061' as tablename, e1061.p1204 as p1204, e1061.p1202 as p1202, NULL::varchar  as p20476 from e1061 union all select e1060.rid as rid, 'e1060' as tablename, e1060.p1204 as p1204, e1060.p1202 as p1202, NULL::varchar  as p20476 from e1060) g1068x1_ ) ) ) ) ) )",
				walker.getAST().toStringTree());

		toParse = "select virage from HUDSON where HUDSON.Size IN (SELECT CAGS.Size FROM CAGS WHERE HUDSON.Reference > CAGS.Reference)";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1062x0_.p6237 ) ( FromClause{level=1} (select g1062x0_.rid as rid, 'e1062' as tablename, g1062x0_.p1204 as p1204, g1062x0_.p1202 as p1202, g1062x0_.p6216_rid as p6216_rid, g1062x0_.p6216_tablename as p6216_tablename, g1062x0_.p6237 as p6237, g1062x0_.p20476 as p20476 from e1062 g1062x0_) g1062x0_ ) ( where ( in g1062x0_.p1202 ( inList ( SELECT ( {select clause} g1068x1_.p1202 ) ( FromClause{level=2} (select e1067.rid as rid, 'e1067' as tablename, e1067.p1204 as p1204, e1067.p1202 as p1202, NULL::varchar  as p20476 from e1067 union all select e1023.rid as rid, 'e1023' as tablename, e1023.p1204 as p1204, e1023.p1202 as p1202, NULL::varchar  as p20476 from e1023 union all select e1062.rid as rid, 'e1062' as tablename, e1062.p1204 as p1204, e1062.p1202 as p1202, e1062.p20476 as p20476 from e1062 union all select e1063.rid as rid, 'e1063' as tablename, e1063.p1204 as p1204, e1063.p1202 as p1202, NULL::varchar  as p20476 from e1063 union all select e1061.rid as rid, 'e1061' as tablename, e1061.p1204 as p1204, e1061.p1202 as p1202, NULL::varchar  as p20476 from e1061 union all select e1060.rid as rid, 'e1060' as tablename, e1060.p1204 as p1204, e1060.p1202 as p1202, NULL::varchar  as p20476 from e1060) g1068x1_ ) ( WHERE ( > g1062x0_.p1204 g1068x1_.p1204 ) ) ) ) ) ) )",
				walker.getAST().toStringTree());

		s.close();
	}

	@Test
	public void testPathExpression() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String toParse = "SELECT its_muff.its_slalom.Reference from only(HUDSON)";
		OntoQLParser parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		AST ast = parser.getAST();
		log.warn(ast.toStringTree());

		OntoQLSQLWalker walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1097x2_.p1204 ) ( FromClause{level=1} ( e1062 g1062x0_ LEFT OUTER JOIN ( (select g1105x1_.rid as rid, 'e1105' as tablename, g1105x1_.p1204 as p1204, g1105x1_.p6226_rid as p6226_rid, g1105x1_.p6226_tablename as p6226_tablename, g1105x1_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x1_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x1_ LEFT OUTER JOIN (select g1097x2_.rid as rid, 'e1097' as tablename, g1097x2_.p1204 as p1204, NULL  as NULL , g1097x2_.p1212 as p1212 from e1097 g1097x2_) g1097x2_ ON g1105x1_.p6226_rid = g1097x2_.rid ) ON g1062x0_.p6216_rid = g1105x1_.rid ) ) )",
				walker.getAST().toStringTree());

		toParse = "SELECT Reference from only(HUDSON) where its_muff.its_slalom.Reference like '3006%'";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1062x0_.p1204 ) ( FromClause{level=1} ( e1062 g1062x0_ LEFT OUTER JOIN ( (select g1105x1_.rid as rid, 'e1105' as tablename, g1105x1_.p1204 as p1204, g1105x1_.p6226_rid as p6226_rid, g1105x1_.p6226_tablename as p6226_tablename, g1105x1_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x1_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x1_ LEFT OUTER JOIN (select g1097x2_.rid as rid, 'e1097' as tablename, g1097x2_.p1204 as p1204, NULL  as NULL , g1097x2_.p1212 as p1212 from e1097 g1097x2_) g1097x2_ ON g1105x1_.p6226_rid = g1097x2_.rid ) ON g1062x0_.p6216_rid = g1105x1_.rid ) ) ( where ( like g1097x2_.p1204 '3006%' ) ) )",
				walker.getAST().toStringTree());

		toParse = "SELECT h1.its_muff.its_slalom.Reference,h2.its_muff.Reference from only(HUDSON) h1, only(HUDSON) h2";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1097x3_.p1204 g1105x4_.p1204 ) ( FromClause{level=1} ( e1062 g1062x0_ LEFT OUTER JOIN ( (select g1105x2_.rid as rid, 'e1105' as tablename, g1105x2_.p1204 as p1204, g1105x2_.p6226_rid as p6226_rid, g1105x2_.p6226_tablename as p6226_tablename, g1105x2_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x2_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x2_ LEFT OUTER JOIN (select g1097x3_.rid as rid, 'e1097' as tablename, g1097x3_.p1204 as p1204, NULL  as NULL , g1097x3_.p1212 as p1212 from e1097 g1097x3_) g1097x3_ ON g1105x2_.p6226_rid = g1097x3_.rid ) ON g1062x0_.p6216_rid = g1105x2_.rid ) ( e1062 g1062x1_ LEFT OUTER JOIN (select g1105x4_.rid as rid, 'e1105' as tablename, g1105x4_.p1204 as p1204, g1105x4_.p6226_rid as p6226_rid, g1105x4_.p6226_tablename as p6226_tablename, g1105x4_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x4_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x4_ ON g1062x1_.p6216_rid = g1105x4_.rid ) ) )",
				walker.getAST().toStringTree());

		toParse = "SELECT h1.its_muff.its_slalom.Reference,h2.its_muff.Reference from only(HUDSON) h1, only(HUDSON) h2 where  h1.its_muff.its_slalom.Reference > h2.its_muff.Reference or (h2.its_muff.Reference is null)";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1097x3_.p1204 g1105x4_.p1204 ) ( FromClause{level=1} ( e1062 g1062x0_ LEFT OUTER JOIN ( (select g1105x2_.rid as rid, 'e1105' as tablename, g1105x2_.p1204 as p1204, g1105x2_.p6226_rid as p6226_rid, g1105x2_.p6226_tablename as p6226_tablename, g1105x2_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x2_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x2_ LEFT OUTER JOIN (select g1097x3_.rid as rid, 'e1097' as tablename, g1097x3_.p1204 as p1204, NULL  as NULL , g1097x3_.p1212 as p1212 from e1097 g1097x3_) g1097x3_ ON g1105x2_.p6226_rid = g1097x3_.rid ) ON g1062x0_.p6216_rid = g1105x2_.rid ) ( e1062 g1062x1_ LEFT OUTER JOIN (select g1105x4_.rid as rid, 'e1105' as tablename, g1105x4_.p1204 as p1204, g1105x4_.p6226_rid as p6226_rid, g1105x4_.p6226_tablename as p6226_tablename, g1105x4_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x4_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x4_ ON g1062x1_.p6216_rid = g1105x4_.rid ) ) ( where ( or ( > g1097x3_.p1204 g1105x4_.p1204 ) ( is null g1105x4_.p1204 ) ) ) )",
				walker.getAST().toStringTree());

		toParse = "SELECT h1.its_muff.its_slalom.Reference,h2.its_muff.Reference from HUDSON h1, HUDSON h2 where h1.oid = h2.oid and h2.its_muff.its_slalom.Reference = 'XL'";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		walker.showAst(walker.getAST(), new PrintStream(baos));
		System.out.println(baos.toString());

		s.close();

	}

	@Test
	public void testPolymorphicPathExpression() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String toParse = "SELECT its_muff.Reference from HUDSON";
		OntoQLParser parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		AST ast = parser.getAST();
		log.warn(ast.toStringTree());
		OntoQLSQLWalker walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1105x1_.p1204 ) ( FromClause{level=1} ( (select g1062x0_.rid as rid, 'e1062' as tablename, g1062x0_.p1204 as p1204, g1062x0_.p1202 as p1202, g1062x0_.p6216_rid as p6216_rid, g1062x0_.p6216_tablename as p6216_tablename, g1062x0_.p6237 as p6237, g1062x0_.p20476 as p20476 from e1062 g1062x0_) g1062x0_ LEFT OUTER JOIN (select g1105x1_.rid as rid, 'e1105' as tablename, g1105x1_.p1204 as p1204, g1105x1_.p6226_rid as p6226_rid, g1105x1_.p6226_tablename as p6226_tablename, g1105x1_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x1_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x1_ ON g1062x0_.p6216_rid = g1105x1_.rid ) ) )",
				walker.getAST().toStringTree());

		toParse = "SELECT h1.its_muff.its_slalom.Reference,h2.its_muff.Reference from HUDSON h1, HUDSON h2";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1097x3_.p1204 g1105x4_.p1204 ) ( FromClause{level=1} ( (select g1062x0_.rid as rid, 'e1062' as tablename, g1062x0_.p1204 as p1204, g1062x0_.p1202 as p1202, g1062x0_.p6216_rid as p6216_rid, g1062x0_.p6216_tablename as p6216_tablename, g1062x0_.p6237 as p6237, g1062x0_.p20476 as p20476 from e1062 g1062x0_) g1062x0_ LEFT OUTER JOIN ( (select g1105x2_.rid as rid, 'e1105' as tablename, g1105x2_.p1204 as p1204, g1105x2_.p6226_rid as p6226_rid, g1105x2_.p6226_tablename as p6226_tablename, g1105x2_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x2_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x2_ LEFT OUTER JOIN (select g1097x3_.rid as rid, 'e1097' as tablename, g1097x3_.p1204 as p1204, NULL  as NULL , g1097x3_.p1212 as p1212 from e1097 g1097x3_) g1097x3_ ON g1105x2_.p6226_rid = g1097x3_.rid ) ON g1062x0_.p6216_rid = g1105x2_.rid ) ( (select g1062x1_.rid as rid, 'e1062' as tablename, g1062x1_.p1204 as p1204, g1062x1_.p1202 as p1202, g1062x1_.p6216_rid as p6216_rid, g1062x1_.p6216_tablename as p6216_tablename, g1062x1_.p6237 as p6237, g1062x1_.p20476 as p20476 from e1062 g1062x1_) g1062x1_ LEFT OUTER JOIN (select g1105x4_.rid as rid, 'e1105' as tablename, g1105x4_.p1204 as p1204, g1105x4_.p6226_rid as p6226_rid, g1105x4_.p6226_tablename as p6226_tablename, g1105x4_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x4_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x4_ ON g1062x1_.p6216_rid = g1105x4_.rid ) ) )",
				walker.getAST().toStringTree());

		s.close();

	}

	@Test
	public void testPrefix() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage("en");

		String toParse = "select HUDSON.Reference from only(HUDSON)";
		System.out.println("input: ." + toParse + "<-");
		OntoQLParser parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		AST ast = parser.getAST();

		log.warn(ast.toStringTree());
		OntoQLSQLWalker walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(walker.getAST().toStringTree(),
				" ( SELECT ( {select clause} g1062x0_" + ".p" + pReference.getInternalId()
						+ " ) ( FromClause{level=1} e" + cHudson.getInternalId() + " g1062x0_ ) )");

		toParse = "select Reference, Size, virage, its_muff, its_muff.Reference from only(HUDSON)";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1062x0_.p1204 g1062x0_.p1202 g1062x0_.p6237 g1105x1_.rid g1105x1_.tablename g1105x1_.p6226_rid g1105x1_.p6226_tablename g1105x1_.p1202 g1105x1_.p1204 NULL::float8  NULL  NULL  NULL::float8  g1105x1_.p20476 g1105x1_.p1204 ) ( FromClause{level=1} ( e1062 g1062x0_ LEFT OUTER JOIN (select g1105x1_.rid as rid, 'e1105' as tablename, g1105x1_.p1204 as p1204, g1105x1_.p6226_rid as p6226_rid, g1105x1_.p6226_tablename as p6226_tablename, g1105x1_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x1_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x1_ ON g1062x0_.p6216_rid = g1105x1_.rid ) ) )",
				walker.getAST().toStringTree());

		toParse = "select h1.Reference, h2.Reference from only(HUDSON) h1,only(HUDSON) h2 where h1.Reference = h2.Reference";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1062x0_.p1204 g1062x1_.p1204 ) ( FromClause{level=1} e1062 g1062x0_ e1062 g1062x1_ ) ( where ( = g1062x0_.p1204 g1062x1_.p1204 ) ) )",
				walker.getAST().toStringTree());

		s.close();
	}

	@Test
	public void testGeneratedTreeNameId() throws Exception {
		OntoQLSession s = getSession();

		// The reference language is not set
		// So, the query must fail
		s.setReferenceLanguage(null);
		String toParse = "SELECT Reference FROM HUDSON";
		System.out.println("input: ." + toParse + "<-");

		OntoQLParser parser = OntoQLParser.getInstance(toParse);
		AST ast = null;
		OntoQLSQLWalker walker = new OntoQLSQLWalker(s);
		try {
			parser.statement();
			ast = parser.getAST();
			walker.statement(ast);
			walker.getParseErrorHandler().throwQueryException();
			Assert.fail();
		} catch (JOBDBCException exc) {
		}

		s.setReferenceLanguage("en");
		toParse = "SELECT Reference FROM only(HUDSON)";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(walker.getAST().toStringTree(),
				" ( SELECT ( {select clause} g1062x0_" + ".p" + pReference.getInternalId()
						+ " ) ( FromClause{level=1} e" + cHudson.getInternalId() + " g1062x0_ ) )");

		toParse = "SELECT Reference FROM only(\"ANCHORAGE (KAYAK PANTS)\")";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(walker.getAST().toStringTree(),
				" ( SELECT ( {select clause} g1060x0_" + ".p" + pReference.getInternalId()
						+ " ) ( FromClause{level=1} e" + cAnchorage.getInternalId() + " g1060x0_ ) )");

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(walker.getAST().toStringTree(),
				" ( SELECT ( {select clause} g1060x0_" + ".p" + pReference.getInternalId()
						+ " ) ( FromClause{level=1} e" + cAnchorage.getInternalId() + " g1060x0_ ) )");

		toParse = "SELECT Reference FROM \"ANCHORAGE (KAYAK PANTS)\"";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1060x0_.p1204 ) ( FromClause{level=1} (select g1060x0_.rid as rid, 'e1060' as tablename, g1060x0_.p1204 as p1204, g1060x0_.p1202 as p1202 from e1060 g1060x0_) g1060x0_ ) )",
				walker.getAST().toStringTree());

		toParse = "SELECT Reference, Buoyancy, \"Buoyancy RAFT\", Size FROM only(\"PERSONAL EQUIPEMENT/SAFETY\") ";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} NULL::varchar  NULL::float8  NULL::float8  NULL::varchar  ) ( FromClause{level=1} (select 0 where false) g1040x0_ ) )",
				walker.getAST().toStringTree());

		toParse = "SELECT \"Chest measurement\" FROM only(\"PERSONAL EQUIPEMENT/SAFETY\")";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} NULL  ) ( FromClause{level=1} (select 0 where false) g1040x0_ ) )",
				walker.getAST().toStringTree());

		// Expression de chemin
		toParse = "SELECT h.its_muff.Reference FROM only(HUDSON) h";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		ASTPrinter printer = new ASTPrinter(SQLTokenTypes.class);
		log.warn(printer.showAsString(ast, "--- OntoQL AST ---"));

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1105x1_.p1204 ) ( FromClause{level=1} ( e1062 g1062x0_ LEFT OUTER JOIN (select g1105x1_.rid as rid, 'e1105' as tablename, g1105x1_.p1204 as p1204, g1105x1_.p6226_rid as p6226_rid, g1105x1_.p6226_tablename as p6226_tablename, g1105x1_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x1_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x1_ ON g1062x0_.p6216_rid = g1105x1_.rid ) ) )",
				walker.getAST().toStringTree());

		s.close();

	}

	@Test
	public void testCount() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String toParse = "SELECT count(*) from CAGS";
		System.out.println("input: ." + toParse + "<-");
		OntoQLParser parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		AST ast = parser.getAST();

		OntoQLSQLWalker walker = new OntoQLSQLWalker(s);
		walker.statement(ast);

		Assert.assertEquals(
				" ( SELECT ( {select clause} ( count * ) ) ( FromClause{level=1} (select e1067.rid as rid, 'e1067' as tablename, e1067.p1204 as p1204, e1067.p1202 as p1202, NULL::varchar  as p20476 from e1067 union all select e1023.rid as rid, 'e1023' as tablename, e1023.p1204 as p1204, e1023.p1202 as p1202, NULL::varchar  as p20476 from e1023 union all select e1062.rid as rid, 'e1062' as tablename, e1062.p1204 as p1204, e1062.p1202 as p1202, e1062.p20476 as p20476 from e1062 union all select e1063.rid as rid, 'e1063' as tablename, e1063.p1204 as p1204, e1063.p1202 as p1202, NULL::varchar  as p20476 from e1063 union all select e1061.rid as rid, 'e1061' as tablename, e1061.p1204 as p1204, e1061.p1202 as p1202, NULL::varchar  as p20476 from e1061 union all select e1060.rid as rid, 'e1060' as tablename, e1060.p1204 as p1204, e1060.p1202 as p1202, NULL::varchar  as p20476 from e1060) g1068x0_ ) )",
				walker.getAST().toStringTree());

		toParse = "SELECT count(*) from only(CAGS)";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();

		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);

		Assert.assertEquals(
				" ( SELECT ( {select clause} ( count * ) ) ( FromClause{level=1} (select 0 where false) g1068x0_ ) )",
				walker.getAST().toStringTree());

		s.close();

	}

	@Test
	public void testGeneratedTreeInternalId() throws Exception {
		OntoQLSession s = getSession();

		// Une propriété, Une classe
		String toParse = "SELECT !" + pReference.getInternalId() + " FROM only(!" + cHudson.getInternalId() + ")";
		System.out.println("input: ." + toParse + "<-");
		OntoQLParser parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		AST ast = parser.getAST();

		OntoQLSQLWalker walker = new OntoQLSQLWalker(s);
		walker.statement(ast);

		ASTPrinter printer = new ASTPrinter(SQLTokenTypes.class);
		log.info(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		Assert.assertEquals(walker.getAST().toStringTree(),
				" ( SELECT ( {select clause} g1062x0_" + ".p" + pReference.getInternalId()
						+ " ) ( FromClause{level=1} e" + cHudson.getInternalId() + " g1062x0_ ) )");

		// Plusieurs propriétés
		toParse = "SELECT !" + pSize.getInternalId() + ", !" + pReference.getInternalId() + " FROM only(!"
				+ cHudson.getInternalId() + ")";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL : " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.info(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		Assert.assertEquals(walker.getAST().toStringTree(),
				" ( SELECT ( {select clause} g1062x0_" + ".p" + pSize.getInternalId() + " g1062x0_" + ".p"
						+ pReference.getInternalId() + " ) ( FromClause{level=1} e" + cHudson.getInternalId()
						+ " g1062x0_ ) )");

		// Des propriétés ne sont pas utilisés
		toParse = "SELECT !" + pSize.getInternalId() + ", !" + pReference.getInternalId() + " FROM only(!"
				+ cDurance.getInternalId() + ")";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL : " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.info(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		Assert.assertEquals(
				" ( SELECT ( {select clause} NULL::varchar  g1064x0_.p1204 ) ( FromClause{level=1} e1064 g1064x0_ ) )",
				walker.getAST().toStringTree());

		// Des propriétés ne sont pas définies sur la classe

		toParse = "SELECT !" + pSize.getInternalId() + ", !" + pVirage.getInternalId() + " FROM only(!"
				+ cPersonnalEquipementSafety.getInternalId() + ")";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL : " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		try {
			walker.statement(ast);
			walker.getParseErrorHandler().throwQueryException();
			Assert.fail();
		} catch (QueryException exc) {
		}

		// La classe n'a pas d'extension
		toParse = "SELECT !" + pReference.getInternalId() + " FROM only(!" + cCags.getInternalId() + ")";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL : " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.info(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		Assert.assertEquals(
				" ( SELECT ( {select clause} NULL::varchar  ) ( FromClause{level=1} (select 0 where false) g1068x0_ ) )",
				walker.getAST().toStringTree());

		// Requéte polymorphe
		toParse = "SELECT !" + pReference.getInternalId() + " FROM !" + cHelmets.getInternalId();
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL : " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1066x0_.p1204 ) ( FromClause{level=1} (select e1065.rid as rid, 'e1065' as tablename, e1065.p1204 as p1204, NULL  as NULL , e1065.p1212 as p1212 from e1065 union all select e1064.rid as rid, 'e1064' as tablename, e1064.p1204 as p1204, NULL  as NULL , e1064.p1212 as p1212 from e1064 union all select e1097.rid as rid, 'e1097' as tablename, e1097.p1204 as p1204, NULL  as NULL , e1097.p1212 as p1212 from e1097) g1066x0_ ) )",
				walker.getAST().toStringTree());

		// Filter condition
		toParse = "SELECT !" + pReference.getInternalId() + " FROM only(!" + cHudson.getInternalId() + ") Where !"
				+ pReference.getInternalId() + " like '3%'";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL : " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.info(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		Assert.assertEquals(walker.getAST().toStringTree(),
				" ( SELECT ( {select clause} g1062x0_" + ".p" + pReference.getInternalId()
						+ " ) ( FromClause{level=1} e" + cHudson.getInternalId() + " g1062x0_ ) ( Where ( like g1062x0_"
						+ ".p" + pReference.getInternalId() + " '3%' ) ) )");

		toParse = "SELECT count(!" + pReference.getInternalId() + ") FROM only(!" + cHudson.getInternalId()
				+ ") Where !" + pReference.getInternalId() + " like '3%' group by !" + pReference.getInternalId()
				+ " having !" + pSize.getInternalId() + " = 'Jaune' order by !" + pReference.getInternalId() + " asc";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL : " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.info(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		Assert.assertEquals(
				" ( SELECT ( {select clause} ( count g1062x0_.p1204 ) ) ( FromClause{level=1} e1062 g1062x0_ ) ( Where ( like g1062x0_.p1204 '3%' ) ) ( group g1062x0_.p1204 ( having ( = g1062x0_.p1202 'Jaune' ) ) ) ( order g1062x0_.p1204 asc ) )",
				walker.getAST().toStringTree());

		// Distinct
		toParse = "SELECT distinCT !" + pReference.getInternalId() + " FROM only(!" + cHudson.getInternalId()
				+ ") Where !" + pReference.getInternalId() + " like '3%'";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL : " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.info(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		Assert.assertEquals(" ( SELECT ( {select clause} distinCT g1062x0_" + ".p" + pReference.getInternalId()
				+ " ) ( FromClause{level=1} e" + cHudson.getInternalId() + " g1062x0_ ) ( Where ( like g1062x0_" + ".p"
				+ pReference.getInternalId() + " '3%' ) ) )", walker.getAST().toStringTree());

		// OR - AND condition
		toParse = "SELECT !" + pReference.getInternalId() + " FROM only(!" + cHudson.getInternalId() + ") WHERE (!"
				+ pReference.getInternalId() + " = 'TOTO') OR ( (!" + pSize.getInternalId() + " = 'JAUNE') AND (!"
				+ pReference.getInternalId() + " > '3') )";
		System.out.println("input: ." + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL : " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.info(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		Assert.assertEquals(walker.getAST().toStringTree(),
				" ( SELECT ( {select clause} g1062x0_" + ".p" + pReference.getInternalId()
						+ " ) ( FromClause{level=1} e" + cHudson.getInternalId()
						+ " g1062x0_ ) ( WHERE ( OR ( = g1062x0_" + ".p" + pReference.getInternalId()
						+ " 'TOTO' ) ( AND ( = g1062x0_" + ".p" + pSize.getInternalId() + " 'JAUNE' ) ( > g1062x0_"
						+ ".p" + pReference.getInternalId() + " '3' ) ) ) ) )");

		// Path expression
		toParse = "SELECT !" + pItsMuff.getInternalId() + " FROM only(!" + cHudson.getInternalId() + ") WHERE (!"
				+ pReference.getInternalId() + " = 'TOTO') OR ( (!" + pSize.getInternalId() + " = 'JAUNE') AND (!"
				+ pReference.getInternalId() + " > '3') )";
		System.out.println("input: ->" + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		System.out.println("OntoQL : " + ast.toStringTree() + "");
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.info(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		Assert.assertEquals(
				" ( SELECT ( {select clause} g1105x1_.rid g1105x1_.tablename g1105x1_.p6226_rid g1105x1_.p6226_tablename g1105x1_.p1202 g1105x1_.p1204 NULL::float8  NULL  NULL  NULL::float8  g1105x1_.p20476 ) ( FromClause{level=1} ( e1062 g1062x0_ LEFT OUTER JOIN (select g1105x1_.rid as rid, 'e1105' as tablename, g1105x1_.p1204 as p1204, g1105x1_.p6226_rid as p6226_rid, g1105x1_.p6226_tablename as p6226_tablename, g1105x1_.p20476 as p20476, NULL::varchar  as p1202 from e1105 g1105x1_ union all select e1104.rid as rid, 'e1104' as tablename, e1104.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1104.p20476 as p20476, e1104.p1202 as p1202 from e1104 union all select e1103.rid as rid, 'e1103' as tablename, e1103.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1103.p20476 as p20476, e1103.p1202 as p1202 from e1103 union all select e1102.rid as rid, 'e1102' as tablename, e1102.p1204 as p1204, NULL::int8  as p6226_rid, NULL::varchar  as p6226_tablename, e1102.p20476 as p20476, e1102.p1202 as p1202 from e1102) g1105x1_ ON g1062x0_.p6216_rid = g1105x1_.rid ) ) ( WHERE ( OR ( = g1062x0_.p1204 'TOTO' ) ( AND ( = g1062x0_.p1202 'JAUNE' ) ( > g1062x0_.p1204 '3' ) ) ) ) )",
				walker.getAST().toStringTree());

		s.setReferenceLanguage(OntoQLHelper.ENGLISH);
		toParse = "Insert into !1031 (Reference,Size, names) values ('300', 'XL', ARRAY['baby1','baby2'])";
		System.out.println("input: ->" + toParse + "<-");
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.info(printer.showAsString(ast, "--- OntoQL AST ---"));
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		log.info(printer.showAsString(walker.getAST(), "--- SQL AST ---"));
		Assert.assertEquals(
				" ( Insert ( into e1031 ( property-spec version_min p1204 p1202 p6250 ) ) ( values 1 '300' 'XL' ( ARRAY 'baby1' 'baby2' ) ) )",
				walker.getAST().toStringTree());

		s.close();
	}

	@Test
	public void testAddSelectExpr() throws Exception {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);

		String toParse = "select Size, 1 from CAGS";

		OntoQLParser parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		AST ast = parser.getAST();
		log.warn(ast.toStringTree());

		OntoQLSQLWalker walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		List selectExpressions = walker.getExpressionInSelect();
		Assert.assertEquals(selectExpressions.size(), 2);
		SelectExpression selectExpression = (SelectExpression) selectExpressions.get(0);
		Assert.assertEquals(selectExpression.getLabel(), "Size");
		Assert.assertEquals(selectExpression.getDataType().getName(), "NON_QUANTITATIVE_CODE_TYPE");
		selectExpression = (SelectExpression) selectExpressions.get(1);
		Assert.assertEquals(selectExpression.getLabel(), "1");
		Assert.assertEquals(selectExpression.getDataType().getName(), "INT");

		toParse = "select virage+virage, its_muff.Reference from HUDSON";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		selectExpressions = walker.getExpressionInSelect();
		Assert.assertEquals(selectExpressions.size(), 2);
		selectExpression = (SelectExpression) selectExpressions.get(0);
		Assert.assertEquals(selectExpression.getLabel(), "virage + virage");
		Assert.assertEquals(selectExpression.getDataType().getName(), "INT");
		selectExpression = (SelectExpression) selectExpressions.get(1);
		Assert.assertEquals(selectExpression.getLabel(), "Reference");
		Assert.assertEquals(selectExpression.getDataType().getName(), "STRING");

		toParse = "select count(*), sum(virage) from HUDSON";
		parser = OntoQLParser.getInstance(toParse);
		parser.statement();
		ast = parser.getAST();
		log.warn(ast.toStringTree());
		walker = new OntoQLSQLWalker(s);
		walker.statement(ast);
		selectExpressions = walker.getExpressionInSelect();
		Assert.assertEquals(selectExpressions.size(), 2);
		selectExpression = (SelectExpression) selectExpressions.get(0);
		Assert.assertEquals(selectExpression.getLabel(), "count");
		Assert.assertEquals(selectExpression.getDataType().getName(), "INT");
		selectExpression = (SelectExpression) selectExpressions.get(1);
		Assert.assertEquals(selectExpression.getLabel(), "sum");
		Assert.assertEquals(selectExpression.getDataType().getName(), "INT");

		s.close();
	}
}