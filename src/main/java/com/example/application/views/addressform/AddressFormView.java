package com.example.application.views.addressform;

import java.util.List;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

@PageTitle("Address Form")
@Route("address-form")
@PermitAll
@Menu(order = 9, icon = LineAwesomeIconUrl.MAP_MARKER_SOLID)
public class AddressFormView extends Composite<VerticalLayout> {

    private static final List<String> COUNTRIES = List.of("United States", "Canada", "Mexico", "Spain", "Germany", "France");
    private static final List<String> US_STATES = List.of("California", "Florida", "New York", "Texas", "Washington");

    private final ValueSignal<String> street = new ValueSignal<>("");
    private final ValueSignal<String> postal = new ValueSignal<>("");
    private final ValueSignal<String> city = new ValueSignal<>("");
    private final ValueSignal<String> state = new ValueSignal<>("");
    private final ValueSignal<String> country = new ValueSignal<>("");

    public AddressFormView() {
        VerticalLayout column = new VerticalLayout();
        H3 h3 = new H3("Address");
        h3.setWidth("min-content");

        TextField streetField = eagerText("Street address");
        streetField.setWidthFull();
        streetField.bindValue(street, street::set);

        FormLayout form = new FormLayout();
        form.setWidthFull();

        TextField postalField = eagerText("Postal code");
        postalField.bindValue(postal, postal::set);
        TextField cityField = eagerText("City");
        cityField.bindValue(city, city::set);

        ComboBox<String> countryBox = new ComboBox<>("Country");
        countryBox.setItems(COUNTRIES);
        countryBox.setFocusSelectedItem(true);
        countryBox.bindValue(country, v -> country.set(v == null ? "" : v));

        ComboBox<String> stateBox = new ComboBox<>("State");
        stateBox.setItems(US_STATES);
        stateBox.setFocusSelectedItem(true);
        stateBox.bindValue(state.map(s -> s.isEmpty() ? null : s), v -> state.set(v == null ? "" : v));
        // Only US has a State field — show it reactively
        stateBox.bindVisible(country.map("United States"::equals));

        form.add(countryBox, stateBox, postalField, cityField);

        Span summary = new Span();
        summary.bindText(Signal.computed(() -> {
            String s = street.get();
            String p = postal.get();
            String c = city.get();
            String st = state.get();
            String co = country.get();
            if (s.isEmpty() && c.isEmpty() && co.isEmpty()) return "(start typing to see a preview)";
            StringBuilder b = new StringBuilder();
            if (!s.isEmpty()) b.append(s).append(", ");
            if (!p.isEmpty()) b.append(p).append(' ');
            if (!c.isEmpty()) b.append(c);
            if (!st.isEmpty()) b.append(", ").append(st);
            if (!co.isEmpty()) b.append(", ").append(co);
            return b.toString();
        }));
        summary.addClassNames(TextColor.SECONDARY, Padding.SMALL);

        Signal<Boolean> formValid = Signal.computed(() ->
                !street.get().isBlank() && !postal.get().isBlank()
                && !city.get().isBlank() && !country.get().isBlank()
                && (!country.get().equals("United States") || !state.get().isBlank()));

        Button save = new Button("Save", e -> Notification.show("Saved address"));
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        save.bindEnabled(formValid);
        Button cancel = new Button("Cancel", e -> {
            street.set(""); postal.set(""); city.set(""); state.set(""); country.set("");
        });

        Paragraph hint = new Paragraph("The State field appears only when Country = United States.");
        hint.addClassNames(TextColor.SECONDARY, Margin.Top.NONE);

        HorizontalLayout layoutRow = new HorizontalLayout(save, cancel);
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        getContent().setJustifyContentMode(JustifyContentMode.START);
        getContent().setAlignItems(Alignment.CENTER);
        column.setMaxWidth("800px");
        column.setWidth("100%");
        column.add(h3, streetField, form, summary, hint, layoutRow);
        getContent().add(column);
    }

    private TextField eagerText(String label) {
        TextField f = new TextField(label);
        f.setValueChangeMode(ValueChangeMode.EAGER);
        return f;
    }
}
