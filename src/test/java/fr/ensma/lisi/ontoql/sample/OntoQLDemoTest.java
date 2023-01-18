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
public class OntoQLDemoTest extends OntoQLTestCase {

	@Test
	public void testOntoWebStudioODBASEEN() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);
		statement.executeUpdate("INSERT INTO #ontology (#namespace) values ('http://www.ecatpme.fr/')");
		s.setDefaultNameSpace("http://www.ecatpme.fr/");

		statement.executeUpdate(
				"CREATE #CLASS bearing (DESCRIPTOR (#code ='0002-38491502100024#01-BEARING#1', #name[fr] = 'roulement', #definition[en]='general bearing parts family', #definition[fr]='famille de composants roulement'))");
		statement.executeUpdate(
				"ALTER #CLASS bearing ADD \"inner diameter\" Real DESCRIPTOR (#code = '0002-38491502100024#02-INNER_DIAMETER#1', #name[fr] = 'diamètre interne', #definition[fr] = 'le diamètre interne du roulement', #definition[en] = 'the bearing inner diameter')");
		statement.executeUpdate(
				"ALTER #CLASS bearing ADD \"outer diameter\" Real DESCRIPTOR (#code = '0002-38491502100024#02-OUTER_DIAMETER#1', #name[fr] = 'diamètre externe', #definition[fr] = 'le diamètre externe du roulement', #definition[en] = 'the bearing outer diameter')");
		statement.executeUpdate(
				"ALTER #CLASS bearing ADD designation String DESCRIPTOR (#code = '0002-38491502100024#02-DESIGNATION#1', #name[fr] = 'désignation', #definition[fr] = 'la désignation du paw', #definition[en] = 'the paw designation')");
		statement.executeUpdate(
				"CREATE #CLASS paw under bearing (DESCRIPTOR (#code ='0002-38491502100024#01-PAW#1', #name[fr] = 'paw', #definition[en]='paw parts family', #definition[fr]='famille de composants paw'))");
		statement.executeUpdate(
				"ALTER #CLASS paw ADD thickness Real DESCRIPTOR (#code = '0002-38491502100024#02-THICKNESS#1', #name[fr] = 'épaisseur', #definition[fr] = 'épaisseur du paw', #definition[en] = 'the paw thickness')");
		statement.executeUpdate(
				"ALTER #CLASS paw ADD type ENUM ('T1', 'T2', 'T3') DESCRIPTOR (#code = '0002-38491502100024#02-TYPE#1', #name[fr] = 'type', #definition[fr] = 'le type de paw', #definition[en] = 'the paw type')");

		statement.executeUpdate(
				"CREATE EXTENT OF paw (\"inner diameter\", thickness, \"outer diameter\", designation, type)");
		statement.executeUpdate(
				"insert into paw (\"inner diameter\", thickness, \"outer diameter\", designation, type) values (10.0, 1.0, 15.0,'Nice PAW - type 1', 'T1')");
		statement.executeUpdate(
				"insert into paw (\"inner diameter\", thickness, \"outer diameter\", designation, type) values (11.0, 1.0, 16.5,'Nice PAW - type 2', 'T2')");
		statement.executeUpdate(
				"insert into paw (\"inner diameter\", thickness, \"outer diameter\", designation, type) values (13.0, 2.0, 19.5,'Nice PAW - type 3', 'T3')");
		statement.executeUpdate(
				"insert into paw (\"inner diameter\", thickness, \"outer diameter\", designation, type) values (17.0, 3.0, 25.5,'Very nice PAW - type 1', 'T1')");
		statement.executeUpdate(
				"insert into paw (\"inner diameter\", thickness, \"outer diameter\", designation, type) values (19.0, 4.0, 28.5,'Very nice PAW - type 1', 'T2')");

		t.commit();
	}

	@Test
	public void testOntoWebStudioODBASE() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);
		statement.executeUpdate("INSERT INTO #ontology (#namespace) values ('http://www.cfca.fr/')");
		s.setDefaultNameSpace("http://www.cfca.fr/");
		statement.executeUpdate("CREATE ENTITY #uri_Type under #stringType");

		statement.executeUpdate(
				"CREATE #CLASS Vehicle (DESCRIPTOR (#code ='AFBDF54D', #name[fr] = 'Véhicule', #definition[en]='A Vehicle', #definition[fr]='Un Vehicule'))");
		statement.executeUpdate(
				"ALTER #CLASS Vehicle ADD \"number of wheels\" int DESCRIPTOR (#code = '7244BA605F5B0', #name[fr]='Nombre de roues', #definition[fr]='Nombre de roues')");
		statement.executeUpdate(
				"ALTER #CLASS Vehicle ADD model String DESCRIPTOR (#code = '7244BA605F5CF', #name[fr]='modele', #definition[fr]='modele')");
		statement.executeUpdate(
				"ALTER #CLASS Vehicle ADD tyreReferences String ARRAY DESCRIPTOR (#code = '7244BA605F5CF', #name[fr]='referencesPneumatique', #definition[fr]='referencesPneumatique')");
		statement.executeUpdate(
				"CREATE #CLASS Car under Vehicle (DESCRIPTOR (#code ='7244BA608F155', #name[fr] = 'Voiture', #definition[en]='A Car', #definition[fr]='Une Voiture'))");
		statement.executeUpdate(
				"ALTER #CLASS Car ADD color String DESCRIPTOR (#code = '7244BA608F184', #name[fr]='Couleur', #definition[fr]='Couleur')");
		statement.executeUpdate("CREATE extent of Car (color, \"number of wheels\", model, tyreReferences)");
		statement.executeUpdate(
				"CREATE #CLASS Truck under Vehicle (DESCRIPTOR (#code ='7244BA60ECE32', #name[fr] = 'Camion', #definition[en]='A truck', #definition[fr]='Un Camion'))");
		statement.executeUpdate(
				"ALTER #CLASS Truck ADD company String DESCRIPTOR (#code = '7244BA60ECE52', #name[fr]='company', #definition[fr]='Entreprise')");
		statement.executeUpdate("CREATE extent of Truck (company, \"number of wheels\", model, tyreReferences)");
		statement.executeUpdate(
				"CREATE #CLASS Motorcycle under Vehicle (DESCRIPTOR (#code ='7244BA61A2B4D', #name[fr] = 'Moto', #definition[en]='A motorcycle', #definition[fr]='Une moto'))");
		statement.executeUpdate(
				"ALTER #CLASS Motorcycle ADD capacity int DESCRIPTOR (#code = '7244BA60D23AF', #name[fr]='Cylindree', #definition[fr]='Cylindree')");
		statement.executeUpdate("CREATE extent of Motorcycle (capacity, \"number of wheels\", model, tyreReferences)");
		statement.executeUpdate(
				"CREATE #CLASS Person (DESCRIPTOR (#code ='AXDFBDF54D', #name[fr] = 'Personne', #definition[en]='A person', #definition[fr]='Une personne'))");
		statement.executeUpdate(
				"ALTER #CLASS Person ADD age int DESCRIPTOR (#code = '7244BA612645F', #name[fr]='age', #definition[fr]='age')");
		statement.executeUpdate(
				"ALTER #CLASS Person ADD \"other vehicles\" REF(Vehicle) ARRAY DESCRIPTOR(#code = '7244BA61264AF', #name[fr] = 'autres vehicules', #definition[fr]='autres vehicules')");
		statement.executeUpdate(
				"ALTER #CLASS Person ADD name String DESCRIPTOR(#code='7244BA6126430', #name[fr]='nom', #definition[fr]='nom')");
		statement.executeUpdate(
				"ALTER #CLASS Person ADD \"main vehicle\" REF(Vehicle) DESCRIPTOR(#code='7244BA612647F', #name[fr] = 'vehicule principal', #definition[fr] = 'vehicule principal')");
		statement.executeUpdate(
				"ALTER #CLASS Person ADD hasDriverLicence boolean DESCRIPTOR(#code='7244BA612647F', #name[fr] = 'A un permis', #definition[fr] = 'Permet de savoir si la personne a un permis')");
		statement.executeUpdate(
				"ALTER #CLASS Person ADD blogUri URIType DESCRIPTOR(#code='7244BA612647F', #name[fr] = 'BlogUri', #definition[fr] = 'A un blog')");
		statement.executeUpdate(
				"ALTER #CLASS Person ADD sexe ENUM ('homme','femme','inconnu') DESCRIPTOR(#code='7244BA612647F', #name[fr] = 'sexe', #definition[fr] = 'permet de connaitre le sexe')");
		statement.executeUpdate(
				"ALTER #CLASS Person ADD taille Real DESCRIPTOR(#code='7244BA612647F', #name[fr] = 'taille', #definition[fr] = 'la taille de la personne')");
		statement.executeUpdate(
				"create extent of Person (age, \"other vehicles\", name, \"main vehicle\", hasDriverLicence, blogUri, sexe, taille)");

		statement.executeUpdate(
				"INSERT INTO Car (oid, \"number of wheels\", color, model, tyreReferences) values (29, 4, 'Black', 'BMW Z3', ARRAY['Michelin', 'Pirreli'])");
		statement.executeUpdate(
				"INSERT INTO Car (oid, \"number of wheels\", color, model) values (31, 4, 'Grey', 'Porsche Cayenne')");
		statement.executeUpdate(
				"INSERT INTO Car (oid, \"number of wheels\", color, model) values (39, 4, 'Blue', 'Peugeot 406')");
		statement.executeUpdate(
				"INSERT INTO Car (oid, \"number of wheels\", color, model) values (30, 4, 'Blue', 'Peugeot 406')");
		statement.executeUpdate(
				"INSERT INTO Car (oid, \"number of wheels\", color, model) values (41, 4, 'Red', 'Mini Cooper')");
		statement.executeUpdate(
				"INSERT INTO Car (oid, \"number of wheels\", color, model) values (42, 4, 'Black', 'Citroen C6')");
		statement.executeUpdate(
				"INSERT INTO Truck (oid, company, \"number of wheels\", model) values (35, 'DHL', 12, 'Mercedes-Benz')");
		statement.executeUpdate(
				"INSERT INTO Truck (oid, company, \"number of wheels\", model) values (33, 'Dentressangle', 12, 'Mercedes Actros')");
		statement.executeUpdate(
				"INSERT INTO Motorcycle (oid, \"number of wheels\", capacity, model) values (32, 2, 1170, 'BMW 1200R')");
		statement.executeUpdate(
				"INSERT INTO Person (oid, age, \"other vehicles\", name, \"main vehicle\", hasDriverLicence, blogUri, sexe, taille) values (36, 36, ARRAY[30,32,31], 'Martin', 41, true, 'http://www.google.fr', 'homme', 1.87)");

		t.rollback();
	}

	@Test
	public void testOntoWebStudioODBASEFR() throws SQLException, QueryException {
		OntoQLSession s = getSession();
		Transaction t = s.beginTransaction();

		OntoQLStatement statement = s.createOntoQLStatement();
		s.setReferenceLanguage(OntoQLHelper.ENGLISH);
		statement.executeUpdate("INSERT INTO #ontology (#namespace) values ('http://www.ecatpme.fr/')");
		s.setDefaultNameSpace("http://www.ecatpme.fr/");

		statement.executeUpdate(
				"CREATE #CLASS bearing (DESCRIPTOR (#code ='0002-38491502100024#01-BEARING#1', #name[fr] = 'roulement', #definition[en]='general bearing parts family', #definition[fr]='famille de composants roulement'))");
		statement.executeUpdate(
				"ALTER #CLASS bearing ADD \"inner diameter\" Real DESCRIPTOR (#code = '0002-38491502100024#02-INNER_DIAMETER#1', #name[fr] = 'diamètre interne', #definition[fr] = 'le diamètre interne du roulement', #definition[en] = 'the bearing inner diameter')");
		statement.executeUpdate(
				"ALTER #CLASS bearing ADD \"outer diameter\" Real DESCRIPTOR (#code = '0002-38491502100024#02-OUTER_DIAMETER#1', #name[fr] = 'diamètre externe', #definition[fr] = 'le diamètre externe du roulement', #definition[en] = 'the bearing outer diameter')");
		statement.executeUpdate(
				"ALTER #CLASS bearing ADD designation String DESCRIPTOR (#code = '0002-38491502100024#02-DESIGNATION#1', #name[fr] = 'désignation', #definition[fr] = 'la désignation du paw', #definition[en] = 'the paw designation')");
		statement.executeUpdate(
				"CREATE #CLASS paw under bearing (DESCRIPTOR (#code ='0002-38491502100024#01-PAW#1', #name[fr] = 'paw', #definition[en]='paw parts family', #definition[fr]='famille de composants paw'))");
		statement.executeUpdate(
				"ALTER #CLASS paw ADD thickness Real DESCRIPTOR (#code = '0002-38491502100024#02-THICKNESS#1', #name[fr] = 'épaisseur', #definition[fr] = 'épaisseur du paw', #definition[en] = 'the paw thickness')");
		statement.executeUpdate(
				"ALTER #CLASS paw ADD type ENUM ('T1', 'T2', 'T3') DESCRIPTOR (#code = '0002-38491502100024#02-TYPE#1', #name[fr] = 'type', #definition[fr] = 'le type de paw', #definition[en] = 'the paw type')");

		statement.executeUpdate(
				"CREATE EXTENT OF paw (\"inner diameter\", thickness, \"outer diameter\", designation, type)");
		statement.executeUpdate(
				"insert into paw (\"inner diameter\", thickness, \"outer diameter\", designation, type) values (10.0, 1.0, 15.0,'Joli PAW - type 1', 'T1')");
		statement.executeUpdate(
				"insert into paw (\"inner diameter\", thickness, \"outer diameter\", designation, type) values (11.0, 1.0, 16.5,'Joli PAW - type 2', 'T2')");
		statement.executeUpdate(
				"insert into paw (\"inner diameter\", thickness, \"outer diameter\", designation, type) values (13.0, 2.0, 19.5,'Joli PAW - type 3', 'T3')");
		statement.executeUpdate(
				"insert into paw (\"inner diameter\", thickness, \"outer diameter\", designation, type) values (17.0, 3.0, 25.5,'Très joli PAW - type 1', 'T1')");
		statement.executeUpdate(
				"insert into paw (\"inner diameter\", thickness, \"outer diameter\", designation, type) values (19.0, 4.0, 28.5,'Très joli PAW - type 2', 'T2')");

		t.commit();
	}
}
