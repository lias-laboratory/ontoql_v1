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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Map;

import javax.transaction.Synchronization;

import org.hibernate.HibernateException;
import org.hibernate.Transaction;
import org.junit.Before;

import fr.ensma.lisi.ontoql.core.AbstractEntityClass;
import fr.ensma.lisi.ontoql.core.AbstractFactoryEntityDB;
import fr.ensma.lisi.ontoql.core.EntityClass;
import fr.ensma.lisi.ontoql.core.EntityProperty;
import fr.ensma.lisi.ontoql.core.ontodb.FactoryEntityOntoDB;
import fr.ensma.lisi.ontoql.jobdbc.OntoQLSession;
import fr.ensma.lisi.ontoql.jobdbc.impl.OntoQLSessionImpl;
import fr.ensma.lisi.ontoql.util.OntoQLHelper;

/**
 * @author Stéphane JEAN
 */
public abstract class OntoQLTestCase {

	public static final String HOST = "localhost";

	public static final String PORT = "5432";

	public static final String USR = "postgres";

	public static final String PWD = "psql";

	private static Class lastTestClass;

	private OntoQLSession session;

	private Connection database;

	protected AbstractFactoryEntityDB entityFactory;

	/**
	 * Hashtable contenant pour chaque nom de classe son identifiant interne.
	 */
	protected static Map<String, String> classes;

	protected static Hashtable<String, String> properties;

	public AbstractEntityClass cPersonnalEquipementSafety;

	public EntityClass cBuoyancyAids;

	public AbstractEntityClass cCags;

	public AbstractEntityClass cHudson;

	public AbstractEntityClass cStandard;

	public AbstractEntityClass cRandonnee;

	public AbstractEntityClass cOntario;

	public AbstractEntityClass cFjord;

	public EntityClass cNeoprene;

	public AbstractEntityClass cMuffs;

	public EntityClass cSafety;

	public EntityClass cSafetyRope;

	public EntityClass cElasticLeash;

	public AbstractEntityClass cHelmets;

	public AbstractEntityClass cSlalom;

	public AbstractEntityClass cDurance;

	public AbstractEntityClass cJunior;

	public AbstractEntityClass cNeopreneMuffs;

	public AbstractEntityClass cNeopreneMuffsForChildren;

	public AbstractEntityClass cStandardNeopreneMuffs;

	public AbstractEntityClass cAnchorage;

	public AbstractEntityClass cFocBaby;

	public EntityProperty pSize;

	public EntityProperty pReference;

	public EntityProperty pDiameterOpening;

	public EntityProperty pDiameterDring;

	public EntityProperty pItsMuff;

	public EntityProperty pItsSlalom;

	public EntityProperty pVirage;

	public EntityProperty pColor;

	public EntityProperty pBuoyancy;

	public EntityProperty pBuoyancyRaft;

	public EntityProperty pChestMeasurement;

	public EntityProperty pWeightOfTheUser;

	public EntityProperty pNames;

	public OntoQLSession getSession() {
		return session;
	}

	public String getUrl() {
		return "jdbc:postgresql://" + HOST + ":" + PORT + "/OntoQLJUnitTestMain";
	}

	@Before
	public void setUp() throws Exception {
		if (session == null || lastTestClass != getClass()) {
			Class.forName("org.postgresql.Driver");
			database = DriverManager.getConnection(getUrl(), USR, PWD);
			database.setAutoCommit(false);
			session = new OntoQLSessionImpl(database) {

				private static final long serialVersionUID = 896431144853262312L;

				@Override
				public Transaction beginTransaction() throws HibernateException {
					Transaction myTransaction = new Transaction() {

						@Override
						public void begin() throws HibernateException {
						}

						@Override
						public void commit() throws HibernateException {
							try {
								database.commit();
							} catch (SQLException e) {
								new HibernateException(e);
							}
						}

						@Override
						public void rollback() throws HibernateException {
							try {
								database.rollback();
							} catch (SQLException e) {
								new HibernateException(e);
							}
						}

						@Override
						public boolean wasRolledBack() throws HibernateException {
							return false;
						}

						@Override
						public boolean wasCommitted() throws HibernateException {
							return false;
						}

						@Override
						public boolean isActive() throws HibernateException {
							return false;
						}

						@Override
						public void registerSynchronization(Synchronization synchronization) throws HibernateException {
						}

						@Override
						public void setTimeout(int seconds) {
						}
					};

					return myTransaction;
				}
			};

			session.setDefaultNameSpace("http://lisi.ensma.fr/");
			session.setReferenceLanguage(OntoQLHelper.ENGLISH);

			lastTestClass = getClass();
		}

		if (getClasses() == null) {
			// Independent DB (rid) test case
			// buildClasses();

			// Dependent DB test case
			buildClassesWithoutLoading();
		}

		entityFactory = new FactoryEntityOntoDB(session);

		cPersonnalEquipementSafety = (AbstractEntityClass) entityFactory
				.createCategory((String) classes.get("PERSONAL EQUIPEMENT/SAFETY"));
		cBuoyancyAids = (EntityClass) entityFactory.createCategory((String) classes.get("BUOYANCY AIDS"));
		cCags = (AbstractEntityClass) entityFactory.createCategory((String) classes.get("CAGS"));
		cHudson = (AbstractEntityClass) entityFactory.createCategory((String) classes.get("HUDSON"));

		cStandard = (AbstractEntityClass) entityFactory.createCategory((String) classes.get("STANDARD"));
		cRandonnee = (AbstractEntityClass) entityFactory.createCategory((String) classes.get("RANDONNEE"));
		cOntario = (AbstractEntityClass) entityFactory.createCategory((String) classes.get("ONTARIO"));
		cFjord = (AbstractEntityClass) entityFactory.createCategory((String) classes.get("FJORD (SEA PARKA)"));

		cNeoprene = (EntityClass) entityFactory.createCategory((String) classes.get("NEOPRENE"));
		cMuffs = (AbstractEntityClass) entityFactory.createCategory((String) classes.get("MUFFS"));
		cSafety = (EntityClass) entityFactory.createCategory((String) classes.get("SAFETY"));
		cHelmets = (AbstractEntityClass) entityFactory.createCategory((String) classes.get("HELMETS"));
		cSlalom = (AbstractEntityClass) entityFactory.createCategory((String) classes.get("SLALOM"));
		cDurance = (AbstractEntityClass) entityFactory.createCategory((String) classes.get("DURANCE"));
		cJunior = (AbstractEntityClass) entityFactory.createCategory((String) classes.get("JUNIOR"));
		cAnchorage = (AbstractEntityClass) entityFactory
				.createCategory((String) classes.get("ANCHORAGE (KAYAK PANTS)"));
		cSafetyRope = (EntityClass) entityFactory.createCategory((String) classes.get("SAFETY ROPE TO THROW"));
		cElasticLeash = (EntityClass) entityFactory.createCategory((String) classes.get("ELASTIC LEASH"));
		cNeopreneMuffs = (AbstractEntityClass) entityFactory
				.createCategory((String) classes.get("NEOPRENE MUFFS, HOOK AND LOOP CLOSURE"));
		cNeopreneMuffsForChildren = (AbstractEntityClass) entityFactory
				.createCategory((String) classes.get("NEOPRENE MUFFS FOR CHILDREN, HOOK AND LOOP CLOSURE"));
		cStandardNeopreneMuffs = (AbstractEntityClass) entityFactory
				.createCategory((String) classes.get("STANDARD NEOPRENE MUFFS, HOOK AND LOOP CLOSURE"));
		cFocBaby = (AbstractEntityClass) entityFactory.createCategory((String) classes.get("FOC BABY"));

		pSize = (EntityProperty) entityFactory.createDescription((String) properties.get("Size"));
		pReference = (EntityProperty) entityFactory.createDescription((String) properties.get("Reference"));
		pDiameterOpening = (EntityProperty) entityFactory
				.createDescription((String) properties.get("Diameter opening"));
		pDiameterDring = (EntityProperty) entityFactory.createDescription((String) properties.get("Diameter D ring"));
		pItsMuff = (EntityProperty) entityFactory.createDescription((String) properties.get("its_muff"));
		pItsSlalom = (EntityProperty) entityFactory.createDescription((String) properties.get("its_slalom"));
		pVirage = (EntityProperty) entityFactory.createDescription((String) properties.get("virage"));
		pColor = (EntityProperty) entityFactory.createDescription((String) properties.get("Color"));

		pNames = (EntityProperty) entityFactory.createDescription((String) properties.get("names"));

		pBuoyancy = (EntityProperty) entityFactory.createDescription((String) properties.get("Buoyancy"));
		pBuoyancyRaft = (EntityProperty) entityFactory.createDescription((String) properties.get("Buoyancy RAFT"));
		pChestMeasurement = (EntityProperty) entityFactory
				.createDescription((String) properties.get("Chest measurement"));
		pWeightOfTheUser = (EntityProperty) entityFactory
				.createDescription((String) properties.get("Weight of the user"));

	}

	protected boolean dropAfterFailure() {
		return true;
	}

	/**
	 * @return Returns the classes.
	 */
	protected Map<String, String> getClasses() {
		return classes;
	}

	private void buildClassesWithoutLoading() throws Exception {
		classes = new Hashtable<String, String>();
		properties = new Hashtable<String, String>();

		classes.put("PERSONAL EQUIPEMENT/SAFETY", "!1040");
		properties.put("Size", "!1202");
		properties.put("Reference", "!1204");
		properties.put("Buoyancy", "!1205");
		properties.put("Weight of the user", "!1207");
		properties.put("Chest measurement", "!1208");
		properties.put("Buoyancy RAFT", "!1209");
		classes.put("PROPULSION", "!1019");
		properties.put("Length", "!1221");
		properties.put("Diameter", "!1222");
		properties.put("Configuration", "!1223");
		properties.put("Weight", "!1225");
		classes.put("KAYAK PADDLES", "!1106");
		classes.put("BEACH PADDLES, OARS", "!1149");
		classes.put("CHARENTE", "!1018");
		classes.put("BUOYANCY AIDS", "!1039");
		classes.put("WILD WATER BUOYANCY AIDS", "!1038");
		classes.put("ANODIZED SNAP HOOK WITH BROAD OPENING (3 cm)", "!1092");
		classes.put("CANYON OUVERT ", "!1037");
		classes.put("CANYON EXTREME", "!1036");
		classes.put("VERDON", "!1034");
		classes.put("GUISAN", "!1033");
		classes.put("FOC BABY", "!1031");
		classes.put("FOC JUNIOR", "!1035");
		classes.put("BUOYANCY AIDS /STANDARD CE EN 395", "!1032");
		classes.put("ATOLL", "!1028");
		classes.put("SALVAVIDAS PARA MAR", "!1029");
		classes.put("ARTIC", "1027");
		classes.put("SPECIFICS BUOYANCY AIDS", "!1017");
		classes.put("SOLOGNE LUXE CAMOUFLAGE", "!1026");
		classes.put("ORNE", "!1024");
		classes.put("SOLOGNE LUXE", "!1025");
		classes.put("EGALIS", "!1022");
		classes.put("PALMA", "!1077");
		classes.put("SALVAVIDAS PARA ReOS/LAGOS", "!1030");
		classes.put("CELE", "!1020");
		classes.put("CLAIN", "!1075");
		classes.put("SILLAGE", "!1021");
		classes.put("SILLAGE LUXE", "!1076");
		classes.put("ISERE", "!1073");
		classes.put("ALASKA", "!1070");
		classes.put("CLAIN ZIP", "!1074");
		classes.put("COLORADO", "!1071");
		classes.put("AVOCET", "!1072");
		classes.put("UBAYE", "!1069");
		classes.put("CAGS", "!1068");
		classes.put("STANDARD", "!1067");
		classes.put("RANDONNEE", "!1023");
		classes.put("ONTARIO", "!1063");
		classes.put("FJORD (SEA PARKA)", "!1061");
		classes.put("SPRAY DECKS", "!1059");
		classes.put("VERTEX COATED SPRAY DECK", "!1058");
		classes.put("ANCHORAGE (KAYAK PANTS)", "!1060");
		classes.put("COATING FABRIC SPRAY DECK", "!1057");
		classes.put("COATING FABRIC SPRAY DECK", "!1056");
		classes.put("SEA KAYAK SPRAY DECK", "!1055");
		classes.put("NEOPRENE SPRAY DECK LARGE COCKPIT", "!1054");
		classes.put("NEOPRENE SPRAY DECK SLALOM COCKPIT", "!1053");
		classes.put("NEOPRENE SPRAY DECK SLALOM COCKPIT WITH SHOULDER STRAPS", "!1052");
		classes.put("NEOPRENE SPRAY DECK DESCENT COCKPIT", "!1051");
		classes.put("VERTEX COATED SPRAY DECK", "!1050");
		classes.put("3mm NEOPRENE SPRAY DECK", "!1049");
		classes.put("NEOPRENE INTEGRAL SPRAY DECK", "!1048");
		classes.put("NEOPRENE", "!1047");
		classes.put("NEOPRENE BERMUDA", "!1046");
		classes.put("NEOPRENE LONG-JOHN FOR CHILDREN", "!1045");
		classes.put("NEOPRENE LONG-JOHN FOR ADULTS", "!1044");
		classes.put("SOLE SHOES", "!1043");
		classes.put("SOLE BOOTS", "!1042");
		classes.put("NEOPRENE MUFFS, HOOK AND LOOP CLOSURE", "!1104");
		properties.put("its_slalom", "!6226");
		classes.put("SOCKS", "!1041");
		classes.put("NEOPRENE MUFFS FOR CHILDREN, HOOK AND LOOP CLOSURE", "!1103");
		classes.put("STANDARD NEOPRENE MUFFS, HOOK AND LOOP CLOSURE", "!1102");
		classes.put("GLOVES", "!1101");
		classes.put("SAFETY", "!1100");
		classes.put("SAFETY ROPE TO THROW", "!1098");
		classes.put("          ", "!1099");
		classes.put("HELMETS", "!1066");
		properties.put("Color", "!1212");
		properties.put("Adjustable head", "!1213");
		classes.put("JUNIOR", "!1065");
		classes.put("DURANCE", "!1064");
		classes.put("SLALOM", "!1097");
		classes.put("SAFETY ROPE TO THROW 20 METERS", "!1096");
		classes.put("SAFETY ROPE TO THROW 10 METERS", "!1095");
		classes.put("PADDLE LEASH", "!1094");
		properties.put("Lenght", "!1214");
		classes.put("ELASTIC LEASH", "!1093");
		properties.put("lenght", "!1206");
		properties.put("Diameter opening", "!1215");
		properties.put("Diameter D ring", "!1216");
		classes.put("TOWAGE GIRTH", "!1091");
		properties.put("length", "!1217");
		classes.put("WATERPROOF BAGS", "!1088");
		properties.put("Capacity", "!1219");
		classes.put("DAGGER", "!1090");
		classes.put("PVC WATERPROOF BAG", "!1087");
		classes.put("KIT 6EME CATEGORY", "!1089");
		classes.put("HIGH RESISTANCE WATERPROOF BAG", "!1086");
		classes.put("WATERPROOF SPORTING BAG", "!1084");
		classes.put("INFLATABLE AND FLOATING WATERPROOF BAG", "!1085");
		classes.put("HIGH RESISTANCE RUCKSACK", "!1083");
		classes.put("FLOATING WATERPROOF CASE", "!1082");
		properties.put("          ", "!1220");
		classes.put("SEAKAYAK WATERPROOF POCKET", "!1081");
		classes.put("HANDY PHONE PVC WATERPROOF BAG", "!1080");
		classes.put("WATERPROOF BAG \"PADDLE FLOAT\"", "!1079");
		classes.put("DECK BAG", "!1078");
		classes.put("OARS", "!1112");
		classes.put("OAR FOR SMALL DINGHIES OR INFLATABLE BOATS", "!1110");
		classes.put("OAR FOR INFLATABLE BOAT", "!1111");
		classes.put("TELESCOPIC PADDLE/BOATHOOK", "!1113");
		classes.put("ALUMINIUM OAR", "!1108");
		classes.put("OAR, TAKE APART POLYETHYLENE BLADE", "!1114");
		classes.put("ALUMINIUM JOINTED OAR ", "!1118");
		classes.put("VARNISHED WOODEN OAR MADE OF NORTH PINE", "!1144");
		classes.put("VARNISHED WOODEN OAR EPINETTE", "!1146");
		classes.put("TAKE APART VARNISHED WOODEN OAR", "!1147");
		classes.put("WATERPROOF EMERGENCY PADDLE", "!1148");
		classes.put("BABY PADDLE", "!1150");
		classes.put("JUNIOR PADDLE", "!1151");
		classes.put("SENIOR PADDLE", "!1152");
		classes.put("BABY OAR", "!1145");
		classes.put("JUNIOR OAR", "!1129");
		classes.put("SENIOR OAR", "!1130");
		classes.put("SENIOR COMBI", "!1120");
		classes.put("ECONOMY PADDLE", "!1121");
		classes.put("KID PADDLE", "!1119");
		classes.put("RENTAL KAYAK PADDLE", "!1123");
		classes.put("JUNIOR SLALOM KAYAK ALUMINIUM PADDLE", "!1122");
		classes.put("SENIOR SLALOM KAYAK ALUMINIUM PADDLE", "!1124");
		classes.put("JUNIOR SLALOM KAYAK ZICRAL PADDLE", "!1125");
		classes.put("JUNIOR SLALOM KAYAK ZICRAL PADDLE PP", "!1126");
		classes.put("SEA KAYAK PADDLES", "!1138");
		classes.put("SENIOR SLALOM KAYAK ZICRAL PADDLE PP", "!1127");
		classes.put("JUNIOR SLALOM KAYAK ALUMINIUM FIBRYLON PADDLE", "!1128");
		classes.put("SENIOR SLALOM KAYAK ALUMINIUM FIBRYLON PADDLE", "!1115");
		classes.put("SLALOM ASYMMETRICAL KAYAK PADDLE", "!1116");
		classes.put("KINETIC PADDLE", "!1117");
		classes.put("RACING SLALOM PADDLE", "!1153");
		classes.put("SENIOR DESCENT KAYAK ALUMINIUM PP PADDLE", "!1135");
		classes.put("ASYMMETRICAL KAYAK PADDLE", "!1134");
		classes.put("SENIOR DESCENT KAYAK ALUMLINIUM FIBRYLON PADDLE", "!1136");
		classes.put("JUNIOR OR SENIOR SLALOM KAYAK PADDLES", "!1137");
		classes.put("CANADIAN ECONOMY PADDLE", "!1169");
		classes.put("SEA KAYAK PADDLE  - BLADE 440 x 180 mm", "!1139");
		classes.put("TAKE APART ECONOMY DOUBLE PADDLE", "!1142");
		classes.put("SEA KAYAK PADDLE - BLADE 450 x 162 mm", "!1140");
		classes.put("TAKE APART PADDLES", "!1141");
		classes.put("JUNIOR SLALOM KAYAK TAKE APART PADDLE", "!1143");
		classes.put("SENIOR SLALOM KAYAK TAKE APART PADDLE", "!1178");
		classes.put("TAKE APART KAYAK PADDLE", "!1177");
		classes.put("SENIOR TAKE APART KAYAK PADDLE", "!1176");
		classes.put("SENIOR DESCENT KAYAK TAKE APART PADDLE", "!1175");
		classes.put("TAKE APART WOODEN KAYAK PADDLE", "!1174");
		classes.put("ALUMINIUM PP CONVERTIBLE PADDLE", "!1173");
		classes.put("ALUMINIUM FIBRYLON CONVERTIBLE PADDLE", "!1172");
		classes.put("CANOE PADDLES", "!1171");
		classes.put("KID CANOE PADDLE", "!1170");
		classes.put("ALUMINIUM PP PADDLE CANOE", "!1168");
		classes.put("ALUMINIUM FIBRYLON PADDLE CANOE", "!1167");
		classes.put("FIBRE TUBE LENGTH 100", "!1155");
		classes.put("ZICRAL CANOE PADDLE", "!1166");
		classes.put("RAFT PADDLE", "!1164");
		classes.put("CANADIAN WOODEN PADDLE", "!1165");
		classes.put("RAFT GUIDE PADDLE", "!1163");
		classes.put("BLADES", "!1161");
		properties.put("Material", "!1226");
		properties.put("Paddle", "!1227");
		classes.put("PADDLES ACCESSORIES AND SPARE PARTS", "!1162");
		classes.put("HANDLE FOR CANOE PADDLE", "!1160");
		classes.put("ALUMINIUM RING", "!1158");
		classes.put("PADDLE GRIP 230 x 26 mm", "!1159");
		classes.put("DRIP RING", "!1131");
		classes.put("ALUMINIUM TUBE DIAMETER 28", "!1180");
		classes.put("SHRINK WRAPP FOR DIAMETER 28", "!1132");
		classes.put("SHRINK WRAPP FOR DIAMETER 30", "!1133");
		classes.put("ZICRAL TUBE DIAMETER 28", "!1179");
		classes.put("ALUMINIUM TUBE DIAMETER 30", "!1157");
		classes.put("ZICRAL TUBE DIAMETER 30", "!1156");
		classes.put("FIBRE TUBE LENGTH 111", "!1154");
		classes.put("FIBRE TUBE LENGTH 116", "!1201");
		classes.put("FIBRE TUBE LENGTH 126", "!1200");
		classes.put("FIBRE TUBE LENGTH 131", "!1199");
		classes.put("FIBRE TUBE LENGTH 136", "!1198");
		classes.put("OARS ACCESSORIES AND SPARE PARTS", "!1197");
		classes.put("ROWLOCKS", "!1196");
		classes.put("PLASTIC ROWLOCK", "!1195");
		properties.put("Stem diameter", "!1230");
		properties.put("Lyra diameter", "!1231");
		properties.put("Adaptable", "!1232");
		classes.put("PLASTIC ROWLOCK HOLDER", "!1194");
		properties.put("inside diameter", "!1233");
		properties.put("outside diameter", "!1234");
		properties.put("center axis", "!1235");
		classes.put("CHROMIUM BRASS ROWLOCK", "!1193");
		properties.put("Stem length", "!1237");
		classes.put("CHROMIUM BRASS CONNECTION", "!1192");
		properties.put("Height", "!1228");
		properties.put("Center axis", "!1229");
		properties.put("Inside diameter", "!1236");
		properties.put("Outside diameter", "!1240");
		classes.put("STRATLINE ROWLOCK", "!1191");
		classes.put("COLLARS", "!1190");
		properties.put("Collorette diameter", "!1243");
		properties.put("Suitable to oar code item", "!1244");
		classes.put("PLUG + PAWL FOR OAR  164", "!1189");
		classes.put("HANDLE FOR OAR", "!1188");
		classes.put("POLYPROPYLENE BLADE", "!1185");
		properties.put("Sizes", "!1246");
		properties.put("Suitable to", "!1247");
		classes.put("BLADES", "!1187");
		classes.put("POLYETHYLENE BLADE ", "!1186");
		classes.put("POLYPROPYLENE BLADE", "!1184");
		classes.put("TRANSOM PADS", "!1183");
		classes.put("PLASTIC TRANSOM PAD", "!1182");
		classes.put("ALUMINIUM TRANSOM PAD", "!1109");
		classes.put("6 LITERS DOUBLE WAY HAND PUMP", "!1181");
		classes.put("3 LITERS PLASTIC PUMP", "!1107");
		classes.put("MUFFS", "!1105");
		classes.put("HUDSON", "!1062");
		properties.put("its_muff", "!6216");
		properties.put("virage", "!6237");
		properties.put("names", "!6250");
	}
}
