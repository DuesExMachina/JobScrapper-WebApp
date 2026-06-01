package com.JobScrapper.OrchestratorService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class OrchestratorServiceApplication {

	public static void main(String[] args) {

		System.out.println("Current Directory: " + System.getProperty("user.dir"));
		Dotenv dotenv = Dotenv.configure()
				.directory("./backend/OrchestratorService/")
				.ignoreIfMissing()
				.load();
		// dotenv.entries().forEach(e -> System.setProperty(e.getKey(), e.getValue()));
		System.out.println("--- Loading Environment Variables ---");
		dotenv.entries().forEach(e -> {
			// Print the key so you know it's found
			System.out.println("Loaded Key: " + e.getKey());

			// Set the property for Spring to use
			System.setProperty(e.getKey(), e.getValue());
		});
		System.out.println("--- End of Env Load ---");
		SpringApplication.run(OrchestratorServiceApplication.class, args);
	}

}
