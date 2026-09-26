package hospital.gui;

import hospital.dao.DoctorDAO;
import hospital.dao.PatientDAO;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * Top-level window: a tabbed pane hosting the Patients, Doctors,
 * and Reports panels, each backed by its own DAO.
 */
public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Hospital Patient Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);

        PatientDAO patientDAO = new PatientDAO("data/patients.txt");
        DoctorDAO doctorDAO = new DoctorDAO("data/doctors.txt");

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Patients", new PatientPanel(patientDAO));
        tabbedPane.addTab("Doctors", new DoctorPanel(doctorDAO));
        tabbedPane.addTab("Reports", new ReportPanel(patientDAO, doctorDAO));

        add(tabbedPane);
    }
}
