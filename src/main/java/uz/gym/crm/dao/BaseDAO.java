package uz.gym.crm.dao;

import java.util.List;
import java.util.Optional;

public interface BaseDAO<T> {
    void save(T entity);

    Optional<T> read(Long id);

    void update(T entity);

    void delete(Long id);

    List<T> getAll();

    boolean existsById(Long id);

    Optional<T> findByUsername(String username);

}