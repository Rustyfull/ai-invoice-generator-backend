package com.elite.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginResponse {
    private String id;
    private String name;
    private String email;
    private String token;
    private String companyName;
    private String address;
    private String phone;
}
