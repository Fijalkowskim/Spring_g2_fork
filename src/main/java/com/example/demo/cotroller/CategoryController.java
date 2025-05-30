package com.example.demo.cotroller;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.exceptions.CategoryArleadyExistException;
import com.example.demo.model.Category;
import com.example.demo.services.CategoryService;
import com.example.demo.services.impl.CategoryServiceImpl;
import com.example.demo.services.impl.ProductServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
class CategoryController {
    final CategoryServiceImpl categoryService;
    final ProductServiceImpl productService;

    @GetMapping("/categories")
    public String showAllCategories(Model model, @Param("keyword") String keyword){
return showAllCategoriesByPageNumber(model, keyword, 1);
    }

    @GetMapping("/categories/{pageNumber}")
    public String showAllCategoriesByPageNumber(Model model, @Param("keyword") String keyword, @PathVariable int pageNumber ) {

        Page<Category> categoryPage=categoryService.showAllCategories(keyword,pageNumber);
        int totalPages= categoryPage.getTotalPages();
        model.addAttribute("title", "List of Categories");
        model.addAttribute("categories", categoryService.showAllCategories(keyword,pageNumber).getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("searchAction", "/categories");
        model.addAttribute("action", "/saveCategory");
        if(totalPages>0){
            model.addAttribute("pages", totalPages);
        }
        else{
            model.addAttribute("pages", 1);
        }
        model.addAttribute("items", categoryPage.getTotalElements());
        model.addAttribute("currentPage", pageNumber);
        return "/categories/categories";
    }

    @GetMapping("/categoryDetails/{id}")
    public String showCategoryDetails(@PathVariable Long id, Model model) {
        var optionalCategory = categoryService.findByCategoryId(id);
        if (optionalCategory.isEmpty()) {
            model.addAttribute("error", "Current category witj id=" + id + " doesn't exist");
            model.addAttribute("errorAction", "/categories");
            model.addAttribute("return", "Return to list of categories");
            return "/error-page";
        }
        model.addAttribute("category", categoryService.findByCategoryId(id).get());
        model.addAttribute("products", productService.findProductByCategoryId(id));
        return "/categories/category";
    }
    @Secured({"ROLE_MANAGER","ROLE_ADMIN"})
        @PostMapping("/saveCategory")
    public String saveCategory(@Valid Category category,
            BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            errorCatch(bindingResult, category, redirectAttributes);
        } else {
            try {
                categoryService.insertCategory(category);
            } catch (CategoryArleadyExistException e) {
                redirectAttributes.addFlashAttribute("error", "Category arleady exists");
                redirectAttributes.addFlashAttribute("category", category);

            }
        }
        return "redirect:/categories";

    }
    @Secured({"ROLE_MANAGER","ROLE_ADMIN"})
        @GetMapping("removeCategory/{id}")
    public String removeCategory(@PathVariable Long id) {
        var products = productService.findProductByCategoryId(id);
        products.forEach(product -> {
            product.setCategory(null);
        });
        categoryService.removeCategoryById(id);
        return "redirect:/categories";
    }
    @Secured({"ROLE_MANAGER","ROLE_ADMIN"})
        @GetMapping("/editCategory/{id}")
    public String editCategory(@PathVariable Long id, Model model) {
        var optionalCategory = categoryService.findByCategoryId(id);
        if (optionalCategory.isEmpty()) {
            model.addAttribute("error", "Edit of category with id=" + id + " is NOT posible");
            model.addAttribute("errorAction", "/categories");
            model.addAttribute("return", "Return to list of categories");
            return "/error-page";
        }
        model.addAttribute("action", "/editedCategory/" + id);
        model.addAttribute("category", optionalCategory.get());
        return "/categories/edit-category";
    }
    @Secured({"ROLE_MANAGER","ROLE_ADMIN"})
        @PostMapping("/editedCategory/{id}")
    public String saveEditedCategory(@PathVariable Long id, @Valid Category category, BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            errorCatch(bindingResult, category, redirectAttributes);
            return "redirect:/editCategory/" + id;
        }
        categoryService.updateCurrentCategory(category, id);
        var url = "redirect:/categoryDetails/" + id;
        return url;
    }

    private void errorCatch(BindingResult bindingResult, Category category, RedirectAttributes redirectAttributes) {
        var errors = bindingResult.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        redirectAttributes.addFlashAttribute("errors", errors);
        redirectAttributes.addFlashAttribute("category", category);
    }

}
