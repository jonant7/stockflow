package com.stockflow.core.shared.domain;

import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Objects;

@Setter
@SuperBuilder
public class EntityStatefulFilter extends EntityFilter {

    private final TriStateBoolean active;

    public TriStateBoolean getActive() {
        return Objects.isNull(active) ? TriStateBoolean.BOTH : active;
    }
}