package com.example.application.views.wizard;

import com.example.application.components.stepper.Step.HasBinder;
import com.example.application.data.checkout.CreditCard;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

@PageTitle("Step 3 - Wizard")
@Route(value = "3", layout = CheckoutWizard.class)
@PermitAll
public class CheckoutStep3View extends Div implements HasBinder {
    BeanValidationBinder<CreditCard> binder = new BeanValidationBinder<>(CreditCard.class);
    TextField cardHolder = new TextField("Cardholder name");
    TextField cardNumber = new TextField("Card Number");
    TextField securityCode = new TextField("Security Code");
    Select<String> expirationMonth = new Select<>();
    Select<String> expirationYear = new Select<>();

    public CheckoutStep3View() {
        addClassNames(Padding.Horizontal.LARGE, Padding.Vertical.MEDIUM);
        add(createForm());

        binder.bindInstanceFields(this);
        binder.setBean(new CreditCard());
    }

    private Section createForm() {
        H3 header = new H3("Credit Card");

        cardHolder.setRequiredIndicatorVisible(true);
        cardHolder.setPattern("[\\p{L} \\-]+");

        cardNumber.setRequiredIndicatorVisible(true);
        cardNumber.setPattern("[\\d ]{12,23}");

        FormLayout nestedForm = new FormLayout();
        nestedForm.setResponsiveSteps(new ResponsiveStep("0", 3));
        securityCode.setRequiredIndicatorVisible(true);
        securityCode.setPattern("[0-9]{3,4}");
        securityCode.addClassNames(Margin.Bottom.SMALL);
        securityCode.setHelperText("3 or 4-digit code on your card");

        expirationMonth.setLabel("Expiration month");
        expirationMonth.setRequiredIndicatorVisible(true);
        expirationMonth.setItems("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");

        expirationYear.setLabel("Expiration year");
        expirationYear.setRequiredIndicatorVisible(true);
        expirationYear.setItems("22", "23", "24", "25", "26");
        nestedForm.add(securityCode, expirationMonth, expirationYear);

        Button save = new Button("Save Order", __ -> {
            UI.getCurrent().navigate(CheckoutStep4View.class);
        });
        save.setThemeName(ButtonVariant.LUMO_PRIMARY.getVariantName());

        FormLayout form = new FormLayout(cardHolder, cardNumber, nestedForm);
        form.setColspan(cardHolder, 2);
        form.setColspan(nestedForm, 2);
        return new Section(header, form, save);
    }

    @Override
    public Binder<?> getBinder() {
        return binder;
    }
}