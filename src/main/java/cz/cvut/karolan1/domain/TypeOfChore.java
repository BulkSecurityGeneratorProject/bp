package cz.cvut.karolan1.domain;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A TypeOfChore.
 */
@Entity
@Table(name = "type_of_chore")
public class TypeOfChore implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "repeatable", nullable = false)
    private Boolean repeatable;

    @Column(name = "interval")
    private Integer interval;

    @Column(name = "points")
    private Integer points;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean isRepeatable() {
        return repeatable;
    }

    public void setRepeatable(Boolean repeatable) {
        this.repeatable = repeatable;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TypeOfChore typeOfChore = (TypeOfChore) o;
        if(typeOfChore.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, typeOfChore.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TypeOfChore{" +
            "id=" + id +
            ", name='" + name + "'" +
            ", description='" + description + "'" +
            ", repeatable='" + repeatable + "'" +
            ", interval='" + interval + "'" +
            ", points='" + points + "'" +
            '}';
    }
}
