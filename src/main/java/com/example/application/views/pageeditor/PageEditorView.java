package com.example.application.views.pageeditor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.vaadin.flow.component.html.DescriptionList;
import com.vaadin.flow.component.html.DescriptionList.Description;
import com.vaadin.flow.component.html.DescriptionList.Term;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Section;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.richtexteditor.RichTextEditorVariant;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import com.vaadin.flow.theme.lumo.LumoUtility.Accessibility;
import com.vaadin.flow.theme.lumo.LumoUtility.Background;
import com.vaadin.flow.theme.lumo.LumoUtility.Border;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderColor;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.Display;
import com.vaadin.flow.theme.lumo.LumoUtility.Flex;
import com.vaadin.flow.theme.lumo.LumoUtility.FlexDirection;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;
import com.vaadin.flow.theme.lumo.LumoUtility.Height;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Overflow;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import jakarta.annotation.security.PermitAll;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Page Editor")
@Route("page-editor")
@PermitAll
@Menu(order = 14, icon = LineAwesomeIconUrl.EDIT)
public class PageEditorView extends Main {

    private static final String SEED_DELTA = "[{\"insert\":\"High quality rich text editor\"},{\"attributes\":{\"header\":2},\"insert\":\"\\n\"},{\"insert\":\"Type to see the modified status, word count, and last modified time on the right react in real time.\\n\"}]";

    private final ValueSignal<String> projectName = new ValueSignal<>("My Project");
    private final ValueSignal<String> content = new ValueSignal<>(SEED_DELTA);
    private final ValueSignal<String> savedContent = new ValueSignal<>(SEED_DELTA);
    private final ValueSignal<LocalDateTime> lastModified = new ValueSignal<>(LocalDateTime.now());

    public PageEditorView() {
        addClassNames(Display.FLEX, Flex.GROW, Height.FULL);

        RichTextEditor editor = new RichTextEditor();
        editor.addClassNames(Border.RIGHT, BorderColor.CONTRAST_10, Flex.GROW);
        editor.addThemeVariants(RichTextEditorVariant.LUMO_NO_BORDER);
        editor.asDelta().setValue(SEED_DELTA);
        editor.asDelta().addValueChangeListener(e -> {
            content.set(e.getValue());
            lastModified.set(LocalDateTime.now());
        });

        add(editor, createSidebar());
    }

    private Section createSidebar() {
        Section sidebar = new Section();
        sidebar.addClassNames(Background.CONTRAST_5, BoxSizing.BORDER, Display.FLEX, FlexDirection.COLUMN,
                Flex.SHRINK_NONE, Overflow.AUTO, Padding.LARGE);
        sidebar.setWidth("256px");

        H2 title = new H2();
        title.bindText(projectName.map(n -> "Project: " + n));
        title.addClassName(Accessibility.SCREEN_READER_ONLY);

        DescriptionList dl = new DescriptionList();
        dl.addClassNames(Display.FLEX, FlexDirection.COLUMN, Gap.LARGE, Margin.Bottom.SMALL, Margin.Top.NONE,
                FontSize.SMALL);

        Description ownerDesc = description("My Name");
        Description createdDesc = description("2021-08-14 14:48");
        Description modifiedDesc = description("");
        modifiedDesc.bindText(lastModified.map(t -> t.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

        // Status badge driven by a computed signal: clean when saved, draft when changed
        Signal<Boolean> dirty = Signal.computed(() -> !content.get().equals(savedContent.peek()));
        Span statusBadge = new Span();
        statusBadge.bindText(dirty.map(d -> d ? "Modified" : "Saved"));
        statusBadge.getElement().getThemeList().add("badge");
        statusBadge.getElement().bindAttribute("theme", dirty.map(d -> d ? "badge error" : "badge success"));

        Span wordCount = new Span();
        wordCount.bindText(content.map(c -> {
            // Strip Quill delta JSON markers and count words
            String text = c.replaceAll("\"[a-zA-Z-]+\":\"[^\"]*\"", "")
                    .replaceAll("[\\{\\}\\[\\]\",:]", " ")
                    .replaceAll("\\s+", " ").trim();
            int count = text.isEmpty() ? 0 : text.split("\\s+").length;
            return count + " words";
        }));
        wordCount.addClassNames(TextColor.SECONDARY, FontSize.SMALL);

        dl.add(item("Owner", ownerDesc), item("Created", createdDesc),
                item("Last modified", modifiedDesc), item("Status", statusBadge), item("Length", wordCount));

        Select<String> select = new Select<>();
        select.setLabel("Project");
        select.setItems("My Project", "Your Project", "Their Project");
        select.bindValue(projectName, projectName::set);

        sidebar.add(title, dl, select);
        return sidebar;
    }

    private Div item(String label, com.vaadin.flow.component.Component value) {
        Term term = new Term(label);
        term.addClassNames(FontWeight.MEDIUM, TextColor.SECONDARY);
        Div wrapper = new Div(term, value);
        if (value instanceof Description d) {
            d.addClassName(Margin.Left.NONE);
        }
        return wrapper;
    }

    private Description description(String value) {
        Description d = new Description(value);
        d.addClassName(Margin.Left.NONE);
        return d;
    }
}
