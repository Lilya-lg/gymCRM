package uz.micro.gym.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/resources/features",
    glue = "uz.micro.gym.cucumber",
    plugin = {"pretty", "html:target/cucumber-report.html"})
public class CucumberTestRunner {}
