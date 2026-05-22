package com.example.application.views;

import java.util.List;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.SvgIcon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.signals.local.ValueSignal;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * The main view is a top-level placeholder for other views.
 */
@Layout
@AnonymousAllowed
@Uses(Icon.class)
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private final ValueSignal<String> title = new ValueSignal<>("");
    private final ValueSignal<Boolean> dark = new ValueSignal<>(false);
    private final AuthenticationContext authenticationContext;

    public MainLayout(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        H1 viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        viewTitle.bindText(title);

        Button darkToggle = new Button(VaadinIcon.MOON.create());
        darkToggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        darkToggle.setAriaLabel("Toggle dark mode");
        darkToggle.addClickListener(e -> dark.update(v -> !v));

        addToNavbar(true, toggle, viewTitle, darkToggle);
    }

    private void addDrawerContent() {
        Span appName = new Span("Vaadin ShowCase");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);
        Scroller scroller = new Scroller(createNavigation());

        Anchor logout = new Anchor("", "logout");
        logout.getElement().addEventListener("click", e -> authenticationContext.logout());
        addToDrawer(header, scroller, createFooter(), new VerticalLayout(logout));
    }


    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        SideNavItem startViews = new SideNavItem("📁 Start views");
        startViews.setExpanded(false);
        SideNavItem customViews = new SideNavItem("📁 Custom views");
        customViews.setExpanded(false);
        nav.addItem(startViews, customViews);

        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        menuEntries.forEach(entry -> {
            SideNavItem parent = entry.order() != null && entry.order() < 50 ? startViews : customViews;
            if (entry.icon() != null) {
                parent.addItem(new SideNavItem(entry.title(), entry.path(), new SvgIcon(entry.icon())));
            } else {
                parent.addItem(new SideNavItem(entry.title(), entry.path()));
            }
        });

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void onAttach(com.vaadin.flow.component.AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI.getCurrent().getElement().getThemeList().bind("dark", dark);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        title.set(MenuConfiguration.getPageHeader(getContent()).orElse(""));
    }
}
