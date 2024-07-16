package com.example.application.views.gridedit;

import org.springframework.data.domain.PageRequest;

import com.example.application.components.datepicker.LocalDatePicker;
import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

@PageTitle("Editable Grid")
@Route(value = "grid-edit", layout = MainLayout.class)
@PreserveOnRefresh
public class GridEditView extends HorizontalLayout {


    private final Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);
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

    public GridEditView(SamplePersonService samplePersonService) {
        add(grid);

        // Some styles
        addClassNames("master-grid-view");
        setSizeFull();
        grid.setSizeFull();
        save.getElement().getThemeList().add("badge success");
        cancel.getElement().getThemeList().add("badge error");
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        buttons.getElement().getThemeList().add("spacing-xs");
        buttons.setPadding(false);

        // Configure Columns
        // edit column
        grid.addComponentColumn(person -> {
            Button edit = new Button(VaadinIcon.PENCIL.create(), e -> {
                editor.cancel();
                editor.editItem(person);
            });
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

        // Load data
        grid.setItems(query -> samplePersonService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        grid.setSelectionMode(Grid.SelectionMode.NONE);

        // Close editor on escape
        UI.getCurrent().addShortcutListener(cancel::click, Key.ESCAPE);

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
    }

    private void save() {
        editor.save();
        editor.closeEditor();
        grid.getDataProvider().refreshAll();
    }
    private void cancel() {
        editor.cancel();
        editor.closeEditor();
    }
}
