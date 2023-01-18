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
import fr.ensma.lisi.ontoql.core.EntityDatatypeCollection;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLSQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.dql.AbstractSelectExpression;
import fr.ensma.lisi.ontoql.engine.tree.dql.SelectExpression;
import fr.ensma.lisi.ontoql.engine.tree.dql.SelectStatement;

/**
 * Represents an array of literal.
 * 
 * @author Stéphane JEAN
 */
public class ArrayNode extends AbstractSelectExpression implements OntoQLSQLTokenTypes {

	private static final long serialVersionUID = -8590097306493977456L;

	/**
	 * Labels for this array.
	 */
	private String label = null;

	/**
	 * Datatype of this array (LiteralNode).
	 */
	private EntityDatatype type = null;

	@Override
	public final EntityDatatype getDataType() {
		if (type == null) {
			loadDataType();
		}
		return type;
	}

	/**
	 * Load the datatype of this collection.
	 */
	private void loadDataType() {
		FactoryEntity factory = getWalker().getFactoryEntity();
		type = factory.createEntityDatatype(EntityDatatype.COLLECTION_NAME);

		if (getFirstChild() instanceof SelectStatement) {
			SelectStatement subQuery = (SelectStatement) getFirstChild();
			// for the moment, we handle only subquery of one degree
			// so, the type of a subquery is the type of the first projected
			// element.
			SelectExpression firstProjectedElement = (SelectExpression) subQuery.getSelectClause()
					.getFirstSelectExpression();
			((EntityDatatypeCollection) type).setDatatype(firstProjectedElement.getDataType());
		} else {
			((EntityDatatypeCollection) type).setDatatype(((SelectExpression) getFirstChild()).getDataType());
		}
	}

	@Override
	public final String getLabel() {
		if (label == null) {
			loadLabel();
		}
		return label;
	}

	/**
	 * Load the datatype of this collection.
	 */
	private void loadLabel() {
		label = getText() + "(";

		LiteralNode currentChild = (LiteralNode) getFirstChild();
		do {
			label += ", " + currentChild.getLabel();
			currentChild = (LiteralNode) currentChild.getNextSibling();
		} while (currentChild != null);
		label += ")";
	}
}
