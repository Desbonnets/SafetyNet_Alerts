package org.example.safetynet_alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.safetynet_alerts.models.MedicalRecord;
import org.example.safetynet_alerts.models.MedicalRecordData;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Service class responsible for handling operations related to medical records.
 * This includes loading medical records data from a JSON file, retrieving medical records by first and last names,
 * adding, updating, and deleting medical records.
 */
@Service
public class MedicalRecordService {

    private static final Logger logger = LogManager.getLogger(MedicalRecordService.class); // Logger initialization
    private List<MedicalRecord> medicalRecordList;
    private final ObjectMapper objectMapper; // Injection of ObjectMapper via constructor

    /**
     * Constructor that initializes the MedicalRecordService by injecting the ObjectMapper and loading the medical records data.
     *
     * @param objectMapper The ObjectMapper instance injected by Spring.
     * @throws IllegalArgumentException if there is an error during the loading of medical records data.
     */
    public MedicalRecordService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        try {
            loadMedicalRecordList();
        } catch (Exception e) {
            logger.error("Error loading JSON data: {}", e.getMessage());
            throw new IllegalArgumentException("Unable to load JSON data", e);
        }
    }

    /**
     * Loads the medical records data from the `data.json` file.
     * The JSON data is parsed and mapped into a {@link MedicalRecordData} object.
     *
     * @throws IOException if there is an error reading the file or parsing the data.
     */
    private void loadMedicalRecordList() throws IOException {
        MedicalRecordData data = objectMapper.readValue(
                new ClassPathResource("data.json").getInputStream(),
                MedicalRecordData.class
        );

        medicalRecordList = data.getMedicalrecords();
        logger.info("Data loaded: {}", medicalRecordList.size());
    }

    /**
     * Retrieves all the medical records.
     *
     * @return a list of all {@link MedicalRecord} objects.
     */
    public List<MedicalRecord> getAllMedicalRecordList() {
        return medicalRecordList;
    }

    /**
     * Retrieves a medical record by the first and last name.
     *
     * @param firstname The first name of the person.
     * @param lastname  The last name of the person.
     * @return the {@link MedicalRecord} object corresponding to the given first and last name,
     *         or null if no record is found.
     */
    public MedicalRecord getMedicalRecordByFirstnameAndLastname(String firstname, String lastname) {
        return medicalRecordList.stream()
                .filter(fireStation -> Objects.equals(fireStation.getFirstName(), firstname) &&
                        Objects.equals(fireStation.getLastName(), lastname)
                )
                .findAny()
                .orElse(null);
    }

    /**
     * Adds a new medical record to the list of medical records.
     * If a record with the same first and last name already exists, the addition will fail.
     *
     * @param medicalRecord The {@link MedicalRecord} object to be added.
     * @return true if the medical record was successfully added, false if it already exists.
     */
    public boolean addMedicalRecord(MedicalRecord medicalRecord) {
        boolean exists = medicalRecordList.stream()
                .anyMatch(existing -> existing.getFirstName().equals(medicalRecord.getFirstName()) &&
                        Objects.equals(existing.getLastName(), medicalRecord.getLastName()));

        if (exists) {
            logger.error("A MedicalRecord with this first and last name already exists: {}", medicalRecord);
            return false;
        }

        medicalRecordList.add(medicalRecord);
        logger.info("MedicalRecord added: {}", medicalRecord);
        return true;
    }

    /**
     * Updates an existing medical record based on the first and last name.
     * If the medical record is not found, an exception will be thrown.
     *
     * @param firstname            The first name of the person.
     * @param lastname             The last name of the person.
     * @param updatedMedicalRecord The updated {@link MedicalRecord} object.
     * @return the updated {@link MedicalRecord} object.
     * @throws IllegalArgumentException if the medical record is not found for the given first and last name.
     */
    public MedicalRecord updateMedicalRecord(String firstname, String lastname, MedicalRecord updatedMedicalRecord) {
        for (int i = 0; i < medicalRecordList.size(); i++) {
            if (medicalRecordList.get(i).getFirstName().equals(firstname) &&
                    Objects.equals(medicalRecordList.get(i).getLastName(), lastname)) {
                medicalRecordList.set(i, updatedMedicalRecord);
                logger.info("MedicalRecord updated: {}", updatedMedicalRecord);
                return medicalRecordList.get(i);
            }
        }
        throw new IllegalArgumentException("MedicalRecord not found for the first and last name: "
                + firstname + " " + lastname);
    }

    /**
     * Deletes a medical record based on the first and last name.
     *
     * @param firstname The first name of the person.
     * @param lastname  The last name of the person.
     * @return true if the medical record was successfully removed, false otherwise.
     */
    public boolean deleteMedicalRecord(String firstname, String lastname) {
        boolean removed = medicalRecordList.removeIf(medicalRecord -> Objects.equals(medicalRecord.getFirstName(), firstname) &&
                medicalRecord.getLastName().equals(lastname));
        if (removed) {
            logger.info("MedicalRecord deleted for the name: {}", firstname + " " + lastname);
        } else {
            logger.warn("No MedicalRecord found for the name: {}", firstname + " " + lastname);
        }
        return removed;
    }
}
