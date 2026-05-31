package com.example.application.views.wizard;

import org.springframework.stereotype.Component;

import com.example.application.data.checkout.CreditCard;
import com.example.application.data.checkout.PersonalDetails;
import com.example.application.data.checkout.ShippingAddress;
import com.vaadin.flow.signals.local.ValueSignal;
import com.vaadin.flow.spring.annotation.VaadinSessionScope;

/**
 * Session-scoped holder for the wizard's three step beans. Each step view
 * shares the same three signals via constructor injection, so writes from one
 * step are immediately visible to any other component bound to them — see
 * {@link CheckoutStep4View} for a live recap.
 * <p>
 * The wrapped beans are mutable, so step views call
 * {@link ValueSignal#modify(java.util.function.Consumer)} after every binder
 * value change to notify subscribers that the in-place mutation is done.
 */
@Component
@VaadinSessionScope
public class CheckoutFormSignal {

    private final ValueSignal<PersonalDetails> personalDetails = new ValueSignal<>(new PersonalDetails());
    private final ValueSignal<ShippingAddress> shippingAddress = new ValueSignal<>(new ShippingAddress());
    private final ValueSignal<CreditCard> creditCard = new ValueSignal<>(new CreditCard());

    public ValueSignal<PersonalDetails> personalDetails() {
        return personalDetails;
    }

    public ValueSignal<ShippingAddress> shippingAddress() {
        return shippingAddress;
    }

    public ValueSignal<CreditCard> creditCard() {
        return creditCard;
    }
}
