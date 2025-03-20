package uz.gym.training.dto;

import java.util.List;
import java.util.Map;

public class TrainerSummaryDTO {
    private String username;
    private String firstName;
    private String lastName;
    private String status;
    private List<Integer> years;
    private Map<Integer, List<MonthSummaryDTO>> monthlySummaries;

    public TrainerSummaryDTO() {
    }

    public TrainerSummaryDTO(String username, String firstName, String lastName, String status,
                             List<Integer> years, Map<Integer, List<MonthSummaryDTO>> monthlySummaries) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.years = years;
        this.monthlySummaries = monthlySummaries;
    }

    public TrainerSummaryDTO(String john_doe, List<Object> emptyList) {
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getStatus() {
        return status;
    }

    public List<Integer> getYears() {
        return years;
    }

    public Map<Integer, List<MonthSummaryDTO>> getMonthlySummaries() {
        return monthlySummaries;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }

    public void setMonthlySummaries(Map<Integer, List<MonthSummaryDTO>> monthlySummaries) {
        this.monthlySummaries = monthlySummaries;
    }
}
