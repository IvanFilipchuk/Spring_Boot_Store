package com.javaproj.admin.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaproj.library.dto.ProductDto;
import com.javaproj.library.model.Category;
import com.javaproj.library.model.Product;
import com.javaproj.library.service.CategoryService;
import com.javaproj.library.service.ProductService;
import com.javaproj.library.utils.ImageUpload;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.sql.Blob;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static java.lang.Double.parseDouble;

@Controller
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    private final CategoryService categoryService;


    @GetMapping("/products")
    public String products(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        List<ProductDto> products = productService.allProduct();
        model.addAttribute("products", products);
        model.addAttribute("size", products.size());
        return "products";
    }

    @GetMapping("/products/{pageNo}")
    public String allProducts(@PathVariable("pageNo") int pageNo, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        Page<ProductDto> products = productService.getAllProducts(pageNo);
        model.addAttribute("title", "Manage Products");
        model.addAttribute("size", products.getSize());
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());
        return "products";
    }

    @GetMapping("/search-products/{pageNo}")
    public String searchProduct(@PathVariable("pageNo") int pageNo,
                                @RequestParam(value = "keyword") String keyword,
                                Model model, Principal principal
    ) {
        if (principal == null) {
            return "redirect:/login";
        }
        Page<ProductDto> products = productService.searchProducts(pageNo, keyword);
        model.addAttribute("title", "Result Search Products");
        model.addAttribute("size", products.getSize());
        model.addAttribute("products", products);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", products.getTotalPages());
        return "product-result";

    }

    @GetMapping("/add-product")
    public String addProductPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("title", "Add Product");
        List<Category> categories = categoryService.findAllByActivatedTrue();
        model.addAttribute("categories", categories);
        model.addAttribute("productDto", new ProductDto());
        return "add-product";
    }

    @PostMapping("/save-product")
    public String saveProduct(@ModelAttribute("productDto") ProductDto product,
                              @RequestParam("imageProduct") MultipartFile imageProduct,
                              RedirectAttributes redirectAttributes, Principal principal) {
        try {
            if (principal == null) {
                return "redirect:/login";
            }
            productService.save(imageProduct, product);
            redirectAttributes.addFlashAttribute("success", "Add new product successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to add new product!");
        }
        return "redirect:/products/0";
    }

    @GetMapping("/update-product/{id}")
    public String updateProductForm(@PathVariable("id") Long id, Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }
        List<Category> categories = categoryService.findAllByActivatedTrue();
        ProductDto productDto = productService.getById(id);
        model.addAttribute("title", "Add Product");
        model.addAttribute("categories", categories);
        model.addAttribute("productDto", productDto);
        return "update-product";
    }

    @PostMapping("/update-product/{id}")
    public String updateProduct(@ModelAttribute("productDto") ProductDto productDto,
                                @RequestParam("imageProduct") MultipartFile imageProduct,
                                RedirectAttributes redirectAttributes, Principal principal) {
        try {
            if (principal == null) {
                return "redirect:/login";
            }
            productService.update(imageProduct, productDto);
            redirectAttributes.addFlashAttribute("success", "Update successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error server, please try again!");
        }
        return "redirect:/products/0";
    }

    @RequestMapping(value = "/enable-product", method = {RequestMethod.PUT, RequestMethod.GET})
    public String enabledProduct(Long id, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            if (principal == null) {
                return "redirect:/login";
            }
            productService.enableById(id);
            redirectAttributes.addFlashAttribute("success", "Enabled successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Enabled failed!");
        }
        return "redirect:/products/0";
    }

    @RequestMapping(value = "/delete-product", method = {RequestMethod.PUT, RequestMethod.GET})
    public String deletedProduct(Long id, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            if (principal == null) {
                return "redirect:/login";
            }
            productService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Deleted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Deleted failed!");
        }
        return "redirect:/products/0";
    }
    @GetMapping("/export-products")
    public String exportProductsToJson(RedirectAttributes redirectAttributes) {
        List<ProductDto> products = productService.allProduct();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String json = objectMapper.writeValueAsString(products);
            objectMapper.writeValue(new File("data/exportJson/products.json"), products);
            redirectAttributes.addFlashAttribute("success", "Produkty wyeksportowano pomyślnie");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Błąd przy eksporcie do JSON");
        }
        return "redirect:/products/0";
    }



    @PostMapping("/import-products")
    public String importJson(@RequestParam("file") MultipartFile file,RedirectAttributes redirectAttributes) {
        try {
            if (!file.getContentType().equals("application/json")) {
                redirectAttributes.addFlashAttribute("error", "Wybierz plik");
                return "redirect:/products/0";
            }
            byte[] fileBytes = file.getBytes();
            String jsonData = new String(fileBytes);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, List<Map<String, Object>>> jsonMap = objectMapper.readValue(jsonData, Map.class);
            List<Map<String, Object>> productsData = jsonMap.get("products");
            if (productsData != null) {
                for (Map<String, Object> productData : productsData) {
                    try {
                        String name = (String) productData.get("title");
                        String description = (String) productData.get("description");
                        int currentQuantity = (int) productData.get("stock");
                        Object priceObject = productData.get("price");
                        double costPrice;

                        if (priceObject instanceof Number) {
                            costPrice = ((Number) priceObject).doubleValue();
                        } else {
                            throw new IllegalArgumentException("Invalid numeric value: " + priceObject);
                        }

                        double salePrice = costPrice - ((double) productData.get("discountPercentage") * costPrice / 100);
                        String categoryTitle = (String) productData.get("category");
                        Category category = categoryService.findOrCreateCategory(categoryTitle);

                        ProductDto productDto = new ProductDto();
                        productDto.setName(name);
                        productDto.setDescription(description);
                        productDto.setCurrentQuantity(currentQuantity);
                        productDto.setCostPrice(costPrice);
                        productDto.setSalePrice(salePrice);
                        productDto.setCategory(category);

                        String base64Image = (String) productData.get("images");
                        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
                        MultipartFile imageProduct = new ByteArrayMultipartFile(imageBytes);
                        productService.save(imageProduct, productDto);
                        redirectAttributes.addFlashAttribute("success", "Zaimportowano produkty pomyślnie");
                    } catch (Exception e) {
                        e.printStackTrace();
                        redirectAttributes.addFlashAttribute("error", "Błąd przy imporcie!");

                    }
                }

            } else {
                redirectAttributes.addFlashAttribute("error", "Nie ma produktów");
            }
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Błąd przy imporcie!");

        }

        return "redirect:/products/0";
    }

    private class ByteArrayMultipartFile implements MultipartFile {
        private final byte[] content;
        private final String name;
        private final String originalFilename;
        private final String contentType;

        public ByteArrayMultipartFile(byte[] content) {
            this.content = content;
            this.name = "file";
            this.originalFilename = "file";
            this.contentType = "application/octet-stream";
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public String getOriginalFilename() {
            return this.originalFilename;
        }

        @Override
        public String getContentType() {
            return this.contentType;
        }

        @Override
        public boolean isEmpty() {
            return this.content == null || this.content.length == 0;
        }

        @Override
        public long getSize() {
            return this.content.length;
        }

        @Override
        public byte[] getBytes() {
            return this.content;
        }

        @Override
        public InputStream getInputStream() {
            return new ByteArrayInputStream(this.content);
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            new FileOutputStream(dest).write(this.content);
        }
    }
}


