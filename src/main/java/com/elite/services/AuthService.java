package com.elite.services;

import com.elite.request.LoginRequest;
import com.elite.request.RegisterRequest;
import com.elite.request.UpdateUserRequest;
import com.elite.response.LoginResponse;
import com.elite.response.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse getMe();
    LoginResponse updateUserProfile(UpdateUserRequest updateUserRequest, String id);
}
