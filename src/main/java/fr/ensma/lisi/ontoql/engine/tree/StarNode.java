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
package fr.ensma.lisi.ontoql.engine.tree;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import antlr.SemanticException;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.engine.tree.dql.FromElement;
import fr.ensma.lisi.ontoql.engine.tree.dql.FromReferenceNode;

/**
 * Represents an internal identifier.
 * 
 * @author Stéphane JEAN
 */
public class StarNode extends FromReferenceNode implements ResolvableNode {

	private static final long serialVersionUID = 2050057448659648539L;

	/**
	 * A logger for this class.
	 */
	private static Log log = LogFactory.getLog(StarNode.class);

	@Override
	public final String getLabel() {
		return null;
	}

	@Override
	public final EntityDatatype getDataType() {
		return null;
	}

	/**
	 * Translate * into the set of properties defined of elements defined in the
	 * from clause.
	 * 
	 * @param star polymorphic node
	 * @return the resolved node
	 * @throws SemanticException if a semantic error is detected
	 * @see ResolvableNode#resolve(AST)
	 */
	public final AST resolve(final AST star) throws SemanticException {
		log.debug("Resolve a Star node");

		IdentNode res = null;
		// The result is a node representing the oid of the first from element
		// It is link to nodes representing properties of this from element and
		// to the nodes representing properties of other from elements

		// First get the list of all elements in the from clause
		List fromElements = getWalker().getCurrentFromClause().getFromElements();
		// variable to iter over this list
		FromElement currentFromElement = null;

		// the set of properties of the current from element
		IdentNode firtResCurrentFromElement = null;
		// store the last properties of the set of properties of the current
		// from element
		IdentNode lastResCurrentFromElement = null;
		// store the last properties of the set of properties of the previous
		// from element
		IdentNode lastResPreviousCurrentFromElement = null;
		// store the result of resolve star (first and last element)
		IdentNode[] resCurrentFromElement;

		// Iters over each element of the list
		for (int i = 0; i < fromElements.size(); i++) {
			currentFromElement = (FromElement) fromElements.get(i);
			// Do not iter on added from element to compute path expressions
			if (!currentFromElement.isImplicitJoin()) {

				resCurrentFromElement = resolveStar(currentFromElement);
				firtResCurrentFromElement = resCurrentFromElement[0];
				lastResCurrentFromElement = resCurrentFromElement[1];

				// the final result is the node of the first from element
				if (i == 0) {
					res = firtResCurrentFromElement;
				} else {
					lastResPreviousCurrentFromElement.setNextSibling(firtResCurrentFromElement);
				}
				lastResPreviousCurrentFromElement = lastResCurrentFromElement;
			}
		}

		return res;
	}

	/**
	 * Translate * for a from element.
	 * 
	 * @param fromElement The from element to translate
	 * @return the first properties translated
	 * @throws SemanticException if a semantic error is detected
	 */
	public final IdentNode[] resolveStar(final FromElement fromElement) throws SemanticException {
		// The result is two nodes corresponding to an oid property or attribute
		// and the last
		// property or attribute
		IdentNode[] res = new IdentNode[2];

		if (fromElement.isEntityFromElement()) {
			res = resolveStarForEntity(fromElement);
		} else {
			res = resolveStarForClass(fromElement);
		}
		return res;
	}

	public IdentNode[] resolveStarForClass(final FromElement fromElement) throws SemanticException {
		IdentNode[] res = new IdentNode[2];

		// First create the node corresponding to the oid property
		// this is the result of this method
		res[0] = getWalker().createPropertyOidNode(fromElement);
		res[0].translateToSQL(false, false);
		// Add it to the projection list
		getWalker().getExpressionInSelect().add(res[0]);

		// create the list of properties node, add it in select, and translate
		// it
		// in SQL resolving the association property
		IdentNode[] listPropertiesNode = getWalker().createDescriptionsNodes(fromElement, true, true);
		res[0].setNextSibling(listPropertiesNode[0]);
		res[1] = listPropertiesNode[1];

		// the last node is the oid node if the previous list is empty
		if (res[1] == null) {
			res[1] = res[0];
		}

		// The result must not be added in the list of the projection
		// This is handle manually
		res[0].setToAddInProjectionList(false);

		return res;
	}

	public IdentNode[] resolveStarForEntity(final FromElement fromElement) throws SemanticException {
		IdentNode[] res = new IdentNode[2];

		// create the list of properties node, add it in select, and translate
		// it
		// in SQL resolving the association property
		IdentNode[] listPropertiesNode = getWalker().createDescriptionsNodes(fromElement, true, true);
		res[0] = listPropertiesNode[0];
		res[1] = listPropertiesNode[1];

		// the last node is the oid node if the previous list is empty
		if (res[1] == null) {
			res[1] = res[0];
		}

		// The result must not be added in the list of the projection
		// This is handle manually
		res[0].setToAddInProjectionList(false);

		return res;
	}
}
