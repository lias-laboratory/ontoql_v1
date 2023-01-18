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
package fr.ensma.lisi.ontoql.core.cache;

import org.hibernate.AssertionFailure;

import fr.ensma.lisi.ontoql.core.Category;

/**
 * Uniquely identifies of a concept instance in a particular session by
 * identifier.
 * <p/>
 *
 * @author Stéphane Jean
 */
public final class InstanceKey {

	private final String identifier; // identifier of an ontology concept or an
	// instance
	private final String conceptId; // name of a class or of an entity

	private final int hashCode;

	/**
	 * Construct a unique identifier for an entity or class instance
	 */
	public InstanceKey(String id, Category concept) {
		if (id == null) {
			throw new AssertionFailure("null identifier");
		}
		this.identifier = id;
		this.conceptId = concept.getInternalId();
		hashCode = generateHashCode(); // cache the hashcode
	}

	/**
	 * @return Returns the conceptName.
	 */
	public String getConceptName() {
		return conceptId;
	}

	/**
	 * @return Returns the identifier.
	 */
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public boolean equals(Object other) {
		InstanceKey otherKey = (InstanceKey) other;
		return otherKey.conceptId.equals(this.conceptId) && otherKey.identifier.equals(this.identifier);
	}

	private int generateHashCode() {
		int result = 17;
		result = 37 * result + conceptId.hashCode();
		result = 37 * result + identifier.hashCode();
		return result;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

	@Override
	public String toString() {
		return "ConceptKey of type " + conceptId + " identified by" + identifier;
	}
}
