package com.example.application.views.masterdetailresponsive;

import static com.example.application.views.masterdetailresponsive.MasterDetailResponsiveView.ROUTE_EDIT;
import static com.example.application.views.masterdetailresponsive.MasterDetailResponsiveView.ROUTE_NEW;

import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import com.example.application.components.datepicker.LocalDatePicker;
import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Shortcuts;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeLeaveEvent;
import com.vaadin.flow.router.BeforeLeaveEvent.ContinueNavigationAction;
import com.vaadin.flow.router.BeforeLeaveObserver;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

@PageTitle("Master-Detail-Responsive")
@Route(value = ROUTE_EDIT, layout = MainLayout.class)
@RouteAlias(value = ROUTE_NEW, layout = MainLayout.class)
@PermitAll
@Menu
@PreserveOnRefresh
public class MasterDetailResponsiveView extends Div implements BeforeEnterObserver, BeforeLeaveObserver {

    final static String ROUTE = "master-detail-responsive";
    final static String ROUTE_ID = "samplePersonID";
    final static String ROUTE_EDIT = ROUTE + "/:" + ROUTE_ID + "?/:action?(edit)";
    final static String ROUTE_EDIT_TPL = ROUTE + "/%s/edit";
    final static String ROUTE_NEW = ROUTE + "/:new(new)";

    private final Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);

    private TextField firstName = new TextField("First Name");
    private TextField lastName = new TextField("Last Name");
    private EmailField email = new EmailField("Email");
    private TextField phone = new TextField("Phone");
    private DatePicker dateOfBirth = new LocalDatePicker("Date Of Birth");
    private TextField occupation = new TextField("Occupation");
    private TextField role = new TextField("Role");
    private Checkbox important = new Checkbox("Important");

    private Div editorLayoutDiv;
    private ConfirmDialog dialog;

    private final Button cancel = new Button("Cancel");
    private final Button save = new Button("Save");
    private final Button delete = new Button("Delete");
    private final Button plus = new Button("+");

    private final BeanValidationBinder<SamplePerson> binder;

    private SamplePerson samplePerson;

    private final SamplePersonService item;

    private ContinueNavigationAction postponed;

    public MasterDetailResponsiveView(SamplePersonService samplePersonService) {
        this.item = samplePersonService;
        addClassNames(ROUTE + "-view");

        // Create UI
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(false);
        horizontalLayout.setSizeFull();
        createGridLayout(horizontalLayout);
        createEditorLayout(horizontalLayout);
        showDetail(false);
        add(horizontalLayout);

        // Configure Grid
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addColumns("firstName", "lastName", "email", "phone", "dateOfBirth", "occupation", "role");
        grid.addColumn("important").setRenderer(new TextRenderer<>(p -> p.isImportant() ? "✔" : "—"));
        grid.getColumns().forEach(c -> c.setAutoWidth(true));

        grid.getColumnByKey("dateOfBirth").setRenderer(
                new LocalDateRenderer<>(SamplePerson::getDateOfBirth, dateOfBirth.getI18n().getDateFormats().get(0)));

        grid.setItems(query -> samplePersonService.list(
                PageRequest.of(query.getPage(), query.getPageSize(), VaadinSpringDataHelpers.toSpringDataSort(query)))
                .stream());

        // when a row is selected or deselected, populate form
        grid.asSingleSelect().addValueChangeListener(event -> {
            if (event.getValue() != null) {
                UI.getCurrent().navigate(String.format(ROUTE_EDIT_TPL, event.getValue().getId()));
            } else {
                clearForm();
                UI.getCurrent().navigate(MasterDetailResponsiveView.class);
            }
        });

        // Configure Form
        binder = new BeanValidationBinder<>(SamplePerson.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        // Add actions to the buttons
        plus.addClickListener(e -> UI.getCurrent().navigate(ROUTE + "/new"));
        cancel.addClickListener(e -> cancel());
        save.addClickListener(e -> this.save());
        delete.addClickListener(e -> this.delete());

        // Close editor on escape
        Shortcuts.addShortcutListener(this, cancel::click, Key.ESCAPE).listenOn(this);

        // Configure dialog to discard unsaved changes
        dialog = new ConfirmDialog("Discard changes", "There are unsaved changes?", "Discard", e -> {
            clearForm();
            if (postponed != null) {
                postponed.proceed();
                postponed = null;
            }
        }, "Cancel", e -> {
            postponed.cancel();
            grid.select(this.samplePerson);
            postponed = null;
        });
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (binder.hasChanges()) {
            return;
        }
        if (event.getRouteParameters().get("new").isPresent()) {
            populateForm(null);
            return;
        }
        Optional<Long> samplePersonId = event.getRouteParameters().get(ROUTE_ID).map(Long::parseLong);
        if (samplePersonId.isPresent()) {
            Optional<SamplePerson> samplePersonFromBackend = item.get(samplePersonId.get());
            if (samplePersonFromBackend.isPresent()) {
                populateForm(samplePersonFromBackend.get());
            } else {
                Notification.show(
                        String.format("The requested samplePerson was not found, ID = %s", samplePersonId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                event.forwardTo(MasterDetailResponsiveView.class);
            }
        } else {
            this.samplePerson = null;
            showDetail(false);
        }
    }

    @Override
    public void beforeLeave(BeforeLeaveEvent event) {
        if (postponed == null && binder.hasChanges()) {
            dialog.open();
            postponed = event.postpone();
        }
    }

    private void createEditorLayout(HasComponents layout) {
        editorLayoutDiv = new Div();
        editorLayoutDiv.addClassNames("editor-layout bg-contrast-5 detail");
        editorLayoutDiv.setMaxWidth("350px");

        Div editorDiv = new Div();
        editorDiv.setClassName("editor");
        editorLayoutDiv.add(editorDiv);

        FormLayout formLayout = new FormLayout();
        formLayout.add(firstName, lastName, email, phone, dateOfBirth, occupation, role, important);

        editorDiv.add(formLayout);
        createButtonLayout(editorLayoutDiv);
        layout.add(editorLayoutDiv);
    }

    private void createButtonLayout(Div layout) {
        FlexLayout buttonLayout = new FlexLayout();
        buttonLayout.setClassName("button-layout");
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_TERTIARY);
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        buttonLayout.add(delete, cancel, save);
        layout.add(buttonLayout);
    }

    private void createGridLayout(HasComponents splitLayout) {
        Div wrapper = new Div();
        wrapper.setId("grid-wrapper");
        wrapper.setWidthFull();
        splitLayout.add(wrapper);
        plus.setClassName("fab-button");
        plus.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        wrapper.add(grid);
        wrapper.add(plus);
    }

    private void cancel() {
        UI.getCurrent().navigate(MasterDetailResponsiveView.class);
    }

    private void delete() {
        if (this.samplePerson != null && this.samplePerson.getId() != null) {
            new ConfirmDialog("Delete", "Do you want to delete the Item?", "Delete", e -> {
                item.delete(this.samplePerson.getId());
                Notification.show("Data updated");
                UI.getCurrent().navigate(this.getClass());
                refreshGrid();
            }, "Cancel", e -> {
            }).open();
        }
    };

    private void save() {
        try {
            if (this.samplePerson == null) {
                this.samplePerson = new SamplePerson();
            }
            binder.writeBean(this.samplePerson);
            item.update(this.samplePerson);
            clearForm();
            refreshGrid();
            Notification.show("Data updated");
            cancel();
        } catch (ObjectOptimisticLockingFailureException exception) {
            Notification n = Notification.show(
                    "Error updating the data. Somebody else has updated the record while you were making changes.");
            n.setPosition(Position.MIDDLE);
            n.addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (ValidationException validationException) {
            Notification.show("Failed to update the data. Check again that all values are valid");
        }
    }

    private void refreshGrid() {
        grid.select(null);
        grid.getDataProvider().refreshAll();
    }

    private void clearForm() {
        populateForm(null);
        showDetail(false);
    }

    private void populateForm(SamplePerson value) {
        this.samplePerson = value;
        grid.select(this.samplePerson);
        binder.readBean(this.samplePerson);
        showDetail(true);
    }

    private void showDetail(boolean show) {
        editorLayoutDiv.setVisible(show);
        plus.setVisible(!show);
    }
}