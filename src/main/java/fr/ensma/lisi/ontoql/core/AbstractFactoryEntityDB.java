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

import fr.ensma.lisi.ontoql.core.cache.InstanceKey;
import fr.ensma.lisi.ontoql.core.cache.PersistenceContext;
import fr.ensma.lisi.ontoql.core.ontodb.InstanceImpl;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSessionFactory;
import fr.ensma.lisi.ontoql.ontoapi.Instance;
import fr.ensma.lisi.ontoql.ontomodel.OntoEntity;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Template implementation of a factory of entity for a database implementation
 * of an OBDB
 * 
 * @author Stéphane Jean
 */
public abstract class AbstractFactoryEntityDB implements FactoryEntity {

	/**
	 * A session for database access.
	 */
	public OntoQLSession session;

	/**
	 * Constructor of a factory with a session.
	 * 
	 * @param session provides access to the datatabase
	 */
	public AbstractFactoryEntityDB(OntoQLSession session) {
		this.session = session;
	}

	@Override
	public Category createCategory(String oid, String namespace) {
		Category res = null;
		if (oid.startsWith(OntoQLHelper.PREFIX_ONTOLOGYMODEL_ELEMENT)) { // ontology id
			res = createEntity(oid);
		} else {
			res = createEntityClass(oid, namespace);
		}
		return res;
	}

	@Override
	public Category createCategory(String oid) {
		return createCategory(oid, session.getDefaultNameSpace());
	}

	@Override
	public Entity createEntity(String oid) {
		return createEntity(oid, OntoQLHelper.NAMESPACE_ONTOLOGY_MODEL);
	}

	@Override
	public Entity createEntity(String oid, String namespace) {
		Entity res = null;

		OntoEntity definedPersistentEntity = ((OntoQLSessionFactory) session.getSessionFactory())
				.getEntityOntologyModel(oid.substring(1), session.getReferenceLanguage());
		if (definedPersistentEntity == null) {
			throw new JOBDBCException(oid + " is not a valid entity name");
		}
		res = new Entity(definedPersistentEntity);
		res.setNamespace(namespace);

		return res;
	}

	@Override
	public abstract EntityClass createEntityClass(String oid, String namespace);

	@Override
	public abstract EntityClass createEntityClass(String oid);

	@Override
	public abstract EntityClass createEntityClass();

	@Override
	public abstract EntityClass createEntityClassRoot();

	@Override
	public abstract EntityProperty createEntityProperty();

	@Override
	public Instance createInstance(String oid, EntityClass baseType) {
		Instance res = null;
		PersistenceContext cache = session.getPersistenceContext();
		InstanceKey instanceKey = new InstanceKey(oid, baseType);
		if (cache.containsInstance(instanceKey)) {
			res = (Instance) cache.getInstance(instanceKey);
		} else {
			res = new InstanceImpl(oid, baseType, session, this);
			cache.addInstance(instanceKey, res);
		}
		return res;
	}

	@Override
	public Description createDescription(String oid, String namespace) throws JOBDBCException {
		Description res = null;

		if (oid.startsWith(OntoQLHelper.PREFIX_ONTOLOGYMODEL_ELEMENT)) {
			res = createAttribute(oid);
		} else {
			res = createEntityProperty(oid, namespace);
		}

		return res;
	}

	@Override
	public Description createDescription(String oid) throws JOBDBCException {
		return createDescription(oid, session.getDefaultNameSpace());
	}

	@Override
	public Description createDescriptionOid(Category aCategory) {
		Description res = null;

		if (aCategory instanceof EntityClass) {
			res = createEntityPropertyOid((EntityClass) aCategory);
		} else {
			res = createAttribute("#oid");
			res.setCurrentContext(aCategory);
			res.isDefined(); // TODO createAttribute must locate this
			// attribute
		}
		return res;
	}

	@Override
	public Attribute createAttribute(String oid) {
		return createAttribute(oid, OntoQLHelper.NAMESPACE_ONTOLOGY_MODEL);
	}

	@Override
	public Attribute createAttribute(String oid, String namespace) {
		Attribute res = null;
		res = new Attribute(oid.substring(1), namespace);
		return res;
	}

	@Override
	public abstract EntityProperty createEntityProperty(String oid, String namespace);

	@Override
	public abstract EntityProperty createEntityProperty(String oid);

	@Override
	public abstract EntityProperty createEntityPropertyOid(EntityClass entityClass);

	@Override
	public abstract EntityProperty createEntityPropertyTypeOf(EntityClass entityClass);

	@Override
	public MultilingualAttribute createMultilingualAttribut(String oid, String lgCode) {
		return new MultilingualAttribute(oid.substring(1), lgCode);
	}

	@Override
	public abstract EntityDatatype createEntityDatatype(String datatypeName, String rid);

	@Override
	public abstract EntityDatatype createEntityDatatype(String datatypeName);

	@Override
	public Object instantiate(Class instanceClass, String nameInstanceClass) {
		Object res = null;
		if (instanceClass == EntityDatatype.class) {
			res = createEntityDatatype(nameInstanceClass);
		}
		return res;
	}
}
