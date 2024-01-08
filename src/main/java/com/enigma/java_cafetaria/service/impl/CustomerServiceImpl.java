package com.enigma.java_cafetaria.service.impl;

import com.enigma.java_cafetaria.dto.requets.CustomerRequest;
import com.enigma.java_cafetaria.dto.response.CustomerResponse;
import com.enigma.java_cafetaria.entity.Customer;
import com.enigma.java_cafetaria.repository.CustomerRepository;
import com.enigma.java_cafetaria.service.CustomerService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<CustomerResponse> getAll() {
        // Gunakan native query untuk mendapatkan semua data customer
        List<Object[]> resultList = entityManager.createNativeQuery(
                        "SELECT id, name, address, mobile_phone, email FROM m_customer")
                .getResultList();

        return resultList.stream()
                .map(row -> CustomerResponse.builder()
                        .id((String) row[0])
                        .customerName((String) row[1])
                        .address((String) row[2])
                        .phone((String) row[3])
                        .email((String) row[4])
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse createNewCustomer(Customer request) {
        // Generate UUID sebagai ID baru
        String newCustomerId = UUID.randomUUID().toString();

        // Gunakan native query untuk menyimpan data customer baru
        entityManager.createNativeQuery(
                        "INSERT INTO m_customer (id, name, address, mobile_phone, email) " +
                                "VALUES (?, ?, ?, ?, ?)")
                .setParameter(1, newCustomerId)
                .setParameter(2, request.getName())
                .setParameter(3, request.getAddress())
                .setParameter(4, request.getMobilePhone())
                .setParameter(5, request.getEmail())
                .executeUpdate();

        // Ambil data customer yang baru ditambahkan dari database menggunakan query
        Customer newCustomer = entityManager.find(Customer.class, newCustomerId);

        // Kembalikan response
        return CustomerResponse.builder()
                .id(newCustomer.getId())
                .customerName(newCustomer.getName())
                .phone(newCustomer.getMobilePhone())
                .build();
    }

    @Override
    public CustomerResponse getById(String id) {
        // Gunakan native query untuk mendapatkan data customer berdasarkan ID
        Object[] result = (Object[]) entityManager.createNativeQuery(
                        "SELECT id, name, address, mobile_phone, email FROM m_customer WHERE id = ?")
                .setParameter(1, id)
                .getSingleResult();

        if (result != null) {
            return CustomerResponse.builder()
                    .id((String) result[0])
                    .customerName((String) result[1])
                    .address((String) result[2])
                    .phone((String) result[3])
                    .email((String) result[4])
                    .build();
        }
        return null;
    }

    @Override
    public void delete(String id) {
        // Gunakan native query untuk menghapus data customer berdasarkan ID
        int deletedRows = entityManager.createNativeQuery(
                        "DELETE FROM m_customer WHERE id = ?")
                .setParameter(1, id)
                .executeUpdate();

        if (deletedRows > 0) {
            System.out.println("delete succeed");
        } else {
            System.out.println("id not found");
        }
    }
}
