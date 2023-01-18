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
import java.util.Set;

import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLResultSet;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * OntoClass.
 *
 * @author Mickael BARON
 */
public class OntoClass extends OntoConcept {

	protected OntoOntology definedBy;

	protected boolean isLoadedDefinedBy;

	protected Set properties;

	protected boolean isLoadedProperties;

	protected Set scopeProperties;

	protected boolean isLoadedScopeProperties;

	protected Set usedProperties;

	protected boolean isLoadedUsedProperties;

	protected Set directSuperclasses;

	protected boolean isLoadedDirectSuperclasses;

	protected Set superclasses;

	protected boolean isLoadedSuperclasses;

	protected Set subclasses;

	protected boolean isLoadedSubclasses;

	protected Set directSubclasses;

	protected boolean isLoadedDirectSubclasses;

	public OntoClass() {
	}

	public OntoClass(int oid, OntoQLSession s) {
		super(oid, s);
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
			OntoQLResultSet resultSet = stmt.executeQuery("SELECT #definedBy FROM #class WHERE #oid = " + oid + "");
			if (resultSet.next()) {
				setDefinedBy(resultSet.getOntoOntology(1));

				isLoadedDefinedBy = true;
			} else {
				throw new JOBDBCException("the instance of class with oid " + getOid() + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	public Set getProperties() {
		if (!isLoadedProperties) {
			loadProperties();
		}
		return properties;
	}

	public void setProperties(Set properties) {
		this.properties = properties;
	}

	public void loadProperties() {
		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			OntoQLResultSet resultSet = stmt.executeQuery("SELECT #properties FROM #class WHERE #oid = " + oid + "");
			if (resultSet.next()) {
				setProperties(resultSet.getSet(1));

				isLoadedProperties = true;
			} else {
				throw new JOBDBCException("the instance of class with oid " + getOid() + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	public Set getScopeProperties() {
		if (!isLoadedScopeProperties) {
			loadScopeProperties();
		}
		return scopeProperties;
	}

	public void setScopeProperties(Set scopeProperties) {
		this.scopeProperties = scopeProperties;
	}

	public void loadScopeProperties() {
		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			OntoQLResultSet resultSet = stmt
					.executeQuery("SELECT #scopeProperties FROM #class WHERE #oid = " + oid + "");
			if (resultSet.next()) {
				setScopeProperties(resultSet.getSet(1));

				isLoadedScopeProperties = true;
			} else {
				throw new JOBDBCException("the instance of class with oid " + getOid() + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	public Set getUsedProperties() {
		if (!isLoadedUsedProperties) {
			loadUsedProperties();
		}
		return usedProperties;
	}

	public void setUsedProperties(Set usedProperties) {
		this.usedProperties = usedProperties;
	}

	public void loadUsedProperties() {
		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			OntoQLResultSet resultSet = stmt
					.executeQuery("SELECT #usedProperties FROM #class WHERE #oid = " + oid + "");
			if (resultSet.next()) {
				setUsedProperties(resultSet.getSet(1));

				isLoadedUsedProperties = true;
			} else {
				throw new JOBDBCException("the instance of class with oid " + getOid() + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	public Set getDirectSuperclasses() {
		if (!isLoadedDirectSuperclasses) {
			loadDirectSuperclasses();
		}
		return directSuperclasses;
	}

	public void setDirectSuperclasses(Set directSuperclasses) {
		this.directSuperclasses = directSuperclasses;
	}

	public void loadDirectSuperclasses() {
		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			OntoQLResultSet resultSet = stmt
					.executeQuery("SELECT #directSuperclasses FROM #class WHERE #oid = " + oid + "");
			if (resultSet.next()) {
				setDirectSuperclasses(resultSet.getSet(1));

				isLoadedDirectSuperclasses = true;
			} else {
				throw new JOBDBCException("the instance of class with oid " + getOid() + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	public Set getSuperclasses() {
		if (!isLoadedSuperclasses) {
			loadSuperclasses();
		}
		return superclasses;
	}

	public void setSuperclasses(Set superclasses) {
		this.superclasses = superclasses;
	}

	public void loadSuperclasses() {
		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			OntoQLResultSet resultSet = stmt.executeQuery("SELECT #superclasses FROM #class WHERE #oid = " + oid + "");
			if (resultSet.next()) {
				setSuperclasses(resultSet.getSet(1));

				isLoadedSuperclasses = true;
			} else {
				throw new JOBDBCException("the instance of class with oid " + getOid() + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	public Set getDirectSubclasses() {
		if (!isLoadedDirectSubclasses) {
			loadDirectSubclasses();
		}
		return directSubclasses;
	}

	public void setDirectSubclasses(Set directSubclasses) {
		this.directSubclasses = directSubclasses;
	}

	public void loadDirectSubclasses() {
		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			OntoQLResultSet resultSet = stmt
					.executeQuery("SELECT #directSubclasses FROM #class WHERE #oid = " + oid + "");
			if (resultSet.next()) {
				setDirectSubclasses(resultSet.getSet(1));

				isLoadedDirectSubclasses = true;
			} else {
				throw new JOBDBCException("the instance of class with oid " + getOid() + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	public Set getSubclasses() {
		if (!isLoadedSubclasses) {
			loadSubclasses();
		}
		return subclasses;
	}

	public void setSubclasses(Set subclasses) {
		this.subclasses = subclasses;
	}

	public void loadSubclasses() {
		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			OntoQLResultSet resultSet = stmt.executeQuery("SELECT #subclasses FROM #class WHERE #oid = " + oid + "");
			if (resultSet.next()) {
				setSubclasses(resultSet.getSet(1));

				isLoadedSubclasses = true;
			} else {
				throw new JOBDBCException("the instance of class with oid " + getOid() + " doesn't exist");
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
					"SELECT #code, #version, #oid, #name[en], #name[fr], #definition[en], #definition[fr], #revision, #note[en], #note[fr], #remark[en], #remark[fr], #dateOfCurrentVersion, #dateOfCurrentRevision, #dateOfOriginalDefinition, #shortName[en], #shortName[fr] FROM #class WHERE "
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
				throw new JOBDBCException("the instance of class with oid " + oid + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}
}
