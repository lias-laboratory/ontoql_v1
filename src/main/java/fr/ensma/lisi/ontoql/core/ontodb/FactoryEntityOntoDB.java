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
package fr.ensma.lisi.ontoql.core.ontodb;

import fr.ensma.lisi.ontoql.core.AbstractFactoryEntityDB;
import fr.ensma.lisi.ontoql.core.Attribute;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.NotSupportedDatatypeException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.util.OntoDBHelper;

/**
 * Implementation on OntoDB of AbstractFactory.
 * 
 * @author Stéphane JEAN
 */
public class FactoryEntityOntoDB extends AbstractFactoryEntityDB {

	/**
	 * Constructor of a factory with a session.
	 * 
	 * @param session provides access to the datatabase
	 */
	public FactoryEntityOntoDB(OntoQLSession session) {
		super(session);
	}

	@Override
	public EntityClass createEntityClass(String oid) {
		EntityClass res = null;
		if (oid.equalsIgnoreCase("RootClass")) {
			res = new EntityClassRootOntoDB(this);
		} else {
			res = new EntityClassOntoDB(oid, session, this);
		}
		return res;
	}

	@Override
	public EntityClass createEntityClass(String oid, String namespace) {
		EntityClass res = null;
		if (oid.equalsIgnoreCase("RootClass")) {
			res = new EntityClassRootOntoDB(this);
		} else {
			res = new EntityClassOntoDB(oid, namespace, session, this);
		}
		return res;
	}

	@Override
	public Attribute createAttribute(String oid, String namespace) {
		Attribute res = null;
		res = new Attribute(oid.substring(1), namespace);
		return res;
	}

	@Override
	public EntityProperty createEntityProperty(String oid) {
		return createEntityProperty(oid, session.getDefaultNameSpace());
	}

	@Override
	public EntityProperty createEntityProperty(String oid, String namespace) {
		EntityProperty res;

		// latter remove this
		if (oid.equals("oid")) {
			res = new EntityPropertyOidOntoDB(this);
		} else if (oid.equals("tablename")) {
			res = new EntityPropertyTypeOfOntoDB(this);
		} else {
			res = new EntityPropertyOntoDB(oid, namespace, session, this);
		}
		return res;
	}

	@Override
	public EntityProperty createEntityPropertyOid(EntityClass entityClass) {
		// The result a property corresponding to an oid property
		EntityProperty res;

		res = new EntityPropertyOidOntoDB(this);
		res.setCurrentContext(entityClass);

		return res;
	}

	@Override
	public EntityProperty createEntityPropertyTypeOf(EntityClass entityClass) {
		// The result a property corresponding to a typeOf property
		EntityProperty res;

		res = new EntityPropertyTypeOfOntoDB(this);
		res.setCurrentContext(entityClass);

		return res;
	}

	@Override
	public EntityDatatype createEntityDatatype(String datatypeTableName, String rid) {
		EntityDatatype res = null;

		if (datatypeTableName.equals(OntoDBHelper.ASSOCIATION_TYPE_TABLE)) {
			res = new EntityDatatypeCategoryOntoDB(session, this);
		} else if (datatypeTableName.equals(OntoDBHelper.COLLECTION_TYPE_TABLE)) {
			res = new EntityDatatypeCollectionOntoDB(session, this);
		} else if (datatypeTableName.equals(OntoDBHelper.INT_TYPE_TABLE)) {
			res = new EntityDatatypeIntOntoDB(session);
		} else if (datatypeTableName.equals(OntoDBHelper.INT_MEASURE_TYPE_TABLE)) {
			res = new EntityDatatypeIntMeasureOntoDB(session);
		} else if (datatypeTableName.equals(OntoDBHelper.REAL_MEASURE_TYPE_TABLE)) {
			res = new EntityDatatypeRealMeasureOntoDB(session);
		} else if (datatypeTableName.equals(OntoDBHelper.STRING_TYPE_TABLE)) {
			res = new EntityDatatypeStringOntoDB(session);
		} else if (datatypeTableName.equals(OntoDBHelper.MULTILINGUAL_STRING_TYPE_TABLE)) {
			res = new EntityDatatypeStringOntoDB(session, true);
		} else if (datatypeTableName.equals(OntoDBHelper.BOOLEAN_TYPE_TABLE)) {
			res = new EntityDatatypeBooleanOntoDB(session);
		} else if (datatypeTableName.equals(OntoDBHelper.ENUMERATE_TYPE_TABLE)) {
			res = new EntityDatatypeEnumerateOntoDB(session);
		} else if (datatypeTableName.equals(OntoDBHelper.URI_TYPE_TABLE)) {
			res = new EntityDatatypeUriOntoDB(session);
		} else if (datatypeTableName.equals(OntoDBHelper.REAL_TYPE_TABLE)) {
			res = new EntityDatatypeRealOntoDB(session);
		} else if (datatypeTableName.equals(OntoDBHelper.COUNT_TYPE_TABLE)) {
			res = new EntityDatatypeCountOntoDB(session);
		} else {
			throw new NotSupportedDatatypeException(
					"Unable to load datatype : " + datatypeTableName + " not yet implemented");
		}
		res.setInternalId(rid);

		return res;
	}

	@Override
	public EntityClass createEntityClass() {
		EntityClass res = new EntityClassOntoDB(null, session, this);
		return res;
	}

	@Override
	public EntityProperty createEntityProperty() {
		EntityProperty res = null;

		res = new EntityPropertyOntoDB(null, session, this);

		return res;
	}

	@Override
	public EntityDatatype createEntityDatatype(String datatypeName) throws JOBDBCException {
		EntityDatatype res = null;

		if (datatypeName.equalsIgnoreCase(EntityDatatype.INT_NAME)) {
			res = new EntityDatatypeIntOntoDB(session);
		} else if (datatypeName.equalsIgnoreCase(EntityDatatype.STRING_NAME)) {
			res = new EntityDatatypeStringOntoDB(session);
		} else if (datatypeName.equalsIgnoreCase(EntityDatatype.BOOLEAN_NAME)) {
			res = new EntityDatatypeBooleanOntoDB(session);
		} else if (datatypeName.equalsIgnoreCase(EntityDatatype.COLLECTION_NAME)) {
			res = new EntityDatatypeCollectionOntoDB(session, this);
		} else if (datatypeName.equalsIgnoreCase(EntityDatatype.ASSOCIATION_NAME)) {
			res = new EntityDatatypeCategoryOntoDB(session, this);
		} else if (datatypeName.equalsIgnoreCase(EntityDatatype.REAL_NAME)) {
			res = new EntityDatatypeRealOntoDB(session);
		} else if (datatypeName.equalsIgnoreCase(EntityDatatype.ENNUMERATE_NAME)) {
			res = new EntityDatatypeEnumerateOntoDB(session);
		} else if (datatypeName.equalsIgnoreCase(EntityDatatype.URI_TYPE_NAME)) {
			res = new EntityDatatypeUriOntoDB(session);
		} else if (datatypeName.equalsIgnoreCase(EntityDatatype.COUNT_TYPE_NAME)) {
			res = new EntityDatatypeCountOntoDB(session);
		} else {
			throw new JOBDBCException("Datatype " + datatypeName + "not recognized");
		}
		return res;
	}

	@Override
	public EntityClass createEntityClassRoot() {
		return new EntityClassRootOntoDB(this);
	}
}
