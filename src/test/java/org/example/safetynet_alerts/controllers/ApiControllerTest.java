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

@WebMvcTest(ApiController.class)
public class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonService personService;

    @MockBean
    private MedicalRecordService medicalRecordService;

    @MockBean
    private FireStationService fireStationService;

    @MockBean
    private PersonInfoService personInfoService;

    private List<Person> mockPersons;

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

    @Test
    void testGetCommunityEmail_Found() throws Exception {
        Mockito.when(personService.getAllEmailByCity(anyString()))
                .thenReturn(List.of("email1@example.com", "email2@example.com"));

        mockMvc.perform(get("/communityEmail")
                        .param("city", "TestCity"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testGetCommunityEmail_NotFound() throws Exception {
        Mockito.when(personService.getAllEmailByCity(anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/communityEmail")
                        .param("city", "TestCity"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", hasSize(0)));
    }

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

    @Test
    void testGetStationsFlood_NotFound() throws Exception {
        Mockito.when(fireStationService.getAddressByFireStationsNumber(anyInt()))
                .thenReturn(null);

        mockMvc.perform(get("/flood/stations")
                        .param("stations", "1,2"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", hasSize(0)));
    }

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

    @Test
    void testGetPhoneAlert_NotFound() throws Exception {
        Mockito.when(fireStationService.getAddressByFireStationsNumber(anyInt()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/phoneAlert")
                        .param("fireStationNumber", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", hasSize(0)));
    }

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

    @Test
    void testGetFire_NotFound() throws Exception {
        Mockito.when(fireStationService.getFireStationByAddress(anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/fire")
                        .param("address", "123 Main St"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", hasSize(0)));
    }

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

    @Test
    void testGetChildAlert_NotFound() throws Exception {
        Mockito.when(personInfoService.getChildAlertByAddress(anyString()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/childAlert")
                        .param("address", "123 Main St"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", hasSize(0)));
    }

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
