package uz.gym.crm.dto;


import java.time.LocalDate;

public class TrainingSessionDTO {
  private String username;
  private String firstName;
  private String lastName;
  private boolean isActive;
  private LocalDate trainingDate;
  private int duration;
  private String actionType;
  private String trainingName;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }

  public LocalDate getTrainingDate() {
    return trainingDate;
  }

  public void setTrainingDate(LocalDate trainingDate) {

    this.trainingDate = trainingDate;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }

  public String getActionType() {
    return actionType;
  }

  public void setActionType(String actionType) {
    this.actionType = actionType;
  }

  public String getTrainingName() {
    return trainingName;
  }

  public void setTrainingName(String trainingName) {
    this.trainingName = trainingName;
  }
}
