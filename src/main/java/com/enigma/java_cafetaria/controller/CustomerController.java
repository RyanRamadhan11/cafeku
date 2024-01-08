package com.enigma.java_cafetaria.controller;

import com.enigma.java_cafetaria.constant.AppPath;
import com.enigma.java_cafetaria.dto.requets.CustomerRequest;
import com.enigma.java_cafetaria.dto.response.CommonResponse;
import com.enigma.java_cafetaria.dto.response.CustomerResponse;
import com.enigma.java_cafetaria.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppPath.CUSTOMER)
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<?> getAllCustomer() {
        List<CustomerResponse> customerList = customerService.getAll();

        return ResponseEntity.ok(
                CommonResponse.<List<CustomerResponse>>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully retrieved all customer")
                        .data(customerList)
                        .build());
    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN')")

    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable String id) {
        CustomerResponse customerResponse = customerService.getById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.<CustomerResponse>builder()
                        .statusCode(HttpStatus.OK.value())
                        .message("Successfully get customer by id")
                        .data(customerResponse)
                        .build());
    }

    //    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable String id) {
        customerService.delete(id);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.builder()
                        .statusCode(HttpStatus.CREATED.value())
                        .message("Successfully Delete Customer")
                        .data(HttpStatus.OK)
                        .build());
    }
}
