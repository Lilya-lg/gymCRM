package uz.gym.crm.domain;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "Test")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String surname;
}
