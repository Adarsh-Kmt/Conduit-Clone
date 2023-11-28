package kamathadarsh.Conduit.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String body;

    private Instant createdAt;


    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(
            name = "articleSlug",
            referencedColumnName = "slug"
    )
    private Article article;

    @OneToOne
    private User user;
}
