package com.engagetech.expenses.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Logout controller for Basic auth configuration.
 */
@RestController
public class LogoutController {

    @GetMapping("/app/logout")
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler()
                    .logout(request, response, auth);
        }
        if (request.getSession() != null) {
            request.getSession().invalidate();
        }
        return "Logout";
    }
}
