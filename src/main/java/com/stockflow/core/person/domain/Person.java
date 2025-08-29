package com.stockflow.core.person.domain;

import com.stockflow.core.shared.domain.AbstractStatefulAuditableEntity;
import com.stockflow.core.shared.domain.IdentificationType;
import com.stockflow.core.shared.domain.Language;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "persons", schema = "stockflow")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Person extends AbstractStatefulAuditableEntity {

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "second_name")
    private String secondName;

    @Column(name = "surname", nullable = false)
    private String surname;

    @Column(name = "second_surname")
    private String secondSurname;

    @Enumerated(EnumType.STRING)
    @Column(name = "identification_type", nullable = false)
    private IdentificationType identificationType;

    @Column(name = "identification_number", nullable = false)
    private String identificationNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "language", nullable = false, length = 5)
    private Language language;

}