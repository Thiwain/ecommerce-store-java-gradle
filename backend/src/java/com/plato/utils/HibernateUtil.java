package com.plato.utils;

import com.plato.models.users.Gender;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.plato.models.grn.*;
import com.plato.models.invoice.*;
import com.plato.models.orders.*;
import com.plato.models.product.*;
import com.plato.models.users.*;

import java.util.Properties;

public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();

                // Hibernate settings equivalent to hibernate.cfg.xml
                Properties settings = new Properties();
                settings.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
                settings.put(Environment.URL, "jdbc:mysql://localhost:3306/platos_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true");
                settings.put(Environment.USER, "root");
                settings.put(Environment.PASS, "2005@Thiwain");
                settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");

                settings.put("hibernate.connection.ssl_mode", "DISABLED");

                settings.put("hibernate.show_sql", "true");
                settings.put("hibernate.format_sql", "true");
                settings.put("hibernate.hbm2ddl.auto", "create");

                configuration.setProperties(settings);

                configuration.addAnnotatedClass(Gender.class);
                configuration.addAnnotatedClass(Access.class);
                configuration.addAnnotatedClass(AdminUser.class);
                configuration.addAnnotatedClass(AdminUserHasAccess.class);
                configuration.addAnnotatedClass(District.class);
                configuration.addAnnotatedClass(Location.class);
                configuration.addAnnotatedClass(User.class);
                configuration.addAnnotatedClass(UserAuth.class);
                configuration.addAnnotatedClass(UserAuthStatus.class);
                configuration.addAnnotatedClass(Cart.class);

                configuration.addAnnotatedClass(Author.class);
                configuration.addAnnotatedClass(Category.class);
                configuration.addAnnotatedClass(ProductDetails.class);
                configuration.addAnnotatedClass(ProductDetailsHasTag.class);
                configuration.addAnnotatedClass(CategoryHasImg.class);
                configuration.addAnnotatedClass(Publisher.class);
                configuration.addAnnotatedClass(SearchTag.class);

                configuration.addAnnotatedClass(FulFillStatus.class);
                configuration.addAnnotatedClass(Orders.class);

                configuration.addAnnotatedClass(Discount.class);
                configuration.addAnnotatedClass(DiscountOfferStatus.class);
                configuration.addAnnotatedClass(Invoice.class);
                configuration.addAnnotatedClass(InvoiceHasProductDetails.class);

                configuration.addAnnotatedClass(Grn.class);
                configuration.addAnnotatedClass(GrnHasProductDetails.class);
                configuration.addAnnotatedClass(Supplier.class);

                configuration.addAnnotatedClass(com.plato.models.deployment.Deployment.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(configuration.getProperties()).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}
