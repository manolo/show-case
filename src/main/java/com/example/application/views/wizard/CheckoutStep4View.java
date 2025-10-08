package com.example.application.views.wizard;

import com.example.application.components.stepper.Wizard;
import com.example.application.data.checkout.CreditCard;
import com.example.application.data.checkout.PersonalDetails;
import com.example.application.data.checkout.ShippingAddress;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

@PageTitle("Checkout Success")
@Route(value = "4", layout = CheckoutWizard.class)
public class CheckoutStep4View extends Div {

	FormLayout formLayout = new FormLayout();

	public CheckoutStep4View() {
		// Restore entities filled an validated in each previous steps
		PersonalDetails personalDetails = Wizard.restoreSessionObject(CheckoutStep1View.class, null);
		ShippingAddress shippingAddress = Wizard.restoreSessionObject(CheckoutStep2View.class, null);
		CreditCard creditCard = Wizard.restoreSessionObject(CheckoutStep3View.class, null);

		addClassNames(Padding.Horizontal.LARGE, Padding.Vertical.MEDIUM);
		H3 heading = new H3("Checkout");
		
		formLayout.setResponsiveSteps(new ResponsiveStep("0", 1));
		addItem("Name", personalDetails.getName());
		addItem("Email", personalDetails.getEmail());
		addItem("Phone", personalDetails.getPhone());
		addItem("Address", shippingAddress.getAddress());
		addItem("Postal Code", shippingAddress.getPostalCode());
		addItem("City", shippingAddress.getCity());
		addItem("State", shippingAddress.getState());
		addItem("Country", shippingAddress.getCountry());
		addItem("Card Holder", creditCard.getCardHolder());
		addItem("Card Number", creditCard.getCardNumber());

		add(new Section(heading, formLayout));

		add(heading, formLayout);
	}

	private void addItem(String label, Object value) {
		FormItem i = new FormItem(new Div("" + value));
		formLayout.addFormItem(i, label);
	}

}
