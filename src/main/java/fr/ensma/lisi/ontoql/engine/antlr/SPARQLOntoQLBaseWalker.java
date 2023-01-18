// $ANTLR 2.7.7 (20060906): "SPARQL-to-OntoQL.g" -> "SPARQLOntoQLBaseWalker.java"$

	
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
 
package fr.ensma.lisi.ontoql.engine.antlr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import fr.ensma.lisi.ontoql.sparql.CategorySPARQL;


import antlr.TreeParser;
import antlr.Token;
import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.ANTLRException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.collections.impl.BitSet;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;


/**
 * OntoQL Query tree to SQL Query tree  Transform.<br>
 * 
 * @author Stephane JEAN 
 */
public class SPARQLOntoQLBaseWalker extends antlr.TreeParser       implements SPARQLOntoQLBaseWalkerTokenTypes
 {

private static Log log = LogFactory.getLog(SPARQLOntoQLBaseWalker.class);

	/** current operator */
	protected int tmpOperator = INNER;
	
	/** operator between classes */
	protected int operator;
	
	/** previous class identified in the query * */
	protected CategorySPARQL previousCategory = null;

	/** current class identified in the query * */
	protected CategorySPARQL currentCategory = null;
	
	/** true if the SPARQL query uses the UNION operator. **/
	protected boolean inUnionQuery = false;
	
	/** the where clause of the translated OntoQL query */
	protected AST whereClause;

	/** Add a class or an entity in the query **/
	protected void addClassOrEntity(AST nodeClass, AST nodeVar) {}

	/** Add a property to the current class defined in the query **/
	protected void addDescriptionOrConstraint(AST nodeScope,AST nodeDescription, AST nodeVar, AST nodeURI) {}

 	protected void addPropertyorAttribute(AST nodeScope, AST nodeDescription,
			AST nodeVar) { }

	/** Get the from clause of the OntoQL query **/
	protected AST getFromClause () {return null;}
	
	/** Add a predicate to the query (i.e, an OntoSelect operator). **/
	protected void addOntoSelect (AST node) {};
	
	/** Set the current operator **/
	protected void setTmpOperator (int operator) {
		tmpOperator = operator;
	}
	
	/** Set the operator between classes **/
	protected void setOperator () {
		operator = tmpOperator;
	}
	
	/** Translate a variable of the SELECT clause into a property **/
	protected AST resolveSelectElement(AST node, boolean aliasNeeded){ return null; };
	
	/** True if this query need a distinct */
	protected boolean needDistinct = false;
	
	protected void reinitTranslator() {}
	
	protected void postProcessSPARQLQuery() {}
	
	protected void addJoin(CategorySPARQL categoryToJoin, CategorySPARQL categoryJoined) {}
public SPARQLOntoQLBaseWalker() {
	tokenNames = _tokenNames;
}

	public final void unionSparqlQuery(AST _t) throws RecognitionException {
		
		AST unionSparqlQuery_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unionSparqlQuery_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case UNION:
			{
				AST __t2 = _t;
				AST tmp1_AST = null;
				AST tmp1_AST_in = null;
				tmp1_AST = astFactory.create((AST)_t);
				tmp1_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp1_AST);
				ASTPair __currentAST2 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,UNION);
				_t = _t.getFirstChild();
				inUnionQuery=true;
				unionSparqlQuery(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				reinitTranslator();
				sparqlQuery(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST2;
				_t = __t2;
				_t = _t.getNextSibling();
				unionSparqlQuery_AST = (AST)currentAST.root;
				break;
			}
			case QUERY:
			{
				sparqlQuery(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				unionSparqlQuery_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = unionSparqlQuery_AST;
		_retTree = _t;
	}
	
	public final void sparqlQuery(AST _t) throws RecognitionException {
		
		AST sparqlQuery_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST sparqlQuery_AST = null;
		AST n_AST = null;
		AST n = null;
		AST w_AST = null;
		AST w = null;
		AST s_AST = null;
		AST s = null;
		AST o_AST = null;
		AST o = null;
		
		try {      // for error handling
			AST __t4 = _t;
			AST tmp2_AST = null;
			AST tmp2_AST_in = null;
			tmp2_AST = astFactory.create((AST)_t);
			tmp2_AST_in = (AST)_t;
			ASTPair __currentAST4 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,QUERY);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case PREFIX:
			{
				n = _t==ASTNULL ? null : (AST)_t;
				namespaceClause(_t);
				_t = _retTree;
				n_AST = (AST)returnAST;
				break;
			}
			case SELECT_WHERE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			AST __t6 = _t;
			AST tmp3_AST = null;
			AST tmp3_AST_in = null;
			tmp3_AST = astFactory.create((AST)_t);
			tmp3_AST_in = (AST)_t;
			ASTPair __currentAST6 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,SELECT_WHERE);
			_t = _t.getFirstChild();
			w = _t==ASTNULL ? null : (AST)_t;
			whereClause(_t);
			_t = _retTree;
			w_AST = (AST)returnAST;
			s = _t==ASTNULL ? null : (AST)_t;
			selectClause(_t);
			_t = _retTree;
			s_AST = (AST)returnAST;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ORDER:
			{
				o = _t==ASTNULL ? null : (AST)_t;
				orderByClause(_t);
				_t = _retTree;
				o_AST = (AST)returnAST;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			currentAST = __currentAST6;
			_t = __t6;
			_t = _t.getNextSibling();
			currentAST = __currentAST4;
			_t = __t4;
			_t = _t.getNextSibling();
			sparqlQuery_AST = (AST)currentAST.root;
			
					// todo I am obliged to encode the number corresponding to the token QUERY
					// because ANTLR recognize QUERY as a node.
					sparqlQuery_AST = (AST)astFactory.make( (new ASTArray(5)).add(astFactory.create(114,"QUERY")).add(n_AST).add((AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(SELECT_FROM,"SELECT_FROM")).add(w_AST).add(s_AST))).add(whereClause).add(o_AST));
					postProcessSPARQLQuery(); 
				
			currentAST.root = sparqlQuery_AST;
			currentAST.child = sparqlQuery_AST!=null &&sparqlQuery_AST.getFirstChild()!=null ?
				sparqlQuery_AST.getFirstChild() : sparqlQuery_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = sparqlQuery_AST;
		_retTree = _t;
	}
	
	public final void namespaceClause(AST _t) throws RecognitionException {
		
		AST namespaceClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST namespaceClause_AST = null;
		AST aliasns = null;
		AST aliasns_AST = null;
		AST ns = null;
		AST ns_AST = null;
		
		try {      // for error handling
			AST __t9 = _t;
			AST tmp4_AST = null;
			AST tmp4_AST_in = null;
			tmp4_AST = astFactory.create((AST)_t);
			tmp4_AST_in = (AST)_t;
			ASTPair __currentAST9 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,PREFIX);
			_t = _t.getFirstChild();
			aliasns = (AST)_t;
			AST aliasns_AST_in = null;
			aliasns_AST = astFactory.create(aliasns);
			match(_t,QNAME);
			_t = _t.getNextSibling();
			ns = (AST)_t;
			AST ns_AST_in = null;
			ns_AST = astFactory.create(ns);
			match(_t,Q_IRI_REF);
			_t = _t.getNextSibling();
			currentAST = __currentAST9;
			_t = __t9;
			_t = _t.getNextSibling();
			namespaceClause_AST = (AST)currentAST.root;
			
					String alias = aliasns_AST.getText();
					alias = alias.substring(0, alias.length()-1);
					String namespace = "'"+ ns_AST.getText()+"'";
					namespaceClause_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(NAMESPACE,"NAMESPACE")).add((AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(QUOTED_STRING,namespace)).add(astFactory.create(NAME_ID,alias)))));
				
			currentAST.root = namespaceClause_AST;
			currentAST.child = namespaceClause_AST!=null &&namespaceClause_AST.getFirstChild()!=null ?
				namespaceClause_AST.getFirstChild() : namespaceClause_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = namespaceClause_AST;
		_retTree = _t;
	}
	
	public final void whereClause(AST _t) throws RecognitionException {
		
		AST whereClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST whereClause_AST = null;
		
		try {      // for error handling
			AST __t21 = _t;
			AST tmp5_AST = null;
			AST tmp5_AST_in = null;
			tmp5_AST = astFactory.create((AST)_t);
			tmp5_AST_in = (AST)_t;
			ASTPair __currentAST21 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,WHERE);
			_t = _t.getFirstChild();
			blocClass(_t);
			_t = _retTree;
			{
			_loop23:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==TRIPLE_TYPE)) {
					blocClass(_t);
					_t = _retTree;
					addJoin(currentCategory, previousCategory);
				}
				else {
					break _loop23;
				}
				
			} while (true);
			}
			currentAST = __currentAST21;
			_t = __t21;
			_t = _t.getNextSibling();
			whereClause_AST = (AST)currentAST.root;
			
					whereClause_AST=getFromClause();
				
			currentAST.root = whereClause_AST;
			currentAST.child = whereClause_AST!=null &&whereClause_AST.getFirstChild()!=null ?
				whereClause_AST.getFirstChild() : whereClause_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = whereClause_AST;
		_retTree = _t;
	}
	
	public final void selectClause(AST _t) throws RecognitionException {
		
		AST selectClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectClause_AST = null;
		AST select = null;
		AST select_AST = null;
		AST d = null;
		AST d_AST = null;
		AST s_AST = null;
		AST s = null;
		
		try {      // for error handling
			AST __t14 = _t;
			select = _t==ASTNULL ? null :(AST)_t;
			AST select_AST_in = null;
			select_AST = astFactory.create(select);
			astFactory.addASTChild(currentAST, select_AST);
			ASTPair __currentAST14 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,SELECT);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case DISTINCT:
			{
				d = (AST)_t;
				AST d_AST_in = null;
				d_AST = astFactory.create(d);
				astFactory.addASTChild(currentAST, d_AST);
				match(_t,DISTINCT);
				_t = _t.getNextSibling();
				break;
			}
			case VAR:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			s = _t==ASTNULL ? null : (AST)_t;
			selectExprList(_t);
			_t = _retTree;
			s_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST14;
			_t = __t14;
			_t = _t.getNextSibling();
			selectClause_AST = (AST)currentAST.root;
			
					if (needDistinct && d == null) {
						selectClause_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(select.getType(),"SELECT")).add(astFactory.create(DISTINCT,"DISTINCT")).add(s_AST));
					}
				
			currentAST.root = selectClause_AST;
			currentAST.child = selectClause_AST!=null &&selectClause_AST.getFirstChild()!=null ?
				selectClause_AST.getFirstChild() : selectClause_AST;
			currentAST.advanceChildToEnd();
			selectClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = selectClause_AST;
		_retTree = _t;
	}
	
	public final void orderByClause(AST _t) throws RecognitionException {
		
		AST orderByClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orderByClause_AST = null;
		AST o = null;
		AST o_AST = null;
		AST a = null;
		AST a_AST = null;
		AST d = null;
		AST d_AST = null;
		AST v = null;
		AST v_AST = null;
		
		try {      // for error handling
			AST __t50 = _t;
			o = _t==ASTNULL ? null :(AST)_t;
			AST o_AST_in = null;
			o_AST = astFactory.create(o);
			ASTPair __currentAST50 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ORDER);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ASCENDING:
			{
				a = (AST)_t;
				AST a_AST_in = null;
				a_AST = astFactory.create(a);
				match(_t,ASCENDING);
				_t = _t.getNextSibling();
				break;
			}
			case DESCENDING:
			{
				d = (AST)_t;
				AST d_AST_in = null;
				d_AST = astFactory.create(d);
				match(_t,DESCENDING);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			v = (AST)_t;
			AST v_AST_in = null;
			v_AST = astFactory.create(v);
			match(_t,VAR);
			_t = _t.getNextSibling();
			currentAST = __currentAST50;
			_t = __t50;
			_t = _t.getNextSibling();
			orderByClause_AST = (AST)currentAST.root;
			
					v_AST = resolveSelectElement(v, false);
					AST s = a_AST==null? d_AST : a_AST;
					orderByClause_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(o.getType(),"order")).add(v_AST).add(s));
				
			currentAST.root = orderByClause_AST;
			currentAST.child = orderByClause_AST!=null &&orderByClause_AST.getFirstChild()!=null ?
				orderByClause_AST.getFirstChild() : orderByClause_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = orderByClause_AST;
		_retTree = _t;
	}
	
	public final void namespaceAlias(AST _t) throws RecognitionException {
		
		AST namespaceAlias_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST namespaceAlias_AST = null;
		AST c = null;
		AST c_AST = null;
		AST a = null;
		AST a_AST = null;
		
		try {      // for error handling
			AST __t11 = _t;
			c = _t==ASTNULL ? null :(AST)_t;
			AST c_AST_in = null;
			c_AST = astFactory.create(c);
			astFactory.addASTChild(currentAST, c_AST);
			ASTPair __currentAST11 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,QUOTED_STRING);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case NAME_ID:
			{
				a = (AST)_t;
				AST a_AST_in = null;
				a_AST = astFactory.create(a);
				astFactory.addASTChild(currentAST, a_AST);
				match(_t,NAME_ID);
				_t = _t.getNextSibling();
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			currentAST = __currentAST11;
			_t = __t11;
			_t = _t.getNextSibling();
			namespaceAlias_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = namespaceAlias_AST;
		_retTree = _t;
	}
	
	public final void selectExprList(AST _t) throws RecognitionException {
		
		AST selectExprList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectExprList_AST = null;
		AST s_AST = null;
		AST s = null;
		
		try {      // for error handling
			{
			int _cnt18=0;
			_loop18:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==VAR)) {
					s = _t==ASTNULL ? null : (AST)_t;
					selectExpr(_t);
					_t = _retTree;
					s_AST = (AST)returnAST;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt18>=1 ) { break _loop18; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt18++;
			} while (true);
			}
			selectExprList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = selectExprList_AST;
		_retTree = _t;
	}
	
	public final void selectExpr(AST _t) throws RecognitionException {
		
		AST selectExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectExpr_AST = null;
		AST v = null;
		AST v_AST = null;
		
		try {      // for error handling
			v = (AST)_t;
			AST v_AST_in = null;
			v_AST = astFactory.create(v);
			match(_t,VAR);
			_t = _t.getNextSibling();
			selectExpr_AST = (AST)currentAST.root;
			
					selectExpr_AST = resolveSelectElement(v,true);
				
			currentAST.root = selectExpr_AST;
			currentAST.child = selectExpr_AST!=null &&selectExpr_AST.getFirstChild()!=null ?
				selectExpr_AST.getFirstChild() : selectExpr_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = selectExpr_AST;
		_retTree = _t;
	}
	
	public final void blocClass(AST _t) throws RecognitionException {
		
		AST blocClass_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST blocClass_AST = null;
		
		try {      // for error handling
			tripleType(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop26:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==OPTIONAL||_t.getType()==FILTER||_t.getType()==TRIPLE_PROP)) {
					filterOrTriplePropertyOrBlockClass(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop26;
				}
				
			} while (true);
			}
			blocClass_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = blocClass_AST;
		_retTree = _t;
	}
	
	public final void tripleType(AST _t) throws RecognitionException {
		
		AST tripleType_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tripleType_AST = null;
		AST v = null;
		AST v_AST = null;
		AST c = null;
		AST c_AST = null;
		
		try {      // for error handling
			AST __t53 = _t;
			AST tmp6_AST = null;
			AST tmp6_AST_in = null;
			tmp6_AST = astFactory.create((AST)_t);
			tmp6_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp6_AST);
			ASTPair __currentAST53 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,TRIPLE_TYPE);
			_t = _t.getFirstChild();
			v = (AST)_t;
			AST v_AST_in = null;
			v_AST = astFactory.create(v);
			astFactory.addASTChild(currentAST, v_AST);
			match(_t,VAR);
			_t = _t.getNextSibling();
			c = (AST)_t;
			AST c_AST_in = null;
			c_AST = astFactory.create(c);
			astFactory.addASTChild(currentAST, c_AST);
			match(_t,QNAME);
			_t = _t.getNextSibling();
			currentAST = __currentAST53;
			_t = __t53;
			_t = _t.getNextSibling();
			
					setOperator();
					addClassOrEntity(c_AST, v_AST);
					String prefix = c.getText().startsWith("rdf") ? "#" : "";
					String name = prefix + "oid";
					addPropertyorAttribute(v_AST, (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(v.getType(),name))), v_AST);
					if (prefix.equals("")) {
						String nameURI = v_AST.getText()+"URI";
						addPropertyorAttribute(v_AST, (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(v.getType(),"URI"))), (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(v.getType(),nameURI))));
					}
				
			tripleType_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = tripleType_AST;
		_retTree = _t;
	}
	
	public final void filterOrTriplePropertyOrBlockClass(AST _t) throws RecognitionException {
		
		AST filterOrTriplePropertyOrBlockClass_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST filterOrTriplePropertyOrBlockClass_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case FILTER:
			{
				filter(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				filterOrTriplePropertyOrBlockClass_AST = (AST)currentAST.root;
				break;
			}
			case OPTIONAL:
			case TRIPLE_PROP:
			{
				triplePropertyOrBlockClass(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				filterOrTriplePropertyOrBlockClass_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = filterOrTriplePropertyOrBlockClass_AST;
		_retTree = _t;
	}
	
	public final void filter(AST _t) throws RecognitionException {
		
		AST filter_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST filter_AST = null;
		AST l_AST = null;
		AST l = null;
		
		try {      // for error handling
			AST __t29 = _t;
			AST tmp7_AST = null;
			AST tmp7_AST_in = null;
			tmp7_AST = astFactory.create((AST)_t);
			tmp7_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp7_AST);
			ASTPair __currentAST29 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,FILTER);
			_t = _t.getFirstChild();
			l = _t==ASTNULL ? null : (AST)_t;
			logicalExpr(_t);
			_t = _retTree;
			l_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			addOntoSelect(l_AST);
			currentAST = __currentAST29;
			_t = __t29;
			_t = _t.getNextSibling();
			filter_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = filter_AST;
		_retTree = _t;
	}
	
	public final void triplePropertyOrBlockClass(AST _t) throws RecognitionException {
		
		AST triplePropertyOrBlockClass_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST triplePropertyOrBlockClass_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case OPTIONAL:
			{
				AST __t55 = _t;
				AST tmp8_AST = null;
				AST tmp8_AST_in = null;
				tmp8_AST = astFactory.create((AST)_t);
				tmp8_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp8_AST);
				ASTPair __currentAST55 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,OPTIONAL);
				_t = _t.getFirstChild();
				setTmpOperator(OPTIONAL);
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case TRIPLE_PROP:
				{
					tripleProperty(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case TRIPLE_TYPE:
				{
					blocClass(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					addJoin(currentCategory, previousCategory);
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				currentAST = __currentAST55;
				_t = __t55;
				_t = _t.getNextSibling();
				triplePropertyOrBlockClass_AST = (AST)currentAST.root;
				break;
			}
			case TRIPLE_PROP:
			{
				tripleProperty(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				triplePropertyOrBlockClass_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = triplePropertyOrBlockClass_AST;
		_retTree = _t;
	}
	
	public final void logicalExpr(AST _t) throws RecognitionException {
		
		AST logicalExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalExpr_AST = null;
		AST e_AST = null;
		AST e = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case AND:
			{
				AST __t31 = _t;
				AST tmp9_AST = null;
				AST tmp9_AST_in = null;
				tmp9_AST = astFactory.create((AST)_t);
				tmp9_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp9_AST);
				ASTPair __currentAST31 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,AND);
				_t = _t.getFirstChild();
				logicalExpr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				logicalExpr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST31;
				_t = __t31;
				_t = _t.getNextSibling();
				logicalExpr_AST = (AST)currentAST.root;
				break;
			}
			case OR:
			{
				AST __t32 = _t;
				AST tmp10_AST = null;
				AST tmp10_AST_in = null;
				tmp10_AST = astFactory.create((AST)_t);
				tmp10_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp10_AST);
				ASTPair __currentAST32 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,OR);
				_t = _t.getFirstChild();
				logicalExpr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				logicalExpr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST32;
				_t = __t32;
				_t = _t.getNextSibling();
				logicalExpr_AST = (AST)currentAST.root;
				break;
			}
			case NOT:
			{
				AST __t33 = _t;
				AST tmp11_AST = null;
				AST tmp11_AST_in = null;
				tmp11_AST = astFactory.create((AST)_t);
				tmp11_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp11_AST);
				ASTPair __currentAST33 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,NOT);
				_t = _t.getFirstChild();
				logicalExpr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST33;
				_t = __t33;
				_t = _t.getNextSibling();
				logicalExpr_AST = (AST)currentAST.root;
				break;
			}
			case EQ:
			case NE:
			case LT:
			case GT:
			case LE:
			case GE:
			{
				comparisonExpr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				logicalExpr_AST = (AST)currentAST.root;
				break;
			}
			case BOUND:
			{
				AST __t34 = _t;
				AST tmp12_AST = null;
				AST tmp12_AST_in = null;
				tmp12_AST = astFactory.create((AST)_t);
				tmp12_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp12_AST);
				ASTPair __currentAST34 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,BOUND);
				_t = _t.getFirstChild();
				e = _t==ASTNULL ? null : (AST)_t;
				expr(_t);
				_t = _retTree;
				e_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST34;
				_t = __t34;
				_t = _t.getNextSibling();
				logicalExpr_AST = (AST)currentAST.root;
				logicalExpr_AST=(AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(SPARQLTokenTypes.IS_NOT_NULL,"is_not_null")).add(e_AST));
				currentAST.root = logicalExpr_AST;
				currentAST.child = logicalExpr_AST!=null &&logicalExpr_AST.getFirstChild()!=null ?
					logicalExpr_AST.getFirstChild() : logicalExpr_AST;
				currentAST.advanceChildToEnd();
				logicalExpr_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = logicalExpr_AST;
		_retTree = _t;
	}
	
	public final void comparisonExpr(AST _t) throws RecognitionException {
		
		AST comparisonExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST comparisonExpr_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case EQ:
			{
				AST __t36 = _t;
				AST tmp13_AST = null;
				AST tmp13_AST_in = null;
				tmp13_AST = astFactory.create((AST)_t);
				tmp13_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp13_AST);
				ASTPair __currentAST36 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,EQ);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST36;
				_t = __t36;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case NE:
			{
				AST __t37 = _t;
				AST tmp14_AST = null;
				AST tmp14_AST_in = null;
				tmp14_AST = astFactory.create((AST)_t);
				tmp14_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp14_AST);
				ASTPair __currentAST37 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,NE);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST37;
				_t = __t37;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case LT:
			{
				AST __t38 = _t;
				AST tmp15_AST = null;
				AST tmp15_AST_in = null;
				tmp15_AST = astFactory.create((AST)_t);
				tmp15_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp15_AST);
				ASTPair __currentAST38 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,LT);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST38;
				_t = __t38;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case GT:
			{
				AST __t39 = _t;
				AST tmp16_AST = null;
				AST tmp16_AST_in = null;
				tmp16_AST = astFactory.create((AST)_t);
				tmp16_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp16_AST);
				ASTPair __currentAST39 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,GT);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST39;
				_t = __t39;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case LE:
			{
				AST __t40 = _t;
				AST tmp17_AST = null;
				AST tmp17_AST_in = null;
				tmp17_AST = astFactory.create((AST)_t);
				tmp17_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp17_AST);
				ASTPair __currentAST40 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,LE);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST40;
				_t = __t40;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case GE:
			{
				AST __t41 = _t;
				AST tmp18_AST = null;
				AST tmp18_AST_in = null;
				tmp18_AST = astFactory.create((AST)_t);
				tmp18_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp18_AST);
				ASTPair __currentAST41 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,GE);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST41;
				_t = __t41;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = comparisonExpr_AST;
		_retTree = _t;
	}
	
	public final void expr(AST _t) throws RecognitionException {
		
		AST expr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expr_AST = null;
		AST v = null;
		AST v_AST = null;
		AST s = null;
		AST s_AST = null;
		AST i = null;
		AST i_AST = null;
		AST a_AST = null;
		AST a = null;
		AST u = null;
		AST u_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case VAR:
			{
				v = (AST)_t;
				AST v_AST_in = null;
				v_AST = astFactory.create(v);
				match(_t,VAR);
				_t = _t.getNextSibling();
				expr_AST = (AST)currentAST.root;
				expr_AST = resolveSelectElement(v, false);
				currentAST.root = expr_AST;
				currentAST.child = expr_AST!=null &&expr_AST.getFirstChild()!=null ?
					expr_AST.getFirstChild() : expr_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case STRING_LITERAL1:
			{
				s = (AST)_t;
				AST s_AST_in = null;
				s_AST = astFactory.create(s);
				match(_t,STRING_LITERAL1);
				_t = _t.getNextSibling();
				expr_AST = (AST)currentAST.root;
				expr_AST = astFactory.create(SPARQLTokenTypes.QUOTED_STRING, s.getText());
				currentAST.root = expr_AST;
				currentAST.child = expr_AST!=null &&expr_AST.getFirstChild()!=null ?
					expr_AST.getFirstChild() : expr_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case INTEGER:
			{
				i = (AST)_t;
				AST i_AST_in = null;
				i_AST = astFactory.create(i);
				match(_t,INTEGER);
				_t = _t.getNextSibling();
				expr_AST = (AST)currentAST.root;
				expr_AST = astFactory.create(SPARQLTokenTypes.NUM_INT, i.getText());
				currentAST.root = expr_AST;
				currentAST.child = expr_AST!=null &&expr_AST.getFirstChild()!=null ?
					expr_AST.getFirstChild() : expr_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case UNARY_MINUS:
			case STAR:
			case PLUS:
			case MINUS:
			case DIV:
			{
				a = _t==ASTNULL ? null : (AST)_t;
				arithmeticExpr(_t);
				_t = _retTree;
				a_AST = (AST)returnAST;
				expr_AST = (AST)currentAST.root;
				expr_AST = a_AST;
				currentAST.root = expr_AST;
				currentAST.child = expr_AST!=null &&expr_AST.getFirstChild()!=null ?
					expr_AST.getFirstChild() : expr_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case Q_IRI_REF:
			{
				u = (AST)_t;
				AST u_AST_in = null;
				u_AST = astFactory.create(u);
				match(_t,Q_IRI_REF);
				_t = _t.getNextSibling();
				expr_AST = (AST)currentAST.root;
				expr_AST = astFactory.create(SPARQLTokenTypes.QUOTED_STRING, "'"+u.getText()+"'");
				currentAST.root = expr_AST;
				currentAST.child = expr_AST!=null &&expr_AST.getFirstChild()!=null ?
					expr_AST.getFirstChild() : expr_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = expr_AST;
		_retTree = _t;
	}
	
	public final void arithmeticExpr(AST _t) throws RecognitionException {
		
		AST arithmeticExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arithmeticExpr_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case PLUS:
			{
				AST __t44 = _t;
				AST tmp19_AST = null;
				AST tmp19_AST_in = null;
				tmp19_AST = astFactory.create((AST)_t);
				tmp19_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp19_AST);
				ASTPair __currentAST44 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,PLUS);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST44;
				_t = __t44;
				_t = _t.getNextSibling();
				arithmeticExpr_AST = (AST)currentAST.root;
				break;
			}
			case MINUS:
			{
				AST __t45 = _t;
				AST tmp20_AST = null;
				AST tmp20_AST_in = null;
				tmp20_AST = astFactory.create((AST)_t);
				tmp20_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp20_AST);
				ASTPair __currentAST45 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,MINUS);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST45;
				_t = __t45;
				_t = _t.getNextSibling();
				arithmeticExpr_AST = (AST)currentAST.root;
				break;
			}
			case DIV:
			{
				AST __t46 = _t;
				AST tmp21_AST = null;
				AST tmp21_AST_in = null;
				tmp21_AST = astFactory.create((AST)_t);
				tmp21_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp21_AST);
				ASTPair __currentAST46 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,DIV);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST46;
				_t = __t46;
				_t = _t.getNextSibling();
				arithmeticExpr_AST = (AST)currentAST.root;
				break;
			}
			case STAR:
			{
				AST __t47 = _t;
				AST tmp22_AST = null;
				AST tmp22_AST_in = null;
				tmp22_AST = astFactory.create((AST)_t);
				tmp22_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp22_AST);
				ASTPair __currentAST47 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,STAR);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST47;
				_t = __t47;
				_t = _t.getNextSibling();
				arithmeticExpr_AST = (AST)currentAST.root;
				break;
			}
			case UNARY_MINUS:
			{
				AST __t48 = _t;
				AST tmp23_AST = null;
				AST tmp23_AST_in = null;
				tmp23_AST = astFactory.create((AST)_t);
				tmp23_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp23_AST);
				ASTPair __currentAST48 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,UNARY_MINUS);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST48;
				_t = __t48;
				_t = _t.getNextSibling();
				arithmeticExpr_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = arithmeticExpr_AST;
		_retTree = _t;
	}
	
	public final void tripleProperty(AST _t) throws RecognitionException {
		
		AST tripleProperty_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tripleProperty_AST = null;
		AST scope = null;
		AST scope_AST = null;
		AST p = null;
		AST p_AST = null;
		AST v = null;
		AST v_AST = null;
		AST u_AST = null;
		AST u = null;
		
		try {      // for error handling
			AST __t58 = _t;
			AST tmp24_AST = null;
			AST tmp24_AST_in = null;
			tmp24_AST = astFactory.create((AST)_t);
			tmp24_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp24_AST);
			ASTPair __currentAST58 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,TRIPLE_PROP);
			_t = _t.getFirstChild();
			scope = (AST)_t;
			AST scope_AST_in = null;
			scope_AST = astFactory.create(scope);
			astFactory.addASTChild(currentAST, scope_AST);
			match(_t,VAR);
			_t = _t.getNextSibling();
			p = (AST)_t;
			AST p_AST_in = null;
			p_AST = astFactory.create(p);
			astFactory.addASTChild(currentAST, p_AST);
			match(_t,QNAME);
			_t = _t.getNextSibling();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case VAR:
			{
				v = (AST)_t;
				AST v_AST_in = null;
				v_AST = astFactory.create(v);
				astFactory.addASTChild(currentAST, v_AST);
				match(_t,VAR);
				_t = _t.getNextSibling();
				break;
			}
			case Q_IRI_REF:
			case STRING_LITERAL1:
			{
				u = _t==ASTNULL ? null : (AST)_t;
				constant(_t);
				_t = _retTree;
				u_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			currentAST = __currentAST58;
			_t = __t58;
			_t = _t.getNextSibling();
			
					addDescriptionOrConstraint(scope_AST, p_AST, v_AST, u_AST);
					setTmpOperator(INNER);
				
			tripleProperty_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = tripleProperty_AST;
		_retTree = _t;
	}
	
	public final void constant(AST _t) throws RecognitionException {
		
		AST constant_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constant_AST = null;
		AST s = null;
		AST s_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case Q_IRI_REF:
			{
				AST tmp25_AST = null;
				AST tmp25_AST_in = null;
				tmp25_AST = astFactory.create((AST)_t);
				tmp25_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp25_AST);
				match(_t,Q_IRI_REF);
				_t = _t.getNextSibling();
				constant_AST = (AST)currentAST.root;
				break;
			}
			case STRING_LITERAL1:
			{
				s = (AST)_t;
				AST s_AST_in = null;
				s_AST = astFactory.create(s);
				astFactory.addASTChild(currentAST, s_AST);
				match(_t,STRING_LITERAL1);
				_t = _t.getNextSibling();
				constant_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = constant_AST;
		_retTree = _t;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"add\"",
		"\"all\"",
		"\"alter\"",
		"\"and\"",
		"\"any\"",
		"\"array\"",
		"\"as\"",
		"\"asc\"",
		"\"attribute\"",
		"\"avg\"",
		"\"between\"",
		"\"boolean\"",
		"\"real\"",
		"\"case\"",
		"\"count\"",
		"\"create\"",
		"\"cross\"",
		"\"delete\"",
		"\"desc\"",
		"\"descriptor\"",
		"\"distinct\"",
		"\"drop\"",
		"\"else\"",
		"\"en\"",
		"\"end\"",
		"\"entity\"",
		"\"except\"",
		"\"exists\"",
		"\"extent\"",
		"\"false\"",
		"\"fr\"",
		"\"from\"",
		"\"full\"",
		"\"group\"",
		"\"having\"",
		"\"in\"",
		"\"inner\"",
		"\"insert\"",
		"\"int\"",
		"\"intersect\"",
		"\"into\"",
		"\"is\"",
		"\"join\"",
		"\"language\"",
		"\"left\"",
		"\"like\"",
		"\"max\"",
		"\"min\"",
		"\"multilingual\"",
		"\"namespace\"",
		"\"natural\"",
		"\"none\"",
		"\"enum\"",
		"\"not\"",
		"\"null\"",
		"\"of\"",
		"\"on\"",
		"\"only\"",
		"\"or\"",
		"\"order\"",
		"\"outer\"",
		"\"preferring\"",
		"\"property\"",
		"\"ref\"",
		"\"right\"",
		"\"select\"",
		"\"set\"",
		"\"string\"",
		"\"sum\"",
		"\"then\"",
		"\"true\"",
		"\"typeof\"",
		"\"under\"",
		"\"union\"",
		"\"union all\"",
		"\"unnest\"",
		"\"update\"",
		"\"using\"",
		"\"values\"",
		"\"view\"",
		"\"when\"",
		"\"where\"",
		"\"uriType\"",
		"\"countType\"",
		"\"limit\"",
		"\"offset\"",
		"\"caseof\"",
		"\"map\"",
		"\"context\"",
		"\"properties\"",
		"\"class\"",
		"AGGREGATE",
		"ALIAS",
		"DOT",
		"NAMESPACE_ALIAS",
		"ROW_STAR",
		"EXPR_LIST",
		"IN_LIST",
		"LANGUE_OP",
		"INDEX_OP",
		"IS_NOT_NULL",
		"IS_NULL",
		"IS_NOT_OF",
		"IS_OF",
		"METHOD_CALL",
		"NOT_BETWEEN",
		"CASE2",
		"NOT_IN",
		"NOT_LIKE",
		"ORDER_ELEMENT",
		"QUERY",
		"RANGE",
		"PROPERTY_DEF",
		"ATTRIBUTE_DEF",
		"ATTRIBUTES",
		"SELECT_FROM",
		"UNARY_MINUS",
		"UNARY_PLUS",
		"VECTOR_EXPR",
		"IDENT",
		"DATATYPE",
		"PREDEFINED_TYPE",
		"ARRAY_DEF",
		"MAPPED_PROPERTIES",
		"CONSTANT",
		"NUM_DOUBLE",
		"NUM_FLOAT",
		"NUM_LONG",
		"OPEN",
		"CLOSE",
		"ONTOLOGY_MODEL_ID",
		"COMMA",
		"EQ",
		"STAR",
		"\"by\"",
		"NUM_INT",
		"NAME_ID",
		"QUOTED_STRING",
		"\"ascending\"",
		"\"descending\"",
		"NE",
		"SQL_NE",
		"LT",
		"GT",
		"LE",
		"GE",
		"CONCAT",
		"PLUS",
		"MINUS",
		"DIV",
		"OPEN_BRACKET",
		"CLOSE_BRACKET",
		"COLON",
		"INTERNAL_ID",
		"EXTERNAL_ID",
		"ID_START_LETTER",
		"ID_LETTER",
		"DOUBLE_QUOTED_STRING",
		"ESCdqs",
		"ESCqs",
		"WS",
		"HEX_DIGIT",
		"EXPONENT",
		"FLOAT_SUFFIX",
		"PREFIX",
		"OPTIONAL",
		"BOUND",
		"FILTER",
		"\"rdf:type\"",
		"TRIPLE_TYPE",
		"TRIPLE_PROP",
		"UNION_LEFT",
		"UNION_RIGHT",
		"SELECT_WHERE",
		"QNAME",
		"Q_IRI_REF",
		"VAR",
		"OPEN_CURLY",
		"CLOSE_CURLY",
		"INTEGER",
		"STRING_LITERAL1",
		"UNDERSCORE",
		"QUESTION_MARK",
		"DOLLAR",
		"NCNAME_PREFIX",
		"NCNAME",
		"NCCHAR",
		"VARNAME",
		"NCCHAR1",
		"NCCHAR1p"
	};
	
	}
	
