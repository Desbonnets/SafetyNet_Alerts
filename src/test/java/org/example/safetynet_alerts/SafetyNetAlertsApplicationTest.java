package org.example.safetynet_alerts;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

/**
 * Unit test class for {@link SafetyNetAlertsApplication}.
 * This class ensures that the application starts correctly without throwing exceptions.
 */
class SafetyNetAlertsApplicationTest {

    /**
     * Test for the {@code main} method.
     * Verifies that the application starts without exceptions by mocking the
     * {@link SpringApplication#run(Class, String...)} method.
     * <p>
     * Steps:
     * <ol>
     *     <li>Mimic the behavior of {@code SpringApplication.run()} using a mock.</li>
     *     <li>Call the {@code main} method of the {@link SafetyNetAlertsApplication} class.</li>
     *     <li>Verify that {@code SpringApplication.run()} is invoked as expected.</li>
     * </ol>
     */
    @Test
    void main_shouldStartApplicationWithoutExceptions() {
        // Simulates the execution of SpringApplication.run without throwing an exception
        try (var springApplicationMock = mockStatic(SpringApplication.class)) {
            // Mock the behavior of SpringApplication.run
            springApplicationMock.when(() ->
                    SpringApplication.run(SafetyNetAlertsApplication.class, new String[] {})
            ).thenReturn(null);

            // Call the main method
            SafetyNetAlertsApplication.main(new String[] {});

            // Verify that SpringApplication.run was invoked
            springApplicationMock.verify(() ->
                    SpringApplication.run(SafetyNetAlertsApplication.class, new String[] {})
            );
        }
    }
}
