package com.example.application.views.about;

import java.util.Locale;
import java.util.stream.Collectors;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.internal.LocaleUtil;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.Version;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;

@PageTitle("About")
@Route(value = "", layout = MainLayout.class)
@Menu
public class AboutView extends VerticalLayout {

    public AboutView() throws InterruptedException {
        setSpacing(false);

        Image img = new Image("images/empty-plant.png", "placeholder plant");
        img.setWidth("200px");
        add(img);

        H2 header = new H2("This place intentionally left empty");
        header.addClassNames(Margin.Top.XLARGE, Margin.Bottom.MEDIUM);
        add(header);
        add(new Paragraph("It’s a place where you can grow your own UI 🤗"));

        String localeDefault = Locale.getDefault().toLanguageTag();
        String localeHeaders = VaadinService.getCurrentRequest().getHeader("Accept-Language").replaceFirst(",.*$", "");
        String localeSystem = System.getProperty("user.language");
        String uiLocale = getLocale().toLanguageTag();
        I18NProvider prov = LocaleUtil.getI18NProvider().orElse(null);
        String providedLocales = prov == null ? null : prov
                .getProvidedLocales().stream().map(Locale::toLanguageTag).collect(Collectors.joining(", "));
        add(new Div("Version: " + Version.getFullVersion()));
        add(new Div(String.format("localeDefault: %s, localeHeaders: %s, localeSystem: %s, uiLocale: %s, providedLocales: %s", localeDefault, localeHeaders, localeSystem, uiLocale, providedLocales)));
        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");

        // Thread.sleep(30000);
    }
}
