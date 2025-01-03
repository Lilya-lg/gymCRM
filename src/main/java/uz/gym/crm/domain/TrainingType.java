package uz.gym.crm.domain;

import javax.persistence.*;

@Entity
@Table(name = "training_types")
public class TrainingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "training_type", nullable = false, unique = true, updatable = false)
    private String trainingType;

    protected TrainingType() {

    }

    public TrainingType(String trainingType) {
        this.trainingType = trainingType;
    }

    public Long getId() {
        return id;
    }

    public String getTrainingType() {
        return trainingType;
    }

}

