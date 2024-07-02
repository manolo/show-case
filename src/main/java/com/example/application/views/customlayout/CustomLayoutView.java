package com.example.application.views.customlayout;

import com.example.application.components.phonenumberfield.PhoneNumberField;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.formlayout.FormLayout.ResponsiveStep;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

@PageTitle("Custom Layout")
@Route(value = "", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class CustomLayoutView extends Composite<VerticalLayout> {

    public CustomLayoutView() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout layoutColumn2 = new VerticalLayout();
        FormLayout formLayout3Col = new FormLayout();
        TextField textField = new TextField();
        TextField textField2 = new TextField();
        TextField textField3 = new TextField();
        EmailField emailField = new EmailField();
        PhoneNumberField phoneNumber = new PhoneNumberField();
        Paragraph textSmall = new Paragraph();
        RichTextEditor richTextEditor = new RichTextEditor();
        VerticalLayout layoutColumn3 = new VerticalLayout();
        Anchor link = new Anchor();
        Anchor link2 = new Anchor();
        Anchor link3 = new Anchor();
        Hr hr = new Hr();
        VerticalLayout layoutColumn4 = new VerticalLayout();
        RouterLink routerLink = new RouterLink();
        HorizontalLayout layoutRow2 = new HorizontalLayout();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.getStyle().set("flex-grow", "1");
        layoutColumn2.setWidth("100%");
        layoutColumn2.getStyle().set("flex-grow", "1");
        formLayout3Col.setWidth("100%");
        formLayout3Col.setResponsiveSteps(new ResponsiveStep("0", 1), new ResponsiveStep("250px", 2),
                new ResponsiveStep("500px", 3));
        textField.setLabel("Name");
        textField.setWidth("min-content");
        textField2.setLabel("Surname");
        textField2.setWidth("min-content");
        textField3.setLabel("2º Surname");
        textField3.setWidth("min-content");
        emailField.setLabel("Email");
        emailField.setWidth("min-content");
        phoneNumber.setLabel("Phone number");
        phoneNumber.setWidth("min-content");
        textSmall.setText("Biography");
        textSmall.setWidth("100%");
        textSmall.getStyle().set("font-size", "var(--lumo-font-size-xs)");
        richTextEditor.setWidth("100%");
        richTextEditor.getStyle().set("flex-grow", "1");
        layoutColumn3.getStyle().set("flex-grow", "1");
        link.setText("Google");
        link.setHref("#");
        link.setWidth("min-content");
        link2.setText("Vaadin");
        link2.setHref("#");
        link2.setWidth("min-content");
        link3.setText("Github");
        link3.setHref("#");
        link3.setWidth("min-content");
        layoutColumn4.setWidthFull();
        layoutColumn3.setFlexGrow(1.0, layoutColumn4);
        layoutColumn4.setWidth("100%");
        layoutColumn4.getStyle().set("flex-grow", "1");
        layoutColumn4.setJustifyContentMode(JustifyContentMode.END);
        layoutColumn4.setAlignItems(Alignment.CENTER);
        routerLink.setText("Hello");
        routerLink.setRoute(CustomLayoutView.class);
        routerLink.setWidth("min-content");
        layoutRow2.addClassName(Gap.MEDIUM);
        layoutRow2.setWidth("100%");
        layoutRow2.setHeight("min-content");
        getContent().add(layoutRow);
        layoutRow.add(layoutColumn2);
        layoutColumn2.add(formLayout3Col);
        formLayout3Col.add(textField);
        formLayout3Col.add(textField2);
        formLayout3Col.add(textField3);
        formLayout3Col.add(emailField);
        formLayout3Col.add(phoneNumber);
        layoutColumn2.add(textSmall);
        layoutColumn2.add(richTextEditor);
        layoutRow.add(layoutColumn3);
        layoutColumn3.add(link);
        layoutColumn3.add(link2);
        layoutColumn3.add(link3);
        layoutColumn3.add(hr);
        layoutColumn3.add(layoutColumn4);
        layoutColumn4.add(routerLink);
        getContent().add(layoutRow2);
    }
}
