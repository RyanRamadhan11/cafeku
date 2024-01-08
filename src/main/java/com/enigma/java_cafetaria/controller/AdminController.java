package com.enigma.java_cafetaria.controller;


import com.enigma.java_cafetaria.constant.AppPath;
import com.enigma.java_cafetaria.dto.response.AdminResponse;
import com.enigma.java_cafetaria.dto.response.CommonResponse;
import com.enigma.java_cafetaria.dto.response.CustomerResponse;
import com.enigma.java_cafetaria.service.AdminService;
import com.enigma.java_cafetaria.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(AppPath.ADMIN)
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<?> getAllAdmin() {
        List<AdminResponse> adminList = adminService.getAll();

        return ResponseEntity.ok(
                CommonResponse.<List<AdminResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully retrieved all admin")
                        .data(adminList)
                        .build());
    }
}
