package com.plato.controllers.admin.authentication;

import com.plato.utils.JsonRequestResponseProcess;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "AdminLogoutServlet", urlPatterns = {"/v1/admin/logout"})
public class AdminLogoutServlet extends HttpServlet {

    private final JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false); 

        if (session != null && session.getAttribute("adminuser") != null) {
            session.invalidate();
            jrrp.jsonResponseProcess(response, 200, true, null, "Logout successful");
        } else {
            jrrp.jsonResponseProcess(response, 401, false, null, "No active admin session");
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles admin logout and session invalidation.";
    }
}
