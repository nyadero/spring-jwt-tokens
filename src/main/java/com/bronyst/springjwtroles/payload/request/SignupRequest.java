package com.bronyst.springjwtroles.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class SignupRequest {
    @NotBlank
    @Size(min = 3, max = 60)
    private String username;

    @NotBlank
    @Size(max=50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 3, max = 40)
    private String password;

    private Set<String> role;
}
