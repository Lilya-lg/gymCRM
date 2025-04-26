package uz.gym.training.dto;

import java.time.Month;

public class MonthSummaryDTO {
  private Month month;
  private int totalTrainingDuration;

  public MonthSummaryDTO(Month month, int totalTrainingDuration) {
    this.month = month;
    this.totalTrainingDuration = totalTrainingDuration;
  }

  public MonthSummaryDTO() {}

  public Month getMonth() {
    return month;
  }

  public int getTotalTrainingDuration() {
    return totalTrainingDuration;
  }

  public void setMonth(Month month) {
    this.month = month;
  }

  public void setTotalTrainingDuration(int totalTrainingDuration) {
    this.totalTrainingDuration = totalTrainingDuration;
  }
}
