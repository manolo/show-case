package com.example.application.views.grideditfilterpaginated;

import java.util.List;

import org.vaadin.klaudeta.PaginatedGrid;

import com.example.application.components.datepicker.LocalDatePicker;
import com.example.application.components.filter.HasFilterParameters;
import com.example.application.components.filter.SamplePersonFilter;
import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonServiceRest;
import com.example.application.views.MainLayout;
import com.example.application.views.gridwithfiltersrest.SamplePersonRestDataProvider;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;

@PageTitle("Editable Grid Paginated")
@Route(value = "grid-edit-paginated", layout = MainLayout.class)
@Menu
@PreserveOnRefresh
public class GridEditPaginatedView extends VerticalLayout {

    PaginatedGrid<SamplePerson, HasFilterParameters> grid = new PaginatedGrid<>(SamplePerson.class);
    private final Editor<SamplePerson> editor = grid.getEditor();
    private final TextField firstName = new TextField();
    private final TextField lastName = new TextField();
    private final EmailField email = new EmailField();
    private final TextField phone = new TextField();
    private final DatePicker dateOfBirth = new LocalDatePicker();
    private final TextField occupation = new TextField();
    private final TextField role = new TextField();
    private final Checkbox important = new Checkbox();
    private final BeanValidationBinder<SamplePerson> binder = new BeanValidationBinder<>(SamplePerson.class);
    private final Button cancel = new Button(VaadinIcon.CLOSE.create(), e -> cancel());
    private final Button save = new Button(VaadinIcon.CHECK.create(), e -> save());
    private final HorizontalLayout buttons = new HorizontalLayout(save, cancel);
    private final SamplePersonFilter samplePersonfilterComponent;
    private ConfirmDialog dialog;

    public GridEditPaginatedView(SamplePersonServiceRest samplePersonService) {
        // Some styles
        addClassNames("grid-edit-view");
        addClassNames("gridwith-filters-view");
        setSizeFull();
        save.getElement().getThemeList().add("badge success");
        cancel.getElement().getThemeList().add("badge error");
        buttons.addClassName("grid-edit-buttons");

        grid.setPageSize(10);
        grid.setPaginatorSize(1);

        // To customize columns with PaginatedGrid we need to clean
        grid.removeAllColumns();
        // edit column
        grid.addComponentColumn(person -> {
            Button edit = new Button(VaadinIcon.PENCIL.create(), e -> edit(person));
            edit.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            return edit;
        }).setWidth("60px").setFrozen(true).setEditorComponent(buttons);
        // entity columns
        grid.addColumns("firstName", "lastName", "email", "phone", "dateOfBirth", "occupation", "role", "important");

        // change renderer to match DatePicker format
        grid.getColumnByKey("dateOfBirth").setRenderer(
                new LocalDateRenderer<>(SamplePerson::getDateOfBirth, dateOfBirth.getI18n().getDateFormats().get(0)));
        // change renderer for the boolean column
        grid.getColumnByKey("important").setRenderer(new TextRenderer<>(p -> p.isImportant() ? "✔" : "᠆"));

        grid.setSelectionMode(Grid.SelectionMode.NONE);

        // Close editor on escape
        Shortcuts.addShortcutListener(this, cancel::click, Key.ESCAPE).listenOn(this);

        // Configure binder and editor
        binder.bindInstanceFields(this);
        editor.setBinder(binder);
        editor.setBuffered(true);
        editor.addSaveListener(e -> samplePersonService.update(e.getItem()));

        // Set each edit-component to each column
        grid.getColumnByKey("firstName").setEditorComponent(firstName);
        grid.getColumnByKey("lastName").setEditorComponent(lastName);
        grid.getColumnByKey("email").setEditorComponent(email);
        grid.getColumnByKey("phone").setEditorComponent(phone);
        grid.getColumnByKey("dateOfBirth").setEditorComponent(dateOfBirth);
        grid.getColumnByKey("occupation").setEditorComponent(occupation);
        grid.getColumnByKey("role").setEditorComponent(role);
        grid.getColumnByKey("important").setEditorComponent(important);

        // Configure dialog to discard unsaved changes
        dialog = new ConfirmDialog("Discard changes", "There are unsaved changes?", "Discard", e -> close(), "Cancel",
                e -> {});
        List<String> occupations = samplePersonService.findDistinctOccupationValues();
        List<String> roles = samplePersonService.findDistinctRoleValues();

        samplePersonfilterComponent = new SamplePersonFilter(grid::refreshPaginator, occupations, roles);
        SamplePersonRestDataProvider samplePersonDataProvider = new SamplePersonRestDataProvider(samplePersonService);
        grid.setDataProvider(samplePersonDataProvider.withFilter(samplePersonfilterComponent));

        add(samplePersonfilterComponent, grid);
    }

    private void edit(SamplePerson person) {
        if (!binder.hasChanges()) {
            editor.cancel();
          editor.editItem(person);
        }
    }

    private void save() {
        editor.save();
        close();
        grid.getDataProvider().refreshAll();
    }

    private void cancel() {
        if (binder.hasChanges()) {
            dialog.open();
        } else {
            close();
        }
    }

    private void close() {
        editor.cancel();
        // workaround because cancel should clear binder
        binder.refreshFields();
        editor.closeEditor();
    }
}
