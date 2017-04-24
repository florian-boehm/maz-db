package de.spiritaner.maz.util.document;

import de.spiritaner.maz.model.Event;
import de.spiritaner.maz.model.Participation;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.docx4j.model.fields.merge.DataFieldName;
import org.docx4j.model.fields.merge.MailMerger;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;

import java.io.File;
import java.io.FileInputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParticipantList {

	final static Logger logger = Logger.getLogger(ParticipantList.class);

	public static void forEvent(Event event, Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Vorlage laden");
		File wordDocument = fileChooser.showOpenDialog(stage);

		if(wordDocument != null) {
			if(wordDocument.getName().endsWith(".docx")) {
				try {
					WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(wordDocument);

					final List<Map<DataFieldName, String>> data = new ArrayList<Map<DataFieldName, String>>();
					final StringBuffer firstNameBuffer = new StringBuffer();
					final StringBuffer familyNameBuffer = new StringBuffer();
					final StringBuffer participatedBuffer = new StringBuffer();

					event.getParticipations().forEach(participation -> {
						firstNameBuffer.append(participation.getPerson().getFirstName()+"\n");
						familyNameBuffer.append(participation.getPerson().getFamilyName()+"\n");
						participatedBuffer.append((participation.getHasParticipated()) ? "Ja\n" : "Nein\n");
					});

					Map<DataFieldName, String> item = new HashMap<DataFieldName, String>();
					item.put(new DataFieldName("eventTitle"), event.getName());
					final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
					item.put(new DataFieldName("eventBegin"), dtf.format(event.getFromDate()));
					item.put(new DataFieldName("eventEnd"), dtf.format(event.getToDate()));

					item.put(new DataFieldName("participantFirstName"), firstNameBuffer.toString());
					item.put(new DataFieldName("participantFamilyName"), familyNameBuffer.toString());
					item.put(new DataFieldName("hasParticipated"), participatedBuffer.toString());
					data.add(item);

					MailMerger.setMERGEFIELDInOutput(MailMerger.OutputField.KEEP_MERGEFIELD);
					MailMerger.performMerge(wordMLPackage, item, true);

					wordMLPackage.save(new File(wordDocument.getParent()+"/out.docx"));
				} catch (Docx4JException e) {
					e.printStackTrace();
				}

				/*try
				{
					FileInputStream fis = new FileInputStream(wordDocument.getAbsolutePath());
					XWPFDocument document = new XWPFDocument(fis);
					XWPFWordExtractor extractor = new XWPFWordExtractor(document);

					logger.info(extractor.getText());

					for(XWPFParagraph paragraph : document.getParagraphs()) {
						for(CTSimpleField field : paragraph.getCTP().getFldSimpleArray()) {
							System.out.println(field.getInstr());
						}
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}*/
			}
		}
	}
}
