package uz.gym.crm.dto.abstr;


import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public abstract class BaseTrainingListDTO {
    @NotBlank(message = "Username is required")
    private String username;
    private LocalDate periodFrom;
    private LocalDate periodTo;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(LocalDate periodFrom) {
        this.periodFrom = periodFrom;
    }

    public LocalDate getPeriodTo() {
        return periodTo;
    }

    public void setPeriodTo(LocalDate periodTo) {
        this.periodTo = periodTo;
    }

}
