package org.example.safetynet_alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.safetynet_alerts.models.MedicalRecord;
import org.example.safetynet_alerts.models.Person;
import org.example.safetynet_alerts.models.PersonsData;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * Service class responsible for retrieving and managing information related to persons,
 * including medical records and fire station coverage. It provides methods for fetching
 * personal information, child alerts, and fire station coverage data.
 */
@Service
public class PersonInfoService {

    private static final Logger logger = LogManager.getLogger(PersonInfoService.class); // Logger initialization
    private List<Person> personList;
    private final ObjectMapper objectMapper; // ObjectMapper injected via constructor
    private final MedicalRecordService medicalRecordService;
    private final FireStationService fireStationService;

    /**
     * Constructor that initializes the PersonInfoService with injected dependencies for ObjectMapper,
     * MedicalRecordService, and FireStationService. It also loads the person data from the JSON file.
     *
     * @param objectMapper        The ObjectMapper instance injected by Spring.
     * @param medicalRecordService The service for accessing medical records.
     * @param fireStationService   The service for accessing fire station data.
     * @throws IllegalArgumentException if there is an error loading the person data.
     */
    public PersonInfoService(
            ObjectMapper objectMapper,
            MedicalRecordService medicalRecordService,
            FireStationService fireStationService
    ) {
        this.objectMapper = objectMapper;
        this.medicalRecordService = medicalRecordService;
        this.fireStationService = fireStationService;
        try {
            loadPersonList();
        } catch (Exception e) {
            logger.error("Error loading JSON data: {}", e.getMessage());
            throw new IllegalArgumentException("Unable to load JSON data", e);
        }
    }

    /**
     * Loads the list of persons from the `data.json` file.
     * The JSON data is parsed and mapped into a {@link PersonsData} object.
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
     * Retrieves information about a list of persons, including personal details and medical information.
     *
     * @param persons A list of {@link Person} objects whose information is to be retrieved.
     * @return a list of maps containing personal information, age, medications, and allergies for each person.
     */
    public List<Map<String, Object>> getAllPersonInfo(List<Person> persons) {
        return persons.stream()
                .map(person -> {
                    MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordByFirstnameAndLastname(
                            person.getFirstName(), person.getLastName());
                    int age = (medicalRecord != null && medicalRecord.getBirthDate() != null)
                            ? DateUtils.calculateAge(medicalRecord.getBirthDate())
                            : 0; // Default value if birthdate is missing

                    return Map.of(
                            "firstName", person.getFirstName(),
                            "lastName", person.getLastName(),
                            "address", person.getAddress(),
                            "age", age,
                            "phone", person.getPhone(),
                            "email", person.getEmail(),
                            "medications", medicalRecord != null ? medicalRecord.getMedications() : Collections.emptyList(),
                            "allergies", medicalRecord != null ? medicalRecord.getAllergies() : Collections.emptyList()
                    );
                })
                .toList();
    }

    /**
     * Retrieves a list of children living at a specific address.
     * It filters persons based on age and address, ensuring that only children (age <= 18) are included.
     *
     * @param address The address where children need to be identified.
     * @return a list of maps containing information about each child and their family members.
     */
    public List<Map<String, Object>> getChildAlertByAddress(String address) {
        List<Person> children = personList.stream()
                .filter(person -> Objects.equals(person.getAddress(), address) &&
                        DateUtils.calculateAge(medicalRecordService.getMedicalRecordByFirstnameAndLastname(person.getFirstName(), person.getLastName()).getBirthDate()) <= 18)
                .toList();

        if (children.isEmpty()) {
            return Collections.emptyList();
        }

        return children.stream()
                .map(child -> {
                    List<Person> familyMembers = personList.stream()
                            .filter(person -> Objects.equals(person.getAddress(), address) && !person.equals(child))
                            .toList();

                    return Map.of(
                            "firstName", child.getFirstName(),
                            "lastName", child.getLastName(),
                            "age", DateUtils.calculateAge(medicalRecordService.getMedicalRecordByFirstnameAndLastname(child.getFirstName(), child.getLastName()).getBirthDate()),
                            "familyMembers", familyMembers.stream()
                                    .map(familyMember -> familyMember.getFirstName() + " " + familyMember.getLastName())
                                    .toList()
                    );
                })
                .toList();
    }

    /**
     * Retrieves information about persons covered by a specific fire station, identified by its station number.
     * It calculates the number of adults and children covered by the station and returns detailed information about them.
     *
     * @param stationNumber The number of the fire station whose coverage is to be checked.
     * @return a map containing information about the persons covered by the fire station,
     *         including their names, addresses, phone numbers, and the count of adults and children.
     */
    public Map<String, Object> getCoverageByFireStation(int stationNumber) {
        List<String> addresses = fireStationService.getAddressByFireStationsNumber(stationNumber);

        List<Person> coveredPersons = personList.stream()
                .filter(person -> addresses.contains(person.getAddress()))
                .toList();

        long adultsCount = coveredPersons.stream()
                .filter(person -> {
                    MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordByFirstnameAndLastname(
                            person.getFirstName(), person.getLastName());
                    logger.info("MedicalRecord BirthDate: {}", medicalRecord.getBirthDate());

                    int age = medicalRecord.getBirthDate() != null
                            ? DateUtils.calculateAge(medicalRecord.getBirthDate())
                            : -1;
                    if(age == -1){
                        throw new IllegalArgumentException("Birthdate cannot be null.");
                    }
                    return age > 18;
                })
                .count();
        logger.info("Adults count: {}", adultsCount);
        long childrenCount = coveredPersons.size() - adultsCount;

        List<Map<String, String>> personDetails = coveredPersons.stream()
                .map(person -> Map.of(
                        "firstName", person.getFirstName(),
                        "lastName", person.getLastName(),
                        "address", person.getAddress(),
                        "phoneNumber", person.getPhone()
                ))
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("persons", personDetails);
        response.put("adultCount", adultsCount);
        response.put("childrenCount", childrenCount);

        return response;
    }

}
