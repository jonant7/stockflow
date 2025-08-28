package com.stockflow.core.business.infrastructure;

import com.stockflow.core.business.domain.Business;
import com.stockflow.core.organization.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface BusinessRepository extends JpaRepository<Business, UUID>, JpaSpecificationExecutor<Business> {

    boolean existsByOrganizationAndEmail(Organization organization, String email);
}