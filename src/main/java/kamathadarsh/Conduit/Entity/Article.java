package kamathadarsh.Conduit.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data

@Table(uniqueConstraints = @UniqueConstraint(name = "slug", columnNames = "slug"))
public class Article implements Serializable{

    @Id
    @org.springframework.data.annotation.Id
    @org.springframework.data.annotation.AccessType(org.springframework.data.annotation.AccessType.Type.PROPERTY)
    private String slug;

    private String title;

    private String description;

    private String body;

    private Instant createdAt;

    private Instant updatedAt;

    @ManyToMany(mappedBy = "favouriteArticleList", fetch = FetchType.LAZY)
    private Set<User> favouriteByList;

    private Integer favouriteCount;


    @ManyToOne
    @JoinColumn(
            name = "authorUsername",
            referencedColumnName = "username"
    )
    private User author;

    @ManyToMany
    @JoinTable(
            name = "article_tag_table",
            joinColumns = @JoinColumn(name = "articleSlug", referencedColumnName = "slug"),
            inverseJoinColumns = @JoinColumn(name = "tagName", referencedColumnName = "tagName")
    )
    private Set<Tag> tags;


    @Override
    public int hashCode(){
        return Objects.hashCode(slug);
    }
}
