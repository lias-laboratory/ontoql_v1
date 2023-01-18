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

import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.ontoapi.Instance;

/**
 * Interface of creation of the entity require for the ontoql to sql conversion.
 * 
 * @author Stephane JEAN
 */
public interface FactoryEntity {

	/**
	 * Create a category (an entity or a class) according to a given identifier in a
	 * given namespace.
	 * 
	 * @param oid       identifier of the category
	 * @param namespace namespace used
	 * @return the corresponding category
	 */
	Category createCategory(String oid, String namespace);

	/**
	 * Create a category (an entity or a class) according to the given identifier.
	 * 
	 * @param oid Identifier of the category
	 * @return the corresponding category
	 */
	Category createCategory(String oid);

	/**
	 * Create an entity according to the given identifier. It must be prefixed by #.
	 * 
	 * @param oid Identifier of the entity
	 * @return the corresponding entity
	 */
	Entity createEntity(String oid);

	/**
	 * Create an entity according to the given identifier and to a given namespace.
	 * It must be prefixed by #.
	 * 
	 * @param oid       Identifier of the entity
	 * @param namespace namespace used
	 * @return the corresponding entity
	 */
	Entity createEntity(String oid, String namespace);

	/**
	 * Create a class according to the given identifier in a given namespace. It can
	 * be an internal identifier (prefix !oid), an external identifier (prefix @oid)
	 * or a name in the default language.
	 * 
	 * @param oid Identifier of the class
	 * @return the corresponding class
	 */
	EntityClass createEntityClass(String oid, String namespace);

	/**
	 * Create a class according to the given identifier. It can be an internal
	 * identifier (prefix !oid), an external identifier (prefix
	 * 
	 * @oid) or a name in the default language.
	 * 
	 * @param oid Identifier of the class
	 * @return the corresponding class
	 */
	EntityClass createEntityClass(String oid);

	/**
	 * Create a new class not persistent in the obdb.
	 * 
	 * @return a new class not persistent in the obdb
	 */
	EntityClass createEntityClass();

	/**
	 * Create the root class (contain all the instances of the obdb)
	 * 
	 * @return the root class
	 */
	EntityClass createEntityClassRoot();

	/**
	 * Create a new property not persistent in the obdb.
	 * 
	 * @return a new property not persistent in the obdb
	 */
	EntityProperty createEntityProperty();

	/**
	 * Create an instance of a class.
	 * 
	 * @param oid      - Identifier of this instance
	 * @param baseType - base type of this instance
	 * @return the corresponding instance as object of the class Instance
	 */
	Instance createInstance(String oid, EntityClass baseType);

	/**
	 * Create a description (an attribute or a property) according to the given
	 * identifier.
	 * 
	 * @param oid       Identifier of the description
	 * @param namespace namespace of the description
	 * @return the corresponding description
	 * @throws JOBDBCException if this description can not be loaded
	 */
	Description createDescription(String oid, String namespace) throws JOBDBCException;

	/**
	 * Create a description (an attribute or a property) according to the given
	 * identifier.
	 * 
	 * @param oid Identifier of the description
	 * @return the corresponding description
	 * @throws JOBDBCException if this description can not be loaded
	 */
	Description createDescription(String oid) throws JOBDBCException;

	/**
	 * Create a description (an attribute or a property) for the given category (an
	 * entity or a class).
	 * 
	 * @param aCategory a category
	 * @return the oid description
	 */
	Description createDescriptionOid(Category aCategory);

	/**
	 * Create an attribute according to the given identifier. It must be prefixed by
	 * #.
	 * 
	 * @param oid Identifier of the attribute
	 * @return the corresponding attribute
	 */
	Attribute createAttribute(String oid);

	/**
	 * Create an attribute according to the given identifier/namespace. It must be
	 * prefixed by #.
	 * 
	 * @param oid       Identifier of the attribute
	 * @param namespace
	 * @return the corresponding attribute
	 */
	Attribute createAttribute(String oid, String namespace);

	/**
	 * Create a property according to the given identifier and a given namespace. It
	 * can be an internal identifier (prefix !oid), an external identifier (prefix
	 * 
	 * @oid) or a name in the default language.
	 * 
	 * @param oid       Identifier of the property
	 * @param namespace namespace used
	 * @return the corresponding property
	 */
	EntityProperty createEntityProperty(String oid, String namespace);

	/**
	 * Create a property according to the given identifier. It can be an internal
	 * identifier (prefix !oid), an external identifier (prefix
	 * 
	 * @oid) or a name in the default language.
	 * 
	 * @param oid Identifier of the property
	 * @return the corresponding property
	 */
	EntityProperty createEntityProperty(String oid);

	/**
	 * Create an oid property for the given entity class.
	 * 
	 * @param entityClass a class
	 * @return the oid property
	 */
	EntityProperty createEntityPropertyOid(EntityClass entityClass);

	/**
	 * Create a typeOf property for the given entity class.
	 * 
	 * @param entityClass a class
	 * @return the typeOf property
	 */
	EntityProperty createEntityPropertyTypeOf(EntityClass entityClass);

	/**
	 * Create a multilingual attribute according to the given identifier in the
	 * given language. The oid must be prefixed by #.
	 * 
	 * @param oid    Identifier of the mulilingual attribute
	 * @param lgCode language of the mulilingual attribute
	 * @return the corresponding mulilingual attribute
	 */
	MultilingualAttribute createMultilingualAttribut(String oid, String lgCode);

	/**
	 * Create a data type.
	 * 
	 * @param datatypeName name of this datatype
	 * @param rid          the identifier of this datatype
	 * @return the buit data type
	 */
	EntityDatatype createEntityDatatype(String datatypeName, String rid);

	/**
	 * Create a new datatype not persistent in the obdb.
	 * 
	 * @param the name of the datatype created
	 * @return a new datatype not persistent in the obdb
	 */
	EntityDatatype createEntityDatatype(String datatypeName);

	/**
	 * Instanciate an object of a given class
	 * 
	 * @param instanceClass     Class of the class to instantiate
	 * @param nameInstanceClass name of the class to instantiate
	 * @return
	 */
	Object instantiate(Class instanceClass, String nameInstanceClass);
}
