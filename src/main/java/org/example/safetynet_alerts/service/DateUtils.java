package org.example.safetynet_alerts.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    public static int calculateAge(String birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("La date de naissance ne peut pas être nulle.");
        }

        Date date = parseDate(birthDate);

        // Convertir java.util.Date en java.time.LocalDate
        LocalDate birthLocalDate = date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // Calculer l'âge en années
        return Period.between(birthLocalDate, LocalDate.now()).getYears();
    }

    private static Date parseDate(String birthDate) {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        try {
            return format.parse(birthDate);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid date format. Expected format: dd/MM/yyyy. Provided: " + birthDate, e);
        }
    }
}
