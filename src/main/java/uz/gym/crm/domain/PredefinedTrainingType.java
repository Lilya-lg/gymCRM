package uz.gym.crm.domain;

public enum PredefinedTrainingType {
    YOGA("Yoga"),
    CARDIO("Cardio"),
    CYCLE("Cycle"),
    PILATES("Pilates");

    private final String displayName;

    PredefinedTrainingType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
