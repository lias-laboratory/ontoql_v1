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

import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;

/**
 * Holds the state of the persistence context of a session including instances
 * of class and instances of entities
 * 
 * @author Stéphane Jean
 */
public interface PersistenceContext {

	/**
	 * Get the session
	 */
	OntoQLSession getSession();

	/**
	 * Clear the state of the persistence context
	 */
	void clear();

	/**
	 * Add a canonical mapping from an instance key to a category instance.
	 * 
	 * @param key
	 * @param instance
	 */
	void addInstance(InstanceKey key, Object instance);

	/**
	 * Get the category instance associated with the given <tt>InstanceKey</tt>
	 * 
	 * @param key
	 * @return
	 */
	Object getInstance(InstanceKey key);

	/**
	 * Is there an instance with the given key in the persistence context.
	 * 
	 * @param key
	 * @return
	 */
	boolean containsInstance(InstanceKey key);

	/**
	 * Remove an instances from the session cache.
	 * 
	 * @param key
	 * @return
	 */
	Object removeInstance(InstanceKey key);
}