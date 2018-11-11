package de.spiritaner.maz.util.validator;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.apache.log4j.Logger;
import org.controlsfx.control.PopOver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PopOverVisitor extends MessageVisitor {

	private static final Logger logger = Logger.getLogger(PopOverVisitor.class);
	public HashMap<Node, PopOver> popOvers = new HashMap<>();

	@Override
	public void reset() {
		super.reset();

		for(PopOver popOver : popOvers.values()) {
			popOver.hide();
			VBox vbox = (VBox) popOver.getContentNode();
			vbox.getChildren().clear();
		}
	}

	@Override
	public void nextNode(Node node) {
		super.nextNode(node);

		if(!popOvers.containsKey(node))
			popOvers.put(node, initPopOver(node));
	}

	@Override
	public void finished() {
		super.finished();

		for(Map.Entry<Node, ArrayList<String>> entry : super.messages.entrySet()) {
			final Node node = entry.getKey();
			final PopOver popOver = popOvers.get(node);

			if(entry.getValue().size() == 0)
				popOver.hide();
			else {
				entry.getValue().forEach(s -> addMsg((VBox) popOver.getContentNode(), s));

				try {
					if (!popOver.isShowing())
						popOver.setAutoHide(true);

					popOver.show(node);
				} catch (NullPointerException e) {
					//logger.warn(e);
				}
			}
		}
	}

	private PopOver initPopOver(final Node node) {
		PopOver tmpPopOver = new PopOver(node);
		VBox vbox = new VBox();

		vbox.setAlignment(Pos.CENTER_LEFT);
		vbox.setPadding(new Insets(2));

		tmpPopOver.setAutoHide(true);
		tmpPopOver.setDetachable(false);
		tmpPopOver.setContentNode(vbox);

		return tmpPopOver;
	}


	public void addMsg(VBox vbox, String message) {
		Label label = new Label(message);

		// TODO Move style to class in stylesheet file
		//label.setPadding(new Insets(2));
		//label.setStyle("-fx-text-fill: #B80024; -fx-font-weight: bold");
		label.getStyleClass().add("validation_label");

		vbox.getChildren().add(label);
	}
}
