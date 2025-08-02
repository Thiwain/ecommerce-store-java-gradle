package com.plato.utils;

import com.plato.config.LoggerConfig;
import com.plato.models.deployment.Deployment;
import com.plato.models.invoice.DiscountOfferStatus;
import com.plato.models.orders.FulFillStatus;
import com.plato.models.product.Category;
import com.plato.models.users.District;
import com.plato.models.users.Gender;
import com.plato.models.users.UserAuthStatus;
import java.sql.Timestamp;
import java.util.Vector;
import java.util.logging.Level;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

public class DataSeederUtil {

    public void seed() {
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
                session.save(new UserAuthStatus(0, "INACTIVE"));
            }

            if (!discountOfferStatusExists(session, "VALID")) {
                session.save(new DiscountOfferStatus(0, "VALID"));
            }
            if (!discountOfferStatusExists(session, "INVALID")) {
                session.save(new DiscountOfferStatus(0, "INVALID"));
            }

            if (!orderFulfill(session, "PENDING")) {
                session.save(new FulFillStatus(0, "PENDING"));
            }

            if (!orderFulfill(session, "FULFILLED")) {
                session.save(new FulFillStatus(0, "FULFILLED"));
            }

            Vector<String> districts = new Vector<>();

            districts.add("Colombo");
            districts.add("Gampaha");
            districts.add("Kalutara");
            districts.add("Kandy");
            districts.add("Matale");
            districts.add("Nuwara Eliya");
            districts.add("Galle");
            districts.add("Matara");
            districts.add("Hambantota");
            districts.add("Jaffna");
            districts.add("Kilinochchi");
            districts.add("Mannar");
            districts.add("Vavuniya");
            districts.add("Mullaitivu");
            districts.add("Batticaloa");
            districts.add("Ampara");
            districts.add("Trincomalee");
            districts.add("Kurunegala");
            districts.add("Puttalam");
            districts.add("Anuradhapura");
            districts.add("Polonnaruwa");
            districts.add("Badulla");
            districts.add("Monaragala");
            districts.add("Ratnapura");
            districts.add("Kegalle");

            for (String district : districts) {
                if (!districtExists(session, district)) {
                    session.save(new District(0, district));
                }
            }

            Vector<String> bookCategories = new Vector<>();
            bookCategories.add("Fiction");
            bookCategories.add("Comics");
            bookCategories.add("Non-Fiction");
            bookCategories.add("Science Fiction");
            bookCategories.add("Fantasy");
            bookCategories.add("Mystery");
            bookCategories.add("Thriller");
            bookCategories.add("Romance");
            bookCategories.add("Historical");
            bookCategories.add("Biography");
            bookCategories.add("Self-Help");
            bookCategories.add("Health & Wellness");
            bookCategories.add("Business");
            bookCategories.add("Education");
            bookCategories.add("Children's Books");
            bookCategories.add("Young Adult");
            bookCategories.add("Philosophy");
            bookCategories.add("Religion");
            bookCategories.add("Science");
            bookCategories.add("Travel");
            bookCategories.add("Art & Photography");
            
            for (String bookCategory : bookCategories) {
                if(!bookCategoryExists(session, bookCategory)){
                    session.save(new Category(0, bookCategory));
                }
            }

            tx.commit();
            session.close();
        } catch (Exception e) {
            LoggerConfig.logger.log(Level.WARNING, "MySQL Driver not found!", e);
            e.printStackTrace();
        }
    }

    private boolean genderExists(Session session, String genderType) {
        Criteria criteria = session.createCriteria(Gender.class);
        criteria.add(Restrictions.eq("genderType", genderType));
        return criteria.uniqueResult() != null;
    }
    
    private boolean bookCategoryExists(Session session, String name) {
        Criteria criteria = session.createCriteria(Category.class);
        criteria.add(Restrictions.eq("name", name));
        return criteria.uniqueResult() != null;
    }

    private boolean orderFulfill(Session session, String status) {
        Criteria criteria = session.createCriteria(FulFillStatus.class);
        criteria.add(Restrictions.eq("status", status));
        return criteria.uniqueResult() != null;
    }

    private boolean discountOfferStatusExists(Session session, String staus) {
        Criteria criteria = session.createCriteria(DiscountOfferStatus.class);
        criteria.add(Restrictions.eq("status", staus));
        return criteria.uniqueResult() != null;
    }

    private boolean authStatusExists(Session session, String status) {
        Criteria criteria = session.createCriteria(UserAuthStatus.class);
        criteria.add(Restrictions.eq("status", status));
        return criteria.uniqueResult() != null;
    }

    private boolean districtExists(Session session, String district) {
        Criteria criteria = session.createCriteria(District.class);
        criteria.add(Restrictions.eq("district", district));
        return criteria.uniqueResult() != null;
    }
}
