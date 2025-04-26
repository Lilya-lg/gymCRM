package uz.gym.training.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "trainer_training_summary")
@CompoundIndex(name = "trainer_name_idx", def = "{'trainerFirstName': 1, 'trainerLastName': 1}")
public class TrainerTrainingSummary {

  @Id private String id;

  private String trainerUsername;
  private String trainerFirstName;
  private String trainerLastName;
  private Boolean trainerStatus;

  private List<YearSummary> yearsList = new ArrayList<>();

  public TrainerTrainingSummary() {}

  public TrainerTrainingSummary(
      String trainerUsername,
      String trainerFirstName,
      String trainerLastName,
      Boolean trainerStatus) {
    this.trainerUsername = trainerUsername;
    this.trainerFirstName = trainerFirstName;
    this.trainerLastName = trainerLastName;
    this.trainerStatus = trainerStatus;
  }

  public String getId() {
    return id;
  }

  public String getTrainerUsername() {
    return trainerUsername;
  }

  public void setTrainerUsername(String trainerUsername) {
    this.trainerUsername = trainerUsername;
  }

  public String getTrainerFirstName() {
    return trainerFirstName;
  }

  public void setTrainerFirstName(String trainerFirstName) {
    this.trainerFirstName = trainerFirstName;
  }

  public String getTrainerLastName() {
    return trainerLastName;
  }

  public void setTrainerLastName(String trainerLastName) {
    this.trainerLastName = trainerLastName;
  }

  public Boolean getTrainerStatus() {
    return trainerStatus;
  }

  public void setTrainerStatus(Boolean trainerStatus) {
    this.trainerStatus = trainerStatus;
  }

  public List<YearSummary> getYearsList() {
    return yearsList;
  }

  public void setYearsList(List<YearSummary> yearsList) {
    this.yearsList = yearsList;
  }
}
