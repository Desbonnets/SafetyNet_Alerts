package org.example.safetynet_alerts.models;

import java.util.List;

public class PersonInfoDTO {

    private String firstName;
    private String lastName;
    private String address;
    private int age;
    private String phone;
    private String email;
    private List<String> medications;
    private List<String> allergies;

    public PersonInfoDTO(
            String firstName,
            String lastName,
            String address,
            int age
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.age = age;
    }

    public PersonInfoDTO(
            String firstName,
            String lastName,
            String address,
            int age,
            String phone,
            String email,
            List<String> medications,
            List<String> allergies
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.age = age;
        this.phone = phone;
        this.email = email;
        this.medications = medications;
        this.allergies = allergies;
    }

    // Getters et setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<String> getMedications() { return medications; }
    public void setMedications(List<String> medications) { this.medications = medications; }
    public List<String> getAllergies() { return allergies; }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }
}
