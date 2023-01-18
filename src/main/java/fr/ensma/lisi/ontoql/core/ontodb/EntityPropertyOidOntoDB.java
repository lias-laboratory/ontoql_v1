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
import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;

/**
 * 
 * @author Stéphane JEAN
 */
public class EntityPropertyOidOntoDB extends AbstractEntityProperty {

	@Override
	public String toSQL(Category context, boolean polymorph) {
		String res = "NULL ";
		if (context != null) {
			boolean isContextPolymorph = ((EntityClass) context).isPolymorph();
			boolean isOidUsed; //
			if (isContextPolymorph) {
				isOidUsed = ((EntityClass) context).getUsedPropertiesPolymorph().length > 0;
			} else {
				isOidUsed = ((EntityClass) context).getUsedProperties().length > 0;
			}
			if (isOidUsed) {
				String tableAlias = context.getTableAlias();
				String alias = tableAlias == null ? ((EntityClass) context).toSQL(false) : tableAlias;
				res = alias + ".rid";
			}
		} else {
			res = "rid";
		}
		return res;
	}

	@Override
	protected String getCurrentLanguage() {
		return null;
	}

	public EntityPropertyOidOntoDB(FactoryEntity factory) {
		this.factory = factory;
	}

	@Override
	public boolean isUsed() throws JOBDBCException {
		boolean res = true;
		if (currentContext != null && ((EntityClass) currentContext).isAbstract()) {
			res = false;
		}
		return res;
	}

	@Override
	public boolean isDefined() throws JOBDBCException {
		return true;
	}

	@Override
	public void setCurrentLanguage(String lg) {
	}

	@Override
	protected String getValueInternalId() {
		return "oid";
	}

	@Override
	protected String getValueExternalId() {
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

	@Override
	protected void setValueInternalId(String internalId) {
	}

	@Override
	protected void setValueExternalId(String externalId) {
	}

	@Override
	public void setName(String name) {
	}

	@Override
	public void setName(String name, String lg) {
	}

	@Override
	protected boolean isInternalIdInitialized() {
		return false;
	}

	@Override
	protected boolean isExternalIdInitialized() {
		return false;
	}

	@Override
	protected void load() throws JOBDBCException {
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
	protected void loadRange() {
		range = factory.createEntityDatatype(EntityDatatype.INT_NAME);
	}

	protected void intializeAttribut() {
	}

	@Override
	public String toString() {
		return "oid";
	}

	@Override
	public void setCode(String code) {
	}

	@Override
	public void setVersion(String version) {
	}

	@Override
	public String getNameExtent() {
		return "rid";
	}

	@Override
	public String getExtent() {
		return getNameExtent() + " int8";
	}
}
