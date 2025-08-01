package com.plato.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

// Protect specific paths (you can adjust as needed)
@WebFilter(urlPatterns = {"/v1/user-profile/*", "/v1/logout"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        boolean isAuthenticated = (session != null && session.getAttribute("user") != null);

        if (!isAuthenticated) {
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json");
            res.getWriter().write("{\"success\": false, \"message\": \"Unauthorized\", \"data\": null}");
            return;
        }

        // Proceed if authenticated
        chain.doFilter(request, response);
    }
}
