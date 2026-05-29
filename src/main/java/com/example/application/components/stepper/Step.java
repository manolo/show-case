package com.example.application.components.stepper;

import java.util.ArrayList;
import java.util.List;

import org.vaadin.lineawesome.LineAwesomeIcon;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.Border;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderColor;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Flex;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.MinWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.Position;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.theme.lumo.LumoUtility.Width;

public class Step extends ListItem implements AfterNavigationObserver, HasTheme {

    public enum State {
        ACTIVE,
        COMPLETE,
        ERROR,
        INACTIVE
    }

    public interface HasBinder {
        Binder<?> getBinder();
    }

    public interface HasValiditySignal {
        Signal<Boolean> validitySignal();
    }

    private final ValueSignal<State> state = new ValueSignal<>(State.INACTIVE);
    private final ValueSignal<Boolean> small = new ValueSignal<>(false);
    private final ValueSignal<Stepper.Orientation> orientation = new ValueSignal<>(Stepper.Orientation.VERTICAL);

    private final RouterLink link;
    private final Div circle;
    private final Span label;
    private final Span description;
    private final Class<? extends Component> routeClass;
    private Step prev;
    private Step next;

    public Step(String labelText, String descriptionText, Class<? extends Component> routeClass) {
        addClassNames(MinWidth.NONE, Position.RELATIVE);
        this.routeClass = routeClass;

        this.circle = new Div();
        this.label = new Span(labelText);
        this.description = new Span(descriptionText);

        Div layout = new Div(this.label, this.description);
        layout.addClassNames(Display.FLEX, FlexDirection.COLUMN, Overflow.HIDDEN);

        this.link = new RouterLink();
        this.link.add(this.circle, layout);
        if (routeClass != null) this.link.setRoute(routeClass);
        add(this.link);

        this.link.bindClassNames(Signal.computed(() -> linkClasses(small.get())));
        this.circle.bindClassNames(Signal.computed(() -> circleClasses(state.get(), small.get())));
        this.label.bindClassNames(Signal.computed(() -> labelClasses(state.get(), small.get(), orientation.get())));
        this.description.bindClassNames(Signal.computed(() -> descriptionClasses(small.get(), orientation.get())));
        this.link.getElement().bindAttribute("aria-current", state.map(s -> s == State.ACTIVE ? "step" : null));

        Signal.effect(this.circle, () -> {
            this.circle.removeAll();
            switch (state.get()) {
                case COMPLETE -> this.circle.add(LineAwesomeIcon.CHECK_SOLID.create());
                case ERROR -> this.circle.add(LineAwesomeIcon.EXCLAMATION_SOLID.create());
                case ACTIVE, INACTIVE -> {}
            }
        });
    }

    public Step(String label, Class<? extends Component> navigationTarget) {
        this(label, "", navigationTarget);
    }

    public void setOrientation(Stepper.Orientation orientation) {
        this.orientation.set(orientation);
    }

    public void setSmall(boolean small) {
        this.small.set(small);
    }

    public void setState(State state) {
        this.state.set(state);
    }

    public State getState() {
        return state.peek();
    }

    private static List<String> linkClasses(boolean small) {
        List<String> classes = new ArrayList<>(List.of(AlignItems.CENTER, Display.FLEX, "no-underline", Padding.SMALL));
        classes.add(small ? Gap.SMALL : Gap.MEDIUM);
        return classes;
    }

    private static List<String> circleClasses(State state, boolean small) {
        List<String> classes = new ArrayList<>(List.of(AlignItems.CENTER, Border.ALL, BoxSizing.BORDER, Display.FLEX,
                Flex.SHRINK_NONE, FontWeight.MEDIUM, JustifyContent.CENTER, "rounded-full"));
        if (small) {
            classes.add(FontSize.XSMALL); classes.add(Height.XSMALL); classes.add(Width.XSMALL);
        } else {
            classes.add(FontSize.SMALL); classes.add(Height.MEDIUM); classes.add(Width.MEDIUM);
        }
        switch (state) {
            case ACTIVE -> {
                classes.add(Background.BASE); classes.add(BorderColor.PRIMARY); classes.add("border-2"); classes.add(TextColor.PRIMARY);
            }
            case COMPLETE -> {
                classes.add(Background.PRIMARY); classes.add(BorderColor.PRIMARY); classes.add(TextColor.PRIMARY_CONTRAST);
            }
            case ERROR -> {
                classes.add(Background.ERROR); classes.add(BorderColor.ERROR); classes.add(TextColor.ERROR_CONTRAST);
            }
            case INACTIVE -> {
                classes.add(Background.BASE); classes.add(BorderColor.CONTRAST_30); classes.add(TextColor.SECONDARY);
            }
        }
        return classes;
    }

    private static List<String> labelClasses(State state, boolean small, Stepper.Orientation orientation) {
        List<String> classes = new ArrayList<>(List.of(FontWeight.MEDIUM));
        if (orientation == Stepper.Orientation.HORIZONTAL) {
            classes.add("lg:overflow-ellipsis"); classes.add("lg:overflow-hidden"); classes.add("lg:whitespace-nowrap");
        }
        if (small) classes.add(FontSize.SMALL);
        switch (state) {
            case ACTIVE -> classes.add(TextColor.PRIMARY);
            case COMPLETE -> classes.add(TextColor.BODY);
            case ERROR -> classes.add(TextColor.ERROR);
            case INACTIVE -> classes.add(TextColor.SECONDARY);
        }
        return classes;
    }

    private static List<String> descriptionClasses(boolean small, Stepper.Orientation orientation) {
        List<String> classes = new ArrayList<>(List.of(TextColor.SECONDARY));
        if (orientation == Stepper.Orientation.HORIZONTAL) {
            classes.add("lg:overflow-ellipsis"); classes.add("lg:overflow-hidden"); classes.add("lg:whitespace-nowrap");
        }
        classes.add(small ? FontSize.XSMALL : FontSize.SMALL);
        return classes;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        setState(this.link.getHref().equals(event.getLocation().getPath()) ? State.ACTIVE : State.INACTIVE);
    }

    public String getHref() {
        return this.link.getHref();
    }

    public Class<? extends Component> getRouteClass() {
        return routeClass;
    }

    public Step getPrevStep() {
        return prev;
    }

    public void setPrevStep(Step previous) {
        this.prev = previous;
    }

    public Step getNextStep() {
        return next;
    }

    public void setNextStep(Step next) {
        this.next = next;
    }
}
