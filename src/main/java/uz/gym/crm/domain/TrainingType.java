package uz.gym.crm.domain;


import jakarta.persistence.*;

@Entity
@Table(name = "training_types")
public class TrainingType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Enumerated(EnumType.STRING)
    private PredefinedTrainingType trainingType;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PredefinedTrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(PredefinedTrainingType trainingType) {
        this.trainingType = trainingType;
    }

}

