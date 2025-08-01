package com.plato.controllers.authentication;

import com.plato.config.LoggerConfig;
import com.plato.models.users.UserAuth;
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

        // Validation
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
            user.setPassword(dto.getPassword()); // You should hash it in real apps!
            user.setVCode(null); // Invalidate the used verification code
            session.update(user);
            tx.commit();

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
