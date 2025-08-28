package com.stockflow.core.business.application;

import com.stockflow.core.BaseMockTest;
import com.stockflow.core.business.domain.Business;
import com.stockflow.core.business.domain.BusinessMother;
import com.stockflow.core.business.domain.BusinessPostDTO;
import com.stockflow.core.business.infrastructure.BusinessRepository;
import com.stockflow.core.organization.domain.Organization;
import com.stockflow.core.organization.domain.OrganizationMother;
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

public class BusinessServiceImplTest extends BaseMockTest {

    @InjectMocks
    private BusinessServiceImpl service;

    @Mock
    private BusinessRepository repository;

    @Test
    void create_shouldCreateBusinessSuccessfully() {
        BusinessPostDTO dto = BusinessMother.businessPostDTO();
        Organization organization = OrganizationMother.activeOrganization();

        when(repository.existsByOrganizationAndEmail(organization, dto.getEmail())).thenReturn(false);
        when(repository.save(any(Business.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Business result = service.create(dto, organization);

        assertThat(result.getOrganization()).isEqualTo(organization);
        assertThat(result.getEmail()).isEqualTo(dto.getEmail());
        assertThat(result.getName()).isEqualTo(dto.getName());
        assertThat(result.getAddress()).isEqualTo(dto.getAddress());
        assertThat(result.getPhone()).isEqualTo(BusinessMother.PHONE_NORMALIZED);
        assertThat(result.getActive()).isTrue();
        verify(repository).save(any(Business.class));
    }

    @Test
    void create_shouldCreateWhenPhoneIsNull() {
        BusinessPostDTO dto = BusinessMother.businessPostDTOWithoutPhone();
        Organization organization = OrganizationMother.activeOrganization();

        when(repository.existsByOrganizationAndEmail(organization, dto.getEmail())).thenReturn(false);
        when(repository.save(any(Business.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Business result = service.create(dto, organization);

        assertThat(result.getOrganization()).isEqualTo(organization);
        assertThat(result.getEmail()).isEqualTo(dto.getEmail());
        assertThat(result.getName()).isEqualTo(dto.getName());
        assertThat(result.getAddress()).isEqualTo(dto.getAddress());
        assertThat(result.getPhone()).isEqualTo(null);
        assertThat(result.getActive()).isTrue();
        verify(repository).save(any(Business.class));
    }

    @Test
    void create_shouldCreateWhenEmailIsNull() {
        BusinessPostDTO dto = BusinessMother.businessPostDTOWithoutEmail();
        Organization organization = OrganizationMother.activeOrganization();

        when(repository.existsByOrganizationAndEmail(organization, dto.getEmail())).thenReturn(false);
        when(repository.save(any(Business.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Business result = service.create(dto, organization);

        assertThat(result.getOrganization()).isEqualTo(organization);
        assertThat(result.getEmail()).isEqualTo(null);
        assertThat(result.getName()).isEqualTo(dto.getName());
        assertThat(result.getAddress()).isEqualTo(dto.getAddress());
        assertThat(result.getPhone()).isEqualTo(BusinessMother.PHONE_NORMALIZED);
        assertThat(result.getActive()).isTrue();
        verify(repository).save(any(Business.class));
    }

    @Test
    void create_shouldThrowWhenEmailIsDuplicated() {
        BusinessPostDTO dto = BusinessMother.businessPostDTO();
        Organization organization = OrganizationMother.activeOrganization();

        when(repository.existsByOrganizationAndEmail(organization, dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto, organization))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("error.business.already_exists_by_email");
    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldUpdateBusiness() {
    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldThrowIfNotFound() {
    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldNotAllowDuplicateEmail() {
    }

    @Test
    void getByIdThrowingException_shouldReturnBusiness() {
        UUID id = UUID.randomUUID();
        Business business = BusinessMother.activeBusiness();

        when(repository.findById(id)).thenReturn(Optional.of(business));

        Business result = service.getByIdThrowingException(id);

        assertThat(result).isSameAs(business);
    }

    @Test
    void getByIdThrowingException_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByIdThrowingException(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void find_shouldReturnBusinessIfExists() {
        UUID id = UUID.randomUUID();
        Business business = BusinessMother.activeBusiness();
        when(repository.findById(id)).thenReturn(Optional.of(business));

        Optional<Business> result = service.find(id);

        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(business);
    }

    @Test
    void find_shouldReturnEmptyIfNotExists() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Business> result = service.find(id);

        assertThat(result).isEmpty();
    }

    @Test
    void list_shouldReturnAllBusinesses() {
        Business business1 = BusinessMother.activeBusiness();
        Business business2 = BusinessMother.inactiveBusiness();
        when(repository.findAll()).thenReturn(List.of(business1, business2));

        Collection<Business> result = service.list();

        assertThat(result).hasSize(2).containsExactlyInAnyOrder(business1, business2);
    }

    @Test
    void toggleActiveStatus_shouldDeactivateIfActive() {
        UUID id = UUID.randomUUID();
        Business business = BusinessMother.activeBusiness();

        when(repository.findById(id)).thenReturn(Optional.of(business));
        when(repository.save(any(Business.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Business result = service.toggleActiveStatus(id);

        assertThat(result.getActive()).isFalse();
        verify(repository).save(argThat(t -> !t.getActive()));
    }

    @Test
    void toggleActiveStatus_shouldActivateIfInactive() {
        UUID id = UUID.randomUUID();
        Business business = BusinessMother.inactiveBusiness();

        when(repository.findById(id)).thenReturn(Optional.of(business));
        when(repository.save(any(Business.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Business result = service.toggleActiveStatus(id);

        assertThat(result.getActive()).isTrue();
        verify(repository).save(argThat(AbstractStatefulAuditableEntity::getActive));
    }

    @Test
    void paged_shouldCallRepositoryFindAllAndReturnPage() {
        Business t1 = BusinessMother.activeBusiness();
        Business t2 = BusinessMother.inactiveBusiness();
        Page<Business> page = new PageImpl<>(List.of(t1, t2));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Business> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));

        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenSearchIsNullAndActiveBoth() {
        Business t1 = BusinessMother.activeBusiness();
        Business t2 = BusinessMother.inactiveBusiness();
        Page<Business> page = new PageImpl<>(List.of(t1, t2));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Business> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenSearchIsBlank() {
        Business t1 = BusinessMother.activeBusiness();
        Page<Business> page = new PageImpl<>(List.of(t1));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search("   ") // blank should be treated as absent
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Business> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenSearchHasValue() {
        Business t1 = BusinessMother.activeBusiness();
        Page<Business> page = new PageImpl<>(List.of(t1));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search("ACME")
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Business> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenActiveTrue() {
        Page<Business> page = new PageImpl<>(List.of(BusinessMother.activeBusiness()));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.TRUE)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Business> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenActiveFalse() {
        Page<Business> page = new PageImpl<>(List.of(BusinessMother.inactiveBusiness()));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.FALSE)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Business> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

}