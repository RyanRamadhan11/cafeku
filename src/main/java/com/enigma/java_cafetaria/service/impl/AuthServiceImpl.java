package com.enigma.java_cafetaria.service.impl;

import com.enigma.java_cafetaria.constant.ERole;
import com.enigma.java_cafetaria.dto.requets.AuthRequest;
import com.enigma.java_cafetaria.dto.response.LoginResponse;
import com.enigma.java_cafetaria.dto.response.RegisterResponse;
import com.enigma.java_cafetaria.entity.*;
import com.enigma.java_cafetaria.repository.AdminRepository;
import com.enigma.java_cafetaria.repository.UserCredentialRepository;
import com.enigma.java_cafetaria.security.JwtUtil;
import com.enigma.java_cafetaria.service.AuthService;
import com.enigma.java_cafetaria.service.CustomerService;
import com.enigma.java_cafetaria.service.RoleService;
import com.enigma.java_cafetaria.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class AuthServiceImpl implements AuthService {

    @PersistenceContext
    private EntityManager entityManager;


    private final UserCredentialRepository userCredentialRepository;
    private final PasswordEncoder passwordEncoder;


    private final CustomerService customerService;
    private final RoleService roleService;

    private final AdminRepository adminRepository;

    private final ValidationUtil validationUtil;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;


    @Override
    public RegisterResponse registerCustomer(AuthRequest request) {
        try {
            //TODO 1: Set Role
            Query roleQuery = entityManager.createNativeQuery(
                            "SELECT * FROM m_role WHERE name = ?", Role.class)  // Menggunakan Role.class untuk menentukan hasil query
                    .setParameter(1, ERole.ROLE_CUSTOMER.name());

            Role role = (Role) roleQuery.getSingleResult();

            // If role does not exist, create and save it
            if (role == null) {
                role = Role.builder()
                        .name(ERole.ROLE_CUSTOMER)
                        .build();
                entityManager.persist(role);
            }

            //TODO 2: Set Credential
            Query credentialQuery = entityManager.createNativeQuery(
                            "SELECT * FROM m_user_credential WHERE username = ?")
                    .setParameter(1, request.getUsername());

            UserCredential existingCredential = (UserCredential) credentialQuery.getResultList().stream().findFirst().orElse(null);

            // Check if credential already exists
            if (existingCredential != null) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
            }

            UserCredential userCredential = UserCredential.builder()
                    .username(request.getUsername())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(role)
                    .build();

            entityManager.persist(userCredential);

            //TODO 3: Set Customer
            Customer customer = Customer.builder()
                    .userCredential(userCredential)
                    .name(request.getCustomerName())
                    .address(request.getAddress())
                    .email(request.getEmail())
                    .mobilePhone(request.getMobilePhone())
                    .build();

            customerService.createNewCustomer(customer);

            return RegisterResponse.builder()
                    .username(userCredential.getUsername())
                    .role(userCredential.getRole().getName().toString())
                    .build();

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
    }

    @Override
    public RegisterResponse registerAdmin(AuthRequest request) {
        try {
            // TODO 1: Set Role
            Query roleQuery = entityManager.createNativeQuery(
                            "SELECT * FROM m_role WHERE name = ?", Role.class)
                    .setParameter(1, ERole.ROLE_ADMIN.name());

            Role role = (Role) roleQuery.getSingleResult();

            // TODO 2: Set Credential
            String userCredentialId = UUID.randomUUID().toString();
            Query credentialQuery = entityManager.createNativeQuery(
                            "INSERT INTO m_user_credential (id, username, password, role_id) VALUES (?, ?, ?, ?)")
                    .setParameter(1, userCredentialId)
                    .setParameter(2, request.getUsername())
                    .setParameter(3, passwordEncoder.encode(request.getPassword()))
                    .setParameter(4, role.getId());

            credentialQuery.executeUpdate();

            // Query untuk mendapatkan userCredential setelah penyimpanan
            Query getUserCredentialQuery = entityManager.createNativeQuery(
                            "SELECT * FROM m_user_credential WHERE id = ?", UserCredential.class)
                    .setParameter(1, userCredentialId);

            UserCredential userCredential = (UserCredential) getUserCredentialQuery.getSingleResult();

            // TODO 3: Set Admin
            Query adminQuery = entityManager.createNativeQuery(
                            "INSERT INTO m_admin (id, name, email, phone, user_credential_id) VALUES (?, ?, ?, ?, ?)")
                    .setParameter(1, UUID.randomUUID().toString())
                    .setParameter(2, request.getUsername())
                    .setParameter(3, request.getEmail())
                    .setParameter(4, request.getMobilePhone())
                    .setParameter(5, userCredential.getId());

            adminQuery.executeUpdate();

            return RegisterResponse.builder()
                    .username(request.getUsername())
                    .role(role.getName().toString())
                    .build();
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "admin already exists");
        }
    }


    @Override
    public LoginResponse login(AuthRequest authRequest) {
        //tempat untuk logic login
        validationUtil.validate(authRequest);

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                authRequest.getUsername().toLowerCase(),
                authRequest.getPassword()
        ));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        AppUser appUser = (AppUser) authentication.getPrincipal();
        String token = jwtUtil.generateToken(appUser);

        return LoginResponse.builder()
                .token(token)
                .role(appUser.getRole().name())
                .build();
    }
}
