package com.example.application.views.addons;

import java.time.LocalDate;

import org.vaadin.textfieldformatter.AbstractPhoneFieldFormatter;
import org.vaadin.textfieldformatter.CreditCardFieldFormatter;
import org.vaadin.textfieldformatter.DateFieldFormatter;
import org.vaadin.textfieldformatter.IBANFormatter;
import org.vaadin.textfieldformatter.NumeralFieldFormatter;
import org.vaadin.textfieldformatter.phone.PhoneI18nFieldFormatter;

import com.example.application.components.toggle.ToggleButton;
import com.example.application.views.MainLayout;
import com.vaadin.componentfactory.addons.inputmask.InputMask;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Addons")
@Route(value = "addons", layout = MainLayout.class)
public class AddonsView extends VerticalLayout {
	
	public AddonsView() {
    	H4 h4 = new H4("Text Field Formatter");
    	add(h4);

		TextField textField = new TextField("ES Phone");
		textField.setPlaceholder("+34 ...");
		new PhoneI18nFieldFormatter(AbstractPhoneFieldFormatter.REGION_ES).extend(textField);
		add(textField);
		
		textField = new TextField("ES Numeral");
		new NumeralFieldFormatter(".", ",", 3).extend(textField);
		add(textField);
		
		TextField cc = new TextField("CC formatter");
		CreditCardFieldFormatter formatter = new CreditCardFieldFormatter();
		formatter.addCreditCardChangedListener(e -> {
			cc.setHelperText(e.getCreditCardType().name());
		});
		formatter.extend(cc);
		add(cc);
		
		
		textField = new TextField("Date");
		textField.setHelperText("format yyyy-MM-dd, limits 2000-01-01  2019-09-03");
		new DateFieldFormatter.Builder()
		.datePattern("yyyyMMdd")
		.delimiter("-")
		.dateMin(LocalDate.of(2000, 01, 01))
		.dateMax(LocalDate.of(2019, 9, 3))
		.build().extend(textField);
		add(textField);
		
		textField = new TextField("IBAN");
		IBANFormatter.fromIBANLength(18).extend(textField);
		add(textField);
		
		h4 = new H4("Toggle Button");
    	add(h4);

    	// Discouraged, use a custom component extending checkbox with custom css
		ToggleButton toggle = new ToggleButton("Toggle (disabled)");
		toggle.setEnabled(false);
		add(toggle);
		
		toggle = new ToggleButton("Toggle");
		Div message = new Div();
		toggle.addValueChangeListener(evt -> message.setText(
		        String.format("Toggle button value changed from '%s' to '%s'",
		        	       evt.getOldValue(), evt.getValue())));
		add(toggle);
		
        // Recommended way, maintain only the custom css, and change the class name		
		Checkbox checkbox = new Checkbox("Toggle checkbox");
		checkbox.addClassName("toggle");
		add(checkbox);
		
		h4 = new H4("Input Mask");
    	add(h4);
		textField= new TextField("Phone with inputMask");
		textField.setPlaceholder("(+00) 000-00-00-00");
		new InputMask("(+00) 000-00-00-00").extend(textField);
		add(textField);

		textField = new TextField("Date witn inputMask");
		textField.setPlaceholder("00/00/0000");
		new InputMask("00/00/0000").extend(textField);
		add(textField);

	}

}
