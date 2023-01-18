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

/**
 * A base AST node for the intermediate tree.
 * 
 * @author Stéphane JEAN
 */
public class SQLNode extends antlr.CommonAST {

	private static final long serialVersionUID = -981667306719261022L;

	/**
	 * The original text for the node, mostly for debugging.
	 */
	private String originalText;

	@Override
	public final void setText(final String s) {
		super.setText(s);
		if (s != null && s.length() > 0 && originalText == null) {
			originalText = s;
		}
	}

	/**
	 * @return the original text of this node
	 */
	final String getOriginalText() {
		return originalText;
	}
}
