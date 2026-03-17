package com.example.application.components.tabbedlayout;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Flex;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class TabbedLayout extends Div implements RouterLayout, BeforeEnterObserver, AfterNavigationObserver {

    private final Tabs tabs = new Tabs();
    private final Div content = new Div();
    private final Map<Tab, TabDef> tabDefs = new LinkedHashMap<>();
    private boolean navigating;
    private GridSync<?> gridSync;

    protected TabbedLayout(TabDef... defs) {
        addClassNames(Display.FLEX, FlexDirection.COLUMN, Height.FULL);

        for (TabDef def : defs) {
            Tab tab = new Tab(def.label());
            tabDefs.put(tab, def);
            tabs.add(tab);
        }

        tabs.addSelectedChangeListener(event -> {
            if (!navigating) {
                TabDef def = tabDefs.get(event.getSelectedTab());
                if (def != null) {
                    String url = getNavigationUrl(def);
                    if (url != null) {
                        navigating = true;
                        UI.getCurrent().navigate(url);
                    }
                }
            }
        });

        content.addClassNames(Flex.GROW, Overflow.AUTO);

        add(tabs, content);
    }

    protected <T> void syncGrid(Grid<T> grid, String idPattern, Function<Long, Optional<T>> resolver) {
        this.gridSync = new GridSync<>(grid, Pattern.compile(idPattern), resolver);
    }

    @SuppressWarnings("unchecked")
    protected <T> T getSelectedItem() {
        return gridSync != null ? (T) gridSync.getSelected() : null;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (gridSync != null) {
            gridSync.sync(event.getLocation().getPath());
        }
    }

    @Override
    public void showRouterLayoutContent(HasElement child) {
        content.removeAll();
        if (child != null) {
            content.getElement().appendChild(child.getElement());
        }
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        navigating = true;
        try {
            String path = event.getLocation().getPath();
            for (Map.Entry<Tab, TabDef> entry : tabDefs.entrySet()) {
                if (path.matches(entry.getValue().pathPattern())) {
                    tabs.setSelectedTab(entry.getKey());
                    return;
                }
            }
        } finally {
            navigating = false;
        }
    }

    protected String getNavigationUrl(TabDef tab) {
        return tab.defaultUrl();
    }

    private static class GridSync<T> {
        private final Grid<T> grid;
        private final Pattern idPattern;
        private final Function<Long, Optional<T>> resolver;

        GridSync(Grid<T> grid, Pattern idPattern, Function<Long, Optional<T>> resolver) {
            this.grid = grid;
            this.idPattern = idPattern;
            this.resolver = resolver;
        }

        T getSelected() {
            return grid.asSingleSelect().getValue();
        }

        void sync(String path) {
            T item = grid.asSingleSelect().getValue();
            if (item == null) {
                Matcher matcher = idPattern.matcher(path);
                if (matcher.find()) {
                    try {
                        Long id = Long.valueOf(matcher.group(1));
                        item = resolver.apply(id).orElse(null);
                    } catch (NumberFormatException ex) {
                    }
                }
            }
            if (item != null) {
                grid.asSingleSelect().setValue(item);
                try {
                    grid.scrollToStart();
                    grid.scrollToItem(item);
                } catch (UnsupportedOperationException ex) {
                    // scrollToItem requires ItemIndexProvider for lazy data
                }
            }
        }
    }
}
