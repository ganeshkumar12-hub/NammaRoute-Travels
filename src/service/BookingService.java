
package service;

import model.Bus;
import model.Seat;
import model.Passenger;
import model.Booking;
import model.WaitlistEntry;
import util.PNRGenerator;

import java.util.ArrayList;
import java.util.Scanner;

public class BookingService {

    private ArrayList<Booking> bookings;
    private BusService busService;
    private WaitlistService waitlistService;
    private Scanner sc;

    public BookingService(BusService busService,
                         WaitlistService waitlistService) {

        this.busService = busService;
        this.waitlistService = waitlistService;
        this.bookings = new ArrayList<>();
        this.sc = new Scanner(System.in);
    }

    // Restore bookings loaded from JSON
    public void setBookings(ArrayList<Booking> bookings) {
        this.bookings = bookings;
    }

    // ================= BOOK TICKETS =================

    public void bookTickets() {

        System.out.println("\n========== TICKET BOOKING ==========");

        System.out.print("Enter Route ID: ");
        String routeId = sc.nextLine().trim();

        Bus bus = busService.getBusByRouteId(routeId);

        if (bus == null) {
            System.out.println("Invalid Route ID!");
            return;
        }

        // Check whether the bus is completely full
        if (bus.getBookedSeatCount() >= bus.getSeats().size()) {

            System.out.println("\nBus is fully booked!");

            System.out.print(
                    "Would you like to join the waitlist? (Y/N): "
            );

            String waitlistChoice = sc.nextLine().trim();

            if (!waitlistChoice.equalsIgnoreCase("Y")) {
                System.out.println("Booking cancelled.");
                return;
            }

            // Collect waitlisted passenger details
            System.out.print("Enter passenger name: ");
            String name = sc.nextLine().trim();

            if (name.isEmpty()) {
                System.out.println("Name cannot be empty!");
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

            WaitlistEntry entry = new WaitlistEntry(
                    routeId, name, age, gender
            );

            waitlistService.addToWaitlist(entry);

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

            // Temporary passenger
            passengers.add(new Passenger(
                    name,
                    age,
                    gender,
                    seat.getSeatNumber()
            ));
        }

        // Female co-seat safety validation
        if (!validateFemaleCoSeatSafety(passengers, routeId)) {
            return;
        }

        // ================= FARE CALCULATION =================

        double totalFare = 0;
        int currentBookedSeats = bus.getBookedSeatCount();
        int totalSeats = bus.getSeats().size();

        System.out.println("\n========== FARE BREAKDOWN ==========");

        for (int i = 0; i < selectedSeats.size(); i++) {

            Seat seat = selectedSeats.get(i);

            double fare;

            if (currentBookedSeats * 100 >= totalSeats * 70) {

                fare = bus.getBasePrice() * 1.20;

                System.out.println(seat.getSeatNumber()
                        + " - Surge Fare: Rs. " + fare);

            } else {

                fare = bus.getBasePrice();

                System.out.println(seat.getSeatNumber()
                        + " - Normal Fare: Rs. " + fare);
            }

            // Store actual fare paid by this passenger
            passengers.get(i).setFarePaid(fare);

            totalFare += fare;

            // Simulate seat occupancy for next seat's fare
            currentBookedSeats++;
        }

        System.out.println("-----------------------------------");
        System.out.println("Total Fare: Rs. " + totalFare);
        System.out.println("===================================");

        // Confirm booking
        System.out.print("Confirm booking? (Y/N): ");
        String confirmation = sc.nextLine().trim();

        if (!confirmation.equalsIgnoreCase("Y")) {
            System.out.println("Booking cancelled by user.");
            return;
        }

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

        // Update seats only after validations
        for (Seat seat : selectedSeats) {
            seat.setBooked(true);
        }

        bookings.add(booking);

        printReceipt(booking, bus);
    }

    // ================= FEMALE CO-SEAT SAFETY =================

    private boolean validateFemaleCoSeatSafety(
            ArrayList<Passenger> newPassengers,
            String routeId) {

        for (Passenger newPassenger : newPassengers) {

            if (newPassenger.getGender().equalsIgnoreCase("M")) {

                String adjacentSeat =
                        getAdjacentSeat(newPassenger.getSeatNumber());

                if (adjacentSeat == null) {
                    continue;
                }

                for (Booking booking : bookings) {

                    if (!booking.getRouteId()
                            .equalsIgnoreCase(routeId)) {
                        continue;
                    }

                    for (Passenger existingPassenger
                            : booking.getPassengers()) {

                        if (existingPassenger.getSeatNumber()
                                .equalsIgnoreCase(adjacentSeat)
                                && existingPassenger.getGender()
                                .equalsIgnoreCase("F")) {

                            System.out.println(
                                    "Sorry, seat "
                                    + newPassenger.getSeatNumber()
                                    + " is reserved for a female co-passenger. "
                                    + "Please choose another seat.");

                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }

    // ================= ADJACENT SEAT =================

    private String getAdjacentSeat(String seatNumber) {

        if (seatNumber == null || seatNumber.length() < 2) {
            return null;
        }

        String prefix = seatNumber.substring(
                0, seatNumber.length() - 1
        );

        char suffix = Character.toUpperCase(
                seatNumber.charAt(seatNumber.length() - 1)
        );

        switch (suffix) {

            case 'A':
                return prefix + "B";

            case 'B':
                return prefix + "A";

            case 'C':
                return prefix + "D";

            case 'D':
                return prefix + "C";

            case 'L':
                return prefix + "U";

            case 'U':
                return prefix + "L";

            default:
                return null;
        }
    }

    // ================= SCANNER =================

    public Scanner getScanner() {
        return sc;
    }

    // ================= FIND BOOKING =================

    private Booking findBookingByPNR(String pnr) {

        for (Booking booking : bookings) {

            if (booking.getPnr().equalsIgnoreCase(pnr)) {
                return booking;
            }
        }

        return null;
    }

    // ================= RECEIPT =================

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
                    + passenger.getGender() + " - Fare: Rs. "
                    + passenger.getFarePaid()
            );
        }

        System.out.println("\nTotal Amount: Rs. "
                + booking.getTotalFare());

        System.out.println("=====================================");
    }

    // ================= BOOKINGS =================

    public ArrayList<Booking> getBookings() {
        return bookings;
    }
}
