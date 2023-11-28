package kamathadarsh.Conduit.Repository;

import kamathadarsh.Conduit.Entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface TagRepository extends JpaRepository<Tag, String> {

    @Query(value = "select * from tag", nativeQuery = true)
    public List<Tag> getAllTags();
    @Query(value = "select * from tag where tag_name = :tagName", nativeQuery = true)
    public Optional<Tag> findTagByTagName(String tagName);

}
