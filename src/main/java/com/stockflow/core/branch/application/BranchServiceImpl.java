package com.stockflow.core.branch.application;

import com.stockflow.core.branch.domain.Branch;
import com.stockflow.core.branch.domain.BranchPostDTO;
import com.stockflow.core.branch.domain.BranchPutDTO;
import com.stockflow.core.branch.infrastructure.BranchRepository;
import com.stockflow.core.business.domain.Business;
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
public class BranchServiceImpl implements BranchService {

    private final BranchRepository repository;

    @Override
    public Branch create(BranchPostDTO dto, Business business) {
        if (dto.getCode().length() != 3) {
            throw new BusinessException("error.branch.code_invalid_length");
        }

        if (!dto.getCode().matches("^(?!000)\\d{3}$")) {
            throw new BusinessException("error.branch.code_invalid_format");
        }

        String normalizedPhone = null;
        if (Objects.nonNull(dto.getPhone()) && !dto.getPhone().isBlank()) {
            normalizedPhone = PhoneUtils.toE164(dto.getPhone(), null);
        }

        if (repository.existsByBusinessAndCode(business, dto.getCode())) {
            throw new BusinessException("error.branch.duplicate_code");
        }

        boolean isMain = Boolean.TRUE.equals(dto.getIsMain());
        if (isMain) {
            repository.findByBusinessAndIsMainIsTrue(business)
                    .ifPresent(existing -> {
                        existing.demoteFromMain();
                        repository.save(existing);
                    });
        }

        Branch branch = Branch.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .phone(normalizedPhone)
                .email(dto.getEmail())
                .address(dto.getAddress())
                .isMain(isMain)
                .business(business)
                .build();
        branch.activate();

        return repository.save(branch);
    }

    @Override
    public Branch update(UUID id, BranchPutDTO dto) {
        // TODO: Implement method and test (see BranchServiceImplTest.class)
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public Optional<Branch> find(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Collection<Branch> list() {
        return repository.findAll();
    }

    @Override
    public Branch getByIdThrowingException(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("message.entity-not-found"));
    }

    @Override
    public Branch toggleActiveStatus(UUID id) {
        final var product = getByIdThrowingException(id);
        if (product.getActive()) {
            product.deactivate();
        } else {
            product.activate();
        }
        return repository.save(product);
    }

    @Override
    public Page<Branch> paged(EntityStatefulFilter filter, Pageable pageable) {
        Specification<Branch> spec = SpecificationUtils.withActive(filter.getActive());

        Optional<String> search = filter.getSearch();
        if (search.isPresent() && !search.get().isBlank()) {
            String trimmedSearch = search.get().trim();

            Specification<Branch> searchSpec = SpecificationUtils.<Branch>withSearch(trimmedSearch, "tin")
                    .or(SpecificationUtils.withSearch(trimmedSearch, "name"))
                    .or(SpecificationUtils.withSearch(trimmedSearch, "email"));

            spec = spec.and(searchSpec);
        }
        return repository.findAll(spec, pageable);
    }

}