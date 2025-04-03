package uz.gym.training.domain;

import uz.gym.crm.dto.TrainingSessionDTO;

import java.time.LocalDate;
import java.util.Objects;

public class TrainingSession {
  private Long id;
  private LocalDate trainingDate;
  private int duration;
  private String actionType;
  private String trainerUsername;
  private String firstName;
  private String lastName;

  public TrainingSession() {}

  public TrainingSession(TrainingSessionDTO sessionDTO) {
    this.trainingDate = sessionDTO.getTrainingDate();
    this.duration = sessionDTO.getDuration();
    this.actionType = sessionDTO.getActionType();
    this.trainerUsername = sessionDTO.getUsername();
    this.firstName = sessionDTO.getFirstName();
    this.lastName = sessionDTO.getLastName();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public String getTrainerUsername() {
    return trainerUsername;
  }

  public void setTrainerUsername(String trainerUsername) {
    this.trainerUsername = trainerUsername;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TrainingSession that = (TrainingSession) o;
    return duration == that.duration
        && Objects.equals(id, that.id)
        && Objects.equals(trainingDate, that.trainingDate)
        && Objects.equals(actionType, that.actionType)
        && Objects.equals(trainerUsername, that.trainerUsername)
        && Objects.equals(firstName, that.firstName)
        && Objects.equals(lastName, that.lastName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id, trainingDate, duration, actionType, trainerUsername, firstName, lastName);
  }
}
