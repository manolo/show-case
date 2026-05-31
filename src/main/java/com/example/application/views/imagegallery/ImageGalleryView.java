package com.example.application.views.imagegallery;

import java.util.Comparator;
import java.util.List;

import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.HasStyle;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.OrderedList;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import com.vaadin.flow.theme.lumo.LumoUtility.AlignItems;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.JustifyContent;
import com.vaadin.flow.theme.lumo.LumoUtility.ListStyleType;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.MaxWidth;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Image Gallery")
@Route("image-gallery")
@PermitAll
@Menu(order = 15, icon = LineAwesomeIconUrl.TH_LIST_SOLID)
public class ImageGalleryView extends Main implements HasComponents, HasStyle {

    private record Photo(String title, String url, int popularity, int year) {}

    private static final List<Photo> PHOTOS = List.of(
            new Photo("Snow mountains under stars",
                    "https://images.unsplash.com/photo-1519681393784-d120267933ba?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=750&q=80", 95, 2019),
            new Photo("Snow covered mountain",
                    "https://images.unsplash.com/photo-1512273222628-4daea6e55abb?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=750&q=80", 80, 2018),
            new Photo("River between mountains",
                    "https://images.unsplash.com/photo-1536048810607-3dc7f86981cb?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=375&q=80", 70, 2020),
            new Photo("Milky way on mountains",
                    "https://images.unsplash.com/photo-1515705576963-95cad62945b6?ixlib=rb-1.2.1&ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=750&q=80", 90, 2017),
            new Photo("Mountain with fog",
                    "https://images.unsplash.com/photo-1513147122760-ad1d5bf68cdb?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1000&q=80", 60, 2021),
            new Photo("Mountain at night",
                    "https://images.unsplash.com/photo-1562832135-14a35d25edef?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=815&q=80", 85, 2022));

    private final ValueSignal<String> sortBy = new ValueSignal<>("Popularity");
    private final OrderedList imageContainer = new OrderedList();

    public ImageGalleryView() {
        addClassNames("image-gallery-view");
        addClassNames(MaxWidth.SCREEN_LARGE, Margin.Horizontal.AUTO, Padding.Bottom.LARGE, Padding.Horizontal.LARGE);

        HorizontalLayout container = new HorizontalLayout();
        container.addClassNames(AlignItems.CENTER, JustifyContent.BETWEEN);

        VerticalLayout headerContainer = new VerticalLayout();
        H2 header = new H2("Beautiful photos");
        header.addClassNames(Margin.Bottom.NONE, Margin.Top.XLARGE, FontSize.XXXLARGE);
        Paragraph description = new Paragraph("Royalty free photos and pictures, courtesy of Unsplash");
        description.addClassNames(Margin.Bottom.XLARGE, Margin.Top.NONE, TextColor.SECONDARY);
        headerContainer.add(header, description);

        Select<String> sortSelect = new Select<>();
        sortSelect.setLabel("Sort by");
        sortSelect.setItems("Popularity", "Newest first", "Oldest first");
        sortSelect.bindValue(sortBy, sortBy::set);

        Span sortHint = new Span();
        sortHint.bindText(sortBy.map(s -> "Showing " + PHOTOS.size() + " photos, sorted by " + s.toLowerCase()));
        sortHint.addClassNames(TextColor.SECONDARY, FontSize.SMALL);

        imageContainer.addClassNames(Gap.MEDIUM, Display.GRID, ListStyleType.NONE, Margin.NONE, Padding.NONE);

        // Signal.effect reorders the container children when sortBy changes
        Signal.effect(imageContainer, () -> {
            imageContainer.removeAll();
            Comparator<Photo> comparator = switch (sortBy.get()) {
                case "Newest first" -> Comparator.comparingInt(Photo::year).reversed();
                case "Oldest first" -> Comparator.comparingInt(Photo::year);
                default -> Comparator.comparingInt(Photo::popularity).reversed();
            };
            PHOTOS.stream().sorted(comparator)
                    .forEach(p -> imageContainer.add(new ImageGalleryViewCard(p.title(), p.url())));
        });

        container.add(headerContainer, sortSelect);
        add(container, sortHint, imageContainer);
    }
}
