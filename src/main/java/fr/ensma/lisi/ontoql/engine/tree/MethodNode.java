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
package fr.ensma.lisi.ontoql.engine.tree;

import antlr.SemanticException;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.cfg.dialect.function.SQLFunction;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.dql.AbstractSelectExpression;
import fr.ensma.lisi.ontoql.engine.tree.dql.FromElement;
import fr.ensma.lisi.ontoql.engine.tree.dql.SelectExpression;

/**
 * Represents a method call.
 * 
 * @author Stéphane JEAN
 */
public class MethodNode extends AbstractSelectExpression implements SelectExpression {

	private static final long serialVersionUID = -5040732737631184627L;

	/**
	 * Name of this method.
	 */
	private String methodName;

	/**
	 * From element referencing by this method.
	 */
	private FromElement fromElement;

	/**
	 * SQL function executed.
	 */
	private SQLFunction function;

	/**
	 * Is this call done in the SELECT clause?
	 */
	private boolean inSelect;

	/**
	 * Return datatype of this method.
	 */
	private EntityDatatype datatype;

	public void setDatatype(EntityDatatype datatype) {
		this.datatype = datatype;
	}

	/**
	 * Resolve this methode call.
	 * 
	 * @param inSelect true if this call in the SELECT clause
	 * @throws SemanticException if a semantic exception is detected
	 */
	public final void resolve(final boolean inSelect) throws SemanticException {
		// Get the function name node.
		AST name = getFirstChild();
		initializeMethodNode(name, inSelect);
		AST exprList = name.getNextSibling();

		dialectFunction(exprList);

	}

	/**
	 * @return the SQL function called
	 */
	public final SQLFunction getSQLFunction() {
		return function;
	}

	/**
	 * @param exprList ?
	 */
	private void dialectFunction(final AST exprList) {
		function = getSessionFactory().findSQLFunction(methodName);
		if (function != null) {
			AST firstChild = exprList != null ? exprList.getFirstChild() : null;
			setDatatype(function.getReturnType(firstChild, getEntityFactory()));
		}
	}

	/**
	 * initialize this node.
	 * 
	 * @param name     name of this node
	 * @param inSelect true if in SELECT clause
	 */
	public final void initializeMethodNode(final AST name, final boolean inSelect) {
		name.setType(SQLTokenTypes.METHOD_NAME);
		String text = name.getText();
		methodName = text.toLowerCase(); // Use the lower case function name.
		this.inSelect = inSelect; // Remember whether we're in a SELECT clause
		// or not.
	}

	/**
	 * @return the name of this method
	 */
	public String getMethodName() {
		return methodName;
	}

	@Override
	public final FromElement getFromElement() {
		return fromElement;
	}

	@Override
	public final EntityDatatype getDataType() {
		return datatype;
	}

	@Override
	public final String getLabel() {
		return function.toString();
	}
}
