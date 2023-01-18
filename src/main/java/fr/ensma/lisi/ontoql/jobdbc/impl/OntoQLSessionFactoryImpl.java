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
import java.sql.Connection;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingException;
import javax.naming.Reference;

import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.classic.Session;
import org.hibernate.engine.FilterDefinition;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.stat.Statistics;

import antlr.SemanticException;
import fr.ensma.lisi.ontoql.cfg.OntologyModel;
import fr.ensma.lisi.ontoql.cfg.dialect.Dialect;
import fr.ensma.lisi.ontoql.cfg.dialect.function.SQLFunction;
import fr.ensma.lisi.ontoql.exception.JOBDBCException;
import fr.ensma.lisi.ontoql.exception.MappingException;
import fr.ensma.lisi.ontoql.exception.QuerySyntaxException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSessionFactory;
import fr.ensma.lisi.ontoql.ontomodel.OntoAttribute;
import fr.ensma.lisi.ontoql.ontomodel.OntoEntity;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Stéphane JEAN
 */
public class OntoQLSessionFactoryImpl implements OntoQLSessionFactory {

	private static final long serialVersionUID = 365648489128303033L;

	private OntologyModel entitiesCoreModel;

	/**
	 * The SQL <tt>Dialect</tt>. Postgresql for the moment
	 */
	private Dialect dialect = new Dialect();

	/**
	 * add an entity of the core model to the mapping.
	 * 
	 * @param name name of an entity
	 * @return the mapping of an entity of the core model
	 */
	public void addEntityOntologyModel(OntoEntity entity) {
		entitiesCoreModel.addEntity(entity);
	}

	/**
	 * remove an entity from the core model to the mapping.
	 * 
	 * @param name name of an entity
	 * @return the mapping of an entity of the core model
	 */
	public void removeEntityOntologyModel(OntoEntity entity) {
		entitiesCoreModel.removeEntity(entity);
	}

	/**
	 * remove an attribute of an entity from the ontology model used.
	 */
	public void removeAttributeFromEntityOntologyModel(OntoEntity entity, OntoAttribute attribute) {
		entitiesCoreModel.removeAttributeFromEntity(entity, attribute);
	}

	/**
	 * Locate a registered sql function by name.
	 *
	 * @param functionName The name of the function to locate
	 * @return The sql function, or null if not found.
	 */
	public SQLFunction findSQLFunction(String functionName) {
		return (SQLFunction) getDialect().getFunctions().get(functionName.toLowerCase());
	}

	/**
	 * Get the SQL <tt>Dialect</tt>
	 */
	public Dialect getDialect() {
		return dialect;
	}

	@Override
	public OntoEntity getEntityOntologyModel(String name, String lg) {

		OntoEntity ep;
		try {
			ep = (OntoEntity) entitiesCoreModel.getEntity(name, lg);
			if (ep == null) {
				ep = (OntoEntity) entitiesCoreModel.getEntity(name, OntoQLHelper.getOtherLanguage(lg));
				if (ep == null) {
					throw new QuerySyntaxException(new SemanticException(name + " is not defined in the mapping."));
				}
			}
		} catch (MappingException e) {
			throw new JOBDBCException(e.getMessage(), e);
		}
		return ep;

	}

	public boolean existEntityInOntologyModel(String name) {
		boolean res = false;
		OntoEntity ep = (OntoEntity) entitiesCoreModel.getEntity(name, OntoQLHelper.ENGLISH);
		res = (ep != null);
		if (!res) {
			ep = (OntoEntity) entitiesCoreModel.getEntity(name, OntoQLHelper.FRENCH);
			res = (ep != null);
		}
		return res;
	}

	/**
	 * @param delegate
	 */
	public OntoQLSessionFactoryImpl(SessionFactory delegate, OntologyModel ontologymodel) {
		super();
		this.delegate = delegate;
		this.entitiesCoreModel = ontologymodel;

	}

	protected SessionFactory delegate;

	public void close() throws HibernateException {
		delegate.close();
	}

	@Override
	public boolean equals(Object obj) {
		return delegate.equals(obj);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void evict(Class persistentClass) throws HibernateException {
		delegate.evict(persistentClass);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void evict(Class persistentClass, Serializable id) throws HibernateException {
		delegate.evict(persistentClass, id);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void evictCollection(String roleName) throws HibernateException {
		delegate.evictCollection(roleName);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void evictCollection(String roleName, Serializable id) throws HibernateException {
		delegate.evictCollection(roleName, id);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void evictEntity(String entityName) throws HibernateException {
		delegate.evictEntity(entityName);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void evictEntity(String entityName, Serializable id) throws HibernateException {
		delegate.evictEntity(entityName, id);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void evictQueries() throws HibernateException {
		delegate.evictQueries();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public void evictQueries(String cacheRegion) throws HibernateException {
		delegate.evictQueries(cacheRegion);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Map getAllClassMetadata() throws HibernateException {
		return delegate.getAllClassMetadata();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Map getAllCollectionMetadata() throws HibernateException {
		return delegate.getAllCollectionMetadata();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
		return delegate.getClassMetadata(persistentClass);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
		return delegate.getClassMetadata(entityName);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
		return delegate.getCollectionMetadata(roleName);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Session getCurrentSession() throws HibernateException {
		return delegate.getCurrentSession();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Reference getReference() throws NamingException {
		return delegate.getReference();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Statistics getStatistics() {
		return delegate.getStatistics();
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public boolean isClosed() {
		return delegate.isClosed();
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Session openSession() throws HibernateException {
		OntoQLSessionImpl res = new OntoQLSessionImpl(delegate.openSession(), this);
		res.factory = this;
		return res;
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Session openSession(Connection connection) {
		return delegate.openSession(connection);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Session openSession(Connection connection, Interceptor interceptor) {
		return delegate.openSession(connection, interceptor);
	}

	/**
	 * 
	 * @param
	 * @return Require : Ensure :
	 */
	public Session openSession(Interceptor interceptor) throws HibernateException {
		return delegate.openSession(interceptor);
	}

	@Override
	public String toString() {
		return delegate.toString();
	}

	@Override
	public StatelessSession openStatelessSession() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StatelessSession openStatelessSession(Connection connection) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set getDefinedFilterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
		// TODO Auto-generated method stub
		return null;
	}
}
