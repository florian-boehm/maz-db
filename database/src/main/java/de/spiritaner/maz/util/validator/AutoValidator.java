package de.spiritaner.maz.util.validator;

import de.spiritaner.maz.view.component.BindableTextField;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AutoValidator {

	public HashMap<Node, ArrayList<Validation>> nodeValidations = new HashMap<>();
	public ValidationVisitor visitor = new SilentVisitor();

	public boolean validateAll() {
		boolean overallResult = true;

		visitor.reset();

		for(Map.Entry<Node, ArrayList<Validation>> entry : nodeValidations.entrySet()) {
			visitor.nextNode(entry.getKey());

			for(Validation validation : entry.getValue()) {
				boolean tmpResult = validation.isValid();
				validation.accept(visitor);
				overallResult = overallResult && tmpResult;
			}
		}

		visitor.finished();

		return overallResult;
	}

	public void add(Node node, Validation validation) {
		if(!nodeValidations.containsKey(node)) {
			nodeValidations.put(node, new ArrayList<>());
		}

		nodeValidations.get(node).add(validation);
	}

	public void add(Validation validation) {
		if(!nodeValidations.containsKey(validation.node)) {
			nodeValidations.put(validation.node, new ArrayList<>());
		}

		nodeValidations.get(validation.node).add(validation);
	}

	public boolean validate(final Node node) {
		boolean overallResult = true;

		visitor.reset();
		visitor.nextNode(node);

		for(Validation validation : nodeValidations.get(node)) {
			boolean tmpResult = validation.isValid();
			validation.accept(visitor);
			overallResult = overallResult && tmpResult;
		}

		visitor.finished();

		return overallResult;
	}

	public void validateOnChange(final Node node) {
		if(nodeValidations.containsKey(node)) {
			if(node instanceof BindableTextField) {
				((BindableTextField) node).textProperty().addListener((obs, oldValue, newValue) -> validate(node));
			}
		}
	}
}
