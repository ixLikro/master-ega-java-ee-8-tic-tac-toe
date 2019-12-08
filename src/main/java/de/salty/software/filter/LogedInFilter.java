package de.salty.software.filter;


import javax.faces.application.ResourceHandler;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class LogedInFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(false);
        String inputNameURI = request.getContextPath() + "/app/index.xhtml";
        String gameURI = request.getContextPath() + "/secured/game.xhtml";

        boolean loggedIn = session != null && session.getAttribute("name") != null;
        boolean inGame = session != null && session.getAttribute("game") != null;
        boolean loginRequest = request.getRequestURI().equals(inputNameURI);
        boolean resourceRequest = request.getRequestURI()
                .startsWith(request.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER);

        if (loggedIn || loginRequest || resourceRequest) {
                filterChain.doFilter(request, response);
        } else {
            response.sendRedirect(inputNameURI);
        }
    }
}
