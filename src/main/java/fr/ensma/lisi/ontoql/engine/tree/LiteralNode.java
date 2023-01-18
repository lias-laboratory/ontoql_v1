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
package fr.ensma.lisi.ontoql.engine.tree;

import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLSQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.dql.AbstractSelectExpression;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Represents a literal.
 * 
 * @author Stéphane JEAN
 */
public class LiteralNode extends AbstractSelectExpression implements OntoQLSQLTokenTypes {

	private static final long serialVersionUID = -7215113800570336011L;

	@Override
	public final EntityDatatype getDataType() {
		FactoryEntity factory = getWalker().getFactoryEntity();
		switch (getType()) {
		case NUM_INT:
			return OntoQLHelper.INT;
		case NUM_FLOAT:
			return OntoQLHelper.REAL;
		case NUM_LONG:
			return OntoQLHelper.REAL;
		case NUM_DOUBLE:
			return OntoQLHelper.REAL;
		case QUOTED_STRING:
			return OntoQLHelper.STRING;
		case TRUE:
		case FALSE:
			return factory.createEntityDatatype(EntityDatatype.BOOLEAN_NAME);
		case NULL:
			return OntoQLHelper.STRING;
		default:
			return null;
		}

	}

	@Override
	public final String getLabel() {
		return getText();
	}
}
