
package service;

import model.Bus;
import model.Seat;

import java.util.ArrayList;

public class BusService {

    private ArrayList<Bus> buses;

    // Constructor
    public BusService() {
        buses = new ArrayList<>();
    }

    // Add Bus
    public boolean addBus(Bus bus) {

        // Check duplicate Route ID
        for (Bus b : buses) {

            if (b.getRouteId().equalsIgnoreCase(bus.getRouteId())) {

                System.out.println("Route ID already exists!");
                return false;
            }
        }

        buses.add(bus);

        System.out.println("Bus added successfully!");

        return true;
    }

    // View All Buses
    public void viewBuses() {

        if (buses.isEmpty()) {

            System.out.println("No buses available.");
            return;
        }

        System.out.println("\n========== AVAILABLE BUSES ==========");

        for (Bus bus : buses) {

            System.out.println("Route ID: " + bus.getRouteId());

            System.out.println("From: " + bus.getOrigin());

            System.out.println("To: " + bus.getDestination());

            System.out.println("Departure: "
                    + bus.getDepartureDateTime());

            System.out.println("Price: "
                    + bus.getBasePrice());

            System.out.println("Bus Type: "
                    + bus.getBusType());

            System.out.println("Total Seats: "
                    + bus.getSeats().size());

            System.out.println("-------------------------------------");
        }
    }

    // Display Seat Map
    public void viewSeatMap(String routeId) {

        Bus selectedBus = null;

        // Find bus using Route ID
        for (Bus bus : buses) {

            if (bus.getRouteId().equalsIgnoreCase(routeId)) {

                selectedBus = bus;
                break;
            }
        }

        // Check whether bus exists
        if (selectedBus == null) {

            System.out.println("Invalid Route ID!");
            return;
        }

        ArrayList<Seat> seats = selectedBus.getSeats();

        System.out.println("\n========== SEAT MAP ==========");

        System.out.println("Bus: "
                + selectedBus.getRouteId());

        System.out.println("Route: "
                + selectedBus.getOrigin()
                + " to "
                + selectedBus.getDestination());

        // Count booked seats
        int booked = 0;

        for (Seat seat : seats) {

            if (seat.isBooked()) {
                booked++;
            }
        }

        // Seater Layout
        if (selectedBus.getBusType().equalsIgnoreCase("SEATER")) {

            for (int i = 0; i < 8; i++) {

                int start = i * 4;

                System.out.printf(
                    "Row %d: [%s] [%s]     [%s] [%s]%n",
                    i + 1,
                    formatSeat(seats.get(start)),
                    formatSeat(seats.get(start + 1)),
                    formatSeat(seats.get(start + 2)),
                    formatSeat(seats.get(start + 3))
                );
            }
        }

        // Sleeper Layout
        else if (selectedBus.getBusType().equalsIgnoreCase("SLEEPER")) {

            for (int i = 0; i < 12; i++) {

                int start = i * 2;

                System.out.printf(
                    "Row %d: Lower [%s] | Upper [%s]%n",
                    i + 1,
                    formatSeat(seats.get(start)),
                    formatSeat(seats.get(start + 1))
                );
            }
        }

        System.out.println("-------------------------------");

        System.out.println("Total Seats: " + seats.size());

        System.out.println("Booked: " + booked);

        System.out.println("Available: " + (seats.size() - booked));
    }

    // Format seat display
    private String formatSeat(Seat seat) {

        if (seat.isBooked()) {
            return "XX";
        }

        return seat.getSeatNumber();
    }

    // Find bus using Route ID
    public Bus getBusByRouteId(String routeId) {

        for (Bus bus : buses) {

            if (bus.getRouteId().equalsIgnoreCase(routeId)) {
                return bus;
            }
        }

        return null;
    }

    // Return all buses
    public ArrayList<Bus> getBuses() {
        return buses;
    }

    // Restore buses loaded from JSON
    public void setBuses(ArrayList<Bus> buses) {
        this.buses = buses;
    }
}
