package org.example.safetynet_alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.safetynet_alerts.models.Person;
import org.example.safetynet_alerts.models.PersonsData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test class for {@link PersonService}.
 * This class validates the functionalities related to person management,
 * including adding, updating, deleting, and retrieving person data.
 */
class PersonServiceTest {

    @Mock
    private ObjectMapper objectMapper; // Mocked ObjectMapper for reading JSON data

    private PersonService personService; // Instance of PersonService under test

    private List<Person> mockPersons; // Mocked list of persons

    /**
     * Setup before each test.
     * Initializes mocks and loads simulated data for testing.
     *
     * @throws IOException if an error occurs while loading mock data.
     */
    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);

        // Mock data for Person
        mockPersons = new ArrayList<>();
        mockPersons.add(new Person("John", "Doe", "123 Main St", "City", 12345, "555-1234", "john.doe@example.com"));
        mockPersons.add(new Person("Jane", "Smith", "456 Elm St", "Town", 67890, "555-5678", "jane.smith@example.com"));

        // Mocking the ObjectMapper to return the mock data
        PersonsData mockData = new PersonsData();
        mockData.setPersons(mockPersons);
        when(objectMapper.readValue(any(InputStream.class), eq(PersonsData.class))).thenReturn(mockData);

        // Initialize the service
        personService = new PersonService(objectMapper);
    }

    /**
     * Tests retrieving the complete list of persons.
     * Verifies that all persons are returned correctly.
     */
    @Test
    void getAllPersonList_ShouldReturnAllPersons() {
        List<Person> persons = personService.getAllPersonList();
        assertEquals(2, persons.size());
        assertEquals("John", persons.get(0).getFirstName());
    }

    /**
     * Tests retrieving a person by their email.
     * Verifies that the correct person is returned when the email exists.
     */
    @Test
    void getPersonListByEmail_ShouldReturnCorrectPerson() {
        Person person = personService.getPersonListByEmail("john.doe@example.com");
        assertNotNull(person);
        assertEquals("John", person.getFirstName());
    }

    /**
     * Tests retrieving a person by email when the email does not exist.
     * Verifies that the result is null.
     */
    @Test
    void getPersonListByEmail_ShouldReturnNullIfNotFound() {
        Person person = personService.getPersonListByEmail("nonexistent@example.com");
        assertNull(person);
    }

    /**
     * Tests adding a new person to the list.
     * Verifies that the person is added and the list size increases.
     */
    @Test
    void addPerson_ShouldAddNewPerson() {
        Person newPerson = new Person("Alice", "Wonder", "789 Oak St", "Village", 11223, "555-7890", "alice.wonder@example.com");
        boolean isAdded = personService.addPerson(newPerson);
        assertTrue(isAdded);
        assertEquals(3, personService.getAllPersonList().size());
    }

    /**
     * Tests adding a duplicate person.
     * Verifies that duplicates are not added and the list size remains unchanged.
     */
    @Test
    void addPerson_ShouldNotAddDuplicatePerson() {
        Person duplicatePerson = new Person("John", "Doe", "123 Main St", "City", 12345, "555-1234", "john.doe@example.com");
        boolean isAdded = personService.addPerson(duplicatePerson);
        assertFalse(isAdded);
        assertEquals(2, personService.getAllPersonList().size());
    }

    /**
     * Tests updating an existing person's details.
     * Verifies that the details are updated correctly.
     */
    @Test
    void updatePerson_ShouldUpdateExistingPerson() {
        Person updatedPerson = new Person("John", "Doe", "123 Main St Updated", "City", 12345, "555-0000", "john.doe@example.com");
        Person result = personService.updatePerson("john.doe@example.com", updatedPerson);
        assertEquals("123 Main St Updated", result.getAddress());
        assertEquals("555-0000", result.getPhone());
    }

    /**
     * Tests updating a person who does not exist.
     * Verifies that an exception is thrown.
     */
    @Test
    void updatePerson_ShouldThrowExceptionIfPersonNotFound() {
        Person updatedPerson = new Person("Non", "Existent", "789 Oak St", "Village", 11223, "555-7890", "non.existent@example.com");
        assertThrows(IllegalArgumentException.class, () ->
                personService.updatePerson("nonexistent@example.com", updatedPerson));
    }

    /**
     * Tests deleting an existing person.
     * Verifies that the person is removed and the list size decreases.
     */
    @Test
    void deletePerson_ShouldRemoveExistingPerson() {
        boolean isDeleted = personService.deletePerson("john.doe@example.com");
        assertTrue(isDeleted);
        assertEquals(1, personService.getAllPersonList().size());
    }

    /**
     * Tests deleting a person who does not exist.
     * Verifies that the method returns false and the list size remains unchanged.
     */
    @Test
    void deletePerson_ShouldReturnFalseIfPersonNotFound() {
        boolean isDeleted = personService.deletePerson("nonexistent@example.com");
        assertFalse(isDeleted);
        assertEquals(2, personService.getAllPersonList().size());
    }

    /**
     * Tests retrieving all email addresses by city.
     * Verifies that the correct emails are returned for the given city.
     */
    @Test
    void getAllEmailByCity_ShouldReturnEmailsForGivenCity() {
        List<String> emails = personService.getAllEmailByCity("City");
        assertEquals(1, emails.size());
        assertEquals("john.doe@example.com", emails.get(0));
    }

    /**
     * Tests retrieving email addresses by a city that does not exist.
     * Verifies that the result is an empty list.
     */
    @Test
    void getAllEmailByCity_ShouldReturnEmptyListIfCityNotFound() {
        List<String> emails = personService.getAllEmailByCity("NonexistentCity");
        assertTrue(emails.isEmpty());
    }

    /**
     * Tests retrieving persons by their last name.
     * Verifies that persons with the given last name are returned correctly.
     */
    @Test
    void getAllPersonByLastname_ShouldReturnPersonsWithGivenLastname() {
        List<Person> persons = personService.getAllPersonByLastname("Doe");
        assertEquals(1, persons.size());
        assertEquals("John", persons.get(0).getFirstName());
    }

    /**
     * Tests retrieving persons by a last name that does not exist.
     * Verifies that the result is an empty list.
     */
    @Test
    void getAllPersonByLastname_ShouldReturnEmptyListIfLastnameNotFound() {
        List<Person> persons = personService.getAllPersonByLastname("Nonexistent");
        assertTrue(persons.isEmpty());
    }

    /**
     * Tests retrieving persons by their address.
     * Verifies that the correct persons are returned for the given address.
     */
    @Test
    void getPersonsByAddress_ShouldReturnPersonsForGivenAddress() {
        List<Person> persons = personService.getPersonsByAddress("123 Main St");
        assertEquals(1, persons.size());
        assertEquals("John", persons.get(0).getFirstName());
    }

    /**
     * Tests retrieving persons by an address that does not exist.
     * Verifies that the result is an empty list.
     */
    @Test
    void getPersonsByAddress_ShouldReturnEmptyListIfAddressNotFound() {
        List<Person> persons = personService.getPersonsByAddress("Nonexistent Address");
        assertTrue(persons.isEmpty());
    }

    /**
     * Tests retrieving all phone numbers from a list of persons.
     * Verifies that the correct phone numbers are returned.
     */
    @Test
    void getAllPhoneByPersons_ShouldReturnPhonesForGivenPersons() {
        List<String> phones = personService.getAllPhoneByPersons(mockPersons);
        assertEquals(2, phones.size());
        assertTrue(phones.contains("555-1234"));
    }

    /**
     * Tests retrieving phone numbers when the persons list is empty.
     * Verifies that the result is an empty list.
     */
    @Test
    void getAllPhoneByPersons_ShouldReturnEmptyListIfPersonsListIsEmpty() {
        List<String> phones = personService.getAllPhoneByPersons(new ArrayList<>());
        assertTrue(phones.isEmpty());
    }
}
