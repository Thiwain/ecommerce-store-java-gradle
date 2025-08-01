/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.plato.controllers.authentication;

import com.plato.config.LoggerConfig;
import com.plato.models.users.UserAuth;
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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

@Data
@NoArgsConstructor
@AllArgsConstructor
class VCodeVerificationReqDTO {

    private String email;
    private String vcode;
}

@WebServlet(name = "PasswordResetRequest", urlPatterns = {"/v1/verify-v-code"})
public class VerifyVcodeServerlet extends HttpServlet {

    JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        VCodeVerificationReqDTO dto;
        try {
            dto = jrrp.jsonRequestProcess(request, VCodeVerificationReqDTO.class);
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 400, false, e, "Bad Request");
            return;
        }
        if (!ValidationUtils.isEmailValid(dto.getEmail())) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid Email");
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            List<UserAuth> list = new DbUtils().multiSearch(
                    session,
                    UserAuth.class,
                    new String[]{"email", "vCode"},
                    new String[]{dto.getEmail(), dto.getVcode()}
            ).list();

            if (list.isEmpty()) {
                jrrp.jsonResponseProcess(response, 400, false, null, "Invalid Details");
                return;
            }

            jrrp.jsonResponseProcess(response, 200, true, null, "Verification Success!");
            return;
        } catch (Exception e) {
            LoggerConfig.logger.log(Level.SEVERE, "System error", e);
            jrrp.jsonResponseProcess(response, 500, false, null, "System error: " + e.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}
