package com.stockflow.core.tenant.infrastructure;

import com.stockflow.core.tenant.domain.Tenant;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID>, JpaSpecificationExecutor<Tenant> {

    boolean existsBySlug(@Pattern(regexp = "^[a-z0-9-]+$") String slug);

}