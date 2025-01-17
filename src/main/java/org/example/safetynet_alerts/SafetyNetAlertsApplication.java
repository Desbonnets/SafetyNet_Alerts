package org.example.safetynet_alerts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class of the Spring Boot application for "SafetyNet Alerts".
 * This class contains the main method that starts the Spring Boot application.
 * It is annotated with {@link SpringBootApplication} to indicate that it is a Spring Boot application.
 */
@SpringBootApplication
public class SafetyNetAlertsApplication {

    /**
     * Main method that starts the Spring Boot application.
     *
     * @param args Command-line arguments passed when running the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(SafetyNetAlertsApplication.class, args);
    }
}
