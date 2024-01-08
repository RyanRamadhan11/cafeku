package com.enigma.java_cafetaria.service.impl;

import com.enigma.java_cafetaria.constant.ERole;
import com.enigma.java_cafetaria.entity.AppUser;
import com.enigma.java_cafetaria.entity.UserCredential;
import com.enigma.java_cafetaria.repository.UserCredentialRepository;
import com.enigma.java_cafetaria.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserCredentialRepository userCredentialRepository;

    @Override
    public AppUser loadUserByUserId(String id) {
        // Gunakan native query untuk mendapatkan data userCredential berdasarkan ID
        Object[] result = (Object[]) entityManager.createNativeQuery(
                        "SELECT uc.id, uc.username, uc.password, r.name " +
                                "FROM m_user_credential uc " +
                                "JOIN m_role r ON uc.role_id = r.id " +
                                "WHERE uc.id = ?")
                .setParameter(1, id)
                .getSingleResult();

        if (result != null) {
            return AppUser.builder()
                    .id((String) result[0])
                    .username((String) result[1])
                    .password((String) result[2])
                    .role(ERole.valueOf((String) result[3]))
                    .build();
        } else {
            throw new UsernameNotFoundException("Invalid credential");
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Gunakan native query untuk mendapatkan data userCredential berdasarkan username
        Object[] result = (Object[]) entityManager.createNativeQuery(
                        "SELECT uc.id, uc.username, uc.password, r.name " +
                                "FROM m_user_credential uc " +
                                "JOIN m_role r ON uc.role_id = r.id " +
                                "WHERE uc.username = ?")
                .setParameter(1, username)
                .getSingleResult();

        if (result != null) {
            return AppUser.builder()
                    .id((String) result[0])
                    .username((String) result[1])
                    .password((String) result[2])
                    .role(ERole.valueOf((String) result[3]))
                    .build();
        } else {
            throw new UsernameNotFoundException("Invalid credential");
        }
    }



    //tanpa native query
//    @Override
//    //method ini untuk memverifikasi jwt nya
//    public AppUser loadUserByUserId(String id) {
//        UserCredential userCredential = userCredentialRepository.findById(id).orElseThrow(()-> new UsernameNotFoundException("invalid credential"));
//
//        return AppUser.builder()
//                .id(userCredential.getId())
//                .username(userCredential.getUsername())
//                .password(userCredential.getPassword())
//                .role(userCredential.getRole().getName())
//                .build();
//    }



    //tanpa native query
//    @Override
//    //method ini untuk cek by usernamenya sebagai authentication untuk login
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
//
//        UserCredential userCredential = userCredentialRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("invalid credential"));
//
//        return AppUser.builder()
//                .id(userCredential.getId())
//                .username(userCredential.getUsername())
//                .password(userCredential.getPassword())
//                .role(userCredential.getRole().getName())
//                .build();
//    }
}