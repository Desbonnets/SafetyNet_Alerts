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
import java.util.Objects;
import java.util.stream.Collectors;

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

    public List<FireStation> getFireStationByNumber(int station) {
        return fireStations.stream()
                .filter(fireStation -> fireStation.getStation() == station)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<String> getAddressByFireStationsNumber(int station) {

        return fireStations.stream()
                .filter(fireStation -> fireStation.getStation() == station)
                .map(FireStation::getAddress)
                .collect(Collectors.toList());
    }

    public List<FireStation> getFireStationByAddress(String address) {

        return fireStations.stream()
                .filter(fireStation -> Objects.equals(fireStation.getAddress(), address))
                .collect(Collectors.toList());
    }

    public boolean addFireStation(FireStation fireStation) {
        boolean exists = fireStations.stream()
                .anyMatch(existing -> existing.getAddress().equals(fireStation.getAddress()) &&
                        existing.getStation() == fireStation.getStation());

        if (exists) {
            logger.error("Une FireStation avec cette adresse et numéro de station existe déjà : {}", fireStation);
            return false;
        }

        fireStations.add(fireStation);
        logger.info("FireStation ajoutée : {}", fireStation);
        return true;
    }

    public FireStation updateFireStation(String address, int station, FireStation updatedFireStation) {
        for (int i = 0; i < fireStations.size(); i++) {
            if (fireStations.get(i).getAddress().equals(address) && fireStations.get(i).getStation() == station) {
                fireStations.set(i, updatedFireStation);
                logger.info("FireStation modifier : {}", updatedFireStation);
                return fireStations.get(i);
            }
        }
        throw new IllegalArgumentException("FireStation non trouvée pour l'adresse et numéro de station : "
                + address + " " + station);
    }

    public boolean deleteFireStation(int station, String address) {
        boolean removed = fireStations.removeIf(fireStation -> fireStation.getStation() == station && fireStation.getAddress().equals(address));
        if (removed) {
            logger.info("FireStation supprimée pour l'adresse : {}", address + " " + station);
        } else {
            logger.warn("Aucune FireStation trouvée pour l'adresse : {}", address + " " + station);
        }
        return removed;
    }
}
