package com.example.application.views.aidata;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import com.vaadin.flow.component.ai.chart.ChartAIController;
import com.vaadin.flow.component.ai.grid.AIDataRow;
import com.vaadin.flow.component.ai.grid.GridAIController;
import com.vaadin.flow.component.ai.orchestrator.AIOrchestrator;
import com.vaadin.flow.component.ai.provider.LLMProvider;
import com.vaadin.flow.component.ai.provider.SpringAILLMProvider;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;

import jakarta.annotation.security.PermitAll;

@PageTitle("Ask your data")
@Route("ai-data")
@PermitAll
@Menu(order = 16, icon = LineAwesomeIconUrl.ROBOT_SOLID)
public class AiDataView extends VerticalLayout {

    private static final String SYSTEM_PROMPT = """
            You help users explore the SamplePerson H2 database.
            Use the schema provided by the tools. Generate plain SELECT statements.
            For chart questions, pick a sensible chart type (column, pie, line, areaspline)
            and choose categorical x-axis and numeric y-axis fields from the result.
            For grid questions, return up to 50 rows with the most relevant columns.
            Refuse anything that is not a read-only question over this database.
            """;

    public AiDataView(ChatClient.Builder chatClientBuilder,
            SamplePersonDatabaseProvider databaseProvider,
            @Value("${spring.ai.openai.apikey:}") String apikey) {
        addClassName("ai-data-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Ask your data");
        Paragraph subtitle = new Paragraph(
                "Type a question in plain language and the LLM uses the SamplePerson schema "
                        + "to render either a chart or a grid. Try \"How many people per role?\" "
                        + "or \"List the 10 youngest people\".");
        subtitle.addClassNames(TextColor.SECONDARY, Margin.Top.NONE);
        add(title, subtitle);

        if (apikey == null || apikey.isBlank()) {
            add(new Span("Set OPENAI_API_KEY in the environment to enable the AI orchestrator."));
            return;
        }

        Chart chart = new Chart(ChartType.COLUMN);
        chart.setHeight("360px");

        Grid<AIDataRow> grid = new Grid<>(AIDataRow.class, false);
        grid.setHeight("360px");

        Tabs visualizationTabs = new Tabs(new Tab("Chart"), new Tab("Grid"));
        Div chartWrapper = new Div(chart);
        chartWrapper.setSizeFull();
        Div gridWrapper = new Div(grid);
        gridWrapper.setSizeFull();
        gridWrapper.setVisible(false);
        visualizationTabs.addSelectedChangeListener(e -> {
            boolean chartTab = visualizationTabs.getSelectedIndex() == 0;
            chartWrapper.setVisible(chartTab);
            gridWrapper.setVisible(!chartTab);
        });

        MessageInput input = new MessageInput();
        input.setWidthFull();
        MessageList messages = new MessageList();
        messages.setHeight("280px");
        messages.setWidthFull();

        LLMProvider llm = new SpringAILLMProvider(chatClientBuilder.build());

        ChartAIController chartController = new ChartAIController(chart, databaseProvider);
        GridAIController gridController = new GridAIController(grid, databaseProvider);

        AIOrchestrator.builder(llm, SYSTEM_PROMPT)
                .withInput(input)
                .withMessageList(messages)
                .withController(chartController)
                .withController(gridController)
                .build();

        HorizontalLayout visualizationRow = new HorizontalLayout(chartWrapper, gridWrapper);
        visualizationRow.setSizeFull();
        VerticalLayout chatColumn = new VerticalLayout(messages, input);
        chatColumn.setPadding(false);
        chatColumn.setSpacing(false);
        chatColumn.setWidthFull();

        add(visualizationTabs, visualizationRow, chatColumn);
        expand(visualizationRow);
    }
}
