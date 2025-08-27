package com.stockflow.core.tenant.application;

import com.stockflow.core.config.MessageResolver;
import com.stockflow.core.shared.domain.EntityStatefulFilter;
import com.stockflow.core.shared.exception.EntityNotFoundException;
import com.stockflow.core.tenant.domain.Tenant;
import com.stockflow.core.tenant.domain.TenantPostDTO;
import com.stockflow.core.tenant.domain.TenantPutDTO;
import com.stockflow.core.tenant.infrastructure.TenantRepository;
import com.stockflow.core.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static com.stockflow.core.shared.infrastructure.SpecificationUtils.withActive;
import static com.stockflow.core.shared.infrastructure.SpecificationUtils.withSearch;

@Service
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final TenantRepository repository;

    @Override
    public Tenant create(TenantPostDTO dto) {
        String baseSlug = SlugUtils.toSlug(dto.getDisplayName());
        String slug = generateUniqueSlug(baseSlug);

        Tenant tenant = Tenant.builder()
                .name(dto.getDisplayName())
                .slug(slug)
                .build();
        tenant.activate();

        return repository.save(tenant);
    }

    @Override
    public Tenant update(UUID id, TenantPutDTO dto) {
        Tenant entity = getByIdThrowingException(id);
        String slug = Objects.isNull(dto.getSlug()) ? generateUniqueSlug(SlugUtils.toSlug(dto.getDisplayName())) : dto.getSlug();
        entity.update(dto, slug);
        return repository.save(entity);

    }

    @Override
    public boolean existsBySlug(String slug) {
        return repository.existsBySlug(slug);
    }

    @Override
    public Optional<Tenant> find(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Collection<Tenant> list() {
        return repository.findAll();
    }

    @Override
    public Tenant getByIdThrowingException(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageResolver.get("error.entity-not-found")));
    }

    @Override
    public Tenant toggleActiveStatus(UUID id) {
        final var tenant = getByIdThrowingException(id);
        if (tenant.getActive()) {
            tenant.deactivate();
        } else {
            tenant.activate();
        }
        return repository.save(tenant);
    }

    @Override
    public Page<Tenant> paged(EntityStatefulFilter filter, Pageable pageable) {
        Specification<Tenant> spec = Specification.allOf(
                withActive(filter.getActive()),
                withSearch(filter.getSearch().orElse(null), "name")
        );

        return repository.findAll(spec, pageable);
    }

    private String generateUniqueSlug(String baseSlug) {
        String slug = baseSlug;
        int counter = 1;

        while (existsBySlug(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }

        return slug;
    }

}