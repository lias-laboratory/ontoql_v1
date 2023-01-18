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
package fr.ensma.lisi.ontoql.engine.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import antlr.RecognitionException;
import fr.ensma.lisi.ontoql.exception.QueryException;
import fr.ensma.lisi.ontoql.exception.QuerySyntaxException;

/**
 * An error handler that counts parsing errors and warnings.
 * 
 * @author Stéphane JEAN
 */
public class ErrorCounter implements ParseErrorHandler {

	/**
	 * A logger for this class.
	 */
	private Log log = LogFactory.getLog(ErrorCounter.class);

	/**
	 * A logger for the parser class.
	 **/
	private Log ontoQLLog = LogFactory.getLog("fr.ensma.lisi.ontoql.engine.PARSER");

	/**
	 * List of errors.
	 */
	private List<String> errorList = new ArrayList<String>();

	/**
	 * List of warning.
	 */
	private List<String> warningList = new ArrayList<String>();

	/**
	 * List of error detected by the parser antlr.
	 */
	private List<RecognitionException> recognitionExceptions = new ArrayList<RecognitionException>();

	@Override
	public final void reportError(final RecognitionException e) {
		reportError(e.toString());
		recognitionExceptions.add(e);
		if (log.isDebugEnabled()) {
			log.debug(e, e);
		}
	}

	@Override
	public final void reportError(final String message) {
		ontoQLLog.error(message);
		errorList.add(message);
	}

	@Override
	public final int getErrorCount() {
		return errorList.size();
	}

	@Override
	public final void reportWarning(final String message) {
		ontoQLLog.debug(message);
		warningList.add(message);
	}

	/**
	 * Helper method to format a message of error.
	 * 
	 * @return a message of error as a String
	 */
	private String getErrorString() {
		StringBuffer buf = new StringBuffer();
		for (Iterator iterator = errorList.iterator(); iterator.hasNext();) {
			buf.append((String) iterator.next());
			if (iterator.hasNext()) {
				buf.append("\n");
			}

		}
		return buf.toString();
	}

	@Override
	public final void throwQueryException() {
		if (getErrorCount() > 0) {
			if (recognitionExceptions.size() > 0) {
				throw new QuerySyntaxException((RecognitionException) recognitionExceptions.get(0));
			} else {
				throw new QueryException(getErrorString());
			}
		} else {
			// all clear
			if (log.isDebugEnabled()) {
				log.debug("throwQueryException() : no errors");
			}
		}
	}
}
