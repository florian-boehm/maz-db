package de.spiritaner.maz.util;

import liquibase.change.custom.CustomSqlChange;
import liquibase.database.Database;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.RawSqlStatement;

public class AssetGenerator implements CustomSqlChange {

	@SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
	private ResourceAccessor resourceAccessor;

	@Override
	public SqlStatement[] generateStatements(Database database) throws CustomChangeException {
		return new SqlStatement[]{
				  // Gender
				  new RawSqlStatement("MERGE INTO PUBLIC.GENDER (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('1', 'Männlich', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.GENDER (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('2', 'Weiblich', 'FALSE', 'FALSE')"),
				  // Contact method type
				  new RawSqlStatement("MERGE INTO PUBLIC.CONTACTMETHODTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('1', 'E-Mail', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.CONTACTMETHODTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('2', 'Mobiltelefon', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.CONTACTMETHODTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('3', 'Diensttelefon', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.CONTACTMETHODTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('4', 'Telefon', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.CONTACTMETHODTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('5', 'Skype', 'FALSE', 'FALSE')"),
				  // Diocese
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('1', 'Aachen', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('2', 'Augsburg', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('3', 'Bamberg', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('4', 'Berlin', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('5', 'Dresden-Meißen', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('6', 'Eichstätt', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('7', 'Erfurt', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('8', 'Essen', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('9', 'Freiburg', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('10', 'Fulda', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('11', 'Görlitz', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('12', 'Hamburg', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('13', 'Hildesheim', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('14', 'Köln', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('15', 'Limburg', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('16', 'Magdeburg', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('17', 'Mainz', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('18', 'München und Freising', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('19', 'Münster', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('20', 'Osnabrück', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('21', 'Paderborn', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('22', 'Passau', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('23', 'Regensburg', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('24', 'Rottenburg-Stuttgart', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('25', 'Speyer', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('26', 'Trier', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.DIOCESE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('26', 'Würzburg', 'FALSE', 'FALSE')"),
				  // Approval type
				  new RawSqlStatement("MERGE INTO PUBLIC.APPROVALTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('1', 'Datenschutzerklärung', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.APPROVALTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('2', 'Fotoerklärung', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.APPROVALTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('3', 'Rundbrief', 'FALSE', 'FALSE')"),
				  // Role type
				  new RawSqlStatement("MERGE INTO PUBLIC.ROLETYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('1', 'MaZ', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.ROLETYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('2', 'MaZ-Team', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.ROLETYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('3', 'MaZ-Interessent/in', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.ROLETYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('4', 'MaZ-Verantwortliche/r', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.ROLETYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('5', 'MaZ anderer Orden', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.ROLETYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('6', 'EMT', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.ROLETYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('7', 'Eltern', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.ROLETYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('8', 'Verwandte', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.ROLETYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('9', 'Ansprechperson Einsatzstelle', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.ROLETYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('10', 'Spender/in', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.ROLETYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('11', 'Ordensmensch', 'FALSE', 'FALSE')"),
				  // Residence type
				  new RawSqlStatement("MERGE INTO PUBLIC.RESIDENCETYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('1', 'Studien-/Ausbildungsort', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.RESIDENCETYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('2', 'Elternhaus', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.RESIDENCETYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('3', 'Einsatzort', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.RESIDENCETYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('4', 'Dienstort/Büro', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.RESIDENCETYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('5', 'Längerfristiger Wohnort', 'FALSE', 'FALSE')"),
				  // Participation type
				  new RawSqlStatement("MERGE INTO PUBLIC.PARTICIPATIONTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('1', 'Teamer/in', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.PARTICIPATIONTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('2', 'Teilnehmer/in', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.PARTICIPATIONTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('3', 'MaZ-Team', 'FALSE', 'FALSE')"),
				  // Event type
				  new RawSqlStatement("MERGE INTO PUBLIC.EVENTTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('1', 'Aktueller Jahrgang (ww-gefördert)', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.EVENTTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('2', 'Aktueller Jahrgang', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.EVENTTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('3', 'Rückkehrerveranstaltung', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.EVENTTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('4', 'Team-Treffen', 'FALSE', 'FALSE')"),
				  // Salutation
				  new RawSqlStatement("MERGE INTO PUBLIC.SALUTATION (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('1', 'Sie', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.SALUTATION (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('2', 'Du', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.SALUTATION (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('3', 'Mann & Frau - Du', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.SALUTATION (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('4', 'Mann & Frau - Sie', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.SALUTATION (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('5', 'Familie - Du', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.SALUTATION (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('6', 'Familie - Sie', 'FALSE', 'FALSE')"),
				  // Relationship type
				  new RawSqlStatement("MERGE INTO PUBLIC.RELATIONSHIPTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('1', 'Vater', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.RELATIONSHIPTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('2', 'Mutter', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.RELATIONSHIPTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('3', 'Tochter', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.RELATIONSHIPTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('4', 'Sohn', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.RELATIONSHIPTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('5', 'Bruder', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.RELATIONSHIPTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('6', 'Schwester', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.RELATIONSHIPTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('7', 'Ehemann', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.RELATIONSHIPTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('8', 'Ehefrau', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.RELATIONSHIPTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('9', 'Lebensgefährte', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.RELATIONSHIPTYPE (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('10', 'Lebensgefährtin', 'FALSE', 'FALSE')"),
				  // Religion
				  new RawSqlStatement("MERGE INTO PUBLIC.RELIGION (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('1', 'Konfessionslos', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.RELIGION (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('2', 'Römisch-katholische Kirche', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.RELIGION (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('3', 'Evangelische Kirche', 'FALSE', 'FALSE')"),
				  // Person group
				  new RawSqlStatement("MERGE INTO PUBLIC.PERSONGROUP (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('1', 'Verantwortliche(r)', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.PERSONGROUP (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('2', 'Gemeinschaft', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.PERSONGROUP (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('3', 'Schwestern/Patres in der Gemeinschaft', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.PERSONGROUP (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('4', 'Partnerorganisation', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.PERSONGROUP (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('5', 'Kontakt in Deutschland', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.PERSONGROUP (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('6', 'Arbeitsplatz', 'FALSE', 'FALSE')"),
				  new RawSqlStatement("MERGE INTO PUBLIC.PERSONGROUP (ID, DESCRIPTION, REMOVABLE, EDITABLE) VALUES ('7', 'Externe(r) Mentor(in)', 'FALSE', 'FALSE')"),
		};
	}

	@Override
	public String getConfirmationMessage() {
		return "Assets have been successfully created by AssetGenerator.class";
	}

	@Override
	public void setUp() throws SetupException {

	}

	@Override
	public void setFileOpener(ResourceAccessor resourceAccessor) {
		this.resourceAccessor = resourceAccessor;
	}

	@Override
	public ValidationErrors validate(Database database) {
		return new ValidationErrors();
	}
}
