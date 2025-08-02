package com.plato.controllers.authentication;

import com.plato.utils.JsonRequestResponseProcess;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/v1/logout"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            response.setStatus(200);
            response.getWriter().write("{\"message\": \"Logout successful\"}");
        } else {
            response.setStatus(401);
            response.getWriter().write("{\"message\": \"Unauthorized\"}");
        }
    }
}
