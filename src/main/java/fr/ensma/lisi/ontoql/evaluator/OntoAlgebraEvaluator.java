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
package fr.ensma.lisi.ontoql.evaluator;

import fr.ensma.lisi.ontoql.core.Attribute;
import fr.ensma.lisi.ontoql.engine.tree.IdentNode;
import fr.ensma.lisi.ontoql.engine.tree.dql.FromElement;

/**
 * Interface defining the method used by the OntoQL engine to evaluate the
 * OntoAlgebre This interface must be implemented for each OBDB.
 * 
 * @author Stéphane Jean
 */
public interface OntoAlgebraEvaluator extends Evaluator {

	/**
	 * This function applies the ontoImage operator on an attribute defined on a
	 * given entity
	 * 
	 * @param attribute     an attribute
	 * @param lgCode        the natural language in which the attribute must be
	 *                      evaluated
	 * @param nodeAttribute node of this attribute in the algebra tree
	 * @param nodeEntity    node of the entity in the algebra tree
	 */
	void ontoImageAttribute(Attribute Attribute, String lgCode, IdentNode nodeAttribute, FromElement nodeEntity);
}
