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
package fr.ensma.lisi.ontoql.cfg.dialect;

import java.util.HashMap;
import java.util.Map;

import antlr.collections.AST;

import fr.ensma.lisi.ontoql.cfg.dialect.function.CastFunction;
import fr.ensma.lisi.ontoql.cfg.dialect.function.SQLFunction;
import fr.ensma.lisi.ontoql.cfg.dialect.function.StandardSQLFunction;
import fr.ensma.lisi.ontoql.cfg.dialect.function.VarArgsSQLFunction;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.exception.QueryException;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Represents a dialect of SQL implemented by a particular RDBMS.
 * 
 * Adapted from the Hibernate project
 * 
 * @author Stephane JEAN
 */
public class Dialect {

	private static final Map<String, SQLFunction> STANDARD_AGGREGATE_FUNCTIONS = new HashMap<String, SQLFunction>();

	static {
		STANDARD_AGGREGATE_FUNCTIONS.put("count", new StandardSQLFunction("count") {
			public EntityDatatype getReturnType(AST firstArgument, FactoryEntity factory) {
				return OntoQLHelper.INT;
			}
		});

		STANDARD_AGGREGATE_FUNCTIONS.put("avg", new StandardSQLFunction("avg") {
			public EntityDatatype getReturnType(AST firstArgument, FactoryEntity factory) throws QueryException {
				return OntoQLHelper.REAL;
			}
		});

		STANDARD_AGGREGATE_FUNCTIONS.put("max", new StandardSQLFunction("max"));
		STANDARD_AGGREGATE_FUNCTIONS.put("min", new StandardSQLFunction("min"));
		STANDARD_AGGREGATE_FUNCTIONS.put("sum", new StandardSQLFunction("sum"));
	}

	private final Map<String, SQLFunction> sqlFunctions = new HashMap<String, SQLFunction>();

	public Dialect() {
		sqlFunctions.putAll(STANDARD_AGGREGATE_FUNCTIONS);

		// Standard sql2003 functions.
		registerFunction("upper", new StandardSQLFunction("upper"));
		registerFunction("lower", new StandardSQLFunction("lower"));
		registerFunction("length", new StandardSQLFunction("length", OntoQLHelper.INT));
		registerFunction("bit_length", new StandardSQLFunction("bit_length", OntoQLHelper.INT));
		registerFunction("coalesce", new StandardSQLFunction("coalesce"));
		registerFunction("nullif", new StandardSQLFunction("nullif"));
		registerFunction("abs", new StandardSQLFunction("abs"));
		registerFunction("mod", new StandardSQLFunction("mod", OntoQLHelper.INT));
		registerFunction("sqrt", new StandardSQLFunction("sqrt", OntoQLHelper.REAL));

		// Other functions like ||
		registerFunction("concat", new VarArgsSQLFunction("concatenation", OntoQLHelper.STRING, "", "||", ""));
		registerFunction("cast", new CastFunction());
	}

	protected void registerFunction(String name, SQLFunction function) {
		sqlFunctions.put(name, function);
	}

	/**
	 * SQL functions as defined in general. The results of this method should be
	 * integrated with the specialisation's data.
	 * 
	 * @return
	 */
	public final Map<String, SQLFunction> getFunctions() {
		return sqlFunctions;
	}
}