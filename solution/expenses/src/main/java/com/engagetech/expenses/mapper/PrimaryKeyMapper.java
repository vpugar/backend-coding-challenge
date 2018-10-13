package com.engagetech.expenses.mapper;

import com.engagetech.expenses.model.WithId;
import org.mapstruct.TargetType;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;

@Repository
public class PrimaryKeyMapper {

    private final EntityManager entityManager;

    public PrimaryKeyMapper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public <T extends WithId> T resolve(Long id, @TargetType Class<T> entityClass) {
        if (id != null) {
            try {
                T resolve = entityManager.find(entityClass, id);
                if (resolve == null) {
                    throw new EntityNotFoundException("No entity " + entityClass.getSimpleName() + " with id " + id);
                }
                return resolve;
            } catch (RuntimeException e) {
                T resolve = entityManager.find(entityClass, id);
                if (resolve == null) {
                    throw new EntityNotFoundException("No entity " + entityClass.getSimpleName() + " with id " + id);
                }
                return resolve;
            }
        } else {
            return null;
        }
    }
}
