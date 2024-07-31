package com.example.application.views.gridwithfiltersrest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.example.application.components.filter.HasFilterParameters;
import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonServiceRest;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

@Service
public class SamplePersonRestDataProvider extends AbstractBackEndDataProvider<SamplePerson, HasFilterParameters> {

    private final SamplePersonServiceRest service;

    public SamplePersonRestDataProvider(SamplePersonServiceRest service) {
        this.service = service;
    }

    private void getSortParameters(List<QuerySortOrder> sorts, HashMap<String, List<String>> map) {
        map.put("sort", sorts.stream().map(
                order -> order.getSorted() + "," + (order.getDirection() == SortDirection.ASCENDING ? "asc" : "desc"))
                .collect(Collectors.toList()));
    }

    @Override
    protected Stream<SamplePerson> fetchFromBackEnd(Query<SamplePerson, HasFilterParameters> query) {
        HashMap<String, List<String>> map = query.getFilter().isPresent()
                ? query.getFilter().get().getFilterParameters()
                : new HashMap<>();
        map.put("page", Arrays.asList(String.valueOf(query.getPage())));
        map.put("size", Arrays.asList(String.valueOf(query.getPageSize())));
        getSortParameters(query.getSortOrders(), map);
        return service.listPersons(toQueryString(map)).stream();
    }

    @Override
    protected int sizeInBackEnd(Query<SamplePerson, HasFilterParameters> query) {
        HashMap<String, List<String>> map = query.getFilter().isPresent()
                ? query.getFilter().get().getFilterParameters()
                : new HashMap<>();
        return (int) service.count(toQueryString(map));
    }

    public ConfigurableFilterDataProvider<SamplePerson, Void, HasFilterParameters> withFilter(
            HasFilterParameters filter) {
        ConfigurableFilterDataProvider<SamplePerson, Void, HasFilterParameters> filterDataProvider = this
                .withConfigurableFilter();
        filterDataProvider.setFilter(filter);
        return filterDataProvider;
    }

    private String toQueryString(HashMap<String, List<String>> map) {
        // Doing this manually because using UriComponentsBuilder, parameters in server
        // are received encoded
        return map.entrySet().stream()
                .map(e -> e.getValue().stream().map(v -> e.getKey() + "=" + v).collect(Collectors.joining("&")))
                .collect(Collectors.joining("&"));
    }
}
