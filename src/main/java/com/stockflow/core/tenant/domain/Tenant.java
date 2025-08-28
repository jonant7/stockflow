package com.stockflow.core.tenant.domain;

import com.stockflow.core.shared.domain.AbstractStatefulAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Table(name = "tenants", schema = "stockflow")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Tenant extends AbstractStatefulAuditableEntity {

    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Pattern(regexp = "^[a-z0-9-]+$")
    @Column(name = "slug", nullable = false, unique = true, length = 64)
    private String slug;

    public void update(TenantPutDTO putDTO, String slug){
        this.name = putDTO.getDisplayName();
        this.slug = slug;
    }

}