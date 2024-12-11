package uz.gym.crm.dao;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class BaseDAOTest<T, ID> {

    protected abstract BaseDAO<T, ID> getDAO();

    protected abstract T createEntity();

    @Test
    void testCreateAndRead() {
        T entity = createEntity();
        getDAO().create(entity);
        List<T> allEntities = getDAO().getAll();
        assertTrue(allEntities.contains(entity));
    }

    @Test
    void testUpdate() {
        T entity = createEntity();
        getDAO().create(entity);
        T updatedEntity = updateEntity(entity);
        getDAO().update(updatedEntity);
        assertEquals(updatedEntity, getDAO().read(getId(entity)));
    }

    @Test
    void testDelete() {
        T entity = createEntity();
        getDAO().create(entity);
        getDAO().delete(getId(entity));
        assertNull(getDAO().read(getId(entity)));
    }

    protected abstract T updateEntity(T entity);

    protected abstract ID getId(T entity);
}
