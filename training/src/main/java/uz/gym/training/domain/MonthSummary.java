package uz.gym.training.domain;

public class MonthSummary {

  private String month;
  private int trainingSummaryDuration;

  public MonthSummary() {}

  public MonthSummary(String month, int trainingSummaryDuration) {
    this.month = month;
    this.trainingSummaryDuration = trainingSummaryDuration;
  }

  public String getMonth() {
    return month;
  }

  public void setMonth(String month) {
    this.month = month;
  }

  public int getTrainingSummaryDuration() {
    return trainingSummaryDuration;
  }

  public void setTrainingSummaryDuration(int trainingSummaryDuration) {
    this.trainingSummaryDuration = trainingSummaryDuration;
  }
}
