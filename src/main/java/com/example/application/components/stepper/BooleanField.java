package com.example.application.components.stepper;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.customfield.CustomField;

public class BooleanField extends CustomField<Boolean> {
	private final Checkbox checkbox;

	public BooleanField(String label) {
		checkbox = new Checkbox(label);
		add(checkbox);
	}

	@Override
	protected Boolean generateModelValue() {
		return checkbox.getValue();
	}

	@Override
	protected void setPresentationValue(Boolean newPresentationValue) {
		checkbox.setValue(newPresentationValue);
	}

}
