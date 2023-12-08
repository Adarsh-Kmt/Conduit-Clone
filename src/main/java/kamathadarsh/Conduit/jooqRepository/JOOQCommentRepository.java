package kamathadarsh.Conduit.jooqRepository;

import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.Comment;

import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.util.List;


import static kamathadarsh.Conduit.jooq.jooqGenerated.Tables.COMMENT;

@AllArgsConstructor
@Repository
public class JOOQCommentRepository {

    private final DSLContext dslContext;

    public List<Comment> findAllCommentsUnderAnArticle(String articleSlug){

        return dslContext.select()
                .from(COMMENT)
                .where(COMMENT.ARTICLE_SLUG.eq(articleSlug))
                .fetchInto(Comment.class);

    }


    public void createComment(Comment newComment){

        dslContext.insertInto(COMMENT)
                .set(COMMENT.ARTICLE_SLUG, newComment.getArticleSlug())
                .set(COMMENT.USER_USERNAME, newComment.getUserUsername())
                .set(COMMENT.CREATED_AT, newComment.getCreatedAt())
                .set(COMMENT.UPDATED_AT, newComment.getUpdatedAt())
                .set(COMMENT.BODY, newComment.getBody())
                .execute();
    }

    public boolean checkIfCommentUnderAnArticleById(String articleSlug, Long commentId){

        return dslContext.fetchExists(dslContext.select()
                .from(COMMENT)
                .where(COMMENT.ARTICLE_SLUG.eq(articleSlug))
                .and(COMMENT.ID.eq(commentId)));

    }

    public void deleteCommentUnderAnArticleById(String articleSlug, Long commentId){

        dslContext.deleteFrom(COMMENT)
                .where(COMMENT.ID.eq(commentId))
                .and(COMMENT.ARTICLE_SLUG.eq(articleSlug))
                .execute();
    }
}
