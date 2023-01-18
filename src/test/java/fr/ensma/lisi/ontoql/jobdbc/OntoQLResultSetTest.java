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

import org.junit.Assert;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.ontoapi.Instance;

/**
 * @author Stéphane JEAN
 */
public class OntoQLResultSetTest extends OntoQLTestCase {

	/**
	 * An OntoQLResultSet is upward compatible with ResultSet This method tests this
	 * property
	 */
	@Test
	public void testUpwardCompatibility() throws Exception {
		OntoQLSession s = getSession();

		s.setReferenceLanguage("en");
		String toParse = "SELECT virage, Size FROM HUDSON order by Size asc";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSet = statement.executeQuery(toParse);
		resultSet.next();
		Assert.assertEquals(resultSet.getInt(1), 3);
		Assert.assertEquals("L", resultSet.getString(2));
		resultSet.next();
		Assert.assertEquals(resultSet.getInt(1), 1);
		Assert.assertEquals(resultSet.getString(2), "XL");
		Assert.assertFalse(resultSet.next());

		toParse = "SELECT Reference, Size, its_muff, virage \n FROM HUDSON order by Size desc";
		resultSet = statement.executeQuery(toParse);
		resultSet.next();
		Assert.assertEquals("300062", resultSet.getString(1));
		Assert.assertEquals("XL", resultSet.getString(2));
		Assert.assertEquals("423", resultSet.getString(3));
		Assert.assertEquals("1", resultSet.getString(4));
		resultSet.next();
		Assert.assertEquals("300061", resultSet.getString(1));
		Assert.assertEquals("L", resultSet.getString(2));
		Assert.assertEquals("423", resultSet.getString(3));
		Assert.assertEquals("3", resultSet.getString(4));

		toParse = "SELECT * FROM HUDSON order by Size desc";
		resultSet = statement.executeQuery(toParse);
		resultSet.next();
		Assert.assertEquals("100", resultSet.getString(1));

		Instance instanceMuffs = resultSet.getInstance(2);
		Assert.assertEquals("423", instanceMuffs.getOid());
		Assert.assertEquals("1", resultSet.getString(3));
		Assert.assertEquals("XL", resultSet.getString(4));
		resultSet.next();
		Assert.assertEquals("101", resultSet.getString(1));
		instanceMuffs = resultSet.getInstance(2);
		Assert.assertEquals("423", instanceMuffs.getOid());
		Instance instanceSlalom = instanceMuffs.getInstancePropertyValue("its_slalom");
		Assert.assertEquals("164", instanceSlalom.getOid());
		Assert.assertEquals("3", resultSet.getString(3));
		Assert.assertEquals("L", resultSet.getString(4));
		Assert.assertEquals("300061", resultSet.getString(5));

		s.close();
	}

	/**
	 * An OntoQLResultSet extends ResultSet providing methods to get instance of
	 * class (getInstance) This method tests this method
	 */
	@Test
	public void testGetInstance() throws Exception {
		OntoQLSession s = getSession();

		s.setReferenceLanguage("en");

		// A simple query
		String toParse = "SELECT h FROM HUDSON h order by Size asc";
		OntoQLStatement statement = s.createOntoQLStatement();
		OntoQLResultSet resultSet = statement.executeQuery(toParse);
		resultSet.next();
		Instance instanceHudson = resultSet.getInstance(1);
		Assert.assertEquals(instanceHudson.getOid(), "101");
		Assert.assertEquals((int) instanceHudson.getIntPropertyValue("virage"), 3);
		Assert.assertEquals(instanceHudson.getStringPropertyValue("Size"), "L");
		Instance instanceMuff = instanceHudson.getInstancePropertyValue("its_muff");
		Assert.assertFalse((instanceMuff).isLoaded());
		resultSet.next();
		instanceHudson = resultSet.getInstance(1);
		Assert.assertEquals(instanceHudson.getOid(), "100");
		Assert.assertEquals((int) instanceHudson.getIntPropertyValue("virage"), 1);
		Assert.assertEquals(instanceHudson.getStringPropertyValue("Size"), "XL");

		// A more complex query
		toParse = "SELECT Reference, h, Size, h.its_muff.Reference FROM HUDSON h order by Size asc";
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery(toParse);
		resultSet.next();
		instanceHudson = resultSet.getInstance(2);
		Assert.assertEquals(instanceHudson.getOid(), "101");
		Assert.assertEquals(3, (int) instanceHudson.getIntPropertyValue("virage"));
		Assert.assertEquals(instanceHudson.getStringPropertyValue("Size"), "L");

		resultSet.next();
		instanceHudson = resultSet.getInstance(2);
		Assert.assertEquals(instanceHudson.getOid(), "100");
		Assert.assertEquals(1, (int) instanceHudson.getIntPropertyValue("virage"));
		Assert.assertEquals("XL", instanceHudson.getStringPropertyValue("Size"));

		// A test with fetch loading
		toParse = "SELECT h, h.its_muff FROM HUDSON h order by Size asc";
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery(toParse);
		resultSet.next();
		instanceHudson = resultSet.getInstance(1);
		Assert.assertEquals(instanceHudson.getOid(), "101");
		Assert.assertEquals(3, (int) instanceHudson.getIntPropertyValue("virage"));
		Assert.assertEquals(instanceHudson.getStringPropertyValue("Size"), "L");
		instanceMuff = instanceHudson.getInstancePropertyValue("its_muff");
		Assert.assertTrue((instanceMuff).isLoaded());

		// A test with polymorphism
		s.getPersistenceContext().clear();
		toParse = "select c from CAGS c where c.oid = 100"; // Retrieve an
		// instance of
		// Hudson (1105)
		statement = s.createOntoQLStatement();
		resultSet = statement.executeQuery(toParse);
		resultSet.next();
		instanceHudson = resultSet.getInstance(1);
		Assert.assertEquals(instanceHudson.getOid(), "100");
		EntityClass baseType = instanceHudson.getBaseType();
		Assert.assertEquals("1062", baseType.getInternalId());
		Assert.assertFalse(instanceHudson.isLoaded());
		// now get a property loaded
		Assert.assertEquals("XL", instanceHudson.getStringPropertyValue("Size"));
		Assert.assertFalse(instanceHudson.isLoaded());
		// get a property non loaded
		Assert.assertEquals(1, (int) instanceHudson.getIntPropertyValue("virage"));
		// the instance must now be loaded
		Assert.assertTrue(instanceHudson.isLoaded());
		s.close();
	}
}
