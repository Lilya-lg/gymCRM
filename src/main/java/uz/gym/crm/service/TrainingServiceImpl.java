package uz.gym.crm.service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uz.gym.crm.dao.TrainingDAO;
import uz.gym.crm.domain.Training;

@Service
public class TrainingServiceImpl extends BaseServiceImpl<Training, Integer> implements TrainingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TrainingServiceImpl.class);
    private final TrainingDAO trainingDAO;

    public TrainingServiceImpl(TrainingDAO trainingDAO) {
        super(trainingDAO);
        this.trainingDAO = trainingDAO;
    }


}

