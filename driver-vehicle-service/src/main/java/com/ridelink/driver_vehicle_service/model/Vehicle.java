package com.ridelink.driver_vehicle_service.model;

public class Vehicle {

    private String registrationNumber;
    private String make;
    private String model;
    private String type;
    private String color;

    public Vehicle() {
    }

    public Vehicle(String registrationNumber, String make,
                   String model, String type, String color) {
        this.registrationNumber = registrationNumber;
        this.make = make;
        this.model = model;
        this.type = type;
        this.color = color;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}