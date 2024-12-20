package org.example.safetynet_alerts.controllers;

import org.example.safetynet_alerts.controllers.PersonController;
import org.example.safetynet_alerts.models.Person;
import org.example.safetynet_alerts.service.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class PersonControllerTest {

    @Mock
    private PersonService personService; // Mock du service PersonService

    @InjectMocks
    private PersonController personController; // Contrôleur à tester

    public PersonControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    private Person createPerson() {
        return new Person(
                "John",
                "Doe",
                "1234 Main St",
                "City",
                12345,
                "john.doe@example.com",
                "321-555-9876");
    }

    /**
     * Teste la récupération de la liste de toutes les personnes trouvées.
     * Vérifie que le statut HTTP est 200 et les données retournées sont correctes.
     */
    @Test
    void getAllPersonInfo_found() {
        List<Person> mockPersons = List.of(createPerson());
        when(personService.getAllPersonList()).thenReturn(mockPersons);

        ResponseEntity<List<Person>> response = personController.getAllPersonInfo();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPersons, response.getBody());
        verify(personService, times(1)).getAllPersonList();
    }

    /**
     * Teste la récupération de la liste des personnes lorsque la liste est vide.
     * Vérifie que le statut HTTP est 404 et que le corps de la réponse est null.
     */
    @Test
    void getAllPersonInfo_notFound() {
        when(personService.getAllPersonList()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Person>> response = personController.getAllPersonInfo();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(personService, times(1)).getAllPersonList();
    }

    /**
     * Teste la récupération des informations d'une personne trouvée.
     * Vérifie que le statut HTTP et les données retournées sont corrects.
     */
    @Test
    void getPersonInfo_found() {
        Person mockPerson = createPerson();
        when(personService.getPersonListByEmail("john.doe@example.com")).thenReturn(mockPerson);

        ResponseEntity<Person> response = personController.getPersonInfo("john.doe@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPerson, response.getBody());
        verify(personService, times(1)).getPersonListByEmail("john.doe@example.com");
    }

    /**
     * Teste la récupération des informations d'une personne non trouvée.
     * Vérifie que le statut HTTP est 404 et que le corps de la réponse est null.
     */
    @Test
    void getPersonInfo_notFound() {
        when(personService.getPersonListByEmail("jane.doe@example.com")).thenReturn(null);

        ResponseEntity<Person> response = personController.getPersonInfo("jane.doe@example.com");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(personService, times(1)).getPersonListByEmail("jane.doe@example.com");
    }

    /**
     * Teste l'ajout d'une personne réussie.
     * Vérifie que le statut HTTP est 201 et que la personne est bien ajoutée.
     */
    @Test
    void postPerson_success() {
        Person newPerson = createPerson();
        when(personService.addPerson(newPerson)).thenReturn(true);

        ResponseEntity<Person> response = personController.postPerson(newPerson);

        URI expectedLocation = URI.create("/person/" + newPerson.getEmail());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(newPerson, response.getBody());
        assertEquals(expectedLocation, response.getHeaders().getLocation());
        verify(personService, times(1)).addPerson(newPerson);
    }

    /**
     * Teste l'échec de l'ajout d'une personne.
     * Vérifie qu'une exception avec un statut 400 est levée en cas d'échec.
     */
    @Test
    void postPerson_failure() {
        Person newPerson = createPerson();
        when(personService.addPerson(newPerson)).thenReturn(false);

        Exception exception = null;
        try {
            personController.postPerson(newPerson);
        } catch (Exception ex) {
            exception = ex;
        }

        assertTrue(exception instanceof ResponseStatusException);
        assertEquals("400 BAD_REQUEST \"Erreur d'enregistrement\"", exception.getMessage());
        verify(personService, times(1)).addPerson(newPerson);
    }

    /**
     * Teste la mise à jour réussie d'une personne.
     * Vérifie que le statut HTTP est 200 et que les données mises à jour sont correctes.
     */
    @Test
    void putPersonInfo_success() {
        Person updatedPerson = createPerson();
        updatedPerson.setCity("New City");

        when(personService.updatePerson("john.doe@example.com", updatedPerson)).thenReturn(updatedPerson);

        ResponseEntity<Person> response = personController.putPersonInfo("john.doe@example.com", updatedPerson);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedPerson, response.getBody());
        verify(personService, times(1)).updatePerson("john.doe@example.com", updatedPerson);
    }

    /**
     * Teste l'échec de la mise à jour d'une personne.
     * Vérifie qu'une exception avec un statut 404 est levée si la personne n'existe pas.
     */
    @Test
    void putPersonInfo_notFound() {
        Person updatedPerson = createPerson();
        when(personService.updatePerson("john.doe@example.com", updatedPerson)).thenReturn(null);

        Exception exception = null;
        try {
            personController.putPersonInfo("john.doe@example.com", updatedPerson);
        } catch (Exception ex) {
            exception = ex;
        }

        assertTrue(exception instanceof ResponseStatusException);
        assertEquals("404 NOT_FOUND \"Email non trouvée pour mise à jour\"", exception.getMessage());
        verify(personService, times(1)).updatePerson("john.doe@example.com", updatedPerson);
    }

    /**
     * Teste la suppression réussie d'une personne.
     * Vérifie que le statut HTTP est 200 en cas de suppression réussie.
     */
    @Test
    void deletePersonInfo_success() {
        when(personService.deletePerson("john.doe@example.com")).thenReturn(true);

        ResponseEntity<Void> response = personController.deletePersonInfo("john.doe@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(personService, times(1)).deletePerson("john.doe@example.com");
    }

    /**
     * Teste l'échec de la suppression d'une personne.
     * Vérifie qu'une exception avec un statut 404 est levée si la personne n'existe pas.
     */
    @Test
    void deletePersonInfo_notFound() {
        when(personService.deletePerson("john.doe@example.com")).thenReturn(false);

        Exception exception = null;
        try {
            personController.deletePersonInfo("john.doe@example.com");
        } catch (Exception ex) {
            exception = ex;
        }

        assertTrue(exception instanceof ResponseStatusException);
        assertEquals("404 NOT_FOUND \"Email non trouvée pour suppression\"", exception.getMessage());
        verify(personService, times(1)).deletePerson("john.doe@example.com");
    }
}

