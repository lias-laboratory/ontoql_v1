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

import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.util.OntoDBHelper;

/**
 * OntoMultilingualAttribute
 *
 * @author Stéphane JEAN
 */
public class OntoMultilingualAttribute extends OntoAttribute {

	/**
	 * True if this type is used for a label (short text) False for a text.
	 */
	private boolean isLabel;

	/**
	 * @param name
	 */
	public OntoMultilingualAttribute(String name, boolean isLabel) {
		super(name);
		this.isLabel = isLabel;
	}

	public boolean isLabel() {
		return isLabel;
	}

	public void setLabel(boolean isLabel) {
		this.isLabel = isLabel;
	}

	/**
	 * @param name
	 */
	public OntoMultilingualAttribute(String name, boolean isLabel, OntoEntity entity) {
		super(name, entity);
		this.isLabel = isLabel;
	}

	/**
	 * @param name
	 */
	public OntoMultilingualAttribute(String name) {
		super(name);
	}

	/**
	 * return the definition of this attribute in SQL (name datatype) or null if it
	 * can not be reprensented in SQL
	 * 
	 * @return the definition of this attribute in SQL or null if it can not be
	 *         reprensented (ref and collection type)
	 */
	public String getSQLDefinition() {
		return null;
		// a multilingual attribute can not be represented in SQL
	}

	/**
	 * Create the table and columns required to store values of this attribute This
	 * attribute must
	 */
	public void create(OntoEntity entity, OntoQLSession session) {
		boolean isNotInherited = entity.equals(ofEntity);
		if (isNotInherited) {
			// required an association table
			OntoDBHelper.createAssociationTable(entity, this, session);
			// and a new column in the table of its entity with a foreign key
			OntoDBHelper.addColumnReferencingAssociationTable(this.ofEntity, this, session);
		}
	}

	/**
	 * Drop the table and columns required to store values of this attribute This
	 * attribute must
	 */
	public void drop(OntoEntity entity, OntoQLSession session) {
		boolean isNotInherited = entity.equals(ofEntity);
		if (isNotInherited) {
			// required an association table
			OntoDBHelper.dropAssociationTable(entity, this, session);
		}
	}

	/**
	 * Add this attribute to the meta-schema.
	 * 
	 * @return The XML definition of this attribute in the configuration file
	 *         (ontology_model.xml).
	 */
	public String toXML() {
		String res = null;

		StringBuffer resTemp = new StringBuffer();
		resTemp.append("\t\t<attributePrimitive name=\"" + name
				+ "\" type=\"String\" optional=\"true\" multilingual=\"true\" label=\"" + isLabel + "\"/>\n\n");
		res = resTemp.toString();

		return res;
	}
}
