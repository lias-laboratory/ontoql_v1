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
package fr.ensma.lisi.ontoql.exception;

/**
 * Knows how to extract a violated constraint name from an error message based
 * on the fact that the constraint name is templated within the message.
 *
 * @author Stéphane JEAN
 */
public abstract class TemplatedViolatedConstraintNameExtracter implements ViolatedConstraintNameExtracter {

	/**
	 * Extracts the constraint name based on a template (i.e.,
	 * <i>templateStart</i><b>constraintName</b><i>templateEnd</i>).
	 * 
	 * @param templateStart The pattern denoting the start of the constraint name
	 *                      within the message.
	 * @param templateEnd   The pattern denoting the end of the constraint name
	 *                      within the message.
	 * @param message       The templated error message containing the constraint
	 *                      name.
	 * @return The found constraint name, or null.
	 */
	protected String extractUsingTemplate(String templateStart, String templateEnd, String message) {
		int templateStartPosition = message.indexOf(templateStart);
		if (templateStartPosition < 0) {
			return null;
		}

		int start = templateStartPosition + templateStart.length();
		int end = message.indexOf(templateEnd, start);
		if (end < 0) {
			end = message.length();
		}

		return message.substring(start, end);
	}
}
