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

public class Document {

	/**
	 * URI of the document
	 */
	private String uri;

	/**
	 * source of the document
	 */
	private String source;

	/**
	 * List of segments of the document
	 */
	private List<Segment> segments;

	public Document() {
		super();
		this.segments = new ArrayList<Segment>(100);
	}

	public Document(String uri) {
		this();
		this.uri = uri;
	}

	public Document(String uri, String source) {
		this(uri);
		this.source = source;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<Segment> getSegments() {
		return segments;
	}

	public void setSegments(List<Segment> segments) {
		this.segments = segments;
	}

	public void addSegment(Segment s) {
		if (!segments.contains(s)) {
			segments.add(s);
		} else {
			throw new IllegalArgumentException("Duplicate segment not allowed");
		}
	}

	/**
	 * 
	 * @param uriSegment : URI of the searched segment
	 * @return the segment with the given URI or null if not found
	 */
	public Segment getSegment(String uriSegment) {
		Segment result = null;
		int indexOfElement = segments.indexOf(new Segment(uriSegment));
		if (indexOfElement != -1) {
			result = segments.get(indexOfElement);
		}
		return result;
	}
}
