package org.example.safetynet_alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.safetynet_alerts.models.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Service class responsible for managing person data. It allows retrieving, adding, updating, and deleting
 * person records, as well as filtering persons by various attributes like email, city, last name, or address.
 */
@Service
public class PersonService {

    private static final Logger logger = LogManager.getLogger(PersonService.class); // Logger initialization
    private List<Person> personList;
    private final ObjectMapper objectMapper; // ObjectMapper injected via constructor

    /**
     * Constructor that initializes the PersonService with an injected ObjectMapper.
     * It loads the person data from the `data.json` file during initialization.
     *
     * @param objectMapper The ObjectMapper instance injected by Spring.
     * @throws IllegalArgumentException if there is an error loading the person data from the JSON file.
     */
    public PersonService(
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
        try {
            loadPersonList();
        } catch (Exception e) {
            logger.error("Error loading JSON data: {}", e.getMessage());
            throw new IllegalArgumentException("Unable to load JSON data", e);
        }
    }

    /**
     * Loads the list of persons from the `data.json` file and maps it to a {@link PersonsData} object.
     *
     * @throws IOException if there is an error reading the file or parsing the data.
     */
    private void loadPersonList() throws IOException {
        PersonsData data = objectMapper.readValue(
                new ClassPathResource("data.json").getInputStream(),
                PersonsData.class
        );
        personList = data.getPersons();
        logger.info("Data loaded: {}", personList.size());
    }

    /**
     * Retrieves the entire list of persons.
     *
     * @return a list of all persons.
     */
    public List<Person> getAllPersonList() {
        return personList;
    }

    /**
     * Retrieves a person by their email address.
     *
     * @param email the email address of the person to retrieve.
     * @return the {@link Person} object with the given email, or null if no person is found.
     */
    public Person getPersonListByEmail(String email) {
        return personList.stream()
                .filter(person -> Objects.equals(person.getEmail(), email))
                .findAny().orElse(null);
    }

    /**
     * Retrieves a list of emails for all persons living in a specific city.
     *
     * @param city the city where persons need to be identified.
     * @return a list of email addresses for persons living in the specified city.
     */
    public List<String> getAllEmailByCity(String city) {
        return personList.stream()
                .filter(person -> Objects.equals(person.getCity(), city))
                .map(Person::getEmail)
                .distinct()
                .toList();
    }

    /**
     * Retrieves a list of persons who share the same last name.
     *
     * @param lastname the last name of the persons to retrieve.
     * @return a list of persons with the given last name.
     */
    public List<Person> getAllPersonByLastname(String lastname) {
        return personList.stream()
                .filter(person -> Objects.equals(person.getLastName(), lastname))
                .toList();
    }

    /**
     * Retrieves a list of persons who live at a specific address.
     *
     * @param address the address where persons need to be identified.
     * @return a list of persons living at the specified address.
     */
    public List<Person> getPersonsByAddress(String address) {
        return personList.stream()
                .filter(person -> Objects.equals(person.getAddress(), address))
                .toList();
    }

    /**
     * Retrieves a list of distinct phone numbers for a list of persons.
     *
     * @param persons the list of persons whose phone numbers are to be retrieved.
     * @return a list of distinct phone numbers for the given persons.
     */
    public List<String> getAllPhoneByPersons(List<Person> persons) {
        if (persons.isEmpty()) {
            return Collections.emptyList();
        }

        return personList.stream()
                .map(Person::getPhone)
                .distinct()
                .toList();
    }

    /**
     * Adds a new person to the list if no person with the same email already exists.
     *
     * @param person the {@link Person} object to add.
     * @return true if the person was added successfully, false if a person with the same email already exists.
     */
    public boolean addPerson(Person person) {
        boolean exists = personList.stream()
                .anyMatch(existing -> existing.getEmail().equals(person.getEmail()));

        if (exists) {
            logger.error("A person with this email already exists: {}", person);
            return false;
        }

        personList.add(person);
        logger.info("Person added: {}", person);
        return true;
    }

    /**
     * Updates the information of a person identified by their email address.
     *
     * @param email        the email address of the person to update.
     * @param updatedPerson the {@link Person} object containing the updated information.
     * @return the updated {@link Person} object.
     * @throws IllegalArgumentException if no person is found with the given email.
     */
    public Person updatePerson(String email, Person updatedPerson) {
        for (int i = 0; i < personList.size(); i++) {
            if (personList.get(i).getEmail().equals(email)) {
                personList.set(i, updatedPerson);
                logger.info("Person updated: {}", updatedPerson);
                return personList.get(i);
            }
        }
        throw new IllegalArgumentException("Person not found for email: " + email);
    }

    /**
     * Deletes a person identified by their email address.
     *
     * @param email the email address of the person to delete.
     * @return true if the person was deleted successfully, false if no person with the given email was found.
     */
    public boolean deletePerson(String email) {
        boolean removed = personList.removeIf(person -> Objects.equals(person.getEmail(), email));
        if (removed) {
            logger.info("Person deleted for email: {}", email);
        } else {
            logger.warn("No person found for email: {}", email);
        }
        return removed;
    }
}
