package com.example.application.views.helloworld;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

/**
 * Imperative (pre Signals) version of HelloWorldView, kept for side by side
 * comparison with the Signals based implementation in HelloWorldView.
 */
@PageTitle("Hello World (Classic)")
@Route("hello-world-classic")
@PermitAll
@Menu(order = 2, icon = LineAwesomeIconUrl.GLOBE_SOLID)
public class HelloWorldClassicView extends HorizontalLayout {

    private TextField name;
    private Button sayHello;

    public HelloWorldClassicView() {
        name = new TextField("Your name");
        sayHello = new Button("Say hello");
        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
        sayHello.addClickShortcut(Key.ENTER);

        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);

        add(name, sayHello);
    }
}
