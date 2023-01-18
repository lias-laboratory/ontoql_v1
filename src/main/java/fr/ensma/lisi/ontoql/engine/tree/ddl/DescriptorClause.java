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
import java.util.Vector;

import antlr.collections.AST;
import fr.ensma.lisi.ontoql.core.Attribute;
import fr.ensma.lisi.ontoql.core.Entity;
import fr.ensma.lisi.ontoql.core.EntityDatatype;
import fr.ensma.lisi.ontoql.core.EntityDatatypeCategory;
import fr.ensma.lisi.ontoql.engine.tree.IdentNode;
import fr.ensma.lisi.ontoql.engine.tree.OntoQLSQLWalkerNode;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * Defines an AST node representing an OntoQL descriptor clause.
 * 
 * @author Stéphane JEAN
 */
public class DescriptorClause extends OntoQLSQLWalkerNode {

	private static final long serialVersionUID = 4130900880216503341L;

	/**
	 * only the core attribute of class and properties (e.g, #name) are handled by
	 * this clause. Other attributes are stored in the following properties
	 */
	private Vector attributesNonHandled = new Vector();

	private Vector attributesValuesNonHandled = new Vector();

	public Vector getAttributesNonHandled() {
		return attributesNonHandled;
	}

	public Vector getAttributesValuesNonHandled() {
		return attributesValuesNonHandled;
	}

	/**
	 * Set the description of an instance of an entity
	 * 
	 * @param instance    The instance described
	 * @param entity      Type of the instance described
	 * @param mappedClass OntoAPI Type of the instance described
	 * @throws JOBDBCException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void setDescriptor(Object instance, Entity entity, Class mappedClass) throws JOBDBCException {
		AST currentAttributeAssignment = getFirstChild();
		AST currentASTAttribute = null;
		Attribute currentAttribute = null;
		AST currentASTValue = null;

		try {

			while (currentAttributeAssignment != null) {
				currentASTAttribute = currentAttributeAssignment.getFirstChild();
				currentAttribute = (Attribute) entity.getDefinedDescription(currentASTAttribute.getText(),
						getSession().getReferenceLanguage());
				currentASTValue = currentASTAttribute.getNextSibling();
				if (currentAttribute == null) {
					throw new JOBDBCException("Attribute " + currentASTAttribute.getText()
							+ " is not defined on the entity " + entity.getName(getSession().getReferenceLanguage()));
				}
				String lg = ((IdentNode) currentASTAttribute).getLgCode();
				Class[] parameterTypes = null;
				Object[] arguments = null;
				EntityDatatype range = currentAttribute.getRange();
				String value = range.ontoQLToValue(currentASTValue.getText());
				if (lg == null) {

					if (range.isAssociationType()) {
						EntityDatatypeCategory rangeAssociation = (EntityDatatypeCategory) range;
						Entity rangeEntity = (Entity) rangeAssociation.getCagetory();
						// Get the ontoApi class corresponding to this
						// entity
						Class rangeEntityClass = rangeEntity.getDelegateEntity().getInternalAPIClass();
						Object rangeInstance = getWalker().getFactoryEntity().instantiate(rangeEntityClass, value);
						parameterTypes = new Class[] { rangeEntityClass };
						arguments = new Object[] { rangeInstance };
					} else {
						parameterTypes = new Class[] { currentAttribute.getRange().getReturnedClass() };
						arguments = new Object[] { value };
					}

				} else {
					parameterTypes = new Class[] { String.class, String.class };
					arguments = new Object[] { value, lg };
				}
				String nameAttribute = currentAttribute.getName();
				String firstLetter = nameAttribute.substring(0, 1).toUpperCase();
				String nameSetter = "set" + firstLetter + nameAttribute.substring(1);
				try {
					Method attributeMethod = mappedClass.getMethod(nameSetter, parameterTypes);
					attributeMethod.invoke(instance, arguments);
				} catch (NoSuchMethodException e) {
					attributesNonHandled.add(OntoQLHelper.PREFIX_ONTOLOGYMODEL_ELEMENT + nameAttribute);
					attributesValuesNonHandled.add(value);
				}

				currentAttributeAssignment = currentAttributeAssignment.getNextSibling();

			}

		} catch (IllegalAccessException e) {
			throw new JOBDBCException(e);
		} catch (InvocationTargetException e) {
			throw new JOBDBCException(e);
		}
	}
}
