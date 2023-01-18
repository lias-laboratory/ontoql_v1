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

import java.sql.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import fr.ensma.lisi.ontoql.core.AbstractEntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeEnumerate;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.util.JDBCHelper;
import fr.ensma.lisi.ontoql.util.OntoDBHelper;

/**
 * Enumerate type.
 * 
 * @author Stéphane JEAN
 * 
 */
public class EntityDatatypeEnumerateOntoDB extends AbstractEntityDatatype implements EntityDatatypeEnumerate {

	private List<String> values = null;

	/**
	 * A session needed to access to the underlying OBDB
	 */
	protected OntoQLSession session;

	/**
	 * Is the value of this datatype loaded
	 */
	protected boolean isLoadedValues() {
		return values != null;
	}

	@Override
	public String getName() {
		return "NON_QUANTITATIVE_CODE_TYPE";
	}

	public EntityDatatypeEnumerateOntoDB(OntoQLSession session) {
		this.session = session;
	}

	@Override
	public String[] getBooleanOperators() {
		return new String[] { OP_IN, OP_LIKE, OP_EG, OP_SUP, OP_INF, OP_SUPEG, OP_INFEG };
	}

	@Override
	public String[] getArithmeticOperators() {
		return new String[] { OP_CONCAT };
	}

	@Override
	public List<String> getValues() {
		if (!isLoadedValues()) {
			loadValues();
		}
		return values;
	}

	@Override
	public String valueToOntoql(String value) {
		return "'" + value + "'";
	}

	@Override
	public String ontoQLToValue(String value) {
		return value.substring(1, value.length() - 1);
	}

	private void loadValues() {
		StringBuffer query = new StringBuffer();
		query.append("SELECT value");
		query.append(" FROM value_code_type_t as v,");
		query.append("dic_value_2_value_code as dtov,");
		query.append("value_domain_2_its_values as vdtov,");
		query.append("non_quantitative_code_type_2_domain as nqcttod ");
		query.append(" WHERE v.rid = dtov.rid_d and ");
		query.append(" dtov.rid_s = vdtov.rid_d and ");
		query.append(" vdtov.rid_s = nqcttod.rid_d and ");
		query.append(" nqcttod.rid_s = " + getInternalId() + "order by value");

		try {
			Statement st = session.connection().createStatement();
			ResultSet rs = st.executeQuery(query.toString());
			List<String> valuesList = new ArrayList<String>();
			while (rs.next()) {
				String value = rs.getString(1);
				valuesList.add(value);
			}

			values = valuesList;

			rs.close();
			st.close();

		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}

	}

	// Get a generated Rid using the sequence for each value
	public List<String> getSequenceRids(OntoQLSession session, List<String> pValues) throws SQLException {
		List<String> res = new ArrayList<String>();
		for (int i = 0; i < pValues.size(); i++) {
			res.add(OntoDBHelper.getSequenceNextVal(session.connection(), "root_table_entity_rid_seq"));
		}
		return res;
	}

	@Override
	public String getTableName() {
		return OntoDBHelper.ENUMERATE_TYPE_TABLE;
	}

	@Override
	public void addValues(List<String> p) {
		try {
			// Get one rid for each value
			List<String> ridsValueCode = getSequenceRids(session, p);

			// Insert each value in the Value_Code_Type table
			String insertStatement;
			PreparedStatement pst;

			for (int i = 0; i < p.size(); i++) {
				insertStatement = "insert into value_code_type_t (rid, value) values (?,?)";
				pst = session.connection().prepareStatement(insertStatement);

				pst.setInt(1, Integer.parseInt(ridsValueCode.get(i)));
				pst.setString(2, p.get(i));
				pst.executeUpdate();
			}

			// Get rids for inserting in Dic_Value.
			List<String> ridsDicValue = getSequenceRids(session, p);

			// Insert in the intermediate table Dic_Value_2_Value_Code.
			List<String> ridsDicValue2ValueCode = new ArrayList<String>();
			for (int i = 0; i < p.size(); i++) {
				ridsDicValue2ValueCode
						.add(OntoDBHelper.insertInIntermediateTable(session.connection(), "Dic_Value_2_Value_Code",
								ridsDicValue.get(i), "DIC_VALUE_E", ridsValueCode.get(i), "VALUE_CODE_TYPE_E"));
			}

			// Insert in Dic_Value.
			for (int i = 0; i < p.size(); i++) {
				insertStatement = "insert into dic_value_e (rid, value_code) values (?,?)";
				pst = session.connection().prepareStatement(insertStatement);
				pst.setInt(1, Integer.parseInt(ridsDicValue.get(i)));
				pst.setInt(2, Integer.parseInt(ridsDicValue2ValueCode.get(i)));
				pst.executeUpdate();
			}

			// Search the value_domain
			ResultSet rs = session.connection().createStatement().executeQuery(
					"select rid_d from non_quantitative_code_type_2_domain where rid_s = " + getInternalId());
			rs.next();
			String ridValueDomain = rs.getString(1);

			// Insert in the intermediate table Value_Domain_2_Its_Values
			String[] ridsValueDomain2ItsValues = new String[p.size()];
			for (int i = 0; i < p.size(); i++) {
				ridsValueDomain2ItsValues[i] = OntoDBHelper.insertInIntermediateTable(session.connection(),
						"Value_Domain_2_Its_Values", ridValueDomain, "VALUE_DOMAIN_E", ridsDicValue.get(i),
						"DIC_VALUE_E");
			}

			values = null;
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	@Override
	public String insert() throws JOBDBCException {
		try {
			// Get one rid for each value
			List<String> ridsValueCode = getSequenceRids(session, values);

			// Insert each value in the Value_Code_Type table
			String insertStatement;
			PreparedStatement pst;
			for (int i = 0; i < values.size(); i++) {
				insertStatement = "insert into value_code_type_t (rid, value) values (?,?)";
				pst = session.connection().prepareStatement(insertStatement);

				pst.setInt(1, Integer.parseInt(ridsValueCode.get(i)));
				pst.setString(2, values.get(i));
				pst.executeUpdate();
			}

			// Get rids for inserting in Dic_Value.
			List<String> ridsDicValue = getSequenceRids(session, values);

			// Insert in the intermediate table Dic_Value_2_Value_Code.
			List<String> ridsDicValue2ValueCode = new ArrayList<String>();
			for (int i = 0; i < values.size(); i++) {
				ridsDicValue2ValueCode
						.add(OntoDBHelper.insertInIntermediateTable(session.connection(), "Dic_Value_2_Value_Code",
								ridsDicValue.get(i), "DIC_VALUE_E", ridsValueCode.get(i), "VALUE_CODE_TYPE_E"));
			}

			// Insert in Dic_Value.
			for (int i = 0; i < values.size(); i++) {
				insertStatement = "insert into dic_value_e (rid, value_code) values (?,?)";
				pst = session.connection().prepareStatement(insertStatement);
				pst.setInt(1, Integer.parseInt(ridsDicValue.get(i)));
				pst.setInt(2, Integer.parseInt(ridsDicValue2ValueCode.get(i)));
				pst.executeUpdate();
			}

			// Get one rid for inserting in Value_Domain.
			String ridValueDomain = OntoDBHelper.getSequenceNextVal(session.connection(), "root_table_entity_rid_seq");

			// Insert in the intermediate table Value_Domain_2_Its_Values
			String[] ridsValueDomain2ItsValues = new String[values.size()];
			for (int i = 0; i < values.size(); i++) {
				ridsValueDomain2ItsValues[i] = OntoDBHelper.insertInIntermediateTable(session.connection(),
						"Value_Domain_2_Its_Values", ridValueDomain, "VALUE_DOMAIN_E", ridsDicValue.get(i),
						"DIC_VALUE_E");
			}

			// Insert in Value_Domain
			insertStatement = "insert into value_domain_e (rid, its_values) values (?,?)";
			pst = session.connection().prepareStatement(insertStatement);
			pst.setInt(1, Integer.parseInt(ridValueDomain));
			Array arrayRidsValueDomain2ItsValues = JDBCHelper.convertToIntegerArray(ridsValueDomain2ItsValues);
			pst.setArray(2, arrayRidsValueDomain2ItsValues);
			pst.executeUpdate();

			// Get one rid for inserting in Non_Quantitative_Code_Type
			String ridNonQuantitativeCodeType = OntoDBHelper.getSequenceNextVal(session.connection(),
					"root_table_entity_rid_seq");

			// Insert in the intermediate table Non_Quantitative_Code_Type_2_Domain
			String ridNonQuantitativeCodeType2Domain = OntoDBHelper.insertInIntermediateTable(session.connection(),
					"Non_Quantitative_Code_Type_2_Domain", ridNonQuantitativeCodeType, "NON_QUANTITATIVE_CODE_TYPE_E",
					ridValueDomain, "VALUE_DOMAIN_E");

			// Insert the non quantitative code type
			insertStatement = "insert into Non_Quantitative_Code_Type_e (rid, domain) values (?,?)";
			pst = session.connection().prepareStatement(insertStatement);
			pst.setInt(1, Integer.parseInt(ridNonQuantitativeCodeType));
			pst.setInt(2, Integer.parseInt(ridNonQuantitativeCodeType2Domain));
			pst.executeUpdate();
			return ridNonQuantitativeCodeType;
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	@Override
	public boolean isAssociationType() {
		return false;
	}

	@Override
	public boolean isCollectionAssociationType() {
		return false;
	}

	@Override
	public Class getReturnedClass() {
		return String[].class;
	}

	@Override
	public String getExtent() {
		return "varchar";
	}

	@Override
	public void setValues(List<String> p) {
		this.values = p;
	}

	@Override
	public void addValue(String p) {
		if (p == null) {
			return;
		}

		List<String> values = new ArrayList<String>();
		values.add(p);

		this.addValues(values);
	}
}
