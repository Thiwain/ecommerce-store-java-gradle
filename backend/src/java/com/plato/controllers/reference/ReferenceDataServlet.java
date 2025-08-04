/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.plato.controllers.reference;

import com.plato.models.product.Category;
import com.plato.models.users.District;
import com.plato.models.users.Gender;
import com.plato.utils.HibernateUtil;
import com.plato.utils.JsonRequestResponseProcess;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;

/**
 *
 * @author Acer
 */
@WebServlet(name = "ReferenceDataServlet", urlPatterns = {"/v1/reference-data"})
public class ReferenceDataServlet extends HttpServlet {

    private final JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");

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
        return "Short description";
    }

}
