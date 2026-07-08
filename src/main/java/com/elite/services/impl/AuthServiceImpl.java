package com.elite.services.impl;

import com.elite.exceptions.UserAlreadyExistsException;
import com.elite.models.User;
import com.elite.repository.UserRepository;
import com.elite.request.LoginRequest;
import com.elite.request.RegisterRequest;
import com.elite.request.UpdateUserRequest;
import com.elite.response.LoginResponse;
import com.elite.response.RegisterResponse;
import com.elite.services.AuthService;
import com.elite.utils.JwtUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;




@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtility jwtUtility;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public RegisterResponse register(RegisterRequest registerRequest) {
        log.info("Register request: {}", registerRequest);
          if(userRepository.findByEmail(registerRequest.getEmail()).isPresent()){
              throw new UserAlreadyExistsException("Email already exists");
          }


          User user = User
                  .builder()
                  .name(registerRequest.getName())
                  .email(registerRequest.getEmail())
                  .password(passwordEncoder.encode(registerRequest.getPassword()))
                  .build();

          User savedUser = userRepository.save(user);

          return RegisterResponse
                  .builder()
                  .id(savedUser.getId())
                  .email(savedUser.getEmail())
                  .name(savedUser.getName())
                  .token(jwtUtility.generateToken((UserDetails) savedUser))
                  .build();

    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        var user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        var jwt = jwtUtility.generateToken((UserDetails) user);

        return LoginResponse
                .builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .token(jwt)
                .companyName(user.getCompanyName())
                .address(user.getAddress())
                .phone(user.getPhone())
                .build();

    }

    @Override
    public LoginResponse getMe() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication == null || !authentication.isAuthenticated()){
            throw new UsernameNotFoundException("User not found");
        }

        User user = (User) authentication.getPrincipal();
        assert user != null;
        return LoginResponse.
                builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .companyName(user.getCompanyName())
                .address(user.getAddress())
                .phone(user.getPhone())
                .build();
    }

    @Override
    public LoginResponse updateUserProfile(UpdateUserRequest updateUserRequest, String id) {
        var user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if(updateUserRequest.getCompanyName() != null){
            user.setCompanyName(updateUserRequest.getCompanyName());
        }

        if(updateUserRequest.getAddress() != null){
            user.setAddress(updateUserRequest.getAddress());
        }

        if(updateUserRequest.getPhone() != null){
            user.setPhone(updateUserRequest.getPhone());
        }

        if(updateUserRequest.getName() != null){
            user.setName(updateUserRequest.getName());
        }

        var updatedUser = userRepository.save(user);

        return LoginResponse.builder()
                .id(updatedUser.getId())
                .email(updatedUser.getEmail())
                .phone(updatedUser.getPhone())
                .address(updatedUser.getAddress())
                .companyName(updatedUser.getCompanyName())
                .name(updatedUser.getName())
                .build();
    }
}
