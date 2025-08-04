package com.plato.controllers.authentication;

import com.plato.config.LoggerConfig;
import com.plato.models.users.District;
import com.plato.models.users.Gender;
import com.plato.models.users.Location;
import com.plato.models.users.User;
import com.plato.models.users.UserAuth;
import com.plato.utils.HibernateUtil;
import com.plato.utils.JsonRequestResponseProcess;
import com.plato.utils.ValidationUtils;

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
import java.util.List;
import java.util.logging.Level;

@Data
@NoArgsConstructor
@AllArgsConstructor
class ProfileUpdateRequestDTO {

    private String fname;
    private String lname;
    private String mobile;
    private Integer genderId;
    private Integer districtId;
    private String address;
}

@WebServlet(name = "ProfileUpdateServlet", urlPatterns = {"/v1/update-profile"})
public class UpdateProfileServlet extends HttpServlet {

    JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession httpSession = request.getSession(false);
        if (httpSession == null || httpSession.getAttribute("user") == null) {
            jrrp.jsonResponseProcess(response, 403, false, null, "Unauthorized access");
            return;
        }

        UserAuth loggedUser = (UserAuth) httpSession.getAttribute("user");
        ProfileUpdateRequestDTO dto;

        try {
            dto = jrrp.jsonRequestProcess(request, ProfileUpdateRequestDTO.class);
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid JSON input");
            return;
        }

        if (!dto.getMobile().isEmpty()) {
            if (!ValidationUtils.isMobileValid(dto.getMobile())) {
                jrrp.jsonResponseProcess(response, 400, false, null, "Invalid Mobile Number");
                return;
            }
        }

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            User user = session.get(User.class, loggedUser.getUser().getId());
            if (user == null) {
                jrrp.jsonResponseProcess(response, 404, false, null, "User not found");
                return;
            }

            user.setFname(dto.getFname());
            user.setLname(dto.getLname());
            user.setMobile(dto.getMobile());

            if (dto.getGenderId() != null) {
                Gender gender = session.get(Gender.class, dto.getGenderId());
                if (gender == null) {
                    jrrp.jsonResponseProcess(response, 400, false, null, "Invalid gender ID");
                    return;
                }
                user.setGender(gender);
            }

            session.update(user);

            if (dto.getDistrictId() != null) {
                List<Location> locationList = session
                        .createQuery("FROM Location l WHERE l.user.id = :uid", Location.class)
                        .setParameter("uid", user.getId())
                        .list();

                District district = session.get(District.class, dto.getDistrictId());
                if (district == null) {
                    jrrp.jsonResponseProcess(response, 400, false, null, "Invalid district ID");
                    return;
                }

                Location location;
                if (locationList.isEmpty()) {
                    location = new Location();
                    location.setUser(user);
                } else {
                    location = locationList.get(0);
                }

                location.setDistrict(district);

                if (!dto.getAddress().isEmpty()) {
                    location.setAddress(dto.getAddress());
                }
                
                location.setDateTime(Timestamp.from(Instant.now()));

                session.saveOrUpdate(location);

            }

            tx.commit();
            jrrp.jsonResponseProcess(response, 200, true, null, "Profile updated successfully");
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 500, false, null, "Server error: " + e.getMessage());
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles user profile updates";
    }
}
