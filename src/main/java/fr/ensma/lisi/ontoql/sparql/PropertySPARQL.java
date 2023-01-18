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

import java.sql.SQLException;

import fr.ensma.lisi.ontoql.engine.SPARQLOntoQLWalker;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;

/**
 * @author Stéphane JEAN
 */
public class PropertySPARQL extends AbstractDescriptionSPARQL {

	boolean isMultivalued = false;

	boolean isMultivaluedInitialized = false;

	public boolean isAttribute() {
		return false;
	}

	public boolean isProperty() {
		return true;
	}

	public PropertySPARQL(String text, String var, SPARQLOntoQLWalker walker) {
		this.walker = walker;
		int indexOfNamespace = text.indexOf(':');
		if (indexOfNamespace != -1) {
			namespace = text.substring(0, indexOfNamespace);
			name = text.substring(indexOfNamespace + 1, text.length());
		} else {
			name = text;
		}
		variable = var;
	}

	@Override
	public boolean isMultivalued() {
		if (!isMultivaluedInitialized) {
			loadMultivalued();
		}

		return isMultivalued;
	}

	public void loadMultivalued() {
		OntoQLSession session = walker.getSession();
		OntoQLStatement stmt = session.createOntoQLStatement();
		String ontoqlQuery = "select p.#oid from #property p, #collectionType r where p.#range = r.#oid and p.#name='"
				+ name + "'";
		try {
			OntoQLResultSet rset = stmt.executeQuery(ontoqlQuery);
			isMultivalued = rset.next();
			isMultivaluedInitialized = true;
		} catch (SQLException e) {
			isMultivalued = false;
			isMultivaluedInitialized = true;
		}
	}

	@Override
	public boolean isOptional() {
		return (!name.equals("oid") && (!name.equals("URI") && (!name.equals("subdivision")) && !isMultivalued()));
	}
}
