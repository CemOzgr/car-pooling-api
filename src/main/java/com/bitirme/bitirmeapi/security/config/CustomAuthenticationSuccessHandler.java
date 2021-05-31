package com.bitirme.bitirmeapi.security.config;

import com.bitirme.bitirmeapi.security.MemberDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        if(principal instanceof MemberDetails) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("id",((MemberDetails) principal).getId());
            payload.put("fullName", ((MemberDetails) principal).getFullName());

            if(((MemberDetails) principal).getProfilePictureLink() != null) {
                payload.put("profilePictureLink", "/api/images/"+((MemberDetails) principal).getProfilePictureLink());
            }

            String json = new ObjectMapper().writeValueAsString(payload);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        }
    }
}
