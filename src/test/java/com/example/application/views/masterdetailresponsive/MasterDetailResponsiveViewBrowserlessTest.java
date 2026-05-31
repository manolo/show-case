package com.example.application.views.masterdetailresponsive;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;

import com.example.application.Application;
import com.vaadin.browserless.BrowserlessApplicationContext;
import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.browserless.BrowserlessUserContext;
import com.vaadin.browserless.SecuredBrowserlessApplicationContext;
import com.vaadin.browserless.SpringBrowserlessApplicationContext;
import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.masterdetaillayout.MasterDetailLayout;

/**
 * Spring-aware browserless test for the 25.2 MasterDetailLayout. Starts a
 * full Spring context so SamplePersonService can be injected into the view,
 * navigates to the route, asserts the layout starts with the placeholder and
 * no detail, then clicks the + FAB and verifies the detail is set.
 */
@SpringBootTest(classes = Application.class)
@DirtiesContext
class MasterDetailResponsiveViewBrowserlessTest extends SpringBrowserlessTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void clickingPlusSwitchesPlaceholderToDetail() {
        SecuredBrowserlessApplicationContext<?> app = SpringBrowserlessApplicationContext
                .createSecured(discoverRoutes(), applicationContext);
        try (BrowserlessApplicationContext autoClose = app) {
            BrowserlessUserContext user = app.newUser("admin", "USER");
            BrowserlessUIContext window = user.newWindow();
            window.navigate(MasterDetailResponsiveView.class);

            MasterDetailLayout layout = window.find(MasterDetailLayout.class).single();
            assertNull(layout.getDetail(), "no detail before clicking +");
            assertNotNull(layout.getDetailPlaceholder(), "placeholder is configured");

            Button plus = window.find(Button.class).withText("+").single();
            plus.click();

            assertNotNull(layout.getDetail(), "+ button installs the editor in the detail slot");
        }
    }
}
