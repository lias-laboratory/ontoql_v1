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

import org.hibernate.util.EqualsHelper;

/**
 * Represents a plib attribut.
 * 
 * @author Stéphane Jean
 */
public class PlibAttribute {

	/**
	 * The name of this attribut
	 */
	private String name;

	/**
	 * The domain of this attribut
	 */
	private PlibEntity ofEntity;

	/**
	 * @param name
	 */
	public PlibAttribute(String name) {
		super();
		this.name = name;
	}

	/**
	 * @param name
	 */
	public PlibAttribute(String name, boolean isOptional) {
		super();
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

	public String toSQL() {
		return getName();
	}

	public PlibEntity getOfEntity() {
		return ofEntity;
	}

	public void setOfEntity(PlibEntity ofEntity) {
		this.ofEntity = ofEntity;
	}

	public boolean equals(Object obj) {
		PlibAttribute other = (PlibAttribute) obj;
		System.out.println(name.equals(other.name));
		// System.out.println(isOptional == other.isOptional());
		System.out.println(EqualsHelper.equals(ofEntity, other.getOfEntity()));
		return name.equals(other.name) && EqualsHelper.equals(ofEntity, other.getOfEntity());
	}

	@Override
	public String toString() {
		return getName();
	}
}
