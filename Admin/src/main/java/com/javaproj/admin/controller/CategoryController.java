package com.javaproj.admin.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaproj.library.model.Category;
import com.javaproj.library.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/categories")
    public String categories(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return "redirect:/login";
        }
        model.addAttribute("title", "Manage Category");
        List<Category> categories = categoryService.findALl();
        model.addAttribute("categories", categories);
        model.addAttribute("size", categories.size());
        model.addAttribute("categoryNew", new Category());
        return "categories";
    }

    @PostMapping("/save-category")
    public String save(@ModelAttribute("categoryNew") Category category, Model model, RedirectAttributes redirectAttributes) {
        try {
            categoryService.save(category);
            model.addAttribute("categoryNew", category);
            redirectAttributes.addFlashAttribute("success", "Dodano pomyślnie!");
        } catch (DataIntegrityViolationException e1) {
            e1.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Duplikat nazwy kategorii, proszę sprawdzić ponownie!");
        } catch (Exception e2) {
            e2.printStackTrace();
            model.addAttribute("categoryNew", category);
            redirectAttributes.addFlashAttribute("error", "Błąd serwera");
        }
        return "redirect:/categories";
    }


    @RequestMapping(value = "/findById", method = {RequestMethod.PUT, RequestMethod.GET})
    @ResponseBody
    public Optional<Category> findById(Long id) {
        return categoryService.findById(id);
    }

    @GetMapping("/update-category")
    public String update(Category category, RedirectAttributes redirectAttributes) {
        try {
            categoryService.update(category);
            redirectAttributes.addFlashAttribute("success", "Pomyślnie zaktualizowano!");
        } catch (DataIntegrityViolationException e1) {
            e1.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Duplikat nazwy kategorii, proszę sprawdzić ponownie!");
        } catch (Exception e2) {
            e2.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Błąd serwera lub duplikat nazwy kategorii, proszę sprawdzić ponownie!");
        }
        return "redirect:/categories";
    }



    @RequestMapping(value = "/delete-category", method = {RequestMethod.GET, RequestMethod.PUT})
    public String delete(Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Usunięto pomyślnie!");
        } catch (DataIntegrityViolationException e1) {
            e1.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Duplicate name of category, please check again!");
        } catch (Exception e2) {
            e2.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Błąd serwera");
        }
        return "redirect:/categories";
    }

    @RequestMapping(value = "/enable-category", method = {RequestMethod.PUT, RequestMethod.GET})
    public String enable(Long id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.enableById(id);
            redirectAttributes.addFlashAttribute("success", "Włączono pomyślnie");
        } catch (DataIntegrityViolationException e1) {
            e1.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Kategoria już istnieje!");
        } catch (Exception e2) {
            e2.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Błąd serwera!");
        }
        return "redirect:/categories";
    }
    @GetMapping("/export-categories")
    public String exportCategoriesToJson(RedirectAttributes redirectAttributes) {
        List<Category> categories = categoryService.findALl();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(categories);
            objectMapper.writeValue(new File("data/exportJson/categories.json"), categories);
            redirectAttributes.addFlashAttribute("success", "Kategorie wyeksportowano pomyślnie");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Błąd przy eksporcie do JSON");
        }
        // Przekierowanie do tej samej strony, na której był użytkownik
        return "redirect:/categories";
    }

    @PostMapping("/import-categories")
    public String importCategoriesFromJson(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Wybierz plik");
            return "redirect:/categories";
        }

        try {
            Path tempFile = Files.createTempFile("import", ".json");
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);
            ObjectMapper objectMapper = new ObjectMapper();
            List<Category> importedCategories = objectMapper.readValue(tempFile.toFile(), objectMapper.getTypeFactory().constructCollectionType(List.class, Category.class));
            List<Category> errorCategories = new ArrayList<>();

            for (Category importedCategory : importedCategories) {
                try {
                    if (!categoryService.existsByName(importedCategory.getName())) {
                        categoryService.save(importedCategory);
                    } else {
                        errorCategories.add(importedCategory);
                    }
                } catch (DataIntegrityViolationException e) {
                    e.printStackTrace();
                    errorCategories.add(importedCategory);
                }
            }
            if (!errorCategories.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Błąd podczas importowania niektórych kategorii.");
                redirectAttributes.addFlashAttribute("errorCategories", errorCategories);
            }else{

            redirectAttributes.addFlashAttribute("success", "Zaimportowano kategorie pomyślnie");}
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Błąd przy imporcie!");
        }

        return "redirect:/categories";
    }




}