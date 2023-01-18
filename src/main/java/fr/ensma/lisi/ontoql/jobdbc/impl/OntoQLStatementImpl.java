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
package fr.ensma.lisi.ontoql.jobdbc.impl;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.QueryException;

import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.AbstractFactoryEntityDB;
import fr.ensma.lisi.ontoql.core.ontodb.FactoryEntityOntoDB;
import fr.ensma.lisi.ontoql.engine.OntoQLParser;
import fr.ensma.lisi.ontoql.engine.OntoQLSQLWalker;
import fr.ensma.lisi.ontoql.engine.SPARQLOntoQLWalker;
import fr.ensma.lisi.ontoql.engine.SPARQLParser;
import fr.ensma.lisi.ontoql.engine.SQLGenerator;
import fr.ensma.lisi.ontoql.exception.JDBCExceptionHelper;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.SQLExceptionConverterFactory;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;

/**
 * The object used for executing an OntoQL statement or an QueryOntoQL object
 * and returning the results it produces.
 * 
 * @author Stéphane JEAN
 */
public class OntoQLStatementImpl implements OntoQLStatement {

	/**
	 * A logger for this class.
	 */
	private static Log log = LogFactory.getLog(OntoQLStatement.class);

	/**
	 * The SQL query executed.
	 */
	private String sqlString = "";

	/**
	 * A reference to the factory creator of this object Enable to create other
	 * entity of the same kind.
	 */
	private AbstractFactoryEntityDB factoryEntity;

	/**
	 * @return the SQL query executed
	 */
	public final String getSQLString() {
		return sqlString;
	}

	/** session to access the database. */
	private OntoQLSession session;

	/**
	 * Constructor with a session.
	 * 
	 * @param session session to access the database
	 */
	public OntoQLStatementImpl(final OntoQLSession session) {
		this.session = session;
		factoryEntity = new FactoryEntityOntoDB(session);
	}

	/**
	 * Executes the given OntoQL statement, which returns a single
	 * <code>ResultSet</code> object.
	 * 
	 * @param ontoql an OntoQL statement to be sent to the session
	 * @return a ResultSet object that contains the data produced by the given
	 *         query; never null
	 * @throws JOBDBCException if a database access error occurs or the given OntoQL
	 *                         statement produces anything other than a single
	 *                         ResultSet object
	 */
	@SuppressWarnings("deprecation")
	public final OntoQLResultSet executeQuery(final String ontoql) throws JOBDBCException {

		OntoQLParser parser = OntoQLParser.getInstance(ontoql);
		log.debug("parsing of the OntoQL query: " + ontoql + " - language: " + session.getReferenceLanguage());

		String sql = null;
		try {
			parser.statement();
			parser.getParseErrorHandler().throwQueryException();
			AST ast = parser.getAST();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			parser.showAst(ast, new PrintStream(baos));
			log.debug("OntoAlgebra Tree : \n" + baos.toString());

			OntoQLSQLWalker w = new OntoQLSQLWalker(this.session);
			w.statement(ast);
			if (w.getParseErrorHandler().getErrorCount() > 0) {
				Statement stmt = session.connection().createStatement();
				try {
					ResultSet resultsetQuerySQL = stmt.executeQuery(ontoql);
					return new OntoQLResultSetImpl(resultsetQuerySQL, new ArrayList(), factoryEntity, session);
				} catch (SQLException e) {
					session.connection().rollback();
					stmt.close();
					w.getParseErrorHandler().throwQueryException();
				}
			}

			List propertiesInSelect = w.getExpressionInSelect();

			baos = new ByteArrayOutputStream();
			AST sqlAst = w.getAST();
			w.showAst(sqlAst, new PrintStream(baos));
			log.debug("SQL AST : " + baos.toString());

			SQLGenerator gen = new SQLGenerator(this.session);
			gen.statement(sqlAst);
			sql = gen.getSQL();
			gen.getParseErrorHandler().throwQueryException();

			log.debug("generated SQL : " + sql);
			sqlString = sql;

			Statement st = session.connection().createStatement();
			ResultSet resultsetQuerySQL = st.executeQuery(sql);

			return new OntoQLResultSetImpl(resultsetQuerySQL, propertiesInSelect, factoryEntity, session);
		} catch (QueryException qe) {
			throw qe;
		} catch (TokenStreamException exc) {
			throw new JOBDBCException(exc);
		} catch (RecognitionException exc) {
			throw new JOBDBCException(exc);
		} catch (SQLException sqle) {
			throw JDBCExceptionHelper.convert(SQLExceptionConverterFactory.buildMinimalSQLExceptionConverter(), sqle,
					sqle.getMessage(), sql);
		}

	}

	/**
	 * Executes the given OntoQL statement, which may be an INSERT, UPDATE, or
	 * DELETE statement or an OntoQL statement that returns nothing, such as an
	 * OntoQL DDL statement.
	 * 
	 * @param ontoql an OntoQL DML or DDL statement
	 * @return either the row count for DML statements, or 0 for DDL statements
	 * @throws JOBDBCException if a database access error occurs
	 */
	@SuppressWarnings("deprecation")
	public final int executeUpdate(final String ontoql) throws JOBDBCException {

		int res = 0;

		OntoQLParser parser = OntoQLParser.getInstance(ontoql);
		log.warn("parse() - OntoQL: " + ontoql);
		log.warn("parse() - ReferenceLanguage: " + session.getReferenceLanguage());
		String sql = null;
		try {
			parser.statement();
			parser.getParseErrorHandler().throwQueryException();
			AST ontoQLAst = parser.getAST();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			parser.showAst(ontoQLAst, new PrintStream(baos));
			log.warn("OntoQL AST : " + baos.toString());

			OntoQLSQLWalker w = new OntoQLSQLWalker(this.session);
			w.statement(ontoQLAst);
			w.getParseErrorHandler().throwQueryException();

			if (w.isDMLStatement()) {

				AST sqlAst = w.getAST();
				baos = new ByteArrayOutputStream();
				w.showAst(w.getAST(), new PrintStream(baos));
				log.warn("SQL AST : " + baos.toString());

				SQLGenerator gen = new SQLGenerator(this.session);
				gen.statement(sqlAst);
				sql = gen.getSQL();
				log.warn("SQL GEN  :  " + sql);

				Statement st = session.connection().createStatement();
				res = st.executeUpdate(sql);
			}
		} catch (QueryException qe) {
			throw qe;
		} catch (TokenStreamException exc) {
			throw new JOBDBCException(exc);
		} catch (RecognitionException exc) {
			throw new JOBDBCException(exc);
		} catch (SQLException sqle) {
			throw JDBCExceptionHelper.convert(SQLExceptionConverterFactory.buildMinimalSQLExceptionConverter(), sqle,
					sqle.getMessage(), sql);
		}

		return res;
	}

	@SuppressWarnings("deprecation")
	@Override
	public OntoQLResultSet executeSPARQLQuery(String sparqlQuery) throws JOBDBCException {

		SPARQLParser parser = SPARQLParser.getInstance(sparqlQuery);
		log.warn("parse() - SPARQL: " + sparqlQuery);
		String sql = null;
		try {
			parser.query();
			parser.getParseErrorHandler().throwQueryException();
			AST sparqlAst = parser.getAST();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			parser.showAst(sparqlAst, new PrintStream(baos));
			log.warn("SPARQL AST : \n" + baos.toString());
			log.warn(sparqlAst.toStringTree());

			SPARQLOntoQLWalker wSPARQL = new SPARQLOntoQLWalker(this.session);
			wSPARQL.unionSparqlQuery(sparqlAst);
			baos = new ByteArrayOutputStream();
			AST ontoqlAst = wSPARQL.getAST();
			wSPARQL.showAst(ontoqlAst, new PrintStream(baos));
			log.warn("OntoQL AST : " + baos.toString());

			OntoQLSQLWalker w = new OntoQLSQLWalker(this.session);
			w.statement(ontoqlAst);
			if (w.getParseErrorHandler().getErrorCount() > 0) {
				Statement stmt = session.connection().createStatement();
				try {
					ResultSet resultsetQuerySQL = stmt.executeQuery(sparqlQuery);
					return new OntoQLResultSetImpl(resultsetQuerySQL, new ArrayList(), factoryEntity, session);
				} catch (SQLException e) {
					session.connection().rollback();
					stmt.close();
					w.getParseErrorHandler().throwQueryException();
				}
			}

			List propertiesInSelect = w.getExpressionInSelect();

			baos = new ByteArrayOutputStream();
			AST sqlAst = w.getAST();
			w.showAst(sqlAst, new PrintStream(baos));
			log.warn("SQL AST : " + baos.toString());

			SQLGenerator gen = new SQLGenerator(this.session);
			gen.statement(sqlAst);
			sql = gen.getSQL();
			gen.getParseErrorHandler().throwQueryException();

			log.warn("generated SQL : " + sql);
			sqlString = sql;

			Statement st = session.connection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);
			ResultSet resultsetQuerySQL = st.executeQuery(sql);

			return new OntoQLResultSetImpl(resultsetQuerySQL, propertiesInSelect, factoryEntity, session);
		} catch (QueryException qe) {
			throw qe;
		} catch (TokenStreamException exc) {
			throw new JOBDBCException(exc);
		} catch (RecognitionException exc) {
			throw new JOBDBCException(exc);
		} catch (SQLException sqle) {
			throw JDBCExceptionHelper.convert(SQLExceptionConverterFactory.buildMinimalSQLExceptionConverter(), sqle,
					sqle.getMessage(), sql);
		}
	}
}
