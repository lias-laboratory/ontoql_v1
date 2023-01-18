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
package fr.ensma.lisi.ontoql.ontoapi;

import java.util.Map;

import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;

/**
 * @author Stéphane Jean
 */
public interface Instance {

	/**
	 * @return the internal identifier of this instance
	 */
	String getOid();

	/**
	 * @return the base type of this instance
	 */
	EntityClass getBaseType();

	/**
	 * Get the loaded values of the properties.
	 * 
	 * @return Map of EntityProperty-Object
	 */
	Map<EntityProperty, Object> getPropertiesValues();

	/**
	 * @param propertyId
	 * @return
	 * @throws JOBDBCException
	 */
	Object getObjectPropertyValue(String propertyId) throws JOBDBCException;

	/**
	 * Get the integer value of a property. If this instance is not loaded, a
	 * database access occurs.
	 * 
	 * @param propertyId the identifier of a property
	 * @return Integer value of this instance for this property. If the value is
	 *         <code>UNKNOWN</code>, the value returned is <code>null</code>
	 * @exception JOBDBCException - if a database access error occurs
	 */
	Integer getIntPropertyValue(String propertyId) throws JOBDBCException;

	/**
	 * Get the float value of a property. If this instance is not loaded, a database
	 * access occurs.
	 * 
	 * @param propertyId the identifier of a property
	 * @return Float value of this instance for this property. If the value is
	 *         <code>UNKNOWN</code>, the value returned is <code>null</code>
	 * @exception JOBDBCException - if a database access error occurs
	 */
	Float getFloatPropertyValue(String propertyId) throws JOBDBCException;

	/**
	 * Get the string value of a property. If this instance is not loaded, a
	 * database access occurs.
	 * 
	 * @param propertyId the identifier of a property
	 * @return String value of this instance for this property
	 * @exception JOBDBCException - if a database access error occurs
	 */
	String getStringPropertyValue(String propertyId) throws JOBDBCException;

	/**
	 * Get the instance value of a property. If this instance is not loaded, a
	 * database access occurs.
	 * 
	 * @param propertyId the identifier of a property
	 * @return Instance value of this instance for this property
	 * @exception JOBDBCException - if a database access error occurs
	 */
	Instance getInstancePropertyValue(String propertyId) throws JOBDBCException;

	/**
	 * Set the value of a property. Even if this is not checked, this property must
	 * be defined on this class
	 * 
	 * @param property the property valued
	 * @param value    value of this property
	 * @exception JOBDBCException - if a database access error occurs
	 */
	void setPropertyValue(EntityProperty property, Object value) throws JOBDBCException;

	/**
	 * Set if this instance is loaded. By default an instance is not loaded until a
	 * Getter method is called
	 * 
	 * @param isLoaded true to state that this instance is loaded
	 */
	void setIsLoaded(boolean isLoaded);

	/**
	 * Get if this instance is loaded.
	 * 
	 * @return True to state that this instance is loaded
	 */
	boolean isLoaded();
}
