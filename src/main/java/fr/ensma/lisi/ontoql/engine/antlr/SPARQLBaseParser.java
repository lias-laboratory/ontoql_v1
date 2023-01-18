// $ANTLR 2.7.7 (20060906): "SPARQL-syntaxique.g" -> "SPARQLBaseParser.java"$


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


import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.collections.AST;
import java.util.Hashtable;
import antlr.ASTFactory;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;

/**
 * SPARQL Grammar
 * <br>
 * This grammar parses the SPARQL query language according to
 * the working draft of October 4, 2006. The comments provided
 * are extracted from the grammar given in this working draft.
 *
 * @author Stephane JEAN 
 */
public class SPARQLBaseParser extends antlr.LLkParser       implements SPARQLTokenTypes
 {

	/** selectClause of the SPARQL query. */
	protected AST selectClauseAST = null;
	
	/** namespaceClause of the SPARQL query. */
	protected AST namespaceClauseAST = null;
	
	/** result of an union query. */
	protected AST unionQueryAST = null;
	
	/** Create a query of an union query. */
	protected AST createUnionQuery(AST triples1, AST triples2) {
		AST res = astFactory.create(UNION);
		res.addChild(createQuery(triples1));
		res.addChild(createQuery(triples2));
		return res;
	}
	
	/** Create a query of an union and a query. */
	protected AST createUnion(AST union, AST triples2) {
		AST res = astFactory.create(UNION);
		res.addChild(union);
		res.addChild(createQuery(triples2));
		return res;
	}
	
	/** Create a query of an union query. */
	protected AST createQuery(AST triples) {
		AST query = astFactory.create(QUERY);
		query.addChild(astFactory.dupTree(namespaceClauseAST));
		AST selectFromAST = astFactory.create(SELECT_WHERE);
		AST whereAST = astFactory.create(WHERE);
		whereAST.addChild(triples);
		selectFromAST.addChild(whereAST);
		selectFromAST.addChild(selectClauseAST);
		query.addChild(selectFromAST);
		return query;
	}
	

protected SPARQLBaseParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public SPARQLBaseParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected SPARQLBaseParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public SPARQLBaseParser(TokenStream lexer) {
  this(lexer,2);
}

public SPARQLBaseParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void query() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST query_AST = null;
		AST p_AST = null;
		AST s_AST = null;
		
		try {      // for error handling
			prolog();
			p_AST = (AST)returnAST;
			selectQuery();
			s_AST = (AST)returnAST;
			query_AST = (AST)currentAST.root;
			
					if (unionQueryAST == null) {
						query_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(QUERY,"query")).add(p_AST).add(s_AST));
					}
					else {
						query_AST = unionQueryAST;
					}
					
				
			currentAST.root = query_AST;
			currentAST.child = query_AST!=null &&query_AST.getFirstChild()!=null ?
				query_AST.getFirstChild() : query_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = query_AST;
	}
	
	public final void prolog() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST prolog_AST = null;
		
		try {      // for error handling
			{
			_loop4:
			do {
				if ((LA(1)==PREFIX)) {
					prefixDecl();
				}
				else {
					break _loop4;
				}
				
			} while (true);
			}
			prolog_AST = (AST)currentAST.root;
			
					namespaceClauseAST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(PREFIX,"PREFIX")).add(astFactory.create(QNAME,"geo:")).add(astFactory.create(Q_IRI_REF,"http://lisi.ensma.fr/")));
					prolog_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(PREFIX,"PREFIX")).add(astFactory.create(QNAME,"geo:")).add(astFactory.create(Q_IRI_REF,"http://lisi.ensma.fr/")));
				
			currentAST.root = prolog_AST;
			currentAST.child = prolog_AST!=null &&prolog_AST.getFirstChild()!=null ?
				prolog_AST.getFirstChild() : prolog_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		returnAST = prolog_AST;
	}
	
	public final void selectQuery() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectQuery_AST = null;
		AST s_AST = null;
		AST w_AST = null;
		AST m_AST = null;
		
		try {      // for error handling
			selectClause();
			s_AST = (AST)returnAST;
			selectClauseAST = s_AST;
			whereClause();
			w_AST = (AST)returnAST;
			solutionModifier();
			m_AST = (AST)returnAST;
			selectQuery_AST = (AST)currentAST.root;
			
					selectQuery_AST = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(SELECT_WHERE,"SELECT_WHERE")).add(w_AST).add(s_AST).add(m_AST));
				
			currentAST.root = selectQuery_AST;
			currentAST.child = selectQuery_AST!=null &&selectQuery_AST.getFirstChild()!=null ?
				selectQuery_AST.getFirstChild() : selectQuery_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = selectQuery_AST;
	}
	
	public final void prefixDecl() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST prefixDecl_AST = null;
		
		try {      // for error handling
			AST tmp1_AST = null;
			tmp1_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp1_AST);
			match(PREFIX);
			AST tmp2_AST = null;
			tmp2_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp2_AST);
			match(QNAME);
			AST tmp3_AST = null;
			tmp3_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp3_AST);
			match(Q_IRI_REF);
			prefixDecl_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		returnAST = prefixDecl_AST;
	}
	
	public final void selectClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectClause_AST = null;
		
		try {      // for error handling
			AST tmp4_AST = null;
			tmp4_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp4_AST);
			match(SELECT);
			{
			switch ( LA(1)) {
			case DISTINCT:
			{
				AST tmp5_AST = null;
				tmp5_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp5_AST);
				match(DISTINCT);
				break;
			}
			case VAR:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			int _cnt10=0;
			_loop10:
			do {
				if ((LA(1)==VAR)) {
					AST tmp6_AST = null;
					tmp6_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp6_AST);
					match(VAR);
				}
				else {
					if ( _cnt10>=1 ) { break _loop10; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt10++;
			} while (true);
			}
			selectClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		returnAST = selectClause_AST;
	}
	
	public final void whereClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST whereClause_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case WHERE:
			{
				AST tmp7_AST = null;
				tmp7_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp7_AST);
				match(WHERE);
				break;
			}
			case OPEN_CURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			groupGraphPattern();
			astFactory.addASTChild(currentAST, returnAST);
			whereClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
		returnAST = whereClause_AST;
	}
	
	public final void solutionModifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST solutionModifier_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case ORDER:
			{
				orderClause();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			solutionModifier_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = solutionModifier_AST;
	}
	
	public final void groupGraphPattern() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST groupGraphPattern_AST = null;
		AST g_AST = null;
		
		try {      // for error handling
			match(OPEN_CURLY);
			graphPattern();
			g_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			match(CLOSE_CURLY);
			groupGraphPattern_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
		returnAST = groupGraphPattern_AST;
	}
	
	public final void orderClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orderClause_AST = null;
		
		try {      // for error handling
			AST tmp10_AST = null;
			tmp10_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp10_AST);
			match(ORDER);
			match(LITERAL_by);
			{
			int _cnt17=0;
			_loop17:
			do {
				if ((LA(1)==ASCENDING||LA(1)==DESCENDING)) {
					orderCondition();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					if ( _cnt17>=1 ) { break _loop17; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt17++;
			} while (true);
			}
			orderClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = orderClause_AST;
	}
	
	public final void orderCondition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orderCondition_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case ASCENDING:
			{
				AST tmp12_AST = null;
				tmp12_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp12_AST);
				match(ASCENDING);
				break;
			}
			case DESCENDING:
			{
				AST tmp13_AST = null;
				tmp13_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp13_AST);
				match(DESCENDING);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			brackettedExpression();
			astFactory.addASTChild(currentAST, returnAST);
			orderCondition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
		returnAST = orderCondition_AST;
	}
	
	public final void brackettedExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST brackettedExpression_AST = null;
		
		try {      // for error handling
			match(OPEN);
			expression();
			astFactory.addASTChild(currentAST, returnAST);
			match(CLOSE);
			brackettedExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		returnAST = brackettedExpression_AST;
	}
	
	public final void graphPattern() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST graphPattern_AST = null;
		
		try {      // for error handling
			filteredBasicGraphPattern();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case OPTIONAL:
			case OPEN_CURLY:
			{
				graphPatternNotTriples();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case DOT:
				{
					match(DOT);
					break;
				}
				case OPTIONAL:
				case FILTER:
				case QNAME:
				case Q_IRI_REF:
				case VAR:
				case OPEN_CURLY:
				case CLOSE_CURLY:
				case STRING_LITERAL1:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				graphPattern();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case CLOSE_CURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			graphPattern_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		returnAST = graphPattern_AST;
	}
	
	public final void filteredBasicGraphPattern() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST filteredBasicGraphPattern_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case QNAME:
			case Q_IRI_REF:
			case VAR:
			case STRING_LITERAL1:
			{
				blockOfTriples();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case OPTIONAL:
			case FILTER:
			case OPEN_CURLY:
			case CLOSE_CURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			{
			switch ( LA(1)) {
			case FILTER:
			{
				constraint();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case DOT:
				{
					match(DOT);
					break;
				}
				case OPTIONAL:
				case FILTER:
				case QNAME:
				case Q_IRI_REF:
				case VAR:
				case OPEN_CURLY:
				case CLOSE_CURLY:
				case STRING_LITERAL1:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				filteredBasicGraphPattern();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case OPTIONAL:
			case OPEN_CURLY:
			case CLOSE_CURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			filteredBasicGraphPattern_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = filteredBasicGraphPattern_AST;
	}
	
	public final void graphPatternNotTriples() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST graphPatternNotTriples_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case OPTIONAL:
			{
				optionalGraphPattern();
				astFactory.addASTChild(currentAST, returnAST);
				graphPatternNotTriples_AST = (AST)currentAST.root;
				break;
			}
			case OPEN_CURLY:
			{
				groupOrUnionGraphPattern();
				astFactory.addASTChild(currentAST, returnAST);
				graphPatternNotTriples_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_10);
		}
		returnAST = graphPatternNotTriples_AST;
	}
	
	public final void blockOfTriples() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST blockOfTriples_AST = null;
		
		try {      // for error handling
			triplesSameSubject();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop31:
			do {
				if ((LA(1)==DOT)) {
					match(DOT);
					{
					switch ( LA(1)) {
					case QNAME:
					case Q_IRI_REF:
					case VAR:
					case STRING_LITERAL1:
					{
						triplesSameSubject();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					case DOT:
					case OPTIONAL:
					case FILTER:
					case OPEN_CURLY:
					case CLOSE_CURLY:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
				}
				else {
					break _loop31;
				}
				
			} while (true);
			}
			blockOfTriples_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		returnAST = blockOfTriples_AST;
	}
	
	public final void constraint() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constraint_AST = null;
		
		try {      // for error handling
			AST tmp19_AST = null;
			tmp19_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp19_AST);
			match(FILTER);
			{
			switch ( LA(1)) {
			case OPEN:
			{
				brackettedExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case BOUND:
			{
				builtInCall();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			constraint_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_10);
		}
		returnAST = constraint_AST;
	}
	
	public final void triplesSameSubject() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST triplesSameSubject_AST = null;
		AST s_AST = null;
		AST p_AST = null;
		AST o_AST = null;
		
		try {      // for error handling
			varOrTerm();
			s_AST = (AST)returnAST;
			verb();
			p_AST = (AST)returnAST;
			objectList();
			o_AST = (AST)returnAST;
			triplesSameSubject_AST = (AST)currentAST.root;
			
					if (p_AST.getType()==TYPE)
						triplesSameSubject_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(TRIPLE_TYPE)).add(s_AST).add(o_AST));
					else 
						triplesSameSubject_AST = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(TRIPLE_PROP)).add(s_AST).add(p_AST).add(o_AST));
				
			currentAST.root = triplesSameSubject_AST;
			currentAST.child = triplesSameSubject_AST!=null &&triplesSameSubject_AST.getFirstChild()!=null ?
				triplesSameSubject_AST.getFirstChild() : triplesSameSubject_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = triplesSameSubject_AST;
	}
	
	public final void optionalGraphPattern() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST optionalGraphPattern_AST = null;
		
		try {      // for error handling
			AST tmp20_AST = null;
			tmp20_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp20_AST);
			match(OPTIONAL);
			groupGraphPattern();
			astFactory.addASTChild(currentAST, returnAST);
			optionalGraphPattern_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_10);
		}
		returnAST = optionalGraphPattern_AST;
	}
	
	public final void groupOrUnionGraphPattern() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST groupOrUnionGraphPattern_AST = null;
		AST g1_AST = null;
		AST g2_AST = null;
		
		try {      // for error handling
			groupGraphPattern();
			g1_AST = (AST)returnAST;
			{
			_loop36:
			do {
				if ((LA(1)==UNION)) {
					AST tmp21_AST = null;
					tmp21_AST = astFactory.create(LT(1));
					match(UNION);
					groupGraphPattern();
					g2_AST = (AST)returnAST;
					
							if (unionQueryAST == null)
								unionQueryAST =createUnionQuery(g1_AST,g2_AST);
							else 
								unionQueryAST = createUnion(unionQueryAST, g2_AST);
						
				}
				else {
					break _loop36;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_10);
		}
		returnAST = groupOrUnionGraphPattern_AST;
	}
	
	public final void builtInCall() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST builtInCall_AST = null;
		
		try {      // for error handling
			AST tmp22_AST = null;
			tmp22_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp22_AST);
			match(BOUND);
			match(OPEN);
			var();
			astFactory.addASTChild(currentAST, returnAST);
			match(CLOSE);
			builtInCall_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_13);
		}
		returnAST = builtInCall_AST;
	}
	
	public final void varOrTerm() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST varOrTerm_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case VAR:
			{
				AST tmp25_AST = null;
				tmp25_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp25_AST);
				match(VAR);
				varOrTerm_AST = (AST)currentAST.root;
				break;
			}
			case QNAME:
			case Q_IRI_REF:
			case STRING_LITERAL1:
			{
				graphTerm();
				astFactory.addASTChild(currentAST, returnAST);
				varOrTerm_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_14);
		}
		returnAST = varOrTerm_AST;
	}
	
	public final void verb() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST verb_AST = null;
		
		try {      // for error handling
			varOrIRIref();
			astFactory.addASTChild(currentAST, returnAST);
			verb_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = verb_AST;
	}
	
	public final void objectList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST objectList_AST = null;
		
		try {      // for error handling
			graphNode();
			astFactory.addASTChild(currentAST, returnAST);
			objectList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = objectList_AST;
	}
	
	public final void graphNode() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST graphNode_AST = null;
		
		try {      // for error handling
			varOrTerm();
			astFactory.addASTChild(currentAST, returnAST);
			graphNode_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = graphNode_AST;
	}
	
	public final void varOrIRIref() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST varOrIRIref_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case VAR:
			{
				var();
				astFactory.addASTChild(currentAST, returnAST);
				varOrIRIref_AST = (AST)currentAST.root;
				break;
			}
			case QNAME:
			case Q_IRI_REF:
			{
				iriRef();
				astFactory.addASTChild(currentAST, returnAST);
				varOrIRIref_AST = (AST)currentAST.root;
				break;
			}
			case TYPE:
			{
				AST tmp26_AST = null;
				tmp26_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp26_AST);
				match(TYPE);
				varOrIRIref_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = varOrIRIref_AST;
	}
	
	public final void graphTerm() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST graphTerm_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case QNAME:
			case Q_IRI_REF:
			{
				iriRef();
				astFactory.addASTChild(currentAST, returnAST);
				graphTerm_AST = (AST)currentAST.root;
				break;
			}
			case STRING_LITERAL1:
			{
				rdfLiteral();
				astFactory.addASTChild(currentAST, returnAST);
				graphTerm_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_14);
		}
		returnAST = graphTerm_AST;
	}
	
	public final void var() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST var_AST = null;
		
		try {      // for error handling
			AST tmp27_AST = null;
			tmp27_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp27_AST);
			match(VAR);
			var_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_16);
		}
		returnAST = var_AST;
	}
	
	public final void iriRef() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST iriRef_AST = null;
		Token  q = null;
		AST q_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case Q_IRI_REF:
			{
				AST tmp28_AST = null;
				tmp28_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp28_AST);
				match(Q_IRI_REF);
				iriRef_AST = (AST)currentAST.root;
				break;
			}
			case QNAME:
			{
				q = LT(1);
				q_AST = astFactory.create(q);
				astFactory.addASTChild(currentAST, q_AST);
				match(QNAME);
				iriRef_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_17);
		}
		returnAST = iriRef_AST;
	}
	
	public final void rdfLiteral() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST rdfLiteral_AST = null;
		
		try {      // for error handling
			string();
			astFactory.addASTChild(currentAST, returnAST);
			rdfLiteral_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = rdfLiteral_AST;
	}
	
	public final void expression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST expression_AST = null;
		
		try {      // for error handling
			conditionalOrExpression();
			astFactory.addASTChild(currentAST, returnAST);
			expression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_19);
		}
		returnAST = expression_AST;
	}
	
	public final void conditionalOrExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conditionalOrExpression_AST = null;
		
		try {      // for error handling
			conditionalAndExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop50:
			do {
				if ((LA(1)==OR)) {
					AST tmp29_AST = null;
					tmp29_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp29_AST);
					match(OR);
					conditionalAndExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop50;
				}
				
			} while (true);
			}
			conditionalOrExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_19);
		}
		returnAST = conditionalOrExpression_AST;
	}
	
	public final void conditionalAndExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST conditionalAndExpression_AST = null;
		
		try {      // for error handling
			valueLogical();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop53:
			do {
				if ((LA(1)==AND)) {
					AST tmp30_AST = null;
					tmp30_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp30_AST);
					match(AND);
					valueLogical();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop53;
				}
				
			} while (true);
			}
			conditionalAndExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_20);
		}
		returnAST = conditionalAndExpression_AST;
	}
	
	public final void valueLogical() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST valueLogical_AST = null;
		
		try {      // for error handling
			relationalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			valueLogical_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_21);
		}
		returnAST = valueLogical_AST;
	}
	
	public final void relationalExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST relationalExpression_AST = null;
		
		try {      // for error handling
			numericExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case EQ:
			{
				AST tmp31_AST = null;
				tmp31_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp31_AST);
				match(EQ);
				numericExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case NE:
			{
				AST tmp32_AST = null;
				tmp32_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp32_AST);
				match(NE);
				numericExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LT:
			{
				AST tmp33_AST = null;
				tmp33_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp33_AST);
				match(LT);
				numericExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case GT:
			{
				AST tmp34_AST = null;
				tmp34_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp34_AST);
				match(GT);
				numericExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LE:
			{
				AST tmp35_AST = null;
				tmp35_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp35_AST);
				match(LE);
				numericExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case GE:
			{
				AST tmp36_AST = null;
				tmp36_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp36_AST);
				match(GE);
				numericExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case AND:
			case OR:
			case CLOSE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			relationalExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_21);
		}
		returnAST = relationalExpression_AST;
	}
	
	public final void numericExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST numericExpression_AST = null;
		
		try {      // for error handling
			additiveExpression();
			astFactory.addASTChild(currentAST, returnAST);
			numericExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_22);
		}
		returnAST = numericExpression_AST;
	}
	
	public final void additiveExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST additiveExpression_AST = null;
		
		try {      // for error handling
			multiplicativeExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop60:
			do {
				switch ( LA(1)) {
				case PLUS:
				{
					AST tmp37_AST = null;
					tmp37_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp37_AST);
					match(PLUS);
					multiplicativeExpression();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case MINUS:
				{
					AST tmp38_AST = null;
					tmp38_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp38_AST);
					match(MINUS);
					multiplicativeExpression();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					break _loop60;
				}
				}
			} while (true);
			}
			additiveExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_22);
		}
		returnAST = additiveExpression_AST;
	}
	
	public final void multiplicativeExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST multiplicativeExpression_AST = null;
		
		try {      // for error handling
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop63:
			do {
				switch ( LA(1)) {
				case STAR:
				{
					AST tmp39_AST = null;
					tmp39_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp39_AST);
					match(STAR);
					unaryExpression();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case DIV:
				{
					AST tmp40_AST = null;
					tmp40_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp40_AST);
					match(DIV);
					unaryExpression();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					break _loop63;
				}
				}
			} while (true);
			}
			multiplicativeExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_23);
		}
		returnAST = multiplicativeExpression_AST;
	}
	
	public final void unaryExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unaryExpression_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NOT:
			{
				AST tmp41_AST = null;
				tmp41_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp41_AST);
				match(NOT);
				primaryExpression();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case PLUS:
			{
				AST tmp42_AST = null;
				tmp42_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp42_AST);
				match(PLUS);
				primaryExpression();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case MINUS:
			{
				AST tmp43_AST = null;
				tmp43_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp43_AST);
				match(MINUS);
				primaryExpression();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case OPEN:
			case BOUND:
			case Q_IRI_REF:
			case VAR:
			case INTEGER:
			case STRING_LITERAL1:
			{
				primaryExpression();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_24);
		}
		returnAST = unaryExpression_AST;
	}
	
	public final void primaryExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primaryExpression_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case OPEN:
			{
				brackettedExpression();
				astFactory.addASTChild(currentAST, returnAST);
				primaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case BOUND:
			{
				builtInCall();
				astFactory.addASTChild(currentAST, returnAST);
				primaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case VAR:
			{
				var();
				astFactory.addASTChild(currentAST, returnAST);
				primaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case STRING_LITERAL1:
			{
				rdfLiteral();
				astFactory.addASTChild(currentAST, returnAST);
				primaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case INTEGER:
			{
				numericLiteral();
				astFactory.addASTChild(currentAST, returnAST);
				primaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case Q_IRI_REF:
			{
				AST tmp44_AST = null;
				tmp44_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp44_AST);
				match(Q_IRI_REF);
				primaryExpression_AST = (AST)currentAST.root;
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_24);
		}
		returnAST = primaryExpression_AST;
	}
	
	public final void numericLiteral() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST numericLiteral_AST = null;
		
		try {      // for error handling
			AST tmp45_AST = null;
			tmp45_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp45_AST);
			match(INTEGER);
			numericLiteral_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_24);
		}
		returnAST = numericLiteral_AST;
	}
	
	public final void string() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST string_AST = null;
		
		try {      // for error handling
			AST tmp46_AST = null;
			tmp46_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp46_AST);
			match(STRING_LITERAL1);
			string_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = string_AST;
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
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 0L, 32L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 0L, 32L, 1099511627776L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 0L, 2097152L, 9007199254740992L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { -9223372036854775806L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { -9223372036854775806L, 8589942784L, 106971486266327040L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 4196354L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 4611686018431584386L, 8589934592L, 106971486329045792L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 0L, 0L, 18014398509481984L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 0L, 0L, 27023796787478528L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 0L, 8589934592L, 106971486266327040L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 0L, 0L, 27032592880500736L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 0L, 8589934592L, 27032592880500736L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 4611686018427388032L, 8589934592L, 106971486329045792L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 0L, 8589934592L, 34931484414443520L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 0L, 0L, 79938893385826304L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 4611686018427388032L, 0L, 79938893448545056L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 0L, 8589934592L, 106989078452371456L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 4611686018427388032L, 8589934592L, 34931484477162272L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 0L, 0L, 32L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 4611686018427387904L, 0L, 32L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 4611686018427388032L, 0L, 32L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { 4611686018427388032L, 0L, 3997984L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 4611686018427388032L, 0L, 29163808L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { 4611686018427388032L, 0L, 62718752L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	
	}
