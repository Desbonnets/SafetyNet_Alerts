package org.example.safetynet_alerts.models;

import java.util.Objects;

/**
 * Represents a fire station with its address and station number.
 * This class provides methods to get and set the address and station number.
 * It also ensures the validity of the data.
 */
public class FireStation {

    private String address; // Address of the fire station
    private int station;    // Fire station number

    /**
     * Constructor to create an instance of FireStation with an address and station number.
     * The arguments are validated to ensure they are not null or invalid.
     *
     * @param address The address of the fire station, cannot be empty or null.
     * @param station The fire station number, must be greater than 0.
     * @throws IllegalArgumentException If the address is null or empty, or if the station number is less than or equal to 0.
     */
    public FireStation(String address, int station) {
        setAddress(address);
        setStation(station);
    }

    /**
     * Returns the address of the fire station.
     *
     * @return The address of the fire station.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of the fire station.
     *
     * @param address The address to set, cannot be empty or null.
     * @throws IllegalArgumentException If the address is empty or null.
     */
    public void setAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            throw new IllegalArgumentException("Address cannot be empty or null.");
        }
        this.address = address;
    }

    /**
     * Returns the fire station number.
     *
     * @return The fire station number.
     */
    public int getStation() {
        return station;
    }

    /**
     * Sets the fire station number.
     *
     * @param station The fire station number to set, must be greater than 0.
     * @throws IllegalArgumentException If the station number is less than or equal to 0.
     */
    public void setStation(int station) {
        if (station <= 0) {
            throw new IllegalArgumentException("Station number must be greater than 0.");
        }
        this.station = station;
    }

    /**
     * Returns a string representation of the fire station.
     *
     * @return A string representation of the fire station's address and station number.
     */
    @Override
    public String toString() {
        return "FireStation{" +
                "address='" + address + '\'' +
                ", station=" + station +
                '}';
    }

    /**
     * Checks if this fire station is equal to another object.
     * Two fire stations are considered equal if they have the same address and station number.
     *
     * @param o The object to compare with this fire station.
     * @return true if the two fire stations are equal, otherwise false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FireStation that = (FireStation) o;
        return station == that.station && address.equals(that.address);
    }

    /**
     * Returns the hash code for this fire station.
     * The hash code is based on the address and station number.
     *
     * @return The hash code of this fire station.
     */
    @Override
    public int hashCode() {
        return Objects.hash(address, station);
    }
}
