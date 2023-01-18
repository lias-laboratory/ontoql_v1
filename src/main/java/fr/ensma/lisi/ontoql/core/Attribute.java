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

import java.util.ArrayList;

import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.ontomodel.OntoAttribute;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Represents an ontology attribut as part of an entity.
 * 
 * @author Stéphane Jean
 */
public class Attribute implements Description {

	/**
	 * The definition of this attribut is given in the mapping.
	 */
	private OntoAttribute mapAttribut;

	/**
	 * The domain of this attribut It is contextual (alias ...).
	 */
	private Entity entityContext;

	/**
	 * The namespace of this attribute.
	 */
	protected String namespace;

	protected String lgCode;

	/**
	 * @return Returns the lgCode.
	 */
	public String getLgCode() {
		return lgCode;
	}

	@Override
	public void setLgCode(String lgCode) {
		this.lgCode = lgCode;
	}

	@Override
	public EntityDatatype getRange() {
		return mapAttribut.getRange();
	}

	@Override
	public void setRange(EntityDatatype datatype) {
		mapAttribut.setRange(datatype);
	}

	public Attribute(String nameMapAttribut) {
		this(nameMapAttribut, OntoQLHelper.NAMESPACE_ONTOLOGY_MODEL);
	}

	public Attribute(String nameMapAttribut, String namespace) {
		mapAttribut = new OntoAttribute(nameMapAttribut);
		this.namespace = namespace;
	}

	public Attribute(OntoAttribute aMapAttribut) {
		mapAttribut = aMapAttribut;
	}

	@Override
	public String getName() {
		return mapAttribut.getName();
	}

	@Override
	public Category getCurrentContext() {
		return entityContext;
	}

	@Override
	public void setCurrentContext(Category persistentEntity) {
		this.entityContext = (Entity) persistentEntity;
	}

	public OntoAttribute getMapAttribut() {
		return mapAttribut;
	}

	public boolean isMapToFunction() {
		return (mapAttribut.getFunctionName() != null);
	}

	public OntoAttribute search(Entity context) {
		OntoAttribute res = null;
		ArrayList listAttributs = context.getMapAttributs();
		int indexOfAttribut = listAttributs.indexOf(this.mapAttribut);

		if (indexOfAttribut != -1) {
			res = (OntoAttribute) listAttributs.get(indexOfAttribut);
			mapAttribut = res;
		}

		return res;
	}

	@Override
	public String toSQL() {
		return toSQL((Entity) getCurrentContext());
	}

	@Override
	public String toSQL(Category context) {
		String res = "";
		String alias = null;
		if (context != null) {
			String tableAlias = context.getTableAlias();
			alias = tableAlias == null ? context.toSQL() : tableAlias;
			res += alias + ".";
		}
		if (isMapToFunction()) {
			res = getMapAttribut().getFunctionName() + "(" + res + getMapAttribut().getFunctionParameter() + ")";
		} else {
			res += mapAttribut.toSQL();
		}

		return res;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String getInternalId() throws JOBDBCException {
		return getName();
	}

	@Override
	public boolean isDefined() {
		return isDefined(entityContext);
	}

	@Override
	public boolean isDefined(Category context) {
		return search((Entity) context) != null;
	}

	public void setMapAttribut(OntoAttribute mapAttribut) {
		this.mapAttribut = mapAttribut;
	}

	@Override
	public boolean isProperty() {
		return false;
	}

	@Override
	public boolean isAttribute() {
		return true;
	}

	@Override
	public String getTypeLabel() {
		return "attribute";
	}

	@Override
	public void checkExistence() {
		// TODO for the moment no means to check that without
		// browsing all the entities ...
	}

	@Override
	public boolean isMultilingualDescription() {
		return false;
	}

}
