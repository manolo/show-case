package com.example.application.views;

import com.example.application.data.User;
import com.example.application.security.AuthenticatedUser;
import com.example.application.views.about.AboutView;
import com.example.application.views.addressform.AddressFormView;
import com.example.application.views.chat.ChatView;
import com.example.application.views.checkoutform.CheckoutFormView;
import com.example.application.views.creditcardform.CreditCardFormView;
import com.example.application.views.customlayout.CustomLayoutView;
import com.example.application.views.dashboard.DashboardView;
import com.example.application.views.data.DataView;
import com.example.application.views.forum.ForumView;
import com.example.application.views.gridwithfilters.GridwithFiltersView;
import com.example.application.views.hello.HelloView;
import com.example.application.views.imagegallery.ImageGalleryView;
import com.example.application.views.map.MapView;
import com.example.application.views.masterdetail.MasterDetailView;
import com.example.application.views.pageeditor.PageEditorView;
import com.example.application.views.personform.PersonFormView;
import com.example.application.views.spreadsheet.SpreadsheetView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
public class MainLayout extends AppLayout {

    private H1 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        Span appName = new Span("Vaadin ShowCase");
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        if (accessChecker.hasAccess(AboutView.class)) {
            nav.addItem(new SideNavItem("About", AboutView.class, LineAwesomeIcon.FILE.create()));

        }
        if (accessChecker.hasAccess(CustomLayoutView.class)) {
            nav.addItem(new SideNavItem("Custom Layout", CustomLayoutView.class,
                    LineAwesomeIcon.PENCIL_RULER_SOLID.create()));

        }
        if (accessChecker.hasAccess(HelloView.class)) {
            nav.addItem(new SideNavItem("Hello", HelloView.class, LineAwesomeIcon.GLOBE_SOLID.create()));

        }
        if (accessChecker.hasAccess(DashboardView.class)) {
            nav.addItem(new SideNavItem("Dashboard", DashboardView.class, LineAwesomeIcon.CHART_AREA_SOLID.create()));

        }
        if (accessChecker.hasAccess(ForumView.class)) {
            nav.addItem(new SideNavItem("Forum", ForumView.class, LineAwesomeIcon.LIST_SOLID.create()));

        }
        if (accessChecker.hasAccess(DataView.class)) {
            nav.addItem(new SideNavItem("Data", DataView.class, LineAwesomeIcon.TH_SOLID.create()));

        }
        if (accessChecker.hasAccess(MasterDetailView.class)) {
            nav.addItem(
                    new SideNavItem("Master-Detail", MasterDetailView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));

        }
        if (accessChecker.hasAccess(PersonFormView.class)) {
            nav.addItem(new SideNavItem("Person Form", PersonFormView.class, LineAwesomeIcon.USER.create()));

        }
        if (accessChecker.hasAccess(AddressFormView.class)) {
            nav.addItem(
                    new SideNavItem("Address Form", AddressFormView.class, LineAwesomeIcon.MAP_MARKER_SOLID.create()));

        }
        if (accessChecker.hasAccess(CreditCardFormView.class)) {
            nav.addItem(new SideNavItem("Credit Card Form", CreditCardFormView.class,
                    LineAwesomeIcon.CREDIT_CARD.create()));

        }
        if (accessChecker.hasAccess(MapView.class)) {
            nav.addItem(new SideNavItem("Map", MapView.class, LineAwesomeIcon.MAP.create()));

        }
        if (accessChecker.hasAccess(SpreadsheetView.class)) {
            nav.addItem(new SideNavItem("Spreadsheet", SpreadsheetView.class, LineAwesomeIcon.FILE_EXCEL.create()));

        }
        if (accessChecker.hasAccess(ChatView.class)) {
            nav.addItem(new SideNavItem("Chat", ChatView.class, LineAwesomeIcon.COMMENTS.create()));

        }
        if (accessChecker.hasAccess(PageEditorView.class)) {
            nav.addItem(new SideNavItem("Page Editor", PageEditorView.class, LineAwesomeIcon.EDIT.create()));

        }
        if (accessChecker.hasAccess(ImageGalleryView.class)) {
            nav.addItem(
                    new SideNavItem("Image Gallery", ImageGalleryView.class, LineAwesomeIcon.TH_LIST_SOLID.create()));

        }
        if (accessChecker.hasAccess(CheckoutFormView.class)) {
            nav.addItem(new SideNavItem("Checkout Form", CheckoutFormView.class, LineAwesomeIcon.CREDIT_CARD.create()));

        }
        if (accessChecker.hasAccess(GridwithFiltersView.class)) {
            nav.addItem(new SideNavItem("Grid with Filters", GridwithFiltersView.class,
                    LineAwesomeIcon.FILTER_SOLID.create()));

        }

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName());
            StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
