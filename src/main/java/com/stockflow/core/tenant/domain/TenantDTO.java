package com.stockflow.core.tenant.domain;

import com.stockflow.core.shared.domain.AbstractEntityDTO;
import lombok.Getter;

@Getter
public class TenantDTO extends AbstractEntityDTO {

    private final String name;
    private final String slug;

    public TenantDTO(Tenant entity) {
        super(entity.getId());
        this.name = entity.getName();
        this.slug = entity.getSlug();
    }

}