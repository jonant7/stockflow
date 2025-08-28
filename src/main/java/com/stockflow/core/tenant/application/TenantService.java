package com.stockflow.core.tenant.application;

import com.stockflow.core.shared.domain.EntityStatefulFilter;
import com.stockflow.core.shared.infrastructure.AbstractEntityStatefulService;
import com.stockflow.core.tenant.domain.Tenant;
import com.stockflow.core.tenant.domain.TenantPostDTO;
import com.stockflow.core.tenant.domain.TenantPutDTO;

import java.util.UUID;

public interface TenantService extends AbstractEntityStatefulService<Tenant, EntityStatefulFilter> {

    Tenant create(TenantPostDTO dto);

    Tenant update(UUID id, TenantPutDTO dto);

    boolean existsBySlug(String slug);

}