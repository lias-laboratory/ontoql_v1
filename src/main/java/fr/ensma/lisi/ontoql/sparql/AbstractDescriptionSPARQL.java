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
import fr.ensma.lisi.ontoql.engine.SPARQLOntoQLWalker;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.util.ASTUtil;
import fr.ensma.lisi.ontoql.util.SPARQLUtil;

/**
 * Default behaviour of a SPARQL property or attribute
 * 
 * @author Stéphane JEAN
 */
public abstract class AbstractDescriptionSPARQL implements DescriptionSPARQL {

	/**
	 * Name of this SPARQL property or attribute
	 */
	protected String name = null;

	/**
	 * Namespace of this SPARQL property or attribute
	 */
	protected String namespace = null;

	/**
	 * variable used to reference this SPARQL property or attribute
	 */
	protected String variable = null;

	/**
	 * True if this SPARQL property or attribute is involved in a join condition
	 * requiring the coalesce operator
	 */
	protected boolean isCoalesce = false;

	/**
	 * The SPARQL property or attribute which is joined with this one in a join
	 * condition requiring the coalesce operator
	 */
	protected DescriptionSPARQL coalesceDescription = null;

	/**
	 * Walker using this SPARQL class or entity
	 */
	protected SPARQLOntoQLWalker walker = null;

	/**
	 * Scope of this SPARQL property or attribute
	 */
	protected CategorySPARQL scope = null;

	@Override
	public void setScope(CategorySPARQL category) {
		scope = category;
	}

	/**
	 * Get the node representing this SPARQL property or attribute
	 * 
	 * @param aliasNeeded True if an alias must be added
	 * @param isCoalesce  True if this SPARQL property or attribute is involved in a
	 *                    join condition requiring the coalesce operator
	 * @return the node representing this SPARQL property or attribute
	 */
	public AST getDotElement(boolean aliasNeeded, boolean isCoalesce, ASTFactory astFactory) {
		AST res = null;
		AST dotNode = null;
		if (isCoalesce) {
			res = ASTUtil.create(astFactory, OntoQLTokenTypes.METHOD_CALL, "(");
			AST coalesceNode = ASTUtil.create(astFactory, OntoQLTokenTypes.IDENT, "COALESCE");
			AST exprListNode = ASTUtil.create(astFactory, OntoQLTokenTypes.EXPR_LIST, "exprList");
			exprListNode.addChild(this.getDotElement(false, false, astFactory));
			exprListNode.addChild(coalesceDescription.getDotElement(false, false, astFactory));
			res.addChild(coalesceNode);
			res.addChild(exprListNode);
		} else {
			String aliasName = null;
			String descriName = null;
			if (isMultivalued()) {
				if (isAttribute()) {
					dotNode = SPARQLUtil.getNodeAttributeURI(name, astFactory);
				} else {
					descriName = "URI";
				}
				aliasName = name;
			} else {
				aliasName = scope.getAlias();
				descriName = name;
			}
			if (dotNode == null) {
				dotNode = ASTUtil.create(astFactory, OntoQLTokenTypes.DOT, "DOT");
				AST prefixNode = ASTUtil.create(astFactory, OntoQLTokenTypes.IDENT, aliasName);
				dotNode.addChild(prefixNode);

				AST propNode = ASTUtil.create(astFactory, OntoQLTokenTypes.IDENT, descriName);
				dotNode.addChild(propNode);
				if (this.namespace != null) {
					AST aliasNode = ASTUtil.create(astFactory, OntoQLTokenTypes.NAMESPACE_ALIAS, this.namespace);
					propNode.addChild(aliasNode);
				}
			}

			if (aliasNeeded) {
				String aliasNodeName = variable.substring(1);
				int indexOfURI = variable.indexOf("URI");
				if (indexOfURI != -1) {
					aliasNodeName = aliasNodeName.substring(0, indexOfURI - 1);
				}
				AST aliasNode = ASTUtil.create(astFactory, OntoQLTokenTypes.IDENT, aliasNodeName);
				res = ASTUtil.create(astFactory, OntoQLTokenTypes.AS, "as");
				res.addChild(dotNode);
				res.addChild(aliasNode);
			} else {
				res = dotNode;
			}
		}
		return res;
	}

	/**
	 * Get the node representing this SPARQL property or attribute
	 * 
	 * @return the node representing this SPARQL property or attribute
	 */
	public AST getDotElement(boolean aliasNeeded) {
		return getDotElement(aliasNeeded, this.isCoalesce, walker.getASTFactory());
	}

	/**
	 * @param isCoalesce the isCoalesce to set
	 */
	public void setCoalesce(boolean isCoalesce) {
		this.isCoalesce = isCoalesce;
	}

	/**
	 * @param coalesceProperty the coalesceProperty to set
	 */
	public void setCoalesceDescription(DescriptionSPARQL coalesceProperty) {
		this.coalesceDescription = coalesceProperty;
	}

	/**
	 * @return the variable
	 */
	public String getVariable() {
		return variable;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	public CategorySPARQL getScope() {
		return scope;
	}
}
