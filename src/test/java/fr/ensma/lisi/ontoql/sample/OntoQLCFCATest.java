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
package fr.ensma.lisi.ontoql.sample;

import java.sql.SQLException;

import org.hibernate.Transaction;
import org.junit.Test;

import fr.ensma.lisi.ontoql.OntoQLTestCase;
import fr.ensma.lisi.ontoql.exception.QueryException;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLStatement;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Mickael BARON
 */
public class OntoQLCFCATest extends OntoQLTestCase {

	@Test
	public void testCFCAODBASE() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		statement.executeUpdate("CREATE ENTITY #uri_Type under #stringType");

		statement.executeUpdate("INSERT INTO #ontology (#namespace) values ('http://www.cfca.fr/')");
		s.setDefaultNameSpace("http://www.cfca.fr/");
		long start = System.currentTimeMillis();

		// CFCA
		statement.executeUpdate(
				"CREATE #CLASS \"Composant CFCA\" (DESCRIPTOR (#code ='0002-41982799300025#01-1#1', #definition[fr]='Famille racine de la classification des composants utilisés par CFCA')))");
		statement.executeUpdate(
				"ALTER #CLASS \"Composant CFCA\" ADD \"Référence BE\" String DESCRIPTOR (#code = '0002-41982799300025#02-1#1', #definition[fr] = 'Référence composant interne au BE')");
		statement.executeUpdate(
				"ALTER #CLASS \"Composant CFCA\" ADD \"Référence ERP\" String DESCRIPTOR (#code = '0002-41982799300025#02-2#1', #definition[fr] = 'Référence composant connue dans l''ERP')");
		statement.executeUpdate(
				"ALTER #CLASS \"Composant CFCA\" ADD \"Fiche Technique\" URI DESCRIPTOR (#code = '0002-41982799300025#02-3#1', #definition[fr] = 'Référence externe à une description technique du composant')");
		statement.executeUpdate(
				"ALTER #CLASS \"Composant CFCA\" ADD Représentation URI DESCRIPTOR (#code = '0002-41982799300025#02-4#1',#definition[fr] = 'Représentation du composant')");
		statement.executeUpdate(
				"ALTER #CLASS \"Composant CFCA\" ADD Désignation String DESCRIPTOR (#code = '0002-41982799300025#02-5#1', #definition[fr] = 'Désignation du composant')");
		statement.executeUpdate(
				"ALTER #CLASS \"Composant CFCA\" ADD Couleur ENUM ('Blanc', 'Bleu', 'Gris', 'Jaune', 'Marron', 'Metal', 'Muti', 'Noir', 'Orange', 'Rouge', 'Transparent', 'Vert', 'Violet','Ivoire') DESCRIPTOR (#code = '0002-41982799300025#02-6#1',#definition[fr] = 'Couleur du composant')");
		statement.executeUpdate(
				"ALTER #CLASS \"Composant CFCA\" ADD Fabricant String DESCRIPTOR (#code = '0002-41982799300025#02-7#1',#definition[fr] = 'Fabricant du composant')");
		statement.executeUpdate(
				"ALTER #CLASS \"Composant CFCA\" ADD \"Référence Fabricant\" String DESCRIPTOR (#code = '0002-41982799300025#02-8#1',#definition = 'Référence fabricant du composant')");
		statement.executeUpdate(
				"ALTER #CLASS \"Composant CFCA\" ADD Normes String ARRAY DESCRIPTOR (#code = '0002-41982799300025#02-21#1', #definition[fr] = 'Normes qualifiant le conducteur')");

		// MonoConducteur
		statement.executeUpdate(
				"CREATE #CLASS MonoConducteur under \"Composant CFCA\" (DESCRIPTOR (#code = '0002-41982799300025#01-2#1', #definition[fr] = '')))");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Section MM2\" Real DESCRIPTOR (#code = '0002-41982799300025#02-9#1', #definition[fr] = 'Section en mm2 du conducteur')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Section AWG\" INT DESCRIPTOR (#code = '0002-41982799300025#02-10#1', #definition[fr] = 'Section AWG du conducteur')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Poids Cuivre\" Real DESCRIPTOR (#code = '0002-41982799300025#02-11#1', #definition[fr] = 'Poids du cuivre par mètre de fil (Kg/m)')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Diamètre Minimum Isolant\" Real DESCRIPTOR (#code = '0002-41982799300025#02-12#1',#definition[fr] = 'Diamètre minimum d''isolant (mm)')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Diamètre Maximum Isolant\" Real DESCRIPTOR (#code = '0002-41982799300025#02-13#1',#definition[fr] = 'Diamètre maximum d''isolant (mm)')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Présence Film Protecteur\" Boolean DESCRIPTOR (#code = '0002-41982799300025#02-14#1',#definition[fr] = 'Booléen, qui, si vrai, indique si un film protecteur est présent sur le conducteur')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD Conditionnement String DESCRIPTOR (#code = '0002-41982799300025#02-15#1',#definition = 'Nature du conditionnement du conducteur')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Présence Connecteur Gauche\" Boolean DESCRIPTOR (#code = '0002-41982799300025#02-16#1', #definition[fr] = 'Booléen, qui, si vrai, indique qu''un connecteur est présent sur l''extrémité gauche du conducteur')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Présence Connecteur Droit\" Boolean DESCRIPTOR (#code = '0002-41982799300025#02-17#1', #definition = 'Booléen, qui, si vrai, indique qu''un connecteur est présent sur l''extrémité droite du conducteur')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Nature Isolant\" String DESCRIPTOR (#code = '0002-41982799300025#02-20#1', #definition[fr] = 'Nature de l''isolant du conducteur')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Nombre de Brins\" Int DESCRIPTOR (#code = '0002-41982799300025#02-22#1', #definition[fr] = 'Nombre de brins du matériau du conducteur')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Diamètre des Brins\" Real DESCRIPTOR (#code = '0002-41982799300025#02-23#1', #definition[fr] = 'Diamètre des brins du conducteur (mm)')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Nature du Conducteur\" String DESCRIPTOR (#code = '0002-41982799300025#02-24#1', #definition[fr] = 'Matériau conducteur')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Resistance Lineique\" Real DESCRIPTOR (#code = '0002-41982799300025#02-25#1', #definition[fr] = 'Résistance linéique du conducteur (Ohm/Km)')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Tension Phase Terre\" Real DESCRIPTOR (#code = '0002-41982799300025#02-26#1', #definition[fr] = 'Tension entre la phase et la terre (V)')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Tension Phase Phase\" Real DESCRIPTOR (#code = '0002-41982799300025#02-26#1', #definition[fr] = 'Tension entre les phases (V)')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Température Min\" Real DESCRIPTOR (#code = '0002-41982799300025#02-18#1', #definition[fr] = 'Température minimum d''utilisation (degré Celcius)')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Température Max\" Real DESCRIPTOR (#code = '0002-41982799300025#02-19#1', #definition[fr] = 'Température maximum d''utilisation (degré Celcius)')");
		statement.executeUpdate(
				"ALTER #CLASS MonoConducteur ADD \"Type\" String DESCRIPTOR (#code = '0002-41982799300025#02-19#1', #definition[fr] = 'Type pour MultiConducteurs')");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 0.2\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-4#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 0.22\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-5#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 0.25\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-6#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 0.34\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-7#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 0.35\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-8#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 0.38\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-9#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 0.4\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-10#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 0.5\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-11#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 0.6\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-12#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 0.63\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-13#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 0.7\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-14#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 0.75\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-15#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 0.81\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-16#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 1\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-17#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 1.31\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-18#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 1.4\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-19#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 1.5\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-20#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 2\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-21#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 2.07\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-22#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 2.5\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-23#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 3\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-24#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 3.31\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-25#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 4\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-26#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 5\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-27#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 5.30\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-28#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 6\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-29#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 7\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-30#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 10\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-31#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 16\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-32#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 20\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-33#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 25\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-34#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 35\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-35#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 40\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-36#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 50\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-37#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 60\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-38#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 70\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-39#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 75\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-40#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 81\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-41#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 95\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-42#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"MonoConducteur section 120\" UNDER MonoConducteur (DESCRIPTOR (#code = '0002-41982799300025#01-43#1',#definition[fr] = ''))");

		// Cosse
		statement.executeUpdate(
				"CREATE #CLASS Cosse UNDER \"Composant CFCA\" (DESCRIPTOR (#code = '0002-41982799300025#01-100#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD \"Préconisation Sertissage\" URI DESCRIPTOR (#code = '0002-41982799300025#02-100#1',#definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD Genre ENUM ('Mâle', 'Femelle', 'Hermaphrodite','Embout', 'Ronde', 'Manchon') DESCRIPTOR (#code = '0002-41982799300025#02-101#1',#definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD Forme ENUM ('Clip Drapeau', 'Clip','Douille', 'Languette', 'Broche', 'Batterie', 'Tubulaire', 'Douille coudée', 'Ronde', 'Préisolé', 'Fourche', 'Roulé Brasé din 46234','NFC 20-130', 'DIN 46225', 'Etroite', 'Ferroviaire NFF00363', 'DIN 46235','Embout', 'boutAbout', 'Manchon', 'Splice', 'Harpon', 'Languette Préisolé','Lire', 'Clip Drapeau Préisolé', 'CLIP RETOUR LANGUETTE', 'Embout sans isolant','Clip Drapeaux', 'Douille Préisolé', 'Broche présiolé') DESCRIPTOR (#code = '0002-41982799300025#02-102#1',#definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD Fût ENUM ('Ouvert', 'Fermé') DESCRIPTOR (#code = '0002-41982799300025#02-103#1',#definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD \"Section Fil Admissible Min\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-104#1',#definition[fr] = 'Section minimum du fil admissible dans la cosse (mm2)')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD \"Section Fil Admissible Max\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-105#1',#definition[fr] = 'Section maximum du fil admissible dans la cosse (mm2)')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD \"Section Fil AWG Admissible Min\" INT DESCRIPTOR (#code = '0002-41982799300025#02-106#1',#definition[fr] = 'Section AWG minimum du fil admissible dans la cosse')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD \"Section Fil AWG Admissible Max\" INT DESCRIPTOR (#code = '0002-41982799300025#02-107#1',#definition[fr] = 'Section maximum du fil admissible dans la cosse')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD \"Longueur de Dénudage\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-108#1',#definition[fr] = 'Longueur de dénudage du fil nécessaire pour son utilisation avec la cosse (mm)')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD Matière STRING DESCRIPTOR (#code = '0002-41982799300025#02-109#1',#definition[fr] = 'Matière de la cosse')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD Finition STRING DESCRIPTOR (#code = '0002-41982799300025#02-110#1',#definition[fr] = 'Finission de la cosse')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD Accrochage Boolean DESCRIPTOR (#code = '0002-41982799300025#02-111#1',#definition[fr] = 'Présence d un système d accrochage sur la cosse')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD Sécurité Boolean DESCRIPTOR (#code = '0002-41982799300025#02-112#1', #definition[fr] = 'Présence d un système de sécurité sur la cosse')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD Largeur REAL DESCRIPTOR (#code = '0002-41982799300025#02-113#1', #definition[fr] = 'Largeur de la cosse (mm)')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD Hauteur REAL DESCRIPTOR (#code = '0002-41982799300025#02-114#1', #definition[fr] = 'Hauteur de la cosse (mm)')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD Drapeau Boolean DESCRIPTOR (#code = '0002-41982799300025#02-115#1',#definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD \"Type Enfichage\" STRING DESCRIPTOR (#code = '0002-41982799300025#02-116#1',#definition[fr] = 'Type d''enfichage de la cosse')");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD Poids REAL DESCRIPTOR (#code = '0002-41982799300025#02-117#1',#definition[fr] = 'Poids de la cosse (g)')");

		statement.executeUpdate(
				"CREATE #CLASS \"Cosse à Fût Ouvert\" UNDER Cosse (DESCRIPTOR (#code = '0002-41982799300025#01-100#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"ALTER #CLASS \"Cosse à Fût Ouvert\" ADD \"Température Min\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-119#1', #definition[fr] = 'Température minimum d''utilisation de la cosse (degré Celcius)')");
		statement.executeUpdate(
				"ALTER #CLASS \"Cosse à Fût Ouvert\" ADD \"Température Max\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-120#1',#definition[fr] = 'Température maximum d''utilisation de la cosse (degré Celcius)')");
		statement.executeUpdate(
				"ALTER #CLASS \"Cosse à Fût Ouvert\" ADD \"Diamètre Minimum Isolant\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-121#1',#definition[fr] = 'Diamètre minimum de l''isolant du conducteur destiné à être utilisé avec la cosse (mm)')");
		statement.executeUpdate(
				"ALTER #CLASS \"Cosse à Fût Ouvert\" ADD \"Diamètre Maximum Isolant\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-122#1',#definition[fr] = 'Diamètre maximum de l''isolant du conducteur destiné à être utilisé avec la cosse (mm)')");
		statement.executeUpdate(
				"ALTER #CLASS \"Cosse à Fût Ouvert\" ADD \"Plage extérieure\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-123#1', #definition[fr] = 'Plage extérieure (ne s''applique qu''aux cosses rondes)')");
		statement.executeUpdate(
				"CREATE #CLASS \"Cosse à Fût Fermé\" UNDER Cosse (DESCRIPTOR (#code = '0002-41982799300025#01-100#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"ALTER #CLASS \"Cosse à Fût Fermé\" ADD \"Température Min\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-119#1', #definition[fr] = 'Température minimum d''utilisation de la cosse (degré Celcius)')");
		statement.executeUpdate(
				"ALTER #CLASS \"Cosse à Fût Fermé\" ADD \"Température Max\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-120#1',#definition[fr] = 'Température maximum d''utilisation de la cosse (degré Celcius)')");

		// Accessoire
		statement.executeUpdate(
				"CREATE #CLASS Accessoire UNDER \"Composant CFCA\" (DESCRIPTOR (#code = '0002-41982799300025#01-200#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Accessoire Etanche\" UNDER Accessoire (DESCRIPTOR (#code = '0002-41982799300025#01-201#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Accessoire Connecteur\" UNDER Accessoire (DESCRIPTOR (#code = '0002-41982799300025#01-202#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"ALTER #CLASS Cosse ADD \"Joint sur Fil\" REF(\"Accessoire Etanche\") DESCRIPTOR (#code = '0002-41982799300025#02-118#1',#definition[fr] = 'Joint sur fil')");

		// DPI
		statement.executeUpdate(
				"CREATE #CLASS DPI UNDER \"Composant CFCA\" (DESCRIPTOR (#code = '0002-41982799300025#01-500#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"ALTER #CLASS DPI ADD \"Section Fil Admissible Min\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-501#1',#definition[fr] = 'Section minimum du fil admissible dans la cosse (mm2)')");
		statement.executeUpdate(
				"ALTER #CLASS DPI ADD \"Section Fil Admissible Max\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-502#1',#definition[fr] = 'Section maximum du fil admissible dans la cosse (mm2)')");
		statement.executeUpdate(
				"ALTER #CLASS DPI ADD \"Section Fil AWG Admissible Min\" INT DESCRIPTOR (#code = '0002-41982799300025#02-503#1',#definition[fr] = 'Section AWG minimum du fil admissible dans la cosse')");
		statement.executeUpdate(
				"ALTER #CLASS DPI ADD \"Section Fil AWG Admissible Max\" INT DESCRIPTOR (#code = '0002-41982799300025#02-504#1',#definition[fr] = 'Section maximum du fil admissible dans la cosse')");
		statement.executeUpdate(
				"ALTER #CLASS DPI ADD \"Diamètre Minimum Isolant\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-505#1',#definition[fr] = 'Diamètre minimum de l''isolant du conducteur destiné à être utilisé avec le connecteur (mm)')");
		statement.executeUpdate(
				"ALTER #CLASS DPI ADD \"Diamètre Maximum Isolant\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-506#1', #definition[fr] = 'Diamètre maximum de l''isolant du conducteur destiné à être utilisé avec le connecteur (mm)')");
		statement.executeUpdate(
				"ALTER #CLASS DPI ADD \"Ampérage Maximum\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-507#1', #definition[fr] = 'Ampérage maximum supporté par la cosse (A)')");
		statement.executeUpdate(
				"ALTER #CLASS DPI ADD \"Nombre de Voies\" INT DESCRIPTOR (#code = '0002-41982799300025#02-508#1', #definition[fr] = 'Nombre de voies du connecteur (mm)')");
		statement.executeUpdate(
				"ALTER #CLASS DPI ADD GWT BOOLEAN DESCRIPTOR (#code = '0002-41982799300025#02-509#1',#definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS DPI ADD \"Nombre de Contacts\" INT DESCRIPTOR (#code = '0002-41982799300025#02-510#1', #definition[fr] = 'Nombre de contacts')");
		statement.executeUpdate(
				"ALTER #CLASS DPI ADD Voltage REAL DESCRIPTOR (#code = '0002-41982799300025#02-511#1', #definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS DPI ADD \"Direct/Indirect\" STRING DESCRIPTOR (#code = '0002-41982799300025#02-512#1', #definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS DPI ADD Marquage BOOLEAN DESCRIPTOR (#code = '0002-41982799300025#02-513#1', #definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS DPI ADD \"Nombre de Cloisons Latérales\" INT DESCRIPTOR (#code = '0002-41982799300025#02-514#1',#definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS DPI ADD \"Nombre de Cloisons Centrales\" INT DESCRIPTOR (#code = '0002-41982799300025#02-515#1',#definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS DPI ADD \"Nombre de Clés de Verrouillage\" INT DESCRIPTOR (#code = '0002-41982799300025#02-516#1',#definition[fr] = '')");
		statement.executeUpdate(
				"CREATE #CLASS HE14 UNDER DPI(DESCRIPTOR (#code = '0002-41982799300025#01-501#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"DPI Pas de 2.5\" UNDER DPI(DESCRIPTOR (#code = '0002-41982799300025#01-502#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"DPI Pas de 3.96\" UNDER DPI(DESCRIPTOR (#code = '0002-41982799300025#01-503#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"DPI Pas de 5\" UNDER DPI(DESCRIPTOR (#code = '0002-41982799300025#01-504#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"RAST 2.5\" UNDER DPI(DESCRIPTOR (#code = '0002-41982799300025#01-505#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"RAST 2.5 MK2\" UNDER DPI(DESCRIPTOR (#code = '0002-41982799300025#01-506#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"RAST 2.5 PRO\" UNDER DPI(DESCRIPTOR (#code = '0002-41982799300025#01-507#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"RAST 2.5 MK1\" UNDER DPI(DESCRIPTOR (#code = '0002-41982799300025#01-508#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"RAST 2.5 PRO MK2\" UNDER DPI(DESCRIPTOR (#code = '0002-41982799300025#01-509#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"RAST 5\" UNDER DPI(DESCRIPTOR (#code = '0002-41982799300025#01-510#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"RAST 5 MK1\" UNDER DPI(DESCRIPTOR (#code = '0002-41982799300025#01-511#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"RAST 5 MK2\" UNDER DPI(DESCRIPTOR (#code = '0002-41982799300025#01-511#1',#definition[fr] = ''))");

		// Gaine
		statement.executeUpdate(
				"CREATE #CLASS Gaine UNDER \"Composant CFCA\" (DESCRIPTOR (#code = '0002-41982799300025#01-600#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"ALTER #CLASS Gaine ADD \"Température Min\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-600#1',#definition[fr] = '(degré Celcius)')");
		statement.executeUpdate(
				"ALTER #CLASS Gaine ADD \"Température Max\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-601#1',#definition[fr] = '(degré Celcius)')");
		statement.executeUpdate(
				"ALTER #CLASS Gaine ADD \"Diamètre Intérieur\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-602#1',#definition[fr] = '(mm)')");
		statement.executeUpdate(
				"ALTER #CLASS Gaine ADD Fendue BOOLEAN DESCRIPTOR (#code = '0002-41982799300025#02-603#1',#definition[fr] = 'Spécifie, si vrai, que la gaine est fendue')");
		statement.executeUpdate(
				"ALTER #CLASS Gaine ADD Profil ENUM ('AHW', 'NormalProfil', 'UFW', 'Aucun') DESCRIPTOR (#code = '0002-41982799300025#02-604#1',#definition[fr] = 'Profil de la gaine')");
		statement.executeUpdate(
				"ALTER #CLASS Gaine ADD Conditionnement ENUM ('Coupée', 'Rouleau') DESCRIPTOR (#code = '0002-41982799300025#02-605#1',#definition[fr] = 'Profil de la gaine')");
		statement.executeUpdate(
				"ALTER #CLASS Gaine ADD \"Longueur maximum\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-606#1',#definition[fr] = 'Longueur maximum de la gaine (mm)')");
		statement.executeUpdate(
				"ALTER #CLASS Gaine ADD \"Diamètre Extérieur\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-607#1',#definition[fr] = 'Longueur maximum de la gaine (mm)')");
		statement.executeUpdate(
				"ALTER #CLASS Gaine ADD Matière ENUM ('ETFE', 'Fibre verre', 'Fibre verre/Epoxy', 'PA6', 'PPAE', 'PPBS', 'PPME', 'PPMOD', 'PVC', 'Silicone', 'Silicone/Fibre verre') DESCRIPTOR (#code = '0002-41982799300025#02-608#1',#definition[fr] = 'Matière de la gaine')");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Acier\" UNDER Gaine (DESCRIPTOR (#code = '0002-41982799300025#01-601#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Feutrine\" UNDER Gaine(DESCRIPTOR (#code = '0002-41982799300025#01-602#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine GAF\" UNDER Gaine(DESCRIPTOR (#code = '0002-41982799300025#01-602#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine GAFL\" UNDER Gaine(DESCRIPTOR (#code = '0002-41982799300025#01-603#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine GANF\" UNDER Gaine(DESCRIPTOR (#code = '0002-41982799300025#01-604#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine GAR\" UNDER Gaine(DESCRIPTOR (#code = '0002-41982799300025#01-605#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Lisse\" UNDER Gaine(DESCRIPTOR (#code = '0002-41982799300025#01-606#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Sili15C2\" UNDER Gaine(DESCRIPTOR (#code = '0002-41982799300025#01-607#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Sili15C3\" UNDER Gaine(DESCRIPTOR (#code = '0002-41982799300025#01-608#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Sili16F3\" UNDER Gaine(DESCRIPTOR (#code = '0002-41982799300025#01-609#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Sili21F1\" UNDER Gaine(DESCRIPTOR (#code = '0002-41982799300025#01-610#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Silitube\" UNDER Gaine(DESCRIPTOR (#code = '0002-41982799300025#01-611#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Spiflex\" UNDER Gaine(DESCRIPTOR (#code = '0002-41982799300025#01-612#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Spiralée\" UNDER Gaine(DESCRIPTOR (#code = '0002-41982799300025#01-613#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Tressée\" UNDER Gaine(DESCRIPTOR (#code = '0002-41982799300025#01-614#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Zipper\" UNDER Gaine(DESCRIPTOR (#code = '0002-41982799300025#01-615#1',#definition[fr] = ''))");

		// GaineThermo
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable\" UNDER \"Composant CFCA\" (DESCRIPTOR (#code = '0002-41982799300025#01-700#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"ALTER #CLASS \"Gaine Thermorétractable\" ADD \"Température Min\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-600#1',#definition[fr] = '(degré Celcius)')");
		statement.executeUpdate(
				"ALTER #CLASS \"Gaine Thermorétractable\" ADD \"Température Max\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-601#1',#definition[fr] = '(degré Celcius)')");
		statement.executeUpdate(
				"ALTER #CLASS \"Gaine Thermorétractable\" ADD \"Diamètre avant rétreint\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-700#1',#definition[fr] = '(mm)')");
		statement.executeUpdate(
				"ALTER #CLASS \"Gaine Thermorétractable\" ADD \"Diamètre après rétreint\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-701#1',#definition[fr] = '(mm)')");
		statement.executeUpdate(
				"ALTER #CLASS \"Gaine Thermorétractable\" ADD Longueur REAL DESCRIPTOR (#code = '0002-41982799300025#02-702#1',#definition[fr] = 'Longueur')");
		statement.executeUpdate(
				"ALTER #CLASS \"Gaine Thermorétractable\" ADD Marquage STRING DESCRIPTOR (#code = '0002-41982799300025#02-703#1',#name[fr] = 'Marquage',#definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS \"Gaine Thermorétractable\" ADD Collante BOOLEAN DESCRIPTOR (#code = '0002-41982799300025#02-704#1',#definition[fr] = 'Spécifie, si vrai, que la gaine est collante')");
		statement.executeUpdate(
				"ALTER #CLASS \"Gaine Thermorétractable\" ADD Epaisseur REAL DESCRIPTOR (#code = '0002-41982799300025#02-705#1',#definition[fr] = '(mm)')");
		statement.executeUpdate(
				"ALTER #CLASS \"Gaine Thermorétractable\" ADD \"Température rétreint\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-706#1',#definition[fr] = '(degré Celcius)')");
		statement.executeUpdate(
				"ALTER #CLASS \"Gaine Thermorétractable\" ADD Matière ENUM ('polyoléfine', 'PVC', 'PVDF', 'PTFE') DESCRIPTOR (#code = '0002-41982799300025#02-608#1',#definition[fr] = 'Matière de la gaine thermorétractable')");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable STF-4\" UNDER \"Gaine Thermorétractable\" (DESCRIPTOR (#code = '0002-41982799300025#01-701#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable SST\" UNDER \"Gaine Thermorétractable\" (DESCRIPTOR (#code = '0002-41982799300025#01-702#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable hélavia\" UNDER \"Gaine Thermorétractable\" (DESCRIPTOR (#code = '0002-41982799300025#01-703#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable G61\" UNDER \"Gaine Thermorétractable\" (DESCRIPTOR (#code = '0002-41982799300025#01-704#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable SER\" UNDER \"Gaine Thermorétractable\" (DESCRIPTOR (#code = '0002-41982799300025#01-705#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable SRV\" UNDER \"Gaine Thermorétractable\" (DESCRIPTOR (#code = '0002-41982799300025#01-706#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable SER-3\" UNDER \"Gaine Thermorétractable\" (DESCRIPTOR (#code = '0002-41982799300025#01-707#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable STF-4\" UNDER \"Gaine Thermorétractable\" (DESCRIPTOR (#code = '0002-41982799300025#01-708#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable STF\" UNDER \"Gaine Thermorétractable\" (DESCRIPTOR (#code = '0002-41982799300025#01-709#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable SER-UL\" UNDER \"Gaine Thermorétractable\" (DESCRIPTOR (#code = '0002-41982799300025#01-710#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable SKY\" UNDER \"Gaine Thermorétractable\" (DESCRIPTOR (#code = '0002-41982799300025#01-711#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable STF-M\" UNDER \"Gaine Thermorétractable\" (DESCRIPTOR (#code = '0002-41982799300025#01-712#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable SER-BIH\" UNDER \"Gaine Thermorétractable\" (DESCRIPTOR (#code = '0002-41982799300025#01-713#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable R\" UNDER \"Gaine Thermorétractable\" (DESCRIPTOR (#code = '0002-41982799300025#01-714#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable STFER\" UNDER \"Gaine Thermorétractable\" (DESCRIPTOR (#code = '0002-41982799300025#01-715#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Gaine Thermorétractable STD\" UNDER \"Gaine Thermorétractable\" (DESCRIPTOR (#code = '0002-41982799300025#01-716#1',#definition[fr] = ''))");

		// ComposantsElectriques
		statement.executeUpdate(
				"CREATE #CLASS \"Composants Electriques\" UNDER \"Composant CFCA\" (DESCRIPTOR (#code = '0002-41982799300025#01-800#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"ALTER #CLASS \"Composants Electriques\" ADD \"A Brancher ou à Sertir\" BOOLEAN DESCRIPTOR (#code = '0002-41982799300025#02-800#1', #definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS \"Composants Electriques\" ADD \"Nombre de Pôles\" INT DESCRIPTOR (#code = '0002-41982799300025#02-801#1',#definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS \"Composants Electriques\" ADD Polarisé BOOLEAN DESCRIPTOR (#code = '0002-41982799300025#02-802#1',#definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS \"Composants Electriques\" ADD Ampérage REAL DESCRIPTOR (#code = '0002-41982799300025#02-803#1',#definition[fr] = '(A)')");
		statement.executeUpdate(
				"ALTER #CLASS \"Composants Electriques\" ADD Tension REAL DESCRIPTOR (#code = '0002-41982799300025#02-804#1', #definition[fr] = '(V)')");
		statement.executeUpdate(
				"ALTER #CLASS \"Composants Electriques\" ADD Résistance REAL DESCRIPTOR (#code = '0002-41982799300025#02-805#1',#definition[fr] = '(Ohm)')");
		statement.executeUpdate(
				"ALTER #CLASS \"Composants Electriques\" ADD Puissance REAL DESCRIPTOR (#code = '0002-41982799300025#02-806#1', #definition = '(W)')");

		// Rubans et Colliers
		statement.executeUpdate(
				"CREATE #CLASS \"Rubans et Colliers\" UNDER \"Composant CFCA\" (DESCRIPTOR (#code = '0002-41982799300025#01-900#1',#definition[fr] = ''))");
		statement.executeUpdate(
				"ALTER #CLASS \"Rubans et Colliers\" ADD Marquage STRING DESCRIPTOR (#code = '0002-41982799300025#02-900#1',#definition[fr] = '')");
		statement.executeUpdate(
				"ALTER #CLASS \"Rubans et Colliers\" ADD Longueur REAL DESCRIPTOR (#code = '0002-41982799300025#02-901#1',#definition[fr] = '(mm)')");
		statement.executeUpdate(
				"ALTER #CLASS \"Rubans et Colliers\" ADD Matière ENUM ('PVC', 'TISSU') DESCRIPTOR (#code = '0002-41982799300025#02-902#1',#definition[fr] = '')");

		// Divers
		statement.executeUpdate(
				"CREATE #CLASS Divers (DESCRIPTOR (#code = '0002-41982799300025#01-301#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS Surcote UNDER Divers (DESCRIPTOR (#code = '0002-41982799300025#01-302#1',#definition = ''))");
		statement.executeUpdate(
				"ALTER #CLASS Surcote ADD Position STRING DESCRIPTOR (#code = '0002-41982799300025#02-309#1',#definition = 'Position du connecteur')");
		statement.executeUpdate(
				"ALTER #CLASS Surcote ADD Valeur REAL DESCRIPTOR (#code = '0002-41982799300025#02-310#1',#name[fr] = 'Valeur',#definition = 'Cote permettant de prendre en compte le positionnement du connecteur pour le calcul de la longueur de fil'))");
		statement.executeUpdate(
				"CREATE #CLASS Port UNDER Divers (DESCRIPTOR (#code = '0002-41982799300025#01-302#1',#definition = ''))");
		statement.executeUpdate(
				"ALTER #CLASS Port ADD Nom STRING DESCRIPTOR (#code = '0002-41982799300025#02-311#1',#definition = 'Nom identifiant le port d''un connecteur')");
		statement.executeUpdate(
				"ALTER #CLASS Port ADD Surcotes REF(Surcote) ARRAY DESCRIPTOR (#code = '0002-41982799300025#02-312#1', #definition = 'Cotes permettant de prendre en compte le positionnement du connecteur pour le calcul de la longueur de fil')");
		statement.executeUpdate(
				"ALTER #CLASS Port ADD \"Cosses Eligibles\" STRING ARRAY DESCRIPTOR (#code = '0002-41982799300025#02-312#1',#definition = 'Types de cosses pouvant être utilisées sur le port du conn	ecteur')");
		statement.executeUpdate(
				"CREATE #CLASS Connecteur UNDER \"Composant CFCA\" (DESCRIPTOR (#code = '0002-41982799300025#01-300#1',#definition = ''))");
		statement.executeUpdate(
				"ALTER #CLASS Connecteur ADD Genre ENUM ('mâle', 'femelle', 'hermaphrodite') DESCRIPTOR (#code = '0002-41982799300025#02-300#1',#definition = '')");
		statement.executeUpdate(
				"ALTER #CLASS Connecteur ADD \"Diamètre Minimum Isolant\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-301#1',#definition = 'Diamètre minimum de l''isolant du conducteur destiné à être utilisé avec le connecteur (mm)')");
		statement.executeUpdate(
				"ALTER #CLASS Connecteur ADD \"Diamètre Maximum Isolant\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-302#1',#definition = 'Diamètre maximum de l''isolant du conducteur destiné à être utilisé avec le connecteur (mm)')");
		statement.executeUpdate(
				"ALTER #CLASS Connecteur ADD \"Nombre De Voies\" INT DESCRIPTOR (#code = '0002-41982799300025#02-303#1',#definition = 'Nombre de voies du connecteur (mm)')");
		statement.executeUpdate(
				"ALTER #CLASS Connecteur ADD Etanche BOOLEAN DESCRIPTOR (#code = '0002-41982799300025#02-304#1',#definition = 'Indicateur qui, si vrai, spécifie que le connecteur est étanche')");
		statement.executeUpdate(
				"ALTER #CLASS Connecteur ADD \"Couleur Marquage Associé\" BOOLEAN DESCRIPTOR (#code = '0002-41982799300025#02-305#1', #definition = 'Couleur du marquage du connecteur')");
		statement.executeUpdate(
				"ALTER #CLASS Connecteur ADD \"Sertissage Après Enfilage\" BOOLEAN DESCRIPTOR (#code = '0002-41982799300025#02-306#1',#definition = 'Indicateur qui, si vrai, spécifie qu''une opération de sertissage après enfilage doit être réalisée')");
		statement.executeUpdate(
				"ALTER #CLASS Connecteur ADD GWT BOOLEAN DESCRIPTOR (#code = '0002-41982799300025#02-307#1',#definition = '')");
		statement.executeUpdate(
				"ALTER #CLASS Connecteur ADD \"Connecteur Inverse\" REF(Connecteur) DESCRIPTOR ( #code = '0002-41982799300025#02-308#1',#definition = 'Connecteur inverse')");
		statement.executeUpdate(
				"ALTER #CLASS Connecteur ADD Ports REF(Port) ARRAY DESCRIPTOR (#code = '0002-41982799300025#02-309#1',#definition = 'Description des ports du connecteur')");
		statement.executeUpdate(
				"ALTER #CLASS Connecteur ADD Orientations STRING ARRAY DESCRIPTOR (#code = '0002-41982799300025#02-310#1',#definition = 'Orientations possibles du connecteur')");
		statement.executeUpdate(
				"ALTER #CLASS Connecteur ADD \"Température Min\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-311#1',#definition = 'Température minimum d''utilisation du connecteur (degré Celcius)')");
		statement.executeUpdate(
				"ALTER #CLASS Connecteur ADD \"Température Max\" REAL DESCRIPTOR (#code = '0002-41982799300025#02-312#1',#definition = 'Température maximum d''utilisation du connecteur (degré Celcius)')");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 1 Voie\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-310#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 2 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-311#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 3 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-312#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 4 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-313#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 5 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-314#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 6 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-315#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 7 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-316#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 8 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-317#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 9 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-318#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 10 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-319#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 11 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-320#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 12 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-321#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 13 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-322#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 14 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-323#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 15 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-324#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 16 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-325#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 17 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-326#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 18 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-327#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 20 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-329#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 21 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-330#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 22 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-331#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 23 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-332#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 24 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-333#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 25 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-334#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 26 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-335#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 27 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-338#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 28 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-339#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 29 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-340#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 30 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-341#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 32 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-343#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 34 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-345#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 35 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-346#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 36 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-347#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 37 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-348#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 40 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-351#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 42 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-353#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 48 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-359#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 52 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-363#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 54 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-365#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 55 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-366#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 56 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-367#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 58 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-369#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 64 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-375#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 70 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-381#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 90 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-401#1',#definition = ''))");
		statement.executeUpdate(
				"CREATE #CLASS \"Connecteur 94 Voies\" UNDER Connecteur (DESCRIPTOR (#code = '0002-41982799300025#01-405#1',#definition = ''))");

		// Extent MonoConducteur.
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 0.2\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 0.22\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 0.25\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 0.34\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 0.35\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 0.38\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 0.4\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 0.5\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 0.6\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 0.63\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 0.7\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 0.75\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 0.81\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 1\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 1.31\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 1.4\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 1.5\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 2\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 2.07\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 2.5\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 3\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 3.31\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 4\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 5\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 5.30\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 6\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 7\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 10\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 16\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 20\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 25\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 35\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 40\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 50\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 60\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 70\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 75\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 81\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 95\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"MonoConducteur section 120\" (Normes, \"Référence BE\", \"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",\"Section MM2\", \"Section AWG\",\"Poids Cuivre\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\","
						+ "\"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\", Type)");

		// Extent Accessoire (Accessoires Etanches)
		statement.executeUpdate(
				"CREATE EXTENT OF \"Accessoire Etanche\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes)");

		// Extent Accessoire (Accessoires Connecteur)
		statement.executeUpdate(
				"CREATE EXTENT OF \"Accessoire Connecteur\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes)");

		// Extent Cosse (Connexions)
		statement.executeUpdate(
				"CREATE EXTENT OF \"Cosse à Fût Ouvert\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Préconisation Sertissage\",Genre,Forme,\"Fût\",\"Section Fil Admissible Min\",\"Section Fil Admissible Max\",\"Section Fil AWG Admissible Min\",\"Section Fil AWG Admissible Max\",\"Longueur de Dénudage\",Matière,Finition,Accrochage,Sécurité,Largeur,Hauteur,Drapeau,\"Type Enfichage\",Poids,\"Joint sur Fil\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\")");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Cosse à Fût Fermé\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Préconisation Sertissage\",Genre,Forme,\"Fût\",\"Section Fil Admissible Min\",\"Section Fil Admissible Max\",\"Section Fil AWG Admissible Min\",\"Section Fil AWG Admissible Max\",\"Longueur de Dénudage\",Matière,Finition,Accrochage,Sécurité,Largeur,Hauteur,Drapeau,\"Type Enfichage\",Poids,\"Joint sur Fil\")");

		// Extent DPI (Dpi)
		statement.executeUpdate(
				"CREATE EXTENT OF HE14 (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Section Fil Admissible Min\",\"Section Fil Admissible Max\",\"Section Fil AWG Admissible Min\",\"Section Fil AWG Admissible Max\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Ampérage Maximum\",\"Nombre de Voies\",GWT, \"Nombre de Contacts\",Voltage,\"Direct/Indirect\",Marquage,\"Nombre de Cloisons Latérales\",\"Nombre de Cloisons Centrales\",\"Nombre de Clés de Verrouillage\")");
		statement.executeUpdate(
				"CREATE EXTENT OF \"DPI Pas de 2.5\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Section Fil Admissible Min\",\"Section Fil Admissible Max\",\"Section Fil AWG Admissible Min\",\"Section Fil AWG Admissible Max\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Ampérage Maximum\",\"Nombre de Voies\",GWT, \"Nombre de Contacts\",Voltage,\"Direct/Indirect\",Marquage,\"Nombre de Cloisons Latérales\",\"Nombre de Cloisons Centrales\",\"Nombre de Clés de Verrouillage\")");
		statement.executeUpdate(
				"CREATE EXTENT OF \"DPI Pas de 3.96\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Section Fil Admissible Min\",\"Section Fil Admissible Max\",\"Section Fil AWG Admissible Min\",\"Section Fil AWG Admissible Max\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Ampérage Maximum\",\"Nombre de Voies\",GWT, \"Nombre de Contacts\",Voltage,\"Direct/Indirect\",Marquage,\"Nombre de Cloisons Latérales\",\"Nombre de Cloisons Centrales\",\"Nombre de Clés de Verrouillage\")");
		statement.executeUpdate(
				"CREATE EXTENT OF \"DPI Pas de 5\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Section Fil Admissible Min\",\"Section Fil Admissible Max\",\"Section Fil AWG Admissible Min\",\"Section Fil AWG Admissible Max\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Ampérage Maximum\",\"Nombre de Voies\",GWT, \"Nombre de Contacts\",Voltage,\"Direct/Indirect\",Marquage,\"Nombre de Cloisons Latérales\",\"Nombre de Cloisons Centrales\",\"Nombre de Clés de Verrouillage\")");
		statement.executeUpdate(
				"CREATE EXTENT OF \"RAST 2.5\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Section Fil Admissible Min\",\"Section Fil Admissible Max\",\"Section Fil AWG Admissible Min\",\"Section Fil AWG Admissible Max\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Ampérage Maximum\",\"Nombre de Voies\",GWT, \"Nombre de Contacts\",Voltage,\"Direct/Indirect\",Marquage,\"Nombre de Cloisons Latérales\",\"Nombre de Cloisons Centrales\",\"Nombre de Clés de Verrouillage\")");
		statement.executeUpdate(
				"CREATE EXTENT OF \"RAST 2.5 MK2\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Section Fil Admissible Min\",\"Section Fil Admissible Max\",\"Section Fil AWG Admissible Min\",\"Section Fil AWG Admissible Max\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Ampérage Maximum\",\"Nombre de Voies\",GWT, \"Nombre de Contacts\",Voltage,\"Direct/Indirect\",Marquage,\"Nombre de Cloisons Latérales\",\"Nombre de Cloisons Centrales\",\"Nombre de Clés de Verrouillage\")");
		statement.executeUpdate(
				"CREATE EXTENT OF \"RAST 2.5 PRO\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Section Fil Admissible Min\",\"Section Fil Admissible Max\",\"Section Fil AWG Admissible Min\",\"Section Fil AWG Admissible Max\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Ampérage Maximum\",\"Nombre de Voies\",GWT, \"Nombre de Contacts\",Voltage,\"Direct/Indirect\",Marquage,\"Nombre de Cloisons Latérales\",\"Nombre de Cloisons Centrales\",\"Nombre de Clés de Verrouillage\")");
		statement.executeUpdate(
				"CREATE EXTENT OF \"RAST 2.5 MK1\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Section Fil Admissible Min\",\"Section Fil Admissible Max\",\"Section Fil AWG Admissible Min\",\"Section Fil AWG Admissible Max\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Ampérage Maximum\",\"Nombre de Voies\",GWT, \"Nombre de Contacts\",Voltage,\"Direct/Indirect\",Marquage,\"Nombre de Cloisons Latérales\",\"Nombre de Cloisons Centrales\",\"Nombre de Clés de Verrouillage\")");
		statement.executeUpdate(
				"CREATE EXTENT OF \"RAST 2.5 PRO MK2\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Section Fil Admissible Min\",\"Section Fil Admissible Max\",\"Section Fil AWG Admissible Min\",\"Section Fil AWG Admissible Max\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Ampérage Maximum\",\"Nombre de Voies\",GWT, \"Nombre de Contacts\",Voltage,\"Direct/Indirect\",Marquage,\"Nombre de Cloisons Latérales\",\"Nombre de Cloisons Centrales\",\"Nombre de Clés de Verrouillage\")");
		statement.executeUpdate(
				"CREATE EXTENT OF \"RAST 5\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Section Fil Admissible Min\",\"Section Fil Admissible Max\",\"Section Fil AWG Admissible Min\",\"Section Fil AWG Admissible Max\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Ampérage Maximum\",\"Nombre de Voies\",GWT, \"Nombre de Contacts\",Voltage,\"Direct/Indirect\",Marquage,\"Nombre de Cloisons Latérales\",\"Nombre de Cloisons Centrales\",\"Nombre de Clés de Verrouillage\")");
		statement.executeUpdate(
				"CREATE EXTENT OF \"RAST 5 MK1\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Section Fil Admissible Min\",\"Section Fil Admissible Max\",\"Section Fil AWG Admissible Min\",\"Section Fil AWG Admissible Max\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Ampérage Maximum\",\"Nombre de Voies\",GWT, \"Nombre de Contacts\",Voltage,\"Direct/Indirect\",Marquage,\"Nombre de Cloisons Latérales\",\"Nombre de Cloisons Centrales\",\"Nombre de Clés de Verrouillage\")");
		statement.executeUpdate(
				"CREATE EXTENT OF \"RAST 5 MK2\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Section Fil Admissible Min\",\"Section Fil Admissible Max\",\"Section Fil AWG Admissible Min\",\"Section Fil AWG Admissible Max\",\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Ampérage Maximum\",\"Nombre de Voies\",GWT, \"Nombre de Contacts\",Voltage,\"Direct/Indirect\",Marquage,\"Nombre de Cloisons Latérales\",\"Nombre de Cloisons Centrales\",\"Nombre de Clés de Verrouillage\")");

		// Extent Gaine (Gaines)
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Acier\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre Intérieur\",Fendue,Profil,\"Longueur maximum\",\"Diamètre Extérieur\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Feutrine\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre Intérieur\",Fendue,Profil,\"Longueur maximum\",\"Diamètre Extérieur\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine GAF\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre Intérieur\",Fendue,Profil,\"Longueur maximum\",\"Diamètre Extérieur\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine GAFL\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre Intérieur\",Fendue,Profil,\"Longueur maximum\",\"Diamètre Extérieur\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine GANF\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre Intérieur\",Fendue,Profil,\"Longueur maximum\",\"Diamètre Extérieur\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine GAR\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre Intérieur\",Fendue,Profil,\"Longueur maximum\",\"Diamètre Extérieur\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Lisse\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre Intérieur\",Fendue,Profil,\"Longueur maximum\",\"Diamètre Extérieur\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Sili15C2\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre Intérieur\",Fendue,Profil,\"Longueur maximum\",\"Diamètre Extérieur\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Sili15C3\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre Intérieur\",Fendue,Profil,\"Longueur maximum\",\"Diamètre Extérieur\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Sili16F3\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre Intérieur\",Fendue,Profil,\"Longueur maximum\",\"Diamètre Extérieur\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Sili21F1\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre Intérieur\",Fendue,Profil,\"Longueur maximum\",\"Diamètre Extérieur\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Silitube\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre Intérieur\",Fendue,Profil,\"Longueur maximum\",\"Diamètre Extérieur\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Spiflex\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre Intérieur\",Fendue,Profil,\"Longueur maximum\",\"Diamètre Extérieur\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Spiralée\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre Intérieur\",Fendue,Profil,\"Longueur maximum\",\"Diamètre Extérieur\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Tressée\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre Intérieur\",Fendue,Profil,\"Longueur maximum\",\"Diamètre Extérieur\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Zipper\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre Intérieur\",Fendue,Profil,\"Longueur maximum\",\"Diamètre Extérieur\",Matière)");

		// Extent GaineThermo (Gaines Thermos)
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Thermorétractable STF-4\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre avant rétreint\",\"Diamètre après rétreint\",Longueur,Marquage,Collante,Epaisseur,\"Température rétreint\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Thermorétractable SST\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre avant rétreint\",\"Diamètre après rétreint\",Longueur,Marquage,Collante,Epaisseur,\"Température rétreint\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Thermorétractable hélavia\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre avant rétreint\",\"Diamètre après rétreint\",Longueur,Marquage,Collante,Epaisseur,\"Température rétreint\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Thermorétractable G61\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre avant rétreint\",\"Diamètre après rétreint\",Longueur,Marquage,Collante,Epaisseur,\"Température rétreint\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Thermorétractable SER\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre avant rétreint\",\"Diamètre après rétreint\",Longueur,Marquage,Collante,Epaisseur,\"Température rétreint\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Thermorétractable SRV\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre avant rétreint\",\"Diamètre après rétreint\",Longueur,Marquage,Collante,Epaisseur,\"Température rétreint\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Thermorétractable SER-3\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre avant rétreint\",\"Diamètre après rétreint\",Longueur,Marquage,Collante,Epaisseur,\"Température rétreint\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Thermorétractable STF\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre avant rétreint\",\"Diamètre après rétreint\",Longueur,Marquage,Collante,Epaisseur,\"Température rétreint\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Thermorétractable SER-UL\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre avant rétreint\",\"Diamètre après rétreint\",Longueur,Marquage,Collante,Epaisseur,\"Température rétreint\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Thermorétractable SKY\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre avant rétreint\",\"Diamètre après rétreint\",Longueur,Marquage,Collante,Epaisseur,\"Température rétreint\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Thermorétractable STF-M\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre avant rétreint\",\"Diamètre après rétreint\",Longueur,Marquage,Collante,Epaisseur,\"Température rétreint\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Thermorétractable SER-BIH\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre avant rétreint\",\"Diamètre après rétreint\",Longueur,Marquage,Collante,Epaisseur,\"Température rétreint\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Thermorétractable R\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre avant rétreint\",\"Diamètre après rétreint\",Longueur,Marquage,Collante,Epaisseur,\"Température rétreint\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Thermorétractable STFER\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre avant rétreint\",\"Diamètre après rétreint\",Longueur,Marquage,Collante,Epaisseur,\"Température rétreint\",Matière)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Gaine Thermorétractable STD\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",\"Diamètre avant rétreint\",\"Diamètre après rétreint\",Longueur,Marquage,Collante,Epaisseur,\"Température rétreint\",Matière)");

		// Extent Port
		statement.executeUpdate("CREATE EXTENT OF Surcote (Position, Valeur)");
		statement.executeUpdate("CREATE EXTENT OF Port (Nom,Surcotes,\"Cosses Eligibles\")");

		// Extent Composants Electriques
		statement.executeUpdate(
				"CREATE EXTENT OF \"Composants Electriques\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"A Brancher ou à Sertir\",\"Nombre de Pôles\",Polarisé,Ampérage,Tension,Résistance,Puissance)");

		// Extent Rubans et Colliers
		statement.executeUpdate(
				"CREATE EXTENT OF \"Rubans et Colliers\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,Marquage,Longueur,Matière)");

		// Extent Connecteur
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 1 Voie\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 2 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 3 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 4 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 5 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 6 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 7 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 8 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 9 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 10 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 11 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 12 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 13 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 14 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 15 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 16 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 17 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 18 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 20 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 21 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 22 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 23 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 24 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 25 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 26 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 27 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 28 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 29 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 30 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 32 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 34 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 35 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 36 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 37 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 40 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 42 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 48 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 52 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 54 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 55 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 56 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 58 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 64 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 70 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 90 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");
		statement.executeUpdate(
				"CREATE EXTENT OF \"Connecteur 94 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\",Normes,\"Température Min\",\"Température Max\",Genre,\"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\",\"Nombre De Voies\",Etanche,\"Couleur Marquage Associé\",\"Sertissage Après Enfilage\",GWT,\"Connecteur Inverse\",Ports,Orientations)");

		statement.executeUpdate("DROP ENTITY #uri_Type");
		System.out.println("Duration : " + (System.currentTimeMillis() - start));

		t.rollback();
	}

	@Test
	public void testCFCAODBASENomenclature() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		s.setDefaultNameSpace("http://www.cfca.fr/");

		// Composants électrique

		// 8005168 00
		statement.executeUpdate("insert into \"Composants Electriques\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Normes," + "\"A Brancher ou à Sertir\"," + "\"Nombre de Pôles\","
				+ "Polarisé," + "Ampérage," + "Tension," + "Résistance," + "Puissance) values (" + "'CFCA-8005168 00', "
				+ "'8005168 00', " + "''," + "''," + "'FUSIBLE 250MA VERRE'," + "null," + "null," + "'0034-1510BF', "
				+ "null, " + "null, " + "null, " + "false, " + "0.25, " + "null, " + "null, " + "null)");

		// 8017768 00
		statement.executeUpdate("insert into \"Composants Electriques\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Normes," + "\"A Brancher ou à Sertir\"," + "\"Nombre de Pôles\","
				+ "Polarisé," + "Ampérage," + "Tension," + "Résistance," + "Puissance) values (" + "'CFCA-8017768 00', "
				+ "'8017768 00', " + "''," + "''," + "'DISJONCTEUR 10AMP'," + "null," + "null," + "'1610-21-10A', "
				+ "null, " + "null, " + "null, " + "null, " + "10.0, " + "null, " + "null, " + "null)");

		// 8017771 00
		statement.executeUpdate("insert into \"Composants Electriques\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Normes," + "\"A Brancher ou à Sertir\"," + "\"Nombre de Pôles\","
				+ "Polarisé," + "Ampérage," + "Tension," + "Résistance," + "Puissance) values (" + "'CFCA-8017771 00', "
				+ "'8017771 00', " + "''," + "''," + "'DISJONCTEUR 06A'," + "null," + "null," + "'1610-21-06A', "
				+ "null, " + "null, " + "null, " + "null, " + "6.0, " + "null, " + "null, " + "null)");

		// 8021995 00
		statement.executeUpdate("insert into \"Composants Electriques\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Normes," + "\"A Brancher ou à Sertir\"," + "\"Nombre de Pôles\","
				+ "Polarisé," + "Ampérage," + "Tension," + "Résistance," + "Puissance) values (" + "'CFCA-8021995 00', "
				+ "'8021995 00', " + "''," + "''," + "'RELAIS 12VS 30A'," + "null," + "null," + "'4RD 003 520 088', "
				+ "null, " + "null, " + "null, " + "null, " + "30.0, " + "12.0, " + "null, " + "null)");

		// 8024950 00
		statement.executeUpdate("insert into \"Composants Electriques\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Normes," + "\"A Brancher ou à Sertir\"," + "\"Nombre de Pôles\","
				+ "Polarisé," + "Ampérage," + "Tension," + "Résistance," + "Puissance) values (" + "'CFCA-8024950 00', "
				+ "'8024950 00', " + "''," + "''," + "'DISJONCTEUR  15A'," + "null," + "null," + "'4RD 003 520 088', "
				+ "null, " + "null, " + "null, " + "null, " + "15.0, " + "null, " + "null, " + "null)");

		// 8029521 00
		statement.executeUpdate("insert into \"Composants Electriques\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Normes," + "\"A Brancher ou à Sertir\"," + "\"Nombre de Pôles\","
				+ "Polarisé," + "Ampérage," + "Tension," + "Résistance," + "Puissance) values (" + "'CFCA-8029521 00', "
				+ "'8029521 00', " + "''," + "''," + "'RELAIS 70A 12V DC 1NO'," + "null," + "null,"
				+ "'0F.REL155/OD705813', " + "null, " + "null, " + "null, " + "null, " + "70.0, " + "12.0, " + "null, "
				+ "null)");

		// 8032669 00
		statement.executeUpdate("insert into \"Composants Electriques\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Normes," + "\"A Brancher ou à Sertir\"," + "\"Nombre de Pôles\","
				+ "Polarisé," + "Ampérage," + "Tension," + "Résistance," + "Puissance) values (" + "'CFCA-8032669 00', "
				+ "'8032669 00', " + "''," + "''," + "'DISJONCTEUR 30A ATOFUSE'," + "null," + "null,"
				+ "'1610-21-30A', " + "null, " + "null, " + "null, " + "null, " + "30.0, " + "null, " + "null, "
				+ "null)");

		// Extent Rubans et Colliers

		// 1003836 00
		statement.executeUpdate("insert into \"Rubans et Colliers\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Normes," + "Marquage," + "Longueur," + "Matière) values ("
				+ "'CFCA-1003836 00', " + "'1003836 00', " + "''," + "''," + "'COLLIER PLT1MM (USA)'," + "null,"
				+ "null," + "'111-01929'," + "null," + "null," + "null," + "null)");

		// 149288 00
		statement.executeUpdate("insert into \"Rubans et Colliers\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Normes," + "Marquage," + "Longueur," + "Matière) values ("
				+ "'CFCA-149288 00', " + "'149288 00', " + "null," + "'scotchNoir.png',"
				+ "'RUB ADH 15X33M 2702 PVC NO T2 A38'," + "'Noir'," + "'SCAPA TAPES FRANCE SA'," + "'103017',"
				+ "null," + "null," + "33.0," + "'PVC')");

		// 1009942 00
		statement.executeUpdate("insert into \"Rubans et Colliers\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Normes," + "Marquage," + "Longueur," + "Matière) values ("
				+ "'CFCA-1009942 00', " + "'1009942 00', " + "''," + "''," + "'ETIQUETTE TYVEK 90X3'," + "'null',"
				+ "'NURSAN'," + "'1009942 00'," + "null," + "null," + "null," + "null)");

		// MonoConducteur

		// 1002785 00
		statement.executeUpdate("insert into \"MonoConducteur section 6\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-1002785 00', " + "'1002785 00', "
				+ "'1002785 00.jpg', " + "null, " + "'FIL 6² H07V2K RG', " + "'Rouge', " + "'NEXANS MAROC', "
				+ "'51507544700', " + "6.0, " + "null, " + "0.0576, " + "4.55, " + "4.65, " + "false, " + "'Carton', "
				+ "false, " + "false, " + "null, " + "90.0, " + "'PVC', " + "84, " + "0.3, " + "'Rouge recuit', "
				+ "3.3, " + "450.0, " + "750.0, " + "ARRAY['Harmonisé'])");

		// 1002882 00
		statement.executeUpdate("insert into \"MonoConducteur section 1.5\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-1002882 00', " + "'1002882 00', "
				+ "'1002882 00.jpg', " + "null, " + "'FIL 1,5² H07V2K NO T2', " + "'Noir', " + "'NEXANS MAROC', "
				+ "'51550544845', " + "1.5, " + "null, " + "0.0144, " + "2.8, " + "2.9, " + "false, " + "'Carton', "
				+ "false, " + "false, " + "null, " + "90.0, " + "'PVC', " + "28, " + "0.247, " + "'Rouge recuit', "
				+ "13.3, " + "450.0, " + "750.0, " + "ARRAY['Harmonisé'])");

		// 1002883 00
		statement.executeUpdate("insert into \"MonoConducteur section 1.5\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-1002883 00', " + "'1002883 00', "
				+ "'1002883 00.jpg', " + "null, " + "'FIL 1,5² H07V2K NO T2', " + "'Marron', " + "'NEXANS MAROC', "
				+ "'51507544000', " + "1.5, " + "null, " + "0.0144, " + "2.8, " + "2.9, " + "false, " + "'Carton', "
				+ "false, " + "false, " + "null, " + "90.0, " + "'PVC', " + "28, " + "0.247, " + "'Rouge recuit', "
				+ "13.3, " + "450.0, " + "750.0," + "ARRAY['Harmonisé'])");

		// 1002886 00
		statement.executeUpdate("insert into \"MonoConducteur section 1.5\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-1002886 00', " + "'1002886 00', "
				+ "'1002886 00.jpg', " + "null, " + "'FIL 1,5² H07V2K NO T2', " + "'Violet', " + "'NEXANS MAROC', "
				+ "'51507543100', " + "1.5, " + "null, " + "0.0144, " + "2.8, " + "2.9, " + "false, " + "'Carton', "
				+ "false, " + "false, " + "null, " + "90.0, " + "'PVC', " + "28, " + "0.247, " + "'Rouge recuit', "
				+ "13.3, " + "450.0, " + "750.0, " + "ARRAY['Harmonisé'])");

		// 1002887 00
		statement.executeUpdate("insert into \"MonoConducteur section 1.5\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-1002887 00', " + "'1002887 00', "
				+ "'1002887 00.jpg', " + "null, " + "'FIL 1,5² H07V2K NO T2', " + "'Rouge', " + "'NEXANS MAROC', "
				+ "'51507544100', " + "1.5, " + "null, " + "0.0144, " + "2.8, " + "2.9, " + "false, " + "'Carton', "
				+ "false, " + "false, " + "null, " + "90.0, " + "'PVC', " + "28, " + "0.247, " + "'Rouge recuit', "
				+ "13.3, " + "450.0, " + "750.0, " + "ARRAY['Harmonisé'])");

		// 1003999 00
		statement.executeUpdate("insert into \"MonoConducteur section 2.5\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-1003999 00', " + "'1003999 00', "
				+ "'1003999 00.jpg', " + "null, " + "'FIL 2,5² H07VK VI', " + "'Violet', " + "'NEXANS MAROC', "
				+ "'51507543200', " + "2.5, " + "null, " + "0.024, " + "3.4, " + "3.6, " + "false, " + "'Carton', "
				+ "false, " + "false, " + "null, " + "70.0, " + "'PVC', " + "49, " + "0.247, " + "'Rouge recuit', "
				+ "7.98, " + "450.0, " + "750.0, " + "ARRAY['Harmonisé'])");

		// 1004005 00
		statement.executeUpdate("insert into \"MonoConducteur section 4\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-1004005 00', " + "'1004005 00', "
				+ "'1004005 00.jpg', " + "null, " + "'FIL 4² H07VK RG DIA EXT 4.10 +/- 0.10', " + "'Rouge', "
				+ "'NEXANS MAROC', " + "'51550544701', " + "4.0, " + "null, " + "0.0384, " + "4.0, " + "4.2, "
				+ "false, " + "'Carton', " + "false, " + "false, " + "null, " + "70.0, " + "'PVC', " + "56, " + "0.3, "
				+ "'Rouge recuit', " + "4.95, " + "450.0, " + "750.0, " + "ARRAY['Harmonisé'])");

		// 1004006 00
		statement.executeUpdate("insert into \"MonoConducteur section 4\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-1004006 00', " + "'1004006 00', "
				+ "'1004006 00.jpg', " + "null, " + "'FIL 4² H07VK NO DIA EXT 4.10 +/- 0.10', " + "'Noir', "
				+ "'NEXANS MAROC', " + "'51550544702', " + "4.0, " + "null, " + "0.0384, " + "4.0, " + "4.2, "
				+ "false, " + "'Carton', " + "false, " + "false, " + "null, " + "70.0, " + "'PVC', " + "56, " + "0.3, "
				+ "'Rouge recuit', " + "4.95, " + "450.0, " + "750.0, " + "ARRAY['Harmonisé'])");

		// 1012827 00
		statement.executeUpdate("insert into \"MonoConducteur section 1.5\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-1012827 00', " + "'1012827 00', "
				+ "'1012827 00.jpg', " + "null, " + "'FIL 1,5² HO7V2K OG 26X0,26 (ELEX)', " + "'Orange', "
				+ "'NEXANS MAROC', " + "'51507543400', " + "1.5, " + "null, " + "0.0144, " + "2.8, " + "2.9, "
				+ "false, " + "'Carton', " + "false, " + "false, " + "null, " + "90.0, " + "'PVC', " + "28, "
				+ "0.247, " + "'Rouge recuit', " + "13.3, " + "450.0, " + "750.0, " + "ARRAY['Harmonisé'])");

		// 75994 00
		statement.executeUpdate("insert into \"MonoConducteur section 1.5\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-75994 00', " + "'75994 00', "
				+ "'75994 00.pdf', " + "null, " + "'FIL 6² 48AU NO', " + "'Noir', " + "'NEXANS MAROC', "
				+ "'51550544862', " + "1.5, " + "null, " + "0.0576, " + "4.3, " + "4.5, " + "false, " + "'Carton', "
				+ "false, " + "false, " + "null, " + "100.0, " + "'PVC', " + "84, " + "0.29, " + "'Rouge recuit', "
				+ "3.3, " + "300.0, " + "500.0, " + "ARRAY['ISO 6722'])");

		// 8000617 00
		statement.executeUpdate("insert into \"MonoConducteur section 1.5\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-8000617 00', " + "'8000617 00', "
				+ "'8000617 00.jpg', " + "null, " + "'FIL 1,5² H07V2K GR', " + "'Gris', " + "'NEXANS MAROC', "
				+ "'51550544711', " + "1.5, " + "null, " + "0.0144, " + "2.8, " + "2.9, " + "false, " + "'Carton', "
				+ "false, " + "false, " + "null, " + "90.0, " + "'PVC', " + "84, " + "0.247, " + "'Rouge recuit', "
				+ "13.3, " + "450.0, " + "750.0, " + "ARRAY['Harmonisé'])");

		// 8005795 00
		statement.executeUpdate("insert into \"MonoConducteur section 1.5\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-8005795 00', " + "'8005795 00', "
				+ "'8005795 00.jpg', " + "null, " + "'FIL 1,5² H07VK IV', " + "'Gris', " + "'NEXANS MAROC', "
				+ "'51550544712', " + "1.5, " + "null, " + "0.0144, " + "2.8, " + "2.9, " + "false, " + "'Carton', "
				+ "false, " + "false, " + "null, " + "70.0, " + "'PVC', " + "28, " + "0.247, " + "'Rouge recuit', "
				+ "13.3, " + "450.0, " + "750.0, " + "ARRAY['Harmonisé'])");

		// 8012729 00
		statement.executeUpdate("insert into \"MonoConducteur section 1.5\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-8012729 00', " + "'8012729 00', "
				+ "'8012729 00.jpg', " + "null, " + "'FIL 2,5² HO7V2K NOIR', " + "'Noir', " + "'NEXANS MAROC', "
				+ "'51507544500', " + "1.5, " + "null, " + "0.024, " + "3.45, " + "3.55, " + "false, " + "'Carton', "
				+ "false, " + "false, " + "null, " + "90.0, " + "'PVC', " + "49, " + "0.247, " + "'Rouge recuit', "
				+ "7.98, " + "450.0, " + "750.0, " + "ARRAY['Harmonisé'])");

		// 8012730 00
		statement.executeUpdate("insert into \"MonoConducteur section 2.5\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-8012730 00', " + "'8012730 00', "
				+ "'8012730 00.jpg', " + "null, " + "'FIL 2,5² HO7V2K ROUGE', " + "'Rouge', " + "'NEXANS MAROC', "
				+ "'51550544840', " + "2.5, " + "null, " + "0.024, " + "3.45, " + "3.55, " + "false, " + "'Carton', "
				+ "false, " + "false, " + "null, " + "90.0, " + "'PVC', " + "49, " + "0.247, " + "'Rouge recuit', "
				+ "7.98, " + "450.0, " + "750.0, " + "ARRAY['Harmonisé'])");

		// 8012734 00
		statement.executeUpdate("insert into \"MonoConducteur section 2.5\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-8012734 00', " + "'8012734 00', "
				+ "'8012734 00.jpg', " + "null, " + "'FIL 2,5² HO7V2K ORANGE', " + "'Orange', " + "'NEXANS MAROC', "
				+ "'51550544843', " + "2.5, " + "null, " + "0.024, " + "3.45, " + "3.55, " + "false, " + "'Carton', "
				+ "false, " + "false, " + "null, " + "90.0, " + "'PVC', " + "49, " + "0.247, " + "'Rouge recuit', "
				+ "7.98, " + "450.0, " + "750.0, " + "ARRAY['Harmonisé'])");

		// 8023087 00
		statement.executeUpdate("insert into \"MonoConducteur section 6\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-8023087 00', " + "'8023087 00', "
				+ "'8023087 00.jpg', " + "null, " + "'FIL 6² H07V2K VI', " + "'Violet', " + "'NEXANS MAROC', "
				+ "'51550544861', " + "6.0, " + "null, " + "0.0576, " + "4.55, " + "4.65, " + "false, " + "'Carton', "
				+ "false, " + "false, " + "null, " + "90.0, " + "'PVC', " + "84, " + "0.3, " + "'Rouge recuit', "
				+ "3.3, " + "450.0, " + "750.0, " + "ARRAY['Harmonisé'])");

		// 8023095 00
		statement.executeUpdate("insert into \"MonoConducteur section 4\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-8023095 00', " + "'8023095 00', " + "null, "
				+ "null, " + "'FIL 4² H07VK VIOLET', " + "'Violet', " + "'NEXANS MAROC', " + "'51550544929', " + "4.0, "
				+ "null, " + "0.0384, " + "4.0, " + "4.2, " + "false, " + "'Carton', " + "false, " + "false, "
				+ "null, " + "70.0, " + "'PVC', " + "56, " + "0.3, " + "'Rouge recuit', " + "4.95, " + "450.0, "
				+ "750.0, " + "ARRAY['Harmonisé'])");

		// 1004263 00
		statement.executeUpdate("insert into \"MonoConducteur section 10\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-1004263 00', " + "'1004263 00', " + "null, "
				+ "null, " + "'CAB 10² AU T2 NO', " + "'Noir', " + "'PRYSMIAN', " + "'7090421X1N', " + "10.0, "
				+ "null, " + "0.096, " + "null, " + "null, " + "false, " + "'Touret', " + "false, " + "false, "
				+ "null, " + "105.0, " + "'PVC', " + "null, " + "null, " + "null, " + "null, " + "null, " + "null, "
				+ "ARRAY['ISO 6722'])");

		// 54244 00
		statement.executeUpdate("insert into \"MonoConducteur section 10\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Section MM2\", " + "\"Section AWG\", "
				+ "\"Poids Cuivre\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\","
				+ "\"Présence Film Protecteur\"," + "Conditionnement," + "\"Présence Connecteur Gauche\","
				+ "\"Présence Connecteur Droit\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Nature Isolant\"," + "\"Nombre de Brins\"," + "\"Diamètre des Brins\","
				+ "\"Nature du Conducteur\"," + "\"Resistance Lineique\"," + "\"Tension Phase Terre\","
				+ "\"Tension Phase Phase\"," + "Normes) values (" + "'CFCA-54244 00', " + "'54244 00', "
				+ "'54244 00.pdf', " + "null, " + "'CAB 10² AU T2 RG', " + "'Rouge', " + "'PRYSMIAN', "
				+ "'7090421X1R', " + "10.0, " + "null, " + "0.096, " + "null, " + "null, " + "null, " + "'Touret', "
				+ "false, " + "false, " + "null, " + "105.0, " + "'PVC', " + "null, " + "null, " + "null, " + "null, "
				+ "null, " + "null, " + "ARRAY['ISO 6722'])");

		// 1004951 00
		statement.executeUpdate("insert into \"Cosse à Fût Ouvert\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "\"Longueur de Dénudage\"," + "Matière," + "Finition,"
				+ "Accrochage," + "Sécurité," + "Largeur," + "Hauteur," + "Drapeau," + "\"Type Enfichage\","
				+ "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\") values (" + "'CFCA-1004951 00', "
				+ "'1004951 00', " + "'1004951 00.pdf', " + "'1004951.jpg'," + "'RSB8152 F6,3-6 ETAME LAIT', "
				+ "'STOCKO CONTACT SA'," + "'S0.114.908'," + "'Femelle'," + "'Clip'," + "'Ouvert'," + "2.502,"
				+ "6.002," + "6.0," + "'Laiton (CuZn)'," + "'Etamé (Sn)'," + "false," + "false," + "6.3," + "0.80,"
				+ "false," + "'RSB8152'," + "3.6," + "4.8)");

		// 1004982 00
		statement.executeUpdate("insert into \"Cosse à Fût Ouvert\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "\"Longueur de Dénudage\"," + "Matière," + "Finition,"
				+ "Accrochage," + "Sécurité," + "Largeur," + "Hauteur," + "Drapeau," + "\"Type Enfichage\","
				+ "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\") values (" + "'CFCA-1004982 00', "
				+ "'1004982 00', " + "'', " + "'1004982.jpg'," + "'RSB7604 LAITON ETAME', " + "'STOCKO CONTACT SA',"
				+ "'S0.240.248'," + "'Femelle'," + "'Clip'," + "'Ouvert'," + "1.0," + "2.5," + "5.0,"
				+ "'Laiton (CuZn)'," + "'Etamé (Sn)'," + "false," + "false," + "4.8," + "0.80," + "false,"
				+ "'RSB7604'," + "2.5," + "3.6)");

		// 1021935 00
		statement.executeUpdate("insert into \"Cosse à Fût Ouvert\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "\"Longueur de Dénudage\"," + "Matière," + "Finition,"
				+ "Sécurité," + "Largeur," + "Hauteur," + "Drapeau," + "\"Type Enfichage\","
				+ "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\") values (" + "'CFCA-1021935 00', "
				+ "'1021935 00', " + "'', " + "'1021935.jpg'," + "'EMB L.ET. (RSB7771V-1.5)', " + "'STOCKO CONTACT SA',"
				+ "'S0.283.016'," + "'Embout'," + "'Embout'," + "'Ouvert'," + "0.75," + "1.5," + "8.0,"
				+ "'Laiton (CuZn)'," + "'Etamé (Sn)'," + "false," + "0.0," + "0.0," + "false," + "'RSB7771'," + "2.05,"
				+ "3.5)");

		// 105584 00
		statement.executeUpdate("insert into \"Cosse à Fût Ouvert\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "\"Longueur de Dénudage\"," + "Matière," + "Finition,"
				+ "Accrochage," + "Sécurité," + "Largeur," + "Hauteur," + "Drapeau," + "\"Type Enfichage\","
				+ "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\") values (" + "'CFCA-105584 00', "
				+ "'105584 00', " + "'105584 00.pdf', " + "'105584.jpg'," + "'RSB7858 F-2.5 L.ETAME', "
				+ "'STOCKO CONTACT SA'," + "'S0.205.408'," + "'Femelle'," + "'Clip'," + "'Ouvert'," + "1.0," + "2.5,"
				+ "6.0," + "'Laiton (CuZn)'," + "'Etamé (Sn)'," + "true," + "false," + "6.3," + "0.80," + "false,"
				+ "'RSB7858'," + "2.0," + "4.3)");

		// 13519 00
		statement.executeUpdate("insert into \"Cosse à Fût Ouvert\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "\"Longueur de Dénudage\"," + "Matière," + "Finition,"
				+ "Sécurité," + "Largeur," + "Hauteur," + "Drapeau," + "\"Type Enfichage\","
				+ "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\") values (" + "'CFCA-13519 00', "
				+ "'13519 00', " + "'13519 00.pdf', " + "'13519.jpg'," + "'CLIP H3 1A2.9 IN AVEC OPERCULE', "
				+ "'MOLEX FRANCE'," + "'989091025'," + "'Femelle'," + "'Clip'," + "'Ouvert'," + "1.0," + "2.9," + "7.0,"
				+ "'cuivre'," + "'pré-étamé'," + "false," + "0.0," + "0.0," + "false," + "'H3F'," + "0.0," + "500.0)");

		// 35863 00
		statement.executeUpdate("insert into \"Cosse à Fût Ouvert\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "\"Longueur de Dénudage\"," + "Finition," + "Sécurité,"
				+ "Drapeau," + "\"Type Enfichage\"," + "\"Diamètre Minimum Isolant\","
				+ "\"Diamètre Maximum Isolant\") values (" + "'CFCA-35863 00', " + "'35863 00', " + "'', "
				+ "'35863.jpg'," + "'CLIP H3 3A5 SN', " + "'MOLEX FRANCE'," + "'989091016'," + "'Femelle'," + "'Clip',"
				+ "'Ouvert'," + "3.0," + "5.0," + "7.0," + "'Etamé (Sn)'," + "false," + "false," + "'H3F'," + "0.0,"
				+ "500.0)");

		// 400442 00
		statement.executeUpdate("insert into \"Cosse à Fût Ouvert\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "\"Longueur de Dénudage\"," + "Sécurité," + "Drapeau,"
				+ "\"Type Enfichage\"," + "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\") values ("
				+ "'CFCA-400442 00', " + "'400442 00', " + "'400442 00.pdf', " + "'400442.jpg',"
				+ "'CAPOCORDA 929937-1', " + "'TYCO ELECTRONICS LOGISTICS AG'," + "'929937-1'," + "'Femelle',"
				+ "'Clip'," + "'Ouvert'," + "1.5," + "2.5," + "5.0," + "false," + "false," + "'JPTETF'," + "2.21,"
				+ "3.0)");

		// 52180 00
		statement.executeUpdate("insert into \"Cosse à Fût Ouvert\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "\"Longueur de Dénudage\"," + "Finition," + "Sécurité,"
				+ "Drapeau," + "\"Type Enfichage\"," + "\"Diamètre Minimum Isolant\","
				+ "\"Diamètre Maximum Isolant\") values (" + "'CFCA-52180 00', " + "'52180 00', " + "'52180 00.pdf', "
				+ "'52180.jpg'," + "'CLIP JPT 0.35A1 SN', " + "'TYCO ELECTRONICS LOGISTICS AG'," + "'144431-1',"
				+ "'Femelle'," + "'Clip'," + "'Ouvert'," + "0.5," + "1.0," + "4.0," + "'Etamé (Sn)'," + "false,"
				+ "false," + "'JPTF'," + "0.0," + "2.15)");

		// 8005905 00
		statement.executeUpdate("insert into \"Cosse à Fût Ouvert\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "\"Longueur de Dénudage\"," + "Matière," + "Finition,"
				+ "Accrochage," + "Sécurité," + "Largeur," + "Drapeau," + "\"Type Enfichage\","
				+ "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\") values (" + "'CFCA-8005905 00', "
				+ "'8005905 00', " + "'8005905 00.pdf', " + "'8005905.jpg'," + "'CLIP 9.5 ACCROCHAGE6²-10²', "
				+ "'ESPECIALIDADES EL ESCUBEDO SA'," + "'4384,02'," + "'Femelle'," + "'Clip'," + "'Ouvert'," + "6.0,"
				+ "10.0," + "6.8," + "'Acier inoxydable'," + "'Etamé (Sn)'," + "true," + "false," + "9.5," + "false,"
				+ "'9.5AccF'," + "0.0," + "7.7)");

		// 8030296 00
		statement.executeUpdate("insert into \"Cosse à Fût Ouvert\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "\"Longueur de Dénudage\"," + "Matière," + "Finition,"
				+ "Sécurité," + "Largeur," + "Hauteur," + "Drapeau," + "\"Type Enfichage\","
				+ "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\") values (" + "'CFCA-8030296 00', "
				+ "'8030296 00', " + "'8030296 00.pdf', " + "'8030296.jpg'," + "'CLIP 6,35 RSB7901 F6,3-2,5 LAIT ET', "
				+ "'STOCKO CONTACT SA'," + "'S0.161.896'," + "'Femelle'," + "'Clip'," + "'Ouvert'," + "1.0," + "2.5,"
				+ "6.0," + "'Laiton (CuZn)'," + "'Etamé (Sn)'," + "false," + "6.3," + "0.80," + "false," + "'RSB7901',"
				+ "2.5," + "3.7)");

		// 90681 00
		statement.executeUpdate("insert into \"Cosse à Fût Ouvert\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "\"Longueur de Dénudage\"," + "Matière," + "Finition,"
				+ "Accrochage," + "Sécurité," + "Largeur," + "Hauteur," + "Drapeau," + "\"Type Enfichage\","
				+ "\"Diamètre Minimum Isolant\"," + "\"Diamètre Maximum Isolant\") values (" + "'CFCA-90681 00', "
				+ "'90681 00', " + "'90681 00.pdf', " + "'90681.jpg'," + "'CLIP BOIT 6,3 4A6 CUZN SN', "
				+ "'TYCO ELECTRONICS LOGISTICS AG'," + "'180351-8'," + "'Femelle'," + "'Clip'," + "'Ouvert'," + "4.0,"
				+ "6.0," + "6.0," + "'Laiton (CuZn)'," + "'Etamé (Sn)'," + "true," + "false," + "6.3," + "0.80,"
				+ "false," + "'RSB7858'," + "3.43," + "5.08)");

		// 1002952 00
		statement.executeUpdate("insert into \"Cosse à Fût Fermé\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "Matière," + "Finition," + "Sécurité," + "\"Type Enfichage\","
				+ "Drapeau) values (" + "'CFCA-1002952 00', " + "'1002952 00', " + "'1002952 00.pdf', "
				+ "'1002952.jpg'," + "'COS TUB 0,25 A 1,5', " + "'CEMBRE'," + "'A03-M4'," + "'Ronde'," + "'NFC 20-130',"
				+ "'Fermé'," + "0.5," + "1.5," + "'Cuivre'," + "'Etamé (Sn)'," + "false," + "'CR04'," + "false)");

		// 1002955 00
		statement.executeUpdate("insert into \"Cosse à Fût Fermé\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "Matière," + "Finition," + "Sécurité," + "\"Type Enfichage\","
				+ "Drapeau) values (" + "'CFCA-1002955 00', " + "'1002955 00', " + "'1002955 00.pdf', "
				+ "'1002955.jpg'," + "'COS TUB DIA:4 L.ETAME 2.5²', " + "'CEMBRE'," + "'A06-M4'," + "'Ronde',"
				+ "'NFC 20-130'," + "'Fermé'," + "1.5," + "2.5," + "'Laiton'," + "'Etamé (Sn)'," + "false," + "'CR04',"
				+ "false)");

		// 1003495 00
		statement.executeUpdate("insert into \"Cosse à Fût Fermé\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "Matière," + "Finition," + "Sécurité," + "\"Type Enfichage\","
				+ "Drapeau) values (" + "'CFCA-1003495 00', " + "'1003495 00', " + "'1003495 00.pdf', "
				+ "'1003495.jpg'," + "'COS TUB 10-5', " + "'KLAUKE'," + "'CNF10-5'," + "'Ronde'," + "'NFC 20-130',"
				+ "'Fermé'," + "6.0," + "10.0," + "'Cuivre'," + "'Etamé (Sn)'," + "false," + "'CR05'," + "false)");

		// 1004939 00
		statement.executeUpdate("insert into \"Cosse à Fût Fermé\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "Matière," + "Finition," + "Sécurité," + "\"Type Enfichage\","
				+ "Drapeau) values (" + "'CFCA-1004939 00', " + "'1004939 00', " + "''," + "'1004939.jpg',"
				+ "'RSQ7117A10-6', " + "'STOCKO CONTACT SA'," + "'S0.102.202'," + "'Ronde',"
				+ "'Roulé Brasé din 46234'," + "'Fermé'," + "4.0," + "6.0," + "'Cuivre'," + "'Etamé (Sn)'," + "false,"
				+ "'CR10'," + "false)");

		// 1014501 00
		statement.executeUpdate("insert into \"Cosse à Fût Fermé\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "Matière," + "Finition," + "Sécurité," + "\"Type Enfichage\","
				+ "Drapeau) values (" + "'CFCA-1014501 00', " + "'1014501 00', " + "'1014501 00.pdf',"
				+ "'1014501.jpg'," + "'OEILLET TUB DIA.4 SECTION MAXI 4²', " + "'KLAUKE'," + "'CNF4-4'," + "'Ronde',"
				+ "'NFC 20-130'," + "'Fermé'," + "2.5," + "4.0," + "'Cuivre'," + "'Etamé (Sn)'," + "false," + "'CR04',"
				+ "false)");

		// 1021341 00
		statement.executeUpdate("insert into \"Cosse à Fût Fermé\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "Matière," + "Finition," + "Sécurité," + "\"Type Enfichage\","
				+ "Drapeau) values (" + "'CFCA-1021341 00', " + "'1021341 00', " + "'1021341 00.pdf',"
				+ "'1021341.jpg'," + "'COS TUB S2.5M10', " + "'CEMBRE'," + "'S2.5M10'," + "'Ronde',"
				+ "'Roulé Brasé din 46234'," + "'Fermé'," + "1.5," + "2.5," + "'Cuivre'," + "'Etamé (Sn)'," + "false,"
				+ "'CR10'," + "false)");

		// 1021988 00
		statement.executeUpdate("insert into \"Cosse à Fût Fermé\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "Sécurité," + "\"Type Enfichage\"," + "Drapeau) values ("
				+ "'CFCA-1021988 00', " + "'1021988 00', " + "''," + "'1021988.jpg'," + "'MANCHONS PARALLELES.4²A6²', "
				+ "'KLAUKE'," + "'151 R'," + "'Manchon'," + "'Manchon'," + "'Fermé'," + "4.0," + "6.0," + "false,"
				+ "'Epissure'," + "false)");

		// 1021989 00
		statement.executeUpdate("insert into \"Cosse à Fût Fermé\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "Sécurité," + "\"Type Enfichage\"," + "Drapeau) values ("
				+ "'CFCA-1021989 00', " + "'1021989 00', " + "''," + "'1021989.jpg',"
				+ "'MANCHONS.PARALLELES.10²A16²', " + "'KLAUKE'," + "'153 R'," + "'Manchon'," + "'Manchon',"
				+ "'Fermé'," + "10.0," + "16.0," + "false," + "'Epissure'," + "false)");

		// 1022011 00
		statement.executeUpdate("insert into \"Cosse à Fût Fermé\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "Sécurité," + "\"Type Enfichage\"," + "Drapeau) values ("
				+ "'CFCA-1022011 00', " + "'1022011 00', " + "''," + "'1022011.jpg'," + "'MANCHONS:PARALLELES 6²A10²', "
				+ "'KLAUKE'," + "'152 R'," + "'Manchon'," + "'Manchon'," + "'Fermé'," + "6.0," + "10.0," + "false,"
				+ "'Epissure'," + "false)");

		// 8000300 00
		statement.executeUpdate("insert into \"Cosse à Fût Fermé\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "Matière," + "Finition," + "Sécurité," + "\"Type Enfichage\","
				+ "Drapeau) values (" + "'CFCA-8000300 00', " + "'8000300 00', " + "'8000300 00.pdf',"
				+ "'8000300.jpg'," + "'COSSE TUB DIA:4 4²-6²', " + "'CEMBRE'," + "'A1-M4'," + "'Ronde',"
				+ "'NFC 20-130'," + "'Fermé'," + "4.0," + "6.0," + "'Cuivre'," + "'Etamé (Sn)'," + "false," + "'CR04',"
				+ "false)");

		// 8000545 00
		statement.executeUpdate("insert into \"Cosse à Fût Fermé\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "Sécurité," + "\"Type Enfichage\"," + "Drapeau) values ("
				+ "'CFCA-8000545 00', " + "'8000545 00', " + "'8000545 00.pdf'," + "'8000545.jpg',"
				+ "'MANCHON PREISOLE 1²-2.5²', " + "'MECATRACTION'," + "'BHT 2'," + "'Manchon'," + "'Manchon',"
				+ "'Fermé'," + "1.0," + "2.5," + "false," + "'Manchon'," + "false)");

		// 8001317 00
		statement.executeUpdate("insert into \"Cosse à Fût Fermé\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "Sécurité," + "\"Type Enfichage\"," + "Drapeau) values ("
				+ "'CFCA-8001317 00', " + "'8001317 00', " + "'8001317 00.jpg'," + "'8001317.jpg',"
				+ "'MANCHON PREISOLE ETANCHE 2.6²-6² JN', " + "'KLAUKE'," + "'700 WS'," + "'Manchon'," + "'Préisolé',"
				+ "'Fermé'," + "2.6," + "6.0," + "false," + "'Manchon'," + "false)");

		// 8020536 00
		statement.executeUpdate("insert into \"Cosse à Fût Fermé\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "Sécurité," + "\"Type Enfichage\"," + "Drapeau) values ("
				+ "'CFCA-8020536 00', " + "'8020536 00', " + "'8020536 00.jpg'," + "'8020536.jpg',"
				+ "'CLIP 2.8*0.5 PREISOLÉ (1.5²-2.5²)', " + "'KLAUKE'," + "'830/1 V'," + "'Femelle'," + "'Clip',"
				+ "'Fermé'," + "1.5," + "2.5," + "false," + "'2.8x0.5Pre'," + "false)");

		// 8026255 00
		statement.executeUpdate("insert into \"Cosse à Fût Fermé\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "Genre," + "Forme," + "\"Fût\"," + "\"Section Fil Admissible Min\","
				+ "\"Section Fil Admissible Max\"," + "Sécurité," + "\"Type Enfichage\"," + "Drapeau) values ("
				+ "'CFCA-8026255 00', " + "'8026255 00', " + "'8026255 00.pdf'," + "'8026255.jpg',"
				+ "'MANCHON A BUTEE 0,5-1,5² ISOLE', " + "'TYCO ELECTRONICS LOGISTICS AG'," + "'967398-000',"
				+ "'Manchon'," + "'Manchon'," + "'Fermé'," + "0.5," + "1.5," + "false," + "'Epissure'," + "false)");

		// Gaine Thermorétractable
		// Normes

		// 1000130 00
		statement.executeUpdate("insert into \"Gaine Thermorétractable SER\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Diamètre avant rétreint\"," + "\"Diamètre après rétreint\"," + "Longueur," + "Marquage,"
				+ "Collante," + "Epaisseur," + "\"Température rétreint\"," + "Matière) values (" + "'CFCA-1000130 00', "
				+ "'1000130 00', " + "'1000130 00.pdf'," + "'1000130.jpg'," + "'HTAT-16/4-0-40', " + "'Noir', "
				+ "'SRATI FRANCE', " + "'STF AT 16/4-40MM NO', " + "-55.0," + "110.0," + "16.0," + "4.0," + "40.0,"
				+ "null," + "true," + "0.8," + "120.0," + "'')");

		// 1004499 00
		statement.executeUpdate("insert into \"Gaine Thermorétractable SER\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Diamètre avant rétreint\"," + "\"Diamètre après rétreint\"," + "Longueur," + "Marquage,"
				+ "Collante," + "Epaisseur," + "\"Température rétreint\"," + "Matière) values (" + "'CFCA-1004499 00', "
				+ "'1004499 00', " + "''," + "'1004499.jpg'," + "'SER-12.7-35-ROUGE', " + "'Rouge', " + "'STERKELEC', "
				+ "'912-22109', " + "-40.0," + "135.0," + "12.7," + "6.4," + "35.0," + "''," + "false," + "0.65,"
				+ "120.0," + "'')");

		// 1004532 00
		statement.executeUpdate("insert into \"Gaine Thermorétractable SER\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Diamètre avant rétreint\"," + "\"Diamètre après rétreint\"," + "Longueur," + "Marquage,"
				+ "Collante," + "Epaisseur," + "\"Température rétreint\"," + "Matière) values (" + "'CFCA-1004532 00', "
				+ "'1004532 00', " + "''," + "'1004532.jpg'," + "'SER 6.40 25MM TRANSPARENT', " + "'Transparant', "
				+ "'SRATI FRANCE', " + "'SER6.4-25-TRANSP', " + "-40.0," + "135.0," + "6.4," + "3.2," + "25.0," + "'',"
				+ "false," + "0.65," + "120.0," + "'polyoléfine')");

		// 1015282 00
		statement.executeUpdate("insert into \"Gaine Thermorétractable SST\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Diamètre avant rétreint\"," + "\"Diamètre après rétreint\"," + "Longueur," + "Marquage,"
				+ "Collante," + "Epaisseur," + "\"Température rétreint\"," + "Matière) values (" + "'CFCA-1015282 00', "
				+ "'1015282 00', " + "'1015282 00.pdf'," + "''," + "'SST 12.7 LG35 NOIRE', " + "'Noir', "
				+ "'SRATI FRANCE', " + "'SST12.7-35-NO', " + "-45.0," + "125.0," + "12.7," + "6.4," + "35.0," + "'',"
				+ "false," + "0.65," + "80.0," + "'polyoléfine')");

		// 8001881 00
		statement.executeUpdate("insert into \"Gaine Thermorétractable SST\" (" + "\"Référence BE\","
				+ "\"Référence ERP\"," + "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur,"
				+ "Fabricant," + "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Diamètre avant rétreint\"," + "\"Diamètre après rétreint\"," + "Longueur," + "Marquage,"
				+ "Collante," + "Epaisseur," + "\"Température rétreint\"," + "Matière) values (" + "'CFCA-8001881 00', "
				+ "'8001881 00', " + "'8001881 00.pdf'," + "''," + "'MANCHON THERMO STF-0060 NOIR LG 30', " + "'Noir', "
				+ "'STERKELEC', " + "'912-22113', " + "-55.0," + "110.0," + "12.7," + "6.0," + "30.0," + "''," + "true,"
				+ "0.5," + "120.0," + "'polyoléfine')");

		// Gaine

		// 1002764 00
		statement.executeUpdate(
				"insert into \"Gaine Zipper\" (" + "\"Référence BE\"," + "\"Référence ERP\"," + "\"Fiche Technique\","
						+ "Représentation," + "Désignation," + "Couleur," + "Fabricant," + "\"Référence Fabricant\","
						+ "\"Température Min\"," + "\"Température Max\"," + "\"Diamètre Intérieur\", " + "Fendue,"
						+ "Profil," + "\"Longueur maximum\", " + "\"Diamètre Extérieur\", " + "Matière) values ("
						+ "'CFCA-1002764 00', " + "'1002764 00', " + "''," + "'gaineZipper.png',"
						+ "'GAINE ZIPPER DIA:40 NOIR', " + "'Noir', " + "'SERTIM', " + "'ZTZ 40 GP/S 63 B', " + "-20.0,"
						+ "105.0," + "40.0," + "true," + "''," + "100000.0," + "44.0," + "'PVC')");

		// 1010898 00
		statement.executeUpdate("insert into \"Gaine GAF\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Diamètre Intérieur\", " + "Fendue," + "Profil," + "\"Longueur maximum\", "
				+ "\"Diamètre Extérieur\", " + "Matière) values (" + "'CFCA-1010898 00', " + "'1010898 00', " + "'',"
				+ "'gaineAnneleeFendue.png'," + "'GAF PPAE DIAM 32', " + "'Noir', " + "'DELFINGEN MA CASABLANCA SARL', "
				+ "'10605', " + "-30.0," + "125.0," + "32.0," + "true," + "''," + "0.0," + "0.0," + "'PPAE')");

		// 1025188 00
		statement.executeUpdate(
				"insert into \"Gaine GAF\" (" + "\"Référence BE\"," + "\"Référence ERP\"," + "\"Fiche Technique\","
						+ "Représentation," + "Désignation," + "Couleur," + "Fabricant," + "\"Référence Fabricant\","
						+ "\"Température Min\"," + "\"Température Max\"," + "\"Diamètre Intérieur\", " + "Fendue,"
						+ "Profil," + "\"Longueur maximum\", " + "\"Diamètre Extérieur\", " + "Matière) values ("
						+ "'CFCA-1025188 00', " + "'1025188 00', " + "''," + "'gaineAnneleeFendue.png',"
						+ "'GAF PPAE DIAM:23 SOFLEX', " + "'Noir', " + "'DELFINGEN MA CASABLANCA SARL', " + "'01379', "
						+ "-30.0," + "125.0," + "23.0," + "true," + "''," + "100000.0," + "28.6," + "'PPAE')");

		// 105812 00
		statement.executeUpdate("insert into \"Gaine GAF\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Diamètre Intérieur\", " + "Fendue," + "Profil," + "\"Longueur maximum\", "
				+ "\"Diamètre Extérieur\", " + "Matière) values (" + "'CFCA-105812 00', " + "'105812 00', " + "'',"
				+ "'gaineAnneleeFendue.png'," + "'GAF.GFC PP AE 06 NOIRE  RLX', " + "'Noir', "
				+ "'DELFINGEN MA CASABLANCA SARL', " + "'1370', " + "-30.0," + "125.0," + "6.0," + "true," + "'',"
				+ "100000.0," + "28.6," + "'PPAE')");

		// 105813 00
		statement.executeUpdate("insert into \"Gaine GAF\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\","
				+ "\"Diamètre Intérieur\", " + "Fendue," + "Profil," + "\"Longueur maximum\", "
				+ "\"Diamètre Extérieur\", " + "Matière) values (" + "'CFCA-105813 00', " + "'105813 00', " + "'',"
				+ "'gaineAnneleeFendue.png'," + "'GAF GFC PP AE 09 NOIRE  RLX', " + "'Noir', "
				+ "'DELFINGEN MA CASABLANCA SARL', " + "'01369', " + "-30.0," + "125.0," + "9.0," + "true," + "'',"
				+ "100000.0," + "0.0," + "'PPAE')");

		// 105814 00
		statement.executeUpdate(
				"insert into \"Gaine GAF\" (" + "\"Référence BE\"," + "\"Référence ERP\"," + "\"Fiche Technique\","
						+ "Représentation," + "Désignation," + "Couleur," + "Fabricant," + "\"Référence Fabricant\","
						+ "\"Température Min\"," + "\"Température Max\"," + "\"Diamètre Intérieur\"," + "Fendue,"
						+ "Profil," + "\"Longueur maximum\"," + "\"Diamètre Extérieur\"," + "Matière) values ("
						+ "'CFCA-105814 00', " + "'105814 00', " + "''," + "'gaineAnneleeFendue.png',"
						+ "'GAF GFC PP AE 11 NOIRE  RLX', " + "'Noir', " + "'DELFINGEN MA CASABLANCA SARL', "
						+ "'01368', " + "-30.0," + "125.0," + "11.0," + "true," + "''," + "0.0," + "0.0," + "'PPAE')");

		// 105815 00
		statement.executeUpdate(
				"insert into \"Gaine GAF\" (" + "\"Référence BE\"," + "\"Référence ERP\"," + "\"Fiche Technique\","
						+ "Représentation," + "Désignation," + "Couleur," + "Fabricant," + "\"Référence Fabricant\","
						+ "\"Température Min\"," + "\"Température Max\"," + "\"Diamètre Intérieur\", " + "Fendue,"
						+ "Profil," + "\"Longueur maximum\", " + "\"Diamètre Extérieur\", " + "Matière) values ("
						+ "'CFCA-105815 00', " + "'105815 00', " + "''," + "'gaineAnneleeFendue.png',"
						+ "'GAF GFC PP AE 13 NOIRE  RLX', " + "'Noir', " + "'DELFINGEN MA CASABLANCA SARL', "
						+ "'01365', " + "-30.0," + "125.0," + "13.0," + "true," + "''," + "0.0," + "18.4," + "'PPAE')");

		// 105816 00
		statement.executeUpdate(
				"insert into \"Gaine GAF\" (" + "\"Référence BE\"," + "\"Référence ERP\"," + "\"Fiche Technique\","
						+ "Représentation," + "Désignation," + "Couleur," + "Fabricant," + "\"Référence Fabricant\","
						+ "\"Température Min\"," + "\"Température Max\"," + "\"Diamètre Intérieur\", " + "Fendue,"
						+ "Profil," + "\"Longueur maximum\", " + "\"Diamètre Extérieur\", " + "Matière) values ("
						+ "'CFCA-105816 00', " + "'105816 00', " + "''," + "'gaineAnneleeFendue.png',"
						+ "'GAF GFC PP AE 16 NOIRE  RLX', " + "'Noir', " + "'DELFINGEN MA CASABLANCA SARL', "
						+ "'01373', " + "-30.0," + "125.0," + "16.0," + "true," + "''," + "0.0," + "0.0," + "'PPAE')");

		// 105817 00
		statement.executeUpdate(
				"insert into \"Gaine GAF\" (" + "\"Référence BE\"," + "\"Référence ERP\"," + "\"Fiche Technique\","
						+ "Représentation," + "Désignation," + "Couleur," + "Fabricant," + "\"Référence Fabricant\","
						+ "\"Température Min\"," + "\"Température Max\"," + "\"Diamètre Intérieur\", " + "Fendue,"
						+ "Profil," + "\"Longueur maximum\", " + "\"Diamètre Extérieur\", " + "Matière) values ("
						+ "'CFCA-105817 00', " + "'105817 00', " + "''," + "'gaineAnneleeFendue.png',"
						+ "'GAF GFC PP AE 19 NOIRE  RLX', " + "'Noir', " + "'DELFINGEN MA CASABLANCA SARL', "
						+ "'01376', " + "-30.0," + "125.0," + "19.0," + "true," + "''," + "0.0," + "0.0," + "'PPAE')");

		// 8000159 00
		statement.executeUpdate(
				"insert into \"Gaine Zipper\" (" + "\"Référence BE\"," + "\"Référence ERP\"," + "\"Fiche Technique\","
						+ "Représentation," + "Désignation," + "Couleur," + "Fabricant," + "\"Référence Fabricant\","
						+ "\"Température Min\"," + "\"Température Max\"," + "\"Diamètre Intérieur\", " + "Fendue,"
						+ "Profil," + "\"Longueur maximum\", " + "\"Diamètre Extérieur\", " + "Matière) values ("
						+ "'CFCA-8000159 00', " + "'8000159 00', " + "''," + "'gaineZipper.png',"
						+ "'GAINE.ZIPPER PVC DIA:60', " + "'Noir', " + "'SERTIM', " + "'ZTZ 60 GP/S 63 B', " + "-20.0,"
						+ "105.0," + "60.0," + "true," + "''," + "100000.0," + "64.0," + "'PVC')");

		// Connecteur

		// 1003045 00
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201201, '1', ARRAY['RSB7858'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201202, '2', ARRAY['RSB7858'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201203, '3', ARRAY['RSB7858'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201204, '4', ARRAY['RSB7858'])");

		statement.executeUpdate("insert into \"Connecteur 4 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-1003045 00', " + "'1003045 00', " + "'1003045 00.pdf',"
				+ "'1003045.jpg'," + "'BT 4V VV 2026.200-G PA6 NAT NO FLAME', " + "'Blanc', " + "'STOCKO FRANCE S.A.', "
				+ "'S0.104.092', " + "0.0," + "0.0," + "''," + "4," + "false," + "false," + "false," + "false,"
				+ "null," + "ARRAY[3201201, 3201202, 3201203, 3201204]," + "null)");

		// 1003402 00
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201205, '1', ARRAY['RSB7858'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201206, '2', ARRAY['RSB7858'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201207, '3', ARRAY['RSB7858'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201208, '4', ARRAY['RSB7858'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201209, '5', ARRAY['RSB7858'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201210, '6', ARRAY['Tyc2.8AccF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201211, '7', ARRAY['Tyc2.8AccF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201212, '8', ARRAY['Tyc2.8AccF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201213, '9', ARRAY['Tyc2.8AccF'])");

		// 1003402 00
		statement.executeUpdate("insert into \"Connecteur 9 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-1003402 00', " + "'1003402 00', " + "''," + "'1003402.jpg',"
				+ "'BOITIER RELAI HELLA', " + "'Noir', " + "'HELLA FRANCE SA', " + "'8JA003526-002', " + "0.0," + "0.0,"
				+ "''," + "9," + "false," + "true," + "false," + "false," + "null,"
				+ "ARRAY[3201205, 3201206, 3201207, 3201208, 3201209, 3201210, 3201211, 3201212, 3201213]," + "null)");

		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201214, '1', ARRAY['RSB7858'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201215, '2', ARRAY['RSB7858'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201216, '3', ARRAY['RSB7858'])");

		// 1005055 00
		statement.executeUpdate("insert into \"Connecteur 3 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-1005055 00', " + "'1005055 00', " + "''," + "'1005055.jpg',"
				+ "'BT 3 VS PC VV 2027-200-G NO FLAME', " + "'', " + "'STOCKO FRANCE S.A.', " + "'S0.102.525', "
				+ "0.0," + "0.0," + "''," + "3," + "false," + "false," + "false," + "false," + "null,"
				+ "ARRAY[3201214, 3201215, 3201216]," + "null)");

		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201217, '1', ARRAY['RSB7900','RSB7901','RSB8115'])");

		// 1005083 00
		statement.executeUpdate("insert into \"Connecteur 1 Voie\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-1005083 00', " + "'1005083 00', " + "''," + "'1005083.jpg',"
				+ "'EH 649-G PA6 V2 NAT NO FLAME', " + "'Blanc', " + "'STOCKO FRANCE S.A.', " + "'S0.102.187', "
				+ "0.0," + "0.0," + "''," + "1," + "false," + "false," + "false," + "false," + "null,"
				+ "ARRAY[3201217]," + "null)");

		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201218, '1', ARRAY['RSB7858'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201219, '2', ARRAY['RSB7858'])");

		// 1005087 00
		statement.executeUpdate("insert into \"Connecteur 2 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-1005087 00', " + "'1005087 00', " + "''," + "'1005087.jpg',"
				+ "'VV2041.200-G PA6 NAT NO FLAME', " + "'Blanc', " + "'STOCKO FRANCE S.A.', " + "'S0.104.705', "
				+ "0.0," + "0.0," + "''," + "2," + "false," + "false," + "false," + "false," + "null,"
				+ "ARRAY[3201218, 3201219]," + "null)");

		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201220, '1', ARRAY['RSB7858'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201221, '2', ARRAY['RSB7858'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201222, '3', ARRAY['9.5AccF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201223, '4', ARRAY['RSB7858'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201224, '5', ARRAY['9.5AccF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201225, '6', ARRAY['RSB7785'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201226, '7', ARRAY['RSB7785'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201227, '8', ARRAY['RSB7785'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201228, '9', ARRAY['RSB7785'])");

		// 1009191 00
		statement.executeUpdate("insert into \"Connecteur 9 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-1009191 00', " + "'1009191 00', " + "''," + "'1009191.jpg',"
				+ "'BOITIER RELAIS 9 VS', " + "'', " + "'FRANCIS BRUN SA', " + "'RE410005', " + "0.0," + "0.0," + "'',"
				+ "9," + "false," + "false," + "false," + "false," + "null,"
				+ "ARRAY[3201220, 3201221, 3201222, 3201223, 3201224, 3201225, 3201226, 3201227, 3201228]," + "null)");

		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201229, '1', ARRAY['RSB7603','RSB7604','RSB8170'])");

		// 1023757 00
		statement.executeUpdate("insert into \"Connecteur 1 Voie\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-1023757 00', " + "'1023757 00', " + "'1023757 00.pdf',"
				+ "'1023757.jpg'," + "'CAP NA 4.8 EH 650-100-G NO FLAME', " + "'Blanc', " + "'STOCKO FRANCE S.A.', "
				+ "'102218', " + "0.0," + "0.0," + "''," + "1," + "false," + "false," + "false," + "false," + "null,"
				+ "ARRAY[3201229]," + "null)");

		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201230, '1', ARRAY['JPTETF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201231, '2', ARRAY['JPTETF'])");

		// 1025203 00
		statement.executeUpdate("insert into \"Connecteur 2 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-1025203 00', " + "'1025203 00', " + "'1025203 00.jpg',"
				+ "'1025203.jpg'," + "'ENSEMBLE CONNECTEUR 2VS', " + "'Blanc', " + "'TYCO ELECTRONICS LOGISTICS AG', "
				+ "'1-828962-1', " + "0.0," + "0.0," + "''," + "2," + "true," + "false," + "false," + "false," + "null,"
				+ "ARRAY[3201230, 3201231]," + "null)");

		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201232, '1', ARRAY['JPTF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201233, '2', ARRAY['JPTF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201234, '3', ARRAY['JPTF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201235, '4', ARRAY['JPTF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201236, '5', ARRAY['JPTF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201237, '6', ARRAY['JPTF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201238, '7', ARRAY['JPTF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201239, '8', ARRAY['JPTF'])");

		// 50718 00
		statement.executeUpdate("insert into \"Connecteur 8 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-50718 00', " + "'50718 00', " + "'50718 00.pdf'," + "'50718.jpg',"
				+ "'BT PC JPTAR-A 8V NO', " + "'Noir', " + "'TYCO ELECTRONICS LOGISTICS AG', " + "'144172-1', " + "0.0,"
				+ "0.0," + "''," + "8," + "false," + "false," + "false," + "false," + "null,"
				+ "ARRAY[3201232, 3201233, 3201234, 3201235, 3201236, 3201237, 3201238, 3201239]," + "null)");

		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201240, '1', ARRAY['JPTF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201241, '2', ARRAY['JPTF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201242, '3', ARRAY['JPTF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201243, '4', ARRAY['JPTF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201244, '5', ARRAY['JPTF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201245, '6', ARRAY['JPTF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201246, '7', ARRAY['JPTF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201247, '8', ARRAY['JPTF'])");

		// 50719 00
		statement.executeUpdate("insert into \"Connecteur 8 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-50719 00', " + "'50719 00', " + "'50719 00.pdf'," + "'50719.jpg',"
				+ "'BT PC JPTAR-B 8V NO', " + "'Noir', " + "'TYCO ELECTRONICS LOGISTICS AG', " + "'144172-2', " + "0.0,"
				+ "0.0," + "''," + "8," + "false," + "false," + "false," + "false," + "null,"
				+ "ARRAY[3201240, 3201241, 3201242, 3201243, 3201244, 3201245, 3201246, 3201247]," + "null)");

		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201248, '1', ARRAY['RSB7901'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201249, '2', ARRAY['RSB7901'])");

		// 73283 00
		statement.executeUpdate("insert into \"Connecteur 2 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-8001178 00', " + "'8001178 00', " + "'8001178 00.pdf',"
				+ "'8001178.jpg'," + "'PORTE FUSIBLE CLIP 6.35', " + "'Noir', "
				+ "'MTA - MECCANOTECNICA CODOGNESE SPA', " + "'01.00351', " + "0.0," + "0.0," + "''," + "2," + "false,"
				+ "false," + "false," + "false," + "null," + "ARRAY[3201248, 3201249]," + "null)");

		// 8001178 00
		statement.executeUpdate("insert into \"Connecteur 2 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-8001178 00', " + "'8001178 00', " + "'8001178 00.pdf'," + "'',"
				+ "'PORTE FUSIBLE CLIP 6.35', " + "'Noir', " + "'MTA - MECCANOTECNICA CODOGNESE SPA', " + "'01.00351', "
				+ "0.0," + "0.0," + "''," + "2," + "false," + "false," + "false," + "false," + "null,"
				+ "ARRAY[3201248, 3201249]," + "null)");

		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201250, '1', ARRAY['H3F'])");
		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201251, '2', ARRAY['H3F'])");
		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201252, '3', ARRAY['H3F'])");
		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201253, '4', ARRAY['H3F'])");
		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201254, '5', ARRAY['H3F'])");
		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201255, '6', ARRAY['H3F'])");
		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201256, '7', ARRAY['H3F'])");
		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201257, '8', ARRAY['H3F'])");
		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201258, '9', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201259, '10', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201260, '11', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201261, '12', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201262, '13', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201263, '14', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201264, '15', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201265, '16', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201266, '17', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201267, '18', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201268, '19', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201269, '20', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201270, '21', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201271, '22', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201272, '23', ARRAY['H3F'])");

		// 8001341 00
		statement.executeUpdate("insert into \"Connecteur 23 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-8001341 00', " + "'8001341 00', " + "''," + "'8001341.jpg',"
				+ "'P.C. H3 23V MOBILE', " + "'', " + "'MOLEX FRANCE', " + "'989571031', " + "0.0," + "0.0," + "'',"
				+ "23," + "false," + "false," + "false," + "false," + "null,"
				+ "ARRAY[3201250, 3201251,3201252,3201253,3201254,3201255,3201256,3201257,3201258,3201259,3201260,3201261,3201262,3201263,3201264,3201265,3201266,3201267,3201268,3201269,3201270,3201271,3201272],"
				+ "null)");

		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201273, '1', ARRAY['H3F'])");
		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201274, '2', ARRAY['H3F'])");
		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201275, '3', ARRAY['H3F'])");
		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201276, '4', ARRAY['H3F'])");
		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201277, '5', ARRAY['H3F'])");
		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201278, '6', ARRAY['H3F'])");
		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201279, '7', ARRAY['H3F'])");
		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201280, '8', ARRAY['H3F'])");
		statement
				.executeUpdate("insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201281, '9', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201282, '10', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201283, '11', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201284, '12', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201285, '13', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201286, '14', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201287, '15', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201288, '16', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201289, '17', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201290, '18', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201291, '19', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201292, '20', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201293, '21', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201294, '22', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201295, '23', ARRAY['H3F'])");

		// 8001407 00
		statement.executeUpdate("insert into \"Connecteur 23 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-8001407 00', " + "'8001407 00', " + "''," + "'8001407.jpg',"
				+ "'P.C. H3 23V NOIR FIXE+JOINT', " + "'Noir', " + "'MOLEX FRANCE', " + "'989581031', " + "0.0,"
				+ "0.0," + "''," + "23," + "false," + "false," + "false," + "false," + "null,"
				+ "ARRAY[3201273, 3201274, 3201275,3201276,3201277,3201278,3201279,3201280,3201281,3201282,3201283,3201284,3201285,3201286,3201287,3201288,3201289,3201290,3201291,3201292,3201293,3201294,3201295],"
				+ "null)");

		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201296, '1', ARRAY['Denudage'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201297, '2', ARRAY['Denudage'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201298, '3', ARRAY['Denudage'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (3201299, '4', ARRAY['Denudage'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012100, '5', ARRAY['Denudage'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012101, '6', ARRAY['Denudage'])");

		// 8001540 00
		statement.executeUpdate("insert into \"Connecteur 6 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-8001540 00', " + "'8001540 00', " + "'8001540 00.pdf',"
				+ "'8001540.jpg'," + "'CONNECTEUR MALE 6VS', " + "'Noir', " + "'WAGO CONTACT', " + "'231606', " + "0.0,"
				+ "0.0," + "''," + "6," + "false," + "false," + "false," + "false," + "null,"
				+ "ARRAY[3201296, 3201297, 3201298,3201299,32012100,32012101]," + "null)");

		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012102, '1', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012103, '2', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012104, '3', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012105, '4', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012106, '5', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012107, '6', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012108, '7', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012109, '8', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012110, '9', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012111, '10', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012112, '11', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012113, '12', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012114, '13', ARRAY['H3F'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012115, '14', ARRAY['H3F'])");

		// 8008215 00
		statement.executeUpdate("insert into \"Connecteur 14 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-8008215 00', " + "'8008215 00', " + "''," + "'',"
				+ "'CONNECTEUR H3 14 VOIES MOBILE', " + "'', " + "'MOLEX FRANCE', " + "'989571021', " + "0.0," + "0.0,"
				+ "''," + "14," + "false," + "false," + "false," + "false," + "null,"
				+ "ARRAY[32012102, 32012103, 32012104, 32012105, 32012106, 32012107, 32012108, 32012109, 32012110, 32012112, 32012113, 32012114, 32012115],"
				+ "null)");

		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012116, '1', ARRAY['JPTF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012117, '2', ARRAY['JPTF'])");

		// 8017516 00
		statement.executeUpdate("insert into \"Connecteur 2 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-8017516 00', " + "'8017516 00', " + "''," + "'8017516.jpg',"
				+ "'BT HP 2 VOIES', " + "'', " + "'SAVOY TECHNOLOGY', " + "'16300-562-699', " + "0.0," + "0.0," + "'',"
				+ "2," + "false," + "false," + "false," + "false," + "null," + "ARRAY[32012116, 32012117]," + "null)");

		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012118, 'moins1', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012119, 'moins2', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012120, 'moins3', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012121, 'moins4', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012122, 'moins5', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012123, 'moins6', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012124, 'moins7', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012125, 'moins8', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012126, 'moins9', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012127, 'moins10', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012128, 'moins11', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012129, 'moins12', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012130, 'B', ARRAY['CR05'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012131, 'H', ARRAY['CR05'])");

		// 8022347 00
		statement.executeUpdate("insert into \"Connecteur 14 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-8022347 00', " + "'8022347 00', " + "''," + "'8022347.jpg',"
				+ "'PORTE FUSIBLE SANS CAPOT 6VS BLUE SEA SYSTEMS', " + "'', " + "'BLUE SEA SYSTEMS', " + "'5030B', "
				+ "0.0," + "0.0," + "''," + "14," + "false," + "false," + "false," + "false," + "null,"
				+ "ARRAY[32012118, 32012119,32012120,32012121, 32012122, 32012123, 32012124, 32012125, 32012126, 32012127, 32012128, 32012129, 32012130, 32012131],"
				+ "null)");

		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012132, '1', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012133, '2', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012134, '3', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012135, '4', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012136, '5', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012137, '6', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012138, '7', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012139, '8', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012140, '9', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012141, '10', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012142, '11', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012143, '12', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012144, 'moins1', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012145, 'moins2', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012146, 'moins3', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012147, 'moins4', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012148, 'moins5', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012149, 'moins6', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012150, 'moins7', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012151, 'moins8', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012152, 'moins9', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012153, 'moins10', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012154, 'moins11', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012155, 'moins12', ARRAY['CR04'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012156, 'B', ARRAY['CR05'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (32012157, 'H', ARRAY['CR05'])");

		// 8022348 00
		statement.executeUpdate("insert into \"Connecteur 26 Voies\" (" + "\"Référence BE\"," + "\"Référence ERP\","
				+ "\"Fiche Technique\"," + "Représentation," + "Désignation," + "Couleur," + "Fabricant,"
				+ "\"Référence Fabricant\"," + "\"Température Min\"," + "\"Température Max\"," + "Genre,"
				+ "\"Nombre De Voies\"," + "Etanche," + "\"Couleur Marquage Associé\","
				+ "\"Sertissage Après Enfilage\"," + "GWT," + "\"Connecteur Inverse\"," + "Ports,"
				+ "Orientations) values (" + "'CFCA-8022348 00', " + "'8022348 00', " + "''," + "'8022348.jpg',"
				+ "'PORTE FUSIBLE SS CAPOT 12VS BLUE SEA SYSTEMS', " + "'', " + "'BLUE SEA SYSTEMS', " + "'5031B', "
				+ "0.0," + "0.0," + "''," + "26," + "false," + "false," + "false," + "false," + "null,"
				+ "ARRAY[32012132, 32012133,32012134,32012135,32012136, 32012137, 32012138, 32012139,32012140,32012141,32012142,32012143,32012144,32012145,32012146,32012147,32012148,32012149,32012150,32012151,32012152,32012153,32012154,32012155,32012156,32012157],"
				+ "null)");

		t.rollback();
	}

	@Test
	public void testCFCAODBASEPortTest() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		s.setReferenceLanguage(OntoQLHelper.FRENCH);
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		s.setDefaultNameSpace("http://www.cfca.fr/");

		// Mono-conducteur
		statement.executeUpdate(
				"insert into \"MonoConducteur section 0.2\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 0.22\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 0.25\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 0.34\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 0.35\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 0.38\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 0.4\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 0.5\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 0.6\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 0.63\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 0.75\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 0.75\" (\"Référence BE\",\"Référence ERP\",Fabricant, \"Section MM2\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\") values ('CFCA-1025761', '1025761', 'DRAKA CABLES VIGO', 0.75, 1.7, 1.9)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 0.81\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 1\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 1.31\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 1.4\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 1.5\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 2\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 2.07\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 2.5\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 3\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 3.31\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 4\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 5\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 5.30\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 6\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 7\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 10\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 16\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 20\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 25\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 35\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 40\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 50\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 60\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 70\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 75\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 81\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 95\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");
		statement.executeUpdate(
				"insert into \"MonoConducteur section 120\" (Normes, \"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\", \"Section MM2\", \"Section AWG\", \"Poids Cuivre\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\", \"Présence Film Protecteur\",Conditionnement,\"Présence Connecteur Gauche\",\"Présence Connecteur Droit\",\"Température Min\",\"Température Max\",\"Nature Isolant\",\"Nombre de Brins\",\"Diamètre des Brins\",\"Nature du Conducteur\",\"Resistance Lineique\",\"Tension Phase Terre\",\"Tension Phase Phase\") values (ARRAY['ISO 666'], 'CFCA-1708', 'ERP-2904', 'http://www.forum-auto.com/pole-technique/mecanique-electronique/sujet546734.htm', 'http://images.forum-auto.com/mesimages/969803/BOULON20FIXATION20COLLIER20ECH.20MBK2041691.jpg','Ceci est un boulon très intéressant', 'Blanc', 'Peugeot', 'Peugeot-34654-4654', 37.2, 38, 12.5,2.5,3.5,true, 'papier', true, false, 2.5, 35.5, 'carton', 5, 45.5,'papier', 45.5,15.5,54.5)");

		// Accessoire (Accessoires Etanches)
		statement.executeUpdate(
				"insert into \"Accessoire Etanche\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\") values ('CFCA-8022802 00', '8022802 00', '', '','OBTURATEUR POUR CONTACT MQS CLEAN BODY', 'Jaune', 'TYCO ELECTRONICS LOGISTICS AG', '284583-1')");
		statement.executeUpdate(
				"insert into \"Accessoire Etanche\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\") values ('CFCA-8022802 00', '8022803 00', '', '','OBTURATEUR PR CONTACT 1,5 CLEAN  BODY', 'Blanc', 'TYCO ELECTRONICS LOGISTICS AG', '284583-1')");

		// Accessoire (Accessoires Connecteur)
		statement.executeUpdate(
				"insert into \"Accessoire Connecteur\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\") values ('CFCA-1002472 00', '1002472 00', '', '','DOMINO BA 2V PAS DE 8MM', 'Blanc', 'AFLOX ACHATS VERRE', '1013440002')");
		statement.executeUpdate(
				"insert into \"Accessoire Connecteur\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Couleur,Fabricant,\"Référence Fabricant\") values ('CFCA-1002473 00', '1002473 00', '', '','DOMINO BA 3V', 'Blanc', 'AFLOX ACHATS VERRE', '1013440003')");

		// Cosse
		statement.executeUpdate(
				"insert into \"Cosse à Fût Ouvert\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8014906 00', '8014906 00', '', '','EMBOUT SPLICE LAITON ETAMÉ LARGEUR:4MM', 'SM CONTACT', '890 4 30 LE')");
		statement.executeUpdate(
				"insert into \"Cosse à Fût Ouvert\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1003373 00', '1003373 00', '', '','EMBOUT 0.28o/0.8o', 'SAVOY TECHNOLOGY', '25424123009')");
		statement.executeUpdate(
				"insert into \"Cosse à Fût Ouvert\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\", \"Type Enfichage\", \"Section Fil Admissible Min\",\"Section Fil Admissible Max\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\") values ('CFCA-1021965 00', '1021965 00', '', '','CLIP 2.8X0.5 LE (RSB7785F.2.8-1)', 'STOCKO CONTACT SA', 'S0.139.158', 'RSB7785', 0.5, 1.0, 1.55,2.6)");
		statement.executeUpdate(
				"insert into \"Cosse à Fût Ouvert\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\", \"Type Enfichage\", \"Section Fil Admissible Min\",\"Section Fil Admissible Max\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\") values ('CFCA-1021964 00', '1021964 00', '', '','CLIP 2.8X0.8 LN (RSB7785.001F2.8-1)', 'STOCKO CONTACT SA', 'S0.335.081', 'RSB7785', 0.5, 1.0, 1.55,2.6)");
		statement.executeUpdate(
				"insert into \"Cosse à Fût Ouvert\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\", \"Type Enfichage\", \"Section Fil Admissible Min\",\"Section Fil Admissible Max\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\") values ('CFCA-8009969 00', '8009969 00', '', '','CLIP 2.8X0.8 L.B DIN RSB7785.003-F-2.8-1 SURMOULE', 'STOCKO CONTACT SA', 'S0.180.315', 'RSB7785', 0.5, 1.0, 1.55,2.6)");
		statement.executeUpdate(
				"insert into \"Cosse à Fût Ouvert\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\", \"Type Enfichage\", \"Section Fil Admissible Min\",\"Section Fil Admissible Max\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\") values ('CFCA-8028824 00', '8028824 00', '', '','CLIP 9,5 ACCRO (6²-8²)', 'MTA  MECCANOTECNICA CODOGNESE SPA', '1707741', '9.5AccF', 6.0, 8.0, 4.3,5.7)");
		statement.executeUpdate(
				"insert into \"Cosse à Fût Ouvert\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\", \"Type Enfichage\", \"Section Fil Admissible Min\",\"Section Fil Admissible Max\", \"Diamètre Minimum Isolant\",\"Diamètre Maximum Isolant\") values ('CFCA-8005905 00', '8005905 00', '', '','CLIP 9.5 ACCROCHAGE6²-10²', 'ESPECIALIDADES EL ESCUBEDO SA', '4384,02', '9.5AccF', 6.0, 10.0, 4.3,7.7)");
		statement.executeUpdate(
				"insert into \"Cosse à Fût Fermé\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1004033 00', '1004033 00', '', '1003497 00 i..jpg','EMB 1² DZ5CE0105 PREISOLE', 'RS COMPONENTS', '254-6413')");
		statement.executeUpdate(
				"insert into \"Cosse à Fût Fermé\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1004034 00', '1004034 00', '', '1003497 00 i..jpg','EMB 2.5² DZ5CE0255', 'CEMBRE', 'PKE2508')");

		// DPI
		statement.executeUpdate(
				"insert into HE14 (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8026307 00', '8026307 00', '', '','BT DPI 5VS HE14 COUDE PR CAB PLAT', 'RS COMPONENTS', '189-9479')");
		statement.executeUpdate(
				"insert into \"DPI Pas de 2.5\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8009928 00', '8009928 00', '', '','CONNECT 3 VS DPI MKF13473-6-10-303', 'STOCKO CONTACT SA', 'S0.479.045')");
		statement.executeUpdate(
				"insert into \"DPI Pas de 2.5\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8003579 00', '8003579 00', '', '','CONNECTEUR 3 VOIES DPI MKF 13473-6-0-303', 'STOCKO CONTACT SA', 'S0.219.061')");
		statement.executeUpdate(
				"insert into \"DPI Pas de 3.96\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8025326 00', '8025326 00', '', '','BT DPI MTA156 11P 0,5² JA', 'TYCO ELECTRONICS LOGISTICS AG', '4-640427-1')");
		statement.executeUpdate(
				"insert into \"DPI Pas de 3.96\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8025333 00', '8025333 00', '', '','BT DPI MTA156 9P 0,5² JA', 'TYCO ELECTRONICS LOGISTICS AG', '3-640427-9')");
		statement.executeUpdate(
				"insert into \"DPI Pas de 5\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1018555 00', '1018555 00', '', '','CONNECTEUR DPI 3VOIES BLANC MTL1', 'STOCKO CONTACT SA', 'S0.134.677')");
		statement.executeUpdate(
				"insert into \"DPI Pas de 5\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1004926 00', '1004926 00', '', '','MTL1F6717-6-000-707', 'STOCKO CONTACT SA', 'S0.303.525')");
		statement.executeUpdate(
				"insert into \"RAST 2.5\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8010013 00', '8010013 00', '', '','CONNEC. RAST 2.5 12 VS', 'TYCO ELECTRONICS LOGISTICS AG', '1-1355182-2')");
		statement.executeUpdate(
				"insert into \"RAST 2.5\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8010014 00', '8010014 00', '', '','CONNEC. RAST 2.5  6VS', 'TYCO ELECTRONICS LOGISTICS AG', '1-1355181-6')");
		statement.executeUpdate(
				"insert into \"RAST 2.5 MK2\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8022207 00', '8022207 00', '', '','CONNECT DPI RAST 2,5 4VS MARKII (A CLOISONS)', 'TYCO ELECTRONICS LOGISTICS AG', '1-1740501-4')");
		statement.executeUpdate(
				"insert into \"RAST 2.5 MK2\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8022208 00', '8027106 00', '', '','CONECT DPI RAST 2,5 6VS MARKII (A CLOISONS)', 'TYCO ELECTRONICS LOGISTICS AG', '2-1740501-6')");
		statement.executeUpdate(
				"insert into \"RAST 2.5 PRO\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8027143 00', '8027143 00', '', '','RAST 2,5 2VS RG DUOPLUG POWER', 'TYCO ELECTRONICS LOGISTICS AG', '1-1740533-2')");
		statement.executeUpdate(
				"insert into \"RAST 2.5 PRO\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8027148 00', '8027148 00', '', '','RAST 2,5 4VS BE DUOPLUG POWER', 'TYCO ELECTRONICS LOGISTICS AG', '1-1740533-4')");
		statement.executeUpdate(
				"insert into \"RAST 2.5 MK1\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8027091 00', '8027091 00', '', '','RAST2,5 4VS NO DUOPLUG MK1', 'TYCO ELECTRONICS LOGISTICS AG', '2-284932-4')");
		statement.executeUpdate(
				"insert into \"RAST 2.5 MK1\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8027106 00', '8027106 00', '', '','RAST 2,5 6VS RGE DUOPLUG MK1', 'TYCO ELECTRONICS LOGISTICS AG', '0-284932-6')");
		statement.executeUpdate(
				"insert into \"RAST 2.5 PRO MK2\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8024709 00', '8024709 00', '', '','CONNECT DPI 4VS RAST 2,5 POWER MKII', 'TYCO ELECTRONICS LOGISTICS AG', '0-1534415-4')");
		statement.executeUpdate(
				"insert into \"RAST 2.5 PRO MK2\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8023869 00', '8023869 00', '', '','CONNECT DPI 2VS RAST 2,5 POWER MKII', 'TYCO ELECTRONICS LOGISTICS AG', '0-1534415-2')");
		statement.executeUpdate(
				"insert into \"RAST 5\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8029248 00', '8029248 00', '', '','MFMP 9561-005-80A-960-000-00', 'STOCKO CONTACT SA', 'S0.108.575')");
		statement.executeUpdate(
				"insert into \"RAST 5\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8029250 00', '8029250 00', '', '','MFMP 9586-003-80A-960-000-00', 'STOCKO CONTACT SA', 'S0.105.112')");
		statement.executeUpdate(
				"insert into \"RAST 5 MK1\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8026806 00', '8026806 00', '', '','RAST5_TAB MONOSHAPE MK1 2VS NO', 'TYCO ELECTRONICS LOGISTICS AG', '1-282002-1')");
		statement.executeUpdate(
				"insert into \"RAST 5 MK1\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8026843 00', '8026843 00', '', '','RAST5 5VS TAB-LIF MONOSHAPE MK1', 'TYCO ELECTRONICS LOGISTICS AG', '0-293141-2')");
		statement.executeUpdate(
				"insert into \"RAST 5 MK2\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8024708 00', '8024708 00', '', '','CONNECT RAST 5 IND 2VS MONOSHAPE MKII', 'TYCO ELECTRONICS LOGISTICS AG', '3-284472-5')");
		statement.executeUpdate(
				"insert into \"RAST 5 MK2\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8031369 00', '8031369 00', '', '','BT RAST 5 MONOSHAPE 5VS', 'TYCO ELECTRONICS LOGISTICS AG', '3-284475-2')");

		// Gaines
		statement.executeUpdate(
				"insert into \"Gaine Acier\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1005147 00', '1005147 00', '', '','GAINE ACIER NOIR DE 8', 'CONTINENTAL AUTOMOTIVE TRADING FRANCE SAS', '2130-702-807')");
		statement.executeUpdate(
				"insert into \"Gaine Acier\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8019891 00', '8019891 00', '', '','GAINE METAL SPR-AS AD14 DIAM.INT.11', 'FLEXA GMBH & CO KG', '1010.111.011')");
		statement.executeUpdate(
				"insert into \"Gaine Feutrine\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1005122 00', '1005122 00', '', '','RUB DE MOUSSE LARGEUR:44mm', 'SCAPA TAPES', '118475')");
		statement.executeUpdate(
				"insert into \"Gaine Feutrine\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8029878 00', '8029878 00', '', '','FEUTRINE ANTIBRUIT 25MM DE LARGE', 'PLASTO ETIQUETTES', '877901')");
		statement.executeUpdate(
				"insert into \"Gaine GAF\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8027910 00', '8027910 00', '', '','GAINE PA6 NW 17', 'SCHLEMMER JOSEF GMBH', '1200178')");
		statement.executeUpdate(
				"insert into \"Gaine GAF\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1025188 00', '1025188 00', '', '','GAF PPAE DIAM:23 SOFLEX', 'DELFINGEN MA CASABLANCA SARL', '01379')");
		statement.executeUpdate(
				"insert into \"Gaine GAFL\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8001260 00', '8001260 00', '', '','GAINE FENDUE A LEVRE D29', 'DELFINGEN FR - ANTEUIL', '13284')");
		statement.executeUpdate(
				"insert into \"Gaine GAFL\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8001261 00', '8001261 00', '', '','GAINE FENDU A LEVRE D26', 'DELFINGEN MA CASABLANCA SARL', '13283')");
		statement.executeUpdate(
				"insert into \"Gaine GANF\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8001262 00', '8001262 00', '', '','GAINE.FENDUE A LEVRE D23', 'DELFINGEN MA CASABLANCA SARL', '13282')");
		statement.executeUpdate(
				"insert into \"Gaine GANF\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8001263 00', '8001263 00', '', '','GAINE_FENDUE A LEVRE D13', 'DELFINGEN MA CASABLANCA SARL', '13279')");
		statement.executeUpdate(
				"insert into \"Gaine GAR\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8030445 00', '8030445 00', '', '','GAINE ANNELEE REFERMABLE 8,5', 'SCHLEMMER JOSEF GMBH', '1940058')");
		statement.executeUpdate(
				"insert into \"Gaine GAR\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8030446 00', '8030446 00', '', '','GAINE  ANNELEE REFERMABLE 11', 'SCHLEMMER JOSEF GMBH', '1940061')");
		statement.executeUpdate(
				"insert into \"Gaine Lisse\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1003863 00', '1003863 00', '', '','GAINE PVC DE 17 NOIRE SERIE GR', 'PLASTELEC TUBES', 'GR-17X18.4-NO')");
		statement.executeUpdate(
				"insert into \"Gaine Lisse\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1003865 00', '1003865 00', '', '','GAINE PVC 5X5,8 NO SERIE GR', 'PLASTELEC TUBES', 'GR5X5.8')");
		statement.executeUpdate(
				"insert into \"Gaine Sili15C2\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1003683 00', '1003683 00', '', '','GAINE SILICONE 15C2', 'OMERIN', 'P0401044')");
		statement.executeUpdate(
				"insert into \"Gaine Sili15C2\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8001225 00', '8001225 00', '', '','SILIGAINE 15C2 Ø7 ROUGE', 'OMERIN', 'P0401009')");
		statement.executeUpdate(
				"insert into \"Gaine Sili15C3\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1021933 00', '1021933 00', '', '','SILIGAINE.DIA:8 LG:150.RG BRIQUE', 'OMERIN', 'P1504048')");
		statement.executeUpdate(
				"insert into \"Gaine Sili15C3\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8000646 00', '8000646 00', '', '','SILIGAINE DIA:12 LG80MM ROUGE BRIQUE', 'OMERIN', 'P1504004')");
		statement.executeUpdate(
				"insert into \"Gaine Sili16F3\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8011131 00', '8011131 00', '', '','SILIGAINE 16F3 D:4 JN LG:250 MM', 'OMERIN', 'P1520003')");
		statement.executeUpdate(
				"insert into \"Gaine Sili16F3\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8019900 00', '8019900 00', '', '','SILIGAINE Ø4 16F3 JAUNE', 'OMERIN', 'P0302005')");
		statement.executeUpdate(
				"insert into \"Gaine Sili21F1\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1003694 00', '1003694 00', '', '','GAINE S.F.VERRE DIA:8 NOIR (21F1)', 'OMERIN', 'P0501004')");
		statement.executeUpdate(
				"insert into \"Gaine Sili21F1\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1003800 00', '1003800 00', '', '','SILIGAINE 21F1 DIA 10 NOIRE', 'OMERIN', 'P0501014')");
		statement.executeUpdate(
				"insert into \"Gaine Silitube\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8019606 00', '8019606 00', '', '','TUBE SILICONE Ø7X12 TRANSPARENT ROULEAU', 'OMERIN', 'P1401051')");
		statement.executeUpdate(
				"insert into \"Gaine Silitube\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8019607 00', '8019607 00', '', '','TUBE SILICONE Ø12X17 TRANSPARENT ROULEAU', 'OMERIN', 'P1401052')");
		statement.executeUpdate(
				"insert into \"Gaine Spiflex\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1002758 00', '1002758 00', '', '','GAINE.SPIFLEX DIA:29-36 BE', 'AUXICOM', 'SPIFLEXPURNW-29-36BE')");
		statement.executeUpdate(
				"insert into \"Gaine Spiflex\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1002755 00', '1002755 00', '', '','GAINE SPIFLEX DIA:21-27 GR', 'AUXICOM', 'SD150A-1')");
		statement.executeUpdate(
				"insert into \"Gaine Spiralée\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8002066 00', '8002066 00', '', '','GAINE SPIRALE NATUREL 85°C Ø6', 'SES SOCIETE ELECTRIQUE STERLING', '0819 0002 018')");
		statement.executeUpdate(
				"insert into \"Gaine Spiralée\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8034417 00', '8034417 00', '', '','GAINE SPIRALE PLIOSPIRE PE12 NOIR', 'SES SOCIETE ELECTRIQUE STERLING ST LOUIS', '0819 0006 010')");
		statement.executeUpdate(
				"insert into \"Gaine Tressée\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1003842 00', '1003842 00', '', '','GAINE TRESSEE DE 12.7MM', 'DELFINGEN MA CASABLANCA SARL', '13251')");
		statement.executeUpdate(
				"insert into \"Gaine Tressée\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1008967 00', '1008967 00', '', '','GAINE TRESSEE DE 6.4MM T4 NOIR', 'DELFINGEN MA CASABLANCA SARL', '13245 NOIR')");
		statement.executeUpdate(
				"insert into \"Gaine Zipper\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1002759 00', '1002759 00', '', '','GAINE ZIPPER PVC DIA:12', 'SERTIM', 'ZTZ 12 GP/S 63 B')");
		statement.executeUpdate(
				"insert into \"Gaine Zipper\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1002760 00', '1002760 00', '', '','GAINE ZIPPER.PVC DIA:16', 'SERTIM', 'ZTZ 16 GP/S 63 B')");

		// GainesThermo
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable STF-4\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1004909 00', '1004909 00', '', '','GAINE HTAT 52/13 LG:100', 'SRATI FRANCE', 'STF-52/13-100-NO')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable STF-4\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1004894 00', '1004894 00', '', '','STF 12/3 LG30 NOIR', 'SRATI FRANCE', 'STF-12/3-30-NO')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable SST\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1003297 00', '1003297 00', '', '','MANCHON B11 4.8 RG LG.25mm', 'SRATI FRANCE', 'SST 4.80-25MM-ROUGE')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable SST\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1004545 00', '1004545 00', '', '','SST 9.50 46MM NOIR', 'SRATI FRANCE', 'SSR9.5-46-NO')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable hélavia\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1004417 00', '1004417 00', '', '','MANCHON HELAVIA BLEU', 'SES SOCIETE ELECTRIQUE STERLING ST LOUIS', '201-072-BE')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable hélavia\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1004420 00', '1004420 00', '', '','MANCHON HELAVIAS ROUGE DIA:2.5mm', 'SES SOCIETE ELECTRIQUE STERLING', '0201 0003 007')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable G61\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8029231 00', '8029231 00', '', '','GT NOIRE 19X35MM (TYPE G61)', 'GREMCO', 'G61 19X35 NOIRE')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable G61\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8027683 00', '8027683 00', '', '','GT NOIRE 4,8X85 MM (TYPE G61)', 'GREMCO', 'G61 4,8X85 NOIRE')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable SER\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1004106 00', '1004106 00', '', '','GC RNF100 1/4X40 CY T4', 'SRATI FRANCE', 'SER6.4-40-TRANSP')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable SER\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1004107 00', '1004107 00', '', '','GC RNF100 3/8X50 CY T4', 'SRATI FRANCE', 'SER9.5-50-TRANSP')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable SER-3\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1004535 00', '1004535 00', '', '','SER 9/3 TRANSPARENT', 'SRATI FRANCE', 'SER9.3-TRANSP')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable SER-3\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1019740 00', '1019740 00', '', '','SER-3 18/6 50MM ROUGE', 'SRATI FRANCE', 'SER-3 18/6 50MM RG')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable STF\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1004901 00', '1004901 00', '', '','STF 18/6 50MM NOIR', 'SRATI FRANCE', 'STF-1900500')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable STF\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1004902 00', '1004902 00', '', '','STF 18/6 60MM NOIR', 'SRATI FRANCE', 'STF-18/6-60-NO')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable SER-UL\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1010333 00', '1010333 00', '', '','MANCHON SER 4,8 NOIR LG 2', 'GREMCO', 'B2-4,8 LG25 NOIRE')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable SKY\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8013431 00', '8013431 00', '', '','SKY 4.8 TRANSLUCIDE LG:20MM', 'SRATI FRANCE', 'SKY 4.8-20-TRANSP')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable SKY\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8030121 00', '8030121 00', '', '','GAINE TRANS SKY DIA3,2 LG60', 'SRATI FRANCE', 'SKY 3,2-60MM-TRANSP')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable STF-M\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1024971 00', '1024971 00', '', '','GAINE THERMOCOLLANTE NOIRE LG150', 'SRATI FRANCE', 'STF-M-70/25 LG150 NO')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable SER-BIH\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8033758 00', '8033758 00', '', '','SER-BIH 19/6 LG :50 JAUNE/VERT', 'SRATI FRANCE', 'SERBIC000PYYH')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable R\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1015450 00', '1015450 00', '', '','THERMO TYPE R2 VIOLET 20MM', 'SRATI FRANCE', 'R2-20-VI')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable R\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1015450 00', '1015451 00', '', '','THERMO TYPE R2 GRIS 20MM', 'SRATI FRANCE', 'R2-20-GR')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable STFER\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8017333 00', '8017333 00', '', '','GAINE TH. STFER NOIRE 250°C LG:30MM±1', 'GREMCO', 'TEF R 3/16 NE')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable STFER\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-8017536 00', '8017536 00', '', '','GAINE TH. STFER 6,40 NOIRE 250°C LG:30MM±1', 'SRATI FRANCE', 'STFER6,4(1/4)-30MMNO')");
		statement.executeUpdate(
				"insert into \"Gaine Thermorétractable STD\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation, Fabricant,\"Référence Fabricant\") values ('CFCA-1004405 00', '1004405 00', '', '','REPERE STD-01 JAUNE CHIFFRE 1', 'SES SOCIETE ELECTRIQUE STERLING ST LOUIS', '037401 100041')");

		// Port
		statement.executeUpdate("insert into Surcote (oid, Position, Valeur) values (999998, 'Postion 1', 45.5)");
		statement.executeUpdate("insert into Surcote (oid, Position, Valeur) values (999999, 'Postion 2', 45.9)");

		statement.executeUpdate(
				"insert into Port (oid, Nom,Surcotes,\"Cosses Eligibles\") values (9999998, 'port 1', ARRAY[999998,999999], ARRAY['Cosse à Fût Ouvert'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom,Surcotes,\"Cosses Eligibles\") values (9999999, 'port 2', ARRAY[999998,999999], ARRAY['Cosse à Fût Ouvert'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (1403201201, '3', ARRAY['9.5AccF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (1403201202, '5', ARRAY['9.5AccF'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (1403201203, '6', ARRAY['RSB7785'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (1403201204, '7', ARRAY['RSB7785'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (1403201205, '8', ARRAY['RSB7785'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (1403201206, '9', ARRAY['RSB7785'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (1403201207, '1', ARRAY['RSB7858'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (1403201208, '2', ARRAY['RSB7858'])");
		statement.executeUpdate(
				"insert into Port (oid, Nom, \"Cosses Eligibles\") values (1403201209, '4', ARRAY['RSB7858'])");

		// Connecteur
		statement.executeUpdate(
				"insert into \"Connecteur 1 Voie\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8007383 00', '8007383 00', '','4082 00 i..jpg', 'CAPUCHON NOIR STOCKO', 'BACKER BHV AB', '1120528601')");
		statement.executeUpdate(
				"insert into \"Connecteur 1 Voie\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8013485 00', '8013485 00', '','4082 00 i..jpg', 'CONNECTEUR F TV A SERTIR POUR CAB 17/19/21', 'CENO EXPORT', 'TOQ384602')");
		statement.executeUpdate(
				"insert into \"Connecteur 2 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1002472 00', '1002472 00', '','4077 00 i..jpg', 'DOMINO BA 2V PAS DE 8MM', 'AFLOX ACHATS VERRE', '1013440002')");
		statement.executeUpdate(
				"insert into \"Connecteur 2 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8013176 00', '8013176 00', '','4079 00 i..jpg', 'CONNECTEUR JPT 2 VS BLANC PL', 'BERNIS TRUCKS', '5010214475')");
		statement.executeUpdate(
				"insert into \"Connecteur 3 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1002473 00', '1002473 00', '','4093 00 i..jpg', 'DOMINO BA 3V', 'AFLOX ACHATS VERRE', '1013440003')");
		statement.executeUpdate(
				"insert into \"Connecteur 3 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8025130 00', '8025130 00', '','4093 00 i..jpg', 'BT 16-3 FEM 3VS BLINDE 400V', 'AMPHENOL TUCHEL ELECTRONICS GMBH', 'C016 12F 003 013 7')");
		statement.executeUpdate(
				"insert into \"Connecteur 4 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8007660 00', '8007660 00', '','4061 00 i..jpg', 'MAXIFUSE 4POS 160A', 'AVNET ABACUS', '354505.ZA000')");
		statement.executeUpdate(
				"insert into \"Connecteur 4 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1018136 00', '1018136 00', '','4061 00 i..jpg', 'BARRETTE  DOMINO DE RACCORDEMENT 4 PÔLES', 'BLINK FRANCE SARL', '88165643')");
		statement.executeUpdate(
				"insert into \"Connecteur 5 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1022514 00', '1022514 00', '','', 'BT 5V AMPHENOL', 'AMPHENOL TUCHEL ELECTRONICS GMBH', '2A64405')");
		statement.executeUpdate(
				"insert into \"Connecteur 5 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8018890 00', '8018890 00', '','', 'BT FEMELLE CIRCULAIRE 5 PÔLES TYPE 720 A SOUDER', 'BINDER FRANCE', '99 9116 00 05')");
		statement.executeUpdate(
				"insert into \"Connecteur 6 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8005583 00', '8005583 00', '','4074 00 i..jpg', 'BOITIER FEMELLE 6VS + TERRE', 'AMPHENOL TUCHEL ELECTRONICS GMBH', 'C016 10G006 000 12')");
		statement.executeUpdate(
				"insert into \"Connecteur 6 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1018322 00', '1018322 00', '','', 'BARRET. DOMINOS DE RACCORDEMENT 6 PÔLES', 'BLINK FRANCE SARL', '88165707')");
		statement.executeUpdate(
				"insert into \"Connecteur 7 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8012819 00', '8012819 00', '','', 'BT FEME 7VS EP1510 93.031.7853.8', 'COM EL SRL ELET PLAST', 'EP1510 93031.7853.0')");
		statement.executeUpdate(
				"insert into \"Connecteur 7 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1017170 00', '1017170 00', '','', 'BT MALE 7VS (NO/MR) ST18/7 MONTE SERRECABLE/VERROU', 'COM.EL SRL ELET-PLAST', 'EP1500 EP/7M+EP/7G')");
		statement.executeUpdate(
				"insert into \"Connecteur 8 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1018323 00', '1018323 00', '','4539 00 i..jpg', 'DOMINOS DE RACCORDEMENT 8 PÔLES', 'BLINK FRANCE SARL', '88165662')");
		statement.executeUpdate(
				"insert into \"Connecteur 8 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-60497 00', '60497 00', '','4539 00 i..jpg', 'BT PC JPTAR-B 8V BA', 'CFCA SA  ETB DE SAINT MARTIN HERES', '144172-4')");
		statement.executeUpdate(
				"insert into \"Connecteur 9 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8002667 00', '8002667 00', '','', 'PORTE CLIPS 9VS BLANC', 'BERNIS TRUCKS', '5000409680')");
		statement.executeUpdate(
				"insert into \"Connecteur 9 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8000802 00', '8000802 00', '','', 'BARETTE 9VS PAS 8MM', 'BLINK FRANCE SARL', '88712194')");
		statement.executeUpdate(
				"insert into \"Connecteur 9 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\", \"Nombre De Voies\", Ports) values ('CFCA-1009191 00', '1009191 00', '','', 'BOITIER RELAIS 9 VS', 'FRANCIS BRUN SA', 'RE410005', 9, ARRAY[1403201201,1403201202,1403201203,1403201204,1403201205,1403201206,1403201207,1403201208,1403201209])");
		statement.executeUpdate(
				"insert into \"Connecteur 10 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-4948 00', '4948 00', '','', 'BOIT PCSP5 CV 10V D8 NAT PA', 'BERNIS TRUCKS', '7703097479')");
		statement.executeUpdate(
				"insert into \"Connecteur 10 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8017839 00', '8017839 00', '','', 'BT PC 10VS BC 1-929504-4', 'BERNIS TRUCKS', '5010214823')");
		statement.executeUpdate(
				"insert into \"Connecteur 11 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8029338 00', '8029338 00', '','', 'BT PC 11VS 8X2,3+3X4,8 GRIS', 'EURO SENKO BV', '6189-0377')");
		statement.executeUpdate(
				"insert into \"Connecteur 11 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8031203 00', '8031203 00', '','', 'BT 11VS TYPE XHP', 'JST FRANCE SAS', 'XHP-11(68555000)')");
		statement.executeUpdate(
				"insert into \"Connecteur 12 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8018995 00', '8018995 00', '','', 'BT PC 12VS POUR IMPER HARTMAN', 'AMPER AUTO SRL', '41118036/8350')");
		statement.executeUpdate(
				"insert into \"Connecteur 12 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1018012 00', '1018012 00', '','', 'BARRETTE DOMINOS DE RACCORDEMENT 12 PÔLES', 'BLINK FRANCE SARL', '88164223')");
		statement.executeUpdate(
				"insert into \"Connecteur 13 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-9237 00', '9237 00', '','', 'BT PC MIC1 13V GR', 'BERNIS TRUCKS', '7701996041')");
		statement.executeUpdate(
				"insert into \"Connecteur 13 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8023233 00', '8023233 00', '','', 'BT 13VS SL-156 VERROU ET POLARISATION', 'RS COMPONENTS', '1-770849-3')");
		statement.executeUpdate(
				"insert into \"Connecteur 14 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8012898 00', '8012898 00', '','', 'PORTE FUSIBLE 6VS BLUE SEA SYSTEMS', 'BLUE SEA SYSTEMS', '5025B')");
		statement.executeUpdate(
				"insert into \"Connecteur 14 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8022347 00', '8022347 00', '','', 'PORTE FUSIBLE SANS CAPOT 6VS BLUE SEA SYSTEMS', 'BLUE SEA SYSTEMS', '5030B')");
		statement.executeUpdate(
				"insert into \"Connecteur 15 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1016372 00', '1016372 00', '','', 'SUB-D 15V NOIR MALE', 'FARNELL IN ONE', '150809')");
		statement.executeUpdate(
				"insert into \"Connecteur 15 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8023696 00', '8023696 00', '','', 'SUBD 15PTS MALE 865615PLTX', 'RS COMPONENTS', '251-706')");
		statement.executeUpdate(
				"insert into \"Connecteur 16 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1015757 00', '1015757 00', '','', 'BT 16V BA AU PAS DE 3.96 AVEC VÉROUILLAGE', 'MOLEX FRANCE', '09-91-1600')");
		statement.executeUpdate(
				"insert into \"Connecteur 16 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8028447 00', '8028447 00', '','', 'CONNECTEUR 16VS MOLEX', 'MOLEX FRANCE', '98977 3061')");
		statement.executeUpdate(
				"insert into \"Connecteur 17 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8027807 00', '8027807 00', '','', 'BT 16-3 BROCHE 17V PANNEAU', 'TTI FRANCE', 'C016 10G 017 810 1')");
		statement.executeUpdate(
				"insert into \"Connecteur 17 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8027808 00', '8027808 00', '','', 'BT  16-3 BROCHE 17V COUDE', 'TTI FRANCE', 'C016 10K 017 032 1')");
		statement.executeUpdate(
				"insert into \"Connecteur 18 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8032689 00', '8032689 00', '','', 'BT18VS TYPE DTV02-18PA-C015', 'COMPAGNIE DEUTSCH GMBH', 'DTV02-18PA-C015')");
		statement.executeUpdate(
				"insert into \"Connecteur 18 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8032689 00', '8032689 00', '','', 'BT18VS TYPE DTV02-18PA-C015', 'COMPAGNIE DEUTSCH GMBH', 'DTV02-18PA-C015')");
		statement.executeUpdate(
				"insert into \"Connecteur 20 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8030299 00', '8030299 00', '','', 'BT 20 VS CGRID NO', 'MOLEX FRANCE', '0701070019')");
		statement.executeUpdate(
				"insert into \"Connecteur 20 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8029381 00', '8029381 00', '','', 'SUPP RELAIS 4VS', 'MTA  MECCANOTECNICA CODOGNESE SPA', '03.00660')");
		statement.executeUpdate(
				"insert into \"Connecteur 21 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8011730 00', '8011730 00', '','', 'BOITIER JPT PORTE LANGUETTE 21VS VIOLET', 'TYCO ELECTRONICS LOGISTICS AG', '1-967630-3')");
		statement.executeUpdate(
				"insert into \"Connecteur 21 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8011747 00', '8011747 00', '','', 'BOITIER JPT PORTE CLIP 21VS VIOLET', 'TYCO ELECTRONICS LOGISTICS AG', '1-967625-3')");
		statement.executeUpdate(
				"insert into \"Connecteur 22 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8006402 00', '8006402 00', '','', 'BT MINIFIT 22 VOIES', 'MOLEX FRANCE', '39-01-2221')");
		statement.executeUpdate(
				"insert into \"Connecteur 22 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1026067 00', '1026067 00', '','', 'BT.22VS MALE MINIFIT NO FLAME', 'PLASTECH', '6740-1221E')");
		statement.executeUpdate(
				"insert into \"Connecteur 23 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8001341 00', '8001341 00', '','', 'P.C. H3 23V MOBILE', 'MOLEX FRANCE', '989571031')");
		statement.executeUpdate(
				"insert into \"Connecteur 23 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8001407 00', '8001407 00', '','', 'P.C. H3 23V NOIR FIXE+JOINT', 'MOLEX FRANCE', '989581031')");
		statement.executeUpdate(
				"insert into \"Connecteur 24 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8028372 00', '8028372 00', '','', 'MODULE SUPPORT MAXRELAIS + MINIFUSIBLES', 'MTA - MECCANOTECNICA CODOGNESE SPA', '0301510')");
		statement.executeUpdate(
				"insert into \"Connecteur 24 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1021313 00', '1021313 00', '','', 'BT.24VS MALE MINIFIT NO FLAME', 'PLASTECH', '6740-1241E')");
		statement.executeUpdate(
				"insert into \"Connecteur 25 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8023334 00', '8023334 00', '','', 'SUBD MALE 25PTS A SERTIR', 'MATELECO OUEST', 'L777RRB25P')");
		statement.executeUpdate(
				"insert into \"Connecteur 25 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-104407 00', '104407 00', '','', 'BT PC JPT 25V NO', 'TYCO ELECTRONICS LOGISTICS AG', '963317-1')");
		statement.executeUpdate(
				"insert into \"Connecteur 26 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8017772 00', '8017772 00', '','', 'PORTE FUSIBLE 12VS BLUE SEA SYSTEMS', 'BLUE SEA SYSTEMS', '5026B')");
		statement.executeUpdate(
				"insert into \"Connecteur 26 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8022348 00', '8022348 00', '','', 'PORTE FUSIBLE SS CAPOT 12VS BLUE SEA SYSTEMS', 'BLUE SEA SYSTEMS', '5031B')");
		statement.executeUpdate(
				"insert into \"Connecteur 27 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8007871 00', '8007871 00', '','', 'BORNIER 062937', 'WAGO CONTACT', '51171702')");
		statement.executeUpdate(
				"insert into \"Connecteur 27 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-9257 00', '9257 00', '','', 'BT PC MIC1 27V NA AVEC MURETS', 'TYCO ELECTRONICS LOGISTICS AG', '142180-1')");
		statement.executeUpdate(
				"insert into \"Connecteur 28 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8032762 00', '8032762 00', '','', 'BT 28VS', 'TYCO ELECTRONICS LOGISTICS AG', '1393436-2')");
		statement.executeUpdate(
				"insert into \"Connecteur 29 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8008735 00', '8008735 00', '','', 'BT PC JPTE 29VS NO', 'TYCO ELECTRONICS LOGISTICS AG', '1-963449-2')");
		statement.executeUpdate(
				"insert into \"Connecteur 29 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8027867 00', '8027867 00', '','', 'BOITIER WAGO PLATINE VERSION ELECTRIQUE', 'WAGO CONTACT', '51029376')");
		statement.executeUpdate(
				"insert into \"Connecteur 30 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1024475 00', '1024475 00', '','', 'CONNECTEUR 30V', 'ERCE SAS DIR. ADM.', 'MBG30R11')");
		statement.executeUpdate(
				"insert into \"Connecteur 30 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8013581 00', '8013581 00', '','', 'CONNECTEUR 30VS PORTE BROCHE', 'MATELECO OUEST', 'MBG30P11')");
		statement.executeUpdate(
				"insert into \"Connecteur 32 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8028000 00', '8028000 00', '','', 'BOITE PORTE A FUSIBLE', 'MTA  MECCANOTECNICA CODOGNESE SPA', '03.00650')");
		statement.executeUpdate(
				"insert into \"Connecteur 32 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1013450 00', '1013450 00', '','', 'CONNECTEUR GR 32V MP100', 'POWER ET SIGNAL GROUP', '628392')");
		statement.executeUpdate(
				"insert into \"Connecteur 34 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8030570 00', '8030570 00', '','', 'BT 34VS', 'TYCO ELECTRONICS LOGISTICS AG', '4-1437290-1')");
		statement.executeUpdate(
				"insert into \"Connecteur 34 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8027423 00', '8027423 00', '','', 'BORNIER WAGO PAC ROI+', 'WAGO CONTACT', '51029283')");
		statement.executeUpdate(
				"insert into \"Connecteur 35 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-1013758 00', '1013758 00', '','', 'BT 35V NO BOSCH 0265051006', 'HEULIEZ BUS', '5010207804')");
		statement.executeUpdate(
				"insert into \"Connecteur 35 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8019663 00', '8019663 00', '','', 'BT AMPSEAL 35VS NOIR', 'TYCO ELECTRONICS LOGISTICS AG', '776164-1')");
		statement.executeUpdate(
				"insert into \"Connecteur 36 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8015223 00', '8015223 00', '','', 'SUPPORT DE CONTACT 36VS', 'MARTINEAU ETS', '1 928 404 200')");
		statement.executeUpdate(
				"insert into \"Connecteur 36 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8014925 00', '8014925 00', '','', 'SUPPORT 36 CONTACT', 'TYCO ELECTRONICS LOGISTICS AG', '0-1355930-2')");
		statement.executeUpdate(
				"insert into \"Connecteur 37 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8013096 00', '8013096 00', '','', 'BOITIER CIRCULAIRE 37VS ADP 1BS37', 'MATELECO OUEST', '121583-0058')");
		statement.executeUpdate(
				"insert into \"Connecteur 40 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8028448 00', '8028448 00', '','', 'MODULE 20 MINIS FUSIBLES', 'MTA - MECCANOTECNICA CODOGNESE SPA', '03.01460')");
		statement.executeUpdate(
				"insert into \"Connecteur 42 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8012624 00', '8012624 00', '','', 'BT PL ETCH MIXTE 42V 2STP+6JPT+34MT2 SAAB', 'TYCO ELECTRONICS LOGISTICS AG', '968393-1')");
		statement.executeUpdate(
				"insert into \"Connecteur 42 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8012675 00', '8012675 00', '','', 'BT PC 42V MIXTE 6JPT+36MT2', 'TYCO ELECTRONICS LOGISTICS AG', '1-967281-1')");
		statement.executeUpdate(
				"insert into \"Connecteur 48 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8020552 00', '8020552 00', '','', 'PC CMC 48V ETCH NO DROIT', 'MOLEX FRANCE', '0989933311')");
		statement.executeUpdate(
				"insert into \"Connecteur 52 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8032759 00', '8032759 00', '','', 'BT TIMER 52VS', 'TYCO ELECTRONICS LOGISTICS AG', '1393450-3')");
		statement.executeUpdate(
				"insert into \"Connecteur 54 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8034488 00', '8034488 00', '','', 'CONNECTEUR 54 VOIES', 'TYCO ELECTRONICS LOGISTICS AG', '1924478-3')");
		statement.executeUpdate(
				"insert into \"Connecteur 55 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-91257 00', '91257 00', '','', 'BT PC JPT 55V ENSBLE', 'TYCO ELECTRONICS LOGISTICS AG', '963534-1')");
		statement.executeUpdate(
				"insert into \"Connecteur 56 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8023917 00', '8023917 00', '','', 'BT PC 56V SICMA 1,5 NOIR', 'RUTRONIK SA AGENCE DE POITIERS', 'PI0001258')");
		statement.executeUpdate(
				"insert into \"Connecteur 58 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8029758 00', '8029758 00', '','', 'BORN  PAC BARTL ECO S X1', 'WAGO CONTACT', '51020817-02')");
		statement.executeUpdate(
				"insert into \"Connecteur 64 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8025953 00', '8025953 00', '','', 'BORNIER PR PAC ECO  LCI', 'WAGO CONTACT', '51028419')");
		statement.executeUpdate(
				"insert into \"Connecteur 70 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8008724 00', '8008724 00', '','', 'BT MIXTE MT2E/JPTE 70 VS NOIR', 'TYCO ELECTRONICS LOGISTICS AG', '1-968879-1')");
		statement.executeUpdate(
				"insert into \"Connecteur 90 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8032729 00', '8032729 00', '','', 'BT PC 90VS SICMA', 'RUTRONIK SA AGENCE DE POITIERS', '211PC902S0009')");
		statement.executeUpdate(
				"insert into \"Connecteur 94 Voies\" (\"Référence BE\",\"Référence ERP\",\"Fiche Technique\",Représentation,Désignation,Fabricant,\"Référence Fabricant\") values ('CFCA-8022794 00', '8022794 00', '','', 'CONNECTEUR 94VS MQS PORTE CLIP', 'TYCO ELECTRONICS LOGISTICS AG', '284743-1')");

		t.rollback();
	}
}