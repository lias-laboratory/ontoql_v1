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

import fr.ensma.lisi.ontoql.core.EntityDatatypeReal;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.util.OntoDBHelper;

/**
 * The datatype int
 * 
 * @author Stéphane JEAN
 */
public class EntityDatatypeRealOntoDB extends EntityDatatypeReal {

	private static final String DEFAULT_VALUE_FORMAT = "NR2 S..3.3";

	/**
	 * A session needed to access to the underlying OBDB
	 */
	protected OntoQLSession session;

	public EntityDatatypeRealOntoDB(OntoQLSession session) {
		this.session = session;
	}

	@Override
	public String getTableName() {
		return OntoDBHelper.REAL_TYPE_TABLE;
	}

	@Override
	public String insert() throws JOBDBCException {
		try {
			String res = null;

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
		return "float8";
	}
}
