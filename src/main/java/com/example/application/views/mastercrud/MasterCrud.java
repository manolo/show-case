package com.example.application.views.mastercrud;

import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.data.domain.PageRequest;

import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.Crud.EditMode;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.component.crud.CrudGrid;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

@PageTitle("Master-Crud")
@Route(value = "master-crud/:samplePersonID?/:action?(edit)", layout = MainLayout.class)
@Uses(Icon.class)
public class MasterCrud extends Div implements BeforeEnterObserver {

    private final String SAMPLEPERSON_ID = "samplePersonID";
    private final String SAMPLEPERSON_EDIT_ROUTE_TEMPLATE = "master-crud/%s/edit";

    private Crud<SamplePerson> crud;
    FormLayout formLayout;

    private TextField firstName;
    private TextField lastName;
    private TextField email;
    private TextField phone;
    private DatePicker dateOfBirth;
    private TextField occupation;
    private TextField role;
    private Checkbox important;

    private final BeanValidationBinder<SamplePerson> binder;

    private SamplePerson samplePerson;

    private final SamplePersonService samplePersonService;

    public MasterCrud(SamplePersonService samplePersonService) {
        this.samplePersonService = samplePersonService;
        addClassNames("master-crud-view");

        createEditorLayout();

        binder = new BeanValidationBinder<>(SamplePerson.class);

        // Bind fields. This is where you'd define e.g. validation rules

        binder.bindInstanceFields(this);

        crud = new Crud<>(SamplePerson.class, new CrudGrid<>(SamplePerson.class, false), new BinderCrudEditor<>(binder, formLayout));
        
//        Crud.removeEditColumn(crud.getGrid());
//        crud.getGrid().addItemDoubleClickListener(event -> crud.edit(event.getItem(),
//                Crud.EditMode.EXISTING_ITEM));
        
        
        crud.setDataProvider(new AbstractBackEndDataProvider<SamplePerson, CrudFilter>() {
            protected Stream<SamplePerson> fetchFromBackEnd(Query<SamplePerson, CrudFilter> query) {
                return samplePersonService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream();
            }

            protected int sizeInBackEnd(Query<SamplePerson, CrudFilter> query) {
                return samplePersonService.count();
            }
        });

        crud.addDeleteListener(e -> {
            samplePersonService.delete(e.getItem().getId());
            UI.getCurrent().navigate(this.getClass());
        });
        crud.addSaveListener(e -> {
            samplePersonService.update(e.getItem());
            UI.getCurrent().navigate(this.getClass());
        });
        crud.addEditListener(e -> {
            UI.getCurrent().navigate(String.format(SAMPLEPERSON_EDIT_ROUTE_TEMPLATE, e.getItem().getId()));
        });
        crud.addCancelListener(e -> {
            UI.getCurrent().navigate(this.getClass());
        });
        
        crud.setSizeFull();
        this.setSizeFull();
        add(crud);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Optional<Long> samplePersonId = event.getRouteParameters().get(SAMPLEPERSON_ID).map(Long::parseLong);
        if (samplePersonId.isPresent()) {
            Optional<SamplePerson> samplePersonFromBackend = samplePersonService.get(samplePersonId.get());
            if (samplePersonFromBackend.isPresent()) {
                crud.edit(samplePersonFromBackend.get(), EditMode.EXISTING_ITEM);
            } else {
                Notification.show(
                        String.format("The requested samplePerson was not found, ID = %s", samplePersonId.get()), 3000,
                        Notification.Position.BOTTOM_START);
                // when a row is selected but the data is no longer available,
                // refresh grid
                event.forwardTo(this.getClass());
            }
        }
    }

    private void createEditorLayout() {
        formLayout = new FormLayout();
        firstName = new TextField("First Name");
        lastName = new TextField("Last Name");
        email = new TextField("Email");
        phone = new TextField("Phone");
        dateOfBirth = new DatePicker("Date Of Birth");
        occupation = new TextField("Occupation");
        role = new TextField("Role");
        important = new Checkbox("Important");
        formLayout.add(firstName, lastName, email, phone, dateOfBirth, occupation, role, important);
    }

    private void clearForm() {
        populateForm(null);
    }

    private void populateForm(SamplePerson value) {
        this.samplePerson = value;
        binder.readBean(this.samplePerson);

    }
}
