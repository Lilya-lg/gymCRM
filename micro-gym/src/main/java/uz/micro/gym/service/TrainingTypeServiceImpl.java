package uz.micro.gym.service;

import org.springframework.stereotype.Service;
import uz.micro.gym.domain.PredefinedTrainingType;
import uz.micro.gym.domain.TrainingType;
import uz.micro.gym.dto.TrainingTypeDTO;
import uz.micro.gym.repository.TrainingTypeRepository;
import uz.micro.gym.service.abstr.TrainingTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {
    private final TrainingTypeRepository trainingTypeRepository;


    public TrainingTypeServiceImpl(TrainingTypeRepository trainingTypeRepository) {
        this.trainingTypeRepository = trainingTypeRepository;
    }


    public TrainingType getOrCreateTrainingType(PredefinedTrainingType predefinedType) {
        return trainingTypeRepository.getOrCreateTrainingType(predefinedType);
    }


    public List<TrainingTypeDTO> getAllTrainingTypes() {
        List<TrainingType> trainingTypes = trainingTypeRepository.findAll();
        return trainingTypes.stream()
                .map(type -> new TrainingTypeDTO(type.getId(), type.getTrainingType().name()))
                .collect(Collectors.toList());
    }
}
