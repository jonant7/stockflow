package com.stockflow.core.person.application;

import com.stockflow.core.person.domain.Person;
import com.stockflow.core.person.domain.PersonPostDTO;
import com.stockflow.core.person.domain.PersonPutDTO;
import com.stockflow.core.shared.domain.EntityStatefulFilter;
import com.stockflow.core.shared.infrastructure.AbstractEntityStatefulService;

import java.util.UUID;

public interface PersonService extends AbstractEntityStatefulService<Person, EntityStatefulFilter> {

    Person create(PersonPostDTO dto);

    Person findOrCreate(PersonPostDTO dto);

    Person update(UUID ID, PersonPutDTO dto);

}