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
 * OntoConcept.
 *
 * @author Mickael BARON
 */
public class OntoConcept extends OntoRoot {

	protected String code;

	protected String name_en;

	protected String name_fr;

	protected String definition_en;

	protected String definition_fr;

	protected String version;

	protected String remark_en;

	protected String remark_fr;

	protected String revision;

	protected String note_en;

	protected String note_fr;

	protected String dateOfCurrentRevision;

	protected String dateOfCurrentVersion;

	protected String dateOfOriginalDefinition;

	protected OntoGraphics icon;

	protected boolean isLoadedIcon;

	protected String shortName_en;

	protected String shortName_fr;

	protected OntoDocument docOfDefinition;

	protected boolean isLoadedDocOfDefinition;

	public OntoConcept() {
	}

	public OntoConcept(int oid, OntoQLSession s) {
		super(oid, s);
	}

	public String getCode() {
		if (!isLoaded) {
			load();
		}
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName(String lg) {
		if (!isLoaded) {
			load();
		}
		if (lg.equals(OntoQLHelper.ENGLISH)) {
			return name_en;
		} else {
			return name_fr;
		}
	}

	public String getName() {
		return getName(session.getReferenceLanguage());
	}

	public void setName(String name, String lg) {
		if (lg.equals(OntoQLHelper.ENGLISH)) {
			this.name_en = name;
		} else {
			this.name_fr = name;
		}
	}

	public void setName(String name) {
		setName(name, session.getReferenceLanguage());
	}

	public String getDefinition(String lg) {
		if (!isLoaded) {
			load();
		}
		if (lg.equals(OntoQLHelper.ENGLISH)) {
			return definition_en;
		} else {
			return definition_fr;
		}
	}

	public String getDefinition() {
		return getDefinition(session.getReferenceLanguage());
	}

	public void setDefinition(String definition, String lg) {
		if (lg.equals(OntoQLHelper.ENGLISH)) {
			this.definition_en = definition;
		} else {
			this.definition_fr = definition;
		}
	}

	public void setDefinition(String definition) {
		setDefinition(definition, session.getReferenceLanguage());
	}

	public String getVersion() {
		if (!isLoaded) {
			load();
		}
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getRevision() {
		if (!isLoaded) {
			load();
		}
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}

	public String getNote(String lg) {
		if (!isLoaded) {
			load();
		}
		if (lg.equals(OntoQLHelper.ENGLISH)) {
			return note_en;
		} else {
			return note_fr;
		}
	}

	public String getNote() {
		return getNote(session.getReferenceLanguage());
	}

	public void setNote(String note, String lg) {
		if (lg.equals(OntoQLHelper.ENGLISH)) {
			this.note_en = note;
		} else {
			this.note_fr = note;
		}
	}

	public void setNote(String note) {
		setNote(note, session.getReferenceLanguage());
	}

	public String getRemark(String lg) {
		if (!isLoaded) {
			load();
		}
		if (lg.equals(OntoQLHelper.ENGLISH)) {
			return remark_en;
		} else {
			return remark_fr;
		}
	}

	public String getRemark() {
		return getRemark(session.getReferenceLanguage());
	}

	public void setRemark(String remark, String lg) {
		if (lg.equals(OntoQLHelper.ENGLISH)) {
			this.remark_en = remark;
		} else {
			this.remark_fr = remark;
		}
	}

	public void setRemark(String remark) {
		setRemark(remark, session.getReferenceLanguage());
	}

	public String getDateOfCurrentVersion() {
		if (!isLoaded) {
			load();
		}
		return dateOfCurrentVersion;
	}

	public void setDateOfCurrentVersion(String dateOfCurrentVersion) {
		this.dateOfCurrentVersion = dateOfCurrentVersion;
	}

	public String getDateOfCurrentRevision() {
		if (!isLoaded) {
			load();
		}
		return dateOfCurrentRevision;
	}

	public void setDateOfCurrentRevision(String dateOfCurrentRevision) {
		this.dateOfCurrentRevision = dateOfCurrentRevision;
	}

	public String getDateOfOriginalDefinition() {
		if (!isLoaded) {
			load();
		}
		return dateOfOriginalDefinition;
	}

	public void setDateOfOriginalDefinition(String dateOfOriginalDefinition) {
		this.dateOfOriginalDefinition = dateOfOriginalDefinition;
	}

	public String getShortName(String lg) {
		if (!isLoaded) {
			load();
		}
		if (lg.equals(OntoQLHelper.ENGLISH)) {
			return shortName_en;
		} else {
			return shortName_fr;
		}
	}

	public String getShortName() {
		return getShortName(session.getReferenceLanguage());
	}

	public void setShortName(String shortName, String lg) {
		if (lg.equals(OntoQLHelper.ENGLISH)) {
			this.shortName_en = shortName;
		} else {
			this.shortName_fr = shortName;
		}
	}

	public void setShortName(String shortName) {
		setShortName(shortName, session.getReferenceLanguage());
	}

	public OntoDocument getDocOfDefinition() {
		if (!isLoadedDocOfDefinition) {
			loadDocOfDefinition();
		}
		return docOfDefinition;
	}

	public void setDocOfDefinition(OntoDocument docOfDefinition) {
		this.docOfDefinition = docOfDefinition;
	}

	public void loadDocOfDefinition() {
		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			OntoQLResultSet resultSet = stmt
					.executeQuery("SELECT #docOfDefinition FROM #concept WHERE #oid = " + oid + "");
			if (resultSet.next()) {
				setDocOfDefinition(resultSet.getOntoDocument(1));

				isLoadedDocOfDefinition = true;
			} else {
				throw new JOBDBCException("the instance of concept with oid " + getOid() + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	public OntoGraphics getIcon() {
		if (!isLoadedIcon) {
			loadIcon();
		}
		return icon;
	}

	public void setIcon(OntoGraphics icon) {
		this.icon = icon;
	}

	public void loadIcon() {
		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			OntoQLResultSet resultSet = stmt.executeQuery("SELECT #icon FROM #concept WHERE #oid = " + oid + "");
			if (resultSet.next()) {
				setIcon(resultSet.getOntoGraphics(1));

				isLoadedIcon = true;
			} else {
				throw new JOBDBCException("the instance of concept with oid " + getOid() + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}

	public void load() {
		try {
			OntoQLStatement stmt = session.createOntoQLStatement();
			String whereClause = "#oid = " + oid;
			OntoQLResultSet resultSet = stmt.executeQuery(
					"SELECT #oid, #code, #name[en], #name[fr], #definition[en], #definition[fr], #version, #revision, #note[en], #note[fr], #remark[en], #remark[fr], #dateOfCurrentVersion, #dateOfCurrentRevision, #dateOfOriginalDefinition, #shortName[en], #shortName[fr] FROM #concept WHERE "
							+ whereClause);
			if (resultSet.next()) {
				setOid(resultSet.getInt(1));
				setCode(resultSet.getString(2));
				setName(resultSet.getString(3), OntoQLHelper.ENGLISH);
				setName(resultSet.getString(4), OntoQLHelper.FRENCH);
				setDefinition(resultSet.getString(5), OntoQLHelper.ENGLISH);
				setDefinition(resultSet.getString(6), OntoQLHelper.FRENCH);
				setVersion(resultSet.getString(7));
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
				throw new JOBDBCException("the instance of concept with oid " + oid + " doesn't exist");
			}
		} catch (SQLException e) {
			throw new JOBDBCException(e);
		}
	}
}
