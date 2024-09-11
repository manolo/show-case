package com.example.application.components.stepper;

import com.vaadin.flow.component.HasTheme;
import com.vaadin.flow.component.html.Nav;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

public class Stepper extends Nav implements HasTheme {
	
    private Orientation orientation;
    
    private UnorderedList list;
    
    private Step[] steps;

    public Stepper(Step... steps) {
    	this.steps = steps;
        for (int i = 0; i < steps.length; i++) {
			if (i > 0) {
				steps[i].setPrevStep(steps[i-1]);
			}
			if (i < steps.length - 1) {
				steps[i].setNextStep(steps[i+1]);
			}
		}
        addClassName("stepper");
        this.list = new UnorderedList(steps);
        this.list.addClassNames(Display.FLEX, FlexDirection.COLUMN, Gap.Column.LARGE, ListStyleType.NONE,
                Margin.Vertical.NONE, Padding.Start.NONE);
        add(this.list);
        setOrientation(Orientation.VERTICAL);
    }
    
    public Step[] getSteps() {
    	return steps;
    }

    public void setOrientation(Orientation orientation) {
        if (this.orientation != null) {
            removeThemeName(this.orientation.name().toLowerCase());
        }
        addThemeName(orientation.name().toLowerCase());
        this.orientation = orientation;

        if (orientation.equals(Orientation.HORIZONTAL)) {
            this.list.addClassNames("lg:items-center", FlexDirection.Breakpoint.Large.ROW);
        } else {
            this.list.removeClassNames("lg:items-center", FlexDirection.Breakpoint.Large.ROW);
        }

        this.list.getChildren().forEach(component -> ((Step) component).setOrientation(orientation));
    }

    public void setSmall(boolean small) {
        if (small) {
            addThemeName(Size.SMALL.name().toLowerCase());
        } else {
            removeThemeName(Size.SMALL.name().toLowerCase());
        }

        this.list.getChildren().forEach(component -> ((Step) component).setSmall(small));
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
