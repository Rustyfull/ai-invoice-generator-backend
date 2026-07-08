package com.elite.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginRequest {

    @Email
    @NotBlank(message ="email is required")
    private String email;

    @NotBlank(message = "password is required")
    private String password;
}
