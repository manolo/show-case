package com.example.application.views;


import org.vaadin.lineawesome.LineAwesomeIcon;

import com.example.application.views.about.AboutView;
import com.example.application.views.addressform.AddressFormView;
import com.example.application.views.chat.ChatView;
import com.example.application.views.checkoutform.CheckoutFormView;
import com.example.application.views.creditcardform.CreditCardFormView;
import com.example.application.views.crud.CrudView;
import com.example.application.views.customlayout.CustomLayoutView;
import com.example.application.views.dashboard.DashboardView;
import com.example.application.views.data.DataView;
import com.example.application.views.forum.ForumView;
import com.example.application.views.gridedit.GridEditView;
import com.example.application.views.grideditfilterpaginated.GridEditPaginatedView;
import com.example.application.views.grideditfilterpaginatedfilter.GridEditPaginatedFilterView;
import com.example.application.views.gridwithfilters.GridwithFiltersView;
import com.example.application.views.gridwithfiltersrest.GridwithFiltersRestView;
import com.example.application.views.hello.HelloView;
import com.example.application.views.imagegallery.ImageGalleryView;
import com.example.application.views.map.MapView;
import com.example.application.views.masterdetail.MasterDetailView;
import com.example.application.views.masterdetailresponsive.MasterDetailResponsiveView;
import com.example.application.views.pageeditor.PageEditorView;
import com.example.application.views.personform.PersonFormView;
import com.example.application.views.spreadsheet.SpreadsheetView;
import com.example.application.views.wizard.CheckoutStep1View;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * The main view is a top-level placeholder for other views.
 */

// Use these if ama.css comes from a customized artifact.jar in the
// folder src//main/resources/META-INF/resources/frontend/ama.css
// CssImport will bundle the css with the js bundle in compile time
//@CssImport("ama.css")
// StyleSheet will request the css to the vaadin app in runtime time
//@StyleSheet("frontend/ama.css")

// This is a better option the ama.css is hosted in a CDN
// All apps are updated automatically without having to compile
//@StyleSheet("https://ama.es/assets/ama.css")
public class MainLayout extends AppLayout {

    private H1 viewTitle;

    public MainLayout() {
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
        nav.addItem(new SideNavItem("About", AboutView.class, LineAwesomeIcon.FILE.create()));
        nav.addItem(
                new SideNavItem("Custom Layout", CustomLayoutView.class, LineAwesomeIcon.PENCIL_RULER_SOLID.create()));
        nav.addItem(new SideNavItem("Hello", HelloView.class, LineAwesomeIcon.GLOBE_SOLID.create()));
        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, LineAwesomeIcon.CHART_AREA_SOLID.create()));
        nav.addItem(new SideNavItem("Forum", ForumView.class, LineAwesomeIcon.LIST_SOLID.create()));
        nav.addItem(new SideNavItem("Data", DataView.class, LineAwesomeIcon.TH_SOLID.create()));
        nav.addItem(new SideNavItem("Edit - Master-Detail", MasterDetailView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
        nav.addItem(new SideNavItem("Edit - Crud", CrudView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
        nav.addItem(new SideNavItem("Edit - Grid", GridEditView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
        nav.addItem(new SideNavItem("Edit - Grid Paginated", GridEditPaginatedView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
        nav.addItem(new SideNavItem("Edit - Grid Filter Paginated", GridEditPaginatedFilterView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
        nav.addItem(new SideNavItem("Edit - Responsive", MasterDetailResponsiveView.class, LineAwesomeIcon.COLUMNS_SOLID.create()));
        nav.addItem(new SideNavItem("Person Form", PersonFormView.class, LineAwesomeIcon.USER.create()));
        nav.addItem(new SideNavItem("Address Form", AddressFormView.class, LineAwesomeIcon.MAP_MARKER_SOLID.create()));
        nav.addItem(
                new SideNavItem("Credit Card Form", CreditCardFormView.class, LineAwesomeIcon.CREDIT_CARD.create()));
        nav.addItem(new SideNavItem("Map", MapView.class, LineAwesomeIcon.MAP.create()));
        nav.addItem(new SideNavItem("Spreadsheet", SpreadsheetView.class, LineAwesomeIcon.FILE_EXCEL.create()));
        nav.addItem(new SideNavItem("Chat", ChatView.class, LineAwesomeIcon.COMMENTS.create()));
        nav.addItem(new SideNavItem("Page Editor", PageEditorView.class, LineAwesomeIcon.EDIT.create()));
        nav.addItem(new SideNavItem("Image Gallery", ImageGalleryView.class, LineAwesomeIcon.TH_LIST_SOLID.create()));
        nav.addItem(new SideNavItem("Checkout Form", CheckoutFormView.class, LineAwesomeIcon.CREDIT_CARD.create()));
        nav.addItem(
                new SideNavItem("Grid with Filters", GridwithFiltersView.class, LineAwesomeIcon.FILTER_SOLID.create()));
        nav.addItem(
                new SideNavItem("Grid with Filters Rest", GridwithFiltersRestView.class, LineAwesomeIcon.FILTER_SOLID.create()));
        
        nav.addItem(
                new SideNavItem("Steeper", CheckoutStep1View.class, LineAwesomeIcon.MONEY_CHECK_ALT_SOLID.create()));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

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
