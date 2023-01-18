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


public interface SPARQLTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int ADD = 4;
	int ALL = 5;
	int ALTER = 6;
	int AND = 7;
	int ANY = 8;
	int ARRAY = 9;
	int AS = 10;
	int ASCENDING = 11;
	int ATTRIBUTE = 12;
	int AVG = 13;
	int BETWEEN = 14;
	int BOOLEAN = 15;
	int REAL = 16;
	int CASE = 17;
	int COUNT = 18;
	int CREATE = 19;
	int CROSS = 20;
	int DELETE = 21;
	int DESCENDING = 22;
	int DESCRIPTOR = 23;
	int DISTINCT = 24;
	int DROP = 25;
	int ELSE = 26;
	int EN = 27;
	int END = 28;
	int ENTITY = 29;
	int EXCEPT = 30;
	int EXISTS = 31;
	int EXTENT = 32;
	int FALSE = 33;
	int FR = 34;
	int FROM = 35;
	int FULL = 36;
	int GROUP = 37;
	int HAVING = 38;
	int IN = 39;
	int INNER = 40;
	int INSERT = 41;
	int INT = 42;
	int INTERSECT = 43;
	int INTO = 44;
	int IS = 45;
	int JOIN = 46;
	int LANGUAGE = 47;
	int LEFT = 48;
	int LIKE = 49;
	int MAX = 50;
	int MIN = 51;
	int MULTILINGUAL = 52;
	int NAMESPACE = 53;
	int NATURAL = 54;
	int NONE = 55;
	int ENUM = 56;
	int NOT = 57;
	int NULL = 58;
	int OF = 59;
	int ON = 60;
	int ONLY = 61;
	int OR = 62;
	int ORDER = 63;
	int OUTER = 64;
	int PREFERRING = 65;
	int PROPERTY = 66;
	int REF = 67;
	int RIGHT = 68;
	int SELECT = 69;
	int SET = 70;
	int STRING = 71;
	int SUM = 72;
	int THEN = 73;
	int TRUE = 74;
	int TYPEOF = 75;
	int UNDER = 76;
	int UNION = 77;
	int UNION_ALL = 78;
	int UNNEST = 79;
	int UPDATE = 80;
	int USING = 81;
	int VALUES = 82;
	int VIEW = 83;
	int WHEN = 84;
	int WHERE = 85;
	int URI = 86;
	int COUNTTYPE = 87;
	int LIMIT = 88;
	int OFFSET = 89;
	int CASEOF = 90;
	int MAP = 91;
	int CONTEXT = 92;
	int PROPERTIES = 93;
	int CLASS = 94;
	int AGGREGATE = 95;
	int ALIAS = 96;
	int DOT = 97;
	int NAMESPACE_ALIAS = 98;
	int ROW_STAR = 99;
	int EXPR_LIST = 100;
	int IN_LIST = 101;
	int LANGUE_OP = 102;
	int INDEX_OP = 103;
	int IS_NOT_NULL = 104;
	int IS_NULL = 105;
	int IS_NOT_OF = 106;
	int IS_OF = 107;
	int METHOD_CALL = 108;
	int NOT_BETWEEN = 109;
	int CASE2 = 110;
	int NOT_IN = 111;
	int NOT_LIKE = 112;
	int ORDER_ELEMENT = 113;
	int QUERY = 114;
	int RANGE = 115;
	int PROPERTY_DEF = 116;
	int ATTRIBUTE_DEF = 117;
	int ATTRIBUTES = 118;
	int SELECT_FROM = 119;
	int UNARY_MINUS = 120;
	int UNARY_PLUS = 121;
	int VECTOR_EXPR = 122;
	int IDENT = 123;
	int DATATYPE = 124;
	int PREDEFINED_TYPE = 125;
	int ARRAY_DEF = 126;
	int MAPPED_PROPERTIES = 127;
	int CONSTANT = 128;
	int NUM_DOUBLE = 129;
	int NUM_FLOAT = 130;
	int NUM_LONG = 131;
	int OPEN = 132;
	int CLOSE = 133;
	int ONTOLOGY_MODEL_ID = 134;
	int COMMA = 135;
	int EQ = 136;
	int STAR = 137;
	int LITERAL_by = 138;
	int NUM_INT = 139;
	int NAME_ID = 140;
	int QUOTED_STRING = 141;
	int LITERAL_ascending = 142;
	int LITERAL_descending = 143;
	int NE = 144;
	int SQL_NE = 145;
	int LT = 146;
	int GT = 147;
	int LE = 148;
	int GE = 149;
	int CONCAT = 150;
	int PLUS = 151;
	int MINUS = 152;
	int DIV = 153;
	int OPEN_BRACKET = 154;
	int CLOSE_BRACKET = 155;
	int COLON = 156;
	int INTERNAL_ID = 157;
	int EXTERNAL_ID = 158;
	int ID_START_LETTER = 159;
	int ID_LETTER = 160;
	int DOUBLE_QUOTED_STRING = 161;
	int ESCdqs = 162;
	int ESCqs = 163;
	int WS = 164;
	int HEX_DIGIT = 165;
	int EXPONENT = 166;
	int FLOAT_SUFFIX = 167;
	int PREFIX = 168;
	int OPTIONAL = 169;
	int BOUND = 170;
	int FILTER = 171;
	int TYPE = 172;
	int TRIPLE_TYPE = 173;
	int TRIPLE_PROP = 174;
	int UNION_LEFT = 175;
	int UNION_RIGHT = 176;
	int SELECT_WHERE = 177;
	int QNAME = 178;
	int Q_IRI_REF = 179;
	int VAR = 180;
	int OPEN_CURLY = 181;
	int CLOSE_CURLY = 182;
	int INTEGER = 183;
	int STRING_LITERAL1 = 184;
	int UNDERSCORE = 185;
	int QUESTION_MARK = 186;
	int DOLLAR = 187;
	int NCNAME_PREFIX = 188;
	int NCNAME = 189;
	int NCCHAR = 190;
	int VARNAME = 191;
	int NCCHAR1 = 192;
	int NCCHAR1p = 193;
}
