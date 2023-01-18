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

import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.Description;
import fr.ensma.lisi.ontoql.engine.tree.IdentNode;
import fr.ensma.lisi.ontoql.engine.tree.dml.InsertStatement;

/**
 * Interface defining the method used by the OntoQL engine to evaluate the
 * OntoAlgebre
 * 
 * This interface must be implemented for each OBDB.
 * 
 * @author Stéphane Jean
 */
public interface DMLEvaluator extends Evaluator {

	/**
	 * This function evaluates the ext function in a DML instruction for an entity
	 * or a class
	 * 
	 * @param category an entity or a class
	 * @param node     the corresponding node in the tree
	 */
	void evaluateExt(Category category, IdentNode node);

	/**
	 * This function evaluates an attribute or a property in a DML instruction
	 * 
	 * @param description an attribute or a property
	 * @param node        the corresponding node in the tree
	 */
	void evaluateDescription(Description description, IdentNode node);

	/**
	 * Do a last transformation of an insert statement
	 * 
	 * @param insert an insert statement on an OBDB
	 */
	void postProcessInsert(InsertStatement insert);
}
