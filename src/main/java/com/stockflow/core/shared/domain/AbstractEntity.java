package com.stockflow.core.shared.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@MappedSuperclass
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractEntity {

    @Id
    @GeneratedValue(generator = "uuid4", strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false, insertable = false, updatable = false, unique = true, columnDefinition = "CHAR(36)")
    private UUID id;

}