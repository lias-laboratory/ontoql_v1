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
package fr.ensma.lisi.ontoql.core.hsqldb;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import fr.ensma.lisi.ontoql.core.AbstractEntityClass;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;

/**
 * Comment types
 *
 * @author Stéphane JEAN
 */
public class EntityClassHSQLDB extends AbstractEntityClass {

	/**
	 * A session needed to access to the underlying OBDB
	 */
	protected OntoQLSession session;

	public EntityClassHSQLDB(String id, String namespace, OntoQLSession session, FactoryEntity factory) {
		super(id, namespace, session.getReferenceLanguage(), factory);
		this.session = session;
	}

	public int createProperty(EntityProperty prop, OntoQLSession session) {
		return 0;
	}

	public EntityClassHSQLDB(String id, OntoQLSession session, FactoryEntity factory) {
		this(id, session.getDefaultNameSpace(), session, factory);
	}

	@Override
	public void dropProperty(EntityProperty prop, OntoQLSession session) {
	}

	@Override
	protected String getCurrentLanguage() {
		return session.getReferenceLanguage();
	}

	@Override
	public void setCurrentLanguage(String lg) {
		session.setReferenceLanguage(lg);
	}

	@Override
	protected void load() throws JOBDBCException {
	}

	@Override
	public void insert() throws JOBDBCException {
		try {
			String query = "insert into class (oid, code, version, name_fr, name_en, definition_fr, definition_en) values (?,?,?,?,?,?,?) ";
			PreparedStatement pst = session.connection().prepareStatement(query);
			pst.setInt(1, 1);
			pst.setString(2, code);
			pst.setString(3, version);
			pst.setString(4, name_fr);
			pst.setString(5, name_en);
			pst.setString(6, definition_fr);
			pst.setString(7, definition_en);
			pst.executeUpdate();
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}

	}

	@Override
	public String getNameExtent() throws JOBDBCException {
		return null;
	}

	@Override
	public void createTable(EntityProperty[] propertiesExtent) throws JOBDBCException {
	}

	@Override
	public void addColumnToTable(EntityProperty propertyToAdd) throws JOBDBCException {
	}

	@Override
	protected void loadDefinedProperties() {
	}

	@Override
	protected void loadDirectSubclasses() {
	}

	@Override
	protected void loadUsedProperties() {
	}

	@Override
	public String toSQL(boolean polymorph) throws JOBDBCException {
		return null;
	}

	@Override
	public String project(EntityProperty[] properties, boolean polymorph) throws JOBDBCException {
		return null;
	}

	@Override
	public void createView(EntityProperty[] propertiesExtent, String query) throws JOBDBCException {
	}
}
