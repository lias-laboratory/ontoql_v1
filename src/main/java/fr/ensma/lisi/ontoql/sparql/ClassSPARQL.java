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

/**
 * A SPARQL class
 * 
 * @author Stéphane JEAN
 */
public class ClassSPARQL extends AbstractCategorySPARQL {

	public boolean isClass() {
		return true;
	}

	public ClassSPARQL(String text, String variable, SPARQLOntoQLWalker walker) {
		this.walker = walker;
		this.variable = variable;
		int indexOfNamespace = text.indexOf(':');
		if (indexOfNamespace != -1) {
			namespace = text.substring(0, indexOfNamespace);
			name = text.substring(indexOfNamespace + 1, text.length());
		} else {
			name = text;
		}
		oid = new PropertySPARQL(namespace + ":oid", variable, walker);
		oid.setScope(this);
		walker.registerVariable(variable, oid);
		alias = walker.getAliasGenerator().createName(name);
	}
}
