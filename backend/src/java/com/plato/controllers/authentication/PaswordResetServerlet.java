package com.plato.controllers.authentication;

import com.plato.config.LoggerConfig;
import com.plato.models.users.UserAuth;
import com.plato.utils.DbUtils;
import com.plato.utils.GoogleMailSenderUtil;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Data
@NoArgsConstructor
@AllArgsConstructor
class PwResetReqDTO {

    private String email;
    private String password;
    private String vcode;
}

@WebServlet(name = "PaswordResetServerlet", urlPatterns = {"/v1/pw-reset"})
public class PaswordResetServerlet extends HttpServlet {

    private final JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        PwResetReqDTO dto;
        try {
            dto = jrrp.jsonRequestProcess(request, PwResetReqDTO.class);
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid Request Payload");
            return;
        }

        if (!ValidationUtils.isEmailValid(dto.getEmail())) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid Email");
            return;
        }

        if (!ValidationUtils.isPasswordValid(dto.getPassword())) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Password must meet criteria");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<UserAuth> users = new DbUtils().multiSearch(
                    session,
                    UserAuth.class,
                    new String[]{"email", "vCode"},
                    new String[]{dto.getEmail(), dto.getVcode()}
            ).list();

            if (users.isEmpty()) {
                jrrp.jsonResponseProcess(response, 400, false, null, "Invalid email or verification code");
                return;
            }

            UserAuth user = users.get(0);
            Transaction tx = session.beginTransaction();
            user.setPassword(dto.getPassword());
            user.setVCode(null);
            session.update(user);
            tx.commit();

            String subject = "Plato's Wisdom - Password Reset Successful";
            String htmlBody = "<!DOCTYPE html>"
                    + "<html lang=\"en\">"
                    + "<head>"
                    + "    <meta charset=\"UTF-8\">"
                    + "    <title>Password Reset Successful</title>"
                    + "</head>"
                    + "<body style=\"font-family: 'Segoe UI', sans-serif; background-color: #f4f4f4; padding: 20px; color: #333;\">"
                    + "    <div style=\"max-width: 500px; margin: auto; background: white; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); overflow: hidden;\">"
                    + "        <div style=\"background-color: #6c63ff; padding: 20px; text-align: center; color: white;\">"
                    + "            <h2 style=\"margin: 0;\">Plato's Wisdom</h2>"
                    + "            <p style=\"margin: 0; font-size: 14px;\">Your account security is important</p>"
                    + "        </div>"
                    + "        <div style=\"padding: 30px; text-align: center;\">"
                    + "            <h3 style=\"margin-bottom: 10px;\">Password Reset Successful</h3>"
                    + "            <p style=\"font-size: 16px; color: #666;\">You have successfully reset your password.</p>"
                    + "            <p style=\"margin-top: 20px; font-size: 14px; color: #666;\">If this wasn't you, please <a href='mailto:support@platoswisdom.com' style='color: #6c63ff;'>contact support</a> immediately.</p>"
                    + "        </div>"
                    + "        <div style=\"background-color: #f1f1f1; padding: 15px; font-size: 12px; text-align: center; color: #999;\">"
                    + "            Thank you for using Plato's Wisdom."
                    + "        </div>"
                    + "    </div>"
                    + "</body>"
                    + "</html>";

            GoogleMailSenderUtil.send(dto.getEmail(), subject, htmlBody);

            jrrp.jsonResponseProcess(response, 200, true, null, "Password reset successful");

        } catch (Exception e) {
            LoggerConfig.logger.log(Level.SEVERE, "Password Reset Error", e);
            jrrp.jsonResponseProcess(response, 500, false, null, "System error: " + e.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles password reset after verification";
    }
}
