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

import java.util.HashMap;
import java.util.Map;

import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;

/**
 * 
 * @author Stéphane Jean
 */
public class PersistenceContextImpl implements PersistenceContext {

	private static final int INIT_COL_SIZE = 8;

	/**
	 * The session "owning" this context.
	 */
	private OntoQLSession session;

	/**
	 * Loaded category instances, by InstanceKey
	 */
	private Map<InstanceKey, Object> entitiesByKey;

	/**
	 * Constructs a PersistentContext, bound to the given session.
	 * 
	 * @param session The session "owning" this context.
	 */
	public PersistenceContextImpl(OntoQLSession session) {
		this.session = session;
		entitiesByKey = new HashMap<InstanceKey, Object>(INIT_COL_SIZE);
	}

	@Override
	public OntoQLSession getSession() {
		return session;
	}

	@Override
	public void clear() {
		entitiesByKey.clear();
	}

	@Override
	public void addInstance(InstanceKey key, Object instance) {
		entitiesByKey.put(key, instance);

	}

	@Override
	public Object getInstance(InstanceKey key) {
		return entitiesByKey.get(key);
	}

	@Override
	public boolean containsInstance(InstanceKey key) {
		return entitiesByKey.containsKey(key);
	}

	@Override
	public Object removeInstance(InstanceKey key) {
		Object entity = entitiesByKey.remove(key);
		return entity;
	}
}
