package com.stockflow.core.shared.infrastructure;

import com.stockflow.core.shared.domain.AbstractStatefulAuditableEntity;
import com.stockflow.core.shared.domain.EntityStatefulFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface AbstractEntityStatefulService<E extends AbstractStatefulAuditableEntity, F extends EntityStatefulFilter> {

    Optional<E> find(UUID id);

    Collection<E> list();

    E getByIdThrowingException(UUID id);

    E toggleActiveStatus(UUID id);

    Page<E> paged(F filter, Pageable pageable);

}