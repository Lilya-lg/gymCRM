package uz.micro.gym.dto;

public class TrainingTypeDTO {
    private Long id;
    private String name;


    public TrainingTypeDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public TrainingTypeDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
