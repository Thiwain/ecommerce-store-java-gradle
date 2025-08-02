package com.plato.filters;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class CORSFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;

        // Allow all origins - remove restrictions for testing
        response.setHeader("Access-Control-Allow-Origin", "*");
        // Allow all methods
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        // Allow all headers
        response.setHeader("Access-Control-Allow-Headers", "*");
        // For credentials, if you use wildcard origin, this will not work:
        // So for testing credentials you may have to set a specific origin, e.g. "http://localhost:3000"
        response.setHeader("Access-Control-Allow-Credentials", "true");

        chain.doFilter(req, res);
    }
}
