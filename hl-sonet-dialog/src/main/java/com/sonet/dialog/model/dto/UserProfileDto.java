package com.sonet.dialog.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class UserProfileDto {

    private String firstName;
    private String lastName;
    @Past
    private Date birthdate;
    @Size(min = 1, max = 1)
    @Pattern(regexp = "[mfMF]")
    private String gender;
    private String city;
    private String biography;
    private UUID uuid;
    private Integer age;
}
