package hospital.dao;

import hospital.exceptions.DuplicateRecordException;
import hospital.exceptions.InvalidInputException;
import hospital.exceptions.RecordNotFoundException;
import hospital.model.Patient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles all file-based persistence and CRUD logic for Patient records.
 * Data is stored as pipe-delimited lines in a plain text file so the
 * whole system works without any external database.
 */
public class PatientDAO {
    private final String filePath;

    public PatientDAO(String filePath) {
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
            System.err.println("Could not initialize patient data file: " + e.getMessage());
        }
    }

    /** READ (all). */
    public List<Patient> getAll() {
        List<Patient> patients = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    patients.add(Patient.fromFileString(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading patient records: " + e.getMessage());
        }
        return patients;
    }

    private void saveAll(List<Patient> patients) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Patient p : patients) {
                writer.write(p.toFileString());
                writer.newLine();
            }
        }
    }

    /** Business-rule validation, enforced before every add/update. */
    public void validate(Patient p) throws InvalidInputException {
        if (p.getId() == null || p.getId().trim().isEmpty()) {
            throw new InvalidInputException("Patient ID cannot be empty.");
        }
        if (p.getName() == null || p.getName().trim().isEmpty()) {
            throw new InvalidInputException("Patient name cannot be empty.");
        }
        if (p.getAge() <= 0 || p.getAge() > 130) {
            throw new InvalidInputException("Patient age must be between 1 and 130.");
        }
        if (p.getGender() == null || p.getGender().trim().isEmpty()) {
            throw new InvalidInputException("Gender cannot be empty.");
        }
    }

    /** CREATE. */
    public void add(Patient patient) throws InvalidInputException, DuplicateRecordException, IOException {
        validate(patient);
        List<Patient> patients = getAll();
        for (Patient p : patients) {
            if (p.getId().equalsIgnoreCase(patient.getId())) {
                throw new DuplicateRecordException("A patient with ID " + patient.getId() + " already exists.");
            }
        }
        patients.add(patient);
        saveAll(patients);
    }

    /** UPDATE. */
    public void update(Patient patient) throws InvalidInputException, RecordNotFoundException, IOException {
        validate(patient);
        List<Patient> patients = getAll();
        boolean found = false;
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getId().equalsIgnoreCase(patient.getId())) {
                patients.set(i, patient);
                found = true;
                break;
            }
        }
        if (!found) {
            throw new RecordNotFoundException("No patient found with ID " + patient.getId());
        }
        saveAll(patients);
    }

    /** DELETE. */
    public void delete(String id) throws RecordNotFoundException, IOException {
        List<Patient> patients = getAll();
        boolean removed = patients.removeIf(p -> p.getId().equalsIgnoreCase(id));
        if (!removed) {
            throw new RecordNotFoundException("No patient found with ID " + id);
        }
        saveAll(patients);
    }

    /** READ (single, by ID). */
    public Patient findById(String id) throws RecordNotFoundException {
        for (Patient p : getAll()) {
            if (p.getId().equalsIgnoreCase(id)) {
                return p;
            }
        }
        throw new RecordNotFoundException("No patient found with ID " + id);
    }

    /** READ (search by partial name match). */
    public List<Patient> searchByName(String keyword) {
        List<Patient> results = new ArrayList<>();
        for (Patient p : getAll()) {
            if (p.getName().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(p);
            }
        }
        return results;
    }
}
