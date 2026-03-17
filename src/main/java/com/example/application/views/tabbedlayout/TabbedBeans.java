package com.example.application.views.tabbedlayout;

import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TabbedBeans {

    @Bean
    @UIScope
    public Grid<SamplePerson> samplePersonGrid(SamplePersonService service) {
        Grid<SamplePerson> grid = new Grid<>(SamplePerson.class, false);
        grid.addColumn("firstName").setAutoWidth(true);
        grid.addColumn("lastName").setAutoWidth(true);
        grid.addColumn("email").setAutoWidth(true);
        grid.addColumn("occupation").setAutoWidth(true);
        grid.addColumn("role").setAutoWidth(true);
        grid.setItemsPageable(service::listItems);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setSizeFull();

        grid.getElement().addAttachListener(e -> {
            SamplePerson p = grid.asSingleSelect().getValue();
            if (p != null) {
                try {
                    grid.scrollToStart();
                    grid.scrollToItem(p);
                } catch (UnsupportedOperationException ex) {
                    // scrollToItem requires ItemIndexProvider for lazy data
                }
            }
        });

        return grid;
    }
}
