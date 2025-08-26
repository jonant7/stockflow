package com.stockflow.core.shared.domain;

import com.stockflow.core.config.ApplicationContext;
import com.stockflow.core.tenant.domain.Tenant;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class TenantEntityListener {

    @PrePersist
    @PreUpdate
    public void setTenant(Object entity) {
        boolean isTenantScoped = entity instanceof AbstractAuditableTenantEntity || entity instanceof AbstractStatefulAuditableTenantEntity;
        if (!isTenantScoped) {
            return;
        }

        Tenant tenant = ApplicationContext.getCurrentTenant()
                .orElseThrow(() -> new IllegalStateException(
                        "No tenant available in context for tenant-scoped entity: "
                                + entity.getClass().getSimpleName()
                ));

        if (entity instanceof AbstractAuditableTenantEntity auditable) {
            auditable.setTenant(tenant);
        } else {
            ((AbstractStatefulAuditableTenantEntity) entity).setTenant(tenant);
        }
    }
}