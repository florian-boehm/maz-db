package de.spiritaner.maz.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.controlsfx.control.PopOver;

import java.time.LocalDate;

/**
 * Created by Florian on 8/11/2016.
 */
public class DateValidator {

	private PopOver popOver;
//	private Label msgLabel;
	private VBox vbox;
	private DatePicker datePicker;

	private Boolean notEmpty;
	private Boolean future;

	private String fieldName;

	private DateValidator() {
		vbox = new VBox();
		vbox.setAlignment(Pos.CENTER_LEFT);
		vbox.setPadding(new Insets(2));
//		msgLabel = new Label("");
//		msgLabel.setStyle("-fx-text-fill: #B80024; -fx-font-weight: bold");
//		msgLabel.setPadding(new Insets(4));

		popOver = new PopOver();
		popOver.setAutoHide(false);
		popOver.setDetachable(false);
		popOver.setContentNode(vbox);
	}

	public static DateValidator create(DatePicker node) {
		DateValidator result = new DateValidator();
		result.datePicker = node;
		return result;
	}

	public void addMsg(String message) {
		Label label = new Label(message);
		label.setPadding(new Insets(2));
		label.setStyle("-fx-text-fill: #B80024; -fx-font-weight: bold");
		vbox.getChildren().add(label);
	}

	public DateValidator valueChanged() {
		datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
			validate(oldValue, newValue);
		});

		return this;
	}

	public DateValidator fieldName(String fieldName) {
		this.fieldName = fieldName;
		return this;
	}

	public DateValidator notEmpty(Boolean notEmpty) {
		this.notEmpty = notEmpty;
		return this;
	}

	public DateValidator future() {
		this.future = Boolean.TRUE;
		return this;
	}

	public DateValidator past() {
		this.future = Boolean.FALSE;
		return this;
	}

	public boolean validate(LocalDate oldValue, LocalDate newValue) {
		boolean result = true;

		vbox.getChildren().clear();

		// Check if the value is null
		if(notEmpty != null && newValue == null) {
			addMsg(fieldName + " darf nicht leer sein!");
			result = false;
		} else {
			// Check if the date is a future date
			if (future != null && future && newValue.compareTo(LocalDate.now()) < 0) {
				addMsg(fieldName + " muss in der Zukunft liegen!");
				result = false;
			} else if(future != null && !future && newValue.compareTo(LocalDate.now()) > 0) {
				addMsg(fieldName + " muss in der Vergangenheit liegen!");
				result = false;
			}
		}

		// Hide or show the messages
		if(result == true)
			popOver.hide();
		else
			if(!popOver.isShowing())
				popOver.show(datePicker);

		return result;
	}

    public boolean validate() {
	    return validate(null, datePicker.getValue());
    }
}
