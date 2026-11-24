
package model;

import java.util.ArrayList;

public class Booking {

    private String pnr;
    private String routeId;
    private ArrayList<Passenger> passengers;
    private double totalFare;

    // Constructor
    public Booking(String pnr, String routeId,
                   ArrayList<Passenger> passengers,
                   double totalFare) {

        this.pnr = pnr;
        this.routeId = routeId;
        this.passengers = passengers;
        this.totalFare = totalFare;
    }

    // Getters
    public String getPnr() {
        return pnr;
    }

    public String getRouteId() {
        return routeId;
    }

    public ArrayList<Passenger> getPassengers() {
        return passengers;
    }

    public double getTotalFare() {
        return totalFare;
    }

    // Update total fare after cancellation
    public void setTotalFare(double totalFare) {
        this.totalFare = totalFare;
    }
}
