package com.stockflow.core.branch.application;

import com.stockflow.core.branch.domain.Branch;
import com.stockflow.core.branch.domain.BranchPostDTO;
import com.stockflow.core.branch.domain.BranchPutDTO;
import com.stockflow.core.business.domain.Business;
import com.stockflow.core.shared.domain.EntityStatefulFilter;
import com.stockflow.core.shared.infrastructure.AbstractEntityStatefulTenantService;

import java.util.UUID;

public interface BranchService extends AbstractEntityStatefulTenantService<Branch, EntityStatefulFilter> {

    Branch create(BranchPostDTO dto, Business business);

    Branch update(UUID id, BranchPutDTO dto);
}