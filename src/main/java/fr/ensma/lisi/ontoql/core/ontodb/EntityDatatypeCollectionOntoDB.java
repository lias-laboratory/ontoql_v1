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
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCollection;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.util.OntoDBHelper;

/**
 * Collection type.
 *
 * @author Stéphane JEAN
 */
public class EntityDatatypeCollectionOntoDB extends AbstractEntityDatatype implements EntityDatatypeCollection {

	/**
	 * Constant for set.
	 */
	private static final String SET_TYPE = "SET_TYPE_E";

	/**
	 * Constant for list.
	 */
	private static final String LIST_TYPE = "LIST_TYPE_E";

	/**
	 * Constant for bag.
	 */
	private static final String BAG_TYPE = "BAG_TYPE_E";

	/**
	 * Constant for array.
	 */
	private static final String ARRAY_TYPE = "ARRAY_TYPE_E";

	/**
	 * A logger for this class.
	 */
	private static Log log = LogFactory.getLog(EntityDatatypeCollectionOntoDB.class);

	/**
	 * kind of aggregate : either set, list, bag or array.
	 */
	private String kindOfAggregate;

	/**
	 * Minimum bound of this collection.
	 */
	private int bound_1;

	/**
	 * Maximum bound of this collection.
	 */
	private int bound_2;

	/**
	 * The attribut datatype is stocked because it contains additional data that the
	 * ENTITY_INSTANCE_TYPE_FOR_AGGREGATE (operators allowed ...).
	 */
	private EntityDatatype datatype = null;

	/**
	 * A session needed to access to the underlying OBDB.
	 */
	private OntoQLSession session;

	/**
	 * Used to create other entity.
	 */
	private FactoryEntity factory;

	@Override
	public boolean isMultilingualType() {
		return getDatatype().isMultilingualType();
	}

	/**
	 * Constructor.
	 * 
	 * @param dt      ontolib datatype
	 * @param session access to the OBDB
	 * @param factory entity factory
	 */
	public EntityDatatypeCollectionOntoDB(OntoQLSession session, FactoryEntity factory) {
		this.session = session;
		this.factory = factory;
	}

	public EntityDatatypeCollectionOntoDB(EntityDatatype dt) {
		this.datatype = dt;
	}

	/**
	 * Is the datatype of this collection loaded ?
	 * 
	 * @return true if the datatype of this collection is loaded.
	 */
	protected boolean isLoadedDatatype() {
		return datatype != null;
	}

	@Override
	public String[] getArithmeticOperators() {
		return new String[] { OP_CONCAT };
	}

	@Override
	public String[] getBooleanOperators() {
		return new String[] { OP_EG };
	}

	@Override
	public String getName() {
		return COLLECTION_NAME;
	}

	@Override
	public boolean isAssociationType() {
		return false;
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
	public EntityDatatype getDatatype() {
		if (!isLoadedDatatype()) {
			loadDatatype();
		}
		return datatype;
	}

	@Override
	public void setDatatype(EntityDatatype d) {
		datatype = d;
	}

	/**
	 * Load the datatype of this collection.
	 */
	public void loadDatatype() {
		StringBuffer query = new StringBuffer();

		query.append("SELECT etoa.tablename_d,");
		query.append("a.bound_1,");
		query.append("a.bound_2,");
		query.append("atov.tablename_d,");
		query.append("atov.rid_d");
		query.append(" FROM entity_instance_type_for_aggregate_e e,");
		query.append("entity_instance_type_for_aggregate_2_type_structure etoa,");
		query.append("aggregate_type_e a,");
		query.append("aggregate_type_2_value_type atov ");
		query.append("WHERE e.rid = etoa.rid_s and etoa.rid_d = a.rid and a.rid = atov.rid_s");
		query.append("  AND e.rid = " + getInternalId());

//	log.info(query.toString());

		try {

			Statement st = session.connection().createStatement();
			ResultSet rs = st.executeQuery(query.toString());

			int i = 1;
			if (!rs.next()) {
				throw new JOBDBCException("Unable to load the datatype " + getInternalId());
			}

			kindOfAggregate = rs.getString(i++);
			bound_1 = rs.getInt(i++);
			bound_2 = rs.getInt(i++);
			datatype = factory.createEntityDatatype(rs.getString(i++), rs.getString(i++));

		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}

	}

	@Override
	public boolean isCollectionAssociationType() {
		return getDatatype().isAssociationType();
	}

	@Override
	public String getTableName() {
		return OntoDBHelper.COLLECTION_TYPE_TABLE;
	}

	@Override
	public String insert() throws JOBDBCException {
		try {
			String res = null;

			Connection connection = session.connection();

			String ridDatatye = getDatatype().insert();

			String ridStructure = OntoDBHelper.getSequenceNextVal(connection, "root_table_entity_rid_seq");

			String ridStructureToValueType = OntoDBHelper.insertInIntermediateTable(connection,
					"aggregate_type_2_value_type", ridStructure, "ARRAY_TYPE_E", ridDatatye,
					getDatatype().getTableName());

			String query = "insert into ARRAY_TYPE_E (rid, bound_1,value_type) values(?,?,?)";
			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, Integer.parseInt(ridStructure));
			pstmt.setInt(2, 0);
			pstmt.setInt(3, Integer.parseInt(ridStructureToValueType));
			pstmt.executeUpdate();
			log.info(query);

			res = OntoDBHelper.getSequenceNextVal(connection, "root_table_entity_rid_seq");

			String ridResToStructure = OntoDBHelper.insertInIntermediateTable(connection,
					"entity_instance_type_for_aggregate_2_type_structure", res, "ENTITY_INSTANCE_TYPE_FOR_AGGREGATE_E",
					ridStructure, "ARRAY_TYPE_E");

			query = "insert into " + getTableName() + " (rid, type_structure) values(?,?)";
			pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, Integer.parseInt(res));
			pstmt.setInt(2, Integer.parseInt(ridResToStructure));
			pstmt.executeUpdate();
			log.info(query);

			return res;

		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	@Override
	public Class getReturnedClass() {
		return Object[].class; // Must not be Object but Datatype
	}

	@Override
	public String getExtent() {
		String res = null;
		res = getDatatype().getExtent() + " " + "[]";
		return res;
	}
}
