package com.rest.services;

import com.rest.models.ParkingRate;
import com.rest.repository.ParkingRateRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer containing fee calculation algorithms, rate rules, and validations.
 * Directly integrates with the ParkingPayment module.
 */
public class ParkingRateService {

    private final ParkingRateRepository rateRepository;

    public ParkingRateService() {
        this.rateRepository = new ParkingRateRepository();
        seedDefaultRates();
    }

    public ParkingRateService(ParkingRateRepository rateRepository) {
        if (rateRepository == null) {
            throw new IllegalArgumentException("ParkingRateRepository cannot be null.");
        }
        this.rateRepository = rateRepository;
        seedDefaultRates();
    }

    /**
     * Seeds initial standard rates if the repository is empty, ensuring out-of-the-box readiness.
     */
    private void seedDefaultRates() {
        if (rateRepository.count() == 0) {
            saveRate("RATE-BIKE", "BIKE", 10.0, 5.0, 15.0);
            saveRate("RATE-CAR", "CAR", 20.0, 10.0, 25.0);
            saveRate("RATE-SUV", "SUV", 30.0, 15.0, 35.0);
            saveRate("RATE-TRUCK", "TRUCK", 50.0, 25.0, 50.0);
            saveRate("RATE-ELECTRIC", "ELECTRIC", 25.0, 12.0, 30.0);
        }
    }

    /**
     * Saves or updates a rate configuration.
     */
    public boolean saveRate(String rateId, String vehicleType, double baseFee, double hourlyRate, double penaltyPerHour) {
        if (!isValidString(rateId) || !isValidString(vehicleType)) {
            System.err.println("[Validation Error] Rate ID and Vehicle Type cannot be blank.");
            return false;
        }

        if (baseFee < 0 || hourlyRate < 0 || penaltyPerHour < 0) {
            System.err.println("[Validation Error] Rates and fees cannot be negative.");
            return false;
        }

        String normalizedId = rateId.trim().toUpperCase();
        String normalizedType = vehicleType.trim().toUpperCase();

        ParkingRate rate = new ParkingRate(normalizedId, normalizedType, baseFee, hourlyRate, penaltyPerHour);
        rateRepository.save(rate);
        return true;
    }

    public Optional<ParkingRate> getRateById(String rateId) {
        if (!isValidString(rateId)) return Optional.empty();
        return rateRepository.findById(rateId.trim().toUpperCase());
    }

    public Optional<ParkingRate> getRateByVehicleType(String vehicleType) {
        if (!isValidString(vehicleType)) return Optional.empty();
        return rateRepository.findByVehicleType(vehicleType.trim());
    }

    public List<ParkingRate> getAllRates() {
        return rateRepository.findAll();
    }

    public boolean deleteRate(String rateId) {
        if (!isValidString(rateId)) return false;
        return rateRepository.deleteById(rateId.trim().toUpperCase());
    }

    /**
     * Calculates the total parking fee between entry and exit timestamps based on vehicle type tariff.
     *
     * @param vehicleType Type of vehicle (e.g. CAR, BIKE).
     * @param entryTime   Vehicle arrival timestamp.
     * @param exitTime    Vehicle departure timestamp.
     * @return Total calculated fee.
     */
    public double calculateFee(String vehicleType, LocalDateTime entryTime, LocalDateTime exitTime) {
        if (entryTime == null || exitTime == null) {
            throw new IllegalArgumentException("Entry time and exit time cannot be null.");
        }

        if (exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("Exit time cannot be earlier than entry time.");
        }

        Optional<ParkingRate> rateOpt = getRateByVehicleType(vehicleType);
        if (rateOpt.isEmpty()) {
            System.err.println("[Warning] No specific rate found for '" + vehicleType + "'. Using standard CAR rate.");
            rateOpt = getRateByVehicleType("CAR");
        }

        ParkingRate rate = rateOpt.orElse(new ParkingRate("DEFAULT", "DEFAULT", 20.0, 10.0, 20.0));

        Duration duration = Duration.between(entryTime, exitTime);
        long totalMinutes = duration.toMinutes();

        // 15-minute free grace period
        if (totalMinutes <= 15) {
            return 0.0;
        }

        // Initial 1 hour covered by base fee
        double fee = rate.getBaseFee();

        if (totalMinutes > 60) {
            long additionalMinutes = totalMinutes - 60;
            // Round up to nearest whole hour for billing
            long additionalHours = (long) Math.ceil(additionalMinutes / 60.0);
            fee += (additionalHours * rate.getHourlyRate());
        }

        return fee;
    }

    public ParkingRateRepository getRateRepository() {
        return rateRepository;
    }

    private boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }
}

