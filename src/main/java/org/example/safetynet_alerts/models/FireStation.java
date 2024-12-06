package org.example.safetynet_alerts.models;

import java.util.Objects;

public class FireStation {
    private String address;
    private int station;

    public FireStation(String address, int station) {
        setAddress(address);
        setStation(station);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("L'adresse ne peut pas être vide ou nulle.");
        }

        this.address = address;
    }

    public int getStation() {
        return station;
    }

    public void setStation(int station) {
        if (station <= 0) {
            throw new IllegalArgumentException("Le numéro de station doit être supérieur à 0.");
        }
        this.station = station;
    }

    @Override
    public String toString() {
        return "FireStation{" +
                "address='" + address + '\'' +
                ", station=" + station +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FireStation that = (FireStation) o;
        return station == that.station && address.equals(that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, station);
    }
}
