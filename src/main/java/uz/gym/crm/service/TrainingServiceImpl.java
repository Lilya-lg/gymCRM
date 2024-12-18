package uz.gym.crm.service;

import org.springframework.stereotype.Service;
import uz.gym.crm.dao.BaseDAO;
import uz.gym.crm.domain.Training;

@Service
public class TrainingServiceImpl extends BaseServiceImpl<Training> implements BaseService<Training> {

    public TrainingServiceImpl(BaseDAO<Training> trainingDAO) {
        super(trainingDAO);
    }


}

