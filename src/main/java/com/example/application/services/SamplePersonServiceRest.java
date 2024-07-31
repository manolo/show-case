package com.example.application.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.application.data.SamplePerson;

@Service
public class SamplePersonServiceRest {

    private static final String BASE_uri = "http://localhost:8080/api/person";

    private RestTemplate restTemplate = new RestTemplate();

    // An intermediate class to get an entity list from the response
    private static class SamplePersonContent {
        public List<SamplePerson> content = new ArrayList<>();
    }

    public SamplePerson getAnswer(Long id) {
        return restTemplate.getForObject(BASE_uri + "?id=" + id, SamplePerson.class);
    }

    public SamplePerson update(SamplePerson entity) {
        return restTemplate.postForObject(BASE_uri, entity, SamplePerson.class);
    }

    public void delete(Long id) {
        restTemplate.delete(BASE_uri + "?id=" + id);
    }

    public int count(String queryString) {
        return restTemplate.getForObject(BASE_uri + "/count?" + queryString, Integer.class);
    }

    public List<SamplePerson> listPersons(String queryString) {
        return restTemplate.getForObject(BASE_uri + "/list?" + queryString, SamplePersonContent.class).content;
    }

    public List<String> findDistinctOccupationValues() {
        return restTemplate.getForObject(BASE_uri + "/distinct-occupations", List.class);
    }

    public List<String> findDistinctRoleValues() {
        return restTemplate.getForObject(BASE_uri + "/distinct-roles", List.class);
    }


}
