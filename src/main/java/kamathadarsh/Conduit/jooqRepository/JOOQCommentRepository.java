package kamathadarsh.Conduit.jooqRepository;

import kamathadarsh.Conduit.Request.CommentRequest;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.Comment;

import kamathadarsh.Conduit.jooq.jooqGenerated.tables.records.CommentRecord;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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
                .and(COMMENT.PARENT_COMMENT_ID.eq(0L))
                .fetchInto(Comment.class);

    }


    public Long createComment(Comment newComment){

        CommentRecord newCommentRecord = dslContext.insertInto(COMMENT)
                .set(COMMENT.ARTICLE_SLUG, newComment.getArticleSlug())
                .set(COMMENT.USER_USERNAME, newComment.getUserUsername())
                .set(COMMENT.CREATED_AT, newComment.getCreatedAt())
                .set(COMMENT.UPDATED_AT, newComment.getUpdatedAt())
                .set(COMMENT.PARENT_COMMENT_ID, 0L)
                .set(COMMENT.BODY, newComment.getBody())
                .returning(COMMENT.ID).fetchOne();

        return newCommentRecord.getId();

    }

    public boolean checkIfCommentExistsByIdUnderAnArticle(Long commentId, String articleSlug){

        return dslContext.fetchExists(dslContext.selectFrom(COMMENT)
                .where(COMMENT.ID.eq(commentId))
                .and(COMMENT.ARTICLE_SLUG.eq(articleSlug)));



    }

    public void deleteCommentUnderAnArticleById(String articleSlug, Long commentId){

        dslContext.deleteFrom(COMMENT)
                .where(COMMENT.ID.eq(commentId))
                .and(COMMENT.ARTICLE_SLUG.eq(articleSlug))
                .execute();
    }

    public void replyToComment(String currUserUsername, String articleSlug, Long parentCommentId, CommentRequest commentRequest){

        dslContext.insertInto(COMMENT)
                .set(COMMENT.BODY, commentRequest.getBody())
                .set(COMMENT.ARTICLE_SLUG, articleSlug)
                .set(COMMENT.USER_USERNAME, currUserUsername)
                .set(COMMENT.PARENT_COMMENT_ID, parentCommentId)
                .set(COMMENT.UPDATED_AT, LocalDateTime.now())
                .set(COMMENT.CREATED_AT, LocalDateTime.now())
                .execute();
    }

    public List<Comment> getRepliesToComment(String articleSlug, Long parentCommentId){

        return dslContext.select(COMMENT.ID,
                COMMENT.BODY,
                COMMENT.USER_USERNAME,
                COMMENT.CREATED_AT,
                COMMENT.UPDATED_AT
                ).from(COMMENT)
                .where(COMMENT.ARTICLE_SLUG.eq(articleSlug))
                .and(COMMENT.PARENT_COMMENT_ID.eq(parentCommentId))
                .fetchInto(Comment.class);
    }


    public List<Long> getIdOfRepliesToComment(String articleSlug, Long parentCommentId){

        return dslContext.select(COMMENT.ID)
                .from(COMMENT)
                .where(COMMENT.ARTICLE_SLUG.eq(articleSlug))
                .and(COMMENT.PARENT_COMMENT_ID.eq(parentCommentId))
                .fetchInto(Long.class);
    }

    public List<Long> getAllParentCommentIdsUnderArticle(String articleSlug){

        return dslContext.select(COMMENT.ID)
                .from(COMMENT)
                .where(COMMENT.ARTICLE_SLUG.eq(articleSlug))
                .and(COMMENT.PARENT_COMMENT_ID.eq(0L))
                .fetchInto(Long.class);
    }
}
