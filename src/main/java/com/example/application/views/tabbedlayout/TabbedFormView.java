package com.example.application.views.tabbedlayout;

import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.security.PermitAll;

import java.util.Optional;

@PageTitle("Edit Person")
@Route(value = ":id/edit", layout = SampleTabbedLayout.class)
@PermitAll
public class TabbedFormView extends Div implements BeforeEnterObserver {

    private final SamplePersonService service;
    private final BeanValidationBinder<SamplePerson> binder = new BeanValidationBinder<>(SamplePerson.class);
    private SamplePerson person;

    private final TextField firstName = new TextField("First Name");
    private final TextField lastName = new TextField("Last Name");
    private final TextField email = new TextField("Email");
    private final TextField phone = new TextField("Phone");
    private final DatePicker dateOfBirth = new DatePicker("Date of Birth");
    private final TextField occupation = new TextField("Occupation");
    private final TextField role = new TextField("Role");
    private final Checkbox important = new Checkbox("Important");

    public TabbedFormView(SamplePersonService service) {
        this.service = service;
        addClassNames(Padding.LARGE);

        FormLayout form = new FormLayout();
        form.add(firstName, lastName, email, phone, dateOfBirth, occupation, role, important);

        binder.bindInstanceFields(this);

        Button save = new Button("Save");
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.addClickListener(e -> {
            try {
                if (person == null) {
                    person = new SamplePerson();
                }
                binder.writeBean(person);
                service.update(person);
                Notification.show("Data updated");
                UI.getCurrent().navigate("tabbed");
            } catch (ValidationException ex) {
                Notification.show("Check that all values are valid", 3000, Notification.Position.BOTTOM_START)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        Button cancel = new Button("Cancel");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancel.addClickListener(e -> UI.getCurrent().navigate("tabbed"));

        HorizontalLayout buttons = new HorizontalLayout(save, cancel);
        add(form, buttons);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> id = event.getRouteParameters().get("id").map(Long::parseLong);
        if (id.isPresent()) {
            Optional<SamplePerson> found = service.get(id.get());
            if (found.isPresent()) {
                person = found.get();
                binder.readBean(person);
            } else {
                Notification.show("Person not found", 3000, Notification.Position.BOTTOM_START);
                event.forwardTo(TabbedListView.class);
            }
        } else {
            event.forwardTo(TabbedListView.class);
        }
    }
}
