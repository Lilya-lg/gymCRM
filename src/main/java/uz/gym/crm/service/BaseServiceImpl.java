package uz.gym.crm.service;




import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.gym.crm.dao.BaseDAO;

import java.util.List;

public abstract class BaseServiceImpl<T, ID> implements BaseService<T, ID> {
    protected final BaseDAO<T, ID> dao;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServiceImpl.class);
    public BaseServiceImpl(BaseDAO<T, ID> dao) {
        this.dao = dao;
    }

    @Override
    public void create(T entity) {
        LOGGER.debug("Attempting to create entity: {}", entity);
        try {
            dao.create(entity);
            LOGGER.info("Entity created successfully: {}", entity);
        } catch (Exception e) {
            LOGGER.error("Error creating entity: {}", entity, e);
            throw e;
        }
    }

    @Override
    public T read(ID id) {
        LOGGER.debug("Reading entity with ID: {}", id);
        try {
            T entity = dao.read(id);
            LOGGER.info("Successfully read entity with ID: {}", id);
            return entity;
        } catch (Exception e) {
            LOGGER.error("Error reading entity with ID: {}", id, e);
            throw e;
        }
    }

    @Override
    public void update(T entity) {
        LOGGER.debug("Updating entity: {}", entity);
        try {
            dao.update(entity);
            LOGGER.info("Entity updated successfully: {}", entity);
        } catch (Exception e) {
            LOGGER.error("Error updating entity: {}", entity, e);
            throw e;
        }

    }

    @Override
    public void delete(ID id) {
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

