package com.example.application.views.tabbedlayout;

import com.example.application.data.SamplePerson;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Tabbed Layout")
@Route(value = "", layout = SampleTabbedLayout.class)
@PermitAll
@Menu(order = 25, icon = LineAwesomeIconUrl.FOLDER_OPEN_SOLID, title = "Tabbed Layout")
public class TabbedListView extends Div {

    public TabbedListView(Grid<SamplePerson> grid) {
        setSizeFull();
        add(grid);

        grid.addItemClickListener(event -> {
            Long id = event.getItem().getId();
            UI.getCurrent().navigate("tabbed/" + id + "/edit");
        });
    }
}
