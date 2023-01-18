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
package fr.ensma.lisi.ontoql.engine.tree.ddl;

import antlr.SemanticException;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.Entity;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLSQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.OntoQLSQLWalkerNode;
import fr.ensma.lisi.ontoql.engine.tree.Statement;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;

/**
 * Defines a top-level AST node representing an OntoQL alter statement.
 */
public class DropStatement extends OntoQLSQLWalkerNode implements Statement {

	private static final long serialVersionUID = 2943071916259943119L;

	/**
	 * The category altered by this AlterStatement.
	 */
	private Category category;

	@Override
	public int getStatementType() {
		return OntoQLSQLTokenTypes.DROP;
	}

	/**
	 * Drop the given entity.
	 * 
	 * @throws JOBDBCException if a semantic exception is detected.
	 */
	public void process() throws SemanticException {

		AST entityTypeNode = getFirstChild();
		FactoryEntity factory = getWalker().getFactoryEntity();
		if (entityTypeNode.getType() == SQLTokenTypes.ENTITY) {
			processDropEntity(entityTypeNode.getFirstChild(), factory);
		} else if (entityTypeNode.getType() == SQLTokenTypes.CLASS) {
			processDropClass(entityTypeNode.getFirstChild(), factory);
		}

	}

	private void processDropEntity(AST nodeNameEntity, FactoryEntity factory) {
		category = factory.createEntity(nodeNameEntity.getText());
		((Entity) category).drop(getWalker().getSession());
	}

	private void processDropClass(AST nodeNameEntity, FactoryEntity factory) {
		// Not yet implemented
	}
}
