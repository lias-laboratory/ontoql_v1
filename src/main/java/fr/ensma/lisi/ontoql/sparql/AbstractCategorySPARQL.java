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

import java.util.ArrayList;
import java.util.List;

import antlr.ASTFactory;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.engine.SPARQLOntoQLWalker;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.util.ASTUtil;

/**
 * Default behaviour of a SPARQL class or entity
 * 
 * @author Stéphane JEAN
 */
public abstract class AbstractCategorySPARQL implements CategorySPARQL {

	/**
	 * Name of this SPARQL class or entity
	 */
	protected String name = null;

	/**
	 * Alias used to reference this SPARQL class or entity
	 */
	protected String alias = null;

	/**
	 * Variable used to reference this SPARQL class or entity
	 */
	protected String variable = null;

	/**
	 * Namespace of this SPARQL class or entity
	 */
	protected String namespace = null;

	/**
	 * Oid of this SPARQL class or entity
	 */
	protected DescriptionSPARQL oid = null;

	/**
	 * list of properties or attributes.
	 */
	protected List<DescriptionSPARQL> descriptions = new ArrayList<DescriptionSPARQL>();

	/**
	 * Walker using this SPARQL class or entity
	 */
	protected SPARQLOntoQLWalker walker = null;

	@Override
	public void addDescription(DescriptionSPARQL description) {
		descriptions.add(description);
		description.setScope(this);
	}

	@Override
	public List getDescriptions() {
		return descriptions;
	}

	@Override
	public AST getFromElement(ASTFactory astFactory) {
		AST res = ASTUtil.create(astFactory, OntoQLTokenTypes.RANGE, "RANGE");
		AST nameNode = ASTUtil.create(astFactory, OntoQLTokenTypes.IDENT, name);
		res.addChild(nameNode);
		if (namespace != null) {
			nameNode.addChild(ASTUtil.create(astFactory, OntoQLTokenTypes.NAMESPACE_ALIAS, namespace));
		}
		AST aliasNode = ASTUtil.create(astFactory, OntoQLTokenTypes.ALIAS, alias);
		res.addChild(aliasNode);

		return res;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}
}
