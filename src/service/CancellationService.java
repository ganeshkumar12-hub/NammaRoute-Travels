
package service;

import model.Booking;
import model.Bus;
import model.Passenger;
import model.Seat;
import model.WaitlistEntry;
import util.PNRGenerator;

import java.util.ArrayList;
import java.util.Scanner;

public class CancellationService {

    private BusService busService;
    private BookingService bookingService;
    private WaitlistService waitlistService;
    private Scanner sc;

    public CancellationService(BusService busService,
                             BookingService bookingService,
                             WaitlistService waitlistService,
                             Scanner sc) {

        this.busService = busService;
        this.bookingService = bookingService;
        this.waitlistService = waitlistService;
        this.sc = sc;
    }

    // ================= CANCEL TICKETS =================

    public void cancelTickets() {

        System.out.println("\n========== TICKET CANCELLATION ==========");

        System.out.print("Enter PNR: ");
        String pnr = sc.nextLine().trim();

        Booking booking = null;

        // Find booking using PNR
        for (Booking b : bookingService.getBookings()) {

            if (b.getPnr().equalsIgnoreCase(pnr)) {
                booking = b;
                break;
            }
        }

        if (booking == null) {
            System.out.println("Invalid PNR. Booking not found.");
            return;
        }

        ArrayList<Passenger> passengers = booking.getPassengers();

        if (passengers.isEmpty()) {
            System.out.println("No active passengers in this booking.");
            return;
        }

        // Display booking details
        System.out.println("\nBooking Details:");
        System.out.println("PNR: " + booking.getPnr());
        System.out.println("Route ID: " + booking.getRouteId());

        System.out.println("\nPassenger Details:");

        for (Passenger p : passengers) {

            System.out.println(
                    p.getSeatNumber() + " - "
                    + p.getName() + " - "
                    + p.getAge() + " - "
                    + p.getGender() + " - Fare: Rs. "
                    + p.getFarePaid()
            );
        }

        System.out.println("\n1. Cancel all seats");
        System.out.println("2. Cancel selected seats");

        System.out.print("Enter your choice: ");

        String choice = sc.nextLine().trim();

        ArrayList<Passenger> cancelledPassengers = new ArrayList<>();

        if (choice.equals("1")) {

            cancelledPassengers.addAll(passengers);

        } else if (choice.equals("2")) {

            System.out.print("Enter seat numbers separated by commas: ");

            String input = sc.nextLine();

            String[] seatNumbers = input.split(",");

            // Validate selected seats
            for (String seatNumber : seatNumbers) {

                String requestedSeat = seatNumber.trim();

                if (requestedSeat.isEmpty()) {
                    System.out.println("Invalid seat number.");
                    return;
                }

                Passenger matchedPassenger = null;

                for (Passenger p : passengers) {

                    if (p.getSeatNumber().equalsIgnoreCase(requestedSeat)) {
                        matchedPassenger = p;
                        break;
                    }
                }

                if (matchedPassenger == null) {
                    System.out.println(
                            "Invalid or already cancelled seat: "
                            + requestedSeat
                    );
                    return;
                }

                if (cancelledPassengers.contains(matchedPassenger)) {
                    System.out.println(
                            "Duplicate seat selected: " + requestedSeat
                    );
                    return;
                }

                cancelledPassengers.add(matchedPassenger);
            }

            if (cancelledPassengers.isEmpty()) {
                System.out.println("No seats selected.");
                return;
            }

        } else {

            System.out.println("Invalid choice.");
            return;
        }

        // Find bus
        Bus bus = busService.getBusByRouteId(booking.getRouteId());

        if (bus == null) {
            System.out.println("Bus not found.");
            return;
        }

        // ================= REFUND CALCULATION =================

        double refund = 0;

        for (Passenger p : cancelledPassengers) {

            double passengerFare = p.getFarePaid();

            // Legacy booking fallback for passengers
            // whose fare was not previously stored
            if (passengerFare <= 0) {

                passengerFare = bus.getBasePrice();

                p.setFarePaid(passengerFare);
            }

            refund += passengerFare;
        }

        // Release cancelled seats
        for (Passenger p : cancelledPassengers) {

            for (Seat seat : bus.getSeats()) {

                if (seat.getSeatNumber().equalsIgnoreCase(
                        p.getSeatNumber())) {

                    seat.setBooked(false);
                    break;
                }
            }
        }

        // Remove cancelled passengers
        passengers.removeAll(cancelledPassengers);

        // ================= REMAINING FARE =================

        double remainingFare = 0;

        for (Passenger p : passengers) {

            double passengerFare = p.getFarePaid();

            // Legacy passenger fallback
            if (passengerFare <= 0) {

                passengerFare = bus.getBasePrice();

                p.setFarePaid(passengerFare);
            }

            remainingFare += passengerFare;
        }

        booking.setTotalFare(remainingFare);

        // ================= CANCELLATION RECEIPT =================

        System.out.println("\n========== CANCELLATION RECEIPT ==========");

        System.out.println("PNR: " + booking.getPnr());

        System.out.println("\nCancelled Seats:");

        for (Passenger p : cancelledPassengers) {

            System.out.println(
                    p.getSeatNumber() + " - "
                    + p.getName() + " - Refund: Rs. "
                    + p.getFarePaid()
            );
        }

        System.out.println("\nRefund Amount: Rs. " + refund);

        System.out.println("Remaining Passengers: "
                + passengers.size());

        System.out.println("Remaining Booking Fare: Rs. "
                + booking.getTotalFare());

        System.out.println("==========================================");

        if (passengers.isEmpty()) {

            System.out.println(
                    "All seats in this booking have been cancelled."
            );
        }

        // Attempt waitlist promotion for released seats
        for (Passenger cancelledPassenger : cancelledPassengers) {

            promoteWaitlistedPassenger(
                    bus,
                    cancelledPassenger.getSeatNumber()
            );
        }
    }

    // ================= AUTO-PROMOTION =================

    private void promoteWaitlistedPassenger(
            Bus bus, String availableSeatNumber) {

        ArrayList<WaitlistEntry> routeWaitlist =
                new ArrayList<>();

        // Collect route-specific waitlist in FIFO order
        for (WaitlistEntry entry : waitlistService.getWaitlist()) {

            if (entry.getRouteId().equalsIgnoreCase(
                    bus.getRouteId())) {

                routeWaitlist.add(entry);
            }
        }

        // Check candidates in FIFO order
        for (WaitlistEntry entry : routeWaitlist) {

            // Safety eligibility
            if (!isPassengerEligible(
                    entry, bus, availableSeatNumber)) {

                continue;
            }

            // Find released seat
            Seat availableSeat = null;

            for (Seat seat : bus.getSeats()) {

                if (seat.getSeatNumber().equalsIgnoreCase(
                        availableSeatNumber)
                        && !seat.isBooked()) {

                    availableSeat = seat;
                    break;
                }
            }

            if (availableSeat == null) {
                return;
            }

            // Generate unique PNR
            String newPnr;

            do {
                newPnr = PNRGenerator.generatePNR();
            } while (findBookingByPNR(newPnr) != null);

            // Calculate promoted passenger's fare
            double fare = bus.getPriceForNewBooking();

            // Create promoted passenger with actual fare
            Passenger promotedPassenger = new Passenger(
                    entry.getName(),
                    entry.getAge(),
                    entry.getGender(),
                    availableSeatNumber,
                    fare
            );

            ArrayList<Passenger> promotedPassengers =
                    new ArrayList<>();

            promotedPassengers.add(promotedPassenger);

            // Create booking
            Booking promotedBooking = new Booking(
                    newPnr,
                    bus.getRouteId(),
                    promotedPassengers,
                    fare
            );

            // Assign seat
            availableSeat.setBooked(true);

            // Add booking
            bookingService.getBookings().add(promotedBooking);

            // Remove from waitlist
            waitlistService.removeFromWaitlist(entry);

            // Promotion confirmation
            System.out.println("\n========== WAITLIST PROMOTION ==========");

            System.out.println(
                    "Waitlisted passenger promoted successfully!"
            );

            System.out.println("Passenger: " + entry.getName());
            System.out.println("Route ID: " + bus.getRouteId());
            System.out.println("Seat Number: " + availableSeatNumber);
            System.out.println("New PNR: " + newPnr);
            System.out.println("Fare: Rs. " + fare);

            System.out.println("========================================");

            return;
        }

        System.out.println(
                "\nNo eligible waitlisted passenger found "
                + "for seat " + availableSeatNumber + "."
        );
    }

    // ================= SAFETY CHECK =================

    private boolean isPassengerEligible(
            WaitlistEntry candidate,
            Bus bus,
            String seatNumber) {

        // Only male passengers need checking
        if (!candidate.getGender().equalsIgnoreCase("M")) {
            return true;
        }

        String adjacentSeat = getAdjacentSeat(seatNumber);

        if (adjacentSeat == null) {
            return true;
        }

        // Check confirmed passengers on the same route
        for (Booking booking : bookingService.getBookings()) {

            if (!booking.getRouteId().equalsIgnoreCase(
                    bus.getRouteId())) {

                continue;
            }

            for (Passenger passenger : booking.getPassengers()) {

                if (passenger.getSeatNumber().equalsIgnoreCase(
                        adjacentSeat)
                        && passenger.getGender().equalsIgnoreCase("F")) {

                    return false;
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

    // ================= FIND BOOKING =================

    private Booking findBookingByPNR(String pnr) {

        for (Booking booking : bookingService.getBookings()) {

            if (booking.getPnr().equalsIgnoreCase(pnr)) {
                return booking;
            }
        }

        return null;
    }
}
