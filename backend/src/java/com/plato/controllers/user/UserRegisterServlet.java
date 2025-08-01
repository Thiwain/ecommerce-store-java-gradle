package com.plato.controllers.user;

import com.plato.config.LoggerConfig;
import com.plato.dto.request.SuccessResponseDTO;
import com.plato.dto.request.UserRequestDTO;
import com.plato.models.users.Gender;
import com.plato.models.users.User;
import com.plato.models.users.UserAuth;
import com.plato.models.users.UserAuthStatus;
import com.plato.utils.DbUtils;
import com.plato.utils.GoogleMailSenderUtil;
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

                String htmlBody = ""
                        + "<!DOCTYPE html>\n"
                        + "<html lang=\"en\">\n"
                        + "<head>\n"
                        + "  <meta charset=\"UTF-8\">\n"
                        + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                        + "  <title>Welcome to Plato's Wisdom</title>\n"
                        + "  <style>\n"
                        + "    body {\n"
                        + "      margin: 0;\n"
                        + "      padding: 0;\n"
                        + "      background-color: #f4f4f7;\n"
                        + "      font-family: 'Segoe UI', sans-serif;\n"
                        + "      color: #333;\n"
                        + "    }\n"
                        + "    .container {\n"
                        + "      max-width: 600px;\n"
                        + "      margin: auto;\n"
                        + "      background: #ffffff;\n"
                        + "      border-radius: 8px;\n"
                        + "      box-shadow: 0 2px 8px rgba(0,0,0,0.1);\n"
                        + "      overflow: hidden;\n"
                        + "    }\n"
                        + "    .header {\n"
                        + "      background-color: #4B0082;\n"
                        + "      color: #fff;\n"
                        + "      text-align: center;\n"
                        + "      padding: 24px 16px;\n"
                        + "      font-size: 24px;\n"
                        + "      font-weight: bold;\n"
                        + "    }\n"
                        + "    .content {\n"
                        + "      padding: 32px 24px;\n"
                        + "      text-align: center;\n"
                        + "    }\n"
                        + "    h1 {\n"
                        + "      color: #4B0082;\n"
                        + "      margin-bottom: 16px;\n"
                        + "    }\n"
                        + "    p {\n"
                        + "      font-size: 16px;\n"
                        + "      line-height: 1.6;\n"
                        + "      margin-bottom: 16px;\n"
                        + "    }\n"
                        + "    .button {\n"
                        + "      display: inline-block;\n"
                        + "      padding: 12px 24px;\n"
                        + "      margin-top: 16px;\n"
                        + "      background-color: #4B0082;\n"
                        + "      color: #fff;\n"
                        + "      text-decoration: none;\n"
                        + "      border-radius: 6px;\n"
                        + "      font-weight: bold;\n"
                        + "      transition: background-color 0.3s ease;\n"
                        + "    }\n"
                        + "    .button:hover {\n"
                        + "      background-color: #360062;\n"
                        + "    }\n"
                        + "    .footer {\n"
                        + "      text-align: center;\n"
                        + "      font-size: 12px;\n"
                        + "      color: #888;\n"
                        + "      padding: 16px;\n"
                        + "    }\n"
                        + "  </style>\n"
                        + "</head>\n"
                        + "<body>\n"
                        + "  <div class=\"container\">\n"
                        + "    <div class=\"header\">📚 Welcome to Plato's Wisdom</div>\n"
                        + "    <div class=\"content\">\n"
                        + "      <h1>Hello, Book Lover!</h1>\n"
                        + "      <p>Thank you for joining Plato’s Wisdom — your personal gateway to timeless literature, modern gems, and hidden treasures.</p>\n"
                        + "      <p>We’ve built this bookstore for minds that seek knowledge, imagination, and depth. From ancient philosophy to the latest fiction, you’ll find it all here.</p>\n"
                        + "      <a href=\"https://platoswisdom.com/shop\" class=\"button\">Browse Our Library</a>\n"
                        + "    </div>\n"
                        + "    <div class=\"footer\">\n"
                        + "      © 2000–2025 Plato’s Wisdom. All rights reserved.<br>\n"
                        + "      You're receiving this email because you signed up at Plato's Wisdom.\n"
                        + "    </div>\n"
                        + "  </div>\n"
                        + "</body>\n"
                        + "</html>";

                GoogleMailSenderUtil.send("thiwainm@gmail.com", "Hello World", htmlBody);

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
