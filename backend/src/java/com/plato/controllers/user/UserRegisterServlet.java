package com.plato.controllers.user;

import com.google.gson.Gson;
import com.plato.utils.JsonRequestResponseProcess;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "UserRegisterServlet", urlPatterns = {"/v1/UserRegisterServlet"})
public class UserRegisterServlet extends HttpServlet {

    JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserRequest userRequest = jrrp.jsonRequsetProcess(request, UserRequest.class);

        PrintWriter out = response.getWriter();
        try {

            if (userRequest.getEmail() == null || userRequest.getEmail().isEmpty()
                    || userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) {
                jrrp.jsonResponseProcess(response, false, null, "Email and password are required");
                return;
            }
            jrrp.jsonResponseProcess(response, true, userRequest, "User data processed successfully");

        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, false, null, "Error: " + e.getMessage());
        } finally {
            out.flush();
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles user registration using Gson to parse and return JSON";
    }
}

class UserRequest {

    private String email;
    private String password;

    public UserRequest() {
    }

    public UserRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
