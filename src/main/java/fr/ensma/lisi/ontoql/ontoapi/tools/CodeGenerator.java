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
package fr.ensma.lisi.ontoql.ontoapi.tools;

import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.ensma.lisi.ontoql.cfg.OntoQLConfiguration;
import fr.ensma.lisi.ontoql.cfg.OntologyModel;
import fr.ensma.lisi.ontoql.ontomodel.OntoEntity;

/**
 * This is a main class that generate the Java class for each entities described
 * in the file ontology_model.xml. A getter method is also added for them in the
 * interface OntoQLResultSet. Moreover a loader method is generated in OntoQL
 * session to allow the loading of a persistent instance of this entity.
 * 
 * @author Stéphane Jean
 */
public class CodeGenerator {

	private static final Log log = LogFactory.getLog(CodeGenerator.class);

	/**
	 * Main method that process the generation of code
	 * 
	 * @param args no arguments are required
	 */
	public static void main(String[] args) {

		try {
			// The entities of the file ontology_model.xml are loaded
			OntoQLConfiguration cfg = new OntoQLConfiguration();
			OntologyModel ontologyModel = cfg.getOntologyModel();
			Collection entities = ontologyModel.getEntities();
			Iterator iterator = entities.iterator();

			// A generator of code is needed
			Generator g = new Generator();

			// for each entity
			OntoEntity currentEntity = null;
			while (iterator.hasNext()) {
				currentEntity = (OntoEntity) iterator.next();
				// generate its JAVA file
				// g.generateJavaClassForEntity(currentEntity,ontologyModel);
				// generate a getter method in OntoQLResultSet
				// g.appendGetterInResultSetForEntity(currentEntity,ontologyModel);
				// generate a loader method in OntoQLSession
				// g.appendMethodsInSessionForEntity(currentEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
