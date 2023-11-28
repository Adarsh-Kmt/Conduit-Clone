package kamathadarsh.Conduit.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.Set;
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "tag_name", columnNames = "tagName"))
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Tag {

    @Id
    private String tagName;

    @ManyToMany(mappedBy = "tags",fetch = FetchType.EAGER)
    private Set<Article> articles;

    @Override
    public int hashCode(){

        return Objects.hashCode(tagName);
    }
}
