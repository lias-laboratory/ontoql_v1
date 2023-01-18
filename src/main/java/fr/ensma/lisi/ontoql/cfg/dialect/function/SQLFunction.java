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
package fr.ensma.lisi.ontoql.cfg.dialect.function;

import java.util.List;

import org.hibernate.QueryException;

import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.FactoryEntity;

/**
 * Provides an interface for supporting various OntoQL functions that are
 * translated to SQL. The Dialect and its sub-classes use this interface to
 * provide details required for processing of the function.
 * 
 * @author Stephane JEAN
 */
public interface SQLFunction {

	/**
	 * The function return type
	 * 
	 * @param firstArgument the first argument or the function
	 * @param factory       to build a new datatype
	 */
	EntityDatatype getReturnType(AST firstArgument, FactoryEntity factory) throws QueryException;

	/**
	 * Does this function have any arguments?
	 * 
	 * @return True if this function have an argument
	 */
	boolean hasArguments();

	/**
	 * If there are no arguments, are parens required?
	 * 
	 * @return True If parentheses are required
	 */
	boolean hasParenthesesIfNoArguments();

	/**
	 * Render the function call as SQL.
	 * 
	 * @param args List of arguments of this function
	 * @return The function call
	 */
	String render(List args, FactoryEntity factory);
}
