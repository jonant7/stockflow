package com.stockflow.core.organization.application;

import com.stockflow.core.organization.domain.Organization;
import com.stockflow.core.organization.domain.OrganizationPostDTO;
import com.stockflow.core.organization.domain.OrganizationPutDTO;
import com.stockflow.core.organization.infrastructure.OrganizationRepository;
import com.stockflow.core.shared.domain.EntityStatefulFilter;
import com.stockflow.core.shared.exception.BusinessException;
import com.stockflow.core.shared.exception.EntityNotFoundException;
import com.stockflow.core.shared.infrastructure.SpecificationUtils;
import com.stockflow.core.util.IdCardUtils;
import com.stockflow.core.util.PhoneUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository repository;

    @Override
    public Organization create(OrganizationPostDTO dto) {
        IdCardUtils.validateRuc(dto.getTin());
        String normalizedNumber  = PhoneUtils.toE164(dto.getPhone(), null);

        if (repository.existsByTin(dto.getTin())) {
            throw new BusinessException("error.organization.already_exists_by_tin");
        }

        if (repository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("error.organization.already_exists_by_email");
        }

        Organization organization = Organization.builder()
                .tin(dto.getTin())
                .name(dto.getName())
                .address(dto.getAddress())
                .phone(normalizedNumber)
                .email(dto.getEmail())
                .build();
        organization.activate();
        return repository.save(organization);
    }

    @Override
    public Organization update(OrganizationPutDTO dto) {
        // TODO: Implement method and test (see OrganizationServiceImplTest.class)
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public Optional<Organization> find(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Collection<Organization> list() {
        return repository.findAll();
    }

    @Override
    public Organization getByIdThrowingException(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("message.entity-not-found"));
    }

    @Override
    public Organization toggleActiveStatus(UUID id) {
        final var product = getByIdThrowingException(id);
        if (product.getActive()) {
            product.deactivate();
        } else {
            product.activate();
        }
        return repository.save(product);
    }

    @Override
    public Page<Organization> paged(EntityStatefulFilter filter, Pageable pageable) {
        Specification<Organization> spec = SpecificationUtils.withActive(filter.getActive());

        Optional<String> search = filter.getSearch();
        if (search.isPresent() && !search.get().isBlank()) {
            String trimmedSearch = search.get().trim();

            Specification<Organization> searchSpec = SpecificationUtils.<Organization>withSearch(trimmedSearch, "tin")
                    .or(SpecificationUtils.withSearch(trimmedSearch, "name"))
                    .or(SpecificationUtils.withSearch(trimmedSearch, "email"));

            spec = spec.and(searchSpec);
        }
        return repository.findAll(spec, pageable);
    }

}