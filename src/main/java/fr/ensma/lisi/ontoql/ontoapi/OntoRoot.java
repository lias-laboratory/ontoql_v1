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

import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;

/**
 * Root class of the OntoAPI hierarchy of classes
 *
 * @author Stéphane JEAN
 */
public abstract class OntoRoot {

	/**
	 * Internal identifier of this class.
	 */
	protected int oid = 0;

	/**
	 * A session needed to access to the underlying OBDB
	 */
	protected OntoQLSession session;

	/**
	 * True if the primitive attributes have been loaded.
	 */
	protected boolean isLoaded = false;

	public OntoRoot() {
	}

	public OntoRoot(int oid, OntoQLSession s) {
		this.oid = oid;
		this.session = s;
	}

	public abstract void load();

	public int getOid() {
		return oid;
	}

	public void setOid(int oid) {
		this.oid = oid;
	}

	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + oid;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OntoRoot other = (OntoRoot) obj;
		if (oid != other.oid)
			return false;
		return true;
	}

}
