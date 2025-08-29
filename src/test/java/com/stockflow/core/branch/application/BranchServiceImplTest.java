package com.stockflow.core.branch.application;

import com.stockflow.core.BaseMockTest;
import com.stockflow.core.branch.domain.Branch;
import com.stockflow.core.branch.domain.BranchMother;
import com.stockflow.core.branch.domain.BranchPostDTO;
import com.stockflow.core.branch.infrastructure.BranchRepository;
import com.stockflow.core.business.domain.Business;
import com.stockflow.core.business.domain.BusinessMother;
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

public class BranchServiceImplTest extends BaseMockTest {

    @InjectMocks
    private BranchServiceImpl service;

    @Mock
    private BranchRepository repository;

    @Test
    void create_shouldCreateBranchSuccessfully() {
        BranchPostDTO dto = BranchMother.branchPostDTO() ;
        Business business = BusinessMother.activeBusiness();

        when(repository.existsByBusinessAndCode(business, dto.getCode())).thenReturn(false);
        when(repository.save(any(Branch.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Branch result = service.create(dto, business);

        assertThat(result.getBusiness()).isEqualTo(business);
        assertThat(result.getCode()).isEqualTo(dto.getCode());
        assertThat(result.getName()).isEqualTo(dto.getName());
        assertThat(result.getPhone()).isEqualTo(BranchMother.PHONE_NORMALIZED);
        assertThat(result.getActive()).isTrue();
        verify(repository).save(any(Branch.class));
    }

    @Test
    void create_shouldThrowWhenCodeLengthInvalid() {
        BranchPostDTO dto = BranchMother.branchPostDTOWithInvalidLengthCode();
        Business business = BusinessMother.activeBusiness();

        assertThatThrownBy(() -> service.create(dto, business))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("error.branch.code_invalid_length");
    }

    @Test
    void create_shouldThrowWhenCodeFormatInvalid() {
        BranchPostDTO dto = BranchMother.branchPostDTOWithInvalidCode();
        Business business = BusinessMother.activeBusiness();

        assertThatThrownBy(() -> service.create(dto, business))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("error.branch.code_invalid_format");
    }

    @Test
    void create_shouldThrowWhenDuplicateCode() {
        BranchPostDTO dto = BranchMother.branchPostDTO();
        Business business = BusinessMother.activeBusiness();

        when(repository.existsByBusinessAndCode(business, dto.getCode())).thenReturn(true);

        assertThatThrownBy(() -> service.create(dto, business))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("error.branch.duplicate_code");
    }

    @Test
    void create_shouldPromoteNewMainAndDemoteOldOne() {
        BranchPostDTO dto = BranchMother.mainBranchPostDTO();
        Business business = BusinessMother.activeBusiness();
        Branch oldMain = BranchMother.activeMainBranch();

        when(repository.existsByBusinessAndCode(business, dto.getCode())).thenReturn(false);
        when(repository.findByBusinessAndIsMainIsTrue(business)).thenReturn(Optional.of(oldMain));
        when(repository.save(any(Branch.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Branch result = service.create(dto, business);

        assertThat(result.isMain()).isTrue();
        verify(repository).save(oldMain);
        verify(repository).save(result);
    }

    @Test
    void create_shouldThrowWhenPhoneInvalid() {
        BranchPostDTO dto = BranchMother.branchPostDTOWithInvalidPhone();
        Business business = BusinessMother.activeBusiness();

        assertThatThrownBy(() -> service.create(dto, business))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("error.phone");
    }

    @Test
    void create_shouldCreateWhenPhoneIsNull() {
        BranchPostDTO dto = BranchMother.branchPostDTOWithoutPhone();
        Business business = BusinessMother.activeBusiness();

        when(repository.existsByBusinessAndCode(business, dto.getCode())).thenReturn(false);
        when(repository.save(any(Branch.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Branch result = service.create(dto, business);

        assertThat(result.getPhone()).isNull();
        assertThat(result.getActive()).isTrue();
        verify(repository).save(any(Branch.class));

    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldUpdateBranch() {
    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldThrowIfNotFound() {
    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldNotAllowDuplicateCode() {
    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldAllowMakingBranchMain() {
    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldThrowWhenPhoneInvalid() {
    }

    @Test
    void getByIdThrowingException_shouldReturnBranch() {
        UUID id = UUID.randomUUID();
        Branch branch = BranchMother.activeBranch();

        when(repository.findById(id)).thenReturn(Optional.of(branch));

        Branch result = service.getByIdThrowingException(id);

        assertThat(result).isSameAs(branch);
    }

    @Test
    void getByIdThrowingException_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByIdThrowingException(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void find_shouldReturnBranchIfExists() {
        UUID id = UUID.randomUUID();
        Branch branch = BranchMother.activeBranch();
        when(repository.findById(id)).thenReturn(Optional.of(branch));

        Optional<Branch> result = service.find(id);

        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(branch);
    }

    @Test
    void find_shouldReturnEmptyIfNotExists() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Branch> result = service.find(id);

        assertThat(result).isEmpty();
    }

    @Test
    void list_shouldReturnAllBranches() {
        Branch branch1 = BranchMother.activeBranch();
        Branch branch2 = BranchMother.inactiveBranch();
        when(repository.findAll()).thenReturn(List.of(branch1, branch2));

        Collection<Branch> result = service.list();

        assertThat(result).hasSize(2).containsExactlyInAnyOrder(branch1, branch2);
    }

    @Test
    void toggleActiveStatus_shouldDeactivateIfActive() {
        UUID id = UUID.randomUUID();
        Branch branch = BranchMother.activeBranch();

        when(repository.findById(id)).thenReturn(Optional.of(branch));
        when(repository.save(any(Branch.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Branch result = service.toggleActiveStatus(id);

        assertThat(result.getActive()).isFalse();
        verify(repository).save(argThat(t -> !t.getActive()));
    }

    @Test
    void toggleActiveStatus_shouldActivateIfInactive() {
        UUID id = UUID.randomUUID();
        Branch branch = BranchMother.inactiveBranch();

        when(repository.findById(id)).thenReturn(Optional.of(branch));
        when(repository.save(any(Branch.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Branch result = service.toggleActiveStatus(id);

        assertThat(result.getActive()).isTrue();
        verify(repository).save(argThat(AbstractStatefulAuditableEntity::getActive));
    }

    @Test
    void paged_shouldCallRepositoryFindAllAndReturnPage() {
        Branch t1 = BranchMother.activeBranch();
        Branch t2 = BranchMother.inactiveBranch();
        Page<Branch> page = new PageImpl<>(List.of(t1, t2));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Branch> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));

        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenSearchIsNullAndActiveBoth() {
        Branch t1 = BranchMother.activeBranch();
        Branch t2 = BranchMother.inactiveBranch();
        Page<Branch> page = new PageImpl<>(List.of(t1, t2));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Branch> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenSearchIsBlank() {
        Branch t1 = BranchMother.activeBranch();
        Page<Branch> page = new PageImpl<>(List.of(t1));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search("   ") // blank should be treated as absent
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Branch> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenSearchHasValue() {
        Branch t1 = BranchMother.activeBranch();
        Page<Branch> page = new PageImpl<>(List.of(t1));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search("ACME")
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Branch> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenActiveTrue() {
        Page<Branch> page = new PageImpl<>(List.of(BranchMother.activeBranch()));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.TRUE)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Branch> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenActiveFalse() {
        Page<Branch> page = new PageImpl<>(List.of(BranchMother.inactiveBranch()));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.FALSE)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Branch> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

}