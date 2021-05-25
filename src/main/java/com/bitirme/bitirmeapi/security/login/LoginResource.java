package com.bitirme.bitirmeapi.security.login;

import com.bitirme.bitirmeapi.security.MemberDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("login")
public class LoginResource {

    @GetMapping("/success")
    public ResponseEntity<Map<String, Object>> loginSuccess(@AuthenticationPrincipal MemberDetails principal) {
        Map<String, Object> memberInfo = new HashMap<>();
        memberInfo.put("id", principal.getId());
        memberInfo.put("fullName", principal.getFullName());
        memberInfo.put("profilePictureLink", "/api/images/"+principal.getProfilePictureLink());

        return new ResponseEntity<>(memberInfo, HttpStatus.OK);
    }

    @GetMapping("/auth-check")
    public HttpStatus checkAuthentication() { return HttpStatus.OK; }

}
