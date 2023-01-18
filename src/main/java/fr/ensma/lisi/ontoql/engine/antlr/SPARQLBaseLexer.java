// $ANTLR 2.7.7 (20060906): "SPARQL-syntaxique.g" -> "SPARQLBaseLexer.java"$


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


import java.io.InputStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.ANTLRException;
import java.io.Reader;
import java.util.Hashtable;
import antlr.CharScanner;
import antlr.InputBuffer;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.Token;
import antlr.CommonToken;
import antlr.RecognitionException;
import antlr.NoViableAltForCharException;
import antlr.MismatchedCharException;
import antlr.TokenStream;
import antlr.ANTLRHashString;
import antlr.LexerSharedInputState;
import antlr.collections.impl.BitSet;
import antlr.SemanticException;

/**
 * SPARQL Lexer
 * <br>
 * This lexer provides the SPARQL parser with tokens
 * @author Stephane Jean
 */
public class SPARQLBaseLexer extends antlr.CharScanner implements SPARQLTokenTypes, TokenStream
 {
public SPARQLBaseLexer(InputStream in) {
	this(new ByteBuffer(in));
}
public SPARQLBaseLexer(Reader in) {
	this(new CharBuffer(in));
}
public SPARQLBaseLexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public SPARQLBaseLexer(LexerSharedInputState state) {
	super(state);
	caseSensitiveLiterals = false;
	setCaseSensitive(false);
	literals = new Hashtable();
	literals.put(new ANTLRHashString("uriType", this), new Integer(86));
	literals.put(new ANTLRHashString("between", this), new Integer(14));
	literals.put(new ANTLRHashString("namespace", this), new Integer(53));
	literals.put(new ANTLRHashString("case", this), new Integer(17));
	literals.put(new ANTLRHashString("delete", this), new Integer(21));
	literals.put(new ANTLRHashString("end", this), new Integer(28));
	literals.put(new ANTLRHashString("fr", this), new Integer(34));
	literals.put(new ANTLRHashString("view", this), new Integer(83));
	literals.put(new ANTLRHashString("language", this), new Integer(47));
	literals.put(new ANTLRHashString("limit", this), new Integer(88));
	literals.put(new ANTLRHashString("insert", this), new Integer(41));
	literals.put(new ANTLRHashString("distinct", this), new Integer(24));
	literals.put(new ANTLRHashString("where", this), new Integer(85));
	literals.put(new ANTLRHashString("alter", this), new Integer(6));
	literals.put(new ANTLRHashString("then", this), new Integer(73));
	literals.put(new ANTLRHashString("typeof", this), new Integer(75));
	literals.put(new ANTLRHashString("select", this), new Integer(69));
	literals.put(new ANTLRHashString("and", this), new Integer(7));
	literals.put(new ANTLRHashString("outer", this), new Integer(64));
	literals.put(new ANTLRHashString("not", this), new Integer(57));
	literals.put(new ANTLRHashString("context", this), new Integer(92));
	literals.put(new ANTLRHashString("using", this), new Integer(81));
	literals.put(new ANTLRHashString("offset", this), new Integer(89));
	literals.put(new ANTLRHashString("from", this), new Integer(35));
	literals.put(new ANTLRHashString("under", this), new Integer(76));
	literals.put(new ANTLRHashString("null", this), new Integer(58));
	literals.put(new ANTLRHashString("real", this), new Integer(16));
	literals.put(new ANTLRHashString("count", this), new Integer(18));
	literals.put(new ANTLRHashString("add", this), new Integer(4));
	literals.put(new ANTLRHashString("like", this), new Integer(49));
	literals.put(new ANTLRHashString("ref", this), new Integer(67));
	literals.put(new ANTLRHashString("natural", this), new Integer(54));
	literals.put(new ANTLRHashString("when", this), new Integer(84));
	literals.put(new ANTLRHashString("class", this), new Integer(94));
	literals.put(new ANTLRHashString("inner", this), new Integer(40));
	literals.put(new ANTLRHashString("preferring", this), new Integer(65));
	literals.put(new ANTLRHashString("except", this), new Integer(30));
	literals.put(new ANTLRHashString("entity", this), new Integer(29));
	literals.put(new ANTLRHashString("set", this), new Integer(70));
	literals.put(new ANTLRHashString("countType", this), new Integer(87));
	literals.put(new ANTLRHashString("rdf:type", this), new Integer(172));
	literals.put(new ANTLRHashString("only", this), new Integer(61));
	literals.put(new ANTLRHashString("intersect", this), new Integer(43));
	literals.put(new ANTLRHashString("map", this), new Integer(91));
	literals.put(new ANTLRHashString("join", this), new Integer(46));
	literals.put(new ANTLRHashString("of", this), new Integer(59));
	literals.put(new ANTLRHashString("is", this), new Integer(45));
	literals.put(new ANTLRHashString("array", this), new Integer(9));
	literals.put(new ANTLRHashString("or", this), new Integer(62));
	literals.put(new ANTLRHashString("any", this), new Integer(8));
	literals.put(new ANTLRHashString("create", this), new Integer(19));
	literals.put(new ANTLRHashString("none", this), new Integer(55));
	literals.put(new ANTLRHashString("full", this), new Integer(36));
	literals.put(new ANTLRHashString("min", this), new Integer(51));
	literals.put(new ANTLRHashString("as", this), new Integer(10));
	literals.put(new ANTLRHashString("by", this), new Integer(138));
	literals.put(new ANTLRHashString("extent", this), new Integer(32));
	literals.put(new ANTLRHashString("all", this), new Integer(5));
	literals.put(new ANTLRHashString("union", this), new Integer(77));
	literals.put(new ANTLRHashString("caseof", this), new Integer(90));
	literals.put(new ANTLRHashString("drop", this), new Integer(25));
	literals.put(new ANTLRHashString("order", this), new Integer(63));
	literals.put(new ANTLRHashString("properties", this), new Integer(93));
	literals.put(new ANTLRHashString("values", this), new Integer(82));
	literals.put(new ANTLRHashString("enum", this), new Integer(56));
	literals.put(new ANTLRHashString("int", this), new Integer(42));
	literals.put(new ANTLRHashString("descriptor", this), new Integer(23));
	literals.put(new ANTLRHashString("ascending", this), new Integer(142));
	literals.put(new ANTLRHashString("boolean", this), new Integer(15));
	literals.put(new ANTLRHashString("cross", this), new Integer(20));
	literals.put(new ANTLRHashString("union all", this), new Integer(78));
	literals.put(new ANTLRHashString("string", this), new Integer(71));
	literals.put(new ANTLRHashString("descending", this), new Integer(143));
	literals.put(new ANTLRHashString("false", this), new Integer(33));
	literals.put(new ANTLRHashString("exists", this), new Integer(31));
	literals.put(new ANTLRHashString("unnest", this), new Integer(79));
	literals.put(new ANTLRHashString("asc", this), new Integer(11));
	literals.put(new ANTLRHashString("property", this), new Integer(66));
	literals.put(new ANTLRHashString("left", this), new Integer(48));
	literals.put(new ANTLRHashString("desc", this), new Integer(22));
	literals.put(new ANTLRHashString("multilingual", this), new Integer(52));
	literals.put(new ANTLRHashString("max", this), new Integer(50));
	literals.put(new ANTLRHashString("attribute", this), new Integer(12));
	literals.put(new ANTLRHashString("sum", this), new Integer(72));
	literals.put(new ANTLRHashString("on", this), new Integer(60));
	literals.put(new ANTLRHashString("into", this), new Integer(44));
	literals.put(new ANTLRHashString("else", this), new Integer(26));
	literals.put(new ANTLRHashString("right", this), new Integer(68));
	literals.put(new ANTLRHashString("in", this), new Integer(39));
	literals.put(new ANTLRHashString("avg", this), new Integer(13));
	literals.put(new ANTLRHashString("update", this), new Integer(80));
	literals.put(new ANTLRHashString("true", this), new Integer(74));
	literals.put(new ANTLRHashString("en", this), new Integer(27));
	literals.put(new ANTLRHashString("group", this), new Integer(37));
	literals.put(new ANTLRHashString("having", this), new Integer(38));
}

public Token nextToken() throws TokenStreamException {
	Token theRetToken=null;
tryAgain:
	for (;;) {
		Token _token = null;
		int _ttype = Token.INVALID_TYPE;
		resetText();
		try {   // for char stream error handling
			try {   // for lexical error handling
				switch ( LA(1)) {
				case '{':
				{
					mOPEN_CURLY(true);
					theRetToken=_returnToken;
					break;
				}
				case '}':
				{
					mCLOSE_CURLY(true);
					theRetToken=_returnToken;
					break;
				}
				case '(':
				{
					mOPEN(true);
					theRetToken=_returnToken;
					break;
				}
				case ')':
				{
					mCLOSE(true);
					theRetToken=_returnToken;
					break;
				}
				case '.':
				{
					mDOT(true);
					theRetToken=_returnToken;
					break;
				}
				case '|':
				{
					mOR(true);
					theRetToken=_returnToken;
					break;
				}
				case '&':
				{
					mAND(true);
					theRetToken=_returnToken;
					break;
				}
				case '+':
				{
					mPLUS(true);
					theRetToken=_returnToken;
					break;
				}
				case '-':
				{
					mMINUS(true);
					theRetToken=_returnToken;
					break;
				}
				case '*':
				{
					mSTAR(true);
					theRetToken=_returnToken;
					break;
				}
				case '/':
				{
					mDIV(true);
					theRetToken=_returnToken;
					break;
				}
				case '=':
				{
					mEQ(true);
					theRetToken=_returnToken;
					break;
				}
				case '0':  case '1':  case '2':  case '3':
				case '4':  case '5':  case '6':  case '7':
				case '8':  case '9':
				{
					mINTEGER(true);
					theRetToken=_returnToken;
					break;
				}
				case '\'':
				{
					mSTRING_LITERAL1(true);
					theRetToken=_returnToken;
					break;
				}
				case ':':  case 'a':  case 'b':  case 'c':
				case 'd':  case 'e':  case 'f':  case 'g':
				case 'h':  case 'i':  case 'j':  case 'k':
				case 'l':  case 'm':  case 'n':  case 'o':
				case 'p':  case 'q':  case 'r':  case 's':
				case 't':  case 'u':  case 'v':  case 'w':
				case 'x':  case 'y':  case 'z':
				{
					mQNAME(true);
					theRetToken=_returnToken;
					break;
				}
				case '$':  case '?':
				{
					mVAR(true);
					theRetToken=_returnToken;
					break;
				}
				case '\t':  case '\n':  case '\r':  case ' ':
				{
					mWS(true);
					theRetToken=_returnToken;
					break;
				}
				default:
					if ((LA(1)=='!') && (LA(2)=='=')) {
						mNE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='<') && (LA(2)=='=')) {
						mLE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='>') && (LA(2)=='=')) {
						mGE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='<') && (_tokenSet_0.member(LA(2)))) {
						mQ_IRI_REF(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='<') && (true)) {
						mLT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='>') && (true)) {
						mGT(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='!') && (true)) {
						mNOT(true);
						theRetToken=_returnToken;
					}
				else {
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				}
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
				_ttype = testLiteralsTable(_ttype);
				_returnToken.setType(_ttype);
				return _returnToken;
			}
			catch (RecognitionException e) {
				throw new TokenStreamRecognitionException(e);
			}
		}
		catch (CharStreamException cse) {
			if ( cse instanceof CharStreamIOException ) {
				throw new TokenStreamIOException(((CharStreamIOException)cse).io);
			}
			else {
				throw new TokenStreamException(cse.getMessage());
			}
		}
	}
}

	public final void mOPEN_CURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = OPEN_CURLY;
		int _saveIndex;
		
		match("{");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCLOSE_CURLY(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CLOSE_CURLY;
		int _saveIndex;
		
		match("}");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mOPEN(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = OPEN;
		int _saveIndex;
		
		match("(");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCLOSE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CLOSE;
		int _saveIndex;
		
		match(")");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DOT;
		int _saveIndex;
		
		match(".");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mOR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = OR;
		int _saveIndex;
		
		match("||");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mAND(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = AND;
		int _saveIndex;
		
		match("&&");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mPLUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = PLUS;
		int _saveIndex;
		
		match('+');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mMINUS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = MINUS;
		int _saveIndex;
		
		match('-');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSTAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STAR;
		int _saveIndex;
		
		match('*');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mDIV(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DIV;
		int _saveIndex;
		
		match('/');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mEQ(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EQ;
		int _saveIndex;
		
		match('=');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LT;
		int _saveIndex;
		
		match('<');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mGT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = GT;
		int _saveIndex;
		
		match('>');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mNE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NE;
		int _saveIndex;
		
		match("!=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mNOT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NOT;
		int _saveIndex;
		
		match("!");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mLE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = LE;
		int _saveIndex;
		
		match("<=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mGE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = GE;
		int _saveIndex;
		
		match(">=");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mUNDERSCORE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = UNDERSCORE;
		int _saveIndex;
		
		match('_');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mCOLON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COLON;
		int _saveIndex;
		
		match(":");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mQUESTION_MARK(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = QUESTION_MARK;
		int _saveIndex;
		
		match("?");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mDOLLAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DOLLAR;
		int _saveIndex;
		
		match("$");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mINTEGER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = INTEGER;
		int _saveIndex;
		
		{
		int _cnt96=0;
		_loop96:
		do {
			if (((LA(1) >= '0' && LA(1) <= '9'))) {
				matchRange('0','9');
			}
			else {
				if ( _cnt96>=1 ) { break _loop96; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt96++;
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mSTRING_LITERAL1(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = STRING_LITERAL1;
		int _saveIndex;
		
		match('\'');
		{
		_loop101:
		do {
			boolean synPredMatched100 = false;
			if (((LA(1)=='\'') && (LA(2)=='\''))) {
				int _m100 = mark();
				synPredMatched100 = true;
				inputState.guessing++;
				try {
					{
					mESCqs(false);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched100 = false;
				}
				rewind(_m100);
inputState.guessing--;
			}
			if ( synPredMatched100 ) {
				mESCqs(false);
			}
			else if ((_tokenSet_1.member(LA(1)))) {
				matchNot('\'');
			}
			else {
				break _loop101;
			}
			
		} while (true);
		}
		match('\'');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mESCqs(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ESCqs;
		int _saveIndex;
		
		match('\'');
		match('\'');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mQ_IRI_REF(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = Q_IRI_REF;
		int _saveIndex;
		
		_saveIndex=text.length();
		mLT(false);
		text.setLength(_saveIndex);
		{
		int _cnt106=0;
		_loop106:
		do {
			if ((_tokenSet_0.member(LA(1)))) {
				{
				match(_tokenSet_0);
				}
			}
			else {
				if ( _cnt106>=1 ) { break _loop106; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt106++;
		} while (true);
		}
		_saveIndex=text.length();
		mGT(false);
		text.setLength(_saveIndex);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mQNAME(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = QNAME;
		int _saveIndex;
		
		boolean synPredMatched114 = false;
		if (((LA(1)=='p') && (LA(2)=='r'))) {
			int _m114 = mark();
			synPredMatched114 = true;
			inputState.guessing++;
			try {
				{
				match("prefix");
				}
			}
			catch (RecognitionException pe) {
				synPredMatched114 = false;
			}
			rewind(_m114);
inputState.guessing--;
		}
		if ( synPredMatched114 ) {
			match("prefix");
			if ( inputState.guessing==0 ) {
				_ttype = PREFIX;
			}
		}
		else {
			boolean synPredMatched116 = false;
			if (((LA(1)=='d') && (LA(2)=='i'))) {
				int _m116 = mark();
				synPredMatched116 = true;
				inputState.guessing++;
				try {
					{
					match("distinct");
					}
				}
				catch (RecognitionException pe) {
					synPredMatched116 = false;
				}
				rewind(_m116);
inputState.guessing--;
			}
			if ( synPredMatched116 ) {
				match("distinct");
				if ( inputState.guessing==0 ) {
					_ttype = DISTINCT;
				}
			}
			else {
				boolean synPredMatched118 = false;
				if (((LA(1)=='o') && (LA(2)=='p'))) {
					int _m118 = mark();
					synPredMatched118 = true;
					inputState.guessing++;
					try {
						{
						match("optional");
						}
					}
					catch (RecognitionException pe) {
						synPredMatched118 = false;
					}
					rewind(_m118);
inputState.guessing--;
				}
				if ( synPredMatched118 ) {
					match("optional");
					if ( inputState.guessing==0 ) {
						_ttype = OPTIONAL;
					}
				}
				else {
					boolean synPredMatched120 = false;
					if (((LA(1)=='b') && (LA(2)=='o'))) {
						int _m120 = mark();
						synPredMatched120 = true;
						inputState.guessing++;
						try {
							{
							match("bound");
							}
						}
						catch (RecognitionException pe) {
							synPredMatched120 = false;
						}
						rewind(_m120);
inputState.guessing--;
					}
					if ( synPredMatched120 ) {
						match("bound");
						if ( inputState.guessing==0 ) {
							_ttype = BOUND;
						}
					}
					else {
						boolean synPredMatched122 = false;
						if (((LA(1)=='f') && (LA(2)=='i'))) {
							int _m122 = mark();
							synPredMatched122 = true;
							inputState.guessing++;
							try {
								{
								match("filter");
								}
							}
							catch (RecognitionException pe) {
								synPredMatched122 = false;
							}
							rewind(_m122);
inputState.guessing--;
						}
						if ( synPredMatched122 ) {
							match("filter");
							if ( inputState.guessing==0 ) {
								_ttype = FILTER;
							}
						}
						else {
							boolean synPredMatched124 = false;
							if (((LA(1)=='o') && (LA(2)=='r'))) {
								int _m124 = mark();
								synPredMatched124 = true;
								inputState.guessing++;
								try {
									{
									match("order");
									}
								}
								catch (RecognitionException pe) {
									synPredMatched124 = false;
								}
								rewind(_m124);
inputState.guessing--;
							}
							if ( synPredMatched124 ) {
								match("order");
								if ( inputState.guessing==0 ) {
									_ttype = ORDER;
								}
							}
							else {
								boolean synPredMatched126 = false;
								if (((LA(1)=='b') && (LA(2)=='y'))) {
									int _m126 = mark();
									synPredMatched126 = true;
									inputState.guessing++;
									try {
										{
										match("by");
										}
									}
									catch (RecognitionException pe) {
										synPredMatched126 = false;
									}
									rewind(_m126);
inputState.guessing--;
								}
								if ( synPredMatched126 ) {
									match("by");
								}
								else {
									boolean synPredMatched128 = false;
									if (((LA(1)=='d') && (LA(2)=='e'))) {
										int _m128 = mark();
										synPredMatched128 = true;
										inputState.guessing++;
										try {
											{
											match("desc");
											}
										}
										catch (RecognitionException pe) {
											synPredMatched128 = false;
										}
										rewind(_m128);
inputState.guessing--;
									}
									if ( synPredMatched128 ) {
										match("desc");
									}
									else {
										boolean synPredMatched130 = false;
										if (((LA(1)=='a') && (LA(2)=='s'))) {
											int _m130 = mark();
											synPredMatched130 = true;
											inputState.guessing++;
											try {
												{
												match("asc");
												}
											}
											catch (RecognitionException pe) {
												synPredMatched130 = false;
											}
											rewind(_m130);
inputState.guessing--;
										}
										if ( synPredMatched130 ) {
											match("asc");
										}
										else {
											boolean synPredMatched132 = false;
											if (((LA(1)=='u') && (LA(2)=='n'))) {
												int _m132 = mark();
												synPredMatched132 = true;
												inputState.guessing++;
												try {
													{
													match("union");
													}
												}
												catch (RecognitionException pe) {
													synPredMatched132 = false;
												}
												rewind(_m132);
inputState.guessing--;
											}
											if ( synPredMatched132 ) {
												match("union");
												if ( inputState.guessing==0 ) {
													_ttype = UNION;
												}
											}
											else {
												boolean synPredMatched134 = false;
												if (((LA(1)=='s') && (LA(2)=='e'))) {
													int _m134 = mark();
													synPredMatched134 = true;
													inputState.guessing++;
													try {
														{
														match("select");
														}
													}
													catch (RecognitionException pe) {
														synPredMatched134 = false;
													}
													rewind(_m134);
inputState.guessing--;
												}
												if ( synPredMatched134 ) {
													match("select");
													if ( inputState.guessing==0 ) {
														_ttype = SELECT;
													}
												}
												else {
													boolean synPredMatched136 = false;
													if (((LA(1)=='w') && (LA(2)=='h'))) {
														int _m136 = mark();
														synPredMatched136 = true;
														inputState.guessing++;
														try {
															{
															match("where");
															}
														}
														catch (RecognitionException pe) {
															synPredMatched136 = false;
														}
														rewind(_m136);
inputState.guessing--;
													}
													if ( synPredMatched136 ) {
														match("where");
														if ( inputState.guessing==0 ) {
															_ttype = WHERE;
														}
													}
													else {
														boolean synPredMatched110 = false;
														if (((_tokenSet_2.member(LA(1))) && (true))) {
															int _m110 = mark();
															synPredMatched110 = true;
															inputState.guessing++;
															try {
																{
																{
																switch ( LA(1)) {
																case 'a':  case 'b':  case 'c':  case 'd':
																case 'e':  case 'f':  case 'g':  case 'h':
																case 'i':  case 'j':  case 'k':  case 'l':
																case 'm':  case 'n':  case 'o':  case 'p':
																case 'q':  case 'r':  case 's':  case 't':
																case 'u':  case 'v':  case 'w':  case 'x':
																case 'y':  case 'z':
																{
																	mNCNAME_PREFIX(false);
																	break;
																}
																case ':':
																{
																	break;
																}
																default:
																{
																	throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
																}
																}
																}
																mCOLON(false);
																}
															}
															catch (RecognitionException pe) {
																synPredMatched110 = false;
															}
															rewind(_m110);
inputState.guessing--;
														}
														if ( synPredMatched110 ) {
															{
															switch ( LA(1)) {
															case 'a':  case 'b':  case 'c':  case 'd':
															case 'e':  case 'f':  case 'g':  case 'h':
															case 'i':  case 'j':  case 'k':  case 'l':
															case 'm':  case 'n':  case 'o':  case 'p':
															case 'q':  case 'r':  case 's':  case 't':
															case 'u':  case 'v':  case 'w':  case 'x':
															case 'y':  case 'z':
															{
																mNCNAME_PREFIX(false);
																break;
															}
															case ':':
															{
																break;
															}
															default:
															{
																throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
															}
															}
															}
															mCOLON(false);
															{
															if ((_tokenSet_3.member(LA(1)))) {
																mNCNAME(false);
															}
															else {
															}
															
															}
														}
														else {
															throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
														}
														}}}}}}}}}}}}
														_ttype = testLiteralsTable(_ttype);
														if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
															_token = makeToken(_ttype);
															_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
														}
														_returnToken = _token;
													}
													
	protected final void mNCNAME_PREFIX(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NCNAME_PREFIX;
		int _saveIndex;
		
		mNCCHAR1p(false);
		{
		switch ( LA(1)) {
		case '.':  case '0':  case '1':  case '2':
		case '3':  case '4':  case '5':  case '6':
		case '7':  case '8':  case '9':  case '_':
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':  case 'g':  case 'h':
		case 'i':  case 'j':  case 'k':  case 'l':
		case 'm':  case 'n':  case 'o':  case 'p':
		case 'q':  case 'r':  case 's':  case 't':
		case 'u':  case 'v':  case 'w':  case 'x':
		case 'y':  case 'z':
		{
			{
			_loop140:
			do {
				if ((_tokenSet_4.member(LA(1))) && (_tokenSet_5.member(LA(2)))) {
					mNCCHAR(false);
				}
				else if ((LA(1)=='.')) {
					mDOT(false);
				}
				else {
					break _loop140;
				}
				
			} while (true);
			}
			mNCCHAR(false);
			break;
		}
		case ':':
		{
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			
					String originalText= new String(text.getBuffer(),_begin,text.length()-_begin);
					if (!originalText.startsWith("rdf"))
						setText("geo");
				
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mNCNAME(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NCNAME;
		int _saveIndex;
		
		mNCCHAR1(false);
		{
		if ((_tokenSet_5.member(LA(1)))) {
			{
			_loop144:
			do {
				if ((_tokenSet_4.member(LA(1))) && (_tokenSet_5.member(LA(2)))) {
					mNCCHAR(false);
				}
				else if ((LA(1)=='.')) {
					mDOT(false);
				}
				else {
					break _loop144;
				}
				
			} while (true);
			}
			mNCCHAR(false);
		}
		else {
		}
		
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mNCCHAR1p(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NCCHAR1p;
		int _saveIndex;
		
		{
		matchRange('a','z');
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mNCCHAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NCCHAR;
		int _saveIndex;
		
		switch ( LA(1)) {
		case '_':  case 'a':  case 'b':  case 'c':
		case 'd':  case 'e':  case 'f':  case 'g':
		case 'h':  case 'i':  case 'j':  case 'k':
		case 'l':  case 'm':  case 'n':  case 'o':
		case 'p':  case 'q':  case 'r':  case 's':
		case 't':  case 'u':  case 'v':  case 'w':
		case 'x':  case 'y':  case 'z':
		{
			mNCCHAR1(false);
			break;
		}
		case '0':  case '1':  case '2':  case '3':
		case '4':  case '5':  case '6':  case '7':
		case '8':  case '9':
		{
			{
			matchRange('0','9');
			}
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mNCCHAR1(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NCCHAR1;
		int _saveIndex;
		
		switch ( LA(1)) {
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':  case 'g':  case 'h':
		case 'i':  case 'j':  case 'k':  case 'l':
		case 'm':  case 'n':  case 'o':  case 'p':
		case 'q':  case 'r':  case 's':  case 't':
		case 'u':  case 'v':  case 'w':  case 'x':
		case 'y':  case 'z':
		{
			mNCCHAR1p(false);
			break;
		}
		case '_':
		{
			mUNDERSCORE(false);
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mVAR(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = VAR;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case '?':
		{
			mQUESTION_MARK(false);
			break;
		}
		case '$':
		{
			mDOLLAR(false);
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		{
		mVARNAME(false);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mVARNAME(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = VARNAME;
		int _saveIndex;
		
		{
		int _cnt153=0;
		_loop153:
		do {
			switch ( LA(1)) {
			case '_':  case 'a':  case 'b':  case 'c':
			case 'd':  case 'e':  case 'f':  case 'g':
			case 'h':  case 'i':  case 'j':  case 'k':
			case 'l':  case 'm':  case 'n':  case 'o':
			case 'p':  case 'q':  case 'r':  case 's':
			case 't':  case 'u':  case 'v':  case 'w':
			case 'x':  case 'y':  case 'z':
			{
				mNCCHAR1(false);
				break;
			}
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				{
				matchRange('0','9');
				}
				break;
			}
			default:
			{
				if ( _cnt153>=1 ) { break _loop153; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			}
			_cnt153++;
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mWS(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = WS;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case ' ':
		{
			match(' ');
			break;
		}
		case '\t':
		{
			match('\t');
			break;
		}
		case '\n':
		{
			match('\n');
			if ( inputState.guessing==0 ) {
				newline();
			}
			break;
		}
		default:
			if ((LA(1)=='\r') && (LA(2)=='\n')) {
				match('\r');
				match('\n');
				if ( inputState.guessing==0 ) {
					newline();
				}
			}
			else if ((LA(1)=='\r') && (true)) {
				match('\r');
				if ( inputState.guessing==0 ) {
					newline();
				}
			}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( inputState.guessing==0 ) {
			_ttype = Token.SKIP;
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[2048];
		data[0]=-8070450553722765313L;
		data[1]=-4035225271761108993L;
		for (int i = 2; i<=1022; i++) { data[i]=-1L; }
		data[1023]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = new long[2048];
		data[0]=-549755813889L;
		for (int i = 1; i<=1022; i++) { data[i]=-1L; }
		data[1023]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = new long[1025];
		data[0]=288230376151711744L;
		data[1]=576460743713488896L;
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = new long[1025];
		data[1]=576460745860972544L;
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = new long[1025];
		data[0]=287948901175001088L;
		data[1]=576460745860972544L;
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = new long[1025];
		data[0]=288019269919178752L;
		data[1]=576460745860972544L;
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	
	}
