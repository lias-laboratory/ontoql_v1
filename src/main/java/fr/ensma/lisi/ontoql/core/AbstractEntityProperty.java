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
package fr.ensma.lisi.ontoql.core;

import java.util.Map;
import java.util.Vector;

import fr.ensma.lisi.ontoql.engine.util.AliasGenerator;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.NotSupportedDatatypeException;
import fr.ensma.lisi.ontoql.util.ArrayHelper;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Methods required on an ontology property for the conversion of OntoQL into
 * SQL
 * 
 * @author Stéphane Jean
 */
public abstract class AbstractEntityProperty implements EntityProperty {

	/**
	 * A reference to the factory creator of this object Enable to create other
	 * entity of the same kind
	 */
	protected FactoryEntity factory;

	/**
	 * the alias used in an OntoQL Query to reference this property
	 */
	protected String alias = null;

	protected Vector nonCoreAttributes = new Vector();

	protected String name_en;

	protected String name_fr;

	protected String definition_en;

	protected String definition_fr;

	/**
	 * The lg code for a multilingual property
	 */
	protected String lgCode = null;

	/**
	 * The range of the property
	 */
	protected EntityDatatype range = null;

	/**
	 * The scope of this property
	 */
	protected EntityClass scope = null;

	/**
	 * The current context in which this property is considered
	 */
	protected Category currentContext = null;

	/**
	 * The current context in which this property is considered
	 */
	protected EntityProperty pathProperty = null;

	/**
	 * The namespace of this ontology property
	 **/
	protected String namespace;

	protected Vector nonCoreAttributesValues = new Vector();

	/**
	 * protected because it doesn't initialized the require attribut but it's
	 * necessary for the under class EntityPropertyRid
	 */
	protected AbstractEntityProperty() {
	}

	public AbstractEntityProperty(String id, String namespace, String lg, FactoryEntity factory) {
		this.namespace = namespace;
		if (id != null) {
			if (id.startsWith(OntoQLHelper.PREFIX_INTERNAL_ID)) {
				id = id.substring(1);
				setValueInternalId(id);
			} else if (id.startsWith(OntoQLHelper.PREFIX_EXTERNAL_ID)) {
				id = id.substring(1);
				setValueExternalId(id);
			} else if (lg == OntoQLHelper.NO_LANGUAGE) {
				setValueExternalId(id);
			} else if (id.startsWith(OntoQLHelper.PREFIX_NAME_ID)) {
				id = id.substring(1, id.length() - 1);
				setName(id, lg);
			} else {
				setName(id, lg);
			}
		}
		this.factory = factory;
	}

	/**
	 * True If the range of the property is loaded
	 */
	protected boolean isLoadedRange() {
		return range != null;
	}

	/**
	 * @return Returns the internal identifier of this property
	 */
	public String getInternalId() throws JOBDBCException {
		if (!isInternalIdInitialized()) {
			load();
		}

		return getValueInternalId();
	}

	/**
	 * Set the range of this class.
	 */
	public void setRange(EntityDatatype range) {
		this.range = range;
	}

	/**
	 * @return get the current language
	 */
	protected abstract String getCurrentLanguage();

	@Override
	public abstract void setCurrentLanguage(String lg);

	/**
	 * @return The value of the internal identifier of this property
	 */
	protected abstract String getValueInternalId();

	/**
	 * @return The value of the external identifier of this property
	 */
	protected abstract String getValueExternalId();

	/**
	 * @return The value of the name of this property in the current language
	 */
	protected abstract String getValueName();

	/**
	 * @return The value of the name of this property in the given language
	 */
	protected abstract String getValueName(String lg);

	/**
	 * Set the value of the external identifier of this property
	 */
	protected abstract void setValueInternalId(String internalId);

	/**
	 * Set the value of the external identifier of this property
	 */
	protected abstract void setValueExternalId(String externalId);

	@Override
	public void setName(String name) {
		setName(name, getCurrentLanguage());
	}

	@Override
	public void setName(String name, String lg) {
		if (lg != null) {
			if (lg.equals(OntoQLHelper.ENGLISH)) {
				this.name_en = name;
			} else if (lg.equals(OntoQLHelper.FRENCH)) {
				this.name_fr = name;
			}
		}
	}

	@Override
	public void setDefinition(String def) {
		setDefinition(def, getCurrentLanguage());
	}

	@Override
	public void setDefinition(String def, String lg) {
		if (lg != null) {
			if (lg.equals(OntoQLHelper.ENGLISH)) {
				this.definition_en = def;
			} else if (lg.equals(OntoQLHelper.FRENCH)) {
				this.definition_fr = def;
			}
		}
	}

	@Override
	public abstract void setCode(String code);

	@Override
	public abstract void setVersion(String version);

	/**
	 * @return True if the internal identifier of this property is valued
	 */
	protected abstract boolean isInternalIdInitialized();

	/**
	 * @return True if the external identifier of this property is valued
	 */
	protected abstract boolean isExternalIdInitialized();

	/**
	 * load the attribut of the class
	 */
	protected abstract void load() throws JOBDBCException;

	/**
	 * @return True if the name of this property in the current language is valued
	 */
	protected abstract boolean isNameInitialized();

	/**
	 * @return True if the name of this property in the given language is valued
	 */
	protected abstract boolean isNameInitialized(String lg);

	@Override
	public String getExternalId() {
		if (!isExternalIdInitialized()) {
			load();
		}
		return getValueExternalId();
	}

	@Override
	public String getName() {
		return getName(getCurrentLanguage());
	}

	@Override
	public String getName(String lg) {
		if (!isNameInitialized(lg)) {
			load();
		}
		return getValueName(lg);
	}

	@Override
	public EntityDatatype getRange() {
		if (!isLoadedRange()) {
			loadRange();
		}
		return range;
	}

	protected abstract void loadRange();

	@Override
	public boolean isEnumerateType() {
		return getRange() instanceof EntityDatatypeEnumerate;
	}

	@Override
	public String getAlias() {
		return alias;
	}

	@Override
	public void checkExistence() {
		getInternalId();
	}

	@Override
	public abstract String getNameExtent() throws JOBDBCException;

	@Override
	public abstract String getExtent();

	@Override
	public void setAlias(String alias) {
		this.alias = alias;
	}

	@Override
	public Category getCurrentContext() {
		return this.currentContext;
	}

	@Override
	public void setCurrentContext(Category currentContext) {
		this.currentContext = currentContext;
	}

	@Override
	public boolean isUsed() throws JOBDBCException {
		return isUsed(getCurrentContext().isPolymorph());
	}

	@Override
	public boolean isUsed(boolean polymorph) throws JOBDBCException {
		return isUsed((EntityClass) getCurrentContext(), polymorph);
	}

	@Override
	public boolean isUsed(EntityClass currentContext, boolean polymorph) throws JOBDBCException {
		boolean res = false;

		// Equals take care of the context. So the context of this property is
		// changed for comparison
		// and then reinitialized at the end of the method.
		AbstractEntityClass contextProperty = (AbstractEntityClass) getCurrentContext();

		this.setCurrentContext(currentContext);
		if (polymorph) {
			res = ArrayHelper.contain(currentContext.getUsedPropertiesPolymorph(), this);
		} else {
			res = ArrayHelper.contain(currentContext.getUsedProperties(), this);
		}
		this.setCurrentContext(contextProperty);
		return res;
	}

	@Override
	public boolean isDefined() {
		return isDefined(getCurrentContext());
	}

	@Override
	public boolean isDefined(Category context) {
		return ArrayHelper.contain(((EntityClass) context).getDefinedProperties(), this);
	}

	@Override
	public abstract String toSQL(Category context, boolean polymorph);

	@Override
	public String toSQL(Category context) {
		boolean polymorph = context != null ? context.isPolymorph() : false;
		return toSQL(context, polymorph);
	}

	@Override
	public String toSQL() {
		return toSQL((AbstractEntityClass) getCurrentContext());
	}

	@Override
	public int hashCode() {

		String valueInternalIdentifier = getInternalId();

		int hash = 7;
		hash = 31 * hash + (null == valueInternalIdentifier ? 0 : valueInternalIdentifier.hashCode());
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		boolean isEquals = false;

		if (this == obj) {
			isEquals = true;
		} else if (this != null && obj != null) {
			// Comparison using internal id values
			AbstractEntityProperty other = ((AbstractEntityProperty) obj);
			String thisValueInternalIdentifier = getValueInternalId();
			String otherValueInternalIdentifier = other.getValueInternalId();

			if (thisValueInternalIdentifier != null && otherValueInternalIdentifier == null) {
				load();
			} else if (thisValueInternalIdentifier == null && otherValueInternalIdentifier != null) {
				other.load();
			}
			if (thisValueInternalIdentifier != null && otherValueInternalIdentifier != null) {
				isEquals = thisValueInternalIdentifier.equals(otherValueInternalIdentifier);
			} else { // Comparison using external id values
				String thisValueExternalIdentifier = getValueExternalId();
				String otherValueExternalIdentifier = other.getValueExternalId();
				if (thisValueExternalIdentifier != null && otherValueExternalIdentifier != null) {
					isEquals = thisValueExternalIdentifier.equals(otherValueExternalIdentifier);
				} else { // Comparison using english name values
					String thisValueNameEn = getValueName(OntoQLHelper.ENGLISH);
					String otherValueNameEn = other.getValueName(OntoQLHelper.ENGLISH);
					if (thisValueNameEn != null && otherValueNameEn != null) {
						isEquals = thisValueNameEn.equals(otherValueNameEn);
					} else { // Comparison using french name values
						String thisValueNameFr = getValueName(OntoQLHelper.FRENCH);
						String otherValueNameFr = other.getValueName(OntoQLHelper.FRENCH);
						if (thisValueNameFr != null && otherValueNameFr != null) {
							isEquals = thisValueNameFr.equals(otherValueNameFr);
						} else { // Properties not loaded with the same
							// kind of identifier
							isEquals = getInternalId().equals(other.getInternalId()); // load of
							// internal
							// identifier
						}
					}
				}
			}

		}

		return isEquals;
	}

	@Override
	public String toString() {
		return toString(null, null);
	}

	@Override
	public String toString(Map<String, String> namespaces, AliasGenerator aliasGenerator) {
		String res = getPathProperty() == null ? "" : getPathProperty().toString(namespaces, aliasGenerator) + ".";
		if (namespaces != null) {
			String namespaceAlias = "";
			if (namespaces.containsKey(namespace)) {
				namespaceAlias = (String) namespaces.get(namespace);
			} else {
				namespaceAlias = aliasGenerator.createAliasNamespace(namespace);
				namespaces.put(namespace, namespaceAlias);
			}
			res += namespaceAlias + ":";
		}

		if (OntoQLHelper.isLanguageAvailable(getCurrentLanguage())) {
			res += getName();
		} else {
			res += getExternalId();
		}
		return res;
	}

	@Override
	public EntityProperty getPathProperty() {
		return pathProperty;
	}

	@Override
	public void setPathProperty(EntityProperty pathProperty) {
		this.pathProperty = pathProperty;
	}

	@Override
	public boolean isProperty() {
		return true;
	}

	@Override
	public boolean isAttribute() {
		return false;
	}

	@Override
	public String getTypeLabel() {
		return "property";
	}

	@Override
	public void insert() throws JOBDBCException {
		// Default do nothing
	}

	@Override
	public EntityClass getScope() {
		return scope;
	}

	@Override
	public void setScope(EntityClass scope) {
		this.scope = scope;
	}

	@Override
	public boolean isMultilingualDescription() {
		boolean isMultilingual = false;
		try {
			isMultilingual = getRange().isMultilingualType();
		} catch (NotSupportedDatatypeException oExc) {
		}
		return isMultilingual;
	}

	@Override
	public void setLgCode(String lgCode) {
		this.lgCode = lgCode;
	}

	@Override
	public String getNamespace() {
		return namespace;
	}

	@Override
	public void setNonCoreAttributes(Vector nonCoreAttributes) {
		this.nonCoreAttributes = nonCoreAttributes;
	}

	@Override
	public void setNonCoreAttributesValues(Vector nonCoreAttributesValues) {
		this.nonCoreAttributesValues = nonCoreAttributesValues;
	}
}