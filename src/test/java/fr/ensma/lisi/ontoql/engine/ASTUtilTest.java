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
package fr.ensma.lisi.ontoql.engine;

import org.junit.Assert;
import org.junit.Test;

import antlr.ASTFactory;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.util.ASTUtil;

/**
 * @author Stéphane JEAN
 */
public class ASTUtilTest {

	@Test
	public void testRemoveChild() {
		ASTFactory factory = new ASTFactory();

		AST query = ASTUtil.create(factory, OntoQLTokenTypes.QUERY, "query");
		AST selectFromNodeOutput = ASTUtil.create(factory, OntoQLTokenTypes.SELECT_FROM, "SELECT_FROM");
		AST orderByNode = ASTUtil.create(factory, OntoQLTokenTypes.ORDER, "ORDER");
		AST IdNode = ASTUtil.create(factory, OntoQLTokenTypes.INTERNAL_ID, "1214");
		orderByNode.addChild(IdNode);
		query.addChild(selectFromNodeOutput);
		query.addChild(orderByNode);

		AST fromNodeOutput = ASTUtil.create(factory, OntoQLTokenTypes.FROM, "FROM");
		AST rangeNodeOutput = ASTUtil.create(factory, OntoQLTokenTypes.QUERY, "query");
		AST aliasFromNodeOutput = ASTUtil.create(factory, OntoQLTokenTypes.ALIAS, "q");
		fromNodeOutput.addChild(rangeNodeOutput);
		rangeNodeOutput.addChild(aliasFromNodeOutput);
		selectFromNodeOutput.addChild(fromNodeOutput);

		Assert.assertEquals(" ( query ( SELECT_FROM ( FROM ( query q ) ) ) ( ORDER 1214 ) )", query.toStringTree());

		ASTUtil.removeChild(query, orderByNode);
		Assert.assertEquals(" ( query ( SELECT_FROM ( FROM ( query q ) ) ) )", query.toStringTree());
	}
}
