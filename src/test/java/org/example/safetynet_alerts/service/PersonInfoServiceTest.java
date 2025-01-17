package org.example.safetynet_alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.safetynet_alerts.models.MedicalRecord;
import org.example.safetynet_alerts.models.Person;
import org.example.safetynet_alerts.models.PersonsData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonInfoServiceTest {

    @Mock
    private MedicalRecordService medicalRecordService;

    @Mock
    private FireStationService fireStationService;

    @Mock
    private ObjectMapper objectMapper;

    private PersonInfoService personInfoService;
    private List<Person> mockPersons;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Mock data for Person
        mockPersons = new ArrayList<>();
        mockPersons.add(
                new Person(
                        "John",
                        "Doe",
                        "123 Street",
                        "City",
                        123456,
                        "123-456-7890",
                        "john.doe@example.com"
                )
        );
        mockPersons.add(
                new Person(
                        "Jane",
                        "Doe",
                        "123 Street",
                        "City",
                        213564,
                        "123-456-7891",
                        "jane.doe@example.com"
                )
        );

        // Mocking the ObjectMapper to return the mock data
        PersonsData mockData = new PersonsData();
        mockData.setPersons(mockPersons);
        when(objectMapper.readValue(any(InputStream.class), eq(PersonsData.class))).thenReturn(mockData);

        // Initialize the service
        personInfoService = new PersonInfoService(objectMapper, medicalRecordService, fireStationService);
    }

    @Test
    void getAllPersonInfo_ShouldReturnPersonInfo() {
        // Préparer les MedicalRecords simulés
        MedicalRecord johnRecord = new MedicalRecord("John", "Doe", "01/01/2000", List.of("med1"), List.of("allergy1"));
        MedicalRecord janeRecord = new MedicalRecord("Jane", "Doe", "01/01/2010", List.of("med2"), List.of("allergy2"));

        when(medicalRecordService.getMedicalRecordByFirstnameAndLastname("John", "Doe")).thenReturn(johnRecord);
        when(medicalRecordService.getMedicalRecordByFirstnameAndLastname("Jane", "Doe")).thenReturn(janeRecord);

        List<Map<String, Object>> result = personInfoService.getAllPersonInfo(
                List.of(new Person("John", "Doe", "123 Street", "City", 123464, "123-456-7890", "john.doe@example.com"),
                        new Person("Jane", "Doe", "123 Street", "City", 2345, "123-456-7891", "jane.doe@example.com"))
        );

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).get("firstName"));
        assertEquals("Doe", result.get(0).get("lastName"));
        assertEquals(25, result.get(0).get("age")); // Supposons une date actuelle en 2025
        assertEquals("med1", ((List<?>) result.get(0).get("medications")).get(0));
        assertEquals("allergy1", ((List<?>) result.get(0).get("allergies")).get(0));
    }

    @Test
    void getChildAlertByAddress_ShouldReturnChildrenInfo() {
        // Préparer les MedicalRecords simulés
        MedicalRecord johnRecord = new MedicalRecord("John", "Doe", "01/01/2010", List.of(), List.of());
        MedicalRecord janeRecord = new MedicalRecord("Jane", "Doe", "01/01/1990", List.of(), List.of());

        when(medicalRecordService.getMedicalRecordByFirstnameAndLastname("John", "Doe")).thenReturn(johnRecord);
        when(medicalRecordService.getMedicalRecordByFirstnameAndLastname("Jane", "Doe")).thenReturn(janeRecord);

        List<Map<String, Object>> result = personInfoService.getChildAlertByAddress("123 Street");

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).get("firstName"));
        assertEquals("Doe", result.get(0).get("lastName"));
        assertEquals(15, result.get(0).get("age")); // Supposons une date actuelle en 2025
        List<String> familyMembers = (List<String>) result.get(0).get("familyMembers");
        assertEquals(1, familyMembers.size());
        assertEquals("Jane Doe", familyMembers.get(0));
    }

    @Test
    void getCoverageByFireStation_ShouldReturnCoverageInfo() {
        // Préparer les MedicalRecords simulés
        MedicalRecord johnRecord = new MedicalRecord("John", "Doe", "01/01/2000", List.of(), List.of());
        MedicalRecord janeRecord = new MedicalRecord("Jane", "Doe", "01/01/2010", List.of(), List.of());

        when(fireStationService.getAddressByFireStationsNumber(1)).thenReturn(List.of("123 Street"));
        when(medicalRecordService.getMedicalRecordByFirstnameAndLastname("John", "Doe")).thenReturn(johnRecord);
        when(medicalRecordService.getMedicalRecordByFirstnameAndLastname("Jane", "Doe")).thenReturn(janeRecord);

        Map<String, Object> result = personInfoService.getCoverageByFireStation(1);

        assertEquals(2, ((List<?>) result.get("persons")).size());
        assertEquals(Long.valueOf(1), result.get("childrenCount"));
        assertEquals(Long.valueOf(1), result.get("adultCount"));
    }
}
