package finances.finances.specifications;

import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class BaseSpecification {

    public static <T> Specification<T> equal(String field, Object value) {
        return (root, query, cb) ->
                value == null ? null : cb.equal(root.get(field), value);
    }

    public static <T> Specification<T> equalIgnoreCase(String field, String value) {
        return (root, query, cb) ->
                value == null ? null : cb.equal(cb.lower(root.get(field)), value.toLowerCase());
    }

    public static <T> Specification<T> joinEqual(String joinField, String nestedField, Object value) {
        return (root, query, cb) ->
                value == null ? null : cb.equal(root.get(joinField).get(nestedField), value);
    }

    public static <T> Specification<T> between(String field, Double min, Double max) {
        return (root, query, cb) -> {
            if (min == null && max == null) return null;
            Path<Double> path = root.get(field);
            if (min == null) return cb.lessThanOrEqualTo(path, max);
            if (max == null) return cb.greaterThanOrEqualTo(path, min);
            return cb.between(path, min, max);
        };
    }

    public static <T> Specification<T> dateTimeBetween(String field, LocalDateTime from, LocalDateTime to) {
        return (root, query, cb) -> {
            if (from == null && to == null) return null;
            Path<LocalDateTime> path = root.get(field);
            if (from == null) return cb.lessThanOrEqualTo(path, to);
            if (to == null) return cb.greaterThanOrEqualTo(path, from);
            return cb.between(path, from, to);
        };
    }

    public static <T> Specification<T> contains(String field, String value) {
        return (root, query, cb) ->
                value == null ? null :
                        cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
    }

    public static <T> Specification<T> isTrue(String field) {
        return (root, query, cb) -> cb.isTrue(root.get(field));
    }

    public static <T> Specification<T> isFalse(String field) {
        return (root, query, cb) -> cb.isFalse(root.get(field));
    }
}