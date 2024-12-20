package org.example.safetynet_alerts.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.safetynet_alerts.controllers.MedicalRecordController;
import org.example.safetynet_alerts.models.MedicalRecord;
import org.example.safetynet_alerts.service.MedicalRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MedicalRecordControllerTest {

    @Mock
    private MedicalRecordService medicalRecordService; // Mock du service MedicalRecordService

    @InjectMocks
    private MedicalRecordController medicalRecordController; // Contrôleur à tester

    public MedicalRecordControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    private MedicalRecord createMedicalRecord() throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date birthDate = sdf.parse("01/01/1990");
        return new MedicalRecord("John", "Doe", birthDate, List.of("Med1", "Med2"), List.of("Allergy1", "Allergy2"));
    }

    /**
     * Teste la récupération des informations d'un dossier médical trouvé.
     * Vérifie que le statut HTTP et les données retournées sont corrects.
     */
    @Test
    void getMedicalRecordInfo_found() throws Exception {
        MedicalRecord mockRecord = createMedicalRecord();
        when(medicalRecordService.getMedicalRecordByFirstnameAndLastname("John", "Doe")).thenReturn(mockRecord);

        ResponseEntity<MedicalRecord> response = medicalRecordController.getMedicalRecordInfo("John", "Doe");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRecord, response.getBody());
        verify(medicalRecordService, times(1)).getMedicalRecordByFirstnameAndLastname("John", "Doe");
    }

    /**
     * Teste la récupération des informations d'un dossier médical non trouvé.
     * Vérifie que le statut HTTP est 404 et que le corps de la réponse est null.
     */
    @Test
    void getMedicalRecordInfo_notFound() throws Exception {
        when(medicalRecordService.getMedicalRecordByFirstnameAndLastname("Jane", "Doe")).thenReturn(null);

        ResponseEntity<MedicalRecord> response = medicalRecordController.getMedicalRecordInfo("Jane", "Doe");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(medicalRecordService, times(1)).getMedicalRecordByFirstnameAndLastname("Jane", "Doe");
    }

    /**
     * Teste l'ajout d'un dossier médical réussi.
     * Vérifie que le statut HTTP est 201 et que le dossier médical est bien ajouté.
     */
    @Test
    void postMedicalRecord_success() throws Exception {
        MedicalRecord newRecord = createMedicalRecord();
        when(medicalRecordService.addMedicalRecord(newRecord)).thenReturn(true);

        ResponseEntity<MedicalRecord> response = medicalRecordController.postMedicalRecord(newRecord);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newRecord, response.getBody());
        verify(medicalRecordService, times(1)).addMedicalRecord(newRecord);
    }

    /**
     * Teste l'échec de l'ajout d'un dossier médical.
     * Vérifie qu'une exception avec un statut 400 est levée en cas d'échec.
     */
    @Test
    void postMedicalRecord_failure() throws Exception {
        MedicalRecord newRecord = createMedicalRecord();
        when(medicalRecordService.addMedicalRecord(newRecord)).thenReturn(false);

        Exception exception = null;
        try {
            medicalRecordController.postMedicalRecord(newRecord);
        } catch (Exception ex) {
            exception = ex;
        }

        assertEquals("400 BAD_REQUEST \"Erreur d'enregistrement\"", exception.getMessage());
        verify(medicalRecordService, times(1)).addMedicalRecord(newRecord);
    }

    /**
     * Teste la mise à jour réussie d'un dossier médical.
     * Vérifie que le statut HTTP est 200 et que les données mises à jour sont correctes.
     */
    @Test
    void putMedicalRecordInfo_success() throws Exception {
        MedicalRecord updatedRecord = createMedicalRecord();
        updatedRecord.setMedications(List.of("Med3"));

        when(medicalRecordService.updateMedicalRecord("John", "Doe", updatedRecord)).thenReturn(updatedRecord);

        ResponseEntity<MedicalRecord> response = medicalRecordController.putMedicalRecordInfo("John", "Doe", updatedRecord);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedRecord, response.getBody());
        verify(medicalRecordService, times(1)).updateMedicalRecord("John", "Doe", updatedRecord);
    }

    /**
     * Teste l'échec de la mise à jour d'un dossier médical.
     * Vérifie qu'une exception avec un statut 404 est levée si le dossier n'existe pas.
     */
    @Test
    void putMedicalRecordInfo_notFound() throws Exception {
        MedicalRecord updatedRecord = createMedicalRecord();
        when(medicalRecordService.updateMedicalRecord("John", "Doe", updatedRecord)).thenReturn(null);

        Exception exception = null;
        try {
            medicalRecordController.putMedicalRecordInfo("John", "Doe", updatedRecord);
        } catch (Exception ex) {
            exception = ex;
        }

        assertEquals("404 NOT_FOUND \"Dossier médical non trouvé pour mise à jour\"", exception.getMessage());
        verify(medicalRecordService, times(1)).updateMedicalRecord("John", "Doe", updatedRecord);
    }

    /**
     * Teste la suppression réussie d'un dossier médical.
     * Vérifie que le statut HTTP est 200 en cas de suppression réussie.
     */
    @Test
    void deleteMedicalRecordInfo_success() throws Exception {
        when(medicalRecordService.deleteMedicalRecord("John", "Doe")).thenReturn(true);

        ResponseEntity<Void> response = medicalRecordController.deleteMedicalRecordInfo("John", "Doe");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(medicalRecordService, times(1)).deleteMedicalRecord("John", "Doe");
    }

    /**
     * Teste l'échec de la suppression d'un dossier médical.
     * Vérifie qu'une exception avec un statut 404 est levée si le dossier n'existe pas.
     */
    @Test
    void deleteMedicalRecordInfo_notFound() throws Exception {
        when(medicalRecordService.deleteMedicalRecord("John", "Doe")).thenReturn(false);

        Exception exception = null;
        try {
            medicalRecordController.deleteMedicalRecordInfo("John", "Doe");
        } catch (Exception ex) {
            exception = ex;
        }

        assertEquals("404 NOT_FOUND \"Dossier médical non trouvé pour suppression\"", exception.getMessage());
        verify(medicalRecordService, times(1)).deleteMedicalRecord("John", "Doe");
    }
}
