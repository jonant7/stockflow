package com.stockflow.core.person.application;

import com.stockflow.core.BaseMockTest;
import com.stockflow.core.person.domain.Person;
import com.stockflow.core.person.domain.PersonMother;
import com.stockflow.core.person.domain.PersonPostDTO;
import com.stockflow.core.person.infrastructure.PersonRepository;
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

public class PersonServiceImplTest extends BaseMockTest {

    @InjectMocks
    private PersonServiceImpl service;

    @Mock
    private PersonRepository repository;

    @Test
    void create_shouldCreatePersonSuccessfully() {
        PersonPostDTO dto = PersonMother.personPostDTO();

        when(repository.findByIdentificationTypeAndIdentificationNumber(dto.getIdentificationType(), dto.getIdentificationNumber()))
                .thenReturn(Optional.empty());
        when(repository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Person result = service.create(dto);

        assertThat(result.getFirstName()).isEqualTo(dto.getFirstName().trim());
        assertThat(result.getSurname()).isEqualTo(dto.getSurname().trim());
        assertThat(result.getIdentificationNumber()).isEqualTo(dto.getIdentificationNumber().trim());
        assertThat(result.getActive()).isTrue();

        verify(repository).save(any(Person.class));
    }

    @Test
    void create_shouldCreatePersonWithRucSuccessfully() {
        PersonPostDTO dto = PersonMother.personPostDTOWithRUC();

        when(repository.findByIdentificationTypeAndIdentificationNumber(dto.getIdentificationType(), dto.getIdentificationNumber()))
                .thenReturn(Optional.empty());
        when(repository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Person result = service.create(dto);

        assertThat(result.getFirstName()).isEqualTo(dto.getFirstName().trim());
        assertThat(result.getSurname()).isEqualTo(dto.getSurname().trim());
        assertThat(result.getIdentificationNumber()).isEqualTo(dto.getIdentificationNumber().trim());
        assertThat(result.getActive()).isTrue();

        verify(repository).save(any(Person.class));
    }

    @Test
    void create_shouldThrowWhenPersonAlreadyExists() {
        PersonPostDTO dto = PersonMother.personPostDTO();
        Person existing = PersonMother.activePerson();

        when(repository.findByIdentificationTypeAndIdentificationNumber(dto.getIdentificationType(), dto.getIdentificationNumber()))
                .thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class)
                .hasMessage("error.person.already_exists_by_identification");
    }

    @Test
    void create_shouldThrowWhenIdentificationInvalid() {
        PersonPostDTO dto = PersonMother.personPostDTOWithInvalidId();

        when(repository.findByIdentificationTypeAndIdentificationNumber(dto.getIdentificationType(), dto.getIdentificationNumber()))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.create(dto))
                .isInstanceOf(BusinessException.class);
    }

    @Test
    void create_shouldTrimFields() {
        PersonPostDTO dto = PersonPostDTO.builder()
                .firstName("  John  ")
                .surname("  Doe  ")
                .identificationType(PersonMother.personPostDTO().getIdentificationType())
                .identificationNumber(PersonMother.personPostDTO().getIdentificationNumber())
                .build();

        when(repository.findByIdentificationTypeAndIdentificationNumber(dto.getIdentificationType(), dto.getIdentificationNumber().trim()))
                .thenReturn(Optional.empty());
        when(repository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Person result = service.create(dto);

        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getSurname()).isEqualTo("Doe");
    }

    @Test
    void create_shouldAllowNullOptionalFields() {
        PersonPostDTO dto = PersonPostDTO.builder()
                .firstName("Jane")
                .surname("Doe")
                .secondName(null)
                .secondSurname(null)
                .identificationType(PersonMother.personPostDTO().getIdentificationType())
                .identificationNumber(PersonMother.personPostDTO().getIdentificationNumber())
                .build();

        when(repository.findByIdentificationTypeAndIdentificationNumber(dto.getIdentificationType(), dto.getIdentificationNumber()))
                .thenReturn(Optional.empty());
        when(repository.save(any(Person.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Person result = service.create(dto);

        assertThat(result.getSecondName()).isNull();
        assertThat(result.getSecondSurname()).isNull();
    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldUpdatePerson() {
    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldThrowIfNotFound() {
    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldNotAllowDuplicateIdentification() {
    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldThrowWhenIdentificationInvalid() {
    }

    @Disabled("Enable when update is implemented")
    @Test
    void update_shouldAllowNullOptionalFields() {
    }

    @Test
    void getByIdThrowingException_shouldReturnPerson() {
        UUID id = UUID.randomUUID();
        Person person = PersonMother.activePerson();

        when(repository.findById(id)).thenReturn(Optional.of(person));

        Person result = service.getByIdThrowingException(id);

        assertThat(result).isSameAs(person);
    }

    @Test
    void getByIdThrowingException_shouldThrowIfNotFound() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByIdThrowingException(id))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void find_shouldReturnPersonIfExists() {
        UUID id = UUID.randomUUID();
        Person person = PersonMother.activePerson();
        when(repository.findById(id)).thenReturn(Optional.of(person));

        Optional<Person> result = service.find(id);

        assertThat(result).isPresent();
        assertThat(result.get()).isSameAs(person);
    }

    @Test
    void find_shouldReturnEmptyIfNotExists() {
        UUID id = UUID.randomUUID();
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Person> result = service.find(id);

        assertThat(result).isEmpty();
    }

    @Test
    void list_shouldReturnAllPersones() {
        Person person1 = PersonMother.activePerson();
        Person person2 = PersonMother.inactivePerson();
        when(repository.findAll()).thenReturn(List.of(person1, person2));

        Collection<Person> result = service.list();

        assertThat(result).hasSize(2).containsExactlyInAnyOrder(person1, person2);
    }

    @Test
    void toggleActiveStatus_shouldDeactivateIfActive() {
        UUID id = UUID.randomUUID();
        Person person = PersonMother.activePerson();

        when(repository.findById(id)).thenReturn(Optional.of(person));
        when(repository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Person result = service.toggleActiveStatus(id);

        assertThat(result.getActive()).isFalse();
        verify(repository).save(argThat(t -> !t.getActive()));
    }

    @Test
    void toggleActiveStatus_shouldActivateIfInactive() {
        UUID id = UUID.randomUUID();
        Person person = PersonMother.inactivePerson();

        when(repository.findById(id)).thenReturn(Optional.of(person));
        when(repository.save(any(Person.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Person result = service.toggleActiveStatus(id);

        assertThat(result.getActive()).isTrue();
        verify(repository).save(argThat(AbstractStatefulAuditableEntity::getActive));
    }

    @Test
    void paged_shouldCallRepositoryFindAllAndReturnPage() {
        Person p1 = PersonMother.activePerson();
        Person p2 = PersonMother.inactivePerson();
        Page<Person> page = new PageImpl<>(List.of(p1, p2));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Person> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));

        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenSearchIsNullAndActiveBoth() {
        Person p1 = PersonMother.activePerson();
        Person p2 = PersonMother.inactivePerson();
        Page<Person> page = new PageImpl<>(List.of(p1, p2));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Person> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenSearchIsBlank() {
        Person p2 = PersonMother.activePerson();
        Page<Person> page = new PageImpl<>(List.of(p2));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search("   ")
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Person> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenSearchHasValue() {
        Person p1 = PersonMother.activePerson();
        Person p2 = PersonMother.inactivePerson();
        Page<Person> page = new PageImpl<>(List.of(p1, p2));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search("John")
                .active(TriStateBoolean.BOTH)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Person> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenActiveTrue() {
        Page<Person> page = new PageImpl<>(List.of(PersonMother.activePerson()));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.TRUE)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Person> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

    @Test
    void paged_shouldCallRepositoryFindAll_whenActiveFalse() {
        Page<Person> page = new PageImpl<>(List.of(PersonMother.inactivePerson()));

        EntityStatefulFilter filter = EntityStatefulFilter.builder()
                .search(null)
                .active(TriStateBoolean.FALSE)
                .build();
        Pageable pageable = Pageable.unpaged();

        when(repository.findAll(anySpecification(), any(Pageable.class))).thenReturn(page);

        Page<Person> result = service.paged(filter, pageable);

        verify(repository).findAll(anySpecification(), any(Pageable.class));
        assertThat(result).isSameAs(page);
    }

}