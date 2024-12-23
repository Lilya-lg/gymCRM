package uz.gym.crm.domain;

public enum TrainingType {
    YOGA("Yoga"),
    CARDIO("Cardio"),
    CYCLE("Cycle"),
    PILATES("Pilates");

    private final String name;

    TrainingType(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return name;
    }
}

