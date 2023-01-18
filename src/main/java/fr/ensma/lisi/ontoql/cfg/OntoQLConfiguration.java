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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.xml.sax.InputSource;

import fr.ensma.lisi.ontoql.exception.MappingException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSessionFactory;
import fr.ensma.lisi.ontoql.jobdbc.impl.OntoQLSessionFactoryImpl;
import fr.ensma.lisi.ontoql.util.XMLHelper;

/**
 * OntoQL configuration Extends Configuration provided by hibernate using
 * delegate Design Pattern.
 * 
 * @author Stephane JEAN
 */
public class OntoQLConfiguration {

	/**
	 * A logger for this class.
	 */
	private static Log log = LogFactory.getLog(OntoQLConfiguration.class);

	public static final String FILE_ONTOLOGY_MODEL = "ontology_model.xml";

	/**
	 * A delegate hibernate Configuration.
	 */
	private Configuration delegate;

	/**
	 * Helper for manipulation XML data.
	 */
	private transient XMLHelper xmlHelper = new XMLHelper();

	private transient OntologyModel ontologyModel = new OntologyModelImpl();

	/**
	 * Default constructor : init the delegate.
	 */
	public OntoQLConfiguration() {
		this(true);
		delegate = new Configuration();
	}

	public void setProperty(String propertyName, String value) {
		delegate.setProperty(propertyName, value);
	}

	/**
	 * 
	 * @param initDelegate True if the delegate must be initialized
	 */
	public OntoQLConfiguration(boolean initDelegate) {
		if (initDelegate) {
			delegate = new Configuration();
		}
		loadCoreOntologyModel();

	}

	/**
	 * Derived the buildSessionFactory to return an OntoQLSessionFactory.
	 * 
	 * @return An OntoQLSessionFactory
	 */
	public final OntoQLSessionFactory buildSessionFactory() {
		log.debug("Build an OntoQLSessionFactory");
		SessionFactory hibernateSessionFactory = delegate.buildSessionFactory();
		return new OntoQLSessionFactoryImpl(hibernateSessionFactory, ontologyModel);
	}

	public Configuration addResource(String path) throws MappingException {
		return delegate.addResource(path);
	}

	/**
	 * Read mappings from an application resource trying different classloaders.
	 * This method will try to load the resource first from the thread context
	 * classloader and then from the classloader that loaded Hibernate.
	 */
	public void loadCoreOntologyModel() throws MappingException {
		log.info("Mapping resource: " + FILE_ONTOLOGY_MODEL);
		InputStream rsrc = Thread.currentThread().getContextClassLoader().getResourceAsStream(FILE_ONTOLOGY_MODEL);
		if (rsrc == null)
			rsrc = Environment.class.getClassLoader().getResourceAsStream(FILE_ONTOLOGY_MODEL);
		if (rsrc == null)
			throw new MappingException("Resource: " + FILE_ONTOLOGY_MODEL + " not found");
		try {
			addInputStream(rsrc);
		} catch (MappingException me) {
			throw new MappingException("Error reading resource: " + FILE_ONTOLOGY_MODEL, me);
		}
	}

	/**
	 * Read the ontology model from an <tt>InputStream</tt>
	 * 
	 * @param xmlInputStream an <tt>InputStream</tt> containing the ontology model
	 */
	public void addInputStream(InputStream xmlInputStream) throws MappingException {
		try {
			List errors = new ArrayList();
			org.dom4j.Document doc = xmlHelper
					.createSAXReader("XML InputStream", errors, XMLHelper.DEFAULT_DTD_RESOLVER)
					.read(new InputSource(xmlInputStream));
			if (errors.size() != 0)
				throw new MappingException("invalid mapping", (Throwable) errors.get(0));
			add(doc);
		} catch (MappingException me) {
			throw me;
		} catch (Exception e) {
			log.error("Could not configure datastore from input stream", e);
			throw new MappingException(e);
		} finally {
			try {
				xmlInputStream.close();
			} catch (IOException ioe) {
				log.error("could not close input stream", ioe);
			}
		}
	}

	protected void add(org.dom4j.Document doc) throws MappingException {
		try {
			XMLBinder.bindRoot(doc, ontologyModel);
		} catch (MappingException me) {
			log.error("Could not compile the mapping document", me);
			throw me;
		}
	}

	public OntologyModel getOntologyModel() {
		return ontologyModel;
	}
}
