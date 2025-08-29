package com.stockflow.core.branch.infrastructure;

import com.stockflow.core.branch.domain.Branch;
import com.stockflow.core.business.domain.Business;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID>, JpaSpecificationExecutor<Branch> {

    boolean existsByBusinessAndCode(Business business, String code);

    Optional<Branch> findByBusinessAndIsMainIsTrue(Business business);
}