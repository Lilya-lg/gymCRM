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
    private final SessionFactory sessionFactory;
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseDAOImpl.class);
    public BaseDAOImpl(Class<T> entityType) {

        this.entityType = entityType;
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    public BaseDAOImpl(Class<T> entityType, SessionFactory sessionFactory) {
        this.entityType = entityType;
        this.sessionFactory = sessionFactory;
    }
    protected SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }
    @Override
    public void save(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(entity);
            LOGGER.debug("Attempting to create entity");
            transaction.commit();
            LOGGER.info("Entity created successfully");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public Optional<T> read(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return Optional.ofNullable(session.get(entityType, id));
        }
    }

    @Override
    public List<T> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM " + entityType.getSimpleName(), entityType).list();
        }
    }

    @Override
    public void update(T entity) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            T entity = session.get(entityType, id);
            if (entity != null) {
                session.delete(entity);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    @Override
    public boolean existsById(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String query = "SELECT COUNT(e) > 0 FROM " + entityType.getSimpleName() + " e WHERE e.id = :id";
            return session.createQuery(query, Boolean.class)
                    .setParameter("id", id)
                    .uniqueResult();
        }
    }

    @Override
    public Optional<T> findByUsername(String username) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String query = "FROM " + entityType.getSimpleName() + " WHERE username = :username";
            T result = session.createQuery(query, entityType)
                    .setParameter("username", username)
                    .uniqueResult();
            return Optional.ofNullable(result);
        }
    }
}
