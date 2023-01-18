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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.Entity;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCategory;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCollection;
import fr.ensma.lisi.ontoql.core.EntityDatatypeInt;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.engine.tree.IdentNode;
import fr.ensma.lisi.ontoql.engine.tree.dql.SelectExpression;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.NotSupportedDatatypeException;
import fr.ensma.lisi.ontoql.exception.NotYetImplementedException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSetMetaData;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.ontoapi.Instance;
import fr.ensma.lisi.ontoql.ontoapi.OntoBooleanType;
import fr.ensma.lisi.ontoql.ontoapi.OntoClass;
import fr.ensma.lisi.ontoql.ontoapi.OntoCollectionType;
import fr.ensma.lisi.ontoql.ontoapi.OntoConcept;
import fr.ensma.lisi.ontoql.ontoapi.OntoContextProperty;
import fr.ensma.lisi.ontoql.ontoapi.OntoDatatype;
import fr.ensma.lisi.ontoql.ontoapi.OntoDependentProperty;
import fr.ensma.lisi.ontoql.ontoapi.OntoDocument;
import fr.ensma.lisi.ontoql.ontoapi.OntoGraphics;
import fr.ensma.lisi.ontoql.ontoapi.OntoIntType;
import fr.ensma.lisi.ontoql.ontoapi.OntoNonDependentProperty;
import fr.ensma.lisi.ontoql.ontoapi.OntoNumberType;
import fr.ensma.lisi.ontoql.ontoapi.OntoOntology;
import fr.ensma.lisi.ontoql.ontoapi.OntoPrimitiveType;
import fr.ensma.lisi.ontoql.ontoapi.OntoProperty;
import fr.ensma.lisi.ontoql.ontoapi.OntoRealMeasureType;
import fr.ensma.lisi.ontoql.ontoapi.OntoRealType;
import fr.ensma.lisi.ontoql.ontoapi.OntoRefType;
import fr.ensma.lisi.ontoql.ontoapi.OntoStringType;
import fr.ensma.lisi.ontoql.util.ArrayHelper;
import fr.ensma.lisi.ontoql.util.StringHelper;

/**
 * Implementation of the <code>OntoQL</code> interface
 * 
 * @author Stéphane JEAN
 */
public class OntoQLResultSetImpl implements OntoQLResultSet {

	/**
	 * Return the SPARQL QueryResults XML Format corresponding to this resultset
	 * 
	 * @exception JOBDBCException if a database access error occurs
	 */
	public String toSPARQLQueryResultsXMLFormat() throws JOBDBCException {
		try {
			beforeFirst();
			StringBuffer XMLFormat = new StringBuffer("<?xml version=\"1.0\"?>\n");
			XMLFormat.append("<sparql xmlns=\"http://www.w3.org/2005/sparql-results#\">\n");
			XMLFormat.append("\t<head>\n");

			ResultSetMetaData rsma = getMetaData();
			int nb_col = rsma.getColumnCount();
			for (int i = 1; i <= nb_col; i++) {
				XMLFormat.append("\t\t<variable name=\"" + rsma.getColumnLabel(i) + "\"/>\n");
			}
			XMLFormat.append("\t</head>\n");
			XMLFormat.append("\t<results>\n");
			String value = null; // current value of a cell
			String valueType = null; // current type of a cell (uri, ...)
			while (next()) {
				XMLFormat.append("\t\t<result>\n");
				for (int i = 1; i <= nb_col; i++) {
					XMLFormat.append("\t\t\t<binding name=\"" + rsma.getColumnLabel(i) + "\">\n");
					value = getString(i);
					if (value.startsWith("http://")) {
						valueType = "uri";
					}
					XMLFormat.append("\t\t\t\t<" + valueType + ">" + value + "</" + valueType + ">\n");
					XMLFormat.append("\t\t\t</binding>\n");
				}
				XMLFormat.append("\t\t</result>\n");
			}
			XMLFormat.append("\t</results>\n");
			XMLFormat.append("</sparql>");

			return XMLFormat.toString();
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoProperty</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoProperty getOntoProperty(int columnIndex) throws JOBDBCException {
		try {
			OntoProperty res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoProperty(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoNonDependentProperty</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoNonDependentProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoNonDependentProperty getOntoNonDependentProperty(int columnIndex) throws JOBDBCException {
		try {
			OntoNonDependentProperty res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoNonDependentProperty(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoPrimitiveType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoPrimitiveType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoPrimitiveType getOntoPrimitiveType(int columnIndex) throws JOBDBCException {
		try {
			OntoPrimitiveType res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoPrimitiveType(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoCollectionType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoCollectionType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoCollectionType getOntoCollectionType(int columnIndex) throws JOBDBCException {
		try {
			OntoCollectionType res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoCollectionType(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoOntology</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoOntology</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoOntology getOntoOntology(int columnIndex) throws JOBDBCException {
		try {
			OntoOntology res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoOntology(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoGraphics</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoGraphics</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoGraphics getOntoGraphics(int columnIndex) throws JOBDBCException {
		try {
			OntoGraphics res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoGraphics(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoRefType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoRefType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoRefType getOntoRefType(int columnIndex) throws JOBDBCException {
		try {
			OntoRefType res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoRefType(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoContextProperty</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoContextProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoContextProperty getOntoContextProperty(int columnIndex) throws JOBDBCException {
		try {
			OntoContextProperty res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoContextProperty(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoRealMeasureType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoRealMeasureType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoRealMeasureType getOntoRealMeasureType(int columnIndex) throws JOBDBCException {
		try {
			OntoRealMeasureType res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoRealMeasureType(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoIntType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoIntType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoIntType getOntoIntType(int columnIndex) throws JOBDBCException {
		try {
			OntoIntType res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoIntType(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoDatatype</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoDatatype</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoDatatype getOntoDatatype(int columnIndex) throws JOBDBCException {
		try {
			OntoDatatype res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoDatatype(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoBooleanType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoBooleanType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoBooleanType getOntoBooleanType(int columnIndex) throws JOBDBCException {
		try {
			OntoBooleanType res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoBooleanType(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoDependentProperty</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoDependentProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoDependentProperty getOntoDependentProperty(int columnIndex) throws JOBDBCException {
		try {
			OntoDependentProperty res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoDependentProperty(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoRealType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoRealType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoRealType getOntoRealType(int columnIndex) throws JOBDBCException {
		try {
			OntoRealType res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoRealType(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoDocument</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoDocument</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoDocument getOntoDocument(int columnIndex) throws JOBDBCException {
		try {
			OntoDocument res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoDocument(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoStringType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoStringType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoStringType getOntoStringType(int columnIndex) throws JOBDBCException {
		try {
			OntoStringType res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoStringType(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoNumberType</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoNumberType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoNumberType getOntoNumberType(int columnIndex) throws JOBDBCException {
		try {
			OntoNumberType res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoNumberType(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoClass</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoClass</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoClass getOntoClass(int columnIndex) throws JOBDBCException {
		try {
			OntoClass res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoClass(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as an instance of the OntoAPI
	 * <code>OntoConcept</code> class.
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return an instance of <code>OntoConcept</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoConcept getOntoConcept(int columnIndex) throws JOBDBCException {
		try {
			OntoConcept res = null;

			int oid = resultSetDelegate.getInt(getMapIndex(columnIndex));
			if (oid != 0)
				res = new OntoConcept(oid, session);

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * Projections in the OntoQL Query
	 */
	private List expressionsInSelect;

	/**
	 * List of column indexes corresponding to an association type
	 */
	private List indexAssociationType = new ArrayList();

	/**
	 * List of column indexes corresponding to a collection type
	 */
	private List indexCollectionType = new ArrayList();

	/**
	 * List of column indexes corresponding to a projection of a dynamic property
	 */
	private List indexDynamicProperty = new ArrayList();

	/**
	 * List of instances loaded for each association type
	 */
	private Map instancesAssociationType = new HashMap();

	/**
	 * List of instances loaded for each collection type
	 */
	private Map instancesCollectionType = new HashMap();

	/**
	 * List of dynamic property values loaded
	 */
	private Map dynamicPropertiesValues = new HashMap();

	/**
	 * ResultSet get from the executed SQL query It is used as a delegate for most
	 * methods
	 */
	private ResultSet resultSetDelegate;

	public int getHoldability() throws SQLException {
		return resultSetDelegate.getHoldability();
	}

	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return resultSetDelegate.getNCharacterStream(columnIndex);
	}

	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return resultSetDelegate.getNCharacterStream(columnLabel);
	}

	public NClob getNClob(int columnIndex) throws SQLException {
		return resultSetDelegate.getNClob(columnIndex);
	}

	public NClob getNClob(String columnLabel) throws SQLException {
		return resultSetDelegate.getNClob(columnLabel);
	}

	public String getNString(int columnIndex) throws SQLException {
		return resultSetDelegate.getNString(columnIndex);
	}

	public String getNString(String columnLabel) throws SQLException {
		return resultSetDelegate.getNString(columnLabel);
	}

	public RowId getRowId(int columnIndex) throws SQLException {
		return resultSetDelegate.getRowId(columnIndex);
	}

	public RowId getRowId(String columnLabel) throws SQLException {
		return resultSetDelegate.getRowId(columnLabel);
	}

	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		return resultSetDelegate.getSQLXML(columnIndex);
	}

	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		return resultSetDelegate.getSQLXML(columnLabel);
	}

	public boolean isClosed() throws SQLException {
		return resultSetDelegate.isClosed();
	}

	public boolean isWrapperFor(Class arg0) throws SQLException {
		return resultSetDelegate.isWrapperFor(arg0);
	}

	public Object unwrap(Class arg0) throws SQLException {
		return resultSetDelegate.unwrap(arg0);
	}

	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
		resultSetDelegate.updateAsciiStream(columnIndex, x, length);
	}

	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
		resultSetDelegate.updateAsciiStream(columnIndex, x);
	}

	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
		resultSetDelegate.updateAsciiStream(columnLabel, x, length);
	}

	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
		resultSetDelegate.updateAsciiStream(columnLabel, x);
	}

	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
		resultSetDelegate.updateBinaryStream(columnIndex, x, length);
	}

	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
		resultSetDelegate.updateBinaryStream(columnIndex, x);
	}

	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
		resultSetDelegate.updateBinaryStream(columnLabel, x, length);
	}

	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
		resultSetDelegate.updateBinaryStream(columnLabel, x);
	}

	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
		resultSetDelegate.updateBlob(columnIndex, inputStream, length);
	}

	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
		resultSetDelegate.updateBlob(columnIndex, inputStream);
	}

	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
		resultSetDelegate.updateBlob(columnLabel, inputStream, length);
	}

	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
		resultSetDelegate.updateBlob(columnLabel, inputStream);
	}

	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		resultSetDelegate.updateCharacterStream(columnIndex, x, length);
	}

	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
		resultSetDelegate.updateCharacterStream(columnIndex, x);
	}

	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		resultSetDelegate.updateCharacterStream(columnLabel, reader, length);
	}

	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {
		resultSetDelegate.updateCharacterStream(columnLabel, reader);
	}

	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
		resultSetDelegate.updateClob(columnIndex, reader, length);
	}

	public void updateClob(int columnIndex, Reader reader) throws SQLException {
		resultSetDelegate.updateClob(columnIndex, reader);
	}

	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
		resultSetDelegate.updateClob(columnLabel, reader, length);
	}

	public void updateClob(String columnLabel, Reader reader) throws SQLException {
		resultSetDelegate.updateClob(columnLabel, reader);
	}

	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
		resultSetDelegate.updateNCharacterStream(columnIndex, x, length);
	}

	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
		resultSetDelegate.updateNCharacterStream(columnIndex, x);
	}

	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {
		resultSetDelegate.updateNCharacterStream(columnLabel, reader, length);
	}

	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {
		resultSetDelegate.updateNCharacterStream(columnLabel, reader);
	}

	public void updateNClob(int columnIndex, NClob clob) throws SQLException {
		resultSetDelegate.updateNClob(columnIndex, clob);
	}

	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
		resultSetDelegate.updateNClob(columnIndex, reader, length);
	}

	public void updateNClob(int columnIndex, Reader reader) throws SQLException {
		resultSetDelegate.updateNClob(columnIndex, reader);
	}

	public void updateNClob(String columnLabel, NClob clob) throws SQLException {
		resultSetDelegate.updateNClob(columnLabel, clob);
	}

	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
		resultSetDelegate.updateNClob(columnLabel, reader, length);
	}

	public void updateNClob(String columnLabel, Reader reader) throws SQLException {
		resultSetDelegate.updateNClob(columnLabel, reader);
	}

	public void updateNString(int columnIndex, String string) throws SQLException {
		resultSetDelegate.updateNString(columnIndex, string);
	}

	public void updateNString(String columnLabel, String string) throws SQLException {
		resultSetDelegate.updateNString(columnLabel, string);
	}

	public void updateRowId(int columnIndex, RowId x) throws SQLException {
		resultSetDelegate.updateRowId(columnIndex, x);
	}

	public void updateRowId(String columnLabel, RowId x) throws SQLException {
		resultSetDelegate.updateRowId(columnLabel, x);
	}

	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
		resultSetDelegate.updateSQLXML(columnIndex, xmlObject);
	}

	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
		resultSetDelegate.updateSQLXML(columnLabel, xmlObject);
	}

	/**
	 * A reference to the factory creator of this object Enable to create other
	 * entity of the same kind
	 */
	protected FactoryEntity factoryEntity;

	/** session to access the database. */
	private OntoQLSession session;

	/**
	 * A map between index of the projections in the OntoQL Query and the index in
	 * the ResultSet
	 */
	private Map mapIndex = new HashMap();

	public OntoQLResultSetImpl(ResultSet resultSetDelegate) {
		this.resultSetDelegate = resultSetDelegate;
	}

	public OntoQLResultSetImpl(ResultSet resultSetDelegate, List expressionsInSelect, FactoryEntity factory,
			OntoQLSession session) {
		this(resultSetDelegate);
		this.expressionsInSelect = expressionsInSelect;
		this.factoryEntity = factory;
		this.session = session;
		try {
			initMapIndex();
		} catch (SQLException e) {
		}
	}

	/**
	 * Init the mapping between the resultset and the select expressions
	 */
	public void initMapIndex() throws SQLException {
		int currentIndex = 1;
		SelectExpression currentProjection = null;
		EntityDatatype currentDatatype = null;
		if (expressionsInSelect.size() == 0) {

			ResultSetMetaData rsmetadata = resultSetDelegate.getMetaData();
			for (int i = 0; i < rsmetadata.getColumnCount(); i++) {
				Integer index = new Integer(i + 1);
				mapIndex.put(index, index);
			}
		}
		for (int i = 0; i < expressionsInSelect.size(); i++) {
			currentProjection = (SelectExpression) expressionsInSelect.get(i);
			mapIndex.put(new Integer(i + 1), new Integer(currentIndex));
			try {
				currentDatatype = currentProjection.getDataType();
				if (currentProjection instanceof IdentNode && ((IdentNode) currentProjection).isDynamicProperty()) {
					indexDynamicProperty.add(new Integer(i));
					currentIndex = currentIndex + 2;
					// oid and classid of the instance
				} else if (currentDatatype != null && currentDatatype.isAssociationType()) {
					Category c = ((EntityDatatypeCategory) currentDatatype).getCagetory();
					if (c.isClass()) {
						EntityClass aClass = (EntityClass) c;
						indexAssociationType.add(new Integer(i));
						EntityProperty[] properties = aClass.getDefinedProperties();
						int nbr = properties.length + 1;
						for (int j = 0; j < properties.length; j++) {
							try {
								if (properties[j].getRange().isAssociationType()) {
									nbr = nbr + 1; // we get its oid + its base
									// type
								}
							} catch (NotSupportedDatatypeException oexc) {
								// do nothing
							}
						}
						currentIndex = currentIndex + nbr;
					}

				} else if (currentDatatype != null && currentDatatype.isCollectionAssociationType()) {
					EntityDatatypeCategory associationType = (EntityDatatypeCategory) ((EntityDatatypeCollection) currentDatatype)
							.getDatatype();
					Category c = associationType.getCagetory();
					if (c.isClass()) {
						indexCollectionType.add(new Integer(i));
						currentIndex++; // base type of instances
					}
				}

			} catch (NotSupportedDatatypeException oexc) {
				// do nothing
			}
			currentIndex++;
		}
	}

	/**
	 * 
	 * @param i
	 * @return
	 */
	public int getMapIndex(int i) {
		return ((Integer) (mapIndex.get(new Integer(i)))).intValue();
	}

	public boolean absolute(int row) throws SQLException {
		return resultSetDelegate.absolute(row);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void afterLast() throws SQLException {
		resultSetDelegate.afterLast();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void beforeFirst() throws SQLException {
		resultSetDelegate.beforeFirst();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void cancelRowUpdates() throws SQLException {
		resultSetDelegate.cancelRowUpdates();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void clearWarnings() throws SQLException {
		resultSetDelegate.clearWarnings();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void close() throws SQLException {
		resultSetDelegate.close();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void deleteRow() throws SQLException {
		resultSetDelegate.deleteRow();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return resultSetDelegate.equals(obj);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public int findColumn(String columnName) throws SQLException {
		return resultSetDelegate.findColumn(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public boolean first() throws SQLException {
		return resultSetDelegate.first();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Array getArray(int i) throws SQLException {
		return resultSetDelegate.getArray(i);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Array getArray(String colName) throws SQLException {
		return resultSetDelegate.getArray(colName);
	}

	/**
	 * Retrieves the value of the designated column in the current row of this
	 * <code>OntoQLResultSet</code> object as a Set of Object
	 * 
	 * @param columnIndex the first column is 1, the second is 2, ...
	 * @return a set of Object
	 * @exception JOBDBCException if a database access error occurs
	 */
	public Set getSet(int columnIndex) throws JOBDBCException {
		Set res = new HashSet();

		try {
			SelectExpression currentProjection = (SelectExpression) expressionsInSelect.get(columnIndex - 1);
			EntityDatatype currentDatatype = currentProjection.getDataType();
			EntityDatatype typeOfCollection = ((EntityDatatypeCollection) currentDatatype).getDatatype();
			// if this is a collection of entity
			if (currentDatatype.isCollectionAssociationType()) {
				Entity entity = (Entity) ((EntityDatatypeCategory) typeOfCollection).getCagetory();
				Object[] resFunction = null;
				try {
					resFunction = (Object[]) resultSetDelegate.getArray(columnIndex).getArray();
				} catch (ClassCastException oExc) {
					// raised if the result is returned as an array of
					// numbers
					long[] resFunctionInt = (long[]) resultSetDelegate.getArray(columnIndex).getArray();
					resFunction = new String[resFunctionInt.length];
					for (int i = 0; i < resFunctionInt.length; i++) {
						resFunction[i] = Long.toString(resFunctionInt[i]);
					}
				}

				entity.getDelegateEntity().getOntoAPIClass();

				for (int i = 0; i < resFunction.length; i++) {
					String oid = resFunction[i].toString();
					res.add(session.newOntoRoot(Integer.valueOf(oid).intValue(), entity));
				}
				// else, this is a collection of primitive types
			} else {
				res = new HashSet();
				Array arrayOfValues = resultSetDelegate.getArray(columnIndex);
				res = ArrayHelper.toSet(arrayOfValues);
			}

			return res;

		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc);
		}
	}

	public Object[] getCollection(int columnIndex) throws JOBDBCException {

		try {
			Object[] res = null;

			Integer integerColumnIndex = new Integer(columnIndex);
			if (instancesCollectionType.containsKey(integerColumnIndex)) {
				res = (Instance[]) instancesCollectionType.get(integerColumnIndex);
			} else {
				SelectExpression currentProjection = (SelectExpression) expressionsInSelect.get(columnIndex - 1);
				EntityDatatype currentDatatype = currentProjection.getDataType();
				EntityDatatype typeOfCollection = ((EntityDatatypeCollection) currentDatatype).getDatatype();
				if (currentDatatype.isCollectionAssociationType()) {
					Entity entity = (Entity) ((EntityDatatypeCategory) typeOfCollection).getCagetory();
					// For the moment we manage only the core ontology model
					// (class and property)
					// Latter this code must be dynamic
					boolean isClass = true;

					Object[] resFunction = null;
					try {
						resFunction = (Object[]) resultSetDelegate.getArray(columnIndex).getArray();
					} catch (ClassCastException oExc) {
						// raised if the result is returned as an array of
						// numbers
						Long[] resFunctionInt = (Long[]) resultSetDelegate.getArray(columnIndex).getArray();
						resFunction = new String[resFunctionInt.length];
						for (int i = 0; i < resFunctionInt.length; i++) {
							resFunction[i] = Long.toString(resFunctionInt[i].longValue());
						}
					}

					if (entity.getDelegateEntity().getName().equals("property")) {
						isClass = false;
						res = new EntityProperty[resFunction.length];
					} else {
						res = new EntityClass[resFunction.length];
					}

					for (int i = 0; i < res.length; i++) {
						String oid = resFunction[i].toString();
						if (isClass) {
							res[i] = factoryEntity.createEntityClass("!" + oid);
						} else {
							res[i] = factoryEntity.createEntityProperty("!" + oid);
						}
					}
				} else {
					Array arrayOfValues = resultSetDelegate.getArray(columnIndex);
					if (typeOfCollection instanceof EntityDatatypeInt) {
						res = (Integer[]) arrayOfValues.getArray();
					} else {
						if (arrayOfValues == null) {
							return null;
						} else {
							res = (Object[]) arrayOfValues.getArray();
						}
					}
				}

			}

			return res;
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc);
		}
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		return resultSetDelegate.getAsciiStream(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public InputStream getAsciiStream(String columnName) throws SQLException {
		return resultSetDelegate.getAsciiStream(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		return resultSetDelegate.getBigDecimal(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		return resultSetDelegate.getBigDecimal(getMapIndex(columnIndex), scale);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		return resultSetDelegate.getBigDecimal(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
		return resultSetDelegate.getBigDecimal(columnName, scale);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return resultSetDelegate.getBinaryStream(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public InputStream getBinaryStream(String columnName) throws SQLException {
		return resultSetDelegate.getBinaryStream(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Blob getBlob(int i) throws SQLException {
		return resultSetDelegate.getBlob(i);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Blob getBlob(String colName) throws SQLException {
		return resultSetDelegate.getBlob(colName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public boolean getBoolean(int columnIndex) throws SQLException {
		return resultSetDelegate.getBoolean(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public boolean getBoolean(String columnName) throws SQLException {
		return resultSetDelegate.getBoolean(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public byte getByte(int columnIndex) throws SQLException {
		return resultSetDelegate.getByte(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public byte getByte(String columnName) throws SQLException {
		return resultSetDelegate.getByte(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public byte[] getBytes(int columnIndex) throws SQLException {
		return resultSetDelegate.getBytes(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public byte[] getBytes(String columnName) throws SQLException {
		return resultSetDelegate.getBytes(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return resultSetDelegate.getCharacterStream(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Reader getCharacterStream(String columnName) throws SQLException {
		return resultSetDelegate.getCharacterStream(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Clob getClob(int i) throws SQLException {
		return resultSetDelegate.getClob(i);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Clob getClob(String colName) throws SQLException {
		return resultSetDelegate.getClob(colName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public int getConcurrency() throws SQLException {
		return resultSetDelegate.getConcurrency();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public String getCursorName() throws SQLException {
		return resultSetDelegate.getCursorName();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Date getDate(int columnIndex) throws SQLException {
		return resultSetDelegate.getDate(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		return resultSetDelegate.getDate(getMapIndex(columnIndex), cal);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Date getDate(String columnName) throws SQLException {
		return resultSetDelegate.getDate(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Date getDate(String columnName, Calendar cal) throws SQLException {
		return resultSetDelegate.getDate(columnName, cal);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public double getDouble(int columnIndex) throws SQLException {
		return resultSetDelegate.getDouble(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public double getDouble(String columnName) throws SQLException {
		return resultSetDelegate.getDouble(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public int getFetchDirection() throws SQLException {
		return resultSetDelegate.getFetchDirection();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public int getFetchSize() throws SQLException {
		return resultSetDelegate.getFetchSize();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public float getFloat(int columnIndex) throws SQLException {
		return resultSetDelegate.getFloat(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public float getFloat(String columnName) throws SQLException {
		return resultSetDelegate.getFloat(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public int getInt(int columnIndex) throws SQLException {
		return resultSetDelegate.getInt(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public int getInt(String columnName) throws SQLException {
		return resultSetDelegate.getInt(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public long getLong(int columnIndex) throws SQLException {
		return resultSetDelegate.getLong(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public long getLong(String columnName) throws SQLException {
		return resultSetDelegate.getLong(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public ResultSetMetaData getMetaData() throws JOBDBCException {
		try {
			return new OntoQLResultSetMetaDataImpl(resultSetDelegate.getMetaData(), expressionsInSelect);
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Object getObject(int columnIndex) throws SQLException {
		Object res = null;
		Integer key = new Integer(getMapIndex(columnIndex));
		if (dynamicPropertiesValues.containsKey(key)) {
			res = dynamicPropertiesValues.get(key);
		} else {
			res = resultSetDelegate.getObject(getMapIndex(columnIndex));
		}
		return res;
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Object getObject(int i, Map map) throws SQLException {
		return resultSetDelegate.getObject(i, map);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Object getObject(String columnName) throws SQLException {
		return resultSetDelegate.getObject(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Object getObject(String colName, Map map) throws SQLException {
		return resultSetDelegate.getObject(colName, map);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Ref getRef(int i) throws SQLException {
		return resultSetDelegate.getRef(i);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Ref getRef(String colName) throws SQLException {
		return resultSetDelegate.getRef(colName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public int getRow() throws SQLException {
		return resultSetDelegate.getRow();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public short getShort(int columnIndex) throws SQLException {
		return resultSetDelegate.getShort(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public short getShort(String columnName) throws SQLException {
		return resultSetDelegate.getShort(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Statement getStatement() throws SQLException {
		return resultSetDelegate.getStatement();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public String getString(int columnIndex) throws SQLException {
		String res = null;
		Integer key = new Integer(getMapIndex(columnIndex));
		if (dynamicPropertiesValues.containsKey(key)) {
			Object value = dynamicPropertiesValues.get(key);
			if (value != null) {
				res = value.toString();
			}
		} else {
			res = resultSetDelegate.getString(getMapIndex(columnIndex));
		}
		return res;
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public String getString(String columnName) throws SQLException {
		return resultSetDelegate.getString(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Time getTime(int columnIndex) throws SQLException {
		return resultSetDelegate.getTime(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		return resultSetDelegate.getTime(getMapIndex(columnIndex), cal);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Time getTime(String columnName) throws SQLException {
		return resultSetDelegate.getTime(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Time getTime(String columnName, Calendar cal) throws SQLException {
		return resultSetDelegate.getTime(columnName, cal);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		return resultSetDelegate.getTimestamp(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		return resultSetDelegate.getTimestamp(getMapIndex(columnIndex), cal);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Timestamp getTimestamp(String columnName) throws SQLException {
		return resultSetDelegate.getTimestamp(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
		return resultSetDelegate.getTimestamp(columnName, cal);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public int getType() throws SQLException {
		return resultSetDelegate.getType();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		return resultSetDelegate.getUnicodeStream(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public InputStream getUnicodeStream(String columnName) throws SQLException {
		return resultSetDelegate.getUnicodeStream(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public URL getURL(int columnIndex) throws SQLException {
		return resultSetDelegate.getURL(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public URL getURL(String columnName) throws SQLException {
		return resultSetDelegate.getURL(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public SQLWarning getWarnings() throws SQLException {
		return resultSetDelegate.getWarnings();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return resultSetDelegate.hashCode();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void insertRow() throws SQLException {
		resultSetDelegate.insertRow();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public boolean isAfterLast() throws SQLException {
		return resultSetDelegate.isAfterLast();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public boolean isBeforeFirst() throws SQLException {
		return resultSetDelegate.isBeforeFirst();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public boolean isFirst() throws SQLException {
		return resultSetDelegate.isFirst();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public boolean isLast() throws SQLException {
		return resultSetDelegate.isLast();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public boolean last() throws SQLException {
		return resultSetDelegate.last();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void moveToCurrentRow() throws SQLException {
		resultSetDelegate.moveToCurrentRow();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void moveToInsertRow() throws SQLException {
		resultSetDelegate.moveToInsertRow();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public boolean next() throws SQLException {
		boolean res = resultSetDelegate.next();
		if (res) {
			// treatment of association types
			for (int i = 0; i < indexAssociationType.size(); i++) {
				int indexColumn = ((Integer) indexAssociationType.get(i)).intValue() + 1;
				instancesAssociationType.put(new Integer(indexColumn), loadInstance(indexColumn));
			}
			// treatment of collection types
			for (int i = 0; i < indexCollectionType.size(); i++) {
				int indexColumn = ((Integer) indexCollectionType.get(i)).intValue() + 1;
				instancesCollectionType.put(new Integer(indexColumn), loadCollectionInstances(indexColumn));
			}
			// treatment of dynamic projection
			for (int i = 0; i < indexDynamicProperty.size(); i++) {
				int indexColumn = ((Integer) indexDynamicProperty.get(i)).intValue() + 1;
				String propertyOid = resultSetDelegate.getString(indexColumn);
				String instanceOid = resultSetDelegate.getString(indexColumn + 1);
				String instanceClassOid = resultSetDelegate.getString(indexColumn + 2);
				Instance instance = factoryEntity.createInstance(instanceOid,
						factoryEntity.createEntityClass("!" + instanceClassOid));
				Object propertyValue = instance.getObjectPropertyValue("!" + propertyOid);
				dynamicPropertiesValues.put(new Integer(indexColumn), propertyValue);
			}
		}
		return res;
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public boolean previous() throws SQLException {
		return resultSetDelegate.previous();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void refreshRow() throws SQLException {
		resultSetDelegate.refreshRow();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public boolean relative(int rows) throws SQLException {
		return resultSetDelegate.relative(rows);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public boolean rowDeleted() throws SQLException {
		return resultSetDelegate.rowDeleted();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public boolean rowInserted() throws SQLException {
		return resultSetDelegate.rowInserted();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public boolean rowUpdated() throws SQLException {
		return resultSetDelegate.rowUpdated();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void setFetchDirection(int direction) throws SQLException {
		resultSetDelegate.setFetchDirection(direction);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void setFetchSize(int rows) throws SQLException {
		resultSetDelegate.setFetchSize(rows);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return resultSetDelegate.toString();
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateArray(int columnIndex, Array x) throws SQLException {
		resultSetDelegate.updateArray(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateArray(String columnName, Array x) throws SQLException {
		resultSetDelegate.updateArray(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		resultSetDelegate.updateAsciiStream(getMapIndex(columnIndex), x, length);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
		resultSetDelegate.updateAsciiStream(columnName, x, length);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		resultSetDelegate.updateBigDecimal(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
		resultSetDelegate.updateBigDecimal(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		resultSetDelegate.updateBinaryStream(getMapIndex(columnIndex), x, length);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
		resultSetDelegate.updateBinaryStream(columnName, x, length);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		resultSetDelegate.updateBlob(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateBlob(String columnName, Blob x) throws SQLException {
		resultSetDelegate.updateBlob(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		resultSetDelegate.updateBoolean(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateBoolean(String columnName, boolean x) throws SQLException {
		resultSetDelegate.updateBoolean(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateByte(int columnIndex, byte x) throws SQLException {
		resultSetDelegate.updateByte(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateByte(String columnName, byte x) throws SQLException {
		resultSetDelegate.updateByte(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		resultSetDelegate.updateBytes(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		resultSetDelegate.updateBytes(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		resultSetDelegate.updateCharacterStream(getMapIndex(columnIndex), x, length);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
		resultSetDelegate.updateCharacterStream(columnName, reader, length);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		resultSetDelegate.updateClob(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateClob(String columnName, Clob x) throws SQLException {
		resultSetDelegate.updateClob(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateDate(int columnIndex, Date x) throws SQLException {
		resultSetDelegate.updateDate(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateDate(String columnName, Date x) throws SQLException {
		resultSetDelegate.updateDate(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateDouble(int columnIndex, double x) throws SQLException {
		resultSetDelegate.updateDouble(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateDouble(String columnName, double x) throws SQLException {
		resultSetDelegate.updateDouble(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateFloat(int columnIndex, float x) throws SQLException {
		resultSetDelegate.updateFloat(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateFloat(String columnName, float x) throws SQLException {
		resultSetDelegate.updateFloat(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateInt(int columnIndex, int x) throws SQLException {
		resultSetDelegate.updateInt(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateInt(String columnName, int x) throws SQLException {
		resultSetDelegate.updateInt(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateLong(int columnIndex, long x) throws SQLException {
		resultSetDelegate.updateLong(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateLong(String columnName, long x) throws SQLException {
		resultSetDelegate.updateLong(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateNull(int columnIndex) throws SQLException {
		resultSetDelegate.updateNull(getMapIndex(columnIndex));
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateNull(String columnName) throws SQLException {
		resultSetDelegate.updateNull(columnName);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateObject(int columnIndex, Object x) throws SQLException {
		resultSetDelegate.updateObject(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
		resultSetDelegate.updateObject(getMapIndex(columnIndex), x, scale);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateObject(String columnName, Object x) throws SQLException {
		resultSetDelegate.updateObject(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateObject(String columnName, Object x, int scale) throws SQLException {
		resultSetDelegate.updateObject(columnName, x, scale);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		resultSetDelegate.updateRef(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateRef(String columnName, Ref x) throws SQLException {
		resultSetDelegate.updateRef(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateRow() throws SQLException {
		resultSetDelegate.updateRow();
	}

	public void updateShort(int columnIndex, short x) throws SQLException {
		resultSetDelegate.updateShort(getMapIndex(columnIndex), x);
	}

	public void updateShort(String columnName, short x) throws SQLException {
		resultSetDelegate.updateShort(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateString(int columnIndex, String x) throws SQLException {
		resultSetDelegate.updateString(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateString(String columnName, String x) throws SQLException {
		resultSetDelegate.updateString(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateTime(int columnIndex, Time x) throws SQLException {
		resultSetDelegate.updateTime(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateTime(String columnName, Time x) throws SQLException {
		resultSetDelegate.updateTime(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		resultSetDelegate.updateTimestamp(getMapIndex(columnIndex), x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
		resultSetDelegate.updateTimestamp(columnName, x);
	}

	/**
	 * 
	 * @param Require : Ensure :
	 */
	public boolean wasNull() throws SQLException {
		return resultSetDelegate.wasNull();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ensma.lisi.jobdbc.external.OntoqlResultSet#getOntoQLMetaData()
	 */
	public OntoQLResultSetMetaData getOntoQLMetaData() throws JOBDBCException {
		try {
			return new OntoQLResultSetMetaDataImpl(resultSetDelegate.getMetaData(), expressionsInSelect);
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	public Instance loadInstance(int columnIndex) throws JOBDBCException {
		Instance res = null;
		try {
			int currentIndex = getMapIndex(columnIndex);
			String oid = resultSetDelegate.getString(currentIndex);
			if (oid == null) {
				return null;
			}
			EntityDatatype dt = ((SelectExpression) expressionsInSelect.get(columnIndex - 1)).getDataType();
			EntityClass typeGeneral = (EntityClass) ((EntityDatatypeCategory) dt).getCagetory();

			// the type of this instance may be a more specific class
			// (polymorphic query)
			EntityClass baseClass = factoryEntity
					.createEntityClass("!" + resultSetDelegate.getString(currentIndex + 1).substring(1));
			res = factoryEntity.createInstance(oid, baseClass);

			// now we must load manually this instance
			// with the value in this resultSet
			EntityProperty[] properties = typeGeneral.getDefinedProperties();
			int indexResultSet = currentIndex + 2; // index in the resultset
			// delegate (+2 because of
			// the rid and tablename)

			for (int i = 0; i < properties.length; i++) {
				Object value = null;
				String typeName = "NOT SUPPORTED";
				try {
					typeName = properties[i].getRange().getName();
				} catch (NotSupportedDatatypeException oExc) {

				}
				if (typeName.equals(EntityDatatype.ASSOCIATION_NAME)) {
					String oidClassRange = resultSetDelegate.getString(indexResultSet + 1);
					if (StringHelper.isEmpty(oidClassRange)) {
						value = null;
					} else {
						oidClassRange = oidClassRange.substring(1);
						EntityClass classRange = factoryEntity.createEntityClass("!" + oidClassRange);
						value = factoryEntity.createInstance(resultSetDelegate.getString(indexResultSet), classRange);
					}
					indexResultSet += 2;
				} else {
					String valueString = resultSetDelegate.getString(indexResultSet);
					if (valueString == null) {
						value = null;
					} else {
						// TODO see if real measure type and
						// int measure type must be supported
						if (typeName.equals(EntityDatatype.INT_NAME) || typeName.equals("INT_MEASURE_TYPE")) {
							value = Integer.valueOf(valueString);
						} else if (typeName.equals(EntityDatatype.REAL_NAME)
								|| (typeName.equals("REAL_MEASURE_TYPE"))) {
							value = Float.valueOf(valueString);
						} else {
							value = valueString;
						}
					}
					indexResultSet++;
				}
				res.setPropertyValue(properties[i], value);
			}
			if (baseClass.equals(typeGeneral)) {
				res.setIsLoaded(true);
			}

			return res;
		} catch (ClassCastException oExc) {
			throw new JOBDBCException("This column doesn't contain instances of a class");
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	public Instance[] loadCollectionInstances(int columnIndex) throws JOBDBCException {
		Instance[] res = null;
		try {
			int currentIndex = getMapIndex(columnIndex);
			Array tabOids = resultSetDelegate.getArray(currentIndex);
			Array tabTypes = resultSetDelegate.getArray(currentIndex + 1);
			if (tabTypes != null && tabOids != null) {
				String[] types = (String[]) tabTypes.getArray();
				Long[] oids = (Long[]) tabOids.getArray();
				res = new Instance[oids.length];
				Instance currentInstance = null;
				EntityClass currentBaseClass = null;
				for (int i = 0; i < oids.length; i++) {
					currentBaseClass = factoryEntity.createEntityClass("!" + types[i].substring(1));
					currentInstance = factoryEntity.createInstance(String.valueOf(oids[i]), currentBaseClass);
					res[i] = currentInstance;
				}
			}
			return res;
		} catch (ClassCastException oExc) {
			throw new JOBDBCException("This column doesn't contain a collection of instances of a class");
		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	public Instance getInstance(int columnIndex) throws JOBDBCException {
		return (Instance) instancesAssociationType.get(new Integer(columnIndex));
	}

	/**
	 * @see fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet#getEntityClass(int)
	 */
	public EntityClass getEntityClass(int columnIndex) throws JOBDBCException {

		try {
			EntityClass res = null;

			String oidClass = resultSetDelegate.getString(getMapIndex(columnIndex));

			res = factoryEntity.createEntityClass("!" + oidClass);

			return res;

		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	public EntityProperty getEntityProperty(int columnIndex) throws JOBDBCException {

		try {
			EntityProperty res = null;

			String oidProperty = resultSetDelegate.getString(getMapIndex(columnIndex));

			res = factoryEntity.createEntityProperty("!" + oidProperty);

			return res;

		} catch (SQLException oExc) {
			throw new JOBDBCException(oExc.getMessage());
		}
	}

	public <T> T getObject(String name, Class<T> pClass) {
		throw new NotYetImplementedException();
	}

	public <T> T getObject(int columnIndex, Class<T> type) {
		throw new NotYetImplementedException();
	}
}
