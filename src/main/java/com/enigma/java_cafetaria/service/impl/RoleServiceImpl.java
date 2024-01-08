package com.enigma.java_cafetaria.service.impl;

import com.enigma.java_cafetaria.entity.Role;
import com.enigma.java_cafetaria.repository.RoleRepository;
import com.enigma.java_cafetaria.service.RoleService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class RoleServiceImpl implements RoleService {

    @PersistenceContext
    private EntityManager entityManager;

    //inject
    private final RoleRepository repository;

    @Override
    public Role getOrSave(Role role) {
        Optional<Role> optionalRole = repository.findByName(role.getName());

        if (optionalRole.isPresent()) {
            return optionalRole.get();
        } else {
            // Gunakan native query untuk menyimpan role baru
            entityManager.createNativeQuery(
                            "INSERT INTO m_role (id, name) VALUES (?, ?)")
                    .setParameter(1, role.getId())
                    .setParameter(2, role.getName().name())
                    .executeUpdate();

            // Ambil data role yang baru ditambahkan dari database menggunakan query
            Role newRole = entityManager.find(Role.class, role.getId());

            // Kembalikan role yang baru ditambahkan
            return newRole;
        }
    }



    //tanpa native query
//    @Override
//    public Role getOrSave(Role role) {
//        Optional<Role> optionalRole = repository.findByName(role.getName());
//        //jika ada di DB di get
//        if (!optionalRole.isEmpty()){
//            return optionalRole.get();
//        }
//
//        return repository.save(role);
//    }

}

