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

import fr.ensma.lisi.ontoql.exception.JOBDBCException;

/**
 * Represent the datatype booleqn.
 *
 * @author Stephane JEAN
 */
public class EntityDatatypeBoolean extends AbstractEntityDatatype {

	@Override
	public String[] getArithmeticOperators() {
		return new String[] {};
	}

	@Override
	public String[] getBooleanOperators() {
		return new String[] { OP_EG };
	}

	@Override
	public String getExtent() {
		return null;
	}

	@Override
	public String getName() {
		return BOOLEAN_NAME;
	}

	@Override
	public Class getReturnedClass() {
		return Boolean.class;
	}

	@Override
	public String getTableName() {
		return null;
	}

	@Override
	public String insert() throws JOBDBCException {
		return null;
	}

	@Override
	public boolean isAssociationType() {
		return false;
	}

	@Override
	public boolean isCollectionAssociationType() {
		return false;
	}

	@Override
	public String ontoQLToValue(String value) {
		return value;
	}

	@Override
	public String valueToOntoql(String value) {
		return value;
	}

	@Override
	public boolean isMultilingualType() {
		return false;
	}
}
