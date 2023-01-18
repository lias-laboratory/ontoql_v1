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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ensma.lisi.ontoql.core.AbstractEntityClass;
import fr.ensma.lisi.ontoql.core.AbstractEntityProperty;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.engine.util.AliasGenerator;
import fr.ensma.lisi.ontoql.util.ArrayHelper;
import fr.ensma.lisi.ontoql.util.EqualsHelper;

/**
 * A datatype for an ontoql query describe in a table of Query by example
 * application
 * 
 * @author Stéphane JEAN
 */
public class QueryOntoQLTable {

	private static final Log log = LogFactory.getLog(QueryOntoQLTable.class);

	public static final String ORDER_BY_ASC = "ASC";

	public static final String ORDER_BY_DESC = "DESC";

	/**
	 * Properties selected
	 */
	protected EntityProperty[] target;

	/**
	 * The range of the query
	 */
	protected EntityClass[] range;

	/**
	 * The predicat in the form of disjonctive conditions
	 */
	protected Condition[] disjonctiveConditions;

	/**
	 * The property in the ORDER BY clause
	 */
	protected EntityProperty[] orderBy;

	/**
	 * Type of Order by : ASC OR DESC
	 */
	protected String orderByType = ORDER_BY_ASC; // Default ASC

	/**
	 * True if the query is polymorphic on the range class
	 */
	protected boolean[] polymorph;

	/**
	 * Alias generated during the translation to an OntoQL query
	 */
	protected Hashtable aliasTable;

	/**
	 * True If generated alias are used
	 */
	protected boolean prefixGenerated = false;

	/**
	 * True If name of class must be used as alias
	 */
	protected boolean prefixByName = false;

	public QueryOntoQLTable() {
		disjonctiveConditions = new Condition[0];
		orderBy = new AbstractEntityProperty[0];
		this.range = new AbstractEntityClass[] { null };
		this.polymorph = new boolean[] { false };
	}

	/**
	 * @param target
	 * @param range
	 * @param disjonctiveConditions
	 */
	public QueryOntoQLTable(AbstractEntityProperty[] target, AbstractEntityClass range,
			Condition[] disjonctiveConditions) {
		super();
		this.target = target;
		this.range = new AbstractEntityClass[] { range };
		this.disjonctiveConditions = disjonctiveConditions;
		orderBy = new AbstractEntityProperty[0];
		this.polymorph = new boolean[] { false };
	}

	/**
	 * @param target
	 * @param range
	 * @param disjonctiveConditions
	 * @param polymorphe
	 */
	public QueryOntoQLTable(AbstractEntityProperty[] target, AbstractEntityClass range,
			Condition[] disjonctiveConditions, boolean polymorphe) {
		this(target, range, disjonctiveConditions);
		this.polymorph = new boolean[] { polymorphe };

	}

	/**
	 * @param disjonctiveConditions The disjonctiveConditions to set.
	 */
	public void setDisjonctiveConditions(Condition[] disjonctiveConditions) {
		this.disjonctiveConditions = disjonctiveConditions;
	}

	/**
	 * @param polymorphe The polymorphe to set.
	 */
	public void setPolymorph(boolean polymorphe) {
		this.polymorph[0] = polymorphe;
	}

	/**
	 * @param range The range to set.
	 */
	public void setRange(EntityClass range) {
		this.range[0] = range;
	}

	/**
	 * @param target The target to set.
	 */
	public void setTarget(EntityProperty[] target) {
		this.target = target;
	}

	/*
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		final QueryOntoQLTable otherQueryTable = (QueryOntoQLTable) other;
		boolean equalsPolymorph = ArrayHelper.isEquals(this.polymorph, otherQueryTable.polymorph);
		boolean equalsRange = ArrayHelper.isEquals(this.range, otherQueryTable.range);
		boolean equalsTarget = ArrayHelper.isEquals(otherQueryTable.target, this.target);
		boolean equalsConditions = ArrayHelper.isEquals(this.disjonctiveConditions,
				otherQueryTable.disjonctiveConditions);
		boolean equalsOrderBy = ArrayHelper.isEquals(this.orderBy, otherQueryTable.orderBy);
		boolean equalsOrderByType = EqualsHelper.equals(orderByType, otherQueryTable.orderByType);

		log.warn(equalsTarget + "");

		return equalsPolymorph && equalsRange && equalsTarget && equalsConditions && equalsOrderBy && equalsOrderByType;
	}

	public boolean doubleInRange(Object[] range) {
		boolean res = false;
		if (range.length > 1) {
			Object[] subRange = ArrayHelper.slice(range, 1, range.length - 1);
			if (ArrayHelper.contain(subRange, range[0])) {
				res = true;
			} else {
				res = doubleInRange(subRange);
			}
		}
		return res;
	}

	@Override
	public String toString() {

		// We need to record the namespace used
		Map namespaces = new HashMap();

		// If Auto-join then generated alias must be used
		prefixGenerated = doubleInRange(range);
		AliasGenerator aliasGenerator = new AliasGenerator();
		aliasTable = new Hashtable(range.length);
		for (int i = 0; i < range.length; i++) {
			if (range[i].getCategoryAlias() != null) {
				aliasTable.put(range[i], range[i].getCategoryAlias());
			}
		}

		// Else used name of class as prefix if ambiguities are possible
		prefixByName = !prefixGenerated && range.length > 1;

		String res = "SELECT ";

		String prefix = "";
		String namespaceAlias = "";
		for (int i = 0; i < target.length; i++) {
			if (prefixGenerated) {
				// current context of the property
				EntityClass currentContext = null;
				// if this is not a path expression
				if (target[i].getPathProperty() == null) {
					currentContext = (EntityClass) target[i].getCurrentContext();
				} else { // else the context if the context of the path
					// expression
					currentContext = (EntityClass) target[i].getPathProperty().getCurrentContext();
				}
				String alias = (String) aliasTable.get(currentContext);
				if (alias == null) {
					alias = aliasGenerator.createName(currentContext.toString());
					aliasTable.put(currentContext, alias);
				}
				prefix = alias + ".";
			} else if (prefixByName) {
				EntityProperty pathProperty = target[i];
				while (pathProperty.getPathProperty() != null) {
					pathProperty = pathProperty.getPathProperty();
				}
				prefix = applySyntax(
						((EntityClass) pathProperty.getCurrentContext()).toString(namespaces, aliasGenerator)) + ".";
			}

			res += prefix + namespaceAlias + applySyntax(target[i].toString(namespaces, aliasGenerator)) + ", ";
		}
		res = res.substring(0, res.length() - 2);

		res += " \n FROM ";

		for (int i = 0; i < range.length; i++) {

			res += polymorph[i] ? "" : "only(";
			res += applySyntax(range[i].toString(namespaces, aliasGenerator));
			if (prefixGenerated) {
				res += " as " + (String) aliasTable.get(range[i]);
			}
			res += polymorph[i] ? "" : ")";
			res += ", ";
		}
		res = res.substring(0, res.length() - 2);

		res += " \n WHERE (";
		for (int i = 0; i < disjonctiveConditions.length; i++) {
			res += disjonctiveConditions[i].toString(namespaces, aliasGenerator) + ") \n OR (";
		}

		res = res.substring(0, res.length() - 7);

		if (orderBy != null && orderBy.length > 0) {
			res += " ORDER BY ";
			prefix = "";
			for (int i = 0; i < orderBy.length; i++) {
				if (prefixGenerated) {
					EntityClass currentContext = (EntityClass) orderBy[i].getCurrentContext();
					String alias = (String) aliasTable.get(currentContext);
					prefix = alias + ".";
				} else if (prefixByName) {
					prefix = applySyntax(
							((EntityClass) orderBy[i].getCurrentContext()).toString(namespaces, aliasGenerator)) + ".";
				}
				res += prefix + applySyntax(orderBy[i].toString(namespaces, aliasGenerator)) + ", ";
			}
			res = res.substring(0, res.length() - 2);
			res += " " + orderByType;
		}

		Set namespaceEntries = namespaces.entrySet();
		for (Iterator iter = namespaceEntries.iterator(); iter.hasNext();) {
			Entry element = (Entry) iter.next();
			res += "\nUSING NAMESPACE " + element.getValue() + " = '" + element.getKey() + "', ";
		}
		res = res.substring(0, res.length() - 2);

		return res;
	}

	public static String applySyntax(String nameClassorProperty) {
		String res = nameClassorProperty;
		if (nameClassorProperty.indexOf(' ') != -1) {
			int indexNamespace = nameClassorProperty.indexOf(':');
			if (indexNamespace == -1) {
				res = "\"" + nameClassorProperty + "\"";
			} else {
				String namespace = nameClassorProperty.substring(0, indexNamespace + 1);
				String name = nameClassorProperty.substring(indexNamespace + 1, nameClassorProperty.length());
				res = namespace + "\"" + name + "\"";
			}
		}
		return res;
	}

	/**
	 * Merge this queryOntoQLTable with another queryOntoqlTable
	 * 
	 * @return The merged table reprensenting an OntoQL Query
	 */
	public QueryOntoQLTable merge(QueryOntoQLTable otherQueryOntoqlTable) {
		QueryOntoQLTable res = new QueryOntoQLTable();

		// Target result = Merge of target
		Object[] resTargetObject = ArrayHelper.join(this.target, otherQueryOntoqlTable.target);
		AbstractEntityProperty[] resTarget = new AbstractEntityProperty[resTargetObject.length];
		for (int i = 0; i < resTargetObject.length; i++) {
			resTarget[i] = (AbstractEntityProperty) resTargetObject[i];
		}
		res.setTarget(resTarget);

		// Range result = Merge of range
		Object[] resRangeObject = ArrayHelper.join(this.range, otherQueryOntoqlTable.range);
		AbstractEntityClass[] resRange = new AbstractEntityClass[resRangeObject.length];
		for (int i = 0; i < resRangeObject.length; i++) {
			resRange[i] = (AbstractEntityClass) resRangeObject[i];
		}
		res.setRange(resRange);

		res.setPolymorph(ArrayHelper.join(this.polymorph, otherQueryOntoqlTable.polymorph));

		// Where result : Merge by disjunctive condition
		int thisNbrCondition = this.disjonctiveConditions.length;
		int otherNbrCondition = otherQueryOntoqlTable.disjonctiveConditions.length;

		int nbrCondition = thisNbrCondition > otherNbrCondition ? thisNbrCondition : otherNbrCondition;
		Condition[] resConditions = new Condition[nbrCondition];
		Condition resCondition = null;

		// Variable to handle Natural Join
		List naturalJoinConditionElement = new ArrayList();

		for (int i = 0; i < resConditions.length; i++) {
			resCondition = new Condition(res);
			if (i >= thisNbrCondition) {
				resCondition = otherQueryOntoqlTable.disjonctiveConditions[i];
				for (int j = 0; j < resCondition.conditions.length; j++) {
					resCondition.conditions[j].setCondition(resCondition);
					if (resCondition.conditions[j].isNaturalJoinConditionElement()) {
						naturalJoinConditionElement.add(resCondition.conditions[j]);
					}
				}
			} else if (i >= otherNbrCondition) {
				resCondition = this.disjonctiveConditions[i];
				for (int j = 0; j < resCondition.conditions.length; j++) {
					resCondition.conditions[j].setCondition(resCondition);
					if (resCondition.conditions[j].isNaturalJoinConditionElement()) {
						naturalJoinConditionElement.add(resCondition.conditions[j]);
					}
				}
			} else {
				Object[] resConditionsElementObject = ArrayHelper.join(this.disjonctiveConditions[i].conditions,
						otherQueryOntoqlTable.disjonctiveConditions[i].conditions);
				ConditionElement[] resConditionsElement = new ConditionElement[resConditionsElementObject.length];
				for (int j = 0; j < resConditionsElementObject.length; j++) {
					resConditionsElement[j] = (ConditionElement) resConditionsElementObject[j];
					resConditionsElement[j].setCondition(resCondition);
					if (resConditionsElement[j].isNaturalJoinConditionElement()) {
						naturalJoinConditionElement.add(resConditionsElement[j]);
					}
				}
				resCondition.setConditions(resConditionsElement);
			}
			resCondition.setQueryOntoqlTable(res);
			resConditions[i] = resCondition;
		}
		res.setDisjonctiveConditions(resConditions);

		// Treatment of natural join
		ConditionElement currentConditionElement = null;
		for (int i = 0; i < naturalJoinConditionElement.size(); i++) {
			currentConditionElement = (ConditionElement) naturalJoinConditionElement.get(i);
			resTarget = removeNaturalJoin(resTarget, currentConditionElement);
			res.setTarget(resTarget);
		}

		// order by result = Merge of order by
		Object[] resOrderObject = ArrayHelper.join(this.orderBy, otherQueryOntoqlTable.orderBy);
		AbstractEntityProperty[] resOrder = new AbstractEntityProperty[resOrderObject.length];
		for (int i = 0; i < resOrderObject.length; i++) {
			resOrder[i] = (AbstractEntityProperty) resOrderObject[i];
		}
		res.setOrderBy(resOrder);
		// Must carefully handle the fact that orderByType is always initialized
		// even if not meaningfull
		if (this.orderBy.length > 0) {
			res.setOrderByType(this.orderByType);
		} else {
			res.setOrderByType(otherQueryOntoqlTable.orderByType);
		}

		return res;
	}

	/**
	 * Auxiliaire method to remove a property from the range clause when it's
	 ** involved as a right hand side property
	 * 
	 * @param properties
	 * @param cdtElt
	 * @return
	 */
	private AbstractEntityProperty[] removeNaturalJoin(AbstractEntityProperty[] properties, ConditionElement cdtElt) {
		Vector propertiesAsList = ArrayHelper.toVector(properties);

		AbstractEntityClass context = null;
		for (int i = 0; i < properties.length; i++) {
			context = (AbstractEntityClass) properties[i].getCurrentContext();
			String contextName = context.getCategoryAlias() == null ? context.getName() : context.getCategoryAlias();
			if (properties[i].getName().equals(cdtElt.getRightPropertyName())
					&& contextName.equals(cdtElt.getRightPropertyContextName())) {
				propertiesAsList.remove(i);
			}
		}
		properties = new AbstractEntityProperty[propertiesAsList.size()];
		for (int i = 0; i < propertiesAsList.size(); i++) {
			properties[i] = (AbstractEntityProperty) propertiesAsList.get(i);
		}
		return properties;
	}

	/**
	 * @return Returns the target.
	 */
	public EntityProperty[] getTarget() {
		return target;
	}

	/**
	 * @param orderBy The orderBy to set.
	 */
	public void setOrderBy(EntityProperty[] orderBy) {
		this.orderBy = orderBy;
	}

	/**
	 * @param orderByType The orderByType to set.
	 */
	public void setOrderByType(String orderByType) {
		this.orderByType = orderByType;
	}

	/**
	 * @param range The range to set.
	 */
	public void setRange(AbstractEntityClass[] range) {
		this.range = range;
	}

	/**
	 * @param polymorph The polymorph to set.
	 */
	public void setPolymorph(boolean[] polymorph) {
		this.polymorph = polymorph;
	}

	/**
	 * @return Returns the aliasTable.
	 */
	public Hashtable getAliasTable() {
		return aliasTable;
	}

	/**
	 * @return Returns the prefixGenerated.
	 */
	public boolean isPrefixGenerated() {
		return prefixGenerated;
	}

	/**
	 * @return Returns the prefixByName.
	 */
	public boolean isPrefixByName() {
		return prefixByName;
	}
}