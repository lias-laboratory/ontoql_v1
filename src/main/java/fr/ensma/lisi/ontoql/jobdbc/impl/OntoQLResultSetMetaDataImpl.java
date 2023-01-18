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
package fr.ensma.lisi.ontoql.jobdbc.impl;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;

import fr.ensma.lisi.ontoql.core.Description;
import fr.ensma.lisi.ontoql.engine.tree.IdentNode;
import fr.ensma.lisi.ontoql.engine.tree.dql.SelectExpression;
import fr.ensma.lisi.ontoql.exception.NotSupportedDatatypeException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSetMetaData;

/**
 * @author Stéphane JEAN
 */
public class OntoQLResultSetMetaDataImpl implements OntoQLResultSetMetaData {

	/**
	 * List of used properties in select
	 */
	private List propertiesInSelect;

	/**
	 * Delegate
	 */
	private ResultSetMetaData resultSetMetaDataDelegate;

	public OntoQLResultSetMetaDataImpl(ResultSetMetaData resultSetMetaDataDelegate, List propertiesInSelect) {
		this.resultSetMetaDataDelegate = resultSetMetaDataDelegate;
		this.propertiesInSelect = propertiesInSelect;
	}

	@Override
	public int hashCode() {
		return resultSetMetaDataDelegate.hashCode();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public String getTableName(int column) throws SQLException {
		return resultSetMetaDataDelegate.getTableName(column);
	}

	@Override
	public String toString() {
		return resultSetMetaDataDelegate.toString();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public String getColumnTypeName(int column) throws SQLException {
		String res = "NOT SUPPORTED";
		try {
			res = ((SelectExpression) propertiesInSelect.get(column - 1)).getDataType().getName();
		} catch (NotSupportedDatatypeException oExc) {

		}
		return res;
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public boolean isSearchable(int column) throws SQLException {
		return resultSetMetaDataDelegate.isSearchable(column);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public int getColumnCount() throws SQLException {
		return propertiesInSelect.size();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public int getPrecision(int column) throws SQLException {
		return resultSetMetaDataDelegate.getPrecision(column);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public boolean isReadOnly(int column) throws SQLException {
		return resultSetMetaDataDelegate.isReadOnly(column);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public String getColumnName(int column) throws SQLException {
		String res;

		SelectExpression exprInColumn = ((SelectExpression) propertiesInSelect.get(column - 1));
		String alias = exprInColumn.getAlias();
		if (alias == null) {
			res = exprInColumn.getLabel();
		} else {
			res = alias;
		}

		return res;
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public boolean isAutoIncrement(int column) throws SQLException {
		return resultSetMetaDataDelegate.isAutoIncrement(column);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public boolean isDefinitelyWritable(int column) throws SQLException {
		return resultSetMetaDataDelegate.isDefinitelyWritable(column);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public int isNullable(int column) throws SQLException {
		return resultSetMetaDataDelegate.isNullable(column);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public boolean isCaseSensitive(int column) throws SQLException {
		return resultSetMetaDataDelegate.isCaseSensitive(column);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public boolean isCurrency(int column) throws SQLException {
		return resultSetMetaDataDelegate.isCurrency(column);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public String getColumnLabel(int column) throws SQLException {
		// The test I have performed let me conclude
		// that this method return the same value as getColumnName
		return getColumnName(column);
	}

	public String getSchemaName(int column) throws SQLException {
		return resultSetMetaDataDelegate.getSchemaName(column);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return resultSetMetaDataDelegate.equals(obj);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public String getCatalogName(int column) throws SQLException {
		return resultSetMetaDataDelegate.getCatalogName(column);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public String getColumnClassName(int column) throws SQLException {
		return resultSetMetaDataDelegate.getColumnClassName(column);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public boolean isSigned(int column) throws SQLException {
		return resultSetMetaDataDelegate.isSigned(column);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public int getScale(int column) throws SQLException {
		return resultSetMetaDataDelegate.getScale(column);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public boolean isWritable(int column) throws SQLException {
		return resultSetMetaDataDelegate.isWritable(column);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public int getColumnType(int column) throws SQLException {
		return resultSetMetaDataDelegate.getColumnType(column);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public int getColumnDisplaySize(int column) throws SQLException {
		return resultSetMetaDataDelegate.getColumnDisplaySize(column);
	}

	public Description getDescription(int column) throws SQLException {
		return ((IdentNode) propertiesInSelect.get(column - 1)).getDescription();
	}

	public boolean isWrapperFor(Class arg0) throws SQLException {
		return resultSetMetaDataDelegate.isWrapperFor(arg0);
	}

	public Object unwrap(Class arg0) throws SQLException {
		return resultSetMetaDataDelegate.unwrap(arg0);
	}
}
