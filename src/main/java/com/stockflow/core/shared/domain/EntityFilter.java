package com.stockflow.core.shared.domain;

import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Optional;


@Setter
@SuperBuilder
public class EntityFilter {

    private final String search;

    public Optional<String> getSearch() {
        return Optional.ofNullable(search);
    }
}
