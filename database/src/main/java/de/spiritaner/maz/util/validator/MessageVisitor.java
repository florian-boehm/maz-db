package de.spiritaner.maz.util.validator;

import javafx.scene.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessageVisitor implements ValidationVisitor {

	public HashMap<Node, ArrayList<String>> messages;
	public Node currentNode = null;

	@Override
	public void visit(Validation validation) {
		if(!validation.isValid())
			messages.get(currentNode).add(validation.msg.get());
	}

	@Override
	public void nextNode(Node node) {
		messages.put(node, new ArrayList<>());
		currentNode = node;
	}

	@Override
	public void reset() {
		messages = new HashMap<>();
	}

	@Override
	public void finished() {

	}

	@Override
	public String toString() {
		String result = "";

		for(Map.Entry<Node, ArrayList<String>> entry : messages.entrySet()) {
			for(String msg : entry.getValue()) {
				result += entry.getKey() + ": " + msg + "\n";
			}
		}

		return result;
	}
}
