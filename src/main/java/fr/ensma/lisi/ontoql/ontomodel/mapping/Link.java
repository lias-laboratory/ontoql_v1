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
package fr.ensma.lisi.ontoql.ontomodel.mapping;

import java.util.ArrayList;
import java.util.List;

import fr.ensma.lisi.ontoql.core.Entity;

/**
 * @author Stéphane JEAN
 */
public class Link {

	/**
	 * The link between the entity of the core model and the original model : a list
	 * of association tables
	 */
	private List<String> associationTables = new ArrayList<String>();

	/**
	 * The domain of this attribut in the original model
	 */
	private Entity entityInOriginalModel;

	/**
	 * @return Returns the associationTable.
	 */
	public List<String> getAssociationTables() {
		return associationTables;
	}

	public void addAssociationTable(String associationTable) {
		associationTables.add(associationTable);
	}

	/**
	 * @param associationTable The associationTable to set.
	 */
	public void setAssociationTables(List<String> associationTable) {
		this.associationTables = associationTable;
	}

	/**
	 * @return Returns the entityInOriginalModel.
	 */
	public Entity getEntityInOriginalModel() {
		return entityInOriginalModel;
	}

	/**
	 * @param entityInOriginalModel The entityInOriginalModel to set.
	 */
	public void setEntityInOriginalModel(Entity entityInOriginalModel) {
		this.entityInOriginalModel = entityInOriginalModel;
	}
}
