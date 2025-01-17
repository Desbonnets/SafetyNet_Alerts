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

/**
 * Service class responsible for handling operations related to fire stations.
 * This includes loading fire station data from a JSON file, retrieving fire stations by various criteria,
 * adding, updating, and deleting fire stations.
 */
@Service
public class FireStationService {

    private static final Logger logger = LogManager.getLogger(FireStationService.class); // Logger initialization
    private List<FireStation> fireStations;
    private final ObjectMapper objectMapper; // Injection of ObjectMapper via constructor

    /**
     * Constructor that initializes the FireStationService by injecting the ObjectMapper and loading the fire stations data.
     *
     * @param objectMapper The ObjectMapper instance injected by Spring.
     * @throws IllegalArgumentException if there is an error during the loading of fire stations data.
     */
    public FireStationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        try {
            loadFireStations();
        } catch (Exception e) {
            logger.error("Error loading JSON data: {}", e.getMessage());
            throw new IllegalArgumentException("Unable to load JSON data", e);
        }
    }

    /**
     * Loads the fire stations data from the `data.json` file.
     * The JSON data is parsed and mapped into a {@link FireStationsData} object.
     *
     * @throws IOException if there is an error reading the file or parsing the data.
     */
    private void loadFireStations() throws IOException {
        FireStationsData data = objectMapper.readValue(
                new ClassPathResource("data.json").getInputStream(),
                FireStationsData.class
        );

        fireStations = data.getFirestations();
        logger.info("Data loaded: {}", fireStations);
    }

    /**
     * Retrieves all the fire stations.
     *
     * @return a list of all {@link FireStation} objects.
     */
    public List<FireStation> getAllFireStations() {
        return fireStations;
    }

    /**
     * Retrieves the fire stations by their station number.
     *
     * @param station The fire station number.
     * @return a list of {@link FireStation} objects that match the given station number.
     */
    public List<FireStation> getFireStationByNumber(int station) {
        return fireStations.stream()
                .filter(fireStation -> fireStation.getStation() == station)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the addresses of the fire stations by their station number.
     *
     * @param station The fire station number.
     * @return a list of addresses corresponding to the given station number.
     */
    public List<String> getAddressByFireStationsNumber(int station) {
        return fireStations.stream()
                .filter(fireStation -> fireStation.getStation() == station)
                .map(FireStation::getAddress)
                .toList();
    }

    /**
     * Retrieves fire stations by their address.
     *
     * @param address The address of the fire station.
     * @return a list of {@link FireStation} objects that match the given address.
     */
    public List<FireStation> getFireStationByAddress(String address) {
        return fireStations.stream()
                .filter(fireStation -> Objects.equals(fireStation.getAddress(), address))
                .toList();
    }

    /**
     * Adds a new fire station to the list of fire stations.
     * If a fire station with the same address and station number already exists, the addition will fail.
     *
     * @param fireStation The {@link FireStation} object to be added.
     * @return true if the fire station was successfully added, false if it already exists.
     */
    public boolean addFireStation(FireStation fireStation) {
        boolean exists = fireStations.stream()
                .anyMatch(existing -> existing.getAddress().equals(fireStation.getAddress()) &&
                        existing.getStation() == fireStation.getStation());

        if (exists) {
            logger.error("A FireStation with this address and station number already exists: {}", fireStation);
            return false;
        }

        fireStations.add(fireStation);
        logger.info("FireStation added: {}", fireStation);
        return true;
    }

    /**
     * Updates an existing fire station's details based on its address and station number.
     * If the fire station is not found, an exception will be thrown.
     *
     * @param address              The address of the fire station to be updated.
     * @param station              The station number of the fire station to be updated.
     * @param updatedFireStation   The updated {@link FireStation} object.
     * @return the updated {@link FireStation} object.
     * @throws IllegalArgumentException if the fire station is not found for the given address and station number.
     */
    public FireStation updateFireStation(String address, int station, FireStation updatedFireStation) {
        for (int i = 0; i < fireStations.size(); i++) {
            if (fireStations.get(i).getAddress().equals(address) && fireStations.get(i).getStation() == station) {
                fireStations.set(i, updatedFireStation);
                logger.info("FireStation updated: {}", updatedFireStation);
                return fireStations.get(i);
            }
        }
        throw new IllegalArgumentException("FireStation not found for the address and station number: "
                + address + " " + station);
    }

    /**
     * Deletes a fire station based on its station number and address.
     *
     * @param station The station number of the fire station to be deleted.
     * @param address The address of the fire station to be deleted.
     * @return true if the fire station was successfully removed, false otherwise.
     */
    public boolean deleteFireStation(int station, String address) {
        boolean removed = fireStations.removeIf(fireStation -> fireStation.getStation() == station && fireStation.getAddress().equals(address));
        if (removed) {
            logger.info("FireStation deleted for address: {}", address + " " + station);
        } else {
            logger.warn("No FireStation found for address: {}", address + " " + station);
        }
        return removed;
    }
}
