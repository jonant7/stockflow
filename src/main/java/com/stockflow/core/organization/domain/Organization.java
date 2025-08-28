package com.stockflow.core.organization.domain;

import com.stockflow.core.shared.domain.AbstractStatefulAuditableTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "organizations", schema = "stockflow")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Organization extends AbstractStatefulAuditableTenantEntity {

    @Column(name = "tin", unique = true, nullable = false, length = 13)
    private String tin;

    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", nullable = false, length = 128)
    private String email;

    @Column(name = "marketplace_enabled", nullable = false)
    private boolean marketplaceEnabled = false;

}