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

/**
 * Unit test class for {@link PersonInfoService}.
 * This class validates the functionalities of the service that manages
 * detailed person information, including age, medical records, and coverage.
 */
class PersonInfoServiceTest {

    @Mock
    private MedicalRecordService medicalRecordService; // Mocked MedicalRecordService for handling medical data

    @Mock
    private FireStationService fireStationService; // Mocked FireStationService for handling fire station data

    @Mock
    private ObjectMapper objectMapper; // Mocked ObjectMapper for reading JSON data

    private PersonInfoService personInfoService; // Instance of PersonInfoService under test
    private List<Person> mockPersons; // Mocked list of persons

    /**
     * Setup before each test.
     * Initializes mocks and loads simulated data for testing.
     *
     * @throws IOException if an error occurs while loading mock data.
     */
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

    /**
     * Tests retrieving all person information, including age and medical data.
     * Verifies that the data is returned correctly for each person.
     */
    @Test
    void getAllPersonInfo_ShouldReturnPersonInfo() {
        MedicalRecord johnRecord = new MedicalRecord("John", "Doe", "01/01/2000", List.of("med1"), List.of("allergy1"));
        MedicalRecord janeRecord = new MedicalRecord("Jane", "Doe", "01/01/2010", List.of("med2"), List.of("allergy2"));

        when(medicalRecordService.getMedicalRecordByFirstnameAndLastname("John", "Doe")).thenReturn(johnRecord);
        when(medicalRecordService.getMedicalRecordByFirstnameAndLastname("Jane", "Doe")).thenReturn(janeRecord);

        List<Map<String, Object>> result = personInfoService.getAllPersonInfo(mockPersons);

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).get("firstName"));
        assertEquals(25, result.get(0).get("age")); // Assuming the current year is 2025
        assertEquals("med1", ((List<?>) result.get(0).get("medications")).get(0));
    }

    /**
     * Tests retrieving child alert information by address.
     * Verifies that children and their family members are returned correctly.
     */
    @Test
    void getChildAlertByAddress_ShouldReturnChildrenInfo() {
        MedicalRecord johnRecord = new MedicalRecord("John", "Doe", "01/01/2010", List.of(), List.of());
        MedicalRecord janeRecord = new MedicalRecord("Jane", "Doe", "01/01/1990", List.of(), List.of());

        when(medicalRecordService.getMedicalRecordByFirstnameAndLastname("John", "Doe")).thenReturn(johnRecord);
        when(medicalRecordService.getMedicalRecordByFirstnameAndLastname("Jane", "Doe")).thenReturn(janeRecord);

        List<Map<String, Object>> result = personInfoService.getChildAlertByAddress("123 Street");

        assertEquals(1, result.size());
        assertEquals("John", result.get(0).get("firstName"));
        assertEquals(15, result.get(0).get("age")); // Assuming the current year is 2025
        List<String> familyMembers = (List<String>) result.get(0).get("familyMembers");
        assertEquals(1, familyMembers.size());
        assertEquals("Jane Doe", familyMembers.get(0));
    }

    /**
     * Tests retrieving fire station coverage.
     * Verifies that persons, children count, and adult count are returned correctly.
     */
    @Test
    void getCoverageByFireStation_ShouldReturnCoverageInfo() {
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
