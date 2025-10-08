package com.example.application.views.wizard;

import com.example.application.components.stepper.Step;
import com.example.application.components.stepper.Stepper.Orientation;
import com.example.application.components.stepper.Wizard;
import com.example.application.views.MainLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.ParentLayout;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;

@PageTitle("Wizard")
@RoutePrefix(value = "wizard")
@ParentLayout(MainLayout.class)
public class CheckoutWizard extends Wizard {

    public CheckoutWizard() {
        super(Orientation.HORIZONTAL, new Step("Step 1", CheckoutStep1View.class),
                        new Step("Step 2", CheckoutStep2View.class),
                new Step("Step 3", CheckoutStep3View.class), new Step("Step 4", CheckoutStep4View.class));
        addClassNames(MaxWidth.SCREEN_MEDIUM);
    }
}
