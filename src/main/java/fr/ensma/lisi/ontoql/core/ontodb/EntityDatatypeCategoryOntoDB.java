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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ensma.lisi.ontoql.core.AbstractEntityDatatype;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCategory;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.util.OntoDBHelper;

/**
 * Datatype for relationship.
 * 
 * @author Stéphane JEAN
 */
public class EntityDatatypeCategoryOntoDB extends AbstractEntityDatatype implements EntityDatatypeCategory {

	private static final Log log = LogFactory.getLog(EntityDatatypeCategoryOntoDB.class);

	/**
	 * Used to create other entity.
	 */
	private FactoryEntity factory;

	/**
	 * The attribut category is stocked because it contains additional data that the
	 * CLASS_INSTANCE_TYPE (alias ..).
	 */
	protected Category category = null;

	/**
	 * Is the class of this datatype loaded
	 */
	protected boolean isLoadedCategory() {
		return category != null;
	}

	/**
	 * A session needed to access to the underlying OBDB.
	 */
	private OntoQLSession session;

	/**
	 * Constructor.
	 * 
	 * @param dt      ontolib datatype
	 * @param session access to the OBDB
	 * @param factory entity factory
	 */
	public EntityDatatypeCategoryOntoDB(OntoQLSession session, FactoryEntity factory) {
		this.session = session;
		this.factory = factory;
	}

	public EntityDatatypeCategoryOntoDB(Category dt) {
		this.category = dt;
	}

	@Override
	public String[] getBooleanOperators() {
		return new String[] { OP_EG };
	}

	@Override
	public String[] getArithmeticOperators() {
		return new String[] {};
	}

	@Override
	public Category getCagetory() {
		if (!isLoadedCategory()) {
			loadCategory();
		}
		return category;
	}

	@Override
	public void setCategory(Category c) {
		this.category = c;
	}

	/**
	 * Load the class range
	 */
	public void loadCategory() {

		// TODO select dt.#class from #datatypeClass dt

		StringBuffer query = new StringBuffer();
		query.append("SELECT ctod.rid_d, supplier.code ");
		query.append("FROM class_instance_type_2_domain ctod, class_bsu_e cbsu, supplier_bsu_e supplier ");
		query.append(
				"WHERE ctod.rid_d = cbsu.rid and cbsu.rid_supplier = supplier.rid and ctod.rid_s = " + getInternalId());

		log.debug(query.toString());

		try {

			Statement st = session.connection().createStatement();
			ResultSet rs = st.executeQuery(query.toString());

			int i = 1;
			if (!rs.next()) {
				throw new JOBDBCException("Unable to load the datatype " + getInternalId());
			}

			category = (EntityClass) factory.createCategory("!" + rs.getString(i++) + "", rs.getString(i++));

		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}

	}

	@Override
	public String getName() {
		return ASSOCIATION_NAME;
	}

	@Override
	public String valueToOntoql(String value) {
		return value;
	}

	@Override
	public String ontoQLToValue(String value) {
		return value;
	}

	@Override
	public boolean isAssociationType() {
		return true;
	}

	@Override
	public boolean isCollectionAssociationType() {
		return false;
	}

	@Override
	public String getTableName() {
		return OntoDBHelper.ASSOCIATION_TYPE_TABLE;
	}

	@Override
	public String insert() throws JOBDBCException {
		try {
			String res = null;

			Connection connection = session.connection();

			res = OntoDBHelper.getSequenceNextVal(connection, "root_table_entity_rid_seq");

			String ridResToDomain = OntoDBHelper.insertInIntermediateTable(connection, "CLASS_INSTANCE_TYPE_2_DOMAIN",
					res, "CLASS_INSTANCE_TYPE_E", category.getInternalId(), "CLASS_BSU_E");

			String query = "insert into " + getTableName() + " (rid, domain) values(?,?)";
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, Integer.parseInt(res));
			pstmt.setInt(2, Integer.parseInt(ridResToDomain));
			pstmt.executeUpdate();

			return res;

		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	@Override
	public Class getReturnedClass() {
		return getCagetory().getClass();
	}

	@Override
	public String getExtent() {
		return "int8";
	}
}
