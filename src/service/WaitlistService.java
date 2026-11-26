
package service;

import model.WaitlistEntry;
import util.JsonStorage;

import java.util.ArrayList;

public class WaitlistService {

    private ArrayList<WaitlistEntry> waitlist;

    // Constructor: Load waitlist from JSON
    public WaitlistService() {

        this.waitlist = JsonStorage.loadWaitlist();

        System.out.println(
                "Waitlist data loaded: " + waitlist.size() + " entries."
        );
    }

    // Add passenger to waitlist
    public boolean addToWaitlist(WaitlistEntry entry) {

        int routeWaitlistCount = 0;

        for (WaitlistEntry existing : waitlist) {

            if (existing.getRouteId().equalsIgnoreCase(
                    entry.getRouteId())) {

                routeWaitlistCount++;
            }
        }

        // Maximum 5 passengers per route
        if (routeWaitlistCount >= 5) {

            System.out.println(
                    "Waitlist is full for this route (maximum 5)."
            );

            return false;
        }

        waitlist.add(entry);

        // Save after addition
        JsonStorage.saveWaitlist(waitlist);

        System.out.println(
                "Passenger added to waitlist successfully!"
        );

        return true;
    }

    // Display waitlist for a route
    public void viewWaitlist(String routeId) {

        System.out.println("\n========== WAITLIST ==========");

        int position = 1;
        boolean found = false;

        for (WaitlistEntry entry : waitlist) {

            if (entry.getRouteId().equalsIgnoreCase(routeId)) {

                System.out.println(
                        position + ". "
                        + entry.getName() + " - "
                        + entry.getAge() + " - "
                        + entry.getGender()
                );

                position++;
                found = true;
            }
        }

        if (!found) {
            System.out.println("No passengers in waitlist.");
        }

        System.out.println("==============================");
    }

    // Remove entry
    public boolean removeFromWaitlist(WaitlistEntry entry) {

        boolean removed = waitlist.remove(entry);

        // Save after removal
        if (removed) {
            JsonStorage.saveWaitlist(waitlist);
        }

        return removed;
    }

    // Get waitlist entries
    public ArrayList<WaitlistEntry> getWaitlist() {
        return waitlist;
    }

    // Restore waitlist
    public void setWaitlist(ArrayList<WaitlistEntry> waitlist) {

        this.waitlist = waitlist;

        JsonStorage.saveWaitlist(waitlist);
    }
}
