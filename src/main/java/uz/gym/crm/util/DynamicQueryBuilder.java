package uz.gym.crm.util;


import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.HashMap;
import java.util.Map;

public class DynamicQueryBuilder<T> {
    private final StringBuilder queryBuilder;
    private final Map<String, Object> parameters = new HashMap<>();

    public DynamicQueryBuilder(String baseQuery) {
        this.queryBuilder = new StringBuilder(baseQuery);
    }

    public DynamicQueryBuilder<T> addCondition(String condition, String paramName, Object value) {
        if (value != null) {
            if (!queryBuilder.toString().contains("WHERE")) {
                queryBuilder.append(" WHERE ");
            } else {
                queryBuilder.append(" AND ");
            }
            queryBuilder.append(condition);
            parameters.put(paramName, value);
        }
        return this;
    }

    public Query<T> buildQuery(Session session, Class<T> resultClass) {
        Query<T> query = session.createQuery(queryBuilder.toString(), resultClass);
        parameters.forEach(query::setParameter);
        return query;
    }
}
