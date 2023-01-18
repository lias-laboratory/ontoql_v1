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
 * Class for the e-Wok Hub project.
 * 
 * @author Stéphane JEAN
 */

public class Segment {

	/**
	 * URI of the segment
	 */
	private String uri;

	/**
	 * Annotations on the segment
	 */
	private List<Annotation> annotations;

	public Segment() {
		super();
		annotations = new ArrayList<Annotation>(5);
	}

	public Segment(String uri) {
		this();
		this.uri = uri;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public List<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<Annotation> annotations) {
		this.annotations = annotations;
	}

	public void addAnnotation(Annotation a) {
		if (!annotations.contains(a)) {
			annotations.add(a);
		} else {
			throw new IllegalArgumentException("Duplicate annotation not allowed");
		}
	}

	/**
	 * 
	 * @param uriSegment : URI of the searched segment
	 * @return the segment with the given URI or null if not found
	 */
	public Annotation getAnnotation(String uriPredicateOfAnnotation) {
		Annotation result = null;
		int indexOfElement = annotations.indexOf(new Annotation(uriPredicateOfAnnotation));
		if (indexOfElement != -1) {
			result = annotations.get(indexOfElement);
		}
		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
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
		Segment other = (Segment) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}
}
