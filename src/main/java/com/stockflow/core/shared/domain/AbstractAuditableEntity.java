package com.stockflow.core.shared.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@MappedSuperclass
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditableEntity extends AbstractEntity {

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 64)
    private String createdBy;

    @CreatedDate
    @Column(name = "created_date", nullable = false)
    private OffsetDateTime createdDate;

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 64)
    private String lastModifiedBy;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private OffsetDateTime lastModifiedDate;

}