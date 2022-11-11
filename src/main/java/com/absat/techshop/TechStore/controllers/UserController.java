package com.absat.techshop.TechStore.controllers;

import com.absat.techshop.TechStore.dto.UserDTO;
import com.absat.techshop.TechStore.models.Category;
import com.absat.techshop.TechStore.models.Product;
import com.absat.techshop.TechStore.models.User;
import com.absat.techshop.TechStore.security.UserDetails;
import com.absat.techshop.TechStore.services.CategoryService;
import com.absat.techshop.TechStore.services.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("")
public class UserController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;

    @Autowired
    public UserController(ProductService productService, CategoryService categoryService, ModelMapper modelMapper) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @RequestMapping(value={"/","/category/{id}"}, method=RequestMethod.GET)
    public String main(Model model,  @PathVariable(name = "id") Optional<String> id){
        int category_id=-1;
        if(id.isPresent()){
            category_id=Integer.parseInt(id.get());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails=new UserDetails(new User());
        boolean isAdmin=false;
        try{
            userDetails=(UserDetails) authentication.getPrincipal();
            if(userDetails.getUser().getRole().equals("ROLE_ADMIN") || userDetails.getUser().getRole().equals("ROLE_MODER")){
                isAdmin=true;
            }
        }
        catch (RuntimeException ex){
            System.out.println("its anonymous");
        }
        List<Product> productList;
        if(category_id!=-1){
            productList=productService.findByCategory(category_id);
        }
        else {
            productList=productService.findAll();
        }
        model.addAttribute("products",productList);
        model.addAttribute("categories",categoryService.findAll());
        model.addAttribute("currentUser",UserToUserDTO(userDetails.getUser()));
        model.addAttribute("isPrivilege",isAdmin);

        return "user/main";
    }

    @GetMapping("/products/{id}")
    public String toDetails(@PathVariable("id") int id,Model model,@ModelAttribute("currentUser") UserDTO userDTO){

        model.addAttribute("product",productService.findOne(id).get());

        return "user/product_details";
    }
    private UserDTO UserToUserDTO(User user){
        return modelMapper.map(user,UserDTO.class);
    }
}
