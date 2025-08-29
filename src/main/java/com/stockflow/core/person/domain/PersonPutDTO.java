package com.stockflow.core.person.domain;

import com.stockflow.core.shared.domain.IdentificationType;
import com.stockflow.core.shared.domain.Language;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PersonPutDTO {

    @NotBlank
    private String firstName;

    private String secondName;

    @NotBlank
    private String surname;

    private String secondSurname;

    @NotNull
    private IdentificationType identificationType;

    @NotBlank
    private String identificationNumber;

    private Language language;

}