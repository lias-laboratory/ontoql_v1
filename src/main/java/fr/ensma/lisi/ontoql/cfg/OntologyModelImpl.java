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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.MappingException;

import fr.ensma.lisi.ontoql.ontomodel.OntoAttribute;
import fr.ensma.lisi.ontoql.ontomodel.OntoEntity;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * The ontology model considered by OntoQL.
 * 
 * @author Stephane JEAN
 */
public class OntologyModelImpl implements OntologyModel {

	private static final Log log = LogFactory.getLog(OntologyModelImpl.class);

	/**
	 * Default language used
	 */
	private static String DEFAULT_LG = OntoQLHelper.ENGLISH;

	/**
	 * map(name, entity) for the default language
	 */
	private final Map<String, OntoEntity> entitiesDefaultLanguage;

	/**
	 * Association of natural langages and the maps (name, entity)
	 */
	private final Map<String, Map<String, OntoEntity>> entitiesByLanguage;

	private final Map<OntoEntity, List<Link>> link;

	private String defaultPackage;

	public OntologyModelImpl() {
		this.entitiesByLanguage = new HashMap<String, Map<String, OntoEntity>>();
		entitiesByLanguage.put(OntoQLHelper.FRENCH, new HashMap<String, OntoEntity>());
		this.entitiesDefaultLanguage = new HashMap<String, OntoEntity>();
		this.link = new HashMap<OntoEntity, List<Link>>();
	}

	@Override
	public String getDefaultPackage() {
		return defaultPackage;
	}

	@Override
	public void setDefaultPackage(String defaultPackage) {
		this.defaultPackage = defaultPackage;
	}

	@Override
	public void addEntity(OntoEntity entity) throws MappingException {
		Object old = entitiesDefaultLanguage.put(entity.getName().toUpperCase(), entity);
		if (old != null) {
			log.warn("duplicate entity definition: " + entity.getName());
		}
		Map<String, OntoEntity> entitiesInFrench = entitiesByLanguage.get(OntoQLHelper.FRENCH);
		entitiesInFrench.put(entity.getName(OntoQLHelper.FRENCH).toUpperCase(), entity);
	}

	@Override
	public void removeEntity(OntoEntity entity) throws MappingException {
		entitiesDefaultLanguage.remove(entity.getName().toUpperCase());
		Map entitiesInFrench = (Map) entitiesByLanguage.get(OntoQLHelper.FRENCH);
		entitiesInFrench.remove(entity.getName(OntoQLHelper.FRENCH).toUpperCase());

	}

	@Override
	public void removeAttributeFromEntity(OntoEntity entity, OntoAttribute attribute) {
		entity.removeDefinedAttribute(attribute);
		entitiesDefaultLanguage.put(entity.getName().toUpperCase(), entity);
		Map<String, OntoEntity> entitiesInFrench = entitiesByLanguage.get(OntoQLHelper.FRENCH);
		entitiesInFrench.put(entity.getName().toUpperCase(), entity);
	}

	@Override
	public Collection getEntities() {
		return entitiesDefaultLanguage.values();
	}

	@Override
	public void addLink(OntoEntity entity, Link aLink) {
		List<Link> links = link.get(entity);
		if (links == null) {
			links = new ArrayList<Link>();
			links.add(aLink);
			link.put(entity, links);
		} else {
			links.add(aLink);
		}
	}

	@Override
	public Link getLink(OntoEntity entity, String toEntityName) {
		Link res = null;

		List links = (List) link.get(entity);
		Link currentLink = null;
		if (links != null) {
			for (int i = 0; i < links.size(); i++) {
				currentLink = (Link) links.get(i);
				if (currentLink.getEntityPlib().getName().equals(toEntityName)) {
					return currentLink;
				}
			}
		}

		return res;

	}

	@Override
	public OntoEntity getEntity(String name, String lg) {
		OntoEntity res = null;
		if (lg == null || lg.equals(DEFAULT_LG) || lg.equals(OntoQLHelper.NO_LANGUAGE)) {
			res = getEntity(name);
		} else {
			Map mapUsed = (Map) entitiesByLanguage.get(lg);
			res = getEntity(name, mapUsed);
		}
		return res;
	}

	@Override
	public OntoEntity getEntity(String name) {
		return (OntoEntity) entitiesDefaultLanguage.get(name.toUpperCase());
	}

	/**
	 * Get an entity according to a given name and a given map
	 */
	private OntoEntity getEntity(String name, Map entities) {
		return (OntoEntity) entities.get(name.toUpperCase());
	}

}