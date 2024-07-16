package com.example.application.data;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface SamplePersonRepository
        extends JpaRepository<SamplePerson, Long>, JpaSpecificationExecutor<SamplePerson> {

    @Query(value = "SELECT DISTINCT occupation FROM sample_person", nativeQuery = true)
    List<String> findDistinctOccupationValues();

}
