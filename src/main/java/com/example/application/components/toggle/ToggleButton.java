package com.example.application.components.toggle;

import com.vaadin.flow.component.checkbox.Checkbox;

/**
 * @deprecated: This is here only for reference, the recommended way to use a
 *              toggle button is to use directly Vaadin Checkbox, set a class,
 *              and add a custom css for it
 */
@Deprecated
public class ToggleButton extends Checkbox {
	
	public ToggleButton() {
		addClassName("toggle");
	}
	
	public ToggleButton(String label) {
		super(label);
		addClassName("toggle");
	}

}
