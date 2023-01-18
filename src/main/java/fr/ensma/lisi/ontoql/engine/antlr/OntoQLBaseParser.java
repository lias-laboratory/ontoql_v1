// $ANTLR 2.7.7 (20060906): "OntoQL-syntaxique.g" -> "OntoQLBaseParser.java"$


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

import fr.ensma.lisi.ontoql.engine.util.ASTUtil;


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
 * OntoQL Grammar
 * <br>
 * This grammar parses the OntoQL language for Ontology Based DataBase (OBDB).
 *
 * @author Stephane JEAN 
 */
public class OntoQLBaseParser extends antlr.LLkParser       implements OntoQLTokenTypes
 {
    
	/**
	 * Returns the negated equivalent of the expression.
	 * @param x The expression to negate.
	 */
	public AST negateNode(AST x) {
		// Just create a 'not' parent for the default behavior.
		return ASTUtil.createParent(astFactory, NOT, "not", x);
	}

	/**
	 * Returns the 'cleaned up' version of a comparison operator sub-tree.
	 * @param x The comparison operator to clean up.
	 */
	public AST processEqualityExpression(AST x) throws RecognitionException {
		return x;
	}
	

protected OntoQLBaseParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public OntoQLBaseParser(TokenBuffer tokenBuf) {
  this(tokenBuf,3);
}

protected OntoQLBaseParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

public OntoQLBaseParser(TokenStream lexer) {
  this(lexer,3);
}

public OntoQLBaseParser(ParserSharedInputState state) {
  super(state,3);
  tokenNames = _tokenNames;
  buildTokenTypeASTClassMap();
  astFactory = new ASTFactory(getTokenTypeToASTClassMap());
}

	public final void statement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST statement_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case ALTER:
			case CREATE:
			case DROP:
			case SET:
			{
				ddlStatement();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case UPDATE:
			{
				updateStatement();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case DELETE:
			{
				deleteStatement();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case SELECT:
			case OPEN:
			{
				queryExpression();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case INSERT:
			{
				insertStatement();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			statement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = statement_AST;
	}
	
	public final void ddlStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ddlStatement_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case CREATE:
			{
				createStatement();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case ALTER:
			{
				alterStatement();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case DROP:
			{
				dropStatement();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case SET:
			{
				parameterStatement();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			ddlStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = ddlStatement_AST;
	}
	
	public final void updateStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST updateStatement_AST = null;
		
		try {      // for error handling
			AST tmp1_AST = null;
			tmp1_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp1_AST);
			match(UPDATE);
			targetClass();
			astFactory.addASTChild(currentAST, returnAST);
			setClause();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case WHERE:
			{
				whereClause();
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
			updateStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = updateStatement_AST;
	}
	
	public final void deleteStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST deleteStatement_AST = null;
		
		try {      // for error handling
			AST tmp2_AST = null;
			tmp2_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp2_AST);
			match(DELETE);
			match(FROM);
			targetClass();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case WHERE:
			{
				whereClause();
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
			deleteStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = deleteStatement_AST;
	}
	
	public final void queryExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST queryExpression_AST = null;
		
		try {      // for error handling
			queryTerm();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop107:
			do {
				if ((LA(1)==EXCEPT||LA(1)==UNION)) {
					{
					switch ( LA(1)) {
					case UNION:
					{
						AST tmp4_AST = null;
						tmp4_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp4_AST);
						match(UNION);
						break;
					}
					case EXCEPT:
					{
						AST tmp5_AST = null;
						tmp5_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp5_AST);
						match(EXCEPT);
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
					case ALL:
					{
						AST tmp6_AST = null;
						tmp6_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp6_AST);
						match(ALL);
						break;
					}
					case DISTINCT:
					{
						AST tmp7_AST = null;
						tmp7_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp7_AST);
						match(DISTINCT);
						break;
					}
					case SELECT:
					case OPEN:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					queryTerm();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop107;
				}
				
			} while (true);
			}
			queryExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		returnAST = queryExpression_AST;
	}
	
	public final void insertStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST insertStatement_AST = null;
		
		try {      // for error handling
			AST tmp8_AST = null;
			tmp8_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp8_AST);
			match(INSERT);
			intoClause();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case SELECT:
			{
				selectStatement();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case VALUES:
			{
				valueClause();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			insertStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = insertStatement_AST;
	}
	
	public final void createStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST createStatement_AST = null;
		
		try {      // for error handling
			AST tmp9_AST = null;
			tmp9_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp9_AST);
			match(CREATE);
			{
			switch ( LA(1)) {
			case EXTENT:
			{
				ddlExtent();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case VIEW:
			{
				viewDefinition();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case ONTOLOGY_MODEL_ID:
			{
				ontologyDefinition();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case ENTITY:
			{
				entityDefinition();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			createStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = createStatement_AST;
	}
	
	public final void alterStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST alterStatement_AST = null;
		
		try {      // for error handling
			AST tmp10_AST = null;
			tmp10_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp10_AST);
			match(ALTER);
			{
			switch ( LA(1)) {
			case ENTITY:
			{
				alterEntity();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case ONTOLOGY_MODEL_ID:
			{
				alterClass();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EXTENT:
			{
				alterExtent();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			alterStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = alterStatement_AST;
	}
	
	public final void dropStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dropStatement_AST = null;
		
		try {      // for error handling
			AST tmp11_AST = null;
			tmp11_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp11_AST);
			match(DROP);
			dropEntity();
			astFactory.addASTChild(currentAST, returnAST);
			dropStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = dropStatement_AST;
	}
	
	public final void parameterStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST parameterStatement_AST = null;
		
		try {      // for error handling
			AST tmp12_AST = null;
			tmp12_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp12_AST);
			match(SET);
			{
			switch ( LA(1)) {
			case NAMESPACE:
			{
				namepaceSpecification();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case LANGUAGE:
			{
				languageSpecification();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			parameterStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = parameterStatement_AST;
	}
	
	public final void ddlExtent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ddlExtent_AST = null;
		
		try {      // for error handling
			AST tmp13_AST = null;
			tmp13_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp13_AST);
			match(EXTENT);
			match(OF);
			identifier();
			astFactory.addASTChild(currentAST, returnAST);
			match(OPEN);
			propIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop21:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					propIdentifier();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop21;
				}
				
			} while (true);
			}
			match(CLOSE);
			ddlExtent_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = ddlExtent_AST;
	}
	
	public final void viewDefinition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST viewDefinition_AST = null;
		
		try {      // for error handling
			AST tmp18_AST = null;
			tmp18_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp18_AST);
			match(VIEW);
			{
			switch ( LA(1)) {
			case ONTOLOGY_MODEL_ID:
			case NAME_ID:
			case INTERNAL_ID:
			case EXTERNAL_ID:
			{
				identifier();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case AS:
			case OF:
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
			case OF:
			{
				typedClause();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case AS:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(AS);
			match(OPEN);
			selectStatement();
			astFactory.addASTChild(currentAST, returnAST);
			match(CLOSE);
			viewDefinition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = viewDefinition_AST;
	}
	
	public final void ontologyDefinition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ontologyDefinition_AST = null;
		
		try {      // for error handling
			ontologyDefinitionHead();
			astFactory.addASTChild(currentAST, returnAST);
			ontologyDefinitionBody();
			astFactory.addASTChild(currentAST, returnAST);
			ontologyDefinition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = ontologyDefinition_AST;
	}
	
	public final void entityDefinition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST entityDefinition_AST = null;
		
		try {      // for error handling
			AST tmp22_AST = null;
			tmp22_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp22_AST);
			match(ENTITY);
			AST tmp23_AST = null;
			tmp23_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp23_AST);
			match(ONTOLOGY_MODEL_ID);
			{
			switch ( LA(1)) {
			case UNDER:
			{
				subEntityClause();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case OPEN:
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
			case OPEN:
			{
				entityElementList();
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
			entityDefinition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = entityDefinition_AST;
	}
	
	public final void identifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST identifier_AST = null;
		Token  n = null;
		AST n_AST = null;
		
		try {      // for error handling
			{
			if ((LA(1)==NAME_ID) && (LA(2)==COLON)) {
				n = LT(1);
				n_AST = astFactory.create(n);
				astFactory.addASTChild(currentAST, n_AST);
				match(NAME_ID);
				n_AST.setType(NAMESPACE_ALIAS);
				match(COLON);
			}
			else if ((_tokenSet_2.member(LA(1))) && (_tokenSet_3.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			{
			switch ( LA(1)) {
			case NAME_ID:
			{
				AST tmp25_AST = null;
				tmp25_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp25_AST);
				match(NAME_ID);
				break;
			}
			case INTERNAL_ID:
			{
				AST tmp26_AST = null;
				tmp26_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp26_AST);
				match(INTERNAL_ID);
				break;
			}
			case EXTERNAL_ID:
			{
				AST tmp27_AST = null;
				tmp27_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp27_AST);
				match(EXTERNAL_ID);
				break;
			}
			case ONTOLOGY_MODEL_ID:
			{
				AST tmp28_AST = null;
				tmp28_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp28_AST);
				match(ONTOLOGY_MODEL_ID);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			identifier_AST = (AST)currentAST.root;
			
					identifier_AST.setType(IDENT);
				
			identifier_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		returnAST = identifier_AST;
	}
	
	public final void typedClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST typedClause_AST = null;
		
		try {      // for error handling
			AST tmp29_AST = null;
			tmp29_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp29_AST);
			match(OF);
			identifier();
			astFactory.addASTChild(currentAST, returnAST);
			typedClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
		returnAST = typedClause_AST;
	}
	
	public final void selectStatement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectStatement_AST = null;
		
		try {      // for error handling
			queryRule();
			astFactory.addASTChild(currentAST, returnAST);
			selectStatement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
		returnAST = selectStatement_AST;
	}
	
	public final void subEntityClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST subEntityClause_AST = null;
		
		try {      // for error handling
			AST tmp30_AST = null;
			tmp30_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp30_AST);
			match(UNDER);
			AST tmp31_AST = null;
			tmp31_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp31_AST);
			match(ONTOLOGY_MODEL_ID);
			subEntityClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
		returnAST = subEntityClause_AST;
	}
	
	public final void entityElementList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST entityElementList_AST = null;
		AST a_AST = null;
		
		try {      // for error handling
			match(OPEN);
			attributeDefinition();
			a_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop17:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					attributeDefinition();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop17;
				}
				
			} while (true);
			}
			match(CLOSE);
			entityElementList_AST = (AST)currentAST.root;
			
					entityElementList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(ATTRIBUTES,"ATTRIBUTES")).add(a_AST));
				
			currentAST.root = entityElementList_AST;
			currentAST.child = entityElementList_AST!=null &&entityElementList_AST.getFirstChild()!=null ?
				entityElementList_AST.getFirstChild() : entityElementList_AST;
			currentAST.advanceChildToEnd();
			entityElementList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = entityElementList_AST;
	}
	
	public final void attributeDefinition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST attributeDefinition_AST = null;
		Token  a = null;
		AST a_AST = null;
		AST d_AST = null;
		
		try {      // for error handling
			a = LT(1);
			a_AST = astFactory.create(a);
			match(ONTOLOGY_MODEL_ID);
			datatype();
			d_AST = (AST)returnAST;
			attributeDefinition_AST = (AST)currentAST.root;
			
					attributeDefinition_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(ATTRIBUTE_DEF,"ATTRIBUTE_DEF")).add(a_AST).add(d_AST));
				
			currentAST.root = attributeDefinition_AST;
			currentAST.child = attributeDefinition_AST!=null &&attributeDefinition_AST.getFirstChild()!=null ?
				attributeDefinition_AST.getFirstChild() : attributeDefinition_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		returnAST = attributeDefinition_AST;
	}
	
	public final void datatype() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST datatype_AST = null;
		Token  a = null;
		AST a_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case BOOLEAN:
			case REAL:
			case INT:
			case MULTILINGUAL:
			case ENUM:
			case STRING:
			case URI:
			case COUNTTYPE:
			{
				predefinedType();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case REF:
			{
				referenceType();
				astFactory.addASTChild(currentAST, returnAST);
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
			case ARRAY:
			{
				a = LT(1);
				a_AST = astFactory.create(a);
				astFactory.makeASTRoot(currentAST, a_AST);
				match(ARRAY);
				a_AST.setType(ARRAY_DEF);
				break;
			}
			case EOF:
			case DESCRIPTOR:
			case WHERE:
			case CLOSE:
			case COMMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			datatype_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		returnAST = datatype_AST;
	}
	
	public final void propIdentifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST propIdentifier_AST = null;
		AST i_AST = null;
		Token  lb = null;
		AST lb_AST = null;
		
		try {      // for error handling
			identifier();
			i_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case OPEN_BRACKET:
			{
				lb = LT(1);
				lb_AST = astFactory.create(lb);
				astFactory.makeASTRoot(currentAST, lb_AST);
				match(OPEN_BRACKET);
				{
				switch ( LA(1)) {
				case FR:
				{
					AST tmp35_AST = null;
					tmp35_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp35_AST);
					match(FR);
					lb_AST.setType(LANGUE_OP);
					break;
				}
				case EN:
				{
					AST tmp36_AST = null;
					tmp36_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp36_AST);
					match(EN);
					lb_AST.setType(LANGUE_OP);
					break;
				}
				case NUM_INT:
				{
					AST tmp37_AST = null;
					tmp37_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp37_AST);
					match(NUM_INT);
					lb_AST.setType(INDEX_OP);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(CLOSE_BRACKET);
				break;
			}
			case EOF:
			case AND:
			case AS:
			case ASCENDING:
			case BETWEEN:
			case BOOLEAN:
			case REAL:
			case DESCENDING:
			case ELSE:
			case END:
			case EXCEPT:
			case FROM:
			case GROUP:
			case HAVING:
			case IN:
			case INT:
			case INTERSECT:
			case IS:
			case LIKE:
			case MULTILINGUAL:
			case ENUM:
			case NOT:
			case ON:
			case OR:
			case ORDER:
			case PREFERRING:
			case REF:
			case STRING:
			case THEN:
			case UNION:
			case USING:
			case WHEN:
			case WHERE:
			case URI:
			case COUNTTYPE:
			case LIMIT:
			case MAP:
			case DOT:
			case OPEN:
			case CLOSE:
			case COMMA:
			case EQ:
			case STAR:
			case LITERAL_ascending:
			case LITERAL_descending:
			case NE:
			case SQL_NE:
			case LT:
			case GT:
			case LE:
			case GE:
			case CONCAT:
			case PLUS:
			case MINUS:
			case DIV:
			case CLOSE_BRACKET:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			propIdentifier_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		returnAST = propIdentifier_AST;
	}
	
	public final void ontologyDefinitionHead() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ontologyDefinitionHead_AST = null;
		
		try {      // for error handling
			AST tmp39_AST = null;
			tmp39_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp39_AST);
			match(ONTOLOGY_MODEL_ID);
			identifier();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case UNDER:
			case CONTEXT:
			{
				optionalHeadClause();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case CASEOF:
			case OPEN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			ontologyDefinitionHead_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_10);
		}
		returnAST = ontologyDefinitionHead_AST;
	}
	
	public final void ontologyDefinitionBody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ontologyDefinitionBody_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case EOF:
			case OPEN:
			{
				{
				switch ( LA(1)) {
				case OPEN:
				{
					classDefinitionBody();
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
				ontologyDefinitionBody_AST = (AST)currentAST.root;
				break;
			}
			case CASEOF:
			{
				aPosterioriCaseOfDefinitionBody();
				astFactory.addASTChild(currentAST, returnAST);
				ontologyDefinitionBody_AST = (AST)currentAST.root;
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
			recover(ex,_tokenSet_0);
		}
		returnAST = ontologyDefinitionBody_AST;
	}
	
	public final void optionalHeadClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST optionalHeadClause_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case UNDER:
			{
				AST tmp40_AST = null;
				tmp40_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp40_AST);
				match(UNDER);
				break;
			}
			case CONTEXT:
			{
				AST tmp41_AST = null;
				tmp41_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp41_AST);
				match(CONTEXT);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			identifier();
			astFactory.addASTChild(currentAST, returnAST);
			optionalHeadClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_10);
		}
		returnAST = optionalHeadClause_AST;
	}
	
	public final void classDefinitionBody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST classDefinitionBody_AST = null;
		
		try {      // for error handling
			match(OPEN);
			{
			switch ( LA(1)) {
			case DESCRIPTOR:
			{
				descriptorClause();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case PROPERTIES:
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
			{
			switch ( LA(1)) {
			case PROPERTIES:
			{
				propertiesClause();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
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
			match(CLOSE);
			classDefinitionBody_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = classDefinitionBody_AST;
	}
	
	public final void aPosterioriCaseOfDefinitionBody() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST aPosterioriCaseOfDefinitionBody_AST = null;
		
		try {      // for error handling
			caseOfClause();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case OPEN:
			{
				mappedPropertiesClause();
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
			aPosterioriCaseOfDefinitionBody_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = aPosterioriCaseOfDefinitionBody_AST;
	}
	
	public final void descriptorClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST descriptorClause_AST = null;
		
		try {      // for error handling
			AST tmp44_AST = null;
			tmp44_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp44_AST);
			match(DESCRIPTOR);
			match(OPEN);
			assignment();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop42:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					assignment();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop42;
				}
				
			} while (true);
			}
			match(CLOSE);
			descriptorClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		returnAST = descriptorClause_AST;
	}
	
	public final void propertiesClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST propertiesClause_AST = null;
		
		try {      // for error handling
			AST tmp48_AST = null;
			tmp48_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp48_AST);
			match(PROPERTIES);
			match(OPEN);
			propertyDefinition();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop45:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					propertyDefinition();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop45;
				}
				
			} while (true);
			}
			match(CLOSE);
			propertiesClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = propertiesClause_AST;
	}
	
	public final void caseOfClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST caseOfClause_AST = null;
		
		try {      // for error handling
			AST tmp52_AST = null;
			tmp52_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp52_AST);
			match(CASEOF);
			identifier();
			astFactory.addASTChild(currentAST, returnAST);
			caseOfClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
		returnAST = caseOfClause_AST;
	}
	
	public final void mappedPropertiesClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST mappedPropertiesClause_AST = null;
		AST l_AST = null;
		
		try {      // for error handling
			match(OPEN);
			listOfMapProperty();
			l_AST = (AST)returnAST;
			match(CLOSE);
			mappedPropertiesClause_AST = (AST)currentAST.root;
			
					mappedPropertiesClause_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(MAPPED_PROPERTIES,"MAPPED_PROPERTIES")).add(l_AST));
				
			currentAST.root = mappedPropertiesClause_AST;
			currentAST.child = mappedPropertiesClause_AST!=null &&mappedPropertiesClause_AST.getFirstChild()!=null ?
				mappedPropertiesClause_AST.getFirstChild() : mappedPropertiesClause_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = mappedPropertiesClause_AST;
	}
	
	public final void listOfMapProperty() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST listOfMapProperty_AST = null;
		
		try {      // for error handling
			mapProperty();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop36:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					mapProperty();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop36;
				}
				
			} while (true);
			}
			listOfMapProperty_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = listOfMapProperty_AST;
	}
	
	public final void mapProperty() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST mapProperty_AST = null;
		
		try {      // for error handling
			propIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp56_AST = null;
			tmp56_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp56_AST);
			match(MAP);
			propIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			mapProperty_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_13);
		}
		returnAST = mapProperty_AST;
	}
	
	public final void assignment() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST assignment_AST = null;
		
		try {      // for error handling
			propIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			AST tmp57_AST = null;
			tmp57_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp57_AST);
			match(EQ);
			{
			switch ( LA(1)) {
			case ALL:
			case ANY:
			case ARRAY:
			case AVG:
			case CASE:
			case COUNT:
			case EXISTS:
			case FALSE:
			case MAX:
			case MIN:
			case NULL:
			case SUM:
			case TRUE:
			case TYPEOF:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case OPEN:
			case ONTOLOGY_MODEL_ID:
			case NUM_INT:
			case NAME_ID:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case INTERNAL_ID:
			case EXTERNAL_ID:
			{
				concatenation();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case BOOLEAN:
			case REAL:
			case INT:
			case MULTILINGUAL:
			case ENUM:
			case REF:
			case STRING:
			case URI:
			case COUNTTYPE:
			{
				datatype();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			assignment_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_14);
		}
		returnAST = assignment_AST;
	}
	
	public final void propertyDefinition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST propertyDefinition_AST = null;
		AST p_AST = null;
		AST d_AST = null;
		AST desc_AST = null;
		
		try {      // for error handling
			propIdentifier();
			p_AST = (AST)returnAST;
			datatype();
			d_AST = (AST)returnAST;
			{
			switch ( LA(1)) {
			case DESCRIPTOR:
			{
				descriptorClause();
				desc_AST = (AST)returnAST;
				break;
			}
			case EOF:
			case CLOSE:
			case COMMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			propertyDefinition_AST = (AST)currentAST.root;
			
					propertyDefinition_AST = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(PROPERTY_DEF,"PROPERTY_DEF")).add(p_AST).add(d_AST).add(desc_AST));
				
			currentAST.root = propertyDefinition_AST;
			currentAST.child = propertyDefinition_AST!=null &&propertyDefinition_AST.getFirstChild()!=null ?
				propertyDefinition_AST.getFirstChild() : propertyDefinition_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		returnAST = propertyDefinition_AST;
	}
	
	public final void predefinedType() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST predefinedType_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case MULTILINGUAL:
			case STRING:
			{
				{
				switch ( LA(1)) {
				case MULTILINGUAL:
				{
					AST tmp58_AST = null;
					tmp58_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp58_AST);
					match(MULTILINGUAL);
					break;
				}
				case STRING:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				AST tmp59_AST = null;
				tmp59_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp59_AST);
				match(STRING);
				break;
			}
			case URI:
			{
				AST tmp60_AST = null;
				tmp60_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp60_AST);
				match(URI);
				break;
			}
			case COUNTTYPE:
			{
				AST tmp61_AST = null;
				tmp61_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp61_AST);
				match(COUNTTYPE);
				break;
			}
			case INT:
			{
				AST tmp62_AST = null;
				tmp62_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp62_AST);
				match(INT);
				break;
			}
			case REAL:
			{
				AST tmp63_AST = null;
				tmp63_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp63_AST);
				match(REAL);
				break;
			}
			case ENUM:
			{
				AST tmp64_AST = null;
				tmp64_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp64_AST);
				match(ENUM);
				inList();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case BOOLEAN:
			{
				AST tmp65_AST = null;
				tmp65_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp65_AST);
				match(BOOLEAN);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			predefinedType_AST = (AST)currentAST.root;
			predefinedType_AST.setType(PREDEFINED_TYPE);
			predefinedType_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = predefinedType_AST;
	}
	
	public final void referenceType() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST referenceType_AST = null;
		
		try {      // for error handling
			{
			AST tmp66_AST = null;
			tmp66_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp66_AST);
			match(REF);
			match(OPEN);
			identifier();
			astFactory.addASTChild(currentAST, returnAST);
			match(CLOSE);
			}
			referenceType_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		returnAST = referenceType_AST;
	}
	
	public final void inList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST inList_AST = null;
		AST x_AST = null;
		
		try {      // for error handling
			compoundExpr();
			x_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			inList_AST = (AST)currentAST.root;
			inList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(IN_LIST,"inList")).add(inList_AST));
			currentAST.root = inList_AST;
			currentAST.child = inList_AST!=null &&inList_AST.getFirstChild()!=null ?
				inList_AST.getFirstChild() : inList_AST;
			currentAST.advanceChildToEnd();
			inList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_16);
		}
		returnAST = inList_AST;
	}
	
	public final void alterEntity() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST alterEntity_AST = null;
		
		try {      // for error handling
			AST tmp69_AST = null;
			tmp69_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp69_AST);
			match(ENTITY);
			AST tmp70_AST = null;
			tmp70_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp70_AST);
			match(ONTOLOGY_MODEL_ID);
			alterEntityAction();
			astFactory.addASTChild(currentAST, returnAST);
			alterEntity_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = alterEntity_AST;
	}
	
	public final void alterClass() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST alterClass_AST = null;
		
		try {      // for error handling
			AST tmp71_AST = null;
			tmp71_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp71_AST);
			match(ONTOLOGY_MODEL_ID);
			classIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			alterClassAction();
			astFactory.addASTChild(currentAST, returnAST);
			alterClass_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = alterClass_AST;
	}
	
	public final void alterExtent() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST alterExtent_AST = null;
		
		try {      // for error handling
			AST tmp72_AST = null;
			tmp72_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp72_AST);
			match(EXTENT);
			match(OF);
			classIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			alterExtentAction();
			astFactory.addASTChild(currentAST, returnAST);
			alterExtent_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = alterExtent_AST;
	}
	
	public final void alterEntityAction() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST alterEntityAction_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ADD:
			{
				addAttributeDefinition();
				astFactory.addASTChild(currentAST, returnAST);
				alterEntityAction_AST = (AST)currentAST.root;
				break;
			}
			case DROP:
			{
				dropAttribute();
				astFactory.addASTChild(currentAST, returnAST);
				alterEntityAction_AST = (AST)currentAST.root;
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
			recover(ex,_tokenSet_0);
		}
		returnAST = alterEntityAction_AST;
	}
	
	public final void addAttributeDefinition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST addAttributeDefinition_AST = null;
		
		try {      // for error handling
			AST tmp74_AST = null;
			tmp74_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp74_AST);
			match(ADD);
			{
			switch ( LA(1)) {
			case ATTRIBUTE:
			{
				match(ATTRIBUTE);
				break;
			}
			case ONTOLOGY_MODEL_ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			attributeDefinition();
			astFactory.addASTChild(currentAST, returnAST);
			addAttributeDefinition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = addAttributeDefinition_AST;
	}
	
	public final void dropAttribute() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dropAttribute_AST = null;
		
		try {      // for error handling
			AST tmp76_AST = null;
			tmp76_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp76_AST);
			match(DROP);
			{
			switch ( LA(1)) {
			case ATTRIBUTE:
			{
				match(ATTRIBUTE);
				break;
			}
			case ONTOLOGY_MODEL_ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			AST tmp78_AST = null;
			tmp78_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp78_AST);
			match(ONTOLOGY_MODEL_ID);
			dropAttribute_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = dropAttribute_AST;
	}
	
	public final void classIdentifier() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST classIdentifier_AST = null;
		
		try {      // for error handling
			identifier();
			astFactory.addASTChild(currentAST, returnAST);
			classIdentifier_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_17);
		}
		returnAST = classIdentifier_AST;
	}
	
	public final void alterExtentAction() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST alterExtentAction_AST = null;
		
		try {      // for error handling
			AST tmp79_AST = null;
			tmp79_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp79_AST);
			match(ADD);
			propIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			alterExtentAction_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = alterExtentAction_AST;
	}
	
	public final void alterClassAction() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST alterClassAction_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ADD:
			{
				addPropertyDefinition();
				astFactory.addASTChild(currentAST, returnAST);
				alterClassAction_AST = (AST)currentAST.root;
				break;
			}
			case DROP:
			{
				dropPropertyDefinition();
				astFactory.addASTChild(currentAST, returnAST);
				alterClassAction_AST = (AST)currentAST.root;
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
			recover(ex,_tokenSet_0);
		}
		returnAST = alterClassAction_AST;
	}
	
	public final void addPropertyDefinition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST addPropertyDefinition_AST = null;
		
		try {      // for error handling
			AST tmp80_AST = null;
			tmp80_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp80_AST);
			match(ADD);
			{
			switch ( LA(1)) {
			case PROPERTY:
			{
				match(PROPERTY);
				break;
			}
			case ONTOLOGY_MODEL_ID:
			case NAME_ID:
			case INTERNAL_ID:
			case EXTERNAL_ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			propertyDefinition();
			astFactory.addASTChild(currentAST, returnAST);
			addPropertyDefinition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = addPropertyDefinition_AST;
	}
	
	public final void dropPropertyDefinition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dropPropertyDefinition_AST = null;
		
		try {      // for error handling
			AST tmp82_AST = null;
			tmp82_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp82_AST);
			match(DROP);
			{
			switch ( LA(1)) {
			case PROPERTY:
			{
				match(PROPERTY);
				break;
			}
			case ONTOLOGY_MODEL_ID:
			case NAME_ID:
			case INTERNAL_ID:
			case EXTERNAL_ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			propIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			dropPropertyDefinition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = dropPropertyDefinition_AST;
	}
	
	public final void dropEntity() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST dropEntity_AST = null;
		
		try {      // for error handling
			AST tmp84_AST = null;
			tmp84_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp84_AST);
			match(ENTITY);
			AST tmp85_AST = null;
			tmp85_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp85_AST);
			match(ONTOLOGY_MODEL_ID);
			dropEntity_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = dropEntity_AST;
	}
	
	public final void namepaceSpecification() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST namepaceSpecification_AST = null;
		
		try {      // for error handling
			AST tmp86_AST = null;
			tmp86_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp86_AST);
			match(NAMESPACE);
			{
			switch ( LA(1)) {
			case NAME_ID:
			case QUOTED_STRING:
			{
				namespaceAlias();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case NONE:
			{
				AST tmp87_AST = null;
				tmp87_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp87_AST);
				match(NONE);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			namepaceSpecification_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = namepaceSpecification_AST;
	}
	
	public final void languageSpecification() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST languageSpecification_AST = null;
		
		try {      // for error handling
			AST tmp88_AST = null;
			tmp88_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp88_AST);
			match(LANGUAGE);
			{
			switch ( LA(1)) {
			case EN:
			case FR:
			{
				languageId();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case NONE:
			{
				AST tmp89_AST = null;
				tmp89_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp89_AST);
				match(NONE);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			languageSpecification_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = languageSpecification_AST;
	}
	
	public final void namespaceAlias() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST namespaceAlias_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case NAME_ID:
			{
				AST tmp90_AST = null;
				tmp90_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp90_AST);
				match(NAME_ID);
				match(EQ);
				break;
			}
			case QUOTED_STRING:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			AST tmp92_AST = null;
			tmp92_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp92_AST);
			match(QUOTED_STRING);
			namespaceAlias_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		returnAST = namespaceAlias_AST;
	}
	
	public final void languageId() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST languageId_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case FR:
			{
				AST tmp93_AST = null;
				tmp93_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp93_AST);
				match(FR);
				languageId_AST = (AST)currentAST.root;
				break;
			}
			case EN:
			{
				AST tmp94_AST = null;
				tmp94_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp94_AST);
				match(EN);
				languageId_AST = (AST)currentAST.root;
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
			recover(ex,_tokenSet_0);
		}
		returnAST = languageId_AST;
	}
	
	public final void targetClass() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST targetClass_AST = null;
		AST c_AST = null;
		Token  o = null;
		AST o_AST = null;
		AST conly_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ONTOLOGY_MODEL_ID:
			case NAME_ID:
			case INTERNAL_ID:
			case EXTERNAL_ID:
			{
				classIdentifier();
				c_AST = (AST)returnAST;
				targetClass_AST = (AST)currentAST.root;
				
						AST range = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(RANGE,"RANGE")).add(c_AST));
						targetClass_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FROM,"FROM")).add(range));
					
				currentAST.root = targetClass_AST;
				currentAST.child = targetClass_AST!=null &&targetClass_AST.getFirstChild()!=null ?
					targetClass_AST.getFirstChild() : targetClass_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case ONLY:
			{
				o = LT(1);
				o_AST = astFactory.create(o);
				match(ONLY);
				match(OPEN);
				classIdentifier();
				conly_AST = (AST)returnAST;
				match(CLOSE);
				targetClass_AST = (AST)currentAST.root;
				
						AST range = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(RANGE,"RANGE")).add(conly_AST).add(o_AST));
						targetClass_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(FROM,"FROM")).add(range));
					
				currentAST.root = targetClass_AST;
				currentAST.child = targetClass_AST!=null &&targetClass_AST.getFirstChild()!=null ?
					targetClass_AST.getFirstChild() : targetClass_AST;
				currentAST.advanceChildToEnd();
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
			recover(ex,_tokenSet_19);
		}
		returnAST = targetClass_AST;
	}
	
	public final void setClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST setClause_AST = null;
		
		try {      // for error handling
			{
			AST tmp97_AST = null;
			tmp97_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp97_AST);
			match(SET);
			assignment();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop86:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					assignment();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop86;
				}
				
			} while (true);
			}
			}
			setClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_20);
		}
		returnAST = setClause_AST;
	}
	
	public final void whereClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST whereClause_AST = null;
		
		try {      // for error handling
			AST tmp99_AST = null;
			tmp99_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp99_AST);
			match(WHERE);
			logicalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			whereClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_21);
		}
		returnAST = whereClause_AST;
	}
	
	public final void concatenation() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST concatenation_AST = null;
		Token  c = null;
		AST c_AST = null;
		
		try {      // for error handling
			additiveExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case CONCAT:
			{
				c = LT(1);
				c_AST = astFactory.create(c);
				astFactory.makeASTRoot(currentAST, c_AST);
				match(CONCAT);
				c_AST.setType(EXPR_LIST); c_AST.setText("concatList");
				additiveExpression();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop209:
				do {
					if ((LA(1)==CONCAT)) {
						match(CONCAT);
						additiveExpression();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop209;
					}
					
				} while (true);
				}
				concatenation_AST = (AST)currentAST.root;
				concatenation_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(METHOD_CALL,"||")).add((AST)astFactory.make( (new ASTArray(1)).add(astFactory.create(IDENT,"concat")))).add(c_AST));
				currentAST.root = concatenation_AST;
				currentAST.child = concatenation_AST!=null &&concatenation_AST.getFirstChild()!=null ?
					concatenation_AST.getFirstChild() : concatenation_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case EOF:
			case AND:
			case AS:
			case ASCENDING:
			case BETWEEN:
			case DESCENDING:
			case EXCEPT:
			case FROM:
			case GROUP:
			case HAVING:
			case IN:
			case INTERSECT:
			case IS:
			case LIKE:
			case NOT:
			case ON:
			case OR:
			case ORDER:
			case PREFERRING:
			case THEN:
			case UNION:
			case USING:
			case WHERE:
			case LIMIT:
			case CLOSE:
			case COMMA:
			case EQ:
			case LITERAL_ascending:
			case LITERAL_descending:
			case NE:
			case SQL_NE:
			case LT:
			case GT:
			case LE:
			case GE:
			case CLOSE_BRACKET:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			concatenation_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_22);
		}
		returnAST = concatenation_AST;
	}
	
	public final void intoClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST intoClause_AST = null;
		
		try {      // for error handling
			AST tmp101_AST = null;
			tmp101_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp101_AST);
			match(INTO);
			classIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case OPEN:
			{
				insertablePropertySpec();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case SELECT:
			case VALUES:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			intoClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_23);
		}
		returnAST = intoClause_AST;
	}
	
	public final void valueClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST valueClause_AST = null;
		
		try {      // for error handling
			AST tmp102_AST = null;
			tmp102_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp102_AST);
			match(VALUES);
			match(OPEN);
			concatenation();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop93:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					concatenation();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop93;
				}
				
			} while (true);
			}
			match(CLOSE);
			valueClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		returnAST = valueClause_AST;
	}
	
	public final void insertablePropertySpec() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST insertablePropertySpec_AST = null;
		
		try {      // for error handling
			match(OPEN);
			propIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop98:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					propIdentifier();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop98;
				}
				
			} while (true);
			}
			match(CLOSE);
			insertablePropertySpec_AST = (AST)currentAST.root;
			
					// Just need *something* to distinguish this on the hql-sql.g side
					insertablePropertySpec_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(RANGE,"property-spec")).add(insertablePropertySpec_AST));
				
			currentAST.root = insertablePropertySpec_AST;
			currentAST.child = insertablePropertySpec_AST!=null &&insertablePropertySpec_AST.getFirstChild()!=null ?
				insertablePropertySpec_AST.getFirstChild() : insertablePropertySpec_AST;
			currentAST.advanceChildToEnd();
			insertablePropertySpec_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_23);
		}
		returnAST = insertablePropertySpec_AST;
	}
	
	public final void queryTerm() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST queryTerm_AST = null;
		
		try {      // for error handling
			queryPrimary();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop111:
			do {
				if ((LA(1)==INTERSECT)) {
					AST tmp109_AST = null;
					tmp109_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp109_AST);
					match(INTERSECT);
					{
					switch ( LA(1)) {
					case ALL:
					{
						AST tmp110_AST = null;
						tmp110_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp110_AST);
						match(ALL);
						break;
					}
					case DISTINCT:
					{
						AST tmp111_AST = null;
						tmp111_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp111_AST);
						match(DISTINCT);
						break;
					}
					case SELECT:
					case OPEN:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					queryPrimary();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop111;
				}
				
			} while (true);
			}
			queryTerm_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_24);
		}
		returnAST = queryTerm_AST;
	}
	
	public final void queryPrimary() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST queryPrimary_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case SELECT:
			{
				selectStatement();
				astFactory.addASTChild(currentAST, returnAST);
				queryPrimary_AST = (AST)currentAST.root;
				break;
			}
			case OPEN:
			{
				match(OPEN);
				queryExpression();
				astFactory.addASTChild(currentAST, returnAST);
				match(CLOSE);
				queryPrimary_AST = (AST)currentAST.root;
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
			recover(ex,_tokenSet_5);
		}
		returnAST = queryPrimary_AST;
	}
	
	public final void queryRule() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST queryRule_AST = null;
		AST sf_AST = null;
		AST w_AST = null;
		AST g_AST = null;
		AST o_AST = null;
		AST l_AST = null;
		AST p_AST = null;
		AST n_AST = null;
		
		try {      // for error handling
			selectFrom();
			sf_AST = (AST)returnAST;
			{
			switch ( LA(1)) {
			case WHERE:
			{
				whereClause();
				w_AST = (AST)returnAST;
				break;
			}
			case EOF:
			case EXCEPT:
			case GROUP:
			case INTERSECT:
			case ORDER:
			case PREFERRING:
			case UNION:
			case USING:
			case LIMIT:
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
			{
			switch ( LA(1)) {
			case GROUP:
			{
				groupByClause();
				g_AST = (AST)returnAST;
				break;
			}
			case EOF:
			case EXCEPT:
			case INTERSECT:
			case ORDER:
			case PREFERRING:
			case UNION:
			case USING:
			case LIMIT:
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
			{
			switch ( LA(1)) {
			case ORDER:
			{
				orderByClause();
				o_AST = (AST)returnAST;
				break;
			}
			case EOF:
			case EXCEPT:
			case INTERSECT:
			case PREFERRING:
			case UNION:
			case USING:
			case LIMIT:
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
			{
			switch ( LA(1)) {
			case LIMIT:
			{
				limitClause();
				l_AST = (AST)returnAST;
				break;
			}
			case EOF:
			case EXCEPT:
			case INTERSECT:
			case PREFERRING:
			case UNION:
			case USING:
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
			{
			switch ( LA(1)) {
			case PREFERRING:
			{
				preferenceClause();
				p_AST = (AST)returnAST;
				break;
			}
			case EOF:
			case EXCEPT:
			case INTERSECT:
			case UNION:
			case USING:
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
			{
			switch ( LA(1)) {
			case USING:
			{
				namespaceClause();
				n_AST = (AST)returnAST;
				break;
			}
			case EOF:
			case EXCEPT:
			case INTERSECT:
			case UNION:
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
			queryRule_AST = (AST)currentAST.root;
			
					queryRule_AST = (AST)astFactory.make( (new ASTArray(8)).add(astFactory.create(QUERY,"query")).add(n_AST).add(sf_AST).add(w_AST).add(g_AST).add(o_AST).add(l_AST).add(p_AST));
				
			currentAST.root = queryRule_AST;
			currentAST.child = queryRule_AST!=null &&queryRule_AST.getFirstChild()!=null ?
				queryRule_AST.getFirstChild() : queryRule_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
		returnAST = queryRule_AST;
	}
	
	public final void selectFrom() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectFrom_AST = null;
		AST s_AST = null;
		AST f_AST = null;
		
		try {      // for error handling
			{
			selectClause();
			s_AST = (AST)returnAST;
			}
			{
			fromClause();
			f_AST = (AST)returnAST;
			}
			selectFrom_AST = (AST)currentAST.root;
			
					// Create an artificial token so the 'FROM' can be placed
					// before the SELECT in the tree to make tree processing
					// simpler.
					selectFrom_AST = (AST)astFactory.make( (new ASTArray(3)).add(astFactory.create(SELECT_FROM,"SELECT_FROM")).add(f_AST).add(s_AST));
				
			currentAST.root = selectFrom_AST;
			currentAST.child = selectFrom_AST!=null &&selectFrom_AST.getFirstChild()!=null ?
				selectFrom_AST.getFirstChild() : selectFrom_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_25);
		}
		returnAST = selectFrom_AST;
	}
	
	public final void groupByClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST groupByClause_AST = null;
		
		try {      // for error handling
			AST tmp114_AST = null;
			tmp114_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp114_AST);
			match(GROUP);
			match(LITERAL_by);
			valueExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop158:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					valueExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop158;
				}
				
			} while (true);
			}
			{
			switch ( LA(1)) {
			case HAVING:
			{
				havingClause();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case EXCEPT:
			case INTERSECT:
			case ORDER:
			case PREFERRING:
			case UNION:
			case USING:
			case LIMIT:
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
			groupByClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_26);
		}
		returnAST = groupByClause_AST;
	}
	
	public final void orderByClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orderByClause_AST = null;
		
		try {      // for error handling
			AST tmp117_AST = null;
			tmp117_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp117_AST);
			match(ORDER);
			match(LITERAL_by);
			orderElement();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop162:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					orderElement();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop162;
				}
				
			} while (true);
			}
			orderByClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_27);
		}
		returnAST = orderByClause_AST;
	}
	
	public final void limitClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST limitClause_AST = null;
		
		try {      // for error handling
			AST tmp120_AST = null;
			tmp120_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp120_AST);
			match(LIMIT);
			AST tmp121_AST = null;
			tmp121_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp121_AST);
			match(NUM_INT);
			{
			switch ( LA(1)) {
			case OFFSET:
			{
				AST tmp122_AST = null;
				tmp122_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp122_AST);
				match(OFFSET);
				AST tmp123_AST = null;
				tmp123_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp123_AST);
				match(NUM_INT);
				break;
			}
			case EOF:
			case EXCEPT:
			case INTERSECT:
			case PREFERRING:
			case UNION:
			case USING:
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
			limitClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_28);
		}
		returnAST = limitClause_AST;
	}
	
	public final void preferenceClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST preferenceClause_AST = null;
		
		try {      // for error handling
			AST tmp124_AST = null;
			tmp124_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp124_AST);
			match(PREFERRING);
			identifier();
			astFactory.addASTChild(currentAST, returnAST);
			preferenceClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_29);
		}
		returnAST = preferenceClause_AST;
	}
	
	public final void namespaceClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST namespaceClause_AST = null;
		
		try {      // for error handling
			match(USING);
			AST tmp126_AST = null;
			tmp126_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp126_AST);
			match(NAMESPACE);
			namespaceAlias();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop168:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					namespaceAlias();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop168;
				}
				
			} while (true);
			}
			namespaceClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
		returnAST = namespaceClause_AST;
	}
	
	public final void selectClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectClause_AST = null;
		
		try {      // for error handling
			AST tmp128_AST = null;
			tmp128_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp128_AST);
			match(SELECT);
			{
			switch ( LA(1)) {
			case DISTINCT:
			{
				AST tmp129_AST = null;
				tmp129_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp129_AST);
				match(DISTINCT);
				break;
			}
			case ALL:
			case ANY:
			case ARRAY:
			case AVG:
			case CASE:
			case COUNT:
			case EXISTS:
			case FALSE:
			case MAX:
			case MIN:
			case NOT:
			case NULL:
			case SUM:
			case TRUE:
			case TYPEOF:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case OPEN:
			case ONTOLOGY_MODEL_ID:
			case STAR:
			case NUM_INT:
			case NAME_ID:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case INTERNAL_ID:
			case EXTERNAL_ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			selectList();
			astFactory.addASTChild(currentAST, returnAST);
			selectClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_30);
		}
		returnAST = selectClause_AST;
	}
	
	public final void fromClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fromClause_AST = null;
		
		try {      // for error handling
			AST tmp130_AST = null;
			tmp130_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp130_AST);
			match(FROM);
			tableReference();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop133:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					tableReference();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop133;
				}
				
			} while (true);
			}
			fromClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_25);
		}
		returnAST = fromClause_AST;
	}
	
	public final void selectList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectList_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case STAR:
			{
				AST tmp132_AST = null;
				tmp132_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp132_AST);
				match(STAR);
				tmp132_AST.setType(ROW_STAR);
				selectList_AST = (AST)currentAST.root;
				break;
			}
			case ALL:
			case ANY:
			case ARRAY:
			case AVG:
			case CASE:
			case COUNT:
			case EXISTS:
			case FALSE:
			case MAX:
			case MIN:
			case NOT:
			case NULL:
			case SUM:
			case TRUE:
			case TYPEOF:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case OPEN:
			case ONTOLOGY_MODEL_ID:
			case NUM_INT:
			case NAME_ID:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case INTERNAL_ID:
			case EXTERNAL_ID:
			{
				selectSublist();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop128:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						selectSublist();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop128;
					}
					
				} while (true);
				}
				selectList_AST = (AST)currentAST.root;
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
			recover(ex,_tokenSet_30);
		}
		returnAST = selectList_AST;
	}
	
	public final void selectSublist() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST selectSublist_AST = null;
		
		try {      // for error handling
			valueExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case AS:
			{
				AST tmp134_AST = null;
				tmp134_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp134_AST);
				match(AS);
				identifier();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case FROM:
			case COMMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			selectSublist_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_31);
		}
		returnAST = selectSublist_AST;
	}
	
	public final void valueExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST valueExpression_AST = null;
		
		try {      // for error handling
			logicalOrExpression();
			astFactory.addASTChild(currentAST, returnAST);
			valueExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_32);
		}
		returnAST = valueExpression_AST;
	}
	
	public final void tableReference() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tableReference_AST = null;
		AST t_AST = null;
		AST j_AST = null;
		
		try {      // for error handling
			tablePrimary();
			t_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case CROSS:
			case FULL:
			case INNER:
			case JOIN:
			case LEFT:
			case NATURAL:
			case RIGHT:
			{
				joinedTable();
				j_AST = (AST)returnAST;
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case EXCEPT:
			case GROUP:
			case INTERSECT:
			case ON:
			case ORDER:
			case PREFERRING:
			case UNION:
			case USING:
			case WHERE:
			case LIMIT:
			case CLOSE:
			case COMMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			tableReference_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_33);
		}
		returnAST = tableReference_AST;
	}
	
	public final void tablePrimary() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST tablePrimary_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ONTOLOGY_MODEL_ID:
			case NAME_ID:
			case INTERNAL_ID:
			case EXTERNAL_ID:
			{
				fromClass();
				astFactory.addASTChild(currentAST, returnAST);
				tablePrimary_AST = (AST)currentAST.root;
				break;
			}
			case ONLY:
			{
				onlySpec();
				astFactory.addASTChild(currentAST, returnAST);
				tablePrimary_AST = (AST)currentAST.root;
				break;
			}
			case UNNEST:
			{
				collectionDerivedTable();
				astFactory.addASTChild(currentAST, returnAST);
				tablePrimary_AST = (AST)currentAST.root;
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
			recover(ex,_tokenSet_34);
		}
		returnAST = tablePrimary_AST;
	}
	
	public final void joinedTable() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST joinedTable_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case CROSS:
			{
				crossJoin();
				astFactory.addASTChild(currentAST, returnAST);
				joinedTable_AST = (AST)currentAST.root;
				break;
			}
			case FULL:
			case INNER:
			case JOIN:
			case LEFT:
			case RIGHT:
			{
				qualifiedJoin();
				astFactory.addASTChild(currentAST, returnAST);
				joinedTable_AST = (AST)currentAST.root;
				break;
			}
			case NATURAL:
			{
				naturalJoin();
				astFactory.addASTChild(currentAST, returnAST);
				joinedTable_AST = (AST)currentAST.root;
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
			recover(ex,_tokenSet_33);
		}
		returnAST = joinedTable_AST;
	}
	
	public final void fromClass() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST fromClass_AST = null;
		AST c_AST = null;
		AST a_AST = null;
		
		try {      // for error handling
			identifier();
			c_AST = (AST)returnAST;
			{
			switch ( LA(1)) {
			case AS:
			case ONTOLOGY_MODEL_ID:
			case NAME_ID:
			case INTERNAL_ID:
			case EXTERNAL_ID:
			{
				asAlias();
				a_AST = (AST)returnAST;
				break;
			}
			case EOF:
			case CROSS:
			case EXCEPT:
			case FULL:
			case GROUP:
			case INNER:
			case INTERSECT:
			case JOIN:
			case LEFT:
			case NATURAL:
			case ON:
			case ORDER:
			case PREFERRING:
			case RIGHT:
			case UNION:
			case USING:
			case WHERE:
			case LIMIT:
			case CLOSE:
			case COMMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			fromClass_AST = (AST)currentAST.root;
			fromClass_AST = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(RANGE,"RANGE")).add(c_AST).add(null).add(a_AST));
			currentAST.root = fromClass_AST;
			currentAST.child = fromClass_AST!=null &&fromClass_AST.getFirstChild()!=null ?
				fromClass_AST.getFirstChild() : fromClass_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_34);
		}
		returnAST = fromClass_AST;
	}
	
	public final void onlySpec() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST onlySpec_AST = null;
		Token  o = null;
		AST o_AST = null;
		AST c_AST = null;
		AST a_AST = null;
		
		try {      // for error handling
			o = LT(1);
			o_AST = astFactory.create(o);
			match(ONLY);
			match(OPEN);
			identifier();
			c_AST = (AST)returnAST;
			match(CLOSE);
			{
			switch ( LA(1)) {
			case AS:
			case ONTOLOGY_MODEL_ID:
			case NAME_ID:
			case INTERNAL_ID:
			case EXTERNAL_ID:
			{
				asAlias();
				a_AST = (AST)returnAST;
				break;
			}
			case EOF:
			case CROSS:
			case EXCEPT:
			case FULL:
			case GROUP:
			case INNER:
			case INTERSECT:
			case JOIN:
			case LEFT:
			case NATURAL:
			case ON:
			case ORDER:
			case PREFERRING:
			case RIGHT:
			case UNION:
			case USING:
			case WHERE:
			case LIMIT:
			case CLOSE:
			case COMMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			onlySpec_AST = (AST)currentAST.root;
			onlySpec_AST = (AST)astFactory.make( (new ASTArray(4)).add(astFactory.create(RANGE,"RANGE")).add(c_AST).add(o_AST).add(a_AST));
			currentAST.root = onlySpec_AST;
			currentAST.child = onlySpec_AST!=null &&onlySpec_AST.getFirstChild()!=null ?
				onlySpec_AST.getFirstChild() : onlySpec_AST;
			currentAST.advanceChildToEnd();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_34);
		}
		returnAST = onlySpec_AST;
	}
	
	public final void collectionDerivedTable() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST collectionDerivedTable_AST = null;
		
		try {      // for error handling
			AST tmp137_AST = null;
			tmp137_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp137_AST);
			match(UNNEST);
			match(OPEN);
			collectionValueExpression();
			astFactory.addASTChild(currentAST, returnAST);
			match(CLOSE);
			asAlias();
			astFactory.addASTChild(currentAST, returnAST);
			collectionDerivedTable_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_34);
		}
		returnAST = collectionDerivedTable_AST;
	}
	
	public final void asAlias() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST asAlias_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case AS:
			{
				match(AS);
				break;
			}
			case ONTOLOGY_MODEL_ID:
			case NAME_ID:
			case INTERNAL_ID:
			case EXTERNAL_ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			alias();
			astFactory.addASTChild(currentAST, returnAST);
			asAlias_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_34);
		}
		returnAST = asAlias_AST;
	}
	
	public final void collectionValueExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST collectionValueExpression_AST = null;
		
		try {      // for error handling
			valueExpression();
			astFactory.addASTChild(currentAST, returnAST);
			collectionValueExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = collectionValueExpression_AST;
	}
	
	public final void alias() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST alias_AST = null;
		AST a_AST = null;
		
		try {      // for error handling
			identifier();
			a_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			a_AST.setType(ALIAS);
			alias_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_34);
		}
		returnAST = alias_AST;
	}
	
	public final void crossJoin() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST crossJoin_AST = null;
		
		try {      // for error handling
			AST tmp141_AST = null;
			tmp141_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp141_AST);
			match(CROSS);
			AST tmp142_AST = null;
			tmp142_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp142_AST);
			match(JOIN);
			tableReference();
			astFactory.addASTChild(currentAST, returnAST);
			crossJoin_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_33);
		}
		returnAST = crossJoin_AST;
	}
	
	public final void qualifiedJoin() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST qualifiedJoin_AST = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case FULL:
			case INNER:
			case LEFT:
			case RIGHT:
			{
				joinType();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case JOIN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			AST tmp143_AST = null;
			tmp143_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp143_AST);
			match(JOIN);
			tableReference();
			astFactory.addASTChild(currentAST, returnAST);
			joinCondition();
			astFactory.addASTChild(currentAST, returnAST);
			
				
				
			qualifiedJoin_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_33);
		}
		returnAST = qualifiedJoin_AST;
	}
	
	public final void naturalJoin() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST naturalJoin_AST = null;
		
		try {      // for error handling
			AST tmp144_AST = null;
			tmp144_AST = astFactory.create(LT(1));
			astFactory.addASTChild(currentAST, tmp144_AST);
			match(NATURAL);
			{
			switch ( LA(1)) {
			case FULL:
			case INNER:
			case LEFT:
			case RIGHT:
			{
				joinType();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case JOIN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			AST tmp145_AST = null;
			tmp145_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp145_AST);
			match(JOIN);
			tableReference();
			astFactory.addASTChild(currentAST, returnAST);
			naturalJoin_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_33);
		}
		returnAST = naturalJoin_AST;
	}
	
	public final void joinType() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST joinType_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case INNER:
			{
				AST tmp146_AST = null;
				tmp146_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp146_AST);
				match(INNER);
				joinType_AST = (AST)currentAST.root;
				break;
			}
			case FULL:
			case LEFT:
			case RIGHT:
			{
				outerJoinType();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case OUTER:
				{
					AST tmp147_AST = null;
					tmp147_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp147_AST);
					match(OUTER);
					break;
				}
				case JOIN:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				joinType_AST = (AST)currentAST.root;
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
			recover(ex,_tokenSet_35);
		}
		returnAST = joinType_AST;
	}
	
	public final void joinCondition() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST joinCondition_AST = null;
		
		try {      // for error handling
			AST tmp148_AST = null;
			tmp148_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp148_AST);
			match(ON);
			logicalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			joinCondition_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_33);
		}
		returnAST = joinCondition_AST;
	}
	
	public final void logicalExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalExpression_AST = null;
		
		try {      // for error handling
			valueExpression();
			astFactory.addASTChild(currentAST, returnAST);
			logicalExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_36);
		}
		returnAST = logicalExpression_AST;
	}
	
	public final void outerJoinType() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST outerJoinType_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case LEFT:
			{
				AST tmp149_AST = null;
				tmp149_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp149_AST);
				match(LEFT);
				outerJoinType_AST = (AST)currentAST.root;
				break;
			}
			case RIGHT:
			{
				AST tmp150_AST = null;
				tmp150_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp150_AST);
				match(RIGHT);
				outerJoinType_AST = (AST)currentAST.root;
				break;
			}
			case FULL:
			{
				AST tmp151_AST = null;
				tmp151_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp151_AST);
				match(FULL);
				outerJoinType_AST = (AST)currentAST.root;
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
			recover(ex,_tokenSet_37);
		}
		returnAST = outerJoinType_AST;
	}
	
	public final void havingClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST havingClause_AST = null;
		
		try {      // for error handling
			AST tmp152_AST = null;
			tmp152_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp152_AST);
			match(HAVING);
			logicalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			havingClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_26);
		}
		returnAST = havingClause_AST;
	}
	
	public final void orderElement() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST orderElement_AST = null;
		
		try {      // for error handling
			valueExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case ASCENDING:
			case DESCENDING:
			case LITERAL_ascending:
			case LITERAL_descending:
			{
				ascendingOrDescending();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case EXCEPT:
			case INTERSECT:
			case PREFERRING:
			case UNION:
			case USING:
			case LIMIT:
			case CLOSE:
			case COMMA:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			orderElement_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_38);
		}
		returnAST = orderElement_AST;
	}
	
	public final void ascendingOrDescending() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST ascendingOrDescending_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ASCENDING:
			case LITERAL_ascending:
			{
				{
				switch ( LA(1)) {
				case ASCENDING:
				{
					AST tmp153_AST = null;
					tmp153_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp153_AST);
					match(ASCENDING);
					break;
				}
				case LITERAL_ascending:
				{
					AST tmp154_AST = null;
					tmp154_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp154_AST);
					match(LITERAL_ascending);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				ascendingOrDescending_AST = (AST)currentAST.root;
				ascendingOrDescending_AST.setType(ASCENDING);
				ascendingOrDescending_AST = (AST)currentAST.root;
				break;
			}
			case DESCENDING:
			case LITERAL_descending:
			{
				{
				switch ( LA(1)) {
				case DESCENDING:
				{
					AST tmp155_AST = null;
					tmp155_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp155_AST);
					match(DESCENDING);
					break;
				}
				case LITERAL_descending:
				{
					AST tmp156_AST = null;
					tmp156_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp156_AST);
					match(LITERAL_descending);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				ascendingOrDescending_AST = (AST)currentAST.root;
				ascendingOrDescending_AST.setType(DESCENDING);
				ascendingOrDescending_AST = (AST)currentAST.root;
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
			recover(ex,_tokenSet_38);
		}
		returnAST = ascendingOrDescending_AST;
	}
	
	public final void logicalOrExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalOrExpression_AST = null;
		
		try {      // for error handling
			logicalAndExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop182:
			do {
				if ((LA(1)==OR)) {
					AST tmp157_AST = null;
					tmp157_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp157_AST);
					match(OR);
					logicalAndExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop182;
				}
				
			} while (true);
			}
			logicalOrExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_32);
		}
		returnAST = logicalOrExpression_AST;
	}
	
	public final void logicalAndExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST logicalAndExpression_AST = null;
		
		try {      // for error handling
			negatedExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop185:
			do {
				if ((LA(1)==AND)) {
					AST tmp158_AST = null;
					tmp158_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp158_AST);
					match(AND);
					negatedExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop185;
				}
				
			} while (true);
			}
			logicalAndExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_39);
		}
		returnAST = logicalAndExpression_AST;
	}
	
	public final void negatedExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST negatedExpression_AST = null;
		AST x_AST = null;
		AST y_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NOT:
			{
				AST tmp159_AST = null;
				tmp159_AST = astFactory.create(LT(1));
				match(NOT);
				negatedExpression();
				x_AST = (AST)returnAST;
				negatedExpression_AST = (AST)currentAST.root;
				negatedExpression_AST = negateNode(x_AST);
				currentAST.root = negatedExpression_AST;
				currentAST.child = negatedExpression_AST!=null &&negatedExpression_AST.getFirstChild()!=null ?
					negatedExpression_AST.getFirstChild() : negatedExpression_AST;
				currentAST.advanceChildToEnd();
				break;
			}
			case ALL:
			case ANY:
			case ARRAY:
			case AVG:
			case CASE:
			case COUNT:
			case EXISTS:
			case FALSE:
			case MAX:
			case MIN:
			case NULL:
			case SUM:
			case TRUE:
			case TYPEOF:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case OPEN:
			case ONTOLOGY_MODEL_ID:
			case NUM_INT:
			case NAME_ID:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case INTERNAL_ID:
			case EXTERNAL_ID:
			{
				equalityExpression();
				y_AST = (AST)returnAST;
				negatedExpression_AST = (AST)currentAST.root;
				negatedExpression_AST = y_AST;
				currentAST.root = negatedExpression_AST;
				currentAST.child = negatedExpression_AST!=null &&negatedExpression_AST.getFirstChild()!=null ?
					negatedExpression_AST.getFirstChild() : negatedExpression_AST;
				currentAST.advanceChildToEnd();
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
			recover(ex,_tokenSet_40);
		}
		returnAST = negatedExpression_AST;
	}
	
	public final void equalityExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST equalityExpression_AST = null;
		AST x_AST = null;
		Token  is = null;
		AST is_AST = null;
		Token  ne = null;
		AST ne_AST = null;
		AST y_AST = null;
		
		try {      // for error handling
			relationalExpression();
			x_AST = (AST)returnAST;
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop192:
			do {
				if ((_tokenSet_41.member(LA(1)))) {
					{
					switch ( LA(1)) {
					case EQ:
					{
						AST tmp160_AST = null;
						tmp160_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp160_AST);
						match(EQ);
						break;
					}
					case IS:
					{
						is = LT(1);
						is_AST = astFactory.create(is);
						astFactory.makeASTRoot(currentAST, is_AST);
						match(IS);
						is_AST.setType(EQ);
						{
						switch ( LA(1)) {
						case NOT:
						{
							match(NOT);
							is_AST.setType(NE);
							break;
						}
						case ALL:
						case ANY:
						case ARRAY:
						case AVG:
						case CASE:
						case COUNT:
						case EXISTS:
						case FALSE:
						case MAX:
						case MIN:
						case NULL:
						case OF:
						case SUM:
						case TRUE:
						case TYPEOF:
						case NUM_DOUBLE:
						case NUM_FLOAT:
						case NUM_LONG:
						case OPEN:
						case ONTOLOGY_MODEL_ID:
						case NUM_INT:
						case NAME_ID:
						case QUOTED_STRING:
						case PLUS:
						case MINUS:
						case INTERNAL_ID:
						case EXTERNAL_ID:
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
						case OF:
						{
							AST tmp162_AST = null;
							tmp162_AST = astFactory.create(LT(1));
							astFactory.addASTChild(currentAST, tmp162_AST);
							match(OF);
							break;
						}
						case ALL:
						case ANY:
						case ARRAY:
						case AVG:
						case CASE:
						case COUNT:
						case EXISTS:
						case FALSE:
						case MAX:
						case MIN:
						case NULL:
						case SUM:
						case TRUE:
						case TYPEOF:
						case NUM_DOUBLE:
						case NUM_FLOAT:
						case NUM_LONG:
						case OPEN:
						case ONTOLOGY_MODEL_ID:
						case NUM_INT:
						case NAME_ID:
						case QUOTED_STRING:
						case PLUS:
						case MINUS:
						case INTERNAL_ID:
						case EXTERNAL_ID:
						{
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						break;
					}
					case NE:
					{
						AST tmp163_AST = null;
						tmp163_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp163_AST);
						match(NE);
						break;
					}
					case SQL_NE:
					{
						ne = LT(1);
						ne_AST = astFactory.create(ne);
						astFactory.makeASTRoot(currentAST, ne_AST);
						match(SQL_NE);
						ne_AST.setType(NE);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					relationalExpression();
					y_AST = (AST)returnAST;
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop192;
				}
				
			} while (true);
			}
			equalityExpression_AST = (AST)currentAST.root;
			
						// Post process the equality expression to clean up 'is null', etc.
						equalityExpression_AST = processEqualityExpression(equalityExpression_AST);
					
			currentAST.root = equalityExpression_AST;
			currentAST.child = equalityExpression_AST!=null &&equalityExpression_AST.getFirstChild()!=null ?
				equalityExpression_AST.getFirstChild() : equalityExpression_AST;
			currentAST.advanceChildToEnd();
			equalityExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_40);
		}
		returnAST = equalityExpression_AST;
	}
	
	public final void relationalExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST relationalExpression_AST = null;
		Token  n = null;
		AST n_AST = null;
		Token  i = null;
		AST i_AST = null;
		Token  b = null;
		AST b_AST = null;
		Token  l = null;
		AST l_AST = null;
		
		try {      // for error handling
			concatenation();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case EOF:
			case AND:
			case AS:
			case ASCENDING:
			case DESCENDING:
			case EXCEPT:
			case FROM:
			case GROUP:
			case HAVING:
			case INTERSECT:
			case IS:
			case ON:
			case OR:
			case ORDER:
			case PREFERRING:
			case THEN:
			case UNION:
			case USING:
			case WHERE:
			case LIMIT:
			case CLOSE:
			case COMMA:
			case EQ:
			case LITERAL_ascending:
			case LITERAL_descending:
			case NE:
			case SQL_NE:
			case LT:
			case GT:
			case LE:
			case GE:
			case CLOSE_BRACKET:
			{
				{
				{
				_loop198:
				do {
					if (((LA(1) >= LT && LA(1) <= GE))) {
						{
						switch ( LA(1)) {
						case LT:
						{
							AST tmp164_AST = null;
							tmp164_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp164_AST);
							match(LT);
							break;
						}
						case GT:
						{
							AST tmp165_AST = null;
							tmp165_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp165_AST);
							match(GT);
							break;
						}
						case LE:
						{
							AST tmp166_AST = null;
							tmp166_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp166_AST);
							match(LE);
							break;
						}
						case GE:
						{
							AST tmp167_AST = null;
							tmp167_AST = astFactory.create(LT(1));
							astFactory.makeASTRoot(currentAST, tmp167_AST);
							match(GE);
							break;
						}
						default:
						{
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
						}
						additiveExpression();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop198;
					}
					
				} while (true);
				}
				}
				break;
			}
			case BETWEEN:
			case IN:
			case LIKE:
			case NOT:
			{
				{
				switch ( LA(1)) {
				case NOT:
				{
					n = LT(1);
					n_AST = astFactory.create(n);
					match(NOT);
					break;
				}
				case BETWEEN:
				case IN:
				case LIKE:
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
				case IN:
				{
					{
					i = LT(1);
					i_AST = astFactory.create(i);
					astFactory.makeASTRoot(currentAST, i_AST);
					match(IN);
					
										i_AST.setType( (n == null) ? IN : NOT_IN);
										i_AST.setText( (n == null) ? "in" : "not in");
									
					inList();
					astFactory.addASTChild(currentAST, returnAST);
					}
					break;
				}
				case BETWEEN:
				{
					{
					b = LT(1);
					b_AST = astFactory.create(b);
					astFactory.makeASTRoot(currentAST, b_AST);
					match(BETWEEN);
					
										b_AST.setType( (n == null) ? BETWEEN : NOT_BETWEEN);
										b_AST.setText( (n == null) ? "between" : "not between");
									
					betweenList();
					astFactory.addASTChild(currentAST, returnAST);
					}
					break;
				}
				case LIKE:
				{
					{
					l = LT(1);
					l_AST = astFactory.create(l);
					astFactory.makeASTRoot(currentAST, l_AST);
					match(LIKE);
					
										l_AST.setType( (n == null) ? LIKE : NOT_LIKE);
										l_AST.setText( (n == null) ? "like" : "not like");
									
					concatenation();
					astFactory.addASTChild(currentAST, returnAST);
					}
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
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
			recover(ex,_tokenSet_42);
		}
		returnAST = relationalExpression_AST;
	}
	
	public final void additiveExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST additiveExpression_AST = null;
		
		try {      // for error handling
			multiplyExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop213:
			do {
				if ((LA(1)==PLUS||LA(1)==MINUS)) {
					{
					switch ( LA(1)) {
					case PLUS:
					{
						AST tmp168_AST = null;
						tmp168_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp168_AST);
						match(PLUS);
						break;
					}
					case MINUS:
					{
						AST tmp169_AST = null;
						tmp169_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp169_AST);
						match(MINUS);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					multiplyExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop213;
				}
				
			} while (true);
			}
			additiveExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_43);
		}
		returnAST = additiveExpression_AST;
	}
	
	public final void betweenList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST betweenList_AST = null;
		
		try {      // for error handling
			concatenation();
			astFactory.addASTChild(currentAST, returnAST);
			match(AND);
			concatenation();
			astFactory.addASTChild(currentAST, returnAST);
			betweenList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_42);
		}
		returnAST = betweenList_AST;
	}
	
	public final void compoundExpr() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST compoundExpr_AST = null;
		
		try {      // for error handling
			{
			match(OPEN);
			{
			switch ( LA(1)) {
			case ALL:
			case ANY:
			case ARRAY:
			case AVG:
			case CASE:
			case COUNT:
			case EXISTS:
			case FALSE:
			case MAX:
			case MIN:
			case NOT:
			case NULL:
			case SUM:
			case TRUE:
			case TYPEOF:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case OPEN:
			case ONTOLOGY_MODEL_ID:
			case NUM_INT:
			case NAME_ID:
			case QUOTED_STRING:
			case PLUS:
			case MINUS:
			case INTERNAL_ID:
			case EXTERNAL_ID:
			{
				{
				valueExpression();
				astFactory.addASTChild(currentAST, returnAST);
				{
				_loop272:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						valueExpression();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop272;
					}
					
				} while (true);
				}
				}
				break;
			}
			case SELECT:
			{
				subQuery();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			match(CLOSE);
			}
			compoundExpr_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_16);
		}
		returnAST = compoundExpr_AST;
	}
	
	public final void multiplyExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST multiplyExpression_AST = null;
		
		try {      // for error handling
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop217:
			do {
				if ((LA(1)==STAR||LA(1)==DIV)) {
					{
					switch ( LA(1)) {
					case STAR:
					{
						AST tmp174_AST = null;
						tmp174_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp174_AST);
						match(STAR);
						break;
					}
					case DIV:
					{
						AST tmp175_AST = null;
						tmp175_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp175_AST);
						match(DIV);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					unaryExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop217;
				}
				
			} while (true);
			}
			multiplyExpression_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_44);
		}
		returnAST = multiplyExpression_AST;
	}
	
	public final void unaryExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST unaryExpression_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case MINUS:
			{
				AST tmp176_AST = null;
				tmp176_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp176_AST);
				match(MINUS);
				tmp176_AST.setType(UNARY_MINUS);
				unaryExpression();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case PLUS:
			{
				AST tmp177_AST = null;
				tmp177_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp177_AST);
				match(PLUS);
				tmp177_AST.setType(UNARY_PLUS);
				unaryExpression();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case CASE:
			{
				caseExpression();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case ALL:
			case ANY:
			case EXISTS:
			{
				quantifiedExpression();
				astFactory.addASTChild(currentAST, returnAST);
				unaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case ARRAY:
			case AVG:
			case COUNT:
			case FALSE:
			case MAX:
			case MIN:
			case NULL:
			case SUM:
			case TRUE:
			case TYPEOF:
			case NUM_DOUBLE:
			case NUM_FLOAT:
			case NUM_LONG:
			case OPEN:
			case ONTOLOGY_MODEL_ID:
			case NUM_INT:
			case NAME_ID:
			case QUOTED_STRING:
			case INTERNAL_ID:
			case EXTERNAL_ID:
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
			recover(ex,_tokenSet_45);
		}
		returnAST = unaryExpression_AST;
	}
	
	public final void caseExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST caseExpression_AST = null;
		
		try {      // for error handling
			if ((LA(1)==CASE) && (LA(2)==WHEN)) {
				AST tmp178_AST = null;
				tmp178_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp178_AST);
				match(CASE);
				{
				int _cnt221=0;
				_loop221:
				do {
					if ((LA(1)==WHEN)) {
						whenClause();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt221>=1 ) { break _loop221; } else {throw new NoViableAltException(LT(1), getFilename());}
					}
					
					_cnt221++;
				} while (true);
				}
				{
				switch ( LA(1)) {
				case ELSE:
				{
					elseClause();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case END:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(END);
				caseExpression_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==CASE) && (_tokenSet_46.member(LA(2)))) {
				AST tmp180_AST = null;
				tmp180_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp180_AST);
				match(CASE);
				tmp180_AST.setType(CASE2);
				unaryExpression();
				astFactory.addASTChild(currentAST, returnAST);
				{
				int _cnt224=0;
				_loop224:
				do {
					if ((LA(1)==WHEN)) {
						altWhenClause();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						if ( _cnt224>=1 ) { break _loop224; } else {throw new NoViableAltException(LT(1), getFilename());}
					}
					
					_cnt224++;
				} while (true);
				}
				{
				switch ( LA(1)) {
				case ELSE:
				{
					elseClause();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case END:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(END);
				caseExpression_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_45);
		}
		returnAST = caseExpression_AST;
	}
	
	public final void quantifiedExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST quantifiedExpression_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ALL:
			case EXISTS:
			{
				{
				switch ( LA(1)) {
				case EXISTS:
				{
					AST tmp182_AST = null;
					tmp182_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp182_AST);
					match(EXISTS);
					break;
				}
				case ALL:
				{
					AST tmp183_AST = null;
					tmp183_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp183_AST);
					match(ALL);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(OPEN);
				{
				subQuery();
				astFactory.addASTChild(currentAST, returnAST);
				}
				match(CLOSE);
				quantifiedExpression_AST = (AST)currentAST.root;
				break;
			}
			case ANY:
			{
				{
				AST tmp186_AST = null;
				tmp186_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp186_AST);
				match(ANY);
				}
				match(OPEN);
				{
				switch ( LA(1)) {
				case SELECT:
				{
					subQuery();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case ONTOLOGY_MODEL_ID:
				case NAME_ID:
				case INTERNAL_ID:
				case EXTERNAL_ID:
				{
					propIdentifier();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(CLOSE);
				quantifiedExpression_AST = (AST)currentAST.root;
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
			recover(ex,_tokenSet_45);
		}
		returnAST = quantifiedExpression_AST;
	}
	
	public final void primaryExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST primaryExpression_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case AVG:
			case COUNT:
			case MAX:
			case MIN:
			case SUM:
			case TYPEOF:
			case ONTOLOGY_MODEL_ID:
			case NAME_ID:
			case INTERNAL_ID:
			case EXTERNAL_ID:
			{
				identPrimary();
				astFactory.addASTChild(currentAST, returnAST);
				primaryExpression_AST = (AST)currentAST.root;
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
				constant();
				astFactory.addASTChild(currentAST, returnAST);
				primaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case OPEN:
			{
				match(OPEN);
				{
				switch ( LA(1)) {
				case ALL:
				case ANY:
				case ARRAY:
				case AVG:
				case BOOLEAN:
				case REAL:
				case CASE:
				case COUNT:
				case EXISTS:
				case FALSE:
				case INT:
				case MAX:
				case MIN:
				case MULTILINGUAL:
				case ENUM:
				case NOT:
				case NULL:
				case ONLY:
				case REF:
				case STRING:
				case SUM:
				case TRUE:
				case TYPEOF:
				case URI:
				case COUNTTYPE:
				case NUM_DOUBLE:
				case NUM_FLOAT:
				case NUM_LONG:
				case OPEN:
				case ONTOLOGY_MODEL_ID:
				case NUM_INT:
				case NAME_ID:
				case QUOTED_STRING:
				case PLUS:
				case MINUS:
				case INTERNAL_ID:
				case EXTERNAL_ID:
				{
					{
					{
					switch ( LA(1)) {
					case ONLY:
					{
						AST tmp190_AST = null;
						tmp190_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp190_AST);
						match(ONLY);
						break;
					}
					case ALL:
					case ANY:
					case ARRAY:
					case AVG:
					case BOOLEAN:
					case REAL:
					case CASE:
					case COUNT:
					case EXISTS:
					case FALSE:
					case INT:
					case MAX:
					case MIN:
					case MULTILINGUAL:
					case ENUM:
					case NOT:
					case NULL:
					case REF:
					case STRING:
					case SUM:
					case TRUE:
					case TYPEOF:
					case URI:
					case COUNTTYPE:
					case NUM_DOUBLE:
					case NUM_FLOAT:
					case NUM_LONG:
					case OPEN:
					case ONTOLOGY_MODEL_ID:
					case NUM_INT:
					case NAME_ID:
					case QUOTED_STRING:
					case PLUS:
					case MINUS:
					case INTERNAL_ID:
					case EXTERNAL_ID:
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
					case ALL:
					case ANY:
					case ARRAY:
					case AVG:
					case CASE:
					case COUNT:
					case EXISTS:
					case FALSE:
					case MAX:
					case MIN:
					case NOT:
					case NULL:
					case SUM:
					case TRUE:
					case TYPEOF:
					case NUM_DOUBLE:
					case NUM_FLOAT:
					case NUM_LONG:
					case OPEN:
					case ONTOLOGY_MODEL_ID:
					case NUM_INT:
					case NAME_ID:
					case QUOTED_STRING:
					case PLUS:
					case MINUS:
					case INTERNAL_ID:
					case EXTERNAL_ID:
					{
						valueExpression();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					case BOOLEAN:
					case REAL:
					case INT:
					case MULTILINGUAL:
					case ENUM:
					case REF:
					case STRING:
					case URI:
					case COUNTTYPE:
					{
						datatype();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					{
					_loop245:
					do {
						if ((LA(1)==COMMA)) {
							match(COMMA);
							{
							switch ( LA(1)) {
							case ONLY:
							{
								AST tmp192_AST = null;
								tmp192_AST = astFactory.create(LT(1));
								astFactory.addASTChild(currentAST, tmp192_AST);
								match(ONLY);
								break;
							}
							case ALL:
							case ANY:
							case ARRAY:
							case AVG:
							case BOOLEAN:
							case REAL:
							case CASE:
							case COUNT:
							case EXISTS:
							case FALSE:
							case INT:
							case MAX:
							case MIN:
							case MULTILINGUAL:
							case ENUM:
							case NOT:
							case NULL:
							case REF:
							case STRING:
							case SUM:
							case TRUE:
							case TYPEOF:
							case URI:
							case COUNTTYPE:
							case NUM_DOUBLE:
							case NUM_FLOAT:
							case NUM_LONG:
							case OPEN:
							case ONTOLOGY_MODEL_ID:
							case NUM_INT:
							case NAME_ID:
							case QUOTED_STRING:
							case PLUS:
							case MINUS:
							case INTERNAL_ID:
							case EXTERNAL_ID:
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
							case ALL:
							case ANY:
							case ARRAY:
							case AVG:
							case CASE:
							case COUNT:
							case EXISTS:
							case FALSE:
							case MAX:
							case MIN:
							case NOT:
							case NULL:
							case SUM:
							case TRUE:
							case TYPEOF:
							case NUM_DOUBLE:
							case NUM_FLOAT:
							case NUM_LONG:
							case OPEN:
							case ONTOLOGY_MODEL_ID:
							case NUM_INT:
							case NAME_ID:
							case QUOTED_STRING:
							case PLUS:
							case MINUS:
							case INTERNAL_ID:
							case EXTERNAL_ID:
							{
								valueExpression();
								astFactory.addASTChild(currentAST, returnAST);
								break;
							}
							case BOOLEAN:
							case REAL:
							case INT:
							case MULTILINGUAL:
							case ENUM:
							case REF:
							case STRING:
							case URI:
							case COUNTTYPE:
							{
								datatype();
								astFactory.addASTChild(currentAST, returnAST);
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
							break _loop245;
						}
						
					} while (true);
					}
					}
					break;
				}
				case SELECT:
				{
					subQuery();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(CLOSE);
				primaryExpression_AST = (AST)currentAST.root;
				break;
			}
			case ARRAY:
			{
				arrayValueExpression();
				astFactory.addASTChild(currentAST, returnAST);
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
			recover(ex,_tokenSet_45);
		}
		returnAST = primaryExpression_AST;
	}
	
	public final void whenClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST whenClause_AST = null;
		
		try {      // for error handling
			{
			AST tmp194_AST = null;
			tmp194_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp194_AST);
			match(WHEN);
			logicalExpression();
			astFactory.addASTChild(currentAST, returnAST);
			match(THEN);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			}
			whenClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_47);
		}
		returnAST = whenClause_AST;
	}
	
	public final void elseClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST elseClause_AST = null;
		
		try {      // for error handling
			{
			AST tmp196_AST = null;
			tmp196_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp196_AST);
			match(ELSE);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			}
			elseClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_48);
		}
		returnAST = elseClause_AST;
	}
	
	public final void altWhenClause() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST altWhenClause_AST = null;
		
		try {      // for error handling
			{
			AST tmp197_AST = null;
			tmp197_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp197_AST);
			match(WHEN);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			match(THEN);
			unaryExpression();
			astFactory.addASTChild(currentAST, returnAST);
			}
			altWhenClause_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_47);
		}
		returnAST = altWhenClause_AST;
	}
	
	public final void subQuery() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST subQuery_AST = null;
		
		try {      // for error handling
			queryRule();
			astFactory.addASTChild(currentAST, returnAST);
			subQuery_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = subQuery_AST;
	}
	
	public final void identPrimary() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST identPrimary_AST = null;
		Token  op = null;
		AST op_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case ONTOLOGY_MODEL_ID:
			case NAME_ID:
			case INTERNAL_ID:
			case EXTERNAL_ID:
			{
				propIdentifier();
				astFactory.addASTChild(currentAST, returnAST);
				{
				switch ( LA(1)) {
				case DOT:
				{
					AST tmp199_AST = null;
					tmp199_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp199_AST);
					match(DOT);
					path();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case EOF:
				case AND:
				case AS:
				case ASCENDING:
				case BETWEEN:
				case DESCENDING:
				case ELSE:
				case END:
				case EXCEPT:
				case FROM:
				case GROUP:
				case HAVING:
				case IN:
				case INTERSECT:
				case IS:
				case LIKE:
				case NOT:
				case ON:
				case OR:
				case ORDER:
				case PREFERRING:
				case THEN:
				case UNION:
				case USING:
				case WHEN:
				case WHERE:
				case LIMIT:
				case OPEN:
				case CLOSE:
				case COMMA:
				case EQ:
				case STAR:
				case LITERAL_ascending:
				case LITERAL_descending:
				case NE:
				case SQL_NE:
				case LT:
				case GT:
				case LE:
				case GE:
				case CONCAT:
				case PLUS:
				case MINUS:
				case DIV:
				case CLOSE_BRACKET:
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
				case OPEN:
				{
					{
					op = LT(1);
					op_AST = astFactory.create(op);
					astFactory.makeASTRoot(currentAST, op_AST);
					match(OPEN);
					op_AST.setType(METHOD_CALL);
					exprList();
					astFactory.addASTChild(currentAST, returnAST);
					match(CLOSE);
					{
					switch ( LA(1)) {
					case DOT:
					{
						AST tmp201_AST = null;
						tmp201_AST = astFactory.create(LT(1));
						astFactory.makeASTRoot(currentAST, tmp201_AST);
						match(DOT);
						path();
						astFactory.addASTChild(currentAST, returnAST);
						break;
					}
					case EOF:
					case AND:
					case AS:
					case ASCENDING:
					case BETWEEN:
					case DESCENDING:
					case ELSE:
					case END:
					case EXCEPT:
					case FROM:
					case GROUP:
					case HAVING:
					case IN:
					case INTERSECT:
					case IS:
					case LIKE:
					case NOT:
					case ON:
					case OR:
					case ORDER:
					case PREFERRING:
					case THEN:
					case UNION:
					case USING:
					case WHEN:
					case WHERE:
					case LIMIT:
					case CLOSE:
					case COMMA:
					case EQ:
					case STAR:
					case LITERAL_ascending:
					case LITERAL_descending:
					case NE:
					case SQL_NE:
					case LT:
					case GT:
					case LE:
					case GE:
					case CONCAT:
					case PLUS:
					case MINUS:
					case DIV:
					case CLOSE_BRACKET:
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
					break;
				}
				case EOF:
				case AND:
				case AS:
				case ASCENDING:
				case BETWEEN:
				case DESCENDING:
				case ELSE:
				case END:
				case EXCEPT:
				case FROM:
				case GROUP:
				case HAVING:
				case IN:
				case INTERSECT:
				case IS:
				case LIKE:
				case NOT:
				case ON:
				case OR:
				case ORDER:
				case PREFERRING:
				case THEN:
				case UNION:
				case USING:
				case WHEN:
				case WHERE:
				case LIMIT:
				case CLOSE:
				case COMMA:
				case EQ:
				case STAR:
				case LITERAL_ascending:
				case LITERAL_descending:
				case NE:
				case SQL_NE:
				case LT:
				case GT:
				case LE:
				case GE:
				case CONCAT:
				case PLUS:
				case MINUS:
				case DIV:
				case CLOSE_BRACKET:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				identPrimary_AST = (AST)currentAST.root;
				break;
			}
			case AVG:
			case COUNT:
			case MAX:
			case MIN:
			case SUM:
			{
				aggregate();
				astFactory.addASTChild(currentAST, returnAST);
				identPrimary_AST = (AST)currentAST.root;
				break;
			}
			case TYPEOF:
			{
				AST tmp202_AST = null;
				tmp202_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp202_AST);
				match(TYPEOF);
				match(OPEN);
				identifier();
				astFactory.addASTChild(currentAST, returnAST);
				match(CLOSE);
				{
				switch ( LA(1)) {
				case DOT:
				{
					AST tmp205_AST = null;
					tmp205_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp205_AST);
					match(DOT);
					path();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case EOF:
				case AND:
				case AS:
				case ASCENDING:
				case BETWEEN:
				case DESCENDING:
				case ELSE:
				case END:
				case EXCEPT:
				case FROM:
				case GROUP:
				case HAVING:
				case IN:
				case INTERSECT:
				case IS:
				case LIKE:
				case NOT:
				case ON:
				case OR:
				case ORDER:
				case PREFERRING:
				case THEN:
				case UNION:
				case USING:
				case WHEN:
				case WHERE:
				case LIMIT:
				case CLOSE:
				case COMMA:
				case EQ:
				case STAR:
				case LITERAL_ascending:
				case LITERAL_descending:
				case NE:
				case SQL_NE:
				case LT:
				case GT:
				case LE:
				case GE:
				case CONCAT:
				case PLUS:
				case MINUS:
				case DIV:
				case CLOSE_BRACKET:
				{
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				identPrimary_AST = (AST)currentAST.root;
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
			recover(ex,_tokenSet_45);
		}
		returnAST = identPrimary_AST;
	}
	
	public final void constant() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST constant_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case NUM_INT:
			{
				AST tmp206_AST = null;
				tmp206_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp206_AST);
				match(NUM_INT);
				constant_AST = (AST)currentAST.root;
				break;
			}
			case NUM_FLOAT:
			{
				AST tmp207_AST = null;
				tmp207_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp207_AST);
				match(NUM_FLOAT);
				constant_AST = (AST)currentAST.root;
				break;
			}
			case NUM_LONG:
			{
				AST tmp208_AST = null;
				tmp208_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp208_AST);
				match(NUM_LONG);
				constant_AST = (AST)currentAST.root;
				break;
			}
			case NUM_DOUBLE:
			{
				AST tmp209_AST = null;
				tmp209_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp209_AST);
				match(NUM_DOUBLE);
				constant_AST = (AST)currentAST.root;
				break;
			}
			case QUOTED_STRING:
			{
				AST tmp210_AST = null;
				tmp210_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp210_AST);
				match(QUOTED_STRING);
				constant_AST = (AST)currentAST.root;
				break;
			}
			case NULL:
			{
				AST tmp211_AST = null;
				tmp211_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp211_AST);
				match(NULL);
				constant_AST = (AST)currentAST.root;
				break;
			}
			case TRUE:
			{
				AST tmp212_AST = null;
				tmp212_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp212_AST);
				match(TRUE);
				constant_AST = (AST)currentAST.root;
				break;
			}
			case FALSE:
			{
				AST tmp213_AST = null;
				tmp213_AST = astFactory.create(LT(1));
				astFactory.addASTChild(currentAST, tmp213_AST);
				match(FALSE);
				constant_AST = (AST)currentAST.root;
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
			recover(ex,_tokenSet_45);
		}
		returnAST = constant_AST;
	}
	
	public final void arrayValueExpression() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayValueExpression_AST = null;
		
		try {      // for error handling
			if ((LA(1)==ARRAY) && (LA(2)==OPEN_BRACKET)) {
				arrayValueConstructorByEnumeration();
				astFactory.addASTChild(currentAST, returnAST);
				arrayValueExpression_AST = (AST)currentAST.root;
			}
			else if ((LA(1)==ARRAY) && (LA(2)==OPEN)) {
				arrayValueConstructorByQuery();
				astFactory.addASTChild(currentAST, returnAST);
				arrayValueExpression_AST = (AST)currentAST.root;
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_45);
		}
		returnAST = arrayValueExpression_AST;
	}
	
	public final void arrayValueConstructorByEnumeration() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayValueConstructorByEnumeration_AST = null;
		
		try {      // for error handling
			AST tmp214_AST = null;
			tmp214_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp214_AST);
			match(ARRAY);
			match(OPEN_BRACKET);
			{
			valueExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			_loop250:
			do {
				if ((LA(1)==COMMA)) {
					match(COMMA);
					valueExpression();
					astFactory.addASTChild(currentAST, returnAST);
				}
				else {
					break _loop250;
				}
				
			} while (true);
			}
			}
			match(CLOSE_BRACKET);
			arrayValueConstructorByEnumeration_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_45);
		}
		returnAST = arrayValueConstructorByEnumeration_AST;
	}
	
	public final void arrayValueConstructorByQuery() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST arrayValueConstructorByQuery_AST = null;
		
		try {      // for error handling
			AST tmp218_AST = null;
			tmp218_AST = astFactory.create(LT(1));
			astFactory.makeASTRoot(currentAST, tmp218_AST);
			match(ARRAY);
			match(OPEN);
			subQuery();
			astFactory.addASTChild(currentAST, returnAST);
			match(CLOSE);
			arrayValueConstructorByQuery_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_45);
		}
		returnAST = arrayValueConstructorByQuery_AST;
	}
	
	public final void path() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST path_AST = null;
		
		try {      // for error handling
			propIdentifier();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case DOT:
			{
				AST tmp221_AST = null;
				tmp221_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp221_AST);
				match(DOT);
				path();
				astFactory.addASTChild(currentAST, returnAST);
				break;
			}
			case EOF:
			case AND:
			case AS:
			case ASCENDING:
			case BETWEEN:
			case DESCENDING:
			case ELSE:
			case END:
			case EXCEPT:
			case FROM:
			case GROUP:
			case HAVING:
			case IN:
			case INTERSECT:
			case IS:
			case LIKE:
			case NOT:
			case ON:
			case OR:
			case ORDER:
			case PREFERRING:
			case THEN:
			case UNION:
			case USING:
			case WHEN:
			case WHERE:
			case LIMIT:
			case OPEN:
			case CLOSE:
			case COMMA:
			case EQ:
			case STAR:
			case LITERAL_ascending:
			case LITERAL_descending:
			case NE:
			case SQL_NE:
			case LT:
			case GT:
			case LE:
			case GE:
			case CONCAT:
			case PLUS:
			case MINUS:
			case DIV:
			case CLOSE_BRACKET:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			path_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_49);
		}
		returnAST = path_AST;
	}
	
	public final void exprList() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST exprList_AST = null;
		
		try {      // for error handling
			valueExpression();
			astFactory.addASTChild(currentAST, returnAST);
			{
			switch ( LA(1)) {
			case CLOSE:
			case COMMA:
			{
				{
				_loop261:
				do {
					if ((LA(1)==COMMA)) {
						match(COMMA);
						valueExpression();
						astFactory.addASTChild(currentAST, returnAST);
					}
					else {
						break _loop261;
					}
					
				} while (true);
				}
				break;
			}
			case AS:
			{
				match(AS);
				{
				switch ( LA(1)) {
				case BOOLEAN:
				case REAL:
				case INT:
				case MULTILINGUAL:
				case ENUM:
				case REF:
				case STRING:
				case URI:
				case COUNTTYPE:
				{
					datatype();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				case ONTOLOGY_MODEL_ID:
				case NAME_ID:
				case INTERNAL_ID:
				case EXTERNAL_ID:
				{
					identifier();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			exprList_AST = (AST)currentAST.root;
			exprList_AST = (AST)astFactory.make( (new ASTArray(2)).add(astFactory.create(EXPR_LIST,"exprList")).add(exprList_AST));
			currentAST.root = exprList_AST;
			currentAST.child = exprList_AST!=null &&exprList_AST.getFirstChild()!=null ?
				exprList_AST.getFirstChild() : exprList_AST;
			currentAST.advanceChildToEnd();
			exprList_AST = (AST)currentAST.root;
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		returnAST = exprList_AST;
	}
	
	public final void aggregate() throws RecognitionException, TokenStreamException {
		
		returnAST = null;
		ASTPair currentAST = new ASTPair();
		AST aggregate_AST = null;
		
		try {      // for error handling
			switch ( LA(1)) {
			case AVG:
			case MAX:
			case MIN:
			case SUM:
			{
				{
				switch ( LA(1)) {
				case SUM:
				{
					AST tmp224_AST = null;
					tmp224_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp224_AST);
					match(SUM);
					break;
				}
				case AVG:
				{
					AST tmp225_AST = null;
					tmp225_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp225_AST);
					match(AVG);
					break;
				}
				case MAX:
				{
					AST tmp226_AST = null;
					tmp226_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp226_AST);
					match(MAX);
					break;
				}
				case MIN:
				{
					AST tmp227_AST = null;
					tmp227_AST = astFactory.create(LT(1));
					astFactory.makeASTRoot(currentAST, tmp227_AST);
					match(MIN);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(OPEN);
				additiveExpression();
				astFactory.addASTChild(currentAST, returnAST);
				match(CLOSE);
				aggregate_AST = (AST)currentAST.root;
				aggregate_AST.setType(AGGREGATE);
				aggregate_AST = (AST)currentAST.root;
				break;
			}
			case COUNT:
			{
				AST tmp230_AST = null;
				tmp230_AST = astFactory.create(LT(1));
				astFactory.makeASTRoot(currentAST, tmp230_AST);
				match(COUNT);
				match(OPEN);
				{
				switch ( LA(1)) {
				case STAR:
				{
					AST tmp232_AST = null;
					tmp232_AST = astFactory.create(LT(1));
					astFactory.addASTChild(currentAST, tmp232_AST);
					match(STAR);
					tmp232_AST.setType(ROW_STAR);
					break;
				}
				case DISTINCT:
				case ONTOLOGY_MODEL_ID:
				case NAME_ID:
				case INTERNAL_ID:
				case EXTERNAL_ID:
				{
					{
					switch ( LA(1)) {
					case DISTINCT:
					{
						AST tmp233_AST = null;
						tmp233_AST = astFactory.create(LT(1));
						astFactory.addASTChild(currentAST, tmp233_AST);
						match(DISTINCT);
						break;
					}
					case ONTOLOGY_MODEL_ID:
					case NAME_ID:
					case INTERNAL_ID:
					case EXTERNAL_ID:
					{
						break;
					}
					default:
					{
						throw new NoViableAltException(LT(1), getFilename());
					}
					}
					}
					path();
					astFactory.addASTChild(currentAST, returnAST);
					break;
				}
				default:
				{
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
				}
				match(CLOSE);
				aggregate_AST = (AST)currentAST.root;
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
			recover(ex,_tokenSet_45);
		}
		returnAST = aggregate_AST;
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
		"FLOAT_SUFFIX"
	};
	
	protected void buildTokenTypeASTClassMap() {
		tokenTypeToASTClassMap=null;
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 2L, 0L, 32L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 0L, 0L, 1610616896L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { -2642647642968830830L, 9092608762L, 1879036912L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 1024L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 8797166764034L, 8192L, 32L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 2L, 0L, 16L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 2L, 0L, 160L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 8388610L, 2097152L, 160L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { -3237475805768332158L, 8756798090L, 201311152L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 2L, 67108864L, 16L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 2L, 536870912L, 160L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 0L, 0L, 32L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 0L, 0L, 160L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 2L, 2097152L, 160L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 8389122L, 2097152L, 160L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { -3458720085592502654L, 19014146L, 134463904L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 33554450L, 2359392L, 48L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 8797166764034L, 8192L, 160L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 2L, 2097216L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 2L, 2097152L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { -9223363102249058302L, 16916482L, 32L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { -3314041397815784318L, 19014146L, 138396064L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 0L, 262176L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { 1073741826L, 8192L, 32L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = { -9223363102249058302L, 19013634L, 32L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = { -9223363239688011774L, 16916482L, 32L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = { 8797166764034L, 16916482L, 32L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	private static final long[] mk_tokenSet_28() {
		long[] data = { 8797166764034L, 139266L, 32L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
	private static final long[] mk_tokenSet_29() {
		long[] data = { 8797166764034L, 139264L, 32L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_29 = new BitSet(mk_tokenSet_29());
	private static final long[] mk_tokenSet_30() {
		long[] data = { 34359738368L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_30 = new BitSet(mk_tokenSet_30());
	private static final long[] mk_tokenSet_31() {
		long[] data = { 34359738368L, 0L, 128L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_31 = new BitSet(mk_tokenSet_31());
	private static final long[] mk_tokenSet_32() {
		long[] data = { -8070441288400368638L, 19014146L, 134267040L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_32 = new BitSet(mk_tokenSet_32());
	private static final long[] mk_tokenSet_33() {
		long[] data = { -8070441597642211326L, 19013634L, 160L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_33 = new BitSet(mk_tokenSet_33());
	private static final long[] mk_tokenSet_34() {
		long[] data = { -8052074187179687934L, 19013650L, 160L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_34 = new BitSet(mk_tokenSet_34());
	private static final long[] mk_tokenSet_35() {
		long[] data = { 70368744177664L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_35 = new BitSet(mk_tokenSet_35());
	private static final long[] mk_tokenSet_36() {
		long[] data = { -8070441597642211326L, 19014146L, 160L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_36 = new BitSet(mk_tokenSet_36());
	private static final long[] mk_tokenSet_37() {
		long[] data = { 70368744177664L, 1L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_37 = new BitSet(mk_tokenSet_37());
	private static final long[] mk_tokenSet_38() {
		long[] data = { 8797166764034L, 16916482L, 160L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_38 = new BitSet(mk_tokenSet_38());
	private static final long[] mk_tokenSet_39() {
		long[] data = { -3458755269972980734L, 19014146L, 134267040L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_39 = new BitSet(mk_tokenSet_39());
	private static final long[] mk_tokenSet_40() {
		long[] data = { -3458755269972980606L, 19014146L, 134267040L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_40 = new BitSet(mk_tokenSet_40());
	private static final long[] mk_tokenSet_41() {
		long[] data = { 35184372088832L, 0L, 196864L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_41 = new BitSet(mk_tokenSet_41());
	private static final long[] mk_tokenSet_42() {
		long[] data = { -3458720085600891774L, 19014146L, 134463904L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_42 = new BitSet(mk_tokenSet_42());
	private static final long[] mk_tokenSet_43() {
		long[] data = { -3314041397815784318L, 19014146L, 142590368L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_43 = new BitSet(mk_tokenSet_43());
	private static final long[] mk_tokenSet_44() {
		long[] data = { -3314041397815784318L, 19014146L, 167756192L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_44 = new BitSet(mk_tokenSet_44());
	private static final long[] mk_tokenSet_45() {
		long[] data = { -3314041397480239998L, 20062722L, 201311136L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_45 = new BitSet(mk_tokenSet_45());
	private static final long[] mk_tokenSet_46() {
		long[] data = { 291608086610060064L, 3328L, 1635792990L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_46 = new BitSet(mk_tokenSet_46());
	private static final long[] mk_tokenSet_47() {
		long[] data = { 335544320L, 1048576L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_47 = new BitSet(mk_tokenSet_47());
	private static final long[] mk_tokenSet_48() {
		long[] data = { 268435456L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_48 = new BitSet(mk_tokenSet_48());
	private static final long[] mk_tokenSet_49() {
		long[] data = { -3314041397480239998L, 20062722L, 201311152L, 0L, 0L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_49 = new BitSet(mk_tokenSet_49());
	
	}
