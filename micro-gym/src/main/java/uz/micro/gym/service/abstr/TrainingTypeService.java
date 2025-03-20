package uz.micro.gym.service.abstr;


import uz.micro.gym.domain.PredefinedTrainingType;
import uz.micro.gym.domain.TrainingType;
import uz.micro.gym.dto.TrainingTypeDTO;

import java.util.List;

public interface TrainingTypeService {
    TrainingType getOrCreateTrainingType(PredefinedTrainingType predefinedType);

    List<TrainingTypeDTO> getAllTrainingTypes();
}
