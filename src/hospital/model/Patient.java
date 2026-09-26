package hospital.model;

/**
 * Concrete entity representing a hospital patient.
 *
 * OOP principle - INHERITANCE: reuses id/name/age/gender/contact from Person.
 * OOP principle - POLYMORPHISM: overrides toFileString()/toReportString()
 * with Patient-specific behaviour, and implements Reportable.
 */
public class Patient extends Person implements Reportable {
    private String diagnosis;
    private String admissionDate;
    private String assignedDoctorId;
    private String status; // "Admitted" or "Discharged"

    public Patient(String id, String name, int age, String gender, String contactNumber,
                   String diagnosis, String admissionDate, String assignedDoctorId, String status) {
        super(id, name, age, gender, contactNumber);
        this.diagnosis = diagnosis;
        this.admissionDate = admissionDate;
        this.assignedDoctorId = assignedDoctorId;
        this.status = status;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getAdmissionDate() {
        return admissionDate;
    }

    public void setAdmissionDate(String admissionDate) {
        this.admissionDate = admissionDate;
    }

    public String getAssignedDoctorId() {
        return assignedDoctorId;
    }

    public void setAssignedDoctorId(String assignedDoctorId) {
        this.assignedDoctorId = assignedDoctorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String getRole() {
        return "Patient";
    }

    /** Pipe-delimited row used for file persistence. */
    @Override
    public String toFileString() {
        return String.join("|",
                getId(), getName(), String.valueOf(getAge()), getGender(), getContactNumber(),
                diagnosis, admissionDate, assignedDoctorId, status);
    }

    /** Rebuilds a Patient from a stored file line. */
    public static Patient fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        return new Patient(p[0], p[1], Integer.parseInt(p[2]), p[3], p[4], p[5], p[6], p[7], p[8]);
    }

    @Override
    public String toReportString() {
        return String.format("Patient %-8s | %-18s | Age: %-3d | Diagnosis: %-20s | Doctor ID: %-8s | Status: %s",
                getId(), getName(), getAge(), diagnosis, assignedDoctorId, status);
    }
}
