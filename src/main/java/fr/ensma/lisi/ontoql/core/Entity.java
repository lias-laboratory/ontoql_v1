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
package fr.ensma.lisi.ontoql.core;

import java.util.ArrayList;
import java.util.List;

import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.ontomodel.OntoAttribute;
import fr.ensma.lisi.ontoql.ontomodel.OntoEntity;
import fr.ensma.lisi.ontoql.ontomodel.OntoMultilingualAttribute;
import fr.ensma.lisi.ontoql.util.ArrayHelper;
import fr.ensma.lisi.ontoql.util.EqualsHelper;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * An entity of the ontology model loaded.
 * 
 * @author Stephane JEAN
 */
public class Entity extends AbstractCategory implements Cloneable {

	/**
	 * The definition of this entity.
	 */
	private OntoEntity delegateEntity;

	/**
	 * Setter for the superEntity attribute.
	 * 
	 * @param superEntity
	 */
	public void setSuperEntity(Entity superEntity) {
		this.delegateEntity.setSuperMapEntity(superEntity.getDelegateEntity());
	}

	/**
	 * Construct a new entity with its delegate
	 * 
	 * @param delegateEntity its delegate
	 */
	public Entity(OntoEntity delegateEntity) {
		this.delegateEntity = delegateEntity;
	}

	/**
	 * Construct a new entity and its delegate according to its name
	 * 
	 * @param name a name of entity
	 */
	public Entity(String nameEntity) {
		OntoEntity entity = new OntoEntity(nameEntity);
		this.delegateEntity = entity;
	}

	@Override
	public String getInternalId() throws JOBDBCException {
		return getName();
	}

	@Override
	public String getName() {
		return delegateEntity.getName();
	}

	/**
	 * Get the name of this entity in a given natural language
	 * 
	 * @param lg
	 * @return
	 */
	public String getName(String lg) {
		return delegateEntity.getName(lg);
	}

	/**
	 * Set the name of this entity in a given natural language
	 * 
	 * @param lg
	 * @return
	 */
	public void setName(String name, String lg) {
		delegateEntity.setName(name, lg);
	}

	@Override
	public String toSQL() throws JOBDBCException {
		String res = "";
		String alias = tableAlias == null ? "" : " " + tableAlias;
		res = delegateEntity.toSQL() + alias;
		return res;

	}

	@Override
	public Category[] getSubcategories() {
		return getSubentities();
	}

	@Override
	public boolean isClass() {
		return false;
	}

	@Override
	public boolean isEntity() {
		return true;
	}

	@Override
	public String getTypeLabel() {
		return "entity";
	}

	/**
	 * Get all the sub entities of this entity.
	 * 
	 * @return all the sub entities of this entity.
	 */
	public Entity[] getSubentities() {
		Entity[] res = null;
		Entity[] directSubentities = getDirectSubentities();
		Object[] resObject = directSubentities;
		for (int i = 0; i < directSubentities.length; i++) {
			resObject = ArrayHelper.join(resObject, directSubentities[i].getSubentities());
		}
		int nbSubEntities = resObject.length;
		res = new Entity[nbSubEntities];
		for (int i = 0; i < nbSubEntities; i++) {
			res[i] = (Entity) resObject[i];
		}

		return res;
	}

	/**
	 * Get the direct sub entities of this entity.
	 * 
	 * @return the direct sub entities of this entity.
	 */
	private Entity[] getDirectSubentities() {
		Entity[] res = null;
		List<OntoEntity> subMapEntities = delegateEntity.getDirectSubMapEntities();
		int nbSubEntities = subMapEntities.size();

		res = new Entity[nbSubEntities];
		for (int i = 0; i < nbSubEntities; i++) {
			res[i] = new Entity((OntoEntity) subMapEntities.get(i));
		}
		return res;
	}

	/**
	 * Create the table and columns required in the OBDB to store instances of this
	 * entity
	 * 
	 * @param session access to the database
	 */
	public int create(OntoQLSession session) {
		return delegateEntity.create(session);
	}

	/**
	 * drop the table and columns required in the OBDB to store instances of this
	 * entity
	 * 
	 * @param session access to the database
	 */
	public int drop(OntoQLSession session) {
		return delegateEntity.drop(session);
	}

	/**
	 * Create the table and columns required in the OBDB to store instances of this
	 * entity
	 * 
	 * @param session access to the database
	 */
	public int createAttribute(Attribute attribute, OntoQLSession session) {
		return delegateEntity.createAttribute(attribute.getMapAttribut(), session);
	}

	/**
	 * Drop the table and columns required in the OBDB to store instances of this
	 * entity
	 * 
	 * @param session access to the database
	 */
	public int dropAttribute(Attribute attribute, OntoQLSession session) {
		return delegateEntity.dropAttribute(attribute.getMapAttribut(), session);
	}

	@Override
	public Object clone() {
		try {
			Entity e = (Entity) super.clone();
			e.delegateEntity = delegateEntity;
			return e;
		} catch (CloneNotSupportedException e) {
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	@Override
	public boolean equals(Object obj) {
		boolean res = false;
		if (obj != null) {
			Entity otherEntity = (Entity) obj;
			res = EqualsHelper.equals(categoryAlias, otherEntity.categoryAlias)
					&& isPolymorph == otherEntity.isPolymorph
					&& EqualsHelper.equals(tableAlias, otherEntity.tableAlias);
		}
		return res;
	}

	/**
	 * Get its delegate entity.
	 * 
	 * @return its delegate entity
	 */
	public OntoEntity getDelegateEntity() {
		return delegateEntity;
	}

	/**
	 * Set its delegate entity.
	 * 
	 * @param delegateEntity its delegate entity
	 */
	public void setDelegateEntity(OntoEntity delegateEntity) {
		this.delegateEntity = delegateEntity;
	}

	public ArrayList getMapAttributs() throws JOBDBCException {
		return delegateEntity.getApplicableAttributes();
	}

	public Attribute[] getDefinedAttributes() {
		ArrayList definedAttributes = getMapAttributs();
		Attribute[] res = new Attribute[definedAttributes.size()];

		OntoAttribute definedAttribute = null;
		for (int i = 0; i < definedAttributes.size(); i++) {
			definedAttribute = (OntoAttribute) definedAttributes.get(i);
			if (definedAttribute instanceof OntoMultilingualAttribute) {
				res[i] = new MultilingualAttribute((OntoMultilingualAttribute) definedAttribute);
			} else {
				res[i] = new Attribute(definedAttribute);
			}
		}
		return res;
	}

	public Description getDefinedDescription(String identifier) {
		return getDefinedDescription(identifier, OntoQLHelper.ENGLISH);
	}

	@Override
	public Description getDefinedDescription(String identifier, String lg) {
		Description res = null;
		identifier = removeSyntaxIdentifier(identifier);
		ArrayList definedAttributes = getMapAttributs();
		OntoAttribute definedAttribute = null;
		for (int i = 0; i < definedAttributes.size(); i++) {
			definedAttribute = (OntoAttribute) definedAttributes.get(i);
			if (definedAttribute.getName(lg).equals(identifier)
					|| definedAttribute.getName(OntoQLHelper.getOtherLanguage(lg)).equals(identifier)) {
				if (definedAttribute instanceof OntoMultilingualAttribute) {
					res = new MultilingualAttribute((OntoMultilingualAttribute) definedAttribute);
				} else {
					res = new Attribute(definedAttribute);
				}
				res.setCurrentContext(this);
				break;
			}
		}
		return res;
	}

	/**
	 * Remove the OntoQL syntax of an ontology identifier (#)
	 * 
	 * @param id an ontology identifier
	 * @return the identifier without the syntax
	 */
	private String removeSyntaxIdentifier(String id) {
		return id.substring(1);
	}

	public boolean isIdentifiedByBSU() {
		boolean result = false;

		// For the moment hard coded.
		// Must be discovered by browsing the hierarchy of #Class and #property
		if (getName().equals("class") || getName().equals("property") || getName().equals("contextProperty")) {
			result = true;
		}
		return result;
	}

	@Override
	public boolean isAbstract() {
		// latter must be specified in the mapping
		return false;
	}
}