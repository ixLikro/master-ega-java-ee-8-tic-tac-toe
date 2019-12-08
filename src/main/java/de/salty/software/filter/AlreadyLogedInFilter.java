package de.salty.software.filter;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class AlreadyLogedInFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpSession session = request.getSession(false);
        String homeURI = request.getContextPath() + "/secured/landingpage.xhtml";
        String gameURI = request.getContextPath() + "/secured/game.xhtml";

        boolean loggedIn = false;
        if(session != null){
            if(session.getAttribute("name") != null){
                loggedIn = true;
            }
        }

        boolean inGame = session != null && session.getAttribute("game") != null;

        if (!loggedIn) {
            filterChain.doFilter(request, response);
        } else {
            if(inGame){
                response.sendRedirect(gameURI);
            }else {
                response.sendRedirect(homeURI);
            }
        }
    }
}
