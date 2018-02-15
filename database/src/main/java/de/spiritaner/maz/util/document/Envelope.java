package de.spiritaner.maz.util.document;

import de.spiritaner.maz.controller.DocumentPageController;
import de.spiritaner.maz.model.Address;
import de.spiritaner.maz.model.Person;
import de.spiritaner.maz.model.Residence;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceDialog;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Envelope {

	final static Logger logger = Logger.getLogger(Envelope.class);

	public static void create(final ObservableList<Person> persons, final File template, final File outFile, final DocumentPageController mpc, final Options options) {
		if (template != null && outFile != null && persons != null && options != null) {
			if (template.getName().endsWith(".docx")) {
				mpc.setProgress(true, "Speichere Ausgabedatei ...", 0);

				int i = 1;

				MailMergeData data = new MailMergeData();
				String[] headers = {"title", "name", "additional", "street", "city", "country"};
				data.getHeaders().addAll(Arrays.asList(headers));

				//if(options.isMergeRelationshipAddresses()) FamilyHelper.merge(persons);

				for (final Person person : persons) {
					mpc.setProgress(true, "Speichere Ausgabedatei ...", ((double) i++) / persons.size());

					Hibernate.initialize(person.getResidences());
					person.initVolatiles();
					final List<Residence> residences = person.getResidences();
					residences.removeIf(residence -> !residence.isForPost());
					final List<Address> postAddresses = new ArrayList<>();
					residences.forEach(residence -> postAddresses.add(residence.getAddress()));

					/*EntityManager em = CoreDatabase.getFactory().createEntityManager();
					TypedQuery<Residence> postAddressQuery = em.createNamedQuery("Residence.findPostAddressForPerson", Residence.class);
					postAddressQuery.setParameter("person",person);
					List<Residence> postAddresses = postAddressQuery.getResultList();*/

					if(postAddresses.size() >= 1) {
						final Address address;

						if(postAddresses.size() > 1 && options.isAskOnMultiplePostAddresses()) {
							address = askForResidence(person, postAddresses);
						} else {
							address = postAddresses.get(0);
						}

						String[] personAddressHeader = {
								  getGenderPrefix(person) + ((person.getHonorific() == null) ? "" : person.getHonorific()),
								  person.getFullName(),
								  address.getAddition(),
								  address.getStreet() + " " + address.getHouseNumber(),
								  address.getPostCode() + " " + address.getCity(),
								  address.getState() + " - " + address.getCountry(),
						};

						data.getData().add(Arrays.asList(personAddressHeader));
					}
				}


				try {
					MailMerge.merge(template, outFile, data);
				} catch (Exception e) {
					logger.error(e);
				}

				logger.info("Envelopes were created at " + outFile.getAbsolutePath());
			}
		}
	}

	public static Address askForResidence(final Person person, final List<Address> addresses) {
		ChoiceDialog<Address> dialog = new ChoiceDialog<>(addresses.get(0), addresses);
		dialog.setTitle("Auswahl der Postadresse");
		dialog.setHeaderText(person.getFullName() + " hat mehrere mögl. Postadressen ...");
		dialog.setContentText("Zu verwendende Adresse:");

		Optional<Address> result = dialog.showAndWait();

		return result.orElseGet(() -> addresses.get(0));
	}

	private static String getGenderPrefix(final Person person) {
		if (person.getGender() != null) {
			switch (person.getGender().getId().intValue()) {
				case 1:
					return "Herr ";
				case 2:
					return "Frau ";
				default:
					return "";
			}
		}

		return "";
	}

	public static class Options {
		boolean askOnMultiplePostAddresses;
		boolean mergeRelationshipAddresses;

		public boolean isAskOnMultiplePostAddresses() {
			return askOnMultiplePostAddresses;
		}

		public void setAskOnMultiplePostAddresses(boolean askOnMultiplePostAddresses) {
			this.askOnMultiplePostAddresses = askOnMultiplePostAddresses;
		}

		public boolean isMergeRelationshipAddresses() {
			return mergeRelationshipAddresses;
		}

		public void setMergeRelationshipAddresses(boolean mergeRelationshipAddresses) {
			this.mergeRelationshipAddresses = mergeRelationshipAddresses;
		}
	}
}
