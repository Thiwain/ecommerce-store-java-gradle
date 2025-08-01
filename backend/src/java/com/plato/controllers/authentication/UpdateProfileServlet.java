package com.plato.controllers.authentication;

import com.plato.config.LoggerConfig;
import com.plato.models.users.Gender;
import com.plato.models.users.User;
import com.plato.models.users.UserAuth;
import com.plato.utils.HibernateUtil;
import com.plato.utils.JsonRequestResponseProcess;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.logging.Level;

@Data
@NoArgsConstructor
@AllArgsConstructor
class ProfileUpdateReqDTO {

    private String fname;
    private String lname;
    private int genderId;
    private String mobile;
}

@WebServlet(name = "UpdateProfileServlet", urlPatterns = {"/v1/update-profile"})
public class UpdateProfileServlet extends HttpServlet {

    private final JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute("user") == null) {
            jrrp.jsonResponseProcess(response, 401, false, null, "Unauthorized access.");
            return;
        }

        ProfileUpdateReqDTO dto;
        try {
            dto = jrrp.jsonRequestProcess(request, ProfileUpdateReqDTO.class);
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid request data.");
            return;
        }

        UserAuth userAuth = (UserAuth) httpSession.getAttribute("user");

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // Fetch up-to-date UserAuth from DB
            UserAuth freshAuth = session.get(UserAuth.class, userAuth.getId());
            if (freshAuth == null) {
                jrrp.jsonResponseProcess(response, 404, false, null, "User not found.");
                return;
            }

            // Update fields
            User user = freshAuth.getUser();
            user.setFname(dto.getFname());
            user.setLname(dto.getLname());
            user.setMobile(dto.getMobile());
            user.setGender(session.get(Gender.class, dto.getGenderId()));
            user.setDateTime(Timestamp.from(Instant.now()));

            freshAuth.setUpdateDateTime(Timestamp.from(Instant.now()));

            session.update(user);
            session.update(freshAuth);

            tx.commit();

            // Update session with latest userAuth
            httpSession.setAttribute("user", freshAuth);

            jrrp.jsonResponseProcess(response, 200, true, null, "Profile updated successfully.");
        } catch (Exception e) {
            LoggerConfig.logger.log(Level.SEVERE, "Profile update failed", e);
            jrrp.jsonResponseProcess(response, 500, false, null, "Internal Server Error.");
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles profile update for authenticated users.";
    }
}
