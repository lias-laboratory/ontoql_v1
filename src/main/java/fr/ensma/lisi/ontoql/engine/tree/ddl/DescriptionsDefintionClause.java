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
package fr.ensma.lisi.ontoql.engine.tree.ddl;

import java.util.ArrayList;

import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.AbstractEntityProperty;
import fr.ensma.lisi.ontoql.core.Attribute;
import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.Description;
import fr.ensma.lisi.ontoql.core.Entity;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.engine.tree.OntoQLSQLWalkerNode;

/**
 * Defines an AST node representing an OntoQL descriptor clause.
 * 
 * @author Stéphane JEAN
 */
public class DescriptionsDefintionClause extends OntoQLSQLWalkerNode {

	private static final long serialVersionUID = 6217525646901843113L;

	/**
	 * The scope of the description of this clause.
	 */
	private Category descriptionScope;

	/**
	 * Returns an array of Properties gathered from the children of the given parent
	 * AST node.
	 * 
	 * @return an array of Properties gathered from the children of the given parent
	 *         AST node.
	 */
	public Description[] collectDescription(boolean isProperty) {
		Description[] res = null;

		AST firstChild = getFirstChild();
		AST parent = this;
		ArrayList<Description> list = new ArrayList<Description>(parent.getNumberOfChildren());
		for (AST n = firstChild; n != null; n = n.getNextSibling()) {
			if (isProperty) {
				list.add(((DescriptionDefinitionNode) n).getPropertyDefined((EntityClass) descriptionScope));
			} else {
				list.add(((DescriptionDefinitionNode) n).getAttributeDefined((Entity) descriptionScope));
			}
		}
		if (isProperty) {
			res = (AbstractEntityProperty[]) list.toArray(new AbstractEntityProperty[list.size()]);
		} else {
			res = (Attribute[]) list.toArray(new Attribute[list.size()]);
		}

		return res;
	}

	/**
	 * Returns an array of Properties gathered from the children of the given parent
	 * AST node.
	 * 
	 * @return an array of Properties gathered from the children of the given parent
	 *         AST node.
	 */
	public EntityProperty[] collectProperties() {
		return (EntityProperty[]) collectDescription(true);
	}

	/**
	 * Returns an array of Properties gathered from the children of the given parent
	 * AST node.
	 * 
	 * @return an array of Properties gathered from the children of the given parent
	 *         AST node.
	 */
	public Attribute[] collectAttributes() {
		return (Attribute[]) collectDescription(false);
	}

	public Category getDescriptionScope() {
		return descriptionScope;
	}

	public void setDescriptionScope(Category descriptionScope) {
		this.descriptionScope = descriptionScope;
	}
}
