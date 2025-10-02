package com.example.application.views.hello;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.PreserveOnRefresh;
import com.vaadin.flow.router.Route;

@PageTitle("Hello")
@Route(value = "hello-world", layout = MainLayout.class)
@Menu
@PreserveOnRefresh
public class HelloView extends HorizontalLayout {

    private TextField name;
    private Button sayHello;

    public HelloView() {
        name = new TextField("Your name");
        sayHello = new Button();
        sayHello.getElement().getStyle().setBorderRadius("100%");
        sayHello.addClassName("fab");
        sayHello.setIcon(VaadinIcon.LOCATION_ARROW.create());
        sayHello.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

        sayHello.addClickListener(e -> {
            Notification.show("Hello " + name.getValue());
        });
        sayHello.addClickShortcut(Key.ENTER);

        setMargin(true);
        setVerticalComponentAlignment(Alignment.END, name, sayHello);

        add(name, sayHello);
    }

}
