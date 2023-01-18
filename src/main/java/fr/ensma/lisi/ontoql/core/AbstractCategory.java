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
package fr.ensma.lisi.ontoql.core;

/**
 * Implementation of methods implemented in the same way for the class and for
 * the entities
 * 
 * @author Stéphane Jean
 */
public abstract class AbstractCategory implements Category {

	/**
	 * the alias used for this class in the ontoql query
	 */
	protected String categoryAlias = null;

	/**
	 * the alias used for this class in the generated sql query
	 */
	protected String tableAlias = null;

	/**
	 * The namespace of the this ontology class.
	 */
	protected String namespace;

	/**
	 * Is this property used in a polymorphic context
	 */
	protected boolean isPolymorph = false;

	@Override
	public boolean isPolymorph() {
		return isPolymorph;
	}

	@Override
	public void setPolymorph(boolean polymorph) {
		this.isPolymorph = polymorph;
	}

	@Override
	public String getCategoryAlias() {
		return categoryAlias;
	}

	@Override
	public void setCategoryAlias(String entityAlias) {
		this.categoryAlias = entityAlias;
	}

	@Override
	public String getTableAlias() {
		return tableAlias;
	}

	@Override
	public void setTableAlias(String tableAlias) {
		this.tableAlias = tableAlias;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
}
