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

import antlr.SemanticException;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.AbstractEntityClass;
import fr.ensma.lisi.ontoql.core.Attribute;
import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.Entity;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLSQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.OntoQLSQLWalkerNode;
import fr.ensma.lisi.ontoql.engine.tree.Statement;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;

/**
 * Defines a top-level AST node representing an OntoQL alter statement.
 * 
 * @author Stéphane JEAN
 */
public class AlterStatement extends OntoQLSQLWalkerNode implements Statement {

	private static final long serialVersionUID = -2703742320226915399L;

	/**
	 * The category altered by this AlterStatement.
	 */
	private Category category;

	@Override
	public int getStatementType() {
		return OntoQLSQLTokenTypes.CREATE;
	}

	/**
	 * Altered the given entity.
	 * 
	 * @throws JOBDBCException if a semantic exception is detected.
	 */
	public void process() throws SemanticException {

		AST entityTypeNode = getFirstChild();
		FactoryEntity factory = getWalker().getFactoryEntity();
		if (entityTypeNode.getType() == SQLTokenTypes.ENTITY) {
			processAlterEntity(entityTypeNode.getFirstChild(), factory);
		} else if (entityTypeNode.getType() == SQLTokenTypes.EXTENT) {
			processAlterExtent(entityTypeNode.getFirstChild(), factory);
		} else {
			processAlterClass(entityTypeNode.getFirstChild(), factory);
		}
	}

	public void processAlterExtent(AST classNode, FactoryEntity factory) throws SemanticException {
		AbstractEntityClass aClass = (AbstractEntityClass) factory.createCategory(classNode.getText());
		AST actionNode = classNode.getNextSibling();
		// add a property
		EntityProperty propertyAdded = factory.createEntityProperty(actionNode.getNextSibling().getText());
		aClass.addColumnToTable(propertyAdded);
	}

	private void processAlterEntity(AST nodeNameEntity, FactoryEntity factory) {
		category = factory.createEntity(nodeNameEntity.getText());
		AST actionNode = nodeNameEntity.getNextSibling();
		if (actionNode.getType() == OntoQLSQLTokenTypes.DROP) {
			String attributeName = actionNode.getNextSibling().getText();
			Attribute attributeDropped = (Attribute) ((Entity) category).getDefinedDescription(attributeName);
			if (attributeDropped == null) {
				throw new JOBDBCException("The attribute " + attributeName + " is not defined on the entity #"
						+ ((Entity) category).getName());
			} else {
				((Entity) category).dropAttribute(attributeDropped, getWalker().getSession());
			}
		} else {
			// add an attribute
			Attribute attributeAdded = ((DescriptionDefinitionNode) actionNode.getNextSibling())
					.getAttributeDefined((Entity) category);
			((Entity) category).createAttribute(attributeAdded, getWalker().getSession());
		}

	}

	private void processAlterClass(AST nodeNameEntity, FactoryEntity factory) {
		category = factory.createCategory(nodeNameEntity.getText());
		AST actionNode = nodeNameEntity.getNextSibling();
		if (actionNode.getType() == OntoQLSQLTokenTypes.DROP) {
			String propertyName = actionNode.getNextSibling().getText();
			EntityProperty propertyDropped = (EntityProperty) ((EntityClass) category)
					.getDefinedDescription(propertyName, getWalker().getSession().getReferenceLanguage());
			if (propertyName == null) {
				throw new JOBDBCException(
						"The property " + propertyName + " is not defined on the class " + category.getName());
			} else {
				((EntityClass) category).dropProperty(propertyDropped, getWalker().getSession());
			}
		} else {
			// add a property
			EntityProperty propertyAdded = ((DescriptionDefinitionNode) actionNode.getNextSibling())
					.getPropertyDefined((EntityClass) category);
			((EntityClass) category).createProperty(propertyAdded, getWalker().getSession());
		}
	}
}
