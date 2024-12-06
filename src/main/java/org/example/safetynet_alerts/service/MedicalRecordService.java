package org.example.safetynet_alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.safetynet_alerts.models.FireStation;
import org.example.safetynet_alerts.models.FireStationsData;
import org.example.safetynet_alerts.models.MedicalRecord;
import org.example.safetynet_alerts.models.MedicalRecordData;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class MedicalRecordService {

    private static final Logger logger = LogManager.getLogger(MedicalRecordService.class); // Initialisation correcte du Logger
    private List<MedicalRecord> medicalRecordList;
    private final ObjectMapper objectMapper; // Injection de l'ObjectMapper via le constructeur

    // Injection de l'ObjectMapper par Spring
    public MedicalRecordService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper; // Assignation de l'ObjectMapper injecté
        try {
            loadMedicalRecordList();
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des données JSON : {}", e.getMessage());
            throw new IllegalArgumentException("Impossible de charger les données JSON", e);
        }
    }

    private void loadMedicalRecordList() throws IOException {
        // Lire le fichier JSON et le mapper sur la classe MedicalRecordData
        MedicalRecordData data = objectMapper.readValue(
                new ClassPathResource("data.json").getInputStream(),
                MedicalRecordData.class
        );

        // Extraire la liste des stations de la structure racine
        medicalRecordList = data.getMedicalRecordList();
        logger.info("Données chargées : {}", medicalRecordList.size());
    }

    public List<MedicalRecord> getAllMedicalRecordList() {
        return medicalRecordList;
    }

    public List<MedicalRecord> getMedicalRecordByFirstnameAndLastname(String firstname, String lastname) {
        return medicalRecordList.stream()
                .filter(fireStation -> Objects.equals(fireStation.getFirstName(), firstname) &&
                        Objects.equals(fireStation.getLastName(), lastname)
                )
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean addMedicalRecord(MedicalRecord medicalRecord) {
        boolean exists = medicalRecordList.stream()
                .anyMatch(existing -> existing.getFirstName().equals(medicalRecord.getFirstName()) &&
                        Objects.equals(existing.getLastName(), medicalRecord.getLastName()));

        if (exists) {
            logger.error("Une MedicalRecord avec ce nom et prénom existe déjà : {}", medicalRecord);
            return false;
        }

        medicalRecordList.add(medicalRecord);
        logger.info("MedicalRecord ajoutée : {}", medicalRecord);
        return true;
    }

    public MedicalRecord updateMedicalRecord(String firstname, String lastname, MedicalRecord updatedMedicalRecord) {
        for (int i = 0; i < medicalRecordList.size(); i++) {
            if (medicalRecordList.get(i).getFirstName().equals(firstname) &&
                    Objects.equals(medicalRecordList.get(i).getLastName(), lastname)) {
                medicalRecordList.set(i, updatedMedicalRecord);
                logger.info("MedicalRecord modifier : {}", updatedMedicalRecord);
                return medicalRecordList.get(i);
            }
        }
        throw new IllegalArgumentException("MedicalRecord non trouvée pour le nom et prénom : "
                + firstname + " " + lastname);
    }

    public boolean deleteMedicalRecord(String firstname, String lastname) {
        boolean removed = medicalRecordList.removeIf(medicalRecord -> Objects.equals(medicalRecord.getFirstName(), firstname) &&
                medicalRecord.getLastName().equals(lastname));
        if (removed) {
            logger.info("MedicalRecord supprimée pour le nom : {}", firstname + " " + lastname);
        } else {
            logger.warn("Aucune MedicalRecord trouvée pour le nom : {}", firstname + " " + lastname);
        }
        return removed;
    }
}
