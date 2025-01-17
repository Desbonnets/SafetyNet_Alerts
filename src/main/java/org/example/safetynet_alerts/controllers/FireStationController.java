package org.example.safetynet_alerts.controllers;

import org.example.safetynet_alerts.models.FireStation;
import org.example.safetynet_alerts.service.FireStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/firestation")
public class FireStationController {

    @Autowired
    private FireStationService fireStationService;

    @GetMapping("/{stationNumber}")
    public ResponseEntity<List<FireStation>> getFirestationInfo(@PathVariable int stationNumber) {
        List<FireStation> fireStations = fireStationService.getFireStationByNumber(stationNumber);
        if (fireStations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        return ResponseEntity.ok(fireStations);
    }

    @PostMapping("")
    public ResponseEntity<FireStation> postFirestation(@RequestBody FireStation fireStation) {
        boolean isSuccess = fireStationService.addFireStation(fireStation);
        if (isSuccess) {
            URI location = URI.create("/firestations/" + fireStation.getStation()); // Remplacer par un identifiant si nécessaire
            return ResponseEntity.created(location).body(fireStation);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erreur d'enregistrement");
        }
    }

    @PutMapping("/{address}/{station}")
    public ResponseEntity<FireStation> putFirestationInfo(@PathVariable String address, @PathVariable int station, @RequestBody FireStation updatedFireStation) {
        FireStation fireStation = fireStationService.updateFireStation(address, station, updatedFireStation);
        if (fireStation != null) {
            return ResponseEntity.ok(fireStation);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Station non trouvée pour mise à jour");
        }
    }

    @DeleteMapping("/{address}/{station}")
    public ResponseEntity<Void> deleteFirestationInfo(@PathVariable String address, @PathVariable int station) {
        boolean isDeleted = fireStationService.deleteFireStation(station, address);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Station non trouvée pour suppression");
        }
    }
}
