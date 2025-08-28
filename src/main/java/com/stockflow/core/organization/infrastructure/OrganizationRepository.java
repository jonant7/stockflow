package com.stockflow.core.organization.infrastructure;

import com.stockflow.core.organization.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<Organization, UUID>, JpaSpecificationExecutor<Organization> {

    Optional<Organization> findByTin(String tin);

    boolean existsByTin(String tin);

    boolean existsByEmail(String email);

}