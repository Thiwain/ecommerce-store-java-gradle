package com.plato.listners;

import com.plato.models.deployment.Deployment;
import com.plato.models.users.Gender;
import com.plato.models.users.UserAuthStatus;
import com.plato.utils.HibernateUtil;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.sql.Timestamp;
import javax.security.auth.message.AuthStatus;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

@WebListener
public class ApplicationListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            Deployment deployment = new Deployment();
            deployment.setTimestamp(new Timestamp(System.currentTimeMillis()));
            session.save(deployment);

            if (!genderExists(session, "Male")) {
                Gender male = new Gender(0, "Male");
                session.save(male);
            }

            if (!genderExists(session, "Female")) {
                Gender female = new Gender(0, "Female");
                session.save(female);
            }

            if (!authStatusExists(session, "ACTIVE")) {
                session.save(new UserAuthStatus(0, "ACTIVE"));
            }

            if (!authStatusExists(session, "INACTIVE")) {

            }

            tx.commit();
        } catch (Exception e) {
            e.printStackTrace(); // Log in production
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        HibernateUtil.getSessionFactory().close();
    }

    private boolean genderExists(Session session, String genderType) {
        Criteria criteria = session.createCriteria(Gender.class);
        criteria.add(Restrictions.eq("genderType", genderType));
        return criteria.uniqueResult() != null;
    }

    private boolean authStatusExists(Session session, String status) {
        Criteria criteria = session.createCriteria(AuthStatus.class);
        criteria.add(Restrictions.eq("status", status));
        return criteria.uniqueResult() != null;
    }

}
