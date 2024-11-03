package com.task.api_dev_task;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.task.api_dev_task.entity.User;
import com.task.api_dev_task.repository.UserRepository;

@SpringBootApplication
//@OpenAPIDefinition(
//	info = @Info(
//		title = "API Dev Task",
//		version = "1.0",
//		description = "API Documentation"
//	)
//)
public class ApiDevTaskApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiDevTaskApplication.class, args);
	}

	@Bean
	CommandLineRunner commandLineRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// Create admin user
			User admin = new User();
			admin.setUsername("admin");
			admin.setPassword(passwordEncoder.encode("admin123"));
			
			// Create regular user
			User user = new User();
			user.setUsername("user");
			user.setPassword(passwordEncoder.encode("user123"));

			// Save users to database
			userRepository.save(admin);
			userRepository.save(user);
		};
	}
}
