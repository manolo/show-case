package com.example.application.views.gridwithfiltersrest;

import java.util.List;

import com.example.application.components.datepicker.LocalDatePicker;
import com.example.application.components.filter.HasFilterParameters;
import com.example.application.components.filter.SamplePersonFilter;
import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonServiceRest;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.theme.lumo.LumoUtility;

@PageTitle("Grid with Filters REST")
@Route(value = "grid-with-filters-rest", layout = MainLayout.class)
@PermitAll
@Menu
@PreserveOnRefresh
public class GridwithFiltersRestView extends Div {

    private final Grid<SamplePerson> grid;

    private final SamplePersonFilter samplePersonfilterComponent;

    public GridwithFiltersRestView(SamplePersonServiceRest samplePersonService,
            SamplePersonRestDataProvider samplePersonDataProvider) {
        setSizeFull();
        addClassNames("gridwith-filters-view");

        List<String> occupations = samplePersonService.findDistinctOccupationValues();
        List<String> roles = samplePersonService.findDistinctRoleValues();

        grid = createGrid();
        samplePersonfilterComponent = new SamplePersonFilter(() -> grid.getDataProvider().refreshAll(), occupations,
                roles);
        ConfigurableFilterDataProvider<SamplePerson, Void, HasFilterParameters> filterDataProvider = samplePersonDataProvider
                .withConfigurableFilter();
        grid.setDataProvider(filterDataProvider);
        grid.addAttachListener(e -> filterDataProvider.setFilter(samplePersonfilterComponent));

        VerticalLayout layout = new VerticalLayout(createMobileFilters(), samplePersonfilterComponent, grid);
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        add(layout);
    }

    private HorizontalLayout createMobileFilters() {
        // Mobile version
        HorizontalLayout mobileFilters = new HorizontalLayout();
        mobileFilters.setWidthFull();
        mobileFilters.addClassNames(LumoUtility.Padding.MEDIUM, LumoUtility.BoxSizing.BORDER,
                LumoUtility.AlignItems.CENTER);
        mobileFilters.addClassName("mobile-filters");

        Icon mobileIcon = LumoIcon.PLUS.create();
        Span filtersHeading = new Span("Filters");
        mobileFilters.add(mobileIcon, filtersHeading);
        mobileFilters.setFlexGrow(1, filtersHeading);
        mobileFilters.addClickListener(e -> {
            if (samplePersonfilterComponent.isVisible()) {
                samplePersonfilterComponent.setVisible(false);
                mobileIcon.getElement().setAttribute("icon", "lumo:plus");
            } else {
                samplePersonfilterComponent.setVisible(true);
                mobileIcon.getElement().setAttribute("icon", "lumo:minus");
            }
        });
        return mobileFilters;
    }

    private Grid<SamplePerson> createGrid() {
        Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("phone").setAutoWidth(true);
        grid.addColumn("dateOfBirth").setAutoWidth(true).setRenderer(new LocalDateRenderer<>(
                SamplePerson::getDateOfBirth, new LocalDatePicker().getI18n().getDateFormats().get(0)));
        grid.addColumn("occupation").setAutoWidth(true);
        grid.addColumn("role").setAutoWidth(true);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.addClassNames(LumoUtility.Border.TOP, LumoUtility.BorderColor.CONTRAST_10);
        return grid;
    }
}