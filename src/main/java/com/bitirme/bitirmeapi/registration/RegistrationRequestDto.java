package com.bitirme.bitirmeapi.registration;

import com.bitirme.bitirmeapi.registration.validation.ValidPassword;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class RegistrationRequestDto {

    @Pattern(regexp = "^[a-zA-ZiİşŞçÇöÖüÜğ]+$",
            message = "First name can not contain special characters, numbers and whitespace")
    private String firstName;

    @Pattern(regexp = "^[a-zA-ZiİşŞçÇöÖüÜğ]+$",
            message = "Last name can not contain special characters, numbers and whitespace")
    private String lastName;

    @NotEmpty(message = "email can not be empty")
    @Email(message = "email is not valid")
    private String email;

    @Pattern(regexp = "^$|(^(05(\\d{9}))$)", message = "Contact number is not valid")
    private String contactNo;

    @ValidPassword
    private String password;

    @NotEmpty(message = "matching password can not be empty")
    private String matchingPassword;

}
