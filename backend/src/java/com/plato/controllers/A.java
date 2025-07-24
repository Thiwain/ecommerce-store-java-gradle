package com.plato.controllers;

import com.plato.models.users.Gender;
import com.plato.models.users.User;
import com.plato.utils.HibernateUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.Transaction;

@WebServlet(name = "A", urlPatterns = {"/A"})
public class A extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Use try-with-resources for PrintWriter
        try (PrintWriter out = response.getWriter()) {
            out.println("Servlet is running...<br>");

            // Initialize session and transaction separately
            Session session = null;
            Transaction transaction = null;

            try {

                session = HibernateUtil.getSessionFactory().openSession();
                out.println("Hibernate session opened<br>");

                transaction = session.beginTransaction();
                out.println("Transaction started<br>");

                // Get or create gender
                Gender gender = session.get(Gender.class, 1);
                if (gender == null) {
                    gender = new Gender();
                    gender.setGender_type("Male");
                    session.save(gender);
                }

// Create user
                User u = new User();
                u.setFname("Jhon");
                u.setLname("Doe");
                u.setGender(gender);
                u.setMobile("0771234567");
                u.setDateTime(new Timestamp(System.currentTimeMillis()));
                // <-- Set this, it's NOT NULL in DB!

// Save user only once
                session.save(u);

                out.println("Student saved (pending commit)<br>");

                transaction.commit();
                out.println("Transaction committed<br>");
            } catch (Exception hEx) {
                // Rollback transaction if active and exception occurs
                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                    out.println("Transaction rolled back due to error.<br>");
                }
                hEx.printStackTrace();
                out.println("<br><b>Hibernate Error:</b> " + hEx.getMessage());
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
