package hospital.gui;

import hospital.dao.DoctorDAO;
import hospital.dao.PatientDAO;
import hospital.model.Doctor;
import hospital.model.Patient;
import hospital.model.Reportable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Generates a text report over all patients and doctors.
 *
 * OOP principle - POLYMORPHISM in action: the loop below only knows
 * about the Reportable interface, yet calling toReportString() on a
 * Patient and on a Doctor produces two completely different formats,
 * resolved at runtime.
 */
public class ReportPanel extends JPanel {
    private final PatientDAO patientDAO;
    private final DoctorDAO doctorDAO;
    private final JTextArea reportArea;

    public ReportPanel(PatientDAO patientDAO, DoctorDAO doctorDAO) {
        this.patientDAO = patientDAO;
        this.doctorDAO = doctorDAO;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        JScrollPane scrollPane = new JScrollPane(reportArea);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton generateBtn = new JButton("Generate Report");
        JButton saveBtn = new JButton("Save Report to File");
        generateBtn.addActionListener(e -> generateReport());
        saveBtn.addActionListener(e -> saveReport());
        buttonPanel.add(generateBtn);
        buttonPanel.add(saveBtn);

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=======================================\n");
        sb.append(" HOSPITAL MANAGEMENT SYSTEM - REPORT\n");
        sb.append(" Generated: ").append(LocalDateTime.now()).append("\n");
        sb.append("=======================================\n\n");

        List<Patient> patients = patientDAO.getAll();
        List<Doctor> doctors = doctorDAO.getAll();

        long admitted = patients.stream().filter(p -> p.getStatus().equalsIgnoreCase("Admitted")).count();
        long discharged = patients.size() - admitted;

        sb.append("Total Patients: ").append(patients.size())
                .append(" (Admitted: ").append(admitted).append(", Discharged: ").append(discharged).append(")\n");
        sb.append("Total Doctors: ").append(doctors.size()).append("\n\n");

        sb.append("--- PATIENT RECORDS ---\n");
        for (Reportable r : patients) {
            sb.append(r.toReportString()).append("\n");
        }

        sb.append("\n--- DOCTOR RECORDS ---\n");
        for (Reportable r : doctors) {
            sb.append(r.toReportString()).append("\n");
        }

        reportArea.setText(sb.toString());
    }

    private void saveReport() {
        if (reportArea.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Generate a report first.", "Nothing to Save", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("hospital_report.txt"));
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(chooser.getSelectedFile()))) {
                writer.print(reportArea.getText());
                JOptionPane.showMessageDialog(this, "Report saved successfully.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to save report: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
