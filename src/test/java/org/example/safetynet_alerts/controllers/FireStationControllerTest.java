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
 * Classe de test pour le contrôleur {@link FireStationController}.
 * Cette classe vérifie les fonctionnalités principales des points d'entrée API pour gérer les stations de pompiers.
 */
class FireStationControllerTest {

    @Mock
    private FireStationService fireStationService; // Mock du service FireStationService

    @InjectMocks
    private FireStationController fireStationController; // Contrôleur à tester

    /**
     * Constructeur par défaut.
     * Initialise les mocks nécessaires pour les tests.
     */
    public FireStationControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Teste la récupération des informations d'une station trouvée.
     * Vérifie que le statut HTTP et les données retournées sont corrects.
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
     * Teste la récupération des informations d'une station non trouvée.
     * Vérifie que le statut HTTP est 404 et que le corps de la réponse est null.
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
     * Teste l'ajout d'une station de pompiers réussie.
     * Vérifie que le statut HTTP est 201 et que la station est bien ajoutée.
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
     * Teste l'échec de l'ajout d'une station de pompiers.
     * Vérifie qu'une exception avec un statut 400 est levée en cas d'échec.
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

        assertEquals("400 BAD_REQUEST \"Erreur d'enregistrement\"", exception.getMessage());
        verify(fireStationService, times(1)).addFireStation(newStation);
    }

    /**
     * Teste la mise à jour réussie d'une station de pompiers.
     * Vérifie que le statut HTTP est 200 et que les données mises à jour sont correctes.
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
     * Teste l'échec de la mise à jour d'une station de pompiers.
     * Vérifie qu'une exception avec un statut 404 est levée si la station n'existe pas.
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

        assertEquals("404 NOT_FOUND \"Station non trouvée pour mise à jour\"", exception.getMessage());
        verify(fireStationService, times(1)).updateFireStation(address, station, updatedStation);
    }

    /**
     * Teste la suppression réussie d'une station de pompiers.
     * Vérifie que le statut HTTP est 200 en cas de suppression réussie.
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
     * Teste l'échec de la suppression d'une station de pompiers.
     * Vérifie qu'une exception avec un statut 404 est levée si la station n'existe pas.
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

        assertEquals("404 NOT_FOUND \"Station non trouvée pour suppression\"", exception.getMessage());
        verify(fireStationService, times(1)).deleteFireStation(station, address);
    }
}
