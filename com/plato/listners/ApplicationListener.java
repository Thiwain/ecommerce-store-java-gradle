/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/ServletListener.java to edit this template
 */
package com.plato.listners;

import com.plato.models.deployment.Deployment;
import com.plato.utils.HibernateUtil;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Timestamp;

@WebListener()
public class ApplicationListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Transaction tx = session.beginTransaction();
//            Deployment deployment = new Deployment();
//            deployment.setTimestamp(new Timestamp(System.currentTimeMillis()));
//            session.save(deployment);
//            tx.commit(); 
        } catch (Exception e) {
            e.printStackTrace(); // Log actual cause of crash
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        HibernateUtil.getSessionFactory().close();
    }
}
