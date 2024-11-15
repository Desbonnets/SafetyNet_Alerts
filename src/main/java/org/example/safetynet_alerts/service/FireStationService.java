package org.example.safetynet_alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.example.safetynet_alerts.models.FireStation;
import org.example.safetynet_alerts.models.FireStationsData;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class FireStationService {

    private static final Logger logger = LogManager.getLogger(FireStationService.class); // Initialisation correcte du Logger
    private List<FireStation> fireStations;
    private final ObjectMapper objectMapper; // Injection de l'ObjectMapper via le constructeur

    // Injection de l'ObjectMapper par Spring
    public FireStationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper; // Assignation de l'ObjectMapper injecté
        try {
            loadFireStations();
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des données JSON : {}", e.getMessage());
            throw new IllegalArgumentException("Impossible de charger les données JSON", e);
        }
    }

    private void loadFireStations() throws IOException {
        // Lire le fichier JSON et le mapper sur la classe FireStationsData
        FireStationsData data = objectMapper.readValue(
                new ClassPathResource("data.json").getInputStream(),
                FireStationsData.class
        );

        // Extraire la liste des stations de la structure racine
        fireStations = data.getFirestations();
        logger.info("Données chargées : {}", fireStations);
    }

    public List<FireStation> getAllFireStations() {
        return fireStations;
    }

    public FireStation getFireStationByNumber(int station) {
        return fireStations.stream()
                .filter(fireStation -> fireStation.getStation() == station)
                .findFirst()
                .orElse(null);
    }
}
