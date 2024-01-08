package com.enigma.java_cafetaria.service.impl;

import com.enigma.java_cafetaria.entity.MenuPrice;
import com.enigma.java_cafetaria.repository.MenuPriceRepository;
import com.enigma.java_cafetaria.service.MenuPriceService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MenuPriceServiceImpl implements MenuPriceService {

    @PersistenceContext
    private EntityManager entityManager;
    private final MenuPriceRepository menuPriceRepository;

    @Override
    public MenuPrice create(MenuPrice menuPrice) {
        String id = UUID.randomUUID().toString();
        String nativeQuery = "INSERT INTO m_menu_price (id, is_active, price, category_id, menu_id) VALUES (?, ?, ?, ?, ?)";
        Query query = entityManager.createNativeQuery(nativeQuery);

        query.setParameter(1, id);
        query.setParameter(2, menuPrice.isActive());
        query.setParameter(3, menuPrice.getPrice());
        query.setParameter(4, menuPrice.getCategory().getId()); // Sesuaikan dengan ID yang sesuai di objek Category
        query.setParameter(5, menuPrice.getMenu().getId());     // Sesuaikan dengan ID yang sesuai di objek Menu

        query.executeUpdate();

        // Mengembalikan objek MenuPrice yang sama yang telah diisi dengan ID baru
        menuPrice.setId(id);
        return menuPrice;
    }

//tanpa native query
//    @Override
//    public MenuPrice create(MenuPrice menuPrice) {
//        return menuPriceRepository.save(menuPrice);
//    }


    @Override
    public MenuPrice createOrUpdate(MenuPrice menuPrice) {
        if (menuPrice.getId() == null) {
            // Jika ID belum ada, itu adalah produk harga baru yang perlu dibuat
            String sql = "INSERT INTO m_menu_price (is_active, price, category_id, menu_id) " +
                    "VALUES (?, ?, ?, ?)";

            entityManager.createNativeQuery(sql)
                    .setParameter(1, menuPrice.isActive())
                    .setParameter(2, menuPrice.getPrice())
                    .setParameter(3, menuPrice.getCategory().getId())
                    .setParameter(4, menuPrice.getMenu().getId())
                    .executeUpdate();
        } else {
            // Jika ID sudah ada, itu adalah produk harga yang perlu diperbarui
            String sql = "UPDATE m_menu_price " +
                    "SET is_active = ?, price = ?, category_id = ?, menu_id = ? " +
                    "WHERE id = ?";

            entityManager.createNativeQuery(sql)
                    .setParameter(1, menuPrice.isActive())
                    .setParameter(2, menuPrice.getPrice())
                    .setParameter(3, menuPrice.getCategory().getId())
                    .setParameter(4, menuPrice.getMenu().getId())
                    .setParameter(5, menuPrice.getId())
                    .executeUpdate();
        }

        return menuPrice;
    }

    //tanpa native query
//    @Override
//    public MenuPrice createOrUpdate(MenuPrice menuPrice) {
//        if (menuPrice.getId() == null) {
//            // Jika ID belum ada, itu adalah produk harga baru yang perlu dibuat
//            return menuPriceRepository.save(menuPrice);
//        } else {
//            // Jika ID sudah ada, itu adalah produk harga yang perlu diperbarui
//            return menuPriceRepository.saveAndFlush(menuPrice);
//        }
//    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM m_menu_price WHERE id = ?";

        entityManager.createNativeQuery(sql)
                .setParameter(1, id)
                .executeUpdate();
    }

    //tanpa native query
//    @Override
//    public void delete(String id) {
//
//        menuPriceRepository.deleteById(id);
//    }

    @Override
    public MenuPrice getById(String id) {
        String sql = "SELECT * FROM m_menu_price WHERE id = ?";

        return (MenuPrice) entityManager.createNativeQuery(sql, MenuPrice.class)
                .setParameter(1, id)
                .getSingleResult();
    }

    //tanpa native query
//    @Override
//    public MenuPrice getById(String id) {
//        return menuPriceRepository.findById(id).orElseThrow();
//    }

}
