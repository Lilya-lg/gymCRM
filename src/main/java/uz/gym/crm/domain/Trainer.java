package uz.gym.crm.domain;
import jakarta.persistence.*;

@Entity
public class Trainer extends User{

    private String specialization;

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

}
