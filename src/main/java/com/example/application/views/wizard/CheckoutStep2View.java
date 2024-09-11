package com.example.application.views.wizard;

import com.example.application.components.stepper.Step.HasBinder;
import com.example.application.data.checkout.ShippingAddress;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

@PageTitle("Step 2 - Wizard")
@Route(value = "2", layout = CheckoutWizard.class)
public class CheckoutStep2View extends Div implements HasBinder {
	
	ComboBox<String> country = new ComboBox<>("Country");
	TextArea address = new TextArea("Street address");
	TextField postalCode = new TextField("Postal Code");
	TextField city = new TextField("City");
	ComboBox<String> state = new ComboBox<>("State");
	Checkbox samebillingaddress = new Checkbox("Billing address is the same as shipping address");
	Checkbox remember = new Checkbox("Remember address for next time");
	BeanValidationBinder<ShippingAddress> binder = new BeanValidationBinder<>(ShippingAddress.class);

	public CheckoutStep2View() {
		addClassNames(Padding.Horizontal.LARGE, Padding.Vertical.MEDIUM);
		add( createForm());

		binder.bindInstanceFields(this);
		binder.setBean(new ShippingAddress());
	}

	private Section createForm() {
		H3 header = new H3("Shipping address");

		country.setRequiredIndicatorVisible(true);
		country.setItems(ShippingAddress.countries);

		address.setMaxLength(200);
		address.setRequiredIndicatorVisible(true);

		postalCode.setRequiredIndicatorVisible(true);
		postalCode.setPattern("[\\d \\p{L}]*");
		postalCode.addClassNames(Margin.Bottom.SMALL);

		city.setRequiredIndicatorVisible(true);

		state.setRequiredIndicatorVisible(true);
		state.setItems(ShippingAddress.states);
		// Allowing custom values in combo-box is a bit tricky
		state.setAllowCustomValue(true);
		state.addCustomValueSetListener(e -> {
			String customValue = e.getDetail();
			ShippingAddress.states.add(customValue);
			state.setItems(ShippingAddress.states);
			state.setValue(customValue);
		});

		samebillingaddress.addClassNames(Margin.Top.SMALL);
		
        FormLayout form = new FormLayout(country, state, address, postalCode, city, samebillingaddress, remember);
        form.setColspan(address, 2);
		return new Section(header,form);
	}

	@Override
	public Binder<?> getBinder() {
		return binder;
	}

}
