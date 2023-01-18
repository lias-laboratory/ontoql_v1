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
package fr.ensma.lisi.ontoql.sparql;

import antlr.ASTFactory;
import antlr.collections.AST;

/**
 * A SPARQL property or attribute
 * 
 * @author Stéphane JEAN
 */
public interface DescriptionSPARQL {

	/**
	 * Get the scope of this SPARQL property or attribute return the scope of this
	 * SPARQL property or attribute
	 */
	CategorySPARQL getScope();

	/**
	 * Set the scope of this SPARQL property or attribute
	 * 
	 * @param category
	 */
	void setScope(CategorySPARQL category);

	/**
	 * Get the node corresponding to this SPARQL property or attribute
	 * 
	 * @param isCoalesce True if this SPARQL property or attribute is involved in a
	 *                   join condition requiring the coalesce operator
	 * @param astFactory Factory of node
	 * @return the node corresponding to this SPARQL property or attribute
	 */
	AST getDotElement(boolean aliasNeeded, boolean isCoalesce, ASTFactory astFactory);

	/**
	 * Get the node corresponding to this SPARQL property or attribute
	 * 
	 * @return the node corresponding to this SPARQL property or attribute
	 */
	AST getDotElement(boolean aliasNeeded);

	/**
	 * Get the variable corresponding to this property
	 * 
	 * @return the variable corresponding to this property
	 */
	String getVariable();

	/**
	 * Get the name of a SPARQL property or attribute
	 * 
	 * @return the name of a SPARQL property or attribute
	 */
	String getName();

	/**
	 * True if this property or attribute is multivalued
	 * 
	 * @return True if this property or attribute is multivalued
	 */
	boolean isMultivalued();

	/**
	 * True if this property or attribute is optional
	 * 
	 * @return True if this property or attribute is optional
	 */
	boolean isOptional();

	/**
	 * True if this is a property
	 * 
	 * @return True if this is a property
	 */
	boolean isProperty();

	/**
	 * True if this is an attribute
	 * 
	 * @return True if this is an attribute
	 */
	boolean isAttribute();

	/**
	 * Set wether this description needs the coalesce operator to be translated
	 * 
	 * @param isOptional
	 */
	void setCoalesce(boolean isOptional);

	/**
	 * @param coalesceDescription
	 */
	void setCoalesceDescription(DescriptionSPARQL coalesceDescription);

}
