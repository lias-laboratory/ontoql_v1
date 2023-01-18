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
package fr.ensma.lisi.ontoql.engine.tree.dml;

import antlr.SemanticException;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLSQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.OntoQLSQLWalkerNode;
import fr.ensma.lisi.ontoql.exception.QueryException;

/**
 * Defines a top-level AST node representing an OntoQL "insert values"
 * statement.
 * 
 * @author Stéphane JEAN
 */
public class InsertStatement extends OntoQLSQLWalkerNode implements fr.ensma.lisi.ontoql.engine.tree.Statement {

	private static final long serialVersionUID = -4334616342214546988L;

	@Override
	public int getStatementType() {
		return OntoQLSQLTokenTypes.INSERT;
	}

	/**
	 * Performs detailed semantic validation on this insert statement tree.
	 * 
	 * @throws QueryException Indicates validation failure.
	 */
	public void validate() throws SemanticException {
		getIntoClause().validateTypes(getValuesClause());
	}

	/**
	 * Retrieve this insert statement's into-clause.
	 * 
	 * @return The into-clause
	 */
	public IntoClause getIntoClause() {
		return (IntoClause) getFirstChild();
	}

	/**
	 * Retrieve this insert statement's values-clause.
	 * 
	 * @return The values-clause.
	 */
	public ValuesClause getValuesClause() {
		return ((ValuesClause) getIntoClause().getNextSibling());
	}
}
