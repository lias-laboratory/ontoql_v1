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
package fr.ensma.lisi.ontoql.ontoapi;

import java.sql.SQLException;

import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * OntoProperty.
 *
 * @author Mickael BARON
 */
public class OntoProperty extends OntoConcept {

	protected OntoClass scope;

	protected boolean isLoadedScope;

	protected OntoDatatype range;

	protected boolean isLoadedRange;

	protected OntoOntology definedBy;

	protected boolean isLoadedDefinedBy;

	public OntoProperty() {
	}

	public OntoProperty(int oid, OntoQLSession s) {
		super(oid, s);
	}

	public OntoClass getScope() {
		if (!isLoadedScope) {
			loadScope();
		}
		return scope;
	}

	public void setScope(OntoClass scope) {
		this.scope = scope;
	}

	public void loadScope() {
		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			OntoQLResultSet resultSet = stmt.executeQuery("SELECT #scope FROM #property WHERE #oid = " + oid + "");
			if (resultSet.next()) {
				setScope(resultSet.getOntoClass(1));

				isLoadedScope = true;
			} else {
				throw new JOBDBCException("the instance of property with oid " + getOid() + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	public OntoDatatype getRange() {
		if (!isLoadedRange) {
			loadRange();
		}
		return range;
	}

	public void setRange(OntoDatatype range) {
		this.range = range;
	}

	public void loadRange() {
		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			OntoQLResultSet resultSet = stmt.executeQuery("SELECT #range FROM #property WHERE #oid = " + oid + "");
			if (resultSet.next()) {
				setRange(resultSet.getOntoDatatype(1));

				isLoadedRange = true;
			} else {
				throw new JOBDBCException("the instance of property with oid " + getOid() + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	public OntoOntology getDefinedBy() {
		if (!isLoadedDefinedBy) {
			loadDefinedBy();
		}
		return definedBy;
	}

	public void setDefinedBy(OntoOntology definedBy) {
		this.definedBy = definedBy;
	}

	public void loadDefinedBy() {
		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			OntoQLResultSet resultSet = stmt.executeQuery("SELECT #definedBy FROM #property WHERE #oid = " + oid + "");
			if (resultSet.next()) {
				setDefinedBy(resultSet.getOntoOntology(1));

				isLoadedDefinedBy = true;
			} else {
				throw new JOBDBCException("the instance of property with oid " + getOid() + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	public void load() {
		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			String whereClause = "#oid = " + oid;
			if (oid == 0) {
				if (code != null) {
					whereClause = "#code = '" + code + "' AND #version = '" + version + "'";
				} else {
					if (name_en != null) {
						whereClause = "#name[en] = '" + name_en + "'";
					} else {
						whereClause = "#name[fr] = '" + name_fr + "'";
					}
				}
			}

			OntoQLResultSet resultSet = stmt.executeQuery(
					"SELECT #code, #version, #oid, #name[en], #name[fr], #definition[en], #definition[fr], #revision, #note[en], #note[fr], #remark[en], #remark[fr], #dateOfCurrentVersion, #dateOfCurrentRevision, #dateOfOriginalDefinition, #shortName[en], #shortName[fr] FROM #property WHERE "
							+ whereClause);
			if (resultSet.next()) {
				setCode(resultSet.getString(1));
				setVersion(resultSet.getString(2));
				setOid(resultSet.getInt(3));
				setName(resultSet.getString(4), OntoQLHelper.ENGLISH);
				setName(resultSet.getString(5), OntoQLHelper.FRENCH);
				setDefinition(resultSet.getString(6), OntoQLHelper.ENGLISH);
				setDefinition(resultSet.getString(7), OntoQLHelper.FRENCH);
				setRevision(resultSet.getString(8));
				setNote(resultSet.getString(9), OntoQLHelper.ENGLISH);
				setNote(resultSet.getString(10), OntoQLHelper.FRENCH);
				setRemark(resultSet.getString(11), OntoQLHelper.ENGLISH);
				setRemark(resultSet.getString(12), OntoQLHelper.FRENCH);
				setDateOfCurrentVersion(resultSet.getString(13));
				setDateOfCurrentRevision(resultSet.getString(14));
				setDateOfOriginalDefinition(resultSet.getString(15));
				setShortName(resultSet.getString(16), OntoQLHelper.ENGLISH);
				setShortName(resultSet.getString(17), OntoQLHelper.FRENCH);

				isLoaded = true;
			} else {
				throw new JOBDBCException("the instance of property with oid " + oid + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}
}
