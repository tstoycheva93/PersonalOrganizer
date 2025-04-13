package app.collaborator.model;

import app.category.model.Category;
import app.team.model.Team;
import app.user.model.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GeneratorType;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class Collaborator {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToOne
    private User user;
    private boolean approvedInvitation;
    @OneToMany
    private List<Category> teamCategories;



}
