/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.plato.utils;

import com.google.gson.Gson;
import com.plato.dto.response.ApiResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JsonRequestResponseProcess {

    private final Gson gson = new Gson();

    public <T> T jsonRequsetProcess(HttpServletRequest request, Class<T> clazz) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        StringBuilder jsonBuilder = new StringBuilder();

        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }

        return gson.fromJson(jsonBuilder.toString(), clazz);
    }

    public <T> void jsonResponseProcess(HttpServletResponse response, boolean status, T data, String message) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<T> apiResponse = new ApiResponse<>(
                status,
                message,
                data
        );

        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(apiResponse));
            out.flush();
        }
    }
}
