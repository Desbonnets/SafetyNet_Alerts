package org.example.safetynet_alerts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.safetynet_alerts.models.FireStation;
import org.example.safetynet_alerts.models.FireStationsData;
import org.example.safetynet_alerts.models.Person;
import org.example.safetynet_alerts.models.PersonsData;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PersonService {

    private static final Logger logger = LogManager.getLogger(PersonService.class); // Initialisation correcte du Logger
    private List<Person> personList;
    private final ObjectMapper objectMapper; // Injection de l'ObjectMapper via le constructeur

    // Injection de l'ObjectMapper par Spring
    public PersonService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper; // Assignation de l'ObjectMapper injecté
        try {
            loadFireStations();
        } catch (Exception e) {
            logger.error("Erreur lors du chargement des données JSON : {}", e.getMessage());
            throw new IllegalArgumentException("Impossible de charger les données JSON", e);
        }
    }

    private void loadFireStations() throws IOException {
        // Lire le fichier JSON et le mapper sur la classe PersonsData
        PersonsData data = objectMapper.readValue(
                new ClassPathResource("data.json").getInputStream(),
                PersonsData.class
        );

        // Extraire la liste des stations de la structure racine
        personList = data.getPersonList();
        logger.info("Données chargées : {}", personList.size());
    }

    public List<Person> getAllPersonList() {
        return personList;
    }

    public List<Person> getPersonListByEmail(String email) {
        return personList.stream()
                .filter(person -> Objects.equals(person.getEmail(), email))
                .distinct()
                .collect(Collectors.toList());
    }

    public boolean addPerson(Person person) {
        boolean exists = personList.stream()
                .anyMatch(existing -> existing.getEmail().equals(person.getEmail()));

        if (exists) {
            logger.error("Une Person avec cette adresse et numéro de station existe déjà : {}", person);
            return false;
        }

        personList.add(person);
        logger.info("Person ajoutée : {}", person);
        return true;
    }

    public Person updatePerson(String email, Person updatedPerson) {
        for (int i = 0; i < personList.size(); i++) {
            if (personList.get(i).getEmail().equals(email)) {
                personList.set(i, updatedPerson);
                logger.info("Person modifier : {}", updatedPerson);
                return personList.get(i);
            }
        }
        throw new IllegalArgumentException("Person non trouvée pour l'email : "
                + email);
    }

    public boolean deletePerson(String email) {
        boolean removed = personList.removeIf(fireStation -> Objects.equals(fireStation.getEmail(), email));
        if (removed) {
            logger.info("Person supprimée pour l'email : {}", email);
        } else {
            logger.warn("Aucune Person trouvée pour l'email : {}", email);
        }
        return removed;
    }
}
