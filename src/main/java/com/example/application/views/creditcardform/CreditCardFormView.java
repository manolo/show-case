package com.example.application.views.creditcardform;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Credit Card Form")
@Route("credit-card-form")
@PermitAll
@Menu(order = 10, icon = LineAwesomeIconUrl.CREDIT_CARD)
public class CreditCardFormView extends Div {

    private final ValueSignal<String> cardNumber = new ValueSignal<>("");
    private final ValueSignal<String> cardHolder = new ValueSignal<>("");
    private final ValueSignal<Integer> month = new ValueSignal<>(null);
    private final ValueSignal<Integer> year = new ValueSignal<>(null);
    private final ValueSignal<String> csc = new ValueSignal<>("");

    public CreditCardFormView() {
        addClassName("credit-card-form-view");

        H3 title = new H3("Credit Card");

        TextField cardNumberField = new TextField("Credit card number");
        cardNumberField.setPlaceholder("1234 5678 9123 4567");
        cardNumberField.setAllowedCharPattern("[\\d ]");
        cardNumberField.setRequired(true);
        cardNumberField.setValueChangeMode(ValueChangeMode.EAGER);
        cardNumberField.bindValue(cardNumber, cardNumber::set);

        TextField cardHolderField = new TextField("Cardholder name");
        cardHolderField.setValueChangeMode(ValueChangeMode.EAGER);
        cardHolderField.bindValue(cardHolder, cardHolder::set);

        Select<Integer> monthSelect = new Select<>();
        monthSelect.setPlaceholder("Month");
        monthSelect.setItems(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        monthSelect.bindValue(month, month::set);

        Select<Integer> yearSelect = new Select<>();
        yearSelect.setPlaceholder("Year");
        yearSelect.setItems(25, 26, 27, 28, 29, 30);
        yearSelect.bindValue(year, year::set);

        PasswordField cscField = new PasswordField("CSC");
        cscField.setValueChangeMode(ValueChangeMode.EAGER);
        cscField.bindValue(csc, csc::set);

        HorizontalLayout expirationRow = new HorizontalLayout(monthSelect, yearSelect);
        expirationRow.setFlexGrow(1.0, monthSelect, yearSelect);
        monthSelect.setWidth("100px");
        yearSelect.setWidth("100px");

        FormLayout formLayout = new FormLayout();
        formLayout.add(cardNumberField, cardHolderField, expirationRow, cscField);

        // Live preview card with bindText + getStyle().bind on background
        Div card = new Div();
        card.addClassNames(Padding.LARGE, BorderRadius.LARGE);
        card.getStyle().set("color", "white").set("min-height", "180px").set("max-width", "320px");
        card.getStyle().bind("background", Signal.computed(() -> {
            String n = cardNumber.get().replaceAll("\\s+", "");
            if (n.startsWith("4")) return "linear-gradient(135deg, #1a1f71, #4a5cff)"; // Visa
            if (n.startsWith("5") || n.startsWith("2")) return "linear-gradient(135deg, #eb001b, #f79e1b)"; // Mastercard
            if (n.startsWith("3")) return "linear-gradient(135deg, #2e7d32, #66bb6a)"; // Amex / Diners
            return "linear-gradient(135deg, #455a64, #90a4ae)";
        }));

        Span cardNumDisplay = new Span();
        cardNumDisplay.bindText(cardNumber.map(n -> {
            String padded = (n + "•••• •••• •••• ••••").substring(0, 19);
            return padded.replaceAll("(.{4})(?!$)", "$1 ").trim();
        }));
        cardNumDisplay.addClassNames(FontSize.XXLARGE, FontWeight.MEDIUM);
        cardNumDisplay.getStyle().set("letter-spacing", "0.1em");

        Span cardHolderDisplay = new Span();
        cardHolderDisplay.bindText(cardHolder.map(s -> s.isEmpty() ? "CARDHOLDER NAME" : s.toUpperCase()));
        cardHolderDisplay.addClassNames(FontSize.SMALL);

        Span expDisplay = new Span();
        expDisplay.bindText(Signal.computed(() -> {
            Integer m = month.get();
            Integer y = year.get();
            String mm = m == null ? "MM" : String.format("%02d", m);
            String yy = y == null ? "YY" : String.format("%02d", y);
            return mm + "/" + yy;
        }));
        expDisplay.addClassNames(FontSize.SMALL);

        HorizontalLayout footer = new HorizontalLayout(cardHolderDisplay, expDisplay);
        footer.setAlignItems(com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment.BASELINE);
        footer.setJustifyContentMode(com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.BETWEEN);
        footer.setWidthFull();

        VerticalLayout cardInner = new VerticalLayout(cardNumDisplay, footer);
        cardInner.setPadding(false);
        cardInner.setSpacing(false);
        cardInner.setSizeFull();
        card.add(cardInner);

        Signal<Boolean> formValid = Signal.computed(() -> {
            String n = cardNumber.get().replaceAll("\\s+", "");
            return n.length() >= 13 && !cardHolder.get().isBlank()
                    && month.get() != null && year.get() != null
                    && csc.get().length() >= 3;
        });

        Button submit = new Button("Submit", e -> Notification.show("Submitted"));
        submit.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        submit.bindEnabled(formValid);

        Button cancel = new Button("Cancel", e -> {
            cardNumber.set(""); cardHolder.set("");
            month.set(null); year.set(null); csc.set("");
        });

        HorizontalLayout buttons = new HorizontalLayout(submit, cancel);
        buttons.addClassNames(Gap.MEDIUM, Margin.Top.MEDIUM);

        add(title, formLayout, card, buttons);
    }

    private Component title() {
        return new H3("Credit Card");
    }
}
