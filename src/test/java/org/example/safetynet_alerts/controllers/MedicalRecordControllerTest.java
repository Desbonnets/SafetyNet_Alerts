package org.example.safetynet_alerts.controllers;

import org.example.safetynet_alerts.models.MedicalRecord;
import org.example.safetynet_alerts.service.MedicalRecordService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the {@link MedicalRecordController}.
 * This class tests the core functionality of the API endpoints for managing medical records.
 */
class MedicalRecordControllerTest {

    @Mock
    private MedicalRecordService medicalRecordService; // Mock for the MedicalRecordService

    @InjectMocks
    private MedicalRecordController medicalRecordController; // Controller to test

    public MedicalRecordControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    private MedicalRecord createMedicalRecord() {
        return new MedicalRecord("John", "Doe", "01/01/1990", List.of("Med1", "Med2"), List.of("Allergy1", "Allergy2"));
    }

    /**
     * Tests retrieving a found medical record.
     * Verifies that the HTTP status and returned data are correct.
     */
    @Test
    void getMedicalRecordInfo_found() {
        MedicalRecord mockRecord = createMedicalRecord();
        when(medicalRecordService.getMedicalRecordByFirstnameAndLastname("John", "Doe")).thenReturn(mockRecord);

        ResponseEntity<MedicalRecord> response = medicalRecordController.getMedicalRecordInfo("John", "Doe");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockRecord, response.getBody());
        verify(medicalRecordService, times(1)).getMedicalRecordByFirstnameAndLastname("John", "Doe");
    }

    /**
     * Tests retrieving a not found medical record.
     * Verifies that the HTTP status is 404 and that the response body is null.
     */
    @Test
    void getMedicalRecordInfo_notFound() {
        when(medicalRecordService.getMedicalRecordByFirstnameAndLastname("Jane", "Doe")).thenReturn(null);

        ResponseEntity<MedicalRecord> response = medicalRecordController.getMedicalRecordInfo("Jane", "Doe");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(medicalRecordService, times(1)).getMedicalRecordByFirstnameAndLastname("Jane", "Doe");
    }

    /**
     * Tests the successful addition of a medical record.
     * Verifies that the HTTP status is 201 and the record is correctly added.
     */
    @Test
    void postMedicalRecord_success() {
        MedicalRecord newRecord = createMedicalRecord();
        when(medicalRecordService.addMedicalRecord(newRecord)).thenReturn(true);

        ResponseEntity<MedicalRecord> response = medicalRecordController.postMedicalRecord(newRecord);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newRecord, response.getBody());
        verify(medicalRecordService, times(1)).addMedicalRecord(newRecord);
    }

    /**
     * Tests the failure of adding a medical record.
     * Verifies that an exception with a 400 status is thrown on failure.
     */
    @Test
    void postMedicalRecord_failure() {
        MedicalRecord newRecord = createMedicalRecord();
        when(medicalRecordService.addMedicalRecord(newRecord)).thenReturn(false);

        Exception exception = null;
        try {
            medicalRecordController.postMedicalRecord(newRecord);
        } catch (Exception ex) {
            exception = ex;
        }

        assertEquals("400 BAD_REQUEST \"Registration error\"", exception.getMessage());
        verify(medicalRecordService, times(1)).addMedicalRecord(newRecord);
    }

    /**
     * Tests the successful update of a medical record.
     * Verifies that the HTTP status is 200 and that the updated data is correct.
     */
    @Test
    void putMedicalRecordInfo_success() {
        MedicalRecord updatedRecord = createMedicalRecord();
        updatedRecord.setMedications(List.of("Med3"));

        when(medicalRecordService.updateMedicalRecord("John", "Doe", updatedRecord)).thenReturn(updatedRecord);

        ResponseEntity<MedicalRecord> response = medicalRecordController.putMedicalRecordInfo("John", "Doe", updatedRecord);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedRecord, response.getBody());
        verify(medicalRecordService, times(1)).updateMedicalRecord("John", "Doe", updatedRecord);
    }

    /**
     * Tests the failure of updating a medical record.
     * Verifies that an exception with a 404 status is thrown if the record is not found.
     */
    @Test
    void putMedicalRecordInfo_notFound() {
        MedicalRecord updatedRecord = createMedicalRecord();
        when(medicalRecordService.updateMedicalRecord("John", "Doe", updatedRecord)).thenReturn(null);

        Exception exception = null;
        try {
            medicalRecordController.putMedicalRecordInfo("John", "Doe", updatedRecord);
        } catch (Exception ex) {
            exception = ex;
        }

        assertEquals("404 NOT_FOUND \"Medical record not found for update\"", exception.getMessage());
        verify(medicalRecordService, times(1)).updateMedicalRecord("John", "Doe", updatedRecord);
    }

    /**
     * Tests the successful deletion of a medical record.
     * Verifies that the HTTP status is 200 on successful deletion.
     */
    @Test
    void deleteMedicalRecordInfo_success() {
        when(medicalRecordService.deleteMedicalRecord("John", "Doe")).thenReturn(true);

        ResponseEntity<Void> response = medicalRecordController.deleteMedicalRecordInfo("John", "Doe");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(medicalRecordService, times(1)).deleteMedicalRecord("John", "Doe");
    }

    /**
     * Tests the failure of deleting a medical record.
     * Verifies that an exception with a 404 status is thrown if the record is not found.
     */
    @Test
    void deleteMedicalRecordInfo_notFound() {
        when(medicalRecordService.deleteMedicalRecord("John", "Doe")).thenReturn(false);

        Exception exception = null;
        try {
            medicalRecordController.deleteMedicalRecordInfo("John", "Doe");
        } catch (Exception ex) {
            exception = ex;
        }

        assertEquals("404 NOT_FOUND \"Medical record not found for deletion\"", exception.getMessage());
        verify(medicalRecordService, times(1)).deleteMedicalRecord("John", "Doe");
    }

    /**
     * Tests retrieving all medical records.
     * Verifies that the HTTP status is 200 and the correct list of records is returned.
     */
    @Test
    void getAllMedicalRecordInfo_found() {
        MedicalRecord mockRecord1 = createMedicalRecord();
        MedicalRecord mockRecord2 = new MedicalRecord("Jane", "Doe", "02/02/1992", List.of("Med3"), List.of("Allergy3"));
        List<MedicalRecord> mockMedicalRecords = List.of(mockRecord1, mockRecord2);

        when(medicalRecordService.getAllMedicalRecordList()).thenReturn(mockMedicalRecords);

        ResponseEntity<List<MedicalRecord>> response = medicalRecordController.getAllMedicalRecordInfo();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockMedicalRecords, response.getBody());
        assertEquals(2, response.getBody().size());
        verify(medicalRecordService, times(1)).getAllMedicalRecordList();
    }

    /**
     * Tests retrieving all medical records when no records are found.
     * Verifies that the HTTP status is 404 and the response body is null.
     */
    @Test
    void getAllMedicalRecordInfo_notFound() {
        when(medicalRecordService.getAllMedicalRecordList()).thenReturn(Collections.emptyList());

        ResponseEntity<List<MedicalRecord>> response = medicalRecordController.getAllMedicalRecordInfo();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(medicalRecordService, times(1)).getAllMedicalRecordList();
    }

}
