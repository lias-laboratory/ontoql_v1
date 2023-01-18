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
package fr.ensma.lisi.ontoql.core;

import fr.ensma.lisi.ontoql.ontomodel.OntoMultilingualAttribute;

/**
 * 
 * @author Stephane JEAN
 */
public class MultilingualAttribute extends Attribute {

	/**
	 * @param name
	 */
	public MultilingualAttribute(String name) {
		super(name);
	}

	/**
	 * Constructor with an ontoAttribute
	 * 
	 * @param ontoAttribute
	 */
	public MultilingualAttribute(OntoMultilingualAttribute ontoAttribute) {
		super(ontoAttribute);
	}

	/**
	 * @param name
	 */
	public MultilingualAttribute(String name, String lgCode) {
		super(name);
		this.lgCode = lgCode;
	}

	@Override
	public String toSQL(Category context) {
		String res = "";
		if (context != null) {
			String tableAlias = context.getTableAlias();
			String alias = tableAlias == null ? context.toSQL() : tableAlias;
			res += alias + ".";
		}
		res += "value";

		return res;
	}

	@Override
	public boolean isMultilingualDescription() {
		return true;
	}
}
