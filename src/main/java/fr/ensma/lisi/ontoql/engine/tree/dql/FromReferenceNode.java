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

import fr.ensma.lisi.ontoql.engine.tree.DisplayableNode;
import fr.ensma.lisi.ontoql.engine.tree.InitializeableNode;
import fr.ensma.lisi.ontoql.engine.tree.ResolvableNode;

/**
 * Represents a reference to a FROM element, for example a class alias in a
 * WHERE clause.
 * 
 * @author Stéphane JEAN
 */
public abstract class FromReferenceNode extends AbstractSelectExpression
		implements ResolvableNode, DisplayableNode, InitializeableNode {

	private static final long serialVersionUID = -6537768299087228346L;

	/**
	 * The from element referenced by this node.
	 */
	private FromElement fromElement;

	@Override
	public FromElement getFromElement() {
		return fromElement;
	}

	/**
	 * set the from element referenced by this node.
	 * 
	 * @param fromElement the from element referenced by this node
	 */
	public final void setFromElement(final FromElement fromElement) {
		this.fromElement = fromElement;
	}

	@Override
	public String getDisplayText() {
		StringBuffer buf = new StringBuffer();
		buf.append("{").append((fromElement == null) ? "no fromElement" : fromElement.getDisplayText());
		buf.append("}");
		return buf.toString();
	}
}
