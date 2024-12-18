package uz.gym.crm.service;

import java.util.List;

public interface BaseService<T> {
    void create(T entity);

    T read(Long id);

    void update(T entity);

    void delete(Long id);

    List<T> getAll();
}

