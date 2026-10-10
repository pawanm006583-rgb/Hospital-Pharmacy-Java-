import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import javax.swing.*;

public class HospitalGUI extends JFrame {

    private HospitalManager manager = new HospitalManager();
    private JTextArea output = new JTextArea();

    public HospitalGUI() {
        setTitle("Hospital Pharmacy System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel title = new JLabel(
                "HOSPITAL PHARMACY & PRESCRIPTION SYSTEM",
                SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JPanel buttons = new JPanel(new GridLayout(4, 3, 8, 8));

        button(buttons, "Doctors", e -> showDoctors());
        button(buttons, "Patients", e -> patientMenu());
        button(buttons, "Medicines", e -> medicineMenu());
        button(buttons, "Create Prescription", e -> createPrescription());
        button(buttons, "Process Prescription", e -> processPrescription());
        button(buttons, "Search Medicine", e -> searchMedicine());
        button(buttons, "Sort Medicines", e -> sortMedicine());
        button(buttons, "Add Stock", e -> addStock());
        button(buttons, "Bills", e -> showBills());
        button(buttons, "Prescription History", e -> showHistory());
        button(buttons, "Exit", e -> System.exit(0));

        output.setEditable(false);
        output.setFont(new Font("Monospaced", Font.PLAIN, 13));

        setLayout(new BorderLayout(10, 10));
        add(title, BorderLayout.NORTH);
        add(buttons, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane(output);
scrollPane.setPreferredSize(new Dimension(900, 220));

add(scrollPane, BorderLayout.SOUTH);

        output.setText("Welcome to Hospital Pharmacy System!");
    }

    private void button(JPanel panel, String name,
                        java.awt.event.ActionListener action) {
        JButton b = new JButton(name);
        b.addActionListener(action);
        panel.add(b);
    }

    // ---------- DOCTORS ----------

    private void showDoctors() {
    StringBuilder s = new StringBuilder();

    s.append("========== DOCTORS ==========\n\n");

    for (Doctor d : manager.getDoctors()) {
        s.append("Doctor ID      : ").append(d.getId()).append("\n");
        s.append("Name           : ").append(d.getName()).append("\n");
        s.append("Specialization : ").append(d.getSpecialization()).append("\n");
        s.append("-----------------------------\n");
    }

    output.setText(s.toString());
}

    // ---------- PATIENTS ----------

    private void patientMenu() {
        String[] options = {"Add", "Update", "Delete", "View"};
        int c = JOptionPane.showOptionDialog(
                this, "Patient Management", "Patients",
                0, JOptionPane.PLAIN_MESSAGE, null,
                options, options[0]);

        try {
            if (c == 0) {
                String id = input("Patient ID:");
                String name = input("Name:");
                int age = Integer.parseInt(input("Age:"));

                manager.addPatient(new Patient(id, name, age));
                output.setText("Patient added successfully.");
            }
            else if (c == 1) {
                String id = input("Patient ID:");
                String name = input("New Name:");
                int age = Integer.parseInt(input("New Age:"));

                manager.updatePatient(id, name, age);
                output.setText("Patient updated successfully.");
            }
            else if (c == 2) {
                manager.deletePatient(input("Patient ID:"));
                output.setText("Patient deleted successfully.");
            }
            else if (c == 3) {
                StringBuilder s = new StringBuilder("PATIENTS\n-----------------------------\n");
                for (Patient p : manager.getPatients()) s.append(p).append("\n");
                output.setText(s.toString());
            }
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    // ---------- MEDICINES ----------

    private void medicineMenu() {
        String[] options = {"Add", "Update", "Delete", "View"};
        int c = JOptionPane.showOptionDialog(
                this, "Medicine Management", "Medicines",
                0, JOptionPane.PLAIN_MESSAGE, null,
                options, options[0]);

        try {
            if (c == 0) {
                String id = input("Medicine ID:");
                String name = input("Medicine Name:");
                double price = Double.parseDouble(input("Price:"));
                int stock = Integer.parseInt(input("Stock:"));
                LocalDate expiry = LocalDate.parse(
                        input("Expiry (YYYY-MM-DD):"));

                manager.addMedicine(
                        new Medicine(id, name, price, stock, expiry));

                output.setText("Medicine added successfully.");
            }
            else if (c == 1) {
                String id = input("Medicine ID:");
                String name = input("New Name:");
                double price = Double.parseDouble(input("New Price:"));
                int stock = Integer.parseInt(input("New Stock:"));
                LocalDate expiry = LocalDate.parse(
                        input("New Expiry (YYYY-MM-DD):"));

                manager.updateMedicine(id, name, price, stock, expiry);
                output.setText("Medicine updated successfully.");
            }
            else if (c == 2) {
                manager.deleteMedicine(input("Medicine ID:"));
                output.setText("Medicine deleted successfully.");
            }
            else if (c == 3) {
                showMedicines(manager.getMedicines());
            }
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    // ---------- PRESCRIPTION ----------

    private void createPrescription() {
        try {
            String id = input("Prescription ID:");
            String patient = input("Patient ID:");
            String doctor = input("Doctor ID:");

            Prescription p =
                    new Prescription(id, patient, doctor);

            int n = Integer.parseInt(
                    input("Number of medicines:"));

            for (int i = 0; i < n; i++) {
                String medicine = input("Medicine ID:");
                int qty = Integer.parseInt(input("Quantity:"));

                if (manager.findMedicine(medicine) == null)
                    throw new InvalidPrescriptionException(
                            "Medicine not found.");

                if (qty <= 0)
                    throw new InvalidPrescriptionException(
                            "Quantity must be positive.");

                p.addMedicine(medicine, qty);
            }

            manager.createPrescription(p);
            output.setText("Prescription created successfully.");

        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    // ---------- PROCESS ----------

    private void processPrescription() {
        try {
            String id = input("Prescription ID:");

            Bill bill = manager.processPrescription(id);

            output.setText(
                    "PRESCRIPTION PROCESSED\n" +
                    "-----------------------------\n" +
                    "Bill generated successfully!\n\n" +
                    bill
            );

        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    // ---------- SEARCH ----------

    private void searchMedicine() {
        String text = input("Medicine ID or Name:");

        showMedicines(manager.searchMedicine(text));
    }

    // ---------- SORT ----------

    private void sortMedicine() {
        String[] options = {"Name", "Price", "Expiry"};

        int c = JOptionPane.showOptionDialog(
                this, "Sort Medicines By", "Sorting",
                0, JOptionPane.PLAIN_MESSAGE, null,
                options, options[0]);

        if (c == 0) showMedicines(manager.sortByName());
        if (c == 1) showMedicines(manager.sortByPrice());
        if (c == 2) showMedicines(manager.sortByExpiry());
    }

    // ---------- STOCK ----------

    private void addStock() {
        try {
            String id = input("Medicine ID:");
            int qty = Integer.parseInt(input("Quantity to add:"));

            manager.addStock(id, qty);

            output.setText(
                    "Stock updated successfully.\n\n" +
                    manager.findMedicine(id));

        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    // ---------- BILL + HISTORY ----------

    private void showBills() {
        StringBuilder s =
                new StringBuilder("BILLS\n-----------------------------\n");

        for (Bill b : manager.getBills())
            s.append(b).append("\n");

        output.setText(s.toString());
    }

    private void showHistory() {
        StringBuilder s =
                new StringBuilder(
                        "PRESCRIPTION HISTORY\n" +
                        "-----------------------------\n");

        for (Prescription p : manager.getHistory())
            s.append(p).append("\n");

        output.setText(s.toString());
    }

    private void showMedicines(List<Medicine> list) {
        StringBuilder s =
                new StringBuilder(
                        "MEDICINES\n" +
                        "----------------------------------------\n");

        for (Medicine m : list)
            s.append(m).append("\n");

        output.setText(s.toString());
    }

    // ---------- HELPERS ----------

    private String input(String message) {
        String value =
                JOptionPane.showInputDialog(this, message);

        if (value == null || value.trim().isEmpty())
            throw new IllegalArgumentException(
                    "Input cannot be empty.");

        return value.trim();
    }

    private void error(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Error",
                JOptionPane.ERROR_MESSAGE);

        output.setText("ERROR: " + message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> new HospitalGUI().setVisible(true));
    }
}
