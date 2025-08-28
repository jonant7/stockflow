package com.stockflow.core.tenant.domain;

public class TenantMother {
    public static Tenant activeTenant() {
        Tenant tenant = Tenant.builder()
                .name("Active Tenant")
                .slug("active-tenant")
                .build();
        tenant.activate();
        return tenant;
    }

    public static Tenant inactiveTenant() {
        Tenant tenant = Tenant.builder()
                .name("Inactive Tenant")
                .slug("inactive-tenant")
                .build();
        tenant.deactivate();
        return tenant;
    }

    public static TenantPostDTO tenantPostDTO() {
        return TenantPostDTO.builder()
                .displayName("Some Tenant")
                .build();
    }

    public static TenantPutDTO tenantPutDTO() {
        return TenantPutDTO.builder()
                .displayName("Updated Tenant")
                .slug("updated-slug")
                .build();
    }

    public static TenantPutDTO tenantPutDTONullSlug() {
        return TenantPutDTO.builder()
                .displayName("Updated Tenant")
                .slug(null)
                .build();
    }

}