package com.absat.techshop.TechStore.controllers;

import com.absat.techshop.TechStore.dto.UserDTO;
import com.absat.techshop.TechStore.models.Comment;
import com.absat.techshop.TechStore.models.Product;
import com.absat.techshop.TechStore.models.Purchase;
import com.absat.techshop.TechStore.models.User;
import com.absat.techshop.TechStore.security.UserDetails;
import com.absat.techshop.TechStore.services.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    private final UserService userService;
    private final CommentService commentService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(ProductService productService, CategoryService categoryService, ModelMapper modelMapper, PurchaseService purchaseService, UserService userService, CommentService commentService, PasswordEncoder passwordEncoder) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
        this.purchaseService = purchaseService;
        this.userService = userService;
        this.commentService = commentService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = {"/", "/category/{id}"}, method = RequestMethod.GET)
    public String main(Model model, @PathVariable(name = "id") Optional<String> id,
                       @RequestParam(value = "substring", required = false) String searchSubstring) {
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
        if (searchSubstring != null) {
            productList = productService.findByName(searchSubstring);
        } else if (category_id != -1) {
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
    public String toDetails(@PathVariable("id") int id, Model model, @ModelAttribute("currentUser") UserDTO userDTO,
                            @ModelAttribute("comment") Comment comment) {

        model.addAttribute("product", productService.findOne(id).get());
        model.addAttribute("categories", categoryService.findAll());

        model.addAttribute("comments", commentService.findByProduct(productService.findOne(id).get()));


        return "user/product_details";
    }

    @PostMapping("/comment")
    public String toComment(@ModelAttribute("comment") Comment comment,
                            @RequestParam(value = "product_id", required = true) int productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        User author = userDetails.getUser();
        Product product = productService.findOne(productId).get();
        comment.setAuthor(author);
        comment.setProduct(product);
        commentService.save(comment);

        return "redirect:/products/" + productId;
    }

    @PreAuthorize("hasAnyRole('ROLE_USER','ROLE_MODER','ROLE_ADMIN')")
    @GetMapping("/profile")
    public String toProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        model.addAttribute("currentUser", userDetails.getUser());

        return "user/profile";
    }

    @PostMapping("/profile/edit")
    public String updateProfile(Model model, @RequestParam(value = "id", required = true) int id,
                                @RequestParam(value = "avatar", defaultValue = "") String avatar,
                                @RequestParam(value = "name", required = true) String name,
                                @RequestParam(value = "surname", required = true) String surname) {
        User user = userService.findOne(id).get();
        user.setAvatar(avatar);
        user.setName(name);
        user.setSurname(surname);
        model.addAttribute("currentUser", user);
//        if (bindingResult.hasErrors())
//            return "user/profile";
///////  ERROR - Need for fix
        userService.update(user);
        return "redirect:/profile";
    }

    @PostMapping("/profile/editPassword")      /// ERROR
    public String updateProfile2(Model model,
                                 @RequestParam(value = "id", required = true) int id,
                                 @RequestParam(value = "email", required = true) String email,
                                 @RequestParam(value = "past_password", defaultValue = "") String pastPassword,
                                 @RequestParam(value = "new_password", required = true) String newPassword) {
        User user = userService.findOne(id).get();
        user.setEmail(email);
        if (user.getPassword().equals(passwordEncoder.encode(pastPassword))) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }
        model.addAttribute("currentUser", user);
//        if (bindingResult.hasErrors())
//            return "user/profile";
        userService.update(user);
        return "redirect:/profile";
    }


    @GetMapping("/profile/history")
    public String toHistory(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        List<Purchase> purchases = purchaseService.findByBuyer(user);

        model.addAttribute("currentUser", user);
        model.addAttribute("purchases", purchases);

        return "user/user_purchase_history";
    }

    @GetMapping("/cart")
    public String toCart(HttpServletRequest request, Model model, @RequestParam(value = "isNotEnough", defaultValue = "false") boolean isNotEnough) {
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
        model.addAttribute("isNotEnough", isNotEnough);
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
                            HttpServletResponse response, HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        boolean countIsNotEnough = false;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(productId)) {
                    int realCount = productService.getProductCount(Integer.parseInt(productId));
                    int updatedCount = Integer.parseInt(cookie.getValue()) + 1;
                    if (updatedCount <= realCount) {
                        cookie.setValue(String.valueOf(updatedCount));
                        response.addCookie(cookie);
                    } else {
                        countIsNotEnough = true;
                    }
                    break;
                }
            }
        }
        redirectAttributes.addAttribute("isNotEnough", countIsNotEnough);
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
        Product product;
        for (int i = 0; i < productList.size(); i++) {
            product = productList.get(i);
            product.setCount(product.getCount() - counts.get(i));
            productService.update(product);
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
