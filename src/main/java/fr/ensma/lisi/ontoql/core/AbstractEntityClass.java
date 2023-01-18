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
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * This abstract class implements methods required on an ontology class for the
 * evaluation of an OntoQL instruction.
 * 
 * @author Stéphane Jean
 */
public abstract class AbstractEntityClass extends AbstractCategory implements EntityClass {

	/**
	 * Internal identifier of this class.
	 */
	protected Long id;

	/**
	 * Code of this class.
	 */
	protected String code;

	/**
	 * Version of this class.
	 */
	protected String version;

	/**
	 * Name in english of this class.
	 */
	protected String name_en;

	/**
	 * Name in french of this class.
	 */
	protected String name_fr;

	/**
	 * Definition in English of this class.
	 */
	protected String definition_en;

	/**
	 * Definition in French of this class.
	 */
	protected String definition_fr;

	/**
	 * A reference to the factory creator of this object Enable to create other
	 * entity of the same kind
	 */
	protected FactoryEntity factory;

	/**
	 * All the direct subclasses of the class
	 */
	protected EntityClass[] directSubclasses = null;

	/**
	 * TODO change with multiple inheritance The superClass of this class.
	 */
	protected EntityClass superClass = null;

	/**
	 * The properties defined by this class
	 */
	protected EntityProperty[] scopeProperties = null;

	/**
	 * All the properties (inherited included) defined on this class
	 */
	protected EntityProperty[] definedProperties = null;

	/**
	 * The properties used in the extent of the class
	 */
	protected EntityProperty[] usedProperties = null;

	/**
	 * The properties used in the extent of the class or one of its subclass
	 */
	protected EntityProperty[] usedPropertiesPolymorph = null;

	/**
	 * True if all the properties used in the extent of the class or one of its
	 * subclasses has been loaded
	 */
	protected boolean isLoadedUsedPropertiesPolymorph = false;

	protected Vector nonCoreAttributes = new Vector();

	protected Vector nonCoreAttributesValues = new Vector();

	/**
	 * @return The value of the name of this class in the current language
	 */
	protected String getValueName() {
		return getValueName(getCurrentLanguage());
	}

	/**
	 * Gives the value of the name of this class in the current language
	 * 
	 * @param lg a given natural language
	 * @return The value of the name of this class in this language
	 */
	protected String getValueName(String lg) {
		String res = null;
		if (lg != null) {
			if (lg.equals(OntoQLHelper.ENGLISH)) {
				res = name_en;
			} else if (lg.equals(OntoQLHelper.FRENCH)) {
				res = name_fr;
			}
		}
		return res;
	}

	/**
	 * @return The value of the name of this class in the current language
	 */
	protected String getValueDefinition() {
		return getValueDefinition(getCurrentLanguage());
	}

	/**
	 * Gives the value of the name of this class in the current language
	 * 
	 * @param lg a given natural language
	 * @return The value of the name of this class in this language
	 */
	protected String getValueDefinition(String lg) {
		String res = null;
		if (lg != null) {
			if (lg.equals(OntoQLHelper.ENGLISH)) {
				res = definition_en;
			} else if (lg.equals(OntoQLHelper.FRENCH)) {
				res = definition_fr;
			}
		}
		return res;
	}

	/**
	 * True if all the direct subclasses of the class has been loaded
	 */
	protected boolean isLoadedDirectSubclasses() {
		return directSubclasses != null;
	}

	/**
	 * True if all the defined properties on the class has been loaded
	 */
	protected boolean isLoadedDefinedProperties() {
		return definedProperties != null;
	}

	/**
	 * True if all the properties used in the extent of the class has been loaded
	 */
	protected boolean isLoadedUsedProperties() {
		return usedProperties != null;
	}

	public AbstractEntityClass(String id, String namespace, String lg, FactoryEntity factory) {
		this.namespace = namespace;
		if (id != null) {
			if (isInternalIdentifier(id)) {
				id = removeSyntaxInternalId(id);
				setValueInternalId(id);
			} else if (isExternalIdentifier(id)) {
				id = removeSyntaxExternalId(id);
				setValueExternalId(id);
			} else if (lg == OntoQLHelper.NO_LANGUAGE) {
				setValueExternalId(id);
			} else {
				if (isNameIdentifier(id)) {
					id = removeSyntaxNameIdentifier(id);
				}
				setName(id, lg);
			}
		}
		this.factory = factory;

	}

	/**
	 * Remove the syntax of an internal identifier (!)
	 * 
	 * @param id an internal identifier
	 * @return the internal identifier without the syntax
	 */
	private String removeSyntaxInternalId(String id) {
		id = id.substring(1);
		return id;
	}

	/**
	 * Remove the syntax of an internal identifier (!)
	 * 
	 * @param id an external identifier
	 * @return the external identifier without the syntax
	 */
	private String removeSyntaxExternalId(String id) {
		return removeSyntaxInternalId(id);
	}

	/**
	 * remove the "" from a name identifier
	 * 
	 * @param id the name which identify a class in the OntoQL syntax
	 * @return the name identifier without " ".
	 */
	private String removeSyntaxNameIdentifier(String id) {
		id = id.substring(1, id.length() - 1);
		return id;
	}

	/**
	 * check if a text is a name identifier
	 * 
	 * @param id a text
	 * @return true if the text is a name identifier
	 */
	private boolean isNameIdentifier(String id) {
		return id.startsWith(OntoQLHelper.PREFIX_NAME_ID);
	}

	/**
	 * check if a text is an external identifier
	 * 
	 * @param id a text
	 * @return true if the text is an external identifier
	 */
	private boolean isExternalIdentifier(String id) {
		return id.startsWith(OntoQLHelper.PREFIX_EXTERNAL_ID);
	}

	/**
	 * check if a text is an internal identifier
	 * 
	 * @param id a text
	 * @return true if the text is an internal identifier
	 */
	private boolean isInternalIdentifier(String id) {
		return id.startsWith(OntoQLHelper.PREFIX_INTERNAL_ID);
	}

	@Override
	public Description getDefinedDescription(String identifier, String lg) {
		// for the moment, the language is not taken into account
		return getDefinedProperty(identifier, lg);
	}

	/**
	 * Get a property defined on this class according to an identifier.
	 * 
	 * @param identifier an identifier
	 * @return the property defined on this class having the given identifier or
	 *         null
	 */
	private EntityProperty getDefinedProperty(String identifier, String lg) {
		EntityProperty res = null;
		if (identifier.equals("oid")) {
			res = factory.createEntityPropertyOid(this);
		} else if (isInternalIdentifier(identifier)) {
			identifier = removeSyntaxInternalId(identifier);
			res = getDefinedPropertyByInternalIdentifier(identifier);
		} else if (isExternalIdentifier(identifier)) {
			identifier = removeSyntaxExternalId(identifier);
			res = getDefinedPropertyByExternalIdentifier(identifier);
		} else if (lg == OntoQLHelper.NO_LANGUAGE) {
			res = getDefinedPropertyByExternalIdentifier(identifier);
		} else {
			if (isNameIdentifier(identifier)) {
				identifier = removeSyntaxNameIdentifier(identifier);
			}
			res = getDefinedPropertyByNameIdentifier(identifier);
		}
		return res;
	}

	/**
	 * Get a property defined on this class according to an internal identifier.
	 * 
	 * @param internalId an internal identifier
	 * @return the property defined on this class having the given internal
	 *         identifier or null
	 */
	public EntityProperty getDefinedPropertyByInternalIdentifier(String internalId) {
		EntityProperty res = null;
		EntityProperty[] definedProperties = getDefinedProperties();
		EntityProperty entityProperty = null;
		for (int i = 0; i < definedProperties.length; i++) {
			entityProperty = definedProperties[i];
			if (entityProperty.getInternalId().equals(internalId)) {
				res = entityProperty;
				break;
			}
		}
		return res;
	}

	/**
	 * Get a property defined on this class according to an external identifier.
	 * 
	 * @param externalId an external identifier
	 * @return the property defined on this class having the given external
	 *         identifier or null
	 */
	public EntityProperty getDefinedPropertyByExternalIdentifier(String externalId) {
		EntityProperty res = null;
		EntityProperty[] definedProperties = getDefinedProperties();
		EntityProperty entityProperty = null;
		for (int i = 0; i < definedProperties.length; i++) {
			entityProperty = definedProperties[i];
			if (entityProperty.getExternalId().equals(externalId)) {
				res = entityProperty;
				break;
			}
		}
		return res;
	}

	/**
	 * Get a property defined on this class according to an external identifier.
	 * 
	 * @param nameId an external identifier
	 * @return the property defined on this class having the given name identifier
	 *         or null
	 */
	public EntityProperty getDefinedPropertyByNameIdentifier(String nameId) {
		EntityProperty res = null;
		EntityProperty[] definedProperties = getDefinedProperties();
		EntityProperty entityProperty = null;
		for (int i = 0; i < definedProperties.length; i++) {
			entityProperty = definedProperties[i];
			if (entityProperty.getName().equals(nameId)) {
				res = entityProperty;
				break;
			}
		}
		return res;
	}

	@Override
	public String getInternalId() throws JOBDBCException {
		if (!isInternalIdInitialized()) {
			load();
		}

		return getValueInternalId();
	}

	/**
	 * @return get the current language
	 */
	protected abstract String getCurrentLanguage();

	public abstract void setCurrentLanguage(String lg);

	/**
	 * @return The value of the internal identifier of this class
	 */
	protected String getValueInternalId() {
		return id.toString();
	}

	/**
	 * @return The value of the external identifier of this class
	 */
	protected String getValueExternalId() {
		String res = code;
		if (version != null) {
			res += OntoQLHelper.SEPARATOR_EXTERNAL_ID + version;
		}
		return res;
	}

	/**
	 * Set the value of the external identifier of this class
	 */
	protected void setValueInternalId(String internalId) {
		this.id = Long.valueOf(internalId);
	}

	/**
	 * Set the value of the external identifier of this class
	 */
	protected void setValueExternalId(String externalId) {
		int separatorIndex = externalId.indexOf(OntoQLHelper.SEPARATOR_EXTERNAL_ID);
		if (separatorIndex == -1) {
			// no version
			this.code = externalId;
		} else {
			String code = externalId.substring(0, separatorIndex);
			String version = externalId.substring(separatorIndex + 1, externalId.length());
			this.code = code;
			this.version = version;
		}
	}

	@Override
	public void setName(String name) {
		setName(name, getCurrentLanguage());
	}

	@Override
	public void setName(String name, String lg) {
		if (lg != null) {
			if (lg.equals(OntoQLHelper.ENGLISH)) {
				name_en = name;
			} else if (lg.equals(OntoQLHelper.FRENCH)) {
				name_fr = name;
			}
		}
	}

	@Override
	public void setDefinition(String name) {
		setDefinition(name, getCurrentLanguage());
	}

	@Override
	public void setDefinition(String name, String lg) {
		if (lg != null) {
			if (lg.equals(OntoQLHelper.ENGLISH)) {
				definition_en = name;
			} else if (lg.equals(OntoQLHelper.FRENCH)) {
				definition_fr = name;
			}
		}
	}

	@Override
	public void setCode(String code) {
		this.code = code;
	}

	@Override
	public void setVersion(String version) {
		this.version = version;
	}

	@Override
	public void setSuperClass(EntityClass superClass) {
		this.superClass = superClass;
	}

	/**
	 * @return True if the internal identifier of this class is valued
	 */
	protected boolean isInternalIdInitialized() {
		return id != null;
	}

	/**
	 * @return True if the external identifier of this class is valued
	 */
	protected boolean isExternalIdInitialized() {
		return code != null;
	}

	/**
	 * load the attribut of the class
	 */
	protected abstract void load() throws JOBDBCException;

	/**
	 * @return True if the name of this class in the current language is valued
	 */
	protected boolean isNameInitialized() {
		return isNameInitialized(getCurrentLanguage());
	}

	/**
	 * @return True if the name of this class in the given language is valued
	 */
	protected boolean isNameInitialized(String lg) {
		boolean res = false;
		if (lg != null) {
			if (lg.equals(OntoQLHelper.ENGLISH)) {
				res = name_en != null;
			} else if (lg.equals(OntoQLHelper.FRENCH)) {
				res = name_fr != null;
			}
		}
		return res;
	}

	@Override
	public String getExternalId() {
		if (!isExternalIdInitialized()) {
			load();
		}
		return getValueExternalId();
	}

	@Override
	public String getName(String lg) {
		if (!isNameInitialized(lg)) {
			load();
		}
		return getValueName(lg);
	}

	@Override
	public String getName() {
		return getName(getCurrentLanguage());
	}

	@Override
	public String getCategoryAlias() {
		return categoryAlias;
	}

	@Override
	public void setCategoryAlias(String alias) {
		this.categoryAlias = alias;
	}

	@Override
	public abstract void insert() throws JOBDBCException;

	@Override
	public abstract String getNameExtent() throws JOBDBCException;

	@Override
	public abstract void createTable(EntityProperty[] propertiesExtent) throws JOBDBCException;

	/**
	 * Add a new property in the extent of the class
	 */
	public abstract void addColumnToTable(EntityProperty propertyToAdd) throws JOBDBCException;

	@Override
	public EntityProperty[] getDefinedProperties() {
		// This derived attribut is complex to calculate and
		// will not evolve during the construction (QBE) or execution of a query
		// in consequence an attribut of this class will stock this value
		if (!isLoadedDefinedProperties()) {
			loadDefinedProperties();
		}
		return definedProperties;
	}

	protected abstract void loadDefinedProperties();

	@Override
	public EntityClass[] getDirectSubclasses() {
		if (!isLoadedDirectSubclasses()) {
			loadDirectSubclasses();
		}
		return directSubclasses;
	}

	@Override
	public Category[] getSubcategories() {
		return getDirectSubclasses();
	}

	protected abstract void loadDirectSubclasses();

	@Override
	public EntityProperty[] getUsedProperties() {
		if (!isLoadedUsedProperties()) {
			loadUsedProperties();
		}
		return usedProperties;
	}

	protected abstract void loadUsedProperties();

	@Override
	public boolean isAbstract() {
		return getUsedProperties().length == 0;
	}

	@Override
	public EntityProperty[] getUsedPropertiesPolymorph() {
		if (!isLoadedUsedPropertiesPolymorph) {
			loadUsedPropertiesPolymorph();
			isLoadedUsedPropertiesPolymorph = true;
		}
		return usedPropertiesPolymorph;
	}

	/**
	 * Load the properties used in the extent of this class or used in the extent of
	 * one of its subclasses
	 * 
	 * This method must be overloaded to avoid N+1 Select problem (see SpecEClass)
	 * 
	 */
	protected void loadUsedPropertiesPolymorph() {
		Vector<EntityProperty> res = new Vector<EntityProperty>();

		EntityProperty[] allUsableProperties = getDefinedProperties();

		Vector<EntityProperty> propertyToFound = new Vector<EntityProperty>();
		for (int i = 0; i < allUsableProperties.length; i++) {
			propertyToFound.add(allUsableProperties[i]);
		}

		// remove used properties of this class;
		EntityProperty[] usablePropCurrentClass = getUsedProperties();
		EntityProperty currentProp;
		for (int i = 0; i < usablePropCurrentClass.length; i++) {
			currentProp = usablePropCurrentClass[i];
			res.add(currentProp);
			propertyToFound.remove(currentProp);
		}

		// remove used properties of its subclass;
		int k = 0;
		EntityClass[] itsSubclasses = getDirectSubclasses();
		EntityProperty[] currentUsedProperty;
		EntityClass currentSubclass = null;

		while (propertyToFound.size() > 0 && k < itsSubclasses.length) {
			currentSubclass = itsSubclasses[k];
			currentUsedProperty = currentSubclass.getUsedPropertiesPolymorph();
			for (int i = 0; i < currentUsedProperty.length; i++) {
				currentProp = currentUsedProperty[i];
				currentProp.setCurrentContext(this); // only for comparison
				int indexCurrentProp = propertyToFound.indexOf(currentProp);
				currentProp.setCurrentContext(currentSubclass);
				if (indexCurrentProp != -1) {
					res.add(propertyToFound.get(indexCurrentProp));
					propertyToFound.remove(indexCurrentProp);
				}

			}

			k++;
		}

		if (res.size() == 0) {
			usedPropertiesPolymorph = new AbstractEntityProperty[0];
		} else {
			usedPropertiesPolymorph = (EntityProperty[]) res.toArray(new AbstractEntityProperty[0]);
		}

	}

	@Override
	public String toSQL() {
		return toSQL(isPolymorph());
	}

	@Override
	public abstract String toSQL(boolean polymorph) throws JOBDBCException;

	@Override
	public abstract String project(EntityProperty[] properties, boolean polymorph) throws JOBDBCException;

	@Override
	public boolean equals(Object obj) {
		boolean isEquals = false;

		if (obj != null) {
			isEquals = ((EntityClass) obj).getInternalId().equals(getInternalId());
		}

		return isEquals;
	}

	@Override
	public String toString() {
		return toString(null, null);
	}

	@Override
	public String toString(Map<String, String> namespaces, AliasGenerator aliasGenerator) {

		String res = "";

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
	public boolean isClass() {
		return true;
	}

	@Override
	public boolean isEntity() {
		return false;
	}

	@Override
	public String getTypeLabel() {
		return "class";
	}

	@Override
	public EntityProperty[] getScopeProperties() {
		return scopeProperties;
	}

	@Override
	public void setScopeProperties(EntityProperty[] scopeProperties) {
		this.scopeProperties = scopeProperties;
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