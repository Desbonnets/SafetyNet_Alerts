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
 * Classe de test pour le service {@link FireStationService}.
 * Cette classe teste toutes les fonctionnalités principales du service,
 * notamment la gestion des stations de pompiers.
 */
class FireStationServiceTest {

    @Mock
    private ObjectMapper objectMapper; // Mock de l'ObjectMapper pour lire les données JSON

    private FireStationService fireStationService; // Instance de FireStationService à tester

    private List<FireStation> mockFireStations; // Liste simulée des stations de pompiers

    /**
     * Configuration avant chaque test.
     * Initialise les mocks et charge des données simulées pour les tests.
     *
     * @throws IOException si une erreur se produit lors du chargement des données simulées.
     */
    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Mock des données initiales
        mockFireStations = new ArrayList<>();
        mockFireStations.add(new FireStation("123 Main St", 1));
        mockFireStations.add(new FireStation("456 Elm St", 2));

        // Mock du fichier JSON
        FireStationsData mockData = new FireStationsData();
        mockData.setFirestations(mockFireStations);

        // Mock de l'ObjectMapper pour retourner des données simulées
        when(objectMapper.readValue(any(InputStream.class), eq(FireStationsData.class))).thenReturn(mockData);

        // Initialisation du service avec l'ObjectMapper mocké
        fireStationService = new FireStationService(objectMapper);
    }

    /**
     * Teste la récupération de toutes les stations.
     * Vérifie que toutes les stations initiales sont renvoyées correctement.
     */
    @Test
    void getAllFireStations_shouldReturnAllStations() {
        List<FireStation> result = fireStationService.getAllFireStations();
        assertEquals(mockFireStations, result);
    }

    /**
     * Teste la récupération des stations par numéro.
     * Vérifie que les stations associées au numéro donné sont renvoyées correctement.
     */
    @Test
    void getFireStationByNumber_shouldReturnStationsForGivenNumber() {
        List<FireStation> result = fireStationService.getFireStationByNumber(1);
        assertEquals(1, result.size());
        assertEquals("123 Main St", result.getFirst().getAddress());
    }

    /**
     * Teste la récupération des stations avec un numéro invalide.
     * Vérifie qu'une liste vide est renvoyée si aucune station ne correspond.
     */
    @Test
    void getFireStationByNumber_shouldReturnEmptyListForInvalidNumber() {
        List<FireStation> result = fireStationService.getFireStationByNumber(99);
        assertTrue(result.isEmpty());
    }

    /**
     * Teste l'ajout d'une nouvelle station.
     * Vérifie que la station est ajoutée correctement si elle n'existe pas déjà.
     */
    @Test
    void addFireStation_shouldAddNewStation() {
        FireStation newStation = new FireStation("789 Oak St", 3);
        boolean success = fireStationService.addFireStation(newStation);
        assertTrue(success);
        assertTrue(fireStationService.getAllFireStations().contains(newStation));
    }

    /**
     * Teste l'ajout d'une station déjà existante.
     * Vérifie qu'aucune station en double ne peut être ajoutée.
     */
    @Test
    void addFireStation_shouldNotAddDuplicateStation() {
        FireStation duplicateStation = new FireStation("123 Main St", 1);
        boolean success = fireStationService.addFireStation(duplicateStation);
        assertFalse(success);
    }

    /**
     * Teste la mise à jour d'une station existante.
     * Vérifie que la station est mise à jour correctement.
     */
    @Test
    void updateFireStation_shouldUpdateExistingStation() {
        FireStation updatedStation = new FireStation("123 Main St", 5);
        FireStation result = fireStationService.updateFireStation("123 Main St", 1, updatedStation);
        assertEquals(updatedStation, result);
    }

    /**
     * Teste la mise à jour d'une station inexistante.
     * Vérifie qu'une exception est levée si la station n'existe pas.
     */
    @Test
    void updateFireStation_shouldThrowExceptionIfStationNotFound() {
        FireStation updatedStation = new FireStation("Nonexistent St", 5);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            fireStationService.updateFireStation("Nonexistent St", 99, updatedStation);
        });

        assertEquals("FireStation non trouvée pour l'adresse et numéro de station : Nonexistent St 99", exception.getMessage());
    }

    /**
     * Teste la suppression d'une station existante.
     * Vérifie que la station est supprimée correctement si elle existe.
     */
    @Test
    void deleteFireStation_shouldRemoveStationIfExists() {
        boolean success = fireStationService.deleteFireStation(1, "123 Main St");
        assertTrue(success);
        assertEquals(1, fireStationService.getAllFireStations().size());
    }

    /**
     * Teste la suppression d'une station inexistante.
     * Vérifie que la méthode retourne false si aucune station ne correspond.
     */
    @Test
    void deleteFireStation_shouldReturnFalseIfStationNotFound() {
        boolean success = fireStationService.deleteFireStation(99, "Nonexistent St");
        assertFalse(success);
    }
}
