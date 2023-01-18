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
package fr.ensma.lisi.ontoql.cfg;

import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.hibernate.MappingException;

import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.Entity;
import fr.ensma.lisi.ontoql.core.ontodb.EntityClassRootOntoDB;
import fr.ensma.lisi.ontoql.core.ontodb.EntityDatatypeCategoryOntoDB;
import fr.ensma.lisi.ontoql.core.ontodb.EntityDatatypeCollectionOntoDB;
import fr.ensma.lisi.ontoql.core.ontodb.EntityDatatypeIntOntoDB;
import fr.ensma.lisi.ontoql.core.ontodb.EntityDatatypeStringOntoDB;
import fr.ensma.lisi.ontoql.ontomodel.OntoAttribute;
import fr.ensma.lisi.ontoql.ontomodel.OntoEntity;
import fr.ensma.lisi.ontoql.ontomodel.OntoMultilingualAttribute;
import fr.ensma.lisi.ontoql.ontomodel.mapping.PlibAttribute;
import fr.ensma.lisi.ontoql.ontomodel.mapping.PlibEntity;
import fr.ensma.lisi.ontoql.util.OntoDBHelper;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;
import fr.ensma.lisi.ontoql.util.ReflectHelper;
import fr.ensma.lisi.ontoql.util.StringHelper;

/**
 * Walks an XML mapping document and produces the Hibernate configuration-time
 * metamodel (the classes in the <tt>mapping</tt> package)
 * 
 * @author Stephane JEAN
 */
public final class XMLBinder {

	private static final Log log = LogFactory.getLog(XMLBinder.class);

	private static String NAME_ELEMENT_ATTRIBUTE_PRIMITIVE = "attributePrimitive";

	private static String NAME_ELEMENT_ATTRIBUTE_REF = "attributeRef";

	private static String NAME_ELEMENT_ATTRIBUTE_COLLECTION = "attributeCollection";

	public XMLBinder() {
	}

	/**
	 * The main contract into the xml binder. Performs necessary binding operations
	 * represented by the given DOM.
	 * 
	 * @param doc           The DOM to be parsed and bound.
	 * @param ontologyModel Current bind state.
	 * @throws MappingException
	 */
	public static void bindRoot(Document doc, OntologyModel ontologyModel) throws MappingException {

		Element rootNode = doc.getRootElement();

		extractRootAttributes(rootNode, ontologyModel);

		Iterator nodesIterator = rootNode.elementIterator("entity");
		Vector<Element> nodes = new Vector<Element>(5);
		Vector<OntoEntity> entities = new Vector<OntoEntity>(5);
		while (nodesIterator.hasNext()) {
			Element n = (Element) nodesIterator.next();
			String nameEntity = n.attributeValue("name");
			OntoEntity entity = new OntoEntity(nameEntity);

			Attribute nameFrEntityAttribute = n.attribute("nameFR");
			if (nameFrEntityAttribute != null) {
				entity.setName(nameFrEntityAttribute.getValue(), OntoQLHelper.FRENCH);
			}
			String nameEntityWithUpperCase = StringHelper.firstLetterInUpperCase(nameEntity);
			String nameOntoAPIClass = OntoQLHelper.ONTOAPI_PACKAGE + ".Onto" + nameEntityWithUpperCase;
			String nameInternalAPIClass = OntoQLHelper.CORE_PACKAGE + ".Entity" + nameEntityWithUpperCase;

			try {
				entity.setOntoAPIClass(ReflectHelper.classForName(nameOntoAPIClass));
				entity.setInternalAPIClass(ReflectHelper.classForName(nameInternalAPIClass));
			} catch (ClassNotFoundException cnfe) {
				log.debug("Could not find class: " + nameOntoAPIClass + " or " + nameInternalAPIClass);
			}
			ontologyModel.addEntity(entity);
			nodes.add(n);
			entities.add(entity);
		}
		for (int i = 0; i < nodes.size(); i++) {
			bindEntity((Element) nodes.get(i), (OntoEntity) entities.get(i), ontologyModel);
		}
	}

	public static void extractRootAttributes(Element hmNode, OntologyModel ontologyModel) {
		Attribute packNode = hmNode.attribute("package");
		if (packNode != null)
			ontologyModel.setDefaultPackage(packNode.getValue());
	}

	private static void bindEntity(Element node, OntoEntity entity, OntologyModel ontologyModel)
			throws MappingException {

		Attribute nameSuperEntityAttribute = node.attribute("superEntity");
		if (nameSuperEntityAttribute != null) {
			entity.setSuperMapEntity(ontologyModel.getEntity(nameSuperEntityAttribute.getValue()));
		}

		Attribute nameEntityPLIBAttribute = node.attribute("entityPLIB");
		String nameEntityPLIB = nameEntityPLIBAttribute == null ? entity.getName() : nameEntityPLIBAttribute.getValue();
		entity.getMapTo().setName(nameEntityPLIB);

		Iterator links = node.elementIterator("link");
		while (links.hasNext()) {
			final Element linkElem = (Element) links.next();
			bindLink(linkElem, entity, ontologyModel);
		}

		Iterator attributes = node.elementIterator(NAME_ELEMENT_ATTRIBUTE_PRIMITIVE);
		while (attributes.hasNext()) {
			final Element attributeElem = (Element) attributes.next();
			bindAttribute(attributeElem, entity, ontologyModel);
		}

		Iterator associations = node.elementIterator(NAME_ELEMENT_ATTRIBUTE_REF);
		while (associations.hasNext()) {
			final Element associationElem = (Element) associations.next();
			bindAssociation(associationElem, entity, ontologyModel);
		}

		Iterator collections = node.elementIterator(NAME_ELEMENT_ATTRIBUTE_COLLECTION);
		while (collections.hasNext()) {
			final Element collectionElem = (Element) collections.next();
			bindCollection(collectionElem, entity, ontologyModel);
		}
	}

	public static void bindLink(Element linkElem, OntoEntity entity, OntologyModel ontologyModel) {

		Attribute attributeWithEntityPLIB = linkElem.attribute("withEntityPLIB");
		PlibEntity plibEntity = new PlibEntity(attributeWithEntityPLIB.getValue());

		Attribute attributeAttributePLIB = linkElem.attribute("attributePLIB");
		PlibAttribute plibAttribut = new PlibAttribute(attributeAttributePLIB.getValue());

		Attribute attributeEntityDefPLIB = linkElem.attribute("entityDefPLIB");
		if (attributeEntityDefPLIB != null) {
			PlibEntity entityDef = new PlibEntity(attributeEntityDefPLIB.getValue());
			plibAttribut.setOfEntity(entityDef);
		} else {
			plibAttribut.setOfEntity(entity.getMapTo());
		}

		Link linkToAdd = new Link(plibEntity, plibAttribut);

		Attribute attributeEntityPLIB = linkElem.attribute("entityPLIB");
		if (attributeEntityPLIB != null) {
			// We must add the link
			Link linkWithAnotherEntity = searchLink(entity, attributeEntityPLIB.getValue(), ontologyModel);
			linkToAdd.setRequiredLink(linkWithAnotherEntity);
			PlibEntity entityDef = new PlibEntity(attributeEntityPLIB.getValue());
			plibAttribut.setOfEntity(entityDef);
		}

		ontologyModel.addLink(entity, linkToAdd);
	}

	public static void bindAttribute(Element attributeElem, OntoEntity entity, OntologyModel ontologyModel) {

		Attribute nameAttribute = attributeElem.attribute("name");
		String name = nameAttribute.getValue();
		Attribute attributeAttributePLIB = attributeElem.attribute("attributePLIB");
		String nameAttributePLIB = null;
		if (attributeAttributePLIB != null) {
			nameAttributePLIB = attributeAttributePLIB.getValue();
		} else {
			nameAttributePLIB = name;
		}

		OntoAttribute attribute = null;
		Attribute multilingualAttribute = attributeElem.attribute("multilingual");
		if (multilingualAttribute != null && multilingualAttribute.getText().equals("true")) {
			boolean label = false;
			Attribute labelAttribute = attributeElem.attribute("label");
			if (labelAttribute != null && labelAttribute.getValue().equals("true")) {
				label = true;
			}
			attribute = new OntoMultilingualAttribute(name, label, entity);
		} else {
			attribute = new OntoAttribute(name, entity);
		}

		Attribute nameFrAttribute = attributeElem.attribute("nameFR");
		if (nameFrAttribute != null) {
			attribute.setName(nameFrAttribute.getValue(), OntoQLHelper.FRENCH);
		}

		Attribute attributeOptional = attributeElem.attribute("optional");
		if (attributeOptional != null && attributeOptional.getValue().equals("true")) {
			attribute.setOptional(true);
		}

		Attribute attributeType = attributeElem.attribute("type");
		if (attributeType.getValue().equalsIgnoreCase("String")) {
			attribute.setRange(new EntityDatatypeStringOntoDB(null));
		} else if (attributeType.getValue().equalsIgnoreCase("Int")) {
			attribute.setRange(new EntityDatatypeIntOntoDB(null));
		}

		attribute.getMapTo().setName(nameAttributePLIB);

		Attribute attributeEntityPLIB = attributeElem.attribute("entityPLIB");

		// keep the last plib entity linked to this attribute
		PlibEntity lastEntityLink = null;

		Attribute attributeEntityDefPLIB = attributeElem.attribute("entityDefPLIB");
		if (attributeEntityDefPLIB != null) {
			PlibEntity entityDef = new PlibEntity(attributeEntityDefPLIB.getValue());
			lastEntityLink = entityDef;
		} else {
			lastEntityLink = entity.getMapTo();
		}

		if (attributeEntityPLIB != null) {
			// We must add the link
			Link linkWithAnotherEntity = addLink(attribute, entity, attributeEntityPLIB.getValue(), ontologyModel);
			lastEntityLink = linkWithAnotherEntity.getEntityPlib();
		}

		if (attribute instanceof OntoMultilingualAttribute) {
			// the plib attribute references a translated label
			PlibAttribute attributeTranslatedLabel = new PlibAttribute(nameAttributePLIB);
			attributeTranslatedLabel.setOfEntity(lastEntityLink);
			attribute.addLink(attributeTranslatedLabel);
		}

	}

	/**
	 * Add a link to an attribute.
	 * 
	 * @param attribute         the attribute.
	 * @param entity            The entity of this attribute
	 * @param nameOfOtherEntity Name of the entity to link
	 * @param ontologyModel     the ontology model built
	 * @return the link built
	 */
	private static Link addLink(OntoAttribute attribute, OntoEntity entity, String nameOfOtherEntity,
			OntologyModel ontologyModel) {

		Link linkWithAnotherEntity = searchLink(entity, nameOfOtherEntity, ontologyModel);

		Link requiredLink = linkWithAnotherEntity.getRequiredLink();
		if (requiredLink != null) {
			attribute.addLink(requiredLink.getAttributePLIB());
		}
		attribute.getMapTo().setOfEntity(linkWithAnotherEntity.getEntityPlib());
		attribute.addLink(linkWithAnotherEntity.getAttributePLIB());

		return linkWithAnotherEntity;
	}

	/**
	 * Search a link between 2 entities in the current mapping.
	 * 
	 * @param entity            first entity (FROM)
	 * @param nameOfOtherEntity second entity (TO)
	 * @param ontologyModel     current mapping
	 * @return the link found or raise a mapping exception
	 */
	private static Link searchLink(OntoEntity entity, String nameOfOtherEntity, OntologyModel ontologyModel) {
		Link res = ontologyModel.getLink(entity, nameOfOtherEntity);
		if (res == null) {
			throw new MappingException("The link between the entity " + entity.getName() + " and the entity "
					+ nameOfOtherEntity + " must be defined.");
		}
		return res;
	}

	public static void bindAssociation(Element associationElem, OntoEntity entity, OntologyModel ontologyModel) {

		Attribute nameAssociation = associationElem.attribute("name");
		String name = nameAssociation.getValue();

		OntoAttribute attribute = new OntoAttribute(name, entity);

		Attribute nameFrAttribute = associationElem.attribute("nameFR");
		if (nameFrAttribute != null) {
			attribute.setName(nameFrAttribute.getValue(), OntoQLHelper.FRENCH);
		}

		Attribute attributeOptional = associationElem.attribute("optional");
		if (attributeOptional != null && attributeOptional.getValue().equals("true")) {
			attribute.setOptional(true);
		}

		Attribute attributeEntity = associationElem.attribute("entity");
		String nameEntityAssociated = attributeEntity.getValue();
		Category entityAssociated = null;
		OntoEntity refEntity = ontologyModel.getEntity(nameEntityAssociated);
		if (refEntity == null) {
			// this must be a class
			entityAssociated = new EntityClassRootOntoDB(null);
		} else {
			entityAssociated = new Entity(nameEntityAssociated);
			((Entity) entityAssociated).setDelegateEntity(refEntity);
		}

		// The return of this association is the oid of its codomain
		// the plib attribute corresponding to this oid can be searched
		// However it's more efficient to hardcode that only, class, property
		// and ontology used the rid_bsu instead of the rid
		attribute.getMapTo().setName(OntoDBHelper.getPLIBAttributeForOid(entityAssociated.getName()));

		attribute.setRange(new EntityDatatypeCategoryOntoDB(entityAssociated));
		attribute.getMapTo().setOfEntity(entity.getMapTo());
		Attribute attributeLink = associationElem.attribute("attributePLIB");
		String nameAttributePLIB = null;
		if (attributeLink != null) {
			nameAttributePLIB = attributeLink.getValue();
		} else {
			nameAttributePLIB = name;
		}
		PlibAttribute linkAttribute = new PlibAttribute(nameAttributePLIB);
		PlibEntity lastEntityLink = entity.getMapTo();

		Attribute attributeEntityPLIB = associationElem.attribute("entityPLIB");
		Attribute attributeEntityDefPLIB = associationElem.attribute("entityDefPLIB");
		if (attributeEntityDefPLIB != null) {
			PlibEntity entityDef = new PlibEntity(attributeEntityDefPLIB.getValue());
			lastEntityLink = entityDef;
		} else {
			lastEntityLink = entity.getMapTo();
		}

		if (attributeEntityPLIB != null) {
			// We must add the link
			Link linkWithAnotherEntity = addLink(attribute, entity, attributeEntityPLIB.getValue(), ontologyModel);
			lastEntityLink = linkWithAnotherEntity.getEntityPlib();
		}

		linkAttribute.setOfEntity(lastEntityLink);
		attribute.addLink(linkAttribute);

	}

	public static void bindCollection(Element collectionElem, OntoEntity entity, OntologyModel ontologyModel) {

		Attribute nameCollection = collectionElem.attribute("name");
		String name = nameCollection.getValue();
		OntoAttribute attribute = new OntoAttribute(name, entity);

		Attribute nameFrAttribute = collectionElem.attribute("nameFR");
		if (nameFrAttribute != null) {
			attribute.setName(nameFrAttribute.getValue(), OntoQLHelper.FRENCH);
		}

		Attribute attributeType = collectionElem.attribute("type");
		if (attributeType.getValue().equals("ref")) {
			Attribute attributeEntity = collectionElem.attribute("entity");
			String nameEntityAssociated = attributeEntity.getValue();
			Entity entityAssociated = new Entity(nameEntityAssociated);
			entityAssociated.setDelegateEntity((OntoEntity) ontologyModel.getEntity(nameEntityAssociated));
			attribute.setRange(new EntityDatatypeCollectionOntoDB(new EntityDatatypeCategoryOntoDB(entityAssociated)));
		}

		Attribute attributePlib = collectionElem.attribute("attributePLIB");
		Attribute attributeFunction = collectionElem.attribute("function");
		String nameAttributePLIB = null;
		if (attributeFunction == null) {
			if (attributePlib != null) {
				nameAttributePLIB = attributePlib.getValue();
			} else {
				nameAttributePLIB = name;
			}
			attribute.getMapTo().setName(nameAttributePLIB);
			PlibAttribute linkAttribute = new PlibAttribute(nameAttributePLIB);
			PlibEntity lastEntityLink = entity.getMapTo();
			linkAttribute.setOfEntity(lastEntityLink);
			attribute.addLink(linkAttribute);
		} else { // this is a derived collection
			attribute.setFunctionName(attributeFunction.getValue());
			attribute.setFunctionParameter(OntoDBHelper.getPLIBAttributeForOid(entity.getName()));
		}

		Attribute attributeEntityPLIB = collectionElem.attribute("entityPLIB");
		if (attributeEntityPLIB != null) {
			// We must add the link
			addLink(attribute, entity, attributeEntityPLIB.getValue(), ontologyModel);
		}
	}
}
