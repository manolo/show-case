package com.example.application.views.layout;

import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Layout")
@Route("layout")
@PermitAll
@Menu(order = 0, icon = LineAwesomeIconUrl.PENCIL_RULER_SOLID)
@Uses(Icon.class)
public class LayoutView extends Composite<VerticalLayout> {

    private final ValueSignal<SamplePerson> selected = new ValueSignal<>(null);

    public LayoutView(SamplePersonService samplePersonService) {
        HorizontalLayout layoutRow = new HorizontalLayout();
        VerticalLayout buttonsColumn = new VerticalLayout();
        Button buttonPrimary = new Button("Button");
        buttonPrimary.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // Primary button enabled only when a row is selected — Signal.computed drives it.
        buttonPrimary.bindEnabled(selected.map(p -> p != null));
        Button buttonSecondary = new Button("Button");
        Button buttonTertiary = new Button("Button");
        buttonTertiary.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        VerticalLayout gridColumn = new VerticalLayout();
        Grid<SamplePerson> basicGrid = new Grid<>(SamplePerson.class);
        basicGrid.setItemsPageable(samplePersonService::listItems);
        basicGrid.asSingleSelect().addValueChangeListener(e -> selected.set(e.getValue()));

        // Reactive summary banner — bound to the grid selection signal
        Span summary = new Span();
        summary.bindText(Signal.computed(() -> {
            SamplePerson p = selected.get();
            if (p == null) return "Select a person from the grid";
            return "Selected: " + p.getFirstName() + " " + p.getLastName()
                    + " — " + (p.getEmail() == null ? "" : p.getEmail());
        }));
        summary.addClassNames(TextColor.SECONDARY, FontSize.SMALL, Padding.SMALL);

        VerticalLayout menuColumn = new VerticalLayout();
        MenuBar menu = new MenuBar();

        getContent().setHeightFull();
        getContent().setWidthFull();
        getContent().setFlexGrow(1.0, layoutRow);
        layoutRow.setWidthFull();
        layoutRow.addClassName(Gap.MEDIUM);
        buttonsColumn.setHeightFull();
        buttonsColumn.setWidth(null);
        layoutRow.setFlexGrow(1.0, gridColumn);
        gridColumn.setHeightFull();
        gridColumn.setWidth(null);
        menuColumn.setHeightFull();
        menuColumn.setWidth(null);
        setMenuSampleData(menu);

        getContent().add(summary, layoutRow);
        layoutRow.add(buttonsColumn, gridColumn, menuColumn);
        buttonsColumn.add(buttonPrimary, buttonSecondary, buttonTertiary);
        gridColumn.add(basicGrid);
        menuColumn.add(menu);
    }

    private void setMenuSampleData(MenuBar menuBar) {
        menuBar.addThemeVariants(MenuBarVariant.LUMO_ICON);
        MenuItem share = menuBar.addItem(new Text("Share"));
        share.add(new Icon(VaadinIcon.ANGLE_DOWN));
        // Disable Share until a person is selected — using bindEnabled on a MenuItem
        share.bindEnabled(selected.map(p -> p != null));
        share.setTooltipText("Pick a person from the grid first");
        SubMenu shareSubMenu = share.getSubMenu();
        MenuItem onSocialMedia = shareSubMenu.addItem("On social media");
        SubMenu socialMediaSubMenu = onSocialMedia.getSubMenu();
        socialMediaSubMenu.addItem("Facebook");
        socialMediaSubMenu.addItem("Twitter");
        socialMediaSubMenu.addItem("Instagram");
        shareSubMenu.addItem("By email");
        shareSubMenu.addItem("Get Link");

        Span hint = new Span(" Selection drives Share");
        hint.addClassNames(FontSize.XSMALL, FontWeight.LIGHT, TextColor.SECONDARY);
    }
}
