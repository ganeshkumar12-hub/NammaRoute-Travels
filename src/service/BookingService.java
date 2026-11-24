
package service;

import model.Bus;
import model.Seat;
import model.Passenger;
import model.Booking;
import util.PNRGenerator;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.ArrayList;
import model.Booking;
public class BookingService {

    private ArrayList<Booking> bookings;
    private BusService busService;
    private Scanner sc;

    public BookingService(BusService busService) {
        this.busService = busService;
        this.bookings = new ArrayList<>();
        this.sc = new Scanner(System.in);
    }
// Restore bookings loaded from JSON
public void setBookings(ArrayList<Booking> bookings) {
    this.bookings = bookings;
}
    public void bookTickets() {

        System.out.println("\n========== TICKET BOOKING ==========");

        System.out.print("Enter Route ID: ");
        String routeId = sc.nextLine().trim();

        Bus bus = busService.getBusByRouteId(routeId);

        if (bus == null) {
            System.out.println("Invalid Route ID!");
            return;
        }

        System.out.print("Enter number of seats: ");

        int count;

        try {
            count = Integer.parseInt(sc.nextLine().trim());

            if (count <= 0) {
                System.out.println("Invalid number of seats!");
                return;
            }

        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number!");
            return;
        }

        ArrayList<Seat> selectedSeats = new ArrayList<>();

        // Validate all seats before booking
        for (int i = 0; i < count; i++) {

            System.out.print("Enter seat number " + (i + 1) + ": ");
            String seatNumber = sc.nextLine().trim();

            Seat selectedSeat = null;

            for (Seat seat : bus.getSeats()) {

                if (seat.getSeatNumber().equalsIgnoreCase(seatNumber)) {
                    selectedSeat = seat;
                    break;
                }
            }

            if (selectedSeat == null) {
                System.out.println("Invalid seat number: " + seatNumber);
                return;
            }

            if (selectedSeat.isBooked()) {
                System.out.println("Seat already booked: " + seatNumber);
                return;
            }

            // Prevent duplicate seat selection
            for (Seat seat : selectedSeats) {

                if (seat.getSeatNumber().equalsIgnoreCase(seatNumber)) {
                    System.out.println("Duplicate seat selected!");
                    return;
                }
            }

            selectedSeats.add(selectedSeat);
        }

        ArrayList<Passenger> passengers = new ArrayList<>();

        // Collect passenger details
        for (Seat seat : selectedSeats) {

            System.out.println("\nPassenger for seat: "
                    + seat.getSeatNumber());

            System.out.print("Enter name: ");
            String name = sc.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Passenger name cannot be empty!");
                return;
            }

            System.out.print("Enter age: ");

            int age;

            try {
                age = Integer.parseInt(sc.nextLine().trim());

                if (age <= 0) {
                    System.out.println("Invalid age!");
                    return;
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid age!");
                return;
            }

            System.out.print("Enter gender (M/F/Other): ");
            String gender = sc.nextLine().trim();

            if (!(gender.equalsIgnoreCase("M")
                    || gender.equalsIgnoreCase("F")
                    || gender.equalsIgnoreCase("Other"))) {

                System.out.println("Invalid gender!");
                return;
            }

            passengers.add(new Passenger(
                    name,
                    age,
                    gender,
                    seat.getSeatNumber()
            ));
        }

        // Calculate total fare
        double totalFare = bus.getBasePrice() * passengers.size();

        // Generate unique PNR
        String pnr;

        do {
            pnr = PNRGenerator.generatePNR();
        } while (findBookingByPNR(pnr) != null);

        // Create booking
        Booking booking = new Booking(
                pnr,
                routeId,
                passengers,
                totalFare
        );

        // Update seats only after all validations
        for (Seat seat : selectedSeats) {
            seat.setBooked(true);
        }

        bookings.add(booking);

        // Print booking receipt
        printReceipt(booking, bus);
    }

    // Return the existing Scanner for other services
    public Scanner getScanner() {
        return sc;
    }

    // Find booking using PNR
    private Booking findBookingByPNR(String pnr) {

        for (Booking booking : bookings) {

            if (booking.getPnr().equalsIgnoreCase(pnr)) {
                return booking;
            }
        }

        return null;
    }

    // Display booking receipt
    private void printReceipt(Booking booking, Bus bus) {

        System.out.println("\n========== BOOKING RECEIPT ==========");

        System.out.println("PNR: " + booking.getPnr());
        System.out.println("Route ID: " + bus.getRouteId());
        System.out.println("From: " + bus.getOrigin());
        System.out.println("To: " + bus.getDestination());

        System.out.println("\nPassenger Details:");

        for (Passenger passenger : booking.getPassengers()) {

            System.out.println(
                    passenger.getSeatNumber() + " - "
                    + passenger.getName() + " - "
                    + passenger.getAge() + " - "
                    + passenger.getGender()
            );
        }

        System.out.println("\nTotal Amount: Rs. "
                + booking.getTotalFare());

        System.out.println("=====================================");
    }

    // Return all bookings
    public ArrayList<Booking> getBookings() {
        return bookings;
    }
}
