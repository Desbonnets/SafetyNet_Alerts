package org.example.safetynet_alerts.service;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class DateUtilsTest {

    @Test
    void calculateAge_ValidDate_ShouldReturnCorrectAge() {
        // Date correspondant à un âge de 25 ans
        String birthDate = LocalDate.now().minusYears(25).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        int age = DateUtils.calculateAge(birthDate);
        assertEquals(25, age, "L'âge calculé devrait être de 25 ans.");
    }

    @Test
    void calculateAge_BirthdayToday_ShouldReturnCorrectAge() {
        // Date correspondant à un anniversaire aujourd'hui
        String birthDate = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        int age = DateUtils.calculateAge(birthDate);
        assertEquals(0, age, "L'âge calculé devrait être 0 pour une naissance aujourd'hui.");
    }

    @Test
    void calculateAge_InvalidDateFormat_ShouldThrowException() {
        String invalidDate = "2022-01-01"; // Mauvais format
        Exception exception = assertThrows(IllegalArgumentException.class, () -> DateUtils.calculateAge(invalidDate));
        assertTrue(exception.getMessage().contains("Invalid date format"),
                "L'exception devrait contenir un message indiquant un format de date invalide.");
    }

    @Test
    void calculateAge_NullDate_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> DateUtils.calculateAge(null));
        assertEquals("La date de naissance ne peut pas être nulle.", exception.getMessage(),
                "L'exception devrait indiquer que la date de naissance ne peut pas être nulle.");
    }

    @Test
    void calculateAge_FutureDate_ShouldReturnNegativeAge() {
        // Date correspondant à une date future
        String futureDate = LocalDate.now().plusYears(5).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        int age = DateUtils.calculateAge(futureDate);
        assertTrue(age < 0, "L'âge calculé pour une date future devrait être négatif.");
    }

    @Test
    void parseDate_ValidDate_ShouldReturnCorrectDate() {
        // Test de parseDate indirectement via calculateAge
        String validDate = "01/01/2000";
        int age = DateUtils.calculateAge(validDate);
        assertEquals(LocalDate.now().getYear() - 2000, age, "L'âge calculé devrait correspondre à la date fournie.");
    }
}
