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
package fr.ensma.lisi.ontoql.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;

import fr.ensma.lisi.ontoql.core.EntityDatatypeInt;
import fr.ensma.lisi.ontoql.core.EntityDatatypeReal;
import fr.ensma.lisi.ontoql.core.EntityDatatypeString;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.core.hsqldb.FactoryEntityHSQLDB;
import fr.ensma.lisi.ontoql.core.ontodb.FactoryEntityOntoDB;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;

/**
 * @author Stéphane JEAN
 */
public class OntoQLHelper {

	/**
	 * OntoQL <tt>integer</tt> type.
	 */
	public static final EntityDatatypeInt INT = new EntityDatatypeInt();

	/**
	 * OntoQL <tt>string</tt> type.
	 */
	public static final EntityDatatypeString STRING = new EntityDatatypeString();

	/**
	 * OntoQL <tt>real</tt> type.
	 */
	public static final EntityDatatypeReal REAL = new EntityDatatypeReal();

	public static final String ONTOAPI_PACKAGE = "fr.ensma.lisi.ontoql.ontoapi";

	public static final String CORE_PACKAGE = "fr.ensma.lisi.ontoql.core";

	public static final String NAMESPACE_ONTOLOGY_MODEL = "http://www.lisi.ensma.fr/OntoQL#";

	public static final String PREFIX_NAMESPACE_RDF = "rdf";

	public final static String NO_NAMESPACE = null;

	public static final String PREFIX_ONTOLOGYMODEL_ELEMENT = "#";

	public static final String OBDB_NAME = "OntoDB";

	public final static String PREFIX_EXTERNAL_ID = "@";

	public final static String SEPARATOR_EXTERNAL_ID = "-";

	public final static String PREFIX_INTERNAL_ID = "!";

	public final static String PREFIX_NAME_ID = "\"";

	public final static String NO_LANGUAGE = null;

	public final static String ENGLISH = "en";

	public final static String FRENCH = "fr";

	public final static String SPANISH = "es";

	protected static boolean isLoadedLanguages = false;

	protected static Map languages;

	/**
	 * Return another language than those pass in parameter
	 */
	public static String getOtherLanguage(String lg) {
		String otherLg = null;
		if (lg != null) {
			if (lg.equals(ENGLISH))
				otherLg = FRENCH;
			else {
				otherLg = ENGLISH;
			}
		}
		return otherLg;
	}

	/**
	 * True If the language is available
	 */
	public static boolean isLanguageAvailable(String lg) {
		boolean res = false;
		if (lg != null) {
			res = lg.equals(ENGLISH) || lg.equals(FRENCH);
		}
		return res;
	}

	public static String getLanguage(String lg, Session session) {
		if (!isLoadedLanguages) {
			loadLanguages(session);
			isLoadedLanguages = true;
		}
		return (String) languages.get(lg);
	}

	/**
	 * Return true if the command insert a new namespace or set a new default
	 * namespace
	 */
	public static boolean isNamespaceParameterCommand(String cmd) {
		cmd = cmd.toLowerCase();
		return (cmd.indexOf("#ontology") != -1) || (cmd.indexOf("set namespace") != -1);
	}

	/**
	 * Return true if the command set a new default language
	 */
	public static boolean isLanguageParameterCommand(String cmd) {
		cmd = cmd.toLowerCase();
		return cmd.indexOf("set language") != -1;
	}

	public static boolean isNull(String value) {
		value = value.toLowerCase();
		return value.equals("null");
	}

	protected static void loadLanguages(Session session) {

		languages = new HashMap();

		StringBuffer queryBuffer = new StringBuffer();
		queryBuffer.append("SELECT rid, value ");
		queryBuffer.append("FROM array_value_present_translations_language_codes ");

		try {

			Statement st = session.connection().createStatement();
			ResultSet rs = st.executeQuery(queryBuffer.toString());
			String lg;
			String lgCode;
			while (rs.next()) {
				lgCode = rs.getString(1);
				lg = rs.getString(2);
				if (!languages.containsKey(lg)) {
					languages.put(lg, lgCode);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static FactoryEntity constructFactoryEntity(OntoQLSession session) {
		FactoryEntity res = null;
		if (OBDB_NAME.equals("HSQLDB")) {
			res = new FactoryEntityHSQLDB(session);
		} else {
			res = new FactoryEntityOntoDB(session);
		}
		return res;
	}
}
