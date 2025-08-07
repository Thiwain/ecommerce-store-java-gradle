/*
 * Servlet to verify admin user using verification code.
 */
package com.plato.controllers.admin.authentication;

import com.plato.config.LoggerConfig;
import com.plato.models.users.AdminUser;
import com.plato.utils.DbUtils;
import com.plato.utils.HibernateUtil;
import com.plato.utils.JsonRequestResponseProcess;
import com.plato.utils.ValidationUtils;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.Session;
import org.hibernate.Transaction;

@Data
@NoArgsConstructor
@AllArgsConstructor
class ReqDto {

    private String email;
    private String vcode;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class AdminDataDto {

    private String email;
    private String fname;
    private String lname;
}

@WebServlet(name = "VerifyAdminVerificationCodeServlet", urlPatterns = {"/v1/verify-admin-verification-code"})
public class VerifyAdminVerficationCodeServerlet extends HttpServlet {

    private final JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ReqDto dto;
        try {
            dto = jrrp.jsonRequestProcess(request, ReqDto.class);
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid JSON");
            return;
        }

        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Email is required");
            return;
        }

        if (!ValidationUtils.isEmailValid(dto.getEmail())) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid email format");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<AdminUser> adminList = new DbUtils()
                    .simpleSearch(session, AdminUser.class, "email", dto.getEmail())
                    .list();

            if (adminList.isEmpty()) {
                jrrp.jsonResponseProcess(response, 401, false, null, "Admin not found");
                return;
            }

            AdminUser admin = adminList.get(0);
            if (admin.getVCode() == null || !admin.getVCode().equals(dto.getVcode())) {
                jrrp.jsonResponseProcess(response, 401, false, null, "Invalid verification code");
                return;
            }

            AdminDataDto adminDataDto = new AdminDataDto(admin.getEmail(), admin.getFname(), admin.getLname());

            Transaction tx = session.beginTransaction();
            admin.setVCode(null);
            session.update(admin);
            tx.commit();

            HttpSession httpSession = request.getSession();
            httpSession.setAttribute("adminuser", admin);
            httpSession.setMaxInactiveInterval(60 * 60 * 3); 

            jrrp.jsonResponseProcess(response, 200, true, adminDataDto, "Verification successful");

        } catch (Exception e) {
            LoggerConfig.logger.log(Level.SEVERE, "Error verifying admin", e);
            jrrp.jsonResponseProcess(response, 500, false, null, "Internal server error");
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles admin email verification via vcode.";
    }
}
