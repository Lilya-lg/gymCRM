package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import uz.gym.crm.dao.abstr.BaseDAO;
import uz.gym.crm.util.DynamicQueryBuilder;

import javax.persistence.Entity;
import java.util.*;

public class BaseDAOImpl<T> implements BaseDAO<T> {
    private final Class<T> entityType;
    private static final String SELECT_ALL_ENTITIES = "FROM %s e";
    private static final String CHECK_ENTITY_EXISTS_BY_ID = "SELECT COUNT(e) FROM %s e WHERE e.id = :id";
    private final SessionFactory sessionFactory;

    public BaseDAOImpl(Class<T> entityType, SessionFactory sessionFactory) {

        this.entityType = entityType;
        this.sessionFactory = sessionFactory;
    }

    private Session getSession() {
        if (TransactionSynchronizationManager.isActualTransactionActive()) {
            return sessionFactory.getCurrentSession();
        } else {
            return sessionFactory.openSession();
        }
    }

    @Override
    public void save(T entity) {
        Transaction transaction = null;

        try {
            if (getSession().getTransaction().isActive()) {
                getSession().save(entity);
            } else {
                transaction = getSession().beginTransaction();
                getSession().save(entity);
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

        return Optional.ofNullable(getSession().get(entityType, id));

    }

    @Override
    public List<T> getAll() {
        DynamicQueryBuilder<T> queryBuilder = new DynamicQueryBuilder<>(String.format(SELECT_ALL_ENTITIES, getEntityName()));
        Query<T> query = queryBuilder.buildQuery(getSession(), entityType);
        return query.list();
    }

    @Override
    public void update(T entity) {

        getSession().update(entity);

    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;

        transaction = getSession().beginTransaction();
        T entity = getSession().get(entityType, id);
        if (entity != null) {
            getSession().delete(entity);
        }
        transaction.commit();

    }

    @Override
    public boolean existsById(Long id) {
        String query = String.format(CHECK_ENTITY_EXISTS_BY_ID, getEntityName());
        DynamicQueryBuilder<Long> queryBuilder = new DynamicQueryBuilder<>(query);
        queryBuilder.addCondition("e.id = :id", "id", id);
        Query<Long> hibernateQuery = queryBuilder.buildQuery(getSession(), Long.class);
        Long count = hibernateQuery.uniqueResult();
        return count != null && count > 0;


    }

    @Override
    public Optional<T> findByUsername(String username) {
        DynamicQueryBuilder<T> queryBuilder = new DynamicQueryBuilder<>(String.format(SELECT_ALL_ENTITIES, getEntityName()));
        queryBuilder.addCondition("e.username = :username", "username", username);
        Query<T> query = queryBuilder.buildQuery(getSession(), entityType);

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
