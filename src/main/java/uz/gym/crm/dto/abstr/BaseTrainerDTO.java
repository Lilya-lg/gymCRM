package uz.gym.crm.dto.abstr;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public abstract class BaseTrainerDTO {
    @NotBlank(message = "Firstname is required")
    private String firstName;
    @NotBlank(message = "Lastname is required")
    private String secondName;
    @NotNull(message = "Specialization is required")
    private String specialization;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
