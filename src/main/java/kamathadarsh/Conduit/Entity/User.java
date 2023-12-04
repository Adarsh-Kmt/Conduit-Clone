package kamathadarsh.Conduit.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "user_table", uniqueConstraints = @UniqueConstraint(name = "unique_username", columnNames = "username"))

public class User {


    @Id
    private String username;

    private String password;

    private String emailId;

    private String bio;

    private String image;

    private Set<String> followingUserUsernameList;

    private Set<String> followerUserUsernameList;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_favourite_article_table",
            joinColumns = @JoinColumn(name = "username", referencedColumnName = "username"),
            inverseJoinColumns = @JoinColumn(name = "articleSlug", referencedColumnName = "slug")
    )
    private Set<Article> favouriteArticleList;
}
