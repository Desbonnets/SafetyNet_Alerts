package org.example.safetynet_alerts.controllers;

import org.example.safetynet_alerts.models.FireStation;
import org.example.safetynet_alerts.models.Person;
import org.example.safetynet_alerts.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link ApiController}.
 * This class contains unit tests for the various API endpoints in the {@link ApiController}.
 */
@WebMvcTest(ApiController.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    @MockBean
    private FireStationService fireStationService;

    @MockBean
    private PersonInfoService personInfoService;

    private List<Person> mockPersons;

    /**
     * Initializes mock data before each test.
     */
    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        mockPersons = new ArrayList<>();
        mockPersons.add(
                new Person(
                        "John",
                        "Doe",
                        "123 Main St",
                        "City",
                        123456,
                        "123-456-7890",
                        "john.doe@example.com"
                )
        );
    }

    /**
     * Tests the API endpoint for retrieving community emails.
     * @throws Exception if there is an issue with the test execution
     */
    @Test
    void testGetCommunityEmail_Found() throws Exception {
        Mockito.when(personService.getAllEmailByCity(anyString()))
                .thenReturn(List.of("email1@example.com", "email2@example.com"));

        mockMvc.perform(get("/communityEmail")
                        .param("city", "TestCity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    /**
     * Tests the API endpoint for retrieving community emails when no emails are found.
     * @throws Exception if there is an issue with the test execution
     */
    @Test
    void testGetCommunityEmail_NotFound() throws Exception {
        Mockito.when(personService.getAllEmailByCity(anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/communityEmail")
                        .param("city", "TestCity"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Tests the API endpoint for retrieving person information by last name.
     * @throws Exception if there is an issue with the test execution
     */
    @Test
    void testGetPersonInfo_Found() throws Exception {
        Mockito.when(personService.getAllPersonByLastname(anyString()))
                .thenReturn(mockPersons);
        Mockito.when(personInfoService.getAllPersonInfo(any()))
                .thenReturn(List.of(Map.of("name", "John Doe", "age", 30)));

        mockMvc.perform(get("/personInfo")
                        .param("lastName", "Doe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    /**
     * Tests the API endpoint for retrieving person information by last name when no person is found.
     * @throws Exception if there is an issue with the test execution
     */
    @Test
    void testGetPersonInfo_NotFound() throws Exception {
        Mockito.when(personService.getAllPersonByLastname(anyString()))
                .thenReturn(Collections.emptyList());
        Mockito.when(personInfoService.getAllPersonInfo(any()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/personInfo")
                        .param("lastName", "Doe"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Tests the API endpoint for retrieving fire stations affected by a flood.
     * @throws Exception if there is an issue with the test execution
     */
    @Test
    void testGetStationsFlood_Found() throws Exception {
        Mockito.when(fireStationService.getAddressByFireStationsNumber(anyInt()))
                .thenReturn(List.of("123 Main St"));
        Mockito.when(personService.getPersonsByAddress(anyString()))
                .thenReturn(mockPersons);
        Mockito.when(personInfoService.getAllPersonInfo(any()))
                .thenReturn(List.of(Map.of("name", "John Doe")));

        mockMvc.perform(get("/flood/stations")
                        .param("stations", "1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].personInfos[0].name").value("John Doe"));
    }

    /**
     * Tests the API endpoint for retrieving fire stations affected by a flood when no stations are found.
     * @throws Exception if there is an issue with the test execution
     */
    @Test
    void testGetStationsFlood_NotFound() throws Exception {
        Mockito.when(fireStationService.getAddressByFireStationsNumber(anyInt()))
                .thenReturn(null);

        mockMvc.perform(get("/flood/stations")
                        .param("stations", "1,2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Tests the API endpoint for retrieving phone alerts for a fire station.
     * @throws Exception if there is an issue with the test execution
     */
    @Test
    void testGetPhoneAlert_Found() throws Exception {
        Mockito.when(fireStationService.getAddressByFireStationsNumber(anyInt()))
                .thenReturn(List.of("123 Main St"));
        Mockito.when(personService.getPersonsByAddress(anyString()))
                .thenReturn(mockPersons);
        Mockito.when(personService.getAllPhoneByPersons(any()))
                .thenReturn(List.of("123-456-7890"));

        mockMvc.perform(get("/phoneAlert")
                        .param("fireStationNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]").value("123-456-7890"));
    }

    /**
     * Tests the API endpoint for retrieving phone alerts for a fire station when no phones are found.
     * @throws Exception if there is an issue with the test execution
     */
    @Test
    void testGetPhoneAlert_NotFound() throws Exception {
        Mockito.when(fireStationService.getAddressByFireStationsNumber(anyInt()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/phoneAlert")
                        .param("fireStationNumber", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Tests the API endpoint for retrieving information about a fire at a given address.
     * @throws Exception if there is an issue with the test execution
     */
    @Test
    void testGetFire_Found() throws Exception {
        Mockito.when(fireStationService.getFireStationByAddress(anyString()))
                .thenReturn(List.of(new FireStation("123 Main St", 1)));
        Mockito.when(personService.getPersonsByAddress(anyString()))
                .thenReturn(mockPersons);
        Mockito.when(personInfoService.getAllPersonInfo(any()))
                .thenReturn(List.of(Map.of("name", "John Doe")));

        mockMvc.perform(get("/fire")
                        .param("address", "123 Main St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].station").value(1))
                .andExpect(jsonPath("$[0].personInfos[0].name").value("John Doe"));
    }

    /**
     * Tests the API endpoint for retrieving information about a fire at a given address when no fire stations are found.
     * @throws Exception if there is an issue with the test execution
     */
    @Test
    void testGetFire_NotFound() throws Exception {
        Mockito.when(fireStationService.getFireStationByAddress(anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/fire")
                        .param("address", "123 Main St"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Tests the API endpoint for retrieving child alerts based on address.
     * @throws Exception if there is an issue with the test execution
     */
    @Test
    void testGetChildAlert_Found() throws Exception {
        Mockito.when(personInfoService.getChildAlertByAddress(anyString()))
                .thenReturn(List.of(Map.of("childName", "Jane Doe", "age", 8)));

        mockMvc.perform(get("/childAlert")
                        .param("address", "123 Main St"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].childName").value("Jane Doe"));
    }

    /**
     * Tests the API endpoint for retrieving child alerts when no children are found.
     * @throws Exception if there is an issue with the test execution
     */
    @Test
    void testGetChildAlert_NotFound() throws Exception {
        Mockito.when(personInfoService.getChildAlertByAddress(anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/childAlert")
                        .param("address", "123 Main St"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Tests the API endpoint for retrieving fire station coverage information.
     * @throws Exception if there is an issue with the test execution
     */
    @Test
    void testGetCoverageByFireStation_Found() throws Exception {
        Mockito.when(personInfoService.getCoverageByFireStation(anyInt()))
                .thenReturn(Map.of(
                        "persons", List.of(Map.of("name", "John Doe")),
                        "adultCount", 1,
                        "childrenCount", 0
                ));

        mockMvc.perform(get("/firestation")
                        .param("stationNumber", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.persons", hasSize(1)))
                .andExpect(jsonPath("$.adultCount").value(1))
                .andExpect(jsonPath("$.childrenCount").value(0));
    }

    /**
     * Tests the API endpoint for retrieving fire station coverage information when no data is found.
     * @throws Exception if there is an issue with the test execution
     */
    @Test
    void testGetCoverageByFireStation_NotFound() throws Exception {
        Mockito.when(personInfoService.getCoverageByFireStation(anyInt()))
                .thenReturn(Collections.emptyMap());

        mockMvc.perform(get("/firestation")
                        .param("stationNumber", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").isEmpty());
    }
}
