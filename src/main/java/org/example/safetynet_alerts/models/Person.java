package org.example.safetynet_alerts.models;

/**
 * Represents a person with personal information.
 * This class stores the details of a person including their first name, last name, address, city, zip code,
 * phone number, and email address.
 */
public class Person {

    private String firstName; // First name of the person
    private String lastName; // Last name of the person
    private String address; // Address of the person
    private String city; // City where the person resides
    private int zip; // Zip code of the person's location
    private String phone; // Phone number of the person
    private String email; // Email address of the person

    /**
     * Constructs a Person object with the specified details.
     *
     * @param firstName The first name of the person.
     * @param lastName The last name of the person.
     * @param address The address of the person.
     * @param city The city where the person lives.
     * @param zip The zip code of the person's location.
     * @param phone The phone number of the person.
     * @param email The email address of the person.
     */
    public Person(String firstName, String lastName, String address, String city, int zip, String phone, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.zip = zip;
        this.phone = phone;
        this.email = email;
    }

    /**
     * Returns the first name of the person.
     *
     * @return The first name of the person.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name of the person.
     *
     * @return The last name of the person.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the address of the person.
     *
     * @return The address of the person.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Returns the city where the person lives.
     *
     * @return The city of the person.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city where the person lives.
     *
     * @param city The city to be set for the person.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Returns the phone number of the person.
     *
     * @return The phone number of the person.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Returns the email address of the person.
     *
     * @return The email address of the person.
     */
    public String getEmail() {
        return email;
    }
}
