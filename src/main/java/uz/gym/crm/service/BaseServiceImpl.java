package uz.gym.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.gym.crm.dao.BaseDAO;

import java.util.List;

public abstract class BaseServiceImpl<T> implements BaseService<T> {
    // protected final JpaRepository<T, Long> repository;
    protected final BaseDAO<T> dao;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServiceImpl.class);

    public BaseServiceImpl(BaseDAO<T> dao) {
        this.dao = dao;
    }

    @Override
    public void create(T entity) {
        LOGGER.debug("Attempting to create entity: {}", entity);
        try {
            dao.save(entity);
            LOGGER.info("Entity created successfully: {}", entity);
        } catch (Exception e) {
            LOGGER.error("Error creating entity: {}", entity, e);
            throw e;
        }
    }

    public T read(Long id) {
        LOGGER.debug("Reading entity with ID: {}", id);
        try {
            return dao.read(id)
                    .orElseThrow(() -> new IllegalArgumentException("Entity not found for ID: " + id));
        } catch (Exception e) {
            LOGGER.error("Error reading entity with ID: {}", id, e);
            throw e;
        }
    }

    public void update(T entity) {
        LOGGER.debug("Updating entity: {}", entity);
        try {
            Long id = extractId(entity);
            if (!dao.existsById(id)) {
                LOGGER.warn("Entity not found for update: ID={}", id);
                throw new IllegalArgumentException("Entity not found for update: ID=" + id);
            }
            dao.save(entity);
            LOGGER.info("Entity updated successfully: {}", entity);
        } catch (Exception e) {
            LOGGER.error("Error updating entity: {}", entity, e);
            throw e;
        }
    }

    private Long extractId(T entity) {
        try {
            var method = entity.getClass().getMethod("getId");
            return (Long) method.invoke(entity);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to extract ID from entity: " + entity, e);
        }
    }

    public void delete(Long id) {
        LOGGER.debug("Deleting entity with ID: {}", id);
        try {
            dao.delete(id);
            LOGGER.info("Entity deleted successfully with ID: {}", id);
        } catch (Exception e) {
            LOGGER.error("Error deleting entity with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public List<T> getAll() {
        LOGGER.debug("Fetching all entities");
        try {
            List<T> entities = dao.getAll();
            LOGGER.info("Successfully fetched {} entities", entities.size());
            return entities;
        } catch (Exception e) {
            LOGGER.error("Error fetching all entities", e);
            throw e;
        }
    }
}

