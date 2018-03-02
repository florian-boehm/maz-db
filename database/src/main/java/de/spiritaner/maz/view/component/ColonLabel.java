package de.spiritaner.maz.view.component;

import javafx.scene.control.Label;

public class ColonLabel extends Label {

	public ColonLabel() {
		super.textProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null && !newValue.endsWith(":")) {
				textProperty().set(newValue+":");
			}
		});
	}
}
