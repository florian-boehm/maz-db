package de.spiritaner.maz.util.document;

import de.spiritaner.maz.model.Event;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSimpleField;

import java.io.File;
import java.io.FileInputStream;

public class ParticipantList {

	final static Logger logger = Logger.getLogger(ParticipantList.class);

	public static void forEvent(Event event, Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Vorlage laden");
		File wordDocument = fileChooser.showOpenDialog(stage);

		if(wordDocument != null) {
			if(wordDocument.getName().endsWith(".docx")) {
				try
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
				}
			}
		}
	}
}
