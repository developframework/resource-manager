package com.github.developframework.resource.spring.jpa;

import com.github.developframework.expression.ExpressionUtils;
import com.github.developframework.resource.AddCheckExistsLogic;
import com.github.developframework.resource.Entity;
import com.github.developframework.resource.ResourceDefinition;
import com.github.developframework.resource.exception.ResourceExistException;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.stream.Stream;

/**
 * @author qiushui on 2019-08-21.
 */
public class ByFieldJpaAddCheckExistsLogic<
        ENTITY extends Entity<ID>,
        DTO extends com.github.developframework.resource.DTO,
        ID extends Serializable
        > implements AddCheckExistsLogic<ENTITY, DTO, ID> {

    private String[] fields;

    private ResourceDefinition<ENTITY> resourceDefinition;

    private EntityManager entityManager;

    public ByFieldJpaAddCheckExistsLogic(ResourceDefinition<ENTITY> resourceDefinition, EntityManager entityManager, String... fields) {
        this.fields = fields;
        this.resourceDefinition = resourceDefinition;
        this.entityManager = entityManager;
    }

    @Override
    public boolean check(DTO dto) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<ENTITY> root = query.from(resourceDefinition.getEntityClass());
        query.multiselect(
                builder.count(builder.literal(1)).as(Integer.class)
        ).where(
                Stream
                        .of(fields)
                        .map(fieldName -> {
                            Object value = ExpressionUtils.getValue(dto, fieldName);
                            return builder.equal(root.get(fieldName), value);
                        })
                        .toArray(Predicate[]::new)
        );
        Tuple tuple = entityManager.createQuery(query).getSingleResult();
        return tuple.get(0, Integer.class) > 0;
    }

    @Override
    public ResourceExistException getResourceExistException(DTO dto, String resourceName) {
        ResourceExistException exception = new ResourceExistException(resourceName);
        for (String field : fields) {
            exception.addParameter(field, ExpressionUtils.getValue(dto, field));
        }
        return exception;
    }
}
