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
package fr.ensma.lisi.ontoql.core.ontodb;

import fr.ensma.lisi.ontoql.core.AbstractEntityProperty;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;

/**
 * A property representing the typeOf function.
 * 
 * @author Stéphane Jean
 */
public class EntityPropertyTypeOfOntoDB extends AbstractEntityProperty {

	public EntityPropertyTypeOfOntoDB(FactoryEntity factory) {
		this.factory = factory;
	}

	@Override
	protected String getCurrentLanguage() {
		return null;
	}

	@Override
	protected String getValueExternalId() {
		return null;
	}

	@Override
	public void setCode(String code) {
	}

	@Override
	public void setVersion(String version) {
	}

	@Override
	protected String getValueInternalId() {
		return null;
	}

	@Override
	protected String getValueName() {
		return null;
	}

	@Override
	protected String getValueName(String lg) {
		return null;
	}

	protected void intializeAttribut() {
	}

	@Override
	protected boolean isExternalIdInitialized() {
		return false;
	}

	@Override
	protected boolean isInternalIdInitialized() {
		return false;
	}

	@Override
	protected boolean isNameInitialized() {
		return false;
	}

	@Override
	protected boolean isNameInitialized(String lg) {
		return false;
	}

	@Override
	protected void load() throws JOBDBCException {
	}

	/**
	 * The range of this property is its domain
	 */
	protected void loadRange() {
		this.range = new EntityDatatypeCategoryOntoDB(currentContext);
	}

	public boolean isUsed() throws JOBDBCException {
		boolean res = true;
		if (currentContext != null && ((EntityClass) currentContext).isAbstract()) {
			res = false;
		}
		return res;
	}

	@Override
	public void setCurrentLanguage(String lg) {
	}

	@Override
	protected void setValueExternalId(String externalId) {
	}

	@Override
	protected void setValueInternalId(String internalId) {
	}

	@Override
	public void setName(String name, String lg) {
	}

	@Override
	public void setName(String name) {
	}

	@Override
	public String toSQL(Category context, boolean polymorph) {
		String res = "";
		if (context != null && context.isPolymorph()) {
			String tableAlias = context.getTableAlias();
			String alias = tableAlias == null ? ((EntityClass) context).toSQL(false) : tableAlias;
			res += alias + ".";
		} else if (context != null && !context.isPolymorph()) {
			res += "'e" + context.getInternalId() + "' as ";
		}
		res += "tablename";
		return res;
	}

	@Override
	public String getNameExtent() {
		return "tablename";
	}

	@Override
	public String getExtent() {
		return getNameExtent() + " varchar";
	}
}
