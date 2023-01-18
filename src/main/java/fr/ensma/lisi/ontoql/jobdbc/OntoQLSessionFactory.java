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
package fr.ensma.lisi.ontoql.jobdbc;

import org.hibernate.SessionFactory;

import fr.ensma.lisi.ontoql.cfg.dialect.function.SQLFunction;
import fr.ensma.lisi.ontoql.ontomodel.OntoAttribute;
import fr.ensma.lisi.ontoql.ontomodel.OntoEntity;

/**
 * A factory of sessions.
 * 
 * @author Stéphane JEAN
 */
public interface OntoQLSessionFactory extends SessionFactory {

	/**
	 * Get an entity of the ontology model used.
	 * 
	 * @param name name of an entity
	 * @param a    given natural language
	 * @return an entity of the ontology model used.
	 */
	OntoEntity getEntityOntologyModel(String name, String lg);

	/**
	 * test if an entity exists in the ontology model used.
	 * 
	 * @param name name of an entity
	 * @return true if the entity exists in the ontology model used.
	 */
	boolean existEntityInOntologyModel(String name);

	/**
	 * add an entity to the ontology model used.
	 * 
	 * @param name name of an entity
	 * @return the mapping of an entity of the core model
	 */
	void addEntityOntologyModel(OntoEntity entity);

	/**
	 * remove an entity from the ontology model used.
	 * 
	 * @param name name of an entity
	 * @return the mapping of an entity of the core model
	 */
	void removeEntityOntologyModel(OntoEntity entity);

	/**
	 * remove an attribute of an entity from the ontology model used.
	 */
	void removeAttributeFromEntityOntologyModel(OntoEntity entity, OntoAttribute attribute);

	/**
	 * Find the definition of a function called
	 * 
	 * @param name name of the function
	 * @return the definition of a function
	 */
	SQLFunction findSQLFunction(String name);
}
