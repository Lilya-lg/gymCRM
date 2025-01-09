package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import uz.gym.crm.dao.abstr.BaseDAO;
import uz.gym.crm.util.DynamicQueryBuilder;

import javax.persistence.Entity;
import java.util.*;

public class BaseDAOImpl<T> implements BaseDAO<T> {
    private final Class<T> entityType;
    private final Session session;
    private static final String SELECT_ALL_ENTITIES = "FROM %s e";
    private static final String CHECK_ENTITY_EXISTS_BY_ID = "SELECT COUNT(e) FROM %s e WHERE e.id = :id";

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
        DynamicQueryBuilder<T> queryBuilder = new DynamicQueryBuilder<>(String.format(SELECT_ALL_ENTITIES, getEntityName()));
        Query<T> query = queryBuilder.buildQuery(session, entityType);
        return query.list();
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
        DynamicQueryBuilder<Long> queryBuilder = new DynamicQueryBuilder<>(query);
        queryBuilder.addCondition("e.id = :id", "id", id);
        Query<Long> hibernateQuery = queryBuilder.buildQuery(session, Long.class);
        Long count = hibernateQuery.uniqueResult();
        return count != null && count > 0;


    }

    @Override
    public Optional<T> findByUsername(String username) {

        DynamicQueryBuilder<T> queryBuilder = new DynamicQueryBuilder<>(String.format(SELECT_ALL_ENTITIES, getEntityName()));
        queryBuilder.addCondition("e.username = :username", "username", username);
        Query<T> query = queryBuilder.buildQuery(session, entityType);
        return Optional.ofNullable(query.uniqueResult());

    }

    private String getEntityName() {
        Entity entity = entityType.getAnnotation(Entity.class);
        if (entity != null && !entity.name().isEmpty()) {
            return entity.name();
        }
        return entityType.getSimpleName();
    }
}
