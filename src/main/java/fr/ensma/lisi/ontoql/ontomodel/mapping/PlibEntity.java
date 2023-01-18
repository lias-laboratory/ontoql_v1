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

import fr.ensma.lisi.ontoql.exception.JOBDBCException;

/**
 * Entity of the PLIB model
 *
 * @author Stéphane Jean
 */
public class PlibEntity {

	private String name;

	private boolean isAssociationTable;

	public boolean isAssociationTable() {
		return isAssociationTable;
	}

	public void setAssociationTable(boolean isAssociationTable) {
		this.isAssociationTable = isAssociationTable;
	}

	/**
	 * @param name
	 */
	public PlibEntity(String name) {

		this.name = name;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the table.
	 */

	public String toSQL() throws JOBDBCException {
		return isAssociationTable() ? getName() : getName() + "_e";
	}
}