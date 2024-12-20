package org.example.safetynet_alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.safetynet_alerts.models.MedicalRecord;
import org.example.safetynet_alerts.models.Person;
import org.example.safetynet_alerts.models.PersonInfoDTO;
import org.example.safetynet_alerts.models.PersonsData;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PersonInfoService {

    private static final Logger logger = LogManager.getLogger(PersonInfoService.class); // Initialisation correcte du Logger
    private List<Person> personList;
    private final ObjectMapper objectMapper; // Injection de l'ObjectMapper via le constructeur
    private final MedicalRecordService medicalRecordService;
    private final FireStationService fireStationService;

    // Injection de l'ObjectMapper par Spring
    public PersonInfoService(
            ObjectMapper objectMapper,
            MedicalRecordService medicalRecordService,
            FireStationService fireStationService
    ) {
        this.objectMapper = objectMapper; // Assignation de l'ObjectMapper injecté
        this.medicalRecordService = medicalRecordService;
        this.fireStationService = fireStationService;
        try {
            loadPersonList();
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des données JSON : {}", e.getMessage());
            throw new IllegalArgumentException("Impossible de charger les données JSON", e);
        }
    }

    private void loadPersonList() throws IOException {
        // Lire le fichier JSON et le mapper sur la classe PersonsData
        PersonsData data = objectMapper.readValue(
                new ClassPathResource("data.json").getInputStream(),
                PersonsData.class
        );

        // Extraire la liste des stations de la structure racine
        personList = data.getPersonList();
        logger.info("Données chargées : {}", personList.size());
    }

    public List<PersonInfoDTO> getAllPersonInfoDTO(List<Person> persons) {

        if (persons.isEmpty()) {
            return Collections.emptyList();
        }

        return persons.stream()
                .map(person -> {
                    MedicalRecord medicalRecord = medicalRecordService.getMedicalRecordByFirstnameAndLastname(
                            person.getFirstName(), person.getLastName());
                    int age = (medicalRecord != null && medicalRecord.getBirthDate() != null)
                            ? DateUtils.calculateAge(medicalRecord.getBirthDate())
                            : 0; // 0 ou une autre valeur par défaut si la date de naissance est manquante
                    return new PersonInfoDTO(
                            person.getFirstName(),
                            person.getLastName(),
                            person.getAddress(),
                            age,
                            person.getPhone(),
                            person.getEmail(),
                            medicalRecord != null ? medicalRecord.getMedications() : Collections.emptyList(),
                            medicalRecord != null ? medicalRecord.getAllergies() : Collections.emptyList()
                    );
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getChildAlertByAddress(String address) {
        // Filtrer les enfants vivant à l'adresse donnée
        List<Person> children = personList.stream()
                .filter(person -> Objects.equals(person.getAddress(), address) &&
                        // Vérifier que l'âge est inférieur ou égal à 18 en accédant au MedicalRecord de chaque personne
                        DateUtils.calculateAge(medicalRecordService.getMedicalRecordByFirstnameAndLastname(person.getFirstName(), person.getLastName()).getBirthDate()) <= 18)
                .collect(Collectors.toList());

        // Si aucun enfant n'est trouvé, retourner une liste vide
        if (children.isEmpty()) {
            return Collections.emptyList();
        }

        // Construire la liste des informations des enfants
        return children.stream()
                .map(child -> {
                    // Récupérer les autres membres du foyer vivant à la même adresse
                    List<Person> familyMembers = personList.stream()
                            .filter(person -> Objects.equals(person.getAddress(), address) && !person.equals(child)) // Exclure l'enfant actuel
                            .collect(Collectors.toList());

                    // Construire l'objet contenant le prénom, le nom, l'âge et la liste des autres membres du foyer
                    return Map.of(
                            "firstName", child.getFirstName(),
                            "lastName", child.getLastName(),
                            "age", DateUtils.calculateAge(medicalRecordService.getMedicalRecordByFirstnameAndLastname(child.getFirstName(), child.getLastName()).getBirthDate()),
                            "familyMembers", familyMembers.stream()
                                    .map(familyMember -> familyMember.getFirstName() + " " + familyMember.getLastName()) // Liste des noms des membres
                                    .collect(Collectors.toList())
                    );
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> getCoverageByFireStation(int stationNumber) {
        // Récupérer les adresses desservies par la station donnée
        String address = fireStationService.getAddressByFireStationsNumber(stationNumber);

        // Filtrer les personnes vivant à ces adresses
        List<Person> coveredPersons = personList.stream()
                .filter(person -> Objects.equals(address, person.getAddress()))  // Vérifier si l'adresse de la personne est desservie par la station
                .collect(Collectors.toList());

        // Calculer le nombre d'adultes et d'enfants
        long adultsCount = coveredPersons.stream()
                .filter(person -> DateUtils.calculateAge(medicalRecordService.getMedicalRecordByFirstnameAndLastname(person.getFirstName(), person.getLastName()).getBirthDate()) > 18)
                .count();
        long childrenCount = coveredPersons.size() - adultsCount;

        // Construire la liste des informations des personnes couvertes par la caserne
        List<Map<String, String>> personDetails = coveredPersons.stream()
                .map(person -> Map.of(
                        "firstName", person.getFirstName(),
                        "lastName", person.getLastName(),
                        "address", person.getAddress(),
                        "phoneNumber", person.getPhone()
                ))
                .collect(Collectors.toList());

        // Retourner les informations au format attendu
        Map<String, Object> response = new HashMap<>();
        response.put("persons", personDetails);
        response.put("adultCount", adultsCount);
        response.put("childrenCount", childrenCount);

        return response;
    }

}
