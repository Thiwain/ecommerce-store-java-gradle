/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.plato.controllers.authentication;

import com.plato.models.users.UserAuth;
import com.plato.utils.DbUtils;
import com.plato.utils.GoogleMailSenderUtil;
import com.plato.utils.HibernateUtil;
import com.plato.utils.JsonRequestResponseProcess;
import com.plato.utils.ValidationUtils;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
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
class ForgotPwReqDTO {

    private String email;
}

@WebServlet(name = "ForgotPasswordServerlet", urlPatterns = {"/v1/req-verificationCode"})
public class ForgotPasswordServerlet extends HttpServlet {

    JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ForgotPwReqDTO dto;
        try {
            dto = jrrp.jsonRequestProcess(request, ForgotPwReqDTO.class);
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid JSON");
            return;
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            if (!ValidationUtils.isEmailValid(dto.getEmail())) {
                jrrp.jsonResponseProcess(response, 401, false, null, "Invalid Email!");
                return;
            }

            List<UserAuth> list = new DbUtils().simpleSearch(session, UserAuth.class, "email", dto.getEmail()).list();
            if (list.isEmpty()) {
                jrrp.jsonResponseProcess(response, 401, false, null, "Invalid Email!");
                return;
            }

            Transaction tx = session.beginTransaction();

            UserAuth user = list.get(0);
            String v_code = ValidationUtils.generateCode();
            user.setVCode(v_code);

            try {
                String subject = "Plato's Wisdom - Your Verification Code";
                String htmlBody = "<!DOCTYPE html>"
                        + "<html lang=\"en\">"
                        + "<head>"
                        + "    <meta charset=\"UTF-8\">"
                        + "    <title>Verification Code</title>"
                        + "</head>"
                        + "<body style=\"font-family: 'Segoe UI', sans-serif; background-color: #f4f4f4; padding: 20px; color: #333;\">"
                        + "    <div style=\"max-width: 500px; margin: auto; background: white; border-radius: 8px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); overflow: hidden;\">"
                        + "        <div style=\"background-color: #6c63ff; padding: 20px; text-align: center; color: white;\">"
                        + "            <h2 style=\"margin: 0;\">Plato's Wisdom</h2>"
                        + "            <p style=\"margin: 0; font-size: 14px;\">Your account security is important</p>"
                        + "        </div>"
                        + "        <div style=\"padding: 30px; text-align: center;\">"
                        + "            <h3 style=\"margin-bottom: 10px;\">Your Verification Code</h3>"
                        + "            <p style=\"font-size: 24px; font-weight: bold; letter-spacing: 2px; color: #6c63ff;\">" + v_code + "</p>"
                        + "            <p style=\"margin-top: 20px; font-size: 14px; color: #666;\">Enter this code to verify your email address. It expires in 10 minutes.</p>"
                        + "        </div>"
                        + "        <div style=\"background-color: #f1f1f1; padding: 15px; font-size: 12px; text-align: center; color: #999;\">"
                        + "            If you did not request this code, please ignore this email."
                        + "        </div>"
                        + "    </div>"
                        + "</body>"
                        + "</html>";

                GoogleMailSenderUtil.send(dto.getEmail(), subject, htmlBody);

            } catch (Exception e) {
                jrrp.jsonResponseProcess(response, 401, false, null, "Error Occoured During Sending Email!");
                return;
            }

            session.update(user);
            tx.commit();

        }

        jrrp.jsonResponseProcess(response, 200, false, null, "Verification Code Sent.");
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
