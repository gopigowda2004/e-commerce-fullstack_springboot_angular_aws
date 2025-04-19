package com.example.ecommercebackend;

import com.example.ecommercebackend.config.AppConstants;
import com.example.ecommercebackend.model.*;
import com.example.ecommercebackend.repository.CategoryRepository;
import com.example.ecommercebackend.repository.ProductRepository;
import com.example.ecommercebackend.repository.RoleRepository;
import com.example.ecommercebackend.repository.UserRepository;
import com.example.ecommercebackend.service.CartService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@SpringBootApplication
public class ECommerceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(ECommerceBackendApplication.class, args);


        /*byte[] keyBytes = new byte[32]; // 256 bits = 32 bytes
        new SecureRandom().nextBytes(keyBytes);
        String base64Key = Base64.getEncoder().encodeToString(keyBytes);
        System.out.println("Base64 Encoded Key: " + base64Key);

        byte[] decodedKey = Base64.getDecoder().decode(base64Key);
        SecretKey key = Keys.hmacShaKeyFor(decodedKey);
        System.out.println("Base64 Decoded Key: " + base64Key);*/

    }

    @Bean
    public CommandLineRunner initData(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            CartService cartService,
            ProductRepository productRepository,
            CategoryRepository categoryRepository
    ) {
        return args -> {
            // Retrieve or create roles
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> {
                        Role newUserRole = new Role(AppRole.ROLE_USER);
                        return roleRepository.save(newUserRole);
                    });

            Role sellerRole = roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                    .orElseGet(() -> {
                        Role newSellerRole = new Role(AppRole.ROLE_SELLER);
                        return roleRepository.save(newSellerRole);
                    });

            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> {
                        Role newAdminRole = new Role(AppRole.ROLE_ADMIN);
                        return roleRepository.save(newAdminRole);
                    });

            Set<Role> userRoles = Set.of(userRole);
            Set<Role> sellerRoles = Set.of(sellerRole);
            Set<Role> adminRoles = Set.of(userRole, sellerRole, adminRole);


            // Create users if not already present
            if (!userRepository.existsByUserName("user1")) {
                User user1 = new User("user1", "user1@example.com", passwordEncoder.encode("password1"));
                userRepository.save(user1);
            }

            if (!userRepository.existsByUserName("seller1")) {
                User seller1 = new User("seller1", "seller1@example.com", passwordEncoder.encode("password2"));
                userRepository.save(seller1);
            }

            if (!userRepository.existsByUserName("admin")) {
                User admin = new User("admin", "admin@example.com", passwordEncoder.encode("adminPass"));
                userRepository.save(admin);
            }

            // Update roles for existing users
            userRepository.findByUserName("user1").ifPresent(user -> {
                user.setRoles(userRoles);
                userRepository.save(user);
            });

            userRepository.findByUserName("seller1").ifPresent(seller -> {
                seller.setRoles(sellerRoles);
                userRepository.save(seller);
            });

            userRepository.findByUserName("admin").ifPresent(admin -> {
                admin.setRoles(adminRoles);
                userRepository.save(admin);
            });

            //
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            Authentication auth = new UsernamePasswordAuthenticationToken("user1", "password1", List.of(new SimpleGrantedAuthority(AppConstants.ADMIN)));
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            // Create categories
            Category electronics = new Category();
            electronics.setCategoryName("Electronics");

            Category fashion = new Category();
            fashion.setCategoryName("Fashion");

            // Persist categories (make sure you have CategoryRepository injected)
            electronics = categoryRepository.save(electronics);
            fashion = categoryRepository.save(fashion);

            // Create products
            Product mobile = new Product();
            mobile.setProductName("Smartphone");
            mobile.setProductImage("mobile.jpg");
            mobile.setCategory(electronics);
            mobile.setPrice(15000.0);
            mobile.setQuantity(10);

            Product tshirt = new Product();
            tshirt.setProductName("T-Shirt");
            tshirt.setProductImage("tshirt.jpg");
            tshirt.setCategory(fashion);
            tshirt.setPrice(500.0);
            tshirt.setQuantity(50);

            double mobilePriceToUse = Optional.ofNullable(mobile.getSpecialPrice())
                    .orElse(mobile.getPrice()) // or 0.0
                    .doubleValue();

            double tshirtPriceToUse = Optional.ofNullable(tshirt.getSpecialPrice())
                    .orElse(tshirt.getPrice()) // or 0.0
                    .doubleValue();

            mobile.setSpecialPrice(mobilePriceToUse);
            tshirt.setSpecialPrice(tshirtPriceToUse);


            // Persist products (make sure you have ProductRepository injected)
            mobile = productRepository.save(mobile);
            tshirt = productRepository.save(tshirt);

            // Add products to cart with quantities
            cartService.addProductToCart(mobile.getProductId(), 2);
            cartService.addProductToCart(tshirt.getProductId(), 3);
        };
    }

}
