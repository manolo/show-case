package com.example.application.views.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
import com.vaadin.flow.component.tabs.Tabs;

/**
 * Multi-user / multi-window browserless smoke test for {@link ChatView} using
 * the Spring-aware browserless context. Two users (Alice and Bob) each open a
 * window and navigate to ChatView; the test verifies the views are independent
 * (different Tabs instances, different ChatView instances) and that each user
 * gets a fresh state.
 *
 * <p>The deeper assertion (one user's message bumps the other user's unread
 * badge through CollaborationEngine) is intentionally left for an integration
 * test because dispatching the real submit event on the message input web
 * component requires browser-level interaction.
 */
@SpringBootTest(classes = Application.class)
@DirtiesContext
class ChatViewMultiUserBrowserlessTest extends SpringBrowserlessTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void aliceAndBobGetIndependentChatViewInstances() {
        SecuredBrowserlessApplicationContext<?> app = SpringBrowserlessApplicationContext
                .createSecured(discoverRoutes(), applicationContext);
        try (BrowserlessApplicationContext autoClose = app) {
            BrowserlessUserContext alice = app.newUser("alice", "USER");
            BrowserlessUserContext bob = app.newUser("bob", "USER");

            BrowserlessUIContext aliceWindow = alice.newWindow();
            aliceWindow.navigate(ChatView.class);
            Tabs aliceTabs = aliceWindow.find(Tabs.class).single();

            BrowserlessUIContext bobWindow = bob.newWindow();
            bobWindow.navigate(ChatView.class);
            Tabs bobTabs = bobWindow.find(Tabs.class).single();

            assertEquals(3, aliceTabs.getChildren().count(),
                    "ChatView seeds three chat tabs for Alice");
            assertEquals(3, bobTabs.getChildren().count(),
                    "ChatView seeds three chat tabs for Bob");
            assertNotEquals(aliceTabs, bobTabs,
                    "each user receives an independent Tabs instance");

            // Move Alice to #support and verify Bob's window stays on #general.
            aliceWindow.activate();
            aliceTabs.setSelectedIndex(1);
            assertEquals(1, aliceTabs.getSelectedIndex(), "Alice moved to support");

            bobWindow.activate();
            assertEquals(0, bobTabs.getSelectedIndex(),
                    "Bob's window is unaffected by Alice's tab selection");
        }
    }
}
