package com.stockflow.core.branch.domain;

import com.stockflow.core.business.domain.Business;
import com.stockflow.core.shared.domain.AbstractStatefulAuditableTenantEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "branches", schema = "stockflow")
@Getter
@Setter(AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Branch extends AbstractStatefulAuditableTenantEntity {

    @Column(name = "code", nullable = false, length = 3)
    private String code;

    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 128)
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "is_main", nullable = false)
    private boolean isMain = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    public void promoteToMain() {
        this.isMain = true;
    }

    public void demoteFromMain() {
        this.isMain = false;
    }

}