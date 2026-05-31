package com.example.application.views.slider;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.vaadin.browserless.BrowserlessExtension;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.slider.IntegerRangeSlider;
import com.vaadin.flow.component.slider.IntegerRangeSliderValue;
import com.vaadin.flow.component.slider.IntegerSlider;

/**
 * Smokes the 25.2 Slider family wiring: when an IntegerSlider value updates,
 * the bound ValueSignal flips and the echo Span text follows through
 * bindText(signal.map(...)).
 */
class SliderPlaygroundViewBrowserlessTest {

    @RegisterExtension
    BrowserlessExtension browserless = new BrowserlessExtension()
            .withViewPackages(SliderPlaygroundView.class);

    @Test
    void integerSliderEchoUpdatesWhenValueChanges() {
        browserless.navigate(SliderPlaygroundView.class);

        IntegerSlider volume = browserless.find(IntegerSlider.class).first();
        volume.setValue(85);

        Span echo = browserless.find(Span.class).withTextContaining("Volume: ").single();
        assertTrue(echo.getText().contains("85"),
                "echo span should reflect the new IntegerSlider value via the signal binding, got: " + echo.getText());
    }

    @Test
    void integerRangeSliderEchoReflectsBothEnds() {
        browserless.navigate(SliderPlaygroundView.class);

        IntegerRangeSlider price = browserless.find(IntegerRangeSlider.class).first();
        price.setValue(new IntegerRangeSliderValue(100, 200));

        Span echo = browserless.find(Span.class).withTextContaining("Range: ").single();
        assertTrue(echo.getText().contains("100") && echo.getText().contains("200"),
                "echo should mention both start and end, got: " + echo.getText());
    }
}
