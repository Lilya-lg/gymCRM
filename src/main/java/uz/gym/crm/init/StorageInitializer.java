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
import uz.gym.crm.domain.Trainee;
import uz.gym.crm.domain.Trainer;
import uz.gym.crm.domain.Training;
import uz.gym.crm.domain.User;

import java.io.InputStream;
import java.util.List;
import java.util.Map;


@Component
public class StorageInitializer implements BeanPostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageInitializer.class);
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
            if ("trainerStorage".equals(beanName)) {
                LOGGER.info("Initializing trainerStorage with file: {}", trainerFilePath);
                populateStorage((Map<Integer, Trainer>) bean, trainerFilePath, Trainer.class);
                LOGGER.info("trainerStorage initialized successfully.");
            } else if ("traineeStorage".equals(beanName)) {
                LOGGER.info("Initializing traineeStorage with file: {}", traineeFilePath);
                populateStorage((Map<Integer, Trainee>) bean, traineeFilePath, Trainee.class);
                LOGGER.info("traineeStorage initialized successfully.");
            } else if ("trainingStorage".equals(beanName)) {
                LOGGER.info("Initializing trainingStorage with file: {}", trainingFilePath);
                populateStorage((Map<Integer, Training>) bean, trainingFilePath, Training.class);
                LOGGER.info("trainingStorage initialized successfully.");
            }
             else if("userStorage".equals(beanName)){
                LOGGER.info("Initializing userStorage with file: {}", userFilePath);
                populateStorage((Map<Integer, User>) bean, userFilePath, User.class);
                LOGGER.info("userStorage initialized successfully.");
            }
        } catch (Exception e) {
            LOGGER.error("Failed to initialize storage for bean: {}", beanName, e);
            throw new RuntimeException("Failed to initialize storage for bean: " + beanName, e);
        }
        return bean;
    }

    public <T, K> void populateStorage(Map<K, T> storage, Resource fileResource, Class<T> type) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try (InputStream inputStream = fileResource.getInputStream()) {
            List<T> items = objectMapper.readValue(inputStream, objectMapper.getTypeFactory().constructCollectionType(List.class, type));
            for (T item : items) {
                K id = (K) type.getDeclaredMethod("getId").invoke(item); // Ensure entities have getId()
                storage.put(id, item);
            }
        }
    }
}
