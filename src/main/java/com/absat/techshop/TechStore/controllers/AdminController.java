package com.absat.techshop.TechStore.controllers;

import com.absat.techshop.TechStore.models.Brand;
import com.absat.techshop.TechStore.models.Category;
import com.absat.techshop.TechStore.models.Product;
import com.absat.techshop.TechStore.models.User;
import com.absat.techshop.TechStore.security.UserDetails;
import com.absat.techshop.TechStore.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ProductService productService;
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final PurchaseService purchaseService;

    @Autowired
    public AdminController(UserService userService, ProductService productService, CategoryService categoryService, BrandService brandService, PurchaseService purchaseService) {
        this.userService = userService;
        this.productService = productService;
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.purchaseService = purchaseService;
    }

    private boolean isAdmin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails=new UserDetails(new User());
        boolean isAdmin=false;
        try{
            userDetails=(UserDetails) authentication.getPrincipal();
            isAdmin=userDetails.getUser().getRole().equals("ROLE_ADMIN");
        }
        catch (RuntimeException ex){
            System.out.println("its anonymous");
        }
        return isAdmin;
    }

    @GetMapping("/purchases")
    public String toPurchases(Model model){
        model.addAttribute("purchases",purchaseService.findAll());
        model.addAttribute("isAdmin",isAdmin());
        return "admin/purchases_history";
    }
    @GetMapping("/users")
    public String toUsers(Model model, @ModelAttribute("user") User user){
        String[] s = new String[]{"ROLE_USER","ROLE_MODER","ROLE_ADMIN"};
        model.addAttribute("roless",s);
        model.addAttribute("users",userService.findAll());
        return "admin/users";
    }
    @GetMapping("/products")
    public String toProducts(Model model, @ModelAttribute("product")Product product){
        model.addAttribute("brands",brandService.findAll());
        model.addAttribute("categories",categoryService.findAll());
        model.addAttribute("products",productService.findAll());
        model.addAttribute("isAdmin",isAdmin());
        return "admin/products";
    }
    @GetMapping("/categories")
    public String toCategories(Model model, @ModelAttribute("category")Category category){
        model.addAttribute("categories",categoryService.findAll());
        model.addAttribute("isAdmin",isAdmin());
        return "admin/categories";
    }
    @GetMapping("/brands")
    public String toBrands(Model model, @ModelAttribute("brand")Brand brand){
        model.addAttribute("brands",brandService.findAll());
        model.addAttribute("isAdmin",isAdmin());
        return "admin/brands";
    }

    @PostMapping("/products")
    public String create(Model model,@ModelAttribute("product") @Valid Product product,
                         BindingResult bindingResult) {

        model.addAttribute("brands",brandService.findAll());
        model.addAttribute("categories",categoryService.findAll());
        model.addAttribute("products",productService.findAll());

        if (bindingResult.hasErrors())
            return "admin/products";

        productService.save(product);
        return "redirect:/admin/products";
    }
    @PatchMapping("/products")
    public String update(Model model,@ModelAttribute("product") @Valid Product product,
                         BindingResult bindingResult) {

        model.addAttribute("brands",brandService.findAll());
        model.addAttribute("categories",categoryService.findAll());
        model.addAttribute("products",productService.findAll());

        if (bindingResult.hasErrors())
            return "admin/products";

        productService.update(product);
        return "redirect:/admin/products";
    }
    @PostMapping("/products/{id}")
    public String delete(@PathVariable("id")int id) {
        productService.delete(id);
        return "redirect:/admin/products";
    }
    @PostMapping("/products/{id}/count")
    public String productCount(@PathVariable("id")int id,@ModelAttribute("product") Product product) {
        productService.changeCount(id,product.getCount());
        return "redirect:/admin/products";
    }

    @PostMapping("/brands")
    public String createBrand(Model model,@ModelAttribute("brand") @Valid Brand brand,
                         BindingResult bindingResult) {

        model.addAttribute("brands",brandService.findAll());

        if (bindingResult.hasErrors())
            return "admin/brands";

        brandService.save(brand);
        return "redirect:/admin/brands";
    }
    @PatchMapping("/brands")
    public String updateBrand(Model model,@ModelAttribute("brand") @Valid Brand brand,
                         BindingResult bindingResult) {

        model.addAttribute("brands",brandService.findAll());

        if (bindingResult.hasErrors())
            return "admin/brands";

        brandService.update(brand);
        return "redirect:/admin/brands";
    }
    @PostMapping("/brands/{id}")
    public String deleteBrand(@PathVariable("id")int id) {
        brandService.delete(id);
        return "redirect:/admin/brands";
    }
    @PostMapping("/categories")
    public String createCategory(Model model,@ModelAttribute("category") @Valid Category category,
                              BindingResult bindingResult) {

        model.addAttribute("categories",categoryService.findAll());

        if (bindingResult.hasErrors())
            return "admin/categories";

        categoryService.save(category);
        return "redirect:/admin/categories";
    }
    @PatchMapping("/categories")
    public String updateCategory(Model model,@ModelAttribute("category") @Valid Category category,
                              BindingResult bindingResult) {

        model.addAttribute("categories",categoryService.findAll());

        if (bindingResult.hasErrors())
            return "admin/categories";

        categoryService.update(category);
        return "redirect:/admin/categories";
    }
    @PostMapping("/categories/{id}")
    public String deleteCategory(@PathVariable("id")int id) {
        categoryService.delete(id);
        return "redirect:/admin/categories";
    }



    @PostMapping("/users")
    public String createUser(Model model,@ModelAttribute("user") @Valid User user,
                                 BindingResult bindingResult) {

        model.addAttribute("users",userService.findAll());

        if (bindingResult.hasErrors())
            return "admin/users";

        userService.save(user);
        return "redirect:/admin/users";
    }
    @PatchMapping("/users")
    public String updateUser(Model model,@ModelAttribute("user") @Valid User user,
                                 BindingResult bindingResult) {

        model.addAttribute("users",userService.findAll());

        if (bindingResult.hasErrors())
            return "admin/users";

        userService.update(user);
        return "redirect:/admin/users";
    }
    @PostMapping("/users/{id}")
    public String deleteUser(@PathVariable("id")int id) {
        userService.delete(id);
        return "redirect:/admin/users";
    }




}
