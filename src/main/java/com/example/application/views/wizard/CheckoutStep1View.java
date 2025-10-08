package com.example.application.views.wizard;

import com.example.application.components.stepper.Step.HasBinder;
import com.example.application.data.checkout.PersonalDetails;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

@PageTitle("Step 1 - Wizard")
@Route(value = "1", layout = CheckoutWizard.class)
@PermitAll
@Menu(title = "Wizard")
public class CheckoutStep1View extends Div implements HasBinder {

    TextField name = new TextField("Name");
    EmailField email = new EmailField("Email address");
    TextField phone = new TextField("Phone number");
    Checkbox remember = new Checkbox("Remember personal details for next time");
    BeanValidationBinder<PersonalDetails> binder = new BeanValidationBinder<>(PersonalDetails.class);

    public CheckoutStep1View() {
        addClassNames(Padding.Horizontal.LARGE, Padding.Vertical.MEDIUM);
        add(createForm());

        binder.bindInstanceFields(this);
        binder.setBean(new PersonalDetails());
    }

    private Component createForm() {
        H3 header = new H3("Personal details");

        name.setRequiredIndicatorVisible(true);
        name.setPattern("[\\p{L} \\-]+");

        email.setRequiredIndicatorVisible(true);

        phone.setRequiredIndicatorVisible(true);
        phone.setPattern("[\\d \\-\\+]+");

        FormLayout form = new FormLayout(name, email, phone, remember);
        form.setColspan(name, 2);

        return new Section(header, form);
    }

    @Override
    public Binder<?> getBinder() {
        return binder;
    }
}