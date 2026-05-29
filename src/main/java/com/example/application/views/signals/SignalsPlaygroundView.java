package com.example.application.views.signals;

import java.util.List;
import java.util.Locale;

import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ListSignal;
import com.vaadin.flow.signals.local.ValueSignal;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import jakarta.annotation.security.PermitAll;

@PageTitle("Signals Playground")
@Route("signals")
@PermitAll
@Menu(order = 5, icon = LineAwesomeIconUrl.BOLT_SOLID)
public class SignalsPlaygroundView extends VerticalLayout {

    public SignalsPlaygroundView() {
        addClassName("signals-playground-view");
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Vaadin 25.2 Signals Playground");
        title.addClassNames(Margin.Bottom.NONE);
        Paragraph subtitle = new Paragraph("Each section demonstrates one Signals API. Open, type, and watch the UI react.");
        subtitle.addClassNames(TextColor.SECONDARY, Margin.Top.NONE);
        add(title, subtitle);

        add(counterSection());
        add(twoWaySection());
        add(computedSection());
        add(formValidationSection());
        add(visibilitySection());
        add(classBindingSection());
        add(themeBindingSection());
        add(helperTextSection());
        add(placeholderSection());
        add(readOnlySection());
        add(sizeBindingSection());
        add(styleBindingSection());
        add(windowSizeSection());
        add(todoListSection());
        add(localeSection());
        add(flashSection());
        add(routerStateSection());
        add(pageVisibilitySection());
        add(collapseChipsSection());
    }

    // 1. Counter
    private Details counterSection() {
        ValueSignal<Integer> count = new ValueSignal<>(0);

        Button button = new Button();
        button.bindText(count.map(c -> "Clicked " + c + " times"));
        button.addClickListener(e -> count.update(c -> c + 1));

        return demo("1. Counter — ValueSignal + bindText", button);
    }

    // 2. Two-way binding mirror
    private Details twoWaySection() {
        ValueSignal<String> name = new ValueSignal<>("");

        TextField field = new TextField("Type something");
        field.setValueChangeMode(ValueChangeMode.EAGER);
        field.bindValue(name, name::set);

        Span mirror = new Span();
        mirror.bindText(name.map(n -> n.isEmpty() ? "(empty)" : "You typed: " + n));

        return demo("2. Two-way binding — bindValue + bindText mirror", field, mirror);
    }

    // 3. Computed full name
    private Details computedSection() {
        ValueSignal<String> first = new ValueSignal<>("Jane");
        ValueSignal<String> last = new ValueSignal<>("Doe");

        TextField firstField = new TextField("First name");
        firstField.setValueChangeMode(ValueChangeMode.EAGER);
        firstField.bindValue(first, first::set);
        TextField lastField = new TextField("Last name");
        lastField.setValueChangeMode(ValueChangeMode.EAGER);
        lastField.bindValue(last, last::set);

        Span full = new Span();
        full.bindText(Signal.computed(() -> first.get() + " " + last.get()));
        full.addClassNames(FontSize.LARGE);

        return demo("3. Signal.computed — combine two signals", new HorizontalLayout(firstField, lastField), full);
    }

    // 4. Form validation enables submit
    private Details formValidationSection() {
        ValueSignal<String> email = new ValueSignal<>("");
        ValueSignal<String> pass = new ValueSignal<>("");

        TextField emailField = new TextField("Email");
        emailField.setValueChangeMode(ValueChangeMode.EAGER);
        emailField.bindValue(email, email::set);
        PasswordField passField = new PasswordField("Password (min 8)");
        passField.setValueChangeMode(ValueChangeMode.EAGER);
        passField.bindValue(pass, pass::set);

        Button submit = new Button("Sign up");
        submit.bindEnabled(Signal.computed(() -> email.get().contains("@") && pass.get().length() >= 8));

        return demo("4. bindEnabled — submit only valid", new HorizontalLayout(emailField, passField), submit);
    }

    // 5. Conditional visibility
    private Details visibilitySection() {
        ValueSignal<Boolean> show = new ValueSignal<>(false);
        Checkbox toggle = new Checkbox("Show details");
        toggle.bindValue(show, show::set);

        Div details = new Div(new Span("Hidden details revealed!"));
        details.addClassNames(Padding.MEDIUM, "rounded-m");
        details.getStyle().set("background", "var(--lumo-contrast-5pct)");
        details.bindVisible(show);

        return demo("5. bindVisible", toggle, details);
    }

    // 6. Class binding
    private Details classBindingSection() {
        ValueSignal<Boolean> highlighted = new ValueSignal<>(false);
        Checkbox toggle = new Checkbox("Highlight");
        toggle.bindValue(highlighted, highlighted::set);

        Div panel = new Div(new Span("This panel can be highlighted"));
        panel.addClassNames(Padding.MEDIUM, "rounded-m", "playground-panel");
        panel.bindClassName("playground-highlight", highlighted);

        return demo("6. bindClassName", toggle, panel);
    }

    // 7. Theme name binding on Grid
    private Details themeBindingSection() {
        ValueSignal<Boolean> compact = new ValueSignal<>(false);
        ValueSignal<Boolean> stripes = new ValueSignal<>(true);

        Checkbox compactCb = new Checkbox("Compact");
        compactCb.bindValue(compact, compact::set);
        Checkbox stripesCb = new Checkbox("Row stripes");
        stripesCb.bindValue(stripes, stripes::set);

        Grid<String> grid = new Grid<>();
        grid.setItems("Alice", "Bob", "Carol", "Dave", "Eve");
        grid.addColumn(String::toString).setHeader("Name");
        grid.setAllRowsVisible(true);
        grid.bindThemeName("compact", compact);
        grid.bindThemeName("row-stripes", stripes);

        return demo("7. bindThemeName — toggle Grid theme variants", new HorizontalLayout(compactCb, stripesCb), grid);
    }

    // 8. Helper text reactive
    private Details helperTextSection() {
        ValueSignal<String> text = new ValueSignal<>("");
        TextArea area = new TextArea("Comment (max 200)");
        area.setMaxLength(200);
        area.setValueChangeMode(ValueChangeMode.EAGER);
        area.bindValue(text, text::set);
        area.bindHelperText(text.map(t -> (200 - t.length()) + " characters remaining"));
        area.setWidthFull();

        return demo("8. bindHelperText — live character counter", area);
    }

    // 9. Placeholder reactive
    private Details placeholderSection() {
        ValueSignal<Boolean> verbose = new ValueSignal<>(false);
        Checkbox toggle = new Checkbox("Verbose hint");
        toggle.bindValue(verbose, verbose::set);

        TextField field = new TextField("Search");
        field.bindPlaceholder(verbose.map(v -> v ? "Type at least 3 characters to filter results..." : "Search..."));
        field.setWidthFull();

        return demo("9. bindPlaceholder", toggle, field);
    }

    // 10. Read-only binding
    private Details readOnlySection() {
        ValueSignal<Boolean> locked = new ValueSignal<>(false);
        Checkbox toggle = new Checkbox("Lock");
        toggle.bindValue(locked, locked::set);

        TextField field = new TextField("Editable until locked");
        field.setValue("Try editing me");
        field.bindReadOnly(locked);
        field.setWidthFull();

        return demo("10. bindReadOnly", toggle, field);
    }

    // 11. Size binding
    private Details sizeBindingSection() {
        ValueSignal<String> width = new ValueSignal<>("200px");
        NumberField widthField = new NumberField("Width (px)");
        widthField.setValue(200d);
        widthField.setStepButtonsVisible(true);
        widthField.setMin(50);
        widthField.setMax(800);
        widthField.setValueChangeMode(ValueChangeMode.EAGER);
        widthField.addValueChangeListener(e -> width.set(e.getValue().intValue() + "px"));

        Div panel = new Div();
        panel.setHeight("40px");
        panel.getStyle().set("background", "var(--lumo-primary-color-50pct)").set("border-radius", "var(--lumo-border-radius-m)");
        panel.bindWidth(width);

        return demo("11. bindWidth", widthField, panel);
    }

    // 12. Style binding
    private Details styleBindingSection() {
        ValueSignal<String> color = new ValueSignal<>("orange");

        TextField colorField = new TextField("CSS color");
        colorField.setValue("orange");
        colorField.setValueChangeMode(ValueChangeMode.EAGER);
        colorField.bindValue(color, color::set);

        Div panel = new Div();
        panel.setWidth("100%");
        panel.setHeight("40px");
        panel.getStyle().bind("background", color);

        return demo("12. getStyle().bind", colorField, panel);
    }

    // 13. Window size signal
    private Details windowSizeSection() {
        Span dims = new Span();
        dims.bindText(UI.getCurrent().getPage().windowSizeSignal()
                .map(ws -> ws == null ? "(loading...)" : String.format("Window: %d x %d", ws.width(), ws.height())));
        dims.addClassNames(FontSize.LARGE);

        Span responsive = new Span();
        responsive.bindText(UI.getCurrent().getPage().windowSizeSignal()
                .map(ws -> ws != null && ws.width() < 768 ? "Mobile layout" : "Desktop layout"));

        return demo("13. Page.windowSizeSignal", dims, responsive);
    }

    // 14. ListSignal with bindChildren + two-way item binding via updater
    private record Todo(String text, boolean done) {
        Todo withDone(boolean done) { return new Todo(this.text, done); }
    }

    private Details todoListSection() {
        ListSignal<Todo> todos = new ListSignal<>();
        ValueSignal<String> newText = new ValueSignal<>("");

        TextField input = new TextField();
        input.setPlaceholder("New todo");
        input.setValueChangeMode(ValueChangeMode.EAGER);
        input.bindValue(newText, newText::set);

        Button add = new Button(VaadinIcon.PLUS.create());
        add.bindEnabled(newText.map(t -> !t.isBlank()));
        add.addClickListener(e -> {
            todos.insertLast(new Todo(newText.peek(), false));
            newText.set("");
        });

        Span counter = new Span();
        counter.bindText(Signal.computed(() -> {
            long done = todos.get().stream().filter(t -> t.get().done()).count();
            return done + " / " + todos.get().size() + " done";
        }));

        VerticalLayout list = new VerticalLayout();
        list.setPadding(false);
        list.setSpacing(false);
        list.bindChildren(todos, todoSignal -> {
            HorizontalLayout row = new HorizontalLayout();
            row.setSpacing(true);
            Checkbox done = new Checkbox();
            done.bindValue(todoSignal.map(Todo::done), todoSignal.updater(Todo::withDone));
            Span label = new Span();
            label.bindText(todoSignal.map(Todo::text));
            label.getStyle().bind("text-decoration", todoSignal.map(t -> t.done() ? "line-through" : "none"));
            Button delete = new Button(VaadinIcon.TRASH.create(), e -> todos.remove(todoSignal));
            row.add(done, label, delete);
            return row;
        });

        return demo("14. ListSignal + bindChildren + updater (todos)",
                new HorizontalLayout(input, add), counter, list);
    }

    // 15. Locale signal
    private Details localeSection() {
        var localeSignal = UI.getCurrent().localeSignal();
        Button en = new Button("English", e -> UI.getCurrent().setLocale(Locale.ENGLISH));
        Button es = new Button("Español", e -> UI.getCurrent().setLocale(Locale.forLanguageTag("es")));
        Span greeting = new Span();
        greeting.bindText(localeSignal.map(l -> l != null && "es".equals(l.getLanguage()) ? "¡Bienvenido!" : "Welcome!"));
        greeting.addClassNames(FontSize.LARGE);

        return demo("15. UI.localeSignal — read-only via signal, write via UI.setLocale", new HorizontalLayout(en, es), greeting);
    }

    // 16. Flash class on change
    private Details flashSection() {
        ValueSignal<Integer> counter = new ValueSignal<>(0);
        Span value = new Span();
        value.bindText(counter.map(String::valueOf));
        value.addClassNames(FontSize.XXLARGE, Padding.MEDIUM, "rounded-m", "playground-panel");

        Signal.effect(value, () -> {
            counter.get();
            value.getElement().flashClass("playground-flash");
        });

        Button bump = new Button("Increment", e -> counter.update(c -> c + 1));
        return demo("16. flashClass on signal change", bump, value);
    }

    // 17. UI.routerStateSignal — derived navigation context
    private Details routerStateSection() {
        var routerState = UI.getCurrent().routerStateSignal();
        Span path = new Span();
        path.bindText(routerState.map(s -> "Path: " + s.location().getPath()));
        Span target = new Span();
        target.bindText(routerState.map(s -> "Target: " + (s.navigationTarget() != null
                ? s.navigationTarget().getSimpleName() : "(none)")));
        Span params = new Span();
        params.bindText(routerState.map(s -> "Params: " + s.routeParameters().getParameterNames()));
        return demo("17. UI.routerStateSignal — navigation state as a signal", path, target, params);
    }

    // 18. Page.pageVisibilitySignal — browser tab visibility
    private Details pageVisibilitySection() {
        var visibility = UI.getCurrent().getPage().pageVisibilitySignal();
        Span state = new Span();
        state.bindText(visibility.map(v -> "Tab state: " + (v != null ? v.name() : "(loading)")));
        state.addClassNames(FontSize.LARGE);
        Paragraph hint = new Paragraph(
                "Switch to another browser tab and back — the value reflects the Page Visibility API and can gate heavy work.");
        hint.addClassNames(TextColor.SECONDARY);
        return demo("18. Page.pageVisibilitySignal — pause work when tab is hidden", state, hint);
    }

    // 19. MultiSelectComboBox.setCollapseChips — overflow as a single "N items" chip
    private Details collapseChipsSection() {
        MultiSelectComboBox<String> combo = new MultiSelectComboBox<>("Toppings");
        combo.setItems("Cheese", "Tomato", "Basil", "Olives", "Onion", "Mushroom", "Pepper", "Anchovy");
        combo.setCollapseChips(true);
        combo.select("Cheese", "Tomato", "Basil", "Olives");
        combo.setWidth("280px");

        Checkbox collapseToggle = new Checkbox("Collapse chips", true);
        collapseToggle.addValueChangeListener(e -> combo.setCollapseChips(e.getValue()));

        Paragraph hint = new Paragraph(
                "Toggle the checkbox: with collapse on the field shows a single \"4\" chip; with it off the chips overflow.");
        hint.addClassNames(TextColor.SECONDARY, Margin.Top.NONE);

        return demo("19. MultiSelectComboBox.setCollapseChips — overflow collapses to a count chip",
                collapseToggle, combo, hint);
    }

    private Details demo(String title, com.vaadin.flow.component.Component... children) {
        VerticalLayout body = new VerticalLayout(children);
        body.setPadding(false);
        body.setSpacing(true);
        body.addClassNames(Display.FLEX, FlexDirection.COLUMN, Gap.MEDIUM);
        Details details = new Details(title, body);
        details.setOpened(true);
        details.addClassNames(Padding.SMALL);
        return details;
    }
}
