package com.bitirme.bitirmeapi.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/registration")
public class RegistrationResource {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationResource(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("")
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationRequestDto requestDto, Errors errors) {
        if(errors.hasErrors()) {
            String errorMessages = errors.getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining("\n"));
            return new ResponseEntity<>(errorMessages, HttpStatus.BAD_REQUEST);
        }
        if(!requestDto.getPassword().equals(requestDto.getMatchingPassword())) {
            return new ResponseEntity<>("passwords not match", HttpStatus.BAD_REQUEST);
        }
        registrationService.register(requestDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/confirm")
    public HttpStatus confirmToken(@RequestParam String token) {
        registrationService.confirmToken(token);
        return HttpStatus.OK;
    }


}
