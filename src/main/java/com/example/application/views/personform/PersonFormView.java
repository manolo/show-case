package com.example.application.views.personform;

import java.time.LocalDate;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Person Form")
@Route("person-form")
@PermitAll
@Menu(order = 8, icon = LineAwesomeIconUrl.USER)
public class PersonFormView extends Composite<VerticalLayout> {

    private final ValueSignal<String> firstName = new ValueSignal<>("");
    private final ValueSignal<String> lastName = new ValueSignal<>("");
    private final ValueSignal<LocalDate> birthday = new ValueSignal<>(null);
    private final ValueSignal<String> phone = new ValueSignal<>("");
    private final ValueSignal<String> email = new ValueSignal<>("");
    private final ValueSignal<String> occupation = new ValueSignal<>("");

    public PersonFormView() {
        VerticalLayout layoutColumn2 = new VerticalLayout();
        H3 h3 = new H3("Personal Information");
        h3.setWidth("100%");

        FormLayout formLayout2Col = new FormLayout();
        formLayout2Col.setWidth("100%");

        TextField firstNameField = eagerText("First Name");
        firstNameField.bindValue(firstName, firstName::set);
        TextField lastNameField = eagerText("Last Name");
        lastNameField.bindValue(lastName, lastName::set);
        DatePicker birthdayField = new DatePicker("Birthday");
        birthdayField.bindValue(birthday, birthday::set);
        TextField phoneField = eagerText("Phone Number");
        phoneField.bindValue(phone, phone::set);
        EmailField emailField = new EmailField("Email");
        emailField.setValueChangeMode(ValueChangeMode.EAGER);
        emailField.bindValue(email, email::set);
        TextField occupationField = eagerText("Occupation");
        occupationField.bindValue(occupation, occupation::set);

        formLayout2Col.add(firstNameField, lastNameField, birthdayField, phoneField, emailField, occupationField);

        // Reactive live preview: a Span re-renders the full name as the user types
        Span fullName = new Span();
        fullName.bindText(Signal.computed(() -> {
            String fn = firstName.get().trim();
            String ln = lastName.get().trim();
            String composed = (fn + " " + ln).trim();
            return composed.isEmpty() ? "(start typing to see a preview)" : composed;
        }));
        fullName.addClassNames(TextColor.SECONDARY, Padding.SMALL);

        Signal<Boolean> formValid = Signal.computed(() ->
                !firstName.get().isBlank() && !lastName.get().isBlank()
                && email.get().contains("@"));

        Button save = new Button("Save", e -> Notification.show("Saved " + fullName.getText()));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.setWidth("min-content");
        save.bindEnabled(formValid);

        Button cancel = new Button("Cancel", e -> {
            firstName.set("");
            lastName.set("");
            birthday.set(null);
            phone.set("");
            email.set("");
            occupation.set("");
        });
        cancel.setWidth("min-content");

        HorizontalLayout layoutRow = new HorizontalLayout(save, cancel);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");

        Paragraph hint = new Paragraph("Save enables only when both names are set and the email contains '@'.");
        hint.addClassNames(TextColor.SECONDARY, Margin.Top.NONE);

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        layoutColumn2.setWidth("100%");
        layoutColumn2.setMaxWidth("800px");
        layoutColumn2.setHeight("min-content");
        getContent().add(layoutColumn2);
        layoutColumn2.add(h3, formLayout2Col, fullName, hint, layoutRow);
    }

    private TextField eagerText(String label) {
        TextField f = new TextField(label);
        f.setValueChangeMode(ValueChangeMode.EAGER);
        return f;
    }
}
