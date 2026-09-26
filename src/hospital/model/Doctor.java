package hospital.model;

/**
 * Concrete entity representing a hospital doctor.
 *
 * OOP principle - INHERITANCE: reuses id/name/age/gender/contact from Person.
 * OOP principle - POLYMORPHISM: overrides toFileString()/toReportString()
 * with Doctor-specific behaviour, distinct from Patient's.
 */
public class Doctor extends Person implements Reportable {
    private String specialization;
    private String department;

    public Doctor(String id, String name, int age, String gender, String contactNumber,
                  String specialization, String department) {
        super(id, name, age, gender, contactNumber);
        this.specialization = specialization;
        this.department = department;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String getRole() {
        return "Doctor";
    }

    @Override
    public String toFileString() {
        return String.join("|",
                getId(), getName(), String.valueOf(getAge()), getGender(), getContactNumber(),
                specialization, department);
    }

    public static Doctor fromFileString(String line) {
        String[] p = line.split("\\|", -1);
        return new Doctor(p[0], p[1], Integer.parseInt(p[2]), p[3], p[4], p[5], p[6]);
    }

    @Override
    public String toReportString() {
        return String.format("Doctor  %-8s | %-18s | Specialization: %-15s | Department: %s",
                getId(), getName(), specialization, department);
    }
}
