package org.example.safetynet_alerts.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

/**
 * Represents a collection of fire stations.
 * This class is used to deserialize a JSON object containing a list of fire stations.
 * It includes a list of {@link FireStation} objects.
 *
 * The class is annotated with {@link JsonIgnoreProperties} to ignore unknown properties
 * during JSON deserialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FireStationsData {

    private List<FireStation> firestations; // List of fire stations

    /**
     * Returns the list of fire stations.
     *
     * @return A list of {@link FireStation} objects representing the fire stations.
     */
    public List<FireStation> getFirestations() {
        return firestations;
    }

    /**
     * Sets the list of fire stations.
     *
     * @param firestations A list of {@link FireStation} objects to set.
     */
    public void setFirestations(List<FireStation> firestations) {
        this.firestations = firestations;
    }
}
