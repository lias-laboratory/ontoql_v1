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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import antlr.RecognitionException;
import antlr.SemanticException;
import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.AbstractEntityClass;
import fr.ensma.lisi.ontoql.core.AbstractEntityProperty;
import fr.ensma.lisi.ontoql.core.Entity;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.engine.SQLGenerator;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLSQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.IdentNode;
import fr.ensma.lisi.ontoql.engine.tree.OntoQLSQLWalkerNode;
import fr.ensma.lisi.ontoql.engine.tree.TypedClause;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;

/**
 * Defines a top-level AST node representing an OntoQL create statement.
 * 
 * @author Stéphane JEAN
 */
public class CreateStatement extends OntoQLSQLWalkerNode implements fr.ensma.lisi.ontoql.engine.tree.Statement {

	private static final long serialVersionUID = 2310854464722147865L;

	/**
	 * A logger for this class.
	 */
	private static Log log = LogFactory.getLog(CreateStatement.class);

	/**
	 * The entity created by this CreateStatement (not meaningfull for extent).
	 */
	private Entity entity;

	/**
	 * An instance of the entity (not meaningfull for extent).
	 */
	private Object instance;

	/**
	 * Process the creation of the extent of the given class.
	 * 
	 * @param classNode node containing the identifier the class
	 * @param factory   factory to create class and entity
	 * @throws JOBDBCException if a semantic exception is detected.
	 */
	public void processCreateExtent(AST classNode, FactoryEntity factory) throws SemanticException {
		AbstractEntityClass aClass = (AbstractEntityClass) factory.createCategory(classNode.getText());
		AbstractEntityProperty[] propertiesArray = null;
		List<EntityProperty> propertiesList = new ArrayList<EntityProperty>();
		AST currentPropertyNode = classNode.getNextSibling();
		EntityProperty currentProperty = null;
		while (currentPropertyNode != null) {
			currentProperty = (EntityProperty) ((IdentNode) currentPropertyNode).loadDescription(aClass);
			propertiesList.add(currentProperty);
			currentPropertyNode = currentPropertyNode.getNextSibling();
		}

		propertiesArray = new AbstractEntityProperty[propertiesList.size()];
		for (int i = 0; i < propertiesList.size(); i++) {
			propertiesArray[i] = (AbstractEntityProperty) propertiesList.get(i);
		}
		aClass.createTable(propertiesArray);
	}

	/**
	 * Process the creation of a given entity.
	 * 
	 * @param entityNode node containing the name of the new entity
	 * @throws JOBDBCException if a semantic exception is detected.
	 */
	public void processCreateEntity(AST entityNode) throws JOBDBCException {
		Entity entity = new Entity(entityNode.getText().substring(1));

		AST nextNode = entityNode.getNextSibling();
		if (nextNode.getType() == OntoQLTokenTypes.UNDER) {
			Entity superEntity = getEntityFactory().createEntity(nextNode.getFirstChild().getText());
			entity.setSuperEntity(superEntity);
			nextNode = nextNode.getNextSibling();
		}

		if (nextNode != null) {
			// The entity has some attributes definition
			DescriptionsDefintionClause attributesNode = (DescriptionsDefintionClause) nextNode;
			if (attributesNode != null) {
				// Get the list of attributes
				attributesNode.setDescriptionScope(entity);
				attributesNode.collectAttributes();
			}
		}
		entity.create(getWalker().getSession());
	}

	/**
	 * Process the creation of the given entity.
	 * 
	 * @param entityTypeNode node containing the type of entity created
	 * @param factory        factory to create class and entity
	 * @throws JOBDBCException if a semantic exception is detected.
	 */
	public void processCreateClass(AST entityTypeNode, FactoryEntity factory) throws JOBDBCException {

		try {
			AST instanceEntityIdentifierNode = entityTypeNode.getFirstChild();
			entity = factory.createEntity(entityTypeNode.getText());
			// Later must be dynamic (we can use the reflect package)
			if (entity.getName().equalsIgnoreCase("class")) {
				instance = factory.createEntityClass(instanceEntityIdentifierNode.getText());
			} else if (entity.getName().equalsIgnoreCase("property")) {
				instance = factory.createEntityProperty(instanceEntityIdentifierNode.getText());
			}
			AST optionalClauseNode = instanceEntityIdentifierNode.getNextSibling();
			if (optionalClauseNode != null) {
				handleOptionalClause(optionalClauseNode);
			}
			Class mappedClass = entity.getDelegateEntity().getInternalAPIClass();
			// TODO refactor by decomposing into different node
			AST descriptor = getFirstChild().getNextSibling();
			DescriptionsDefintionClause propertiesNode = null;
			DescriptorClause descriptorClause = null;

			if (descriptor != null) {
				if (descriptor.getType() == SQLTokenTypes.DESCRIPTOR) {
					descriptorClause = (DescriptorClause) descriptor;
					descriptorClause.setDescriptor(instance, entity, mappedClass);
					((EntityClass) instance).setNonCoreAttributes(descriptorClause.getAttributesNonHandled());
					((EntityClass) instance)
							.setNonCoreAttributesValues(descriptorClause.getAttributesValuesNonHandled());
					// Process Properties node
					propertiesNode = (DescriptionsDefintionClause) descriptor.getNextSibling();
				} else {
					propertiesNode = (DescriptionsDefintionClause) descriptor;
				}
			}
			if (propertiesNode != null) {
				// Get the list of properties
				propertiesNode.setDescriptionScope((AbstractEntityClass) instance);
				EntityProperty[] propertyList = propertiesNode.collectProperties();
				((EntityClass) instance).setScopeProperties(propertyList);
			}

			Method insertMethode = mappedClass.getMethod("insert", new Class[] {});
			insertMethode.invoke(instance, new Object[] {});

		} catch (NoSuchMethodException e) {
			throw new JOBDBCException(e);
		} catch (IllegalAccessException e) {
			throw new JOBDBCException(e);
		} catch (InvocationTargetException e) {
			throw new JOBDBCException(e.getCause());
		}
	}

	/**
	 * Process the creation of the given entity or extent.
	 * 
	 * @throws JOBDBCException if a semantic exception is detected.
	 */
	public void process() throws SemanticException {

		AST entityTypeNode = getFirstChild();
		FactoryEntity factory = getWalker().getFactoryEntity();
		if (entityTypeNode.getType() == SQLTokenTypes.EXTENT) {
			processCreateExtent(entityTypeNode.getFirstChild(), factory);
		} else if (entityTypeNode.getType() == SQLTokenTypes.VIEW) {
			processCreateView(entityTypeNode.getFirstChild(), factory);
		} else if (entityTypeNode.getType() == SQLTokenTypes.ENTITY) {
			processCreateEntity(entityTypeNode.getFirstChild());
		} else {
			processCreateOntologyElement(entityTypeNode, factory);
		}
	}

	/**
	 * Process the creation of a given ontology element for the moment a class or an
	 * a posteriori case of
	 */
	public void processCreateOntologyElement(AST entityTypeNode, FactoryEntity factory) throws JOBDBCException {
		String entityTypeNodeText = entityTypeNode.getText();
		// For the moment hard coded
		if (entityTypeNodeText.equals("#AposterioriCaseof")) {
			processCreateAposterioriCaseof(entityTypeNode, factory);
		} else {
			processCreateClass(entityTypeNode, factory);
		}
	}

	/**
	 * Process the creation of an a posteriori case of
	 */
	public void processCreateAposterioriCaseof(AST entityTypeNode, FactoryEntity factory) throws JOBDBCException {

	}

	/**
	 * Process the creation of a view.
	 * 
	 * @param viewNode node containing the identifier the class
	 * @param factory  factory to create class and entity
	 * @throws JOBDBCException if a semantic exception is detected.
	 */
	public void processCreateView(AST viewNode, FactoryEntity factory) throws JOBDBCException {

		// Get the name of the view created
		String viewName = null;
		// Not null if an extent of a class is created
		EntityClass aClass = null;

		if (viewNode.getType() == SQLTokenTypes.OF) {
			// An extent of a class is created as a view
			aClass = ((TypedClause) viewNode).getOfClass();
			viewName = aClass.getNameExtent();
		} else {
			viewName = viewNode.getText();
		}

		// Execute the query
		try {
			SQLGenerator gen = new SQLGenerator(getSession());
			gen.statement(viewNode.getNextSibling());
			String sql = gen.getSQL();
			gen.getParseErrorHandler().throwQueryException();
			log.warn("generated SQL : " + sql);

			if (aClass != null) {
				List selectExpressions = getWalker().getExpressionInSelect();
				EntityProperty[] properties = new EntityProperty[selectExpressions.size()];
				IdentNode currentIdentNode = null;
				for (int i = 0; i < selectExpressions.size(); i++) {
					try {
						currentIdentNode = (IdentNode) selectExpressions.get(i);
						properties[i] = (EntityProperty) currentIdentNode.getDescription();
					} catch (ClassCastException oExc) {
						throw new JOBDBCException(
								"The query used to create this extent must only retrieve the value of existing properties");
					}
				}
				aClass.createView(properties, sql);
			}

		} catch (RecognitionException exc) {
			throw new JOBDBCException(exc);
		}
	}

	/**
	 * 
	 * @param optionalNode
	 */
	public void handleOptionalClause(AST optionalNode) throws JOBDBCException {
		try {
			if (optionalNode.getType() == SQLTokenTypes.UNDER) {
				String idSuperClass = optionalNode.getFirstChild().getText();
				EntityClass superClass = getWalker().getFactoryEntity().createEntityClass(idSuperClass); // TODO handle
				// multiple
				// inheritance
				((EntityClass) instance).setSuperClass(superClass);
			} else if (optionalNode.getType() == SQLTokenTypes.CONTEXT) {
				String idSuperClass = optionalNode.getFirstChild().getText();
				EntityClass contextClass = getWalker().getFactoryEntity().createEntityClass(idSuperClass);
				((EntityProperty) instance).setScope(contextClass);
			}
		} catch (ClassCastException e) {
			throw new JOBDBCException(
					"Clause " + optionalNode.getText() + " incompatible avec l'entité " + entity.getName());
		}
	}

	@Override
	public int getStatementType() {
		return OntoQLSQLTokenTypes.CREATE;
	}
}
