package com.plato.controllers.authentication;

import com.plato.models.users.*;
import com.plato.utils.HibernateUtil;
import com.plato.utils.JsonRequestResponseProcess;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.Query;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "LoadProfileServlet", urlPatterns = {"/v1/load-profile"})
public class LoadProfileServlet extends HttpServlet {

    private final JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    static class ProfileResponseDTO {

        private String fname;
        private String lname;
        private String mobile;
        private String email;
        private int genderId;
        private String genderName;
        private int districtId;
        private String districtName;
        private String address;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession httpSession = request.getSession(false);

        if (httpSession == null || httpSession.getAttribute("user") == null) {
            jrrp.jsonResponseProcess(response, 403, false, null, "Unauthorized access");
            return;
        }

        UserAuth sessionUserAuth = (UserAuth) httpSession.getAttribute("user");

        if (sessionUserAuth == null || sessionUserAuth.getUser() == null) {
            jrrp.jsonResponseProcess(response, 403, false, null, "Session expired or user not found");
            return;
        }

        int userId = sessionUserAuth.getUser().getId();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {

            User freshUser = session.get(User.class, userId);
            if (freshUser == null) {
                jrrp.jsonResponseProcess(response, 404, false, null, "User not found in DB");
                return;
            }

            Query<UserAuth> userAuthQuery = session.createQuery(
                    "FROM UserAuth WHERE user.id = :uid", UserAuth.class);
            userAuthQuery.setParameter("uid", userId);
            UserAuth userAuth = userAuthQuery.uniqueResult();

            if (userAuth == null) {
                jrrp.jsonResponseProcess(response, 404, false, null, "UserAuth not found in DB");
                return;
            }

            Query<Location> locationQuery = session.createQuery(
                    "FROM Location WHERE user.id = :uid", Location.class);
            locationQuery.setParameter("uid", userId);
            Location location = locationQuery.uniqueResult();

            // Set location fields conditionally
            int districtId = 0;
            String districtName = "";
            String address = "";

            if (location != null && location.getDistrict() != null) {
                districtId = location.getDistrict().getId();
                districtName = location.getDistrict().getDistrict();
                address = location.getAddress() != null ? location.getAddress() : "";
            }

            ProfileResponseDTO profile = new ProfileResponseDTO(
                    freshUser.getFname(),
                    freshUser.getLname(),
                    freshUser.getMobile(),
                    userAuth.getEmail(),
                    freshUser.getGender() != null ? freshUser.getGender().getId() : 0,
                    freshUser.getGender() != null ? freshUser.getGender().getGenderType() : null,
                    districtId,
                    districtName,
                    address
            );

            jrrp.jsonResponseProcess(response, 200, true, profile, "Profile loaded");

        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 500, false, null, "Server error: " + e.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return "Loads the logged-in user's profile data";
    }
}
