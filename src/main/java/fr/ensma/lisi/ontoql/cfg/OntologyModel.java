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

import java.util.Collection;

import fr.ensma.lisi.ontoql.ontomodel.OntoAttribute;
import fr.ensma.lisi.ontoql.ontomodel.OntoEntity;

/**
 * Interface to the ontology model used.
 * 
 * @author Stephane JEAN
 */
public interface OntologyModel {

	/**
	 * Get the defaultPackage of the JAVA classes corresponding to the ontology
	 * model
	 * 
	 * @return Returns the defaultPackage of the JAVA classes corresponding to the
	 *         ontology model
	 */
	String getDefaultPackage();

	/**
	 * Set the defaultPackage of the JAVA classes corresponding to the ontology
	 * model
	 * 
	 * @param defaultPackage defaultPackage of the JAVA classes corresponding to the
	 *                       ontology model
	 */
	void setDefaultPackage(String defaultPackage);

	/**
	 * Add an entity to the ontology model used
	 * 
	 * @param entity entity to add
	 */
	void addEntity(OntoEntity entity);

	/**
	 * Remove an entity from the ontology model used
	 * 
	 * @param entity entity to remove
	 */
	void removeEntity(OntoEntity entity);

	/**
	 * Get an entity of the ontology model used
	 * 
	 * @param name    name of the entity
	 * @param natural language of this name
	 * @return an entity of the ontology model used
	 */
	OntoEntity getEntity(String name, String lg);

	/**
	 * Get the entities of the ontology model used
	 * 
	 * @return the entities of the ontology model used
	 */
	Collection getEntities();

	public void removeAttributeFromEntity(OntoEntity entity, OntoAttribute attribute);

	/**
	 * Get an entity of the ontology model used
	 * 
	 * @param name name of the entity in the English language
	 * @return an entity of the ontology model used
	 */
	OntoEntity getEntity(String name);

	/**
	 * Add a link (used in the mapping to PLIB) to an entity
	 * 
	 * @param entity a given entity
	 * @param aLink  a link to add to this entity
	 */
	void addLink(OntoEntity entity, Link aLink);

	/**
	 * Get a link defined on an entity according to a name of an entity in the
	 * ontology model mapped
	 * 
	 * @param entity       a given entity
	 * @param toEntityName name of an entity in the ontology model mapped
	 * @return the corresponding link or null if not found
	 */
	Link getLink(OntoEntity entity, String toEntityName);
}