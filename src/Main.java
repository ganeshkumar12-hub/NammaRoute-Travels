
import model.Bus;
import model.Booking;
import model.Passenger;
import model.Seat;

import service.BusService;
import service.BookingService;
import service.CancellationService;
import service.ManifestService;

import util.JsonStorage;

import java.time.LocalDateTime;
import java.util.Scanner;
import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        // Create Bus Service
        BusService service = new BusService();

        // Load buses from JSON
        ArrayList<Bus> savedBuses = JsonStorage.loadBuses();

        if (savedBuses.isEmpty()) {

            System.out.println("\n--- ADD SAMPLE BUSES ---");

            Bus bus1 = new Bus(
                    "NR-101",
                    "Bengaluru",
                    "Goa",
                    LocalDateTime.of(2026, 11, 27, 21, 30),
                    1200,
                    "SEATER"
            );

            Bus bus2 = new Bus(
                    "NR-102",
                    "Bengaluru",
                    "Hyderabad",
                    LocalDateTime.of(2026, 11, 28, 22, 0),
                    1500,
                    "SLEEPER"
            );

            service.addBus(bus1);
            service.addBus(bus2);

            // Save sample buses
            JsonStorage.saveBuses(service.getBuses());

        } else {

            service.setBuses(savedBuses);

            System.out.println("\nSaved buses loaded successfully!");
        }

        // Create Booking Service
        BookingService bookingService = new BookingService(service);

        // Load bookings from JSON
        ArrayList<Booking> savedBookings =
                JsonStorage.loadBookings();

        bookingService.setBookings(savedBookings);

        System.out.println("Saved bookings loaded successfully!");

        // Restore booked seat status
        for (Booking booking : savedBookings) {

            Bus bus = service.getBusByRouteId(
                    booking.getRouteId()
            );

            if (bus != null) {

                for (Passenger passenger : booking.getPassengers()) {

                    for (Seat seat : bus.getSeats()) {

                        if (seat.getSeatNumber().equalsIgnoreCase(
                                passenger.getSeatNumber())) {

                            seat.setBooked(true);
                            break;
                        }
                    }
                }
            }
        }

        System.out.println("Seat status restored successfully!");

        // Create Cancellation Service
        CancellationService cancellationService =
                new CancellationService(
                        service,
                        bookingService,
                        bookingService.getScanner()
                );

        // Create Manifest Service
        ManifestService manifestService =
                new ManifestService(bookingService);

        Scanner sc = bookingService.getScanner();

        // Main Menu
        while (true) {

            System.out.println("\n========== NAMMAROUTE TRAVELS ==========");
            System.out.println("1. View Buses");
            System.out.println("2. View Seat Map");
            System.out.println("3. Book Tickets");
            System.out.println("4. Cancel Tickets");
            System.out.println("5. Exit");
            System.out.println("6. View Passenger Manifest");

            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            switch (choice) {

                case "1":
                    service.viewBuses();
                    break;

                case "2":

                    System.out.print("Enter Route ID: ");

                    String routeId = sc.nextLine().trim();

                    service.viewSeatMap(routeId);
                    break;

                case "3":

                    bookingService.bookTickets();

                    // Save updated data
                    JsonStorage.saveBuses(service.getBuses());
                    JsonStorage.saveBookings(bookingService.getBookings());

                    break;

                case "4":

                    cancellationService.cancelTickets();

                    // Save updated data
                    JsonStorage.saveBuses(service.getBuses());
                    JsonStorage.saveBookings(bookingService.getBookings());

                    break;

                case "5":

                    // Save before exiting
                    JsonStorage.saveBuses(service.getBuses());
                    JsonStorage.saveBookings(bookingService.getBookings());

                    System.out.println(
                            "Thank you for using NammaRoute Travels!"
                    );

                    return;

                case "6":

                    manifestService.viewPassengerManifest();
                    break;

                default:

                    System.out.println(
                            "Invalid choice. Please try again."
                    );
            }
        }
    }
}
