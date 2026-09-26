# Hospital Patient Management System (CSE282 Complex Engineering Project)

A Java Swing desktop application for managing hospital patients and doctors,
with file-based persistence, custom exception handling, and full CRUD.

## Project Structure

```
HospitalManagementSystem/
├── src/
│   └── hospital/
│       ├── Main.java                  # Entry point
│       ├── model/
│       │   ├── Person.java            # Abstract base (encapsulation + abstraction)
│       │   ├── Patient.java           # extends Person, implements Reportable
│       │   ├── Doctor.java            # extends Person, implements Reportable
│       │   └── Reportable.java        # Interface used for polymorphism
│       ├── exceptions/
│       │   ├── InvalidInputException.java
│       │   ├── DuplicateRecordException.java
│       │   └── RecordNotFoundException.java
│       ├── dao/
│       │   ├── PatientDAO.java        # File-based CRUD for patients
│       │   └── DoctorDAO.java         # File-based CRUD for doctors
│       └── gui/
│           ├── MainFrame.java         # JFrame with tabs
│           ├── PatientPanel.java      # Patient CRUD UI
│           ├── DoctorPanel.java       # Doctor CRUD UI
│           └── ReportPanel.java       # Combined report generation
└── data/                              # Created automatically at runtime
    ├── patients.txt
    └── doctors.txt
```

## How to Run

### Option A — IntelliJ IDEA (recommended, matches the rubric's required IDE)
1. Open IntelliJ IDEA → **File > Open** → select the `HospitalManagementSystem` folder.
2. Mark `src` as **Sources Root** if IntelliJ doesn't detect it automatically
   (right-click `src` → Mark Directory as → Sources Root).
3. Open `src/hospital/Main.java` and click the green **Run** arrow next to `main`.
4. Take your GUI screenshots here for the report (Task 4).

### Option B — Command line
```bash
cd HospitalManagementSystem
javac -d out $(find src -name "*.java")
java -cp out hospital.Main
```

The app creates `data/patients.txt` and `data/doctors.txt` automatically on first run.

## How Each Rubric Task Is Covered

**Task 1 — OOP Principles (class hierarchy)**
- *Encapsulation*: all fields in `Person`, `Patient`, `Doctor` are `private` with public
  getters/setters.
- *Abstraction*: `Person` is `abstract` and declares `getRole()` / `toFileString()`
  without implementing them.
- *Inheritance*: `Patient` and `Doctor` both `extends Person`, reusing shared fields.
- *Polymorphism*: `Reportable` interface is implemented differently by `Patient` and
  `Doctor` — `ReportPanel` loops over a `List<Reportable>`-like usage and calls
  `toReportString()`, and the correct overridden version runs for each type. The
  `toFileString()` override in each subclass is a second example.

**Task 2 — CRUD, file persistence, exceptions**
- `PatientDAO` / `DoctorDAO` implement Create, Read (all/by id/by name), Update,
  Delete, all reading/writing a pipe-delimited text file (`data/patients.txt`,
  `data/doctors.txt`) via `BufferedReader`/`BufferedWriter`.
- Business rules are enforced in `validate()` and raise `InvalidInputException`.
- Duplicate IDs raise `DuplicateRecordException`; missing IDs on update/delete raise
  `RecordNotFoundException`. All three are custom checked exceptions.

**Task 3 — Java Swing GUI**
- `MainFrame` hosts a `JTabbedPane` with three panels: Patients, Doctors, Reports.
- Each panel has a form (`JTextField`/`JComboBox`), a `JTable` backed by
  `DefaultTableModel`, and Add/Update/Delete/Search buttons.
- Every DAO call is wrapped in `try-catch`, surfacing errors via `JOptionPane`
  dialogs rather than crashing (e.g. entering a non-numeric age, deleting a
  non-existent ID, adding a duplicate ID).

**Task 4 — Report**
- Use this README's structure and mapping as your outline: system design (DAO +
  model + GUI layering), OOP concept mapping (table above), IDE used (IntelliJ
  IDEA), and add your own GUI screenshots plus a short reflection section.

## Suggested Manual Test Cases (good for screenshots)
1. Add a doctor (e.g. D001) and a patient assigned to that doctor ID.
2. Try adding a patient with the same ID again → see the duplicate-record dialog.
3. Try adding a patient with a non-numeric age → see the validation dialog.
4. Update a patient's status from "Admitted" to "Discharged".
5. Delete a doctor, then generate a report to see the updated counts.
6. Generate and save a report from the Reports tab.
# hospital_management_system_HMS
# hospital_management_system_HMS
