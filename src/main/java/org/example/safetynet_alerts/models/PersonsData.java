package org.example.safetynet_alerts.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Represents a container for a list of persons.
 * This class is used to store a list of {@link Person} objects, typically to map data from JSON responses.
 * It is annotated with {@link JsonIgnoreProperties} to ignore any unknown properties during JSON deserialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonsData {

    private List<Person> persons; // List of persons

    /**
     * Returns the list of persons.
     *
     * @return The list of persons.
     */
    public List<Person> getPersons() {
        return persons;
    }

    /**
     * Sets the list of persons.
     *
     * @param persons The list of persons to be set.
     */
    public void setPersons(List<Person> persons) {
        this.persons = persons;
    }
}
