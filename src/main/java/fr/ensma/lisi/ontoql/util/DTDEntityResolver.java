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
package fr.ensma.lisi.ontoql.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * @author Stéphane JEAN
 */
public class DTDEntityResolver implements EntityResolver, Serializable {

	private static final long serialVersionUID = 6818960626228762991L;

	private static final Log log = LogFactory.getLog(DTDEntityResolver.class);

	private static final String URL = "http://hibernate.sourceforge.net/";

	private transient ClassLoader resourceLoader;

	/**
	 * Default constructor using DTDEntityResolver classloader for resource loading.
	 */
	public DTDEntityResolver() {
		// backward compatibility
		resourceLoader = this.getClass().getClassLoader();
	}

	/**
	 * Set the class loader used to load resouces
	 * 
	 * @param resourceLoader class loader to use
	 */
	public DTDEntityResolver(ClassLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public InputSource resolveEntity(String publicId, String systemId) {
		if (systemId != null && systemId.startsWith(URL)) {
			log.debug("trying to locate " + systemId + " in classpath under org/hibernate/");
			// Search for DTD
			String path = "org/hibernate/" + systemId.substring(URL.length());
			InputStream dtdStream = resourceLoader == null ? getClass().getResourceAsStream(path)
					: resourceLoader.getResourceAsStream(path);
			if (dtdStream == null) {
				log.debug(systemId + " not found in classpath");
				if (systemId.substring(URL.length()).indexOf("2.0") > -1) {
					log.error("Don't use old DTDs, read the Hibernate 3.x Migration Guide!");
				}
				return null;
			} else {
				log.debug("found " + systemId + " in classpath");
				InputSource source = new InputSource(dtdStream);
				source.setPublicId(publicId);
				source.setSystemId(systemId);
				return source;
			}
		} else {
			// use the default behaviour
			return null;
		}
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		/** to allow serialization of configuration */
		ois.defaultReadObject();
		this.resourceLoader = this.getClass().getClassLoader();
	}
}