package app.task.model;

public enum TaskStatus {
    NOT_STARTED("TO DO"),
    IN_PROGRESS("DOING"),
    COMPLETED("DONE"),
    PASSED("PASSED");

    private final String label;

    TaskStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
