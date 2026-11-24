
package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Bus {

    private String routeId;
    private String origin;
    private String destination;
    private LocalDateTime departureDateTime;
    private double basePrice;
    private String busType;
    private ArrayList<Seat> seats;

    // Constructor
    public Bus(String routeId, String origin,
               String destination,
               LocalDateTime departureDateTime,
               double basePrice, String busType) {

        this.routeId = routeId;
        this.origin = origin;
        this.destination = destination;
        this.departureDateTime = departureDateTime;
        this.basePrice = basePrice;
        this.busType = busType;

        seats = new ArrayList<>();

        generateSeats();
    }

    // Generate seats based on bus type
    private void generateSeats() {

        if (busType.equalsIgnoreCase("SEATER")) {

            for (int row = 1; row <= 8; row++) {

                seats.add(new Seat(row + "A", "SEATER"));
                seats.add(new Seat(row + "B", "SEATER"));
                seats.add(new Seat(row + "C", "SEATER"));
                seats.add(new Seat(row + "D", "SEATER"));
            }

        } else if (busType.equalsIgnoreCase("SLEEPER")) {

            for (int i = 1; i <= 12; i++) {

                seats.add(new Seat(i + "L", "LOWER"));
                seats.add(new Seat(i + "U", "UPPER"));
            }
        }
    }

    // Getters
    public String getRouteId() {
        return routeId;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDateTime getDepartureDateTime() {
        return departureDateTime;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public String getBusType() {
        return busType;
    }

    public ArrayList<Seat> getSeats() {
        return seats;
    }
}
