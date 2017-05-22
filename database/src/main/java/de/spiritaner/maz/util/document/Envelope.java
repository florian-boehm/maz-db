package de.spiritaner.maz.util.document;

import de.spiritaner.maz.controller.DocumentPageController;
import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.model.Person;
import javafx.collections.ObservableList;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.Arrays;

public class Envelope {

	final static Logger logger = Logger.getLogger(Envelope.class);

	public static void create(final ObservableList<Person> persons, final File template, final File outFile, final DocumentPageController mpc) {
		if (template != null && outFile != null) {
			if (template.getName().endsWith(".docx")) {
				mpc.setProgress(true, "Speichere Ausgabedatei ...", 0);

				int i = 1;

				for (Person person : persons) {
					person.initVolatiles();

					try {
						mpc.setProgress(true, "Speichere Ausgabedatei ...", ((double) i++) / persons.size());

						MailMergeData data = new MailMergeData();
						String[] headers = {"title", "name", "additional", "street", "city", "country"};
						data.getHeaders().addAll(Arrays.asList(headers));

						if (person.getPreferredResidence() != null) {
							Address address = person.getPreferredResidence().getAddress();
							String genderPrefix = "";

							if (person.getGender() != null) {
								switch (person.getGender().getId().intValue()) {
									case 1:
										genderPrefix = "Herr ";
										break;
									case 2:
										genderPrefix = "Frau ";
										break;
									default:
										genderPrefix = "";
								}
							}

							String[] personAddressHeader = {
									  genderPrefix + ((person.getHonorific() == null) ? "" : person.getHonorific()),
									  person.getFullName(),
									  address.getAddition(),
									  address.getStreet() + " " + address.getHouseNumber(),
									  address.getPostCode() + " " + address.getCity(),
									  address.getState() + " - " + address.getCountry(),
							};

							data.getData().add(Arrays.asList(personAddressHeader));

							MailMerge.merge(template, outFile, data);
						}
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(e);
					}
				}

				logger.info("Envelopes were created at " + outFile.getAbsolutePath());
			}
		}
	}
}
