package com.plato.controllers;

import com.plato.models.users.Gender;
import com.plato.models.users.User;
import com.plato.utils.HibernateUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

@WebServlet(name = "A", urlPatterns = {"/A"})
public class A extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            out.println("Servlet is running...<br>");

            Session session = null;
            Transaction transaction = null;

            try {
                session = HibernateUtil.getSessionFactory().openSession();
                out.println("Hibernate session opened<br>");

                transaction = session.beginTransaction();
                out.println("Transaction started<br>");

                // Try to fetch existing Gender with ID = 1
                Gender gender = session.get(Gender.class, 1);

                if (gender == null) {
                    out.println("Gender with ID 1 not found, creating new gender...<br>");
                    gender = new Gender();
                    gender.setGender_type("Male");
                    session.save(gender);
                    out.println("New gender saved<br>");
                } else {
                    out.println("Gender found: " + gender.getGender_type() + "<br>");
                }

                // Create new user
                User user = new User();
                user.setFname("John");
                user.setLname("Doe");
                user.setGender(gender);
                user.setMobile("0771234567");
                user.setDateTime(new Timestamp(System.currentTimeMillis()));

                session.save(user);
                out.println("User saved<br>");

                transaction.commit();
                out.println("Transaction committed<br>");
            } catch (Exception ex) {
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                    out.println("Transaction rolled back due to error<br>");
                }
                ex.printStackTrace();
                out.println("<br><b>Hibernate Error:</b> " + ex.getMessage());
            } finally {
                if (session != null) {
                    session.close();
                    out.println("Hibernate session closed<br>");
                }
            }

            out.println("OK");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<br><b>Servlet Error:</b> " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
