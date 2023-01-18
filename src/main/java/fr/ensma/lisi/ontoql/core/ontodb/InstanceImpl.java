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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.ontoapi.Instance;

/**
 *
 * @author Stéphane JEAN
 */
public class InstanceImpl implements Instance {

	private static final Log log = LogFactory.getLog(InstanceImpl.class);

	/**
	 * A reference to the factory creator of this object. Enable to create others
	 * entities
	 */
	protected FactoryEntity factory;

	/**
	 * A session needed to access to the underlying OBDB
	 */
	protected OntoQLSession session;

	/**
	 * identifier of this instance
	 */
	private String oid;

	/**
	 * isLoaded is true if a Getter method doesn't require a database access
	 */
	private boolean isLoaded;

	/**
	 * Base class of this instance, i.e. minorant Class for the subsomption
	 * relationship
	 */
	private EntityClass baseType;

	/**
	 * Stores the property-value couples
	 */
	private Map<EntityProperty, Object> propertiesValues = new HashMap<EntityProperty, Object>();

	public InstanceImpl(String oid, EntityClass baseType, OntoQLSession session, FactoryEntity factory) {
		this.session = session;
		this.oid = oid;
		this.baseType = baseType;
		this.factory = factory;
		this.isLoaded = false;
	}

	@Override
	public String getOid() {
		return oid;
	}

	/**
	 * Load this instance if this is not the case
	 */
	public void checkIsLoaded(EntityProperty prop) {
		if (!isLoaded && (!propertiesValues.containsKey(prop))) {
			load();
			setIsLoaded(true);
		}
	}

	/**
	 * Load an instance
	 * 
	 * @throws JOBDBCException - if a database access problem occurs
	 */
	public void load() throws JOBDBCException {
		StringBuffer query = new StringBuffer();
		query.append("SELECT i ");
		query.append("FROM !" + baseType.getInternalId());
		query.append(" as i WHERE oid = " + oid);

		log.warn(query);

		OntoQLStatement stmt = session.createOntoQLStatement();
		OntoQLResultSet rs = stmt.executeQuery(query.toString());
		try {
			rs.next();
			this.propertiesValues = rs.getInstance(1).getPropertiesValues();
			if (rs.next()) {
				throw new JOBDBCException("Unable to load instance identified by " + oid + " of the class " + baseType);
			}

		} catch (SQLException exc) {
			throw new JOBDBCException(exc);
		}

	}

	@Override
	public Integer getIntPropertyValue(String propertyId) throws JOBDBCException {
		return (Integer) getObjectPropertyValue(propertyId);
	}

	@Override
	public Float getFloatPropertyValue(String propertyId) throws JOBDBCException {
		return (Float) getObjectPropertyValue(propertyId);
	}

	@Override
	public String getStringPropertyValue(String propertyId) throws JOBDBCException {
		return (String) getObjectPropertyValue(propertyId);
	}

	@Override
	public Object getObjectPropertyValue(String propertyId) throws JOBDBCException {
		EntityProperty prop = factory.createEntityProperty(propertyId);
		checkIsLoaded(prop);
		if (!propertiesValues.containsKey(prop)) {
			throw new JOBDBCException("The property " + prop + " is not defined on the instance identified by " + oid
					+ " of the class " + baseType);
		}
		return propertiesValues.get(prop);
	}

	@Override
	public Instance getInstancePropertyValue(String propertyId) throws JOBDBCException {
		return (Instance) getObjectPropertyValue(propertyId);
	}

	@Override
	public EntityClass getBaseType() {
		return baseType;
	}

	@Override
	public void setIsLoaded(boolean isLoaded) {
		this.isLoaded = isLoaded;

	}

	@Override
	public void setPropertyValue(EntityProperty property, Object value) throws JOBDBCException {
		propertiesValues.put(property, value);
	}

	@Override
	public Map<EntityProperty, Object> getPropertiesValues() {
		return propertiesValues;
	}

	@Override
	public boolean isLoaded() {
		return isLoaded;
	}

	@Override
	public String toString() {
		return oid;
	}
}
