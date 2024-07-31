package com.example.application.components.filter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.example.application.components.datepicker.LocalDatePicker;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class SamplePersonFilter extends Div {

    private final TextField name = new TextField("Name");
    private final TextField phone = new TextField("Phone");
    private final DatePicker startDate = new LocalDatePicker("Date of Birth");
    private final DatePicker endDate = new LocalDatePicker();
    private final MultiSelectComboBox<String> occupations = new MultiSelectComboBox<>("Occupation");
    private final CheckboxGroup<String> roles = new CheckboxGroup<>("Role");

    public SamplePersonFilter(Runnable onSearch, List<String> occupationItems, List<String> roleItems) {
        setWidthFull();
        addClassName("filter-layout");
        addClassNames(LumoUtility.Padding.Horizontal.LARGE, LumoUtility.Padding.Vertical.MEDIUM,
                LumoUtility.BoxSizing.BORDER);
        name.setPlaceholder("First or last name");


        occupations.setItems(occupationItems);
        roles.setItems(roleItems);
        roles.addClassName("double-width");

        // Action buttons
        Button resetBtn = new Button("Reset");
        resetBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        resetBtn.addClickListener(e -> {
            name.clear();
            phone.clear();
            startDate.clear();
            endDate.clear();
            occupations.clear();
            roles.clear();
            onSearch.run();
        });
        Button searchBtn = new Button("Search");
        searchBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchBtn.addClickListener(e -> onSearch.run());

        Div actions = new Div(resetBtn, searchBtn);
        actions.addClassName(LumoUtility.Gap.SMALL);
        actions.addClassName("actions");

        add(name, phone, createDateRangeFilter(), occupations, roles, actions);
    }

    private Component createDateRangeFilter() {
        startDate.setPlaceholder("From");

        endDate.setPlaceholder("To");

        // For screen readers
        startDate.setAriaLabel("From date");
        endDate.setAriaLabel("To date");

        FlexLayout dateRangeComponent = new FlexLayout(startDate, new Text(" – "), endDate);
        dateRangeComponent.setAlignItems(FlexComponent.Alignment.BASELINE);
        dateRangeComponent.addClassName(LumoUtility.Gap.XSMALL);

        return dateRangeComponent;
    }

    public HashMap<String, List<String>> getFilterParameters() {
        HashMap<String, List<String>> map = new HashMap<>();
        if (!name.isEmpty()) {
            map.put("name", Arrays.asList(name.getValue().toLowerCase()));
        }
        if (!phone.isEmpty()) {
            String ignore = "- ()";
            String lowerCaseFilter = removeIgnotedCharacters(ignore, phone.getValue().toLowerCase());
            map.put("phone", Arrays.asList(lowerCaseFilter));
        }
        if (startDate.getValue() != null) {
            map.put("startDate", Arrays.asList(startDate.getValue().toString()));
        }
        if (endDate.getValue() != null) {
            map.put("endDate", Arrays.asList(endDate.getValue().toString()));
        }
        if (!occupations.isEmpty()) {
            map.put("occupation", List.copyOf(occupations.getValue()));
        }
        if (!roles.isEmpty()) {
            map.put("role", List.copyOf(roles.getValue()));
        }
        return map;
    }

    private String removeIgnotedCharacters(String characters, String in) {
        String result = in;
        for (int i = 0; i < characters.length(); i++) {
            result = result.replace("" + characters.charAt(i), "");
        }
        return result;
    }
}