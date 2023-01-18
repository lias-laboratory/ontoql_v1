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
package fr.ensma.lisi.ontoql.evaluator.ontodb;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import antlr.SemanticException;
import fr.ensma.lisi.ontoql.core.Attribute;
import fr.ensma.lisi.ontoql.core.Category;
import fr.ensma.lisi.ontoql.core.Description;
import fr.ensma.lisi.ontoql.core.Entity;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCategory;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCollection;
import fr.ensma.lisi.ontoql.core.FactoryEntity;
import fr.ensma.lisi.ontoql.core.MultilingualAttribute;
import fr.ensma.lisi.ontoql.engine.OntoQLSQLWalker;
import fr.ensma.lisi.ontoql.engine.tree.IdentNode;
import fr.ensma.lisi.ontoql.engine.tree.dql.FromElement;
import fr.ensma.lisi.ontoql.evaluator.AbstractEvaluator;
import fr.ensma.lisi.ontoql.evaluator.OntoAlgebraEvaluator;
import fr.ensma.lisi.ontoql.exception.JDBCExceptionHelper;
import fr.ensma.lisi.ontoql.exception.SQLExceptionConverterFactory;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.ontomodel.OntoMultilingualAttribute;
import fr.ensma.lisi.ontoql.ontomodel.mapping.PlibAttribute;
import fr.ensma.lisi.ontoql.util.OntoDBHelper;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * OntoAlgebra for OntoDB.
 *
 * @author Stéphane JEAN
 */
public class OntoAlgebraEvaluatorOntoDB extends AbstractEvaluator implements OntoAlgebraEvaluator {

	/**
	 * A reference to its walker.
	 */
	private OntoQLSQLWalker walker;

	private OntoQLSession session;

	/**
	 * A factory to create new entity/class or attribute/property.
	 */
	private FactoryEntity factoryEntity = null;

	/**
	 * List of attribut resolved (due to ontology model differences).
	 */
	private Map<Category, Map<String, FromElement>> attributProceed = new Hashtable<Category, Map<String, FromElement>>();

	public OntoAlgebraEvaluatorOntoDB(OntoQLSQLWalker ontoQLWalker) {
		this.walker = ontoQLWalker;
		session = walker.getSession();
		factoryEntity = walker.getFactoryEntity();
	}

	@Override
	public void evaluateExt(Category category, FromElement node) {
		evaluateExt(category, node, false);

	}

	@Override
	public void evaluateExtPolymorph(Category category, FromElement node) {
		evaluateExt(category, node, true);
	}

	/**
	 * Evaluate the function ext for a class or an entity
	 * 
	 * @param category    a given class or entity
	 * @param node        the corresponding node
	 * @param isPolymorph true if this category is polymorph
	 */
	private void evaluateExt(Category category, FromElement node, boolean isPolymorph) {
		category.setPolymorph(isPolymorph);
		auxEvaluateExt(category, node);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void ontoImageAttribute(Attribute attribute, String lgCode, IdentNode nodeAttribute,
			FromElement nodeEntity) {
		List<PlibAttribute> links = (ArrayList) attribute.getMapAttribut().getLink().clone();

		if (links.size() > 0) {
			if (lgCode != null) {
				links.add(new PlibAttribute(attribute.getName() + "labelOrText"));
				links.add(new PlibAttribute(attribute.getName() + lgCode));
			}
			resolveOntologyModelDifferences(nodeAttribute, nodeEntity, attribute, links);
		}
	}

	/**
	 * Resolve the differences between the ontology model of OntoQL and the ontodb
	 * ontology model.
	 * 
	 * @param node               node to handle
	 * @param currentFromElement from elemnt corresponding to this attribute
	 * @param attribute          attribute processed
	 * @param links              links
	 * @throws SemanticException if a semantic exception is detected
	 */
	public final void resolveOntologyModelDifferences(final IdentNode node, FromElement currentFromElement,
			final Attribute attribute, final List<PlibAttribute> links) {

		// Check if the final link has been resolved
		Map<String, FromElement> linkProceed = attributProceed.get(attribute.getCurrentContext());
		PlibAttribute finalLink = (PlibAttribute) links.get(links.size() - 1);

		if (linkProceed == null) { // No link has been proceed for this entity
			linkProceed = new Hashtable<String, FromElement>();
			// a new hashtable is built to store the links that are going to be
			// proceed
			attributProceed.put(attribute.getCurrentContext(), linkProceed);
		}

		// get the FromElement corresponding to the final link
		FromElement finalFromElement = (FromElement) linkProceed.get(finalLink.getName());

		if (finalFromElement == null) { // The final link must be proceed
			for (int i = 0; i < links.size() - 1; i++) {
				// each intermediate link is put in the hashtable
				// the fromElement is an association table
				currentFromElement = resolveIntermediateLink(currentFromElement, attribute,
						(PlibAttribute) links.get(i), linkProceed);
			}
			finalFromElement = addImplicitJoin(currentFromElement, attribute, finalLink, linkProceed);
			if (finalFromElement == null) { // case of a non multilingual
				// attribute
				finalFromElement = currentFromElement;
			}
		}

		// the path is now resolved
		node.setFromElement(finalFromElement);

	}

	/**
	 * Resolve an intermediate link This link is put in the hashtable containing the
	 * link proceed It is associated with an association table.
	 * 
	 * @param fromElement the fromElement before this link
	 * @param attribut    the attribut proceed
	 * @param link        the intermediate link proceed
	 * @param linkProceed Hashtable containing the link proceed
	 * @return The association FromElement
	 * @throws SemanticException if a semantic exception is detected
	 */
	public final FromElement resolveIntermediateLink(final FromElement fromElement, final Attribute attribut,
			final PlibAttribute link, final Map<String, FromElement> linkProceed) {

		// intermediate link are prefixed by i_
		FromElement res = (FromElement) linkProceed.get("i_" + link.getName());

		if (res == null) { // intermediate link not yet proceed

			Entity currentEntity = (Entity) fromElement.getCategory();
			String condition = null;

			if (link.getName().equals(attribut.getName() + "labelOrText")) {
				MultilingualAttribute mAttribut = (MultilingualAttribute) attribut;
				boolean isLabel = ((OntoMultilingualAttribute) mAttribut.getMapAttribut()).isLabel();
				if (isMultilingualOntology()) {
					String labelOrText = isLabel ? "translated_label" : "translated_text";
					Entity labelOrTextEntity = new Entity(labelOrText);
					res = new FromElement(labelOrTextEntity, true, walker);
					Attribute firstLink = new Attribute("rid_d");
					firstLink.setCurrentContext(currentEntity);
					Attribute labelOrTextLink = new Attribute("rid");
					labelOrTextLink.setCurrentContext(labelOrTextEntity);
					condition = "ON " + firstLink.toSQL() + " = " + labelOrTextLink.toSQL();
				} else {
					String labelOrText = isLabel ? "label_t" : "text_t";
					Entity labelOrTextEntity = new Entity(labelOrText);
					labelOrTextEntity.getDelegateEntity().getMapTo().setAssociationTable(true);
					res = new FromElement(labelOrTextEntity, true, walker);
					Attribute firstLink = new Attribute("rid_d");
					firstLink.setCurrentContext(currentEntity);
					Attribute labelOrTextLink = new Attribute("rid");
					labelOrTextLink.setCurrentContext(labelOrTextEntity);
					condition = "ON " + firstLink.toSQL() + " = " + labelOrTextLink.toSQL();
				}
			} else {
				Attribute firstLink;
				String associationTable = link.getOfEntity().getName() + "_2_" + link.getName();
				Entity associationEntity = new Entity(associationTable);
				associationEntity.getDelegateEntity().getMapTo().setAssociationTable(true);
				res = new FromElement(associationEntity, true, walker);
				Attribute ridAssoc = null;

				if (currentEntity.getDelegateEntity().getMapTo().isAssociationTable()) {
					firstLink = new Attribute("rid_d");
					ridAssoc = new Attribute("rid_s");
				} else {
					firstLink = new Attribute("rid");
					ridAssoc = new Attribute("rid_s");
				}
				ridAssoc.setCurrentContext(associationEntity);
				firstLink.setCurrentContext(currentEntity);
				condition = "ON " + firstLink.toSQL() + " = " + ridAssoc.toSQL();
			}

			walker.addJoin(fromElement, res, condition, attribut.getMapAttribut().isOptional());

			linkProceed.put("i_" + link.getName(), res);
		}

		return res;

	}

	/**
	 * Return true if this ontology is multilingual
	 * 
	 * @return True if this ontology is multilingual
	 */
	private boolean isMultilingualOntology() {
		try {
			Statement st = session.connection().createStatement();
			ResultSet rset = st.executeQuery("select * from present_translations_e");
			return rset.next();
		} catch (SQLException sqle) {
			throw JDBCExceptionHelper.convert(SQLExceptionConverterFactory.buildMinimalSQLExceptionConverter(), sqle,
					"could not execute query", "");
		}
	}

	/**
	 * Proceed last element of links.
	 * 
	 * @param fromElement previous FromElement
	 * @param attribut    attributProceed
	 * @param link0       last link
	 * @return fromElement where the attribut is defined
	 * @throws SemanticException
	 */
	public FromElement addImplicitJoin(FromElement fromElement, Attribute attribut, PlibAttribute link0,
			Map<String, FromElement> linkProceed) {

		FromElement res = null;

		// Special treatment for multilingualAttribut
		if (attribut instanceof MultilingualAttribute) {
			if (isMultilingualOntology()) {
				MultilingualAttribute mAttribut = (MultilingualAttribute) attribut;
				Entity currentEntity = (Entity) fromElement.getCategory();

				boolean isLabel = ((OntoMultilingualAttribute) mAttribut.getMapAttribut()).isLabel();

				String labelOrText = isLabel ? "array_value_translated_label_labels"
						: "array_value_translated_text_texts";
				Entity labelOrTextEntity = new Entity(labelOrText);
				labelOrTextEntity.getDelegateEntity().getMapTo().setAssociationTable(true);
				FromElement generatedFromElement = new FromElement(labelOrTextEntity, true, walker);
				int lgCode = Integer.valueOf(OntoQLHelper.getLanguage(mAttribut.getLgCode(), session)).intValue();

				String firstLinkText = isLabel ? "labels[" : "texts[";
				Attribute firstLink = new Attribute(firstLinkText + lgCode + "]");
				firstLink.setCurrentContext(currentEntity);
				Attribute labelOrTextLink = new Attribute("rid");
				labelOrTextLink.setCurrentContext(labelOrTextEntity);
				String condition = "ON " + firstLink.toSQL() + " = " + labelOrTextLink.toSQL();

				walker.addJoin(fromElement, generatedFromElement, condition, attribut.getMapAttribut().isOptional());
				res = generatedFromElement;
			}
		} else {

			FromElement intermediateFromElement = resolveIntermediateLink(fromElement, attribut, link0, linkProceed);

			Category rangeEntity = null;
			EntityDatatype rangeAttribute = attribut.getRange();
			if (rangeAttribute.isAssociationType()) {
				if (((EntityDatatypeCategory) rangeAttribute).getCagetory().isEntity()) {
					rangeEntity = (Entity) ((Entity) ((EntityDatatypeCategory) rangeAttribute).getCagetory()).clone();
				} else {
					rangeEntity = ((EntityDatatypeCategory) rangeAttribute).getCagetory();
				}
			}

			else if (rangeAttribute.isCollectionAssociationType()) {
				EntityDatatypeCategory categoryCollection = (EntityDatatypeCategory) ((EntityDatatypeCollection) rangeAttribute)
						.getDatatype();
				rangeEntity = (Entity) ((Entity) categoryCollection.getCagetory()).clone();
			} else {
				rangeEntity = new Entity(attribut.getMapAttribut().getMapTo().getOfEntity().getName());
			}

			FromElement generatedFromElement = new FromElement(rangeEntity, true, walker);

			Attribute ridd = new Attribute("rid_d");
			ridd.setCurrentContext(intermediateFromElement.getCategory());
			String ridEntityInOriginalModel = OntoDBHelper.getPLIBAttributeForOid(rangeEntity.getName());

			Description ridPersistentEntityInOriginalModel = null;
			if (rangeEntity.isClass()) {
				ridPersistentEntityInOriginalModel = factoryEntity.createEntityPropertyOid((EntityClass) rangeEntity);
			} else {
				ridPersistentEntityInOriginalModel = new Attribute(ridEntityInOriginalModel);
				ridPersistentEntityInOriginalModel.setCurrentContext(rangeEntity);
			}

			String condition = "ON " + ridd.toSQL() + " = " + ridPersistentEntityInOriginalModel.toSQL();

			walker.addJoin(intermediateFromElement, generatedFromElement, condition,
					attribut.getMapAttribut().isOptional());

			res = generatedFromElement;

		}
		if (isMultilingualOntology()) {
			linkProceed.put(link0.getName(), res);
		}
		return res;
	}
}
