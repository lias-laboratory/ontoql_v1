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
package fr.ensma.lisi.ontoql.cfg;

import fr.ensma.lisi.ontoql.ontomodel.mapping.PlibAttribute;
import fr.ensma.lisi.ontoql.ontomodel.mapping.PlibEntity;

/**
 * Represents a link from a PLIB entity mapped to an entity of the core ontology
 * model to another PLIB entity.
 * 
 * @author Stephane JEAN
 */
public class Link {

	/**
	 * The PLIB entity TO of this link (FROM TO).
	 */
	private PlibEntity withEntityPlib;

	/**
	 * Attribute PLIB that constitute the link between the two plib entities.
	 */
	private PlibAttribute attributePLIB;

	/**
	 * Link required for this link e.g, the link between property_det and class_bsu
	 * requires 2 link property_det->property_bsu and property_bsu->class_bsu The
	 * second requires the first
	 */
	private Link requiredLink;

	public Link getRequiredLink() {
		return requiredLink;
	}

	public void setRequiredLink(Link requiredLink) {
		this.requiredLink = requiredLink;
	}

	public PlibAttribute getAttributePLIB() {
		return attributePLIB;
	}

	public void setAttributePLIB(PlibAttribute attributePLIB) {
		this.attributePLIB = attributePLIB;
	}

	public PlibEntity getEntityPlib() {
		return withEntityPlib;
	}

	public void setEntityPlib(PlibEntity entityPlib) {
		this.withEntityPlib = entityPlib;
	}

	public Link(PlibEntity withEntityPlib, PlibAttribute attributePLIB) {
		super();
		this.withEntityPlib = withEntityPlib;
		this.attributePLIB = attributePLIB;
	}
}
