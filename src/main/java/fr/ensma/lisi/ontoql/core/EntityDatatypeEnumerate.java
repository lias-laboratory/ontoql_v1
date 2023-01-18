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
package fr.ensma.lisi.ontoql.core;

import java.util.List;

/**
 * Enumerate datatype.
 *
 * @author Stephane JEAN
 */
public interface EntityDatatypeEnumerate extends EntityDatatype {

	/**
	 * @return Returns the values of this enumerate type
	 */
	List<String> getValues();

	/**
	 * @param p
	 */
	void addValues(List<String> p);

	/**
	 * @param p
	 */
	void addValue(String p);

	/**
	 * @param p
	 */
	void setValues(List<String> p);
}
