package com.example.application.views.helloworld;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.local.ValueSignal;
import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Hello World")
@Route("hello-world")
@PermitAll
@Menu(order = 2, icon = LineAwesomeIconUrl.GLOBE_SOLID)
public class HelloWorldView extends HorizontalLayout {

    private TextField name;
    private Button sayHello;

    public HelloWorldView() {
        name = new TextField("Your name");
        sayHello = new Button("Say hello");
        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
        sayHello.addClickShortcut(Key.ENTER);

        ValueSignal<String> nameSignal = new ValueSignal<>("");
        name.setValueChangeMode(ValueChangeMode.EAGER);
        name.bindValue(nameSignal, nameSignal::set);
        Span greeting = new Span();
        greeting.bindText(nameSignal.map(n -> n.isEmpty() ? "" : "Hello " + n));
        greeting.bindVisible(nameSignal.map(n -> !n.isEmpty()));

        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, name, sayHello, greeting);

        add(name, sayHello, greeting);
    }

}