package uz.gym.crm.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.gym.crm.domain.BaseEntity;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public abstract class BaseDAOImpl<T extends BaseEntity> implements BaseDAO<T> {
    protected final Map<Long, T> storage;
    private final Function<T, Long> idExtractor;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDAOImpl.class);

    public BaseDAOImpl(Map<Long, T> storage, Function<T, Long> idExtractor) {
        this.storage = storage;
        this.idExtractor = idExtractor;
    }

    @Override
    public void create(T entity) {

        Long id = idExtractor.apply(entity);
        LOGGER.debug("Attempting to create entity with ID: {}", id);
        storage.put(id, entity);
        LOGGER.info("Entity created successfully with ID: {}", id);
    }

    @Override
    public Optional<T> read(Long id) {
        LOGGER.debug("Attempting to read entity with ID: {}", id);
        T entity = storage.get(id);
        if (entity != null) {
            LOGGER.info("Entity read successfully with ID: {}", id);
        } else {
            LOGGER.warn("Entity not found with ID: {}", id);
        }
        return Optional.ofNullable(entity);
    }

    @Override
    public void update(T entity) {
        Long id = idExtractor.apply(entity);
        LOGGER.debug("Attempting to update entity with ID: {}", id);
        if (storage.containsKey(id)) {
            storage.put(id, entity);
            LOGGER.info("Entity updated successfully with ID: {}", id);
        } else {
            LOGGER.warn("Entity not found for update with ID: {}", id);
        }
    }

    @Override
    public void delete(Long id) {
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

    @Override
    public Optional<T> findById(Long id) {
     return Optional.ofNullable(storage.get(id));
    }

}
