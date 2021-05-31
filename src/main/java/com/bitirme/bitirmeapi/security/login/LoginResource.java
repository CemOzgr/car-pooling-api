package com.bitirme.bitirmeapi.security.login;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("login")
public class LoginResource {

    @GetMapping("/auth-check")
    public HttpStatus checkAuthentication() { return HttpStatus.OK; }

}
