package de.spiritaner.maz.util.validator;

import javafx.scene.Node;

public interface ValidationVisitor {

	void visit(Validation validation);

	void nextNode(Node node);

	void reset();

	void finished();
}
