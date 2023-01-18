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

/**
 * @author Mickael BARON
 */
public class ReferenceLanguageEvent {

	private String newReferenceLanguage;

	private String previousReferenceLanguage;

	public ReferenceLanguageEvent(String previousReferenceLanguage, String newReferenceLanguage) {
		this.previousReferenceLanguage = previousReferenceLanguage;
		this.newReferenceLanguage = newReferenceLanguage;
	}

	public String getNewReferenceLanguage() {
		return newReferenceLanguage;
	}

	public String getPreviousReferenceLanguage() {
		return previousReferenceLanguage;
	}

	public boolean isReferenceLanguageHasHChanged() {
		if (previousReferenceLanguage == null && newReferenceLanguage != null) {
			return true;
		} else if (previousReferenceLanguage != null && newReferenceLanguage == null) {
			return true;
		} else if (previousReferenceLanguage != null && newReferenceLanguage != null) {
			return !previousReferenceLanguage.equalsIgnoreCase(newReferenceLanguage);
		} else {
			return false;
		}
	}
}
