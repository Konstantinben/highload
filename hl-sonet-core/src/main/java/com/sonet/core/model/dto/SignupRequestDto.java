package com.sonet.core.model.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.util.Date;

@Data
public class SignupRequestDto {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String firstName;

    private String lastName;

    @Past
    private Date birthdate;

    @Size(min = 1, max = 1)
    @Pattern(regexp = "[mfMF]")
    private String gender;

    private String city;

    private String biography;

    @NotBlank
    private String password;
}
