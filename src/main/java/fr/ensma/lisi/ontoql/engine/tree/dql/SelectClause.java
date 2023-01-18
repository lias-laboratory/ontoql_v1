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
package fr.ensma.lisi.ontoql.engine.tree.dql;

import java.util.ArrayList;
import java.util.List;

import antlr.collections.AST;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.OntoQLSQLWalkerNode;
import fr.ensma.lisi.ontoql.engine.util.ASTPrinter;

/**
 * Represents the list of expressions in a SELECT clause.
 * 
 * @author Stéphane JEAN
 */
public class SelectClause extends OntoQLSQLWalkerNode {

	private static final long serialVersionUID = 6614475262001677627L;

	/**
	 * Returns an array of SelectExpressions gathered from the children of the given
	 * parent AST node.
	 * 
	 * @return an array of SelectExpressions gathered from the children of the given
	 *         parent AST node.
	 */
	public SelectExpression[] collectSelectExpressions() {
		// Get the first child to be considered.
		AST firstChild = getFirstSelectExpression();
		AST parent = this;
		List<AST> list = new ArrayList<AST>(parent.getNumberOfChildren());
		for (AST n = firstChild; n != null; n = n.getNextSibling()) {
			if (n instanceof SelectExpression) {
				list.add(n);
			} else {
				throw new IllegalStateException("Unexpected AST: " + n.getClass().getName() + " "
						+ new ASTPrinter(SQLTokenTypes.class).showAsString(n, ""));
			}
		}
		return (fr.ensma.lisi.ontoql.engine.tree.dql.SelectExpression[]) list
				.toArray(new SelectExpression[list.size()]);
	}

	public AST getFirstSelectExpression() {
		AST n = getFirstChild();
		// Skip 'DISTINCT' and 'ALL', so we return the first expression node.
		while (n != null && (n.getType() == SQLTokenTypes.DISTINCT || n.getType() == SQLTokenTypes.ALL)) {
			n = n.getNextSibling();
		}
		return n;
	}
}
