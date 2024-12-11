package uz.gym.crm.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class BaseDAOImpl<T, ID> implements BaseDAO<T, ID> {
    protected final Map<ID, T> storage = new ConcurrentHashMap<>();
    private final Function<T, ID> idExtractor;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDAOImpl.class);

    public BaseDAOImpl(Function<T, ID> idExtractor) {
        this.idExtractor = idExtractor;
    }

    @Override
    public void create(T entity) {

        ID id = idExtractor.apply(entity);
        LOGGER.debug("Attempting to create entity with ID: {}", id);
        storage.put(id, entity);
        LOGGER.info("Entity created successfully with ID: {}", id);
    }

    @Override
    public T read(ID id) {
        LOGGER.debug("Attempting to read entity with ID: {}", id);
        T entity = storage.get(id);
        if (entity != null) {
            LOGGER.info("Entity read successfully with ID: {}", id);
        } else {
            LOGGER.warn("Entity not found with ID: {}", id);
        }
        return entity;
    }

    @Override
    public void update(T entity) {
        ID id = idExtractor.apply(entity);
        LOGGER.debug("Attempting to update entity with ID: {}", id);
        if (storage.containsKey(id)) {
            storage.put(id, entity);
            LOGGER.info("Entity updated successfully with ID: {}", id);
        } else {
            LOGGER.warn("Entity not found for update with ID: {}", id);
        }
    }

    @Override
    public void delete(ID id) {
        LOGGER.debug("Attempting to delete entity with ID: {}", id);
        if (storage.remove(id) != null) {
            LOGGER.info("Entity deleted successfully with ID: {}", id);
        } else {
            LOGGER.warn("Entity not found for deletion with ID: {}", id);
        }
    }

    @Override
    public List<T> getAll() {
        LOGGER.debug("Attempting to fetch all entities from storage");
        List<T> entities = new ArrayList<>(storage.values());
        LOGGER.info("Fetched {} entities from storage", entities.size());
        return entities;

    }
}
