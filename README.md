# Parking Lot Management System (CLI)

A modular, Java 17+ CLI-based Parking Lot Management System developed by a 5-member team.

---

## Module: Vehicle Management

### Responsibilities & Architecture
- **Model (`com.rest.models.Vehicle`)**: Encapsulates vehicle entity properties (`vehicleNumber`, `ownerName`, `vehicleType`) with constructors, getters, setters, and `toString()`.
- **Repository (`com.rest.repository.VehicleRepository`)**: In-memory temporary storage using `LinkedHashMap` and `ArrayList` for fast O(1) operations while preserving insertion order.
- **Service (`com.rest.services.VehicleService`)**: Business logic, input validation, duplicate check, and data orchestration.
- **CLI View (`com.rest.cli.VehicleMenu`)**: Interactive console user interface utilizing `java.util.Scanner` with formatted tabular display and validation feedback.
- **App Entry Point (`com.rest.Main`)**: Main menu coordinating integration between all 5 team modules.

---

## Compilation & Execution

### 1. Compile All Files
From the project root (`ParkingLotSystem/`):
```bash
javac -d bin src/com/rest/models/Vehicle.java src/com/rest/repository/VehicleRepository.java src/com/rest/services/VehicleService.java src/com/rest/cli/VehicleMenu.java src/com/rest/Main.java
```
Or simply:
```bash
javac -d bin src/com/rest/**/*.java
```

### 2. Run Vehicle Management Module Standalone
```bash
java -cp bin com.rest.cli.VehicleMenu
```

### 3. Run the Full Application (Team Main Menu)
```bash
java -cp bin com.rest.Main
```

---

## Team Integration Guide

Other modules (e.g., Slot Allocation, Ticket Generator, Billing) can integrate with Vehicle Management by injecting or instantiating `VehicleService`:

```java
import com.rest.models.Vehicle;
import com.rest.services.VehicleService;

VehicleService vehicleService = new VehicleService();

// Check if a vehicle is registered
boolean registered = vehicleService.isVehicleRegistered("KA-01-AB-1234");

// Retrieve vehicle details
Optional<Vehicle> vehicleOpt = vehicleService.searchVehicle("KA-01-AB-1234");

// Register a vehicle programmatically
vehicleService.addVehicle("DL-05-ZZ-1111", "John Doe", "Car");
```

