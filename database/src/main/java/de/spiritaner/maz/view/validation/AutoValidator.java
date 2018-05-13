package de.spiritaner.maz.view.validation;

import de.spiritaner.maz.view.binding.Bindable;
import javafx.scene.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class AutoValidator {

	private HashMap<Node, ArrayList<Validator>> nodeValidators;

	public AutoValidator() {
		this.nodeValidators = new HashMap<>();
	}

	public void register(final Node node, final Validator val, final boolean onChange) {
		if(!nodeValidators.containsKey(node)) {
			nodeValidators.put(node, new ArrayList<>());
		}

		nodeValidators.get(node).add(val);
	}

	public boolean validate(Node node) {
		ArrayList<Validator> validators = nodeValidators.get(node);

		validators.forEach(v -> v.validate());
		return true;
	}

	public boolean validateAll() {
		nodeValidators.forEach((node, validators) -> {

		});

		return false;
	}
}
