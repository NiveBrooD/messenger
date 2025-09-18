package com.ramis.messenger.dao;

import java.util.Collection;

public interface Repository<T> {

    Collection<T> getAll();

    T get(Long id);

    void create(T entity);

    void update(T entity);

    void delete(T entity);




}
