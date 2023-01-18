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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import antlr.collections.AST;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLSQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.OntoQLSQLWalkerNode;
import fr.ensma.lisi.ontoql.engine.tree.Statement;
import fr.ensma.lisi.ontoql.engine.util.ASTUtil;

/**
 * Defines a top-level AST node representing an OntoQL query
 * 
 * @author Stéphane JEAN
 */
public class SelectStatement extends OntoQLSQLWalkerNode implements Statement {

	private static final long serialVersionUID = 2520893138410679184L;

	private static final Log log = LogFactory.getLog(SelectStatement.class);

	/**
	 * From clause of this query.
	 */
	private FromClause fromClause;

	/**
	 * Where clause of this query.
	 */
	private AST whereClause;

	@Override
	public int getStatementType() {
		return OntoQLSQLTokenTypes.QUERY;
	}

	/**
	 * Get the from clause of this query
	 * 
	 * @return the from clause of this query
	 */
	public final FromClause getFromClause() {
		if (fromClause == null) {
			fromClause = (FromClause) ASTUtil.findTypeInChildren(this, OntoQLSQLTokenTypes.FROM);
		}
		return fromClause;
	}

	/**
	 * Search the Where clause of this query
	 * 
	 * @return the Where clause of this query or null if there is no such clause
	 */
	protected AST locateWhereClause() {
		return ASTUtil.findTypeInChildren(this, OntoQLSQLTokenTypes.WHERE);
	}

	/**
	 * Is this query has a Where clause?
	 * 
	 * @return True if this query has a Where clause
	 */
	public final boolean hasWhereClause() {
		AST whereClause = locateWhereClause();
		return whereClause != null && whereClause.getNumberOfChildren() > 0;
	}

	/**
	 * Get the Where clause of this query or create a new one if it doesn't has one.
	 * 
	 * @return the Where clause of this query
	 */
	public final AST getWhereClause() {
		if (whereClause == null) {
			whereClause = locateWhereClause();
		}
		return whereClause;
	}

	public final void createWhereClause() {
		log.debug("getWhereClause() : Creating a new WHERE clause...");
		whereClause = ASTUtil.create(getWalker().getASTFactory(), OntoQLSQLTokenTypes.WHERE, "WHERE");
		// inject the WHERE after the parent
		AST parent = getFromClause();
		whereClause.setNextSibling(parent.getNextSibling());
		parent.setNextSibling(whereClause);
	}

	/**
	 * Locate the select clause that is part of this select statement.
	 * 
	 * @return the select clause.
	 */
	public final SelectClause getSelectClause() {
		return (SelectClause) ASTUtil.findTypeInChildren(this, SQLTokenTypes.SELECT_CLAUSE);
	}

}
