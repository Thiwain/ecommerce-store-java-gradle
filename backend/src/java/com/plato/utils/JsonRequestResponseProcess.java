/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.plato.utils;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.plato.dto.response.ApiResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class JsonRequestResponseProcess {

    private final Gson gson = new Gson();

    public <T> T jsonRequestProcess(HttpServletRequest request, Class<T> clazz)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }
        try {
            JsonObject jsonObject = JsonParser.parseString(jsonBuilder.toString()).getAsJsonObject();
            Set<String> validFields = Arrays.stream(clazz.getDeclaredFields())
                    .map(Field::getName)
                    .collect(Collectors.toSet());
            for (String jsonKey : jsonObject.keySet()) {
                if (!validFields.contains(jsonKey)) {
                    throw new ServletException("Unknown field in JSON: " + jsonKey);
                }
            }
            return gson.fromJson(jsonObject, clazz);
        } catch (JsonSyntaxException e) {
            throw new ServletException("Invalid JSON for: " + clazz.getSimpleName(), e);
        }
    }

    public <T> void jsonResponseProcess(HttpServletResponse response, int statusCode, boolean success, T data, String message) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ApiResponse<T> apiResponse = new ApiResponse<>(
                success,
                message,
                data
        );

        try (PrintWriter out = response.getWriter()) {
            out.print(gson.toJson(apiResponse));
            out.flush();
        }
    }

}
