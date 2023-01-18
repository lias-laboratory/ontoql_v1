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
package fr.ensma.lisi.ontoql.jobdbc;

import org.hibernate.classic.Session;

import fr.ensma.lisi.ontoql.core.Entity;
import fr.ensma.lisi.ontoql.core.cache.PersistenceContext;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.ontoapi.OntoBooleanType;
import fr.ensma.lisi.ontoql.ontoapi.OntoClass;
import fr.ensma.lisi.ontoql.ontoapi.OntoCollectionType;
import fr.ensma.lisi.ontoql.ontoapi.OntoConcept;
import fr.ensma.lisi.ontoql.ontoapi.OntoContextProperty;
import fr.ensma.lisi.ontoql.ontoapi.OntoDatatype;
import fr.ensma.lisi.ontoql.ontoapi.OntoDependentProperty;
import fr.ensma.lisi.ontoql.ontoapi.OntoDocument;
import fr.ensma.lisi.ontoql.ontoapi.OntoGraphics;
import fr.ensma.lisi.ontoql.ontoapi.OntoIntType;
import fr.ensma.lisi.ontoql.ontoapi.OntoNonDependentProperty;
import fr.ensma.lisi.ontoql.ontoapi.OntoNumberType;
import fr.ensma.lisi.ontoql.ontoapi.OntoOntology;
import fr.ensma.lisi.ontoql.ontoapi.OntoPrimitiveType;
import fr.ensma.lisi.ontoql.ontoapi.OntoProperty;
import fr.ensma.lisi.ontoql.ontoapi.OntoRealMeasureType;
import fr.ensma.lisi.ontoql.ontoapi.OntoRealType;
import fr.ensma.lisi.ontoql.ontoapi.OntoRefType;
import fr.ensma.lisi.ontoql.ontoapi.OntoRoot;
import fr.ensma.lisi.ontoql.ontoapi.OntoStringType;
import fr.ensma.lisi.ontoql.util.LanguageParametersListener;

/**
 * Interface for executing OntoQL commands.
 * 
 * @author Stéphane JEAN
 */
public interface OntoQLSession extends Session {

	/**
	 * instantiate a new instance of the class <code>OntoProperty</code> with the
	 * oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoProperty newOntoProperty(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoNonDependentProperty</code>
	 * with the oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoNonDependentProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoNonDependentProperty newOntoNonDependentProperty(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoPrimitiveType</code> with
	 * the oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoPrimitiveType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoPrimitiveType newOntoPrimitiveType(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoCollectionType</code> with
	 * the oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoCollectionType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoCollectionType newOntoCollectionType(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoOntology</code> with the
	 * oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoOntology</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoOntology newOntoOntology(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoGraphics</code> with the
	 * oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoGraphics</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoGraphics newOntoGraphics(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoRefType</code> with the oid
	 * taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoRefType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoRefType newOntoRefType(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoContextProperty</code> with
	 * the oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoContextProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoContextProperty newOntoContextProperty(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoRealMeasureType</code> with
	 * the oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoRealMeasureType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoRealMeasureType newOntoRealMeasureType(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoIntType</code> with the oid
	 * taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoIntType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoIntType newOntoIntType(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoDatatype</code> with the
	 * oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoDatatype</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoDatatype newOntoDatatype(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoBooleanType</code> with the
	 * oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoBooleanType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoBooleanType newOntoBooleanType(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoDependentProperty</code>
	 * with the oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoDependentProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoDependentProperty newOntoDependentProperty(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoRealType</code> with the
	 * oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoRealType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoRealType newOntoRealType(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoDocument</code> with the
	 * oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoDocument</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoDocument newOntoDocument(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoStringType</code> with the
	 * oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoStringType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoStringType newOntoStringType(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoNumberType</code> with the
	 * oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoNumberType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoNumberType newOntoNumberType(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoClass</code> with the oid
	 * taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoClass</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoClass newOntoClass(int oid) throws JOBDBCException;

	/**
	 * instantiate a new instance of the class <code>OntoConcept</code> with the oid
	 * taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoConcept</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoConcept newOntoConcept(int oid) throws JOBDBCException;

	/**
	 * Create an instance of the OntoAPI class corresponding to this entity
	 * 
	 * @param oid    identifier of the instance to load
	 * @param entity a given entity
	 * @return an instance of a sublcass of OntoRoot
	 */
	OntoRoot newOntoRoot(int oid, Entity entity);

	/**
	 * instantiate a new instance of the class <code>OntoConcept</code> with a
	 * string taken in parameter.
	 * 
	 * @param id a string containing an identifier of the instance to instantiate
	 * @return an instance of <code>OntoConcept</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	OntoConcept newOntoConcept(String id) throws JOBDBCException;

	/**
	 * Create a statement for executing DDL or DML operations.
	 * 
	 * @return a statement for executing DDL or DML operations.
	 */
	OntoQLStatement createOntoQLStatement();

	/**
	 * Define an alias for a name space.
	 * 
	 * @param nameSpace the given name space
	 * @param alias     alias for the namespace
	 */
	void setNameSpaceAlias(String nameSpace, String alias);

	/**
	 * Get the default namespace used by this session
	 * 
	 * @return the default namespace used by this session
	 */
	String getDefaultNameSpace();

	/**
	 * @param defaultNameSpace The defaultNameSpace to set.
	 */
	void setDefaultNameSpace(String defaultNameSpace);

	/**
	 * @param referenceLanguage The referenceLanguage to set.
	 */
	void setReferenceLanguage(String referenceLanguage);

	/**
	 * @return Returns the referenceLanguage.
	 */
	String getReferenceLanguage();

	/**
	 * Get the persistence context for this session.
	 * 
	 * @return the persistence context for this session
	 */
	PersistenceContext getPersistenceContext();

	/**
	 * Adds an <code>LanguageParametersListener</code> to the
	 * <code>OntoQLSession</code>.
	 * 
	 * @param p the <code>LanguageParametersListener</code> to be added
	 */
	void addLanguageParametersListener(LanguageParametersListener p);

	/**
	 * Removes an <code>LanguageParametersListener</code> from the
	 * <code>OntoQLSession</code>.
	 * 
	 * @param p the listener to be removed
	 */
	void removeLanguageParametersListener(LanguageParametersListener p);

}
