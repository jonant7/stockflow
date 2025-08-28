package com.stockflow.core.organization.application;

import com.stockflow.core.BaseMockTest;
import com.stockflow.core.organization.domain.Organization;
import com.stockflow.core.organization.domain.OrganizationMother;
import com.stockflow.core.organization.domain.OrganizationPostDTO;
import com.stockflow.core.organization.infrastructure.OrganizationRepository;
import com.stockflow.core.shared.domain.AbstractStatefulAuditableEntity;
import com.stockflow.core.shared.domain.EntityStatefulFilter;
import com.stockflow.core.shared.domain.TriStateBoolean;
import com.stockflow.core.shared.exception.BusinessException;
import com.stockflow.core.shared.exception.EntityNotFoundException;
import org.junit.jupiter.api.Disabled;
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

public class OrganizationServiceImplTest extends BaseMockTest {

    @InjectMocks
    private OrganizationServiceImpl service;

    @Mock
    private OrganizationRepository repository;

    @Test
    void create_shouldCreateOrganizationSuccessfully() {
        OrganizationPostDTO dto = OrganizationMother.organizationPostDTO();

        when(repository.existsByTin(dto.getTin())).thenReturn(false);
        when(repository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(repository.save(any(Organization.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Organization result = service.create(dto);

        assertThat(result.getTin()).isEqualTo(dto.getTin());
        assertThat(result.getEmail()).isEqualTo(dto.getEmail());
        assertThat(result.getName()).isEqualTo(dto.getName());
        assertThat(result.getAddress()).isEqualTo(dto.getAddress());
        assertThat(result.getPhone()).isEqualTo(OrganizationMother.PHONE_NORMALIZED);
        assertThat(result.getActive()).isTrue();
        verify(repository).save(any(Organization.class));
    }

    @Test
    void create_shouldThrowWhenTinAlreadyExists() {
        OrganizationPostDTO dto = OrganizationMother.organizationPostDTO();

        when(repository.existsByTin(dto.getTin())).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("error.organization.already_exists_by_tin");
    }

    @Test
    void create_shouldThrowWhenEmailAlreadyExists() {
        OrganizationPostDTO dto = OrganizationMother.organizationPostDTO();

        when(repository.existsByTin(dto.getTin())).thenReturn(false);
        when(repository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("error.organization.already_exists_by_email");
    }

    @Test
    void create_shouldThrowWhenTinIsInvalidRuc() {

        var dto = OrganizationPostDTO.builder()
                .tin("123")
                .name("Name")
                .address("Address")
                .phone("Phone")
                .email("email@example.com")
                .build();

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class);
    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldUpdateOrganization() {
    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldThrowIfNotFound() {
    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldNotAllowDuplicateTin() {
    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldNotAllowDuplicateEmail() {
    }

    @Test
    void getByIdThrowingException_shouldReturnOrganization() {
        UUID id = UUID.randomUUID();
        Organization organization = OrganizationMother.activeOrganization();

        when(repository.findById(id)).thenReturn(Optional.of(organization));

        Organization result = service.getByIdThrowingException(id);

        assertThat(result).isSameAs(organization);
    }

    @Test
    void getByIdThrowingException_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByIdThrowingException(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void find_shouldReturnOrganizationIfExists() {
        UUID id = UUID.randomUUID();
        Organization organization = OrganizationMother.activeOrganization();
        when(repository.findById(id)).thenReturn(Optional.of(organization));

        Optional<Organization> result = service.find(id);

        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(organization);
    }

    @Test
    void find_shouldReturnEmptyIfNotExists() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Organization> result = service.find(id);

        assertThat(result).isEmpty();
    }

    @Test
    void list_shouldReturnAllOrganizations() {
        Organization organization1 = OrganizationMother.activeOrganization();
        Organization organization2 = OrganizationMother.inactiveOrganization();
        when(repository.findAll()).thenReturn(List.of(organization1, organization2));

        Collection<Organization> result = service.list();

        assertThat(result).hasSize(2).containsExactlyInAnyOrder(organization1, organization2);
    }

    @Test
    void toggleActiveStatus_shouldDeactivateIfActive() {
        UUID id = UUID.randomUUID();
        Organization organization = OrganizationMother.activeOrganization();

        when(repository.findById(id)).thenReturn(Optional.of(organization));
        when(repository.save(any(Organization.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Organization result = service.toggleActiveStatus(id);

        assertThat(result.getActive()).isFalse();
        verify(repository).save(argThat(t -> !t.getActive()));
    }

    @Test
    void toggleActiveStatus_shouldActivateIfInactive() {
        UUID id = UUID.randomUUID();
        Organization organization = OrganizationMother.inactiveOrganization();

        when(repository.findById(id)).thenReturn(Optional.of(organization));
        when(repository.save(any(Organization.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Organization result = service.toggleActiveStatus(id);

        assertThat(result.getActive()).isTrue();
        verify(repository).save(argThat(AbstractStatefulAuditableEntity::getActive));
    }

    @Test
    void paged_shouldCallRepositoryFindAllAndReturnPage() {
        Organization t1 = OrganizationMother.activeOrganization();
        Organization t2 = OrganizationMother.inactiveOrganization();
        Page<Organization> page = new PageImpl<>(List.of(t1, t2));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Organization> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));

        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenSearchIsNullAndActiveBoth() {
        Organization t1 = OrganizationMother.activeOrganization();
        Organization t2 = OrganizationMother.inactiveOrganization();
        Page<Organization> page = new PageImpl<>(List.of(t1, t2));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Organization> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenSearchIsBlank() {
        Organization t1 = OrganizationMother.activeOrganization();
        Page<Organization> page = new PageImpl<>(List.of(t1));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search("   ") // blank should be treated as absent
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Organization> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenSearchHasValue() {
        Organization t1 = OrganizationMother.activeOrganization();
        Page<Organization> page = new PageImpl<>(List.of(t1));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search("ACME")
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Organization> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenActiveTrue() {
        Page<Organization> page = new PageImpl<>(List.of(OrganizationMother.activeOrganization()));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.TRUE)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Organization> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenActiveFalse() {
        Page<Organization> page = new PageImpl<>(List.of(OrganizationMother.inactiveOrganization()));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.FALSE)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Organization> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

}