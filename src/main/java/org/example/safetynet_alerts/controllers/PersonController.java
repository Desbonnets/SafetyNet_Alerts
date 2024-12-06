package org.example.safetynet_alerts.controllers;

import org.example.safetynet_alerts.models.FireStation;
import org.example.safetynet_alerts.models.Person;
import org.example.safetynet_alerts.service.FireStationService;
import org.example.safetynet_alerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    @GetMapping("")
    public ResponseEntity<List<Person>> getAllPersonInfo() {
        List<Person> personList = personService.getAllPersonList();
        if (personList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        return ResponseEntity.ok(personList);
    }

    @GetMapping("/{email}")
    public ResponseEntity<Person> getPersonInfo(@PathVariable String email) {
        Person person = personService.getPersonListByEmail(email);
        if (person == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        return ResponseEntity.ok(person);
    }

    @PostMapping("")
    public ResponseEntity<Person> postPerson(@RequestBody Person person) {
        boolean isSuccess = personService.addPerson(person);
        if (isSuccess) {
            URI location = URI.create("/person/" + person.getEmail()); // Remplacer par un identifiant si nécessaire
            return ResponseEntity.created(location).body(person);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erreur d'enregistrement");
        }
    }

    @PutMapping("/{email}")
    public ResponseEntity<Person> putPersonInfo(@PathVariable String email, @RequestBody Person updatedPerson) {
        Person person = personService.updatePerson(email, updatedPerson);
        if (person != null) {
            return ResponseEntity.ok(person);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email non trouvée pour mise à jour");
        }
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deletePersonInfo(@PathVariable String email) {
        boolean isDeleted = personService.deletePerson(email);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email non trouvée pour suppression");
        }
    }
}
