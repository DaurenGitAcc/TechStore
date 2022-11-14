package com.absat.techshop.TechStore.controllers;

import com.absat.techshop.TechStore.dto.UserDTO;
import com.absat.techshop.TechStore.models.Product;
import com.absat.techshop.TechStore.models.User;
import com.absat.techshop.TechStore.security.UserDetails;
import com.absat.techshop.TechStore.services.CategoryService;
import com.absat.techshop.TechStore.services.ProductService;
import com.absat.techshop.TechStore.services.PurchaseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("")
public class UserController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ModelMapper modelMapper;
    private final PurchaseService purchaseService;

    @Autowired
    public UserController(ProductService productService, CategoryService categoryService, ModelMapper modelMapper, PurchaseService purchaseService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
        this.purchaseService = purchaseService;
    }

    @RequestMapping(value = {"/", "/category/{id}"}, method = RequestMethod.GET)
    public String main(Model model, @PathVariable(name = "id") Optional<String> id) {
        int category_id = -1;
        if (id.isPresent()) {
            category_id = Integer.parseInt(id.get());
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = new UserDetails(new User());
        boolean isAdmin = false;
        try {
            userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails.getUser().getRole().equals("ROLE_ADMIN") || userDetails.getUser().getRole().equals("ROLE_MODER")) {
                isAdmin = true;
            }
        } catch (RuntimeException ex) {
            System.out.println("its anonymous");
        }
        List<Product> productList;
        if (category_id != -1) {
            productList = productService.findByCategory(category_id);
        } else {
            productList = productService.findAll();
        }
        model.addAttribute("products", productList);
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("currentUser", UserToUserDTO(userDetails.getUser()));
        model.addAttribute("isPrivilege", isAdmin);

        return "user/main";
    }

    @GetMapping("/products/{id}")
    public String toDetails(@PathVariable("id") int id, Model model, @ModelAttribute("currentUser") UserDTO userDTO) {

        model.addAttribute("product", productService.findOne(id).get());

        return "user/product_details";
    }

    @GetMapping("/cart")
    public String toCart(HttpServletRequest request, Model model) {
        Cookie[] cookies = request.getCookies();
        List<Product> productList = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        for (Cookie cookie : cookies) {
            if (isNumeric(cookie.getName())) {
                Optional<Product> product = productService.findOne(Integer.parseInt(cookie.getName()));
                if (product.isPresent()) {
                    productList.add(product.get());
                    counts.add(Integer.parseInt(cookie.getValue()));
                }
            }
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = new UserDetails(new User());
        boolean isAdmin = false;
        try {
            userDetails = (UserDetails) authentication.getPrincipal();
            if (userDetails.getUser().getRole().equals("ROLE_ADMIN") || userDetails.getUser().getRole().equals("ROLE_MODER")) {
                isAdmin = true;
            }
        } catch (RuntimeException ex) {
            System.out.println("its anonymous");
        }
        int total = 0;
        for (int i = 0; i < productList.size(); i++) {
            total += productList.get(i).getPrice() * counts.get(i);
        }

        model.addAttribute("products", productList);
        model.addAttribute("counts", counts);
        model.addAttribute("isPrivilege", isAdmin);
        model.addAttribute("currentUser", UserToUserDTO(userDetails.getUser()));
        model.addAttribute("totalPrice", total);
        return "user/cart";
    }

    @PostMapping("/cart/minus")
    public String minusCount(@RequestParam(name = "product_id") String productId,
                             HttpServletResponse response, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(productId)) {
                    int count = Integer.parseInt(cookie.getValue()) - 1;
                    if (count <= 0) {
                        cookie.setMaxAge(0);
                    } else {
                        cookie.setValue(String.valueOf(count));
                    }
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/clear")
    public String clearCount(HttpServletResponse response, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (isNumeric(cookie.getName())) {
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/plus")
    public String plusCount(@RequestParam(name = "product_id") String productId,
                            HttpServletResponse response, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(productId)) {
                    cookie.setValue(String.valueOf(Integer.parseInt(cookie.getValue()) + 1));
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/purchase")
    public String Purchase(HttpServletRequest request, HttpServletResponse response, Model model) {
        Cookie[] cookies = request.getCookies();
        List<Product> productList = new ArrayList<>();
        List<Integer> counts = new ArrayList<>();
        for (Cookie cookie : cookies) {
            if (isNumeric(cookie.getName())) {
                Optional<Product> product = productService.findOne(Integer.parseInt(cookie.getName()));
                if (product.isPresent()) {
                    productList.add(product.get());
                    counts.add(Integer.parseInt(cookie.getValue()));
                }
            }
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = new UserDetails(new User());
        boolean isAdmin = false;
        User currentUser = new User();
        try {
            userDetails = (UserDetails) authentication.getPrincipal();
            currentUser = userDetails.getUser();
        } catch (RuntimeException ex) {
            System.out.println("its anonymous");
        }

        for (int i = 0; i < productList.size(); i++) {
            purchaseService.save(currentUser, productList.get(i), counts.get(i));
        }

        return clearCount(response, request);
    }

    private static boolean isNumeric(String str) {
        ParsePosition pos = new ParsePosition(0);
        NumberFormat.getInstance().parse(str, pos);
        return str.length() == pos.getIndex();
    }

    @PostMapping("/cart")
    public String addCart(@RequestParam(name = "product_id") String productId,
                          HttpServletResponse response, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(productId)) {
                    return "redirect:/cart";
                }
            }
        }
        Cookie cookie = new Cookie(productId, "1");
        cookie.setPath("/cart");
        response.addCookie(cookie);
        return "redirect:/cart";
    }

    private UserDTO UserToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }
}
