package org.example.safetynet_alerts.controllers;

import org.example.safetynet_alerts.models.FireStation;
import org.example.safetynet_alerts.service.FireStationService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Test class for the {@link FireStationController}.
 * This class verifies the main functionalities of the API endpoints for managing fire stations.
 */
class FireStationControllerTest {

    @Mock
    private FireStationService fireStationService; // Mock of the FireStationService

    @InjectMocks
    private FireStationController fireStationController; // Controller to test

    /**
     * Default constructor.
     * Initializes the necessary mocks for the tests.
     */
    public FireStationControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests retrieving information of a found station.
     * Verifies that the HTTP status and the returned data are correct.
     */
    @Test
    void getFirestationInfo_found() {
        int stationNumber = 1;
        List<FireStation> mockStations = List.of(new FireStation("123 Main St", 1));
        when(fireStationService.getFireStationByNumber(stationNumber)).thenReturn(mockStations);

        ResponseEntity<List<FireStation>> response = fireStationController.getFirestationInfo(stationNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockStations, response.getBody());
        verify(fireStationService, times(1)).getFireStationByNumber(stationNumber);
    }

    /**
     * Tests retrieving information of a station not found.
     * Verifies that the HTTP status is 404 and that the response body is null.
     */
    @Test
    void getFirestationInfo_notFound() {
        int stationNumber = 1;
        when(fireStationService.getFireStationByNumber(stationNumber)).thenReturn(Collections.emptyList());

        ResponseEntity<List<FireStation>> response = fireStationController.getFirestationInfo(stationNumber);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
        verify(fireStationService, times(1)).getFireStationByNumber(stationNumber);
    }

    /**
     * Tests successfully adding a fire station.
     * Verifies that the HTTP status is 201 and that the station is correctly added.
     */
    @Test
    void postFirestation_success() {
        FireStation newStation = new FireStation("123 Main St", 1);
        when(fireStationService.addFireStation(newStation)).thenReturn(true);

        ResponseEntity<FireStation> response = fireStationController.postFirestation(newStation);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newStation, response.getBody());
        verify(fireStationService, times(1)).addFireStation(newStation);
    }

    /**
     * Tests the failure of adding a fire station.
     * Verifies that an exception with a 400 status is thrown in case of failure.
     */
    @Test
    void postFirestation_failure() {
        FireStation newStation = new FireStation("123 Main St", 1);
        when(fireStationService.addFireStation(newStation)).thenReturn(false);

        Exception exception = null;
        try {
            fireStationController.postFirestation(newStation);
        } catch (Exception ex) {
            exception = ex;
        }

        assertEquals("400 BAD_REQUEST \"Registration error\"", exception.getMessage());
        verify(fireStationService, times(1)).addFireStation(newStation);
    }

    /**
     * Tests the successful update of a fire station.
     * Verifies that the HTTP status is 200 and that the updated data is correct.
     */
    @Test
    void putFirestationInfo_success() {
        String address = "123 Main St";
        int station = 1;
        FireStation updatedStation = new FireStation("123 Main St", 2);
        when(fireStationService.updateFireStation(address, station, updatedStation)).thenReturn(updatedStation);

        ResponseEntity<FireStation> response = fireStationController.putFirestationInfo(address, station, updatedStation);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedStation, response.getBody());
        verify(fireStationService, times(1)).updateFireStation(address, station, updatedStation);
    }

    /**
     * Tests the failure of updating a fire station.
     * Verifies that an exception with a 404 status is thrown if the station is not found.
     */
    @Test
    void putFirestationInfo_notFound() {
        String address = "123 Main St";
        int station = 1;
        FireStation updatedStation = new FireStation("123 Main St", 2);
        when(fireStationService.updateFireStation(address, station, updatedStation)).thenReturn(null);

        Exception exception = null;
        try {
            fireStationController.putFirestationInfo(address, station, updatedStation);
        } catch (Exception ex) {
            exception = ex;
        }

        assertEquals("404 NOT_FOUND \"Station not found for update\"", exception.getMessage());
        verify(fireStationService, times(1)).updateFireStation(address, station, updatedStation);
    }

    /**
     * Tests the successful deletion of a fire station.
     * Verifies that the HTTP status is 200 on successful deletion.
     */
    @Test
    void deleteFirestationInfo_success() {
        String address = "123 Main St";
        int station = 1;
        when(fireStationService.deleteFireStation(station, address)).thenReturn(true);

        ResponseEntity<Void> response = fireStationController.deleteFirestationInfo(address, station);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(fireStationService, times(1)).deleteFireStation(station, address);
    }

    /**
     * Tests the failure of deleting a fire station.
     * Verifies that an exception with a 404 status is thrown if the station is not found.
     */
    @Test
    void deleteFirestationInfo_notFound() {
        String address = "123 Main St";
        int station = 1;
        when(fireStationService.deleteFireStation(station, address)).thenReturn(false);

        Exception exception = null;
        try {
            fireStationController.deleteFirestationInfo(address, station);
        } catch (Exception ex) {
            exception = ex;
        }

        assertEquals("404 NOT_FOUND \"Station not found for deletion\"", exception.getMessage());
        verify(fireStationService, times(1)).deleteFireStation(station, address);
    }
}
