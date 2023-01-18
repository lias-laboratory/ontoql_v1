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
package fr.ensma.lisi.ontoql.ewokhub;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for the e-Wok Hub ANR project.
 * 
 * @author Stéphane JEAN
 */
public class Annotation {

	/**
	 * URI of the predicate
	 */
	private String uriPredicate;

	/**
	 * List of URI objects for the annotation
	 */
	private List<String> uriObjects;

	public Annotation() {
		super();
		uriObjects = new ArrayList<String>();
	}

	public Annotation(String uriPredicate) {
		this();
		this.uriPredicate = uriPredicate;
	}

	public String getUriPredicate() {
		return uriPredicate;
	}

	public void setUriPredicate(String uriPredicate) {
		this.uriPredicate = uriPredicate;
	}

	public List<String> getUriObjects() {
		return uriObjects;
	}

	public void setUriObjects(List<String> uriObjects) {
		this.uriObjects = uriObjects;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uriPredicate == null) ? 0 : uriPredicate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Annotation other = (Annotation) obj;
		if (uriPredicate == null) {
			if (other.uriPredicate != null)
				return false;
		} else if (!uriPredicate.equals(other.uriPredicate))
			return false;
		return true;
	}
}
