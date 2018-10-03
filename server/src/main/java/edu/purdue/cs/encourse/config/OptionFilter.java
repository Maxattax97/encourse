package edu.purdue.cs.encourse.config;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Enumeration;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class OptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (true) {
            System.out.println("----------------------------------");
            System.out.println("Method: " + request.getMethod());
            Enumeration<String> headers = request.getHeaderNames();
            while (headers.hasMoreElements()) {
                String key = headers.nextElement();
                String val = request.getHeader(key);
                System.out.println("Header: [" + key + "] " + val);
            }
            System.out.println("Path: " + request.getContextPath());
            System.out.println("Auth type: " + request.getAuthType());
            System.out.println("Path Info" + request.getPathInfo());
            HttpSession session = request.getSession();
            Enumeration<String> attrs = session.getAttributeNames();
            while (attrs.hasMoreElements()) {
                String key = attrs.nextElement();
                String val = session.getAttribute(key).toString();
                System.out.println("Attributes: [" + key + "] " + val);
            }
            System.out.println("----------------------------------");
        }

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers",
                "X-Requested-With, Content-Type, Authorization, Origin, Accept, Access-Control-Request-Method, Access-Control-Request-Headers, X-XSRF-TOKEN, credential, x-xsrf-token");


        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
