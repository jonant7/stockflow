package com.stockflow.core.person.infrastructure;

import com.stockflow.core.person.domain.Person;
import com.stockflow.core.shared.domain.IdentificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, UUID>, JpaSpecificationExecutor<Person> {

    Optional<Person> findByIdentificationTypeAndIdentificationNumber(IdentificationType identificationType, String identificationNumber);

}