// $ANTLR 2.7.7 (20060906): "OntoQL-semantique.g" -> "OntoQLSQLBaseWalker.java"$

	
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

import fr.ensma.lisi.ontoql.engine.tree.IdentNode;

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
public class OntoQLSQLBaseWalker extends antlr.TreeParser       implements OntoQLSQLTokenTypes
 {

private static Log log = LogFactory.getLog(OntoQLSQLBaseWalker.class);

	private int level = 0;
	private boolean inSelect = false;
	private boolean inFrom = false;
	private boolean inWhere = false;
	private boolean inCase = false;
	private boolean inFunctionCall = false;

	private int statementType;
	private String statementTypeName;
	
	// currentClauseType tracks the current clause within the current
	// statement, regardless of level; currentTopLevelClauseType, on the other
	// hand, tracks the current clause within the top (or primary) statement.
	// Thus, currentTopLevelClauseType ignores the clauses from any subqueries.
	private int currentClauseType;
	private int currentTopLevelClauseType;
	private int currentStatementType;

	public final boolean isSubQuery() {
		return level > 1;
	}

	public final boolean isInFrom() {
		return inFrom;
	}

	public final boolean isInSelect() {
		return inSelect;
	}
	
	public final boolean isInWhere() {
		return inWhere;
	}
	
	public final boolean isInFunctionCall() {
		return inFunctionCall;
	}
	
	public final boolean isInCase() {
		return inCase;
	}

	public final int getStatementType() {
		return statementType;
	}

	public final int getCurrentClauseType() {
		return currentClauseType;
	}
	
	public final int getCurrentStatementType() {
		return currentStatementType;
	}

	public final int getCurrentTopLevelClauseType() {
		return currentTopLevelClauseType;
	}

	public final boolean isSelectStatement() {
		return statementType == SELECT;
	}
	
	public final boolean isDMLStatement() {
		return (statementType == UPDATE ||
		        statementType == INSERT || 
		        statementType == DELETE);
	}
	
	public final boolean isDDLStatement() {
		return (statementType == CREATE ||
		        statementType == ALTER || 
		        statementType == SET || 
		        statementType == DROP);
	}
	
	public final boolean isCurrentSelectStatement() {
		return currentClauseType == SELECT;
	}

	private void beforeStatement(String statementName, int statementType) {
		level++;
		if ( level == 1 ) {
			this.statementTypeName = statementName;
			this.statementType = statementType;
		}
		currentStatementType = statementType;
		if ( log.isDebugEnabled() ) {
			log.debug( statementName + " << begin [level=" + level + ", statement=" + this.statementTypeName + "]" );
		}
	}

	private void beforeStatementCompletion(String statementName) {
		if ( log.isDebugEnabled() ) {
			log.debug( statementName + " : finishing up [level=" + level + ", statement=" + statementTypeName + "]" );
		}
	}

	private void afterStatementCompletion(String statementName) {
		if ( log.isDebugEnabled() ) {
			log.debug( statementName + " >> end [level=" + level + ", statement=" + statementTypeName + "]" );
		}
		level--;
	}

	private void handleClauseStart(int clauseType) {
		currentClauseType = clauseType;
		if ( level == 1 ) {
			currentTopLevelClauseType = clauseType;
		}
	}
	
	protected void reinitWalker() { }
	
	protected void processQuery(AST select,AST query) throws SemanticException { }
	
	protected void postProcessCreate(AST create) throws SemanticException { }
	
	protected void postProcessAlter(AST alter) throws SemanticException { }
	
	protected void postProcessDrop(AST drop) throws SemanticException { }

	///////////////////////////////////////////////////////////////////////////
	// The real implementations for the following are in the subclass.
	
	/////////////////////
	// Methods for DML //
	/////////////////////
	
	protected void postProcessInsert(AST insert) throws SemanticException { }
	
	/** Set the current 'INTO' context. **/
	protected void setIntoClause(AST intoClause) {}
	
	/** Set the current 'VALUES' context. **/
	protected void setValuesClause(AST valuesClause) {}
	
	/** Add type of an instance value in Upadate Statement **/
    protected AST addTypeOfUpdate(AST assignmentNode) {return null;}
    
   	/** process the class or entity instanciated in an INTO clause of an INSERT statement. **/
	protected void processInsertTarget(AST intoClause, AST element) throws SemanticException {}
	
	/** add value in an INSERT statement. **/
	protected void addValueInInsert(AST valueElement) throws SemanticException {}
	
	/** add a subQuery in an INSERT statement. **/
	protected void addQueryInInsert(AST queryElement) throws SemanticException {}
	
	/** process a property or an attribute valuated in an INTO clause of an INSERT statement. **/
	protected void processInsertColumnElement(AST intoColumnElement) throws SemanticException {}
	
	/*
	 * belaidn - insert in the association table for the update statement
	 */
	protected void postProcessUpdate(AST updateStatement) {}
	
	////////////////////////
	// Methods for SELECT //
	////////////////////////
	/** Sets the current 'FROM' context. **/
	protected void pushFromClause(AST fromClause,AST inputFromNode) {}
	
	protected AST createFromElement(AST node,AST alias, AST polymorph, boolean genAlias) throws SemanticException {
		return null;
	}

	protected AST resolve(AST node, AST prefix) throws SemanticException { return null;}

    protected void setAlias(AST selectExpr, AST ident) { }
    
    protected void checkType(AST nodeLeft, AST operand, AST nodeRight) { }
    
   protected void processFunction(AST functionCall,boolean inSelect) throws SemanticException { }
     
   protected void addSelectExpr(AST node) { }
   
   /** unnest a collection in a from clause. */
   protected AST unnest(AST propertyNode, AST aliasNode){return null;}
   
   /** resolve a typeof expression. */
   protected AST resolveTypeOf(AST instanceAliasNode){return null;}
   
   /** Translate IS OF predicate into SQL predicate. **/
   protected AST resolveIsOf(AST instanceNode, boolean neg) {return null;}
   
   /** Downcast an instance (TREAT function) **/
   protected AST resolveTreatFunction(AST function) throws SemanticException {return null;}
	
	/** add a Where clause to the current query. */
	protected AST addWhereClause(AST whereClauseBuilt, AST logicalExpression) {
		return null;
	}
	
	/** Define a given namespace as the default local namespace for a statement. */
	protected void setLocalNamespace(AST nodeNamespace, AST nodeAliasNamespace) {}
	
	/** Define a given namespace as the global default namespace. */
	protected void setGlobalNamespace(AST nodeNamespace) {}
	
	/** Define a given language as the global default language */
	protected void setGlobalLanguage(AST nodeLanguage) {}
	
	protected void rewriteQueryWithPreference(AST nodePreference) {}
public OntoQLSQLBaseWalker() {
	tokenNames = _tokenNames;
}

	public final void statement(AST _t) throws RecognitionException {
		
		AST statement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statement_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ALTER:
			case CREATE:
			case DROP:
			case SET:
			{
				ddlStatement(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case EXCEPT:
			case INTERSECT:
			case UNION:
			case QUERY:
			{
				queryExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case INSERT:
			{
				insertStatement(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case UPDATE:
			{
				updateStatement(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
				break;
			}
			case DELETE:
			{
				deleteStatement(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				statement_AST = (AST)currentAST.root;
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
		returnAST = statement_AST;
		_retTree = _t;
	}
	
	public final void ddlStatement(AST _t) throws RecognitionException {
		
		AST ddlStatement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ddlStatement_AST = null;
		
		try {      // for error handling
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CREATE:
			{
				createStatement(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case ALTER:
			{
				alterStatement(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case DROP:
			{
				dropStatement(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case SET:
			{
				parameterStatement(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			ddlStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = ddlStatement_AST;
		_retTree = _t;
	}
	
	public final void queryExpression(AST _t) throws RecognitionException {
		
		AST queryExpression_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST queryExpression_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case UNION:
			{
				AST __t116 = _t;
				AST tmp1_AST = null;
				AST tmp1_AST_in = null;
				tmp1_AST = astFactory.create((AST)_t);
				tmp1_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp1_AST);
				ASTPair __currentAST116 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,UNION);
				_t = _t.getFirstChild();
				queryExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				reinitWalker();
				queryExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST116;
				_t = __t116;
				_t = _t.getNextSibling();
				queryExpression_AST = (AST)currentAST.root;
				break;
			}
			case EXCEPT:
			{
				AST __t117 = _t;
				AST tmp2_AST = null;
				AST tmp2_AST_in = null;
				tmp2_AST = astFactory.create((AST)_t);
				tmp2_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp2_AST);
				ASTPair __currentAST117 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,EXCEPT);
				_t = _t.getFirstChild();
				queryExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				queryExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST117;
				_t = __t117;
				_t = _t.getNextSibling();
				queryExpression_AST = (AST)currentAST.root;
				break;
			}
			case INTERSECT:
			{
				AST __t118 = _t;
				AST tmp3_AST = null;
				AST tmp3_AST_in = null;
				tmp3_AST = astFactory.create((AST)_t);
				tmp3_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp3_AST);
				ASTPair __currentAST118 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,INTERSECT);
				_t = _t.getFirstChild();
				queryExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				queryExpression(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST118;
				_t = __t118;
				_t = _t.getNextSibling();
				queryExpression_AST = (AST)currentAST.root;
				break;
			}
			case QUERY:
			{
				selectStatement(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				queryExpression_AST = (AST)currentAST.root;
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
		returnAST = queryExpression_AST;
		_retTree = _t;
	}
	
	public final void insertStatement(AST _t) throws RecognitionException {
		
		AST insertStatement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST insertStatement_AST = null;
		AST v_AST = null;
		AST v = null;
		
		try {      // for error handling
			AST __t81 = _t;
			AST tmp4_AST = null;
			AST tmp4_AST_in = null;
			tmp4_AST = astFactory.create((AST)_t);
			tmp4_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp4_AST);
			ASTPair __currentAST81 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,INSERT);
			_t = _t.getFirstChild();
			beforeStatement( "insert", INSERT );
			intoClause(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case QUERY:
			{
				query(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case VALUES:
			{
				v = _t==ASTNULL ? null : (AST)_t;
				valueClause(_t);
				_t = _retTree;
				v_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			currentAST = __currentAST81;
			_t = __t81;
			_t = _t.getNextSibling();
			insertStatement_AST = (AST)currentAST.root;
			
					beforeStatementCompletion( "insert" );
					postProcessInsert( insertStatement_AST );
					afterStatementCompletion( "insert" );
				
			insertStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = insertStatement_AST;
		_retTree = _t;
	}
	
	public final void updateStatement(AST _t) throws RecognitionException {
		
		AST updateStatement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST updateStatement_AST = null;
		AST u = null;
		AST u_AST = null;
		AST f_AST = null;
		AST f = null;
		AST s_AST = null;
		AST s = null;
		AST w_AST = null;
		AST w = null;
		
		try {      // for error handling
			AST __t90 = _t;
			u = _t==ASTNULL ? null :(AST)_t;
			AST u_AST_in = null;
			u_AST = astFactory.create(u);
			astFactory.addASTChild(currentAST, u_AST);
			ASTPair __currentAST90 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,UPDATE);
			_t = _t.getFirstChild();
			beforeStatement( "update", UPDATE );
			f = _t==ASTNULL ? null : (AST)_t;
			dmlFromClause(_t);
			_t = _retTree;
			f_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			s = _t==ASTNULL ? null : (AST)_t;
			setClause(_t);
			_t = _retTree;
			s_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case WHERE:
			{
				w = _t==ASTNULL ? null : (AST)_t;
				whereClause(_t);
				_t = _retTree;
				w_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
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
			currentAST = __currentAST90;
			_t = __t90;
			_t = _t.getNextSibling();
			updateStatement_AST = (AST)currentAST.root;
			
				    beforeStatementCompletion( "update" );
					postProcessUpdate(updateStatement_AST);
					afterStatementCompletion( "update" );
				
			updateStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = updateStatement_AST;
		_retTree = _t;
	}
	
	public final void deleteStatement(AST _t) throws RecognitionException {
		
		AST deleteStatement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST deleteStatement_AST = null;
		
		try {      // for error handling
			AST __t108 = _t;
			AST tmp5_AST = null;
			AST tmp5_AST_in = null;
			tmp5_AST = astFactory.create((AST)_t);
			tmp5_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp5_AST);
			ASTPair __currentAST108 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,DELETE);
			_t = _t.getFirstChild();
			beforeStatement( "delete", DELETE );
			dmlFromClause(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case WHERE:
			{
				whereClause(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
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
			currentAST = __currentAST108;
			_t = __t108;
			_t = _t.getNextSibling();
			deleteStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = deleteStatement_AST;
		_retTree = _t;
	}
	
	public final void createStatement(AST _t) throws RecognitionException {
		
		AST createStatement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST createStatement_AST = null;
		
		try {      // for error handling
			AST __t5 = _t;
			AST tmp6_AST = null;
			AST tmp6_AST_in = null;
			tmp6_AST = astFactory.create((AST)_t);
			tmp6_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp6_AST);
			ASTPair __currentAST5 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,CREATE);
			_t = _t.getFirstChild();
			beforeStatement( "create", CREATE );
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ONTOLOGY_MODEL_ID:
			{
				ontologyDefinition(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EXTENT:
			{
				ddlExtent(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case VIEW:
			{
				viewDefinition(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case ENTITY:
			{
				entityDefinition(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			currentAST = __currentAST5;
			_t = __t5;
			_t = _t.getNextSibling();
			createStatement_AST = (AST)currentAST.root;
			
					beforeStatementCompletion( "create" );
					postProcessCreate( createStatement_AST );
					afterStatementCompletion( "create" );
				
			createStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = createStatement_AST;
		_retTree = _t;
	}
	
	public final void alterStatement(AST _t) throws RecognitionException {
		
		AST alterStatement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST alterStatement_AST = null;
		
		try {      // for error handling
			AST __t59 = _t;
			AST tmp7_AST = null;
			AST tmp7_AST_in = null;
			tmp7_AST = astFactory.create((AST)_t);
			tmp7_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp7_AST);
			ASTPair __currentAST59 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ALTER);
			_t = _t.getFirstChild();
			beforeStatement( "alter", ALTER );
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ENTITY:
			{
				alterEntity(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case ONTOLOGY_MODEL_ID:
			{
				alterClass(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EXTENT:
			{
				alterExtent(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			currentAST = __currentAST59;
			_t = __t59;
			_t = _t.getNextSibling();
			alterStatement_AST = (AST)currentAST.root;
			
					beforeStatementCompletion( "alter" );
					postProcessAlter( alterStatement_AST );
					afterStatementCompletion( "alter" );
				
			alterStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = alterStatement_AST;
		_retTree = _t;
	}
	
	public final void dropStatement(AST _t) throws RecognitionException {
		
		AST dropStatement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dropStatement_AST = null;
		
		try {      // for error handling
			AST __t70 = _t;
			AST tmp8_AST = null;
			AST tmp8_AST_in = null;
			tmp8_AST = astFactory.create((AST)_t);
			tmp8_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp8_AST);
			ASTPair __currentAST70 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,DROP);
			_t = _t.getFirstChild();
			beforeStatement( "drop", DROP );
			dropEntity(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST70;
			_t = __t70;
			_t = _t.getNextSibling();
			dropStatement_AST = (AST)currentAST.root;
			
					beforeStatementCompletion( "drop" );
					postProcessDrop( dropStatement_AST );
					afterStatementCompletion( "drop" );
				
			dropStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = dropStatement_AST;
		_retTree = _t;
	}
	
	public final void parameterStatement(AST _t) throws RecognitionException {
		
		AST parameterStatement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parameterStatement_AST = null;
		
		try {      // for error handling
			AST __t74 = _t;
			AST tmp9_AST = null;
			AST tmp9_AST_in = null;
			tmp9_AST = astFactory.create((AST)_t);
			tmp9_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp9_AST);
			ASTPair __currentAST74 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,SET);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case NAMESPACE:
			{
				namespaceSpecification(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LANGUAGE:
			{
				languageSpecification(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			currentAST = __currentAST74;
			_t = __t74;
			_t = _t.getNextSibling();
			parameterStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = parameterStatement_AST;
		_retTree = _t;
	}
	
	public final void ontologyDefinition(AST _t) throws RecognitionException {
		
		AST ontologyDefinition_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ontologyDefinition_AST = null;
		
		try {      // for error handling
			ontologyDefinitionHead(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			ontologyDefinitionBody(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			ontologyDefinition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = ontologyDefinition_AST;
		_retTree = _t;
	}
	
	public final void ddlExtent(AST _t) throws RecognitionException {
		
		AST ddlExtent_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ddlExtent_AST = null;
		
		try {      // for error handling
			AST __t27 = _t;
			AST tmp10_AST = null;
			AST tmp10_AST_in = null;
			tmp10_AST = astFactory.create((AST)_t);
			tmp10_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp10_AST);
			ASTPair __currentAST27 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,EXTENT);
			_t = _t.getFirstChild();
			identifier(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop29:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==IDENT)) {
					identifier(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop29;
				}
				
			} while (true);
			}
			currentAST = __currentAST27;
			_t = __t27;
			_t = _t.getNextSibling();
			ddlExtent_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = ddlExtent_AST;
		_retTree = _t;
	}
	
	public final void viewDefinition(AST _t) throws RecognitionException {
		
		AST viewDefinition_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST viewDefinition_AST = null;
		
		try {      // for error handling
			AST __t31 = _t;
			AST tmp11_AST = null;
			AST tmp11_AST_in = null;
			tmp11_AST = astFactory.create((AST)_t);
			tmp11_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp11_AST);
			ASTPair __currentAST31 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,VIEW);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case IDENT:
			{
				identifier(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case OF:
			{
				typedClause(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			query(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST31;
			_t = __t31;
			_t = _t.getNextSibling();
			viewDefinition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = viewDefinition_AST;
		_retTree = _t;
	}
	
	public final void entityDefinition(AST _t) throws RecognitionException {
		
		AST entityDefinition_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST entityDefinition_AST = null;
		
		try {      // for error handling
			AST __t8 = _t;
			AST tmp12_AST = null;
			AST tmp12_AST_in = null;
			tmp12_AST = astFactory.create((AST)_t);
			tmp12_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp12_AST);
			ASTPair __currentAST8 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ENTITY);
			_t = _t.getFirstChild();
			AST tmp13_AST = null;
			AST tmp13_AST_in = null;
			tmp13_AST = astFactory.create((AST)_t);
			tmp13_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp13_AST);
			match(_t,ONTOLOGY_MODEL_ID);
			_t = _t.getNextSibling();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case UNDER:
			{
				subEntityClause(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case 3:
			case ATTRIBUTES:
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
			case ATTRIBUTES:
			{
				attributesClause(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
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
			currentAST = __currentAST8;
			_t = __t8;
			_t = _t.getNextSibling();
			entityDefinition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = entityDefinition_AST;
		_retTree = _t;
	}
	
	public final void subEntityClause(AST _t) throws RecognitionException {
		
		AST subEntityClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST subEntityClause_AST = null;
		
		try {      // for error handling
			AST __t12 = _t;
			AST tmp14_AST = null;
			AST tmp14_AST_in = null;
			tmp14_AST = astFactory.create((AST)_t);
			tmp14_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp14_AST);
			ASTPair __currentAST12 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,UNDER);
			_t = _t.getFirstChild();
			AST tmp15_AST = null;
			AST tmp15_AST_in = null;
			tmp15_AST = astFactory.create((AST)_t);
			tmp15_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp15_AST);
			match(_t,ONTOLOGY_MODEL_ID);
			_t = _t.getNextSibling();
			currentAST = __currentAST12;
			_t = __t12;
			_t = _t.getNextSibling();
			subEntityClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = subEntityClause_AST;
		_retTree = _t;
	}
	
	public final void attributesClause(AST _t) throws RecognitionException {
		
		AST attributesClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST attributesClause_AST = null;
		
		try {      // for error handling
			AST __t14 = _t;
			AST tmp16_AST = null;
			AST tmp16_AST_in = null;
			tmp16_AST = astFactory.create((AST)_t);
			tmp16_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp16_AST);
			ASTPair __currentAST14 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ATTRIBUTES);
			_t = _t.getFirstChild();
			attributeDefinition(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop16:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==ATTRIBUTE_DEF)) {
					attributeDefinition(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop16;
				}
				
			} while (true);
			}
			currentAST = __currentAST14;
			_t = __t14;
			_t = _t.getNextSibling();
			attributesClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = attributesClause_AST;
		_retTree = _t;
	}
	
	public final void attributeDefinition(AST _t) throws RecognitionException {
		
		AST attributeDefinition_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST attributeDefinition_AST = null;
		
		try {      // for error handling
			AST __t18 = _t;
			AST tmp17_AST = null;
			AST tmp17_AST_in = null;
			tmp17_AST = astFactory.create((AST)_t);
			tmp17_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp17_AST);
			ASTPair __currentAST18 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ATTRIBUTE_DEF);
			_t = _t.getFirstChild();
			AST tmp18_AST = null;
			AST tmp18_AST_in = null;
			tmp18_AST = astFactory.create((AST)_t);
			tmp18_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp18_AST);
			match(_t,ONTOLOGY_MODEL_ID);
			_t = _t.getNextSibling();
			datatype(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST18;
			_t = __t18;
			_t = _t.getNextSibling();
			attributeDefinition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = attributeDefinition_AST;
		_retTree = _t;
	}
	
	public final void datatype(AST _t) throws RecognitionException {
		
		AST datatype_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST datatype_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case PREDEFINED_TYPE:
			{
				AST __t205 = _t;
				AST tmp19_AST = null;
				AST tmp19_AST_in = null;
				tmp19_AST = astFactory.create((AST)_t);
				tmp19_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp19_AST);
				ASTPair __currentAST205 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,PREDEFINED_TYPE);
				_t = _t.getFirstChild();
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case MULTILINGUAL:
				{
					AST tmp20_AST = null;
					AST tmp20_AST_in = null;
					tmp20_AST = astFactory.create((AST)_t);
					tmp20_AST_in = (AST)_t;
					astFactory.addASTChild(currentAST, tmp20_AST);
					match(_t,MULTILINGUAL);
					_t = _t.getNextSibling();
					break;
				}
				case IN_LIST:
				{
					inRhs(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
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
				currentAST = __currentAST205;
				_t = __t205;
				_t = _t.getNextSibling();
				datatype_AST = (AST)currentAST.root;
				break;
			}
			case REF:
			{
				AST __t207 = _t;
				AST tmp21_AST = null;
				AST tmp21_AST_in = null;
				tmp21_AST = astFactory.create((AST)_t);
				tmp21_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp21_AST);
				ASTPair __currentAST207 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,REF);
				_t = _t.getFirstChild();
				identifier(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST207;
				_t = __t207;
				_t = _t.getNextSibling();
				datatype_AST = (AST)currentAST.root;
				break;
			}
			case ARRAY_DEF:
			{
				AST __t208 = _t;
				AST tmp22_AST = null;
				AST tmp22_AST_in = null;
				tmp22_AST = astFactory.create((AST)_t);
				tmp22_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp22_AST);
				ASTPair __currentAST208 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,ARRAY_DEF);
				_t = _t.getFirstChild();
				datatype(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST208;
				_t = __t208;
				_t = _t.getNextSibling();
				datatype_AST = (AST)currentAST.root;
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
		returnAST = datatype_AST;
		_retTree = _t;
	}
	
	public final void ontologyDefinitionHead(AST _t) throws RecognitionException {
		
		AST ontologyDefinitionHead_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ontologyDefinitionHead_AST = null;
		
		try {      // for error handling
			AST __t36 = _t;
			AST tmp23_AST = null;
			AST tmp23_AST_in = null;
			tmp23_AST = astFactory.create((AST)_t);
			tmp23_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp23_AST);
			ASTPair __currentAST36 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ONTOLOGY_MODEL_ID);
			_t = _t.getFirstChild();
			identifier(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case UNDER:
			case CONTEXT:
			{
				optionalHeadClause(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
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
			currentAST = __currentAST36;
			_t = __t36;
			_t = _t.getNextSibling();
			ontologyDefinitionHead_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = ontologyDefinitionHead_AST;
		_retTree = _t;
	}
	
	public final void ontologyDefinitionBody(AST _t) throws RecognitionException {
		
		AST ontologyDefinitionBody_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ontologyDefinitionBody_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case 3:
			case DESCRIPTOR:
			case PROPERTIES:
			{
				classDefinitionBody(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				ontologyDefinitionBody_AST = (AST)currentAST.root;
				break;
			}
			case CASEOF:
			{
				aPosterioriCaseOfBody(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				ontologyDefinitionBody_AST = (AST)currentAST.root;
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
		returnAST = ontologyDefinitionBody_AST;
		_retTree = _t;
	}
	
	public final void classDefinitionBody(AST _t) throws RecognitionException {
		
		AST classDefinitionBody_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST classDefinitionBody_AST = null;
		
		try {      // for error handling
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case DESCRIPTOR:
			{
				descriptorClause(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case 3:
			case PROPERTIES:
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
			case PROPERTIES:
			{
				propertiesClause(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
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
			classDefinitionBody_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = classDefinitionBody_AST;
		_retTree = _t;
	}
	
	public final void aPosterioriCaseOfBody(AST _t) throws RecognitionException {
		
		AST aPosterioriCaseOfBody_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST aPosterioriCaseOfBody_AST = null;
		
		try {      // for error handling
			AST __t25 = _t;
			AST tmp24_AST = null;
			AST tmp24_AST_in = null;
			tmp24_AST = astFactory.create((AST)_t);
			tmp24_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp24_AST);
			ASTPair __currentAST25 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,CASEOF);
			_t = _t.getFirstChild();
			identifier(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST25;
			_t = __t25;
			_t = _t.getNextSibling();
			aPosterioriCaseOfBody_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = aPosterioriCaseOfBody_AST;
		_retTree = _t;
	}
	
	public final void descriptorClause(AST _t) throws RecognitionException {
		
		AST descriptorClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST descriptorClause_AST = null;
		
		try {      // for error handling
			AST __t42 = _t;
			AST tmp25_AST = null;
			AST tmp25_AST_in = null;
			tmp25_AST = astFactory.create((AST)_t);
			tmp25_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp25_AST);
			ASTPair __currentAST42 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,DESCRIPTOR);
			_t = _t.getFirstChild();
			assignmentAttributeList(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST42;
			_t = __t42;
			_t = _t.getNextSibling();
			descriptorClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = descriptorClause_AST;
		_retTree = _t;
	}
	
	public final void propertiesClause(AST _t) throws RecognitionException {
		
		AST propertiesClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST propertiesClause_AST = null;
		
		try {      // for error handling
			AST __t44 = _t;
			AST tmp26_AST = null;
			AST tmp26_AST_in = null;
			tmp26_AST = astFactory.create((AST)_t);
			tmp26_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp26_AST);
			ASTPair __currentAST44 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,PROPERTIES);
			_t = _t.getFirstChild();
			propertyDef(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop46:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==PROPERTY_DEF)) {
					propertyDef(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop46;
				}
				
			} while (true);
			}
			currentAST = __currentAST44;
			_t = __t44;
			_t = _t.getNextSibling();
			propertiesClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = propertiesClause_AST;
		_retTree = _t;
	}
	
	public final void identifier(AST _t) throws RecognitionException {
		
		AST identifier_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST identifier_AST = null;
		AST n = null;
		AST n_AST = null;
		
		try {      // for error handling
			AST __t202 = _t;
			AST tmp27_AST = null;
			AST tmp27_AST_in = null;
			tmp27_AST = astFactory.create((AST)_t);
			tmp27_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp27_AST);
			ASTPair __currentAST202 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,IDENT);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case NAMESPACE_ALIAS:
			{
				n = (AST)_t;
				AST n_AST_in = null;
				n_AST = astFactory.create(n);
				astFactory.addASTChild(currentAST, n_AST);
				match(_t,NAMESPACE_ALIAS);
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
			currentAST = __currentAST202;
			_t = __t202;
			_t = _t.getNextSibling();
			identifier_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = identifier_AST;
		_retTree = _t;
	}
	
	public final void typedClause(AST _t) throws RecognitionException {
		
		AST typedClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typedClause_AST = null;
		
		try {      // for error handling
			AST __t34 = _t;
			AST tmp28_AST = null;
			AST tmp28_AST_in = null;
			tmp28_AST = astFactory.create((AST)_t);
			tmp28_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp28_AST);
			ASTPair __currentAST34 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,OF);
			_t = _t.getFirstChild();
			identifier(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST34;
			_t = __t34;
			_t = _t.getNextSibling();
			typedClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = typedClause_AST;
		_retTree = _t;
	}
	
	public final void query(AST _t) throws RecognitionException {
		
		AST query_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST query_AST = null;
		AST f_AST = null;
		AST f = null;
		AST s_AST = null;
		AST s = null;
		AST w_AST = null;
		AST w = null;
		AST g_AST = null;
		AST g = null;
		AST o_AST = null;
		AST o = null;
		AST l_AST = null;
		AST l = null;
		AST p_AST = null;
		AST p = null;
		
		try {      // for error handling
			AST __t121 = _t;
			AST tmp29_AST = null;
			AST tmp29_AST_in = null;
			tmp29_AST = astFactory.create((AST)_t);
			tmp29_AST_in = (AST)_t;
			ASTPair __currentAST121 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,QUERY);
			_t = _t.getFirstChild();
			beforeStatement( "select", SELECT );
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case NAMESPACE:
			{
				namespaceClause(_t);
				_t = _retTree;
				break;
			}
			case SELECT_FROM:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			AST __t123 = _t;
			AST tmp30_AST = null;
			AST tmp30_AST_in = null;
			tmp30_AST = astFactory.create((AST)_t);
			tmp30_AST_in = (AST)_t;
			ASTPair __currentAST123 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,SELECT_FROM);
			_t = _t.getFirstChild();
			f = _t==ASTNULL ? null : (AST)_t;
			fromClause(_t);
			_t = _retTree;
			f_AST = (AST)returnAST;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case SELECT:
			{
				s = _t==ASTNULL ? null : (AST)_t;
				selectClause(_t);
				_t = _retTree;
				s_AST = (AST)returnAST;
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
			currentAST = __currentAST123;
			_t = __t123;
			_t = _t.getNextSibling();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case WHERE:
			{
				w = _t==ASTNULL ? null : (AST)_t;
				whereClause(_t);
				_t = _retTree;
				w_AST = (AST)returnAST;
				break;
			}
			case 3:
			case GROUP:
			case ORDER:
			case PREFERRING:
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
				g = _t==ASTNULL ? null : (AST)_t;
				groupClause(_t);
				_t = _retTree;
				g_AST = (AST)returnAST;
				break;
			}
			case 3:
			case ORDER:
			case PREFERRING:
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
				o = _t==ASTNULL ? null : (AST)_t;
				orderClause(_t);
				_t = _retTree;
				o_AST = (AST)returnAST;
				break;
			}
			case 3:
			case PREFERRING:
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
				l = _t==ASTNULL ? null : (AST)_t;
				limitClause(_t);
				_t = _retTree;
				l_AST = (AST)returnAST;
				break;
			}
			case 3:
			case PREFERRING:
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
			case PREFERRING:
			{
				p = _t==ASTNULL ? null : (AST)_t;
				preferenceClause(_t);
				_t = _retTree;
				p_AST = (AST)returnAST;
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
			currentAST = __currentAST121;
			_t = __t121;
			_t = _t.getNextSibling();
			query_AST = (AST)currentAST.root;
			
					query_AST = (AST)astFactory.make( (new ASTArray(8)).add(astFactory.create(SELECT,"SELECT")).add(s_AST).add(f_AST).add(w_AST).add(g_AST).add(o_AST).add(l_AST).add(p_AST));
					beforeStatementCompletion( "select" );
					processQuery( s_AST, query_AST );
					afterStatementCompletion( "select" );
				
			currentAST.root = query_AST;
			currentAST.child = query_AST!=null &&query_AST.getFirstChild()!=null ?
				query_AST.getFirstChild() : query_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = query_AST;
		_retTree = _t;
	}
	
	public final void optionalHeadClause(AST _t) throws RecognitionException {
		
		AST optionalHeadClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST optionalHeadClause_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case UNDER:
			{
				AST __t39 = _t;
				AST tmp31_AST = null;
				AST tmp31_AST_in = null;
				tmp31_AST = astFactory.create((AST)_t);
				tmp31_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp31_AST);
				ASTPair __currentAST39 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,UNDER);
				_t = _t.getFirstChild();
				identifier(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST39;
				_t = __t39;
				_t = _t.getNextSibling();
				optionalHeadClause_AST = (AST)currentAST.root;
				break;
			}
			case CONTEXT:
			{
				AST __t40 = _t;
				AST tmp32_AST = null;
				AST tmp32_AST_in = null;
				tmp32_AST = astFactory.create((AST)_t);
				tmp32_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp32_AST);
				ASTPair __currentAST40 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,CONTEXT);
				_t = _t.getFirstChild();
				identifier(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST40;
				_t = __t40;
				_t = _t.getNextSibling();
				optionalHeadClause_AST = (AST)currentAST.root;
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
		returnAST = optionalHeadClause_AST;
		_retTree = _t;
	}
	
	public final void assignmentAttributeList(AST _t) throws RecognitionException {
		
		AST assignmentAttributeList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignmentAttributeList_AST = null;
		
		try {      // for error handling
			{
			int _cnt52=0;
			_loop52:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==EQ)) {
					assignmentAttribute(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt52>=1 ) { break _loop52; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt52++;
			} while (true);
			}
			assignmentAttributeList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = assignmentAttributeList_AST;
		_retTree = _t;
	}
	
	public final void propertyDef(AST _t) throws RecognitionException {
		
		AST propertyDef_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST propertyDef_AST = null;
		
		try {      // for error handling
			AST __t48 = _t;
			AST tmp33_AST = null;
			AST tmp33_AST_in = null;
			tmp33_AST = astFactory.create((AST)_t);
			tmp33_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp33_AST);
			ASTPair __currentAST48 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,PROPERTY_DEF);
			_t = _t.getFirstChild();
			identifier(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			datatype(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case DESCRIPTOR:
			{
				descriptorClause(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
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
			currentAST = __currentAST48;
			_t = __t48;
			_t = _t.getNextSibling();
			propertyDef_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = propertyDef_AST;
		_retTree = _t;
	}
	
	public final void assignmentAttribute(AST _t) throws RecognitionException {
		
		AST assignmentAttribute_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignmentAttribute_AST = null;
		
		try {      // for error handling
			AST __t54 = _t;
			AST tmp34_AST = null;
			AST tmp34_AST_in = null;
			tmp34_AST = astFactory.create((AST)_t);
			tmp34_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp34_AST);
			ASTPair __currentAST54 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,EQ);
			_t = _t.getFirstChild();
			attribute(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ARRAY:
			case CASE:
			case FALSE:
			case NULL:
			case TRUE:
			case TYPEOF:
			case AGGREGATE:
			case DOT:
			case LANGUE_OP:
			case INDEX_OP:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case IDENT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
			{
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case REF:
			case PREDEFINED_TYPE:
			case ARRAY_DEF:
			{
				datatype(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			currentAST = __currentAST54;
			_t = __t54;
			_t = _t.getNextSibling();
			assignmentAttribute_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = assignmentAttribute_AST;
		_retTree = _t;
	}
	
	public final void attribute(AST _t) throws RecognitionException {
		
		AST attribute_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST attribute_AST = null;
		AST att_AST = null;
		AST att = null;
		AST lg_code_AST = null;
		AST lg_code = null;
		AST i_AST = null;
		AST i = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LANGUE_OP:
			{
				AST __t57 = _t;
				AST tmp35_AST = null;
				AST tmp35_AST_in = null;
				tmp35_AST = astFactory.create((AST)_t);
				tmp35_AST_in = (AST)_t;
				ASTPair __currentAST57 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,LANGUE_OP);
				_t = _t.getFirstChild();
				att = _t==ASTNULL ? null : (AST)_t;
				identifier(_t);
				_t = _retTree;
				att_AST = (AST)returnAST;
				lg_code = _t==ASTNULL ? null : (AST)_t;
				lgCode(_t);
				_t = _retTree;
				lg_code_AST = (AST)returnAST;
				currentAST = __currentAST57;
				_t = __t57;
				_t = _t.getNextSibling();
				attribute_AST = (AST)currentAST.root;
				
						((IdentNode)att_AST).setLgCode(lg_code_AST.getText());
						attribute_AST = att_AST;
					
				currentAST.root = attribute_AST;
				currentAST.child = attribute_AST!=null &&attribute_AST.getFirstChild()!=null ?
					attribute_AST.getFirstChild() : attribute_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case IDENT:
			{
				i = _t==ASTNULL ? null : (AST)_t;
				identifier(_t);
				_t = _retTree;
				i_AST = (AST)returnAST;
				attribute_AST = (AST)currentAST.root;
				
						attribute_AST = i_AST;
					
				currentAST.root = attribute_AST;
				currentAST.child = attribute_AST!=null &&attribute_AST.getFirstChild()!=null ?
					attribute_AST.getFirstChild() : attribute_AST;
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
		returnAST = attribute_AST;
		_retTree = _t;
	}
	
	public final void expr(AST _t) throws RecognitionException {
		
		AST expr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expr_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case TYPEOF:
			case DOT:
			case LANGUE_OP:
			case INDEX_OP:
			case IDENT:
			{
				propertyRef(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr_AST = (AST)currentAST.root;
				break;
			}
			case FALSE:
			case NULL:
			case TRUE:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case NUM_INT:
			case QUOTED_STRING:
			{
				constant(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr_AST = (AST)currentAST.root;
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
				astFactory.addASTChild(currentAST, returnAST);
				expr_AST = (AST)currentAST.root;
				break;
			}
			case ARRAY:
			{
				arrayExpr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr_AST = (AST)currentAST.root;
				break;
			}
			case AGGREGATE:
			case METHOD_CALL:
			{
				functionCall(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr_AST = (AST)currentAST.root;
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
	
	public final void lgCode(AST _t) throws RecognitionException {
		
		AST lgCode_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST lgCode_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case FR:
			{
				AST tmp36_AST = null;
				AST tmp36_AST_in = null;
				tmp36_AST = astFactory.create((AST)_t);
				tmp36_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp36_AST);
				match(_t,FR);
				_t = _t.getNextSibling();
				lgCode_AST = (AST)currentAST.root;
				break;
			}
			case EN:
			{
				AST tmp37_AST = null;
				AST tmp37_AST_in = null;
				tmp37_AST = astFactory.create((AST)_t);
				tmp37_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp37_AST);
				match(_t,EN);
				_t = _t.getNextSibling();
				lgCode_AST = (AST)currentAST.root;
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
		returnAST = lgCode_AST;
		_retTree = _t;
	}
	
	public final void alterEntity(AST _t) throws RecognitionException {
		
		AST alterEntity_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST alterEntity_AST = null;
		
		try {      // for error handling
			AST __t62 = _t;
			AST tmp38_AST = null;
			AST tmp38_AST_in = null;
			tmp38_AST = astFactory.create((AST)_t);
			tmp38_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp38_AST);
			ASTPair __currentAST62 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ENTITY);
			_t = _t.getFirstChild();
			AST tmp39_AST = null;
			AST tmp39_AST_in = null;
			tmp39_AST = astFactory.create((AST)_t);
			tmp39_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp39_AST);
			match(_t,ONTOLOGY_MODEL_ID);
			_t = _t.getNextSibling();
			alterEntityAction(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST62;
			_t = __t62;
			_t = _t.getNextSibling();
			alterEntity_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = alterEntity_AST;
		_retTree = _t;
	}
	
	public final void alterClass(AST _t) throws RecognitionException {
		
		AST alterClass_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST alterClass_AST = null;
		
		try {      // for error handling
			AST __t67 = _t;
			AST tmp40_AST = null;
			AST tmp40_AST_in = null;
			tmp40_AST = astFactory.create((AST)_t);
			tmp40_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp40_AST);
			ASTPair __currentAST67 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ONTOLOGY_MODEL_ID);
			_t = _t.getFirstChild();
			identifier(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			alterClassAction(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST67;
			_t = __t67;
			_t = _t.getNextSibling();
			alterClass_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = alterClass_AST;
		_retTree = _t;
	}
	
	public final void alterExtent(AST _t) throws RecognitionException {
		
		AST alterExtent_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST alterExtent_AST = null;
		
		try {      // for error handling
			AST __t65 = _t;
			AST tmp41_AST = null;
			AST tmp41_AST_in = null;
			tmp41_AST = astFactory.create((AST)_t);
			tmp41_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp41_AST);
			ASTPair __currentAST65 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,EXTENT);
			_t = _t.getFirstChild();
			identifier(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp42_AST = null;
			AST tmp42_AST_in = null;
			tmp42_AST = astFactory.create((AST)_t);
			tmp42_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp42_AST);
			match(_t,ADD);
			_t = _t.getNextSibling();
			identifier(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST65;
			_t = __t65;
			_t = _t.getNextSibling();
			alterExtent_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = alterExtent_AST;
		_retTree = _t;
	}
	
	public final void alterEntityAction(AST _t) throws RecognitionException {
		
		AST alterEntityAction_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST alterEntityAction_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ADD:
			{
				AST tmp43_AST = null;
				AST tmp43_AST_in = null;
				tmp43_AST = astFactory.create((AST)_t);
				tmp43_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp43_AST);
				match(_t,ADD);
				_t = _t.getNextSibling();
				attributeDefinition(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				alterEntityAction_AST = (AST)currentAST.root;
				break;
			}
			case DROP:
			{
				AST tmp44_AST = null;
				AST tmp44_AST_in = null;
				tmp44_AST = astFactory.create((AST)_t);
				tmp44_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp44_AST);
				match(_t,DROP);
				_t = _t.getNextSibling();
				AST tmp45_AST = null;
				AST tmp45_AST_in = null;
				tmp45_AST = astFactory.create((AST)_t);
				tmp45_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp45_AST);
				match(_t,ONTOLOGY_MODEL_ID);
				_t = _t.getNextSibling();
				alterEntityAction_AST = (AST)currentAST.root;
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
		returnAST = alterEntityAction_AST;
		_retTree = _t;
	}
	
	public final void alterClassAction(AST _t) throws RecognitionException {
		
		AST alterClassAction_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST alterClassAction_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ADD:
			{
				AST tmp46_AST = null;
				AST tmp46_AST_in = null;
				tmp46_AST = astFactory.create((AST)_t);
				tmp46_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp46_AST);
				match(_t,ADD);
				_t = _t.getNextSibling();
				propertyDef(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				alterClassAction_AST = (AST)currentAST.root;
				break;
			}
			case DROP:
			{
				AST tmp47_AST = null;
				AST tmp47_AST_in = null;
				tmp47_AST = astFactory.create((AST)_t);
				tmp47_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp47_AST);
				match(_t,DROP);
				_t = _t.getNextSibling();
				identifier(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				alterClassAction_AST = (AST)currentAST.root;
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
		returnAST = alterClassAction_AST;
		_retTree = _t;
	}
	
	public final void dropEntity(AST _t) throws RecognitionException {
		
		AST dropEntity_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dropEntity_AST = null;
		
		try {      // for error handling
			AST __t72 = _t;
			AST tmp48_AST = null;
			AST tmp48_AST_in = null;
			tmp48_AST = astFactory.create((AST)_t);
			tmp48_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp48_AST);
			ASTPair __currentAST72 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ENTITY);
			_t = _t.getFirstChild();
			AST tmp49_AST = null;
			AST tmp49_AST_in = null;
			tmp49_AST = astFactory.create((AST)_t);
			tmp49_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp49_AST);
			match(_t,ONTOLOGY_MODEL_ID);
			_t = _t.getNextSibling();
			currentAST = __currentAST72;
			_t = __t72;
			_t = _t.getNextSibling();
			dropEntity_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = dropEntity_AST;
		_retTree = _t;
	}
	
	public final void namespaceSpecification(AST _t) throws RecognitionException {
		
		AST namespaceSpecification_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST namespaceSpecification_AST = null;
		AST c = null;
		AST c_AST = null;
		
		try {      // for error handling
			AST tmp50_AST = null;
			AST tmp50_AST_in = null;
			tmp50_AST = astFactory.create((AST)_t);
			tmp50_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp50_AST);
			match(_t,NAMESPACE);
			_t = _t.getNextSibling();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case QUOTED_STRING:
			{
				c = (AST)_t;
				AST c_AST_in = null;
				c_AST = astFactory.create(c);
				astFactory.addASTChild(currentAST, c_AST);
				match(_t,QUOTED_STRING);
				_t = _t.getNextSibling();
				setGlobalNamespace(c_AST);
				break;
			}
			case NONE:
			{
				AST tmp51_AST = null;
				AST tmp51_AST_in = null;
				tmp51_AST = astFactory.create((AST)_t);
				tmp51_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp51_AST);
				match(_t,NONE);
				_t = _t.getNextSibling();
				setGlobalNamespace(null);
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			namespaceSpecification_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = namespaceSpecification_AST;
		_retTree = _t;
	}
	
	public final void languageSpecification(AST _t) throws RecognitionException {
		
		AST languageSpecification_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST languageSpecification_AST = null;
		AST l_AST = null;
		AST l = null;
		
		try {      // for error handling
			AST tmp52_AST = null;
			AST tmp52_AST_in = null;
			tmp52_AST = astFactory.create((AST)_t);
			tmp52_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp52_AST);
			match(_t,LANGUAGE);
			_t = _t.getNextSibling();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case EN:
			case FR:
			{
				l = _t==ASTNULL ? null : (AST)_t;
				lgCode(_t);
				_t = _retTree;
				l_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				setGlobalLanguage(l_AST);
				break;
			}
			case NONE:
			{
				AST tmp53_AST = null;
				AST tmp53_AST_in = null;
				tmp53_AST = astFactory.create((AST)_t);
				tmp53_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp53_AST);
				match(_t,NONE);
				_t = _t.getNextSibling();
				setGlobalLanguage(null);
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			languageSpecification_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = languageSpecification_AST;
		_retTree = _t;
	}
	
	public final void intoClause(AST _t) throws RecognitionException {
		
		AST intoClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST intoClause_AST = null;
		AST i = null;
		AST i_AST = null;
		AST p_AST = null;
		AST p = null;
		AST ps_AST = null;
		AST ps = null;
		
		try {      // for error handling
			AST __t84 = _t;
			i = _t==ASTNULL ? null :(AST)_t;
			AST i_AST_in = null;
			i_AST = astFactory.create(i);
			astFactory.addASTChild(currentAST, i_AST);
			ASTPair __currentAST84 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,INTO);
			_t = _t.getFirstChild();
			handleClauseStart( INTO );setIntoClause(i_AST);
			p = _t==ASTNULL ? null : (AST)_t;
			identifier(_t);
			_t = _retTree;
			p_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			processInsertTarget(i_AST, p_AST);
			ps = _t==ASTNULL ? null : (AST)_t;
			insertablePropertySpec(_t);
			_t = _retTree;
			ps_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST84;
			_t = __t84;
			_t = _t.getNextSibling();
			intoClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = intoClause_AST;
		_retTree = _t;
	}
	
	public final void valueClause(AST _t) throws RecognitionException {
		
		AST valueClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST valueClause_AST = null;
		AST v = null;
		AST v_AST = null;
		
		try {      // for error handling
			AST __t111 = _t;
			v = _t==ASTNULL ? null :(AST)_t;
			AST v_AST_in = null;
			v_AST = astFactory.create(v);
			astFactory.addASTChild(currentAST, v_AST);
			ASTPair __currentAST111 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,VALUES);
			_t = _t.getFirstChild();
			setValuesClause(v_AST);
			exprOrSubqueryList(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST111;
			_t = __t111;
			_t = _t.getNextSibling();
			valueClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = valueClause_AST;
		_retTree = _t;
	}
	
	public final void insertablePropertySpec(AST _t) throws RecognitionException {
		
		AST insertablePropertySpec_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST insertablePropertySpec_AST = null;
		AST i_AST = null;
		AST i = null;
		
		try {      // for error handling
			AST __t86 = _t;
			AST tmp54_AST = null;
			AST tmp54_AST_in = null;
			tmp54_AST = astFactory.create((AST)_t);
			tmp54_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp54_AST);
			ASTPair __currentAST86 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,RANGE);
			_t = _t.getFirstChild();
			{
			int _cnt88=0;
			_loop88:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_0.member(_t.getType()))) {
					i = _t==ASTNULL ? null : (AST)_t;
					pathProperty(_t);
					_t = _retTree;
					i_AST = (AST)returnAST;
					astFactory.addASTChild(currentAST, returnAST);
					processInsertColumnElement(i_AST);
				}
				else {
					if ( _cnt88>=1 ) { break _loop88; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt88++;
			} while (true);
			}
			currentAST = __currentAST86;
			_t = __t86;
			_t = _t.getNextSibling();
			insertablePropertySpec_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = insertablePropertySpec_AST;
		_retTree = _t;
	}
	
	public final void pathProperty(AST _t) throws RecognitionException {
		
		AST pathProperty_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST pathProperty_AST = null;
		AST i_AST = null;
		AST i = null;
		AST att_AST = null;
		AST att = null;
		AST lg_code_AST = null;
		AST lg_code = null;
		AST descri_AST = null;
		AST descri = null;
		AST index = null;
		AST index_AST = null;
		AST instanceAlias_AST = null;
		AST instanceAlias = null;
		AST d = null;
		AST d_AST = null;
		AST lhs_AST = null;
		AST lhs = null;
		AST rhs_AST = null;
		AST rhs = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case IDENT:
			{
				i = _t==ASTNULL ? null : (AST)_t;
				identifier(_t);
				_t = _retTree;
				i_AST = (AST)returnAST;
				pathProperty_AST = (AST)currentAST.root;
				pathProperty_AST = i_AST;
				currentAST.root = pathProperty_AST;
				currentAST.child = pathProperty_AST!=null &&pathProperty_AST.getFirstChild()!=null ?
					pathProperty_AST.getFirstChild() : pathProperty_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case LANGUE_OP:
			{
				AST __t175 = _t;
				AST tmp55_AST = null;
				AST tmp55_AST_in = null;
				tmp55_AST = astFactory.create((AST)_t);
				tmp55_AST_in = (AST)_t;
				ASTPair __currentAST175 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,LANGUE_OP);
				_t = _t.getFirstChild();
				att = _t==ASTNULL ? null : (AST)_t;
				identifier(_t);
				_t = _retTree;
				att_AST = (AST)returnAST;
				lg_code = _t==ASTNULL ? null : (AST)_t;
				lgCode(_t);
				_t = _retTree;
				lg_code_AST = (AST)returnAST;
				currentAST = __currentAST175;
				_t = __t175;
				_t = _t.getNextSibling();
				pathProperty_AST = (AST)currentAST.root;
				
						((IdentNode)att_AST).setLgCode(lg_code_AST.getText());
						pathProperty_AST = att_AST;
					
				currentAST.root = pathProperty_AST;
				currentAST.child = pathProperty_AST!=null &&pathProperty_AST.getFirstChild()!=null ?
					pathProperty_AST.getFirstChild() : pathProperty_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case INDEX_OP:
			{
				AST __t176 = _t;
				AST tmp56_AST = null;
				AST tmp56_AST_in = null;
				tmp56_AST = astFactory.create((AST)_t);
				tmp56_AST_in = (AST)_t;
				ASTPair __currentAST176 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,INDEX_OP);
				_t = _t.getFirstChild();
				descri = _t==ASTNULL ? null : (AST)_t;
				identifier(_t);
				_t = _retTree;
				descri_AST = (AST)returnAST;
				index = (AST)_t;
				AST index_AST_in = null;
				index_AST = astFactory.create(index);
				match(_t,NUM_INT);
				_t = _t.getNextSibling();
				currentAST = __currentAST176;
				_t = __t176;
				_t = _t.getNextSibling();
				pathProperty_AST = (AST)currentAST.root;
				
						((IdentNode)descri_AST).setIndex(index.getText());
						pathProperty_AST = descri_AST;
					
				currentAST.root = pathProperty_AST;
				currentAST.child = pathProperty_AST!=null &&pathProperty_AST.getFirstChild()!=null ?
					pathProperty_AST.getFirstChild() : pathProperty_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case TYPEOF:
			{
				AST __t177 = _t;
				AST tmp57_AST = null;
				AST tmp57_AST_in = null;
				tmp57_AST = astFactory.create((AST)_t);
				tmp57_AST_in = (AST)_t;
				ASTPair __currentAST177 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,TYPEOF);
				_t = _t.getFirstChild();
				instanceAlias = _t==ASTNULL ? null : (AST)_t;
				identifier(_t);
				_t = _retTree;
				instanceAlias_AST = (AST)returnAST;
				currentAST = __currentAST177;
				_t = __t177;
				_t = _t.getNextSibling();
				pathProperty_AST = (AST)currentAST.root;
				
						pathProperty_AST=resolveTypeOf(instanceAlias_AST);
					
				currentAST.root = pathProperty_AST;
				currentAST.child = pathProperty_AST!=null &&pathProperty_AST.getFirstChild()!=null ?
					pathProperty_AST.getFirstChild() : pathProperty_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case DOT:
			{
				AST __t178 = _t;
				d = _t==ASTNULL ? null :(AST)_t;
				AST d_AST_in = null;
				d_AST = astFactory.create(d);
				ASTPair __currentAST178 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,DOT);
				_t = _t.getFirstChild();
				lhs = _t==ASTNULL ? null : (AST)_t;
				propertyRefLhs(_t);
				_t = _retTree;
				lhs_AST = (AST)returnAST;
				rhs = _t==ASTNULL ? null : (AST)_t;
				pathProperty(_t);
				_t = _retTree;
				rhs_AST = (AST)returnAST;
				currentAST = __currentAST178;
				_t = __t178;
				_t = _t.getNextSibling();
				pathProperty_AST = (AST)currentAST.root;
				pathProperty_AST = (AST)astFactory.make( (new ASTArray(3)).add(d_AST).add(lhs_AST).add(rhs_AST));
				currentAST.root = pathProperty_AST;
				currentAST.child = pathProperty_AST!=null &&pathProperty_AST.getFirstChild()!=null ?
					pathProperty_AST.getFirstChild() : pathProperty_AST;
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
		returnAST = pathProperty_AST;
		_retTree = _t;
	}
	
	public final void dmlFromClause(AST _t) throws RecognitionException {
		
		AST dmlFromClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dmlFromClause_AST = null;
		AST f = null;
		AST f_AST = null;
		
		try {      // for error handling
			AST __t93 = _t;
			f = _t==ASTNULL ? null :(AST)_t;
			AST f_AST_in = null;
			f_AST = astFactory.create(f);
			astFactory.addASTChild(currentAST, f_AST);
			ASTPair __currentAST93 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,FROM);
			_t = _t.getFirstChild();
			dmlFromClause_AST = (AST)currentAST.root;
			pushFromClause(dmlFromClause_AST,f);
			dmlFromElement(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST93;
			_t = __t93;
			_t = _t.getNextSibling();
			dmlFromClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = dmlFromClause_AST;
		_retTree = _t;
	}
	
	public final void setClause(AST _t) throws RecognitionException {
		
		AST setClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST setClause_AST = null;
		
		try {      // for error handling
			AST __t99 = _t;
			AST tmp58_AST = null;
			AST tmp58_AST_in = null;
			tmp58_AST = astFactory.create((AST)_t);
			tmp58_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp58_AST);
			ASTPair __currentAST99 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,SET);
			_t = _t.getFirstChild();
			assignmentList(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST99;
			_t = __t99;
			_t = _t.getNextSibling();
			setClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = setClause_AST;
		_retTree = _t;
	}
	
	public final void whereClause(AST _t) throws RecognitionException {
		
		AST whereClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST whereClause_AST = null;
		AST w = null;
		AST w_AST = null;
		AST b_AST = null;
		AST b = null;
		
		try {      // for error handling
			AST __t210 = _t;
			w = _t==ASTNULL ? null :(AST)_t;
			AST w_AST_in = null;
			w_AST = astFactory.create(w);
			ASTPair __currentAST210 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,WHERE);
			_t = _t.getFirstChild();
			handleClauseStart( WHERE );
			b = _t==ASTNULL ? null : (AST)_t;
			logicalExpr(_t);
			_t = _retTree;
			b_AST = (AST)returnAST;
			currentAST = __currentAST210;
			_t = __t210;
			_t = _t.getNextSibling();
			whereClause_AST = (AST)currentAST.root;
			
					// Use the *output* AST for the boolean expression!
					whereClause_AST = addWhereClause(w_AST , b_AST);
				
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
	
	public final void dmlFromElement(AST _t) throws RecognitionException {
		
		AST dmlFromElement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dmlFromElement_AST = null;
		AST p_AST = null;
		AST p = null;
		AST o = null;
		AST o_AST = null;
		
		try {      // for error handling
			AST __t95 = _t;
			AST tmp59_AST = null;
			AST tmp59_AST_in = null;
			tmp59_AST = astFactory.create((AST)_t);
			tmp59_AST_in = (AST)_t;
			ASTPair __currentAST95 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,RANGE);
			_t = _t.getFirstChild();
			{
			p = _t==ASTNULL ? null : (AST)_t;
			identifier(_t);
			_t = _retTree;
			p_AST = (AST)returnAST;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ONLY:
			{
				o = (AST)_t;
				AST o_AST_in = null;
				o_AST = astFactory.create(o);
				match(_t,ONLY);
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
			dmlFromElement_AST = (AST)currentAST.root;
			
					   dmlFromElement_AST = createFromElement(p,o,null,false);
				
			currentAST.root = dmlFromElement_AST;
			currentAST.child = dmlFromElement_AST!=null &&dmlFromElement_AST.getFirstChild()!=null ?
				dmlFromElement_AST.getFirstChild() : dmlFromElement_AST;
			currentAST.advanceChildToEnd();
			}
			currentAST = __currentAST95;
			_t = __t95;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = dmlFromElement_AST;
		_retTree = _t;
	}
	
	public final void assignmentList(AST _t) throws RecognitionException {
		
		AST assignmentList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignmentList_AST = null;
		
		try {      // for error handling
			{
			int _cnt102=0;
			_loop102:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==EQ)) {
					assignment(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt102>=1 ) { break _loop102; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt102++;
			} while (true);
			}
			assignmentList_AST = (AST)currentAST.root;
			
					assignmentList_AST = addTypeOfUpdate(assignmentList_AST);
				
			currentAST.root = assignmentList_AST;
			currentAST.child = assignmentList_AST!=null &&assignmentList_AST.getFirstChild()!=null ?
				assignmentList_AST.getFirstChild() : assignmentList_AST;
			currentAST.advanceChildToEnd();
			assignmentList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = assignmentList_AST;
		_retTree = _t;
	}
	
	public final void assignment(AST _t) throws RecognitionException {
		
		AST assignment_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignment_AST = null;
		
		try {      // for error handling
			AST __t104 = _t;
			AST tmp60_AST = null;
			AST tmp60_AST_in = null;
			tmp60_AST = astFactory.create((AST)_t);
			tmp60_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp60_AST);
			ASTPair __currentAST104 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,EQ);
			_t = _t.getFirstChild();
			{
			propertyRef(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			}
			{
			exprOrSubquery(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			}
			currentAST = __currentAST104;
			_t = __t104;
			_t = _t.getNextSibling();
			assignment_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = assignment_AST;
		_retTree = _t;
	}
	
	public final void propertyRef(AST _t) throws RecognitionException {
		
		AST propertyRef_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST propertyRef_AST = null;
		AST path_AST = null;
		AST path = null;
		
		try {      // for error handling
			path = _t==ASTNULL ? null : (AST)_t;
			pathProperty(_t);
			_t = _retTree;
			path_AST = (AST)returnAST;
			propertyRef_AST = (AST)currentAST.root;
			propertyRef_AST=resolve(path_AST, null);
			currentAST.root = propertyRef_AST;
			currentAST.child = propertyRef_AST!=null &&propertyRef_AST.getFirstChild()!=null ?
				propertyRef_AST.getFirstChild() : propertyRef_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = propertyRef_AST;
		_retTree = _t;
	}
	
	public final void exprOrSubquery(AST _t) throws RecognitionException {
		
		AST exprOrSubquery_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST exprOrSubquery_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ARRAY:
			case CASE:
			case FALSE:
			case NULL:
			case TRUE:
			case TYPEOF:
			case AGGREGATE:
			case DOT:
			case LANGUE_OP:
			case INDEX_OP:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case IDENT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
			{
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				exprOrSubquery_AST = (AST)currentAST.root;
				break;
			}
			case QUERY:
			{
				query(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				exprOrSubquery_AST = (AST)currentAST.root;
				break;
			}
			case ANY:
			{
				AST __t248 = _t;
				AST tmp61_AST = null;
				AST tmp61_AST_in = null;
				tmp61_AST = astFactory.create((AST)_t);
				tmp61_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp61_AST);
				ASTPair __currentAST248 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,ANY);
				_t = _t.getFirstChild();
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case QUERY:
				{
					query(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case TYPEOF:
				case DOT:
				case LANGUE_OP:
				case INDEX_OP:
				case IDENT:
				{
					propertyRef(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				currentAST = __currentAST248;
				_t = __t248;
				_t = _t.getNextSibling();
				exprOrSubquery_AST = (AST)currentAST.root;
				break;
			}
			case ALL:
			{
				AST __t250 = _t;
				AST tmp62_AST = null;
				AST tmp62_AST_in = null;
				tmp62_AST = astFactory.create((AST)_t);
				tmp62_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp62_AST);
				ASTPair __currentAST250 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,ALL);
				_t = _t.getFirstChild();
				query(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST250;
				_t = __t250;
				_t = _t.getNextSibling();
				exprOrSubquery_AST = (AST)currentAST.root;
				break;
			}
			case SOME:
			{
				AST __t251 = _t;
				AST tmp63_AST = null;
				AST tmp63_AST_in = null;
				tmp63_AST = astFactory.create((AST)_t);
				tmp63_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp63_AST);
				ASTPair __currentAST251 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,SOME);
				_t = _t.getFirstChild();
				query(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST251;
				_t = __t251;
				_t = _t.getNextSibling();
				exprOrSubquery_AST = (AST)currentAST.root;
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
		returnAST = exprOrSubquery_AST;
		_retTree = _t;
	}
	
	public final void exprOrSubqueryList(AST _t) throws RecognitionException {
		
		AST exprOrSubqueryList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST exprOrSubqueryList_AST = null;
		AST e_AST = null;
		AST e = null;
		AST q_AST = null;
		AST q = null;
		
		try {      // for error handling
			{
			int _cnt114=0;
			_loop114:
			do {
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case ARRAY:
				case CASE:
				case FALSE:
				case NULL:
				case TRUE:
				case TYPEOF:
				case AGGREGATE:
				case DOT:
				case LANGUE_OP:
				case INDEX_OP:
				case METHOD_CALL:
				case CASE2:
				case UNARY_MINUS:
				case IDENT:
				case NUM_DOUBLE:
				case NUM_FLOAT:
				case NUM_LONG:
				case STAR:
				case NUM_INT:
				case QUOTED_STRING:
				case PLUS:
				case MINUS:
				case DIV:
				{
					e = _t==ASTNULL ? null : (AST)_t;
					expr(_t);
					_t = _retTree;
					e_AST = (AST)returnAST;
					astFactory.addASTChild(currentAST, returnAST);
					addValueInInsert(e_AST);
					break;
				}
				case QUERY:
				{
					q = _t==ASTNULL ? null : (AST)_t;
					query(_t);
					_t = _retTree;
					q_AST = (AST)returnAST;
					astFactory.addASTChild(currentAST, returnAST);
					addQueryInInsert(q_AST);
					break;
				}
				default:
				{
					if ( _cnt114>=1 ) { break _loop114; } else {throw new NoViableAltException(_t);}
				}
				}
				_cnt114++;
			} while (true);
			}
			exprOrSubqueryList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = exprOrSubqueryList_AST;
		_retTree = _t;
	}
	
	public final void selectStatement(AST _t) throws RecognitionException {
		
		AST selectStatement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectStatement_AST = null;
		
		try {      // for error handling
			query(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			selectStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = selectStatement_AST;
		_retTree = _t;
	}
	
	public final void namespaceClause(AST _t) throws RecognitionException {
		
		AST namespaceClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST namespaceClause_AST = null;
		
		try {      // for error handling
			AST __t138 = _t;
			AST tmp64_AST = null;
			AST tmp64_AST_in = null;
			tmp64_AST = astFactory.create((AST)_t);
			tmp64_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp64_AST);
			ASTPair __currentAST138 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,NAMESPACE);
			_t = _t.getFirstChild();
			{
			int _cnt140=0;
			_loop140:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==QUOTED_STRING)) {
					namespaceAlias(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt140>=1 ) { break _loop140; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt140++;
			} while (true);
			}
			currentAST = __currentAST138;
			_t = __t138;
			_t = _t.getNextSibling();
			namespaceClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = namespaceClause_AST;
		_retTree = _t;
	}
	
	public final void fromClause(AST _t) throws RecognitionException {
		
		AST fromClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fromClause_AST = null;
		AST f = null;
		AST f_AST = null;
		
		try {      // for error handling
			AST __t185 = _t;
			f = _t==ASTNULL ? null :(AST)_t;
			AST f_AST_in = null;
			f_AST = astFactory.create(f);
			astFactory.addASTChild(currentAST, f_AST);
			ASTPair __currentAST185 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,FROM);
			_t = _t.getFirstChild();
			fromClause_AST = (AST)currentAST.root;
			pushFromClause(fromClause_AST,f); handleClauseStart( FROM );
			fromElementList(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST185;
			_t = __t185;
			_t = _t.getNextSibling();
			fromClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = fromClause_AST;
		_retTree = _t;
	}
	
	public final void selectClause(AST _t) throws RecognitionException {
		
		AST selectClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectClause_AST = null;
		AST d = null;
		AST d_AST = null;
		AST x_AST = null;
		AST x = null;
		
		try {      // for error handling
			AST __t154 = _t;
			AST tmp65_AST = null;
			AST tmp65_AST_in = null;
			tmp65_AST = astFactory.create((AST)_t);
			tmp65_AST_in = (AST)_t;
			ASTPair __currentAST154 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,SELECT);
			_t = _t.getFirstChild();
			handleClauseStart( SELECT );
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case DISTINCT:
			{
				d = (AST)_t;
				AST d_AST_in = null;
				d_AST = astFactory.create(d);
				match(_t,DISTINCT);
				_t = _t.getNextSibling();
				break;
			}
			case ARRAY:
			case AS:
			case CASE:
			case COUNT:
			case FALSE:
			case NULL:
			case TRUE:
			case TYPEOF:
			case AGGREGATE:
			case DOT:
			case ROW_STAR:
			case LANGUE_OP:
			case INDEX_OP:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case IDENT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			x = _t==ASTNULL ? null : (AST)_t;
			selectExprList(_t);
			_t = _retTree;
			x_AST = (AST)returnAST;
			currentAST = __currentAST154;
			_t = __t154;
			_t = _t.getNextSibling();
			selectClause_AST = (AST)currentAST.root;
			
					selectClause_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(SELECT_CLAUSE,"{select clause}")).add(d_AST).add(x_AST));
				
			currentAST.root = selectClause_AST;
			currentAST.child = selectClause_AST!=null &&selectClause_AST.getFirstChild()!=null ?
				selectClause_AST.getFirstChild() : selectClause_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = selectClause_AST;
		_retTree = _t;
	}
	
	public final void groupClause(AST _t) throws RecognitionException {
		
		AST groupClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST groupClause_AST = null;
		
		try {      // for error handling
			AST __t148 = _t;
			AST tmp66_AST = null;
			AST tmp66_AST_in = null;
			tmp66_AST = astFactory.create((AST)_t);
			tmp66_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp66_AST);
			ASTPair __currentAST148 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,GROUP);
			_t = _t.getFirstChild();
			handleClauseStart( GROUP );
			{
			int _cnt150=0;
			_loop150:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_tokenSet_1.member(_t.getType()))) {
					expr(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt150>=1 ) { break _loop150; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt150++;
			} while (true);
			}
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case HAVING:
			{
				AST __t152 = _t;
				AST tmp67_AST = null;
				AST tmp67_AST_in = null;
				tmp67_AST = astFactory.create((AST)_t);
				tmp67_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp67_AST);
				ASTPair __currentAST152 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,HAVING);
				_t = _t.getFirstChild();
				logicalExpr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST152;
				_t = __t152;
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
			currentAST = __currentAST148;
			_t = __t148;
			_t = _t.getNextSibling();
			groupClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = groupClause_AST;
		_retTree = _t;
	}
	
	public final void orderClause(AST _t) throws RecognitionException {
		
		AST orderClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orderClause_AST = null;
		
		try {      // for error handling
			AST __t131 = _t;
			AST tmp68_AST = null;
			AST tmp68_AST_in = null;
			tmp68_AST = astFactory.create((AST)_t);
			tmp68_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp68_AST);
			ASTPair __currentAST131 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ORDER);
			_t = _t.getFirstChild();
			handleClauseStart( ORDER );
			orderExprs(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST131;
			_t = __t131;
			_t = _t.getNextSibling();
			orderClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = orderClause_AST;
		_retTree = _t;
	}
	
	public final void limitClause(AST _t) throws RecognitionException {
		
		AST limitClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST limitClause_AST = null;
		
		try {      // for error handling
			AST __t133 = _t;
			AST tmp69_AST = null;
			AST tmp69_AST_in = null;
			tmp69_AST = astFactory.create((AST)_t);
			tmp69_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp69_AST);
			ASTPair __currentAST133 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,LIMIT);
			_t = _t.getFirstChild();
			AST tmp70_AST = null;
			AST tmp70_AST_in = null;
			tmp70_AST = astFactory.create((AST)_t);
			tmp70_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp70_AST);
			match(_t,NUM_INT);
			_t = _t.getNextSibling();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case OFFSET:
			{
				AST tmp71_AST = null;
				AST tmp71_AST_in = null;
				tmp71_AST = astFactory.create((AST)_t);
				tmp71_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp71_AST);
				match(_t,OFFSET);
				_t = _t.getNextSibling();
				AST tmp72_AST = null;
				AST tmp72_AST_in = null;
				tmp72_AST = astFactory.create((AST)_t);
				tmp72_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp72_AST);
				match(_t,NUM_INT);
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
			currentAST = __currentAST133;
			_t = __t133;
			_t = _t.getNextSibling();
			limitClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = limitClause_AST;
		_retTree = _t;
	}
	
	public final void preferenceClause(AST _t) throws RecognitionException {
		
		AST preferenceClause_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST preferenceClause_AST = null;
		AST i_AST = null;
		AST i = null;
		
		try {      // for error handling
			AST __t136 = _t;
			AST tmp73_AST = null;
			AST tmp73_AST_in = null;
			tmp73_AST = astFactory.create((AST)_t);
			tmp73_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp73_AST);
			ASTPair __currentAST136 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,PREFERRING);
			_t = _t.getFirstChild();
			handleClauseStart( PREFERRING );
			i = _t==ASTNULL ? null : (AST)_t;
			identifier(_t);
			_t = _retTree;
			i_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST136;
			_t = __t136;
			_t = _t.getNextSibling();
			preferenceClause_AST = (AST)currentAST.root;
			
					rewriteQueryWithPreference(i_AST);
					preferenceClause_AST = null;
				
			currentAST.root = preferenceClause_AST;
			currentAST.child = preferenceClause_AST!=null &&preferenceClause_AST.getFirstChild()!=null ?
				preferenceClause_AST.getFirstChild() : preferenceClause_AST;
			currentAST.advanceChildToEnd();
			preferenceClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = preferenceClause_AST;
		_retTree = _t;
	}
	
	public final void orderExprs(AST _t) throws RecognitionException {
		
		AST orderExprs_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orderExprs_AST = null;
		
		try {      // for error handling
			expr(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ASCENDING:
			{
				AST tmp74_AST = null;
				AST tmp74_AST_in = null;
				tmp74_AST = astFactory.create((AST)_t);
				tmp74_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp74_AST);
				match(_t,ASCENDING);
				_t = _t.getNextSibling();
				break;
			}
			case DESCENDING:
			{
				AST tmp75_AST = null;
				AST tmp75_AST_in = null;
				tmp75_AST = astFactory.create((AST)_t);
				tmp75_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp75_AST);
				match(_t,DESCENDING);
				_t = _t.getNextSibling();
				break;
			}
			case 3:
			case ARRAY:
			case CASE:
			case FALSE:
			case NULL:
			case TRUE:
			case TYPEOF:
			case AGGREGATE:
			case DOT:
			case LANGUE_OP:
			case INDEX_OP:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case IDENT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
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
			case ARRAY:
			case CASE:
			case FALSE:
			case NULL:
			case TRUE:
			case TYPEOF:
			case AGGREGATE:
			case DOT:
			case LANGUE_OP:
			case INDEX_OP:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case IDENT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
			{
				orderExprs(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
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
			orderExprs_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = orderExprs_AST;
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
			AST __t142 = _t;
			c = _t==ASTNULL ? null :(AST)_t;
			AST c_AST_in = null;
			c_AST = astFactory.create(c);
			astFactory.addASTChild(currentAST, c_AST);
			ASTPair __currentAST142 = currentAST.copy();
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
			currentAST = __currentAST142;
			_t = __t142;
			_t = _t.getNextSibling();
			
					setLocalNamespace(c_AST, a_AST);
				
			namespaceAlias_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = namespaceAlias_AST;
		_retTree = _t;
	}
	
	public final void logicalExpr(AST _t) throws RecognitionException {
		
		AST logicalExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalExpr_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case AND:
			{
				AST __t212 = _t;
				AST tmp76_AST = null;
				AST tmp76_AST_in = null;
				tmp76_AST = astFactory.create((AST)_t);
				tmp76_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp76_AST);
				ASTPair __currentAST212 = currentAST.copy();
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
				currentAST = __currentAST212;
				_t = __t212;
				_t = _t.getNextSibling();
				logicalExpr_AST = (AST)currentAST.root;
				break;
			}
			case OR:
			{
				AST __t213 = _t;
				AST tmp77_AST = null;
				AST tmp77_AST_in = null;
				tmp77_AST = astFactory.create((AST)_t);
				tmp77_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp77_AST);
				ASTPair __currentAST213 = currentAST.copy();
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
				currentAST = __currentAST213;
				_t = __t213;
				_t = _t.getNextSibling();
				logicalExpr_AST = (AST)currentAST.root;
				break;
			}
			case NOT:
			{
				AST __t214 = _t;
				AST tmp78_AST = null;
				AST tmp78_AST_in = null;
				tmp78_AST = astFactory.create((AST)_t);
				tmp78_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp78_AST);
				ASTPair __currentAST214 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,NOT);
				_t = _t.getFirstChild();
				logicalExpr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST214;
				_t = __t214;
				_t = _t.getNextSibling();
				logicalExpr_AST = (AST)currentAST.root;
				break;
			}
			case BETWEEN:
			case EXISTS:
			case IN:
			case LIKE:
			case IS_NOT_NULL:
			case IS_NULL:
			case IS_NOT_OF:
			case IS_OF:
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
				comparisonExpr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
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
	
	public final void selectExprList(AST _t) throws RecognitionException {
		
		AST selectExprList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectExprList_AST = null;
		
				boolean oldInSelect = inSelect;
				inSelect = true;
			
		
		try {      // for error handling
			{
			int _cnt158=0;
			_loop158:
			do {
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case ARRAY:
				case CASE:
				case COUNT:
				case FALSE:
				case NULL:
				case TRUE:
				case TYPEOF:
				case AGGREGATE:
				case DOT:
				case ROW_STAR:
				case LANGUE_OP:
				case INDEX_OP:
				case METHOD_CALL:
				case CASE2:
				case UNARY_MINUS:
				case IDENT:
				case NUM_DOUBLE:
				case NUM_FLOAT:
				case NUM_LONG:
				case STAR:
				case NUM_INT:
				case QUOTED_STRING:
				case PLUS:
				case MINUS:
				case DIV:
				{
					selectExpr(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case AS:
				{
					aliasedSelectExpr(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					if ( _cnt158>=1 ) { break _loop158; } else {throw new NoViableAltException(_t);}
				}
				}
				_cnt158++;
			} while (true);
			}
			
					inSelect = oldInSelect;
				
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
		AST r = null;
		AST r_AST = null;
		
		try {      // for error handling
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case TYPEOF:
			case DOT:
			case LANGUE_OP:
			case INDEX_OP:
			case IDENT:
			{
				propertyRef(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case COUNT:
			{
				count(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case AGGREGATE:
			case METHOD_CALL:
			{
				functionCall(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
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
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case ARRAY:
			{
				arrayExpr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case FALSE:
			case NULL:
			case TRUE:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case NUM_INT:
			case QUOTED_STRING:
			{
				constant(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case ROW_STAR:
			{
				r = (AST)_t;
				AST r_AST_in = null;
				r_AST = astFactory.create(r);
				astFactory.addASTChild(currentAST, r_AST);
				match(_t,ROW_STAR);
				_t = _t.getNextSibling();
				selectExpr_AST = (AST)currentAST.root;
				selectExpr_AST=resolve(r_AST, null);
				currentAST.root = selectExpr_AST;
				currentAST.child = selectExpr_AST!=null &&selectExpr_AST.getFirstChild()!=null ?
					selectExpr_AST.getFirstChild() : selectExpr_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			selectExpr_AST = (AST)currentAST.root;
			addSelectExpr(selectExpr_AST);
			selectExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = selectExpr_AST;
		_retTree = _t;
	}
	
	public final void aliasedSelectExpr(AST _t) throws RecognitionException {
		
		AST aliasedSelectExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST aliasedSelectExpr_AST = null;
		AST se_AST = null;
		AST se = null;
		AST i_AST = null;
		AST i = null;
		
		try {      // for error handling
			AST __t160 = _t;
			AST tmp79_AST = null;
			AST tmp79_AST_in = null;
			tmp79_AST = astFactory.create((AST)_t);
			tmp79_AST_in = (AST)_t;
			ASTPair __currentAST160 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,AS);
			_t = _t.getFirstChild();
			se = _t==ASTNULL ? null : (AST)_t;
			selectExpr(_t);
			_t = _retTree;
			se_AST = (AST)returnAST;
			i = _t==ASTNULL ? null : (AST)_t;
			identifier(_t);
			_t = _retTree;
			i_AST = (AST)returnAST;
			currentAST = __currentAST160;
			_t = __t160;
			_t = _t.getNextSibling();
			aliasedSelectExpr_AST = (AST)currentAST.root;
			
				    setAlias(se_AST,i_AST);
					aliasedSelectExpr_AST = se_AST;
				
			currentAST.root = aliasedSelectExpr_AST;
			currentAST.child = aliasedSelectExpr_AST!=null &&aliasedSelectExpr_AST.getFirstChild()!=null ?
				aliasedSelectExpr_AST.getFirstChild() : aliasedSelectExpr_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = aliasedSelectExpr_AST;
		_retTree = _t;
	}
	
	public final void count(AST _t) throws RecognitionException {
		
		AST count_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST count_AST = null;
		
		try {      // for error handling
			AST __t169 = _t;
			AST tmp80_AST = null;
			AST tmp80_AST_in = null;
			tmp80_AST = astFactory.create((AST)_t);
			tmp80_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp80_AST);
			ASTPair __currentAST169 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,COUNT);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case DISTINCT:
			{
				AST tmp81_AST = null;
				AST tmp81_AST_in = null;
				tmp81_AST = astFactory.create((AST)_t);
				tmp81_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp81_AST);
				match(_t,DISTINCT);
				_t = _t.getNextSibling();
				break;
			}
			case ARRAY:
			case CASE:
			case FALSE:
			case NULL:
			case TRUE:
			case TYPEOF:
			case AGGREGATE:
			case DOT:
			case ROW_STAR:
			case LANGUE_OP:
			case INDEX_OP:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case IDENT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
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
			case ARRAY:
			case CASE:
			case FALSE:
			case NULL:
			case TRUE:
			case TYPEOF:
			case AGGREGATE:
			case DOT:
			case LANGUE_OP:
			case INDEX_OP:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case IDENT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
			{
				aggregateExpr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case ROW_STAR:
			{
				AST tmp82_AST = null;
				AST tmp82_AST_in = null;
				tmp82_AST = astFactory.create((AST)_t);
				tmp82_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp82_AST);
				match(_t,ROW_STAR);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			currentAST = __currentAST169;
			_t = __t169;
			_t = _t.getNextSibling();
			count_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = count_AST;
		_retTree = _t;
	}
	
	public final void functionCall(AST _t) throws RecognitionException {
		
		AST functionCall_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST functionCall_AST = null;
		AST m = null;
		AST m_AST = null;
		AST e1_AST = null;
		AST e1 = null;
		AST e2_AST = null;
		AST e2 = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case METHOD_CALL:
			{
				AST __t273 = _t;
				m = _t==ASTNULL ? null :(AST)_t;
				AST m_AST_in = null;
				m_AST = astFactory.create(m);
				astFactory.addASTChild(currentAST, m_AST);
				ASTPair __currentAST273 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,METHOD_CALL);
				_t = _t.getFirstChild();
				inFunctionCall=true;
				identifier(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case EXPR_LIST:
				{
					AST __t275 = _t;
					AST tmp83_AST = null;
					AST tmp83_AST_in = null;
					tmp83_AST = astFactory.create((AST)_t);
					tmp83_AST_in = (AST)_t;
					astFactory.addASTChild(currentAST, tmp83_AST);
					ASTPair __currentAST275 = currentAST.copy();
					currentAST.root = currentAST.child;
					currentAST.child = null;
					match(_t,EXPR_LIST);
					_t = _t.getFirstChild();
					e1 = _t==ASTNULL ? null : (AST)_t;
					expr(_t);
					_t = _retTree;
					e1_AST = (AST)returnAST;
					astFactory.addASTChild(currentAST, returnAST);
					{
					_loop277:
					do {
						if (_t==null) _t=ASTNULL;
						switch ( _t.getType()) {
						case ARRAY:
						case CASE:
						case FALSE:
						case NULL:
						case TRUE:
						case TYPEOF:
						case AGGREGATE:
						case DOT:
						case LANGUE_OP:
						case INDEX_OP:
						case METHOD_CALL:
						case CASE2:
						case UNARY_MINUS:
						case IDENT:
						case NUM_DOUBLE:
						case NUM_FLOAT:
						case NUM_LONG:
						case STAR:
						case NUM_INT:
						case QUOTED_STRING:
						case PLUS:
						case MINUS:
						case DIV:
						{
							e2 = _t==ASTNULL ? null : (AST)_t;
							expr(_t);
							_t = _retTree;
							e2_AST = (AST)returnAST;
							astFactory.addASTChild(currentAST, returnAST);
							checkType(e1_AST,m_AST,e2_AST);
							break;
						}
						case REF:
						case PREDEFINED_TYPE:
						case ARRAY_DEF:
						{
							datatype(_t);
							_t = _retTree;
							astFactory.addASTChild(currentAST, returnAST);
							break;
						}
						default:
						{
							break _loop277;
						}
						}
					} while (true);
					}
					currentAST = __currentAST275;
					_t = __t275;
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
				currentAST = __currentAST273;
				_t = __t273;
				_t = _t.getNextSibling();
				functionCall_AST = (AST)currentAST.root;
				processFunction(functionCall_AST,inSelect);
				inFunctionCall=false;
				functionCall_AST = (AST)currentAST.root;
				break;
			}
			case AGGREGATE:
			{
				AST __t278 = _t;
				AST tmp84_AST = null;
				AST tmp84_AST_in = null;
				tmp84_AST = astFactory.create((AST)_t);
				tmp84_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp84_AST);
				ASTPair __currentAST278 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,AGGREGATE);
				_t = _t.getFirstChild();
				aggregateExpr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST278;
				_t = __t278;
				_t = _t.getNextSibling();
				functionCall_AST = (AST)currentAST.root;
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
		returnAST = functionCall_AST;
		_retTree = _t;
	}
	
	public final void arithmeticExpr(AST _t) throws RecognitionException {
		
		AST arithmeticExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arithmeticExpr_AST = null;
		AST oplus = null;
		AST oplus_AST = null;
		AST eplus1_AST = null;
		AST eplus1 = null;
		AST eplus2_AST = null;
		AST eplus2 = null;
		AST ominus = null;
		AST ominus_AST = null;
		AST eminus1_AST = null;
		AST eminus1 = null;
		AST eminus2_AST = null;
		AST eminus2 = null;
		AST odiv = null;
		AST odiv_AST = null;
		AST ediv1_AST = null;
		AST ediv1 = null;
		AST ediv2_AST = null;
		AST ediv2 = null;
		AST ostar = null;
		AST ostar_AST = null;
		AST estar1_AST = null;
		AST estar1 = null;
		AST estar2_AST = null;
		AST estar2 = null;
		AST ounaryminus = null;
		AST ounaryminus_AST = null;
		AST eunaryminus_AST = null;
		AST eunaryminus = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case PLUS:
			{
				AST __t254 = _t;
				oplus = _t==ASTNULL ? null :(AST)_t;
				AST oplus_AST_in = null;
				oplus_AST = astFactory.create(oplus);
				astFactory.addASTChild(currentAST, oplus_AST);
				ASTPair __currentAST254 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,PLUS);
				_t = _t.getFirstChild();
				eplus1 = _t==ASTNULL ? null : (AST)_t;
				expr(_t);
				_t = _retTree;
				eplus1_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				eplus2 = _t==ASTNULL ? null : (AST)_t;
				expr(_t);
				_t = _retTree;
				eplus2_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST254;
				_t = __t254;
				_t = _t.getNextSibling();
				checkType(eplus1_AST,oplus_AST,eplus2_AST);
				arithmeticExpr_AST = (AST)currentAST.root;
				break;
			}
			case MINUS:
			{
				AST __t255 = _t;
				ominus = _t==ASTNULL ? null :(AST)_t;
				AST ominus_AST_in = null;
				ominus_AST = astFactory.create(ominus);
				astFactory.addASTChild(currentAST, ominus_AST);
				ASTPair __currentAST255 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,MINUS);
				_t = _t.getFirstChild();
				eminus1 = _t==ASTNULL ? null : (AST)_t;
				expr(_t);
				_t = _retTree;
				eminus1_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				eminus2 = _t==ASTNULL ? null : (AST)_t;
				expr(_t);
				_t = _retTree;
				eminus2_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST255;
				_t = __t255;
				_t = _t.getNextSibling();
				checkType(eminus1_AST,ominus_AST,eminus2_AST);
				arithmeticExpr_AST = (AST)currentAST.root;
				break;
			}
			case DIV:
			{
				AST __t256 = _t;
				odiv = _t==ASTNULL ? null :(AST)_t;
				AST odiv_AST_in = null;
				odiv_AST = astFactory.create(odiv);
				astFactory.addASTChild(currentAST, odiv_AST);
				ASTPair __currentAST256 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,DIV);
				_t = _t.getFirstChild();
				ediv1 = _t==ASTNULL ? null : (AST)_t;
				expr(_t);
				_t = _retTree;
				ediv1_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				ediv2 = _t==ASTNULL ? null : (AST)_t;
				expr(_t);
				_t = _retTree;
				ediv2_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST256;
				_t = __t256;
				_t = _t.getNextSibling();
				checkType(ediv1_AST,odiv_AST,ediv2_AST);
				arithmeticExpr_AST = (AST)currentAST.root;
				break;
			}
			case STAR:
			{
				AST __t257 = _t;
				ostar = _t==ASTNULL ? null :(AST)_t;
				AST ostar_AST_in = null;
				ostar_AST = astFactory.create(ostar);
				astFactory.addASTChild(currentAST, ostar_AST);
				ASTPair __currentAST257 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,STAR);
				_t = _t.getFirstChild();
				estar1 = _t==ASTNULL ? null : (AST)_t;
				expr(_t);
				_t = _retTree;
				estar1_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				estar2 = _t==ASTNULL ? null : (AST)_t;
				expr(_t);
				_t = _retTree;
				estar2_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST257;
				_t = __t257;
				_t = _t.getNextSibling();
				checkType(estar1_AST,ostar_AST,estar2_AST);
				arithmeticExpr_AST = (AST)currentAST.root;
				break;
			}
			case UNARY_MINUS:
			{
				AST __t258 = _t;
				ounaryminus = _t==ASTNULL ? null :(AST)_t;
				AST ounaryminus_AST_in = null;
				ounaryminus_AST = astFactory.create(ounaryminus);
				astFactory.addASTChild(currentAST, ounaryminus_AST);
				ASTPair __currentAST258 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,UNARY_MINUS);
				_t = _t.getFirstChild();
				eunaryminus = _t==ASTNULL ? null : (AST)_t;
				expr(_t);
				_t = _retTree;
				eunaryminus_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST258;
				_t = __t258;
				_t = _t.getNextSibling();
				checkType(eunaryminus_AST,ounaryminus_AST,null);
				arithmeticExpr_AST = (AST)currentAST.root;
				break;
			}
			case CASE:
			case CASE2:
			{
				caseExpr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
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
	
	public final void arrayExpr(AST _t) throws RecognitionException {
		
		AST arrayExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayExpr_AST = null;
		
		try {      // for error handling
			AST __t164 = _t;
			AST tmp85_AST = null;
			AST tmp85_AST_in = null;
			tmp85_AST = astFactory.create((AST)_t);
			tmp85_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp85_AST);
			ASTPair __currentAST164 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ARRAY);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case ARRAY:
			case CASE:
			case FALSE:
			case NULL:
			case TRUE:
			case TYPEOF:
			case AGGREGATE:
			case DOT:
			case LANGUE_OP:
			case INDEX_OP:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case IDENT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
			{
				{
				int _cnt167=0;
				_loop167:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_tokenSet_1.member(_t.getType()))) {
						expr(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt167>=1 ) { break _loop167; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt167++;
				} while (true);
				}
				break;
			}
			case QUERY:
			{
				query(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			currentAST = __currentAST164;
			_t = __t164;
			_t = _t.getNextSibling();
			arrayExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = arrayExpr_AST;
		_retTree = _t;
	}
	
	public final void constant(AST _t) throws RecognitionException {
		
		AST constant_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constant_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case NUM_INT:
			case QUOTED_STRING:
			{
				literal(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				constant_AST = (AST)currentAST.root;
				break;
			}
			case NULL:
			{
				AST tmp86_AST = null;
				AST tmp86_AST_in = null;
				tmp86_AST = astFactory.create((AST)_t);
				tmp86_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp86_AST);
				match(_t,NULL);
				_t = _t.getNextSibling();
				constant_AST = (AST)currentAST.root;
				break;
			}
			case TRUE:
			{
				AST tmp87_AST = null;
				AST tmp87_AST_in = null;
				tmp87_AST = astFactory.create((AST)_t);
				tmp87_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp87_AST);
				match(_t,TRUE);
				_t = _t.getNextSibling();
				constant_AST = (AST)currentAST.root;
				break;
			}
			case FALSE:
			{
				AST tmp88_AST = null;
				AST tmp88_AST_in = null;
				tmp88_AST = astFactory.create((AST)_t);
				tmp88_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp88_AST);
				match(_t,FALSE);
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
	
	public final void aggregateExpr(AST _t) throws RecognitionException {
		
		AST aggregateExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST aggregateExpr_AST = null;
		
		try {      // for error handling
			expr(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			aggregateExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = aggregateExpr_AST;
		_retTree = _t;
	}
	
	public final void propertyRefLhs(AST _t) throws RecognitionException {
		
		AST propertyRefLhs_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST propertyRefLhs_AST = null;
		AST f_AST = null;
		AST f = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case TYPEOF:
			case DOT:
			case LANGUE_OP:
			case INDEX_OP:
			case IDENT:
			{
				pathProperty(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				propertyRefLhs_AST = (AST)currentAST.root;
				break;
			}
			case METHOD_CALL:
			{
				f = _t==ASTNULL ? null : (AST)_t;
				functionTreatCall(_t);
				_t = _retTree;
				f_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				propertyRefLhs_AST = (AST)currentAST.root;
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
		returnAST = propertyRefLhs_AST;
		_retTree = _t;
	}
	
	public final void functionTreatCall(AST _t) throws RecognitionException {
		
		AST functionTreatCall_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST functionTreatCall_AST = null;
		AST m = null;
		AST m_AST = null;
		
		try {      // for error handling
			AST __t182 = _t;
			m = _t==ASTNULL ? null :(AST)_t;
			AST m_AST_in = null;
			m_AST = astFactory.create(m);
			astFactory.addASTChild(currentAST, m_AST);
			ASTPair __currentAST182 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,METHOD_CALL);
			_t = _t.getFirstChild();
			identifier(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			AST __t183 = _t;
			AST tmp89_AST = null;
			AST tmp89_AST_in = null;
			tmp89_AST = astFactory.create((AST)_t);
			tmp89_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp89_AST);
			ASTPair __currentAST183 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,EXPR_LIST);
			_t = _t.getFirstChild();
			identifier(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			identifier(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			currentAST = __currentAST183;
			_t = __t183;
			_t = _t.getNextSibling();
			currentAST = __currentAST182;
			_t = __t182;
			_t = _t.getNextSibling();
			functionTreatCall_AST = (AST)currentAST.root;
			functionTreatCall_AST = resolveTreatFunction(m_AST);
			currentAST.root = functionTreatCall_AST;
			currentAST.child = functionTreatCall_AST!=null &&functionTreatCall_AST.getFirstChild()!=null ?
				functionTreatCall_AST.getFirstChild() : functionTreatCall_AST;
			currentAST.advanceChildToEnd();
			functionTreatCall_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = functionTreatCall_AST;
		_retTree = _t;
	}
	
	public final void fromElementList(AST _t) throws RecognitionException {
		
		AST fromElementList_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fromElementList_AST = null;
		
				boolean oldInFrom = inFrom;
				inFrom = true;
				
		
		try {      // for error handling
			{
			int _cnt188=0;
			_loop188:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==UNNEST||_t.getType()==RANGE)) {
					fromElement(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt188>=1 ) { break _loop188; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt188++;
			} while (true);
			}
			
					inFrom = oldInFrom;
					
			fromElementList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = fromElementList_AST;
		_retTree = _t;
	}
	
	public final void fromElement(AST _t) throws RecognitionException {
		
		AST fromElement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fromElement_AST = null;
		AST p_AST = null;
		AST p = null;
		AST s = null;
		AST s_AST = null;
		AST a = null;
		AST a_AST = null;
		AST j_AST = null;
		AST j = null;
		AST property_AST = null;
		AST property = null;
		AST alias = null;
		AST alias_AST = null;
		AST j2_AST = null;
		AST j2 = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case RANGE:
			{
				AST __t190 = _t;
				AST tmp90_AST = null;
				AST tmp90_AST_in = null;
				tmp90_AST = astFactory.create((AST)_t);
				tmp90_AST_in = (AST)_t;
				ASTPair __currentAST190 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,RANGE);
				_t = _t.getFirstChild();
				p = _t==ASTNULL ? null : (AST)_t;
				identifier(_t);
				_t = _retTree;
				p_AST = (AST)returnAST;
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case ONLY:
				{
					s = (AST)_t;
					AST s_AST_in = null;
					s_AST = astFactory.create(s);
					match(_t,ONLY);
					_t = _t.getNextSibling();
					break;
				}
				case 3:
				case ALIAS:
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
				case ALIAS:
				{
					a = (AST)_t;
					AST a_AST_in = null;
					a_AST = astFactory.create(a);
					match(_t,ALIAS);
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
				currentAST = __currentAST190;
				_t = __t190;
				_t = _t.getNextSibling();
				fromElement_AST = (AST)currentAST.root;
				
						fromElement_AST = createFromElement(p,s,a,true);
					
				currentAST.root = fromElement_AST;
				currentAST.child = fromElement_AST!=null &&fromElement_AST.getFirstChild()!=null ?
					fromElement_AST.getFirstChild() : fromElement_AST;
				currentAST.advanceChildToEnd();
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case JOIN:
				{
					j = _t==ASTNULL ? null : (AST)_t;
					joinElement(_t);
					_t = _retTree;
					j_AST = (AST)returnAST;
					break;
				}
				case 3:
				case ON:
				case UNNEST:
				case RANGE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				fromElement_AST = (AST)currentAST.root;
				if (fromElement_AST!= null)fromElement_AST.addChild(j_AST);
				break;
			}
			case UNNEST:
			{
				AST __t194 = _t;
				AST tmp91_AST = null;
				AST tmp91_AST_in = null;
				tmp91_AST = astFactory.create((AST)_t);
				tmp91_AST_in = (AST)_t;
				ASTPair __currentAST194 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,UNNEST);
				_t = _t.getFirstChild();
				property = _t==ASTNULL ? null : (AST)_t;
				propertyRef(_t);
				_t = _retTree;
				property_AST = (AST)returnAST;
				alias = (AST)_t;
				AST alias_AST_in = null;
				alias_AST = astFactory.create(alias);
				match(_t,ALIAS);
				_t = _t.getNextSibling();
				currentAST = __currentAST194;
				_t = __t194;
				_t = _t.getNextSibling();
				
						AST previousFromElement = unnest(property_AST,alias_AST);
					
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case JOIN:
				{
					j2 = _t==ASTNULL ? null : (AST)_t;
					joinElement(_t);
					_t = _retTree;
					j2_AST = (AST)returnAST;
					break;
				}
				case 3:
				case ON:
				case UNNEST:
				case RANGE:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				previousFromElement.addChild(j2_AST);
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
		returnAST = fromElement_AST;
		_retTree = _t;
	}
	
	public final void joinElement(AST _t) throws RecognitionException {
		
		AST joinElement_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST joinElement_AST = null;
		AST j_AST = null;
		AST j = null;
		AST f_AST = null;
		AST f = null;
		
		try {      // for error handling
			AST __t197 = _t;
			AST tmp92_AST = null;
			AST tmp92_AST_in = null;
			tmp92_AST = astFactory.create((AST)_t);
			tmp92_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp92_AST);
			ASTPair __currentAST197 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,JOIN);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LEFT:
			case RIGHT:
			{
				j = _t==ASTNULL ? null : (AST)_t;
				joinType(_t);
				_t = _retTree;
				j_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case UNNEST:
			case RANGE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			f = _t==ASTNULL ? null : (AST)_t;
			fromElement(_t);
			_t = _retTree;
			f_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			AST __t199 = _t;
			AST tmp93_AST = null;
			AST tmp93_AST_in = null;
			tmp93_AST = astFactory.create((AST)_t);
			tmp93_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp93_AST);
			ASTPair __currentAST199 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,ON);
			_t = _t.getFirstChild();
			inFrom = false;
			logicalExpr(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			inFrom = true;
			currentAST = __currentAST199;
			_t = __t199;
			_t = _t.getNextSibling();
			currentAST = __currentAST197;
			_t = __t197;
			_t = _t.getNextSibling();
			joinElement_AST = (AST)currentAST.root;
			
					if (j==null || j_AST.getType()==INNER) {
						joinElement_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(INNER_JOIN,"inner join")));
					}
					else if (j_AST.getType()==LEFT) {
						joinElement_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(LEFT_OUTER,"left outer join")));
					}
					else if (j_AST.getType()==RIGHT) {
						joinElement_AST = (AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(RIGHT_OUTER,"right outer join")));
					}
					joinElement_AST.setNextSibling(f_AST);
				
			currentAST.root = joinElement_AST;
			currentAST.child = joinElement_AST!=null &&joinElement_AST.getFirstChild()!=null ?
				joinElement_AST.getFirstChild() : joinElement_AST;
			currentAST.advanceChildToEnd();
			joinElement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = joinElement_AST;
		_retTree = _t;
	}
	
	public final void joinType(AST _t) throws RecognitionException {
		
		AST joinType_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST joinType_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LEFT:
			{
				AST tmp94_AST = null;
				AST tmp94_AST_in = null;
				tmp94_AST = astFactory.create((AST)_t);
				tmp94_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp94_AST);
				match(_t,LEFT);
				_t = _t.getNextSibling();
				joinType_AST = (AST)currentAST.root;
				break;
			}
			case RIGHT:
			{
				AST tmp95_AST = null;
				AST tmp95_AST_in = null;
				tmp95_AST = astFactory.create((AST)_t);
				tmp95_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp95_AST);
				match(_t,RIGHT);
				_t = _t.getNextSibling();
				joinType_AST = (AST)currentAST.root;
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
		returnAST = joinType_AST;
		_retTree = _t;
	}
	
	public final void inRhs(AST _t) throws RecognitionException {
		
		AST inRhs_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST inRhs_AST = null;
		
		try {      // for error handling
			AST __t241 = _t;
			AST tmp96_AST = null;
			AST tmp96_AST_in = null;
			tmp96_AST = astFactory.create((AST)_t);
			tmp96_AST_in = (AST)_t;
			astFactory.addASTChild(currentAST, tmp96_AST);
			ASTPair __currentAST241 = currentAST.copy();
			currentAST.root = currentAST.child;
			currentAST.child = null;
			match(_t,IN_LIST);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case QUERY:
			{
				query(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case 3:
			case ARRAY:
			case CASE:
			case FALSE:
			case NULL:
			case TRUE:
			case TYPEOF:
			case AGGREGATE:
			case DOT:
			case LANGUE_OP:
			case INDEX_OP:
			case METHOD_CALL:
			case CASE2:
			case UNARY_MINUS:
			case IDENT:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case STAR:
			case NUM_INT:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case DIV:
			{
				{
				{
				_loop245:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_tokenSet_1.member(_t.getType()))) {
						expr(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop245;
					}
					
				} while (true);
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
			currentAST = __currentAST241;
			_t = __t241;
			_t = _t.getNextSibling();
			inRhs_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = inRhs_AST;
		_retTree = _t;
	}
	
	public final void comparisonExpr(AST _t) throws RecognitionException {
		
		AST comparisonExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST comparisonExpr_AST = null;
		AST olike = null;
		AST olike_AST = null;
		AST elike1_AST = null;
		AST elike1 = null;
		AST elike2_AST = null;
		AST elike2 = null;
		AST onotlike = null;
		AST onotlike_AST = null;
		AST enotlike1_AST = null;
		AST enotlike1 = null;
		AST enotlike2_AST = null;
		AST enotlike2 = null;
		AST i_AST = null;
		AST i = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case EQ:
			{
				AST __t216 = _t;
				AST tmp97_AST = null;
				AST tmp97_AST_in = null;
				tmp97_AST = astFactory.create((AST)_t);
				tmp97_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp97_AST);
				ASTPair __currentAST216 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,EQ);
				_t = _t.getFirstChild();
				exprOrSubquery(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				exprOrSubquery(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST216;
				_t = __t216;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case NE:
			{
				AST __t217 = _t;
				AST tmp98_AST = null;
				AST tmp98_AST_in = null;
				tmp98_AST = astFactory.create((AST)_t);
				tmp98_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp98_AST);
				ASTPair __currentAST217 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,NE);
				_t = _t.getFirstChild();
				exprOrSubquery(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				exprOrSubquery(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST217;
				_t = __t217;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case LT:
			{
				AST __t218 = _t;
				AST tmp99_AST = null;
				AST tmp99_AST_in = null;
				tmp99_AST = astFactory.create((AST)_t);
				tmp99_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp99_AST);
				ASTPair __currentAST218 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,LT);
				_t = _t.getFirstChild();
				exprOrSubquery(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				exprOrSubquery(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST218;
				_t = __t218;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case GT:
			{
				AST __t219 = _t;
				AST tmp100_AST = null;
				AST tmp100_AST_in = null;
				tmp100_AST = astFactory.create((AST)_t);
				tmp100_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp100_AST);
				ASTPair __currentAST219 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,GT);
				_t = _t.getFirstChild();
				exprOrSubquery(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				exprOrSubquery(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST219;
				_t = __t219;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case LE:
			{
				AST __t220 = _t;
				AST tmp101_AST = null;
				AST tmp101_AST_in = null;
				tmp101_AST = astFactory.create((AST)_t);
				tmp101_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp101_AST);
				ASTPair __currentAST220 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,LE);
				_t = _t.getFirstChild();
				exprOrSubquery(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				exprOrSubquery(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST220;
				_t = __t220;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case GE:
			{
				AST __t221 = _t;
				AST tmp102_AST = null;
				AST tmp102_AST_in = null;
				tmp102_AST = astFactory.create((AST)_t);
				tmp102_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp102_AST);
				ASTPair __currentAST221 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,GE);
				_t = _t.getFirstChild();
				exprOrSubquery(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				exprOrSubquery(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST221;
				_t = __t221;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case LIKE:
			{
				AST __t222 = _t;
				olike = _t==ASTNULL ? null :(AST)_t;
				AST olike_AST_in = null;
				olike_AST = astFactory.create(olike);
				astFactory.addASTChild(currentAST, olike_AST);
				ASTPair __currentAST222 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,LIKE);
				_t = _t.getFirstChild();
				elike1 = _t==ASTNULL ? null : (AST)_t;
				expr(_t);
				_t = _retTree;
				elike1_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				elike2 = _t==ASTNULL ? null : (AST)_t;
				expr(_t);
				_t = _retTree;
				elike2_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST222;
				_t = __t222;
				_t = _t.getNextSibling();
				checkType(elike1_AST,olike_AST, elike2_AST);
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case NOT_LIKE:
			{
				AST __t223 = _t;
				onotlike = _t==ASTNULL ? null :(AST)_t;
				AST onotlike_AST_in = null;
				onotlike_AST = astFactory.create(onotlike);
				astFactory.addASTChild(currentAST, onotlike_AST);
				ASTPair __currentAST223 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,NOT_LIKE);
				_t = _t.getFirstChild();
				enotlike1 = _t==ASTNULL ? null : (AST)_t;
				expr(_t);
				_t = _retTree;
				enotlike1_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				enotlike2 = _t==ASTNULL ? null : (AST)_t;
				expr(_t);
				_t = _retTree;
				enotlike2_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST223;
				_t = __t223;
				_t = _t.getNextSibling();
				checkType(enotlike1_AST,onotlike_AST,enotlike2_AST);
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case BETWEEN:
			{
				AST __t224 = _t;
				AST tmp103_AST = null;
				AST tmp103_AST_in = null;
				tmp103_AST = astFactory.create((AST)_t);
				tmp103_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp103_AST);
				ASTPair __currentAST224 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,BETWEEN);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST224;
				_t = __t224;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case NOT_BETWEEN:
			{
				AST __t225 = _t;
				AST tmp104_AST = null;
				AST tmp104_AST_in = null;
				tmp104_AST = astFactory.create((AST)_t);
				tmp104_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp104_AST);
				ASTPair __currentAST225 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,NOT_BETWEEN);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST225;
				_t = __t225;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case IN:
			{
				AST __t226 = _t;
				AST tmp105_AST = null;
				AST tmp105_AST_in = null;
				tmp105_AST = astFactory.create((AST)_t);
				tmp105_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp105_AST);
				ASTPair __currentAST226 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,IN);
				_t = _t.getFirstChild();
				inLhs(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				inRhs(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST226;
				_t = __t226;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case NOT_IN:
			{
				AST __t227 = _t;
				AST tmp106_AST = null;
				AST tmp106_AST_in = null;
				tmp106_AST = astFactory.create((AST)_t);
				tmp106_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp106_AST);
				ASTPair __currentAST227 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,NOT_IN);
				_t = _t.getFirstChild();
				inLhs(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				inRhs(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST227;
				_t = __t227;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case IS_NULL:
			{
				AST __t228 = _t;
				AST tmp107_AST = null;
				AST tmp107_AST_in = null;
				tmp107_AST = astFactory.create((AST)_t);
				tmp107_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp107_AST);
				ASTPair __currentAST228 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,IS_NULL);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST228;
				_t = __t228;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case IS_NOT_NULL:
			{
				AST __t229 = _t;
				AST tmp108_AST = null;
				AST tmp108_AST_in = null;
				tmp108_AST = astFactory.create((AST)_t);
				tmp108_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp108_AST);
				ASTPair __currentAST229 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,IS_NOT_NULL);
				_t = _t.getFirstChild();
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				currentAST = __currentAST229;
				_t = __t229;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case IS_OF:
			{
				AST __t230 = _t;
				AST tmp109_AST = null;
				AST tmp109_AST_in = null;
				tmp109_AST = astFactory.create((AST)_t);
				tmp109_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp109_AST);
				ASTPair __currentAST230 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,IS_OF);
				_t = _t.getFirstChild();
				i = _t==ASTNULL ? null : (AST)_t;
				propertyRef(_t);
				_t = _retTree;
				i_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				{
				int _cnt234=0;
				_loop234:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_tokenSet_2.member(_t.getType()))) {
						{
						if (_t==null) _t=ASTNULL;
						switch ( _t.getType()) {
						case ONLY:
						{
							AST tmp110_AST = null;
							AST tmp110_AST_in = null;
							tmp110_AST = astFactory.create((AST)_t);
							tmp110_AST_in = (AST)_t;
							astFactory.addASTChild(currentAST, tmp110_AST);
							match(_t,ONLY);
							_t = _t.getNextSibling();
							break;
						}
						case REF:
						case IDENT:
						case PREDEFINED_TYPE:
						case ARRAY_DEF:
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
						case REF:
						case PREDEFINED_TYPE:
						case ARRAY_DEF:
						{
							datatype(_t);
							_t = _retTree;
							astFactory.addASTChild(currentAST, returnAST);
							break;
						}
						case IDENT:
						{
							identifier(_t);
							_t = _retTree;
							astFactory.addASTChild(currentAST, returnAST);
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
						if ( _cnt234>=1 ) { break _loop234; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt234++;
				} while (true);
				}
				currentAST = __currentAST230;
				_t = __t230;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				comparisonExpr_AST = resolveIsOf(i_AST,true);
				currentAST.root = comparisonExpr_AST;
				currentAST.child = comparisonExpr_AST!=null &&comparisonExpr_AST.getFirstChild()!=null ?
					comparisonExpr_AST.getFirstChild() : comparisonExpr_AST;
				currentAST.advanceChildToEnd();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case IS_NOT_OF:
			{
				AST __t235 = _t;
				AST tmp111_AST = null;
				AST tmp111_AST_in = null;
				tmp111_AST = astFactory.create((AST)_t);
				tmp111_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp111_AST);
				ASTPair __currentAST235 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,IS_NOT_OF);
				_t = _t.getFirstChild();
				identifier(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				{
				int _cnt237=0;
				_loop237:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==IDENT)) {
						identifier(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt237>=1 ) { break _loop237; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt237++;
				} while (true);
				}
				currentAST = __currentAST235;
				_t = __t235;
				_t = _t.getNextSibling();
				comparisonExpr_AST = (AST)currentAST.root;
				comparisonExpr_AST = resolveIsOf(i_AST,false);
				currentAST.root = comparisonExpr_AST;
				currentAST.child = comparisonExpr_AST!=null &&comparisonExpr_AST.getFirstChild()!=null ?
					comparisonExpr_AST.getFirstChild() : comparisonExpr_AST;
				currentAST.advanceChildToEnd();
				comparisonExpr_AST = (AST)currentAST.root;
				break;
			}
			case EXISTS:
			{
				AST __t238 = _t;
				AST tmp112_AST = null;
				AST tmp112_AST_in = null;
				tmp112_AST = astFactory.create((AST)_t);
				tmp112_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp112_AST);
				ASTPair __currentAST238 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,EXISTS);
				_t = _t.getFirstChild();
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case ARRAY:
				case CASE:
				case FALSE:
				case NULL:
				case TRUE:
				case TYPEOF:
				case AGGREGATE:
				case DOT:
				case LANGUE_OP:
				case INDEX_OP:
				case METHOD_CALL:
				case CASE2:
				case UNARY_MINUS:
				case IDENT:
				case NUM_DOUBLE:
				case NUM_FLOAT:
				case NUM_LONG:
				case STAR:
				case NUM_INT:
				case QUOTED_STRING:
				case PLUS:
				case MINUS:
				case DIV:
				{
					expr(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case QUERY:
				{
					query(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(_t);
				}
				}
				}
				currentAST = __currentAST238;
				_t = __t238;
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
	
	public final void inLhs(AST _t) throws RecognitionException {
		
		AST inLhs_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST inLhs_AST = null;
		
		try {      // for error handling
			expr(_t);
			_t = _retTree;
			astFactory.addASTChild(currentAST, returnAST);
			inLhs_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		returnAST = inLhs_AST;
		_retTree = _t;
	}
	
	public final void caseExpr(AST _t) throws RecognitionException {
		
		AST caseExpr_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST caseExpr_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CASE:
			{
				AST __t260 = _t;
				AST tmp113_AST = null;
				AST tmp113_AST_in = null;
				tmp113_AST = astFactory.create((AST)_t);
				tmp113_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp113_AST);
				ASTPair __currentAST260 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,CASE);
				_t = _t.getFirstChild();
				inCase = true;
				{
				int _cnt263=0;
				_loop263:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==WHEN)) {
						AST __t262 = _t;
						AST tmp114_AST = null;
						AST tmp114_AST_in = null;
						tmp114_AST = astFactory.create((AST)_t);
						tmp114_AST_in = (AST)_t;
						astFactory.addASTChild(currentAST, tmp114_AST);
						ASTPair __currentAST262 = currentAST.copy();
						currentAST.root = currentAST.child;
						currentAST.child = null;
						match(_t,WHEN);
						_t = _t.getFirstChild();
						logicalExpr(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
						expr(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
						currentAST = __currentAST262;
						_t = __t262;
						_t = _t.getNextSibling();
					}
					else {
						if ( _cnt263>=1 ) { break _loop263; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt263++;
				} while (true);
				}
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case ELSE:
				{
					AST __t265 = _t;
					AST tmp115_AST = null;
					AST tmp115_AST_in = null;
					tmp115_AST = astFactory.create((AST)_t);
					tmp115_AST_in = (AST)_t;
					astFactory.addASTChild(currentAST, tmp115_AST);
					ASTPair __currentAST265 = currentAST.copy();
					currentAST.root = currentAST.child;
					currentAST.child = null;
					match(_t,ELSE);
					_t = _t.getFirstChild();
					expr(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					currentAST = __currentAST265;
					_t = __t265;
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
				currentAST = __currentAST260;
				_t = __t260;
				_t = _t.getNextSibling();
				inCase = false;
				caseExpr_AST = (AST)currentAST.root;
				break;
			}
			case CASE2:
			{
				AST __t266 = _t;
				AST tmp116_AST = null;
				AST tmp116_AST_in = null;
				tmp116_AST = astFactory.create((AST)_t);
				tmp116_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp116_AST);
				ASTPair __currentAST266 = currentAST.copy();
				currentAST.root = currentAST.child;
				currentAST.child = null;
				match(_t,CASE2);
				_t = _t.getFirstChild();
				inCase = true;
				expr(_t);
				_t = _retTree;
				astFactory.addASTChild(currentAST, returnAST);
				{
				int _cnt269=0;
				_loop269:
				do {
					if (_t==null) _t=ASTNULL;
					if ((_t.getType()==WHEN)) {
						AST __t268 = _t;
						AST tmp117_AST = null;
						AST tmp117_AST_in = null;
						tmp117_AST = astFactory.create((AST)_t);
						tmp117_AST_in = (AST)_t;
						astFactory.addASTChild(currentAST, tmp117_AST);
						ASTPair __currentAST268 = currentAST.copy();
						currentAST.root = currentAST.child;
						currentAST.child = null;
						match(_t,WHEN);
						_t = _t.getFirstChild();
						expr(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
						expr(_t);
						_t = _retTree;
						astFactory.addASTChild(currentAST, returnAST);
						currentAST = __currentAST268;
						_t = __t268;
						_t = _t.getNextSibling();
					}
					else {
						if ( _cnt269>=1 ) { break _loop269; } else {throw new NoViableAltException(_t);}
					}
					
					_cnt269++;
				} while (true);
				}
				{
				if (_t==null) _t=ASTNULL;
				switch ( _t.getType()) {
				case ELSE:
				{
					AST __t271 = _t;
					AST tmp118_AST = null;
					AST tmp118_AST_in = null;
					tmp118_AST = astFactory.create((AST)_t);
					tmp118_AST_in = (AST)_t;
					astFactory.addASTChild(currentAST, tmp118_AST);
					ASTPair __currentAST271 = currentAST.copy();
					currentAST.root = currentAST.child;
					currentAST.child = null;
					match(_t,ELSE);
					_t = _t.getFirstChild();
					expr(_t);
					_t = _retTree;
					astFactory.addASTChild(currentAST, returnAST);
					currentAST = __currentAST271;
					_t = __t271;
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
				currentAST = __currentAST266;
				_t = __t266;
				_t = _t.getNextSibling();
				inCase = false;
				caseExpr_AST = (AST)currentAST.root;
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
		returnAST = caseExpr_AST;
		_retTree = _t;
	}
	
	public final void literal(AST _t) throws RecognitionException {
		
		AST literal_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST literal_AST = null;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case NUM_INT:
			{
				AST tmp119_AST = null;
				AST tmp119_AST_in = null;
				tmp119_AST = astFactory.create((AST)_t);
				tmp119_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp119_AST);
				match(_t,NUM_INT);
				_t = _t.getNextSibling();
				literal_AST = (AST)currentAST.root;
				break;
			}
			case NUM_FLOAT:
			{
				AST tmp120_AST = null;
				AST tmp120_AST_in = null;
				tmp120_AST = astFactory.create((AST)_t);
				tmp120_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp120_AST);
				match(_t,NUM_FLOAT);
				_t = _t.getNextSibling();
				literal_AST = (AST)currentAST.root;
				break;
			}
			case NUM_LONG:
			{
				AST tmp121_AST = null;
				AST tmp121_AST_in = null;
				tmp121_AST = astFactory.create((AST)_t);
				tmp121_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp121_AST);
				match(_t,NUM_LONG);
				_t = _t.getNextSibling();
				literal_AST = (AST)currentAST.root;
				break;
			}
			case NUM_DOUBLE:
			{
				AST tmp122_AST = null;
				AST tmp122_AST_in = null;
				tmp122_AST = astFactory.create((AST)_t);
				tmp122_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp122_AST);
				match(_t,NUM_DOUBLE);
				_t = _t.getNextSibling();
				literal_AST = (AST)currentAST.root;
				break;
			}
			case QUOTED_STRING:
			{
				AST tmp123_AST = null;
				AST tmp123_AST_in = null;
				tmp123_AST = astFactory.create((AST)_t);
				tmp123_AST_in = (AST)_t;
				astFactory.addASTChild(currentAST, tmp123_AST);
				match(_t,QUOTED_STRING);
				_t = _t.getNextSibling();
				literal_AST = (AST)currentAST.root;
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
		returnAST = literal_AST;
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
		"SOME"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 0L, 576461585527080960L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 288230384741777920L, 648607142642715648L, 58731022L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 2305843009213693952L, 7493989779944505352L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	}
	
