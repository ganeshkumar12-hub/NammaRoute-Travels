
package service;

import model.Booking;
import model.Bus;
import model.Passenger;
import model.Seat;

import java.util.ArrayList;
import java.util.Scanner;

public class CancellationService {

    private BusService busService;
    private BookingService bookingService;
    private Scanner sc;

    public CancellationService(BusService busService,
                               BookingService bookingService,
                               Scanner sc) {

        this.busService = busService;
        this.bookingService = bookingService;
        this.sc = sc;
    }

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
                    + p.getGender()
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

            // Validate all selected seats before cancellation
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
                            "Invalid or already cancelled seat: " + requestedSeat
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

        // Calculate refund
        double refund = bus.getBasePrice()
                * cancelledPassengers.size();

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

        // Update remaining booking fare
        double remainingFare = bus.getBasePrice()
                * passengers.size();

        booking.setTotalFare(remainingFare);

        // Cancellation receipt
        System.out.println("\n========== CANCELLATION RECEIPT ==========");

        System.out.println("PNR: " + booking.getPnr());

        System.out.println("\nCancelled Seats:");

        for (Passenger p : cancelledPassengers) {

            System.out.println(
                    p.getSeatNumber() + " - " + p.getName()
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
    }
}
