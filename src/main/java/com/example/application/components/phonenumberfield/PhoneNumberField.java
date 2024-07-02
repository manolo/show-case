package com.example.application.components.phonenumberfield;

import com.vaadin.flow.component.HasSize;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;

public class PhoneNumberField extends CustomField<PhoneNumberField.PhoneNumber> implements HasSize {

    public record PhoneNumber(String countryCode, Integer areaCode, Integer subscriber) {
    };

    private String[] countryCodes = new String[]{"+354", "+91", "+62", "+98", "+964", "+353", "+44", "+972", "+39",
            "+225"};

    private Select<String> countryCode;

    private IntegerField areaCode;

    private IntegerField subscriber;

    public PhoneNumberField() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSpacing(true);

        countryCode = new Select<>();
        countryCode.setItems(countryCodes);
        countryCode.setWidth("120px");
        countryCode.setPlaceholder("Country");

        areaCode = new IntegerField();
        areaCode.setWidth("120px");
        areaCode.setPlaceholder("Area");

        subscriber = new IntegerField();
        subscriber.setPlaceholder("Subscriber");

        horizontalLayout.add(countryCode, areaCode, subscriber);
        horizontalLayout.setFlexGrow(1.0, subscriber);

        add(horizontalLayout);
    }

    @Override
    protected PhoneNumber generateModelValue() {
        return new PhoneNumber(countryCode.getValue(), areaCode.getValue(), subscriber.getValue());
    }

    @Override
    protected void setPresentationValue(PhoneNumber value) {
        countryCode.setValue(value.countryCode);
        areaCode.setValue(value.areaCode);
        subscriber.setValue(value.subscriber);
    }

}
