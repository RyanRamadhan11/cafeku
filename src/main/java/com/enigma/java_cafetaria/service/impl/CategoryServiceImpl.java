package com.enigma.java_cafetaria.service.impl;


import com.enigma.java_cafetaria.dto.requets.CategoryRequest;
import com.enigma.java_cafetaria.dto.response.CategoryResponse;
import com.enigma.java_cafetaria.entity.Category;
import com.enigma.java_cafetaria.repository.CategoryRepository;
import com.enigma.java_cafetaria.service.CategoryService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
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
public class CategoryServiceImpl implements CategoryService {

    @PersistenceContext
    private EntityManager entityManager;
    private final CategoryRepository categoryRepository;

    @Override
    public List<CategoryResponse> getAll() {
        String nativeQuery = "SELECT id, category_code, category_name FROM m_category";
        Query query = entityManager.createNativeQuery(nativeQuery);

        List<Object[]> results = query.getResultList();

        List<CategoryResponse> categoryResponses = results.stream()
                .map(result -> CategoryResponse.builder()
                        .id((String) result[0])
                        .categoryCode((String) result[1])
                        .categoryName((String) result[2])
                        .build())
                .collect(Collectors.toList());

        return categoryResponses;
    }

    @Override
    public CategoryResponse create(CategoryRequest categoryRequest) {
        String nativeQuery = "INSERT INTO m_category (id, category_code, category_name) " +
                "VALUES (?, ?, ?)";

        entityManager.createNativeQuery(nativeQuery)
                .setParameter(1, UUID.randomUUID().toString())
                .setParameter(2, categoryRequest.getCategoryCode())
                .setParameter(3, categoryRequest.getCategoryName())
                .executeUpdate();

        // Ambil entity yang baru ditambahkan dari database menggunakan query atau repository
        Category category = categoryRepository.findByCategoryCode(categoryRequest.getCategoryCode());

        return CategoryResponse.builder()
                .id(category.getId())
                .categoryCode(category.getCategoryCode())
                .categoryName(category.getCategoryName())
                .build();
    }

    public CategoryResponse update(CategoryRequest categoryRequest) {
        // Menggunakan native query untuk menemukan kategori berdasarkan ID
        Query findCategoryQuery = entityManager.createNativeQuery(
                        "SELECT * FROM m_category WHERE id = ?", Category.class)
                .setParameter(1, categoryRequest.getId());

        // Eksekusi query native untuk mendapatkan kategori
        List<Category> resultList = findCategoryQuery.getResultList();

        if (!resultList.isEmpty()) {
            Category existingCategory = resultList.get(0);

            // Menggunakan native query untuk update
            Query updateQuery = entityManager.createNativeQuery(
                            "UPDATE m_category SET category_code = ?, category_name = ? WHERE id = ?")
                    .setParameter(1, categoryRequest.getCategoryCode())
                    .setParameter(2, categoryRequest.getCategoryName())
                    .setParameter(3, categoryRequest.getId());

            // Eksekusi query native untuk update
            updateQuery.executeUpdate();

            // Setelah eksekusi, ambil data terbaru dari database
            Category updatedCategory = entityManager.find(Category.class, categoryRequest.getId());

            // Kembalikan response
            return CategoryResponse.builder()
                    .id(updatedCategory.getId())
                    .categoryCode(updatedCategory.getCategoryCode())
                    .categoryName(updatedCategory.getCategoryName())
                    .build();
        } else {
            return null;
        }
    }

    @Override
    public void delete(String id) {
        // Menggunakan native query untuk menghapus
        Query nativeQuery = entityManager.createNativeQuery("DELETE FROM m_category WHERE id = ?")
                .setParameter(1, id);

        // Eksekusi query native
        int deletedRows = nativeQuery.executeUpdate();

        if (deletedRows > 0) {
            System.out.println("Delete category success");
        } else {
            System.out.println("ID not found");
        }
    }

    @Override
    public CategoryResponse getById(String id) {
        // Gunakan native query untuk mendapatkan Kategori berdasarkan ID
        Query nativeQuery = entityManager.createNativeQuery("SELECT * FROM m_category WHERE id = ?", Category.class);
        nativeQuery.setParameter(1, id);

        // Eksekusi query dan dapatkan hasilnya
        Category category = (Category) nativeQuery.getSingleResult();

        if (category != null) {
            return CategoryResponse.builder()
                    .id(category.getId())
                    .categoryCode(category.getCategoryCode())
                    .categoryName(category.getCategoryName())
                    .build();
        }
        return null;

    }
}


