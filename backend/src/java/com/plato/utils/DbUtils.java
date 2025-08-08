package com.plato.utils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Order;

import java.util.List;

public class DbUtils {

    public <T> Criteria simpleSearch(Session session, Class<T> clazz, String col, Object value) {
        Criteria criteria = session.createCriteria(clazz);
        criteria.add(Restrictions.eq(col, value));
        return criteria;
    }

    public <T> Criteria multiSearch(Session session, Class<T> clazz, String[] cols, Object[] values) {
        if (cols.length != values.length) {
            throw new IllegalArgumentException("Column and value arrays must have the same length");
        }
        Criteria criteria = session.createCriteria(clazz);
        for (int i = 0; i < cols.length; i++) {
            criteria.add(Restrictions.eq(cols[i], values[i]));
        }
        return criteria;
    }

    public <T> List<T> findAll(Session session, Class<T> clazz) {
        return session.createCriteria(clazz).list();
    }

    public <T> Criteria likeSearch(Session session, Class<T> clazz, String col, String keyword) {
        Criteria criteria = session.createCriteria(clazz);
        criteria.add(Restrictions.like(col, keyword, MatchMode.ANYWHERE));
        return criteria;
    }

    public <T> Criteria sorted(Session session, Class<T> clazz, String orderByCol, boolean ascending) {
        Criteria criteria = session.createCriteria(clazz);
        if (ascending) {
            criteria.addOrder(Order.asc(orderByCol));
        } else {
            criteria.addOrder(Order.desc(orderByCol));
        }
        return criteria;
    }

    public <T> Criteria filteredAndSorted(Session session, Class<T> clazz, String col, Object value, String orderByCol, boolean ascending) {
        Criteria criteria = simpleSearch(session, clazz, col, value);
        if (ascending) {
            criteria.addOrder(Order.asc(orderByCol));
        } else {
            criteria.addOrder(Order.desc(orderByCol));
        }
        return criteria;
    }

    public <T> T findOne(Session session, Class<T> clazz, String col, Object value) {
        return (T) session.createCriteria(clazz)
                .add(Restrictions.eq(col, value))
                .uniqueResult();
    }

    public <T> List<T> findIn(Session session, Class<T> clazz, String col, List<?> values) {
        return session.createCriteria(clazz)
                .add(Restrictions.in(col, values))
                .list();
    }
}
