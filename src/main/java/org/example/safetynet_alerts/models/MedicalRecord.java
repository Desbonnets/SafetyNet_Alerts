package org.example.safetynet_alerts.models;

import java.util.List;

/**
 * Represents a medical record for an individual.
 * This class contains personal information, medications, and allergies associated with a person.
 * The class includes fields for the person's first name, last name, birthdate, medications, and allergies.
 */
public class MedicalRecord {

    private String firstName; // The person's first name
    private String lastName;  // The person's last name
    private String birthdate; // The person's birthdate
    private List<String> medications; // List of medications prescribed to the person
    private List<String> allergies; // List of allergies the person has

    /**
     * Constructs a new MedicalRecord instance with the specified details.
     *
     * @param firstName The first name of the person.
     * @param lastName The last name of the person.
     * @param birthdate The birthdate of the person.
     * @param medications The list of medications the person is taking.
     * @param allergies The list of allergies the person has.
     */
    public MedicalRecord(String firstName, String lastName, String birthdate, List<String> medications, List<String> allergies) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthdate = birthdate;
        this.medications = medications;
        this.allergies = allergies;
    }

    /**
     * Returns the first name of the person.
     *
     * @return The first name of the person.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name of the person.
     *
     * @return The last name of the person.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the birthdate of the person.
     *
     * @return The birthdate of the person in string format.
     */
    public String getBirthDate() {
        return birthdate;
    }

    /**
     * Returns the list of medications the person is taking.
     *
     * @return A list of medication names.
     */
    public List<String> getMedications() {
        return medications;
    }

    /**
     * Sets the list of medications for the person.
     *
     * @param medications A list of medications to set for the person.
     */
    public void setMedications(List<String> medications) {
        this.medications = medications;
    }

    /**
     * Returns the list of allergies the person has.
     *
     * @return A list of allergies.
     */
    public List<String> getAllergies() {
        return allergies;
    }
}
