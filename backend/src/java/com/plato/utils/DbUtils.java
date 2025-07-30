/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.plato.utils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

public class DbUtils {

    public static <T> Criteria simpleSearch(Session session, Class<T> clazz, String name) {
        Criteria criteria = session.createCriteria(clazz);
        criteria.add(Restrictions.eq("name", name));
        return criteria;
    }
}
