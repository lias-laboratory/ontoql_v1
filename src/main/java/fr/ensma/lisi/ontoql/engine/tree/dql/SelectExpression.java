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

import fr.ensma.lisi.ontoql.core.EntityDatatype;

/**
 * Represents an element of a projection list, i.e. a select expression.
 * 
 * @author Stéphane JEAN
 */
public interface SelectExpression {

	/**
	 * Returns the data type of the select expression.
	 * 
	 * @return The data type of the select expression.
	 */
	EntityDatatype getDataType();

	/**
	 * Returns the FROM element that this expression refers to.
	 * 
	 * @return The FROM element.
	 */
	FromElement getFromElement();

	/**
	 * Returns additional display text for the AST node.
	 * 
	 * @return String - The additional display text.
	 */
	String getLabel();

	/**
	 * Sets the text of the node.
	 * 
	 * @param text the new node text.
	 */
	void setText(String text);

	/**
	 * @param alias alias to set
	 */
	void setAlias(String alias);

	/**
	 * @return alias of this node
	 */
	String getAlias();
}
