package com.engagetech.expenses.mapper;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Stream;

public interface EntityMapper<D, E> {

    E toEntity(D dto);

    D toDto(E entity);

    List<E> toEntities(List<D> dtoList);

    List<D> toDtos(List<E> entityList);

    Stream<E> toEntityStream(Stream<D> dtoList);

    Stream<D> toDtoStream(Stream<E> entityList);

    default Page<D> toDtoPage(Page<E> entity) {
        return entity.map(this::toDto);
    }
}
