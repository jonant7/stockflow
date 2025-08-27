package com.stockflow.core.tenant.application;

import com.stockflow.core.BaseMockTest;
import com.stockflow.core.shared.domain.AbstractStatefulAuditableEntity;
import com.stockflow.core.shared.domain.EntityStatefulFilter;
import com.stockflow.core.shared.domain.TriStateBoolean;
import com.stockflow.core.shared.exception.EntityNotFoundException;
import com.stockflow.core.tenant.domain.Tenant;
import com.stockflow.core.tenant.domain.TenantMother;
import com.stockflow.core.tenant.domain.TenantPostDTO;
import com.stockflow.core.tenant.domain.TenantPutDTO;
import com.stockflow.core.tenant.infrastructure.TenantRepository;
import com.stockflow.core.util.SlugUtils;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TenantServiceImplTest extends BaseMockTest {

    @InjectMocks
    private TenantServiceImpl service;

    @Mock
    private TenantRepository repository;

    @Test
    void create_shouldGenerateUniqueSlug() {
        TenantPostDTO dto = TenantMother.tenantPostDTO();
        String expectedSlug = SlugUtils.toSlug(dto.getDisplayName());

        when(repository.existsBySlug(expectedSlug)).thenReturn(false);
        when(repository.save(any(Tenant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tenant result = service.create(dto);

        assertThat(result.getName()).isEqualTo(dto.getDisplayName());
        assertThat(result.getSlug()).isEqualTo(expectedSlug);
    }

    @Test
    void create_shouldAppendCounterIfSlugExists() {
        TenantPostDTO dto = TenantMother.tenantPostDTO();
        String expectedSlug = SlugUtils.toSlug(dto.getDisplayName());

        when(repository.existsBySlug(expectedSlug)).thenReturn(true);
        when(repository.existsBySlug(expectedSlug + "-1")).thenReturn(false);
        when(repository.save(any(Tenant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tenant result = service.create(dto);

        assertThat(result.getSlug()).isEqualTo(expectedSlug + "-1");
        assertThat(result.getName()).isEqualTo(dto.getDisplayName());
    }

    @Test
    void update_shouldReplaceNameAndSlug() {
        UUID id = UUID.randomUUID();
        Tenant existing = TenantMother.activeTenant();

        TenantPutDTO dto = TenantMother.tenantPutDTO();

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.save(any(Tenant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tenant updated = service.update(id, dto);

        assertThat(updated.getName()).isEqualTo(dto.getDisplayName());
        assertThat(updated.getSlug()).isEqualTo(dto.getSlug());
    }

    @Test
    void update_shouldGenerateSlugWhenDtoSlugIsNull() {
        UUID id = UUID.randomUUID();
        Tenant existing = TenantMother.activeTenant();

        TenantPutDTO dto = TenantMother.tenantPutDTONullSlug();
        String expectedSlug = SlugUtils.toSlug(dto.getDisplayName());

        when(repository.findById(id)).thenReturn(Optional.of(existing));
        when(repository.existsBySlug(expectedSlug)).thenReturn(false);
        when(repository.save(any(Tenant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tenant updated = service.update(id, dto);

        assertThat(updated.getName()).isEqualTo(dto.getDisplayName());
        assertThat(updated.getSlug()).isEqualTo(expectedSlug);
    }

    @Test
    void update_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();
        TenantPutDTO dto = TenantMother.tenantPutDTO();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(id, dto)).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void getByIdThrowingException_shouldReturnTenant() {
        UUID id = UUID.randomUUID();
        Tenant tenant = TenantMother.activeTenant();

        when(repository.findById(id)).thenReturn(Optional.of(tenant));

        Tenant result = service.getByIdThrowingException(id);

        assertThat(result).isSameAs(tenant);
    }

    @Test
    void getByIdThrowingException_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByIdThrowingException(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void find_shouldReturnTenantIfExists() {
        UUID id = UUID.randomUUID();
        Tenant tenant = TenantMother.activeTenant();
        when(repository.findById(id)).thenReturn(Optional.of(tenant));

        Optional<Tenant> result = service.find(id);

        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(tenant);
    }

    @Test
    void find_shouldReturnEmptyIfNotExists() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Tenant> result = service.find(id);

        assertThat(result).isEmpty();
    }

    @Test
    void list_shouldReturnAllTenants() {
        Tenant tenant1 = TenantMother.activeTenant();
        Tenant tenant2 = TenantMother.inactiveTenant();
        when(repository.findAll()).thenReturn(List.of(tenant1, tenant2));

        Collection<Tenant> result = service.list();

        assertThat(result).hasSize(2).containsExactlyInAnyOrder(tenant1, tenant2);
    }

    @Test
    void existsBySlug_shouldDelegateToRepository() {
        String slug = "abc";
        when(repository.existsBySlug(slug)).thenReturn(true);

        boolean exists = service.existsBySlug(slug);

        assertThat(exists).isTrue();
        verify(repository).existsBySlug(slug);
    }

    @Test
    void toggleActiveStatus_shouldDeactivateIfActive() {
        UUID id = UUID.randomUUID();
        Tenant tenant = TenantMother.activeTenant();

        when(repository.findById(id)).thenReturn(Optional.of(tenant));
        when(repository.save(any(Tenant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tenant result = service.toggleActiveStatus(id);

        assertThat(result.getActive()).isFalse();
        verify(repository).save(argThat(t -> !t.getActive()));
    }

    @Test
    void toggleActiveStatus_shouldActivateIfInactive() {
        UUID id = UUID.randomUUID();
        Tenant tenant = TenantMother.inactiveTenant();

        when(repository.findById(id)).thenReturn(Optional.of(tenant));
        when(repository.save(any(Tenant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tenant result = service.toggleActiveStatus(id);

        assertThat(result.getActive()).isTrue();
        verify(repository).save(argThat(AbstractStatefulAuditableEntity::getActive));
    }

    @Test
    void paged_shouldCallRepositoryFindAllAndReturnPage() {
        Tenant t1 = TenantMother.activeTenant();
        Tenant t2 = TenantMother.inactiveTenant();
        Page<Tenant> page = new PageImpl<>(List.of(t1, t2));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Tenant> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));

        assertThat(result).isSameAs(page);

    }

}