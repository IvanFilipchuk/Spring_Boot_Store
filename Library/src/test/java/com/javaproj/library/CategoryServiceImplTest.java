package com.javaproj.library;

import com.javaproj.library.model.Category;
import com.javaproj.library.repository.CategoryRepository;
import com.javaproj.library.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void saveCategory() {
        Category inputCategory = new Category();
        inputCategory.setName("TestCategory");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("TestCategory");
        savedCategory.setActivated(true);
        savedCategory.setDeleted(false);

        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Category result = categoryService.save(inputCategory);

        assertEquals(savedCategory.getId(), result.getId());
        assertEquals(savedCategory.getName(), result.getName());
        assertEquals(savedCategory.isActivated(), result.isActivated());
        assertEquals(savedCategory.isDeleted(), result.isDeleted());

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory() {
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("ExistingCategory");

        Category updatedCategory = new Category();
        updatedCategory.setId(1L);
        updatedCategory.setName("UpdatedCategory");

        when(categoryRepository.getReferenceById(1L)).thenReturn(existingCategory);
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        Category result = categoryService.update(updatedCategory);

        assertEquals(updatedCategory.getId(), result.getId());
        assertEquals(updatedCategory.getName(), result.getName());

        verify(categoryRepository, times(1)).getReferenceById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void findAllByActivatedTrue() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Category1");
        category1.setActivated(true);
        category1.setDeleted(false);

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Category2");
        category2.setActivated(true);
        category2.setDeleted(false);

        when(categoryRepository.findAllByActivatedTrue()).thenReturn(Arrays.asList(category1, category2));

        List<Category> result = categoryService.findAllByActivatedTrue();

        assertEquals(2, result.size());
        assertEquals(category1, result.get(0));
        assertEquals(category2, result.get(1));

        verify(categoryRepository, times(1)).findAllByActivatedTrue();
    }

    @Test
    void deleteCategoryById() {
        long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setActivated(true);
        category.setDeleted(false);

        when(categoryRepository.getById(categoryId)).thenReturn(category);

        categoryService.deleteById(categoryId);

        assertFalse(category.isActivated());
        assertTrue(category.isDeleted());

        verify(categoryRepository, times(1)).getById(categoryId);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void enableCategoryById() {
        long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setActivated(false);
        category.setDeleted(true);

        when(categoryRepository.getById(categoryId)).thenReturn(category);

        categoryService.enableById(categoryId);

        assertTrue(category.isActivated());
        assertFalse(category.isDeleted());

        verify(categoryRepository, times(1)).getById(categoryId);
        verify(categoryRepository, times(1)).save(category);
    }

    @Test
    void findOrCreateCategory_ExistingCategory() {
        String categoryName = "ExistingCategory";
        Category existingCategory = new Category();
        existingCategory.setName(categoryName);

        when(categoryRepository.findByName(categoryName)).thenReturn(Optional.of(existingCategory));

        Category result = categoryService.findOrCreateCategory(categoryName);

        assertEquals(existingCategory, result);

        verify(categoryRepository, times(1)).findByName(categoryName);
        verify(categoryRepository, never()).save(any(Category.class));
    }

}