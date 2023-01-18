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

import java.awt.geom.Arc2D.Float;

import fr.ensma.lisi.ontoql.core.AbstractEntityDatatype;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.util.OntoDBHelper;

/**
 * The datatype real measure.
 * 
 * @author Stéphane JEAN
 */
public class EntityDatatypeRealMeasureOntoDB extends AbstractEntityDatatype {

	/**
	 * A session needed to access to the underlying OBDB
	 */
	protected OntoQLSession session;

	public EntityDatatypeRealMeasureOntoDB(OntoQLSession session) {
		this.session = session;
	}

	@Override
	public String getName() {
		return "REAL_MEASURE_TYPE";
	}

	@Override
	public String[] getBooleanOperators() {
		return new String[] { OP_EG, OP_SUP, OP_INF, OP_SUPEG, OP_INFEG };
	}

	@Override
	public String[] getArithmeticOperators() {
		return new String[] { OP_PLUS, OP_MINUS, OP_DIV, OP_EG };
	}

	@Override
	public String valueToOntoql(String value) {
		return value;
	}

	@Override
	public String ontoQLToValue(String value) {
		return value;
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
		return OntoDBHelper.REAL_MEASURE_TYPE_TABLE;
	}

	@Override
	public String insert() throws JOBDBCException {
		return null;
	}

	@Override
	public Class getReturnedClass() {
		return Float.class;
	}

	@Override
	public String getExtent() {
		return "float8";
	}
}
