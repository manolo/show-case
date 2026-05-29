package com.example.application.views.clipboard;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.clipboard.Clipboard;
import com.vaadin.flow.component.clipboard.ClipboardContent;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import jakarta.annotation.security.PermitAll;

@PageTitle("Clipboard")
@Route("clipboard")
@PermitAll
@Menu(order = 14, icon = LineAwesomeIconUrl.CLIPBOARD)
public class ClipboardView extends VerticalLayout {

    public ClipboardView() {
        addClassName("clipboard-view");
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Vaadin 25.2 Clipboard API");
        Paragraph subtitle = new Paragraph("Each section calls Clipboard.onClick(button) and chains a different write action. Click the button and paste in any text editor to verify.");
        subtitle.addClassNames(TextColor.SECONDARY, Margin.Top.NONE);
        add(title, subtitle);

        add(plainLiteralSection());
        add(fieldValueSection());
        add(multiFormatSection());
    }

    private Details plainLiteralSection() {
        Button copy = new Button("Copy 'Hello, world!'", new Icon(VaadinIcon.COPY));
        copy.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Clipboard.onClick(copy).writeText("Hello, world!",
                value -> Notification.show("Copied: " + value),
                error -> showError("Clipboard write failed: " + error.message()));

        return demo("1. writeText literal — copy a constant string with success/error callbacks", copy);
    }

    private Details fieldValueSection() {
        TextField token = new TextField("API token");
        token.setValue("vaadin-25.2-demo-token");
        token.setClearButtonVisible(true);
        token.setWidth("320px");

        Button copy = new Button(new Icon(VaadinIcon.COPY));
        copy.setAriaLabel("Copy field value");
        Clipboard.onClick(copy).writeText(token,
                value -> Notification.show("Copied field value (" + value.length() + " chars)"),
                error -> showError("Clipboard write failed: " + error.message()));

        HorizontalLayout row = new HorizontalLayout(token, copy);
        row.setAlignItems(Alignment.END);
        row.addClassNames(Gap.SMALL);

        return demo("2. writeText(field) — copy the live value of a TextField (client-side read at click)", row);
    }

    private Details multiFormatSection() {
        String plain = "Visit https://vaadin.com/";
        String html = "Visit <a href=\"https://vaadin.com/\"><b>vaadin.com</b></a>";

        Button copy = new Button("Copy link as plain + HTML", new Icon(VaadinIcon.LINK));
        copy.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        Clipboard.onClick(copy).write(
                ClipboardContent.create().text(plain).html(html),
                value -> Notification.show("Copied plain + HTML; pasted plain: " + value),
                error -> showError("Clipboard write failed: " + error.message()));

        VerticalLayout body = new VerticalLayout(copy,
                new Paragraph("Paste into a plain text editor to see the URL; paste into a rich-text editor like Gmail to see the styled link."));
        body.setSpacing(false);
        body.setPadding(false);

        Html preview = new Html("<div>Preview of the HTML payload: " + html + "</div>");

        return demo("3. write(ClipboardContent) — plain text and HTML together for rich paste targets",
                body, preview);
    }

    private void showError(String message) {
        Notification n = Notification.show(message);
        n.addThemeVariants(NotificationVariant.LUMO_ERROR);
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
