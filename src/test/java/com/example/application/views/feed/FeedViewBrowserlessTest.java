package com.example.application.views.feed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.vaadin.browserless.BrowserlessExtension;
import com.vaadin.flow.component.grid.Grid;

/**
 * Browserless smoke test for {@link FeedView} using the 25.2 browserless-test
 * API. Demonstrates that the ListSignal + Signal.effect(grid, setItems)
 * pattern populates the grid synchronously when the view is constructed,
 * so the data is observable without spinning up a browser.
 */
class FeedViewBrowserlessTest {

    @RegisterExtension
    BrowserlessExtension browserless = new BrowserlessExtension()
            .withViewPackages(FeedView.class);

    @Test
    void feedViewSeedsTwelvePersonsThroughTheListSignal() {
        browserless.navigate(FeedView.class);

        @SuppressWarnings("unchecked")
        Grid<Person> grid = (Grid<Person>) browserless.find(Grid.class).single();

        long count = grid.getGenericDataView().getItems().count();
        assertEquals(12, count, "FeedView should seed 12 persons via ListSignal.insertLast");

        boolean hasJohnSmith = grid.getGenericDataView().getItems()
                .anyMatch(p -> "John Smith".equals(p.getName()));
        assertTrue(hasJohnSmith, "Expected seed entry 'John Smith' in the feed");
    }
}
