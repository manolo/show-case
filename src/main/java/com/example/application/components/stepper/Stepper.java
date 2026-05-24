package com.example.application.components.stepper;

import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

public class Stepper extends Nav implements HasTheme {

    private final ValueSignal<Orientation> orientation = new ValueSignal<>(Orientation.VERTICAL);
    private final ValueSignal<Boolean> small = new ValueSignal<>(false);

    private final UnorderedList list;
    private final Step[] steps;

    public Stepper(Step... steps) {
        this.steps = steps;
        for (int i = 0; i < steps.length; i++) {
            if (i > 0) steps[i].setPrevStep(steps[i - 1]);
            if (i < steps.length - 1) steps[i].setNextStep(steps[i + 1]);
        }
        addClassName("stepper");
        this.list = new UnorderedList(steps);
        this.list.addClassNames(Display.FLEX, FlexDirection.COLUMN, Gap.Column.LARGE, ListStyleType.NONE,
                Margin.Vertical.NONE, Padding.Start.NONE);
        add(this.list);

        bindThemeName("horizontal", orientation.map(o -> o == Orientation.HORIZONTAL));
        bindThemeName("vertical", orientation.map(o -> o == Orientation.VERTICAL));
        bindThemeName("small", small);
        this.list.bindClassName("lg:items-center", orientation.map(o -> o == Orientation.HORIZONTAL));
        this.list.bindClassName(FlexDirection.Breakpoint.Large.ROW, orientation.map(o -> o == Orientation.HORIZONTAL));

        Signal.effect(this, () -> {
            Orientation o = orientation.get();
            for (Step s : steps) s.setOrientation(o);
        });
        Signal.effect(this, () -> {
            boolean sm = small.get();
            for (Step s : steps) s.setSmall(sm);
        });
    }

    public Step[] getSteps() {
        return steps;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation.set(orientation);
    }

    public void setSmall(boolean small) {
        this.small.set(small);
    }

    public void setState(Step.State state, Step step) {
        step.setState(state);
    }

    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    public enum Size {
        SMALL,
        MEDIUM
    }
}
