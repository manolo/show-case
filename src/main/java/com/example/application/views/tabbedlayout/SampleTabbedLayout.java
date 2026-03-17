package com.example.application.views.tabbedlayout;

import com.example.application.components.tabbedlayout.TabDef;
import com.example.application.components.tabbedlayout.TabbedLayout;
import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import jakarta.annotation.security.PermitAll;

@PageTitle("Tabbed Layout")
@RoutePrefix("tabbed")
@ParentLayout(MainLayout.class)
@PermitAll
public class SampleTabbedLayout extends TabbedLayout {

    public SampleTabbedLayout(Grid<SamplePerson> grid, SamplePersonService service) {
        super(
            new TabDef("List", "tabbed/?", "tabbed"),
            new TabDef("Form", "tabbed/\\d+/edit", null),
            new TabDef("Details", "tabbed/\\d+/details", null)
        );
        syncGrid(grid, "tabbed/(\\d+)/", id -> service.get(id));
    }

    @Override
    protected String getNavigationUrl(TabDef tab) {
        SamplePerson person = getSelectedItem();
        if (tab.defaultUrl() == null && person != null) {
            if (tab.label().equals("Form")) {
                return "tabbed/" + person.getId() + "/edit";
            }
            if (tab.label().equals("Details")) {
                return "tabbed/" + person.getId() + "/details";
            }
        }
        return "tabbed";
    }
}
