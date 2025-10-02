package com.example.application.api;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;

@RestController
@RequestMapping("/api/person")
public class SamplePersonServerApi {

    @Autowired
    private SamplePersonService service;


    @GetMapping
    public SamplePerson getAnswer(@RequestParam(value = "id", required = true) Long id) {
        return service.get(id).orElse(null);
    }

    @PostMapping
    public SamplePerson update(@RequestBody @Valid SamplePerson entity) {
        return service.update(entity);
    }

    @DeleteMapping
    public void delete(@RequestParam(value = "id") Long id) {
        service.delete(id);
    }

    @GetMapping("/distinct-occupations")
    public List<String> findDistinctOccupationValues() {
        return service.findDistinctOccupationValues();
    }

    @GetMapping("/distinct-roles")
    public List<String> findDistinctRoleValues() {
        return service.findDistinctRoleValues();
    }

    @GetMapping("/count")
    public int count(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) @Email String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) List<String> occupation,
            @RequestParam(required = false) List<String> role,
            @RequestParam(required = false) Boolean important) {
        Specification<SamplePerson> spec = toSpec(name, firstName, lastName, email, phone, startDate, endDate, occupation, role, important);
        return service.count(spec);
    }

    @GetMapping("/list")
    public Page<SamplePerson> listPersons(
            @PageableDefault(page = 0, size = 10000)
            @SortDefault.SortDefaults({
                @SortDefault(sort = "lastName"),
                @SortDefault(sort = "firstName")
            }) Pageable pageable,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) @Email String email,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) List<String> occupation,
            @RequestParam(required = false) List<String> role,
            @RequestParam(required = false) Boolean important) {
        Specification<SamplePerson> spec = toSpec(name, firstName, lastName, email, phone, startDate, endDate, occupation, role, important);
        return service.list(pageable, spec);
    }

    private Specification<SamplePerson> toSpec(String name, String firstName, String lastName, String email, String phone, LocalDate startDate,
            LocalDate endDate, List<String> occupation, List<String> role, Boolean important) {
        Specification<SamplePerson> spec = Specification.unrestricted();

        if (name != null && !name.isEmpty()) {
            String namePattern = "%" + name.toLowerCase() + "%";
            spec = spec.and((root, query, builder) ->
                builder.or(
                    builder.like(builder.lower(root.get("firstName")), namePattern),
                    builder.like(builder.lower(root.get("lastName")), namePattern)
                )
            );
        } else {
            if (firstName != null && !firstName.isEmpty()) {
                spec = spec.and((root, query, builder) ->
                    builder.like(builder.lower(root.get("firstName")), "%" + firstName.toLowerCase() + "%")
                );
            }
            if (lastName != null && !lastName.isEmpty()) {
                spec = spec.and((root, query, builder) ->
                    builder.like(builder.lower(root.get("lastName")), "%" + lastName.toLowerCase() + "%")
                );
            }

        }

        if (email != null && !email.isEmpty()) {
            spec = spec.and((root, query, builder) ->
                builder.like(root.get("email"), email)
            );
        }

        if (phone != null && !phone.isEmpty()) {
            spec = spec.and((root, query, builder) ->
                builder.like(root.get("phone"), phone)
            );
        }

        if (startDate != null && endDate != null) {
            spec = spec.and((root, query, builder) ->
                builder.between(root.get("dateOfBirth"), startDate, endDate)
            );
        }

        if (occupation != null && !occupation.isEmpty()) {
            spec = spec.and((root, query, builder) ->
                root.get("occupation").in(occupation)
            );
        }

        if (role != null && !role.isEmpty()) {
            spec = spec.and((root, query, builder) ->
                root.get("role").in(role)
            );
        }

        if (important != null) {
            spec = spec.and((root, query, builder) ->
                builder.equal(root.get("important"), important)
            );
        }
        return spec;
    }


}