/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.plato.utils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

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

}
