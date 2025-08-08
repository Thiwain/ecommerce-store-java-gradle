/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Filter.java to edit this template
 */
package com.plato.filters;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebFilter(filterName = "AdminAuthFilter", urlPatterns = {"/v1/admin/*"})
public class AdminAuthFilter implements Filter {

    private static final boolean debug = true;

    private FilterConfig filterConfig = null;

    public AdminAuthFilter() {
    }

    private void doBeforeProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("AdminAuthFilter:DoBeforeProcessing");
        }

    }

    private void doAfterProcessing(ServletRequest request, ServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("AdminAuthFilter:DoAfterProcessing");
        }

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        boolean isAuthenticated = (session != null && session.getAttribute("adminuser") != null);

//        if (!isAuthenticated) {
//            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            res.setContentType("application/json");
//            res.getWriter().write("{\"success\": false, \"message\": \"Unauthorized\", \"data\": null}");
//            return;
//        }

        chain.doFilter(request, response);
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

}
