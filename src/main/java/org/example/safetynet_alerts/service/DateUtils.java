package org.example.safetynet_alerts.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

/**
 * Utility class for handling date-related operations.
 * Provides methods for calculating age based on a given birthdate and other date-related utility functions.
 */
public class DateUtils {

    private DateUtils() {
        throw new IllegalStateException("Utility class");
    }
    /**
     * Calculates the age of a person based on their birthdate.
     * The birthdate should be provided in the format "dd/MM/yyyy".
     *
     * @param birthDate The birthdate of the person as a String in the format "dd/MM/yyyy".
     * @return The calculated age of the person in years.
     * @throws IllegalArgumentException if the birthDate is null or if the date format is invalid.
     */
    public static int calculateAge(String birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("The birth date cannot be null.");
        }

        Date date = parseDate(birthDate);

        // Convert java.util.Date into java.time.LocalDate
        LocalDate birthLocalDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // Calculate age in years
        return Period.between(birthLocalDate, LocalDate.now()).getYears();
    }

    /**
     * Parses a birthdate string into a {@link Date} object.
     * The expected format for the birthdate string is "dd/MM/yyyy".
     *
     * @param birthDate The birthdate string to be parsed.
     * @return The corresponding {@link Date} object.
     * @throws IllegalArgumentException if the date format is invalid.
     */
    private static Date parseDate(String birthDate) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            return format.parse(birthDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: dd/MM/yyyy. Provided: " + birthDate, e);
        }
    }
}
