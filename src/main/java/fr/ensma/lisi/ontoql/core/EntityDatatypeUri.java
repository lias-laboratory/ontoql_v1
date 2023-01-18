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
import fr.ensma.lisi.ontoql.util.StringHelper;

/**
 * The datatype Uri.
 * 
 * @author Mickael BARON
 */
public class EntityDatatypeUri extends AbstractEntityDatatype {

	@Override
	public boolean isMultilingualType() {
		return false;
	}

	public EntityDatatypeUri() {
	}

	@Override
	public String getName() {
		return URI_TYPE_NAME;
	}

	@Override
	public String[] getBooleanOperators() {
		return new String[] { OP_LIKE, OP_NOT_LIKE, OP_EG, OP_SUP, OP_INF, OP_SUPEG, OP_INFEG };
	}

	@Override
	public String[] getArithmeticOperators() {
		return new String[] { OP_CONCAT };
	}

	@Override
	public String valueToOntoql(String value) {
		return "'" + value + "'";
	}

	@Override
	public String ontoQLToValue(String value) {
		return StringHelper.removeFirstAndLastletter(value);
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
	public String getTableName() {
		return null;
	}

	@Override
	public String insert() throws JOBDBCException {
		return null;
	}

	@Override
	public Class getReturnedClass() {
		return String.class;
	}

	@Override
	public String getExtent() {
		return "varchar";
	}
}
