package com.engagetech.expenses.mapper;

public interface EntityMapper<D, E> {

    D toDto(E entity);
}
