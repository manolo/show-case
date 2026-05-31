package com.example.application.views.wizard;

import com.example.application.data.checkout.CreditCard;
import com.example.application.data.checkout.PersonalDetails;
import com.example.application.data.checkout.ShippingAddress;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.FormItem;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

import java.util.function.Function;

@PageTitle("Checkout Success")
@Route(value = "4", layout = CheckoutWizard.class)
@PermitAll
public class CheckoutStep4View extends Div {

    private final FormLayout formLayout = new FormLayout();

    public CheckoutStep4View(CheckoutFormSignal form) {
        addClassNames(Padding.Horizontal.LARGE, Padding.Vertical.MEDIUM);
        H3 heading = new H3("Checkout");
        formLayout.setResponsiveSteps(new ResponsiveStep("0", 1));

        addReactiveItem("Name", form.personalDetails(), PersonalDetails::getName);
        addReactiveItem("Email", form.personalDetails(), PersonalDetails::getEmail);
        addReactiveItem("Phone", form.personalDetails(), PersonalDetails::getPhone);
        addReactiveItem("Address", form.shippingAddress(), ShippingAddress::getAddress);
        addReactiveItem("Postal Code", form.shippingAddress(), ShippingAddress::getPostalCode);
        addReactiveItem("City", form.shippingAddress(), ShippingAddress::getCity);
        addReactiveItem("State", form.shippingAddress(), ShippingAddress::getState);
        addReactiveItem("Country", form.shippingAddress(), ShippingAddress::getCountry);
        addReactiveItem("Card Holder", form.creditCard(), CreditCard::getCardHolder);
        addReactiveItem("Card Number", form.creditCard(), CreditCard::getCardNumber);

        add(new Section(heading, formLayout));
    }

    private <T> void addReactiveItem(String label, ValueSignal<T> signal, Function<T, String> getter) {
        Span value = new Span();
        value.bindText(Signal.computed(() -> {
            T bean = signal.get();
            String raw = bean == null ? null : getter.apply(bean);
            return raw == null || raw.isBlank() ? "—" : raw;
        }));
        FormItem item = new FormItem(value);
        formLayout.addFormItem(item, label);
    }
}
