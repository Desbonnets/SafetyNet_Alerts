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

class FireStationControllerTest {

    @Mock
    private FireStationService fireStationService;

    @InjectMocks
    private FireStationController fireStationController;

    public FireStationControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getFirestationInfo_found() {
        // Arrange
        int stationNumber = 1;
        List<FireStation> mockStations = List.of(new FireStation("123 Main St", 1));
        when(fireStationService.getFireStationByNumber(stationNumber)).thenReturn(mockStations);

        // Act
        ResponseEntity<List<FireStation>> response = fireStationController.getFirestationInfo(stationNumber);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockStations, response.getBody());
        verify(fireStationService, times(1)).getFireStationByNumber(stationNumber);
    }

    @Test
    void getFirestationInfo_notFound() {
        // Arrange
        int stationNumber = 1;
        when(fireStationService.getFireStationByNumber(stationNumber)).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<FireStation>> response = fireStationController.getFirestationInfo(stationNumber);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());
        verify(fireStationService, times(1)).getFireStationByNumber(stationNumber);
    }

    @Test
    void postFirestation_success() {
        // Arrange
        FireStation newStation = new FireStation("123 Main St", 1);
        when(fireStationService.addFireStation(newStation)).thenReturn(true);

        // Act
        ResponseEntity<FireStation> response = fireStationController.postFirestation(newStation);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newStation, response.getBody());
        verify(fireStationService, times(1)).addFireStation(newStation);
    }

    @Test
    void postFirestation_failure() {
        // Arrange
        FireStation newStation = new FireStation("123 Main St", 1);
        when(fireStationService.addFireStation(newStation)).thenReturn(false);

        // Act & Assert
        Exception exception = null;
        try {
            fireStationController.postFirestation(newStation);
        } catch (Exception ex) {
            exception = ex;
        }
        assertEquals("400 BAD_REQUEST \"Erreur d'enregistrement\"", exception.getMessage());
        verify(fireStationService, times(1)).addFireStation(newStation);
    }

    @Test
    void putFirestationInfo_success() {
        // Arrange
        String address = "123 Main St";
        int station = 1;
        FireStation updatedStation = new FireStation("123 Main St", 2);
        when(fireStationService.updateFireStation(address, station, updatedStation)).thenReturn(updatedStation);

        // Act
        ResponseEntity<FireStation> response = fireStationController.putFirestationInfo(address, station, updatedStation);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedStation, response.getBody());
        verify(fireStationService, times(1)).updateFireStation(address, station, updatedStation);
    }

    @Test
    void putFirestationInfo_notFound() {
        // Arrange
        String address = "123 Main St";
        int station = 1;
        FireStation updatedStation = new FireStation("123 Main St", 2);
        when(fireStationService.updateFireStation(address, station, updatedStation)).thenReturn(null);

        // Act & Assert
        Exception exception = null;
        try {
            fireStationController.putFirestationInfo(address, station, updatedStation);
        } catch (Exception ex) {
            exception = ex;
        }
        assertEquals("404 NOT_FOUND \"Station non trouvée pour mise à jour\"", exception.getMessage());
        verify(fireStationService, times(1)).updateFireStation(address, station, updatedStation);
    }

    @Test
    void deleteFirestationInfo_success() {
        // Arrange
        String address = "123 Main St";
        int station = 1;
        when(fireStationService.deleteFireStation(station, address)).thenReturn(true);

        // Act
        ResponseEntity<Void> response = fireStationController.deleteFirestationInfo(address, station);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(fireStationService, times(1)).deleteFireStation(station, address);
    }

    @Test
    void deleteFirestationInfo_notFound() {
        // Arrange
        String address = "123 Main St";
        int station = 1;
        when(fireStationService.deleteFireStation(station, address)).thenReturn(false);

        // Act & Assert
        Exception exception = null;
        try {
            fireStationController.deleteFirestationInfo(address, station);
        } catch (Exception ex) {
            exception = ex;
        }
        assertEquals("404 NOT_FOUND \"Station non trouvée pour suppression\"", exception.getMessage());
        verify(fireStationService, times(1)).deleteFireStation(station, address);
    }
}
