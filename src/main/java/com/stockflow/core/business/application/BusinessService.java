package com.stockflow.core.business.application;

import com.stockflow.core.business.domain.Business;
import com.stockflow.core.business.domain.BusinessPostDTO;
import com.stockflow.core.business.domain.BusinessPutDTO;
import com.stockflow.core.organization.domain.Organization;
import com.stockflow.core.shared.domain.EntityStatefulFilter;
import com.stockflow.core.shared.infrastructure.AbstractEntityStatefulTenantService;

import java.util.UUID;

public interface BusinessService extends AbstractEntityStatefulTenantService<Business, EntityStatefulFilter> {

    Business create(BusinessPostDTO dto, Organization organization);

    Business update(UUID id, BusinessPutDTO dto);

}