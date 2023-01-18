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
 * Provides a standard implementation that supports the majority of the OntoQL
 * functions that are translated to SQL.
 * 
 * @author Stephane JEAN
 */
public class StandardSQLFunction implements SQLFunction {

	private EntityDatatype returnType = null;

	private String name;

	public StandardSQLFunction(String name) {
		this.name = name;
	}

	public StandardSQLFunction(String name, EntityDatatype typeValue) {
		returnType = typeValue;
		this.name = name;
	}

	@Override
	public EntityDatatype getReturnType(AST firstArgument, FactoryEntity factory) {
		return returnType == null ? OntoQLHelper.STRING : returnType;
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
	public String render(List args, FactoryEntity factory) {
		StringBuffer buf = new StringBuffer();
		buf.append(name).append('(');
		for (int i = 0; i < args.size(); i++) {
			buf.append(args.get(i));
			if (i < args.size() - 1)
				buf.append(", ");
		}
		return buf.append(')').toString();
	}

	@Override
	public String toString() {
		return name;
	}
}
