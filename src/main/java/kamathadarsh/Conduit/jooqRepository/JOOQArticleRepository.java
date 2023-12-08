package kamathadarsh.Conduit.jooqRepository;

import kamathadarsh.Conduit.Request.GetArticleRequest;
import kamathadarsh.Conduit.Request.UpdateArticleRequest;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.Article;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.pojos.Tag;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.records.ArticleRecord;
import lombok.AllArgsConstructor;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static kamathadarsh.Conduit.jooq.jooqGenerated.Tables.*;


@AllArgsConstructor
@Repository
public class JOOQArticleRepository {

    private final DSLContext dslContext;

    public String slugify(String articleName){

        String slug = articleName.trim().toLowerCase().replace(" ", "-");
        return slug + "-" + UUID.randomUUID();
    }
    public Optional<Article> findArticleBySlug(String articleSlug){

        return Optional.ofNullable(dslContext.select()
                .from(ARTICLE)
                .where(ARTICLE.SLUG.eq(articleSlug))
                .fetchOneInto(Article.class));
    }

    public List<Tag> getAllTagsAssociatedWithArticle(String articleSlug){

        return dslContext.select()
                .from(TAG)
                .join(ARTICLE_TAG_TABLE).on(ARTICLE_TAG_TABLE.ARTICLE_SLUG.eq(ARTICLE.SLUG))
                .where(ARTICLE_TAG_TABLE.ARTICLE_SLUG.eq(articleSlug))
                .fetchInto(Tag.class);

    }

    public void save(Article article){

        dslContext.insertInto(ARTICLE)
                .set(ARTICLE.SLUG, article.getSlug())
                .set(ARTICLE.DESCRIPTION, article.getDescription())
                .set(ARTICLE.TITLE, article.getTitle())
                .set(ARTICLE.BODY, article.getBody())
                .set(ARTICLE.CREATED_AT, article.getCreatedAt())
                .set(ARTICLE.UPDATED_AT, article.getUpdatedAt())
                .set(ARTICLE.AUTHOR_USERNAME, article.getAuthorUsername())
                .set(ARTICLE.FAVOURITE_COUNT, article.getFavouriteCount())
                .execute();
    }

    public void favouriteArticle(String articleSlug, String currUserUsername){

        dslContext.insertInto(USER_FAVOURITE_ARTICLE_TABLE)
                .set(USER_FAVOURITE_ARTICLE_TABLE.ARTICLE_SLUG, articleSlug)
                .set(USER_FAVOURITE_ARTICLE_TABLE.USERNAME, currUserUsername).execute();
    }

    public void unfavouriteArticle(String articleSlug, String currUserUsername){

        dslContext.deleteFrom(USER_FAVOURITE_ARTICLE_TABLE)
                .where(USER_FAVOURITE_ARTICLE_TABLE.ARTICLE_SLUG.eq(articleSlug))
                .and(USER_FAVOURITE_ARTICLE_TABLE.USERNAME.eq(currUserUsername)).execute();
    }

    public boolean articleIsFavouritedByUser(String currUserUsername, String articleSlug){

        return dslContext.fetchExists(
                dslContext.select().from(USER_FAVOURITE_ARTICLE_TABLE)
                        .where(USER_FAVOURITE_ARTICLE_TABLE.ARTICLE_SLUG.eq(articleSlug),
                                USER_FAVOURITE_ARTICLE_TABLE.USERNAME.eq(currUserUsername))
        );

        
    }

    public List<Article> globalFeed(String currUserUsername, GetArticleRequest getArticleRequest){

        Condition baseCondition = DSL.trueCondition();

        if(getArticleRequest.getIsFavourited() != null && getArticleRequest.getIsFavourited()){

            baseCondition = baseCondition.and(USER_FAVOURITE_ARTICLE_TABLE.USERNAME.eq(currUserUsername));
        }

        if(getArticleRequest.getAuthorUsername() != null && !getArticleRequest.getAuthorUsername().isBlank()){

            baseCondition = baseCondition.and(ARTICLE.AUTHOR_USERNAME.eq(getArticleRequest.getAuthorUsername()));
        }

        if(getArticleRequest.getTags() != null && !getArticleRequest.getTags().isEmpty()){

            baseCondition = baseCondition.and(ARTICLE_TAG_TABLE.TAG_NAME.in(getArticleRequest.getTags()));
        }

        return dslContext.select()
                .from(ARTICLE)
                .where(baseCondition)
                .fetchInto(Article.class);


    }


    public void updateArticle(String articleSlug, UpdateArticleRequest updateArticleRequest){


        ArticleRecord updatedRecord = new ArticleRecord();

        String newArticleSlug = articleSlug;
        if(updateArticleRequest.getTitle() != null && !updateArticleRequest.getTitle().isBlank()){

            newArticleSlug = slugify(updateArticleRequest.getTitle());
            updatedRecord.set(ARTICLE.TITLE, updateArticleRequest.getTitle());
            updatedRecord.set(ARTICLE.SLUG, newArticleSlug);
        }

        if(updateArticleRequest.getBody() != null && !updateArticleRequest.getBody().isBlank()){

            updatedRecord.set(ARTICLE.BODY, updateArticleRequest.getBody());
        }

        if(updateArticleRequest.getDescription() != null && !updateArticleRequest.getDescription().isBlank()){

            updatedRecord.set(ARTICLE.DESCRIPTION, updateArticleRequest.getDescription());
        }

        // change the updated-at time.
        updatedRecord.set(ARTICLE.UPDATED_AT, LocalDateTime.now());

        dslContext.update(ARTICLE)
                .set(updatedRecord)
                .where(ARTICLE.SLUG.eq(articleSlug))
                .execute();

        // if title changes, slug changes, then slug used to refer to article in other tables must also be updated.
        if(updateArticleRequest.getTitle() != null && !updateArticleRequest.getTitle().isBlank()){

            // update comment table
            dslContext.update(COMMENT)
                    .set(COMMENT.ARTICLE_SLUG, newArticleSlug)
                    .where(COMMENT.ARTICLE_SLUG.eq(articleSlug))
                    .execute();

            //update favourite article user table.
            dslContext.update(USER_FAVOURITE_ARTICLE_TABLE)
                    .set(USER_FAVOURITE_ARTICLE_TABLE.ARTICLE_SLUG, newArticleSlug)
                    .where(USER_FAVOURITE_ARTICLE_TABLE.ARTICLE_SLUG.eq(articleSlug))
                    .execute();

            //updating tag article table
            dslContext.update(ARTICLE_TAG_TABLE)
                    .set(ARTICLE_TAG_TABLE.ARTICLE_SLUG, newArticleSlug)
                    .where(ARTICLE_TAG_TABLE.ARTICLE_SLUG.eq(articleSlug))
                    .execute();
        }

    }

    public String getAuthorUsernameOfArticle(String articleSlug){

        return dslContext.select(ARTICLE.AUTHOR_USERNAME)
                .from(ARTICLE)
                .where(ARTICLE.SLUG.eq(articleSlug))
                .fetchOneInto(String.class);
    }

    public void deleteArticle(String articleSlug){

        // delete article record form the article table.
        dslContext.deleteFrom(ARTICLE)
                .where(ARTICLE.SLUG.eq(articleSlug))
                .execute();

        // deleting the article also means deleting all the comments associated with it.
        dslContext.deleteFrom(COMMENT)
                .where(COMMENT.ARTICLE_SLUG.eq(articleSlug))
                .execute();

        // must delete the article slug from user favourite article table.
        dslContext.deleteFrom(USER_FAVOURITE_ARTICLE_TABLE)
                .where(USER_FAVOURITE_ARTICLE_TABLE.ARTICLE_SLUG.eq(articleSlug))
                .execute();

        // must delete corresponding records from tag article table.
        dslContext.deleteFrom(ARTICLE_TAG_TABLE)
                .where(USER_FAVOURITE_ARTICLE_TABLE.ARTICLE_SLUG.eq(articleSlug))
                .execute();



    }


}
