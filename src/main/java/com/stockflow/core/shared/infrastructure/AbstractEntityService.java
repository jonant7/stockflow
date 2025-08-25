package com.stockflow.core.shared.infrastructure;

import com.stockflow.core.shared.domain.AbstractEntity;
import com.stockflow.core.shared.domain.EntityFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface AbstractEntityService<E extends AbstractEntity, F extends EntityFilter> {

    Optional<E> find(UUID id);

    Collection<E> list();

    E getByIdThrowingException(UUID id);

    Page<E> paged(F filter, Pageable pageable);

    void delete(UUID id);

}