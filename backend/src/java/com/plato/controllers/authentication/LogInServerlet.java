/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.plato.controllers.authentication;

import com.plato.config.LoggerConfig;
import com.plato.dto.request.LoginRequestDTO;
import com.plato.models.users.UserAuth;
import com.plato.models.users.UserAuthStatus;
import com.plato.utils.DbUtils;
import com.plato.utils.HibernateUtil;
import com.plato.utils.JsonRequestResponseProcess;
import com.plato.utils.ValidationUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.hibernate.Session;

/**
 *
 * @author Acer
 */
@WebServlet(name = "LogInServerlet", urlPatterns = {"/v1/user-login"})
public class LogInServerlet extends HttpServlet {

    JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LoginRequestDTO dto;
        try {
            dto = jrrp.jsonRequestProcess(request, LoginRequestDTO.class);
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid JSON");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            if (!ValidationUtils.isEmailValid(dto.getEmail())) {
                jrrp.jsonResponseProcess(response, 401, false, null, "Invalid Email!");
                return;
            }

            if (!ValidationUtils.isPasswordValid(dto.getPassword())) {
                jrrp.jsonResponseProcess(response, 401, false, null, "Invalid Password!");
                return;
            }

            List<UserAuth> list = new DbUtils().multiSearch(
                    session,
                    UserAuth.class,
                    new String[]{"email", "password"},
                    new String[]{dto.getEmail(), dto.getPassword()}
            ).list();
            if (list.isEmpty()) {
                jrrp.jsonResponseProcess(response, 401, false, null, "Invalid Email or Password");
                return;
            }
            if (!"ACTIVE".equals(list.get(0).getUserAuthStatus().getStatus())) {
                jrrp.jsonResponseProcess(response, 403, false, null, "Your account is not active. Please verify your email or contact support.");
                return;
            }
            HttpSession httpSession = request.getSession();
            httpSession.setAttribute("user", list.get(0));

            if (dto.isRememberMe()) {
                httpSession.setMaxInactiveInterval(60 * 60 * 24 * 7); 
            }

            String sessionId = httpSession.getId();
            session.beginTransaction();
            list.get(0).setJsonSesId(sessionId);
            session.update(list.get(0));
            session.getTransaction().commit();

            jrrp.jsonResponseProcess(response, 200, true, null, "Log In Successful!");
        } catch (Exception e) {
            LoggerConfig.logger.log(Level.SEVERE, "System error", e);
            jrrp.jsonResponseProcess(response, 500, false, null, "System error: " + e.getMessage());
        }

    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
