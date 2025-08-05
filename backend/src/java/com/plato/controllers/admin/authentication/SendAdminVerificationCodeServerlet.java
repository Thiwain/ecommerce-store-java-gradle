package com.plato.controllers.admin.authentication;

import com.plato.config.LoggerConfig;
import com.plato.models.users.AdminUser;
import com.plato.utils.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
class AdminLoginReqDTO {

    private String email;
}

@WebServlet(name = "SendAdminVerificationCodeServerlet", urlPatterns = {"/v1/send-admin-verification-code"})
public class SendAdminVerificationCodeServerlet extends HttpServlet {

    private final JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        AdminLoginReqDTO dto;

        // Parse JSON request
        try {
            dto = jrrp.jsonRequestProcess(request, AdminLoginReqDTO.class);
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid JSON");
            return;
        }

        // Validate email input
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Email is required");
            return;
        }

        if (!ValidationUtils.isEmailValid(dto.getEmail())) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid email format");
            return;
        }

        // Process admin verification
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<AdminUser> adminList = new DbUtils().simpleSearch(session, AdminUser.class, "email", dto.getEmail()).list();

            if (adminList.isEmpty()) {
                jrrp.jsonResponseProcess(response, 401, false, null, "Admin not found");
                return;
            }

            AdminUser admin = adminList.get(0);

            if (!admin.isActiveStatus() || !"ACTIVE".equals(admin.getUserAuthStatus().getStatus())) {
                jrrp.jsonResponseProcess(response, 403, false, null, "Account is deactivated");
                return;
            }

            String vcode = ValidationUtils.generateCode();

            Transaction tx = session.beginTransaction();
            admin.setVCode(vcode);
            admin.setDateTime(new Timestamp(System.currentTimeMillis())); // Optional: Update timestamp
            session.update(admin);
            tx.commit();

            String htmlBody = buildVerificationEmail(vcode);
            new GoogleMailSenderUtil().send(dto.getEmail(), "Plato's Wisdom Admin Verification Code", htmlBody);

            jrrp.jsonResponseProcess(response, 200, true, null, "Verification code sent to email");

        } catch (Exception e) {
            LoggerConfig.logger.log(Level.SEVERE, "Error sending verification code", e);
            jrrp.jsonResponseProcess(response, 500, false, null, "Internal server error");
        }
    }

    private String buildVerificationEmail(String code) {
        return "<!DOCTYPE html>"
                + "<html lang=\"en\">"
                + "<head><meta charset=\"UTF-8\"><title>Admin Login Verification</title></head>"
                + "<body style=\"font-family: 'Segoe UI', sans-serif; background-color: #f4f4f4; padding: 20px; color: #333;\">"
                + "<div style=\"max-width: 500px; margin: auto; background: white; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1);\">"
                + "  <div style=\"background-color: #6c63ff; padding: 20px; text-align: center; color: white;\">"
                + "    <h2 style=\"margin: 0;\">Plato's Wisdom Admin</h2>"
                + "    <p style=\"margin: 0; font-size: 14px;\">Admin Login Verification Required</p>"
                + "  </div>"
                + "  <div style=\"padding: 30px; text-align: center;\">"
                + "    <h3>Verification Code</h3>"
                + "    <p style=\"font-size: 26px; font-weight: bold; letter-spacing: 2px; color: #6c63ff;\">" + code + "</p>"
                + "    <p style=\"color: #666;\">Enter this code to complete your login. This code expires in 10 minutes.</p>"
                + "    <p style=\"font-size: 12px; color: #999;\">If you didn't request this, ignore this email.</p>"
                + "  </div>"
                + "  <div style=\"background-color: #f1f1f1; padding: 15px; font-size: 12px; text-align: center; color: #999;\">"
                + "    Sent by Plato's Wisdom Admin System"
                + "  </div>"
                + "</div>"
                + "</body></html>";
    }

    @Override
    public String getServletInfo() {
        return "Sends admin login verification code via email";
    }
}
