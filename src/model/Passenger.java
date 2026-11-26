
package model;

public class Passenger {

    private String name;
    private int age;
    private String gender;
    private String seatNumber;
    private double farePaid;

    // Constructor with fare
    public Passenger(String name, int age,
                    String gender, String seatNumber,
                    double farePaid) {

        this.name = name;
        this.age = age;
        this.gender = gender;
        this.seatNumber = seatNumber;
        this.farePaid = farePaid;
    }

    // Backward-compatible constructor
    public Passenger(String name, int age,
                    String gender, String seatNumber) {

        this(name, age, gender, seatNumber, 0.0);
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getGender() {
        return gender;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public double getFarePaid() {
        return farePaid;
    }

    // Setter for fare
    public void setFarePaid(double farePaid) {
        this.farePaid = farePaid;
    }
}
