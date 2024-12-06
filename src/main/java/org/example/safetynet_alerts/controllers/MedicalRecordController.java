package org.example.safetynet_alerts.controllers;

import org.example.safetynet_alerts.models.FireStation;
import org.example.safetynet_alerts.models.MedicalRecord;
import org.example.safetynet_alerts.service.FireStationService;
import org.example.safetynet_alerts.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/medicalRecord")
public class MedicalRecordController {

    @Autowired
    private MedicalRecordService medicalRecordService;

    @GetMapping("/")
    public ResponseEntity<List<MedicalRecord>> getMedicalRecordInfo() {
        List<MedicalRecord> medicalRecordList = medicalRecordService.getAllMedicalRecordList();
        if (medicalRecordList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        return ResponseEntity.ok(medicalRecordList);
    }

    @PostMapping("")
    public ResponseEntity<MedicalRecord> postMedicalRecord(@RequestBody MedicalRecord medicalRecord) {
        boolean isSuccess = medicalRecordService.addMedicalRecord(medicalRecord);
        if (isSuccess) {
            URI location = URI.create("/medicalRecord/" + medicalRecord.getFirstName() + "/" + medicalRecord.getLastName()); // Remplacer par un identifiant si nécessaire
            return ResponseEntity.created(location).body(medicalRecord);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erreur d'enregistrement");
        }
    }

    @PutMapping("/{firstname}/{lastname}")
    public ResponseEntity<MedicalRecord> putMedicalRecordInfo(@PathVariable String firstname, @PathVariable String lastname, @RequestBody MedicalRecord updatedMedicalRecord) {
        MedicalRecord medicalRecord = medicalRecordService.updateMedicalRecord(firstname, lastname, updatedMedicalRecord);
        if (medicalRecord != null) {
            return ResponseEntity.ok(medicalRecord);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MedicalRecord non trouvée pour mise à jour");
        }
    }

    @DeleteMapping("/{firstname}/{lastname}")
    public ResponseEntity<Void> deleteMedicalRecordInfo(@PathVariable String firstname, @PathVariable String lastname) {
        boolean isDeleted = medicalRecordService.deleteMedicalRecord(firstname, lastname);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "MedicalRecord non trouvée pour suppression");
        }
    }
}
