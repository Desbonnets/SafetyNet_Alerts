package org.example.safetynet_alerts.controllers;

import org.example.safetynet_alerts.models.MedicalRecord;
import org.example.safetynet_alerts.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

/**
 * Controller that handles operations related to medical records, including retrieving, adding, updating, and deleting medical record information.
 */
@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    /**
     * Get all medical records in the system.
     *
     * @return a list of all medical records, or a 404 status if no records are found
     */
    @GetMapping("")
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecordInfo() {
        List<MedicalRecord> medicalRecordList = medicalRecordService.getAllMedicalRecordList();
        if (medicalRecordList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        return ResponseEntity.ok(medicalRecordList);
    }

    /**
     * Get a specific medical record by the person's first and last name.
     *
     * @param firstname the first name of the person whose medical record is to be retrieved
     * @param lastname the last name of the person whose medical record is to be retrieved
     * @return the medical record of the person, or a 404 status if no record is found
     */
    @GetMapping("/{firstname}/{lastname}")
    public ResponseEntity<MedicalRecord> getMedicalRecordInfo(@PathVariable String firstname, @PathVariable String lastname) {
        MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordByFirstnameAndLastname(firstname, lastname);
        if (medicalRecord == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        return ResponseEntity.ok(medicalRecord);
    }

    /**
     * Add a new medical record to the system.
     *
     * @param medicalRecord the medical record information to be added
     * @return the newly created medical record with a 201 status, or a 400 status if the creation failed
     */
    @PostMapping("")
    public ResponseEntity<MedicalRecord> postMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        boolean isSuccess = medicalRecordService.addMedicalRecord(medicalRecord);
        if (isSuccess) {
            URI location = URI.create("/medicalRecord/" + medicalRecord.getFirstName() + "/" + medicalRecord.getLastName()); // Replace with an identifier if necessary
            return ResponseEntity.created(location).body(medicalRecord);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration error");
        }
    }

    /**
     * Update an existing medical record by the person's first and last name.
     *
     * @param firstname the first name of the person whose medical record is to be updated
     * @param lastname the last name of the person whose medical record is to be updated
     * @param updatedMedicalRecord the updated medical record information
     * @return the updated medical record, or a 404 status if the record was not found
     */
    @PutMapping("/{firstname}/{lastname}")
    public ResponseEntity<MedicalRecord> putMedicalRecordInfo(@PathVariable String firstname, @PathVariable String lastname, @RequestBody MedicalRecord updatedMedicalRecord) {
        MedicalRecord medicalRecord = medicalRecordService.updateMedicalRecord(firstname, lastname, updatedMedicalRecord);
        if (medicalRecord != null) {
            return ResponseEntity.ok(medicalRecord);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found for update");
        }
    }

    /**
     * Delete a medical record by the person's first and last name.
     *
     * @param firstname the first name of the person whose medical record is to be deleted
     * @param lastname the last name of the person whose medical record is to be deleted
     * @return a 200 status if the deletion was successful, or a 404 status if the record was not found
     */
    @DeleteMapping("/{firstname}/{lastname}")
    public ResponseEntity<Void> deleteMedicalRecordInfo(@PathVariable String firstname, @PathVariable String lastname) {
        boolean isDeleted = medicalRecordService.deleteMedicalRecord(firstname, lastname);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found for deletion");
        }
    }
}
