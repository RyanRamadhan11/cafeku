package com.enigma.java_cafetaria.service;

import com.enigma.java_cafetaria.dto.requets.CustomerRequest;
import com.enigma.java_cafetaria.dto.response.CustomerResponse;
import com.enigma.java_cafetaria.entity.Customer;

import java.util.List;

public interface CustomerService {
//    CustomerResponse create(CustomerRequest customerRequest);

    CustomerResponse createNewCustomer(Customer request);

//    CustomerResponse update(CustomerRequest customerRequest);

    void delete(String id);

    CustomerResponse getById(String id);

    List<CustomerResponse> getAll();
}