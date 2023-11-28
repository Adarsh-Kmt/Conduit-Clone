package kamathadarsh.Conduit.Repository;

import kamathadarsh.Conduit.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select * from comment where article_slug = :articleSlug", nativeQuery = true)
    public List<Comment> findAllCommentsUnderAnArticle(String articleSlug);

    @Query(value = "select * from comment where article_slug = :articleSlug and id = :commentId", nativeQuery = true)
    public Optional<Comment> getCommentUnderAnArticleById(String articleSlug, Long commentId);
}
