package com.example.application.views.datagrid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.vaadin.browserless.BrowserlessExtension;
import com.vaadin.flow.component.grid.HeaderRow.HeaderCell;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.textfield.TextField;

/**
 * Verifies that the Signal.computed filter chain in DataGridView reduces the
 * grid items as soon as a filter signal updates.
 */
class DataGridViewBrowserlessTest {

    @RegisterExtension
    BrowserlessExtension browserless = new BrowserlessExtension()
            .withViewPackages(DataGridView.class);

    @Test
    void clientFilterShrinksGridReactively() {
        browserless.navigate(DataGridView.class);

        @SuppressWarnings("unchecked")
        GridPro<Client> grid = (GridPro<Client>) browserless.find(GridPro.class).single();
        long initialCount = grid.getGenericDataView().getItems().count();
        assertEquals(9, initialCount, "DataGridView seeds 9 clients on first load");

        // The four filter fields live inside the grid's header rows; the client
        // filter is the first one. We reach it through HeaderRow.getCells().
        TextField clientFilter = grid.getHeaderRows().stream()
                .flatMap(row -> row.getCells().stream())
                .map(HeaderCell::getComponent)
                .filter(TextField.class::isInstance)
                .map(TextField.class::cast)
                .findFirst()
                .orElseThrow();

        clientFilter.setValue("son");

        long filteredCount = grid.getGenericDataView().getItems().count();
        assertTrue(filteredCount < initialCount, "filter must reduce row count");
        assertTrue(grid.getGenericDataView().getItems()
                .allMatch(c -> c.getClient().toLowerCase().contains("son")),
                "every remaining row contains the filter substring");
    }
}
