package com.plato.controllers.user;

import com.plato.config.LoggerConfig;
import com.plato.models.users.Gender;
import com.plato.models.users.User;
import com.plato.models.users.UserAuth;
import com.plato.models.users.UserAuthStatus;
import com.plato.utils.DbUtils;
import com.plato.utils.HibernateUtil;
import com.plato.utils.JsonRequestResponseProcess;
import com.plato.utils.ValidationUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
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

@WebServlet(name = "UserRegisterServlet", urlPatterns = {"/v1/UserRegister"})
public class UserRegisterServlet extends HttpServlet {

    private final JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserRequestDTO userRequestDTO;

        try {
            userRequestDTO = jrrp.jsonRequestProcess(request, UserRequestDTO.class);
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, 400, false, null, "Invalid JSON: " + e.getMessage());
            return;
        }

        try (PrintWriter out = response.getWriter(); Session session = HibernateUtil.getSessionFactory().openSession()) {

            if (userRequestDTO.getFname() == null || userRequestDTO.getFname().isEmpty()
                    || userRequestDTO.getLname() == null || userRequestDTO.getLname().isEmpty()
                    || userRequestDTO.getGender() == null || userRequestDTO.getGender().isEmpty()
                    || userRequestDTO.getMobile() == null || userRequestDTO.getMobile().isEmpty()
                    || userRequestDTO.getEmail() == null || userRequestDTO.getEmail().isEmpty()
                    || userRequestDTO.getPassword() == null || userRequestDTO.getPassword().isEmpty()) {
                jrrp.jsonResponseProcess(response, 400, false, null, "Fields shouldn't be empty!");
                return;
            } else if (!ValidationUtils.isEmailValid(userRequestDTO.getEmail())) {
                jrrp.jsonResponseProcess(response, 400, false, null, "Invalid Email Address!");
                return;
            } else if (!ValidationUtils.isPasswordValid(userRequestDTO.getPassword())) {
                jrrp.jsonResponseProcess(response, 400, false, null, "Invalid Password!");
                return;
            } else if (!ValidationUtils.isMobileValid(userRequestDTO.getMobile())) {
                jrrp.jsonResponseProcess(response, 400, false, null, "Invalid Mobile Number!");
                return;
            }

            if (!new DbUtils().simpleSearch(session, UserAuth.class, "email", userRequestDTO.getEmail()).list().isEmpty()) {
                jrrp.jsonResponseProcess(response, 409, false, null, "This user already exists!");
                return;
            }

            org.hibernate.Transaction tx = session.beginTransaction();

            try {
                List<Gender> genders = new DbUtils().simpleSearch(session, Gender.class, "genderType", userRequestDTO.getGender()).list();
                if (genders.isEmpty()) {
                    throw new Exception("Invalid gender type: " + userRequestDTO.getGender());
                }

                User user = new User();
                user.setFname(userRequestDTO.getFname());
                user.setLname(userRequestDTO.getLname());
                user.setGender(genders.get(0));
                user.setMobile(userRequestDTO.getMobile());
                user.setDateTime(new Timestamp(System.currentTimeMillis()));

                session.save(user);
                session.flush();

                List<UserAuthStatus> authStatuses = new DbUtils().simpleSearch(session, UserAuthStatus.class, "status", "ACTIVE").list();
                if (authStatuses.isEmpty()) {
                    throw new Exception("Auth status 'ACTIVE' not found");
                }

                UserAuth ua = new UserAuth();
                ua.setUser(user);
                ua.setEmail(userRequestDTO.getEmail());
                ua.setPassword(userRequestDTO.getPassword());
                ua.setDateTime(new Timestamp(System.currentTimeMillis()));
                ua.setUpdateDateTime(new Timestamp(System.currentTimeMillis()));
                ua.setUserAuthStatus(authStatuses.get(0));

                session.save(ua);

                tx.commit();

                jrrp.jsonResponseProcess(response, 201, true,
                        new SuccessResponseDTO(user.getFname(), user.getLname(), ua.getEmail()),
                        "User registered successfully");

            } catch (Exception e) {
                tx.rollback();
                LoggerConfig.logger.log(Level.SEVERE, "Error occurred during user registration", e);
                jrrp.jsonResponseProcess(response, 500, false, null, "Error: " + e.getMessage());
            }

        } catch (Exception e) {
            LoggerConfig.logger.log(Level.SEVERE, "System error", e);
            jrrp.jsonResponseProcess(response, 500, false, null, "System error: " + e.getMessage());
        }

    }

    @Override
    public String getServletInfo() {
        return "Handles user registration using Gson to parse and return JSON";
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class UserRequestDTO {

    private String fname;
    private String lname;
    private String gender;
    private String mobile;
    private String email;
    private String password;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class SuccessResponseDTO {

    private String fname;
    private String lname;
    private String email;
}
