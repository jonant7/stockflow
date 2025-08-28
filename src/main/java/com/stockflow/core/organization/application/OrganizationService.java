package com.stockflow.core.organization.application;

import com.stockflow.core.organization.domain.Organization;
import com.stockflow.core.organization.domain.OrganizationPostDTO;
import com.stockflow.core.organization.domain.OrganizationPutDTO;
import com.stockflow.core.shared.domain.EntityStatefulFilter;
import com.stockflow.core.shared.infrastructure.AbstractEntityStatefulTenantService;

public interface OrganizationService extends AbstractEntityStatefulTenantService<Organization, EntityStatefulFilter> {

    Organization create(OrganizationPostDTO dto);

    Organization update(OrganizationPutDTO dto);

}