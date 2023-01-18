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
package fr.ensma.lisi.ontoql;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import fr.ensma.lisi.ontoql.annotation.AllAnnotationTests;
import fr.ensma.lisi.ontoql.engine.AllEngineTests;
import fr.ensma.lisi.ontoql.gke.AllGKETests;
import fr.ensma.lisi.ontoql.internalapi.AllInternalAPITests;
import fr.ensma.lisi.ontoql.jobdbc.AllJOBDBCTests;
import fr.ensma.lisi.ontoql.ontoapi.AllOntoAPITests;
import fr.ensma.lisi.ontoql.ontoqbe.AllOntoQBETests;
import fr.ensma.lisi.ontoql.preference.AllPreferenceTests;
import fr.ensma.lisi.ontoql.sparql.AllSPARQLTests;
import fr.ensma.lisi.ontoql.util.AllUtilTests;

/**
 * @author Stéphane JEAN
 */
@RunWith(Suite.class)
@SuiteClasses(value = { AllAnnotationTests.class, AllEngineTests.class,
//	AllEWokHubests.class,
		AllGKETests.class, AllInternalAPITests.class, AllJOBDBCTests.class, AllOntoAPITests.class,
		AllOntoQBETests.class, AllPreferenceTests.class, AllSPARQLTests.class, AllUtilTests.class })
public class AllTests {

}
