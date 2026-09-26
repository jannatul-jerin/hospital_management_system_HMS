package hospital.dao;

import hospital.exceptions.DuplicateRecordException;
import hospital.exceptions.InvalidInputException;
import hospital.exceptions.RecordNotFoundException;
import hospital.model.Doctor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all file-based persistence and CRUD logic for Doctor records.
 * Mirrors PatientDAO's structure to keep the persistence layer consistent.
 */
public class DoctorDAO {
    private final String filePath;

    public DoctorDAO(String filePath) {
        this.filePath = filePath;
        ensureFileExists();
    }

    private void ensureFileExists() {
        File file = new File(filePath);
        File parent = file.getParentFile();
        try {
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Could not initialize doctor data file: " + e.getMessage());
        }
    }

    public List<Doctor> getAll() {
        List<Doctor> doctors = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    doctors.add(Doctor.fromFileString(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading doctor records: " + e.getMessage());
        }
        return doctors;
    }

    private void saveAll(List<Doctor> doctors) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Doctor d : doctors) {
                writer.write(d.toFileString());
                writer.newLine();
            }
        }
    }

    public void validate(Doctor d) throws InvalidInputException {
        if (d.getId() == null || d.getId().trim().isEmpty()) {
            throw new InvalidInputException("Doctor ID cannot be empty.");
        }
        if (d.getName() == null || d.getName().trim().isEmpty()) {
            throw new InvalidInputException("Doctor name cannot be empty.");
        }
        if (d.getSpecialization() == null || d.getSpecialization().trim().isEmpty()) {
            throw new InvalidInputException("Specialization cannot be empty.");
        }
    }

    public void add(Doctor doctor) throws InvalidInputException, DuplicateRecordException, IOException {
        validate(doctor);
        List<Doctor> doctors = getAll();
        for (Doctor d : doctors) {
            if (d.getId().equalsIgnoreCase(doctor.getId())) {
                throw new DuplicateRecordException("A doctor with ID " + doctor.getId() + " already exists.");
            }
        }
        doctors.add(doctor);
        saveAll(doctors);
    }

    public void update(Doctor doctor) throws InvalidInputException, RecordNotFoundException, IOException {
        validate(doctor);
        List<Doctor> doctors = getAll();
        boolean found = false;
        for (int i = 0; i < doctors.size(); i++) {
            if (doctors.get(i).getId().equalsIgnoreCase(doctor.getId())) {
                doctors.set(i, doctor);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new RecordNotFoundException("No doctor found with ID " + doctor.getId());
        }
        saveAll(doctors);
    }

    public void delete(String id) throws RecordNotFoundException, IOException {
        List<Doctor> doctors = getAll();
        boolean removed = doctors.removeIf(d -> d.getId().equalsIgnoreCase(id));
        if (!removed) {
            throw new RecordNotFoundException("No doctor found with ID " + id);
        }
        saveAll(doctors);
    }

    public List<Doctor> searchByName(String keyword) {
        List<Doctor> results = new ArrayList<>();
        for (Doctor d : getAll()) {
            if (d.getName().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(d);
            }
        }
        return results;
    }
}
