package hospital.gui;

import hospital.dao.DoctorDAO;
import hospital.exceptions.DuplicateRecordException;
import hospital.exceptions.InvalidInputException;
import hospital.exceptions.RecordNotFoundException;
import hospital.model.Doctor;

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
 * GUI panel that integrates DoctorDAO's CRUD operations with a form
 * and table, mirroring PatientPanel's structure.
 */
public class DoctorPanel extends JPanel {
    private final DoctorDAO doctorDAO;

    private JTextField idField;
    private JTextField nameField;
    private JTextField ageField;
    private JTextField contactField;
    private JTextField specializationField;
    private JTextField departmentField;
    private JTextField searchField;
    private JComboBox<String> genderBox;
    private JTable table;
    private DefaultTableModel tableModel;

    public DoctorPanel(DoctorDAO doctorDAO) {
        this.doctorDAO = doctorDAO;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);

        refreshTable();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Doctor Record"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        idField = new JTextField(10);
        nameField = new JTextField(12);
        ageField = new JTextField(4);
        genderBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        contactField = new JTextField(10);
        specializationField = new JTextField(15);
        departmentField = new JTextField(12);

        int row = 0;
        addField(panel, gbc, row++, "Doctor ID:", idField);
        addField(panel, gbc, row++, "Name:", nameField);
        addField(panel, gbc, row++, "Age:", ageField);
        addField(panel, gbc, row++, "Gender:", genderBox);
        addField(panel, gbc, row++, "Contact:", contactField);
        addField(panel, gbc, row++, "Specialization:", specializationField);
        addField(panel, gbc, row++, "Department:", departmentField);

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

        String[] columns = {"ID", "Name", "Age", "Gender", "Contact", "Specialization", "Department"};
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
        specializationField.setText(tableModel.getValueAt(row, 5).toString());
        departmentField.setText(tableModel.getValueAt(row, 6).toString());
    }

    private Doctor buildDoctorFromForm() throws InvalidInputException {
        int age;
        try {
            age = Integer.parseInt(ageField.getText().trim());
        } catch (NumberFormatException ex) {
            throw new InvalidInputException("Age must be a valid whole number.");
        }
        return new Doctor(
                idField.getText().trim(),
                nameField.getText().trim(),
                age,
                (String) genderBox.getSelectedItem(),
                contactField.getText().trim(),
                specializationField.getText().trim(),
                departmentField.getText().trim()
        );
    }

    private void handleAdd() {
        try {
            Doctor d = buildDoctorFromForm();
            doctorDAO.add(d);
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Doctor added successfully.");
        } catch (InvalidInputException | DuplicateRecordException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Validation Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleUpdate() {
        try {
            Doctor d = buildDoctorFromForm();
            doctorDAO.update(d);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Doctor updated successfully.");
        } catch (InvalidInputException | RecordNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Update Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDelete() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select or enter a Doctor ID to delete.", "Missing ID", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete doctor " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            doctorDAO.delete(id);
            refreshTable();
            clearForm();
            JOptionPane.showMessageDialog(this, "Doctor deleted successfully.");
        } catch (RecordNotFoundException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Delete Error", JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleSearch() {
        String keyword = searchField.getText().trim();
        List<Doctor> results = keyword.isEmpty() ? doctorDAO.getAll() : doctorDAO.searchByName(keyword);
        populateTable(results);
    }

    public void refreshTable() {
        populateTable(doctorDAO.getAll());
    }

    private void populateTable(List<Doctor> doctors) {
        tableModel.setRowCount(0);
        for (Doctor d : doctors) {
            tableModel.addRow(new Object[]{
                    d.getId(), d.getName(), d.getAge(), d.getGender(), d.getContactNumber(),
                    d.getSpecialization(), d.getDepartment()
            });
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        ageField.setText("");
        genderBox.setSelectedIndex(0);
        contactField.setText("");
        specializationField.setText("");
        departmentField.setText("");
        table.clearSelection();
    }
}
