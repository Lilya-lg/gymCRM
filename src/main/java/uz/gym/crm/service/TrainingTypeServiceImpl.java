package uz.gym.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.gym.crm.dao.*;
import uz.gym.crm.domain.PredefinedTrainingType;
import uz.gym.crm.domain.TrainingType;
import uz.gym.crm.dto.TrainingTypeDTO;
import uz.gym.crm.service.abstr.BaseServiceImpl;
import uz.gym.crm.service.abstr.TrainingTypeService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TrainingTypeServiceImpl extends BaseServiceImpl<TrainingType> implements TrainingTypeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);
    private final TrainingTypeDAOImpl trainingTypeDAO;
    @Autowired
    TrainingTypeService trainingTypeService;

    @Autowired
    public TrainingTypeServiceImpl(TrainingTypeDAOImpl trainingTypeDao, UserDAOImpl userDAO) {
        super(trainingTypeDao, userDAO);
        this.trainingTypeDAO = trainingTypeDao;
    }

    @Override
    public TrainingType getOrCreateTrainingType(PredefinedTrainingType predefinedType) {
        return trainingTypeDAO.getOrCreateTrainingType(predefinedType);
    }

    @Override
    public List<TrainingTypeDTO> getAllTrainingTypes() {
        List<TrainingType> trainingTypes = trainingTypeDAO.getAll();
        return trainingTypes.stream()
                .map(type -> new TrainingTypeDTO(type.getId(), type.getTrainingType().name()))
                .collect(Collectors.toList());
    }
}
