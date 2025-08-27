package com.stockflow.core.shared.infrastructure;

import com.stockflow.core.shared.domain.TriStateBoolean;
import jakarta.persistence.criteria.Expression;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.Objects;

@NoArgsConstructor
public final class SpecificationUtils {

    public static <T> Specification<T> withActive(TriStateBoolean active) {
        return (root, query, cb) -> {
            Expression<Boolean> activeExpr = root.get("active");
            return switch (active) {
                case TRUE -> cb.isTrue(activeExpr);
                case FALSE -> cb.isFalse(activeExpr);
                case BOTH -> cb.conjunction();
            };
        };
    }

    public static <T> Specification<T> withSearch(String search, String searchFieldName) {
        return (root, query, cb) -> {
            if (Objects.isNull(search) || search.isBlank()) {
                return cb.conjunction();
            }
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.like(cb.lower(root.get(searchFieldName).as(String.class)), pattern);
        };
    }

}
