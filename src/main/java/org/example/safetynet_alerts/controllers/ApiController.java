package org.example.safetynet_alerts.controllers;

import org.example.safetynet_alerts.models.FireStation;
import org.example.safetynet_alerts.models.Person;
import org.example.safetynet_alerts.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class ApiController {

    @Autowired
    private PersonService personService;
    @Autowired
    private MedicalRecordService medicalRecordService;
    @Autowired
    private FireStationService fireStationService;
    @Autowired
    private PersonInfoService personInfoService;

    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getCommunityEmail(@RequestParam String city) {
        List<String> emailList = personService.getAllEmailByCity(city);
        if (emailList == null || emailList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }
        return ResponseEntity.ok(emailList);
    }

    @GetMapping("/personInfo")
    public ResponseEntity<List<Map<String, Object>>> getPersonInfo(@RequestParam String lastName) {
        List<Person> persons = personService.getAllPersonByLastname(lastName);
        List<Map<String, Object>> personInfos = personInfoService.getAllPersonInfo(persons);

        if (personInfos == null || personInfos.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }

        return ResponseEntity.ok(personInfos);
    }

    @GetMapping("/flood/stations")
    public ResponseEntity<List<Map<String, Object>>> getStationsFlood(@RequestParam List<Integer> stations) {

        // Construire le résultat en traitant chaque station
        List<Map<String, Object>> result = stations.stream()
                .map(station -> {
                    // Récupérer les adresses pour la station
                    List<String> addresses = fireStationService.getAddressByFireStationsNumber(station);
                    if (addresses == null || addresses.isEmpty()) {
                        return null; // Ignorer les stations sans adresses
                    }

                    // Récupérer les personnes associées aux adresses
                    List<Person> persons = addresses.stream()
                            .flatMap(address -> {
                                List<Person> personsAtAddress = personService.getPersonsByAddress(address);
                                return personsAtAddress != null ? personsAtAddress.stream() : Stream.empty();
                            })
                            .collect(Collectors.toList());

                    // Transformer les personnes en informations détaillées
                    List<Map<String, Object>> personInfos = personInfoService.getAllPersonInfo(persons);

                    // Retourner les données pour cette station
                    return Map.of(
                            "station", station,
                            "personInfos", personInfos
                    );
                })
                .filter(Objects::nonNull) // Filtrer les stations sans données
                .filter(map -> !((List<?>) map.get("personInfos")).isEmpty()) // Filtrer les stations sans personnes
                .collect(Collectors.toList());

        // Si aucune donnée trouvée, retourner NOT_FOUND
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }

        // Retourner les résultats
        return ResponseEntity.ok(result);
    }


    @GetMapping("/fire")
    public ResponseEntity<List<Map<String, Object>>> getFire(@RequestParam String address) {

        List<FireStation> fireStations = fireStationService.getFireStationByAddress(address);

        List<Map<String, Object>> result = fireStations.stream()
                .map(fireStation -> {

                    // Récupérer les personnes associées aux adresses
                    List<Person> persons = personService.getPersonsByAddress(address);

                    // Transformer les personnes en DTOs
                    List<Map<String, Object>> personInfos = personInfoService.getAllPersonInfo(persons);

                    // Retourner les données pour cette station
                    return Map.of(
                            "station", fireStation.getStation(),
                            "personInfos", personInfos
                    );
                })
                .filter(map -> !((List<?>) map.get("personInfos")).isEmpty()) // Filtrer les stations sans personnes
                .collect(Collectors.toList());

        // Si aucune donnée trouvée, retourner NOT_FOUND
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }

        // Retourner les résultats
        return ResponseEntity.ok(result);
    }

    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhoneAlert(@RequestParam int fireStationNumber) {

        List<String> addresses = fireStationService.getAddressByFireStationsNumber(fireStationNumber);
        if (addresses == null || addresses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }

        // Récupérer les personnes vivant à ces adresses
        List<Person> persons = addresses.stream()
                .flatMap(address -> personService.getPersonsByAddress(address).stream())
                .collect(Collectors.toList());

        List<String> listPhone = personService.getAllPhoneByPersons(persons);

        if (listPhone == null || listPhone.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }

        return ResponseEntity.ok(listPhone);
    }

    @GetMapping("/childAlert")
    public ResponseEntity<List<Map<String, Object>>> getChildAlert(@RequestParam String address) {

        List<Map<String, Object>> result = personInfoService.getChildAlertByAddress(address);

        // Si aucune donnée trouvée, retourner NOT_FOUND
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/firestation")
    public ResponseEntity<Map<String, Object>> getCoverageByFireStation(@RequestParam int stationNumber) {

        Map<String, Object> result = personInfoService.getCoverageByFireStation(stationNumber);

        // Si aucune donnée trouvée, retourner NOT_FOUND
        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyMap());
        }

        return ResponseEntity.ok(result);
    }
}
