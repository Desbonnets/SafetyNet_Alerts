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

/**
 * Controller that manages operations related to fire stations, including retrieving, adding, updating, and deleting fire station information.
 */
@RestController
@RequestMapping("/firestation")
public class FireStationController {

    @Autowired
    private FireStationService fireStationService;

    /**
     * Get information about a fire station by its number.
     *
     * @param stationNumber the number of the fire station whose information is to be retrieved
     * @return a list of fire station information, or a 404 status if no station is found
     */
    @GetMapping("/{stationNumber}")
    public ResponseEntity<List<FireStation>> getFirestationInfo(@PathVariable int stationNumber) {
        List<FireStation> fireStations = fireStationService.getFireStationByNumber(stationNumber);
        if (fireStations.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        return ResponseEntity.ok(fireStations);
    }

    /**
     * Add a new fire station to the system.
     *
     * @param fireStation the fire station information to be added
     * @return the newly created fire station with a 201 status, or a 400 status if the creation failed
     */
    @PostMapping("")
    public ResponseEntity<FireStation> postFirestation(@RequestBody FireStation fireStation) {
        boolean isSuccess = fireStationService.addFireStation(fireStation);
        if (isSuccess) {
            URI location = URI.create("/firestation/" + fireStation.getStation()); // Replace with an identifier if necessary
            return ResponseEntity.created(location).body(fireStation);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration error");
        }
    }

    /**
     * Update information for a specific fire station by address and station number.
     *
     * @param address the address of the fire station to be updated
     * @param station the number of the fire station to be updated
     * @param updatedFireStation the updated fire station information
     * @return the updated fire station information, or a 404 status if the station was not found
     */
    @PutMapping("/{address}/{station}")
    public ResponseEntity<FireStation> putFirestationInfo(@PathVariable String address, @PathVariable int station, @RequestBody FireStation updatedFireStation) {
        FireStation fireStation = fireStationService.updateFireStation(address, station, updatedFireStation);
        if (fireStation != null) {
            return ResponseEntity.ok(fireStation);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found for update");
        }
    }

    /**
     * Delete a fire station by its address and station number.
     *
     * @param address the address of the fire station to be deleted
     * @param station the number of the fire station to be deleted
     * @return a 200 status if the deletion was successful, or a 404 status if the station was not found
     */
    @DeleteMapping("/{address}/{station}")
    public ResponseEntity<Void> deleteFirestationInfo(@PathVariable String address, @PathVariable int station) {
        boolean isDeleted = fireStationService.deleteFireStation(station, address);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Station not found for deletion");
        }
    }
}
