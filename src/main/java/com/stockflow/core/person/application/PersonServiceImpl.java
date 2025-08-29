package com.stockflow.core.person.application;

import com.stockflow.core.person.domain.Person;
import com.stockflow.core.person.domain.PersonPostDTO;
import com.stockflow.core.person.domain.PersonPutDTO;
import com.stockflow.core.person.infrastructure.PersonRepository;
import com.stockflow.core.shared.domain.EntityStatefulFilter;
import com.stockflow.core.shared.domain.Language;
import com.stockflow.core.shared.exception.BusinessException;
import com.stockflow.core.shared.exception.EntityNotFoundException;
import com.stockflow.core.shared.infrastructure.SpecificationUtils;
import com.stockflow.core.util.IdCardUtils;
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
public class PersonServiceImpl implements PersonService {

    private final PersonRepository repository;

    @Override
    public Person create(PersonPostDTO dto) {
        String idNumber = dto.getIdentificationNumber().trim();

        repository.findByIdentificationTypeAndIdentificationNumber(
                        dto.getIdentificationType(), idNumber)
                .ifPresent(p -> {
                    throw new BusinessException("error.person.already_exists_by_identification");
                });

        IdCardUtils.validate(idNumber, dto.getIdentificationType());

        Person person = Person.builder()
                .firstName(dto.getFirstName().trim())
                .secondName(Objects.nonNull(dto.getSecondName()) ? dto.getSecondName().trim() : null)
                .surname(dto.getSurname().trim())
                .secondSurname(Objects.nonNull(dto.getSecondSurname()) ? dto.getSecondSurname().trim() : null)
                .identificationType(dto.getIdentificationType())
                .identificationNumber(idNumber)
                .language(Language.ES)
                .build();

        person.activate();
        return repository.save(person);
    }

    @Override
    public Person findOrCreate(PersonPostDTO dto) {
        return repository.findByIdentificationTypeAndIdentificationNumber(dto.getIdentificationType(), dto.getIdentificationNumber())
                .orElseGet(() -> create(dto));
    }

    @Override
    public Person update(UUID ID, PersonPutDTO dto) {
        // TODO: Implement method and test (see PersonServiceImplTest.class)
        throw new UnsupportedOperationException("Method not implemented");
    }

    @Override
    public Optional<Person> find(UUID id) {
        return repository.findById(id);
    }

    @Override
    public Collection<Person> list() {
        return repository.findAll();
    }

    @Override
    public Person getByIdThrowingException(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("message.entity-not-found"));
    }

    @Override
    public Person toggleActiveStatus(UUID id) {
        final var product = getByIdThrowingException(id);
        if (product.getActive()) {
            product.deactivate();
        } else {
            product.activate();
        }
        return repository.save(product);
    }

    @Override
    public Page<Person> paged(EntityStatefulFilter filter, Pageable pageable) {
        Specification<Person> spec = SpecificationUtils.withActive(filter.getActive());

        Optional<String> search = filter.getSearch();
        if (search.isPresent() && !search.get().isBlank()) {
            String trimmedSearch = search.get().trim();

            Specification<Person> searchSpec = SpecificationUtils.<Person>withSearch(trimmedSearch, "identificationNumber")
                    .or(SpecificationUtils.withSearch(trimmedSearch, "firstName"))
                    .or(SpecificationUtils.withSearch(trimmedSearch, "secondName"))
                    .or(SpecificationUtils.withSearch(trimmedSearch, "surname"))
                    .or(SpecificationUtils.withSearch(trimmedSearch, "secondSurname"));

            spec = spec.and(searchSpec);
        }
        return repository.findAll(spec, pageable);
    }

}