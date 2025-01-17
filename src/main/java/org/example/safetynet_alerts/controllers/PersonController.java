package org.example.safetynet_alerts.controllers;

import org.example.safetynet_alerts.models.Person;
import org.example.safetynet_alerts.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

/**
 * Controller that handles operations related to persons, including retrieving, adding, updating, and deleting person information.
 */
@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    private PersonService personService;

    /**
     * Get all persons in the system.
     *
     * @return a list of all persons, or a 404 status if no persons are found
     */
    @GetMapping("")
    public ResponseEntity<List<Person>> getAllPersonInfo() {
        List<Person> personList = personService.getAllPersonList();
        if (personList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        return ResponseEntity.ok(personList);
    }

    /**
     * Get a specific person by their email.
     *
     * @param email the email of the person whose information is to be retrieved
     * @return the person with the specified email, or a 404 status if no person is found
     */
    @GetMapping("/{email}")
    public ResponseEntity<Person> getPersonInfo(@PathVariable String email) {
        Person person = personService.getPersonListByEmail(email);
        if (person == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
        return ResponseEntity.ok(person);
    }

    /**
     * Add a new person to the system.
     *
     * @param person the person information to be added
     * @return the newly created person with a 201 status, or a 400 status if the creation failed
     */
    @PostMapping("")
    public ResponseEntity<Person> postPerson(@RequestBody Person person) {
        boolean isSuccess = personService.addPerson(person);
        if (isSuccess) {
            URI location = URI.create("/person/" + person.getEmail()); // Replace with an identifier if necessary
            return ResponseEntity.created(location).body(person);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration error");
        }
    }

    /**
     * Update an existing person's information by their email.
     *
     * @param email the email of the person whose information is to be updated
     * @param updatedPerson the updated person information
     * @return the updated person, or a 404 status if the person was not found
     */
    @PutMapping("/{email}")
    public ResponseEntity<Person> putPersonInfo(@PathVariable String email, @RequestBody Person updatedPerson) {
        Person person = personService.updatePerson(email, updatedPerson);
        if (person != null) {
            return ResponseEntity.ok(person);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found for update");
        }
    }

    /**
     * Delete a person by their email.
     *
     * @param email the email of the person whose information is to be deleted
     * @return a 200 status if the deletion was successful, or a 404 status if the person was not found
     */
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deletePersonInfo(@PathVariable String email) {
        boolean isDeleted = personService.deletePerson(email);
        if (isDeleted) {
            return ResponseEntity.ok().build();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Email not found for deletion");
        }
    }
}
