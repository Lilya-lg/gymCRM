package uz.gym.crm.dao;

import org.hibernate.Session;
import uz.gym.crm.domain.TrainingType;

public class TrainingTypeDAOImpl extends BaseDAOImpl<TrainingType>{
    private final Session session;

    public TrainingTypeDAOImpl(Session session) {

        super(TrainingType.class, session);
        this.session = session;
    }
}
