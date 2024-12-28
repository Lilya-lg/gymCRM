package uz.gym.crm.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uz.gym.crm.config.HibernateUtil;

import java.util.*;

public  class BaseDAOImpl<T> implements BaseDAO<T> {
    private final Class<T> entityType;
    //private final SessionFactory sessionFactory;
    private final Session session;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDAOImpl.class);
    public BaseDAOImpl(Class<T> entityType,Session session) {

        this.entityType = entityType;
        this.session = session;
    }


    @Override
    public void save(T entity) {
        Transaction transaction = null;

        try {
            // Check if there's an active transaction
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

            return session.createQuery("FROM " + entityType.getSimpleName(), entityType).list();

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

            String query = "SELECT COUNT(e) > 0 FROM " + entityType.getSimpleName() + " e WHERE e.id = :id";
            return session.createQuery(query, Boolean.class)
                    .setParameter("id", id)
                    .uniqueResult();

    }

    @Override
    public Optional<T> findByUsername(String username) {

            String query = "FROM " + entityType.getSimpleName() + " WHERE username = :username";
            T result = session.createQuery(query, entityType)
                    .setParameter("username", username)
                    .uniqueResult();
            return Optional.ofNullable(result);

    }
}
