package com.plato.controllers.authentication;

import com.plato.models.users.*;
import com.plato.utils.DbUtils;
import com.plato.utils.HibernateUtil;
import com.plato.utils.JsonRequestResponseProcess;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Session;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "LoadProfileServlet", urlPatterns = {"/v1/load-profile"})
public class LoadProfileServlet extends HttpServlet {

    JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

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

        UserAuth userAuth = (UserAuth) httpSession.getAttribute("user");
        User loggedUser = userAuth.getUser();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Re-fetch full User entity to ensure session-bound proxies are resolved
            User user = session.get(User.class, loggedUser.getId());

            List<Location> userLocation = new DbUtils().simpleSearch(session, Location.class, "user", user).list();
            Location ul = userLocation.isEmpty() ? null : userLocation.get(0);

            // Now safely access lazy-loaded Gender within the session
            ProfileResponseDTO prdto = new ProfileResponseDTO(
                    user.getFname(),
                    user.getLname(),
                    user.getMobile(),
                    userAuth.getEmail(),
                    user.getGender().getId(),
                    user.getGender().getGenderType(),
                    ul != null && ul.getDistrict() != null ? ul.getDistrict().getId() : 0,
                    ul != null && ul.getDistrict() != null ? ul.getDistrict().getDistrict() : "",
                    ul != null ? ul.getAddress() : ""
            );

            jrrp.jsonResponseProcess(response, 200, true, prdto, "Profile loaded successfully");
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 500, false, null, "Server error: " + e.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return "Loads the logged-in user's profile data";
    }
}
