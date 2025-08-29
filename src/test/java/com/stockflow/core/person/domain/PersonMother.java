package com.stockflow.core.person.domain;


import com.stockflow.core.shared.domain.IdentificationType;

public class PersonMother {

    private static final String FIRST_NAME = "Wile";
    private static final String SECOND_NAME = "E.";
    private static final String SURNAME = "Coyote";
    private static final String SECOND_SURNAME = "DOE";
    private static final String IDENTIFICATION_NUMBER = "1710034065";
    private static final String INVALID_IDENTIFICATION_NUMBER = "1798765432";
    private static final String RUC = "1710034065001";


    public static Person activePerson() {
        Person person = Person.builder()
                .firstName(FIRST_NAME)
                .secondName(SECOND_NAME)
                .surname(SURNAME)
                .secondSurname(SECOND_SURNAME)
                .identificationType(IdentificationType.ID_CARD)
                .identificationNumber(IDENTIFICATION_NUMBER)
                .build();
        person.activate();
        return person;
    }

    public static Person inactivePerson() {
        Person person = Person.builder()
                .firstName(FIRST_NAME)
                .secondName(SECOND_NAME)
                .surname(SURNAME)
                .secondSurname(SECOND_SURNAME)
                .identificationType(IdentificationType.ID_CARD)
                .identificationNumber(IDENTIFICATION_NUMBER)
                .build();
        person.deactivate();
        return person;
    }

    public static Person activeRucPerson() {
        Person person = Person.builder()
                .firstName(FIRST_NAME)
                .secondName(SECOND_NAME)
                .surname(SURNAME)
                .secondSurname(SECOND_SURNAME)
                .identificationType(IdentificationType.RUC)
                .identificationNumber(RUC)
                .build();
        person.activate();
        return person;
    }

    public static Person inactiveRucPerson() {
        Person person = Person.builder()
                .firstName(FIRST_NAME)
                .secondName(SECOND_NAME)
                .surname(SURNAME)
                .secondSurname(SECOND_SURNAME)
                .identificationType(IdentificationType.RUC)
                .identificationNumber(RUC)
                .build();
        person.deactivate();
        return person;
    }

    public static PersonPostDTO personPostDTO() {
        return PersonPostDTO.builder()
                .firstName(FIRST_NAME)
                .secondName(SECOND_NAME)
                .surname(SURNAME)
                .secondSurname(SECOND_SURNAME)
                .identificationType(IdentificationType.ID_CARD)
                .identificationNumber(IDENTIFICATION_NUMBER)
                .build();
    }

    public static PersonPostDTO personPostDTOWithInvalidId() {
        return PersonPostDTO.builder()
                .firstName(FIRST_NAME)
                .secondName(SECOND_NAME)
                .surname(SURNAME)
                .secondSurname(SECOND_SURNAME)
                .identificationType(IdentificationType.ID_CARD)
                .identificationNumber(INVALID_IDENTIFICATION_NUMBER)
                .build();
    }

    public static PersonPostDTO personPostDTOWithRUC() {
        return PersonPostDTO.builder()
                .firstName(FIRST_NAME)
                .secondName(SECOND_NAME)
                .surname(SURNAME)
                .secondSurname(SECOND_SURNAME)
                .identificationType(IdentificationType.RUC)
                .identificationNumber(RUC)
                .build();
    }

    public static PersonPutDTO personPutDTO() {
        return PersonPutDTO.builder()
                .firstName("John")
                .surname("Doe")
                .identificationType(IdentificationType.ID_CARD)
                .identificationNumber(IDENTIFICATION_NUMBER)
                .build();
    }

}