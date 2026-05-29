package com.example.application.views.geolocation;

import java.time.Duration;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.geolocation.Geolocation;
import com.vaadin.flow.component.geolocation.GeolocationAvailability;
import com.vaadin.flow.component.geolocation.GeolocationError;
import com.vaadin.flow.component.geolocation.GeolocationOptions;
import com.vaadin.flow.component.geolocation.GeolocationPending;
import com.vaadin.flow.component.geolocation.GeolocationPosition;
import com.vaadin.flow.component.geolocation.GeolocationWatcher;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.map.Map;
import com.vaadin.flow.component.map.configuration.Coordinate;
import com.vaadin.flow.component.map.configuration.View;
import com.vaadin.flow.component.map.configuration.feature.MarkerFeature;
import com.vaadin.flow.component.map.configuration.layer.FeatureLayer;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

import jakarta.annotation.security.PermitAll;

@PageTitle("Geolocation")
@Route("geolocation")
@PermitAll
@Menu(order = 11, icon = LineAwesomeIconUrl.MAP_MARKER_SOLID)
public class GeolocationView extends HorizontalLayout {

    private final Map map = new Map();
    private final FeatureLayer featureLayer = map.getFeatureLayer();
    private final ValueSignal<String> status = new ValueSignal<>("(no fix yet)");
    private final ValueSignal<GeolocationWatcher> watcher = new ValueSignal<>(null);

    public GeolocationView() {
        addClassName("geolocation-view");
        setSizeFull();
        setPadding(false);
        setSpacing(false);

        map.getElement().setAttribute("theme", "borderless");
        map.setHeightFull();
        View defaultView = new View();
        defaultView.setCenter(new Coordinate(7, 55));
        defaultView.setZoom(4);
        map.setView(defaultView);

        VerticalLayout sidebar = buildSidebar();
        add(map, sidebar);
        expand(map);
    }

    private VerticalLayout buildSidebar() {
        VerticalLayout sidebar = new VerticalLayout();
        sidebar.setWidth("320px");
        sidebar.addClassNames(Padding.MEDIUM, BoxSizing.BORDER);

        H3 title = new H3("Geolocation API");

        Checkbox highAccuracy = new Checkbox("High accuracy", true);
        NumberField timeoutSeconds = new NumberField("Timeout (seconds)");
        timeoutSeconds.setValue(10d);
        timeoutSeconds.setMin(1);
        NumberField maxAgeSeconds = new NumberField("Max age (seconds)");
        maxAgeSeconds.setValue(0d);
        maxAgeSeconds.setMin(0);

        Span availability = new Span();
        availability.getElement().getThemeList().add("badge");
        availability.bindText(Geolocation.availabilityHintSignal()
                .map(a -> "Permission: " + (a != null ? a.name() : GeolocationAvailability.UNKNOWN.name())));

        Span statusLabel = new Span();
        statusLabel.bindText(status);

        Button locateButton = new Button("Get my position");
        locateButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        locateButton.addClickListener(e -> requestOnce(highAccuracy.getValue(),
                timeoutSeconds.getValue(), maxAgeSeconds.getValue()));

        Button watchToggle = new Button("Start watching");
        watchToggle.addClickListener(e -> {
            GeolocationWatcher current = watcher.peek();
            if (current == null) {
                GeolocationWatcher w = Geolocation.watchPosition(this, optionsFrom(
                        highAccuracy.getValue(), timeoutSeconds.getValue(), maxAgeSeconds.getValue()));
                watcher.set(w);
                watchToggle.setText("Stop watching");
            } else {
                current.stop();
                watcher.set(null);
                watchToggle.setText("Start watching");
            }
        });

        Button clearButton = new Button("Clear pins", e -> {
            for (var feature : featureLayer.getFeatures().toArray(new MarkerFeature[0])) {
                featureLayer.removeFeature(feature);
            }
            status.set("(cleared)");
        });

        sidebar.add(title, availability, highAccuracy, timeoutSeconds, maxAgeSeconds,
                new HorizontalLayout(locateButton, watchToggle), clearButton, statusLabel);
        return sidebar;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        Signal.effect(this, () -> {
            GeolocationWatcher w = watcher.get();
            if (w == null) {
                status.set("(idle)");
                return;
            }
            var result = w.positionSignal().get();
            if (result instanceof GeolocationPending) {
                status.set("Waiting for a fix...");
            } else if (result instanceof GeolocationPosition pos) {
                applyPosition(pos);
                status.set(formatPosition(pos));
            } else if (result instanceof GeolocationError err) {
                status.set("Error: " + err.errorCode() + " (" + err.debugInfo() + ")");
            }
        });
    }

    private void requestOnce(boolean highAcc, Double timeoutSeconds, Double maxAgeSeconds) {
        status.set("Requesting position...");
        Geolocation.getPosition(
                pos -> {
                    applyPosition(pos);
                    status.set(formatPosition(pos));
                },
                err -> {
                    status.set("Error: " + err.errorCode() + " (" + err.debugInfo() + ")");
                    Notification notification = Notification.show("Geolocation failed: " + err.debugInfo());
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                },
                optionsFrom(highAcc, timeoutSeconds, maxAgeSeconds));
    }

    private GeolocationOptions optionsFrom(boolean highAcc, Double timeoutSeconds, Double maxAgeSeconds) {
        return GeolocationOptions.builder()
                .highAccuracy(highAcc)
                .timeout(Duration.ofMillis(Math.round((timeoutSeconds != null ? timeoutSeconds : 10) * 1000)))
                .maximumAge(Duration.ofMillis(Math.round((maxAgeSeconds != null ? maxAgeSeconds : 0) * 1000)))
                .build();
    }

    private void applyPosition(GeolocationPosition pos) {
        Coordinate coordinate = new Coordinate(pos.coords().longitude(), pos.coords().latitude());
        featureLayer.addFeature(new MarkerFeature(coordinate));
        View view = map.getView();
        view.setCenter(coordinate);
        if (view.getZoom() < 10) {
            view.setZoom(13);
        }
    }

    private String formatPosition(GeolocationPosition pos) {
        return String.format("lat=%.5f, lon=%.5f (accuracy %.0f m)",
                pos.coords().latitude(), pos.coords().longitude(), pos.coords().accuracy());
    }
}
