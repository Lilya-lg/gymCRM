package uz.micro.gym.domain;

public enum PredefinedTrainingType {
    YOGA("Yoga"), CARDIO("Cardio"), CYCLE("Cycle"), PILATES("Pilates");

    private final String displayName;

    PredefinedTrainingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static PredefinedTrainingType fromName(String name) {
        for (PredefinedTrainingType type : values()) {
            if (type.getDisplayName().equalsIgnoreCase(name)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid specialization: " + name);
    }
}
