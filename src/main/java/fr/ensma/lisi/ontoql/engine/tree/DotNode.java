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

import antlr.SemanticException;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.AbstractEntityProperty;
import fr.ensma.lisi.ontoql.core.Attribute;
import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.Description;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.dql.FromClause;
import fr.ensma.lisi.ontoql.engine.tree.dql.FromElement;
import fr.ensma.lisi.ontoql.engine.tree.dql.FromReferenceNode;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.QueryException;

/**
 * Represents a path expression.
 * 
 * @author Stéphane JEAN
 */
public class DotNode extends FromReferenceNode implements ResolvableNode {

	private static final long serialVersionUID = 1240492146635947453L;

	@Override
	public final EntityDatatype getDataType() {
		// A dot node is never in the transformed tree
		// The last ident node is only left
		// Thus this method will never be used for type checking
		return null;
	}

	/**
	 * Helper method to resolve a path expression.
	 * 
	 * @param propNode  node corresponding to an association property
	 * @param polymorph true if the query is polymorphic
	 * @return the resolved node
	 * @throws SemanticException if a semantic error is detected
	 */
	protected final AST resolveDotExpresession(final IdentNode propNode, final boolean polymorph)
			throws SemanticException {

		Category currentContext = propNode.getFromElement().getCategory();
		Description pathProp = propNode.getDescription();
		pathProp.setCurrentContext(currentContext);

		// The path of this property is the next sibling
		AST pathPropNode = propNode.getNextSibling();

		// Add an alias to the range of the path property
		FromElement fromElementAlreadyAdded = getWalker().getGeneratedFromElement((AbstractEntityProperty) pathProp);
		if (fromElementAlreadyAdded == null) {
			// the path hasn't already been proceed
			fromElementAlreadyAdded = getWalker().addImplicitJoin(propNode.getFromElement(), propNode, polymorph);
		}

		if (pathPropNode.getType() == OntoQLTokenTypes.DOT) {
			return ((DotNode) pathPropNode).resolve(null, fromElementAlreadyAdded);
		} else {

			AbstractEntityProperty finalPathProperty = null;
			try {
				AST firstChild = pathPropNode.getFirstChild();
				if (firstChild != null && firstChild.getType() == OntoQLTokenTypes.NAMESPACE_ALIAS) {
					finalPathProperty = (AbstractEntityProperty) getEntityFactory()
							.createDescription(pathPropNode.getText(), getNamespace(firstChild.getText()));
				} else {
					finalPathProperty = (AbstractEntityProperty) getEntityFactory()
							.createDescription(pathPropNode.getText());
				}

				finalPathProperty.getInternalId();
			} catch (JOBDBCException exc) {
				throw new SemanticException(exc.getMessage());
			}
			finalPathProperty.setCurrentContext(fromElementAlreadyAdded.getCategory());
			if (!finalPathProperty.isDefined()) {
				throw new QueryException(
						"The property '" + finalPathProperty.getName() + "' is not defined on the range of the class "
								+ fromElementAlreadyAdded.getCategory().getName());
			}

			pathPropNode.setType(SQLTokenTypes.COLUMN);
			pathPropNode.setText(finalPathProperty.toSQL());
			((IdentNode) pathPropNode).setDescription(finalPathProperty);

			return pathPropNode;
		}

	}

	@Override
	public final String getLabel() {
		// Like getDatatype this method will not be used
		return "";
	}

	@Override
	public final AST resolve(AST prefix) throws SemanticException {
		AST res = resolve(prefix, null);
		return res;
	}

	/**
	 * Helper method to resolve a path expression.
	 * 
	 * @param prefix prefix of this node
	 * @param f      from element corresponding to this node
	 * @return the resolved node
	 * @throws SemanticException if a semantic error is detected
	 */
	public final AST resolve(AST prefix, final FromElement f) throws SemanticException {
		// Get the current from clause
		FromClause currentFromClause = getWalker().getCurrentFromClause();
		// Get the left child of this dot node
		IdentNode propNode = (IdentNode) getFirstChild();

		// It may be a prefix
		String prefixText = propNode.getText();
		FromElement currentFromElement = currentFromClause.getFromElement(prefixText);
		if (currentFromElement != null) {
			return ((ResolvableNode) propNode.getNextSibling()).resolve(propNode);
		}

		if (f == null) {
			propNode.resolve(prefix, true, true);
		} else {
			propNode.setFromElement(f);
			propNode.resolve(prefix, false, true);
		}

		if (propNode.getDescription() instanceof Attribute) {
			AST pathPropNode = propNode.getNextSibling();
			if (pathPropNode.getType() == OntoQLTokenTypes.DOT) {
				return ((DotNode) pathPropNode).resolve(null, propNode.getFromElement());
			} else {
				((IdentNode) pathPropNode).setFromElement(propNode.getFromElement());
				return ((IdentNode) pathPropNode).resolve(null, false, true);
			}
		}

		return resolveDotExpresession(propNode, propNode.getFromElement().getCategory().isPolymorph());
	}
}
