package com.stockflow.core.business.application;

import com.stockflow.core.business.domain.Business;
import com.stockflow.core.business.domain.BusinessPostDTO;
import com.stockflow.core.business.domain.BusinessPutDTO;
import com.stockflow.core.business.infrastructure.BusinessRepository;
import com.stockflow.core.organization.domain.Organization;
import com.stockflow.core.shared.domain.EntityStatefulFilter;
import com.stockflow.core.shared.exception.BusinessException;
import com.stockflow.core.shared.exception.EntityNotFoundException;
import com.stockflow.core.shared.infrastructure.SpecificationUtils;
import com.stockflow.core.util.PhoneUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

    private final BusinessRepository repository;

    @Override
    public Business create(BusinessPostDTO dto, Organization organization) {
        String normalizedPhone = null;

        if (Objects.nonNull(dto.getPhone()) && !dto.getPhone().isBlank()) {
            normalizedPhone = PhoneUtils.toE164(dto.getPhone(), null);
        }

        if (repository.existsByOrganizationAndEmail(organization, dto.getEmail())) {
            throw new BusinessException("error.business.already_exists_by_email");
        }

        Business business = Business.builder()
                .organization(organization)
                .name(dto.getName().trim())
                .phone(normalizedPhone)
                .email(Objects.nonNull(dto.getEmail()) ? dto.getEmail().trim() : null)
                .address(dto.getAddress().trim())
                .build();
        business.activate();
        return repository.save(business);
    }


    @Override
    public Business update(UUID id, BusinessPutDTO dto) {
        // TODO: Implement method and test (see BusinessServiceImplTest.class)
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public Optional<Business> find(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Collection<Business> list() {
        return repository.findAll();
    }

    @Override
    public Business getByIdThrowingException(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("message.entity-not-found"));
    }

    @Override
    public Business toggleActiveStatus(UUID id) {
        final var product = getByIdThrowingException(id);
        if (product.getActive()) {
            product.deactivate();
        } else {
            product.activate();
        }
        return repository.save(product);
    }

    @Override
    public Page<Business> paged(EntityStatefulFilter filter, Pageable pageable) {
        Specification<Business> spec = SpecificationUtils.withActive(filter.getActive());

        Optional<String> search = filter.getSearch();
        if (search.isPresent() && !search.get().isBlank()) {
            String trimmedSearch = search.get().trim();

            Specification<Business> searchSpec = SpecificationUtils.<Business>withSearch(trimmedSearch, "tin")
                    .or(SpecificationUtils.withSearch(trimmedSearch, "name"))
                    .or(SpecificationUtils.withSearch(trimmedSearch, "email"));

            spec = spec.and(searchSpec);
        }
        return repository.findAll(spec, pageable);
    }
}