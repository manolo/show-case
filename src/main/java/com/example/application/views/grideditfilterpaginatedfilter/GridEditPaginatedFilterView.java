package com.example.application.views.grideditfilterpaginatedfilter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.vaadin.klaudeta.PaginatedGrid;

import com.example.application.components.datepicker.LocalDatePicker;
import com.example.application.components.filter.HasFilterParameters;
import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonServiceRest;
import com.example.application.views.MainLayout;
import com.example.application.views.gridwithfiltersrest.SamplePersonRestDataProvider;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.shared.InputField;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.HasValueChangeMode;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;

@PageTitle("Editable Grid Filter Paginated")
@Route(value = "grid-edit-filter-paginated", layout = MainLayout.class)
@Menu
@PreserveOnRefresh
public class GridEditPaginatedFilterView extends VerticalLayout implements HasFilterParameters {

    private final PaginatedGrid<SamplePerson, HasFilterParameters> grid = new PaginatedGrid<>(SamplePerson.class);
    private final Editor<SamplePerson> editor = grid.getEditor();
    private final BeanValidationBinder<SamplePerson> binder = new BeanValidationBinder<>(SamplePerson.class);

    // Fields used for the row editor
    private final TextField firstName = new TextField();
    private final TextField lastName = new TextField();
    private final EmailField email = new EmailField();
    private final TextField phone = new TextField();
    private final DatePicker dateOfBirth = new LocalDatePicker();
    private final TextField occupation = new TextField();
    private final TextField role = new TextField();
    private final Checkbox important = new Checkbox();
    // Buttons in the row editor
    private final Button cancel = new Button(VaadinIcon.CLOSE.create(), e -> cancel());
    private final Button save = new Button(VaadinIcon.CHECK.create(), e -> save());
    private final HorizontalLayout buttons = new HorizontalLayout(save, cancel);

    // Fields used in the header for Filtering data
    private final TextField nameFilter = new TextField();
    private final TextField phoneFilter = new TextField();
    private final TextField emailFilter = new TextField();
    private final MultiSelectComboBox<String> occupationsFilter = new MultiSelectComboBox<>();
    private final MultiSelectComboBox<String> rolesFilter = new MultiSelectComboBox<>();
    private final Checkbox importantFilter = new Checkbox();

    // Confirmation dialog
    private ConfirmDialog dialog;

    public GridEditPaginatedFilterView(SamplePersonServiceRest samplePersonService) {
        // Some styles
        addClassNames("grid-edit-view");
        addClassNames("gridwith-filters-view");
        setSizeFull();

        save.getElement().getThemeList().add("badge success");
        cancel.getElement().getThemeList().add("badge error");
        buttons.addClassName("grid-edit-buttons");

        grid.setPageSize(10);
        grid.setPaginatorSize(1);

        // In order customize columns with PaginatedGrid we need to clean
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

        // don't do anything when clicking a row since we have the edit button
        grid.setSelectionMode(Grid.SelectionMode.NONE);

        // Close row editor on escape
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

        // Configure Filter
        HeaderRow headerRow = grid.prependHeaderRow();
        createFilterHeader(headerRow, nameFilter, "firstName", "lastName");
        createFilterHeader(headerRow, emailFilter, "email");
        createFilterHeader(headerRow, phoneFilter, "phone");
        createFilterHeader(headerRow, occupationsFilter, "occupation");
        createFilterHeader(headerRow, rolesFilter, "role");
        createFilterHeader(headerRow, importantFilter, "important");
        occupationsFilter.setItems(samplePersonService.findDistinctOccupationValues());
        rolesFilter.setItems(samplePersonService.findDistinctRoleValues());

        // Configure and set the data provider
        SamplePersonRestDataProvider samplePersonDataProvider = new SamplePersonRestDataProvider(samplePersonService);
        grid.setDataProvider(samplePersonDataProvider.withFilter(this));

        add(grid);
    }

    private <T extends InputField<?,?>> void createFilterHeader(HeaderRow headerRow, T field, String... columnNames) {
        if (field instanceof HasValueChangeMode) {
            ((HasValueChangeMode)field).setValueChangeMode(ValueChangeMode.EAGER);
        }
        if (field instanceof TextField) {
            ((TextField)field).setClearButtonVisible(true);
        }
        field.addValueChangeListener(e -> grid.refreshPaginator());
        if (columnNames.length > 1) {
            Column<?>[] cols = Arrays.asList(columnNames).stream().map(n -> grid.getColumnByKey(n)).toArray(Column<?>[]::new);
            headerRow.join(cols).setComponent((Component)field);
        } else {
            headerRow.getCell(grid.getColumnByKey(columnNames[0])).setComponent((Component)field);
        }
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

    @Override
    public HashMap<String, List<String>> getFilterParameters() {
        HashMap<String, List<String>> map = new HashMap<>();
        if (!nameFilter.isEmpty()) {
            map.put("name", Arrays.asList(nameFilter.getValue().toLowerCase()));
        }
        if (!phoneFilter.isEmpty()) {
            String ignore = "- ()";
            String lowerCaseFilter = removeIgnotedCharacters(ignore, phoneFilter.getValue().toLowerCase());
            map.put("phone", Arrays.asList(lowerCaseFilter));
        }
        if (!occupationsFilter.isEmpty()) {
            map.put("occupation", List.copyOf(occupationsFilter.getValue()));
        }
        if (!rolesFilter.isEmpty()) {
            map.put("role", List.copyOf(rolesFilter.getValue()));
        }
        if (importantFilter.getValue()) {
            map.put("important", Arrays.asList("true"));
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
