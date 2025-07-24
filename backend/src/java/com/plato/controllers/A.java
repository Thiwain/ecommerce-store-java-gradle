package com.plato.controllers;

import com.plato.utils.HibernateUtil;
import java.io.IOException;
import java.io.PrintWriter;
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
//        try (PrintWriter out = response.getWriter()) {
//            out.println("Servlet is running...<br>");
//
//            // Initialize session and transaction separately
//            Session session = null;
//            Transaction transaction = null;
//
//            try {
//                session = HibernateUtil.getSessionFactory().openSession();
//                out.println("Hibernate session opened<br>");
//
//                transaction = session.beginTransaction();
//                out.println("Transaction started<br>");
//
//                Student s = new Student();
//                s.setName("Hello");
//                s.setAge(22);
//                s.setGrade("A");
//
//                session.save(s);
//                out.println("Student saved (pending commit)<br>");
//
//                transaction.commit();
//                out.println("Transaction committed<br>");
//            } catch (Exception hEx) {
//                // Rollback transaction if active and exception occurs
//                if (transaction != null && transaction.isActive()) {
//                    transaction.rollback();
//                    out.println("Transaction rolled back due to error.<br>");
//                }
//                hEx.printStackTrace();
//                out.println("<br><b>Hibernate Error:</b> " + hEx.getMessage());
//            } finally {
//                if (session != null) {
//                    session.close();
//                    out.println("Hibernate session closed<br>");
//                }
//            }
//
//            out.println("OK");
//        } catch (Exception e) {
//            e.printStackTrace();
//            response.getWriter().println("<br><b>Servlet Error:</b> " + e.getMessage());
//        }
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
