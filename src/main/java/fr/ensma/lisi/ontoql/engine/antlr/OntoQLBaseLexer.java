// $ANTLR 2.7.7 (20060906): "OntoQL-syntaxique.g" -> "OntoQLBaseLexer.java"$


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
 * OntoQL Lexer
 * <br>
 * This lexer provides the OntoQL parser with tokens when a reference language is defined
 * @author Stephane Jean
 */
public class OntoQLBaseLexer extends antlr.CharScanner implements OntoQLTokenTypes, TokenStream
 {
public OntoQLBaseLexer(InputStream in) {
	this(new ByteBuffer(in));
}
public OntoQLBaseLexer(Reader in) {
	this(new CharBuffer(in));
}
public OntoQLBaseLexer(InputBuffer ib) {
	this(new LexerSharedInputState(ib));
}
public OntoQLBaseLexer(LexerSharedInputState state) {
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
				case '=':
				{
					mEQ(true);
					theRetToken=_returnToken;
					break;
				}
				case ',':
				{
					mCOMMA(true);
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
				case '[':
				{
					mOPEN_BRACKET(true);
					theRetToken=_returnToken;
					break;
				}
				case ']':
				{
					mCLOSE_BRACKET(true);
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
				case ':':
				{
					mCOLON(true);
					theRetToken=_returnToken;
					break;
				}
				case '|':
				{
					mCONCAT(true);
					theRetToken=_returnToken;
					break;
				}
				case '@':
				{
					mEXTERNAL_ID(true);
					theRetToken=_returnToken;
					break;
				}
				case '#':
				{
					mONTOLOGY_MODEL_ID(true);
					theRetToken=_returnToken;
					break;
				}
				case '\'':
				{
					mQUOTED_STRING(true);
					theRetToken=_returnToken;
					break;
				}
				case '\t':  case '\n':  case '\r':  case ' ':
				{
					mWS(true);
					theRetToken=_returnToken;
					break;
				}
				case '.':  case '0':  case '1':  case '2':
				case '3':  case '4':  case '5':  case '6':
				case '7':  case '8':  case '9':
				{
					mNUM_INT(true);
					theRetToken=_returnToken;
					break;
				}
				default:
					if ((LA(1)=='<') && (LA(2)=='>')) {
						mSQL_NE(true);
						theRetToken=_returnToken;
					}
					else if ((LA(1)=='!'||LA(1)=='^') && (LA(2)=='=')) {
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
					else if ((LA(1)=='!') && ((LA(2) >= '0' && LA(2) <= '9'))) {
						mINTERNAL_ID(true);
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
					else if ((_tokenSet_0.member(LA(1)))) {
						mNAME_ID(true);
						theRetToken=_returnToken;
					}
				else {
					if (LA(1)==EOF_CHAR) {uponEOF(); _returnToken = makeToken(Token.EOF_TYPE);}
				else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
				}
				}
				if ( _returnToken==null ) continue tryAgain; // found SKIP token
				_ttype = _returnToken.getType();
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
	
	public final void mSQL_NE(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = SQL_NE;
		int _saveIndex;
		
		match("<>");
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
		
		switch ( LA(1)) {
		case '!':
		{
			match("!=");
			break;
		}
		case '^':
		{
			match("^=");
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
	
	public final void mCOMMA(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = COMMA;
		int _saveIndex;
		
		match(',');
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
		
		match('(');
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
		
		match(')');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mOPEN_BRACKET(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = OPEN_BRACKET;
		int _saveIndex;
		
		match('[');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mCLOSE_BRACKET(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CLOSE_BRACKET;
		int _saveIndex;
		
		match(']');
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
	
	public final void mCOLON(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
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
	
	public final void mCONCAT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = CONCAT;
		int _saveIndex;
		
		match("||");
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mID_START_LETTER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ID_START_LETTER;
		int _saveIndex;
		
		switch ( LA(1)) {
		case '_':
		{
			match('_');
			break;
		}
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':  case 'g':  case 'h':
		case 'i':  case 'j':  case 'k':  case 'l':
		case 'm':  case 'n':  case 'o':  case 'p':
		case 'q':  case 'r':  case 's':  case 't':
		case 'u':  case 'v':  case 'w':  case 'x':
		case 'y':  case 'z':
		{
			matchRange('a','z');
			break;
		}
		default:
			if (((LA(1) >= '\u0080' && LA(1) <= '\ufffe'))) {
				matchRange('\u0080','\ufffe');
			}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mID_LETTER(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ID_LETTER;
		int _saveIndex;
		
		switch ( LA(1)) {
		case '$':
		{
			match('$');
			break;
		}
		case '0':  case '1':  case '2':  case '3':
		case '4':  case '5':  case '6':  case '7':
		case '8':  case '9':
		{
			matchRange('0','9');
			break;
		}
		default:
			if ((_tokenSet_1.member(LA(1)))) {
				mID_START_LETTER(false);
			}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mINTERNAL_ID(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = INTERNAL_ID;
		int _saveIndex;
		
		match('!');
		{
		int _cnt305=0;
		_loop305:
		do {
			if (((LA(1) >= '0' && LA(1) <= '9'))) {
				matchRange('0','9');
			}
			else {
				if ( _cnt305>=1 ) { break _loop305; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt305++;
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mEXTERNAL_ID(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EXTERNAL_ID;
		int _saveIndex;
		
		match('@');
		{
		int _cnt308=0;
		_loop308:
		do {
			switch ( LA(1)) {
			case 'a':  case 'b':  case 'c':  case 'd':
			case 'e':  case 'f':  case 'g':  case 'h':
			case 'i':  case 'j':  case 'k':  case 'l':
			case 'm':  case 'n':  case 'o':  case 'p':
			case 'q':  case 'r':  case 's':  case 't':
			case 'u':  case 'v':  case 'w':  case 'x':
			case 'y':  case 'z':
			{
				matchRange('a','z');
				break;
			}
			case '0':  case '1':  case '2':  case '3':
			case '4':  case '5':  case '6':  case '7':
			case '8':  case '9':
			{
				matchRange('0','9');
				break;
			}
			case '_':
			{
				match('_');
				break;
			}
			case '$':
			{
				match('$');
				break;
			}
			default:
			{
				if ( _cnt308>=1 ) { break _loop308; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			}
			_cnt308++;
		} while (true);
		}
		{
		if ((LA(1)=='-')) {
			mMINUS(false);
			{
			_loop311:
			do {
				if (((LA(1) >= '0' && LA(1) <= '9'))) {
					matchRange('0','9');
				}
				else {
					break _loop311;
				}
				
			} while (true);
			}
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
	
	public final void mNAME_ID(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NAME_ID;
		int _saveIndex;
		
		if ((_tokenSet_1.member(LA(1)))) {
			mID_START_LETTER(false);
			{
			_loop314:
			do {
				if ((_tokenSet_2.member(LA(1)))) {
					mID_LETTER(false);
				}
				else {
					break _loop314;
				}
				
			} while (true);
			}
		}
		else if ((LA(1)=='"')) {
			mDOUBLE_QUOTED_STRING(false);
		}
		else {
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		
		_ttype = testLiteralsTable(_ttype);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mDOUBLE_QUOTED_STRING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = DOUBLE_QUOTED_STRING;
		int _saveIndex;
		
		match('\"');
		{
		_loop320:
		do {
			boolean synPredMatched319 = false;
			if (((LA(1)=='\''))) {
				int _m319 = mark();
				synPredMatched319 = true;
				inputState.guessing++;
				try {
					{
					mESCdqs(false);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched319 = false;
				}
				rewind(_m319);
inputState.guessing--;
			}
			if ( synPredMatched319 ) {
				mESCdqs(false);
			}
			else if ((_tokenSet_3.member(LA(1)))) {
				matchNot('\"');
			}
			else {
				break _loop320;
			}
			
		} while (true);
		}
		match('\"');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mONTOLOGY_MODEL_ID(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ONTOLOGY_MODEL_ID;
		int _saveIndex;
		
		match('#');
		mNAME_ID(false);
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mESCdqs(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = ESCdqs;
		int _saveIndex;
		
		match('\'');
		match('\'');
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	public final void mQUOTED_STRING(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = QUOTED_STRING;
		int _saveIndex;
		
		match('\'');
		{
		_loop326:
		do {
			boolean synPredMatched325 = false;
			if (((LA(1)=='\'') && (LA(2)=='\''))) {
				int _m325 = mark();
				synPredMatched325 = true;
				inputState.guessing++;
				try {
					{
					mESCqs(false);
					}
				}
				catch (RecognitionException pe) {
					synPredMatched325 = false;
				}
				rewind(_m325);
inputState.guessing--;
			}
			if ( synPredMatched325 ) {
				mESCqs(false);
			}
			else if ((_tokenSet_4.member(LA(1)))) {
				matchNot('\'');
			}
			else {
				break _loop326;
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
	
	public final void mNUM_INT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = NUM_INT;
		int _saveIndex;
		Token f1=null;
		Token f2=null;
		Token f3=null;
		Token f4=null;
		boolean isDecimal=false; Token t=null;
		
		switch ( LA(1)) {
		case '.':
		{
			match('.');
			if ( inputState.guessing==0 ) {
				_ttype = DOT;
			}
			{
			if (((LA(1) >= '0' && LA(1) <= '9'))) {
				{
				int _cnt333=0;
				_loop333:
				do {
					if (((LA(1) >= '0' && LA(1) <= '9'))) {
						matchRange('0','9');
					}
					else {
						if ( _cnt333>=1 ) { break _loop333; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
					}
					
					_cnt333++;
				} while (true);
				}
				{
				if ((LA(1)=='e')) {
					mEXPONENT(false);
				}
				else {
				}
				
				}
				{
				if ((LA(1)=='d'||LA(1)=='f')) {
					mFLOAT_SUFFIX(true);
					f1=_returnToken;
					if ( inputState.guessing==0 ) {
						t=f1;
					}
				}
				else {
				}
				
				}
				if ( inputState.guessing==0 ) {
					
										if (t != null && t.getText().toUpperCase().indexOf('F')>=0)
										{
											_ttype = NUM_FLOAT;
										}
										else
										{
											_ttype = NUM_DOUBLE; // assume double
										}
									
				}
			}
			else {
			}
			
			}
			break;
		}
		case '0':  case '1':  case '2':  case '3':
		case '4':  case '5':  case '6':  case '7':
		case '8':  case '9':
		{
			{
			switch ( LA(1)) {
			case '0':
			{
				match('0');
				if ( inputState.guessing==0 ) {
					isDecimal = true;
				}
				{
				switch ( LA(1)) {
				case 'x':
				{
					{
					match('x');
					}
					{
					int _cnt340=0;
					_loop340:
					do {
						if ((_tokenSet_5.member(LA(1))) && (true) && (true)) {
							mHEX_DIGIT(false);
						}
						else {
							if ( _cnt340>=1 ) { break _loop340; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
						}
						
						_cnt340++;
					} while (true);
					}
					break;
				}
				case '0':  case '1':  case '2':  case '3':
				case '4':  case '5':  case '6':  case '7':
				{
					{
					int _cnt342=0;
					_loop342:
					do {
						if (((LA(1) >= '0' && LA(1) <= '7'))) {
							matchRange('0','7');
						}
						else {
							if ( _cnt342>=1 ) { break _loop342; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
						}
						
						_cnt342++;
					} while (true);
					}
					break;
				}
				default:
					{
					}
				}
				}
				break;
			}
			case '1':  case '2':  case '3':  case '4':
			case '5':  case '6':  case '7':  case '8':
			case '9':
			{
				{
				matchRange('1','9');
				}
				{
				_loop345:
				do {
					if (((LA(1) >= '0' && LA(1) <= '9'))) {
						matchRange('0','9');
					}
					else {
						break _loop345;
					}
					
				} while (true);
				}
				if ( inputState.guessing==0 ) {
					isDecimal=true;
				}
				break;
			}
			default:
			{
				throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
			}
			}
			}
			{
			if ((LA(1)=='l')) {
				{
				match('l');
				}
				if ( inputState.guessing==0 ) {
					_ttype = NUM_LONG;
				}
			}
			else if (((_tokenSet_6.member(LA(1))))&&(isDecimal)) {
				{
				switch ( LA(1)) {
				case '.':
				{
					match('.');
					{
					_loop350:
					do {
						if (((LA(1) >= '0' && LA(1) <= '9'))) {
							matchRange('0','9');
						}
						else {
							break _loop350;
						}
						
					} while (true);
					}
					{
					if ((LA(1)=='e')) {
						mEXPONENT(false);
					}
					else {
					}
					
					}
					{
					if ((LA(1)=='d'||LA(1)=='f')) {
						mFLOAT_SUFFIX(true);
						f2=_returnToken;
						if ( inputState.guessing==0 ) {
							t=f2;
						}
					}
					else {
					}
					
					}
					break;
				}
				case 'e':
				{
					mEXPONENT(false);
					{
					if ((LA(1)=='d'||LA(1)=='f')) {
						mFLOAT_SUFFIX(true);
						f3=_returnToken;
						if ( inputState.guessing==0 ) {
							t=f3;
						}
					}
					else {
					}
					
					}
					break;
				}
				case 'd':  case 'f':
				{
					mFLOAT_SUFFIX(true);
					f4=_returnToken;
					if ( inputState.guessing==0 ) {
						t=f4;
					}
					break;
				}
				default:
				{
					throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
				}
				}
				}
				if ( inputState.guessing==0 ) {
					
									if (t != null && t.getText().toUpperCase() .indexOf('F') >= 0)
									{
										_ttype = NUM_FLOAT;
									}
									else
									{
										_ttype = NUM_DOUBLE; // assume double
									}
								
				}
			}
			else {
			}
			
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
	
	protected final void mEXPONENT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = EXPONENT;
		int _saveIndex;
		
		{
		match('e');
		}
		{
		switch ( LA(1)) {
		case '+':
		{
			match('+');
			break;
		}
		case '-':
		{
			match('-');
			break;
		}
		case '0':  case '1':  case '2':  case '3':
		case '4':  case '5':  case '6':  case '7':
		case '8':  case '9':
		{
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		{
		int _cnt360=0;
		_loop360:
		do {
			if (((LA(1) >= '0' && LA(1) <= '9'))) {
				matchRange('0','9');
			}
			else {
				if ( _cnt360>=1 ) { break _loop360; } else {throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());}
			}
			
			_cnt360++;
		} while (true);
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	protected final void mFLOAT_SUFFIX(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = FLOAT_SUFFIX;
		int _saveIndex;
		
		switch ( LA(1)) {
		case 'f':
		{
			match('f');
			break;
		}
		case 'd':
		{
			match('d');
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
	
	protected final void mHEX_DIGIT(boolean _createToken) throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype; Token _token=null; int _begin=text.length();
		_ttype = HEX_DIGIT;
		int _saveIndex;
		
		{
		switch ( LA(1)) {
		case '0':  case '1':  case '2':  case '3':
		case '4':  case '5':  case '6':  case '7':
		case '8':  case '9':
		{
			matchRange('0','9');
			break;
		}
		case 'a':  case 'b':  case 'c':  case 'd':
		case 'e':  case 'f':
		{
			matchRange('a','f');
			break;
		}
		default:
		{
			throw new NoViableAltForCharException((char)LA(1), getFilename(), getLine(), getColumn());
		}
		}
		}
		if ( _createToken && _token==null && _ttype!=Token.SKIP ) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length()-_begin));
		}
		_returnToken = _token;
	}
	
	
	private static final long[] mk_tokenSet_0() {
		long[] data = new long[3072];
		data[0]=17179869184L;
		data[1]=576460745860972544L;
		for (int i = 2; i<=1022; i++) { data[i]=-1L; }
		data[1023]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = new long[3072];
		data[1]=576460745860972544L;
		for (int i = 2; i<=1022; i++) { data[i]=-1L; }
		data[1023]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = new long[3072];
		data[0]=287948969894477824L;
		data[1]=576460745860972544L;
		for (int i = 2; i<=1022; i++) { data[i]=-1L; }
		data[1023]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = new long[2048];
		data[0]=-566935683073L;
		for (int i = 1; i<=1022; i++) { data[i]=-1L; }
		data[1023]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = new long[2048];
		data[0]=-549755813889L;
		for (int i = 1; i<=1022; i++) { data[i]=-1L; }
		data[1023]=9223372036854775807L;
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = new long[1025];
		data[0]=287948901175001088L;
		data[1]=541165879296L;
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = new long[1025];
		data[0]=70368744177664L;
		data[1]=481036337152L;
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	
	}
