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

import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.exception.QueryException;

/**
 * ANSI-SQL style <tt>cast(foo as type)</tt> where the type is an OntoQL type
 * 
 * Adapted from Hibernate
 * 
 * @author Stephane JEAN
 */
public class CastFunction implements SQLFunction {

	/**
	 * The datatype into which the expression is converted.
	 */
	private EntityDatatype datatypeConversion;

	private void loadDatatypeConversion(String datatypeText, FactoryEntity factory) {
		String typeText = datatypeText;
		datatypeConversion = factory.createEntityDatatype(typeText);
	}

	public EntityDatatype getDatatypeConversion(String datatypeText, FactoryEntity factory) {
		// we must reload the datatype each times because the same object
		// CastFunction is reused for all cast.
		loadDatatypeConversion(datatypeText, factory);
		return datatypeConversion;
	}

	@Override
	public EntityDatatype getReturnType(AST firstArgument, FactoryEntity factory) throws QueryException {
		AST datatypeNode = firstArgument.getNextSibling();
		return getDatatypeConversion(datatypeNode.getText(), factory);
	}

	@Override
	public boolean hasArguments() {
		return true;
	}

	@Override
	public boolean hasParenthesesIfNoArguments() {
		return true;
	}

	@Override
	public String render(List args, FactoryEntity factory) throws QueryException {
		if (args.size() != 2) {
			throw new QueryException("cast() requires two arguments");
		}
		getDatatypeConversion((String) args.get(1), factory);
		String sqlType = datatypeConversion.getExtent();

		return "cast(" + args.get(0) + " as " + sqlType + ')';
	}
}
