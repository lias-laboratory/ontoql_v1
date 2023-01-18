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
package fr.ensma.lisi.ontoql.util;

import antlr.ASTFactory;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.util.ASTUtil;

/**
 * @author Stéphane JEAN
 */
public class SPARQLUtil {

	/**
	 * Return a node corresponding to the #uri attribute
	 * 
	 * @param aliasName  alias of the scope of this attribute
	 * @param astFactory constructor of node
	 * @return a node corresponding to the #uri attribute
	 */
	public static AST getNodeAttributeURI(String aliasName, ASTFactory astFactory) {

		AST res = ASTUtil.create(astFactory, OntoQLTokenTypes.METHOD_CALL, "||");
		AST concatNode = ASTUtil.create(astFactory, OntoQLTokenTypes.IDENT, "concat");
		AST exprListNode = ASTUtil.create(astFactory, OntoQLTokenTypes.EXPR_LIST, "concatList");

		AST dotOWLNamespaceNode = ASTUtil.create(astFactory, OntoQLTokenTypes.DOT, "DOT");
		AST prefixNode = ASTUtil.create(astFactory, OntoQLTokenTypes.IDENT, aliasName);
		dotOWLNamespaceNode.addChild(prefixNode);
		AST OWLNamespaceNode = ASTUtil.create(astFactory, OntoQLTokenTypes.IDENT, "#OWLNamespace");
		dotOWLNamespaceNode.addChild(OWLNamespaceNode);

		AST dotCodeNode = ASTUtil.create(astFactory, OntoQLTokenTypes.DOT, "DOT");
		AST prefixCodeNode = ASTUtil.create(astFactory, OntoQLTokenTypes.IDENT, aliasName);
		dotCodeNode.addChild(prefixCodeNode);
		AST codeNode = ASTUtil.create(astFactory, OntoQLTokenTypes.IDENT, "#code");
		dotCodeNode.addChild(codeNode);

		exprListNode.addChild(dotOWLNamespaceNode);
		exprListNode.addChild(dotCodeNode);
		res.addChild(concatNode);
		res.addChild(exprListNode);

		return res;

	}
}
