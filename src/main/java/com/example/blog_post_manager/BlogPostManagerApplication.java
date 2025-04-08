package com.example.blog_post_manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlogPostManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogPostManagerApplication.class, args);
    }

//    @Component
//    class Setup implements CommandLineRunner {
//
//        private final RoleRepository roleRepository;
//        private final UserManagementService userManagementService;
//
//        Setup(RoleRepository roleRepository, UserManagementService userManagementService) {
//            this.roleRepository = roleRepository;
//            this.userManagementService = userManagementService;
//        }
//
//        @Override
//        public void run(String... args) throws Exception {
//            if (!roleRepository.existsByName(UserRole.USER)) {
//                roleRepository.save(new Role(UserRole.USER));
//            }
//            if (!roleRepository.existsByName(UserRole.ADMIN)) {
//                roleRepository.save(new Role(UserRole.ADMIN));
//            }
//
//            userManagementService.createUser("admin", "password");
//            userManagementService.addRoleToUser("admin", UserRole.ADMIN);
//        }
//    }
}
