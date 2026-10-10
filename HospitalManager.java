import java.time.LocalDate;
import java.util.*;

public class HospitalManager {

    private ArrayList<Doctor> doctors = new ArrayList<>();
    private ArrayList<Patient> patients = new ArrayList<>();
    private ArrayList<Medicine> medicines = new ArrayList<>();

    // Required collection concepts
    private LinkedList<Prescription> history = new LinkedList<>();
    private HashMap<String, Medicine> medicineMap = new HashMap<>();
    private TreeMap<String, Medicine> sortedMedicines =
            new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private ArrayList<Prescription> prescriptions = new ArrayList<>();
    private ArrayList<Bill> bills = new ArrayList<>();

    public HospitalManager() {
        loadSampleData();
    }

    private void loadSampleData() {
        doctors.add(new Doctor("D01", "Dr. Sharma", "General Physician"));
        doctors.add(new Doctor("D02", "Dr. Mehta", "Cardiologist"));

        addPatient(new Patient("P01", "Rahul", 21));
        addPatient(new Patient("P02", "Priya", 22));

        addMedicine(new Medicine("M01", "Paracetamol", 5,
                100, LocalDate.of(2027, 12, 31)));
        addMedicine(new Medicine("M02", "Amoxicillin", 12,
                50, LocalDate.of(2027, 10, 30)));
        addMedicine(new Medicine("M03", "Cetirizine", 8,
                75, LocalDate.of(2028, 3, 15)));
    }

    // ---------- PATIENT CRUD ----------

    public void addPatient(Patient p) {
        if (findPatient(p.getId()) != null)
            throw new IllegalArgumentException("Patient ID already exists.");
        patients.add(p);
    }

    public void updatePatient(String id, String name, int age) {
        Patient p = findPatient(id);
        if (p == null) throw new IllegalArgumentException("Patient not found.");
        p.setName(name);
        p.setAge(age);
    }

    public void deletePatient(String id) {
        Patient p = findPatient(id);
        if (p == null) throw new IllegalArgumentException("Patient not found.");
        patients.remove(p);
    }

    public Patient findPatient(String id) {
        for (Patient p : patients)
            if (p.getId().equalsIgnoreCase(id)) return p;
        return null;
    }

    public Doctor findDoctor(String id) {
    for (Doctor d : doctors)
        if (d.getId().equalsIgnoreCase(id)) return d;
    return null;
    }

    // ---------- MEDICINE CRUD ----------

    public void addMedicine(Medicine m) {
        if (medicineMap.containsKey(m.getId()))
            throw new IllegalArgumentException("Medicine ID already exists.");

        medicines.add(m);
        medicineMap.put(m.getId(), m);
        sortedMedicines.put(m.getName(), m);
    }

    public Medicine findMedicine(String id) {
        return medicineMap.get(id);
    }

    public void updateMedicine(String id, String name,
                            double price, int stock,
                            LocalDate expiry) {
        Medicine m = findMedicine(id);
        if (m == null) throw new IllegalArgumentException("Medicine not found.");

        sortedMedicines.remove(m.getName());
        m.setName(name);
        m.setPrice(price);
        m.setQuantity(stock);
        m.setExpiry(expiry);
        sortedMedicines.put(name, m);
    }

    public void deleteMedicine(String id) {
        Medicine m = findMedicine(id);
        if (m == null) throw new IllegalArgumentException("Medicine not found.");

        medicines.remove(m);
        medicineMap.remove(id);
        sortedMedicines.remove(m.getName());
    }

    // ---------- PRESCRIPTION ----------

    public void createPrescription(Prescription p)
            throws InvalidPrescriptionException {

        if (findPatient(p.getPatientId()) == null)
            throw new InvalidPrescriptionException("Patient does not exist.");

        if (findDoctor(p.getDoctorId()) == null)
            throw new InvalidPrescriptionException("Doctor does not exist.");

        if (p.getMedicines().isEmpty())
            throw new InvalidPrescriptionException(
                    "Prescription needs at least one medicine.");

        if (findPrescription(p.getId()) != null)
            throw new InvalidPrescriptionException(
                    "Prescription ID already exists.");

        prescriptions.add(p);
        history.addFirst(p);
    }

    public Prescription findPrescription(String id) {
        for (Prescription p : prescriptions)
            if (p.getId().equalsIgnoreCase(id)) return p;
        return null;
    }

    // ---------- PROCESS + BILL ----------

    public Bill processPrescription(String id)
            throws MedicineUnavailableException {

        Prescription p = findPrescription(id);

        if (p == null)
            throw new MedicineUnavailableException("Prescription not found.");

        if (p.isProcessed())
            throw new MedicineUnavailableException(
                    "Prescription is already processed.");

        double total = 0;

        // Check stock first
        for (int i = 0; i < p.getMedicines().size(); i++) {
            Medicine m = findMedicine(p.getMedicines().get(i));
            int qty = p.getQuantities().get(i);

            if (m.getQuantity() < qty)
                throw new MedicineUnavailableException(
                        m.getName() + " stock is insufficient.");

            if (m.getExpiry().isBefore(LocalDate.now()))
                throw new MedicineUnavailableException(
                        m.getName() + " is expired.");
        }

        // Reduce stock and calculate bill
        for (int i = 0; i < p.getMedicines().size(); i++) {
            Medicine m = findMedicine(p.getMedicines().get(i));
            int qty = p.getQuantities().get(i);

            m.setQuantity(m.getQuantity() - qty);
            total += m.getPrice() * qty;
        }

        p.setProcessed(true);

        Bill bill = new Bill(
                "B" + (bills.size() + 1),
                p.getId(),
                p.getPatientId(),
                total
        );

        bills.add(bill);
        return bill;
    }

    // ---------- SEARCH ----------

    public ArrayList<Medicine> searchMedicine(String text) {
        ArrayList<Medicine> result = new ArrayList<>();

        for (Medicine m : medicines)
            if (m.getId().toLowerCase().contains(text.toLowerCase())
                    || m.getName().toLowerCase().contains(text.toLowerCase()))
                result.add(m);

        return result;
    }

    public ArrayList<Patient> searchPatient(String text) {
        ArrayList<Patient> result = new ArrayList<>();

        for (Patient p : patients)
            if (p.getId().toLowerCase().contains(text.toLowerCase())
                    || p.getName().toLowerCase().contains(text.toLowerCase()))
                result.add(p);

        return result;
    }

    // ---------- SORTING ----------

    public ArrayList<Medicine> sortByName() {
        return new ArrayList<>(sortedMedicines.values());
    }

    public ArrayList<Medicine> sortByPrice() {
        ArrayList<Medicine> list = new ArrayList<>(medicines);
        list.sort(Comparator.comparingDouble(Medicine::getPrice));
        return list;
    }

    public ArrayList<Medicine> sortByExpiry() {
        ArrayList<Medicine> list = new ArrayList<>(medicines);
        list.sort(Comparator.comparing(Medicine::getExpiry));
        return list;
    }

    public void addStock(String id, int quantity) {
        Medicine m = findMedicine(id);
        if (m == null) throw new IllegalArgumentException("Medicine not found.");
        if (quantity <= 0) throw new IllegalArgumentException("Invalid quantity.");
        m.setQuantity(m.getQuantity() + quantity);
    }

    public ArrayList<Doctor> getDoctors() { return doctors; }
    public ArrayList<Patient> getPatients() { return patients; }
    public ArrayList<Medicine> getMedicines() { return medicines; }
    public LinkedList<Prescription> getHistory() { return history; }
    public ArrayList<Bill> getBills() { return bills; }
}
