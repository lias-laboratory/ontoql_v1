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

import fr.ensma.lisi.ontoql.core.EntityClass;

/**
 * The clause used to create an extent of a class.
 * 
 * @author Stéphane JEAN
 */
public class TypedClause extends OntoQLSQLWalkerNode {

	private static final long serialVersionUID = -8182038901983185149L;

	/**
	 * The class which extent will be created.
	 */
	private EntityClass ofClass = null;

	/**
	 * Get the class which extent will be created.
	 * 
	 * @return the class which extent will be created.
	 */
	public EntityClass getOfClass() {
		if (ofClass == null) {
			loadOfClass();
		}
		return ofClass;
	}

	/**
	 * Load the class which extent will be created.
	 */
	public void loadOfClass() {
		String idOfClass = getFirstChild().getText();
		ofClass = getWalker().getFactoryEntity().createEntityClass(idOfClass);
	}
}
