
package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import model.Bus;
import model.Booking;
import model.WaitlistEntry;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class JsonStorage {

    private static final String DATA_FOLDER = "data";

    private static final String BUS_FILE = DATA_FOLDER + "/buses.json";
    private static final String BOOKING_FILE = DATA_FOLDER + "/bookings.json";
    private static final String WAITLIST_FILE = DATA_FOLDER + "/waitlist.json";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(
                    LocalDateTime.class,
                    new LocalDateTimeAdapter()
            )
            .setPrettyPrinting()
            .create();

    // ================= SAVE BUSES =================

    public static void saveBuses(ArrayList<Bus> buses) {

        try {
            new File(DATA_FOLDER).mkdirs();

            try (FileWriter writer = new FileWriter(BUS_FILE)) {
                gson.toJson(buses, writer);
            }

            System.out.println("Bus data saved successfully!");

        } catch (IOException e) {
            System.out.println("Error saving bus data: " + e.getMessage());
        }
    }

    // ================= LOAD BUSES =================

    public static ArrayList<Bus> loadBuses() {

        File file = new File(BUS_FILE);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<ArrayList<Bus>>() {}.getType();

        try (FileReader reader = new FileReader(file)) {

            ArrayList<Bus> buses = gson.fromJson(reader, type);

            return buses == null ? new ArrayList<>() : buses;

        } catch (IOException e) {
            System.out.println("Error loading bus data: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ================= SAVE BOOKINGS =================

    public static void saveBookings(ArrayList<Booking> bookings) {

        try {
            new File(DATA_FOLDER).mkdirs();

            try (FileWriter writer = new FileWriter(BOOKING_FILE)) {
                gson.toJson(bookings, writer);
            }

            System.out.println("Booking data saved successfully!");

        } catch (IOException e) {
            System.out.println("Error saving booking data: " + e.getMessage());
        }
    }

    // ================= LOAD BOOKINGS =================

    public static ArrayList<Booking> loadBookings() {

        File file = new File(BOOKING_FILE);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<ArrayList<Booking>>() {}.getType();

        try (FileReader reader = new FileReader(file)) {

            ArrayList<Booking> bookings = gson.fromJson(reader, type);

            return bookings == null ? new ArrayList<>() : bookings;

        } catch (IOException e) {
            System.out.println("Error loading booking data: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // ================= SAVE WAITLIST =================

    public static void saveWaitlist(ArrayList<WaitlistEntry> waitlist) {

        try {
            new File(DATA_FOLDER).mkdirs();

            try (FileWriter writer = new FileWriter(WAITLIST_FILE)) {
                gson.toJson(waitlist, writer);
            }

            System.out.println("Waitlist data saved successfully!");

        } catch (IOException e) {
            System.out.println("Error saving waitlist data: " + e.getMessage());
        }
    }

    // ================= LOAD WAITLIST =================

    public static ArrayList<WaitlistEntry> loadWaitlist() {

        File file = new File(WAITLIST_FILE);

        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<ArrayList<WaitlistEntry>>() {}.getType();

        try (FileReader reader = new FileReader(file)) {

            ArrayList<WaitlistEntry> waitlist = gson.fromJson(reader, type);

            return waitlist == null ? new ArrayList<>() : waitlist;

        } catch (IOException e) {
            System.out.println("Error loading waitlist data: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
