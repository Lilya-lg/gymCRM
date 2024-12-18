package uz.gym.crm.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import uz.gym.crm.domain.*;

import java.io.InputStream;
import java.util.List;
import java.util.Map;


@Component
public class StorageInitializer implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageInitializer.class);
    private static final String TRAINER_STORAGE_BEAN = "trainerStorage";
    private static final String TRAINEE_STORAGE_BEAN = "traineeStorage";
    private static final String TRAINING_STORAGE_BEAN = "trainingStorage";
    private static final String USER_STORAGE_BEAN = "userStorage";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Value("${data.file.user}")
    private Resource userFilePath;

    @Value("${data.file.trainer}")
    private Resource trainerFilePath;

    @Value("${data.file.trainee}")
    private Resource traineeFilePath;

    @Value("${data.file.training}")
    private Resource trainingFilePath;

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            switch (beanName) {
                case TRAINER_STORAGE_BEAN:
                    LOGGER.info("Initializing trainerStorage with file: {}", trainerFilePath);
                    populateStorage((Map<Long, Trainer>) bean, trainerFilePath, Trainer.class);
                    LOGGER.info("trainerStorage initialized successfully.");
                    break;

                case TRAINEE_STORAGE_BEAN:
                    LOGGER.info("Initializing traineeStorage with file: {}", traineeFilePath);
                    populateStorage((Map<Long, Trainee>) bean, traineeFilePath, Trainee.class);
                    LOGGER.info("traineeStorage initialized successfully.");
                    break;

                case TRAINING_STORAGE_BEAN:
                    LOGGER.info("Initializing trainingStorage with file: {}", trainingFilePath);
                    populateStorage((Map<Long, Training>) bean, trainingFilePath, Training.class);
                    LOGGER.info("trainingStorage initialized successfully.");
                    break;

                case USER_STORAGE_BEAN:
                    LOGGER.info("Initializing userStorage with file: {}", userFilePath);
                    populateStorage((Map<Long, User>) bean, userFilePath, User.class);
                    LOGGER.info("userStorage initialized successfully.");
                    break;

                default:
                    LOGGER.debug("No initialization required for bean: {}", beanName);
            }
        } catch (Exception e) {
            LOGGER.error("Failed to initialize storage for bean: {}", beanName, e);
            throw new RuntimeException("Failed to initialize storage for bean: " + beanName, e);
        }
        return bean;
    }

    public <T extends BaseEntity, K> void populateStorage(Map<K, T> storage, Resource fileResource, Class<T> type) throws Exception {
        try (InputStream inputStream = fileResource.getInputStream()) {
            List<T> items = OBJECT_MAPPER.readValue(inputStream,
                    OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, type));
            for (T item : items) {
                K id = (K) Long.valueOf(item.getId());
                storage.put(id, item);
            }
        }
    }
}