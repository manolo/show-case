package com.example.application.views.gridedit;

import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import jakarta.annotation.security.PermitAll;
import org.springframework.data.domain.PageRequest;

@PageTitle("Editable Grid Dbl-Click")
@Route(value = "grid-editor", layout = MainLayout.class)
@PermitAll
@PreserveOnRefresh
@Menu
public class GridEditorView extends VerticalLayout {

    private final SamplePersonService service;
    private final Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);
    private final Editor<SamplePerson> editor = grid.getEditor();
    private final BeanValidationBinder<SamplePerson> binder = new BeanValidationBinder<>(SamplePerson.class);

    private final TextField firstName = new TextField();
    private final TextField lastName = new TextField();
    private final EmailField email = new EmailField();
    private final TextField phone = new TextField();
    private final TextField occupation = new TextField();
    private final TextField role = new TextField();
    private final Checkbox important = new Checkbox();

    private final Button saveButton = new Button("Save");
    private final Button cancelButton = new Button("Cancel");

    public GridEditorView(SamplePersonService service) {
        this.service = service;
        addClassName("grid-edit-view");
        setSizeFull();
        setSpacing(false);
        setPadding(false);

        configureGrid();
        configureEditor();
        configureFooter();
    }

    private void configureGrid() {
        grid.addColumn("firstName").setHeader("First Name").setAutoWidth(true).setSortable(true);
        grid.addColumn("lastName").setHeader("Last Name").setAutoWidth(true).setSortable(true);
        grid.addColumn("email").setHeader("Email").setAutoWidth(true).setSortable(true);
        grid.addColumn("phone").setHeader("Phone").setAutoWidth(true).setSortable(true);
        grid.addColumn("occupation").setHeader("Occupation").setAutoWidth(true).setSortable(true);
        grid.addColumn("role").setHeader("Role").setAutoWidth(true).setSortable(true);
        grid.addColumn(new TextRenderer<>(p -> p.isImportant() ? "Yes" : "No"))
                .setKey("important").setHeader("Important").setSortable(true);

        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setColumnReorderingAllowed(true);
        grid.setSelectionMode(Grid.SelectionMode.NONE);
        grid.setHeightFull();

        grid.setItems(query -> service.list(
                PageRequest.of(query.getPage(), query.getPageSize(),
                        VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        grid.addItemDoubleClickListener(e -> {
            if (editor.isOpen() && binder.hasChanges()) {
                return;
            }
            if (editor.isOpen()) {
                editor.cancel();
            }
            editor.editItem(e.getItem());
            updateButtons();
        });

        add(grid);
        expand(grid);
    }

    private void configureEditor() {
        binder.bindInstanceFields(this);
        editor.setBinder(binder);
        editor.setBuffered(true);

        grid.getColumnByKey("firstName").setEditorComponent(firstName);
        grid.getColumnByKey("lastName").setEditorComponent(lastName);
        grid.getColumnByKey("email").setEditorComponent(email);
        grid.getColumnByKey("phone").setEditorComponent(phone);
        grid.getColumnByKey("occupation").setEditorComponent(occupation);
        grid.getColumnByKey("role").setEditorComponent(role);
        grid.getColumnByKey("important").setEditorComponent(important);

        editor.addSaveListener(e -> {
            service.update(e.getItem());
            Notification.show("Saved: " + e.getItem().getFirstName() + " " + e.getItem().getLastName(), 3000,
                    Notification.Position.BOTTOM_START);
        });

        binder.addStatusChangeListener(e -> updateButtons());
    }

    private void configureFooter() {
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.setEnabled(false);
        cancelButton.setEnabled(false);

        saveButton.addClickListener(e -> {
            editor.save();
            updateButtons();
            grid.getDataProvider().refreshAll();
        });

        cancelButton.addClickListener(e -> {
            if (binder.hasChanges()) {
                ConfirmDialog dialog = new ConfirmDialog(
                        "Discard changes?",
                        "Unsaved changes will be lost.",
                        "Discard", evt -> {
                    editor.cancel();
                    binder.refreshFields();
                    updateButtons();
                    Notification.show("Edit cancelled", 2000, Notification.Position.BOTTOM_START);
                });
                dialog.setCancelable(true);
                dialog.setCancelText("Keep editing");
                dialog.setConfirmButtonTheme("error primary");
                dialog.open();
            } else {
                editor.cancel();
                binder.refreshFields();
                updateButtons();
            }
        });

        HorizontalLayout footer = new HorizontalLayout(saveButton, cancelButton);
        footer.setSpacing(true);
        footer.getStyle().set("padding", "var(--lumo-space-s) var(--lumo-space-m)");
        footer.setWidthFull();
        add(footer);
    }

    private void updateButtons() {
        boolean open = editor.isOpen();
        boolean dirty = open && binder.hasChanges();
        saveButton.setEnabled(dirty);
        cancelButton.setEnabled(open);
    }
}
