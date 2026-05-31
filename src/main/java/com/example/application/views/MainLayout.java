package com.example.application.views;

import java.util.List;
import java.util.Set;

import com.vaadin.flow.component.Component;
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
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.aura.Aura;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * The main view is a top-level placeholder for other views.
 */
@Layout
@AnonymousAllowed
@Uses(Icon.class)
public class MainLayout extends AppLayout {

    private final ValueSignal<Boolean> dark = new ValueSignal<>(false);
    private final ValueSignal<Boolean> aura = new ValueSignal<>(false);
    private Registration auraStylesheet;
    private final H1 viewTitle = new H1();
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

        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE, LumoUtility.Flex.GROW);

        Button darkToggle = new Button(VaadinIcon.MOON.create());
        darkToggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        darkToggle.setAriaLabel("Toggle dark mode");
        darkToggle.addClickListener(e -> dark.update(v -> !v));

        Button themeToggle = new Button(VaadinIcon.PALETTE.create());
        themeToggle.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        themeToggle.setAriaLabel("Toggle Lumo / Aura theme");
        themeToggle.getElement().setProperty("title", "Toggle Lumo / Aura theme");
        themeToggle.addClickListener(e -> aura.update(v -> !v));

        addToNavbar(true, toggle, viewTitle, themeToggle, darkToggle);
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


    /**
     * First path segment of each route that mirrors a start.vaadin.com view.
     * Originals are kept under the {@code -classic} suffix where a Signals
     * counterpart exists, and under the plain start path otherwise.
     */
    private static final Set<String> START_ROUTES = Set.of(
            "empty", "hello-world-classic", "dashboard-classic", "feed-classic",
            "data-grid-classic", "master-detail-classic", "collaborative-master-detail-classic",
            "person-form-classic", "address-form-classic", "credit-card-form-classic", "map-classic",
            "spreadsheet-classic", "chat-classic", "page-editor-classic", "image-gallery-classic",
            "checkout-form-classic", "grid-with-filters-classic", "layout-classic");

    /** Same start views migrated to Vaadin Signals in this showcase. */
    private static final Set<String> START_SIGNALS_ROUTES = Set.of(
            "hello-world", "dashboard", "feed", "data-grid", "master-detail",
            "collaborative-master-detail", "person-form", "address-form",
            "credit-card-form", "map", "spreadsheet", "chat", "page-editor",
            "image-gallery", "checkout-form", "grid-with-filters", "layout");

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        SideNavItem startViews = new SideNavItem("📁 Start views");
        startViews.setExpanded(false);
        SideNavItem signalsViews = new SideNavItem("📁 Start views – Signals");
        signalsViews.setExpanded(false);
        SideNavItem customViews = new SideNavItem("📁 Custom views");
        customViews.setExpanded(false);
        nav.addItem(startViews, signalsViews, customViews);

        List<MenuEntry> menuEntries = MenuConfiguration.getMenuEntries();
        menuEntries.forEach(entry -> {
            String firstSegment = firstSegment(entry.path());
            SideNavItem parent;
            if (START_ROUTES.contains(firstSegment)) {
                parent = startViews;
            } else if (START_SIGNALS_ROUTES.contains(firstSegment)) {
                parent = signalsViews;
            } else {
                parent = customViews;
            }
            if (entry.icon() != null) {
                parent.addItem(new SideNavItem(entry.title(), entry.path(), new SvgIcon(entry.icon())));
            } else {
                parent.addItem(new SideNavItem(entry.title(), entry.path()));
            }
        });

        return nav;
    }

    private static String firstSegment(String path) {
        if (path == null || path.isEmpty()) return "";
        String stripped = path.startsWith("/") ? path.substring(1) : path;
        int slash = stripped.indexOf('/');
        return slash < 0 ? stripped : stripped.substring(0, slash);
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void onAttach(com.vaadin.flow.component.AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();
        ui.getElement().getThemeList().bind("dark", dark);
        viewTitle.bindText(ui.routerStateSignal().map(state -> state.currentView()
                .filter(view -> view instanceof Component)
                .flatMap(view -> MenuConfiguration.getPageHeader((Component) view))
                .orElse("")));
        Signal.effect(this, () -> {
            if (aura.get()) {
                if (auraStylesheet == null) {
                    auraStylesheet = ui.getPage().addStyleSheet(Aura.STYLESHEET);
                }
            } else if (auraStylesheet != null) {
                auraStylesheet.remove();
                auraStylesheet = null;
            }
        });
    }
}
