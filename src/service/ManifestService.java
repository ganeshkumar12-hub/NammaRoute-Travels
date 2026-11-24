
package service;

import model.Booking;
import model.Passenger;

import java.util.ArrayList;
import java.util.Comparator;

public class ManifestService {

    private BookingService bookingService;

    public ManifestService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public void viewPassengerManifest() {

        System.out.println("\n========== PASSENGER MANIFEST ====================");

        System.out.print("Enter Route ID: ");
        String routeId = bookingService.getScanner().nextLine().trim();

        ArrayList<Passenger> manifest = new ArrayList<>();

        // Find all passengers for the selected route
        for (Booking booking : bookingService.getBookings()) {

            if (booking.getRouteId().equalsIgnoreCase(routeId)) {

                // Add only active passengers
                manifest.addAll(booking.getPassengers());
            }
        }

        if (manifest.isEmpty()) {
            System.out.println("No passengers found for this route.");
            return;
        }

        // Sort passengers by seat number
        manifest.sort(
                Comparator.comparing(
                        Passenger::getSeatNumber,
                        String.CASE_INSENSITIVE_ORDER
                )
        );

        System.out.println("\nRoute ID: " + routeId);

        System.out.println("----------------------------------------------------");
        System.out.printf("%-12s %-20s %-8s %-10s%n",
                "Seat Number", "Passenger Name", "Age", "Gender");
        System.out.println("----------------------------------------------------");

        for (Passenger passenger : manifest) {

            System.out.printf("%-12s %-20s %-8d %-10s%n",
                    passenger.getSeatNumber(),
                    passenger.getName(),
                    passenger.getAge(),
                    passenger.getGender());
        }

        System.out.println("----------------------------------------------------");
        System.out.println("Total Passengers: " + manifest.size());
        System.out.println("====================================================");
    }
}
