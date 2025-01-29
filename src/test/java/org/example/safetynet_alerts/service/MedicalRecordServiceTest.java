package org.example.safetynet_alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.safetynet_alerts.models.MedicalRecord;
import org.example.safetynet_alerts.models.MedicalRecordData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test class for {@link MedicalRecordService}.
 * This class validates the core functionalities of the service,
 * including the management of medical records.
 */
class MedicalRecordServiceTest {

    @Mock
    private ObjectMapper objectMapper; // Mocked ObjectMapper for reading JSON data

    private MedicalRecordService medicalRecordService; // Instance of MedicalRecordService under test

    private List<MedicalRecord> mockMedicalRecords; // Mocked list of medical records

    /**
     * Setup before each test.
     * Initializes mocks and loads simulated data for testing.
     *
     * @throws IOException if an error occurs while loading mock data.
     * @throws ParseException if an error occurs while parsing dates in mock data.
     */
    @BeforeEach
    void setUp() throws IOException, ParseException {
        MockitoAnnotations.openMocks(this);

        // Mock data for MedicalRecord
        mockMedicalRecords = new ArrayList<>();
        mockMedicalRecords.add(new MedicalRecord(
                "John",
                "Doe",
                "01/01/1980",
                List.of("med1"),
                List.of("allergy1")
        ));
        mockMedicalRecords.add(new MedicalRecord(
                "Jane",
                "Smith",
                "02/02/1990",
                List.of("med2"),
                List.of("allergy2")
        ));

        // Mocking the ObjectMapper to return the mock data
        MedicalRecordData mockData = new MedicalRecordData();
        mockData.setMedicalrecords(mockMedicalRecords);

        when(objectMapper.readValue(any(InputStream.class), eq(MedicalRecordData.class)))
                .thenReturn(mockData);

        // Initialize the service
        medicalRecordService = new MedicalRecordService(objectMapper);
    }

    /**
     * Tests retrieving all medical records.
     * Verifies that all records are returned correctly.
     */
    @Test
    void getAllMedicalRecordList_ShouldReturnAllRecords() {
        List<MedicalRecord> records = medicalRecordService.getAllMedicalRecordList();
        assertEquals(2, records.size());
        assertEquals("John", records.get(0).getFirstName());
    }

    /**
     * Tests retrieving a medical record by first and last name.
     * Verifies that the correct record is returned for a valid name.
     */
    @Test
    void getMedicalRecordByFirstnameAndLastname_ShouldReturnCorrectRecord() {
        MedicalRecord record = medicalRecordService.getMedicalRecordByFirstnameAndLastname("John", "Doe");
        assertNotNull(record);
        assertEquals("John", record.getFirstName());
        assertEquals("Doe", record.getLastName());
    }

    /**
     * Tests retrieving a medical record with an invalid name.
     * Verifies that null is returned if the record does not exist.
     */
    @Test
    void getMedicalRecordByFirstnameAndLastname_ShouldReturnNullIfNotFound() {
        MedicalRecord record = medicalRecordService.getMedicalRecordByFirstnameAndLastname("Non", "Existent");
        assertNull(record);
    }

    /**
     * Tests adding a new medical record.
     * Verifies that the record is added correctly.
     *
     * @throws ParseException if there is an error parsing the date for the new record.
     */
    @Test
    void addMedicalRecord_ShouldAddNewRecord() throws ParseException {
        MedicalRecord newRecord = new MedicalRecord(
                "Alice",
                "Wonder",
                "03/03/2000",
                List.of("med3"),
                List.of("allergy3")
        );
        boolean isAdded = medicalRecordService.addMedicalRecord(newRecord);
        assertTrue(isAdded);
        assertEquals(3, medicalRecordService.getAllMedicalRecordList().size());
    }

    /**
     * Tests updating an existing medical record.
     * Verifies that the record is updated correctly.
     *
     * @throws ParseException if there is an error parsing the date for the updated record.
     */
    @Test
    void updateMedicalRecord_ShouldUpdateExistingRecord() throws ParseException {
        MedicalRecord updatedRecord = new MedicalRecord(
                "John",
                "Doe",
                "01/01/1985",
                List.of("medUpdated"),
                List.of("allergyUpdated")
        );
        MedicalRecord result = medicalRecordService.updateMedicalRecord("John", "Doe", updatedRecord);
        assertEquals("01/01/1985", result.getBirthDate());
        assertEquals("medUpdated", result.getMedications().get(0));
    }

    /**
     * Tests deleting an existing medical record.
     * Verifies that the record is removed correctly.
     */
    @Test
    void deleteMedicalRecord_ShouldRemoveExistingRecord() {
        boolean isDeleted = medicalRecordService.deleteMedicalRecord("John", "Doe");
        assertTrue(isDeleted);
        assertEquals(1, medicalRecordService.getAllMedicalRecordList().size());
    }

    /**
     * Tests adding a duplicate medical record.
     * Verifies that duplicate records are not added.
     */
    @Test
    void addMedicalRecord_ShouldNotAddDuplicateRecord() {
        MedicalRecord duplicateRecord = new MedicalRecord(
                "John",
                "Doe",
                "01/01/1980",
                List.of("med1"),
                List.of("allergy1")
        );
        boolean isAdded = medicalRecordService.addMedicalRecord(duplicateRecord);
        assertFalse(isAdded);
        assertEquals(2, medicalRecordService.getAllMedicalRecordList().size());
    }

    /**
     * Tests updating a non-existent medical record.
     * Verifies that an exception is thrown if the record does not exist.
     */
    @Test
    void updateMedicalRecord_ShouldThrowExceptionIfRecordNotFound() {
        MedicalRecord updatedRecord = new MedicalRecord(
                "Non",
                "Existent",
                "01/01/1985",
                List.of("medUpdated"),
                List.of("allergyUpdated")
        );
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                medicalRecordService.updateMedicalRecord("Non", "Existent", updatedRecord));
        assertEquals("MedicalRecord not found for the first and last name: Non Existent", exception.getMessage());
    }

    /**
     * Tests deleting a non-existent medical record.
     * Verifies that the method returns false if no matching record is found.
     */
    @Test
    void deleteMedicalRecord_ShouldReturnFalseIfRecordNotFound() {
        boolean isDeleted = medicalRecordService.deleteMedicalRecord("Non", "Existent");
        assertFalse(isDeleted);
        assertEquals(2, medicalRecordService.getAllMedicalRecordList().size());
    }

    /**
     * Tests retrieving a medical record when null parameters are passed.
     * Verifies that null is returned if either the first or last name is null.
     */
    @Test
    void getMedicalRecordByFirstnameAndLastname_ShouldHandleNullParameters() {
        MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordByFirstnameAndLastname(null, null);
        assertNull(medicalRecord);
    }
}
