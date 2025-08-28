package com.stockflow.core.business.domain;

import com.stockflow.core.organization.domain.Organization;
import com.stockflow.core.shared.domain.AbstractStatefulAuditableTenantEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "businesses", schema = "stockflow")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Business extends AbstractStatefulAuditableTenantEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "marketplace_enabled", nullable = false)
    private boolean marketplaceEnabled = false;

}