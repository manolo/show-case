package com.example.application.views.masterdetail;

import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import jakarta.annotation.security.PermitAll;

import java.util.Optional;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Master-Detail")
@Route("master-detail/:samplePersonID?/:action?(edit)")
@PermitAll
@Menu(order = 6, icon = LineAwesomeIconUrl.COLUMNS_SOLID)
@Uses(Icon.class)
public class MasterDetailView extends Div implements BeforeEnterObserver {

    private static final String SAMPLEPERSON_ID = "samplePersonID";
    private static final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "master-detail/%s/edit";

    private final Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);

    private final TextField firstName = new TextField("First Name");
    private final TextField lastName = new TextField("Last Name");
    private final TextField email = new TextField("Email");
    private final TextField phone = new TextField("Phone");
    private final DatePicker dateOfBirth = new DatePicker("Date Of Birth");
    private final TextField occupation = new TextField("Occupation");
    private final TextField role = new TextField("Role");
    private final Checkbox important = new Checkbox("Important");

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");

    private final BeanValidationBinder<SamplePerson> binder;

    // Selection lives in a signal — everything else (header, buttons enabled, navigation) reacts to it.
    private final ValueSignal<SamplePerson> selected = new ValueSignal<>(null);

    private final SamplePersonService samplePersonService;

    public MasterDetailView(SamplePersonService samplePersonService) {
        this.samplePersonService = samplePersonService;
        addClassNames("master-detail-view", "master-detail");

        SplitLayout splitLayout = new SplitLayout();
        createGridLayout(splitLayout);
        createEditorLayout(splitLayout);
        add(splitLayout);

        // Grid configuration
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("phone").setAutoWidth(true);
        grid.addColumn("dateOfBirth").setAutoWidth(true);
        grid.addColumn("occupation").setAutoWidth(true);
        grid.addColumn("role").setAutoWidth(true);
        LitRenderer<SamplePerson> importantRenderer = LitRenderer.<SamplePerson>of(
                "<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
                .withProperty("icon", p -> p.isImportant() ? "check" : "minus")
                .withProperty("color", p -> p.isImportant()
                        ? "var(--lumo-primary-text-color)" : "var(--lumo-disabled-text-color)");
        grid.addColumn(importantRenderer).setHeader("Important").setAutoWidth(true);

        grid.setItemsPageable(samplePersonService::listItems);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);

        // Single grid listener — push selection into the signal; URL navigation is a Signal.effect
        grid.asSingleSelect().addValueChangeListener(event -> selected.set(event.getValue()));

        // Reactive navigation: whenever selection changes, sync the URL.
        Signal.effect(this, () -> {
            SamplePerson p = selected.get();
            UI ui = UI.getCurrent();
            if (ui == null) return;
            if (p != null && p.getId() != null) {
                ui.navigate(String.format(SAMPLEPERSON_EDIT_ROUTE_TEMPLATE, p.getId()));
            } else {
                ui.navigate(MasterDetailView.class);
            }
        });

        // Bind form
        binder = new BeanValidationBinder<>(SamplePerson.class);
        binder.bindInstanceFields(this);

        // Save / Delete enabled state derived from selection — no manual toggling needed
        Signal<Boolean> hasSelection = selected.map(p -> p != null && p.getId() != null);
        delete.bindEnabled(hasSelection);

        cancel.addClickListener(e -> {
            selected.set(null);
            clearForm();
            refreshGrid();
        });

        save.addClickListener(e -> {
            try {
                SamplePerson current = selected.get();
                if (current == null) current = new SamplePerson();
                binder.writeBean(current);
                samplePersonService.update(current);
                selected.set(null);
                clearForm();
                refreshGrid();
                Notification.show("Data updated");
            } catch (ObjectOptimisticLockingFailureException exception) {
                Notification n = Notification.show(
                        "Error updating the data. Somebody else has updated the record while you were making changes.");
                n.setPosition(Position.MIDDLE);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (ValidationException validationException) {
                Notification.show("Failed to update the data. Check again that all values are valid");
            }
        });

        delete.addClickListener(e -> {
            SamplePerson current = selected.get();
            if (current == null || current.getId() == null) return;
            samplePersonService.delete(current.getId());
            selected.set(null);
            clearForm();
            refreshGrid();
            Notification.show("Deleted");
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> samplePersonId = event.getRouteParameters().get(SAMPLEPERSON_ID).map(Long::parseLong);
        if (samplePersonId.isPresent()) {
            Optional<SamplePerson> samplePersonFromBackend = samplePersonService.get(samplePersonId.get());
            if (samplePersonFromBackend.isPresent()) {
                populateForm(samplePersonFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested samplePerson was not found, ID = %s", samplePersonId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                refreshGrid();
                event.forwardTo(MasterDetailView.class);
            }
        }
    }

    private void createEditorLayout(SplitLayout splitLayout) {
        Div editorLayoutDiv = new Div();
        editorLayoutDiv.setClassName("editor-layout");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        // Reactive header — shows the selected name as soon as the row is clicked.
        Span header = new Span();
        header.bindText(Signal.computed(() -> {
            SamplePerson p = selected.get();
            if (p == null) return "New record";
            String name = (p.getFirstName() == null ? "" : p.getFirstName()) + " "
                    + (p.getLastName() == null ? "" : p.getLastName());
            return name.isBlank() ? ("Record #" + p.getId()) : name.trim();
        }));
        header.addClassNames(FontSize.LARGE, FontWeight.MEDIUM, Padding.SMALL);

        Span subtitle = new Span();
        subtitle.bindText(selected.map(p -> p == null ? "No selection — fill the form to create" : "Editing existing record"));
        subtitle.addClassNames(FontSize.SMALL, TextColor.SECONDARY, Padding.Horizontal.SMALL);

        FormLayout formLayout = new FormLayout();
        formLayout.add(firstName, lastName, email, phone, dateOfBirth, occupation, role, important);

        editorDiv.add(header, subtitle, formLayout);
        createButtonLayout(editorLayoutDiv);

        splitLayout.addToSecondary(editorLayoutDiv);
    }

    private void createButtonLayout(Div editorLayoutDiv) {
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setClassName("button-layout");
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        buttonLayout.add(save, cancel, delete);
        editorLayoutDiv.add(buttonLayout);
    }

    private void createGridLayout(SplitLayout splitLayout) {
        Div wrapper = new Div();
        wrapper.setClassName("grid-wrapper");
        splitLayout.addToPrimary(wrapper);
        wrapper.add(grid);
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(SamplePerson value) {
        if (value != null && (selected.get() == null
                || !java.util.Objects.equals(value.getId(),
                        selected.get() == null ? null : selected.get().getId()))) {
            selected.set(value);
        }
        binder.readBean(value);
    }
}
