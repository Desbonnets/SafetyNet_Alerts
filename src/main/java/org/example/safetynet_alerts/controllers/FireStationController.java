package org.example.safetynet_alerts.controllers;

import org.example.safetynet_alerts.models.FireStation;
import org.example.safetynet_alerts.service.FireStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class FireStationController {

    @Autowired
    private FireStationService fireStationService;

    @GetMapping("/firestation")
    public FireStation getFirestationInfo(@RequestParam("stationNumber") int stationNumber) {

        FireStation fireStation = fireStationService.getFireStationByNumber(stationNumber);
        if (fireStation == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Station introuvable pour le numéro " + stationNumber);
        }
        // Logique pour récupérer des informations en fonction du numéro de station
        return fireStation;
    }
}
