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
import java.sql.SQLException;
import java.sql.Statement;

import fr.ensma.lisi.ontoql.core.EntityDatatypeCount;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.util.OntoDBHelper;

/**
 * The datatype int.
 * 
 * @author Stéphane JEAN
 */
public class EntityDatatypeCountOntoDB extends EntityDatatypeCount {

	private static final String DEFAULT_VALUE_FORMAT = "NR1 S..4";

	/**
	 * A session needed to access to the underlying OBDB
	 */
	protected OntoQLSession session;

	public EntityDatatypeCountOntoDB(OntoQLSession session) {
		this.session = session;
	}

	@Override
	public String getTableName() {
		return OntoDBHelper.COUNT_TYPE_TABLE;
	}

	@Override
	public String insert() throws JOBDBCException {
		try {
			String res = null;

			@SuppressWarnings("deprecation")
			Connection connection = session.connection();

			res = OntoDBHelper.getSequenceNextVal(connection, "root_table_entity_rid_seq");
			String query = "insert into " + getTableName() + " (rid, value_format) values(?,?)";

			PreparedStatement pstmt = connection.prepareStatement(query);
			pstmt.setInt(1, Integer.parseInt(res));
			pstmt.setString(2, DEFAULT_VALUE_FORMAT);
			pstmt.executeUpdate();

			return res;

		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	@Override
	public String getExtent() {
		return "int8";
	}

	@Override
	public void postInsert(String propertyIdentifier) throws JOBDBCException {
		try {
			@SuppressWarnings("deprecation")
			Connection connection = session.connection();

			String query = "CREATE SEQUENCE counttype_p" + propertyIdentifier + "_seq " + "START WITH 1 "
					+ "INCREMENT BY 1 " + "NO MAXVALUE " + "NO MINVALUE " + "CACHE 1;";

			Statement createStatement = connection.createStatement();
			createStatement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();

			throw new JOBDBCException(e);
		}
	}

	@Override
	public String getExtent(String propertyIdentifier) throws JOBDBCException {
		return this.getExtent() + " DEFAULT nextval('public.counttype_p" + propertyIdentifier + "_seq'::text)";
	}
}
