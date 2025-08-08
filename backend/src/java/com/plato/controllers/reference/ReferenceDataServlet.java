package com.plato.controllers.reference;

import com.plato.models.product.Author;
import com.plato.models.product.Category;
import com.plato.models.product.Publisher;
import com.plato.models.users.District;
import com.plato.models.users.Gender;
import com.plato.utils.HibernateUtil;
import com.plato.utils.JsonRequestResponseProcess;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.query.Query;

@WebServlet(name = "ReferenceDataServlet", urlPatterns = {"/v1/reference-data"})
public class ReferenceDataServlet extends HttpServlet {

    private final JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");
        String search = request.getParameter("search");
        if (type == null || type.isEmpty()) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Missing type parameter");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            switch (type.toLowerCase()) {
                case "genders":
                    List<Gender> genders = session.createQuery("FROM Gender", Gender.class).list();
                    jrrp.jsonResponseProcess(response, 200, true, genders, "Genders loaded");
                    break;

                case "districts":
                    List<District> districts = session.createQuery("FROM District", District.class).list();
                    jrrp.jsonResponseProcess(response, 200, true, districts, "Districts loaded");
                    break;

                case "categories":
                    List<Category> categories = session.createQuery("FROM Category", Category.class).list();
                    jrrp.jsonResponseProcess(response, 200, true, categories, "Categories loaded");
                    break;

                case "authors":
                    List<Author> authors;
                    if (search == null || search.trim().isEmpty()) {
                        // No search - get top 3 by name
                        authors = session.createQuery("FROM Author ORDER BY name", Author.class)
                                .setMaxResults(3)
                                .list();
                    } else {
                        String searchPattern = "%" + search.trim().toLowerCase() + "%";
                        Query<Author> authorQuery = session.createQuery(
                                "FROM Author WHERE lower(name) LIKE :searchPattern ORDER BY name", Author.class);
                        authorQuery.setParameter("searchPattern", searchPattern);
                        authorQuery.setMaxResults(3);
                        authors = authorQuery.list();
                    }
                    jrrp.jsonResponseProcess(response, 200, true, authors, "Authors loaded");
                    break;

                case "publishers":
                    List<Publisher> publishers;
                    if (search == null || search.trim().isEmpty()) {
                        publishers = session.createQuery("FROM Publisher ORDER BY name", Publisher.class)
                                .setMaxResults(3)
                                .list();
                    } else {
                        String searchPattern = "%" + search.trim().toLowerCase() + "%";
                        Query<Publisher> publisherQuery = session.createQuery(
                                "FROM Publisher WHERE lower(name) LIKE :searchPattern ORDER BY name", Publisher.class);
                        publisherQuery.setParameter("searchPattern", searchPattern);
                        publisherQuery.setMaxResults(3);
                        publishers = publisherQuery.list();
                    }
                    jrrp.jsonResponseProcess(response, 200, true, publishers, "Publishers loaded");
                    break;

                default:
                    jrrp.jsonResponseProcess(response, 400, false, null, "Invalid type parameter");
                    break;
            }
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 500, false, null, "Server error: " + e.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return "Reference Data Servlet with search support for authors and publishers";
    }

}
