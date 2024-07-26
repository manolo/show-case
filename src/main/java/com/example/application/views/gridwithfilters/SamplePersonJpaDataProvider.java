package com.example.application.views.gridwithfilters;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.application.components.filter.SamplePersonFilter;
import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.vaadin.flow.data.provider.AbstractBackEndDataProvider;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.data.provider.SortDirection;

@Service
public class SamplePersonJpaDataProvider extends AbstractBackEndDataProvider<SamplePerson, SamplePersonFilter> {

    private final SamplePersonService service;

    public SamplePersonJpaDataProvider(SamplePersonService service) {
        this.service = service;
    }

    private Sort createSort(List<QuerySortOrder> sorts) {
        return Sort.by(sorts.stream()
                .map(sortOrder -> new Sort.Order(
                        sortOrder.getDirection() == SortDirection.ASCENDING ? Sort.Direction.ASC : Sort.Direction.DESC,
                        sortOrder.getSorted()))
                .toArray(Sort.Order[]::new));
    }

    @Override
    protected Stream<SamplePerson> fetchFromBackEnd(Query<SamplePerson, SamplePersonFilter> query) {
        int offset = query.getOffset();
        int limit = query.getLimit();
        Pageable pageable = PageRequest.of(offset / limit, limit, createSort(query.getSortOrders()));
        HashMap<String, List<String>> map = query.getFilter().get().getFilterParameters();
        return service.list(pageable, toPredicate(map)).stream();
    }

    @Override
    protected int sizeInBackEnd(Query<SamplePerson, SamplePersonFilter> query) {
        HashMap<String, List<String>> map = query.getFilter().get().getFilterParameters();
        return (int)service.count(toPredicate(map));
    }

    public ConfigurableFilterDataProvider<SamplePerson, Void, SamplePersonFilter> withFilter(
            SamplePersonFilter filter) {
        ConfigurableFilterDataProvider<SamplePerson, Void, SamplePersonFilter> filterDataProvider = this
                .withConfigurableFilter();
        filterDataProvider.setFilter(filter);
        return filterDataProvider;
    }

    private Specification<SamplePerson> toPredicate(HashMap<String, List<String>> map) {
        Specification<SamplePerson> spec = Specification.where(null);
        if (map.get("name") != null) {
            String namePattern = "%" + map.get("name").get(0).toLowerCase() + "%";
            spec = spec.and((root, query, builder) -> builder.or(
                    builder.like(builder.lower(root.get("firstName")), namePattern),
                    builder.like(builder.lower(root.get("lastName")), namePattern)));
        }
        if (map.get("email") != null) {
            spec = spec.and((root, query, builder) -> builder.equal(root.get("email"), map.get("email").get(0)));
        }
        if (map.get("phone") != null) {
            spec = spec.and((root, query, builder) -> builder.like(root.get("phone"), map.get("phone").get(0)));
        }
        if (map.get("startDate") != null && map.get("endDate") != null) {
            spec = spec.and((root, query, builder) -> builder.between(root.get("dateOfBirth"),
                    map.get("startDate").get(0), map.get("endDate").get(0)));
        }
        if (map.get("occupation") != null) {
            spec = spec.and((root, query, builder) -> root.get("occupation").in(map.get("occupation")));
        }
        if (map.get("role") != null) {
            spec = spec.and((root, query, builder) -> root.get("role").in(map.get("role")));
        }
        if (map.get("important") != null) {
            spec = spec
                    .and((root, query, builder) -> builder.equal(root.get("important"), map.get("important").get(0)));
        }
        return spec;
    }

}
