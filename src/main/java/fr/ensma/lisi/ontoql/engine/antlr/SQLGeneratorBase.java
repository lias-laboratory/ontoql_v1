// $ANTLR 2.7.7 (20060906): "OntoQL-generation.g" -> "SQLGeneratorBase.java"$

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
 * SQL Generator Tree Parser, providing SQL rendering of SQL ASTs produced by the previous phase, OntoqlSqlWalker.  All
 * syntax decoration such as extra spaces, lack of spaces, extra parens, etc. should be added by this class.
 * <br>
 * This grammar processes the SQL AST and produces an SQL string.  The intent is to move dialect-specific
 * code into a sub-class that will override some of the methods, just like the other two grammars in this system.
 *
 * @author Stephane JEAN
 */
public class SQLGeneratorBase extends antlr.TreeParser       implements SQLTokenTypes
 {

	private static Log log = LogFactory.getLog(SQLGeneratorBase.class);

   /** the buffer resulting SQL statement is written to */
	private StringBuffer buf = new StringBuffer();

	protected void out(String s) {
		buf.append(s);
	}
	
	
	protected void out(AST n) {
		out(n.getText());
	}

	protected void separator(AST n, String sep) {
		if (n.getNextSibling() != null)
			out(sep);
	}
	
	protected void beginFunctionTemplate(AST m,AST i) {
		// if template is null we just write the function out as it appears in the hql statement
		out(i);
		if (!m.getText().equals("||"))
			out("(");
	}

	protected void endFunctionTemplate(AST m) {
	   if (!m.getText().equals("||"))
	      out(")");
	}
	
	protected void commaBetweenParameters(String comma) {
		out(comma);
	}
	

	/**
	 * Returns the last character written to the output, or -1 if there isn't one.
	 */
	protected int getLastChar() {
		int len = buf.length();
		if ( len == 0 )
			return -1;
		else
			return buf.charAt( len - 1 );
	}
	
	/**
	 * Add a aspace if the previous token was not a space or a parenthesis.
	 */
	protected void optionalSpace() {
		// Implemented in the sub-class.
	}

	protected StringBuffer getStringBuffer() {
		return buf;
	}
	
	
	protected void fromFragmentSeparator(AST a) {
		AST next = a.getNextSibling();
		if (next != null && next.getType() != OntoQLSQLTokenTypes.JOIN_CONDITION) {
			out(", ");
		}
	}
	
	protected void reInit(){
		buf = new StringBuffer();
	}



public SQLGeneratorBase() {
	tokenNames = _tokenNames;
}

	public final void statement(AST _t) throws RecognitionException {
		
		AST statement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
			reInit();
			
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case EXCEPT:
			case INTERSECT:
			case SELECT:
			case UNION:
			{
				queryExpression(_t);
				_t = _retTree;
				break;
			}
			case INSERT:
			{
				insertStatement(_t);
				_t = _retTree;
				break;
			}
			case UPDATE:
			{
				updateStatement(_t);
				_t = _retTree;
				break;
			}
			case DELETE:
			{
				deleteStatement(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void queryExpression(AST _t) throws RecognitionException {
		
		AST queryExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case UNION:
			{
				AST __t34 = _t;
				AST tmp1_AST_in = (AST)_t;
				match(_t,UNION);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					out("(");
				}
				queryExpression(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(") union (");
				}
				queryExpression(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(")");
				}
				_t = __t34;
				_t = _t.getNextSibling();
				break;
			}
			case EXCEPT:
			{
				AST __t35 = _t;
				AST tmp2_AST_in = (AST)_t;
				match(_t,EXCEPT);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					out("(");
				}
				queryExpression(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(") except (");
				}
				queryExpression(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(")");
				}
				_t = __t35;
				_t = _t.getNextSibling();
				break;
			}
			case INTERSECT:
			{
				AST __t36 = _t;
				AST tmp3_AST_in = (AST)_t;
				match(_t,INTERSECT);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					out("(");
				}
				queryExpression(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(") intersect (");
				}
				queryExpression(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(")");
				}
				_t = __t36;
				_t = _t.getNextSibling();
				break;
			}
			case SELECT:
			{
				selectStatement(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void insertStatement(AST _t) throws RecognitionException {
		
		AST insertStatement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST r = null;
		
		try {      // for error handling
			AST __t3 = _t;
			AST tmp4_AST_in = (AST)_t;
			match(_t,INSERT);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				out( "insert " );
			}
			AST __t4 = _t;
			AST tmp5_AST_in = (AST)_t;
			match(_t,INTO);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				out( "into " );
			}
			r = (AST)_t;
			match(_t,TABLE);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				out(r);
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case RANGE:
			{
				insertColumnList(_t);
				_t = _retTree;
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
			_t = __t4;
			_t = _t.getNextSibling();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case SELECT:
			{
				selectStatement(_t);
				_t = _retTree;
				break;
			}
			case VALUES:
			{
				valueClause(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t3;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void updateStatement(AST _t) throws RecognitionException {
		
		AST updateStatement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST a = null;
		
		try {      // for error handling
			AST __t18 = _t;
			AST tmp6_AST_in = (AST)_t;
			match(_t,UPDATE);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				out("update ");
			}
			AST __t19 = _t;
			AST tmp7_AST_in = (AST)_t;
			match(_t,FROM);
			_t = _t.getFirstChild();
			a = (AST)_t;
			match(_t,TABLE);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				out(a.getText());
			}
			_t = __t19;
			_t = _t.getNextSibling();
			setClause(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case WHERE:
			{
				whereClause(_t);
				_t = _retTree;
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
			_t = __t18;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void deleteStatement(AST _t) throws RecognitionException {
		
		AST deleteStatement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t31 = _t;
			AST tmp8_AST_in = (AST)_t;
			match(_t,DELETE);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				out("delete");
			}
			from(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case WHERE:
			{
				whereClause(_t);
				_t = _retTree;
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
			_t = __t31;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void insertColumnList(AST _t) throws RecognitionException {
		
		AST insertColumnList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST a = null;
		
		try {      // for error handling
			AST __t8 = _t;
			AST tmp9_AST_in = (AST)_t;
			match(_t,RANGE);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				out("(");
			}
			{
			int _cnt10=0;
			_loop10:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==COLUMN)) {
					a = (AST)_t;
					match(_t,COLUMN);
					_t = _t.getNextSibling();
					if ( inputState.guessing==0 ) {
						out(a); separator(a,", ");
					}
				}
				else {
					if ( _cnt10>=1 ) { break _loop10; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt10++;
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				out(")");
			}
			_t = __t8;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void selectStatement(AST _t) throws RecognitionException {
		
		AST selectStatement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			query(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void valueClause(AST _t) throws RecognitionException {
		
		AST valueClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t12 = _t;
			AST tmp10_AST_in = (AST)_t;
			match(_t,VALUES);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				out("values (");
			}
			{
			int _cnt14=0;
			_loop14:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_0.member(_t.getType()))) {
					valueElement(_t);
					_t = _retTree;
				}
				else {
					if ( _cnt14>=1 ) { break _loop14; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt14++;
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				out(")");
			}
			_t = __t12;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void valueElement(AST _t) throws RecognitionException {
		
		AST valueElement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST c = null;
		AST s = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ARRAY:
			case CASE:
			case COUNT:
			case FALSE:
			case NULL:
			case REF:
			case TRUE:
			case AGGREGATE:
			case ALIAS:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case PREDEFINED_TYPE:
			case ARRAY_DEF:
			case CONSTANT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
			case COLUMN:
			{
				c = _t==ASTNULL ? null : (AST)_t;
				simpleExpr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					separator(c,", ");
				}
				break;
			}
			case SELECT:
			{
				{
				if ( inputState.guessing==0 ) {
					out("(");
				}
				s = _t==ASTNULL ? null : (AST)_t;
				selectStatement(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(")");separator(s,", ");
				}
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void simpleExpr(AST _t) throws RecognitionException {
		
		AST simpleExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST c = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case FALSE:
			case NULL:
			case TRUE:
			case ALIAS:
			case CONSTANT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case NUM_INT:
			case QUOTED_STRING:
			case COLUMN:
			{
				c = _t==ASTNULL ? null : (AST)_t;
				constant(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(c);
				}
				break;
			}
			case METHOD_CALL:
			{
				methodCall(_t);
				_t = _retTree;
				break;
			}
			case COUNT:
			{
				count(_t);
				_t = _retTree;
				break;
			}
			case AGGREGATE:
			{
				aggregate(_t);
				_t = _retTree;
				break;
			}
			case CASE:
			case CASE2:
			case UNARY_MINUS:
			case STAR:
			case PLUS:
			case MINUS:
			case DIV:
			{
				arithmeticExpr(_t);
				_t = _retTree;
				break;
			}
			case ARRAY:
			{
				arrayExpr(_t);
				_t = _retTree;
				break;
			}
			case REF:
			case PREDEFINED_TYPE:
			case ARRAY_DEF:
			{
				datatypeExpr(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void setClause(AST _t) throws RecognitionException {
		
		AST setClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t22 = _t;
			AST tmp11_AST_in = (AST)_t;
			match(_t,SET);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				out(" set ");
			}
			eqExpr(_t);
			_t = _retTree;
			{
			_loop24:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==EQ)) {
					if ( inputState.guessing==0 ) {
						out(", ");
					}
					eqExpr(_t);
					_t = _retTree;
				}
				else {
					break _loop24;
				}
				
			} while (true);
			}
			_t = __t22;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void whereClause(AST _t) throws RecognitionException {
		
		AST whereClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t29 = _t;
			AST tmp12_AST_in = (AST)_t;
			match(_t,WHERE);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				out(" where ");
			}
			booleanExpr(_t, false );
			_t = _retTree;
			_t = __t29;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void eqExpr(AST _t) throws RecognitionException {
		
		AST eqExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t27 = _t;
			AST tmp13_AST_in = (AST)_t;
			match(_t,EQ);
			_t = _t.getFirstChild();
			setElement(_t);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				out("=");
			}
			valueElement(_t);
			_t = _retTree;
			_t = __t27;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void setElement(AST _t) throws RecognitionException {
		
		AST setElement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST p = null;
		
		try {      // for error handling
			p = (AST)_t;
			match(_t,COLUMN);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				out(p.getText().substring(p.getText().indexOf('.')+1,p.getText().length()));
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void booleanExpr(AST _t,
		 boolean parens 
	) throws RecognitionException {
		
		AST booleanExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST j = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case AND:
			case NOT:
			case OR:
			{
				booleanOp(_t, parens );
				_t = _retTree;
				break;
			}
			case BETWEEN:
			case EXISTS:
			case IN:
			case LIKE:
			case IS_NOT_NULL:
			case IS_NULL:
			case NOT_BETWEEN:
			case NOT_IN:
			case NOT_LIKE:
			case EQ:
			case NE:
			case LT:
			case GT:
			case LE:
			case GE:
			{
				comparisonExpr(_t, parens );
				_t = _retTree;
				break;
			}
			case JOIN_CONDITION:
			{
				j = (AST)_t;
				match(_t,JOIN_CONDITION);
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					out(j);
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void from(AST _t) throws RecognitionException {
		
		AST from_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST f = null;
		
		try {      // for error handling
			AST __t81 = _t;
			f = _t==ASTNULL ? null :(AST)_t;
			match(_t,FROM);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				out(" from ");
			}
			{
			_loop83:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==TABLE)) {
					fromTable(_t,true);
					_t = _retTree;
				}
				else {
					break _loop83;
				}
				
			} while (true);
			}
			_t = __t81;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void query(AST _t) throws RecognitionException {
		
		AST query_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t39 = _t;
			AST tmp14_AST_in = (AST)_t;
			match(_t,SELECT);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				out("select ");
			}
			selectClause(_t);
			_t = _retTree;
			from(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case WHERE:
			{
				AST __t41 = _t;
				AST tmp15_AST_in = (AST)_t;
				match(_t,WHERE);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					out(" where ");
				}
				whereExpr(_t);
				_t = _retTree;
				_t = __t41;
				_t = _t.getNextSibling();
				break;
			}
			case 3:
			case GROUP:
			case ORDER:
			case LIMIT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case GROUP:
			{
				AST __t43 = _t;
				AST tmp16_AST_in = (AST)_t;
				match(_t,GROUP);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					out(" group by ");
				}
				groupExprs(_t);
				_t = _retTree;
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case HAVING:
				{
					AST __t45 = _t;
					AST tmp17_AST_in = (AST)_t;
					match(_t,HAVING);
					_t = _t.getFirstChild();
					if ( inputState.guessing==0 ) {
						out(" having ");
					}
					booleanExpr(_t,false);
					_t = _retTree;
					_t = __t45;
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
				_t = __t43;
				_t = _t.getNextSibling();
				break;
			}
			case 3:
			case ORDER:
			case LIMIT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ORDER:
			{
				AST __t47 = _t;
				AST tmp18_AST_in = (AST)_t;
				match(_t,ORDER);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					out(" order by ");
				}
				orderExprs(_t);
				_t = _retTree;
				_t = __t47;
				_t = _t.getNextSibling();
				break;
			}
			case 3:
			case LIMIT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LIMIT:
			{
				AST __t49 = _t;
				AST tmp19_AST_in = (AST)_t;
				match(_t,LIMIT);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					out(" limit ");
				}
				selectExpr(_t);
				_t = _retTree;
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case OFFSET:
				{
					AST tmp20_AST_in = (AST)_t;
					match(_t,OFFSET);
					_t = _t.getNextSibling();
					if ( inputState.guessing==0 ) {
						out(" offset ");
					}
					selectExpr(_t);
					_t = _retTree;
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
				_t = __t49;
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
			_t = __t39;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void selectClause(AST _t) throws RecognitionException {
		
		AST selectClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t60 = _t;
			AST tmp21_AST_in = (AST)_t;
			match(_t,SELECT_CLAUSE);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case DISTINCT:
			{
				distinct(_t);
				_t = _retTree;
				break;
			}
			case ARRAY:
			case CASE:
			case COUNT:
			case FALSE:
			case NULL:
			case TRUE:
			case AGGREGATE:
			case ALIAS:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case CONSTANT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
			case COLUMN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			{
			int _cnt63=0;
			_loop63:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_1.member(_t.getType()))) {
					selectColumn(_t);
					_t = _retTree;
				}
				else {
					if ( _cnt63>=1 ) { break _loop63; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt63++;
			} while (true);
			}
			_t = __t60;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void whereExpr(AST _t) throws RecognitionException {
		
		AST whereExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			booleanExpr(_t,false);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void groupExprs(AST _t) throws RecognitionException {
		
		AST groupExprs_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			expr(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ALL:
			case ANY:
			case ARRAY:
			case CASE:
			case COUNT:
			case FALSE:
			case NULL:
			case REF:
			case SELECT:
			case TRUE:
			case AGGREGATE:
			case ALIAS:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case PREDEFINED_TYPE:
			case ARRAY_DEF:
			case CONSTANT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
			case SOME:
			case COLUMN:
			{
				if ( inputState.guessing==0 ) {
					out(", ");
				}
				groupExprs(_t);
				_t = _retTree;
				break;
			}
			case 3:
			case HAVING:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void orderExprs(AST _t) throws RecognitionException {
		
		AST orderExprs_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST dir = null;
		
		try {      // for error handling
			{
			expr(_t);
			_t = _retTree;
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ASCENDING:
			case DESCENDING:
			{
				dir = _t==ASTNULL ? null : (AST)_t;
				orderDirection(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(" "); out(dir);
				}
				break;
			}
			case 3:
			case ALL:
			case ANY:
			case ARRAY:
			case CASE:
			case COUNT:
			case FALSE:
			case NULL:
			case REF:
			case SELECT:
			case TRUE:
			case AGGREGATE:
			case ALIAS:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case PREDEFINED_TYPE:
			case ARRAY_DEF:
			case CONSTANT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
			case SOME:
			case COLUMN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ALL:
			case ANY:
			case ARRAY:
			case CASE:
			case COUNT:
			case FALSE:
			case NULL:
			case REF:
			case SELECT:
			case TRUE:
			case AGGREGATE:
			case ALIAS:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case PREDEFINED_TYPE:
			case ARRAY_DEF:
			case CONSTANT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
			case SOME:
			case COLUMN:
			{
				if ( inputState.guessing==0 ) {
					out(", ");
				}
				orderExprs(_t);
				_t = _retTree;
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
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void selectExpr(AST _t) throws RecognitionException {
		
		AST selectExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST c = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case FALSE:
			case NULL:
			case TRUE:
			case ALIAS:
			case CONSTANT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case NUM_INT:
			case QUOTED_STRING:
			case COLUMN:
			{
				c = _t==ASTNULL ? null : (AST)_t;
				constant(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(c);
				}
				break;
			}
			case COUNT:
			{
				count(_t);
				_t = _retTree;
				break;
			}
			case METHOD_CALL:
			{
				methodCall(_t);
				_t = _retTree;
				break;
			}
			case AGGREGATE:
			{
				aggregate(_t);
				_t = _retTree;
				break;
			}
			case CASE:
			case CASE2:
			case UNARY_MINUS:
			case STAR:
			case PLUS:
			case MINUS:
			case DIV:
			{
				arithmeticExpr(_t);
				_t = _retTree;
				break;
			}
			case ARRAY:
			{
				arrayExpr(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void expr(AST _t) throws RecognitionException {
		
		AST expr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST c = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ARRAY:
			case CASE:
			case COUNT:
			case FALSE:
			case NULL:
			case REF:
			case TRUE:
			case AGGREGATE:
			case ALIAS:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case PREDEFINED_TYPE:
			case ARRAY_DEF:
			case CONSTANT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
			case COLUMN:
			{
				simpleExpr(_t);
				_t = _retTree;
				break;
			}
			case SELECT:
			{
				parenSelect(_t);
				_t = _retTree;
				break;
			}
			case ANY:
			{
				AST __t122 = _t;
				AST tmp22_AST_in = (AST)_t;
				match(_t,ANY);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					out("any ");
				}
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case SELECT:
				{
					quantified(_t);
					_t = _retTree;
					break;
				}
				case COLUMN:
				{
					c = (AST)_t;
					match(_t,COLUMN);
					_t = _t.getNextSibling();
					if ( inputState.guessing==0 ) {
						out("(" + c + ")");
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				_t = __t122;
				_t = _t.getNextSibling();
				break;
			}
			case ALL:
			{
				AST __t124 = _t;
				AST tmp23_AST_in = (AST)_t;
				match(_t,ALL);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					out("all ");
				}
				quantified(_t);
				_t = _retTree;
				_t = __t124;
				_t = _t.getNextSibling();
				break;
			}
			case SOME:
			{
				AST __t125 = _t;
				AST tmp24_AST_in = (AST)_t;
				match(_t,SOME);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					out("some ");
				}
				quantified(_t);
				_t = _retTree;
				_t = __t125;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void orderDirection(AST _t) throws RecognitionException {
		
		AST orderDirection_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ASCENDING:
			{
				AST tmp25_AST_in = (AST)_t;
				match(_t,ASCENDING);
				_t = _t.getNextSibling();
				break;
			}
			case DESCENDING:
			{
				AST tmp26_AST_in = (AST)_t;
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
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void distinct(AST _t) throws RecognitionException {
		
		AST distinct_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST tmp27_AST_in = (AST)_t;
			match(_t,DISTINCT);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				out("distinct ");
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void selectColumn(AST _t) throws RecognitionException {
		
		AST selectColumn_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST p = null;
		
		try {      // for error handling
			p = _t==ASTNULL ? null : (AST)_t;
			selectExpr(_t);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				separator(p,", ");
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void constant(AST _t) throws RecognitionException {
		
		AST constant_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case NUM_DOUBLE:
			{
				AST tmp28_AST_in = (AST)_t;
				match(_t,NUM_DOUBLE);
				_t = _t.getNextSibling();
				break;
			}
			case NUM_FLOAT:
			{
				AST tmp29_AST_in = (AST)_t;
				match(_t,NUM_FLOAT);
				_t = _t.getNextSibling();
				break;
			}
			case NUM_INT:
			{
				AST tmp30_AST_in = (AST)_t;
				match(_t,NUM_INT);
				_t = _t.getNextSibling();
				break;
			}
			case NUM_LONG:
			{
				AST tmp31_AST_in = (AST)_t;
				match(_t,NUM_LONG);
				_t = _t.getNextSibling();
				break;
			}
			case QUOTED_STRING:
			{
				AST tmp32_AST_in = (AST)_t;
				match(_t,QUOTED_STRING);
				_t = _t.getNextSibling();
				break;
			}
			case CONSTANT:
			{
				AST tmp33_AST_in = (AST)_t;
				match(_t,CONSTANT);
				_t = _t.getNextSibling();
				break;
			}
			case TRUE:
			{
				AST tmp34_AST_in = (AST)_t;
				match(_t,TRUE);
				_t = _t.getNextSibling();
				break;
			}
			case FALSE:
			{
				AST tmp35_AST_in = (AST)_t;
				match(_t,FALSE);
				_t = _t.getNextSibling();
				break;
			}
			case NULL:
			{
				AST tmp36_AST_in = (AST)_t;
				match(_t,NULL);
				_t = _t.getNextSibling();
				break;
			}
			case COLUMN:
			{
				AST tmp37_AST_in = (AST)_t;
				match(_t,COLUMN);
				_t = _t.getNextSibling();
				break;
			}
			case ALIAS:
			{
				AST tmp38_AST_in = (AST)_t;
				match(_t,ALIAS);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void count(AST _t) throws RecognitionException {
		
		AST count_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t68 = _t;
			AST tmp39_AST_in = (AST)_t;
			match(_t,COUNT);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				out("count(");
			}
			countExpr(_t);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				out(")");
			}
			_t = __t68;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void methodCall(AST _t) throws RecognitionException {
		
		AST methodCall_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST m = null;
		AST i = null;
		log.debug("METHOD CALL");
		
		try {      // for error handling
			AST __t72 = _t;
			m = _t==ASTNULL ? null :(AST)_t;
			match(_t,METHOD_CALL);
			_t = _t.getFirstChild();
			i = (AST)_t;
			match(_t,METHOD_NAME);
			_t = _t.getNextSibling();
			if ( inputState.guessing==0 ) {
				beginFunctionTemplate(m,i);
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case EXPR_LIST:
			{
				AST __t74 = _t;
				AST tmp40_AST_in = (AST)_t;
				match(_t,EXPR_LIST);
				_t = _t.getFirstChild();
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case ALL:
				case ANY:
				case ARRAY:
				case CASE:
				case COUNT:
				case FALSE:
				case NULL:
				case REF:
				case SELECT:
				case TRUE:
				case AGGREGATE:
				case ALIAS:
				case METHOD_CALL:
				case CASE2:
				case UNARY_MINUS:
				case PREDEFINED_TYPE:
				case ARRAY_DEF:
				case CONSTANT:
				case NUM_DOUBLE:
				case NUM_FLOAT:
				case NUM_LONG:
				case STAR:
				case NUM_INT:
				case QUOTED_STRING:
				case PLUS:
				case MINUS:
				case DIV:
				case SOME:
				case COLUMN:
				{
					arguments(_t);
					_t = _retTree;
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
				_t = __t74;
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
			if ( inputState.guessing==0 ) {
				endFunctionTemplate(m);
			}
			_t = __t72;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void aggregate(AST _t) throws RecognitionException {
		
		AST aggregate_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST a = null;
		
		try {      // for error handling
			AST __t70 = _t;
			a = _t==ASTNULL ? null :(AST)_t;
			match(_t,AGGREGATE);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				out(a); out("(");
			}
			expr(_t);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				out(")");
			}
			_t = __t70;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void arithmeticExpr(AST _t) throws RecognitionException {
		
		AST arithmeticExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case PLUS:
			case MINUS:
			{
				additiveExpr(_t);
				_t = _retTree;
				break;
			}
			case STAR:
			case DIV:
			{
				multiplicativeExpr(_t);
				_t = _retTree;
				break;
			}
			case UNARY_MINUS:
			{
				AST __t141 = _t;
				AST tmp41_AST_in = (AST)_t;
				match(_t,UNARY_MINUS);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					out("-");
				}
				expr(_t);
				_t = _retTree;
				_t = __t141;
				_t = _t.getNextSibling();
				break;
			}
			case CASE:
			case CASE2:
			{
				caseExpr(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void arrayExpr(AST _t) throws RecognitionException {
		
		AST arrayExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST a = null;
		
		try {      // for error handling
			AST __t134 = _t;
			a = _t==ASTNULL ? null :(AST)_t;
			match(_t,ARRAY);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				out(a);
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case 3:
			case ARRAY:
			case CASE:
			case COUNT:
			case FALSE:
			case NULL:
			case REF:
			case TRUE:
			case AGGREGATE:
			case ALIAS:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case PREDEFINED_TYPE:
			case ARRAY_DEF:
			case CONSTANT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
			case COLUMN:
			{
				simpleExprArray(_t);
				_t = _retTree;
				break;
			}
			case SELECT:
			{
				queryExprArray(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t134;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void countExpr(AST _t) throws RecognitionException {
		
		AST countExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ROW_STAR:
			{
				AST tmp42_AST_in = (AST)_t;
				match(_t,ROW_STAR);
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					out("*");
				}
				break;
			}
			case ARRAY:
			case CASE:
			case COUNT:
			case FALSE:
			case NULL:
			case REF:
			case TRUE:
			case AGGREGATE:
			case ALIAS:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case PREDEFINED_TYPE:
			case ARRAY_DEF:
			case CONSTANT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
			case COLUMN:
			{
				simpleExpr(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void arguments(AST _t) throws RecognitionException {
		
		AST arguments_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			expr(_t);
			_t = _retTree;
			{
			_loop78:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_2.member(_t.getType()))) {
					if ( inputState.guessing==0 ) {
						commaBetweenParameters(", ");
					}
					expr(_t);
					_t = _retTree;
				}
				else {
					break _loop78;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void fromTable(AST _t,
		boolean addSeparator
	) throws RecognitionException {
		
		AST fromTable_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST a = null;
		AST j = null;
		
		try {      // for error handling
			AST __t85 = _t;
			a = _t==ASTNULL ? null :(AST)_t;
			match(_t,TABLE);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				out(a);
			}
			{
			_loop90:
			do {
				if (_t==null) _t=ASTNULL;
				if (((_t.getType() >= LEFT_OUTER && _t.getType() <= INNER_JOIN))) {
					{
					if (_t==null) _t=ASTNULL;
					switch ( _t.getType()) {
					case LEFT_OUTER:
					{
						AST tmp43_AST_in = (AST)_t;
						match(_t,LEFT_OUTER);
						_t = _t.getNextSibling();
						if ( inputState.guessing==0 ) {
							out(" left outer join ");
						}
						break;
					}
					case INNER_JOIN:
					{
						AST tmp44_AST_in = (AST)_t;
						match(_t,INNER_JOIN);
						_t = _t.getNextSibling();
						if ( inputState.guessing==0 ) {
							out(" inner join ");
						}
						break;
					}
					case RIGHT_OUTER:
					{
						AST tmp45_AST_in = (AST)_t;
						match(_t,RIGHT_OUTER);
						_t = _t.getNextSibling();
						if ( inputState.guessing==0 ) {
							out(" right outer join ");
						}
						break;
					}
					default:
					{
						throw new NoViableAltException(_t);
					}
					}
					}
					fromTable(_t,false);
					_t = _retTree;
					{
					if (_t==null) _t=ASTNULL;
					switch ( _t.getType()) {
					case JOIN_CONDITION:
					{
						j = (AST)_t;
						match(_t,JOIN_CONDITION);
						_t = _t.getNextSibling();
						if ( inputState.guessing==0 ) {
							out(" " +j.getText());
						}
						break;
					}
					case ON:
					{
						AST __t89 = _t;
						AST tmp46_AST_in = (AST)_t;
						match(_t,ON);
						_t = _t.getFirstChild();
						if ( inputState.guessing==0 ) {
							out (" on ");
						}
						booleanExpr(_t, false );
						_t = _retTree;
						_t = __t89;
						_t = _t.getNextSibling();
						break;
					}
					default:
					{
						throw new NoViableAltException(_t);
					}
					}
					}
				}
				else {
					break _loop90;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				if (addSeparator) fromFragmentSeparator(a);
			}
			_t = __t85;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void booleanOp(AST _t,
		 boolean parens 
	) throws RecognitionException {
		
		AST booleanOp_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case AND:
			{
				AST __t92 = _t;
				AST tmp47_AST_in = (AST)_t;
				match(_t,AND);
				_t = _t.getFirstChild();
				booleanExpr(_t,true);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(" and ");
				}
				booleanExpr(_t,true);
				_t = _retTree;
				_t = __t92;
				_t = _t.getNextSibling();
				break;
			}
			case OR:
			{
				AST __t93 = _t;
				AST tmp48_AST_in = (AST)_t;
				match(_t,OR);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					if (parens) out("(");
				}
				booleanExpr(_t,false);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(" or ");
				}
				booleanExpr(_t,false);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					if (parens) out(")");
				}
				_t = __t93;
				_t = _t.getNextSibling();
				break;
			}
			case NOT:
			{
				AST __t94 = _t;
				AST tmp49_AST_in = (AST)_t;
				match(_t,NOT);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					out(" not (");
				}
				booleanExpr(_t,false);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(")");
				}
				_t = __t94;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void comparisonExpr(AST _t,
		 boolean parens 
	) throws RecognitionException {
		
		AST comparisonExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case EQ:
			case NE:
			case LT:
			case GT:
			case LE:
			case GE:
			{
				binaryComparisonExpression(_t);
				_t = _retTree;
				break;
			}
			case BETWEEN:
			case EXISTS:
			case IN:
			case LIKE:
			case IS_NOT_NULL:
			case IS_NULL:
			case NOT_BETWEEN:
			case NOT_IN:
			case NOT_LIKE:
			{
				if ( inputState.guessing==0 ) {
					if (parens) out("(");
				}
				exoticComparisonExpression(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					if (parens) out(")");
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void binaryComparisonExpression(AST _t) throws RecognitionException {
		
		AST binaryComparisonExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case EQ:
			{
				AST __t98 = _t;
				AST tmp50_AST_in = (AST)_t;
				match(_t,EQ);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out("=");
				}
				expr(_t);
				_t = _retTree;
				_t = __t98;
				_t = _t.getNextSibling();
				break;
			}
			case NE:
			{
				AST __t99 = _t;
				AST tmp51_AST_in = (AST)_t;
				match(_t,NE);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out("<>");
				}
				expr(_t);
				_t = _retTree;
				_t = __t99;
				_t = _t.getNextSibling();
				break;
			}
			case GT:
			{
				AST __t100 = _t;
				AST tmp52_AST_in = (AST)_t;
				match(_t,GT);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(">");
				}
				expr(_t);
				_t = _retTree;
				_t = __t100;
				_t = _t.getNextSibling();
				break;
			}
			case GE:
			{
				AST __t101 = _t;
				AST tmp53_AST_in = (AST)_t;
				match(_t,GE);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(">=");
				}
				expr(_t);
				_t = _retTree;
				_t = __t101;
				_t = _t.getNextSibling();
				break;
			}
			case LT:
			{
				AST __t102 = _t;
				AST tmp54_AST_in = (AST)_t;
				match(_t,LT);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out("<");
				}
				expr(_t);
				_t = _retTree;
				_t = __t102;
				_t = _t.getNextSibling();
				break;
			}
			case LE:
			{
				AST __t103 = _t;
				AST tmp55_AST_in = (AST)_t;
				match(_t,LE);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out("<=");
				}
				expr(_t);
				_t = _retTree;
				_t = __t103;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void exoticComparisonExpression(AST _t) throws RecognitionException {
		
		AST exoticComparisonExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LIKE:
			{
				AST __t105 = _t;
				AST tmp56_AST_in = (AST)_t;
				match(_t,LIKE);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					optionalSpace(); out("like ");
				}
				expr(_t);
				_t = _retTree;
				_t = __t105;
				_t = _t.getNextSibling();
				break;
			}
			case NOT_LIKE:
			{
				AST __t106 = _t;
				AST tmp57_AST_in = (AST)_t;
				match(_t,NOT_LIKE);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					optionalSpace(); out("not like ");
				}
				expr(_t);
				_t = _retTree;
				_t = __t106;
				_t = _t.getNextSibling();
				break;
			}
			case BETWEEN:
			{
				AST __t107 = _t;
				AST tmp58_AST_in = (AST)_t;
				match(_t,BETWEEN);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(" between ");
				}
				expr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(" and ");
				}
				expr(_t);
				_t = _retTree;
				_t = __t107;
				_t = _t.getNextSibling();
				break;
			}
			case NOT_BETWEEN:
			{
				AST __t108 = _t;
				AST tmp59_AST_in = (AST)_t;
				match(_t,NOT_BETWEEN);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(" not between ");
				}
				expr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(" and ");
				}
				expr(_t);
				_t = _retTree;
				_t = __t108;
				_t = _t.getNextSibling();
				break;
			}
			case IN:
			{
				AST __t109 = _t;
				AST tmp60_AST_in = (AST)_t;
				match(_t,IN);
				_t = _t.getFirstChild();
				inLhs(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					optionalSpace(); out("in");
				}
				inList(_t);
				_t = _retTree;
				_t = __t109;
				_t = _t.getNextSibling();
				break;
			}
			case NOT_IN:
			{
				AST __t110 = _t;
				AST tmp61_AST_in = (AST)_t;
				match(_t,NOT_IN);
				_t = _t.getFirstChild();
				inLhs(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					optionalSpace(); out("not in ");
				}
				inList(_t);
				_t = _retTree;
				_t = __t110;
				_t = _t.getNextSibling();
				break;
			}
			case EXISTS:
			{
				AST __t111 = _t;
				AST tmp62_AST_in = (AST)_t;
				match(_t,EXISTS);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					optionalSpace(); out("exists ");
				}
				quantified(_t);
				_t = _retTree;
				_t = __t111;
				_t = _t.getNextSibling();
				break;
			}
			case IS_NULL:
			{
				AST __t112 = _t;
				AST tmp63_AST_in = (AST)_t;
				match(_t,IS_NULL);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				_t = __t112;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					out(" is null");
				}
				break;
			}
			case IS_NOT_NULL:
			{
				AST __t113 = _t;
				AST tmp64_AST_in = (AST)_t;
				match(_t,IS_NOT_NULL);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				_t = __t113;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					out(" is not null");
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void inLhs(AST _t) throws RecognitionException {
		
		AST inLhs_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			expr(_t);
			_t = _retTree;
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void inList(AST _t) throws RecognitionException {
		
		AST inList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t116 = _t;
			AST tmp65_AST_in = (AST)_t;
			match(_t,IN_LIST);
			_t = _t.getFirstChild();
			if ( inputState.guessing==0 ) {
				out(" ");
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case SELECT:
			{
				parenSelect(_t);
				_t = _retTree;
				break;
			}
			case 3:
			case ARRAY:
			case CASE:
			case COUNT:
			case FALSE:
			case NULL:
			case REF:
			case TRUE:
			case AGGREGATE:
			case ALIAS:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case PREDEFINED_TYPE:
			case ARRAY_DEF:
			case CONSTANT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
			case COLUMN:
			{
				simpleExprList(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t116;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void quantified(AST _t) throws RecognitionException {
		
		AST quantified_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				out("(");
			}
			{
			selectStatement(_t);
			_t = _retTree;
			}
			if ( inputState.guessing==0 ) {
				out(")");
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void parenSelect(AST _t) throws RecognitionException {
		
		AST parenSelect_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				out("(");
			}
			selectStatement(_t);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				out(")");
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void simpleExprList(AST _t) throws RecognitionException {
		
		AST simpleExprList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST e = null;
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				out("(");
			}
			{
			_loop120:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_3.member(_t.getType()))) {
					e = _t==ASTNULL ? null : (AST)_t;
					simpleExpr(_t);
					_t = _retTree;
					if ( inputState.guessing==0 ) {
						separator(e," , ");
					}
				}
				else {
					break _loop120;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				out(")");
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void datatypeExpr(AST _t) throws RecognitionException {
		
		AST datatypeExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST p = null;
		AST c = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case PREDEFINED_TYPE:
			{
				p = (AST)_t;
				match(_t,PREDEFINED_TYPE);
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					out(p);
				}
				break;
			}
			case REF:
			{
				AST __t131 = _t;
				AST tmp66_AST_in = (AST)_t;
				match(_t,REF);
				_t = _t.getFirstChild();
				c = _t==ASTNULL ? null : (AST)_t;
				constant(_t);
				_t = _retTree;
				_t = __t131;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					out("ref "); out(c);
				}
				break;
			}
			case ARRAY_DEF:
			{
				AST __t132 = _t;
				AST tmp67_AST_in = (AST)_t;
				match(_t,ARRAY_DEF);
				_t = _t.getFirstChild();
				datatypeExpr(_t);
				_t = _retTree;
				_t = __t132;
				_t = _t.getNextSibling();
				if ( inputState.guessing==0 ) {
					out("array ");
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void simpleExprArray(AST _t) throws RecognitionException {
		
		AST simpleExprArray_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST e = null;
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				out("[");
			}
			{
			_loop138:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_3.member(_t.getType()))) {
					e = _t==ASTNULL ? null : (AST)_t;
					simpleExpr(_t);
					_t = _retTree;
					if ( inputState.guessing==0 ) {
						separator(e," , ");
					}
				}
				else {
					break _loop138;
				}
				
			} while (true);
			}
			if ( inputState.guessing==0 ) {
				out("]");
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void queryExprArray(AST _t) throws RecognitionException {
		
		AST queryExprArray_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if ( inputState.guessing==0 ) {
				out("(");
			}
			query(_t);
			_t = _retTree;
			if ( inputState.guessing==0 ) {
				out(")");
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void additiveExpr(AST _t) throws RecognitionException {
		
		AST additiveExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case PLUS:
			{
				AST __t143 = _t;
				AST tmp68_AST_in = (AST)_t;
				match(_t,PLUS);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out("+");
				}
				expr(_t);
				_t = _retTree;
				_t = __t143;
				_t = _t.getNextSibling();
				break;
			}
			case MINUS:
			{
				AST __t144 = _t;
				AST tmp69_AST_in = (AST)_t;
				match(_t,MINUS);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out("-");
				}
				expr(_t);
				_t = _retTree;
				_t = __t144;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void multiplicativeExpr(AST _t) throws RecognitionException {
		
		AST multiplicativeExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case DIV:
			{
				AST __t146 = _t;
				AST tmp70_AST_in = (AST)_t;
				match(_t,DIV);
				_t = _t.getFirstChild();
				nestedExpr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out("/");
				}
				nestedExpr(_t);
				_t = _retTree;
				_t = __t146;
				_t = _t.getNextSibling();
				break;
			}
			case STAR:
			{
				AST __t147 = _t;
				AST tmp71_AST_in = (AST)_t;
				match(_t,STAR);
				_t = _t.getFirstChild();
				nestedExpr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out("*");
				}
				nestedExpr(_t);
				_t = _retTree;
				_t = __t147;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void caseExpr(AST _t) throws RecognitionException {
		
		AST caseExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CASE:
			{
				AST __t152 = _t;
				AST tmp72_AST_in = (AST)_t;
				match(_t,CASE);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					out("case");
				}
				{
				int _cnt155=0;
				_loop155:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==WHEN)) {
						AST __t154 = _t;
						AST tmp73_AST_in = (AST)_t;
						match(_t,WHEN);
						_t = _t.getFirstChild();
						if ( inputState.guessing==0 ) {
							out( " when ");
						}
						booleanExpr(_t,false);
						_t = _retTree;
						if ( inputState.guessing==0 ) {
							out(" then ");
						}
						expr(_t);
						_t = _retTree;
						_t = __t154;
						_t = _t.getNextSibling();
					}
					else {
						if ( _cnt155>=1 ) { break _loop155; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt155++;
				} while (true);
				}
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case ELSE:
				{
					AST __t157 = _t;
					AST tmp74_AST_in = (AST)_t;
					match(_t,ELSE);
					_t = _t.getFirstChild();
					if ( inputState.guessing==0 ) {
						out(" else ");
					}
					expr(_t);
					_t = _retTree;
					_t = __t157;
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
				if ( inputState.guessing==0 ) {
					out(" end");
				}
				_t = __t152;
				_t = _t.getNextSibling();
				break;
			}
			case CASE2:
			{
				AST __t158 = _t;
				AST tmp75_AST_in = (AST)_t;
				match(_t,CASE2);
				_t = _t.getFirstChild();
				if ( inputState.guessing==0 ) {
					out("case ");
				}
				expr(_t);
				_t = _retTree;
				{
				int _cnt161=0;
				_loop161:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==WHEN)) {
						AST __t160 = _t;
						AST tmp76_AST_in = (AST)_t;
						match(_t,WHEN);
						_t = _t.getFirstChild();
						if ( inputState.guessing==0 ) {
							out( " when ");
						}
						expr(_t);
						_t = _retTree;
						if ( inputState.guessing==0 ) {
							out(" then ");
						}
						expr(_t);
						_t = _retTree;
						_t = __t160;
						_t = _t.getNextSibling();
					}
					else {
						if ( _cnt161>=1 ) { break _loop161; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt161++;
				} while (true);
				}
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case ELSE:
				{
					AST __t163 = _t;
					AST tmp77_AST_in = (AST)_t;
					match(_t,ELSE);
					_t = _t.getFirstChild();
					if ( inputState.guessing==0 ) {
						out(" else ");
					}
					expr(_t);
					_t = _retTree;
					_t = __t163;
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
				if ( inputState.guessing==0 ) {
					out(" end");
				}
				_t = __t158;
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
		_retTree = _t;
	}
	
	public final void nestedExpr(AST _t) throws RecognitionException {
		
		AST nestedExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			boolean synPredMatched150 = false;
			if (_t==null) _t=ASTNULL;
			if (((_t.getType()==PLUS||_t.getType()==MINUS))) {
				AST __t150 = _t;
				synPredMatched150 = true;
				inputState.guessing++;
				try {
					{
					arithmeticExpr(_t);
					_t = _retTree;
					}
				}
				catch (RecognitionException pe) {
					synPredMatched150 = false;
				}
				_t = __t150;
inputState.guessing--;
			}
			if ( synPredMatched150 ) {
				if ( inputState.guessing==0 ) {
					out("(");
				}
				additiveExpr(_t);
				_t = _retTree;
				if ( inputState.guessing==0 ) {
					out(")");
				}
			}
			else if ((_tokenSet_2.member(_t.getType()))) {
				expr(_t);
				_t = _retTree;
			}
			else {
				throw new NoViableAltException(_t);
			}
			
		}
		catch (RecognitionException ex) {
			if (inputState.guessing==0) {
				reportError(ex);
				if (_t!=null) {_t = _t.getNextSibling();}
			} else {
			  throw ex;
			}
		}
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
		"SELECT_CLAUSE",
		"LEFT_OUTER",
		"RIGHT_OUTER",
		"INNER_JOIN",
		"JOIN_CONDITION",
		"METHOD_NAME",
		"SOME",
		"COLUMN",
		"TABLE"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 288230384742040064L, 6989674589051683880L, 140737547086351L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 288230384742040064L, 72145561410601984L, 140737547086351L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 288230384742040352L, 6989674589051683880L, 211106291264015L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 288230384742040064L, 6989674589051683848L, 140737547086351L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	}
	
