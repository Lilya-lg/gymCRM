package uz.gym.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.gym.crm.domain.PredefinedTrainingType;
import uz.gym.crm.domain.TrainingType;
import uz.gym.crm.dto.TrainingTypeDTO;
import uz.gym.crm.repository.TrainingTypeRepository;
import uz.gym.crm.service.abstr.TrainingTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainingTypeServiceImpl implements TrainingTypeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);
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
