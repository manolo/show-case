package com.example.application.views.slider;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.slider.DecimalRangeSlider;
import com.vaadin.flow.component.slider.DecimalRangeSliderValue;
import com.vaadin.flow.component.slider.DecimalSlider;
import com.vaadin.flow.component.slider.IntegerRangeSlider;
import com.vaadin.flow.component.slider.IntegerRangeSliderValue;
import com.vaadin.flow.component.slider.IntegerSlider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import jakarta.annotation.security.PermitAll;

@PageTitle("Slider")
@Route("slider")
@PermitAll
@Menu(order = 15, icon = LineAwesomeIconUrl.SLIDERS_H_SOLID)
public class SliderPlaygroundView extends VerticalLayout {

    public SliderPlaygroundView() {
        addClassName("slider-view");
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Vaadin 25.2 Slider family");
        Paragraph subtitle = new Paragraph(
                "Slider, RangeSlider and the decimal variants graduated from experimental in 25.2. Each section binds a slider to a signal so you can see the value (and a derived preview) react in real time.");
        subtitle.addClassNames(TextColor.SECONDARY, Margin.Top.NONE);
        add(title, subtitle);

        add(integerSliderSection());
        add(decimalSliderSection());
        add(integerRangeSection());
        add(decimalRangeWithPreviewSection());
    }

    private Details integerSliderSection() {
        ValueSignal<Integer> volume = new ValueSignal<>(60);

        IntegerSlider slider = new IntegerSlider("Volume", 0, 100);
        slider.setValue(60);
        slider.setValueAlwaysVisible(true);
        slider.setValueChangeMode(ValueChangeMode.EAGER);
        slider.bindValue(volume, volume::set);

        Span echo = new Span();
        echo.addClassNames(FontSize.LARGE);
        echo.bindText(volume.map(v -> "Volume: " + v + " / 100"));

        return demo("IntegerSlider — value bound to a ValueSignal<Integer> with EAGER updates", slider, echo);
    }

    private Details decimalSliderSection() {
        ValueSignal<Double> zoom = new ValueSignal<>(1.5);

        DecimalSlider slider = new DecimalSlider("Zoom", 0.0, 10.0);
        slider.setStep(0.5);
        slider.setValue(1.5);
        slider.setValueAlwaysVisible(true);
        slider.setValueChangeMode(ValueChangeMode.EAGER);
        slider.bindValue(zoom, zoom::set);

        Span echo = new Span();
        echo.addClassNames(FontSize.LARGE);
        echo.bindText(zoom.map(z -> String.format("Zoom: %.1fx", z)));

        return demo("DecimalSlider — fractional values with a custom step", slider, echo);
    }

    private Details integerRangeSection() {
        ValueSignal<IntegerRangeSliderValue> price = new ValueSignal<>(new IntegerRangeSliderValue(50, 300));

        IntegerRangeSlider slider = new IntegerRangeSlider("Price", 0, 500);
        slider.setValue(new IntegerRangeSliderValue(50, 300));
        slider.setValueAlwaysVisible(true);
        slider.setValueChangeMode(ValueChangeMode.EAGER);
        slider.bindValue(price, price::set);

        Span echo = new Span();
        echo.addClassNames(FontSize.LARGE);
        echo.bindText(price.map(p -> "Range: " + p.start() + " - " + p.end() + " €"));

        return demo("IntegerRangeSlider — two thumbs as IntegerRangeSliderValue record", slider, echo);
    }

    private Details decimalRangeWithPreviewSection() {
        ValueSignal<DecimalRangeSliderValue> range = new ValueSignal<>(new DecimalRangeSliderValue(0.2, 0.8));

        DecimalRangeSlider slider = new DecimalRangeSlider("Opacity range", 0.0, 1.0);
        slider.setStep(0.05);
        slider.setValue(new DecimalRangeSliderValue(0.2, 0.8));
        slider.setValueAlwaysVisible(true);
        slider.setValueChangeMode(ValueChangeMode.EAGER);
        slider.bindValue(range, range::set);

        com.vaadin.flow.component.html.Div previewPanel = new com.vaadin.flow.component.html.Div(
                new Span("Live preview panel"));
        previewPanel.addClassNames(Padding.MEDIUM, "rounded-m");
        previewPanel.getStyle().set("background", "var(--lumo-primary-color)");
        previewPanel.getStyle().set("color", "var(--lumo-primary-contrast-color)");
        previewPanel.getStyle().bind("opacity",
                Signal.computed(() -> String.valueOf(range.get().end())));
        previewPanel.getStyle().bind("width",
                Signal.computed(() -> Math.round(range.get().end() * 100) + "%"));

        Span echo = new Span();
        echo.bindText(range.map(r -> String.format("Opacity = end (%.2f), width = end x 100%%", r.end())));
        echo.addClassNames(TextColor.SECONDARY);

        return demo("DecimalRangeSlider — drives getStyle().bind for live preview", slider, echo, previewPanel);
    }

    private Details demo(String title, com.vaadin.flow.component.Component... children) {
        VerticalLayout body = new VerticalLayout(children);
        body.setSpacing(true);
        body.setPadding(false);
        Details details = new Details(title, body);
        details.setOpened(true);
        return details;
    }
}
