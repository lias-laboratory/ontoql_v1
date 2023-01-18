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
package fr.ensma.lisi.ontoql.jobdbc.impl;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.jdbc.Work;
import org.hibernate.stat.SessionStatistics;
import org.hibernate.type.Type;

import fr.ensma.lisi.ontoql.cfg.OntoQLConfiguration;
import fr.ensma.lisi.ontoql.core.Entity;
import fr.ensma.lisi.ontoql.core.cache.PersistenceContext;
import fr.ensma.lisi.ontoql.core.cache.PersistenceContextImpl;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSessionFactory;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
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
import fr.ensma.lisi.ontoql.util.DefaultNameSpaceEvent;
import fr.ensma.lisi.ontoql.util.LanguageParametersListener;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;
import fr.ensma.lisi.ontoql.util.ReferenceLanguageEvent;

/**
 * Concrete implementation of a Session, and also the central, organizing
 * component of OntoQL internal implementation. As such, this class exposes two
 * interfaces; Session itself, to the application, and SessionImplementor, to
 * other components This class is not threadsafe.
 * 
 * @author Stéphane JEAN
 */
public class OntoQLSessionImpl implements OntoQLSession {

	private static final long serialVersionUID = -2039348546093154752L;

	protected List<LanguageParametersListener> refLanguageParametersListener;

	/**
	 * instantiate an instance of the class <code>OntoProperty</code> with the oid
	 * taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoProperty newOntoProperty(int oid) throws JOBDBCException {
		return new OntoProperty(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoNonDependentProperty</code>
	 * with the oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoNonDependentProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoNonDependentProperty newOntoNonDependentProperty(int oid) throws JOBDBCException {
		return new OntoNonDependentProperty(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoPrimitiveType</code> with the
	 * oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoPrimitiveType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoPrimitiveType newOntoPrimitiveType(int oid) throws JOBDBCException {
		return new OntoPrimitiveType(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoCollectionType</code> with the
	 * oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoCollectionType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoCollectionType newOntoCollectionType(int oid) throws JOBDBCException {
		return new OntoCollectionType(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoOntology</code> with the oid
	 * taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoOntology</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoOntology newOntoOntology(int oid) throws JOBDBCException {
		return new OntoOntology(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoGraphics</code> with the oid
	 * taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoGraphics</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoGraphics newOntoGraphics(int oid) throws JOBDBCException {
		return new OntoGraphics(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoRefType</code> with the oid
	 * taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoRefType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoRefType newOntoRefType(int oid) throws JOBDBCException {
		return new OntoRefType(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoContextProperty</code> with
	 * the oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoContextProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoContextProperty newOntoContextProperty(int oid) throws JOBDBCException {
		return new OntoContextProperty(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoRealMeasureType</code> with
	 * the oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoRealMeasureType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoRealMeasureType newOntoRealMeasureType(int oid) throws JOBDBCException {
		return new OntoRealMeasureType(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoIntType</code> with the oid
	 * taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoIntType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoIntType newOntoIntType(int oid) throws JOBDBCException {
		return new OntoIntType(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoDatatype</code> with the oid
	 * taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoDatatype</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoDatatype newOntoDatatype(int oid) throws JOBDBCException {
		return new OntoDatatype(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoBooleanType</code> with the
	 * oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoBooleanType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoBooleanType newOntoBooleanType(int oid) throws JOBDBCException {
		return new OntoBooleanType(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoDependentProperty</code> with
	 * the oid taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoDependentProperty</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoDependentProperty newOntoDependentProperty(int oid) throws JOBDBCException {
		return new OntoDependentProperty(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoRealType</code> with the oid
	 * taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoRealType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoRealType newOntoRealType(int oid) throws JOBDBCException {
		return new OntoRealType(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoDocument</code> with the oid
	 * taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoDocument</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoDocument newOntoDocument(int oid) throws JOBDBCException {
		return new OntoDocument(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoStringType</code> with the oid
	 * taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoStringType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoStringType newOntoStringType(int oid) throws JOBDBCException {
		return new OntoStringType(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoNumberType</code> with the oid
	 * taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoNumberType</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoNumberType newOntoNumberType(int oid) throws JOBDBCException {
		return new OntoNumberType(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoClass</code> with the oid
	 * taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoClass</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoClass newOntoClass(int oid) throws JOBDBCException {
		return new OntoClass(oid, this);
	}

	/**
	 * instantiate an instance of the class <code>OntoConcept</code> with the oid
	 * taken in parameter.
	 * 
	 * @param oid the oid of the instance to instantiate
	 * @return an instance of <code>OntoConcept</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoConcept newOntoConcept(int oid) throws JOBDBCException {
		return new OntoConcept(oid, this);
	}

	/**
	 * Create an instance of the OntoAPI class corresponding to this entity
	 * 
	 * @param oid    identifier of the instance to load
	 * @param entity a given entity
	 * @return an instance of a sublcass of OntoRoot
	 */
	public OntoRoot newOntoRoot(int oid, Entity entity) {
		OntoRoot res = null;

		try {
			Class ontoAPIClass = entity.getDelegateEntity().getOntoAPIClass();
			Class[] parameterTypes = new Class[] { int.class, OntoQLSession.class };
			Constructor cstrOntoAPIClass = ontoAPIClass.getConstructor(parameterTypes);
			Object[] parameterValues = new Object[] { new Integer(oid), this };
			res = (OntoRoot) cstrOntoAPIClass.newInstance(parameterValues);
		} catch (Exception exc) {
			throw new JOBDBCException(exc.getMessage());
		}

		return res;
	}

	/**
	 * instantiate a new instance of the class <code>OntoConcept</code> with a
	 * string taken in parameter.
	 * 
	 * @param id a string containing an identifier of the instance to instantiate
	 * @return an instance of <code>OntoConcept</code>
	 * @exception JOBDBCException if a database access error occurs
	 */
	public OntoConcept newOntoConcept(String id) throws JOBDBCException {
		OntoConcept res = this.newOntoConcept(0);
		if (id.startsWith(OntoQLHelper.PREFIX_INTERNAL_ID)) {
			id = id.substring(1);
			res.setOid(Integer.parseInt(id));
		} else if (id.startsWith(OntoQLHelper.PREFIX_EXTERNAL_ID)) {
			id = id.substring(1);
			int separatorIndex = id.indexOf('-');
			String code = id.substring(1, separatorIndex);
			String version = id.substring(separatorIndex + 1, id.length());
			res.setCode(code);
			res.setVersion(version);
		} else if (id.startsWith(OntoQLHelper.PREFIX_NAME_ID)) {
			id = id.substring(1, id.length() - 1);
			res.setName(id, referenceLanguage);
		} else {
			res.setName(id, referenceLanguage);
		}
		return res;
	}

	private static Log log = LogFactory.getLog(OntoQLSessionImpl.class);

	// Delegate
	private Session hibernateSession;

	private PersistenceContext persistenceContext;

	protected String referenceLanguage;

	protected String defaultNameSpace;

	protected Map nameSpaceAlias;

	// to be independant of hibernate
	protected Connection connection;

	protected OntoQLSessionFactory factory;

	public OntoQLSessionImpl(Connection c) {

		this.connection = c;
		this.refLanguageParametersListener = new ArrayList<LanguageParametersListener>();

		// A configuration is needed to load the ontology model
		OntoQLConfiguration config = new OntoQLConfiguration(false);
		this.factory = new OntoQLSessionFactoryImpl(null, config.getOntologyModel());
		nameSpaceAlias = new HashMap();
		this.persistenceContext = new PersistenceContextImpl(this);
		this.setDefaultNameSpace(OntoQLHelper.NO_NAMESPACE);
		this.setReferenceLanguage(OntoQLHelper.ENGLISH);
	}

	public OntoQLSessionImpl(Session hibernateSession, OntoQLSessionFactory factory) {
		this.refLanguageParametersListener = new ArrayList<LanguageParametersListener>();
		this.hibernateSession = hibernateSession;
		this.factory = factory;
		nameSpaceAlias = new HashMap();
		this.persistenceContext = new PersistenceContextImpl(this);
		this.setReferenceLanguage(OntoQLHelper.ENGLISH);
	}

	public OntoQLStatement createOntoQLStatement() {
		return new OntoQLStatementImpl(this);
	}

	/**
	 * Define an alias for a name space.
	 * 
	 * @param nameSpace
	 * @param alias     alias for the namespace
	 */
	public void setNameSpaceAlias(String nameSpace, String alias) {
		nameSpaceAlias.put(nameSpace, alias);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see fr.ensma.lisi.ontoql.jobdbc.OntoQLSession#getDefaultNameSpace()
	 */
	public String getDefaultNameSpace() {
		return defaultNameSpace;
	}

	/**
	 * @param defaultNameSpace The defaultNameSpace to set.
	 */
	public void setDefaultNameSpace(String defaultNameSpace) {
		DefaultNameSpaceEvent currentDefaultNameSpaceEvent = new DefaultNameSpaceEvent(this.defaultNameSpace,
				defaultNameSpace);

		this.defaultNameSpace = defaultNameSpace;

		for (LanguageParametersListener current : refLanguageParametersListener) {
			current.defaultNameSpacePerformed(currentDefaultNameSpaceEvent);
		}
	}

	/**
	 * @param referenceLanguage The referenceLanguage to set.
	 */
	public void setReferenceLanguage(String referenceLanguage) {
		ReferenceLanguageEvent currentReferenceLanguageEvent = new ReferenceLanguageEvent(this.referenceLanguage,
				referenceLanguage);

		this.referenceLanguage = referenceLanguage;

		for (LanguageParametersListener current : refLanguageParametersListener) {
			current.referenceLanguagePerformed(currentReferenceLanguageEvent);
		}
	}

	/**
	 * @return Returns the referenceLanguage.
	 */
	public String getReferenceLanguage() {
		return referenceLanguage;
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Transaction beginTransaction() throws HibernateException {
		return hibernateSession.beginTransaction();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void cancelQuery() throws HibernateException {
		hibernateSession.cancelQuery();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void clear() {
		hibernateSession.clear();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Connection close() throws JOBDBCException {
		if (hibernateSession != null) {
			return hibernateSession.close();
		} else {
			try {
				connection.close();
				return connection;
			} catch (SQLException oexc) {
				throw new JOBDBCException(oexc);
			}
		}
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Connection connection() throws JOBDBCException {
		if (hibernateSession == null) {
			return connection;
		}
		return hibernateSession.connection();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public boolean contains(Object arg0) {
		return hibernateSession.contains(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Criteria createCriteria(Class arg0) {
		return hibernateSession.createCriteria(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Criteria createCriteria(Class arg0, String arg1) {
		return hibernateSession.createCriteria(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Criteria createCriteria(String arg0) {
		return hibernateSession.createCriteria(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Criteria createCriteria(String arg0, String arg1) {
		return hibernateSession.createCriteria(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Query createFilter(Object arg0, String arg1) throws HibernateException {
		return hibernateSession.createFilter(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Query createQuery(String arg0) throws HibernateException {
		return hibernateSession.createQuery(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public SQLQuery createSQLQuery(String arg0) throws HibernateException {
		return hibernateSession.createSQLQuery(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Query createSQLQuery(String arg0, String arg1, Class arg2) {
		return hibernateSession.createSQLQuery(arg0, arg1, arg2);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Query createSQLQuery(String arg0, String[] arg1, Class[] arg2) {
		return hibernateSession.createSQLQuery(arg0, arg1, arg2);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void delete(Object arg0) throws HibernateException {
		hibernateSession.delete(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public int delete(String arg0) throws HibernateException {
		return hibernateSession.delete(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public int delete(String arg0, Object arg1, Type arg2) throws HibernateException {
		return hibernateSession.delete(arg0, arg1, arg2);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public int delete(String arg0, Object[] arg1, Type[] arg2) throws HibernateException {
		return hibernateSession.delete(arg0, arg1, arg2);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void disableFilter(String arg0) {
		hibernateSession.disableFilter(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Connection disconnect() throws HibernateException {
		return hibernateSession.disconnect();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Filter enableFilter(String arg0) {
		return hibernateSession.enableFilter(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return hibernateSession.equals(obj);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void evict(Object arg0) throws HibernateException {
		hibernateSession.evict(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Collection filter(Object arg0, String arg1) throws HibernateException {
		return hibernateSession.filter(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Collection filter(Object arg0, String arg1, Object arg2, Type arg3) throws HibernateException {
		return hibernateSession.filter(arg0, arg1, arg2, arg3);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Collection filter(Object arg0, String arg1, Object[] arg2, Type[] arg3) throws HibernateException {
		return hibernateSession.filter(arg0, arg1, arg2, arg3);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public List find(String arg0) throws HibernateException {
		return hibernateSession.find(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public List find(String arg0, Object arg1, Type arg2) throws HibernateException {
		return hibernateSession.find(arg0, arg1, arg2);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public List find(String arg0, Object[] arg1, Type[] arg2) throws HibernateException {
		return hibernateSession.find(arg0, arg1, arg2);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void flush() throws HibernateException {
		hibernateSession.flush();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Object get(Class arg0, Serializable arg1) throws HibernateException {
		return hibernateSession.get(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Object get(Class arg0, Serializable arg1, LockMode arg2) throws HibernateException {
		return hibernateSession.get(arg0, arg1, arg2);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Object get(String arg0, Serializable arg1) throws HibernateException {
		return hibernateSession.get(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Object get(String arg0, Serializable arg1, LockMode arg2) throws HibernateException {
		return hibernateSession.get(arg0, arg1, arg2);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public CacheMode getCacheMode() {
		return hibernateSession.getCacheMode();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public LockMode getCurrentLockMode(Object arg0) throws HibernateException {
		return hibernateSession.getCurrentLockMode(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Filter getEnabledFilter(String arg0) {
		return hibernateSession.getEnabledFilter(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public EntityMode getEntityMode() {
		return hibernateSession.getEntityMode();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public String getEntityName(Object arg0) throws HibernateException {
		return hibernateSession.getEntityName(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public FlushMode getFlushMode() {
		return hibernateSession.getFlushMode();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Serializable getIdentifier(Object arg0) throws HibernateException {
		return hibernateSession.getIdentifier(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Query getNamedQuery(String arg0) throws HibernateException {
		return hibernateSession.getNamedQuery(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public org.hibernate.Session getSession(EntityMode arg0) {
		return hibernateSession.getSession(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public SessionFactory getSessionFactory() {
		return factory;
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public SessionStatistics getStatistics() {
		return hibernateSession.getStatistics();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return hibernateSession.hashCode();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public boolean isConnected() {
		return hibernateSession.isConnected();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public boolean isDirty() throws HibernateException {
		return hibernateSession.isDirty();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public boolean isOpen() {
		return hibernateSession.isOpen();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Iterator iterate(String arg0) throws HibernateException {
		return hibernateSession.iterate(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Iterator iterate(String arg0, Object arg1, Type arg2) throws HibernateException {
		return hibernateSession.iterate(arg0, arg1, arg2);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Iterator iterate(String arg0, Object[] arg1, Type[] arg2) throws HibernateException {
		return hibernateSession.iterate(arg0, arg1, arg2);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Object load(Class c, Serializable oid) throws HibernateException {
		return hibernateSession.load(c, oid);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Object load(Class arg0, Serializable arg1, LockMode arg2) throws HibernateException {
		return hibernateSession.load(arg0, arg1, arg2);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void load(Object arg0, Serializable arg1) throws HibernateException {
		hibernateSession.load(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Object load(String arg0, Serializable arg1) throws HibernateException {
		return hibernateSession.load(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Object load(String arg0, Serializable arg1, LockMode arg2) throws HibernateException {
		return hibernateSession.load(arg0, arg1, arg2);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void lock(Object arg0, LockMode arg1) throws HibernateException {
		hibernateSession.lock(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void lock(String arg0, Object arg1, LockMode arg2) throws HibernateException {
		hibernateSession.lock(arg0, arg1, arg2);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Object merge(Object arg0) throws HibernateException {
		return hibernateSession.merge(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Object merge(String arg0, Object arg1) throws HibernateException {
		return hibernateSession.merge(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void persist(Object arg0) throws HibernateException {
		hibernateSession.persist(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void persist(String arg0, Object arg1) throws HibernateException {
		hibernateSession.persist(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void reconnect() throws HibernateException {
		hibernateSession.reconnect();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void reconnect(Connection arg0) throws HibernateException {
		hibernateSession.reconnect(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void refresh(Object arg0) throws HibernateException {
		hibernateSession.refresh(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void refresh(Object arg0, LockMode arg1) throws HibernateException {
		hibernateSession.refresh(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void replicate(Object arg0, ReplicationMode arg1) throws HibernateException {
		hibernateSession.replicate(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void replicate(String arg0, Object arg1, ReplicationMode arg2) throws HibernateException {
		hibernateSession.replicate(arg0, arg1, arg2);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Serializable save(Object arg0) throws HibernateException {
		return hibernateSession.save(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void save(Object arg0, Serializable arg1) throws HibernateException {
		hibernateSession.save(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Serializable save(String arg0, Object arg1) throws HibernateException {
		return hibernateSession.save(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void save(String arg0, Object arg1, Serializable arg2) throws HibernateException {
		hibernateSession.save(arg0, arg1, arg2);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void saveOrUpdate(Object arg0) throws HibernateException {
		hibernateSession.saveOrUpdate(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void saveOrUpdate(String arg0, Object arg1) throws HibernateException {
		hibernateSession.saveOrUpdate(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Object saveOrUpdateCopy(Object arg0) throws HibernateException {
		return hibernateSession.saveOrUpdateCopy(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Object saveOrUpdateCopy(Object arg0, Serializable arg1) throws HibernateException {
		return hibernateSession.saveOrUpdateCopy(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Object saveOrUpdateCopy(String arg0, Object arg1) throws HibernateException {
		return hibernateSession.saveOrUpdateCopy(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Object saveOrUpdateCopy(String arg0, Object arg1, Serializable arg2) throws HibernateException {
		return hibernateSession.saveOrUpdateCopy(arg0, arg1, arg2);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void setCacheMode(CacheMode arg0) {
		hibernateSession.setCacheMode(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void setFlushMode(FlushMode arg0) {
		hibernateSession.setFlushMode(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return hibernateSession.toString();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void update(Object arg0) throws HibernateException {
		hibernateSession.update(arg0);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void update(Object arg0, Serializable arg1) throws HibernateException {
		hibernateSession.update(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void update(String arg0, Object arg1) throws HibernateException {
		hibernateSession.update(arg0, arg1);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void update(String arg0, Object arg1, Serializable arg2) throws HibernateException {
		hibernateSession.update(arg0, arg1, arg2);
	}

	/**
	 * @return Returns the persistenceContext.
	 */
	public PersistenceContext getPersistenceContext() {
		return persistenceContext;
	}

	@Override
	public void addLanguageParametersListener(LanguageParametersListener p) {
		refLanguageParametersListener.add(p);
	}

	@Override
	public void removeLanguageParametersListener(LanguageParametersListener p) {
		if (p != null) {
			refLanguageParametersListener.remove(p);
		}
	}

	@Override
	public void delete(String entityName, Object object) throws HibernateException {
		// TODO Auto-generated method stub

	}

	@Override
	public Transaction getTransaction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setReadOnly(Object entity, boolean readOnly) {
		// TODO Auto-generated method stub

	}

	@Override
	public void doWork(Work work) throws HibernateException {
		// TODO Auto-generated method stub

	}
}
