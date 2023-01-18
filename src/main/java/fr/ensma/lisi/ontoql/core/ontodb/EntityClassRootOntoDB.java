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

import fr.ensma.lisi.ontoql.core.AbstractEntityClass;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;

/**
 *
 * @author Stéphane JEAN
 */
public class EntityClassRootOntoDB extends AbstractEntityClass {

	@Override
	public void addColumnToTable(EntityProperty propertyToAdd) throws JOBDBCException {
	}

	public void dropProperty(EntityProperty prop, OntoQLSession session) {
	}

	public EntityClassRootOntoDB(FactoryEntity factory) {
		super(null, null, null, factory);
	}

	@Override
	public void createTable(EntityProperty[] propertiesExtent) throws JOBDBCException {
	}

	@Override
	protected String getCurrentLanguage() {
		return null;
	}

	@Override
	public String getNameExtent() throws JOBDBCException {
		return null;
	}

	@Override
	protected String getValueExternalId() {
		return null;
	}

	@Override
	protected String getValueInternalId() {
		// used to generate an alias
		return "root";
	}

	@Override
	protected String getValueName() {
		return "RootClass";
	}

	@Override
	protected String getValueName(String lg) {
		return "RootClass";
	}

	@Override
	public void insert() throws JOBDBCException {
		// EntityClassOntoDB delegate = new EntityClassOntoDB("RootClass",
		// ((FactoryOntoDB)factory).session, factory);
		// delegate.insert();
	}

	@Override
	public int createProperty(EntityProperty prop, OntoQLSession session) {
		return 0;
	}

	@Override
	protected boolean isExternalIdInitialized() {
		return true;
	}

	@Override
	protected boolean isInternalIdInitialized() {
		return true;
	}

	@Override
	protected boolean isNameInitialized() {
		return true;
	}

	@Override
	protected boolean isNameInitialized(String lg) {
		return true;
	}

	@Override
	protected void load() throws JOBDBCException {
	}

	@Override
	protected void loadDefinedProperties() {
		definedProperties = new EntityProperty[0];
	}

	@Override
	protected void loadDirectSubclasses() {
	}

	@Override
	protected void loadUsedProperties() {
		// the oid is used
		usedProperties = new EntityProperty[1];
	}

	@Override
	public String project(EntityProperty[] properties, boolean polymorph) throws JOBDBCException {
		return null;
	}

	@Override
	public void setCode(String code) {
	}

	@Override
	public void setCurrentLanguage(String lg) {
	}

	@Override
	public void setName(String name, String lg) {
	}

	@Override
	public void setName(String name) {
	}

	@Override
	public void setSuperClass(EntityClass superClass) {
	}

	@Override
	protected void setValueExternalId(String externalId) {
	}

	@Override
	protected void setValueInternalId(String internalId) {
	}

	@Override
	public void setVersion(String version) {
	}

	@Override
	public String toSQL(boolean polymorph) throws JOBDBCException {
		String res = "";

		String alias = tableAlias == null ? "" : " " + tableAlias;
		res = polymorph ? "instances_polymorph" : "instances";
		res += alias;

		return res;
	}

	@Override
	public void createView(EntityProperty[] propertiesExtent, String query) throws JOBDBCException {
	}
}
