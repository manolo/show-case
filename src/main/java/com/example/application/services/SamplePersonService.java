package com.example.application.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.application.data.SamplePerson;
import com.example.application.data.SamplePersonRepository;

@Service
@PreAuthorize("isAuthenticated()")
public class SamplePersonService {

    private final SamplePersonRepository repository;

    public SamplePersonService(SamplePersonRepository repository) {
        this.repository = repository;
    }

    public Optional<SamplePerson> get(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public SamplePerson update(SamplePerson entity) {
        return repository.save(entity);
    }

    @Transactional
    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<SamplePerson> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<SamplePerson> listItems(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    public Page<SamplePerson> list(Pageable pageable, Specification<SamplePerson> filter) {
        return repository.findAll(filter, pageable);
    }

    public List<SamplePerson> listItems(Pageable pageable, Specification<SamplePerson> filter) {
        return repository.findAll(filter, pageable).getContent();
    }

    public int count() {
        return (int)repository.count();
    }

    public int count(Specification<SamplePerson> filter) {
        return (int)repository.count(filter);
    }

    public List<String> findDistinctOccupationValues() {
        return repository.findDistinctOccupationValues();
    }

    public List<String> findDistinctRoleValues() {
        return repository.findDistinctRoleValues();
    }
}
