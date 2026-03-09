package com.example.application.views.addons;

// TODO: Uncomment textfieldformatter imports when the addon supports Vaadin 25
// (currently uses elemental.json.JsonValue which was replaced by Jackson in Vaadin 25)
// import org.vaadin.textfieldformatter.AbstractPhoneFieldFormatter;
// import org.vaadin.textfieldformatter.CreditCardFieldFormatter;
// import org.vaadin.textfieldformatter.DateFieldFormatter;
// import org.vaadin.textfieldformatter.IBANFormatter;
// import org.vaadin.textfieldformatter.NumeralFieldFormatter;
// import org.vaadin.textfieldformatter.phone.PhoneI18nFieldFormatter;

import com.example.application.components.toggle.ToggleButton;
import com.example.application.views.MainLayout;
import com.vaadin.componentfactory.addons.inputmask.InputMask;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;

@PageTitle("Addons")
@Route(value = "addons", layout = MainLayout.class)
@PermitAll
@Menu
public class AddonsView extends Div {

    public AddonsView() {
        addClassNames(Display.FLEX, FlexDirection.COLUMN, Height.FULL);

        Main content = new Main();
        content.addClassNames(Display.GRID, MaxWidth.SCREEN_SMALL, JustifyContent.START, Padding.Top.XLARGE, Padding.Horizontal.XLARGE);
        add(content);

        // TODO: textfieldformatter addon is not compatible with Vaadin 25
        // (uses elemental.json API removed in favor of Jackson)
        content.add(new H3("Text Field Formatter"));
        content.add(new Paragraph("Disabled: textfieldformatter addon pending Vaadin 25 compatibility"));

        content.add(new H3("Toggle Button"));

        // Discouraged, use a custom component extending checkbox with custom css
        ToggleButton toggle = new ToggleButton("Toggle (extends checkbox)");
        Div message = new Div();
        toggle.addValueChangeListener(evt -> message.setText(
                String.format("Toggle button value changed from '%s' to '%s'",
                        evt.getOldValue(), evt.getValue())));
        content.add(toggle);

        toggle = new ToggleButton("Toggle disabled (extends checkbox)");
        toggle.setEnabled(false);
        content.add(toggle);

        // Recommended way, maintain only the custom css, and change the class name
        Checkbox checkbox = new Checkbox("Toggle (checkbox with css)");
        checkbox.addClassName("toggle");
        content.add(checkbox);

        checkbox = new Checkbox("Toggle disabled (checkbox with css)");
        checkbox.setEnabled(false);
        checkbox.addClassName("toggle");
        content.add(checkbox);

        content.add(new H3("Input Mask"));

        TextField textField = new TextField("Phone with inputMask");
        textField.setPlaceholder("(+00) 000-00-00-00");
        new InputMask("(+00) 000-00-00-00").extend(textField);
        content.add(textField);

        textField = new TextField("Date with inputMask");
        textField.setPlaceholder("00/00/0000");
        new InputMask("00/00/0000").extend(textField);
        content.add(textField);

    }

}
