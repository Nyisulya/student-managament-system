package com.studentmanagement.studentmanagement.config;

import com.studentmanagement.studentmanagement.user.Role;
import com.studentmanagement.studentmanagement.user.User;
import com.studentmanagement.studentmanagement.user.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeUsers(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Create ADMIN user
            if (!userRepository.existsByUsername("admin")) {

                User admin = new User();

                admin.setUsername("admin");
                admin.setEmail("admin@student.com");
                admin.setPassword(
                        passwordEncoder.encode("Admin123")
                );
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
            }

            // Create TEACHER user
            if (!userRepository.existsByUsername("teacher")) {

                User teacher = new User();

                teacher.setUsername("teacher");
                teacher.setEmail("teacher@student.com");
                teacher.setPassword(
                        passwordEncoder.encode("Teacher123")
                );
                teacher.setRole(Role.TEACHER);

                userRepository.save(teacher);
            }

            // Create STUDENT user
            if (!userRepository.existsByUsername("student")) {

                User student = new User();

                student.setUsername("student");
                student.setEmail("student@student.com");
                student.setPassword(
                        passwordEncoder.encode("Student123")
                );
                student.setRole(Role.STUDENT);

                userRepository.save(student);
            }
        };
    }
}