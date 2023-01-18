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
package fr.ensma.lisi.ontoql.engine;

import java.lang.reflect.Constructor;

import antlr.ASTFactory;
import antlr.Token;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLSQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.AggregateNode;
import fr.ensma.lisi.ontoql.engine.tree.ArithmeticNode;
import fr.ensma.lisi.ontoql.engine.tree.ArrayNode;
import fr.ensma.lisi.ontoql.engine.tree.Case2Node;
import fr.ensma.lisi.ontoql.engine.tree.CaseNode;
import fr.ensma.lisi.ontoql.engine.tree.DotNode;
import fr.ensma.lisi.ontoql.engine.tree.IdentNode;
import fr.ensma.lisi.ontoql.engine.tree.InitializeableNode;
import fr.ensma.lisi.ontoql.engine.tree.LiteralNode;
import fr.ensma.lisi.ontoql.engine.tree.MethodNode;
import fr.ensma.lisi.ontoql.engine.tree.SQLNode;
import fr.ensma.lisi.ontoql.engine.tree.StarNode;
import fr.ensma.lisi.ontoql.engine.tree.TypedClause;
import fr.ensma.lisi.ontoql.engine.tree.UnaryNode;
import fr.ensma.lisi.ontoql.engine.tree.ddl.AlterStatement;
import fr.ensma.lisi.ontoql.engine.tree.ddl.CreateStatement;
import fr.ensma.lisi.ontoql.engine.tree.ddl.DescriptionDefinitionNode;
import fr.ensma.lisi.ontoql.engine.tree.ddl.DescriptionsDefintionClause;
import fr.ensma.lisi.ontoql.engine.tree.ddl.DescriptorClause;
import fr.ensma.lisi.ontoql.engine.tree.ddl.DropStatement;
import fr.ensma.lisi.ontoql.engine.tree.dml.InsertStatement;
import fr.ensma.lisi.ontoql.engine.tree.dml.IntoClause;
import fr.ensma.lisi.ontoql.engine.tree.dml.ValuesClause;
import fr.ensma.lisi.ontoql.engine.tree.dql.FromClause;
import fr.ensma.lisi.ontoql.engine.tree.dql.SelectClause;
import fr.ensma.lisi.ontoql.engine.tree.dql.SelectStatement;

/**
 * Custom AST factory the intermediate tree that causes ANTLR to create
 * specialized AST nodes, given the AST node type (from OntoqlSqlTokenTypes).
 * OntoQLSQLWalker registers this factory with itself when it is initialized.
 * 
 * @author Stéphane JEAN
 */
public class SQLASTFactory extends ASTFactory implements OntoQLSQLTokenTypes {

	/**
	 * Reference to the walker of the logical tree.
	 */
	private OntoQLSQLWalker walker;

	/**
	 * Create factory with a specific mapping from token type to Java AST node type.
	 * Your subclasses of ASTFactory can override and reuse the map stuff.
	 * 
	 * @param walker the walker of the logical tree
	 */
	public SQLASTFactory(final OntoQLSQLWalker walker) {
		super();
		this.walker = walker;
	}

	@Override
	public final Class getASTNodeType(final int tokenType) {
		switch (tokenType) {
		case SELECT:
			return SelectStatement.class;
		case SELECT_CLAUSE:
			return SelectClause.class;
		case INSERT:
			return InsertStatement.class;
		case INTO:
			return IntoClause.class;
		case VALUES:
			return ValuesClause.class;
		case CREATE:
			return CreateStatement.class;
		case ALTER:
			return AlterStatement.class;
		case DROP:
			return DropStatement.class;
		case DESCRIPTOR:
			return DescriptorClause.class;
		case PROPERTIES:
		case ATTRIBUTES:
			return DescriptionsDefintionClause.class;
		case PROPERTY_DEF:
		case ATTRIBUTE_DEF:
			return DescriptionDefinitionNode.class;
		case FROM:
			return FromClause.class;
		case DOT:
			return DotNode.class;
		// Alias references and identifiers use the same node class.
		case IDENT:
			return IdentNode.class;
		case METHOD_CALL:
			return MethodNode.class;
		case COUNT:
		case AGGREGATE:
			return AggregateNode.class;
		case NUM_INT:
		case NUM_FLOAT:
		case NUM_LONG:
		case NUM_DOUBLE:
		case QUOTED_STRING:
		case TRUE:
		case FALSE:
		case NULL:
			return LiteralNode.class;
		case ARRAY:
			return ArrayNode.class;
		case ROW_STAR:
			return StarNode.class;
		case OF:
			return TypedClause.class;
		case PLUS:
		case MINUS:
		case STAR:
		case DIV:
			return ArithmeticNode.class;
		case UNARY_MINUS:
		case UNARY_PLUS:
			return UnaryNode.class;
		case CASE2:
			return Case2Node.class;
		case CASE:
			return CaseNode.class;
		default:
			return SQLNode.class;
		} // switch
	}

	@Override
	protected final AST createUsingCtor(final Token token, final String className) {
		Class c;
		AST t;
		try {
			c = Class.forName(className);
			Class[] tokenArgType = new Class[] { antlr.Token.class };
			Constructor ctor = c.getConstructor(tokenArgType);
			if (ctor != null) {
				t = (AST) ctor.newInstance(new Object[] { token }); // make a
				// new one
				initializeSqlNode(t);
			} else {
				// just do the regular thing if you can't find the ctor
				// Your AST must have default ctor to use this.
				t = create(c);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid class or can't make instance, " + className);
		}
		return t;
	}

	/**
	 * Initialize a node.
	 * 
	 * @param t the node to initialize
	 */
	private void initializeSqlNode(final AST t) {
		// Initialize SQL nodes here.
		if (t instanceof InitializeableNode) {
			InitializeableNode initializeableNode = (InitializeableNode) t;
			initializeableNode.initialize(walker);
		}
	}

	/**
	 * Actually instantiate the AST node.
	 * 
	 * @param c The class to instantiate.
	 * @return The instantiated and initialized node.
	 */
	protected final AST create(final Class c) {
		AST t;
		try {
			t = (AST) c.newInstance(); // make a new one
			initializeSqlNode(t);
		} catch (Exception e) {
			error("Can't create AST Node " + c.getName());
			return null;
		}
		return t;
	}
}
