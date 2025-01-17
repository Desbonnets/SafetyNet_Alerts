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
import java.util.stream.Stream;

/**
 * Controller that handles API endpoints for community alerts and fire station related queries.
 */
@RestController
public class ApiController {

    @Autowired
    private PersonService personService;
    @Autowired
    private FireStationService fireStationService;
    @Autowired
    private PersonInfoService personInfoService;

    /**
     * Get a list of community email addresses for a given city.
     *
     * @param city the name of the city for which emails are to be fetched
     * @return a list of email addresses in the specified city, or an empty list if no emails are found
     */
    @GetMapping("/communityEmail")
    public ResponseEntity<List<String>> getCommunityEmail(@RequestParam String city) {
        List<String> emailList = personService.getAllEmailByCity(city);
        if (emailList == null || emailList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }
        return ResponseEntity.ok(emailList);
    }

    /**
     * Get detailed information about people by their last name.
     *
     * @param lastName the last name of the people whose information is to be fetched
     * @return a list of person details, or an empty list if no people with the specified last name are found
     */
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

    /**
     * Get information about stations affected by floods, given a list of fire station numbers.
     *
     * @param stations the list of fire station numbers to check for flood information
     * @return a list of fire stations with associated person information affected by floods, or an empty list if no data is found
     */
    @GetMapping("/flood/stations")
    public ResponseEntity<List<Map<String, Object>>> getStationsFlood(@RequestParam List<Integer> stations) {

        List<Map<String, Object>> result = stations.stream()
                .map(station -> {
                    List<String> addresses = fireStationService.getAddressByFireStationsNumber(station);
                    if (addresses == null || addresses.isEmpty()) {
                        return null;
                    }

                    List<Person> persons = addresses.stream()
                            .flatMap(address -> {
                                List<Person> personsAtAddress = personService.getPersonsByAddress(address);
                                return personsAtAddress != null ? personsAtAddress.stream() : Stream.empty();
                            })
                            .toList();

                    List<Map<String, Object>> personInfos = personInfoService.getAllPersonInfo(persons);

                    return Map.of(
                            "station", station,
                            "personInfos", personInfos
                    );
                })
                .filter(Objects::nonNull)
                .filter(map -> !((List<?>) map.get("personInfos")).isEmpty())
                .toList();

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Get fire station information for a given address.
     *
     * @param address the address for which fire station information is to be fetched
     * @return a list of fire station details for the address, or an empty list if no data is found
     */
    @GetMapping("/fire")
    public ResponseEntity<List<Map<String, Object>>> getFire(@RequestParam String address) {

        List<FireStation> fireStations = fireStationService.getFireStationByAddress(address);

        List<Map<String, Object>> result = fireStations.stream()
                .map(fireStation -> {
                    List<Person> persons = personService.getPersonsByAddress(address);
                    List<Map<String, Object>> personInfos = personInfoService.getAllPersonInfo(persons);

                    return Map.of(
                            "station", fireStation.getStation(),
                            "personInfos", personInfos
                    );
                })
                .filter(map -> !((List<?>) map.get("personInfos")).isEmpty())
                .toList();

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Get a list of phone numbers for a given fire station number.
     *
     * @param fireStationNumber the fire station number to get phone alerts for
     * @return a list of phone numbers associated with the fire station, or an empty list if no data is found
     */
    @GetMapping("/phoneAlert")
    public ResponseEntity<List<String>> getPhoneAlert(@RequestParam int fireStationNumber) {

        List<String> addresses = fireStationService.getAddressByFireStationsNumber(fireStationNumber);
        if (addresses == null || addresses.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }

        List<Person> persons = addresses.stream()
                .flatMap(address -> personService.getPersonsByAddress(address).stream())
                .toList();

        List<String> listPhone = personService.getAllPhoneByPersons(persons);

        if (listPhone == null || listPhone.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }

        return ResponseEntity.ok(listPhone);
    }

    /**
     * Get a list of children at a specific address.
     *
     * @param address the address for which child alerts are to be fetched
     * @return a list of children and their details at the given address, or an empty list if no children are found
     */
    @GetMapping("/childAlert")
    public ResponseEntity<List<Map<String, Object>>> getChildAlert(@RequestParam String address) {

        List<Map<String, Object>> result = personInfoService.getChildAlertByAddress(address);

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyList());
        }

        return ResponseEntity.ok(result);
    }

    /**
     * Get coverage information by fire station number.
     *
     * @param stationNumber the fire station number to get coverage details for
     * @return coverage information for the specified fire station, or an empty map if no data is found
     */
    @GetMapping("/firestation")
    public ResponseEntity<Map<String, Object>> getCoverageByFireStation(@RequestParam int stationNumber) {

        Map<String, Object> result = personInfoService.getCoverageByFireStation(stationNumber);

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.emptyMap());
        }

        return ResponseEntity.ok(result);
    }
}
