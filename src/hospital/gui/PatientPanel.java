package hospital.gui;

import hospital.dao.PatientDAO;
import hospital.exceptions.DuplicateRecordException;
import hospital.exceptions.InvalidInputException;
import hospital.exceptions.RecordNotFoundException;
import hospital.model.Patient;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

/**
 * GUI panel that integrates PatientDAO's CRUD operations with a form
 * and table. All DAO calls are wrapped in try-catch so runtime and
 * business-rule errors are shown to the user instead of crashing the app.
 */
public class PatientPanel extends JPanel {
    private final PatientDAO patientDAO;

    private JTextField idField;
    private JTextField nameField;
    private JTextField ageField;
    private JTextField contactField;
    private JTextField diagnosisField;
    private JTextField dateField;
    private JTextField doctorIdField;
    private JTextField searchField;
    private JComboBox<String> genderBox;
    private JComboBox<String> statusBox;
    private JTable table;
    private DefaultTableModel tableModel;

    public PatientPanel(PatientDAO patientDAO) {
        this.patientDAO = patientDAO;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);

        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Patient Record"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField(10);
        nameField = new JTextField(12);
        ageField = new JTextField(4);
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        contactField = new JTextField(10);
        diagnosisField = new JTextField(15);
        dateField = new JTextField(10);
        doctorIdField = new JTextField(8);
        statusBox = new JComboBox<>(new String[]{"Admitted", "Discharged"});

        int row = 0;
        addField(panel, gbc, row++, "Patient ID:", idField);
        addField(panel, gbc, row++, "Name:", nameField);
        addField(panel, gbc, row++, "Age:", ageField);
        addField(panel, gbc, row++, "Gender:", genderBox);
        addField(panel, gbc, row++, "Contact:", contactField);
        addField(panel, gbc, row++, "Diagnosis:", diagnosisField);
        addField(panel, gbc, row++, "Admission Date (YYYY-MM-DD):", dateField);
        addField(panel, gbc, row++, "Assigned Doctor ID:", doctorIdField);
        addField(panel, gbc, row++, "Status:", statusBox);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");

        addBtn.addActionListener(e -> handleAdd());
        updateBtn.addActionListener(e -> handleUpdate());
        deleteBtn.addActionListener(e -> handleDelete());
        clearBtn.addActionListener(e -> clearForm());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 4;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void addField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        panel.add(new JLabel(label), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        panel.add(field, gbc);
    }

    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Search by Name");
        JButton refreshBtn = new JButton("Show All");
        searchBtn.addActionListener(e -> handleSearch());
        refreshBtn.addActionListener(e -> refreshTable());
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);

        String[] columns = {"ID", "Name", "Age", "Gender", "Contact", "Diagnosis", "Admission Date", "Doctor ID", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.getSelectionModel().addListSelectionListener(this::handleRowSelect);
        JScrollPane scrollPane = new JScrollPane(table);

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void handleRowSelect(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        idField.setText(tableModel.getValueAt(row, 0).toString());
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        ageField.setText(tableModel.getValueAt(row, 2).toString());
        genderBox.setSelectedItem(tableModel.getValueAt(row, 3).toString());
        contactField.setText(tableModel.getValueAt(row, 4).toString());
        diagnosisField.setText(tableModel.getValueAt(row, 5).toString());
        dateField.setText(tableModel.getValueAt(row, 6).toString());
        doctorIdField.setText(tableModel.getValueAt(row, 7).toString());
        statusBox.setSelectedItem(tableModel.getValueAt(row, 8).toString());
    }

    private Patient buildPatientFromForm() throws InvalidInputException {
        int age;
        try {
            age = Integer.parseInt(ageField.getText().trim());
        } catch (NumberFormatException ex) {
            throw new InvalidInputException("Age must be a valid whole number.");
        }
        return new Patient(
                idField.getText().trim(),
                nameField.getText().trim(),
                age,
                (String) genderBox.getSelectedItem(),
                contactField.getText().trim(),
                diagnosisField.getText().trim(),
                dateField.getText().trim(),
                doctorIdField.getText().trim(),
                (String) statusBox.getSelectedItem()
        );
    }

    private void handleAdd() {
        try {
            Patient p = buildPatientFromForm();
            patientDAO.add(p);
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Patient added successfully.");
        } catch (InvalidInputException | DuplicateRecordException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        try {
            Patient p = buildPatientFromForm();
            patientDAO.update(p);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Patient updated successfully.");
        } catch (InvalidInputException | RecordNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Update Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select or enter a Patient ID to delete.", "Missing ID", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete patient " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            patientDAO.delete(id);
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Patient deleted successfully.");
        } catch (RecordNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearch() {
        String keyword = searchField.getText().trim();
        List<Patient> results = keyword.isEmpty() ? patientDAO.getAll() : patientDAO.searchByName(keyword);
        populateTable(results);
    }

    public void refreshTable() {
        populateTable(patientDAO.getAll());
    }

    private void populateTable(List<Patient> patients) {
        tableModel.setRowCount(0);
        for (Patient p : patients) {
            tableModel.addRow(new Object[]{
                    p.getId(), p.getName(), p.getAge(), p.getGender(), p.getContactNumber(),
                    p.getDiagnosis(), p.getAdmissionDate(), p.getAssignedDoctorId(), p.getStatus()
            });
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        ageField.setText("");
        genderBox.setSelectedIndex(0);
        contactField.setText("");
        diagnosisField.setText("");
        dateField.setText("");
        doctorIdField.setText("");
        statusBox.setSelectedIndex(0);
        table.clearSelection();
    }
}
