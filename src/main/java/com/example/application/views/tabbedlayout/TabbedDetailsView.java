package com.example.application.views.tabbedlayout;

import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import jakarta.annotation.security.PermitAll;

import java.util.Optional;

@PageTitle("Person Details")
@Route(value = ":id/details", layout = SampleTabbedLayout.class)
@PermitAll
public class TabbedDetailsView extends Div implements BeforeEnterObserver {

    private final SamplePersonService service;
    private final VerticalLayout details = new VerticalLayout();

    public TabbedDetailsView(SamplePersonService service) {
        this.service = service;
        addClassNames(Padding.LARGE);
        add(details);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> id = event.getRouteParameters().get("id").map(Long::parseLong);
        if (id.isPresent()) {
            Optional<SamplePerson> found = service.get(id.get());
            if (found.isPresent()) {
                populateDetails(found.get());
            } else {
                Notification.show("Person not found", 3000, Notification.Position.BOTTOM_START);
                event.forwardTo(TabbedListView.class);
            }
        } else {
            event.forwardTo(TabbedListView.class);
        }
    }

    private void populateDetails(SamplePerson person) {
        details.removeAll();
        details.add(new H3(person.getFirstName() + " " + person.getLastName()));
        addField("Email", person.getEmail());
        addField("Phone", person.getPhone());
        addField("Date of Birth", person.getDateOfBirth() != null ? person.getDateOfBirth().toString() : "");
        addField("Occupation", person.getOccupation());
        addField("Role", person.getRole());
        addField("Important", person.isImportant() ? "Yes" : "No");
    }

    private void addField(String label, String value) {
        if (value != null && !value.isBlank()) {
            Div row = new Div();
            Span labelSpan = new Span(label + ": ");
            labelSpan.getStyle().set("font-weight", "bold");
            row.add(labelSpan, new Span(value));
            details.add(row);
        }
    }
}
