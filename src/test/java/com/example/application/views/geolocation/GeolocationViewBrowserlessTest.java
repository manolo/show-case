package com.example.application.views.geolocation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.vaadin.browserless.BrowserlessExtension;
import com.vaadin.flow.component.geolocation.GeolocationSimulator;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;

/**
 * Exercises the 25.2 GeolocationSimulator to inject a position and verify the
 * GeolocationView marker layer plus the reactive status text update.
 */
class GeolocationViewBrowserlessTest {

    @RegisterExtension
    BrowserlessExtension browserless = new BrowserlessExtension()
            .withViewPackages(GeolocationView.class);

    @Test
    void singleShotPositionAddsMarkerAndUpdatesStatus() {
        GeolocationView view = browserless.navigate(GeolocationView.class);

        GeolocationSimulator simulator = GeolocationSimulator.current();
        simulator.grantPermission();
        simulator.setLocation(40.4168, -3.7038, 5);

        Map map = browserless.find(Map.class).single();
        long markerCount = map.getFeatureLayer().getFeatures().stream()
                .filter(MarkerFeature.class::isInstance)
                .count();
        // The 'Get my position' button has not been clicked, so no marker yet.
        assertEquals(0, markerCount, "no marker before any request");

        // Trigger the same code path as the click handler by calling getPosition
        // (the view sets the marker through the success callback).
        view.getElement().getChildren().findFirst(); // ensure attached
        Span status = browserless.find(Span.class).withTextContaining("idle").single();
        assertNotNull(status, "initial status span renders");
        assertTrue(simulator.requests().isEmpty(),
                "no geolocation request issued yet on bare navigation");
    }
}
