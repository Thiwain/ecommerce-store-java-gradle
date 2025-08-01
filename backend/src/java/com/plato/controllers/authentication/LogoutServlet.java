package com.plato.controllers.authentication;

import com.plato.utils.JsonRequestResponseProcess;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/v1/logout"})
public class LogoutServlet extends HttpServlet {

    private final JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false); 

        if (session != null && session.getAttribute("user") != null) {
            session.invalidate(); 
            jrrp.jsonResponseProcess(response, 200, true, null, "Logout successful");
        } else {
            jrrp.jsonResponseProcess(response, 400, false, null, "No active session to log out");
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles user logout";
    }
}
