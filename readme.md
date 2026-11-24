
# NammaRoute Travels – Bus Booking System

## Project Overview
NammaRoute Travels is a Java-based console application that allows users to manage bus services, view seat availability, book tickets, cancel reservations, and view passenger manifests.

The application uses JSON files for local data persistence, ensuring that bus details, bookings, and seat statuses are retained after restarting the application.

## Features

- Add and view bus services.
- Support for Seater and Sleeper bus layouts.
- Automatic seat generation.
- Interactive seat availability display.
- Book multiple seats in a single transaction.
- Passenger detail collection.
- Unique PNR generation.
- Full and partial ticket cancellation.
- Refund calculation.
- Passenger manifest generation.
- JSON-based data persistence.
- Restore booking and seat information after restart.

## Technologies Used

- Java 17+
- Object-Oriented Programming
- Gson
- JSON
- Collections Framework
- VS Code
- Git and GitHub

## Project Structure

```text
NammaRouteTravels/
│
├── data/
│   ├── buses.json
│   └── bookings.json
│
├── lib/
│   └── gson-2.15.0.jar
│
├── out/
│
├── src/
│   ├── model/
│   │   ├── Bus.java
│   │   ├── Seat.java
│   │   ├── Passenger.java
│   │   └── Booking.java
│   │
│   ├── service/
│   │   ├── BusService.java
│   │   ├── BookingService.java
│   │   ├── CancellationService.java
│   │   └── ManifestService.java
│   │
│   ├── util/
│   │   ├── PNRGenerator.java
│   │   ├── JsonStorage.java
│   │   └── LocalDateTimeAdapter.java
│   │
│   └── Main.java
│
└── README.md
```

## How to Run

### Prerequisites
- Java JDK 17 or above.
- Gson JAR file.

### Compile

```bash
javac -cp "lib/*" -d out src/model/*.java src/util/*.java src/service/*.java src/Main.java
```

### Run

On Windows:

```bash
java -cp "out;lib/*" Main
```

On Linux/macOS:

```bash
java -cp "out:lib/*" Main
```

## Data Storage

The application uses Gson to store information locally.

- `buses.json` stores bus and seat information.
- `bookings.json` stores PNR and passenger booking information.

## Learning Outcomes

- Understanding Java OOP concepts.
- Implementing ArrayList and collections.
- Designing modular Java applications.
- Exception handling and input validation.
- Working with JSON using Gson.
- File handling and data persistence.
- Implementing real-world booking and cancellation workflows.

## Author

Seeripi Ganesh Kumar

GitHub: https://github.com/ganeshkumar12-hub
