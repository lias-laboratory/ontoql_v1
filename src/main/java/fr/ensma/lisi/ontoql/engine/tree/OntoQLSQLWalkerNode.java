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

import antlr.ASTFactory;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.engine.OntoQLSQLWalker;
import fr.ensma.lisi.ontoql.engine.util.AliasGenerator;
import fr.ensma.lisi.ontoql.evaluator.DMLEvaluator;
import fr.ensma.lisi.ontoql.evaluator.OntoAlgebraEvaluator;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSessionFactory;

/**
 * A semantic analysis node, that points back to the main analyzer.
 * 
 * @author Stéphane JEAN
 */
public class OntoQLSQLWalkerNode extends SQLNode implements InitializeableNode {

	private static final long serialVersionUID = -6531848081738956671L;

	/**
	 * A pointer back to the phase 2 processor.
	 */
	private OntoQLSQLWalker walker;

	/**
	 * @param param object of this node
	 * @see InitializeableNode#initialize(Object)
	 */
	public void initialize(final Object param) {
		walker = (OntoQLSQLWalker) param;
	}

	/**
	 * @return the phase 2 processor
	 */
	public final OntoQLSQLWalker getWalker() {
		return walker;
	}

	/**
	 * @return the factory of node
	 */
	public final ASTFactory getASTFactory() {
		return walker.getASTFactory();
	}

	/**
	 * @return reference to the session factory
	 */
	public final OntoQLSessionFactory getSessionFactory() {
		return walker.getSessionFactory();
	}

	/**
	 * @return reference to the factory of concept
	 */
	public final FactoryEntity getEntityFactory() {
		return walker.getFactoryEntity();
	}

	/**
	 * @return the default namespace used
	 */
	public final String getDefaultNamespace() {
		return walker.getDefaultNamespace();
	}

	/**
	 * @return the namespace corresponding to a given alias
	 */
	public final String getNamespace(String alias) {
		return walker.getNamespace(alias);
	}

	/**
	 * @return Returns the session.
	 */
	public final OntoQLSession getSession() {
		return walker.getSession();
	}

	/**
	 * @return the evaluator of OntoAlgebra
	 */
	public final DMLEvaluator getDMLEvaluator() {
		return walker.getDMLEvaluator();
	}

	/**
	 * @return the evaluator of OntoAlgebra
	 */
	public final OntoAlgebraEvaluator getOntoAlgebraEvaluator() {
		return walker.getOntoAlgebraEvaluator();
	}

	/**
	 * @return the generator of alias
	 */
	public final AliasGenerator getAliasGenerator() {
		return walker.getAliasGenerator();
	}
}
