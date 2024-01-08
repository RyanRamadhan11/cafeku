package com.enigma.java_cafetaria.service.impl;

import com.enigma.java_cafetaria.dto.requets.MenuRequest;
import com.enigma.java_cafetaria.dto.response.CategoryResponse;
import com.enigma.java_cafetaria.dto.response.MenuResponse;
import com.enigma.java_cafetaria.entity.Category;
import com.enigma.java_cafetaria.entity.Menu;
import com.enigma.java_cafetaria.entity.MenuPrice;
import com.enigma.java_cafetaria.repository.MenuRepository;
import com.enigma.java_cafetaria.service.CategoryService;
import com.enigma.java_cafetaria.service.MenuPriceService;
import com.enigma.java_cafetaria.service.MenuService;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final CategoryService categoryService;
    private final MenuPriceService menuPriceService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Menu create(Menu menu) {
        String sql = "INSERT INTO m_menu (menu_code, menu_name, status, category_id) VALUES (?, ?, ?, ?)";

        entityManager.createNativeQuery(sql)
                .setParameter(1, menu.getMenuCode())
                .setParameter(2, menu.getMenuName())
                .setParameter(3, menu.getStatus())
                .setParameter(4, menu.getCategory().getId())
                .executeUpdate();

        return menu;
    }

    @Override
    public List<Menu> getAll() {
        String sql = "SELECT * FROM m_menu";

        Query query = entityManager.createNativeQuery(sql, Menu.class);
        List<Menu> menus = query.getResultList();

        return menus;
    }

    @Override
//    @Transactional
    public Menu getById(String id) {
        String sql = "SELECT * FROM m_menu WHERE id = ?";

        Query query = entityManager.createNativeQuery(sql, Menu.class)
                .setParameter(1, id);

        try {
            return (Menu) query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Menu update(Menu menu) {
        String sql = "UPDATE m_menu SET menu_code = ?, menu_name = ?, status = ?, category_id = ? WHERE id = ?";

        entityManager.createNativeQuery(sql)
                .setParameter(1, menu.getMenuCode())
                .setParameter(2, menu.getMenuName())
                .setParameter(3, menu.getStatus())
                .setParameter(4, menu.getCategory().getId())
                .setParameter(5, menu.getId())
                .executeUpdate();

        // Refresh the entity to reflect the changes
        entityManager.refresh(menu);

        return menu;
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM m_menu WHERE id = ?";

        int deletedRows = entityManager.createNativeQuery(sql)
                .setParameter(1, id)
                .executeUpdate();

        if (deletedRows == 0) {
            throw new EntityNotFoundException("Menu with id " + id + " not found");
        }
    }

    @Override
    public MenuResponse createMenuAndMenuPrice(MenuRequest menuRequest) {
        CategoryResponse categoryResponse = categoryService.getById(menuRequest.getCategoryId().getId());

        String idMenuUUID = UUID.randomUUID().toString();

        // Create Menu using native query
        String menuInsertSql = "INSERT INTO m_menu (id, menu_code, menu_name, status, category_id) VALUES (?, ?, ?, ?, ?)";

        entityManager.createNativeQuery(menuInsertSql)
                .setParameter(1, idMenuUUID)
                .setParameter(2, menuRequest.getMenuCode())
                .setParameter(3, menuRequest.getMenuName())
                .setParameter(4, menuRequest.getStatus())
                .setParameter(5, categoryResponse.getId())
                .executeUpdate();

        // Retrieve the generated ID for the Menu
        String menuIdSql = "SELECT id FROM m_menu WHERE menu_code = ?";
        String menuId = (String) entityManager.createNativeQuery(menuIdSql)
                .setParameter(1, menuRequest.getMenuCode())
                .getSingleResult();


        // Create MenuPrice using native query
        String menuPriceInsertSql = "INSERT INTO m_menu_price (id, is_active, price, category_id, menu_id) VALUES (?, ?, ?, ?, ?)";

        // Generate ID for MenuPrice
        String menuPriceId = UUID.randomUUID().toString();

        entityManager.createNativeQuery(menuPriceInsertSql)
                .setParameter(1, menuPriceId)
                .setParameter(2, true)
                .setParameter(3, menuRequest.getPrice())
                .setParameter(4, categoryResponse.getId())
                .setParameter(5, menuId)
                .executeUpdate();

        return MenuResponse.builder()
                .menuId(menuId)
                .menuPriceId(menuPriceId)
                .menuCode(menuRequest.getMenuCode())
                .menuName(menuRequest.getMenuName())
                .status(menuRequest.getStatus())
                .price(menuRequest.getPrice())
                .category(CategoryResponse.builder()
                        .id(categoryResponse.getId())
                        .categoryCode(categoryResponse.getCategoryCode())
                        .categoryName(categoryResponse.getCategoryName())
                        .build())
                .build();
    }

    @Override
    public MenuResponse updateMenuAndMenuPrice(MenuRequest menuRequest) {
        CategoryResponse categoryResponse = categoryService.getById(menuRequest.getCategoryId().getId());

        // Retrieve existing Menu using native query
        String menuSelectSql = "SELECT * FROM m_menu WHERE id = ?";

        Menu menu = (Menu) entityManager.createNativeQuery(menuSelectSql, Menu.class)
                .setParameter(1, menuRequest.getId())
                .getSingleResult();

        if (menu != null) {
            // Update properties of the Menu
            menu.setMenuCode(menuRequest.getMenuCode());
            menu.setMenuName(menuRequest.getMenuName());
            menu.setStatus(menuRequest.getStatus());
            menu.setCategory(Category.builder()
                    .id(categoryResponse.getId())
                    .build());

            // Save changes to the Menu
            entityManager.merge(menu);

            // Retrieve existing MenuPrice using native query
            String menuPriceSelectSql = "SELECT * FROM m_menu_price WHERE menu_id = ?";

            MenuPrice menuPrice = null;
            try {
                menuPrice = (MenuPrice) entityManager.createNativeQuery(menuPriceSelectSql, MenuPrice.class)
                        .setParameter(1, menu.getId())
                        .getSingleResult();
            } catch (NoResultException e) {
                // No existing MenuPrice found, create a new one
                menuPrice = new MenuPrice();
            }

            // Update properties of the MenuPrice
            menuPrice.setActive(true);
            menuPrice.setPrice(menuRequest.getPrice());
            menuPrice.setCategory(Category.builder()
                    .id(categoryResponse.getId())
                    .build());
            menuPrice.setMenu(menu);

            // Save changes to the MenuPrice
            entityManager.merge(menuPrice);

            // Return the corresponding response
            return MenuResponse.builder()
                    .menuId(menu.getId())
                    .menuPriceId(menuPrice.getId())
                    .menuCode(menu.getMenuCode())
                    .menuName(menu.getMenuName())
                    .status(menu.getStatus())
                    .price(menuPrice.getPrice())
                    .category(CategoryResponse.builder()
                            .id(categoryResponse.getId())
                            .categoryCode(categoryResponse.getCategoryCode())
                            .categoryName(categoryResponse.getCategoryName())
                            .build())
                    .build();
        } else {
            // Menu not found
            return null;
        }
    }

    @Override
    public MenuResponse getMenuAndMenuPriceByCategoryId(String id) {
        // Mendapatkan branch berdasarkan ID
        CategoryResponse categoryResponse = categoryService.getById(id);

        // Mendapatkan harga produk pertama yang aktif jika ada dengan native query
        String nativeQuery = "SELECT mp.* FROM m_menu_price mp " +
                "INNER JOIN m_menu m ON mp.menu_id = m.id " +
                "WHERE m.category_id = ? AND mp.is_active = true " +
                "LIMIT 1";


        List<MenuPrice> menuPrices = entityManager.createNativeQuery(nativeQuery, MenuPrice.class)
                .setParameter(1, id)
                .getResultList();

        // Membuat respons produk jika harga produk aktif ditemukan
        return menuPrices.stream().findFirst().map(price ->
                        MenuResponse.builder()
                                .menuId(price.getMenu().getId())
                                .menuPriceId(price.getMenu().getId())
                                .menuCode(price.getMenu().getMenuCode())
                                .menuName(price.getMenu().getMenuName())
                                .status(price.getMenu().getStatus())
                                .price(price.getPrice())
                                .category(CategoryResponse.builder()
                                        .id(categoryResponse.getId())
                                        .categoryCode(categoryResponse.getCategoryCode())
                                        .categoryName(categoryResponse.getCategoryName())
                                        .build())
                                .build())
                .orElse(null); // Mengembalikan null jika tidak ada harga produk aktif
    }

    @Override
    public void deleteMenuAndMenuPrice(String id) {
        // Delete each MenuPrice using native query
        String deleteMenuPriceSql = "DELETE FROM m_menu_price WHERE menu_id = ?";

        entityManager.createNativeQuery(deleteMenuPriceSql)
                .setParameter(1, id)
                .executeUpdate();

        // Delete the Menu using native query
        String deleteMenuSql = "DELETE FROM m_menu WHERE id = ?";

        entityManager.createNativeQuery(deleteMenuSql)
                .setParameter(1, id)
                .executeUpdate();
    }



    @Override
    public Page<MenuResponse> getAllMenuByCodeNamePrice(String menuCode, String menuName, Long minPrice, Long maxPrice, Integer page, Integer size) {
        Specification<Menu> specification = (root, query, criteriaBuilder) -> {

            Join<Menu, MenuPrice> menuPrices = root.join("menuPrices");
            List<Predicate> predicates = new ArrayList<>();

            if (menuCode != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("menuCode")), "%" + menuCode.toLowerCase() + "%"));
            }
            if (menuName != null) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("menuName")), "%" + menuName.toLowerCase() + "%"));
            }
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(menuPrices.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(menuPrices.get("price"), maxPrice));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        Pageable pageable = PageRequest.of(page, size);

        Page<Menu> menus = menuRepository.findAll(specification, pageable);

        List<MenuResponse> menuResponses = new ArrayList<>();

        for (Menu menu : menus.getContent()) {

            Optional<MenuPrice> menuPrice = menu.getMenuPrices()
                    .stream()
                    .filter(MenuPrice::isActive).findFirst();

            if (menuPrice.isEmpty()) continue;

            Category category = menuPrice.get().getCategory();

            menuResponses.add(MenuResponse.builder()
                    .menuId(menu.getId())
                    .menuPriceId(menuPrice.get().getId())
                    .menuCode(menu.getMenuCode())
                    .menuName(menu.getMenuName())
                    .status(menu.getStatus())
                    .price(menuPrice.get().getPrice())
                    .category(CategoryResponse.builder()
                            .id(category.getId())
                            .categoryCode(category.getCategoryCode())
                            .categoryName(category.getCategoryName())
                            .build())
                    .build());
        }
        return new PageImpl<>(menuResponses, pageable, menus.getTotalElements());
    }

}
