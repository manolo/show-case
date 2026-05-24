package com.example.application.views.datagrid;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.SelectionMode;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.gridpro.GridPro;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.NumberRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.signals.local.ValueSignal;
import jakarta.annotation.security.PermitAll;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Data Grid")
@Route("data-grid")
@PermitAll
@Menu(order = 5, icon = LineAwesomeIconUrl.TH_SOLID)
public class DataGridView extends Div {

    private GridPro<Client> grid;
    private List<Client> clients;

    private final ValueSignal<String> clientFilter = new ValueSignal<>("");
    private final ValueSignal<String> amountFilter = new ValueSignal<>("");
    private final ValueSignal<String> statusFilter = new ValueSignal<>("");
    private final ValueSignal<LocalDate> dateFilter = new ValueSignal<>(null);

    private Grid.Column<Client> clientColumn;
    private Grid.Column<Client> amountColumn;
    private Grid.Column<Client> statusColumn;
    private Grid.Column<Client> dateColumn;

    public DataGridView() {
        addClassName("data-grid-view");
        setSizeFull();
        createGrid();
        add(grid);
    }

    private void createGrid() {
        createGridComponent();
        addColumnsToGrid();
        addFiltersToGrid();
    }

    private void createGridComponent() {
        grid = new GridPro<>();
        grid.setSelectionMode(SelectionMode.MULTI);
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COLUMN_BORDERS);
        grid.setHeight("100%");

        clients = getClients();
        Signal<List<Client>> filtered = Signal.computed(() -> clients.stream()
                .filter(c -> StringUtils.containsIgnoreCase(c.getClient(), clientFilter.get()))
                .filter(c -> StringUtils.containsIgnoreCase(Double.toString(c.getAmount()), amountFilter.get()))
                .filter(c -> statusFilter.get().isEmpty() || StringUtils.equals(c.getStatus(), statusFilter.get()))
                .filter(c -> dateFilter.get() == null || dateFilter.get().equals(LocalDate.parse(c.getDate())))
                .toList());
        Signal.effect(grid, () -> grid.setItems(filtered.get()));
    }

    private void addColumnsToGrid() {
        createClientColumn();
        createAmountColumn();
        createStatusColumn();
        createDateColumn();
    }

    private void createClientColumn() {
        clientColumn = grid.addColumn(new ComponentRenderer<>(client -> {
            HorizontalLayout hl = new HorizontalLayout();
            hl.setAlignItems(Alignment.CENTER);
            Image img = new Image(client.getImg(), "");
            Span span = new Span();
            span.setClassName("name");
            span.setText(client.getClient());
            hl.add(img, span);
            return hl;
        })).setComparator(client -> client.getClient()).setHeader("Client");
    }

    private void createAmountColumn() {
        amountColumn = grid
                .addEditColumn(Client::getAmount,
                        new NumberRenderer<>(client -> client.getAmount(), NumberFormat.getCurrencyInstance(Locale.US)))
                .text((item, newValue) -> item.setAmount(Double.parseDouble(newValue)))
                .setComparator(client -> client.getAmount()).setHeader("Amount");
    }

    private void createStatusColumn() {
        statusColumn = grid.addEditColumn(Client::getClient, new ComponentRenderer<>(client -> {
            Span span = new Span();
            span.setText(client.getStatus());
            span.getElement().setAttribute("theme", "badge " + client.getStatus().toLowerCase());
            return span;
        })).select((item, newValue) -> item.setStatus(newValue), Arrays.asList("Pending", "Success", "Error"))
                .setComparator(client -> client.getStatus()).setHeader("Status");
    }

    private void createDateColumn() {
        dateColumn = grid
                .addColumn(new LocalDateRenderer<>(client -> LocalDate.parse(client.getDate()),
                        () -> DateTimeFormatter.ofPattern("M/d/yyyy")))
                .setComparator(client -> client.getDate()).setHeader("Date").setWidth("180px").setFlexGrow(0);
    }

    private void addFiltersToGrid() {
        HeaderRow filterRow = grid.appendHeaderRow();

        TextField clientField = filterTextField();
        clientField.bindValue(clientFilter, clientFilter::set);
        filterRow.getCell(clientColumn).setComponent(clientField);

        TextField amountField = filterTextField();
        amountField.bindValue(amountFilter, amountFilter::set);
        filterRow.getCell(amountColumn).setComponent(amountField);

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.setItems(Arrays.asList("Pending", "Success", "Error"));
        statusBox.setPlaceholder("Filter");
        statusBox.setClearButtonVisible(true);
        statusBox.setWidth("100%");
        statusBox.bindValue(statusFilter.map(s -> s.isEmpty() ? null : s), v -> statusFilter.set(v == null ? "" : v));
        filterRow.getCell(statusColumn).setComponent(statusBox);

        DatePicker datePicker = new DatePicker();
        datePicker.setPlaceholder("Filter");
        datePicker.setClearButtonVisible(true);
        datePicker.setWidth("100%");
        datePicker.bindValue(dateFilter, dateFilter::set);
        filterRow.getCell(dateColumn).setComponent(datePicker);
    }

    private TextField filterTextField() {
        TextField field = new TextField();
        field.setPlaceholder("Filter");
        field.setClearButtonVisible(true);
        field.setWidth("100%");
        field.setValueChangeMode(ValueChangeMode.EAGER);
        return field;
    }

    private List<Client> getClients() {
        return Arrays.asList(
                createClient(4957, "https://randomuser.me/api/portraits/women/42.jpg", "Amarachi Nkechi", 47427.0,
                        "Success", "2019-05-09"),
                createClient(675, "https://randomuser.me/api/portraits/women/24.jpg", "Bonelwa Ngqawana", 70503.0,
                        "Success", "2019-05-09"),
                createClient(6816, "https://randomuser.me/api/portraits/men/42.jpg", "Debashis Bhuiyan", 58931.0,
                        "Success", "2019-05-07"),
                createClient(5144, "https://randomuser.me/api/portraits/women/76.jpg", "Jacqueline Asong", 25053.0,
                        "Pending", "2019-04-25"),
                createClient(9800, "https://randomuser.me/api/portraits/men/24.jpg", "Kobus van de Vegte", 7319.0,
                        "Pending", "2019-04-22"),
                createClient(3599, "https://randomuser.me/api/portraits/women/94.jpg", "Mattie Blooman", 18441.0,
                        "Error", "2019-04-17"),
                createClient(3989, "https://randomuser.me/api/portraits/men/76.jpg", "Oea Romana", 33376.0, "Pending",
                        "2019-04-17"),
                createClient(1077, "https://randomuser.me/api/portraits/men/94.jpg", "Stephanus Huggins", 75774.0,
                        "Success", "2019-02-26"),
                createClient(8942, "https://randomuser.me/api/portraits/men/16.jpg", "Torsten Paulsson", 82531.0,
                        "Pending", "2019-02-21"));
    }

    private Client createClient(int id, String img, String client, double amount, String status, String date) {
        Client c = new Client();
        c.setId(id);
        c.setImg(img);
        c.setClient(client);
        c.setAmount(amount);
        c.setStatus(status);
        c.setDate(date);

        return c;
    }
};