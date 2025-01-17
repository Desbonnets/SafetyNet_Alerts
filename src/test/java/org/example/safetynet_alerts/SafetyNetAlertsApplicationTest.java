package org.example.safetynet_alerts;

import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;

import static org.mockito.Mockito.mockStatic;

class SafetyNetAlertsApplicationTest {

    @Test
    void main_shouldStartApplicationWithoutExceptions() {
        // Simule l'exécution de SpringApplication.run sans lever d'exception
        try (var springApplicationMock = mockStatic(SpringApplication.class)) {
            // Mock du comportement de SpringApplication.run
            springApplicationMock.when(() ->
                    SpringApplication.run(SafetyNetAlertsApplication.class, new String[] {})
            ).thenReturn(null);

            // Appel de la méthode main
            SafetyNetAlertsApplication.main(new String[] {});

            // Vérifie que SpringApplication.run a bien été appelé
            springApplicationMock.verify(() ->
                    SpringApplication.run(SafetyNetAlertsApplication.class, new String[] {})
            );
        }
    }
}
