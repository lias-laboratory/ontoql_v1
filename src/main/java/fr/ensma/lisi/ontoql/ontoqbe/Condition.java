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
package fr.ensma.lisi.ontoql.ontoqbe;

import java.util.Map;

import fr.ensma.lisi.ontoql.engine.util.AliasGenerator;
import fr.ensma.lisi.ontoql.util.ArrayHelper;

/**
 * Represent a disjonctive condition in the where clause of a visually query
 * construct
 * 
 * @author Stéphane JEAN
 */
public class Condition {

	/**
	 * Parent table representing an OntoQL query
	 */
	protected QueryOntoQLTable queryOntoqlTable;

	protected ConditionElement[] conditions;

	@Override
	public String toString() {
		return toString(null, null);
	}

	public String toString(Map namespaces, AliasGenerator aliasGenerator) {
		String res = "";

		if (!isEmpty()) {
			for (int i = 0; i < conditions.length; i++) {
				res += (i != 0) ? " AND " : "";
				res += ((ConditionElement) conditions[i]).toString(namespaces, aliasGenerator);
			}
		}

		return res;
	}

	public boolean isEmpty() {
		return conditions == null || conditions.length == 0;
	}

	public Condition(QueryOntoQLTable queryOntoqlTable) {
		super();
		this.queryOntoqlTable = queryOntoqlTable;

	}

	@Override
	public boolean equals(Object obj) {
		final Condition other = (Condition) obj;

		if (conditions == null && other.conditions == null) {
			return true;
		} else if (conditions != null) {

			return ArrayHelper.isEquals(this.conditions, other.conditions);
		}
		return false;
	}

	/**
	 * @param conditions The conditions to set.
	 */
	public void setConditions(ConditionElement[] conditions) {
		this.conditions = conditions;
	}

	/**
	 * @return Returns the queryOntoqlTable.
	 */
	public QueryOntoQLTable getQueryOntoqlTable() {
		return queryOntoqlTable;
	}

	/**
	 * @param queryOntoqlTable The queryOntoqlTable to set.
	 */
	public void setQueryOntoqlTable(QueryOntoQLTable queryOntoqlTable) {
		this.queryOntoqlTable = queryOntoqlTable;
	}
}