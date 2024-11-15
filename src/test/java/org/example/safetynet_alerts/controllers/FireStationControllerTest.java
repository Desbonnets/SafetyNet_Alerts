package org.example.safetynet_alerts.controllers;

import org.example.safetynet_alerts.models.FireStation;
import org.example.safetynet_alerts.service.FireStationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(FireStationController.class)
class FireStationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FireStationService fireStationService;

    @Test
    void testGetFirestationInfo_StationExists() throws Exception {
        // Mock du service
        FireStation mockFireStation = new FireStation("1509 Culver St", 3);
        when(fireStationService.getFireStationByNumber(3)).thenReturn(mockFireStation);

        // Simuler une requête GET et vérifier la réponse
        mockMvc.perform(get("/firestation")
                        .param("stationNumber", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("1509 Culver St"))
                .andExpect(jsonPath("$.station").value(3));
    }

    @Test
    void testGetFirestationInfo_StationNotFound() throws Exception {
        // Simuler un retour null
        when(fireStationService.getFireStationByNumber(5)).thenReturn(null);

        // Simuler une requête GET et vérifier une exception
        mockMvc.perform(get("/firestation")
                        .param("stationNumber", "5"))
                .andExpect(status().isNotFound());
    }
}
