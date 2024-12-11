package uz.gym.crm.service;
import java.util.List;

public interface BaseService<T, ID> {
    void create(T entity);
    T read(ID id);
    void update(T entity);
    void delete(ID id);
    List<T> getAll();
}

