package com.sharmachait.PrimaryBackend;

import com.sharmachait.PrimaryBackend.models.entity.User;
import com.sharmachait.PrimaryBackend.models.response.AuthResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PrimaryBackendApplication {

	public static void main(String[] args) {
		User res = new User();
		res.setUsername("somemessage");
		SpringApplication.run(PrimaryBackendApplication.class, args);
	}

}
