package com.enigma.java_cafetaria.controller;

import com.enigma.java_cafetaria.constant.AppPath;
import com.enigma.java_cafetaria.dto.requets.AuthRequest;
import com.enigma.java_cafetaria.dto.response.CommonResponse;
import com.enigma.java_cafetaria.dto.response.LoginResponse;
import com.enigma.java_cafetaria.dto.response.RegisterResponse;
import com.enigma.java_cafetaria.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppPath.AUTH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody AuthRequest authRequest) {
        RegisterResponse registerResponse = authService.registerAdmin(authRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<RegisterResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully created register new Admin")
                        .data(registerResponse)
                        .build());
    }

    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(@RequestBody AuthRequest authRequest) {
        RegisterResponse registerResponse = authService.registerCustomer(authRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.<RegisterResponse>builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully created register new customer")
                        .data(registerResponse)
                        .build());
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginCustomer(@RequestBody AuthRequest authRequest) {
        LoginResponse loginResponse = authService.login(authRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<LoginResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully Login")
                        .data(loginResponse)
                        .build());
    }
}
