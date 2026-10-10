import java.util.ArrayList;

public class Prescription {
    private String id, patientId, doctorId;
    private ArrayList<String> medicines = new ArrayList<>();
    private ArrayList<Integer> quantities = new ArrayList<>();
    private boolean processed;

    public Prescription(String id, String patientId, String doctorId) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
    }

    public void addMedicine(String medicineId, int quantity) {
        medicines.add(medicineId);
        quantities.add(quantity);
    }

    public String getId() { return id; }
    public String getPatientId() { return patientId; }
    public String getDoctorId() { return doctorId; }
    public ArrayList<String> getMedicines() { return medicines; }
    public ArrayList<Integer> getQuantities() { return quantities; }
    public boolean isProcessed() { return processed; }
    public void setProcessed(boolean processed) { this.processed = processed; }

    public String toString() {
        return id + " | Patient: " + patientId +
            " | Doctor: " + doctorId +
            " | Medicines: " + medicines.size() +
            " | " + (processed ? "Processed" : "Pending");
    }
}
