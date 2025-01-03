package uz.gym.crm.dao;


import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "test_entity")
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private String username;

    public TestEntity(String username) {
        this.username = username;
    }

    public TestEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestEntity that = (TestEntity) o;
        return id.equals(that.id) && username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}
