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

import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.engine.util.AliasGenerator;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.util.ArrayHelper;
import fr.ensma.lisi.ontoql.util.EqualsHelper;

/**
 * Represent a condition on a given property
 * 
 * @author Stéphane JEAN
 */
public class ConditionElement {

	/**
	 * The given property
	 */
	protected EntityProperty onProperty;

	/**
	 * Boolean operator of the condition
	 */
	protected String booleanOperator;

	/**
	 * Right hand side of the logical condition
	 */
	protected String cste;

	/**
	 * The name of property of the Right hand side of the logical condition In most
	 * case value of this property is null
	 */
	protected String rightPropertyName;

	/**
	 * The name of the context of the property of the Right hand side
	 */
	protected String rightPropertyContextName;

	/**
	 * parent condition
	 */
	protected Condition condition;

	public ConditionElement(Condition condition) {
		super();
		this.condition = condition;
	}

	/**
	 * Require that the range is defined
	 */
	public void initialize(String conditionString) {

		int indexEspace = conditionString.indexOf(' ');

		try {
			String firstElement = conditionString.substring(0, indexEspace);
			if (ArrayHelper.contain(onProperty.getRange().getBooleanOperators(), firstElement)) {
				this.booleanOperator = conditionString.substring(0, indexEspace);
				this.cste = conditionString.substring(indexEspace + 1, conditionString.length());
			} else { // constant with space
				this.booleanOperator = EntityDatatype.OP_EG;
				this.cste = conditionString;
			}
		} catch (IndexOutOfBoundsException oexc) {
			throw new JOBDBCException("The condition '" + conditionString + "' is invalid");
		}

	}

	@Override
	public String toString() {
		return toString(null, null);
	}

	public String toString(Map namespaces, AliasGenerator aliasGenerator) {
		String res = "";
		QueryOntoQLTable parentTable = this.getCondition().getQueryOntoqlTable();
		if (parentTable.isPrefixGenerated()) {
			res += parentTable.getAliasTable().get(onProperty.getCurrentContext()) + ".";
		} else if (parentTable.isPrefixByName()) {
			res += QueryOntoQLTable.applySyntax(
					((EntityClass) onProperty.getCurrentContext()).toString(namespaces, aliasGenerator)) + ".";
		}
		String csteWithNamespace = cste;
		loadRightProperty();
		if (getRightPropertyName() != null) {
			csteWithNamespace = "";
			String namespaceAlias = (String) namespaces.get(onProperty.getNamespace());
			if (getRightPropertyContextName() != null) {
				csteWithNamespace = namespaceAlias + ":" + getRightPropertyContextName() + ".";
			}
			csteWithNamespace += namespaceAlias + ":" + getRightPropertyName();
		}

		res += QueryOntoQLTable.applySyntax(onProperty.toString(namespaces, aliasGenerator)) + " " + booleanOperator
				+ " " + csteWithNamespace + " ";
		return res;
	}

	/**
	 * @param onProperty The onProperty to set.
	 */
	public void setOnProperty(EntityProperty onProperty) {
		this.onProperty = onProperty;
	}

	/**
	 * @param operateur
	 * @param cste
	 */
	public ConditionElement(Condition condition, String operateur, String cste) {
		this(condition);
		this.booleanOperator = operateur;
		this.cste = cste;
	}

	/**
	 * @param cste The cste to set.
	 */
	public void setCste(String cste) {
		this.cste = cste;
	}

	/**
	 * @param operateur The operateur to set.
	 */
	public void setBooleanOperator(String operateur) {
		this.booleanOperator = operateur;
	}

	@Override
	public boolean equals(Object obj) {
		final ConditionElement other = (ConditionElement) obj;
		return EqualsHelper.equals(this.onProperty, other.onProperty)
				&& EqualsHelper.equals(this.booleanOperator, other.booleanOperator)
				&& EqualsHelper.equals(cste, other.cste);
	}

	private void loadRightProperty() {
		int endOfPrefix = cste.indexOf(".");
		if (endOfPrefix != -1) {
			String prefix = cste.substring(0, endOfPrefix);
			String idProp = cste.substring(endOfPrefix + 1, cste.length());
			setRightPropertyName(idProp);
			setRightPropertyContextName(prefix);
		} else if (!cste.startsWith("'") && !cste.startsWith("(")) {
			// a property has the same syntax as an integer
			try {
				Integer.parseInt(cste);
			} catch (NumberFormatException oExc) {
				// but this is not an integer
				setRightPropertyName(cste);
			}
		}
	}

	public boolean isNaturalJoinConditionElement() {
		boolean res = false;

		if (booleanOperator.equals("=")) {
			// A Natural Join Condition is mandatory prefixed by the name of
			// the class or a generated alias
			loadRightProperty();
			String s = onProperty.getCurrentContext().getName();
			if (onProperty.getName().equals(getRightPropertyName())
					&& !onProperty.getCurrentContext().getName().equals(getRightPropertyContextName())) {
				res = true;
			}
		}

		return res;

	}

	/**
	 * @return Returns the booleanOperator.
	 */
	public String getBooleanOperator() {
		return booleanOperator;
	}

	/**
	 * @return Returns the cste.
	 */
	public String getCste() {
		return cste;
	}

	/**
	 * @return Returns the condition.
	 */
	public Condition getCondition() {
		return condition;
	}

	/**
	 * @param condition The condition to set.
	 */
	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	/**
	 * @return Returns the rightPropertyContextName.
	 */
	public String getRightPropertyContextName() {
		return rightPropertyContextName;
	}

	/**
	 * @param rightPropertyContextName The rightPropertyContextName to set.
	 */
	public void setRightPropertyContextName(String rightPropertyContextName) {
		this.rightPropertyContextName = rightPropertyContextName;
	}

	/**
	 * @return Returns the rightPropertyName.
	 */
	public String getRightPropertyName() {
		return rightPropertyName;
	}

	/**
	 * @param rightPropertyName The rightPropertyName to set.
	 */
	public void setRightPropertyName(String rightPropertyName) {
		this.rightPropertyName = rightPropertyName;
	}
}
