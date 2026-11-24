
package model;

public class Seat {

    private String seatNumber;
    private String seatType;
    private boolean isBooked;

    public Seat(String seatNumber, String seatType) {
        this.seatNumber = seatNumber;
        this.seatType = seatType;
        this.isBooked = false;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public String getSeatType() {
        return seatType;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }
}
