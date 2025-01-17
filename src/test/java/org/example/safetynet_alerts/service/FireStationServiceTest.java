package org.example.safetynet_alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.safetynet_alerts.models.FireStation;
import org.example.safetynet_alerts.models.FireStationsData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test class for {@link FireStationService}.
 * This class verifies the primary functionalities of the service,
 * including the management of fire stations.
 */
class FireStationServiceTest {

    @Mock
    private ObjectMapper objectMapper; // Mocked ObjectMapper for reading JSON data

    private FireStationService fireStationService; // Instance of FireStationService under test

    private List<FireStation> mockFireStations; // Simulated list of fire stations

    /**
     * Setup before each test.
     * Initializes mocks and loads simulated data for testing.
     *
     * @throws IOException if an error occurs while loading mock data.
     */
    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Mock initial data
        mockFireStations = new ArrayList<>();
        mockFireStations.add(new FireStation("123 Main St", 1));
        mockFireStations.add(new FireStation("456 Elm St", 2));

        // Mock JSON data
        FireStationsData mockData = new FireStationsData();
        mockData.setFirestations(mockFireStations);

        // Mock ObjectMapper behavior to return simulated data
        when(objectMapper.readValue(any(InputStream.class), eq(FireStationsData.class))).thenReturn(mockData);

        // Initialize service with mocked ObjectMapper
        fireStationService = new FireStationService(objectMapper);
    }

    /**
     * Tests retrieving all fire stations.
     * Verifies that all initial stations are returned correctly.
     */
    @Test
    void getAllFireStations_shouldReturnAllStations() {
        List<FireStation> result = fireStationService.getAllFireStations();
        assertEquals(mockFireStations, result);
    }

    /**
     * Tests retrieving fire stations by station number.
     * Verifies that the stations associated with the given number are returned correctly.
     */
    @Test
    void getFireStationByNumber_shouldReturnStationsForGivenNumber() {
        List<FireStation> result = fireStationService.getFireStationByNumber(1);
        assertEquals(1, result.size());
        assertEquals("123 Main St", result.get(0).getAddress());
    }

    /**
     * Tests retrieving fire stations with an invalid number.
     * Verifies that an empty list is returned if no station matches.
     */
    @Test
    void getFireStationByNumber_shouldReturnEmptyListForInvalidNumber() {
        List<FireStation> result = fireStationService.getFireStationByNumber(99);
        assertTrue(result.isEmpty());
    }

    /**
     * Tests adding a new fire station.
     * Verifies that the station is added correctly if it does not already exist.
     */
    @Test
    void addFireStation_shouldAddNewStation() {
        FireStation newStation = new FireStation("789 Oak St", 3);
        boolean success = fireStationService.addFireStation(newStation);
        assertTrue(success);
        assertTrue(fireStationService.getAllFireStations().contains(newStation));
    }

    /**
     * Tests adding a duplicate fire station.
     * Verifies that duplicate stations cannot be added.
     */
    @Test
    void addFireStation_shouldNotAddDuplicateStation() {
        FireStation duplicateStation = new FireStation("123 Main St", 1);
        boolean success = fireStationService.addFireStation(duplicateStation);
        assertFalse(success);
    }

    /**
     * Tests updating an existing fire station.
     * Verifies that the station is updated correctly.
     */
    @Test
    void updateFireStation_shouldUpdateExistingStation() {
        FireStation updatedStation = new FireStation("123 Main St", 5);
        FireStation result = fireStationService.updateFireStation("123 Main St", 1, updatedStation);
        assertEquals(updatedStation, result);
    }

    /**
     * Tests updating a non-existent fire station.
     * Verifies that an exception is thrown if the station does not exist.
     */
    @Test
    void updateFireStation_shouldThrowExceptionIfStationNotFound() {
        FireStation updatedStation = new FireStation("Nonexistent St", 5);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fireStationService.updateFireStation("Nonexistent St", 99, updatedStation);
        });

        assertEquals("FireStation not found for the address and station number: Nonexistent St 99", exception.getMessage());
    }

    /**
     * Tests deleting an existing fire station.
     * Verifies that the station is removed correctly if it exists.
     */
    @Test
    void deleteFireStation_shouldRemoveStationIfExists() {
        boolean success = fireStationService.deleteFireStation(1, "123 Main St");
        assertTrue(success);
        assertEquals(1, fireStationService.getAllFireStations().size());
    }

    /**
     * Tests deleting a non-existent fire station.
     * Verifies that the method returns false if no matching station is found.
     */
    @Test
    void deleteFireStation_shouldReturnFalseIfStationNotFound() {
        boolean success = fireStationService.deleteFireStation(99, "Nonexistent St");
        assertFalse(success);
    }

    /**
     * Tests retrieving addresses by fire station number.
     * Verifies that the correct address is returned for a valid station number.
     */
    @Test
    void getAddressByFireStationsNumber_ShouldReturnCorrectAddressForStation() {
        List<String> address = fireStationService.getAddressByFireStationsNumber(1);
        assertEquals(1, address.size());
        assertEquals("123 Main St", address.get(0));
    }

    /**
     * Tests retrieving addresses for an invalid fire station number.
     * Verifies that an empty list is returned for a non-existent station.
     */
    @Test
    void getAddressByFireStationsNumber_ShouldReturnNullForInvalidStation() {
        List<String> address = fireStationService.getAddressByFireStationsNumber(99);
        assertTrue(address.isEmpty());
    }

    /**
     * Tests retrieving fire stations by address.
     * Verifies that the stations for the given address are returned correctly.
     */
    @Test
    void getFireStationByAddress_ShouldReturnStationsForGivenAddress() {
        List<FireStation> result = fireStationService.getFireStationByAddress("123 Main St");
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getStation());
    }

    /**
     * Tests retrieving fire stations for a non-existent address.
     * Verifies that an empty list is returned.
     */
    @Test
    void getFireStationByAddress_ShouldReturnEmptyListForInvalidAddress() {
        List<FireStation> result = fireStationService.getFireStationByAddress("Nonexistent St");
        assertTrue(result.isEmpty());
    }
}
