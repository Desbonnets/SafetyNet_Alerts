package org.example.safetynet_alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.safetynet_alerts.models.Person;
import org.example.safetynet_alerts.models.PersonsData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PersonServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    private PersonService personService;

    private List<Person> mockPersons;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Mock data for Person
        mockPersons = new ArrayList<>();
        mockPersons.add(new Person(
                "John",
                "Doe",
                "123 Main St",
                "City",
                12345,
                "555-1234",
                "john.doe@example.com"
        ));
        mockPersons.add(new Person(
                "Jane",
                "Smith",
                "456 Elm St",
                "Town",
                67890,
                "555-5678",
                "jane.smith@example.com"
        ));

        // Mocking the ObjectMapper to return the mock data
        PersonsData mockData = new PersonsData();
        mockData.setPersonList(mockPersons);
        when(objectMapper.readValue(any(InputStream.class), eq(PersonsData.class))).thenReturn(mockData);

        // Initialize the service
        personService = new PersonService(objectMapper);
    }

    @Test
    void getAllPersonList_ShouldReturnAllPersons() {
        List<Person> persons = personService.getAllPersonList();
        assertEquals(2, persons.size());
        assertEquals("John", persons.get(0).getFirstName());
    }

    @Test
    void getPersonListByEmail_ShouldReturnCorrectPerson() {
        Person person = personService.getPersonListByEmail("john.doe@example.com");
        assertNotNull(person);
        assertEquals("John", person.getFirstName());
        assertEquals("Doe", person.getLastName());
    }

    @Test
    void getPersonListByEmail_ShouldReturnNullIfNotFound() {
        Person person = personService.getPersonListByEmail("nonexistent@example.com");
        assertNull(person);
    }

    @Test
    void addPerson_ShouldAddNewPerson() {
        Person newPerson = new Person(
                "Alice",
                "Wonder",
                "789 Oak St",
                "Village",
                11223,
                "555-7890",
                "alice.wonder@example.com"
        );
        boolean isAdded = personService.addPerson(newPerson);
        assertTrue(isAdded);
        assertEquals(3, personService.getAllPersonList().size());
    }

    @Test
    void addPerson_ShouldNotAddDuplicatePerson() {
        Person duplicatePerson = new Person(
                "John",
                "Doe",
                "123 Main St",
                "City",
                12345,
                "555-1234",
                "john.doe@example.com"
        );
        boolean isAdded = personService.addPerson(duplicatePerson);
        assertFalse(isAdded);
        assertEquals(2, personService.getAllPersonList().size());
    }

    @Test
    void updatePerson_ShouldUpdateExistingPerson() {
        Person updatedPerson = new Person(
                "John",
                "Doe",
                "123 Main St Updated",
                "City",
                12345,
                "555-0000",
                "john.doe@example.com"
        );
        Person result = personService.updatePerson("john.doe@example.com", updatedPerson);
        assertEquals("123 Main St Updated", result.getAddress());
        assertEquals("555-0000", result.getPhone());
    }

    @Test
    void updatePerson_ShouldThrowExceptionIfPersonNotFound() {
        Person updatedPerson = new Person(
                "Non",
                "Existent",
                "789 Oak St",
                "Village",
                11223,
                "555-7890",
                "non.existent@example.com"
        );
        assertThrows(IllegalArgumentException.class, () ->
                personService.updatePerson("nonexistent@example.com", updatedPerson));
    }

    @Test
    void deletePerson_ShouldRemoveExistingPerson() {
        boolean isDeleted = personService.deletePerson("john.doe@example.com");
        assertTrue(isDeleted);
        assertEquals(1, personService.getAllPersonList().size());
    }

    @Test
    void deletePerson_ShouldReturnFalseIfPersonNotFound() {
        boolean isDeleted = personService.deletePerson("nonexistent@example.com");
        assertFalse(isDeleted);
        assertEquals(2, personService.getAllPersonList().size());
    }
}
