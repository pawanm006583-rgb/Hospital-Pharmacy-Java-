public class Bill {
    private String id, prescriptionId, patientId;
    private double total;

    public Bill(String id, String prescriptionId, String patientId,
                double total) {
        this.id = id;
        this.prescriptionId = prescriptionId;
        this.patientId = patientId;
        this.total = total;
    }

    public String toString() {
        return id + " | Prescription: " + prescriptionId +
            " | Patient: " + patientId +
            " | Total: ₹" + String.format("%.2f", total);
    }
}
