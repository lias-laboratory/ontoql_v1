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

import java.util.ArrayList;
import java.util.List;

import antlr.ASTFactory;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.DisplayableNode;
import fr.ensma.lisi.ontoql.engine.tree.OntoQLSQLWalkerNode;
import fr.ensma.lisi.ontoql.engine.util.ASTUtil;

/**
 * Represents a values-clause of an OntoQL INSERT statement.
 * 
 * @author Stéphane JEAN
 */
public class ValuesClause extends OntoQLSQLWalkerNode implements DisplayableNode {

	private static final long serialVersionUID = 925271048709844972L;

	/**
	 * The list of values.
	 */
	private List<String> values = new ArrayList<String>(5);

	/**
	 * List of the values types.
	 */
	private List<EntityDatatype> valuesTypes = new ArrayList<EntityDatatype>(5);

	/**
	 * Add a value
	 * 
	 * @param value a value
	 */
	public void addValue(String value) {
		values.add(value);
	}

	/**
	 * Set a value at a given index
	 * 
	 * @param i     an index
	 * @param value a value
	 */
	public void setValue(int i, String value) {
		values.set(i, value);
	}

	/**
	 * Add a type of value
	 * 
	 * @param type a type of value
	 */
	public void addValueType(EntityDatatype type) {
		valuesTypes.add(type);
	}

	@Override
	public String getDisplayText() {
		StringBuffer buf = new StringBuffer();
		buf.append("values={").append(values).append("}");
		return buf.toString();
	}

	/**
	 * Append an int value after a given node value or in first if this node is null
	 * 
	 * @param node  previous node or null if the value must be the first
	 * @param value an inserted int value
	 */
	public void appendIntValue(AST node, String value) {
		appendValue(node, value + "", SQLTokenTypes.NUM_INT);
	}

	/**
	 * Append a value after a given node value or in first if this node is null
	 * 
	 * @param node  previous node or null if the value must be the first
	 * @param value an inserted value
	 * @param type  of the value
	 */
	private void appendValue(AST node, String value, int type) {
		ASTFactory inputAstFactory = this.getASTFactory();
		AST valueNode = ASTUtil.create(inputAstFactory, type, value);
		if (node == null) {
			ASTUtil.appendSibling(valueNode, getFirstChild());
			setFirstChild(valueNode);
		} else {
			ASTUtil.insertSibling(valueNode, node);
		}
	}

	/**
	 * Append a string value after a given node value or in first if this node is
	 * null
	 * 
	 * @param node  previous node or null if the value must be the first
	 * @param value an inserted string value
	 */
	public void appendStringValue(AST node, String value) {
		appendValue(node, value + "", SQLTokenTypes.QUOTED_STRING);
	}

	/**
	 * Get the list of values.
	 * 
	 * @return the list of values
	 */
	public List<String> getValues() {
		return values;
	}

	/**
	 * Get the list of datatype of this values.
	 * 
	 * @return the list of datatype of this values
	 */
	public List<EntityDatatype> getValuesTypes() {
		return valuesTypes;
	}
}
