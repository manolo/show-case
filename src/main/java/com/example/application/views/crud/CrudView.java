package com.example.application.views.crud;

import static com.example.application.views.crud.CrudView.ROUTE_EDIT;
import static com.example.application.views.crud.CrudView.ROUTE_NEW;

import java.util.Optional;
import java.util.stream.Stream;

import com.example.application.components.datepicker.LocalDatePicker;
import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.Crud.EditMode;
import com.vaadin.flow.component.crud.CrudEditorPosition;
import com.vaadin.flow.component.crud.CrudFilter;
import com.vaadin.flow.component.crud.CrudGrid;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;

@PageTitle("Crud")
@Route(value = ROUTE_EDIT, layout = MainLayout.class)
@RouteAlias(value = ROUTE_NEW, layout = MainLayout.class)
@PreserveOnRefresh
public class CrudView extends Div implements BeforeEnterObserver {

    final static String ROUTE = "crud";
    final static String ROUTE_ID = "samplePersonID";
    final static String ROUTE_EDIT = ROUTE + "/:" + ROUTE_ID + "?/:action?(edit)";
    final static String ROUTE_EDIT_TPL = ROUTE + "/%s/edit";
    final static String ROUTE_NEW = ROUTE + "/:new(new)";

    private Crud<SamplePerson> crud;

    private TextField firstName = new TextField("First Name");
    private TextField lastName = new TextField("Last Name");
    private EmailField email = new EmailField("Email");
    private TextField phone = new TextField("Phone");
    private DatePicker dateOfBirth = new LocalDatePicker("Date Of Birth");
    private TextField occupation = new TextField("Occupation");
    private TextField role = new TextField("Role");
    private Checkbox important = new Checkbox("Important");
    private FormLayout formLayout = new FormLayout(firstName, lastName, email, phone, dateOfBirth, occupation, role,
            important);

    private final BeanValidationBinder<SamplePerson> binder;

    private final SamplePersonService samplePersonService;

    public CrudView(SamplePersonService samplePersonService) {
        this.samplePersonService = samplePersonService;
        addClassNames("crud-view");

        binder = new BeanValidationBinder<>(SamplePerson.class);

        // Bind fields. This is where you'd define e.g. validation rules
        binder.bindInstanceFields(this);

        crud = new Crud<>(SamplePerson.class, new CrudGrid<>(SamplePerson.class, false),
                new BinderCrudEditor<>(binder, formLayout));

        crud.setDataProvider(new AbstractBackEndDataProvider<SamplePerson, CrudFilter>() {
            protected Stream<SamplePerson> fetchFromBackEnd(Query<SamplePerson, CrudFilter> query) {
                return samplePersonService.list(VaadinSpringDataHelpers.toSpringPageRequest(query)).stream();
            }

            protected int sizeInBackEnd(Query<SamplePerson, CrudFilter> query) {
                return samplePersonService.count();
            }
        });

        crud.getGrid().getColumnByKey("dateOfBirth").setRenderer(
                new LocalDateRenderer<>(SamplePerson::getDateOfBirth, dateOfBirth.getI18n().getDateFormats().get(0)));

        crud.setEditorPosition(CrudEditorPosition.ASIDE);

        crud.addDeleteListener(e -> {
            samplePersonService.delete(e.getItem().getId());
            UI.getCurrent().navigate(this.getClass());
        });
        crud.addSaveListener(e -> {
            samplePersonService.update(e.getItem());
            UI.getCurrent().navigate(this.getClass());
        });
        crud.addEditListener(e -> {
            UI.getCurrent().navigate(String.format(ROUTE_EDIT_TPL, e.getItem().getId()));
        });
        crud.addCancelListener(e -> {
            UI.getCurrent().navigate(this.getClass());
        });
        crud.addNewListener(e -> {
            UI.getCurrent().navigate("crud/new");
        });

        crud.setSizeFull();
        this.setSizeFull();
        add(crud);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (event.getRouteParameters().get("new").isPresent()) {
            crud.setOpened(true);
            return;
        }
        Optional<Long> samplePersonId = event.getRouteParameters().get(ROUTE_ID).map(Long::parseLong);
        if (samplePersonId.isPresent()) {
            Optional<SamplePerson> samplePersonFromBackend = samplePersonService.get(samplePersonId.get());
            if (samplePersonFromBackend.isPresent()) {
                crud.edit(samplePersonFromBackend.get(), EditMode.EXISTING_ITEM);
            } else {
                event.forwardTo(this.getClass());
            }
        }
    }

}
