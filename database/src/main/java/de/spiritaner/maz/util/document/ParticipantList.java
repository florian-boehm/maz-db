package de.spiritaner.maz.util.document;

import de.spiritaner.maz.controller.DocumentPageController;
import de.spiritaner.maz.model.Approval;
import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.util.database.CoreDatabase;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;

import javax.persistence.EntityManager;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParticipantList {

	final static Logger logger = Logger.getLogger(ParticipantList.class);

	public static void createForEvent(final Event event, final File template, final File outFile, final DocumentPageController mpc) {
		if (template != null && outFile != null) {
			if (template.getName().endsWith(".docx")) {
				try {
					final StringBuffer firstNameBuffer = new StringBuffer();
					final StringBuffer familyNameBuffer = new StringBuffer();
					final StringBuffer participatedBuffer = new StringBuffer();
					final StringBuffer photoApprovalBuffer = new StringBuffer();

					mpc.setProgress(true, "Bereite Daten vor ...", -1);

					event.getParticipations().forEach(participation -> {
						firstNameBuffer.append(participation.getPerson().getFirstName() + "\n");
						familyNameBuffer.append(participation.getPerson().getFamilyName() + "\n");
						participatedBuffer.append((participation.getHasParticipated()) ? "Ja\n" : "Nein\n");

						// Load photo approval information from participating person
						EntityManager em = CoreDatabase.getFactory().createEntityManager();
						em.getTransaction().begin();
						Hibernate.initialize(participation.getPerson().getApprovals());
						List<Approval> approvals = participation.getPerson().getApprovals();

						approvals.forEach(approval -> {
							if (approval.getApprovalType().getId() == 2)
								photoApprovalBuffer.append(approval.isApproved() ? "Ja\n" : "Nein\n");
						});

						em.getTransaction().commit();
					});

					final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

					final MailMergeData data = new MailMergeData();
					String[] headers = {"eventTitle", "eventBegin", "eventEnd", "eventLocation", "participantFirstName", "participantFamilyName", "hasParticipated", "participantPhotoApproval"};
					data.getHeaders().addAll(Arrays.asList(headers));

					final ArrayList<String> eventData = new ArrayList<>();
					eventData.add(0, event.getName());
					eventData.add(1, dtf.format(event.getFromDate()));
					eventData.add(2, dtf.format(event.getToDate()));
					eventData.add(3, event.getLocation());
					eventData.add(4, firstNameBuffer.toString().trim());
					eventData.add(5, familyNameBuffer.toString().trim());
					eventData.add(6, participatedBuffer.toString().trim());
					eventData.add(7, photoApprovalBuffer.toString().trim());

					data.getData().add(eventData);

					mpc.setProgress(true, "Füge Daten in Vorlage ein ...", -1);
					MailMerge.merge(template, outFile, data);
					mpc.setProgress(true, "Speichere Ausgabedatei ...", -1);
					logger.info("Participant list was created at " + outFile.getAbsolutePath());
				} catch (Exception e) {
					logger.error(e);
				}
			}
		}
	}
}
