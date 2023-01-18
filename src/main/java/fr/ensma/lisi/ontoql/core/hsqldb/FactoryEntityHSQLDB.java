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
package fr.ensma.lisi.ontoql.core.hsqldb;

import fr.ensma.lisi.ontoql.core.AbstractFactoryEntityDB;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;

/**
 * Implementation on HSQLDB of FactoryEntity.
 * 
 * @author Stéphane JEAN
 */
public class FactoryEntityHSQLDB extends AbstractFactoryEntityDB {

	/**
	 * Constructor of a factory with a session.
	 * 
	 * @param session provides access to the datatabase
	 */
	public FactoryEntityHSQLDB(OntoQLSession session) {
		super(session);
	}

	@Override
	public EntityClass createEntityClass(String oid) {
		EntityClass res = null;
		res = new EntityClassHSQLDB(oid, session, this);
		return res;
	}

	@Override
	public EntityClass createEntityClass(String oid, String namespace) {
		EntityClass res = null;
		res = new EntityClassHSQLDB(oid, namespace, session, this);
		return res;
	}

	@Override
	public EntityClass createEntityClass() {
		EntityClass res = new EntityClassHSQLDB(null, session, this);
		return res;
	}

	@Override
	public EntityClass createEntityClassRoot() {
		return null;
	}

	@Override
	public EntityDatatype createEntityDatatype(String datatypeName, String rid) {
		return null;
	}

	@Override
	public EntityDatatype createEntityDatatype(String datatypeName) {
		return null;
	}

	@Override
	public EntityProperty createEntityProperty() {
		return null;
	}

	@Override
	public EntityProperty createEntityProperty(String oid, String namespace) {
		return null;
	}

	@Override
	public EntityProperty createEntityProperty(String oid) {
		return null;
	}

	@Override
	public EntityProperty createEntityPropertyOid(EntityClass entityClass) {
		return null;
	}

	@Override
	public EntityProperty createEntityPropertyTypeOf(EntityClass entityClass) {
		return null;
	}
}
