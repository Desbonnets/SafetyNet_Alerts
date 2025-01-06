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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MedicalRecordServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    private MedicalRecordService medicalRecordService;

    private List<MedicalRecord> mockMedicalRecords;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @BeforeEach
    void setUp() throws IOException, ParseException {
        MockitoAnnotations.openMocks(this);

        // Mock data for MedicalRecord
        mockMedicalRecords = new ArrayList<>();
        mockMedicalRecords.add(new MedicalRecord(
                "John",
                "Doe",
                dateFormat.parse("01/01/1980"),
                List.of("med1"),
                List.of("allergy1")
        ));
        mockMedicalRecords.add(new MedicalRecord(
                "Jane",
                "Smith",
                dateFormat.parse("02/02/1990"),
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

    @Test
    void getAllMedicalRecordList_ShouldReturnAllRecords() {
        List<MedicalRecord> records = medicalRecordService.getAllMedicalRecordList();
        assertEquals(2, records.size());
        assertEquals("John", records.get(0).getFirstName());
    }

    @Test
    void getMedicalRecordByFirstnameAndLastname_ShouldReturnCorrectRecord() {
        MedicalRecord record = medicalRecordService.getMedicalRecordByFirstnameAndLastname("John", "Doe");
        assertNotNull(record);
        assertEquals("John", record.getFirstName());
        assertEquals("Doe", record.getLastName());
    }

    @Test
    void getMedicalRecordByFirstnameAndLastname_ShouldReturnNullIfNotFound() {
        MedicalRecord record = medicalRecordService.getMedicalRecordByFirstnameAndLastname("Non", "Existent");
        assertNull(record);
    }

    @Test
    void addMedicalRecord_ShouldAddNewRecord() throws ParseException {
        MedicalRecord newRecord = new MedicalRecord(
                "Alice",
                "Wonder",
                dateFormat.parse("03/03/2000"),
                List.of("med3"),
                List.of("allergy3")
        );
        boolean isAdded = medicalRecordService.addMedicalRecord(newRecord);
        assertTrue(isAdded);
        assertEquals(3, medicalRecordService.getAllMedicalRecordList().size());
    }

    @Test
    void updateMedicalRecord_ShouldUpdateExistingRecord() throws ParseException {
        MedicalRecord updatedRecord = new MedicalRecord(
                "John",
                "Doe",
                dateFormat.parse("01/01/1985"),
                List.of("medUpdated"),
                List.of("allergyUpdated")
        );
        MedicalRecord result = medicalRecordService.updateMedicalRecord("John", "Doe", updatedRecord);
        assertEquals(dateFormat.parse("01/01/1985"), result.getBirthDate());
        assertEquals("medUpdated", result.getMedications().get(0));
    }

    @Test
    void deleteMedicalRecord_ShouldRemoveExistingRecord() {
        boolean isDeleted = medicalRecordService.deleteMedicalRecord("John", "Doe");
        assertTrue(isDeleted);
        assertEquals(1, medicalRecordService.getAllMedicalRecordList().size());
    }
}
