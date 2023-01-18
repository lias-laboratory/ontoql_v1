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
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Support for slightly more general templating than
 * <tt>StandardSQLFunction</tt>, with an unlimited number of arguments.
 * 
 * @author Stephane JEAN
 */
public class VarArgsSQLFunction implements SQLFunction {

	/**
	 * A name for this function.
	 */
	private final String name;

	/**
	 * Before the list of arguments.
	 */
	private final String begin;

	/**
	 * Separator between each argument.
	 */
	private final String sep;

	/**
	 * After the list of arguments.
	 */
	private final String end;

	/**
	 * Data type of this function.
	 */
	private final EntityDatatype returnType;

	/**
	 * Constructor of a VarArgsSQLFunction.
	 * 
	 * @param aBegin String before the list of arguments
	 * @param aSep   String between each argument
	 * @param aEnd   String after the list of arguments
	 */
	public VarArgsSQLFunction(final String name, final EntityDatatype returnType, final String aBegin,
			final String aSep, final String aEnd) {
		this.name = name;
		this.begin = aBegin;
		this.sep = aSep;
		this.end = aEnd;
		this.returnType = returnType;
	}

	/**
	 * Constructor of a VarArgsSQLFunction.
	 * 
	 * @param aBegin String before the list of arguments
	 * @param aSep   String between each argument
	 * @param aEnd   String after the list of arguments
	 */
	public VarArgsSQLFunction(final String name, final String aBegin, final String aSep, final String aEnd) {
		this.name = name;
		this.begin = aBegin;
		this.sep = aSep;
		this.end = aEnd;
		this.returnType = null;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public EntityDatatype getReturnType(AST firstArgument, FactoryEntity factory) {
		return returnType == null ? OntoQLHelper.STRING : returnType;
	}

	@Override
	public final boolean hasArguments() {
		return true;
	}

	@Override
	public final boolean hasParenthesesIfNoArguments() {
		return true;
	}

	@Override
	public final String render(final List args, FactoryEntity factory) {
		StringBuffer buf = new StringBuffer().append(begin);
		for (int i = 0; i < args.size(); i++) {
			buf.append(args.get(i));
			if (i < args.size() - 1) {
				buf.append(sep);
			}
		}
		return buf.append(end).toString();
	}
}
