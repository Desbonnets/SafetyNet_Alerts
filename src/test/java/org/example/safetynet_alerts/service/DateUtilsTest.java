package org.example.safetynet_alerts.service;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test class for {@link DateUtils}.
 * This class tests various scenarios for the {@code calculateAge} and related date utilities.
 */
class DateUtilsTest {

    /**
     * Tests that the private constructor of {@link DateUtils} throws an {@link IllegalStateException}.
     */
    @Test
    void constructor_ShouldThrowException() {
        try {
            // Access the private constructor
            Constructor<DateUtils> constructor = DateUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true); // Make it accessible

            // Verify that invoking the constructor throws an exception
            InvocationTargetException exception = assertThrows(InvocationTargetException.class, constructor::newInstance,
                    "Invoking the private constructor should throw an InvocationTargetException");

            // Verify the cause of the exception is IllegalStateException
            Throwable cause = exception.getCause();
            assertNotNull(cause, "The cause of the exception should not be null.");
            assertTrue(cause instanceof IllegalStateException, "The cause should be an IllegalStateException.");
            assertEquals("Utility class", cause.getMessage(), "The exception message should be 'Utility class'.");
        } catch (NoSuchMethodException e) {
            fail("No such constructor found: " + e.getMessage());
        }
    }

    /**
     * Tests {@link DateUtils#calculateAge(String)} with a valid date string.
     * Ensures the method calculates the correct age.
     */
    @Test
    void calculateAge_ValidDate_ShouldReturnCorrectAge() {
        String birthDate = LocalDate.now().minusYears(25).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        int age = DateUtils.calculateAge(birthDate);
        assertEquals(25, age, "The calculated age should be 25 years.");
    }

    /**
     * Tests {@link DateUtils#calculateAge(String)} with a birth date corresponding to today's date.
     * Verifies the age is calculated as 0.
     */
    @Test
    void calculateAge_BirthdayToday_ShouldReturnCorrectAge() {
        String birthDate = LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        int age = DateUtils.calculateAge(birthDate);
        assertEquals(0, age, "The calculated age should be 0 for a birth date of today.");
    }

    /**
     * Tests {@link DateUtils#calculateAge(String)} with an invalid date format.
     * Verifies that an {@link IllegalArgumentException} is thrown.
     */
    @Test
    void calculateAge_InvalidDateFormat_ShouldThrowException() {
        String invalidDate = "2022-01-01"; // Incorrect format
        Exception exception = assertThrows(IllegalArgumentException.class, () -> DateUtils.calculateAge(invalidDate));
        assertTrue(exception.getMessage().contains("Invalid date format"),
                "The exception should indicate an invalid date format.");
    }

    /**
     * Tests {@link DateUtils#calculateAge(String)} with a null date.
     * Ensures that an {@link IllegalArgumentException} is thrown with an appropriate message.
     */
    @Test
    void calculateAge_NullDate_ShouldThrowException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> DateUtils.calculateAge(null));
        assertEquals("The birth date cannot be null.", exception.getMessage(),
                "The exception message should indicate that the birth date cannot be null.");
    }

    /**
     * Tests {@link DateUtils#calculateAge(String)} with a future birth date.
     * Verifies that the method returns a negative age.
     */
    @Test
    void calculateAge_FutureDate_ShouldReturnNegativeAge() {
        String futureDate = LocalDate.now().plusYears(5).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        int age = DateUtils.calculateAge(futureDate);
        assertTrue(age < 0, "The calculated age for a future date should be negative.");
    }

    /**
     * Tests {@link DateUtils#calculateAge(String)} indirectly by parsing a valid date.
     * Verifies the correct age is calculated for the given date string.
     */
    @Test
    void parseDate_ValidDate_ShouldReturnCorrectDate() {
        String validDate = "01/01/2000";
        int age = DateUtils.calculateAge(validDate);
        assertEquals(LocalDate.now().getYear() - 2000, age,
                "The calculated age should match the provided date.");
    }
}
