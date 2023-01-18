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
package fr.ensma.lisi.ontoql.sparql;

import fr.ensma.lisi.ontoql.engine.SPARQLOntoQLWalker;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Stéphane JEAN
 */
public class AttributeSPARQL extends AbstractDescriptionSPARQL {

	public boolean isAttribute() {
		return true;
	}

	public boolean isProperty() {
		return false;
	}

	public AttributeSPARQL(String text, String var, SPARQLOntoQLWalker walker) {
		this.walker = walker;
		name = text;
		if (!text.equals("#oid")) {
			int indexOfNamespace = text.indexOf(':');
			name = OntoQLHelper.PREFIX_ONTOLOGYMODEL_ELEMENT
					+ walker.getMappingOfEntityOrAttribute(text.substring(indexOfNamespace + 1, text.length()));
		}
		variable = var;
	}

	@Override
	public boolean isMultivalued() {
		return name.equals("#superclasses");
	}

	@Override
	public boolean isOptional() {
		return (name.equals("#definition"));
	}
}
