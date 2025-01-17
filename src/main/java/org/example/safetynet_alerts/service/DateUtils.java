package org.example.safetynet_alerts.service;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {

    public static int calculateAge(Date birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("La date de naissance ne peut pas être nulle.");
        }

        // Convertir java.util.Date en java.time.LocalDate
        LocalDate birthLocalDate = birthDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        // Calculer l'âge en années
        return Period.between(birthLocalDate, LocalDate.now()).getYears();
    }
}
