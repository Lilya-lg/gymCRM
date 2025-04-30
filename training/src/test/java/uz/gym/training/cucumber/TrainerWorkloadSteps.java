package uz.gym.training.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import uz.gym.training.domain.MonthSummary;
import uz.gym.training.domain.TrainerTrainingSummary;
import uz.gym.training.domain.YearSummary;
import uz.gym.training.repository.abstr.TrainerTrainingSummaryRepository;
import uz.gym.training.util.TestJwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class TrainerWorkloadSteps {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TestJwtTokenProvider testJwtTokenProvider;
    @Autowired
    private TrainerTrainingSummaryRepository trainerTrainingSummaryRepository;
    private MvcResult mvcResult;
    private String jwtToken;

    @Given("the trainer with ID {int} exists")
    public void trainerExists(int trainerId) {
        jwtToken = testJwtTokenProvider.generateTestToken("testuser");
        trainerTrainingSummaryRepository.deleteByTrainerUsername(String.valueOf(trainerId));
        TrainerTrainingSummary trainerSummary = new TrainerTrainingSummary();
        trainerSummary.setTrainerUsername(String.valueOf(trainerId));
        trainerSummary.setTrainerFirstName("John");
        trainerSummary.setTrainerLastName("Doe");
        trainerSummary.setTrainerStatus(true);
        YearSummary yearSummary = new YearSummary();
        yearSummary.setYear(2025);

        MonthSummary monthSummary = new MonthSummary();
        monthSummary.setMonth("January");
        monthSummary.setTrainingSummaryDuration(10);

        yearSummary.getMonthsList().add(monthSummary);
        trainerSummary.getYearsList().add(yearSummary);

        trainerTrainingSummaryRepository.save(trainerSummary);
    }

    @Given("no trainer with ID {int} exists")
    public void trainerDoesNotExist(int trainerId) {
        jwtToken = testJwtTokenProvider.generateTestToken("testuser");
    }

    @When("the client retrieves the workload summary for trainer ID {int}")
    public void clientRetrievesSummary(int trainerId) throws Exception {
        mvcResult = mockMvc.perform(get("/api/trainings/" + trainerId+"/summary")
                        .header("Authorization", "Bearer " + jwtToken))
                .andReturn();
    }

    @Then("the response status should be {int}")
    public void checkResponseStatus(int expectedStatus) throws Exception {
        int actualStatus = mvcResult.getResponse().getStatus();
        if (actualStatus != expectedStatus) {
            throw new AssertionError("Expected status " + expectedStatus + " but got " + actualStatus);
        }
    }

    @Then("the response should include total training hours")
    public void checkTrainingHours() throws Exception {
        String body = mvcResult.getResponse().getContentAsString();
        if (!body.contains("totalTrainingDuration")) {
            throw new AssertionError("Expected totalHours in response body but got: " + body);
        }
    }
    @When("the client retrieves the workload summary for trainer ID {int} without authentication")
    public void clientRetrievesSummaryWithoutAuth(int trainerId) throws Exception {
        mvcResult = mockMvc.perform(get("/api/trainings/" + trainerId + "/summary"))
                .andReturn();
    }


}
