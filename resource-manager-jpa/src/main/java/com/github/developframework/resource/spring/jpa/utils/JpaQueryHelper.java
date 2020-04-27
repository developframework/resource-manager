package com.github.developframework.resource.spring.jpa.utils;

import com.github.developframework.resource.spring.jpa.PO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.support.PageableExecutionUtils;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.util.List;
import java.util.Optional;

/**
 * JPA查询助手
 *
 * @author qiushui on 2020-04-22.
 */
public final class JpaQueryHelper {

    public static <T extends PO<?>> Order[] sortToOrders(Root<T> root, CriteriaBuilder builder, Sort sort) {
        if (sort != null) {
            return sort
                    .get()
                    .map(o -> o.getDirection() == Sort.Direction.ASC ? builder.asc(root.get(o.getProperty())) : builder.desc(root.get(o.getProperty())))
                    .toArray(Order[]::new);
        }
        return null;
    }

    /**
     * 查询列表
     */
    public static <T extends PO<?>, R> List<R> queryList(EntityManager entityManager, Sort sort, Class<T> entityClass, Class<R> resultClass, HandleSelect<T> handleSelect, Specification<T> specification) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<R> query = builder.createQuery(resultClass);
        final Root<T> root = query.from(entityClass);
        query.multiselect(handleSelect.handle(root, builder)).where(specification.toPredicate(root, query, builder)).orderBy(sortToOrders(root, builder, sort));
        return entityManager.createQuery(query).getResultList();
    }

    public static <T extends PO<?>> List<T> queryList(EntityManager entityManager, Sort sort, Class<T> entityClass, HandleSelect<T> handleSelect, Specification<T> specification) {
        return queryList(entityManager, sort, entityClass, entityClass, handleSelect, specification);
    }

    /**
     * 查询单个
     */
    public static <T extends PO<?>, R> Optional<R> queryOne(EntityManager entityManager, Class<T> entityClass, Class<R> resultClass, HandleSelect<T> handleSelect, Specification<T> specification) {
        final List<R> list = queryList(entityManager, null, entityClass, resultClass, handleSelect, specification);
        if (list.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(0));
        }
    }

    public static <T extends PO<?>> Optional<T> queryOne(EntityManager entityManager, Class<T> entityClass, HandleSelect<T> handleSelect, Specification<T> specification) {
        return queryOne(entityManager, entityClass, entityClass, handleSelect, specification);
    }

    /**
     * 查询个数
     */
    public static <T extends PO<?>> long queryCount(EntityManager entityManager, Class<T> entityClass, Specification<T> specification) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Tuple> query = builder.createTupleQuery();
        final Root<T> root = query.from(entityClass);
        return entityManager
                .createQuery(query.multiselect(builder.count(root).as(Long.class)).where(specification.toPredicate(root, query, builder)))
                .getSingleResult()
                .get(0, Long.class);
    }

    /**
     * 查询分页
     */
    public static <T extends PO<?>, R> Page<R> queryPager(EntityManager entityManager, Pageable pageable, Class<T> entityClass, Class<R> resultClass, HandleSelect<T> handleSelect, Specification<T> specification) {
        final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<R> query = builder.createQuery(resultClass);
        final Root<T> root = query.from(entityClass);
        query.multiselect(handleSelect.handle(root, builder)).where(specification.toPredicate(root, query, builder)).orderBy(sortToOrders(root, builder, pageable.getSort()));
        final List<R> content = entityManager.createQuery(query).setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();
        final long count = queryCount(entityManager, entityClass, specification);
        return PageableExecutionUtils.getPage(content, pageable, () -> count);
    }

    @FunctionalInterface
    public interface HandleSelect<T> {

        Selection<?>[] handle(Root<T> root, CriteriaBuilder builder);
    }
}
