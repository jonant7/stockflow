package com.stockflow.core.shared.domain;

import lombok.Getter;

import java.util.UUID;

@Getter
public class AbstractEntityDTO {

    private final UUID id;

    protected AbstractEntityDTO(UUID id) {
        this.id = id;
    }

}