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
package fr.ensma.lisi.ontoql.ontomodel;

import java.util.ArrayList;

import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCategory;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCollection;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.ontomodel.mapping.PlibAttribute;
import fr.ensma.lisi.ontoql.util.OntoDBHelper;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Represents a map ontology attribut
 * 
 * @author Stéphane Jean
 */
public class OntoAttribute {

	private boolean isOptional;

	private EntityDatatype range;

	/**
	 * The name of this attribute
	 */
	protected String name;

	/**
	 * The name in French of this attribute
	 */
	protected String name_fr;

	/**
	 * The name of the function which compute this attribute
	 */
	private String functionName;

	/**
	 * The parameter of the function which compute this attribute
	 */
	private String functionParameter;

	/**
	 * The link between the entity of the core model and the original model
	 */
	private ArrayList link = new ArrayList();

	private PlibAttribute mapTo;

	public void addLink(PlibAttribute a) {
		link.add(a);
	}

	/**
	 * @return Returns the range.
	 */
	public EntityDatatype getRange() {
		return range;
	}

	/**
	 * @param range The range to set.
	 */
	public void setRange(EntityDatatype range) {
		this.range = range;
	}

	@Override
	public boolean equals(Object obj) {
		return name.equals(((OntoAttribute) obj).name);
	}

	/**
	 * @param name
	 */
	public OntoAttribute(String name) {
		mapTo = new PlibAttribute(name);
		this.name = name;
		this.name_fr = name;
	}

	/**
	 * @param name
	 */
	public OntoAttribute(String name, OntoEntity ofEntity) {
		this(name);
		setOfEntity(ofEntity);
		ofEntity.addDefinedAttribute(this);
	}

	/**
	 * The domain of this attribut Its contextual (what alias ...)
	 */
	protected OntoEntity ofEntity;

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return Returns the name in a given natural language
	 */
	public String getName(String lg) {
		String res = null;
		if (lg == null || lg.equals(OntoQLHelper.NO_LANGUAGE) || lg.equals(OntoQLHelper.ENGLISH)) {
			res = name;
		} else if (lg.equals(OntoQLHelper.FRENCH)) {
			res = name_fr;
		}
		return res;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	public void setName(String name, String lg) {
		if (lg.equals(OntoQLHelper.ENGLISH)) {
			this.name = name;
		} else if (lg.equals(OntoQLHelper.FRENCH)) {
			name_fr = name;
		}
	}

	public void setOfEntity(OntoEntity ofEntity) {
		this.ofEntity = ofEntity;
	}

	/**
	 * @return The name of the attribute in the database.
	 */
	public String toSQL() {
		return mapTo.toSQL();
	}

	/**
	 * @return The XML definition of this attribute in the configuration file
	 *         (ontology_model.xml).
	 */
	public String toXML() {
		String res = null;

		StringBuffer resTemp = new StringBuffer();
		// the result depends on the datatype of this attribute
		EntityDatatype range = this.getRange();
		if (range.isAssociationType()) {
			Category categoryRange = ((EntityDatatypeCategory) range).getCagetory();
			resTemp.append("\t\t<attributeRef name=\"" + name + "\" entity=\"" + categoryRange.getName() + "\"/>\n\n");
		} else if (range instanceof EntityDatatypeCollection) {
			EntityDatatype datatypeRange = ((EntityDatatypeCollection) range).getDatatype();

			resTemp.append("\t\t<attributeCollection name=\"" + name + "\"");
			if (datatypeRange.isAssociationType()) {
				Category categoryRange = ((EntityDatatypeCategory) datatypeRange).getCagetory();
				resTemp.append(" type=\"ref\" entity=\"" + categoryRange.getName() + "\"/>\n\n");
			} else {
				resTemp.append(" type=\"" + datatypeRange.getName() + "\"/>\n\n");
			}
		} else {
			resTemp.append("\t\t<attributePrimitive name=\"" + name + "\" type=\"" + range.getName() + "\"/>\n\n");
		}
		res = resTemp.toString();

		return res;
	}

	/**
	 * Create an attribute on a given entity
	 */
	public void create(OntoEntity entity, OntoQLSession session) {
		boolean isNotInherited = entity.equals(ofEntity);
		// the actions required depends on the datatype of this attribute
		EntityDatatype range = this.getRange();
		if (range.isAssociationType()) {
			// required an association table
			OntoDBHelper.createAssociationTable(entity, this, session);
			// and a new column in the table of its entity with a foreign key
			if (isNotInherited) {
				OntoDBHelper.addColumnReferencingAssociationTable(entity, this, session);
			}

		} else if (range instanceof EntityDatatypeCollection) {
			EntityDatatype datatypeRange = ((EntityDatatypeCollection) range).getDatatype();
			if (datatypeRange.isAssociationType()) {
				// required an association table
				OntoDBHelper.createAssociationTable(entity, this, session);
				// and a new column in the table of its entity without a foreign
				// key because this is not possible with array
				if (isNotInherited) {
					// only if this attribute is not inherited
					OntoDBHelper.addColumnArrayForAssociation(entity, this, session);
				}
			} else {
				// and a new column in the table for this collection
				OntoDBHelper.addColumnArray(entity, this, session);
			}
		}
	}

	/**
	 * Drop an attribute on a given entity
	 */
	public void drop(OntoEntity entity, OntoQLSession session) {

		// the actions required depends on the datatype of this attribute
		EntityDatatype range = this.getRange();
		if (range.isAssociationType()) {
			// required an association table
			OntoDBHelper.dropAssociationTable(entity, this, session);
		} else if (range instanceof EntityDatatypeCollection) {
			EntityDatatype datatypeRange = ((EntityDatatypeCollection) range).getDatatype();
			if (datatypeRange.isAssociationType()) {
				// required an association table
				OntoDBHelper.dropAssociationTable(entity, this, session);
			}
			// else nothing to do (column)
		}
	}

	/**
	 * Create the table and columns required to store values of this attribute
	 */
	public void create(OntoQLSession session) {
		create(ofEntity, session);
	}

	/**
	 * return the definition of this attribute in SQL (name datatype) or null if it
	 * can not be reprensented in SQL
	 * 
	 * @return the definition of this attribute in SQL or null if it can not be
	 *         reprensented (ref and collection type)
	 */
	public String getSQLDefinition() {
		String res = null;

		EntityDatatype range = this.getRange();
		if (!range.isAssociationType() && !(range instanceof EntityDatatypeCollection)) {
			res = getName() + " " + range.getExtent();
		}

		return res;
	}

	public PlibAttribute getMapTo() {
		return mapTo;
	}

	public void setMapTo(PlibAttribute mapTo) {
		this.mapTo = mapTo;
	}

	public ArrayList getLink() {
		return link;
	}

	public boolean isOptional() {
		return isOptional;
	}

	public void setOptional(boolean isOptional) {
		this.isOptional = isOptional;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

	public String getFunctionParameter() {
		return functionParameter;
	}

	public void setFunctionParameter(String functionParameter) {
		this.functionParameter = functionParameter;
	}

	public OntoEntity getOfEntity() {
		return ofEntity;
	}

}
