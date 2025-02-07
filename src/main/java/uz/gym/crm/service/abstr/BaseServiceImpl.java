package uz.gym.crm.service.abstr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import uz.gym.crm.repository.UserRepository;

import java.util.List;


@Transactional
public abstract class BaseServiceImpl<T, R extends JpaRepository<T, Long>> implements BaseService<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(BaseServiceImpl.class);
    protected final R repository;
    protected final UserRepository userRepository;

    public BaseServiceImpl(R repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    public void create(T entity) {
        LOGGER.debug("Attempting to create entity: {}", entity);
        repository.save(entity);
        LOGGER.info("Entity created successfully: {}", entity);
    }

    @Override
    public T read(Long id) {
        LOGGER.debug("Reading entity with ID: {}", id);
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Entity not found for ID: " + id));
    }

    @Override
    public void update(T entity) {
        LOGGER.debug("Updating entity: {}", entity);
        repository.save(entity);
        LOGGER.info("Entity updated successfully: {}", entity);
    }

    @Override
    public void delete(Long id) {
        LOGGER.debug("Deleting entity with ID: {}", id);
        repository.deleteById(id);
        LOGGER.info("Entity deleted successfully with ID: {}", id);
    }

    @Override
    public List<T> getAll() {
        LOGGER.debug("Fetching all entities");
        List<T> entities = repository.findAll();
        LOGGER.info("Successfully fetched {} entities", entities.size());
        return entities;
    }

    @Override
    public boolean authenticate(String username, String password) {
        return userRepository.findByUsernameAndPassword(username, password).isPresent();
    }
}
