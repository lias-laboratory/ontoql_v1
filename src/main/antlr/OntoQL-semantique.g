header
{
	
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
}

/**
 * OntoQL Query tree to SQL Query tree  Transform.<br>
 *
 * @author Stephane JEAN
 */
class OntoQLSQLBaseWalker extends TreeParser;

options
{
	importVocab=OntoQL;        // import definitions from "Ontoql"
	exportVocab=OntoQLSQL;     // Call the resulting definitions "OntoqlSql"
	buildAST=true;
}

tokens
{
	SELECT_CLAUSE;
	LEFT_OUTER;
	RIGHT_OUTER;
	INNER_JOIN;
	JOIN_CONDITION;
	METHOD_NAME;    // An IDENT that is a method name.
}

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
}


// The main statement rule.
statement
	: ddlStatement | queryExpression | insertStatement | updateStatement | deleteStatement 
	;

ddlStatement
	: (createStatement | alterStatement | dropStatement | parameterStatement)
	;

createStatement
	: #(CREATE { beforeStatement( "create", CREATE ); } (ontologyDefinition | ddlExtent | viewDefinition | entityDefinition) ) {
		beforeStatementCompletion( "create" );
		postProcessCreate( #createStatement );
		afterStatementCompletion( "create" );
	}
	;

entityDefinition
	: #(ENTITY ONTOLOGY_MODEL_ID (subEntityClause)? (attributesClause)?)
	;

subEntityClause
	: #(UNDER ONTOLOGY_MODEL_ID)
	;

attributesClause
	: #(ATTRIBUTES attributeDefinition (attributeDefinition)* )
	;

attributeDefinition
	: #(ATTRIBUTE_DEF ONTOLOGY_MODEL_ID datatype)
	;
	
ontologyDefinition
	: ontologyDefinitionHead ontologyDefinitionBody
	;

ontologyDefinitionBody
	: classDefinitionBody
	| aPosterioriCaseOfBody
	;

classDefinitionBody
	: (descriptorClause)? (propertiesClause)?
	;

aPosterioriCaseOfBody
	: #(CASEOF identifier)
	;

ddlExtent
	: #(EXTENT identifier (identifier)*)
	;

viewDefinition
	: #(VIEW (identifier | typedClause) query)
	;

typedClause
	: #(OF identifier)
	;
	
ontologyDefinitionHead
	: #(ONTOLOGY_MODEL_ID identifier (optionalHeadClause)? )
	;

optionalHeadClause
	:	
	#(UNDER identifier) |
	#(CONTEXT identifier)
	;

descriptorClause
	: #(DESCRIPTOR assignmentAttributeList)
	;

propertiesClause
	: #(PROPERTIES propertyDef (propertyDef)* )
	;

propertyDef
	: #(PROPERTY_DEF identifier datatype (descriptorClause)?)
	;

assignmentAttributeList 
	: (assignmentAttribute)+ 
	;

assignmentAttribute
	: #( EQ attribute (expr|datatype) ) 
	;

attribute!
	: #(LANGUE_OP att:identifier lg_code:lgCode) {
		((IdentNode)#att).setLgCode(#lg_code.getText());
		#attribute = #att;
	}
	| i:identifier {
		#attribute = #i;
	}
	;

alterStatement
	: #(ALTER { beforeStatement( "alter", ALTER ); } (alterEntity|alterClass|alterExtent))  {
		beforeStatementCompletion( "alter" );
		postProcessAlter( #alterStatement );
		afterStatementCompletion( "alter" );
	}
	;

alterEntity
	: #(ENTITY ONTOLOGY_MODEL_ID alterEntityAction)
	;

alterEntityAction
	: ADD attributeDefinition
	| DROP ONTOLOGY_MODEL_ID
	;

alterExtent
	: #(EXTENT identifier ADD identifier)
	;
	
alterClass
	: #(ONTOLOGY_MODEL_ID identifier alterClassAction)
	;

alterClassAction
	: ADD propertyDef
	| DROP identifier
	;

dropStatement
	: #(DROP { beforeStatement( "drop", DROP ); } dropEntity)  {
		beforeStatementCompletion( "drop" );
		postProcessDrop( #dropStatement );
		afterStatementCompletion( "drop" );
	}
	;

dropEntity
	: #(ENTITY ONTOLOGY_MODEL_ID)
	;

parameterStatement
	: #(SET (namespaceSpecification | languageSpecification))
	;

namespaceSpecification
	: NAMESPACE (c:QUOTED_STRING {setGlobalNamespace(#c);}  | NONE {setGlobalNamespace(null);})
	;

languageSpecification
	: LANGUAGE (l:lgCode {setGlobalLanguage(#l);} | NONE {setGlobalLanguage(null);})
	;


insertStatement
	: #( INSERT  { beforeStatement( "insert", INSERT ); } intoClause (query | v:valueClause) ) {
		beforeStatementCompletion( "insert" );
		postProcessInsert( #insertStatement );
		afterStatementCompletion( "insert" );
	}
	;

intoClause
	: #( i:INTO { handleClauseStart( INTO );setIntoClause(#i);} p:identifier {processInsertTarget(#i, #p);} ps:insertablePropertySpec )
	;
	
insertablePropertySpec
	: #( RANGE (i:pathProperty {processInsertColumnElement(#i);} )+ )
	;
	
updateStatement
	: #( u:UPDATE { beforeStatement( "update", UPDATE ); } f:dmlFromClause s:setClause (w:whereClause)? ) {
	    beforeStatementCompletion( "update" );
		postProcessUpdate(#updateStatement);
		afterStatementCompletion( "update" );
	}
	;

dmlFromClause
	: #(f:FROM { pushFromClause(#dmlFromClause,f); } dmlFromElement )
	;

dmlFromElement! 
	: #(RANGE (p:identifier (o:ONLY)?  {
		   #dmlFromElement = createFromElement(p,o,null,false);
	 } ))
	 ;

setClause
	: #( SET assignmentList )
	;
	
assignmentList 
	: (assignment)+ {
		#assignmentList = addTypeOfUpdate(#assignmentList);
	}
	;

assignment
	: #( EQ (propertyRef) (exprOrSubquery)  ) 
	;

deleteStatement
	: #( DELETE { beforeStatement( "delete", DELETE ); } dmlFromClause (whereClause)? ) 
	;

valueClause
	: #( v:VALUES {setValuesClause(#v);} exprOrSubqueryList ) 
	;

exprOrSubqueryList
	: (e:expr {addValueInInsert(#e);} | q:query {addQueryInInsert(#q);})+
	;

queryExpression
	: #(UNION queryExpression {reinitWalker();} queryExpression)
	| #(EXCEPT queryExpression queryExpression)
	| #(INTERSECT queryExpression queryExpression)
	| selectStatement
	;

selectStatement
	: query
	;

// The query / subquery rule.
query!
	: #( QUERY { beforeStatement( "select", SELECT ); }
			(namespaceClause)?
			// The first phase places the FROM first to make processing the SELECT simpler.
			#(SELECT_FROM
				f:fromClause
				(s:selectClause)?
			)
			(w:whereClause)?
			(g:groupClause)?
			(o:orderClause)?
			(l:limitClause)?
			(p:preferenceClause)?
		) {
		#query = #([SELECT,"SELECT"], #s, #f, #w, #g, #o, #l, #p);
		beforeStatementCompletion( "select" );
		processQuery( #s, #query );
		afterStatementCompletion( "select" );
	}
	;


orderClause
	: #(ORDER { handleClauseStart( ORDER ); } orderExprs)
	;

limitClause
	: #(LIMIT NUM_INT (OFFSET NUM_INT)?)
	;

preferenceClause
	: #(PREFERRING { handleClauseStart( PREFERRING ); } i:identifier) {
		rewriteQueryWithPreference(#i);
		#preferenceClause = null;
	}
	;

namespaceClause
	: #(NAMESPACE (namespaceAlias)+ ) 
	;

namespaceAlias
	: #(c:QUOTED_STRING (a:NAME_ID)?) {
		setLocalNamespace(#c, #a);
	}
	;

orderExprs
	: expr ( ASCENDING | DESCENDING )? (orderExprs)?
	;

groupClause
	: #(GROUP { handleClauseStart( GROUP ); } (expr)+ ( #(HAVING logicalExpr) )? )
	;

selectClause!
	: #(SELECT { handleClauseStart( SELECT ); } (d:DISTINCT)? x:selectExprList ) {
		#selectClause = #([SELECT_CLAUSE,"{select clause}"], #d, #x);
	}
	;

selectExprList {
		boolean oldInSelect = inSelect;
		inSelect = true;
	}
	: ( selectExpr | aliasedSelectExpr )+ {
		inSelect = oldInSelect;
	}
	;

aliasedSelectExpr!
	: #(AS se:selectExpr i:identifier) {
	    setAlias(#se,#i);
		#aliasedSelectExpr = #se;
	}
	;


selectExpr
	: (propertyRef
	| count
	| functionCall
	| arithmeticExpr
	| arrayExpr
	| constant
	| r:ROW_STAR {#selectExpr=resolve(#r, null);}) {addSelectExpr(#selectExpr);}
	;

arrayExpr
	: #(ARRAY ((expr)+ | query))
	;

count
	: #(COUNT ( DISTINCT )? ( aggregateExpr | ROW_STAR  ) )
	;

aggregateExpr
	: expr // for arithmetic operation
	;

propertyRef!
	: path:pathProperty {#propertyRef=resolve(#path, null);}
	;

pathProperty!
	: i:identifier {#pathProperty = #i;}
	|
	#(LANGUE_OP att:identifier lg_code:lgCode) {
		((IdentNode)#att).setLgCode(#lg_code.getText());
		#pathProperty = #att;
	}
	|
	#(INDEX_OP descri:identifier index:NUM_INT) {
		((IdentNode)#descri).setIndex(index.getText());
		#pathProperty = #descri;
	}  
	|
	#(TYPEOF instanceAlias:identifier) {
		#pathProperty=resolveTypeOf(#instanceAlias);
	}
	|
	 #(d:DOT lhs:propertyRefLhs rhs:pathProperty ){#pathProperty = #(#d, #lhs, #rhs);}
	;


lgCode
	:
	FR | EN
	;
	

propertyRefLhs
	: pathProperty | f:functionTreatCall
	;

functionTreatCall
	: #(m:METHOD_CALL identifier #(EXPR_LIST identifier identifier) ) {#functionTreatCall = resolveTreatFunction(#m);}
	;

fromClause 
	: #(f:FROM { pushFromClause(#fromClause,f); handleClauseStart( FROM ); } fromElementList )
	;

fromElementList {
		boolean oldInFrom = inFrom;
		inFrom = true;
		}
	: (fromElement)+ {
		inFrom = oldInFrom;
		}
	;
	
fromElement! 
	// A simple class name, alias element.
	: #(RANGE p:identifier (s:ONLY)? (a:ALIAS)? ) {
		#fromElement = createFromElement(p,s,a,true);
	} (j:joinElement)? {if (#fromElement!= null)#fromElement.addChild(#j);}  
	| #(UNNEST property:propertyRef alias:ALIAS) {
		AST previousFromElement = unnest(#property,#alias);
	} (j2:joinElement)? {#previousFromElement.addChild(#j2);} 
	;

joinElement
	: #(JOIN (j:joinType)? f:fromElement #(ON {inFrom = false;} logicalExpr {inFrom = true;})) {
		if (j==null || #j.getType()==INNER) {
			#joinElement = #([INNER_JOIN, "inner join"]);
		}
		else if (#j.getType()==LEFT) {
			#joinElement = #([LEFT_OUTER, "left outer join"]);
		}
		else if (#j.getType()==RIGHT) {
			#joinElement = #([RIGHT_OUTER, "right outer join"]);
		}
		#joinElement.setNextSibling(#f);
	}
	;

joinType
	: LEFT
	| RIGHT
	;

identifier
	:  #(IDENT (n:NAMESPACE_ALIAS)?)
	;

datatype
	: #(PREDEFINED_TYPE (MULTILINGUAL|inRhs)?)
	| #(REF identifier)
	| #(ARRAY_DEF datatype)
	;
	
whereClause!
	: #(w:WHERE { handleClauseStart( WHERE ); } b:logicalExpr ) {
		// Use the *output* AST for the boolean expression!
		#whereClause = addWhereClause(#w , #b);
	}
	;


	
logicalExpr
	: #(AND logicalExpr logicalExpr)
	| #(OR logicalExpr logicalExpr)
	| #(NOT logicalExpr)
	| comparisonExpr
	;
	
comparisonExpr
	: #(EQ exprOrSubquery  exprOrSubquery)
	| #(NE exprOrSubquery exprOrSubquery)
	| #(LT exprOrSubquery exprOrSubquery)
	| #(GT exprOrSubquery exprOrSubquery)
	| #(LE exprOrSubquery exprOrSubquery)
	| #(GE exprOrSubquery exprOrSubquery)
	| #(olike:LIKE elike1:expr elike2:expr ){checkType(#elike1,#olike, #elike2);}
	| #(onotlike:NOT_LIKE enotlike1:expr enotlike2:expr ){checkType(#enotlike1,#onotlike,#enotlike2);}
	| #(BETWEEN expr expr expr)
	| #(NOT_BETWEEN expr expr expr)
	| #(IN inLhs inRhs )
	| #(NOT_IN inLhs inRhs )
	| #(IS_NULL expr)
	| #(IS_NOT_NULL expr)
	| #(IS_OF  i:propertyRef ((ONLY)? (datatype | identifier))+) {#comparisonExpr = resolveIsOf(#i,true);}
	| #(IS_NOT_OF identifier (identifier)+) {#comparisonExpr = resolveIsOf(#i,false);}
	| #(EXISTS ( expr | query ) )
	;

inRhs
	: #(IN_LIST ( query | ( (expr)* ) ) )
	;

inLhs
	: expr
	;

exprOrSubquery
	: expr
	| query
	| #(ANY (query | propertyRef))
	| #(ALL query)
	| #(SOME query)
	;


expr
	: propertyRef	// Resolve the top level 'address expression'
	| constant
	| arithmeticExpr
	| arrayExpr
	| functionCall
	;

arithmeticExpr
	: #(oplus:PLUS eplus1:expr eplus2:expr){checkType(#eplus1,#oplus,#eplus2);}
	| #(ominus:MINUS eminus1:expr eminus2:expr){checkType(#eminus1,#ominus,#eminus2);}
	| #(odiv:DIV ediv1:expr ediv2:expr){checkType(#ediv1,#odiv,#ediv2);}
	| #(ostar:STAR estar1:expr estar2:expr){checkType(#estar1,#ostar,#estar2);}
	| #(ounaryminus:UNARY_MINUS eunaryminus:expr){checkType(#eunaryminus,#ounaryminus,null);}
	| caseExpr
	;

caseExpr
	: #(CASE { inCase = true; } (#(WHEN logicalExpr expr))+ (#(ELSE expr))?) { inCase = false; }
	| #(CASE2 { inCase = true; } expr (#(WHEN expr expr))+ (#(ELSE expr))?) { inCase = false; }
	;

functionCall
	: #(m:METHOD_CALL  {inFunctionCall=true;} identifier ( #(EXPR_LIST e1:expr (e2:expr {checkType(#e1,#m,#e2);}|datatype)* ) )? )
		{ processFunction(#functionCall,inSelect); } {inFunctionCall=false;}
	| #(AGGREGATE aggregateExpr )
	;

constant
	: literal
	| NULL
	| TRUE 
	| FALSE
	;

literal
	: NUM_INT
	| NUM_FLOAT
	| NUM_LONG
	| NUM_DOUBLE
	| QUOTED_STRING
	;

