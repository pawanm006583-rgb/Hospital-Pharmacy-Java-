public class Doctor {
    private String id, name, specialization;

    public Doctor(String id, String name, String specialization) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }

    public String toString() {
        return id + " | " + name + " | " + specialization;
    }
}
