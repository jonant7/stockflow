package com.stockflow.core.shared.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
@Setter(AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractStatefulAuditableEntity extends AbstractAuditableEntity {

    @Column(name = "active", nullable = false)
    private Boolean active = Boolean.TRUE;

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

}