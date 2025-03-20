package uz.gym.training.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.gym.training.domain.Trainer;


import java.util.Optional;

public interface TrainerRepository extends JpaRepository<Trainer, Long> {
    Optional<Trainer> findByUsername(String username);
}