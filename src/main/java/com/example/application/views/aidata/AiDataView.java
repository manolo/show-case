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
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.messages.MessageInput;
import com.vaadin.flow.component.messages.MessageList;
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

    private static final String CHART_PROMPT = """
            You help users explore the SamplePerson H2 database with charts.
            Use the schema provided by the tools, generate plain SELECT statements,
            and pick a sensible chart type (column, pie, line, areaspline) plus
            categorical x-axis and numeric y-axis fields from the result.
            IMPORTANT: H2 SQL dialect rules:
            - Never use reserved words as column aliases (value, year, count, key,
              order, group, etc.). Use neutral names like label, count_value, total,
              measure, amount instead.
            - Always alias COUNT(*) and other aggregates with a non-reserved name.
            Refuse anything that is not a read-only question over this database.
            """;

    private static final String GRID_PROMPT = """
            You help users explore the SamplePerson H2 database with tabular data.
            Use the schema provided by the tools, generate plain SELECT statements,
            and return up to 50 rows with the most relevant columns.
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
                        + "to render either a chart or a grid. Switch tabs to pick the visualisation; each tab has its own AIOrchestrator wired to the matching controller. "
                        + "Try \"How many people per role?\" on the Grid tab or \"Show users per role as a pie chart\" on the Chart tab.");
        subtitle.addClassNames(TextColor.SECONDARY, Margin.Top.NONE);
        add(title, subtitle);

        if (apikey == null || apikey.isBlank()) {
            add(new Span("Set OPENAI_API_KEY in the environment to enable the AI orchestrator."));
            return;
        }

        ChatClient chatClient = chatClientBuilder.build();
        Div chartTab = buildOrchestratedTab(new SpringAILLMProvider(chatClient), databaseProvider, true);
        Div gridTab = buildOrchestratedTab(new SpringAILLMProvider(chatClient), databaseProvider, false);
        gridTab.setVisible(false);

        Tabs tabs = new Tabs(new Tab("Chart"), new Tab("Grid"));
        tabs.addSelectedChangeListener(e -> {
            boolean chart = tabs.getSelectedIndex() == 0;
            chartTab.setVisible(chart);
            gridTab.setVisible(!chart);
        });

        add(tabs, chartTab, gridTab);
        expand(chartTab);
        expand(gridTab);
    }

    private Div buildOrchestratedTab(LLMProvider llm, SamplePersonDatabaseProvider databaseProvider, boolean chartMode) {
        MessageInput input = new MessageInput();
        input.setWidthFull();
        MessageList messages = new MessageList();
        messages.setHeight("240px");
        messages.setWidthFull();

        Div visualization = new Div();
        visualization.setSizeFull();

        AIOrchestrator.Builder builder = AIOrchestrator.builder(llm, chartMode ? CHART_PROMPT : GRID_PROMPT)
                .withInput(input)
                .withMessageList(messages);

        if (chartMode) {
            Chart chart = new Chart(ChartType.COLUMN);
            chart.setHeight("360px");
            visualization.add(chart);
            builder.withController(new ChartAIController(chart, databaseProvider));
        } else {
            Grid<AIDataRow> grid = new Grid<>(AIDataRow.class, false);
            grid.setHeight("360px");
            visualization.add(grid);
            builder.withController(new GridAIController(grid, databaseProvider));
        }
        builder.build();

        Div tabBody = new Div(new HorizontalLayout(visualization), messages, input);
        tabBody.setSizeFull();
        return tabBody;
    }
}
