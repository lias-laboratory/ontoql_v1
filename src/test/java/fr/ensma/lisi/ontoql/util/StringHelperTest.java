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

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Stéphane JEAN
 */
public class StringHelperTest {

	@Test
	public void testFormatSQL() {
		String inputQuery = "(select g1067x0_.p1204, g1067x0_.p1202 from e1067 g1067x0_) union all (select g1023x0_.p1204, g1023x0_.p1202 from e1023 g1023x0_) union all (select g1062x0_.p1204, g1062x0_.p1202 from e1062 g1062x0_) union all (select g1063x0_.p1204, g1063x0_.p1202 from e1063 g1063x0_) union all (select g1061x0_.p1204, g1061x0_.p1202 from e1061 g1061x0_) union all (select g1060x0_.p1204, g1060x0_.p1202 from e1060 g1060x0_)";
		String outputQueryExpected = "(select g1067x0_.p1204, g1067x0_.p1202 \nfrom e1067 g1067x0_) \nunion all \n(select g1023x0_.p1204, g1023x0_.p1202 \nfrom e1023 g1023x0_) \nunion all \n(select g1062x0_.p1204, g1062x0_.p1202 \nfrom e1062 g1062x0_) \nunion all \n(select g1063x0_.p1204, g1063x0_.p1202 \nfrom e1063 g1063x0_) \nunion all \n(select g1061x0_.p1204, g1061x0_.p1202 \nfrom e1061 g1061x0_) \nunion all \n(select g1060x0_.p1204, g1060x0_.p1202 \nfrom e1060 g1060x0_)";
		Assert.assertEquals(outputQueryExpected, StringHelper.formatSQL(inputQuery));
	}
}
