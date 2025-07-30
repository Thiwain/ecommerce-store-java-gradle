package com.plato.controllers.user;

import com.google.gson.Gson;
import com.plato.utils.JsonRequestResponseProcess;
import com.plato.utils.ValidationUtils;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@WebServlet(name = "UserRegisterServlet", urlPatterns = {"/v1/UserRegister"})
public class UserRegisterServlet extends HttpServlet {

    JsonRequestResponseProcess jrrp = new JsonRequestResponseProcess();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserRequestDTO userRequestDTO;

        try {
            userRequestDTO = jrrp.jsonRequestProcess(request, UserRequestDTO.class);
        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, false, null, "Invalid JSON: " + e.getMessage());
            return;
        }

        PrintWriter out = response.getWriter();
        try {

            if (userRequestDTO.getFname() == null || userRequestDTO.getFname().isEmpty()
                    || userRequestDTO.getLname() == null || userRequestDTO.getLname().isEmpty()
                    || userRequestDTO.getGender() == null || userRequestDTO.getGender().isEmpty()
                    || userRequestDTO.getMobile() == null || userRequestDTO.getMobile().isEmpty()
                    || userRequestDTO.getEmail() == null || userRequestDTO.getEmail().isEmpty()
                    || userRequestDTO.getPassword() == null || userRequestDTO.getPassword().isEmpty()) {
                jrrp.jsonResponseProcess(response, false, null, "Fields Shouldn't be Empty!");
            } else if (!ValidationUtils.isEmailValid(userRequestDTO.getEmail())) {
                jrrp.jsonResponseProcess(response, false, null, "Invalid Email Address!");
            } else if (!ValidationUtils.isPasswordValid(userRequestDTO.getPassword())) {
                jrrp.jsonResponseProcess(response, false, null, "Invalid Password!");
            } else if (!ValidationUtils.isMobileValid(userRequestDTO.getMobile())) {
                jrrp.jsonResponseProcess(response, false, null, "Invalid Mobile Number!");
            }

            jrrp.jsonResponseProcess(response, true, userRequestDTO, "User data processed successfully");

        } catch (Exception e) {
            jrrp.jsonResponseProcess(response, false, null, "Error: " + e.getMessage());
        } finally {
            out.flush();
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
