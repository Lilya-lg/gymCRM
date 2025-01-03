package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import java.util.*;

public class BaseDAOImpl<T> implements BaseDAO<T> {
    private final Class<T> entityType;
    private final Session session;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDAOImpl.class);
    private static final String SELECT_ENTITY_BY_ID = "FROM %s WHERE id = :id";
    private static final String SELECT_ALL_ENTITIES = "FROM %s";
    private static final String CHECK_ENTITY_EXISTS_BY_ID = "SELECT COUNT(e) > 0 FROM %s e WHERE e.id = :id";
    private static final String SELECT_ENTITY_BY_USERNAME = "FROM %s WHERE username = :username";

    public BaseDAOImpl(Class<T> entityType, Session session) {

        this.entityType = entityType;
        this.session = session;
    }


    @Override
    public void save(T entity) {
        Transaction transaction = null;

        try {
            if (session.getTransaction().isActive()) {
                session.save(entity);
            } else {
                transaction = session.beginTransaction();
                session.save(entity);
                transaction.commit();
            }
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public Optional<T> read(Long id) {

        return Optional.ofNullable(session.get(entityType, id));

    }

    @Override
    public List<T> getAll() {
        String query = String.format(SELECT_ALL_ENTITIES, getEntityName());
        return session.createQuery(query, entityType).list();
    }

    @Override
    public void update(T entity) {
        Transaction transaction = null;

        transaction = session.beginTransaction();
        session.update(entity);
        transaction.commit();
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;

        transaction = session.beginTransaction();
        T entity = session.get(entityType, id);
        if (entity != null) {
            session.delete(entity);
        }
        transaction.commit();

    }

    @Override
    public boolean existsById(Long id) {
        String query = String.format(CHECK_ENTITY_EXISTS_BY_ID, getEntityName());
        return session.createQuery(query, Boolean.class).setParameter("id", id).uniqueResult();


    }

    @Override
    public Optional<T> findByUsername(String username) {

        String query = String.format(SELECT_ENTITY_BY_USERNAME, getEntityName());
        T result = session.createQuery(query, entityType).setParameter("username", username).uniqueResult();
        return Optional.ofNullable(result);

    }

    private String getEntityName() {
        Entity entity = entityType.getAnnotation(Entity.class);
        if (entity != null && !entity.name().isEmpty()) {
            return entity.name();
        }
        return entityType.getSimpleName();
    }
}
