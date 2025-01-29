package org.example.safetynet_alerts.controllers;

import org.example.safetynet_alerts.models.Person;
import org.example.safetynet_alerts.service.PersonService;
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

/**
 * Unit tests for the PersonController class.
 * This test class uses Mockito to mock dependencies (PersonService) and tests the controller's behavior.
 * It ensures that each endpoint in the PersonController behaves correctly according to the service's responses.
 */
class PersonControllerTest {

    @Mock
    private PersonService personService; // Mock for PersonService

    @InjectMocks
    private PersonController personController; // PersonController under test

    /**
     * Initializes mocks before each test.
     * This method is called before each test to set up the mock objects.
     */
    public PersonControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Creates a mock person with default data for testing.
     * @return a Person object with mock data
     */
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
     * Tests the retrieval of a list of all persons when persons are found.
     * Verifies that the status code is 200 and the returned data is correct.
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
     * Tests the retrieval of a list of persons when the list is empty.
     * Verifies that the status code is 404 and the response body is null.
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
     * Tests the retrieval of a person's information when the person is found.
     * Verifies that the status code and the returned data are correct.
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
     * Tests the retrieval of a person's information when the person is not found.
     * Verifies that the status code is 404 and the response body is null.
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
     * Tests the successful addition of a new person.
     * Verifies that the status code is 201 and the person is correctly added.
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
     * Tests the failure scenario for adding a person.
     * Verifies that an exception with status 400 is thrown when the addition fails.
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
        assertEquals("400 BAD_REQUEST \"Registration error\"", exception.getMessage());
        verify(personService, times(1)).addPerson(newPerson);
    }

    /**
     * Tests the successful update of a person's information.
     * Verifies that the status code is 200 and the updated data is correct.
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
     * Tests the failure scenario for updating a person's information when the person is not found.
     * Verifies that an exception with status 404 is thrown.
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
        assertEquals("404 NOT_FOUND \"Email not found for update\"", exception.getMessage());
        verify(personService, times(1)).updatePerson("john.doe@example.com", updatedPerson);
    }

    /**
     * Tests the successful deletion of a person.
     * Verifies that the status code is 200 when the deletion is successful.
     */
    @Test
    void deletePersonInfo_success() {
        when(personService.deletePerson("john.doe@example.com")).thenReturn(true);

        ResponseEntity<Void> response = personController.deletePersonInfo("john.doe@example.com");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(personService, times(1)).deletePerson("john.doe@example.com");
    }

    /**
     * Tests the failure scenario for deleting a person when the person is not found.
     * Verifies that an exception with status 404 is thrown.
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
        assertEquals("404 NOT_FOUND \"Email not found for deletion\"", exception.getMessage());
        verify(personService, times(1)).deletePerson("john.doe@example.com");
    }
}
