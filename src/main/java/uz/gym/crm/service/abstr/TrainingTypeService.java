package uz.gym.crm.service.abstr;

import uz.gym.crm.domain.PredefinedTrainingType;
import uz.gym.crm.domain.TrainingType;
import uz.gym.crm.dto.TrainingTypeDTO;

import java.util.List;

public interface TrainingTypeService extends BaseService<TrainingType> {
    TrainingType getOrCreateTrainingType(PredefinedTrainingType predefinedType);

    List<TrainingTypeDTO> getAllTrainingTypes();
}
