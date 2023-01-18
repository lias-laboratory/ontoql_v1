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
package fr.ensma.lisi.ontoql.engine.tree.dql;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import antlr.ASTFactory;
import antlr.SemanticException;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLSQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.DisplayableNode;
import fr.ensma.lisi.ontoql.engine.tree.OntoQLSQLWalkerNode;
import fr.ensma.lisi.ontoql.engine.util.ASTUtil;

/**
 * Represents the 'FROM' part of a query or subquery.
 * 
 * @author Stéphane JEAN
 */
public class FromClause extends OntoQLSQLWalkerNode implements OntoQLSQLTokenTypes, DisplayableNode {

	private static final long serialVersionUID = -1980571134308443154L;

	/**
	 * Root level of nested query.
	 */
	public static final int ROOT_LEVEL = 1;

	/**
	 * Level of nested query.
	 */
	private int level = ROOT_LEVEL;

	/**
	 * List of from elements of this from clause.
	 */
	private Set<FromElement> fromElements = new HashSet<FromElement>();

	/**
	 * Map beetween alias and from element.
	 */
	private Map<String, FromElement> fromElementByClassAlias = new HashMap<String, FromElement>();

	/**
	 * Pointer to the parent FROM clause, if there is one.
	 */
	private FromClause parentFromClause;

	/**
	 * Collection of FROM clauses of which this clause is the parent.
	 */
	private Set<FromClause> childFromClauses;

	/**
	 * Counts the from elements as they are added.
	 */
	private int fromElementCounter = 0;

	/**
	 * Add a from element to this from clause.
	 * 
	 * @param path     from element to add
	 * @param star     node corresponding to the polymorphic operator
	 * @param alias    alias of this from element
	 * @param genAlias true if we must generate an alias
	 * @return the from element added
	 * @throws SemanticException if a semantic error is detected
	 */
	public final FromElement addFromElement(final AST node, final AST star, final AST alias, final boolean genAlias)
			throws SemanticException {
		String path = node.getText();
		String namespaceAlias = null;
		AST firstChild = node.getFirstChild();
		if (firstChild != null) {
			namespaceAlias = firstChild.getText();
		}
		// The path may be a reference to an alias defined in the parent query.
		String classAlias = (alias == null) ? path : alias.getText();
		checkForDuplicateClassAlias(classAlias);
		FromElement fromElement = new FromElement(path, namespaceAlias, classAlias, star, genAlias, this.getWalker());
		registerFromElement(fromElement);
		fromElement.setFromClause(this);
		return fromElement;
	}

	/**
	 * Add a AND condition in the Where condition of this from clause. If the Where
	 * clause doesn't exist, it's created.
	 * 
	 * @param condition a condition to add in the where clause
	 */
	public void addWhereCondition(String condition) {

		// A node factory is required
		ASTFactory astFactory = getWalker().getASTFactory();

		AST joinCondition = ASTUtil.create(astFactory, OntoQLSQLTokenTypes.JOIN_CONDITION, condition);

		AST whereClause = this.getNextSibling();
		if (whereClause == null || whereClause.getType() != WHERE) {
			// the where clause doesn't exist
			whereClause = ASTUtil.create(getWalker().getASTFactory(), OntoQLSQLTokenTypes.WHERE, "where");
			whereClause.addChild(joinCondition);
			ASTUtil.appendSibling(this, whereClause);
		} else {
			// the where clause exist, a AND condition is added
			AST andClause = ASTUtil.create(getWalker().getASTFactory(), OntoQLSQLTokenTypes.AND, "and");
			andClause.addChild(joinCondition);
			andClause.addChild(whereClause.getFirstChild());
			whereClause.setFirstChild(andClause);
		}
	}

	/**
	 * Retreives the from element represented by the given alias.
	 * 
	 * @param aliasOrClassName The alias by which to locate the from-element.
	 * @return The from element assigned the given alias, or null if none.
	 */
	public final FromElement getFromElement(final String aliasOrClassName) {
		FromElement fromElement = (FromElement) fromElementByClassAlias.get(aliasOrClassName);
		if (fromElement == null && parentFromClause != null) {
			fromElement = parentFromClause.getFromElement(aliasOrClassName);
		}
		return fromElement;
	}

	/**
	 * Convenience method to check whether a given token represents a from element
	 * alias.
	 * 
	 * @param possibleAlias The potential from-element alias to check.
	 * @return True if the possibleAlias is an alias to a from-element visible from
	 *         this point in the query graph.
	 */
	public final boolean isFromElementAlias(final String possibleAlias) {
		boolean isAlias = fromElementByClassAlias.containsKey(possibleAlias);
		if (!isAlias && parentFromClause != null) {
			// try the parent FromClause...
			isAlias = parentFromClause.isFromElementAlias(possibleAlias);
		}
		return isAlias;
	}

	/**
	 * Get the first from element of this from clause.
	 * 
	 * @return the first from element of this from clause
	 */
	public final FromElement getFirstFromElement() {
		return (FromElement) getFromElements().get(0);
	}

	/**
	 * Returns true if the from node contains the class alias name.
	 * 
	 * @param alias The OntoQL class alias name.
	 * @return true if the from node contains the class alias name.
	 */
	public final boolean containsClassAlias(final String alias) {
		return fromElementByClassAlias.keySet().contains(alias);
	}

	/**
	 * Register a from element.
	 * 
	 * @param element the from element to register
	 */
	public final void registerFromElement(final FromElement element) {
		fromElements.add(element);
		element.setFromClause(this);
		if (element.getCategory() != null) {
			String classAlias = element.getCategory().getCategoryAlias();
			if (classAlias != null) {
				fromElementByClassAlias.put(classAlias, element);
			}
		}
	}

	/**
	 * Check if the alias is already used.
	 * 
	 * @param classAlias the alias to test
	 * @throws SemanticException if the alias exists
	 */
	private void checkForDuplicateClassAlias(final String classAlias) throws SemanticException {
		if (classAlias != null && fromElementByClassAlias.containsKey(classAlias)) {
			throw new SemanticException("Duplicate definition of alias '" + classAlias + "'");
		}
	}

	/**
	 * Returns the list of from elements in order.
	 * 
	 * @return the list of from elements (instances of FromElement).
	 */
	public final List getFromElements() {
		return ASTUtil.collectDirectChildren(this);
	}

	/**
	 * Gives a string representation of this node
	 * 
	 * @return a string representing this node
	 * @see DisplayableNode#getDisplayText()
	 */
	public final String getDisplayText() {
		return "FromClause{" + "level=" + level + ", fromElementCounter=" + fromElementCounter + ", fromElements="
				+ getFromElements().size() + "}";
	}

	/**
	 * Set a from clause as the parent of this one.
	 * 
	 * @param parentFromClause the parent from clause of this from clause
	 */
	public final void setParentFromClause(final FromClause parentFromClause) {
		this.parentFromClause = parentFromClause;
		if (parentFromClause != null) {
			level = parentFromClause.getLevel() + 1;
			parentFromClause.addChild(this);
		}
	}

	/**
	 * Add a child from clause to this from clause.
	 * 
	 * @param fromClause the new child of this from clause
	 */
	private void addChild(final FromClause fromClause) {
		if (childFromClauses == null) {
			childFromClauses = new HashSet<FromClause>();
		}
		childFromClauses.add(fromClause);
	}

	/**
	 * Is this a from clause of a nested query?
	 * 
	 * @return true if this is the from clause of a nested query
	 */
	public final boolean isSubQuery() {
		return parentFromClause != null;
	}

	/**
	 * @return the parent from clause
	 */
	public final FromClause getParentFromClause() {
		return parentFromClause;
	}

	/**
	 * @return the level of nesting of this from clause
	 */
	public final int getLevel() {
		return level;
	}

	/**
	 * @return the next index of the from elements of this from clause
	 */
	public final int nextFromElementCounter() {
		return fromElementCounter++;
	}

	/**
	 * @return a string representation of this object
	 * @see Object#toString()
	 */
	public final String toString() {
		return "FromClause{" + "level=" + level + "}";
	}
}
