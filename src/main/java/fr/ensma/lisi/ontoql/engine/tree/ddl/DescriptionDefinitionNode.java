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
import java.util.List;

import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.Attribute;
import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.Description;
import fr.ensma.lisi.ontoql.core.Entity;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCollection;
import fr.ensma.lisi.ontoql.core.EntityDatatypeEnumerate;
import fr.ensma.lisi.ontoql.core.EntityDatatypeString;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.core.ontodb.EntityDatatypeCategoryOntoDB;
import fr.ensma.lisi.ontoql.engine.antlr.OntoQLSQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.antlr.SQLTokenTypes;
import fr.ensma.lisi.ontoql.engine.tree.OntoQLSQLWalkerNode;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.ontomodel.OntoAttribute;
import fr.ensma.lisi.ontoql.ontomodel.OntoMultilingualAttribute;
import fr.ensma.lisi.ontoql.ontomodel.mapping.PlibAttribute;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;
import fr.ensma.lisi.ontoql.util.StringHelper;

/**
 * Defines an AST node representing an OntoQL descriptor clause.
 * 
 * @author Stéphane JEAN
 */
public class DescriptionDefinitionNode extends OntoQLSQLWalkerNode {

	private static final long serialVersionUID = 4801740046787262977L;

	/**
	 * Attribute defined by this node.
	 */
	private Description descriptionDefined = null;

	/**
	 * Get the attribute defined by this node.
	 * 
	 * @return the attribute defined by this node.
	 */
	public Attribute getAttributeDefined(Entity scopeAttribute) {
		if (descriptionDefined == null) {
			loadAttributeDefined(scopeAttribute);
		}
		return (Attribute) descriptionDefined;
	}

	/**
	 * Get the property defined by this node.
	 * 
	 * @return the property defined by this node.
	 */
	public EntityProperty getPropertyDefined(EntityClass scopeProperty) {
		if (descriptionDefined == null) {
			loadPropertyDefined(scopeProperty);
		}
		return (EntityProperty) descriptionDefined;
	}

	private void loadPropertyDefined(EntityClass scopeProperty) {
		AST idPropertyNode = getFirstChild();
		AST datatypePropertyNode = idPropertyNode.getNextSibling();
		AST descriptorNode = datatypePropertyNode.getNextSibling();

		String idProperty = idPropertyNode.getText();
		// We need a factory to create the datatype and the property
		FactoryEntity factory = getWalker().getFactoryEntity();
		EntityDatatype datatypeProperty = getDatatype(datatypePropertyNode, factory, scopeProperty);
		descriptionDefined = factory.createEntityProperty(idProperty);
		descriptionDefined.setRange(datatypeProperty);
		((EntityProperty) descriptionDefined).setScope(scopeProperty);
		// handle a description of this property
		if (descriptorNode != null) {
			DescriptorClause descriptorClause = (DescriptorClause) descriptorNode;
			Entity entity = factory.createEntity("#property");
			Class mappedClass = entity.getDelegateEntity().getInternalAPIClass();
			descriptorClause.setDescriptor(descriptionDefined, entity, mappedClass);
			((EntityProperty) descriptionDefined).setNonCoreAttributes(descriptorClause.getAttributesNonHandled());
			((EntityProperty) descriptionDefined)
					.setNonCoreAttributesValues(descriptorClause.getAttributesValuesNonHandled());
		}

	}

	/**
	 * Load the attribute defined by this node.
	 */
	private void loadAttributeDefined(Entity scopeAttribute) {

		AST nameAttributeNode = getFirstChild();
		AST datatypeAttributeNode = nameAttributeNode.getNextSibling();
		String nameAttribute = nameAttributeNode.getText().substring(1);
		// We need a factory to create the datatype and the property
		FactoryEntity factory = getWalker().getFactoryEntity();
		EntityDatatype datatypeAttribute = getDatatype(datatypeAttributeNode, factory, scopeAttribute);
		OntoAttribute aMapAttribute = null;
		// check if this attribute is multilingual
		if ((datatypeAttribute instanceof EntityDatatypeString)
				&& ((EntityDatatypeString) datatypeAttribute).isMultilingualType()) {
			aMapAttribute = new OntoMultilingualAttribute(nameAttribute, true, scopeAttribute.getDelegateEntity());
			PlibAttribute attributeTranslatedLabel = new PlibAttribute(nameAttribute);
			attributeTranslatedLabel.setOfEntity(scopeAttribute.getDelegateEntity().getMapTo());
			aMapAttribute.addLink(attributeTranslatedLabel);
		} else {
			aMapAttribute = new OntoAttribute(nameAttribute, scopeAttribute.getDelegateEntity());
			// if #oid is created, must be mapped to rid
			if (nameAttribute.equals("oid")) {
				aMapAttribute.setMapTo(new PlibAttribute("rid"));
			}
			// add a link if it is an association type
			if (datatypeAttribute.isAssociationType() || datatypeAttribute.isCollectionAssociationType()) {
				PlibAttribute attributeLink = new PlibAttribute(nameAttribute);
				attributeLink.setOfEntity(scopeAttribute.getDelegateEntity().getMapTo());
				aMapAttribute.addLink(attributeLink);
			}
		}
		// For the moment, we handle only optional attribute
		aMapAttribute.setOptional(true);
		descriptionDefined = new Attribute(aMapAttribute);
		descriptionDefined.setRange(datatypeAttribute);
	}

	/**
	 * Get the datatype of this node
	 */
	private EntityDatatype getDatatype(AST nodeDatatype, FactoryEntity factory, Category categoryDefined) {
		EntityDatatype res = null;

		AST firstChild = nodeDatatype.getFirstChild();
		if (nodeDatatype.getType() == SQLTokenTypes.REF) {
			res = factory.createEntityDatatype(EntityDatatype.ASSOCIATION_NAME);
			String nameRefType = firstChild.getText();
			Category categoryAssociation = null;
			boolean isCategoryDefined = false;
			if (categoryDefined.isEntity()) {
				isCategoryDefined = nameRefType.equals('#' + categoryDefined.getName());
			} else {
				String idOfCategory = null;
				if (getWalker().getSession().getReferenceLanguage() == OntoQLHelper.NO_LANGUAGE) {
					idOfCategory = ((EntityClass) categoryDefined).getExternalId();
				} else {
					idOfCategory = categoryDefined.getName();
				}
				isCategoryDefined = nameRefType.equals(idOfCategory);
			}
			if (isCategoryDefined) {
				// This an attribute which reference the entity defined
				categoryAssociation = categoryDefined;
			} else {
				categoryAssociation = factory.createCategory(nameRefType);
			}
			((EntityDatatypeCategoryOntoDB) res).setCategory(categoryAssociation);
		} else if (nodeDatatype.getType() == SQLTokenTypes.ARRAY_DEF) {
			res = factory.createEntityDatatype(EntityDatatype.COLLECTION_NAME);
			((EntityDatatypeCollection) res).setDatatype(getDatatype(firstChild, factory, categoryDefined));
		} else {
			String nameDatatype = nodeDatatype.getText();
			res = factory.createEntityDatatype(nameDatatype);
			// check if this datatype is multilingual
			if (firstChild != null) {
				if (firstChild.getType() == OntoQLSQLTokenTypes.MULTILINGUAL) {
					((EntityDatatypeString) res).setMultilingual(true);
				} else if (firstChild.getType() == OntoQLSQLTokenTypes.IN_LIST) {
					List<String> enumValues = new ArrayList<String>();
					AST firstValueNode = firstChild.getFirstChild();

					enumValues.add(StringHelper.removeFirstAndLastletter(firstValueNode.getText()));
					while (firstValueNode.getNextSibling() != null) {
						firstValueNode = firstValueNode.getNextSibling();
						enumValues.add(StringHelper.removeFirstAndLastletter(firstValueNode.getText()));
					}
					((EntityDatatypeEnumerate) res).setValues(enumValues);
				} else {
					throw new JOBDBCException("Not Yet Implemented.");
				}
			}
		}

		return res;
	}
}
