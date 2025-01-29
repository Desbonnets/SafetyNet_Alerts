package org.example.safetynet_alerts.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Represents the data for a collection of medical records.
 * This class encapsulates a list of {@link MedicalRecord} objects.
 * The class is annotated with {@link JsonIgnoreProperties} to ignore any unknown properties during JSON deserialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MedicalRecordData {

    private List<MedicalRecord> medicalrecords; // List of medical records

    /**
     * Returns the list of medical records.
     *
     * @return A list of {@link MedicalRecord} objects.
     */
    public List<MedicalRecord> getMedicalrecords() {
        return medicalrecords;
    }

    /**
     * Sets the list of medical records.
     *
     * @param medicalrecords A list of {@link MedicalRecord} objects to set.
     */
    public void setMedicalrecords(List<MedicalRecord> medicalrecords) {
        this.medicalrecords = medicalrecords;
    }
}
