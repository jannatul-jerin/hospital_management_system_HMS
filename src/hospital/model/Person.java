package hospital.model;

/**
 * Abstract base class for all people in the hospital system.
 *
 * OOP principle - ENCAPSULATION: all fields are private and only
 * reachable through public getters/setters, so internal state cannot
 * be corrupted directly by outside code.
 *
 * OOP principle - ABSTRACTION: this class defines the common shape of
 * a "person in the hospital" but leaves role-specific behaviour
 * (getRole, toFileString) to be defined by subclasses.
 */
public abstract class Person {
    private String id;
    private String name;
    private int age;
    private String gender;
    private String contactNumber;

    public Person(String id, String name, int age, String gender, String contactNumber) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contactNumber = contactNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    /** Every concrete subclass must say what role it plays. */
    public abstract String getRole();

    /** Every concrete subclass must know how to serialize itself for file storage. */
    public abstract String toFileString();

    @Override
    public String toString() {
        return String.format("[%s] %s (ID: %s, Age: %d, Gender: %s)", getRole(), name, id, age, gender);
    }
}
